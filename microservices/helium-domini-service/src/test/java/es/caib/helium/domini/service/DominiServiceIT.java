package es.caib.helium.domini.service;

import com.github.jenspiegsa.wiremockextension.Managed;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.BasicCredentials;
import es.caib.helium.domini.DominiTestHelper;
import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.mapper.DominiMapper;
import es.caib.helium.domini.model.DominiDto;
import es.caib.helium.domini.model.OrigenCredencialsEnum;
import es.caib.helium.domini.model.PagedList;
import es.caib.helium.domini.model.ResultatDomini;
import es.caib.helium.domini.model.TipusAuthEnum;
import es.caib.helium.domini.model.TipusDominiEnum;
import es.caib.helium.domini.repository.DominiRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@ExtendWith({WireMockExtension.class})
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class DominiServiceIT {

    @Autowired
    DominiServiceImpl dominiService;
    @Autowired
    DominiRepository dominiRepository;
    @Autowired
    DominiMapper dominiMapper;

    @Value("${spring.datasource.url}") String db_url;

    @Managed
    WireMockServer wireMockServer = with(wireMockConfig().dynamicPort());

    private static Domini domini1;
    private static Domini domini2;
    private static Domini domini3;
    private static Domini domini4;
    private static Domini domini5;
    private static DominiDto dto1;
    private static DominiDto dto2;
    private static DominiDto dto3;
    private static DominiDto dto4;
    private static DominiDto dto5;

    @BeforeEach
    void setUp() {

        // Dominis de test:
        // |---------------------------------------|---------------------------------------|
        // | Entorn 1                              | Entorn 2                              |
        // |---------------------------------------|---------------------------------------|
        // | ID    | Codi  | TExp  | Tipus | Auth  | ID    | Codi  | TExp  | Tipus | Auth  |
        // |---------------------------------------|---------------------------------------|
        // | 1     | CD1   | 2     | REST  | BASIC | 2     | CD1   | 3     | SQL   | NONE  |
        // | 3     | CD2   | 2     | WS    | BASIC | 4     | CD2   | null  | WS    | TOKEN |
        // |                                       | 5     | CD1   | 1     | REST  | NONE  |
        // |---------------------------------------|---------------------------------------|

        // Dominis
        domini1 = DominiTestHelper.generateDomini(1, "CD1",1L, 2L, TipusDominiEnum.CONSULTA_REST, TipusAuthEnum.HTTP_BASIC);
        domini2 = DominiTestHelper.generateDomini(2, "CD1",2L, 3L, TipusDominiEnum.CONSULTA_SQL, TipusAuthEnum.NONE);
        domini3 = DominiTestHelper.generateDomini(3, "CD2",1L, 2L, TipusDominiEnum.CONSULTA_WS, TipusAuthEnum.HTTP_BASIC);
        domini4 = DominiTestHelper.generateDomini(4, "CD2",2L, null, TipusDominiEnum.CONSULTA_WS, TipusAuthEnum.USERNAMETOKEN);
        domini5 = DominiTestHelper.generateDomini(5, "CD1",2L, 1L, TipusDominiEnum.CONSULTA_REST, TipusAuthEnum.NONE);

        // Urls dels dominis
        domini1.setUrl("http://localhost:" + wireMockServer.port() + "/api/rest/test");
        domini2.setUrl(db_url);
        domini2.setSql("select id, codi from hel_domini where entorn_id = :entorn_id");
//        domini2.setSql("select d.codi from hel_domini d");
        domini2.setOrigenCredencials(OrigenCredencialsEnum.ATRIBUTS);
        domini2.setUsuari("sa");
        domini2.setContrasenya("");
        domini3.setUrl("http://localhost:" + wireMockServer.port() + "/api/ws/test");
        domini4.setUrl("http://localhost:" + wireMockServer.port() + "/api/ws/test");
        domini5.setUrl("http://localhost:" + wireMockServer.port() + "/api/rest/test");

        dto1 = dominiService.createDomini(dominiMapper.entityToDto(domini1));
        dto2 = dominiService.createDomini(dominiMapper.entityToDto(domini2));
        dto3 = dominiService.createDomini(dominiMapper.entityToDto(domini3));
        dto4 = dominiService.createDomini(dominiMapper.entityToDto(domini4));
        dto5 = dominiService.createDomini(dominiMapper.entityToDto(domini5));

    }

    @Test
    @DisplayName("Crear domini")
    void whenCreateDomini_thenReturn() {

        // Given
        Domini domini = DominiTestHelper.generateDomini(1, "COD",1L, 2L, TipusDominiEnum.CONSULTA_REST, TipusAuthEnum.HTTP_BASIC);
        DominiDto dto = dominiMapper.entityToDto(domini);

        // When
        DominiDto creat = dominiService.createDomini(dto);

        // Then
        assertThat(creat).isNotNull();
        assertThat(creat.getId()).isNotNull();

        dto = dominiService.getById(creat.getId());
        DominiTestHelper.comprovaDomini(creat, dto);

    }

    @Test
    @DisplayName("Crear domini - Error unique")
    void whenCreateDomini_thenErrorUnique() {

        // Given
        Domini domini = DominiTestHelper.generateDomini(1, "CD1",1L, 2L, TipusDominiEnum.CONSULTA_REST, TipusAuthEnum.HTTP_BASIC);
        final DominiDto dto = dominiMapper.entityToDto(domini);

        // When
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class,
                () -> dominiService.createDomini(dto));

        // Then
        assertThat(exception.getMessage()).contains("HEL_DOMINI(CODI, ENTORN_ID, EXPEDIENT_TIPUS_ID)");
    }

    @Test
    @DisplayName("Modificar domini")
    void whenUpdateDomini_thenReturn() {
        // Given
        dto1.setCodi("CD3");
        dto1.setNom("Nom");
        dto1.setDescripcio("Desc");
        dto1.setTipus(TipusDominiEnum.CONSULTA_WS);
        dto1.setUrl("url");
        dto1.setTipusAuth(TipusAuthEnum.USERNAMETOKEN);
        dto1.setOrigenCredencials(OrigenCredencialsEnum.PROPERTIES);
        dto1.setUsuari("user");
        dto1.setContrasenya("pass");
        dto1.setSql("sql");
        dto1.setJndiDatasource("jndi");
        dto1.setCacheSegons(1);
        dto1.setTimeout(1);
        dto1.setOrdreParams("ordre");

        // When
        DominiDto modificat = dominiService.updateDomini(dto1.getId(), dto1);

        // Then
        assertThat(modificat).isNotNull();
        assertThat(modificat.getId()).isNotNull();
        assertThat(modificat.getId()).isEqualTo(dto1.getId());
        DominiTestHelper.comprovaDomini(modificat, dto1);
    }

    @Test
    @DisplayName("Modificar domini - Error unique")
    void whenUpdateDomini_thenUniqueError() {

        // Given
        dto3.setCodi("CD1");

        // When
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class,
                () -> dominiService.updateDomini(dto3.getId(), dto3));

        // Then
        assertThat(exception.getMessage()).contains("HEL_DOMINI(CODI, ENTORN_ID, EXPEDIENT_TIPUS_ID)");
    }

    @Test
    @DisplayName("Eliminar domini")
    void whenDelete_thenDeleted() {
        // Given
        Domini domini = DominiTestHelper.generateDomini(1, "CD9",1L, 2L, TipusDominiEnum.CONSULTA_REST, TipusAuthEnum.HTTP_BASIC);
        DominiDto dto = dominiService.createDomini(dominiMapper.entityToDto(domini));

        int numDominis = dominiRepository.findAll().size();
        Long dominiId = dto.getId();

        // TODO: Revisar perquè el delete del repositori no funciona

        // When
        dominiService.delete(dominiId);

        // Then
        assertThat(dominiRepository.findAll().size()).isEqualTo(numDominis - 1);
        assertThat(dominiRepository.findById(dominiId).isPresent()).isFalse();

    }

    @Test
    @DisplayName("Eliminar domini - Error no trobat")
    void whenDelete_thenNotFoundError() {
        // Given
        final Long ID = -1L;

        // TODO: Revisar perquè el delete del repositori no funciona

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dominiService.delete(ID));

        // Then
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Consulta de domini per id")
    void whenGetById_thenFound() {
        // Given

        // When
        DominiDto dto = dominiService.getById(dto1.getId());

        // Then
        assertThat(dto.getId()).isEqualTo(dto1.getId());
        DominiTestHelper.comprovaDomini(dto, dto1);
    }

    @Test
    @DisplayName("Consulta de domini per id - Error no trobat")
    void whenGetById_thenNotFoundError() {
        // Given
        final Long ID = -1L;

        // TODO: Revisar perquè el delete del repositori no funciona

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dominiService.getById(ID));

        // Then
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    @DisplayName("Consulta de domini per entorn i codi")
    void whenGetByEntornAndCodi_thenFound() {
        // Given
        final Long ENTORN_ID = 2L;
        final String CODI = "CD2";

        // When
        DominiDto dto = dominiService.getByEntornAndCodi(ENTORN_ID, CODI);

        // Then
        assertThat(dto.getId()).isEqualTo(dto4.getId());
        DominiTestHelper.comprovaDomini(dto, dto4);
    }

    @Test
    @DisplayName("Consulta de domini per entorn i codi - Error no trobat")
    void whenGetByEntornAndCodi_thenNotFoundError() {
        // Given
        final Long ENTORN_ID = -1L;
        final String CODI = "CD";

        // TODO: Revisar perquè el delete del repositori no funciona

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dominiService.getByEntornAndCodi(ENTORN_ID, CODI));

        // Then
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    @DisplayName("Consulta de domini per tipus d'expedient i codi")
    void whenGetByExpedientTipusAndCodi_thenFound() {
        // Given
        final Long EXPTIP_ID = 2L;
        final Long EXPTIP_PARE_ID = 1L;
        final String CODI = "CD1";

        // When
        DominiDto dto = dominiService.getByExpedientTipusAndCodi(EXPTIP_ID, EXPTIP_PARE_ID, CODI);

        // Then
        assertThat(dto.getId()).isEqualTo(dto1.getId());
        DominiTestHelper.comprovaDomini(dto, dto1);
    }

    @Test
    @DisplayName("Consulta de domini per tipus d'expedient i codi - Error no trobat")
    void whenGetByExpedientTipusAndCodi_thenNotFoundError() {
        // Given
        final Long EXPTIP_ID = -1L;
        final String CODI = "CD";

        // TODO: Revisar perquè el delete del repositori no funciona

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dominiService.getByExpedientTipusAndCodi(EXPTIP_ID, null, CODI));

        // Then
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @DisplayName("Llistar dominis")
    @CsvSource({
            // Descripcio, entorn, expTip, expTipPare, filtreRsql, pageNum, pageSize, propertySort, totalRetornat, firstId
            "Tots els dominis,                  2, , , ,                  0, 10, ,    3, ",             // -> Els 3 dominis de l'entorn 2
            "Sense herencia. Codi conté CD ,    2, , , codi==*CD*,        0, 0, nom,  3, Domini_nom2",  // -> Els 3 dominis de l'entorn 2 (tots contenen CD)
            "Sense Herencia. TipusExp,          2, 3, , ,                 0, 10, nom, 2, Domini_nom2",  // -> El domini3 del tipexp 3 i el domini 4 amb tipexp null
            "Sense Herencia. TipusExp i codi,   1, 2, , codi=nic=CD1,     0, 10, ,    1, Domini_nom3",  // -> El Domini3 de l'entorn 1, amb codi diferent a CD1
            "Sense herencia. Sense resultats,   2, , , usuari=ic=nouser,  0, 10, ,    0, ",             // -> Cap domini (cap té com a usuari 'nouser'
            "Amb herencia. Sobrescrit,          2, 1, 3, ,                0, 0, ,     2, ",             // -> El domini5, que sobrescriu el domini 2, i el domini4 amb topexp null
            "Amb herencia. Heretat,             2, 9, 3, codi==CD1,       0, 10, ,    1, Domini_nom2",  // -> El domini2, com a tipexp pare, i amb codi CD1
    })
    void whenListDominis_thenReturn(
            String descripcio,
            Long entorn,
            Long expedientTipus,
            Long expedientTipusPare,
            String filtreRsql,
            int pageNum,
            int pageSize,
            String propertySort,
            int totalRetornat,
            String firstNom
    ) {
        System.out.println("Executant test: " + descripcio);

        // Given
        Pageable pageable = Pageable.unpaged();
        Sort sort = null;
        if (pageSize > 0) {
            if (propertySort != null && !propertySort.isBlank()) {
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(propertySort));
            } else {
                pageable = PageRequest.of(pageNum, pageSize);
            }
        } else if (propertySort != null && !propertySort.isBlank()) {
            sort = Sort.by(propertySort);
        }

        // When
        PagedList<DominiDto> pagina = dominiService.listDominis(
                entorn,
                expedientTipus,
                expedientTipusPare,
                filtreRsql,
                pageable,
                sort
        );

        // Then
        assertThat(pagina.getTotalElements()).isEqualTo(totalRetornat);
        if (firstNom != null && !firstNom.isBlank())
            assertThat(pagina.getContent().get(0).getNom()).isEqualTo(firstNom);
    }
    // TODO: Definir proves

    @Test
    @DisplayName("Consulta de dominis - Error RSQL")
    void whenListDominis_thenRsqlError() {
        // Given
        final Long ENTORN_ID = 2L;
        final Long EXPTIP_ID = 3L;
        final Long EXPTIP_PARE_ID = 4L;

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dominiService.listDominis(
                        ENTORN_ID,
                        EXPTIP_ID,
                        EXPTIP_PARE_ID,
                        "usuari=usuari",
                        Pageable.unpaged(),
                        null)
        );

        // Then
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @DisplayName("Llistar dominis per entorn")
    @CsvSource({
            // Descripcio, entorn, filtreRsql, totalRetornat
            "Tots els dominis, 2, , 3",
            "Codi = CD1, 2, codi==CD1, 2 ",
            "Exp tipus = 3, 2, expedientTipus==3, 1",
            "Sense resultats, 2, usuari=ic=nouser, 0"
    })
    void whenListDominisByEntorn_thenReturn(
            String descripcio,
            Long entorn,
            String filtreRsql,
            int totalRetornat
    ) {

        System.out.println("Executant test: " + descripcio);


        // When
        PagedList<DominiDto> pagina = dominiService.listDominisByEntorn(
                entorn,
                filtreRsql,
                Pageable.unpaged(),
                null
        );

        // Then
        assertThat(pagina.getTotalElements()).isEqualTo(totalRetornat);

    }

    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @DisplayName("Llistar dominis per expedient tipus")
    @CsvSource({
            // Descripcio, expedient tipus, filtreRsql, pageNum, pageSize, propertySort, totalRetornat, firstId
            "Tots els dominis, 2, , 0, 10, , 2, ",
            "Codi conté CD , 2, codi==*CD*, 0, 0, nom, 2, Domini_nom1",
            "Sense resultat, 2, usuari=ic=nouser, 0, 0, , 0, "
    })
    void whenListDominisByExpedientTipus_thenReturn(
            String descripcio,
            Long expedientTipus,
            String filtreRsql,
            int pageNum,
            int pageSize,
            String propertySort,
            int totalRetornat,
            String firstNom
    ) {

        System.out.println("Executant test: " + descripcio);

        // Given
        Pageable pageable = Pageable.unpaged();
        Sort sort = null;
        if (pageSize > 0) {
            if (propertySort != null && !propertySort.isBlank()) {
                pageable = PageRequest.of(pageNum, pageSize, Sort.by(propertySort));
            } else {
                pageable = PageRequest.of(pageNum, pageSize);
            }
        } else if (propertySort != null && !propertySort.isBlank()) {
            sort = Sort.by(propertySort);
        }

        // When
        PagedList<DominiDto> pagina = dominiService.listDominisByExpedientTipus(
                expedientTipus,
                filtreRsql,
                pageable,
                sort
        );

        // Then
        assertThat(pagina.getTotalElements()).isEqualTo(totalRetornat);
        if (firstNom != null && !firstNom.isBlank())
            assertThat(pagina.getContent().get(0).getNom()).isEqualTo(firstNom);

    }

    @Test
    @DisplayName("Consulta de domini REST")
    void whenConsultaDominiRest_thenReturn() {
        // Given
        final String IDENTIFICADOR = "IDF1";
        Map<String, String> parametres = DominiTestHelper.generateParams();
        String jsonResponse = DominiTestHelper.generateJsonResponse(parametres, IDENTIFICADOR);
        wireMockServer.stubFor(get(urlPathEqualTo("/api/rest/test")).willReturn(okJson(jsonResponse)));

        // When
        ResultatDomini resultat = dominiService.consultaDomini(dto1.getId(), IDENTIFICADOR, parametres);

        // Then
        assertThat(resultat).isNotNull();
        assertThat(resultat).isNotEmpty();
        assertThat(resultat.size()).isEqualTo(3);
        verify(1, getRequestedFor(urlPathEqualTo("/api/rest/test"))
                .withBasicAuth(new BasicCredentials("usuari", "password")));
    }

    @Test
    @DisplayName("Consulta de domini REST amb caché")
    void whenConsultaDominiRest_thenReturnCache() {
        // Given
        final String IDENTIFICADOR = "IDF1";
        Map<String, String> parametres = DominiTestHelper.generateParams();
        String jsonResponse = DominiTestHelper.generateJsonResponse(parametres, IDENTIFICADOR);
        wireMockServer.stubFor(get(urlPathEqualTo("/api/rest/test")).willReturn(okJson(jsonResponse)));

        // When
        ResultatDomini resultat = dominiService.consultaDomini(dto1.getId(), IDENTIFICADOR, parametres);
        ResultatDomini resultat2 = dominiService.consultaDomini(dto1.getId(), IDENTIFICADOR, parametres);

        // Then
        assertThat(resultat).isNotNull();
        assertThat(resultat).isNotEmpty();
        assertThat(resultat.size()).isEqualTo(3);
        verify(1, getRequestedFor(urlPathEqualTo("/api/rest/test"))
                .withBasicAuth(new BasicCredentials("usuari", "password")));
        assertThat(resultat).isEqualTo(resultat2);
    }

    @Test
    @DisplayName("Consulta de domini REST - Error url")
    void whenConsultaDominiRest_thenReturnErrorUrl() {
        // Given
        final String IDENTIFICADOR = "IDF1";
        Map<String, String> parametres = DominiTestHelper.generateParams();
        wireMockServer.stubFor(get(urlPathEqualTo("/api/rest/test")).willReturn(notFound()));

        // When
        Exception exception = assertThrows(ResponseStatusException.class,
                () -> dominiService.consultaDomini(dto1.getId(), IDENTIFICADOR, parametres));

        // Then
        assertThat(HttpStatus.INTERNAL_SERVER_ERROR)
                .isEqualTo(((ResponseStatusException)exception).getStatus());
    }

    @Test
    @DisplayName("Consulta de domini WS")
    void whenConsultaDominiWs_thenReturn() {
        // Given
        final String IDENTIFICADOR = "IDF1";
        Map<String, String> parametres = DominiTestHelper.generateParams();
        wireMockServer.stubFor(post(urlPathEqualTo("/api/ws/test"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("content-type", "text/xml")
                        .withBody(DominiTestHelper.SOAP_RESPONSE)
                ));

        // When
        ResultatDomini resultat = dominiService.consultaDomini(dto3.getId(), IDENTIFICADOR, parametres);

        // Then
        assertThat(resultat).isNotNull();
        assertThat(resultat).isNotEmpty();
        assertThat(resultat.get(0).getColumnes()).isNotEmpty();
        assertThat(resultat.get(0).getColumnes().size()).isEqualTo(9);
        verify(1, postRequestedFor(urlPathEqualTo("/api/ws/test")));
    }

    @Test
    @DisplayName("Consulta de domini WS - Error url")
    void whenConsultaDominiWs_thenReturnErrorUrl() {
        // Given
        final String IDENTIFICADOR = "IDF1";
        Map<String, String> parametres = DominiTestHelper.generateParams();
        wireMockServer.stubFor(post(urlPathEqualTo("/api/ws/test")).willReturn(notFound()));

        // When
        Exception exception = assertThrows(ResponseStatusException.class,
                () -> dominiService.consultaDomini(dto3.getId(), IDENTIFICADOR, parametres));

        // Then
        assertThat(HttpStatus.INTERNAL_SERVER_ERROR)
                .isEqualTo(((ResponseStatusException)exception).getStatus());
    }

    @Test
    @DisplayName("Consulta de domini intern")
    void whenConsultaDominiIntern_thenReturn() {
        // Given
        final String IDENTIFICADOR = "UNITAT_PER_CODI";
        Map<String, String> parametres = DominiTestHelper.generateParams();
        parametres.put("entorn", "Test");
        dominiService.setDomini_intern_url("http://localhost:" + wireMockServer.port() + "/api/ws/test");
        wireMockServer.stubFor(post(urlPathEqualTo("/api/ws/test"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("content-type", "text/xml")
                        .withBody(DominiTestHelper.SOAP_RESPONSE)
                ));

        // When
        ResultatDomini resultat = dominiService.consultaDomini(0L, IDENTIFICADOR, parametres);

        // Then
        assertThat(resultat).isNotNull();
        assertThat(resultat).isNotEmpty();
        assertThat(resultat.get(0).getColumnes()).isNotEmpty();
        assertThat(resultat.get(0).getColumnes().size()).isEqualTo(9);
        verify(1, postRequestedFor(urlPathEqualTo("/api/ws/test")));
    }

    @Test
    @DisplayName("Consulta de domini intern - Error entorn null")
    void whenConsultaDominiIntern_thenReturnErrorEntorn() {
        // Given
        final String IDENTIFICADOR = "UNITAT_PER_CODI";
        Map<String, String> parametres = DominiTestHelper.generateParams();
        dominiService.setDomini_intern_url("http://localhost:" + wireMockServer.port() + "/api/ws/test");
        wireMockServer.stubFor(post(urlPathEqualTo("/api/ws/test")).willReturn(notFound()));

        // When
        Exception exception = assertThrows(ResponseStatusException.class,
                () -> dominiService.consultaDomini(0L, IDENTIFICADOR, parametres));

        // Then
        assertThat(HttpStatus.BAD_REQUEST)
                .isEqualTo(((ResponseStatusException)exception).getStatus());
    }

    @Test
    @DisplayName("Consulta de domini intern - Error url")
    void whenConsultaDominiIntern_thenReturnErrorUrl() {
        // Given
        final String IDENTIFICADOR = "UNITAT_PER_CODI";
        Map<String, String> parametres = DominiTestHelper.generateParams();
        parametres.put("entorn", "Test");
        dominiService.setDomini_intern_url("http://localhost:" + wireMockServer.port() + "/api/ws/test");
        wireMockServer.stubFor(post(urlPathEqualTo("/api/ws/test")).willReturn(notFound()));

        // When
        Exception exception = assertThrows(ResponseStatusException.class,
                () -> dominiService.consultaDomini(0L, IDENTIFICADOR, parametres));

        // Then
        assertThat(HttpStatus.INTERNAL_SERVER_ERROR)
                .isEqualTo(((ResponseStatusException)exception).getStatus());
    }

    @Test
    @DisplayName("Consulta de domini SQL")
    @Sql(scripts = "/scripts/domini_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
    @Sql(scripts = "/scripts/domini_clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
    void whenConsultaDominiSql_thenReturn() {
        // Given
        final String IDENTIFICADOR = "IDF1";
        Map<String, String> parametres = new HashMap<>();
        parametres.put("entorn_id", "3");
        parametres.put("entorn_id-type", "int");

        // When
        ResultatDomini resultat = dominiService.consultaDomini(dto2.getId(), IDENTIFICADOR, parametres);

        // Then
        assertThat(resultat).isNotNull();
        assertThat(resultat).isNotEmpty();
        assertThat(resultat.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("Consulta de domini SQL - Error url")
    void whenConsultaDominiSql_thenReturnErrorUrl() {
        // Given
        final String IDENTIFICADOR = "IDF1";
        Map<String, String> parametres = DominiTestHelper.generateParams();

        dto2.setUrl(db_url + "_error");
        dominiService.updateDomini(dto2.getId(), dto2);

        // When
        Exception exception = assertThrows(ResponseStatusException.class,
                () -> dominiService.consultaDomini(dto2.getId(), IDENTIFICADOR, parametres));

        // Then
        assertThat(HttpStatus.INTERNAL_SERVER_ERROR)
                .isEqualTo(((ResponseStatusException)exception).getStatus());
    }

    @AfterEach
    void tearDown() {
        dominiRepository.deleteAll();
        dominiRepository.flush();
    }
}