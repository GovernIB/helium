package es.caib.helium.expedient.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.ValidationException;

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

import es.caib.helium.expedient.ExpedientTestHelper;
import es.caib.helium.expedient.ProcesTestHelper;
import es.caib.helium.expedient.TascaTestHelper;
import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.domain.Proces;
import es.caib.helium.expedient.domain.Responsable;
import es.caib.helium.expedient.domain.Tasca;
import es.caib.helium.expedient.mapper.TascaMapper;
import es.caib.helium.expedient.model.TascaDto;
import es.caib.helium.expedient.repository.ExpedientRepository;
import es.caib.helium.expedient.repository.ProcesRepository;
import es.caib.helium.expedient.repository.ResponsableRepository;
import es.caib.helium.expedient.repository.TascaRepository;

@ExtendWith(MockitoExtension.class)
class TascaServiceTest {

    @Mock
    TascaRepository tascaRepository;
    
    @Mock
    ExpedientRepository expedientRepository;

    @Mock
    ProcesRepository procesRepository;

    @Mock
	Optional<Expedient> expedientOptional;

    @Mock
	Optional<Proces> procesOptional;

    @Mock
    ResponsableRepository responsableRepository;

    @Mock
    Environment environment;
    
    @Spy
    TascaMapper mapper = Mappers.getMapper(TascaMapper.class);

    @InjectMocks
    TascaServiceImpl tascaService;

    long expedientId = 1L;
    Expedient expedient;
    Proces proces;
    Tasca tasca;

    @BeforeEach
    void setUp() {
        expedient = ExpedientTestHelper.generateExpedient(1, 1L, 1L, expedientId, "1", "1/2021", "Títol 1");
        proces = ProcesTestHelper.generateProces(0, "1", expedient, "JPBM01", "pd1", "descripcio", new Date());
        tasca = TascaTestHelper.generateTasca(1, "1", proces, "nom 1", "títol 1");
    }
    
    @Test
    @DisplayName("Crear tasca")
    void whenCreateTasca_thenReturnTasca() {

        // Given
        TascaDto tascaDto = mapper.entityToDto(tasca);
        tascaDto.setProcesId(tasca.getProces().getProcesId());
        Responsable responsable = tasca.getResponsables().get(0);
        tasca.getResponsables().clear();

        given(procesRepository.findByProcesId(tascaDto.getProcesId())).willReturn(procesOptional);
        given(tascaRepository.save(any(Tasca.class))).willReturn(tasca);
        given(procesOptional.isPresent()).willReturn(true);
        given(procesOptional.get()).willReturn(proces);
        given(responsableRepository.save(any(Responsable.class))).willReturn(responsable);

        // When
        TascaDto creat = tascaService.createTasca(tascaDto);

        // Then
        assertThat(creat).isNotNull();
        TascaTestHelper.comprovaTasca(tascaDto, creat);

        then(procesRepository).should(times(1)).findByProcesId(tascaDto.getProcesId());
        then(procesRepository).shouldHaveNoMoreInteractions();
        then(procesOptional).should(times(1)).isPresent();
        then(procesOptional).should(times(1)).get();
        then(procesOptional).shouldHaveNoMoreInteractions();
        then(tascaRepository).should(times(1)).save(any(Tasca.class));
        then(tascaRepository).shouldHaveNoMoreInteractions();
        then(responsableRepository).should(times(1)).save(any(Responsable.class));
        then(responsableRepository).shouldHaveNoMoreInteractions();
        
    }

    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @DisplayName("Crear tasca - Errors validació blank o negatiu")
    @CsvSource({
            // Descripcio, camp blank o -1
    		"Tasca tascaId -1, tascaId",
	        "Tasca nom '', nom",
	        "Tasca títol '', titol"
    })
    void whenCreateTascaValidacioNegativeBlank_thenReturnError(
            String descripcio,
            String campNull) {

        System.out.println("Executant test: " + descripcio);

        // Given
        switch (campNull) {
	        case "tascaId":
	            tasca.setTascaId("");
	            break;
	        case "nom":
	            tasca.setNom("");
	            break;
	        case "titol":
	            tasca.setTitol("");
	            break;
	        case "dataCreacio":
	            tasca.setDataCreacio(null);
	            break;
        }
        TascaDto tascaDto = mapper.entityToDto(tasca);

        // When
        Exception exception = assertThrows(
                ValidationException.class,
                () -> tascaService.createTasca(tascaDto));

        // Then
        then(tascaRepository).should(never()).save(any(Tasca.class));
        assertThat(exception.getMessage()).contains(campNull + "=");
    }

    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @DisplayName("Crear tasca - Errors validació blank o negatiu")
    @CsvSource({
            // Descripcio, camp null
    		"Tasca tascaId null, tascaId",
	        "Proces null, proces",
	        "Tasca nom null, nom",
	        "Tasca títol null, titol",
	        "Data creació null, dataCreacio"
    })
    void whenCreateTascaValidacioNull_thenReturnError(
            String descripcio,
            String campNull) {

        System.out.println("Executant test: " + descripcio);

        // Given
        switch (campNull) {
	        case "tascaId":
	            tasca.setTascaId(null);
	            break;
	        case "proces":
	            tasca.setProces(null);
	            break;
	        case "nom":
	            tasca.setNom(null);
	            break;
	        case "titol":
	            tasca.setTitol(null);
	            break;
	        case "dataCreacio":
	            tasca.setDataCreacio(null);
	            break;
        }
        TascaDto tascaDto = mapper.entityToDto(tasca);

        // When
        Exception exception = assertThrows(
                ValidationException.class,
                () -> tascaService.createTasca(tascaDto));

        // Then
        then(tascaRepository).should(never()).save(any(Tasca.class));
        assertThat(exception.getMessage()).contains(campNull + "=");
    }


