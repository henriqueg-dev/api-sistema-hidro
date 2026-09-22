package api.sistema.hidro.service;

import api.sistema.hidro.catalogo.entity.ContaEntity;
import api.sistema.hidro.catalogo.entity.UsuarioIndiceEntity;
import api.sistema.hidro.catalogo.repository.ContaRepository;
import api.sistema.hidro.catalogo.repository.UsuarioIndiceRepository;
import api.sistema.hidro.dto.LoginRequest;
import api.sistema.hidro.dto.LoginResponse;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.entity.UsuarioEntity;
import api.sistema.hidro.enums.FinalidadeCodigo;
import api.sistema.hidro.repository.UsuarioRepository;
import api.sistema.hidro.security.JwtUtil;
import api.sistema.hidro.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * E-mail é único no sistema inteiro (índice no catálogo), mas a senha e o restante do cadastro
 * ficam no banco do tenant — por isso login e recuperação de senha sempre passam por
 * {@link #resolverConta(String)} antes de tocar {@link UsuarioRepository}.
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

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;
    private final UsuarioIndiceRepository usuarioIndiceRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CodigoVerificacaoService codigoVerificacaoService;
    private final EmailService emailService;

    public LoginResponse login(LoginRequest request) {
        Long contaId = resolverConta(request.getEmail());

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

    public void esqueciMinhaSenha(String email) {
        Long contaId = usuarioIndiceRepository.findById(email)
                .map(UsuarioIndiceEntity::getContaId)
                .orElse(null);
        if (contaId == null) return;

        TenantContext.definir(contaId);
        try {
            usuarioRepository.findByEmail(email)
                    .filter(usuario -> !Boolean.TRUE.equals(usuario.getConvitePendente()))
                    .ifPresent(usuario -> {
                        String codigo = codigoVerificacaoService.gerar(usuario, FinalidadeCodigo.RECUPERACAO_SENHA);
                        emailService.enviarCodigoRecuperacao(usuario.getEmail(), usuario.getNome(), codigo);
                    });
        } finally {
            TenantContext.limpar();
        }
    }

    public void redefinirSenha(String email, String codigo, String novaSenha) {
        TenantContext.definir(resolverConta(email));
        try {
            UsuarioEntity usuarioEntity = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

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

    private Long resolverConta(String email) {
        return usuarioIndiceRepository.findById(email)
                .map(UsuarioIndiceEntity::getContaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }
}
