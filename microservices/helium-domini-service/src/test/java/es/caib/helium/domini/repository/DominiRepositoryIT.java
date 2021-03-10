package es.caib.helium.domini.repository;

import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.model.OrigenCredencialsEnum;
import es.caib.helium.domini.model.TipusAuthEnum;
import es.caib.helium.domini.model.TipusDominiEnum;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
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
        domini = Domini.builder()
                .codi("Domini_codi")
                .nom("Domini_nom")
                .tipus(TipusDominiEnum.CONSULTA_REST)
                .url("http://localhost:8080/api/rest")
                .tipusAuth(TipusAuthEnum.HTTP_BASIC)
                .origenCredencials(OrigenCredencialsEnum.ATRIBUTS)
                .usuari("usuari")
                .contrasenya("password")
                .sql("select * from hel_domini")
                .jndiDatasource("java:/es.caib.helium.db")
                .descripcio("Domini_descripcio")
                .cacheSegons(3600)
                .timeout(300)
                .ordreParams("none")
                .entorn(1L)
                .expedientTipus(2L)
                .build();

        domini2 = Domini.builder()
                .codi("Domini_codi")
                .nom("Domini_nom2")
                .tipus(TipusDominiEnum.CONSULTA_WS)
                .url("http://localhost:8080/api/rest2")
                .tipusAuth(TipusAuthEnum.USERNAMETOKEN)
                .origenCredencials(OrigenCredencialsEnum.PROPERTIES)
                .usuari("usuari2")
                .contrasenya("password2")
                .sql("select * from hel_domini2")
                .jndiDatasource("java:/es.caib.helium.db2")
                .descripcio("Domini_descripcio2")
                .cacheSegons(3602)
                .timeout(302)
                .ordreParams("none2")
                .entorn(2L)
                .expedientTipus(3L)
                .build();

    }

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
    class DominiRepositoryListIT {

        private Domini domini3;
        private Domini domini4;

        @BeforeEach
        void setUp() {
            domini3 = Domini.builder()
                    .codi("Domini_codi_b")
                    .nom("Domini_nom3")
                    .tipus(TipusDominiEnum.CONSULTA_REST)
                    .url("http://localhost:8080/api/rest")
                    .tipusAuth(TipusAuthEnum.HTTP_BASIC)
                    .origenCredencials(OrigenCredencialsEnum.ATRIBUTS)
                    .usuari("usuari3")
                    .contrasenya("password3")
                    .sql("select * from hel_domini3")
                    .jndiDatasource("java:/es.caib.helium.db3")
                    .descripcio("Domini_descripcio3")
                    .cacheSegons(3603)
                    .timeout(303)
                    .ordreParams("none3")
                    .entorn(1L)
                    .expedientTipus(2L)
                    .build();

            domini4 = Domini.builder()
                    .codi("Domini_codi_b")
                    .nom("Domini_nom2")
                    .tipus(TipusDominiEnum.CONSULTA_WS)
                    .url("http://localhost:8080/api/rest2")
                    .tipusAuth(TipusAuthEnum.USERNAMETOKEN)
                    .origenCredencials(OrigenCredencialsEnum.PROPERTIES)
                    .usuari("usuari2")
                    .contrasenya("password2")
                    .sql("select * from hel_domini2")
                    .jndiDatasource("java:/es.caib.helium.db2")
                    .descripcio("Domini_descripcio2")
                    .cacheSegons(3602)
                    .timeout(302)
                    .ordreParams("none2")
                    .entorn(2L)
                    .expedientTipus(null)
                    .build();

            entityManager.persistAndFlush(domini);
            entityManager.persistAndFlush(domini2);
            entityManager.persistAndFlush(domini3);
            entityManager.persistAndFlush(domini4);
        }

        @Test
        @DisplayName("Llista tots")
        void whenFindAll_thenReturnDominiList() {

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

            List<Domini> llista = dominiRepository.findAll(DominiSpecifications.belongsToEntorn(0L));
            assertThat(llista).isEmpty();

        }

        // belongsToExpedientTipus
        // ------------------------------------------------------------------

        @Test
        @DisplayName("Llista amb especificacions - expedient tipus")
        void whenBelongsToExpedientTipus_thenReturnDominiList() {

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

            List<Domini> llista = dominiRepository.findAll(DominiSpecifications.belongsToExpedientTipus(0L));
            assertThat(llista).isEmpty();

        }

        // belongsToExpedientTipusAndEntorn
        // ------------------------------------------------------------------

        @Test
        @DisplayName("Llista amb especificacions - expedient tipus + entorn")
        void whenBelongsToExpedientTipusAndEntorn_thenReturnDominiList() {

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

            List<Domini> llista = dominiRepository.findAll(
                    DominiSpecifications.belongsToExpedientTipus(0L, 0L));
            assertThat(llista).isEmpty();

        }

        // isGlobal
        // ------------------------------------------------------------------

        @Test
        @DisplayName("Llista amb especificacions - global")
        void whenGlobal_thenReturnDominiList() {

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

            List<Domini> llista = dominiRepository.findAll(
                    DominiSpecifications.isGlobal(domini.getEntorn()));
            assertThat(llista).isEmpty();

        }

        // belongsToExpedientTipusOrIsGlobal
        // ------------------------------------------------------------------

        @Test
        @DisplayName("Llista amb especificacions - expedient tipus o global")
        void whenBelongsToExpedientTipusOrGlobal_thenReturnDominiList() {

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

            List<Domini> llista = dominiRepository.findAll(
                    DominiSpecifications.belongsToExpedientTipusOrIsGlobal(0L, 0L));
            assertThat(llista).isEmpty();

        }

        // dominisAmbHerencia
        // ------------------------------------------------------------------

        // dominisSenseHerencia
        // ------------------------------------------------------------------

        // dominisList
        // ------------------------------------------------------------------

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