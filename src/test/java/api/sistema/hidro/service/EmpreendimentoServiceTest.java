package api.sistema.hidro.service;

import api.sistema.hidro.dto.EmpreendimentoRequestDTO;
import api.sistema.hidro.dto.EmpreendimentoResponseDTO;
import api.sistema.hidro.enums.TipoEmpreendimento;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.model.EmpreendimentoModel;
import api.sistema.hidro.model.EmpresaModel;
import api.sistema.hidro.repository.EmpreendimentoRepository;
import api.sistema.hidro.repository.EmpresaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpreendimentoServiceTest {

    @Mock
    private EmpreendimentoRepository empreendimentoRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private EmpreendimentoService empreendimentoService;

    @Test
    void deveCriarEmpreendimentoVinculadoAEmpresaExistente() {
        EmpresaModel empresa = EmpresaModel.builder().id(1L).nome("Construtora Alfa").build();

        EmpreendimentoRequestDTO dto = new EmpreendimentoRequestDTO();
        dto.setNome("Edifício Central");
        dto.setTipo(TipoEmpreendimento.PREDIO);
        dto.setNumPavimentos(12);
        dto.setEndereco("Rua Um, 100");
        dto.setConcessionaria("Copasa");
        dto.setEmpresaId(1L);

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        EmpreendimentoResponseDTO resposta = empreendimentoService.criar(dto);

        assertThat(resposta.getNome()).isEqualTo("Edifício Central");
        assertThat(resposta.getTipo()).isEqualTo(TipoEmpreendimento.PREDIO);
        assertThat(resposta.getEmpresaId()).isEqualTo(1L);
        assertThat(resposta.getEmpresaNome()).isEqualTo("Construtora Alfa");
        verify(empreendimentoRepository).save(any(EmpreendimentoModel.class));
    }

    @Test
    void deveLancarExcecaoAoCriarEmpreendimentoComEmpresaInexistente() {
        EmpreendimentoRequestDTO dto = new EmpreendimentoRequestDTO();
        dto.setEmpresaId(99L);

        when(empresaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empreendimentoService.criar(dto))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Empresa não encontrada");

        verify(empreendimentoRepository, never()).save(any(EmpreendimentoModel.class));
    }

    @Test
    void deveListarEmpreendimentosAtivosPorEmpresa() {
        EmpresaModel empresa = EmpresaModel.builder().id(1L).nome("Construtora Alfa").build();
        EmpreendimentoModel empreendimento = EmpreendimentoModel.builder()
                .id(10L)
                .nome("Edifício Central")
                .tipo(TipoEmpreendimento.PREDIO)
                .numPavimentos(12)
                .endereco("Rua Um, 100")
                .concessionaria("Copasa")
                .empresa(empresa)
                .ativo(true)
                .build();

        when(empreendimentoRepository.findByEmpresaIdAndAtivoTrue(1L)).thenReturn(List.of(empreendimento));

        List<EmpreendimentoResponseDTO> resultado = empreendimentoService.listarPorEmpresa(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Edifício Central");
        assertThat(resultado.get(0).getEmpresaId()).isEqualTo(1L);
    }
}
