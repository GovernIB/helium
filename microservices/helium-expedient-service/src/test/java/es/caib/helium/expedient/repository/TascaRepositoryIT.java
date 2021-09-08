package es.caib.helium.expedient.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import es.caib.helium.expedient.ExpedientTestHelper;
import es.caib.helium.expedient.ProcesTestHelper;
import es.caib.helium.expedient.TascaTestHelper;
import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.domain.Proces;
import es.caib.helium.expedient.domain.Tasca;

@DataJpaTest
class TascaRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TascaRepository tascaRepository;

    private Expedient expedient;
    private Proces proces;
    private Tasca tasca;
    
    @BeforeEach
    void setUp() {
        expedient = entityManager.persistAndFlush(
        		ExpedientTestHelper.generateExpedient(1, 1L, 1L, 1L, "1", "1/2021", "Títol 1"));
        proces = entityManager.persistAndFlush(
        		ProcesTestHelper.generateProces(0, "1", expedient, "JPBM01", "dp1", "descripcio1", new Date()));
        tasca = TascaTestHelper.generateTasca(1, "1", proces, "nom 1", "títol 1");
    }


    // FindById
    // ------------------------------------------------------------------
    @Test
    @DisplayName("Consulta per id")
    void whenFindId_thenReturnTasca() {

        Tasca creat = entityManager.persistAndFlush(tasca);

        Optional<Tasca> trobat = tascaRepository.findByTascaId(
                creat.getTascaId());

        assertTrue(trobat.isPresent());
        TascaTestHelper.comprovaTasca(tasca, trobat.get());
    }

}