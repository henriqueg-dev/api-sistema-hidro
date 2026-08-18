package api.sistema.hidro.repository;

import api.sistema.hidro.enums.CondicaoSanca;
import api.sistema.hidro.enums.FaixaPavimentos;
import api.sistema.hidro.enums.TipoPrumada;
import api.sistema.hidro.entity.PrumadaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrumadaRepository extends JpaRepository<PrumadaEntity, Long> {

    List<PrumadaEntity> findByTipoAndAtivoTrue(TipoPrumada tipo);

    Optional<PrumadaEntity> findByTipoAndNumPavimentosAndDesconectorAndCondicaoSanca(
            TipoPrumada tipo,
            FaixaPavimentos numPavimentos,
            String desconector,
            CondicaoSanca condicaoSanca);
}
