package es.caib.helium.domini.repository;

import es.caib.helium.domini.DominiTestHelper;
import es.caib.helium.domini.domain.Domini;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class DominiRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DominiRepository dominiRepository;

    private Domini domini;
    private Domini domini2;

    @BeforeEach
    void setUp() {
        domini = DominiTestHelper.generateDomini(1, "Domini_codi", 1L, 2L);
        domini2 = DominiTestHelper.generateDomini(2, "Domini_codi", 2L, 3L);
    }

    // Dominis existents:
    // |------------------|-----------------|
    // | Entorn 1         | Entorn 2        |
    // |------------------|-----------------|
    // | Domini 1:        | Domini 2:       |
    // | - Domini_codi    | - Domini_codi   |
    // | - expTip 2       | - expTip 3      |
    // |------------------|-----------------|


    // FindByEntornAndId
    // ------------------------------------------------------------------
    @Test
    @DisplayName("Consulta per entorn i id")
    void whenFindByEntornAndId_thenReturnDomini() {

        Domini creat = entityManager.persistAndFlush(domini);

        Optional<Domini> trobat = dominiRepository.findByEntornAndId(
                domini.getEntorn(),
                creat.getId());

        assertTrue(trobat.isPresent());
        comprovaDomini(domini, trobat.get());
    }

    @Test
    @DisplayName("Consulta per entorn i id inexistent")
    void whenInvalidEntornAndId_thenReturnNull() {
        Optional<Domini> trobat = dominiRepository.findByEntornAndId(0L, 0L);
        assertTrue(trobat.isEmpty());
    }

    // FindByEntornAndCodi
    // ------------------------------------------------------------------
    @Test
    @DisplayName("Consulta per entorn i codi")
    void whenFindByEntornAndCodi_thenReturnDomini() {

        domini.setExpedientTipus(null);
        entityManager.persistAndFlush(domini);

        Optional<Domini> trobat = dominiRepository.findByEntornAndCodi(
                domini.getEntorn(),
                domini.getCodi());

        assertTrue(trobat.isPresent());
        comprovaDomini(domini, trobat.get());
    }

    @Test
    @DisplayName("Consulta per entorn i codi inexistent")
    void whenInvalidEntornAndCodi_thenReturnNull() {
        Optional<Domini> trobat = dominiRepository.findByEntornAndCodi(0L, "Codi_inexistent");
        assertTrue(trobat.isEmpty());
    }

    // FindByExpedientTipusAndCodi
    // ------------------------------------------------------------------
    @Test
    @DisplayName("Consulta per expedient tipus i codi")
    void whenFindByExpedientTipusAndCodi_thenReturnDomini() {

        entityManager.persistAndFlush(domini);

        Optional<Domini> trobat = dominiRepository.findByExpedientTipusAndCodi(
                domini.getExpedientTipus(),
                domini.getCodi());

        assertTrue(trobat.isPresent());
        comprovaDomini(domini, trobat.get());
    }

    @Test
    @DisplayName("Consulta per expedient tipus i codi inexistent")
    void whenInvalidExpedientTipusAndCodi_thenReturnNull() {
        Optional<Domini> trobat = dominiRepository.findByEntornAndCodi(0L, "Codi_inexistent");
        assertTrue(trobat.isEmpty());
    }

    // FindByExpedientTipusAndIdAmbHerencia
    // ------------------------------------------------------------------
    @Test
    @DisplayName("Consulta per expedient tipus i codi amb herencia sobrescrit")
    void whenFindByExpedientTipusAndCodiAmbHerenciaSobrescrit_thenReturnDominiPare() {

        entityManager.persistAndFlush(domini);
        entityManager.persistAndFlush(domini2);

        Optional<Domini> trobat = dominiRepository.findByExpedientTipusAndCodiAmbHerencia(
                domini.getExpedientTipus(),
                domini2.getExpedientTipus(),
                domini.getCodi());

        assertTrue(trobat.isPresent());
        comprovaDomini(domini, trobat.get());
    }

    @Test
    @DisplayName("Consulta per expedient tipus i codi amb herencia")
    void whenFindByExpedientTipusAndCodiAmbHerencia_thenReturnDominiPare() {

        entityManager.persistAndFlush(domini);
        entityManager.persistAndFlush(domini2);

        Optional<Domini> trobat = dominiRepository.findByExpedientTipusAndCodiAmbHerencia(
                0L,
                domini2.getExpedientTipus(),
                domini.getCodi());

        assertTrue(trobat.isPresent());
        comprovaDomini(domini2, trobat.get());
    }

    @Test
    @DisplayName("Consulta per expedient tipus i codi sense herencia")
    void whenFindByExpedientTipusAndCodiSenseHerencia_thenReturnDomini() {

        entityManager.persistAndFlush(domini);
        entityManager.persistAndFlush(domini2);

        Optional<Domini> trobat = dominiRepository.findByExpedientTipusAndCodiAmbHerencia(
                domini.getExpedientTipus(),
                0L,
                domini.getCodi());

        assertTrue(trobat.isPresent());
        comprovaDomini(domini, trobat.get());
    }

    @Test
    @DisplayName("Consulta per expedient tipus i codi amb herencia inexistent")
    void whenInvalidExpedientTipusAndCodiAmbHerencia_thenReturnNull() {
        Optional<Domini> trobat = dominiRepository.findByExpedientTipusAndCodiAmbHerencia(0L, 0L,"Codi_inexistent");
        assertTrue(trobat.isEmpty());
    }


    // FindAll + especificacions
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Llista de dominis utilitzant especificacions")
    class DominiRepositorySpecificationIT {

        private Domini domini4;

        @BeforeEach
        void setUp() {
            Domini domini3 = DominiTestHelper.generateDomini(3, "Domini_codi_b", 1L, 2L);
            domini4 = DominiTestHelper.generateDomini(4, "Domini_codi_b", 2L, null);

            entityManager.persistAndFlush(domini);
            entityManager.persistAndFlush(domini2);
            entityManager.persistAndFlush(domini3);
            entityManager.persistAndFlush(domini4);
        }

        // Dominis existents:
        // |------------------|-----------------|
        // | Entorn 1         | Entorn 2        |
        // |------------------|-----------------|
        // | Domini 1:        | Domini 2:       |
        // | - Domini_codi    | - Domini_codi   |
        // | - expTip 2       | - expTip 3      |
        // |------------------|-----------------|
        // | Domini 3:        | Domini 4:       |
        // | - Domini_codi_b  | - Domini_codi_b |
        // | - expTip 2       | - expTip null   |
        // |------------------|-----------------|


        @Test
        @DisplayName("Llista tots")
        void whenFindAll_thenReturnDominiList() {

            // Existeixen 4 dominis en total
            List<Domini> llista = dominiRepository.findAll();

            assertNotNull(llista);
            assertThat(llista).isNotEmpty();
            assertThat(llista.size()).isEqualTo(4);

        }

        // belongsToEntorn
        // ------------------------------------------------------------------

        @Test
        @DisplayName("Llista amb especificacions - entorn")
        void whenBelongsToEntorn_thenReturnDominiList() {

            // Hi ha 2 dominis a l'entorn 1
            List<Domini> llista = dominiRepository.findAll(DominiSpecifications.belongsToEntorn(domini.getEntorn()));

            assertNotNull(llista);
            assertThat(llista).isNotEmpty();
            assertThat(llista.size()).isEqualTo(2);

            for (Domini d: llista) {
                assertThat(d.getEntorn()).isEqualTo(domini.getEntorn());
            }
        }

        @Test
        @DisplayName("Llista amb especificacions - entorn inexistent")
        void whenNoBelongsToEntorn_thenReturnEmptyList() {

            // No hi ha cap domini a l'entorn 0
            List<Domini> llista = dominiRepository.findAll(DominiSpecifications.belongsToEntorn(0L));
            assertThat(llista).isEmpty();

        }

        // belongsToExpedientTipus
        // ------------------------------------------------------------------

        @Test
        @DisplayName("Llista amb especificacions - expedient tipus")
        void whenBelongsToExpedientTipus_thenReturnDominiList() {

            // Els dominis 1 i 3 pertanyen al tipus d'expedient 2 (tot i que són d'entorns diferents)
            List<Domini> llista = dominiRepository.findAll(DominiSpecifications.belongsToExpedientTipus(domini.getExpedientTipus()));

            assertNotNull(llista);
            assertThat(llista).isNotEmpty();
            assertThat(llista.size()).isEqualTo(2);

            for (Domini d: llista) {
                assertThat(d.getExpedientTipus()).isEqualTo(domini.getExpedientTipus());
            }
        }

        @Test
        @DisplayName("Llista amb especificacions - expedient tipus inexistent")
        void whenNoBelongsToExpedientTipus_thenReturnEmptyList() {

            // No hi ha cap domini a l'expedient tipus 0
            List<Domini> llista = dominiRepository.findAll(DominiSpecifications.belongsToExpedientTipus(0L));
            assertThat(llista).isEmpty();

        }

        // belongsToExpedientTipusAndEntorn
        // ------------------------------------------------------------------

        @Test
        @DisplayName("Llista amb especificacions - expedient tipus + entorn")
        void whenBelongsToExpedientTipusAndEntorn_thenReturnDominiList() {

            // Només hi ha el domini 2 amb expedient tipus 3 a l'entorn 2
            List<Domini> llista = dominiRepository.findAll(
                    DominiSpecifications.belongsToExpedientTipus(
                            domini2.getEntorn(),
                            domini2.getExpedientTipus()));

            assertNotNull(llista);
            assertThat(llista).isNotEmpty();
            assertThat(llista.size()).isEqualTo(1);

            for (Domini d: llista) {
                assertAll(
                        () -> assertThat(d.getExpedientTipus()).isEqualTo(domini2.getExpedientTipus()),
                        () -> assertThat(d.getEntorn()).isEqualTo(domini2.getEntorn())
                );
            }
        }

        @Test
        @DisplayName("Llista amb especificacions - expedient tipus + entorn inexistent")
        void whenNoBelongsToExpedientTipusAndEntorn_thenReturnEmptyList() {

            // No hi ha cap domini a l'entorn 0
            List<Domini> llista = dominiRepository.findAll(
                    DominiSpecifications.belongsToExpedientTipus(0L, 0L));
            assertThat(llista).isEmpty();

        }

        // isGlobal
        // ------------------------------------------------------------------

        @Test
        @DisplayName("Llista amb especificacions - global")
        void whenGlobal_thenReturnDominiList() {

            // Hi ha el domini 4 global a l'entorn 2
            List<Domini> llista = dominiRepository.findAll(
                    DominiSpecifications.isGlobal(domini4.getEntorn()));

            assertNotNull(llista);
            assertThat(llista).isNotEmpty();
            assertThat(llista.size()).isEqualTo(1);

            for (Domini d: llista) {
                assertAll(
                        () -> assertThat(d.getExpedientTipus()).isNull(),
                        () -> assertThat(d.getEntorn()).isEqualTo(domini4.getEntorn())
                );
            }
        }

        @Test
        @DisplayName("Llista amb especificacions - global buit")
        void whenGlobalBuit_thenReturnEmptyList() {

            // No hi ha cap domini global a l'entorn 1
            List<Domini> llista = dominiRepository.findAll(
                    DominiSpecifications.isGlobal(domini.getEntorn()));
            assertThat(llista).isEmpty();

        }

        // belongsToExpedientTipusOrIsGlobal
        // ------------------------------------------------------------------

        @Test
        @DisplayName("Llista amb especificacions - expedient tipus o global")
        void whenBelongsToExpedientTipusOrGlobal_thenReturnDominiList() {

            // Consultam els dominis de l'entorn 2 i expedient tipus 3
            // Ha de trobar els dominis 4 (global) i 2
            List<Domini> llista = dominiRepository.findAll(
                    DominiSpecifications.belongsToExpedientTipusOrIsGlobal(
                            domini2.getEntorn(),
                            domini2.getExpedientTipus()));

            assertNotNull(llista);
            assertThat(llista).isNotEmpty();
            assertThat(llista.size()).isEqualTo(2);

            Condition<Domini> expTipus = new Condition<>(d -> domini2.getExpedientTipus().equals(d.getExpedientTipus()), "expTipus");
            Condition<Domini> global = new Condition<>(d -> d.getExpedientTipus() == null, "global");

            for (Domini d: llista) {
                assertAll(
                        () -> assertThat(d).is(anyOf(expTipus, global)),
                        () -> assertThat(d.getEntorn()).isEqualTo(domini2.getEntorn())
                );
            }
        }

        @Test
        @DisplayName("Llista amb especificacions - expedient tipus o global buit")
        void whenNoBelongsToExpedientTipusOrGlobal_thenReturnEmptyList() {

            // No existeix cap domini en l'entorn 0
            List<Domini> llista = dominiRepository.findAll(
                    DominiSpecifications.belongsToExpedientTipusOrIsGlobal(0L, 0L));
            assertThat(llista).isEmpty();

        }

        // Dominis amb Herencia

        @Nested
        @DisplayName("Llista de dominis amb herencia utilitzant especificacions")
        class DominiRepositorySpecificationHerenciaIT {

            @BeforeEach
            void setUp() {
                Domini domini5 = DominiTestHelper.generateDomini(5, "Domini_codi", 2L, 1L);
                entityManager.persistAndFlush(domini5);
            }

            // Dominis existents:
            // |------------------|-----------------|
            // | Entorn 1         | Entorn 2        |
            // |------------------|-----------------|
            // | Domini 1:        | Domini 2:       |
            // | - Domini_codi    | - Domini_codi   |
            // | - expTip 2       | - expTip 3      |
            // |------------------|-----------------|
            // | Domini 3:        | Domini 4:       |
            // | - Domini_codi_b  | - Domini_codi_b |
            // | - expTip 2       | - expTip null   |
            // |------------------|-----------------|
            // |                  | Domini 5:       |
            // |                  | - Domini_codi   |
            // |                  | - expTip 1      |
            // |------------------|-----------------|


            // dominisAmbHerencia
            // ------------------------------------------------------------------

            // Consultam els dominis de l'entorn 2, i utilitzam el tipus d'expedient 3
            @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
            @DisplayName("Llista amb especificacions - dominis amb herencia")
            @CsvSource({
                    // Descripcio, Entorn, Expedient tipus, Expedient tipus pare, Incloure globals
                    "Sobrescrit amb globals, 2, 3, 1, true",
                    "Sobrescrit sense globals, 2, 3, 1, false",
                    "Heretat amb globals, 2, 0, 3, true",
                    "Heretat sense globals, 2, 0, 3, false, "
            })
            void whenDominisSenseHerencia_thenReturnDominiList(
                    String descripcio,
                    Long entorn,
                    Long expedientTipus,
                    Long expedientTipusPare,
                    boolean incloureGlobals) {

                System.out.println("Executant test: " + descripcio);
                List<Domini> llista = dominiRepository.findAll(
                        DominiSpecifications.dominisAmbHerencia(
                                entorn,
                                expedientTipus,
                                expedientTipusPare,
                                incloureGlobals));

                assertNotNull(llista);
                assertThat(llista).isNotEmpty();

                checkDominisAmbHerencia(entorn, expedientTipus, expedientTipusPare, incloureGlobals, llista);
            }

            @Test
            @DisplayName("Llista amb especificacions - dominis amb herencia inexistent")
            void whenInvalidDominisAmbHerencia_thenReturnNull() {

                // No existeix cap domini en l'entorn 0
                List<Domini> llista = dominiRepository.findAll(
                        DominiSpecifications.dominisAmbHerencia(
                                0L,
                                0L,
                                0L,
                                true));
                assertTrue(llista.isEmpty());
            }

            // dominisSenseHerencia
            // ------------------------------------------------------------------

            // Consultam els dominis de l'entorn 2, i utilitzam el tipus d'expedient 3
            @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
            @DisplayName("Llista amb especificacions - dominis sense herencia")
            @CsvSource({
                    // Descripcio, Entorn, Expedient tipus, Incloure globals
                    "Entorn + Global, 2, , true",
                    "TipusExp + Global, 2, 3, true",
                    "Global, 2, , false",
                    "TipusExp, 2, 3, false"
            })
            void whenDominisSenseHerencia_thenReturnDominiList(
                    String descripcio,
                    Long entorn,
                    Long expedientTipus,
                    boolean incloureGlobals) {

                System.out.println("Executant test: " + descripcio);

                List<Domini> llista = dominiRepository.findAll(
                        DominiSpecifications.dominisSenseHerencia(
                                entorn,
                                expedientTipus,
                                incloureGlobals));

                assertNotNull(llista);
                assertThat(llista).isNotEmpty();

                checkDominisSenseHerencia(entorn, expedientTipus, incloureGlobals, llista);
            }

            @Test
            @DisplayName("Llista amb especificacions - dominis sense herencia inexistent")
            void whenInvalidDominisSenseHerencia_thenReturnNull() {

                // No existeix cap domini en l'entorn 0
                List<Domini> llista = dominiRepository.findAll(
                        DominiSpecifications.dominisSenseHerencia(
                                0L,
                                0L,
                                true));
                assertTrue(llista.isEmpty());
            }

            // dominisList
            // ------------------------------------------------------------------
            // Consultam els dominis de l'entorn 2, i utilitzam el tipus d'expedient 3
            @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
            @DisplayName("Llista amb especificacions - dominis")
            @CsvSource({
                    // Entorn, Expedient tipus, Incloure globals
                    "No Herencia. Entorn + Global, 2, , , true",
                    "No Herencia. TipusExp + Global, 2, 3, , true",
                    "No Herencia. Global, 2, , , false",
                    "No Herencia. TipusExp, 2, 3, , false",
                    "Sobrescrit amb globals, 2, 3, 1, true",
                    "Sobrescrit sense globals, 2, 3, 1, false",
                    "Heretat amb globals, 2, 0, 3, true",
                    "Heretat sense globals, 2, 0, 3, false"
            })
            void whenDominisList_thenReturnDominiList(
                    String descripcio,
                    Long entorn,
                    Long expedientTipus,
                    Long expedientTipusPare,
                    boolean incloureGlobals) {

                System.out.println("Executant test: " + descripcio);

                List<Domini> llista = dominiRepository.findAll(
                        DominiSpecifications.dominisList(
                                entorn,
                                expedientTipus,
                                expedientTipusPare,
                                incloureGlobals));

                assertNotNull(llista);
                assertThat(llista).isNotEmpty();

                if (expedientTipusPare == null) {
                    checkDominisSenseHerencia(entorn, expedientTipus, incloureGlobals, llista);
                } else {
                    checkDominisAmbHerencia(entorn, expedientTipus, expedientTipusPare, incloureGlobals, llista);
                }
            }

            @Test
            @DisplayName("Llista amb especificacions - dominis inexistent")
            void whenInvalidDominisList_thenReturnNull() {

                // No existeix cap domini en l'entorn 0
                List<Domini> llista = dominiRepository.findAll(
                        DominiSpecifications.dominisList(
                                0L,
                                0L,
                                0L,
                                true));
                assertTrue(llista.isEmpty());
            }

            private void checkDominisAmbHerencia(
                    Long entorn,
                    Long expedientTipus,
                    Long expedientTipusPare,
                    boolean incloureGlobals, List<Domini> llista) {

                Long zero = 0L;
                Condition<Domini> expTipus = new Condition<>(d -> expedientTipus.equals(d.getExpedientTipus()), "expTipus");
                Condition<Domini> expTipusPare = new Condition<>(d -> expedientTipusPare.equals(d.getExpedientTipus()), "expTipusPare");
                Condition<Domini> global = new Condition<>(d -> d.getExpedientTipus() == null, "global");

                // Incloem globals
                if (incloureGlobals) {
                    // - Amb domini sobrescrit
                    if (!zero.equals(expedientTipus)) {
                        assertThat(llista.size()).isEqualTo(2);
                        for (Domini d: llista) {
                            assertAll(
                                    () -> assertThat(d).is(anyOf(expTipus, global)),
                                    () -> assertThat(d.getEntorn()).isEqualTo(entorn)
                            );
                        }
                        // - Amb domini NO sobrescrit
                    } else {
                        assertThat(llista.size()).isEqualTo(2);
                        for (Domini d: llista) {
                            assertAll(
                                    () -> assertThat(d).is(anyOf(expTipusPare, global)),
                                    () -> assertThat(d.getEntorn()).isEqualTo(entorn)
                            );
                        }
                    }

                    // No incloem globals
                } else {
                    // - Amb domini sobrescrit
                    if (!zero.equals(expedientTipus)) {
                        assertThat(llista.size()).isEqualTo(1);
                        assertThat(llista.get(0).getEntorn()).isEqualTo(entorn);
                        assertThat(llista.get(0).getExpedientTipus()).isEqualTo(expedientTipus);
                        // - Amb domini NO sobrescrit
                    } else {
                        assertThat(llista.size()).isEqualTo(1);
                        assertThat(llista.get(0).getEntorn()).isEqualTo(entorn);
                        assertThat(llista.get(0).getExpedientTipus()).isEqualTo(expedientTipusPare);
                    }
                }
            }

            private void checkDominisSenseHerencia(Long entorn, Long expedientTipus, boolean incloureGlobals, List<Domini> llista) {
                // Incloem globals
                if (incloureGlobals) {

                    // - Sense expedient tipus: ha de trobar tots els dominis de l'entorn (2, 4 i 5)
                    if (expedientTipus == null) {
                        assertThat(llista.size()).isEqualTo(3);
                        for (Domini d : llista) {
                            assertThat(d.getEntorn()).isEqualTo(entorn);
                        }

                    // - Amb expedient tipus 3: ha de trobar únicament els domini 2 i 4 (global)
                    } else {
                        Condition<Domini> expTipus = new Condition<>(d -> expedientTipus.equals(d.getExpedientTipus()), "expTipus");
                        Condition<Domini> global = new Condition<>(d -> d.getExpedientTipus() == null, "global");

                        assertThat(llista.size()).isEqualTo(2);
                        for (Domini d : llista) {
                            assertAll(
                                    () -> assertThat(d).is(anyOf(expTipus, global)),
                                    () -> assertThat(d.getEntorn()).isEqualTo(entorn)
                            );
                        }
                    }

                // No incloem globals
                } else {

                    // - Sense expedient tipus: ha de trobar només els dominis globals (Domini 4)
                    if (expedientTipus == null) {
                        assertThat(llista.size()).isEqualTo(1);
                        assertThat(llista.get(0).getEntorn()).isEqualTo(entorn);
                        assertThat(llista.get(0).getExpedientTipus()).isNull();

                    // - Amb expedient tipus 3: ha de trobar únicament el domini 2
                    } else {
                        assertThat(llista.size()).isEqualTo(1);
                        assertThat(llista.get(0).getEntorn()).isEqualTo(entorn);
                        assertThat(llista.get(0).getExpedientTipus()).isEqualTo(expedientTipus);
                    }
                }
            }
        }
    }


    private void comprovaDomini(Domini domini, Domini trobat) {
        assertAll("Comprovar dades del domini",
                () -> assertEquals(domini.getCodi(), trobat.getCodi(), "Codi incorrecte"),
                () -> assertEquals(domini.getNom(), trobat.getNom(), "Nom incorrecte"),
                () -> assertEquals(domini.getTipus(), trobat.getTipus(), "Tipus incorrecte"),
                () -> assertEquals(domini.getUrl(), trobat.getUrl(), "Url incorrecte"),
                () -> assertEquals(domini.getTipusAuth(), trobat.getTipusAuth(), "Tipus autenticació incorrecte"),
                () -> assertEquals(domini.getOrigenCredencials(), trobat.getOrigenCredencials(), "Origen credencials incorrecte"),
                () -> assertEquals(domini.getUsuari(), trobat.getUsuari(), "Usuari incorrecte"),
                () -> assertEquals(domini.getContrasenya(), trobat.getContrasenya(), "Contrasenya incorrecte"),
                () -> assertEquals(domini.getSql(), trobat.getSql(), "Sql incorrecte"),
                () -> assertEquals(domini.getJndiDatasource(), trobat.getJndiDatasource(), "Jndi incorrecte"),
                () -> assertEquals(domini.getDescripcio(), trobat.getDescripcio(), "Descripció incorrecte"),
                () -> assertEquals(domini.getCacheSegons(), trobat.getCacheSegons(), "Cache incorrecte"),
                () -> assertEquals(domini.getTimeout(), trobat.getTimeout(), "Timeout incorrecte"),
                () -> assertEquals(domini.getOrdreParams(), trobat.getOrdreParams(), "Ordre incorrecte"),
                () -> assertEquals(domini.getEntorn(), trobat.getEntorn(), "Entorn incorrecte"),
                () -> assertEquals(domini.getExpedientTipus(), trobat.getExpedientTipus(), "Tipus d'expedient incorrecte")
        );
    }
}