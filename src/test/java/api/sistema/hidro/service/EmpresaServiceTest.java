package api.sistema.hidro.service;

import api.sistema.hidro.dto.EmpresaRequestDTO;
import api.sistema.hidro.dto.EmpresaResponseDTO;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.model.EmpresaModel;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private EmpresaService empresaService;

    @Test
    void deveCriarEmpresaComNomeInformado() {
        EmpresaRequestDTO dto = new EmpresaRequestDTO();
        dto.setNome("Construtora Alfa");

        EmpresaResponseDTO resposta = empresaService.criar(dto);

        assertThat(resposta.getNome()).isEqualTo("Construtora Alfa");
        verify(empresaRepository).save(any(EmpresaModel.class));
    }

    @Test
    void deveListarApenasEmpresasAtivas() {
        EmpresaModel empresa = EmpresaModel.builder().id(1L).nome("Ativa").ativo(true).build();
        when(empresaRepository.findByAtivoTrue()).thenReturn(List.of(empresa));

        List<EmpresaResponseDTO> resultado = empresaService.listarTodas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Ativa");
    }

    @Test
    void deveLancarExcecaoQuandoEmpresaNaoEncontrada() {
        when(empresaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empresaService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Empresa não encontrada");
    }
}
