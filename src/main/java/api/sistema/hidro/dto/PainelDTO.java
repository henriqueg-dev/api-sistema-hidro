package api.sistema.hidro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/** Indicadores da tela inicial. Orçamentos e alterações só vêm para o administrador. */
@Getter
@AllArgsConstructor
public class PainelDTO {
    private long clientes;
    private long empreendimentos;
    private List<ContagemCalculo> calculos;
    private List<EmpreendimentoResponseDTO> empreendimentosRecentes;
    private ResumoOrcamentos orcamentos;
    private List<RevisaoResponseDTO> ultimasAlteracoes;

    @Getter
    @AllArgsConstructor
    public static class ContagemCalculo {
        private String nome;
        private long quantidade;
    }

    @Getter
    @AllArgsConstructor
    public static class ResumoOrcamentos {
        private long enviados;
        private double valorEnviados;
        private long aprovados;
        private double valorAprovados;
        /** Rascunhos e enviados que passaram da validade sem resposta. */
        private long vencidos;
    }
}
