package api.sistema.hidro.dto;

import api.sistema.hidro.enums.StatusOrcamento;
import api.sistema.hidro.enums.TipoEmpreendimento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class OrcamentoResponseDTO {
    private Long id;
    private Long clienteId;
    private String clienteNome;
    private String nomeEmpreendimento;
    private TipoEmpreendimento tipoEmpreendimento;

    private Double quantidade;
    private Double valorUnitario;
    private Double valorTotal;
    private StatusOrcamento status;
    private String observacoes;
    private Integer validadeDias;
    private LocalDate dataValidade;
    private Long empreendimentoGeradoId;

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
