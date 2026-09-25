package api.sistema.hidro.service;

import api.sistema.hidro.dto.PiscinaReferenciasDTO;
import api.sistema.hidro.dto.TabelaNormativaDTO;
import api.sistema.hidro.enums.ContribuicaoDespejo;
import api.sistema.hidro.enums.DiametroPiscina;
import api.sistema.hidro.enums.DiametroRamal;
import api.sistema.hidro.enums.FaixaTemperatura;
import api.sistema.hidro.enums.HidrometroPadrao;
import api.sistema.hidro.enums.TipoConexao;
import api.sistema.hidro.enums.TipoUsoPiscina;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Tabelas de consulta montadas a partir das mesmas constantes que os cálculos usam, para a
 * tela nunca divergir do que o sistema de fato calcula.
 */
@Service
@RequiredArgsConstructor
public class TabelaNormativaService {

    private static final Locale PT_BR = Locale.of("pt", "BR");

    private static final String GRUPO_AGUA_FRIA = "Água fria — NBR 5626";
    private static final String GRUPO_ESGOTO = "Esgoto sanitário — NBR 8160";
    private static final String GRUPO_TANQUE = "Tanque séptico — NBR 7229";
    private static final String GRUPO_SUMIDOURO = "Sumidouro — NBR 13969";
    private static final String GRUPO_PISCINA = "Piscinas — NBR 10339";

    private static final String VAZAO_PREDIAL = "Vazão predial";
    private static final String RAMAL_PREDIAL = "Ramal predial e hidrômetro";
    private static final String CAIXA_GORDURA = "Caixa de gordura e sabão";
    private static final String TANQUE_SEPTICO = "Tanque séptico";
    private static final String PISCINA = "Piscina";
    private static final String RECALQUE = "Recalque";
    private static final String SUMIDOURO = "Sumidouro";

    private final PiscinaService piscinaService;

    public List<TabelaNormativaDTO> listar() {
        PiscinaReferenciasDTO piscina = piscinaService.referencias();

        List<TabelaNormativaDTO> tabelas = new ArrayList<>(List.of(
                tubosPvc(),
                hidrometros(),
                limitesAlimentador(),
                coeficientesVazaoPredial(),
                parametrosRecalque()));
        tabelas.addAll(TabelasNbr8160.todas(GRUPO_ESGOTO));
        tabelas.addAll(List.of(
                caixaGordura(),
                contribuicaoDespejos(),
                periodoDetencao(),
                acumulacaoLodo(),
                profundidadeUtil(),
                parametrosTanque(),
                taxaAplicacaoSumidouro(),
                tempoFiltracao(),
                diametrosPiscina(piscina),
                comprimentoEquivalente()));
        return tabelas;
    }

    private TabelaNormativaDTO tubosPvc() {
        List<List<String>> linhas = new ArrayList<>();
        for (DiametroRamal diametro : DiametroRamal.values()) {
            linhas.add(List.of("DN " + diametro.getDn(), num(diametro.getDiametroInternoMm())));
        }
        return new TabelaNormativaDTO(GRUPO_AGUA_FRIA, "Tubos de PVC soldável",
                "Catálogo do fabricante", List.of(RAMAL_PREDIAL, PISCINA),
                List.of("Diâmetro nominal (mm)", "Diâmetro interno (mm)"), linhas,
                "O cálculo usa o diâmetro interno; o DN é o que se compra.");
    }

    private TabelaNormativaDTO hidrometros() {
        List<List<String>> linhas = new ArrayList<>();
        for (HidrometroPadrao hidrometro : HidrometroPadrao.values()) {
            linhas.add(List.of(num(hidrometro.getVazaoNominalM3h()), num(hidrometro.getVazaoMaximaM3h())));
        }
        return new TabelaNormativaDTO(GRUPO_AGUA_FRIA, "Hidrômetros padronizados",
                "ABNT NBR 14005 / ISO 4064", List.of(RAMAL_PREDIAL),
                List.of("Vazão nominal Qn (m³/h)", "Vazão máxima (m³/h)"), linhas,
                "O sistema sugere o menor medidor que cobre a vazão de projeto; a escolha final "
                        + "segue a tabela da concessionária.");
    }

