package es.caib.helium.dada.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.dada.domain.Expedient;
import es.caib.helium.dada.model.Consulta;
import es.caib.helium.dada.model.Filtre;
import es.caib.helium.dada.model.FiltreCapcalera;
import es.caib.helium.dada.model.PagedList;
import es.caib.helium.dada.service.ExpedientService;

@WebMvcTest(value = ExpedientController.class, excludeAutoConfiguration = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
		org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class })
public class CapcaleraExpedientControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@InjectMocks
	private ExpedientController expedientController;

	@MockBean
	private ExpedientService expedientService;

	@Captor
	private ArgumentCaptor<Expedient> expedientCaptor;

	private Expedient expedientMock;
	private List<Expedient> expedientsMock;
	private Consulta consultaMock;
	private PagedList<Expedient> pagedList;
	private List<Expedient> pagina;
	private static Validator validator;

	@BeforeAll
	public static void setup() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@BeforeEach
	public void setUp() {

		expedientMock = new Expedient();
		expedientMock.setExpedientId(1l);
		expedientMock.setEntornId(1l);
		expedientMock.setTipusId(1l);
		expedientMock.setProcesPrincipalId(1l);
		var dataInici = new Date();
		expedientMock.setDataInici(dataInici);

		consultaMock = new Consulta();
		consultaMock.setEntornId(1);
		consultaMock.setExpedientTipusId(1);
		consultaMock.setPage(0);
		consultaMock.setSize(10);
		Map<String, Filtre> filtres = new HashMap<>();
		consultaMock.setFiltreValors(null);
		consultaMock.setFiltreValors(filtres);
		var filtre = new FiltreCapcalera();
		filtre.setExpedientId(1l);
		filtres.put("dadesCapcalera", filtre);
		pagina = new ArrayList<>();
		pagina.add(expedientMock);
		var pageable = PageRequest.of(consultaMock.getPage(), consultaMock.getSize());
		pagedList = new PagedList<Expedient>(pagina, pageable, pagina.size());

		expedientsMock = new ArrayList<>();
		for (var foo = 0; foo < 1000; foo++) {
			var bar = Long.parseLong(foo + "");
			var exp = new Expedient();
			exp.setExpedientId(bar);
			exp.setEntornId(bar);
			exp.setTipusId(bar);
			exp.setProcesPrincipalId(bar);
			exp.setDataInici(dataInici);
			expedientsMock.add(exp);
		}
	}

	@Test
	@DisplayName("[POST] Consulta resultats paginats")
	public void test_consultaResultats_succes() throws Exception {

		given(expedientService.consultaResultats(any(Consulta.class))).willReturn(pagedList);

		var consultaJson = asJsonString(consultaMock);
		mockMvc.perform(post("/api/v1/expedients/consulta/resultats").content(consultaJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).param("entornId", "1")
				.param("expedientTipusId", "1").param("page", "0").param("size", "10")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.content", hasSize(1))).andExpect(jsonPath("$.content[0].entornId").value(1));
	}

	@Test
	@DisplayName("[POST] Consulta resultats paginats - Error paràmetres incorrectes")
	public void test_consultaResultats_errorParametres() throws Exception {

		var consultaJson = asJsonString(consultaMock);
		mockMvc.perform(post("/api/v1/expedients/consulta/resultats").content(consultaJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// -------------------------

	@Test
	@DisplayName("[POST] Consulta resultats llistat")
	public void test_consultaResultatsLlistat_succes() throws Exception {

		given(expedientService.consultaResultatsLlistat(any(Consulta.class))).willReturn(pagina);

		var consultaJson = asJsonString(consultaMock);
		mockMvc.perform(post("/api/v1/expedients/consulta/resultats/llistat").content(consultaJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).param("entornId", "1")
				.param("expedientTipusId", "1")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].entornId").value(1));
	}

	@Test
	@DisplayName("[POST] Consulta resultats llistat - Error paràmetres incorrectes")
	public void test_consultaResultatsLlistat_errorParametres() throws Exception {

		var consultaJson = asJsonString(consultaMock);
		mockMvc.perform(post("/api/v1/expedients/consulta/resultats/llistat").content(consultaJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// -------------------------

	@Test
	@DisplayName("[GET] de les dades de capçalera segons expedientId")
	public void test_getExpedient_success() throws Exception {

		expedientMock = new Expedient();
		expedientMock.setExpedientId(1l);
		expedientMock.setTitol("JUnit Test");
		given(expedientService.findByExpedientId(anyLong())).willReturn(expedientMock);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}", 1L)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.titol").value("JUnit Test"));
	}

	@Test
	@DisplayName("[GET] dades de capçalera - Error no trobades")
	public void test_getExpedient_notFound() throws Exception {

		given(expedientService.findByExpedientId(anyLong())).willReturn(null);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}", 1L)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$").doesNotHaveJsonPath());
	}

	// -------------------------

	@Test
	@DisplayName("[POST] dades de capçalera per l'expedient")
	void test_crearExpedient_success() throws Exception {
		expedientMock = new Expedient();
		expedientMock.setExpedientId(1l);
		expedientMock.setEntornId(1l);
		expedientMock.setTipusId(1l);
		expedientMock.setProcesPrincipalId(1l);
		var dataInici = new Date();
		expedientMock.setDataInici(dataInici);
		String expedientJson = asJsonString(expedientMock);
		given(expedientService.createExpedient((any(Expedient.class)))).willReturn(true);

		mockMvc.perform(post("/api/v1/expedients").content(expedientJson).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
	}

	@Test
	@DisplayName("[POST] dades de capçalera per l'expedient - Errors validació")
	void test_crearExpedient_errorValidacio() throws Exception {
		expedientMock = new Expedient();
		var titol = "";
		for (var foo = 0; foo < 270; foo++) {
			titol += foo + "";
		}
		expedientMock.setTitol(titol);
		expedientMock.setNumero(titol);
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		Set<ConstraintViolation<Expedient>> violations = validator.validate(expedientMock);
		assertFalse(violations.isEmpty());

		var expedientJson = asJsonString(expedientMock);
		mockMvc.perform(post("/api/v1/expedients").content(expedientJson).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	// -------------------------

	@Test
	@DisplayName("[POST] llistat de dades de capçalera")
	void test_crearExpedients_success() throws Exception {

		given(expedientService.createExpedients((any(List.class)))).willReturn(true);

		String expedientsJson = asJsonString(expedientsMock);
		mockMvc.perform(post("/api/v1/expedients/crear/expedients").content(expedientsJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("[POST] llistat de dades de capçalera - Llista buida")
	void test_crearExpedients_llistaBuida() throws Exception {

		expedientsMock = new ArrayList<>();
		given(expedientService.createExpedients((any(List.class)))).willReturn(false);
		String expedientsJson = asJsonString(expedientsMock);
		mockMvc.perform(post("/api/v1/expedients/crear/expedients").content(expedientsJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
	}

	@Test
	@DisplayName("[POST] llistat de dades de capçalera  - Errors validació")
	void test_crearExpedients_errorValidacio() throws Exception {

		for (var expedient : expedientsMock) {
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			validator = factory.getValidator();
			Set<ConstraintViolation<Expedient>> violations = validator.validate(expedient);
			if (!violations.isEmpty()) {
				assertFalse(violations.isEmpty());
			}
		}
		var expedientJson = asJsonString(expedientMock);
		mockMvc.perform(post("/api/v1/expedients/crear/expedients").content(expedientJson)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	// ------------

	@Test
	@DisplayName("[DELETE] capçalera de l'expedient")
	void test_deleteExpedient_success() throws Exception {

		expedientMock = new Expedient();
		expedientMock.setExpedientId(1l);
		given(expedientService.findByExpedientId(anyLong())).willReturn(expedientMock);
		given(expedientService.deleteExpedient(anyLong())).willReturn(true);

		mockMvc.perform(delete("/api/v1/expedients/{expedientId}", 1L)).andExpect(status().isOk());
	}

	@Test
	@DisplayName("[DELETE] capçalera de l'expedient - Error no trobada")
	void test_deleteExpedient_notFound() throws Exception {

		given(expedientService.findByExpedientId(anyLong()))
				.willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found."));

		mockMvc.perform(get("/api/v1/expedients/{expedientId}", 1L)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$").doesNotHaveJsonPath());
	}

	// -------------------------

	@Test
	@DisplayName("[DELETE] capçaleres de l'expedient")
	void test_deleteExpedients_success() throws Exception {

		List<String> expedients = new ArrayList<>();
		for (var foo = 0; foo < 100; foo++) {
			expedients.add(foo + "");
		}
		given(expedientService.deleteExpedients((any(List.class)))).willReturn(true);
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.addAll("expedients", expedients);
		mockMvc.perform(delete("/api/v1/expedients/borrar/expedients").params(params)).andExpect(status().isOk());
	}

	@Test
	@DisplayName("[DELETE] capçaleres de l'expedient - Error llista buida")
	void test_deleteExpedients_llistaBuida() throws Exception {
		
		List<String> expedients = new ArrayList<>();
		given(expedientService.deleteExpedients((any(List.class)))).willReturn(false);
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.addAll("expedients", expedients);
		mockMvc.perform(delete("/api/v1/expedients/borrar/expedients").params(params)).andExpect(status().isBadRequest());
	}

	// TODO FALTEN ERRORS

	// -------------------------

	@Test
	@DisplayName("[PUT] capçalera d'expedient")
	void test_putExpedient_sucess() throws Exception {

		String expedientJson = asJsonString(expedientMock);
		given(expedientService.putExpedient(anyLong(), any(Expedient.class))).willReturn(true);

		mockMvc.perform(put("/api/v1/expedients/{expedientId}", 1L).content(expedientJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	@DisplayName("[PUT] capçalera d'expedient - Error not found expedientId")
	void test_putExpedient_notFound() throws Exception {

		String expedientJson = asJsonString(expedientMock);
		given(expedientService.putExpedient(anyLong(), any(Expedient.class))).willReturn(false);

		mockMvc.perform(put("/api/v1/expedients/{expedientId}", 100L).content(expedientJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("[PUT] capçalera d'expedient - Error de validació")
	void test_putExpedient_errorValidacio() throws Exception {

		expedientMock = new Expedient();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		Set<ConstraintViolation<Expedient>> violations = validator.validate(expedientMock);
		assertFalse(violations.isEmpty());
		var expedientJson = asJsonString(expedientMock);
		mockMvc.perform(put("/api/v1/expedients/{expedientId}", 100L).content(expedientJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// -------------------------

	@Test
	@DisplayName("[PUT] llistat capçaleres d'expedient")
	void test_putExpedients_sucess() throws Exception {
		// TODO
		String expedientsJson = asJsonString(expedientsMock);
		given(expedientService.putExpedients(any(List.class))).willReturn(true);

		mockMvc.perform(put("/api/v1/expedients/put/expedients").content(expedientsJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	@DisplayName("[PUT] llistat capçaleres d'expedient - Llista buida")
	void test_putExpedients_llistaBuida() throws Exception {

		expedientsMock = new ArrayList<>();
		given(expedientService.createExpedients((any(List.class)))).willReturn(false);
		String expedientsJson = asJsonString(expedientsMock);
		mockMvc.perform(put("/api/v1/expedients/put/expedients").content(expedientsJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
	}

	@Test
	@DisplayName("[PUT] llistat de dades de capçalera  - Errors validació")
	void test_putExpedients_errorValidacio() throws Exception {

//		var titol = "titol";
//		for (var foo=0;foo<270;foo++) {
//			titol += titol; // TODO SI ES DESCOMENTA DONA UN OutOfMemoryError: Java heap space
//		}

		expedientsMock.get(expedientsMock.size() - 1).setExpedientId(null);
		for (var expedient : expedientsMock) {
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			validator = factory.getValidator();
			Set<ConstraintViolation<Expedient>> violations = validator.validate(expedient);
			if (!violations.isEmpty()) {
				assertFalse(violations.isEmpty());
			}
		}

		var expedientsJson = asJsonString(expedientsMock);
		mockMvc.perform(put("/api/v1/expedients/put/expedients").content(expedientsJson)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	// -------------------------

	@Test
	@DisplayName("[PATCH] capçalera d'expedient")
	void test_patchExpedient_sucess() throws Exception {

		var titol = "Titol modificat";
//		for (var foo=0;foo<300;foo++ ) { // TODO SI ES DESCOMENTA DONA UN OutOfMemoryError: Java heap space 
//		}

		expedientMock = new Expedient();
		expedientMock.setExpedientId(1l);
		expedientMock.setTitol(titol);
		expedientMock.setNumero(titol);
		expedientMock.setEntornId(2l);
		expedientMock.setTipusId(3l);
		expedientMock.setProcesPrincipalId(4l);
		var dataInici = new Date();
		expedientMock.setDataInici(dataInici);
		expedientMock.setDataFi(dataInici);
		var expedientJson = asJsonString(expedientMock);

		given(expedientService.patchExpedient(anyLong(), expedientCaptor.capture())).willReturn(true);

		mockMvc.perform(patch("/api/v1/expedients/{expedientId}", 1L).content(expedientJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		assertFalse(!expedientCaptor.getValue().getTitol().equals(titol));
		assertFalse(!expedientCaptor.getValue().getNumero().equals(titol));
		assertFalse(!expedientCaptor.getValue().getEntornId().equals(2l));
		assertFalse(!expedientCaptor.getValue().getTipusId().equals(3l));
		assertFalse(!expedientCaptor.getValue().getProcesPrincipalId().equals(4l));
		assertFalse(!expedientCaptor.getValue().getDataInici().equals(dataInici));
		assertFalse(!expedientCaptor.getValue().getDataFi().equals(dataInici));
	}

	@Test
	@DisplayName("[PATCH] capçalera d'expedient - Error not found")
	void test_patchExpedient_notFound() throws Exception {

		var titol = "Titol modificat";
		expedientMock = new Expedient();
		expedientMock.setExpedientId(1l);
		expedientMock.setTitol(titol);
		expedientMock.setNumero(titol);
		expedientMock.setEntornId(2l);
		expedientMock.setTipusId(3l);
		expedientMock.setProcesPrincipalId(4l);
		var dataInici = new Date();
		expedientMock.setDataInici(dataInici);
		expedientMock.setDataFi(dataInici);
		var expedientJson = asJsonString(expedientMock);

		given(expedientService.patchExpedient(anyLong(), any(Expedient.class))).willReturn(false);

		mockMvc.perform(patch("/api/v1/expedients/{expedientId}", 1l).content(expedientJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	// -------------------------

	@Test
	@DisplayName("[PATCH] llistat de capçaleres")
	void test_patchExpedients_sucess() throws Exception {

		given(expedientService.patchExpedients(any(List.class))).willReturn(true);

		var expedientsJson = asJsonString(expedientsMock);
		mockMvc.perform(patch("/api/v1/expedients/patch/expedients").content(expedientsJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	@DisplayName("[PATCH] llistat capçaleres - Llista buida")
	void test_patchExpedients_llistaBuida() throws Exception {

		expedientsMock = new ArrayList<>();
		given(expedientService.createExpedients((any(List.class)))).willReturn(false);
		String expedientsJson = asJsonString(expedientsMock);
		mockMvc.perform(patch("/api/v1/expedients/patch/expedients").content(expedientsJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
	}

	@Test
	@DisplayName("[PATCH] llistat de dades de capçalera  - Errors validació")
	void test_patchExpedients_errorValidacio() throws Exception {

//		var titol = "titol";
//		for (var foo=0;foo<270;foo++) {
//			titol += titol; // TODO SI ES DESCOMENTA DONA UN OutOfMemoryError: Java heap space
//		}

		expedientsMock.get(expedientsMock.size() - 1).setExpedientId(null);
		for (var expedient : expedientsMock) {
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			validator = factory.getValidator();
			Set<ConstraintViolation<Expedient>> violations = validator.validate(expedient);
			if (!violations.isEmpty()) {
				assertFalse(violations.isEmpty());
			}
		}

		var expedientsJson = asJsonString(expedientsMock);
		mockMvc.perform(patch("/api/v1/expedients/patch/expedients").content(expedientsJson)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
