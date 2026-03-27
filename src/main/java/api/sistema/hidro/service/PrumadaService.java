package api.sistema.hidro.service;

import api.sistema.hidro.dto.PrumadaConsultaDTO;
import api.sistema.hidro.dto.PrumadaRespostaDTO;
import api.sistema.hidro.model.PrumadaModel;
import api.sistema.hidro.repository.PrumadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrumadaService {

    private final PrumadaRepository prumadaRepository;

    public PrumadaRespostaDTO consultar(PrumadaConsultaDTO dto) {
        String faixa = resolverFaixaPavimentos(dto.getTipo(), dto.getNumPavimentos());

        PrumadaModel prumada = prumadaRepository
                .findByTipoAndNumPavimentosAndDesconectorAndCondicaoSanca(
                        dto.getTipo(),
                        faixa,
                        dto.getDesconector(),
                        dto.getCondicaoSanca())
                .orElseThrow(() -> new RuntimeException(
                        "Nenhuma configuração encontrada para os parâmetros informados"));

        return new PrumadaRespostaDTO(
                prumada.getTipo(),
                prumada.getNumPavimentos(),
                prumada.getDesconector(),
                prumada.getCondicaoSanca(),
                prumada.getDescricao());
    }

    private String resolverFaixaPavimentos(String tipo, Integer num) {
        if (tipo.equals("COZINHA")) {
            if (num <= 9) return "ATE_9";
            if (num <= 16) return "ATE_16";
            if (num <= 18) return "ATE_18";
            return "ACIMA_18";
        } else {
            if (num <= 5) return "ATE_5";
            if (num <= 9) return "ATE_9";
            if (num <= 16) return "ATE_16";
            if (num <= 18) return "ATE_18";
            return "ACIMA_18";
        }
    }

    public List<PrumadaModel> listarTodos(String tipo) {
        return prumadaRepository.findByTipoAndAtivoTrue(tipo);
    }
}