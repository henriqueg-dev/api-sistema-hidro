package api.sistema.hidro.repository;

import api.sistema.hidro.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

    @Query("""
            select e from ClienteEntity e
            where e.ativo = true
              and lower(e.nome) like lower(concat('%', :busca, '%'))
            order by e.nome
            """)
    List<ClienteEntity> buscarPorNome(@Param("busca") String busca);
}