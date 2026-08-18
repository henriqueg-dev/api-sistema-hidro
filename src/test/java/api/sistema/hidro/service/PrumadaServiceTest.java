package api.sistema.hidro.service;

import api.sistema.hidro.dto.PrumadaConsultaDTO;
import api.sistema.hidro.dto.PrumadaRespostaDTO;
import api.sistema.hidro.enums.CondicaoSanca;
import api.sistema.hidro.enums.TipoPrumada;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.entity.PrumadaEntity;
import api.sistema.hidro.repository.PrumadaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrumadaServiceTest {

    @Mock
    private PrumadaRepository prumadaRepository;

    @InjectMocks
    private PrumadaService prumadaService;

    @Test
    void deveResolverFaixaAte9ParaCozinhaComPoucosPavimentos() {
        PrumadaConsultaDTO dto = new PrumadaConsultaDTO();
        dto.setTipo(TipoPrumada.COZINHA);
        dto.setNumPavimentos(9);
        dto.setDesconector("50mm");
        dto.setCondicaoSanca(CondicaoSanca.SEM_SANCA);

        PrumadaEntity prumada = PrumadaEntity.builder()
                .tipo(TipoPrumada.COZINHA)
                .numPavimentos("ATE_9")
                .desconector("50mm")
                .condicaoSanca(CondicaoSanca.SEM_SANCA)
                .descricao("descrição de teste")
                .build();

        when(prumadaRepository.findByTipoAndNumPavimentosAndDesconectorAndCondicaoSanca(
                TipoPrumada.COZINHA, "ATE_9", "50mm", CondicaoSanca.SEM_SANCA))
                .thenReturn(Optional.of(prumada));

        PrumadaRespostaDTO resposta = prumadaService.consultar(dto);

        assertThat(resposta.getNumPavimentos()).isEqualTo("ATE_9");
        assertThat(resposta.getDescricao()).isEqualTo("descrição de teste");
    }

    @Test
    void deveResolverFaixaAte5ParaArsComPoucosPavimentos() {
        PrumadaConsultaDTO dto = new PrumadaConsultaDTO();
        dto.setTipo(TipoPrumada.ARS);
        dto.setNumPavimentos(5);
        dto.setDesconector("50mm");
        dto.setCondicaoSanca(CondicaoSanca.SEM_SANCA);

        PrumadaEntity prumada = PrumadaEntity.builder()
                .tipo(TipoPrumada.ARS)
                .numPavimentos("ATE_5")
                .desconector("50mm")
                .condicaoSanca(CondicaoSanca.SEM_SANCA)
                .descricao("descrição ars")
                .build();

        when(prumadaRepository.findByTipoAndNumPavimentosAndDesconectorAndCondicaoSanca(
                TipoPrumada.ARS, "ATE_5", "50mm", CondicaoSanca.SEM_SANCA))
                .thenReturn(Optional.of(prumada));

        PrumadaRespostaDTO resposta = prumadaService.consultar(dto);

        assertThat(resposta.getNumPavimentos()).isEqualTo("ATE_5");
    }

    @Test
    void deveLancarExcecaoQuandoNenhumaPrumadaEncontrada() {
        PrumadaConsultaDTO dto = new PrumadaConsultaDTO();
        dto.setTipo(TipoPrumada.COZINHA);
        dto.setNumPavimentos(3);
        dto.setDesconector("50mm");
        dto.setCondicaoSanca(CondicaoSanca.SEM_SANCA);

        when(prumadaRepository.findByTipoAndNumPavimentosAndDesconectorAndCondicaoSanca(
                TipoPrumada.COZINHA, "ATE_9", "50mm", CondicaoSanca.SEM_SANCA))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> prumadaService.consultar(dto))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