    @Test
    @DisplayName("Modificar tasca")
    void whenUpdateTasca_thenReturnTasca() {
        // Given
        Tasca tasca2 = TascaTestHelper.generateTasca(2, "2", proces, "nom 2", "títol 2");
        TascaDto tascaDto = mapper.entityToDto(tasca2);
        given(tascaRepository.findByTascaId(any())).willReturn(Optional.of(tasca2));
        given(tascaRepository.save(any(Tasca.class))).will(
                (InvocationOnMock invocation) -> invocation.getArgument(0, Tasca.class));

        // When
        TascaDto response = tascaService.updateTasca("2", tascaDto);

        // Then
        assertThat(response).isNotNull();
        tascaDto.setId(response.getId());
        TascaTestHelper.comprovaTasca(tascaDto, response);
        then(tascaRepository).should().findByTascaId(any());
        then(tascaRepository).should().save(any(Tasca.class));
        then(tascaRepository).shouldHaveNoMoreInteractions();
    }

    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @DisplayName("Modificar tasca - Errors validació")
    @CsvSource({
	        // Descripcio, camp null o negatiu
			"Tasca tascaId '', tascaId",
	        "Tasca nom blank, nom,",
	        "Tasca títol blank, titol"
    })
    void whenUpdateTascaValidacioBlank_thenReturnError(
            String descripcio,
            String campNull) {

        System.out.println("Executant test: " + descripcio);

        // Given
        given(tascaRepository.findByTascaId(any())).willReturn(Optional.of(tasca));
        switch (campNull) {
	        case "tascaId":
	            tasca.setTascaId("");
	            break;
	        case "nom":
	            tasca.setNom("");
	            break;
	        case "titol":
	            tasca.setTitol("");
	            break;
	    }
        TascaDto tascaDto = mapper.entityToDto(tasca);

        // When
        Exception exception = assertThrows(
                ValidationException.class,
                () -> tascaService.updateTasca("1", tascaDto));

        // Then
        then(tascaRepository).should(never()).save(any(Tasca.class));
        assertThat(exception.getMessage()).contains(campNull + "=");
    }

    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @DisplayName("Modificar tasca - Errors validació")
    @CsvSource({
	        // Descripcio, camp null o negatiu
			"Tasca tascaId null, tascaId",
			"Proces null, proces",
	        "Tasca nom null, nom",
	        "Tasca títol null, titol",
	        "Data creació null, dataCreacio"
    })
    void whenUpdateTascaValidacioNull_thenReturnError(
            String descripcio,
            String campNull) {

        System.out.println("Executant test: " + descripcio);

        // Given
        given(tascaRepository.findByTascaId(any())).willReturn(Optional.of(tasca));
        switch (campNull) {
	        case "tascaId":
	            tasca.setTascaId(null);
	            break;
	        case "proces":
	            tasca.setProces(null);
	            break;
	        case "nom":
	            tasca.setNom(null);
	            break;
	        case "titol":
	            tasca.setTitol(null);
	            break;
	        case "dataCreacio":
	            tasca.setDataCreacio(null);
	            break;
	    }
        TascaDto tascaDto = mapper.entityToDto(tasca);

        // When
        Exception exception = assertThrows(
                ValidationException.class,
                () -> tascaService.updateTasca("1", tascaDto));

        // Then
        then(tascaRepository).should(never()).save(any(Tasca.class));
        assertThat(exception.getMessage()).contains(campNull + "=");
    }

    
    @Test
    @DisplayName("Eliminar tasca")
    void whenDeleteTasca() {
        // Given
        given(tascaRepository.findByTascaId(any())).willReturn(Optional.of(tasca));

        // When
        tascaService.delete("1");

        // Then
        then(tascaRepository).should().findByTascaId(any());
        then(tascaRepository).should().delete("1");
        then(tascaRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Consulta tasca per id")
    void whenGetById_thenReturn() {
        // Given
        given(tascaRepository.findByTascaId(any())).willReturn(Optional.of(tasca));

        // When
        TascaDto dto = tascaService.getById("1");

        // Then
        then(tascaRepository).should().findByTascaId(any());
        then(tascaRepository).shouldHaveNoMoreInteractions();
        assertThat(dto).isNotNull();
        TascaTestHelper.comprovaTasca(dto, mapper.entityToDto(tasca));

    }

    @Test
    @DisplayName("Consulta tasca per id - Error no trobat")
    void whenGetById_thenReturnError() {
        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> tascaService.getById("1"));

        // Then
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Consulta tasques")
    void whenListTasques_thenReturn() {
        // Given
        List<Tasca> tasques = new ArrayList<>();
        tasques.add(tasca);
        given(tascaRepository.findAll(any(Specification.class), any(Sort.class))).willReturn(tasques);

        // When
		Page<TascaDto> page = tascaService.listTasques(null, null, null, null, null, null, null, null, null, null, null,
				null, false, false, false, null, Pageable.unpaged(), null);

        // Then
        then(tascaRepository).should().findAll(any(Specification.class), any(Sort.class));
        then(tascaRepository).shouldHaveNoMoreInteractions();
        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Consulta de tasques - Sense resultats")
    void whenListTasques_thenReturnEmptyPage() {
        // Given
        List<Tasca> tasques = new ArrayList<>();
        given(tascaRepository.findAll(any(Specification.class), any(Sort.class))).willReturn(tasques);

        // When
		Page<TascaDto> page = tascaService.listTasques(null, null, null, null, null, null, null, null, null, null, null,
				null, false, false, false, null, Pageable.unpaged(), null);

        // Then
        then(tascaRepository).should().findAll(any(Specification.class), any(Sort.class));
        then(tascaRepository).shouldHaveNoMoreInteractions();
        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(0L);
    }
}