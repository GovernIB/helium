package es.caib.helium.domini.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import es.caib.helium.domini.DominiTestHelper;
import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.mapper.DominiMapper;
import es.caib.helium.domini.model.DominiDto;
import es.caib.helium.domini.model.FilaResultat;
import es.caib.helium.domini.model.ParellaCodiValor;
import es.caib.helium.domini.model.ResultatDomini;
import es.caib.helium.domini.model.TipusAuthEnum;
import es.caib.helium.domini.model.TipusDominiEnum;
import es.caib.helium.domini.repository.DominiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static es.caib.helium.domini.DominiTestHelper.CODI;
import static es.caib.helium.domini.DominiTestHelper.VALOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class DominiServiceTest {

    @Mock
    DominiRepository dominiRepository;
//    @Mock
//    DominiInternService dominiInternService;
    @Mock
    DominiWsService dominiWsService;
//    @Mock
//    DominiSqlService dominiSqlService;
//    @Mock
//    DominiRestService dominiRestService;

    @Mock
    HazelcastInstance hazelcastInstance;
    private TestHazelcastInstanceFactory hazelcastFactory = new TestHazelcastInstanceFactory();

    @Mock
    Environment environment;

    @Spy
    DominiMapper mapper = Mappers.getMapper(DominiMapper.class);

    @InjectMocks
    DominiServiceImpl dominiService;

    Domini domini;

    @BeforeEach
    void setUp() {
        domini = DominiTestHelper.generateDomini(1, "COD", 1L, 2L);
    }

    @Test
    @DisplayName("Crear domini")
    void whenCreateDomini_thenReturnDomini() {

        // Given
        DominiDto dominiDto = mapper.entityToDto(domini);
        given(dominiRepository.save(any(Domini.class))).willReturn(domini);

        // When
        DominiDto creat = dominiService.createDomini(dominiDto);

        // Then
        assertThat(creat).isNotNull();
        DominiTestHelper.comprovaDomini(dominiDto, creat);
        then(dominiRepository).should(times(1)).save(any(Domini.class));
        then(dominiRepository).shouldHaveNoMoreInteractions();
    }

    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @DisplayName("Crear domini - Errors validació")
    @CsvSource({
            // Descripcio, camp null
            "Codi null, codi",
            "Nom null, nom",
            "Entorn null, entorn",
            "Url null, url",
            "Tipus SQL - Sql null, sql",
            "Basic auth - usuari null, usuari",
            "Basic auth - contrasenya null, contrasenya",
            "Tipus null, tipus"
    })
    void whenCreateDominiValidacio_thenReturnError(
            String descripcio,
            String campNull) {

        System.out.println("Executant test: " + descripcio);

        // Given
        switch (campNull) {
            case "codi":
                domini.setCodi(null);
                break;
            case "nom":
                domini.setNom(null);
                break;
            case "entorn":
                domini.setEntorn(null);
                break;
            case "url":
                domini.setUrl(null);
                break;
            case "sql":
                domini.setTipus(TipusDominiEnum.CONSULTA_SQL);
                domini.setSql(null);
                break;
            case "usuari":
                domini.setTipusAuth(TipusAuthEnum.HTTP_BASIC);
                domini.setUsuari(null);
                break;
            case "contrasenya":
                domini.setTipusAuth(TipusAuthEnum.HTTP_BASIC);
                domini.setContrasenya(null);
                break;
            case "tipus":
                domini.setTipus(null);
        }
        DominiDto dominiDto = mapper.entityToDto(domini);

        // When
        Exception exception = assertThrows(
                ValidationException.class,
                () -> dominiService.createDomini(dominiDto));

        // Then
        then(dominiRepository).should(never()).save(any(Domini.class));
        assertThat(exception.getMessage()).contains(campNull + "=");
    }


    @Test
    @DisplayName("Modificar domini")
    void whenUpdateDomini_thenReturnDomini() {
        // Given
        DominiDto dominiDto = mapper.entityToDto(domini);
        Domini domini2 = DominiTestHelper.generateDomini(2, "CDM", 1L, 2L);
        given(dominiRepository.findById(anyLong())).willReturn(Optional.of(domini2));
        given(dominiRepository.save(any(Domini.class))).will(
                (InvocationOnMock invocation) -> invocation.getArgument(0, Domini.class));

        // When
        DominiDto response = dominiService.updateDomini(1L, dominiDto);

        // Then
        assertThat(response).isNotNull();
        DominiTestHelper.comprovaDomini(dominiDto, response);
        then(dominiRepository).should().findById(anyLong());
        then(dominiRepository).should().save(any(Domini.class));
        then(dominiRepository).shouldHaveNoMoreInteractions();
    }

    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @DisplayName("Crear domini - Errors validació")
    @CsvSource({
            // Descripcio, camp null
            "Sobrescrit amb globals, codi",
            "Sobrescrit sense globals, nom",
            "Heretat amb globals, entorn",
            "Heretat sense globals, url",
            "Heretat sense globals, sql",
            "Heretat sense globals, usuari",
            "Heretat sense globals, contrasenya"
    })
    void whenUpdateDominiValidacio_thenReturnError(
            String descripcio,
            String campNull) {

        System.out.println("Executant test: " + descripcio);

        // Given
        given(dominiRepository.findById(anyLong())).willReturn(Optional.of(domini));
        switch (campNull) {
            case "codi":
                domini.setCodi(null);
                break;
            case "nom":
                domini.setNom(null);
                break;
            case "entorn":
                domini.setEntorn(null);
                break;
            case "url":
                domini.setUrl(null);
                break;
            case "sql":
                domini.setTipus(TipusDominiEnum.CONSULTA_SQL);
                domini.setSql(null);
                break;
            case "usuari":
                domini.setTipusAuth(TipusAuthEnum.HTTP_BASIC);
                domini.setUsuari(null);
                break;
            case "contrasenya":
                domini.setTipusAuth(TipusAuthEnum.HTTP_BASIC);
                domini.setContrasenya(null);
                break;
        }
        DominiDto dominiDto = mapper.entityToDto(domini);

        // When
        Exception exception = assertThrows(
                ValidationException.class,
                () -> dominiService.updateDomini(1L, dominiDto));

        // Then
        then(dominiRepository).should(never()).save(any(Domini.class));
        assertThat(exception.getMessage()).contains(campNull + "=");
    }

    @Test
    @DisplayName("Eliminar domini")
    void whenDeleteDomini() {
        // Given
        given(dominiRepository.findById(anyLong())).willReturn(Optional.of(domini));

        // When
        dominiService.delete(1L);

        // Then
        then(dominiRepository).should().findById(anyLong());
        then(dominiRepository).should().delete(anyLong());
        then(dominiRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Consulta domini per id")
    void whenGetById_thenReturn() {
        // Given
        given(dominiRepository.findById(anyLong())).willReturn(Optional.of(domini));

        // When
        DominiDto dto = dominiService.getById(1L);

        // Then
        then(dominiRepository).should().findById(anyLong());
        then(dominiRepository).shouldHaveNoMoreInteractions();
        assertThat(dto).isNotNull();
        DominiTestHelper.comprovaDomini(dto, mapper.entityToDto(domini));

    }

    @Test
    @DisplayName("Consulta domini per id - Error no trobat")
    void whenGetById_thenReturnError() {
        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dominiService.getById(1L));

        // Then
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Consulta dominis")
    void whenListDominis_thenReturn() {
        // Given
        List<Domini> dominis = new ArrayList<>();
        dominis.add(domini);
        given(dominiRepository.findAll(any(Specification.class), any(Sort.class))).willReturn(dominis);

        // When
        Page<DominiDto> page = dominiService.listDominis(
                1L,
                2L,
                null,
                null,
                Pageable.unpaged(),
                null);

        // Then
        then(dominiRepository).should().findAll(any(Specification.class), any(Sort.class));
        then(dominiRepository).shouldHaveNoMoreInteractions();
        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Consulta de dominis - Sense resultats")
    void whenListDominis_thenReturnEmptyPage() {
        // Given
        List<Domini> dominis = new ArrayList<>();
        given(dominiRepository.findAll(any(Specification.class), any(Sort.class))).willReturn(dominis);

        // When
        Page<DominiDto> page = dominiService.listDominis(
                1L,
                2L,
                null,
                null,
                Pageable.unpaged(),
                null);

        // Then
        then(dominiRepository).should().findAll(any(Specification.class), any(Sort.class));
        then(dominiRepository).shouldHaveNoMoreInteractions();
        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Consulta de domini per entorn i codi")
    void whenGetByEntornAndCodi_thenReturn() {
        // Given
        given(dominiRepository.findByEntornAndCodi(anyLong(), anyString())).willReturn(Optional.of(domini));

        // When
        DominiDto dto = dominiService.getByEntornAndCodi(1L, "COD");

        // Then
        then(dominiRepository).should().findByEntornAndCodi(anyLong(), anyString());
        then(dominiRepository).shouldHaveNoMoreInteractions();
        assertThat(dto).isNotNull();
        DominiTestHelper.comprovaDomini(dto, mapper.entityToDto(domini));
    }

    @Test
    @DisplayName("Consulta de domini per entorn i codi - Error no trobat")
    void whenGetByEntornAndCodi_thenReturnException() {
        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dominiService.getByEntornAndCodi(anyLong(), anyString()));

        // Then
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Consulta de dominis per entorn")
    void whenListDominisByEntorn_thenReturn() {
        // Given
        List<Domini> dominis = new ArrayList<>();
        dominis.add(domini);
        given(dominiRepository.findAll(any(Specification.class), any(Sort.class))).willReturn(dominis);

        // When
        Page<DominiDto> page = dominiService.listDominisByEntorn(
                1L,
                null,
                Pageable.unpaged(),
                null);

        // Then
        then(dominiRepository).should().findAll(any(Specification.class), any(Sort.class));
        then(dominiRepository).shouldHaveNoMoreInteractions();
        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Consulta de domini per tipus d'expedient i codi")
    void whenGetByExpedientTipusAndCodi_thenReturn() {
        // Given
        given(dominiRepository.findByExpedientTipusAndCodiAmbHerencia(anyLong(), anyLong(), anyString())).willReturn(Optional.of(domini));

        // When
        DominiDto dto = dominiService.getByExpedientTipusAndCodi(1L, 2L, "COD");

        // Then
        then(dominiRepository).should().findByExpedientTipusAndCodiAmbHerencia(anyLong(), anyLong(), anyString());
        then(dominiRepository).shouldHaveNoMoreInteractions();
        assertThat(dto).isNotNull();
        DominiTestHelper.comprovaDomini(dto, mapper.entityToDto(domini));
    }

    @Test
    @DisplayName("Consulta de domini per tipus d'expedient i codi - Error no trobat")
    void whenGetByExpedientTipusAndCodi_thenReturnError() {
        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> dominiService.getByExpedientTipusAndCodi(anyLong(), anyLong(),anyString()));

        // Then
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Consulta de dominis per tipus d'expedient")
    void whenListDominisByExpedientTipus_thenReturn() {
        // Given
        List<Domini> dominis = new ArrayList<>();
        dominis.add(domini);
        given(dominiRepository.findAll(any(Specification.class), any(Sort.class))).willReturn(dominis);

        // When
        Page<DominiDto> page = dominiService.listDominisByExpedientTipus(
                1L,
                null,
                Pageable.unpaged(),
                null);

        // Then
        then(dominiRepository).should().findAll(any(Specification.class), any(Sort.class));
        then(dominiRepository).shouldHaveNoMoreInteractions();
        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Consulta de dades de domini")
    void whenConsultaDomini_thenReturn() {
        // Given
        domini.setId(1L);
        ResultatDomini resultatDomini = new ResultatDomini();
        FilaResultat fila = new FilaResultat();
        List<ParellaCodiValor> columnes = new ArrayList<>();
        columnes.add(ParellaCodiValor.builder().codi(CODI).valor(VALOR).build());
        fila.setColumnes(columnes);
        resultatDomini.add(fila);
        IMap<Object, Object> mockedMap = hazelcastFactory.newHazelcastInstance().getMap("HeliumMS");
        given(dominiRepository.findById(anyLong())).willReturn(Optional.of(domini));
        given(dominiWsService.consultaDomini(any(Domini.class), anyString(), nullable(Map.class))).willReturn(resultatDomini);
        given(hazelcastInstance.getMap(anyString())).willReturn(mockedMap);
        System.setProperty("usuari1", "usuari");
        System.setProperty("password1", "password");
        // When
        ResultatDomini resultat = dominiService.consultaDomini(1L, "IDF", null);

        // Then
        then(dominiRepository).should().findById(anyLong());
        then(dominiRepository).shouldHaveNoMoreInteractions();
        then(dominiWsService).should().consultaDomini(any(Domini.class), anyString(), nullable(Map.class));
        then(dominiWsService).shouldHaveNoMoreInteractions();
        assertThat(resultatDomini).isNotNull();
        assertThat(resultatDomini.size()).isEqualTo(1);
        assertThat(resultatDomini.get(0).getColumnes()).isNotNull();
        assertThat(resultatDomini.get(0).getColumnes().size()).isEqualTo(1);
        assertThat(resultatDomini.get(0).getColumnes().get(0).getCodi()).isEqualTo(CODI);
        assertThat(resultatDomini.get(0).getColumnes().get(0).getValor()).isEqualTo(VALOR);
    }
}