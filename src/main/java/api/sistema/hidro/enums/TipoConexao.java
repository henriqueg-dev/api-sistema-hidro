package api.sistema.hidro.enums;

import api.sistema.hidro.exception.RegraNegocioException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Perda de carga localizada em comprimento equivalente de tubo reto (m), por conexão
 * e DN, para PVC. DN 200 a 300 são extrapolados na tabela de origem; só os DN 50 a 110
 * têm diâmetro interno e podem ser usados nos trechos.
 */
public enum TipoConexao {

    JOELHO_90("Joelho 90°",
            1.2, 1.5, 2.0, 3.2, 3.4, 3.7, 3.9, 4.3, 4.9, 5.4, 7.1, 8.7, 10.0),
    JOELHO_45("Joelho 45°",
            0.5, 0.7, 1.0, 1.0, 1.3, 1.7, 1.8, 1.9, 2.4, 2.6, 3.4, 4.2, 5.0),
    CURVA_90("Curva 90°",
            0.5, 0.6, 0.7, 1.2, 1.3, 1.4, 1.5, 1.6, 1.9, 2.1, 2.8, 3.4, 4.0),
    CURVA_45("Curva 45°",
            0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.6, 1.9, 2.3),
    TE_90_PASSAGEM_DIRETA("Tê 90° passagem direta",
            0.8, 0.9, 1.5, 2.2, 2.3, 2.4, 2.5, 2.6, 3.3, 3.8, 4.8, 5.9, 6.9),
    TE_90_SAIDA_LATERAL("Tê 90° saída lateral",
            2.4, 3.1, 4.6, 7.3, 7.6, 7.8, 8.0, 8.3, 10.0, 11.0, 14.0, 17.0, 21.0),
    REGISTRO_GAVETA_ABERTO("Registro gaveta aberto",
            0.2, 0.3, 0.4, 0.7, 0.8, 0.9, 0.9, 1.0, 1.1, 1.2, 1.6, 2.0, 2.4),
    VALVULA_GLOBO_ABERTA("Válvula globo aberta",
            11.0, 15.0, 22.0, 36.0, 38.0, 38.0, 40.0, 42.0, 51.0, 57.0, 72.0, 89.0, 106.0),
    SAIDA_DE_CANAL("Saída de canal",
            0.9, 1.3, 1.4, 3.2, 3.3, 3.5, 3.7, 3.9, 4.9, 5.5, 6.9, 8.6, 10.0),
    ENTRADA_NORMAL("Entrada normal",
            0.4, 0.5, 0.6, 1.0, 1.5, 1.6, 2.0, 2.2, 2.5, 2.8, 3.8, 4.7, 5.6),
    ENTRADA_DE_BORDA("Entrada de borda",
            1.0, 1.2, 1.8, 2.3, 2.8, 3.3, 3.7, 4.0, 5.0, 5.6, 7.2, 9.0, 11.0),
    VALVULA_PE_E_CRIVO("Válvula de pé e crivo",
            9.5, 13.0, 16.0, 18.0, 24.0, 25.0, 27.0, 29.0, 37.0, 43.0, 53.0, 66.0, 78.0),
    VALVULA_RETENCAO_HORIZONTAL("Válvula de retenção horizontal",
            2.7, 3.8, 4.9, 6.8, 7.1, 8.2, 9.3, 10.0, 13.0, 14.0, 18.0, 22.0, 26.0),
    VALVULA_RETENCAO_VERTICAL("Válvula de retenção vertical",
            4.1, 5.8, 7.4, 9.1, 11.0, 13.0, 14.0, 16.0, 19.0, 21.0, 28.0, 34.0, 41.0);

    // Em classe aninhada porque campo estático do enum só inicializa depois das
    // constantes, e o construtor precisa deste array.
    private static final class Colunas {
        static final int[] DIAMETROS =
                {25, 32, 40, 50, 60, 75, 85, 110, 140, 160, 200, 250, 300};
    }

    private final String descricao;
    private final double[] comprimentos;

    TipoConexao(String descricao, double... comprimentos) {
        this.descricao = descricao;
        this.comprimentos = comprimentos;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static TipoConexao fromJson(String valor) {
        if (valor == null || valor.isBlank()) return null;
        for (TipoConexao tipo : values()) {
            if (tipo.name().equalsIgnoreCase(valor) || tipo.descricao.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de conexão inválido: " + valor);
    }

    public static int[] diametrosTabelados() {
        return Colunas.DIAMETROS.clone();
    }

    /** Comprimento equivalente (m) desta conexão no diâmetro nominal informado. */
    public double comprimentoEquivalenteM(int dn) {
        for (int i = 0; i < Colunas.DIAMETROS.length; i++) {
            if (Colunas.DIAMETROS[i] == dn) return comprimentos[i];
        }
        throw new RegraNegocioException(
                "Não há comprimento equivalente tabelado para " + descricao + " em DN " + dn + ".");
    }

    public double[] comprimentosTabelados() {
        return comprimentos.clone();
    }
}
