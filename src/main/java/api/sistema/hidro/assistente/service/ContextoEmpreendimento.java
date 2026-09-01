package api.sistema.hidro.assistente.service;

import api.sistema.hidro.entity.CaixaGorduraEntity;
import api.sistema.hidro.entity.EmpreendimentoEntity;
import api.sistema.hidro.entity.PiscinaEntity;
import api.sistema.hidro.entity.RamalPredialEntity;
import api.sistema.hidro.entity.TanqueSepticoEntity;
import api.sistema.hidro.entity.VazaoPredialEntity;
import api.sistema.hidro.repository.CaixaGorduraRepository;
import api.sistema.hidro.repository.PiscinaRepository;
import api.sistema.hidro.repository.RamalPredialRepository;
import api.sistema.hidro.repository.TanqueSepticoRepository;
import api.sistema.hidro.repository.VazaoPredialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Descreve em texto os cálculos já salvos de um empreendimento, para o assistente responder
 * sobre eles. Cada cálculo novo do sistema entra aqui como um método próprio.
 */
@Component
@RequiredArgsConstructor
public class ContextoEmpreendimento {

    private final CaixaGorduraRepository caixaGorduraRepository;
    private final VazaoPredialRepository vazaoPredialRepository;
    private final RamalPredialRepository ramalPredialRepository;
    private final TanqueSepticoRepository tanqueSepticoRepository;
    private final PiscinaRepository piscinaRepository;

    public String montar(EmpreendimentoEntity empreendimento) {
        StringBuilder contexto = new StringBuilder();
        Long id = empreendimento.getId();

        identificacao(contexto, empreendimento);

        boolean temCalculo = caixasGordura(contexto, id);
        temCalculo |= vazoesPrediais(contexto, id);
        temCalculo |= ramaisPrediais(contexto, id);
        temCalculo |= tanquesSepticos(contexto, id);
        temCalculo |= piscinas(contexto, id);

        if (!temCalculo) {
            contexto.append("\nNenhum cálculo foi salvo para este empreendimento ainda.\n");
        }

        return contexto.toString();
    }

    private void identificacao(StringBuilder contexto, EmpreendimentoEntity empreendimento) {
        contexto.append("# Empreendimento em discussão\n\n")
                .append("- Nome: ").append(empreendimento.getNome()).append('\n')
                .append("- Tipo: ").append(empreendimento.getTipo()).append('\n')
                .append("- Pavimentos: ").append(empreendimento.getNumPavimentos()).append('\n')
                .append("- Endereço: ").append(empreendimento.getEndereco()).append('\n')
                .append("- Concessionária: ").append(empreendimento.getConcessionaria()).append('\n');
    }

    private boolean caixasGordura(StringBuilder contexto, Long empreendimentoId) {
        List<CaixaGorduraEntity> caixas =
                caixaGorduraRepository.findByEmpreendimentoIdOrderByCriadoEmAsc(empreendimentoId);

        if (caixas.isEmpty()) return false;

        contexto.append("\n## Cálculos de caixa de gordura e sabão (V = 2xN + 20)\n\n");
        for (int i = 0; i < caixas.size(); i++) {
            CaixaGorduraEntity caixa = caixas.get(i);
            contexto.append(i + 1).append(". Taxa de ocupação ").append(caixa.getTaxaOcupacao())
                    .append(" hab/apto, ").append(caixa.getNumApartamentos()).append(" apartamentos, ")
                    .append("população ").append(caixa.getPopulacao()).append(" hab, ")
                    .append("volume ").append(caixa.getVolumeLitros()).append(" L\n");
        }
        return true;
    }

    private boolean vazoesPrediais(StringBuilder contexto, Long empreendimentoId) {
        List<VazaoPredialEntity> vazoes =
                vazaoPredialRepository.findByEmpreendimentoIdOrderByCriadoEmAsc(empreendimentoId);

        if (vazoes.isEmpty()) return false;

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
        return true;
    }

    private boolean ramaisPrediais(StringBuilder contexto, Long empreendimentoId) {
        List<RamalPredialEntity> ramais =
                ramalPredialRepository.findByEmpreendimentoIdOrderByCriadoEmAsc(empreendimentoId);

        if (ramais.isEmpty()) return false;

        contexto.append("\n## Ramal predial e hidrômetro (NBR 5626)\n\n");
        for (RamalPredialEntity ramal : ramais) {
            contexto.append("- População ").append(ramal.getPopulacao())
                    .append(", consumo per capita ").append(ramal.getConsumoPerCapita())
                    .append(" L/hab.dia, consumo diário ").append(ramal.getConsumoDiarioM3())
                    .append(" m3 (").append(ramal.getConsumoMensalM3()).append(" m3/mes), ")
                    .append("reposição em ").append(ramal.getTempoReposicaoH()).append(" h, ")
                    .append("vazão de projeto ").append(ramal.getVazaoProjetoM3h()).append(" m3/h (")
                    .append(ramal.getVazaoProjetoLs()).append(" L/s), ")
                    .append("diâmetro teórico ").append(ramal.getDiametroTeoricoMm()).append(" mm, ")
                    .append("adotado DN ").append(ramal.getDnAdotadoMm()).append(" mm a ")
                    .append(ramal.getVelocidadeMs()).append(" m/s, ")
                    .append("hidrômetro Qn ").append(ramal.getHidrometro().getVazaoNominalM3h())
                    .append(" m3/h\n");
        }
        return true;
    }

