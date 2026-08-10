package api.sistema.hidro.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PerfilUsuario {
    ADMIN("Administrador"),
    ENGENHEIRO("Engenheiro");

    private final String descricao;

    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }

    /** Valor enviado nas respostas da API. */
    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    /** Nas requisições aceita tanto a constante (ADMIN) quanto a descrição. */
    @JsonCreator
    public static PerfilUsuario fromJson(String valor) {
        if (valor == null || valor.isBlank()) return null;
        for (PerfilUsuario perfil : values()) {
            if (perfil.name().equalsIgnoreCase(valor) || perfil.descricao.equalsIgnoreCase(valor)) {
                return perfil;
            }
        }
        throw new IllegalArgumentException("Perfil de usuário inválido: " + valor);
    }
}
