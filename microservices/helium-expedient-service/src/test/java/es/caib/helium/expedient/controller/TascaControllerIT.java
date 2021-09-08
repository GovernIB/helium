package es.caib.helium.expedient.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.expedient.ProcesTestHelper;
import es.caib.helium.expedient.TascaTestHelper;
import es.caib.helium.expedient.model.ProcesDto;
import es.caib.helium.expedient.model.TascaDto;
import es.caib.helium.expedient.repository.TascaRepository;
import es.caib.helium.expedient.service.ExpedientService;
import es.caib.helium.expedient.service.ProcesService;
import es.caib.helium.expedient.service.TascaService;
import es.caib.helium.ms.model.PagedList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class TascaControllerIT {

    public static final String API_V1_TASCA = "/api/v1/tasques/";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    TascaService tascaService;
    @Autowired
    ExpedientService expedientService;
    @Autowired
    ProcesService procesService;
    @Autowired
    TascaRepository tascaRepository;
    
    @Spy
    ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        // Processos
        ProcesDto proces1 = procesService.createProces(ProcesTestHelper.generateProcesDto(0, "p1", 1L, "pd1", "desc1"));
        ProcesDto proces2 = procesService.createProces(ProcesTestHelper.generateProcesDto(0, "p2", 2L, "pd2", "desc1"));
    	// int index, Long entorn, Long tascaTipus, Long tascaId, 
    	// Long tascaProcessInstanceId, String tascaNumero,String tascaTitol
    	// Tasques
        tascaService.createTasca(TascaTestHelper.generateTascaDto(0, "1", proces1.getProcesId(), "tasca1", "Tasca 1"));
        tascaService.createTasca(TascaTestHelper.generateTascaDto(1, "2", proces1.getProcesId(), "tasca2", "Tasca 2"));
        tascaService.createTasca(TascaTestHelper.generateTascaDto(2, "3", proces1.getProcesId(), "tasca3", "Tasca 3"));
        tascaService.createTasca(TascaTestHelper.generateTascaDto(3, "4", proces2.getProcesId(), "tasca4", "Tasca 4"));
        tascaService.createTasca(TascaTestHelper.generateTascaDto(4, "5", proces2.getProcesId(), "tasca5", "Tasca 5"));
        
//        List<String> responsables = new ArrayList<String>();
//        for (long i = 1; i <= 5L; i++) {
//        	responsables.add("usuari" + i);
//        	tascaService.setResponsables(i, responsables);
//        }
    }

    @Test
    @DisplayName("Consulta llista de tasques")
    void whenListTasquesV1_thenReturnList() throws Exception {

        String url = API_V1_TASCA + "?entornId=1";

        PagedList<TascaDto> pagedList = restTemplate.exchange(
        		url,
        		HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<PagedList<TascaDto>>() {}).getBody();

        assertThat(pagedList.getContent()).hasSize(5);

        pagedList.getContent().forEach(tascaDto -> {
            TascaDto fetchedTascadto = restTemplate.getForObject(API_V1_TASCA + tascaDto.getId(), TascaDto.class);
            assertThat(tascaDto.getId()).isEqualTo(fetchedTascadto.getId());
        });
    }

    @Test
    @DisplayName("Consulta llistat de tasques sense resposta HttpStatus.NO_CONTENT")
    void whenListTasquesV1_thenReturnNoContent() throws Exception {
        
		final UriComponentsBuilder builder = UriComponentsBuilder.fromPath(API_V1_TASCA);
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("entornId", "1");
		queryParams.put("expedientTipusId", "8");
		queryParams.put("usuariAssignat", "admin");
		queryParams.put("nom", "Tasca nom");
		queryParams.put("titol", "Tasca títol");
		queryParams.put("expedientId", "1");
		queryParams.put("expedientTitol", "Expedient títol");
		queryParams.put("expedientNumero", "Expedient número");
		queryParams.put("dataCreacioInici", "01-06-2021");
		queryParams.put("dataCreacioFi", "02-06-2021");
		queryParams.put("dataLimitInici", "04-06-2021");
		queryParams.put("dataLimitFi", "05-06-2021");
		queryParams.put("mostrarAssignadesUsuari", "true");
		queryParams.put("mostrarAssignadesGrup", "true");
		queryParams.put("nomesPendents", "true");
		queryParams.put("filtre", "nom=ic=nom");		
        
		if (queryParams != null) {
			for (String param : queryParams.keySet())
				builder.queryParam(param, queryParams.get(param));
		}
		String url = builder.build().toUri().toString();
			
        ResponseEntity<PagedList<TascaDto>> response = restTemplate.exchange(
        		url,
        		HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<PagedList<TascaDto>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Creació tasca amb excepció DataIntegrityViolation")
    void whencreateTascaV1_thenDataIntegrityViolationException() throws Exception {

        String url = API_V1_TASCA;
        
        TascaDto tascaIdExistent = TascaTestHelper.generateTascaDto(1, "1", "p1",  "tasca1", "Tasca 1");

        ResponseEntity<Void> response = 
        		restTemplate.exchange(
        				url, 
        				HttpMethod.POST, 
        				new HttpEntity<TascaDto>(tascaIdExistent),
        				Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Creació tasca correcta")
    void whencreateTascaV1_thenResponseEntityCreated() throws Exception {

        String url = API_V1_TASCA;
        
        TascaDto tasca6 = TascaTestHelper.generateTascaDto(6, "6", "p1", "tasca6", "Tasca 6");

        ResponseEntity<Void> response = 
        		restTemplate.exchange(
        				url, 
        				HttpMethod.POST, 
        				new HttpEntity<TascaDto>(tasca6),
        				Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
    
    
    @Test
    @DisplayName("Modificació tasca 1 correcta")
    void whenUpdateTascaV1_thenResponseEntityNoConent() throws Exception {

        String url = API_V1_TASCA + 1L;
        
        TascaDto tasca1 = restTemplate.getForObject(url, TascaDto.class);

        tasca1.setTitol(tasca1.getTitol() + " XXX");
        
        ResponseEntity<Void> response = 
        		restTemplate.exchange(
        				url, 
        				HttpMethod.PUT, 
        				new HttpEntity<TascaDto>(tasca1),
        				Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        tasca1 = restTemplate.getForObject(url, TascaDto.class);
        
        assertThat(tasca1.getTitol()).endsWith("XXX");
    }
    
    @Test
    @DisplayName("Patch tasca 1 correcta")
    void whenPatchTascaV1_thenResponseEntityNoContent() throws Exception {

        String url = API_V1_TASCA + 1L;
                
        String jsonString =
                "[" +
                    "{\"op\":\"replace\", \"path\":\"/titol\", \"value\":\"" + "XXX" + "\"}" +
                "]";
        JsonNode tascaPathJson = objectMapper.readTree(jsonString);
        
        ResponseEntity<Void> response = 
        		restTemplate.exchange(
        				url, 
        				HttpMethod.PATCH, 
        				new HttpEntity<JsonNode>(tascaPathJson),
        				Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        // Comprova que s'ha fixat correctament el títol
        TascaDto tasca1 = restTemplate.getForObject(url, TascaDto.class);
        
        assertThat(tasca1.getTitol()).asString().isEqualTo("XXX");
    }
    
    @Test
    @DisplayName("Esborrar tasca 1 correcta")
    void whenDeleteTascaV1_thenResponseEntityNoContent() throws Exception {

        String url = API_V1_TASCA + 1L;        
        
        ResponseEntity<Void> response = 
        		restTemplate.exchange(
        				url, 
        				HttpMethod.DELETE, 
        				null,
        				Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        TascaDto tasca1 = restTemplate.getForObject(API_V1_TASCA + 1L, TascaDto.class);
        
        assertThat(tasca1.getId()).isNull();
    }
    
    
    @Test
    @DisplayName("Consulta llista de responsables de la tasca 5")
    void whenGetResponsablesV1_thenReturnResponsablesList() throws Exception {

        String url = API_V1_TASCA + "/5/responsables";

        ResponseEntity<List<String>> responsables = restTemplate.exchange(
        		url,
        		HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<String>>() {});

        assertThat(responsables.getBody()).hasSize(4);

        for(String responsable : responsables.getBody()) {
            assertThat(responsable.startsWith("usuari"));
            assertThat(responsable.length() == "usuari".length() + 1);
        }
    }
    
    @Test
    @DisplayName("Modificar llista de responsables de la tasca 4")
    void whenSetResponsablesV1_thenReturnResponsablesList() throws Exception {

        List<String> codisUsuaris = new ArrayList<String>();
        String codiUsuari = String.valueOf(new Date().getTime()); 
        codisUsuaris.add(codiUsuari);

        String url = API_V1_TASCA + "/4/responsables";
		final UriComponentsBuilder builder = UriComponentsBuilder.fromPath(url);
		builder.queryParam("responsables", codiUsuari);
		url = builder.build().toUri().toString();

        ResponseEntity<Void> response = 
        		restTemplate.exchange(
        				url, 
        				HttpMethod.POST, 
        				new HttpEntity<List<String>>(codisUsuaris),
        				Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        ResponseEntity<List<String>> codisUsuarisFetched = restTemplate.exchange(
        		url,
        		HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<String>>() {});

        assertThat(codisUsuarisFetched.getBody()).hasSize(1);
        assertThat(codisUsuarisFetched.getBody().get(0)).isEqualTo(codiUsuari);
    }
    
    @Test
    @DisplayName("Esborrar responsables tasca 3 correcta")
    void whenDeleteResponsablesV1_thenResponseEntityNoContent() throws Exception {

        String url = API_V1_TASCA + "/3/responsables";
        
        ResponseEntity<Void> response = 
        		restTemplate.exchange(
        				url, 
        				HttpMethod.DELETE, 
        				null,
        				Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        ResponseEntity<List<String>> responsables = restTemplate.exchange(
        		url,
        		HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<String>>() {});

        assertThat(responsables.getBody()).hasSize(0);
    }
    
    
    @AfterEach
    void tearDown() {
        for (int i = 1; i <= 6; i++)
        	try {
        		tascaService.delete(String.valueOf(i));
        	} catch (Exception e) {
        		System.err.println("Error esborrant la tasca amb ID " + i + ": " + e.getMessage());
        	}
        for (long i = 1L; i <= 2; i++)
        	try {
        		procesService.delete("p"+i);
        	} catch (Exception e) {
        		System.err.println("Error esborrant el procés amb ID p" + i + ": " + e.getMessage());
        	}
        for (long i = 1L; i <= 2; i++)
        	try {
        		expedientService.delete(i);
        	} catch (Exception e) {
        		System.err.println("Error esborrant l'expedient amb ID " + i + ": " + e.getMessage());
        	}
    }

}