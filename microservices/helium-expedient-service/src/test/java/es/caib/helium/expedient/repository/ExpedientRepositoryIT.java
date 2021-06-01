package es.caib.helium.expedient.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import es.caib.helium.expedient.ExpedientTestHelper;
import es.caib.helium.expedient.domain.Expedient;

@DataJpaTest
class ExpedientRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ExpedientRepository expedientRepository;

    private Expedient expedient;
    private Expedient expedient2;

    @BeforeEach
    void setUp() {
        expedient = ExpedientTestHelper.generateExpedient(1, 1L, 1L, 1L, "1", "1/2021", "Títol 1");
        expedient2 = ExpedientTestHelper.generateExpedient(1, 2L, 2L, 2L, "2", "2/2021", "Títol 2");
    }

    // Expedients existents:
    // |---------------------|---------------------|
    // | Entorn 1            | Entorn 2            |
    // |---------------------|---------------------|
    // | Expedient tipus 1:  | Expedient tipus 2:  |
    // |---------------------|---------------------|
    // | Expedient 1:        | Expedient 2:        |
    // | - 1/2021            | - 2/2021            |
    // | - Títol 1           | - Títol 2           |
    // |---------------------|---------------------|


    // FindByEntornAndId
    // ------------------------------------------------------------------
    @Test
    @DisplayName("Consulta per id")
    void whenFindId_thenReturnExpedient() {

        Expedient creat = entityManager.persistAndFlush(expedient);

        Optional<Expedient> trobat = expedientRepository.findById(
                creat.getId());

        assertTrue(trobat.isPresent());
        ExpedientTestHelper.comprovaExpedient(expedient, trobat.get());
    }

}