package api.sistema.hidro.service;

import api.sistema.hidro.dto.TabelaNormativaDTO;

import java.util.Arrays;
import java.util.List;

/** Tabelas 1 a 8 da NBR 8160:1999, só para consulta: nenhum cálculo do sistema as usa. */
final class TabelasNbr8160 {

    private static final String TRACO = "—";

    private TabelasNbr8160() {
    }

    static List<TabelaNormativaDTO> todas(String grupo) {
        return List.of(
                tabela(grupo, "Unidades Hunter de contribuição (UHC) dos aparelhos", "Tabela 1",
                        List.of("Aparelho", "UHC", "DN do ramal de descarga (mm)"),
                        new String[][] {
                                {"Bacia sanitária", "6", "100"},
                                {"Banheira de residência", "2", "40"},
                                {"Bebedouro", "0,5", "40"},
                                {"Bidê", "1", "40"},
                                {"Chuveiro de residência", "2", "40"},
                                {"Chuveiro coletivo", "4", "40"},
                                {"Lavatório de residência", "1", "40"},
                                {"Lavatório geral", "2", "40"},
                                {"Mictório com válvula de descarga", "6", "75"},
                                {"Mictório com caixa de descarga", "5", "50"},
                                {"Mictório com descarga automática", "2", "40"},
                                {"Mictório com calha (por metro)", "2", "50"},
                                {"Pia de cozinha residencial", "3", "50"},
                                {"Pia de cozinha industrial", "4", "50"},
                                {"Tanque de lavar roupa", "3", "40"},
                                {"Máquina de lavar louças", "2", "50"},
                                {"Máquina de lavar roupas", "3", "50"}},
                        null),

                tabela(grupo, "UHC de aparelhos não relacionados na Tabela 1", "Tabela 2",
                        List.of("DN (mm)", "Número máximo de UHC"),
                        new String[][] {{"40", "2"}, {"50", "3"}, {"75", "5"}, {"100", "6"}},
                        "Vale pelo diâmetro do ramal de descarga do aparelho."),

                tabela(grupo, "Diâmetro mínimo dos ramais de esgoto", "Tabela 3",
                        List.of("DN (mm)", "Número máximo de UHC"),
                        new String[][] {{"40", "3"}, {"50", "6"}, {"75", "20"}, {"100", "160"}},
                        null),

                tabela(grupo, "Dimensionamento de ramais de ventilação", "Tabela 4",
                        List.of("Grupo de aparelhos", "UHC", "DN (mm)"),
                        new String[][] {
                                {"Sem vasos sanitários", "Até 12", "40"},
                                {"Sem vasos sanitários", "13 a 18", "50"},
                                {"Sem vasos sanitários", "19 a 36", "75"},
                                {"Com vasos sanitários", "Até 17", "50"},
                                {"Com vasos sanitários", "18 a 60", "75"}},
                        null),

                tabela(grupo, "Dimensionamento do tubo de queda", "Tabela 5",
                        List.of("DN (mm)", "Máx. UHC — prédio de até 3 pavimentos",
                                "Máx. UHC — prédio com mais de 3 pavimentos"),
                        new String[][] {
                                {"40", "4", "8"},
                                {"50", "10", "24"},
                                {"75", "30", "70"},
                                {"100", "240", "500"},
                                {"150", "960", "1.900"},
                                {"200", "2.200", "3.600"},
                                {"250", "3.800", "5.600"},
                                {"300", "6.000", "8.400"}},
                        null),

                tabela(grupo, "Distância máxima do sifão ao ramal de ventilação", "Tabela 6",
                        List.of("DN do ramal de descarga (mm)", "Distância máxima (m)"),
                        new String[][] {{"40", "1,00"}, {"50", "1,20"}, {"75", "1,80"}, {"100", "2,40"}},
                        null),

                tabela(grupo, "Dimensionamento da coluna e do barrilete de ventilação", "Tabela 7",
                        List.of("DN do tubo de queda (mm)", "UHC", "Vent. DN 30", "Vent. DN 40",
                                "Vent. DN 50", "Vent. DN 75", "Vent. DN 100", "Vent. DN 150",
                                "Vent. DN 200"),
                        new String[][] {
                                {"40", "8", "15", "46", TRACO, TRACO, TRACO, TRACO, TRACO},
                                {"40", "10", "9", "30", TRACO, TRACO, TRACO, TRACO, TRACO},
                                {"50", "12", "9", "23", "61", TRACO, TRACO, TRACO, TRACO},
                                {"50", "20", "8", "15", "46", TRACO, TRACO, TRACO, TRACO},
                                {"75", "10", TRACO, "13", "46", "317", TRACO, TRACO, TRACO},
                                {"75", "21", TRACO, "10", "33", "247", TRACO, TRACO, TRACO},
                                {"75", "53", TRACO, "8", "29", "207", TRACO, TRACO, TRACO},
                                {"75", "102", TRACO, "8", "26", "189", TRACO, TRACO, TRACO},
                                {"100", "43", TRACO, TRACO, "11", "76", "299", TRACO, TRACO},
                                {"100", "140", TRACO, TRACO, "8", "61", "226", TRACO, TRACO},
                                {"100", "320", TRACO, TRACO, "7", "52", "195", TRACO, TRACO},
                                {"100", "530", TRACO, TRACO, "6", "46", "177", TRACO, TRACO},
                                {"150", "500", TRACO, TRACO, TRACO, "10", "40", "305", TRACO},
                                {"150", "1.100", TRACO, TRACO, TRACO, "8", "31", "238", TRACO},
                                {"150", "2.000", TRACO, TRACO, TRACO, "7", "26", "201", TRACO},
                                {"150", "2.900", TRACO, TRACO, TRACO, "6", "23", "183", TRACO},
                                {"200", "1.800", TRACO, TRACO, TRACO, TRACO, "10", "73", "286"},
                                {"200", "3.400", TRACO, TRACO, TRACO, TRACO, "7", "57", "219"},
                                {"200", "5.600", TRACO, TRACO, TRACO, TRACO, "6", "49", "186"},
                                {"200", "7.600", TRACO, TRACO, TRACO, TRACO, "5", "43", "171"}},
                        "Comprimento máximo permitido da coluna de ventilação, em metros, para "
                                + "cada DN mínimo do tubo de ventilação. Traço: combinação não prevista."),

                tabela(grupo, "Dimensionamento de coletores e subcoletores", "Tabela 8",
                        List.of("DN (mm)", "Declividade 0,5%", "Declividade 1%", "Declividade 2%", "Declividade 4%"),
                        new String[][] {
                                {"100", TRACO, "180", "216", "250"},
                                {"150", TRACO, "700", "840", "1.000"},
                                {"200", "1.400", "1.600", "1.920", "2.300"},
                                {"250", "2.500", "2.900", "3.500", "4.200"},
                                {"300", "3.900", "4.600", "5.600", "6.700"},
                                {"400", "7.000", "8.300", "10.000", "12.000"}},
                        "Número máximo de UHC para cada declividade mínima do coletor."));
    }

    private static TabelaNormativaDTO tabela(String grupo, String titulo, String tabelaNorma,
                                             List<String> colunas, String[][] linhas, String nota) {
        return new TabelaNormativaDTO(grupo, titulo, "NBR 8160:1999, " + tabelaNorma, List.of(),
                colunas, Arrays.stream(linhas).map(List::of).toList(), nota);
    }
}
