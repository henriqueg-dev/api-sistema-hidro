package api.sistema.hidro.catalogo.service;

import api.sistema.hidro.catalogo.entity.ContaEntity;
import api.sistema.hidro.catalogo.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Religa o DataSource de cada conta já existente assim que a aplicação sobe. */
@Component
@RequiredArgsConstructor
public class TenantBootstrapRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TenantBootstrapRunner.class);

    private final ContaRepository contaRepository;
    private final TenantProvisionamentoService provisionamentoService;

    @Override
    public void run(ApplicationArguments args) {
        for (ContaEntity conta : contaRepository.findAll()) {
            provisionamentoService.reconectar(conta.getId());
        }
        log.info("{} conta(s) religada(s) no startup", contaRepository.count());
    }
}
