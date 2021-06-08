package es.caib.helium.expedient.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.expedient.ExpedientTestHelper;
import es.caib.helium.expedient.model.ExpedientDto;
import es.caib.helium.expedient.repository.ExpedientRepository;
import es.caib.helium.expedient.service.ExpedientService;
import es.caib.helium.ms.model.PagedList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class ExpedientControllerIT {

    public static final String API_V1_EXPEDIENT = "/api/v1/expedients/";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ExpedientService expedientService;
    @Autowired
    ExpedientRepository expedientRepository;
    
    @Spy
    ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
    	// int index, Long entorn, Long expedientTipus, Long expedientId, 
    	// Long expedientProcessInstanceId, String expedientNumero,String expedientTitol
        expedientService.createExpedient(ExpedientTestHelper.generateExpedientDto(1, 1L, 1L, 1L, "1", "1/2021", "Expedient 1"));
        expedientService.createExpedient(ExpedientTestHelper.generateExpedientDto(2, 1L, 1L, 2L, "2", "2/2021", "Expedient 2"));
        expedientService.createExpedient(ExpedientTestHelper.generateExpedientDto(3, 2L, 1L, 3L, "3", "3/2021", "Expedient 3"));
        expedientService.createExpedient(ExpedientTestHelper.generateExpedientDto(4, 2L, 1L, 4L, "4", "4/2021", "Expedient 4"));
        expedientService.createExpedient(ExpedientTestHelper.generateExpedientDto(5, 2L, 1L, 5L, "5", "5/2021", "Expedient 5"));
    }

    @Test
    @DisplayName("Consulta de dades de expedient")
    void whenListExpedientsV1_thenReturnList() throws Exception {

        String url = API_V1_EXPEDIENT + "?entornId=2";

        PagedList<ExpedientDto> pagedList = restTemplate.exchange(
        		url,
        		HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<PagedList<ExpedientDto>>() {}).getBody();

        assertThat(pagedList.getContent()).hasSize(3);

        pagedList.getContent().forEach(expedientDto -> {
            ExpedientDto fetchedExpedientdto = restTemplate.getForObject(API_V1_EXPEDIENT + expedientDto.getId(), ExpedientDto.class);

            assertThat(expedientDto.getId()).isEqualByComparingTo(fetchedExpedientdto.getId());
        });
    }

    @Test
    @DisplayName("Consulta sense resposta HttpStatus.NO_CONTENT")
    void whenListExpedientsV1_thenReturnNoContent() throws Exception {

        String url = API_V1_EXPEDIENT + "?entornId=3";
        
        ResponseEntity<PagedList<ExpedientDto>> response = restTemplate.exchange(
        		url,
        		HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<PagedList<ExpedientDto>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Creació expedient amb excepció DataIntegrityViolation")
    void whencreateExpedientV1_thenDataIntegrityViolationException() throws Exception {

        String url = API_V1_EXPEDIENT;
        
        ExpedientDto expedientIdExistent = ExpedientTestHelper.generateExpedientDto(1, 1L, 1L, 1L, "1", "1/2021", "Expedient 1");
        
        ResponseEntity<Void> response = 
        		restTemplate.exchange(
        				url, 
        				HttpMethod.POST, 
        				new HttpEntity<ExpedientDto>(expedientIdExistent),
        				Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Creació expedient correcta")
    void whencreateExpedientV1_thenResponseEntityCreated() throws Exception {

        String url = API_V1_EXPEDIENT;
        
        ExpedientDto expedient6 = ExpedientTestHelper.generateExpedientDto(6, 1L, 1L, 6L, "6", "6/2021", "Expedient 6");

        ResponseEntity<Void> response = 
        		restTemplate.exchange(
        				url, 
        				HttpMethod.POST, 
        				new HttpEntity<ExpedientDto>(expedient6),
        				Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
    
    
    @Test
    @DisplayName("Modificació expedient 1 correcta")
    void whenUpdateExpedientV1_thenResponseEntityNoConent() throws Exception {

        String url = API_V1_EXPEDIENT + 1L;
        
        ExpedientDto expedient1 = restTemplate.getForObject(url, ExpedientDto.class);

        expedient1.setTitol(expedient1.getTitol() + " XXX");
        
        ResponseEntity<Void> response = 
        		restTemplate.exchange(
        				url, 
        				HttpMethod.PUT, 
        				new HttpEntity<ExpedientDto>(expedient1),
        				Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        expedient1 = restTemplate.getForObject(API_V1_EXPEDIENT + 1L, ExpedientDto.class);
        
        assertThat(expedient1.getTitol()).endsWith("XXX");
    }
    
    @Test
    @DisplayName("Patch expedient 1 correcta")
    void whenPatchExpedientV1_thenResponseEntityNoContent() throws Exception {

        String url = API_V1_EXPEDIENT + 1L;
                
        String jsonString =
                "[" +
                    "{\"op\":\"replace\", \"path\":\"/titol\", \"value\":\"" + "XXX" + "\"}" +
                "]";
        JsonNode expedientPathJson = objectMapper.readTree(jsonString);
        
        ResponseEntity<Void> response = 
        		restTemplate.exchange(
        				url, 
        				HttpMethod.PATCH, 
        				new HttpEntity<JsonNode>(expedientPathJson),
        				Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        // Comprova que s'ha fixat correctament el títol
        ExpedientDto expedient1 = restTemplate.getForObject(API_V1_EXPEDIENT, ExpedientDto.class);
        
        assertThat(expedient1.getTitol()).asString().isEqualTo("XXX");
    }
    
    @Test
    @DisplayName("Esborrar expedient 1 correcta")
    void whenDeleteExpedientV1_thenResponseEntityNoContent() throws Exception {

        String url = API_V1_EXPEDIENT + 1L;        
        
        ResponseEntity<Void> response = 
        		restTemplate.exchange(
        				url, 
        				HttpMethod.DELETE, 
        				null,
        				Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        ExpedientDto expedient1 = restTemplate.getForObject(API_V1_EXPEDIENT + 1L, ExpedientDto.class);
        
        assertThat(expedient1.getId()).isNull();
    }

    
    @AfterEach
    void tearDown() {
        // TODO: no funciona el delete del repository
        for (long i = 1L; i <= 5; i++)
        	expedientService.delete(i);
    }

}