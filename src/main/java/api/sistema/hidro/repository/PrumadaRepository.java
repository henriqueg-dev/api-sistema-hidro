package api.sistema.hidro.repository;

import api.sistema.hidro.model.PrumadaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrumadaRepository extends JpaRepository<PrumadaModel, Long> {

    List<PrumadaModel> findByTipoAndAtivoTrue(String tipo);

    Optional<PrumadaModel> findByTipoAndNumPavimentosAndDesconectorAndCondicaoSanca(
            String tipo,
            String numPavimentos,
            String desconector,
            String condicaoSanca);
}