    private TabelaNormativaDTO limitesAlimentador() {
        List<List<String>> linhas = List.of(
                List.of("Tempo máximo de reposição — residência unifamiliar",
                        CalculoRamalPredial.TEMPO_REPOSICAO_UNIFAMILIAR_H + " h", "NBR 5626, item 6.7"),
                List.of("Tempo máximo de reposição — demais edificações",
                        CalculoRamalPredial.TEMPO_REPOSICAO_MAXIMO_H + " h", "NBR 5626, item 6.7"),
                List.of("Velocidade máxima padrão",
                        num(CalculoRamalPredial.VELOCIDADE_MAXIMA_PADRAO_MS) + " m/s",
                        "NBR 5626, nota do item 6.8.3"));
        return new TabelaNormativaDTO(GRUPO_AGUA_FRIA, "Limites do alimentador predial",
                "NBR 5626", List.of(RAMAL_PREDIAL),
                List.of("Parâmetro", "Valor", "Origem"), linhas, null);
    }

    private TabelaNormativaDTO coeficientesVazaoPredial() {
        List<List<String>> linhas = List.of(
                List.of("K1 — dia de maior consumo", num(VazaoPredialService.K1), "Adotado no sistema"),
                List.of("K2 — hora de maior consumo", num(VazaoPredialService.K2), "Adotado no sistema"),
                List.of("Reservatório inferior", percentual(VazaoPredialService.FRACAO_INFERIOR),
                        "Prática de projeto"),
                List.of("Reservatório superior", percentual(VazaoPredialService.FRACAO_SUPERIOR),
                        "Prática de projeto"));
        return new TabelaNormativaDTO(GRUPO_AGUA_FRIA, "Coeficientes da vazão predial",
                "Constantes do cálculo", List.of(VAZAO_PREDIAL),
                List.of("Parâmetro", "Valor", "Origem"), linhas,
                "A NBR 5626 fixa a reserva mínima de 24 h de consumo, mas não como dividi-la "
                        + "entre os reservatórios.");
    }

    private TabelaNormativaDTO parametrosRecalque() {
        List<List<String>> linhas = new ArrayList<>(List.of(
                List.of("Vazão mínima de recalque",
                        percentual(CalculoRecalque.FRACAO_MINIMA_HORARIA) + " do consumo diário por hora",
                        "Critério usual de projeto"),
                List.of("Diâmetro de recalque (Forchheimer)",
                        "D = " + num(CalculoRecalque.FORCHHEIMER_C) + " × (h/24)^¼ × √Q", "Literatura técnica"),
                List.of("Diâmetro de sucção", "Diâmetro comercial seguinte ao do recalque", "Literatura técnica"),
                List.of("Velocidade máxima", num(CalculoRecalque.VELOCIDADE_MAXIMA_MS) + " m/s", "NBR 5626")));
        double inicio = 0;
        for (double[] faixa : CalculoRecalque.FOLGAS) {
            linhas.add(List.of("Folga do motor — " + num(inicio) + " a " + num(faixa[0]) + " cv",
                    num(faixa[1]) + "%", "Prática de projeto"));
            inicio = faixa[0];
        }
        linhas.add(List.of("Folga do motor — acima de " + num(inicio) + " cv",
                CalculoRecalque.FOLGA_ACIMA + "%", "Prática de projeto"));

        return new TabelaNormativaDTO(GRUPO_AGUA_FRIA, "Instalação de recalque",
                "NBR 5626 e literatura técnica", List.of(RECALQUE),
                List.of("Parâmetro", "Valor", "Origem"), linhas,
                "A folga sobre a potência não é normativa e varia entre fontes acima de 5 cv; "
                        + "confirme o motor na curva do fabricante.");
    }

    private TabelaNormativaDTO caixaGordura() {
        List<List<String>> linhas = List.of(
                List.of("Volume da caixa", CaixaGorduraService.FORMULA + " (L)",
                        "NBR 8160, caixa de gordura especial"),
                List.of("N — população atendida", "taxa de ocupação × número de apartamentos",
                        "Dado do empreendimento"));
        return new TabelaNormativaDTO(GRUPO_ESGOTO, "Caixa de gordura e sabão", "NBR 8160",
                List.of(CAIXA_GORDURA), List.of("Parâmetro", "Valor", "Origem"), linhas, null);
    }

    private TabelaNormativaDTO contribuicaoDespejos() {
        List<List<String>> linhas = new ArrayList<>();
        for (ContribuicaoDespejo tipo : ContribuicaoDespejo.values()) {
            linhas.add(List.of(
                    RotulosPdf.contribuicaoDespejo(tipo),
                    RotulosPdf.unidadeContribuicao(tipo.getUnidade()),
                    num(tipo.getContribuicaoLitros()),
                    num(tipo.getLodoFrescoLitros())));
        }
        return new TabelaNormativaDTO(GRUPO_TANQUE, "Contribuição diária de esgoto (C) e de lodo fresco (Lf)",
                "NBR 7229, Tabela 1", List.of(TANQUE_SEPTICO),
                List.of("Prédio", "Unidade (N)", "C (L/unidade·dia)", "Lf (L/unidade·dia)"), linhas,
                "A unidade nem sempre é pessoa: restaurante conta refeições, cinema conta lugares.");
    }

