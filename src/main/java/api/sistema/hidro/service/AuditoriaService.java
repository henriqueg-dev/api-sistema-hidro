package api.sistema.hidro.service;

import api.sistema.hidro.dto.AuditoriaResponseDTO;
import api.sistema.hidro.dto.RevisaoResponseDTO;
import api.sistema.hidro.entity.RevisaoEntity;
import api.sistema.hidro.enums.EntidadeAuditavel;
import api.sistema.hidro.repository.RevisaoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditoriaService {

    /** Até onde seguir relação atrás de um nome: conexão -> trecho -> piscina -> empreendimento. */
    private static final int PROFUNDIDADE_MAXIMA = 4;

    /** Teto de linhas devolvidas, com ou sem busca. */
    private static final int LIMITE = 200;

    @PersistenceContext
    private EntityManager entityManager;

    private final RevisaoRepository revisaoRepository;

    /**
     * Sem busca, devolve as últimas revisões. Com busca, varre o log inteiro do mais novo para o
     * mais antigo — o nome do que foi alterado é montado em memória, então não dá para filtrar em
     * SQL. No volume de um escritório isso é imediato; se o log crescer muito, o filtro precisará
     * virar coluna persistida.
     */
    public List<RevisaoResponseDTO> linhaDoTempo(String busca) {
        AuditReader leitor = AuditReaderFactory.get(entityManager);
        String termo = busca == null ? "" : busca.trim().toLowerCase();

        if (termo.isEmpty()) {
            return revisaoRepository.findTop200ByOrderByIdDesc()
                    .stream()
                    .map(revisao -> paraDTO(leitor, revisao))
                    .toList();
        }

        return revisaoRepository.findAllByOrderByIdDesc()
                .stream()
                .map(revisao -> paraDTO(leitor, revisao))
                .filter(revisao -> combina(revisao, termo))
                .limit(LIMITE)
                .toList();
    }

    private RevisaoResponseDTO paraDTO(AuditReader leitor, RevisaoEntity revisao) {
        return new RevisaoResponseDTO(
                revisao.getId(),
                revisao.getDataOperacao(),
                revisao.getUsuarioId(),
                revisao.getUsuarioNome(),
                revisao.getUsuarioEmail(),
                alteracoesDe(leitor, revisao.getId()));
    }

    private boolean combina(RevisaoResponseDTO revisao, String termo) {
        return Stream.concat(
                        Stream.of(revisao.getUsuarioNome(), revisao.getUsuarioEmail()),
                        revisao.getAlteracoes().stream())
                .filter(Objects::nonNull)
                .anyMatch(campo -> campo.toLowerCase().contains(termo));
    }

    public List<AuditoriaResponseDTO> historico(EntidadeAuditavel entidade, Long id) {
        AuditReader leitor = AuditReaderFactory.get(entityManager);

        @SuppressWarnings("unchecked")
        List<Object[]> revisoes = leitor.createQuery()
                .forRevisionsOfEntity(entidade.getClasse(), false, true)
                .add(AuditEntity.id().eq(id))
                .addOrder(AuditEntity.revisionNumber().asc())
                .getResultList();

        List<AuditoriaResponseDTO> historico = new ArrayList<>();
        for (Object[] linha : revisoes) {
            RevisaoEntity revisao = (RevisaoEntity) linha[1];
            historico.add(new AuditoriaResponseDTO(
                    revisao.getId(),
                    revisao.getDataOperacao(),
                    descrever((RevisionType) linha[2]),
                    revisao.getUsuarioId(),
                    revisao.getUsuarioNome(),
                    revisao.getUsuarioEmail(),
                    valoresSimples(linha[0])));
        }
        return historico;
    }

    /** "Orçamento — Jardins": o tipo e o nome do que foi tocado naquela revisão. */
    private List<String> alteracoesDe(AuditReader leitor, int revisao) {
        return leitor.getCrossTypeRevisionChangesReader().findEntities(revisao)
                .stream()
                .map(entidade -> {
                    Object real = desembrulhar(entidade);
                    String tipo = descreverTipo(real == null ? entidade.getClass() : real.getClass());
                    String nome = nomeDe(entidade, PROFUNDIDADE_MAXIMA);
                    return nome.isBlank() ? tipo : tipo + " — " + nome;
                })
                .distinct()
                .sorted()
                .toList();
    }

    private String descreverTipo(Class<?> classe) {
        for (EntidadeAuditavel entidade : EntidadeAuditavel.values()) {
            if (entidade.getClasse().isAssignableFrom(classe)) {
                return entidade.getDescricao();
            }
        }
        return classe.getSimpleName();
    }

    /**
     * Nome legível do que foi alterado. Relação é resolvida na tabela viva, e não no histórico:
     * registro criado antes da auditoria não tem linha _aud, e ficaria sem nome.
     */
    private String nomeDe(Object entidade, int profundidade) {
        if (entidade == null || profundidade == 0) return "";

        for (String candidato : new String[] {"nome", "nomeEmpreendimento"}) {
            Object valor = ler(entidade, candidato);
            if (valor instanceof String texto && !texto.isBlank()) return texto;
        }

        for (Field campo : camposDe(entidade)) {
            if (!ehEntidadeAuditavel(campo.getType())) continue;

            Object relacionado = aoVivo(campo.getType(), idDe(ler(entidade, campo.getName())));
            String nome = nomeDe(relacionado, profundidade - 1);
            if (!nome.isBlank()) return nome;
        }
        return "";
    }

    private Object idDe(Object relacao) {
        if (relacao == null) return null;

        LazyInitializer inicializador = HibernateProxy.extractLazyInitializer(relacao);
        return inicializador != null ? inicializador.getIdentifier() : ler(relacao, "id");
    }

    private Object aoVivo(Class<?> tipo, Object id) {
        if (id == null) return null;
        try {
            return entityManager.find(tipo, id);
        } catch (RuntimeException e) {
            return null;
        }
    }

    /** O Envers devolve proxy: o campo pode estar na superclasse, não na classe concreta. */
    private List<Field> camposDe(Object alvo) {
        Object real = desembrulhar(alvo);
        if (real == null) return List.of();

        List<Field> campos = new ArrayList<>();
        for (Class<?> classe = real.getClass(); classe != null; classe = classe.getSuperclass()) {
            campos.addAll(List.of(classe.getDeclaredFields()));
        }
        return campos;
    }

    /** Auditoria referencia registro já apagado: nesse caso não há o que ler. */
    private Object desembrulhar(Object alvo) {
        if (alvo == null) return null;
        try {
            return Hibernate.unproxy(alvo);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private boolean ehEntidadeAuditavel(Class<?> tipo) {
        for (EntidadeAuditavel entidade : EntidadeAuditavel.values()) {
            if (entidade.getClasse().equals(tipo)) return true;
        }
        return false;
    }

    private Object ler(Object alvo, String nomeCampo) {
        Object real = desembrulhar(alvo);
        if (real == null) return null;

        for (Field campo : camposDe(real)) {
            if (!campo.getName().equals(nomeCampo)) continue;
            try {
                campo.setAccessible(true);
                return campo.get(real);
            } catch (ReflectiveOperationException | RuntimeException e) {
                return null;
            }
        }
        return null;
    }

    private String descrever(RevisionType tipo) {
        return switch (tipo) {
            case ADD -> "CRIACAO";
            case MOD -> "ALTERACAO";
            case DEL -> "EXCLUSAO";
        };
    }

    /**
     * Só campos de valor: relações viram proxy e estourariam fora da sessão, e a senha do
     * usuário nem chega a ser auditada.
     */
    private Map<String, Object> valoresSimples(Object entidade) {
        Map<String, Object> valores = new LinkedHashMap<>();
        if (entidade == null) return valores;

        for (Field campo : camposDe(entidade)) {
            if (!ehValorSimples(campo.getType())) continue;
            valores.put(campo.getName(), String.valueOf(ler(entidade, campo.getName())));
        }
        return valores;
    }

    private boolean ehValorSimples(Class<?> tipo) {
        return tipo.isPrimitive()
                || tipo.isEnum()
                || CharSequence.class.isAssignableFrom(tipo)
                || Number.class.isAssignableFrom(tipo)
                || Boolean.class.isAssignableFrom(tipo)
                || Temporal.class.isAssignableFrom(tipo);
    }
}
