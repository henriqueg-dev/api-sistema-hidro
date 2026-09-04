package api.sistema.hidro.enums;

import api.sistema.hidro.entity.CaixaGorduraEntity;
import api.sistema.hidro.entity.ClienteEntity;
import api.sistema.hidro.entity.ConexaoTrechoEntity;
import api.sistema.hidro.entity.EmpreendimentoEntity;
import api.sistema.hidro.entity.OrcamentoEntity;
import api.sistema.hidro.entity.PiscinaEntity;
import api.sistema.hidro.entity.RamalPredialEntity;
import api.sistema.hidro.entity.TanqueSepticoEntity;
import api.sistema.hidro.entity.TrechoPiscinaEntity;
import api.sistema.hidro.entity.UsuarioEntity;
import api.sistema.hidro.entity.VazaoPredialEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** O que a auditoria aceita consultar; o nome é o que aparece na URL. */
public enum EntidadeAuditavel {

    CLIENTE("Cliente", ClienteEntity.class),
    EMPREENDIMENTO("Empreendimento", EmpreendimentoEntity.class),
    ORCAMENTO("Orçamento", OrcamentoEntity.class),
    USUARIO("Usuário", UsuarioEntity.class),
    PISCINA("Piscina", PiscinaEntity.class),
    TRECHO_PISCINA("Trecho de piscina", TrechoPiscinaEntity.class),
    CONEXAO_TRECHO("Conexão de trecho", ConexaoTrechoEntity.class),
    RAMAL_PREDIAL("Ramal predial", RamalPredialEntity.class),
    TANQUE_SEPTICO("Tanque séptico", TanqueSepticoEntity.class),
    VAZAO_PREDIAL("Vazão predial", VazaoPredialEntity.class),
    CAIXA_GORDURA("Caixa de gordura e sabão", CaixaGorduraEntity.class);

    private final String descricao;
    private final Class<?> classe;

    EntidadeAuditavel(String descricao, Class<?> classe) {
        this.descricao = descricao;
        this.classe = classe;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    public Class<?> getClasse() {
        return classe;
    }

    @JsonCreator
    public static EntidadeAuditavel fromJson(String valor) {
        if (valor == null || valor.isBlank()) return null;
        for (EntidadeAuditavel entidade : values()) {
            if (entidade.name().equalsIgnoreCase(valor) || entidade.descricao.equalsIgnoreCase(valor)) {
                return entidade;
            }
        }
        throw new IllegalArgumentException("Entidade auditável inválida: " + valor);
    }
}