    private TabelaNormativaDTO periodoDetencao() {
        List<List<String>> linhas = new ArrayList<>();
        int inicio = 0;
        for (TanqueSepticoService.FaixaDetencao faixa : TanqueSepticoService.PERIODOS_DETENCAO) {
            String contribuicao = inicio == 0
                    ? "Até " + num(faixa.ateLitrosDia())
                    : num(inicio + 1) + " a " + num(faixa.ateLitrosDia());
            linhas.add(List.of(contribuicao, num(faixa.dias(), 2), horas(faixa.dias())));
            inicio = faixa.ateLitrosDia();
        }
        double acima = TanqueSepticoService.PERIODO_DETENCAO_ACIMA_DIAS;
        linhas.add(List.of("Acima de " + num(inicio), num(acima, 2), horas(acima)));

        return new TabelaNormativaDTO(GRUPO_TANQUE, "Período de detenção (T)", "NBR 7229, Tabela 2",
                List.of(TANQUE_SEPTICO), List.of("Contribuição diária (L)", "T (dias)", "T (horas)"),
                linhas, null);
    }

    private TabelaNormativaDTO acumulacaoLodo() {
        List<String> colunas = new ArrayList<>(List.of("Intervalo entre limpezas"));
        for (FaixaTemperatura faixa : FaixaTemperatura.values()) {
            colunas.add(RotulosPdf.faixaTemperatura(faixa));
        }

        List<List<String>> linhas = new ArrayList<>();
        for (int anos = 1; anos <= 5; anos++) {
            List<String> linha = new ArrayList<>(List.of(anos + (anos == 1 ? " ano" : " anos")));
            for (FaixaTemperatura faixa : FaixaTemperatura.values()) {
                linha.add(num(faixa.taxaAcumulacaoLodo(anos)));
            }
            linhas.add(linha);
        }
        return new TabelaNormativaDTO(GRUPO_TANQUE, "Taxa de acumulação total de lodo (K), em dias",
                "NBR 7229, Tabela 3", List.of(TANQUE_SEPTICO), colunas, linhas,
                "A temperatura é a ambiente média do mês mais frio.");
    }

    private TabelaNormativaDTO profundidadeUtil() {
        List<List<String>> linhas = new ArrayList<>();
        Double anterior = null;
        for (GeometriaTanqueSeptico.Faixa.PorVolume linha : GeometriaTanqueSeptico.Faixa.POR_VOLUME) {
            String volume;
            if (linha.ateM3() == null) volume = "Acima de " + num(anterior);
            else if (anterior == null) volume = "Até " + num(linha.ateM3());
            else volume = num(anterior) + " a " + num(linha.ateM3());

            linhas.add(List.of(volume, num(linha.faixa().minimoM(), 2), num(linha.faixa().maximoM(), 2)));
            anterior = linha.ateM3();
        }
        return new TabelaNormativaDTO(GRUPO_TANQUE, "Profundidade útil", "NBR 7229, Tabela 4",
                List.of(TANQUE_SEPTICO),
                List.of("Volume útil (m³)", "Profundidade mínima (m)", "Profundidade máxima (m)"),
                linhas, null);
    }

    private TabelaNormativaDTO parametrosTanque() {
        List<List<String>> linhas = List.of(
                List.of("Volume útil", TanqueSepticoService.FORMULA + " (L)", "NBR 7229"),
                List.of("Volume útil mínimo", num(TanqueSepticoService.VOLUME_MINIMO_LITROS) + " L",
                        "Prática de projeto; não consta da NBR 7229"),
                List.of("Contribuição diária máxima do método",
                        num(TanqueSepticoService.VAZAO_MAXIMA_LITROS_DIA) + " L/dia",
                        "Limite adotado no sistema"));
        return new TabelaNormativaDTO(GRUPO_TANQUE, "Parâmetros do dimensionamento",
                "NBR 7229 e prática de projeto", List.of(TANQUE_SEPTICO),
                List.of("Parâmetro", "Valor", "Origem"), linhas, null);
    }

