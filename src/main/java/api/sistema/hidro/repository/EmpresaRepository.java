package api.sistema.hidro.repository;

import api.sistema.hidro.entity.EmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<EmpresaEntity, Long> {

    @Query("""
            select e from EmpresaEntity e
            where e.ativo = true
              and lower(e.nome) like lower(concat('%', :busca, '%'))
            order by e.nome
            """)
    List<EmpresaEntity> buscarPorNome(@Param("busca") String busca);
}