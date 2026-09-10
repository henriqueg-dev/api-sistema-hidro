package api.sistema.hidro.service;

import api.sistema.hidro.entity.CodigoVerificacaoEntity;
import api.sistema.hidro.entity.UsuarioEntity;
import api.sistema.hidro.enums.FinalidadeCodigo;
import api.sistema.hidro.exception.RegraNegocioException;
import api.sistema.hidro.repository.CodigoVerificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CodigoVerificacaoService {

    private static final SecureRandom ALEATORIO = new SecureRandom();
    private static final String MSG_CODIGO_INVALIDO = "Código inválido ou expirado";

    private final CodigoVerificacaoRepository codigoVerificacaoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${codigo-verificacao.recuperacao-senha.expiracao-minutos:15}")
    private long expiracaoRecuperacaoMinutos;

    @Value("${codigo-verificacao.convite.expiracao-minutos:2880}")
    private long expiracaoConviteMinutos;

    @Value("${codigo-verificacao.max-tentativas:5}")
    private int maxTentativas;

    @Transactional
    public String gerar(UsuarioEntity usuario, FinalidadeCodigo finalidade) {
        String codigo = String.format("%06d", ALEATORIO.nextInt(1_000_000));
        long minutos = finalidade == FinalidadeCodigo.CONVITE
                ? expiracaoConviteMinutos
                : expiracaoRecuperacaoMinutos;

        CodigoVerificacaoEntity entidade = codigoVerificacaoRepository.findByUsuario(usuario)
                .orElseGet(() -> CodigoVerificacaoEntity.builder().usuario(usuario).build());

        entidade.setCodigoHash(passwordEncoder.encode(codigo));
        entidade.setFinalidade(finalidade);
        entidade.setExpiraEm(LocalDateTime.now().plusMinutes(minutos));
        entidade.setTentativas(0);
        codigoVerificacaoRepository.save(entidade);

        return codigo;
    }

    @Transactional
    public FinalidadeCodigo validar(UsuarioEntity usuario, String codigo) {
        CodigoVerificacaoEntity entidade = codigoVerificacaoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RegraNegocioException(MSG_CODIGO_INVALIDO));

        if (entidade.getExpiraEm().isBefore(LocalDateTime.now())) {
            codigoVerificacaoRepository.delete(entidade);
            throw new RegraNegocioException(MSG_CODIGO_INVALIDO);
        }

        if (!passwordEncoder.matches(codigo, entidade.getCodigoHash())) {
            entidade.setTentativas(entidade.getTentativas() + 1);
            if (entidade.getTentativas() >= maxTentativas) {
                codigoVerificacaoRepository.delete(entidade);
            } else {
                codigoVerificacaoRepository.save(entidade);
            }
            throw new RegraNegocioException(MSG_CODIGO_INVALIDO);
        }

        FinalidadeCodigo finalidade = entidade.getFinalidade();
        codigoVerificacaoRepository.delete(entidade);
        return finalidade;
    }
}
