package api.sistema.hidro.service;

import api.sistema.hidro.catalogo.entity.ContaEntity;
import api.sistema.hidro.catalogo.entity.UsuarioIndiceEntity;
import api.sistema.hidro.catalogo.repository.ContaRepository;
import api.sistema.hidro.catalogo.repository.UsuarioIndiceRepository;
import api.sistema.hidro.dto.LoginRequest;
import api.sistema.hidro.dto.LoginResponse;
import api.sistema.hidro.exception.GlobalExceptionHandler;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.exception.RegraNegocioException;
import api.sistema.hidro.entity.UsuarioEntity;
import api.sistema.hidro.enums.FinalidadeCodigo;
import api.sistema.hidro.repository.UsuarioRepository;
import api.sistema.hidro.security.JwtUtil;
import api.sistema.hidro.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * E-mail é único no sistema inteiro (índice no catálogo), mas a senha e o restante do cadastro
 * ficam no banco do tenant — por isso login e recuperação de senha sempre passam por
 * {@link #buscarConta(String)} antes de tocar {@link UsuarioRepository}.
 *
 * <p>Nenhum método daqui usa {@code @Transactional}: como cada um mistura catálogo e tenant, um
 * {@code @Transactional} sem qualificador demarcaria a transação errada (o
 * catalogoTransactionManager, marcado {@code @Primary}) e deixaria as escritas no tenant
 * silenciosamente fora de qualquer transação. Cada escrita no tenant é feita por um método
 * {@code @Transactional("tenantTransactionManager")} de outro service ({@link
 * CodigoVerificacaoService}), atômico em si mesmo.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Duration INTERVALO_MINIMO_REENVIO = Duration.ofMinutes(1);

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;
    private final UsuarioIndiceRepository usuarioIndiceRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CodigoVerificacaoService codigoVerificacaoService;
    private final EmailService emailService;

    private String hashFicticio;

    public LoginResponse login(LoginRequest request) {
        Long contaId = buscarConta(request.getEmail()).orElse(null);
        if (contaId == null) {
            simularVerificacaoSenha(request.getSenha());
            throw new BadCredentialsException(GlobalExceptionHandler.MSG_CREDENCIAIS_INVALIDAS);
        }

        TenantContext.definir(contaId);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getSenha()));

            UsuarioEntity usuarioEntity = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

            String token = jwtUtil.gerarToken(usuarioEntity.getEmail(), usuarioEntity.getPerfil().name(), contaId);
            String nomeEscritorio = contaRepository.findById(contaId)
                    .map(ContaEntity::getNomeEscritorio)
                    .orElse(null);

            return new LoginResponse(token, usuarioEntity.getNome(), usuarioEntity.getEmail(),
                    usuarioEntity.getPerfil().name(), nomeEscritorio);
        } finally {
            TenantContext.limpar();
        }
    }

    /** Assíncrono: a resposta sai no mesmo tempo exista ou não o e-mail. */
    @Async
    public void esqueciMinhaSenha(String email) {
        Long contaId = buscarConta(email).orElse(null);
        if (contaId == null) return;

        TenantContext.definir(contaId);
        try {
            usuarioRepository.findByEmail(email)
                    .filter(usuario -> !Boolean.TRUE.equals(usuario.getConvitePendente()))
                    .filter(usuario -> !codigoVerificacaoService.emitidoHaMenosDe(
                            usuario, FinalidadeCodigo.RECUPERACAO_SENHA, INTERVALO_MINIMO_REENVIO))
                    .ifPresent(usuario -> {
                        String codigo = codigoVerificacaoService.gerar(usuario, FinalidadeCodigo.RECUPERACAO_SENHA);
                        emailService.enviarCodigoRecuperacao(usuario.getEmail(), usuario.getNome(), codigo);
                    });
        } finally {
            TenantContext.limpar();
        }
    }

    public void redefinirSenha(String email, String codigo, String novaSenha) {
        // E-mail desconhecido responde como código errado, sem confirmar se a conta existe.
        TenantContext.definir(buscarConta(email)
                .orElseThrow(() -> new RegraNegocioException(CodigoVerificacaoService.MSG_CODIGO_INVALIDO)));
        try {
            UsuarioEntity usuarioEntity = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RegraNegocioException(CodigoVerificacaoService.MSG_CODIGO_INVALIDO));

            FinalidadeCodigo finalidade = codigoVerificacaoService.validar(usuarioEntity, codigo);

            usuarioEntity.setSenha(passwordEncoder.encode(novaSenha));
            if (finalidade == FinalidadeCodigo.CONVITE) {
                usuarioEntity.setAtivo(true);
                usuarioEntity.setConvitePendente(false);
            }
            usuarioRepository.save(usuarioEntity);
        } finally {
            TenantContext.limpar();
        }
    }

    private Optional<Long> buscarConta(String email) {
        return usuarioIndiceRepository.findById(email).map(UsuarioIndiceEntity::getContaId);
    }

    // E-mail inexistente também paga o custo do BCrypt, para o tempo de resposta não denunciá-lo.
    private void simularVerificacaoSenha(String senha) {
        if (hashFicticio == null) {
            hashFicticio = passwordEncoder.encode("hash-ficticio");
        }
        passwordEncoder.matches(senha, hashFicticio);
    }
}
