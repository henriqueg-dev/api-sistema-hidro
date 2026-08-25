package api.sistema.hidro.service;

import api.sistema.hidro.config.ClaudeClientProvider;
import api.sistema.hidro.dto.ConversaDetalheDTO;
import api.sistema.hidro.dto.ConversaResponseDTO;
import api.sistema.hidro.dto.MensagemRequestDTO;
import api.sistema.hidro.dto.MensagemResponseDTO;
import api.sistema.hidro.entity.CaixaGorduraEntity;
import api.sistema.hidro.entity.ConversaEntity;
import api.sistema.hidro.entity.EmpreendimentoEntity;
import api.sistema.hidro.entity.MensagemEntity;
import api.sistema.hidro.entity.UsuarioEntity;
import api.sistema.hidro.entity.VazaoPredialEntity;
import api.sistema.hidro.enums.PapelMensagem;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.exception.RegraNegocioException;
import api.sistema.hidro.repository.CaixaGorduraRepository;
import api.sistema.hidro.repository.ConversaRepository;
import api.sistema.hidro.repository.EmpreendimentoRepository;
import api.sistema.hidro.repository.MensagemRepository;
import api.sistema.hidro.repository.UsuarioRepository;
import api.sistema.hidro.repository.VazaoPredialRepository;
import com.anthropic.errors.AnthropicServiceException;
import com.anthropic.models.messages.CacheControlEphemeral;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.OutputConfig;
import com.anthropic.models.messages.TextBlock;
import com.anthropic.models.messages.TextBlockParam;
import com.anthropic.models.messages.ThinkingConfigAdaptive;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssistenteService {

    private static final Logger log = LoggerFactory.getLogger(AssistenteService.class);

    /** Instruções permanentes do assistente. Editável sem mexer no código Java. */
    private static final String CAMINHO_PROMPT = "prompts/assistente-sistema.md";

    private static final long MAX_TOKENS = 8000L;

    /** Quantas mensagens anteriores são reenviadas como histórico a cada pergunta. */
    private static final int LIMITE_HISTORICO = 20;

    private static final int TAMANHO_TITULO = 60;

    private final ConversaRepository conversaRepository;
    private final MensagemRepository mensagemRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpreendimentoRepository empreendimentoRepository;
    private final CaixaGorduraRepository caixaGorduraRepository;
    private final VazaoPredialRepository vazaoPredialRepository;
    private final ClaudeClientProvider claudeClientProvider;

    public boolean assistenteConfigurado() {
        return claudeClientProvider.configurado();
    }

    public List<ConversaResponseDTO> listarConversas(String email) {
        UsuarioEntity usuario = buscarUsuario(email);
        return conversaRepository.findByUsuarioIdOrderByAtualizadoEmDesc(usuario.getId())
                .stream()
                .map(this::toConversaDTO)
                .toList();
    }

    public ConversaDetalheDTO buscarConversa(Long id, String email) {
        return montarDetalhe(buscarConversaDoUsuario(id, email));
    }

    @Transactional
    public ConversaDetalheDTO criarConversa(MensagemRequestDTO dto, String email) {
        UsuarioEntity usuario = buscarUsuario(email);

        EmpreendimentoEntity empreendimento = null;
        if (dto.getEmpreendimentoId() != null) {
            empreendimento = empreendimentoRepository.findById(dto.getEmpreendimentoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Empreendimento não encontrado"));
        }

        ConversaEntity conversa = ConversaEntity.builder()
                .usuario(usuario)
                .empreendimento(empreendimento)
                .titulo(gerarTitulo(dto.getMensagem()))
                .build();

        conversaRepository.save(conversa);
        responder(conversa, dto.getMensagem());

        return montarDetalhe(conversa);
    }

    @Transactional
    public MensagemResponseDTO enviarMensagem(Long conversaId, MensagemRequestDTO dto, String email) {
        ConversaEntity conversa = buscarConversaDoUsuario(conversaId, email);
        MensagemEntity resposta = responder(conversa, dto.getMensagem());

        // Reordena a conversa no topo da lista lateral.
        conversaRepository.save(conversa);

        return toMensagemDTO(resposta);
    }

    @Transactional
    public void excluirConversa(Long id, String email) {
        ConversaEntity conversa = buscarConversaDoUsuario(id, email);
        mensagemRepository.deleteByConversaId(conversa.getId());
        conversaRepository.delete(conversa);
    }

    /** Grava a pergunta, consulta o modelo com o histórico da conversa e grava a resposta. */
    private MensagemEntity responder(ConversaEntity conversa, String pergunta) {
        List<MensagemEntity> historico =
                mensagemRepository.findByConversaIdOrderByCriadoEmAsc(conversa.getId());

        mensagemRepository.save(MensagemEntity.builder()
                .conversa(conversa)
                .papel(PapelMensagem.USUARIO)
                .conteudo(pergunta)
                .build());

        String texto = consultarClaude(conversa, historico, pergunta);

        return mensagemRepository.save(MensagemEntity.builder()
                .conversa(conversa)
                .papel(PapelMensagem.ASSISTENTE)
                .conteudo(texto)
                .build());
    }

    private String consultarClaude(ConversaEntity conversa,
                                   List<MensagemEntity> historico,
                                   String pergunta) {
        MessageCreateParams.Builder builder = MessageCreateParams.builder()
                .model(claudeClientProvider.getModelo())
                .maxTokens(MAX_TOKENS)
                .thinking(ThinkingConfigAdaptive.builder().build())
                .outputConfig(OutputConfig.builder()
                        .effort(OutputConfig.Effort.HIGH)
                        .build())
                .systemOfTextBlockParams(montarSystem(conversa));

        // As mensagens mais antigas são descartadas para a requisição não crescer sem limite.
        historico.stream()
                .skip(Math.max(0, historico.size() - LIMITE_HISTORICO))
                .forEach(mensagem -> {
                    if (mensagem.getPapel() == PapelMensagem.USUARIO) {
                        builder.addUserMessage(mensagem.getConteudo());
                    } else {
                        builder.addAssistantMessage(mensagem.getConteudo());
                    }
                });

        builder.addUserMessage(pergunta);

        try {
            Message resposta = claudeClientProvider.obter().messages().create(builder.build());

            String texto = resposta.content().stream()
                    .flatMap(bloco -> bloco.text().stream())
                    .map(TextBlock::text)
                    .collect(Collectors.joining("\n\n"));

            if (texto.isBlank()) {
                // Acontece quando o modelo recusa a pergunta (stop_reason "refusal").
                throw new RegraNegocioException(
                        "O assistente não conseguiu responder a essa pergunta. Tente reformulá-la.");
            }

            return texto;
        } catch (AnthropicServiceException ex) {
            log.error("Falha na chamada à API do Claude", ex);
            throw new RegraNegocioException(
                    "Não foi possível falar com o assistente agora. Tente novamente em instantes.");
        }
    }

    /**
     * Primeiro bloco: instruções fixas, marcadas para cache (o mesmo texto se repete em
     * toda pergunta). Segundo bloco: dados do empreendimento, quando a conversa tem um.
     */
    private List<TextBlockParam> montarSystem(ConversaEntity conversa) {
        List<TextBlockParam> blocos = new ArrayList<>();

        blocos.add(TextBlockParam.builder()
                .text(carregarPrompt())
                .cacheControl(CacheControlEphemeral.builder().build())
                .build());

        if (conversa.getEmpreendimento() != null) {
            blocos.add(TextBlockParam.builder()
                    .text(montarContexto(conversa.getEmpreendimento()))
                    .build());
        }

        return blocos;
    }

    private String montarContexto(EmpreendimentoEntity empreendimento) {
        StringBuilder contexto = new StringBuilder();
        contexto.append("# Empreendimento em discussão\n\n")
                .append("- Nome: ").append(empreendimento.getNome()).append('\n')
                .append("- Tipo: ").append(empreendimento.getTipo()).append('\n')
                .append("- Pavimentos: ").append(empreendimento.getNumPavimentos()).append('\n')
                .append("- Endereço: ").append(empreendimento.getEndereco()).append('\n')
                .append("- Concessionária: ").append(empreendimento.getConcessionaria()).append('\n');

        List<CaixaGorduraEntity> caixas =
                caixaGorduraRepository.findByEmpreendimentoIdOrderByCriadoEmAsc(empreendimento.getId());

        if (!caixas.isEmpty()) {
            contexto.append("\n## Cálculos de caixa de gordura e sabão (V = 2xN + 20)\n\n");
            for (int i = 0; i < caixas.size(); i++) {
                CaixaGorduraEntity caixa = caixas.get(i);
                contexto.append(i + 1).append(". Taxa de ocupação ").append(caixa.getTaxaOcupacao())
                        .append(" hab/apto, ").append(caixa.getNumApartamentos()).append(" apartamentos, ")
                        .append("população ").append(caixa.getPopulacao()).append(" hab, ")
                        .append("volume ").append(caixa.getVolumeLitros()).append(" L\n");
            }
        }

        List<VazaoPredialEntity> vazoes =
                vazaoPredialRepository.findByEmpreendimentoIdOrderByCriadoEmAsc(empreendimento.getId());

        if (!vazoes.isEmpty()) {
            contexto.append("\n## Cálculos de vazão predial\n\n");
            for (int i = 0; i < vazoes.size(); i++) {
                VazaoPredialEntity vazao = vazoes.get(i);
                contexto.append(i + 1).append(". População ").append(vazao.getPopulacao()).append(" hab, ")
                        .append("consumo per capita ").append(vazao.getConsumoPerCapita()).append(" L/hab.dia, ")
                        .append("reserva ").append(vazao.getCapacidadeEquivalenteDias()).append(" dia(s), ")
                        .append("volume ").append(vazao.getVolumeCaixaM3()).append(" m3, ")
                        .append("vazão média ").append(vazao.getVazaoMediaLps()).append(" L/s, ")
                        .append("máxima diária ").append(vazao.getVazaoMaximaDiariaLps()).append(" L/s, ")
                        .append("máxima horária ").append(vazao.getVazaoMaximaHoraLps()).append(" L/s\n");
            }
        }

        if (caixas.isEmpty() && vazoes.isEmpty()) {
            contexto.append("\nNenhum cálculo foi salvo para este empreendimento ainda.\n");
        }

        return contexto.toString();
    }

    private String carregarPrompt() {
        try {
            return new ClassPathResource(CAMINHO_PROMPT).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new UncheckedIOException("Não foi possível carregar " + CAMINHO_PROMPT, ex);
        }
    }

    private String gerarTitulo(String mensagem) {
        String limpa = mensagem.strip().replaceAll("\\s+", " ");
        return limpa.length() <= TAMANHO_TITULO
                ? limpa
                : limpa.substring(0, TAMANHO_TITULO).strip() + "...";
    }

    private ConversaEntity buscarConversaDoUsuario(Long id, String email) {
        ConversaEntity conversa = conversaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conversa não encontrada"));

        // Uma conversa só é visível para quem a criou.
        if (!conversa.getUsuario().getEmail().equals(email)) {
            throw new RecursoNaoEncontradoException("Conversa não encontrada");
        }

        return conversa;
    }

    private UsuarioEntity buscarUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    private ConversaDetalheDTO montarDetalhe(ConversaEntity conversa) {
        List<MensagemResponseDTO> mensagens =
                mensagemRepository.findByConversaIdOrderByCriadoEmAsc(conversa.getId())
                        .stream()
                        .map(this::toMensagemDTO)
                        .toList();

        return new ConversaDetalheDTO(toConversaDTO(conversa), mensagens);
    }

    private ConversaResponseDTO toConversaDTO(ConversaEntity conversa) {
        EmpreendimentoEntity empreendimento = conversa.getEmpreendimento();
        return new ConversaResponseDTO(
                conversa.getId(),
                conversa.getTitulo(),
                empreendimento != null ? empreendimento.getId() : null,
                empreendimento != null ? empreendimento.getNome() : null,
                conversa.getCriadoEm(),
                conversa.getAtualizadoEm());
    }

    private MensagemResponseDTO toMensagemDTO(MensagemEntity mensagem) {
        return new MensagemResponseDTO(
                mensagem.getId(),
                mensagem.getPapel(),
                mensagem.getConteudo(),
                mensagem.getCriadoEm());
    }
}
