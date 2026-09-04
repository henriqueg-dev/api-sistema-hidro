package api.sistema.hidro.service;

import api.sistema.hidro.enums.ContribuicaoDespejo;
import api.sistema.hidro.enums.FaixaTemperatura;
import api.sistema.hidro.enums.FormaTanque;
import api.sistema.hidro.enums.HidrometroPadrao;
import api.sistema.hidro.enums.TipoEmpreendimento;
import api.sistema.hidro.enums.UnidadeContribuicao;

import java.util.Map;

/**
 * Rótulos em português para os memoriais em PDF. Os enums aqui não carregam descrição (o
 * frontend mantém os próprios rótulos em constants/opcoes.js, sem afetar o JSON da API) —
 * esta classe espelha esses mesmos textos só para exibição no documento.
 */
final class RotulosPdf {

    private static final Map<HidrometroPadrao, String> HIDROMETROS = Map.ofEntries(
            Map.entry(HidrometroPadrao.QN_0_75, "0,75 m³/h"),
            Map.entry(HidrometroPadrao.QN_1_5, "1,5 m³/h"),
            Map.entry(HidrometroPadrao.QN_2_5, "2,5 m³/h"),
            Map.entry(HidrometroPadrao.QN_3_5, "3,5 m³/h"),
            Map.entry(HidrometroPadrao.QN_5, "5 m³/h"),
            Map.entry(HidrometroPadrao.QN_7, "7 m³/h"),
            Map.entry(HidrometroPadrao.QN_10, "10 m³/h"),
            Map.entry(HidrometroPadrao.QN_15, "15 m³/h"),
            Map.entry(HidrometroPadrao.QN_20, "20 m³/h"),
            Map.entry(HidrometroPadrao.QN_30, "30 m³/h"));

    private static final Map<ContribuicaoDespejo, String> CONTRIBUICOES_DESPEJO = Map.ofEntries(
            Map.entry(ContribuicaoDespejo.RESIDENCIA_PADRAO_ALTO, "Residência — padrão alto"),
            Map.entry(ContribuicaoDespejo.RESIDENCIA_PADRAO_MEDIO, "Residência — padrão médio"),
            Map.entry(ContribuicaoDespejo.RESIDENCIA_PADRAO_BAIXO, "Residência — padrão baixo"),
            Map.entry(ContribuicaoDespejo.HOTEL, "Hotel (sem lavanderia e cozinha)"),
            Map.entry(ContribuicaoDespejo.ALOJAMENTO_PROVISORIO, "Alojamento provisório"),
            Map.entry(ContribuicaoDespejo.FABRICA, "Fábrica em geral"),
            Map.entry(ContribuicaoDespejo.ESCRITORIO, "Escritório"),
            Map.entry(ContribuicaoDespejo.EDIFICIO_PUBLICO_COMERCIAL, "Edifício público ou comercial"),
            Map.entry(ContribuicaoDespejo.ESCOLA, "Escola (externato)"),
            Map.entry(ContribuicaoDespejo.RESTAURANTE, "Restaurante e similares"),
            Map.entry(ContribuicaoDespejo.BAR, "Bar"),
            Map.entry(ContribuicaoDespejo.CINEMA_TEATRO, "Cinema, teatro e locais de curta permanência"),
            Map.entry(ContribuicaoDespejo.SANITARIO_PUBLICO, "Sanitário público"));

    private static final Map<UnidadeContribuicao, String> UNIDADES_CONTRIBUICAO = Map.of(
            UnidadeContribuicao.PESSOA, "pessoas",
            UnidadeContribuicao.REFEICAO, "refeições",
            UnidadeContribuicao.LUGAR, "lugares",
            UnidadeContribuicao.BACIA_SANITARIA, "bacias sanitárias");

    private static final Map<FormaTanque, String> FORMAS_TANQUE = Map.of(
            FormaTanque.PRISMATICO_RETANGULAR, "Prismático retangular",
            FormaTanque.CILINDRICO, "Cilíndrico");

    private static final Map<FaixaTemperatura, String> FAIXAS_TEMPERATURA = Map.of(
            FaixaTemperatura.ATE_10, "Até 10 °C",
            FaixaTemperatura.DE_10_A_20, "Entre 10 °C e 20 °C",
            FaixaTemperatura.ACIMA_20, "Acima de 20 °C");

    private static final Map<String, String> CONCESSIONARIAS = Map.of(
            "DMAE", "DMAE — Uberlândia/MG",
            "SABESP", "SABESP — São Paulo/SP",
            "CESAMA", "CESAMA — Juiz de Fora/MG",
            "COPASA", "COPASA — Minas Gerais",
            "SANEAGO", "SANEAGO — Goiás",
            "SANEPAR", "SANEPAR — Paraná",
            "OUTRA", "Outra");

    private RotulosPdf() {
    }

    static String hidrometro(HidrometroPadrao valor) {
        return valor == null ? "" : HIDROMETROS.getOrDefault(valor, valor.name());
    }

    static String contribuicaoDespejo(ContribuicaoDespejo valor) {
        return valor == null ? "" : CONTRIBUICOES_DESPEJO.getOrDefault(valor, valor.name());
    }

    static String unidadeContribuicao(UnidadeContribuicao valor) {
        return valor == null ? "" : UNIDADES_CONTRIBUICAO.getOrDefault(valor, valor.name());
    }

    static String formaTanque(FormaTanque valor) {
        return valor == null ? "" : FORMAS_TANQUE.getOrDefault(valor, valor.name());
    }

    static String faixaTemperatura(FaixaTemperatura valor) {
        return valor == null ? "" : FAIXAS_TEMPERATURA.getOrDefault(valor, valor.name());
    }

    /** A concessionária vem do empreendimento como texto livre; mapeia quando bate uma conhecida. */
    static String concessionaria(String chave) {
        if (chave == null || chave.isBlank()) return "";
        return CONCESSIONARIAS.getOrDefault(chave, chave);
    }

    static String unidadeOrcamento(TipoEmpreendimento tipo) {
        return tipo == TipoEmpreendimento.PREDIO ? "apartamentos" : "m²";
    }
}