    private TabelaNormativaDTO taxaAplicacaoSumidouro() {
        List<List<String>> linhas = new ArrayList<>();
        for (int i = 0; i < CalculoSumidouro.TABELA_A1.size(); i++) {
            CalculoSumidouro.PontoTabela ponto = CalculoSumidouro.TABELA_A1.get(i);
            String percolacao = num(ponto.percolacaoMinM()) + (i == 0 ? " ou menos" : "");
            linhas.add(List.of(percolacao, taxa(ponto.taxaAplicacao())));
        }
        return new TabelaNormativaDTO(GRUPO_SUMIDOURO, "Taxa máxima de aplicação diária",
                "NBR 13969, Tabela A.1", List.of(SUMIDOURO),
                List.of("Taxa de percolação (min/m)", "Taxa de aplicação (m³/m²·dia)"), linhas,
                "Entre as linhas, interpolar. Acima de "
                        + num(CalculoSumidouro.TABELA_A1.get(CalculoSumidouro.TABELA_A1.size() - 1).percolacaoMinM())
                        + " min/m o solo não comporta sumidouro.");
    }

    private TabelaNormativaDTO tempoFiltracao() {
        List<List<String>> linhas = new ArrayList<>();
        for (TipoUsoPiscina tipo : TipoUsoPiscina.values()) {
            linhas.add(List.of(
                    tipo.getDescricao(),
                    tipo.tempoMaximoFiltracaoH(0.5) + " h",
                    tipo.tempoMaximoFiltracaoH(1.0) + " h",
                    tipo.tempoMaximoFiltracaoH(2.0) + " h"));
        }
        return new TabelaNormativaDTO(GRUPO_PISCINA, "Tempo máximo de filtração", "NBR 10339, Tabela 1",
                List.of(PISCINA),
                List.of("Tipologia", "Até 0,60 m", "0,60 a 1,50 m", "Acima de 1,50 m"), linhas,
                "As colunas são a profundidade da piscina.");
    }

    private TabelaNormativaDTO diametrosPiscina(PiscinaReferenciasDTO referencias) {
        List<Map<String, Object>> recalque = referencias.getFaixasRecalque();
        List<Map<String, Object>> succao = referencias.getFaixasSuccao();

        List<List<String>> linhas = new ArrayList<>();
        for (int i = 0; i < recalque.size(); i++) {
            linhas.add(List.of("DN " + recalque.get(i).get("dn"), faixa(recalque.get(i)), faixa(succao.get(i))));
        }
        return new TabelaNormativaDTO(GRUPO_PISCINA, "Diâmetro por vazão da bomba",
                "Faixas aproximadas da NBR 10339, Tabela 3", List.of(PISCINA),
                List.of("Diâmetro", "Recalque (m³/h)", "Sucção (m³/h)"), linhas,
                "Velocidade máxima de " + num(DiametroPiscina.VELOCIDADE_MAX_SUCCAO) + " m/s na sucção e "
                        + num(DiametroPiscina.VELOCIDADE_MAX_RECALQUE) + " m/s no recalque; o cálculo "
                        + "confere a velocidade real e alerta quando a faixa estoura.");
    }

    private TabelaNormativaDTO comprimentoEquivalente() {
        List<String> colunas = new ArrayList<>(List.of("Conexão"));
        for (int dn : TipoConexao.diametrosTabelados()) {
            colunas.add("DN " + dn);
        }

        List<List<String>> linhas = new ArrayList<>();
        for (TipoConexao tipo : TipoConexao.values()) {
            List<String> linha = new ArrayList<>(List.of(tipo.getDescricao()));
            for (double comprimento : tipo.comprimentosTabelados()) {
                linha.add(num(comprimento));
            }
            linhas.add(linha);
        }
        return new TabelaNormativaDTO(GRUPO_PISCINA, "Comprimento equivalente das conexões (m)",
                "Tabela de perdas localizadas para PVC", List.of(PISCINA), colunas, linhas,
                "Metros de tubo reto que causam a mesma perda de carga. DN 200 a 300 são "
                        + "extrapolados na tabela de origem; nos trechos só entram DN 50 a 110.");
    }

    private static String faixa(Map<String, Object> faixa) {
        return num(((Number) faixa.get("de")).doubleValue()) + " a "
                + num(((Number) faixa.get("ate")).doubleValue());
    }

    /** A Tabela 2 traz o período também em horas inteiras. */
    private static String horas(double dias) {
        return String.valueOf(Math.round(dias * 24));
    }

    /** Taxas da Tabela A.1 com ao menos duas casas, como na norma: 0,20 e 0,065. */
    private static String taxa(double valor) {
        return new DecimalFormat("0.00#", DecimalFormatSymbols.getInstance(PT_BR)).format(valor);
    }

    private static String percentual(double fracao) {
        return num(fracao * 100) + "%";
    }

    private static String num(double valor, int casas) {
        return String.format(PT_BR, "%." + casas + "f", valor);
    }

    /** DecimalFormat não é thread-safe: um por chamada. */
    private static String num(double valor) {
        return new DecimalFormat("#,##0.###", DecimalFormatSymbols.getInstance(PT_BR)).format(valor);
    }
}
