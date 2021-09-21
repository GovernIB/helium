package es.caib.helium.expedient.service;

import es.caib.helium.client.dada.DadaClient;
import es.caib.helium.expedient.ExpedientTestHelper;
import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.mapper.ExpedientMapper;
import es.caib.helium.expedient.model.ExpedientDto;
import es.caib.helium.expedient.model.ExpedientEstatTipusEnum;
import es.caib.helium.expedient.repository.ExpedientRepository;
import es.caib.helium.expedient.repository.ProcesRepository;
import es.caib.helium.expedient.repository.TascaRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ExpedientServiceTest {

    @Mock
    ExpedientRepository expedientRepository;

    @Mock
    TascaRepository tascaRepository;

    @Mock
    ProcesRepository procesRepository;

    @Mock
    DadaClient dadaClient;

    @Mock
    Environment environment;

    @Spy
    ExpedientMapper mapper = Mappers.getMapper(ExpedientMapper.class);

    @InjectMocks
    ExpedientServiceImpl expedientService;

    Expedient expedient;

    @BeforeEach
    void setUp() {
        // Index, EntornId, ExpedientTipusId, ExpedientId, ProcessInstanceId, Numero, Titol
        expedient = ExpedientTestHelper.generateExpedient(1, 1L, 1L, 1L, "6", "1/2021", "Títol 1");
        expedient.setEstatTipus(ExpedientEstatTipusEnum.CUSTOM);
        expedient.setEstatId(1L);;
    }

    @Test
    @DisplayName("Crear expedient")
    void whenCreateExpedient_thenReturnExpedient() throws Exception {

        // Given
        ExpedientDto expedientDto = mapper.entityToDto(expedient);
        given(expedientRepository.save(any(Expedient.class))).willReturn(expedient);
        given(procesRepository.findAll(any(Specification.class))).willReturn(null);

        // When
        ExpedientDto creat = expedientService.createExpedient(expedientDto);

        // Then
        assertThat(creat).isNotNull();
        ExpedientTestHelper.comprovaExpedient(expedientDto, creat);
        then(expedientRepository).should(times(1)).save(any(Expedient.class));
        then(expedientRepository).shouldHaveNoMoreInteractions();
        then(dadaClient).should(times(1)).crearExpedient(any(es.caib.helium.client.dada.model.Expedient.class));
        then(dadaClient).shouldHaveNoMoreInteractions();
    }

    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @DisplayName("Crear expedient - Errors validació")
    @CsvSource({
            // Descripcio, camp null
	        "Entorn id -1, entornId",
	        "Expedient tipus id -1, expedientTipusId",
	        "Expedient id -1, id",
	        "Process instance id '', processInstanceId",
	        "Número '', numero",
            "Estat id null, estatId",
    })
    void whenCreateExpedientValidacioNegativeBlank_thenReturnError(
            String descripcio,
            String campNull) {

        System.out.println("Executant test: " + descripcio);

        // Given
        switch (campNull) {
	        case "entornId":
	            expedient.setEntornId(-1L);
	            break;
	        case "expedientTipusId":
	            expedient.setExpedientTipusId(-1L);
	            break;
	        case "id":
	            expedient.setId(-1L);
	            break;
	        case "processInstanceId":
	            expedient.setProcessInstanceId("");
	            break;
	        case "numero":
	            expedient.setNumero("");
	            break;
            case "estatId":
                expedient.setEstatTipus(ExpedientEstatTipusEnum.CUSTOM);
                expedient.setEstatId(null);
        }
        ExpedientDto expedientDto = mapper.entityToDto(expedient);

        // When
        Exception exception = assertThrows(
                ValidationException.class,
                () -> expedientService.createExpedient(expedientDto));

        // Then
        then(expedientRepository).should(never()).save(any(Expedient.class));
        assertThat(exception.getMessage()).contains(campNull + "=");
    }

    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @DisplayName("Crear expedient - Errors validació")
    @CsvSource({
            // Descripcio, camp null
	        "Entorn id null, entornId",
	        "Expedient tipus id null, expedientTipusId",
	        "Expedient id null, id",
	        "Process instance id null, processInstanceId",
	        "Número null, numero",
            "Data Inici null, dataInici",
            "Estat tipus null, estatTipus",
            "Estat id null, estatId"
    })
    void whenCreateExpedientValidacio_thenReturnError(
            String descripcio,
            String campNull) {

        System.out.println("Executant test: " + descripcio);

        // Given
        switch (campNull) {
	        case "entornId":
	            expedient.setEntornId(null);
	            break;
	        case "expedientTipusId":
	            expedient.setExpedientTipusId(null);
	            break;
	        case "id":
	            expedient.setId(null);
	            break;
	        case "processInstanceId":
	            expedient.setProcessInstanceId(null);
	            break;
	        case "numero":
	            expedient.setNumero(null);
	            break;
            case "dataInici":
                expedient.setDataInici(null);
                break;
            case "estatTipus":
                expedient.setEstatTipus(null);
	        case "estatId":
	            expedient.setEstatId(null);
        }
        ExpedientDto expedientDto = mapper.entityToDto(expedient);

        // When
        Exception exception = assertThrows(
                ValidationException.class,
                () -> expedientService.createExpedient(expedientDto));

        // Then
        then(expedientRepository).should(never()).save(any(Expedient.class));
        assertThat(exception.getMessage()).contains(campNull + "=");
    }


    @Test
    @DisplayName("Modificar expedient")
    void whenUpdateExpedient_thenReturnExpedient() {
        // Given
        ExpedientDto expedientDto = mapper.entityToDto(expedient);
        Expedient expedient2 = ExpedientTestHelper.generateExpedient(2, 1L, 1L, 2L, "7", "2/2021", "Títol 2");
        expedient2.setNumeroDefault(expedient.getNumeroDefault());
        given(expedientRepository.findById(anyLong())).willReturn(Optional.of(expedient2));
        given(expedientRepository.save(any(Expedient.class))).will(
                (InvocationOnMock invocation) -> invocation.getArgument(0, Expedient.class));

        // When
        ExpedientDto response = expedientService.updateExpedient(2L, expedientDto);

        // Then
        assertThat(response).isNotNull();
        expedientDto.setId(response.getId());
        ExpedientTestHelper.comprovaExpedient(expedientDto, response);
        then(expedientRepository).should().findById(anyLong());
        then(expedientRepository).should().save(any(Expedient.class));
        then(expedientRepository).shouldHaveNoMoreInteractions();
    }

    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @DisplayName("Crear expedient - Errors validació")
    @CsvSource({
	        // Descripcio, camp null
	        "Entorn id null, entornId",
	        "Expedient tipus id null, expedientTipusId",
	        "Expedient id null, id",
	        "Process instance id null, processInstanceId",
	        "Número null, numero",
	        "Data Inici null, dataInici",
	        "Estat codi null, estatTipus",
            "Estat id null, estatId"
    })
    void whenUpdateExpedientValidacio_thenReturnError(
            String descripcio,
            String campNull) {

        System.out.println("Executant test: " + descripcio);

        // Given
        given(expedientRepository.findById(anyLong())).willReturn(Optional.of(expedient));
        switch (campNull) {
	        case "entornId":
	            expedient.setEntornId(null);
	            break;
	        case "expedientTipusId":
	            expedient.setExpedientTipusId(null);
	            break;
	        case "id":
	            expedient.setId(null);
	            break;
	        case "processInstanceId":
	            expedient.setProcessInstanceId(null);
	            break;
	        case "numero":
	            expedient.setNumero(null);
	            break;
	        case "dataInici":
	            expedient.setDataInici(null);
	            break;
	        case "estatTipus":
	            expedient.setEstatTipus(null);
	        case "estatId":
	            expedient.setEstatId(null);
	    }
        ExpedientDto expedientDto = mapper.entityToDto(expedient);

        // When
        Exception exception = assertThrows(
                ValidationException.class,
                () -> expedientService.updateExpedient(1L, expedientDto));

        // Then
        then(expedientRepository).should(never()).save(any(Expedient.class));
        assertThat(exception.getMessage()).contains(campNull + "=");
    }

    @Test
    @DisplayName("Eliminar expedient")
    void whenDeleteExpedient() throws Exception {
        // Given
        given(expedientRepository.findById(anyLong())).willReturn(Optional.of(expedient));

        // When
        expedientService.delete(1L);

        // Then
        then(expedientRepository).should().findById(anyLong());
        then(expedientRepository).should().delete(anyLong());
        then(expedientRepository).shouldHaveNoMoreInteractions();
        then(dadaClient).should(times(1)).deleteExpedient(1L);
        then(dadaClient).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Consulta expedient per id")
    void whenGetById_thenReturn() {
        // Given
        given(expedientRepository.findById(anyLong())).willReturn(Optional.of(expedient));

        // When
        ExpedientDto dto = expedientService.getById(1L);

        // Then
        then(expedientRepository).should().findById(anyLong());
        then(expedientRepository).shouldHaveNoMoreInteractions();
        assertThat(dto).isNotNull();
        ExpedientTestHelper.comprovaExpedient(dto, mapper.entityToDto(expedient));

    }

    @Test
    @DisplayName("Consulta expedient per id - Error no trobat")
    void whenGetById_thenReturnError() {
        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> expedientService.getById(1L));

        // Then
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Consulta expedients")
    void whenListExpedients_thenReturn() {
        // Given
        List<Expedient> expedients = new ArrayList<>();
        expedients.add(expedient);
        given(expedientRepository.findAll(any(Specification.class), any(Sort.class))).willReturn(expedients);

        // When
		Page<ExpedientDto> page = expedientService.listExpedients(null, null, null, null, null, null, null, null, null, null, null, false, false,
				null, false, false, false, false, false, false, false, null, Pageable.unpaged(), null);

        // Then
        then(expedientRepository).should().findAll(any(Specification.class), any(Sort.class));
        then(expedientRepository).shouldHaveNoMoreInteractions();
        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Consulta de expedients - Sense resultats")
    void whenListExpedients_thenReturnEmptyPage() {
        // Given
        List<Expedient> expedients = new ArrayList<>();
        given(expedientRepository.findAll(any(Specification.class), any(Sort.class))).willReturn(expedients);

        // When
		Page<ExpedientDto> page = expedientService.listExpedients(null, null, null, null, null, null, null, null, null, null, null, false, false,
				null, false, false, false, false, false, false, false, null, Pageable.unpaged(), null);

        // Then
        then(expedientRepository).should().findAll(any(Specification.class), any(Sort.class));
        then(expedientRepository).shouldHaveNoMoreInteractions();
        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(0L);
    }
}