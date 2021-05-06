package es.caib.helium.dada.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.dada.domain.Dada;
import es.caib.helium.dada.domain.Expedient;
import es.caib.helium.dada.service.ExpedientService;
import es.caib.helium.enums.Tipus;

@WebMvcTest(value = ExpedientController.class, excludeAutoConfiguration = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
		org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class })
public class DadaExpedientControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@InjectMocks
	private ExpedientController expedientController;

	@MockBean
	private ExpedientService expedientService;

	private Validator validator;
	private Dada dadaMock;
	private List<Dada> dadesMock;
	private final int nDades = 10;
	private Expedient expedientMock;
	private final String cod = "codi";
	private final String codi = "codi0";
	private final Long unLong = 1l;

	@BeforeAll
	public void setup() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@BeforeEach
	public void setUp() {

		expedientMock = new Expedient();
		expedientMock.setExpedientId(unLong);
		expedientMock.setEntornId(unLong);
		expedientMock.setTipusId(unLong);
		expedientMock.setProcesPrincipalId(unLong);
		var dataInici = new Date();
		expedientMock.setDataInici(dataInici);

		dadesMock = new ArrayList<>();
		for (var foo = 0; foo < nDades; foo++) {

			var dada = new Dada();
			dada.setExpedientId(unLong);
			dada.setCodi(cod + foo);
			dada.setProcesId(unLong);
			dada.setTipus(Tipus.String);
			if (foo == 0) {
				dadaMock = dada;
			}

			dadesMock.add(dadaMock);
		}
	}

	@Test
	@DisplayName("[GET] llista de dades del expedient")
	public void test_getDades_success() throws Exception {

		given(expedientService.getDades(anyLong())).willReturn(dadesMock);
		given(expedientService.findByExpedientId(anyLong())).willReturn(expedientMock);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/dades", unLong)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$", hasSize(nDades))).andExpect(jsonPath("$[0].codi").value(codi));
	}

	@Test
	@DisplayName("[GET] llista de dades del expedient - Error no content")
	public void test_getDades_noContent() throws Exception {

		given(expedientService.findByExpedientId(anyLong())).willReturn(expedientMock);
		given(expedientService.getDades(anyLong())).willReturn(new ArrayList<>());

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/dades", unLong)).andExpect(status().isNoContent())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	@DisplayName("[GET] llista de dades del expedient - Error no existeix expedient")
	public void test_getDades_notFound() throws Exception {

		given(expedientService.findByExpedientId(anyLong())).willReturn(null);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/dades", unLong)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$").doesNotHaveJsonPath());
	}

	@Test
	@DisplayName("[GET] llista de dades del expedient - Error bad request")
	public void test_getDades_badRequest() throws Exception {

		given(expedientService.getDades(anyLong())).willReturn(null);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/dades", "foo")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$").doesNotHaveJsonPath());
	}

	// -----------------------

	@Test
	@DisplayName("[GET] dada del expedient segons codi")
	public void test_getDadaByCodi() throws Exception {

		given(expedientService.getDadaByCodi(anyLong(), nullable(String.class))).willReturn(dadesMock.get(0));
		given(expedientService.findByExpedientId(anyLong())).willReturn(expedientMock);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/dades/{codi}", unLong, codi)).andExpect(status().isOk())
				.andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").exists()).andExpect(jsonPath("$.codi").value(codi));
	}

	@Test
	@DisplayName("[GET] dada del expedient segons codi - Error no existeix expedient")
	public void test_getDadaByCodi_notFound() throws Exception {

		given(expedientService.findByExpedientId(anyLong())).willReturn(null);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/dades/{codi}", unLong, codi))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("[GET] dada del expedient segons codi - Error no content")
	public void test_getDadaByCodi_noContent() throws Exception {

		given(expedientService.findByExpedientId(anyLong())).willReturn(expedientMock);
		given(expedientService.getDadaByCodi(anyLong(), nullable(String.class))).willReturn(null);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/dades/{codi}", unLong, codi))
				.andExpect(status().isNoContent()).andExpect(jsonPath("$").doesNotHaveJsonPath());
	}

	@Test
	@DisplayName("[GET] dada del expedient segons codi - Error bad request")
	public void test_getDadaByCodi_badRequest() throws Exception {

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/dades/{codi}", "foo", codi))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$").doesNotHaveJsonPath());
	}

	// ------------------------

	@Test
	@DisplayName("[GET] llista de dades del expedient segons procesId")
	public void test_getDadesByProces_success() throws Exception {

		given(expedientService.findByExpedientId(anyLong())).willReturn(expedientMock);
		given(expedientService.getDadesByProces(anyLong(), anyLong())).willReturn(dadesMock);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/proces/{procesId}/dades", unLong, unLong))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").exists()).andExpect(jsonPath("$", hasSize(nDades)))
				.andExpect(jsonPath("$[0].codi").value(codi));
	}

	@Test
	@DisplayName("[GET] llista de dades del expedient segons procesId - Error no content")
	public void test_getDadesByProces_noContent() throws Exception {

		given(expedientService.findByExpedientId(anyLong())).willReturn(expedientMock);
		given(expedientService.getDades(anyLong())).willReturn(new ArrayList<>());

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/proces/{procesId}/dades", unLong, unLong))
				.andExpect(status().isNoContent()).andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	@DisplayName("[GET] llista de dades del expedient segons procesId - Error no existeix expedient")
	public void test_getDadesByProces_notFound() throws Exception {

		given(expedientService.findByExpedientId(anyLong())).willReturn(null);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/proces/{proceId}/dades", unLong, unLong))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$").doesNotHaveJsonPath());
	}

	@Test
	@DisplayName("[GET] llista de dades del expedient segons procesId - Error bad request")
	public void test_getDadesByProces_badRequest() throws Exception {

		given(expedientService.getDades(anyLong())).willReturn(null);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/proces/{procesId}/dades", "foo", unLong))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$").doesNotHaveJsonPath());
	}

	// -----------------------

	@Test
	@DisplayName("[GET] llista de dades del expedient segons procesId i codi")
	public void test_getDadesByProcesAndCodi_success() throws Exception {

		given(expedientService.findByExpedientId(anyLong())).willReturn(expedientMock);
		given(expedientService.getDadaByProcesAndCodi(anyLong(), anyLong(), nullable(String.class)))
				.willReturn(dadaMock);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/proces/{procesId}/dades/{codi}", unLong, unLong, codi))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").exists()).andExpect(jsonPath("$.codi").value(codi));
	}

	@Test
	@DisplayName("[GET] llista de dades del expedient segons procesId i codi - Error no content")
	public void test_getDadaByProcesAndCodi_noContent() throws Exception {

		given(expedientService.findByExpedientId(anyLong())).willReturn(expedientMock);
		given(expedientService.getDadaByProcesAndCodi(anyLong(), anyLong(), nullable(String.class))).willReturn(null);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/proces/{procesId}/dades/{codi}", unLong, unLong, codi))
				.andExpect(status().isNoContent()).andExpect(jsonPath("$").doesNotHaveJsonPath());
	}

	@Test
	@DisplayName("[GET] llista de dades del expedient segons procesId i codi - Error no existeix expedient")
	public void test_getDadaByProcesAndCodi_notFound() throws Exception {

		given(expedientService.findByExpedientId(anyLong())).willReturn(null);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/proces/{procesId}/dades/{codi}", unLong, unLong, codi))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$").doesNotHaveJsonPath());
	}

	@Test
	@DisplayName("[GET] llista de dades del expedient segons procesId i codi - Error bad request")
	public void test_getDadaByProcesAndCodi_badRequest() throws Exception {

		given(expedientService.getDadaByProcesAndCodi(anyLong(), anyLong(), nullable(String.class))).willReturn(null);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/proces/{procesId}/dades/{codi}", "foo", "bar", codi))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$").doesNotHaveJsonPath());
	}

	// --------------------

	@Test
	@DisplayName("[POST] dades per l'expedientId")
	void test_postDadesByExpedientId_success() throws Exception {
		String dadesJson = asJsonString(dadesMock);
		given(expedientService.createDades(anyLong(), anyLong(), any(List.class))).willReturn(true);

		mockMvc.perform(post("/api/v1/expedients/{expedientId}/dades", unLong).content(dadesJson)
				.contentType(MediaType.APPLICATION_JSON).param("procesId", unLong + "")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
	}

	@Test
	@DisplayName("[POST] dades per l'expedientId - Errors validació")
	void test_postDadesByExpedientId_errorValidacio() throws Exception {

		dadesMock.get(dadesMock.size() - 1).setCodi(null);
		for (var dada : dadesMock) {
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			validator = factory.getValidator();
			Set<ConstraintViolation<Dada>> violations = validator.validate(dada);
			if (!violations.isEmpty()) {
				assertFalse(violations.isEmpty());
			}
		}

		var dadesJson = asJsonString(dadesMock);
		mockMvc.perform(post("/api/v1/expedients/{expedientId}/dades", unLong).content(dadesJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("[POST] dada per l'expedientId - Error conflict")
	void test_postDadesByExpedientId_conflict() throws Exception {
		String dadesJson = asJsonString(dadesMock);
		given(expedientService.createDades(anyLong(), anyLong(), any(List.class))).willReturn(false);

		mockMvc.perform(post("/api/v1/expedients/{expedientId}/dades", unLong).content(dadesJson)
				.contentType(MediaType.APPLICATION_JSON).param("procesId", unLong + "")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isConflict());
	}

	// --------------

	@Test
	@DisplayName("[PUT] dada per l'expedientId i codi")
	void test_putDadaByExpedientIdAndCodi_sucess() throws Exception {
		String dadaJson = asJsonString(dadaMock);
		given(expedientService.putDadaByExpedientIdAndCodi(anyLong(), nullable(String.class), any(Dada.class)))
				.willReturn(true);

		mockMvc.perform(put("/api/v1/expedients/{expedientId}/dades/{codi}", unLong, codi).content(dadaJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	@DisplayName("[PUT] dada per l'expedientId i codi - Error not found expedientId")
	void test_putDadaByExpedientIdAndCodi_notFound() throws Exception {
		String dadaJson = asJsonString(dadaMock);
		given(expedientService.putDadaByExpedientIdAndCodi(anyLong(), nullable(String.class), any(Dada.class)))
				.willReturn(false);

		mockMvc.perform(put("/api/v1/expedients/{expedientId}/dades/{codi}", 100l, codi + "_").content(dadaJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("[PUT] dada per l'expedientId i codi - Error de validació")
	void test_putDadaByExpedientIdAndCodi_errorValidacio() throws Exception {
		dadaMock = new Dada();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		Set<ConstraintViolation<Dada>> violations = validator.validate(dadaMock);
		assertFalse(violations.isEmpty());

		var dadaJson = asJsonString(dadaMock);
		mockMvc.perform(put("/api/v1/expedients/{expedientId}/dades/{codi}", unLong, codi + "_").content(dadaJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// ---------------------------

	@Test
	@DisplayName("[DELETE] dada de l'expedientId amb codi")
	void test_deleteDadaByExpedientIdAndCodi_success() throws Exception {

		dadaMock = new Dada();
		dadaMock.setExpedientId(1l);
		given(expedientService.deleteDadaByExpedientIdAndCodi(anyLong(), nullable(String.class))).willReturn(true);

		mockMvc.perform(delete("/api/v1/expedients/{expedientId}/dades/{codi}", unLong, codi))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("[DELETE] dada de l'expedientId amb codi - Error no trobada")
	void test_deleteDadaByExpedientIdAndCodi_notFound() throws Exception {

		given(expedientService.deleteDadaByExpedientIdAndCodi(anyLong(), nullable(String.class))).willReturn(false);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/dades/{codi}", unLong, codi))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$").doesNotHaveJsonPath());
	}

	// --------------------

	@Test
	@DisplayName("[POST] dada per l'expedientId")
	void test_postDadesByExpedientIdProcesId_success() throws Exception {
		String dadesJson = asJsonString(dadesMock);
		mockMvc.perform(post("/api/v1/expedients/{expedientId}/proces/{procesId}/dades", unLong, unLong)
				.content(dadesJson).contentType(MediaType.APPLICATION_JSON).param("procesId", unLong + "")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
	}

	@Test
	@DisplayName("[POST]dada per l'expedientId - Errors validació")
	void test_crearExpedient_errorValidacio() throws Exception {
		dadaMock = new Dada();
		var codi = "";
		for (var foo = 0; foo < 270; foo++) {
			codi += foo + "";
		}
		dadesMock = new ArrayList<>();
		dadesMock.add(dadaMock);
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		Set<ConstraintViolation<Dada>> violations = validator.validate(dadaMock);
		assertFalse(violations.isEmpty());
		var dadesJson = asJsonString(dadesMock);
		mockMvc.perform(post("/api/v1/expedients/{expedientId}/proces/{procesId}/dades", unLong, unLong)
				.content(dadesJson).contentType(MediaType.APPLICATION_JSON).param("procesId", unLong + "")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	// --------------

	@Test
	@DisplayName("[PUT] dada per l'expedientId, procesId i codi")
	void test_putDadaByExpedientIdProcesIdAndCodi_sucess() throws Exception {
		String dadaJson = asJsonString(dadaMock);
		given(expedientService.putDadaByExpedientIdProcesIdAndCodi(anyLong(), anyLong(), nullable(String.class),
				any(Dada.class))).willReturn(true);

		mockMvc.perform(put("/api/v1/expedients/{expedientId}/proces/{procesId}/dades/{codi}", unLong, unLong, codi)
				.content(dadaJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("[PUT] dada per l'expedientId, procesId i codi - Error not found expedientId")
	void test_putDadaByExpedientIdProcesIdAndCodi_notFound() throws Exception {
		String dadaJson = asJsonString(dadaMock);
		given(expedientService.putDadaByExpedientIdProcesIdAndCodi(anyLong(), anyLong(), nullable(String.class),
				any(Dada.class))).willReturn(false);

		mockMvc.perform(put("/api/v1/expedients/{expedientId}/proces/{procesId}/dades/{codi}", 100l, unLong, codi + "_")
				.content(dadaJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("[PUT] dada per l'expedientId, procesId i codi - Error de validació")
	void test_putDadaByExpedientIdProcesIdAndCodi_errorValidacio() throws Exception {

		dadaMock = new Dada();
		var dadaJson = asJsonString(dadaMock);
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		Set<ConstraintViolation<Dada>> violations = validator.validate(dadaMock);
		assertFalse(violations.isEmpty());

		mockMvc.perform(put("/api/v1/expedients/{expedientId}/proces/{procesId}/dades/{codi}", unLong, unLong, codi)
				.content(dadaJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// ---------------------------

	@Test
	@DisplayName("[DELETE] dada de l'expedientId amb procesId i codi")
	void test_deleteDadaByExpedientIdProcesIdAndCodi_success() throws Exception {

		dadaMock = new Dada();
		dadaMock.setExpedientId(1l);
		given(expedientService.deleteDadaByExpedientIdAndProcesIdAndCodi(anyLong(), anyLong(), nullable(String.class)))
				.willReturn(true);

		mockMvc.perform(delete("/api/v1/expedients/{expedientId}/proces/{procesId}/dades/{codi}", unLong, unLong, codi))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("[DELETE] dada de l'expedientId amb procesId i codi - Error expedient no trobat")
	void test_deleteDadaByExpedientIdProcesIdAndCodi_notFound() throws Exception {

		given(expedientService.deleteDadaByExpedientIdAndProcesIdAndCodi(anyLong(), anyLong(), nullable(String.class)))
				.willReturn(false);

		mockMvc.perform(get("/api/v1/expedients/{expedientId}/proces/{procesId}/dades/{codi}", unLong, unLong, codi))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$").doesNotHaveJsonPath());
	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
