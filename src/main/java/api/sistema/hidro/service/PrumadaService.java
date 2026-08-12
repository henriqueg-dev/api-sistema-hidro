package api.sistema.hidro.service;

import api.sistema.hidro.dto.PrumadaConsultaDTO;
import api.sistema.hidro.dto.PrumadaRespostaDTO;
import api.sistema.hidro.enums.FaixaPavimentos;
import api.sistema.hidro.enums.TipoPrumada;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.model.PrumadaModel;
import api.sistema.hidro.repository.PrumadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrumadaService {

    private final PrumadaRepository prumadaRepository;

    public PrumadaRespostaDTO consultar(PrumadaConsultaDTO dto) {
        FaixaPavimentos faixa = FaixaPavimentos.resolver(dto.getTipo(), dto.getNumPavimentos());

        PrumadaModel prumada = prumadaRepository
                .findByTipoAndNumPavimentosAndDesconectorAndCondicaoSanca(
                        dto.getTipo(),
                        faixa,
                        dto.getDesconector(),
                        dto.getCondicaoSanca())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhuma configuração encontrada para os parâmetros informados"));

        return new PrumadaRespostaDTO(
                prumada.getTipo(),
                prumada.getNumPavimentos(),
                prumada.getDesconector(),
                prumada.getCondicaoSanca(),
                prumada.getDescricao());
    }

    public List<PrumadaRespostaDTO> listarTodos(TipoPrumada tipo) {
        return prumadaRepository.findByTipoAndAtivoTrue(tipo).stream()
                .map(prumada -> new PrumadaRespostaDTO(
                        prumada.getTipo(),
                        prumada.getNumPavimentos(),
                        prumada.getDesconector(),
                        prumada.getCondicaoSanca(),
                        prumada.getDescricao()))
                .toList();
    }
}
