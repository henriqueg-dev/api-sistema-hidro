package api.sistema.hidro.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Posição do trecho no circuito. Define o sinal do desnível e o limite de velocidade.
 * SUCCAO (ralo→filtro): P jus = P mont + desnível − Hf, máx 1,8 m/s.
 * RECALQUE (filtro→bocal): P jus = P mont − Hf − desnível, máx 3,0 m/s.
 */
public enum SentidoTrecho {

    SUCCAO("Sucção", DiametroPiscina.VELOCIDADE_MAX_SUCCAO),
    RECALQUE("Recalque", DiametroPiscina.VELOCIDADE_MAX_RECALQUE);

    private final String descricao;
    private final double velocidadeMaximaMs;

    SentidoTrecho(String descricao, double velocidadeMaximaMs) {
        this.descricao = descricao;
        this.velocidadeMaximaMs = velocidadeMaximaMs;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static SentidoTrecho fromJson(String valor) {
        if (valor == null || valor.isBlank()) return null;
        for (SentidoTrecho sentido : values()) {
            if (sentido.name().equalsIgnoreCase(valor) || sentido.descricao.equalsIgnoreCase(valor)) {
                return sentido;
            }
        }
        throw new IllegalArgumentException("Sentido do trecho inválido: " + valor);
    }

    public double getVelocidadeMaximaMs() {
        return velocidadeMaximaMs;
    }

    /** Pressão disponível ao final do trecho (mca). */
    public double pressaoJusante(double pressaoMontanteMca, double desnivelM, double hfM) {
        return this == SUCCAO
                ? pressaoMontanteMca + desnivelM - hfM
                : pressaoMontanteMca - hfM - desnivelM;
    }
}