    private boolean tanquesSepticos(StringBuilder contexto, Long empreendimentoId) {
        List<TanqueSepticoEntity> tanques =
                tanqueSepticoRepository.findByEmpreendimentoIdOrderByCriadoEmAsc(empreendimentoId);

        if (tanques.isEmpty()) return false;

        contexto.append("\n## Cálculos de tanque séptico (V = 1000 + N x (C x T + K x Lf))\n\n");
        for (int i = 0; i < tanques.size(); i++) {
            TanqueSepticoEntity tanque = tanques.get(i);
            contexto.append(i + 1).append(". Taxa de ocupação ").append(tanque.getTaxaOcupacao())
                    .append(", ").append(tanque.getNumUnidades()).append(" unidades, ")
                    .append("população ").append(tanque.getPopulacao()).append(", ")
                    .append("contribuição ").append(tanque.getContribuicaoDespejo().name())
                    .append(" (").append(tanque.getContribuicaoDiariaLitros()).append(" L/dia), ")
                    .append("detenção ").append(tanque.getPeriodoDetencaoDias()).append(" dia(s), ")
                    .append("temperatura ").append(tanque.getFaixaTemperatura().name())
                    .append(" com limpeza a cada ").append(tanque.getIntervaloLimpezaAnos())
                    .append(" ano(s) e taxa de acumulação ").append(tanque.getTaxaAcumulacaoDias())
                    .append(" dias, volume calculado ").append(tanque.getVolumeCalculadoLitros())
                    .append(" L, volume útil adotado ").append(tanque.getVolumeLitros()).append(" L");

            geometriaTanque(contexto, tanque);
            contexto.append('\n');
        }
        return true;
    }

    private void geometriaTanque(StringBuilder contexto, TanqueSepticoEntity tanque) {
        if (tanque.getFormaTanque() == null) {
            contexto.append(", sem geometria definida");
            return;
        }

        contexto.append(", forma ").append(tanque.getFormaTanque().name())
                .append(", profundidade útil ").append(tanque.getProfundidadeUtilM()).append(" m");

        if (tanque.getDiametroM() != null) {
            contexto.append(", diâmetro ").append(tanque.getDiametroM()).append(" m");
        } else {
            contexto.append(", ").append(tanque.getLarguraM()).append(" x ")
                    .append(tanque.getComprimentoM()).append(" m");
        }
    }

    private boolean piscinas(StringBuilder contexto, Long empreendimentoId) {
        List<PiscinaEntity> piscinas =
                piscinaRepository.findByEmpreendimentoIdOrderByCriadoEmAsc(empreendimentoId);

        if (piscinas.isEmpty()) return false;

        contexto.append("\n## Piscinas (NBR 10339)\n\n");
        for (PiscinaEntity piscina : piscinas) {
            contexto.append("- ").append(piscina.getNome())
                    .append(" (").append(piscina.getTipoUso().getDescricao()).append("): ")
                    .append(piscina.getLarguraM()).append(" x ").append(piscina.getComprimentoM())
                    .append(" x ").append(piscina.getProfundidadeM()).append(" m, ")
                    .append("área ").append(piscina.getAreaM2()).append(" m2, ")
                    .append("volume ").append(piscina.getVolumeM3()).append(" m3, ")
                    .append("filtração em ").append(piscina.getTempoFiltracaoH()).append(" h, ")
                    .append("vazão de projeto ").append(piscina.getVazaoProjetoM3h()).append(" m3/h, ")
                    .append("bomba ").append(piscina.getVazaoBombaM3h()).append(" m3/h a ")
                    .append(piscina.getAlturaManometricaMca()).append(" mca, ")
                    .append("DN recalque ").append(piscina.getDnRecalqueMm()).append(" mm (")
                    .append(String.format("%.2f", piscina.getVelocidadeRecalqueMs())).append(" m/s), ")
                    .append("DN sucção ").append(piscina.getDnSuccaoMm()).append(" mm (")
                    .append(String.format("%.2f", piscina.getVelocidadeSuccaoMs())).append(" m/s), ")
                    .append(piscina.getNumBocaisRetornoAdotado()).append(" bocais, ")
                    .append(piscina.getNumSkimmersAdotado()).append(" skimmers, ")
                    .append(piscina.getNumRalosAdotado()).append(" ralos, ")
                    .append(piscina.getNumAspiradores()).append(" aspiradores");

            if (piscina.getPressaoResidualMca() != null) {
                contexto.append(", pressão residual no bocal mais desfavorável ")
                        .append(String.format("%.2f", piscina.getPressaoResidualMca())).append(" mca");
            } else {
                contexto.append(", sem trechos lançados (pressão residual não calculada)");
            }

            contexto.append('\n');
        }
        return true;
    }
}
