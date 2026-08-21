package api.sistema.hidro.repository;

import api.sistema.hidro.entity.EmpreendimentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpreendimentoRepository extends JpaRepository<EmpreendimentoEntity, Long> {

    List<EmpreendimentoEntity> findByEmpresaIdAndAtivoTrue(Long empresaId);

    @Query("""
            select e from EmpreendimentoEntity e
            where e.empresa.id = :empresaId
              and e.ativo = true
              and lower(e.nome) like lower(concat('%', :busca, '%'))
            order by e.nome
            """)
    List<EmpreendimentoEntity> buscarPorEmpresa(@Param("empresaId") Long empresaId,
                                                @Param("busca") String busca);
}