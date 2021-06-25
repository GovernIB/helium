package es.caib.helium.monitor.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.jms.domini.Parametre;
import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.enums.TipusAccio;
import es.caib.helium.jms.events.IntegracioEvent;
import es.caib.helium.monitor.domini.Consulta;
import es.caib.helium.monitor.domini.PagedList;
import es.caib.helium.monitor.service.BddService;
import es.caib.helium.monitor.service.CuaListener;


@WebMvcTest(value = MonitorController.class, excludeAutoConfiguration = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
		org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class })
public class MonitorControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@InjectMocks
	private MonitorController monitorController;
	
	@MockBean
	private BddService bddService;
	@MockBean
	private CuaListener cuaListener;
	private PagedList<IntegracioEvent> pagedList;
	private Consulta consultaMock;
	private IntegracioEvent eventMock;
	private List<IntegracioEvent> pagina;
	private List<IntegracioEvent> eventsMock;
	private static Validator validator;
	private List<Parametre> parametresMock;
	
	private static CodiIntegracio accioCodi = CodiIntegracio.ARXIU;
	
	@BeforeAll
	public static void setup() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@BeforeEach
	public void setUp() {
		
		pagina = new ArrayList<>();
		
		consultaMock = new Consulta();
		consultaMock.setCodi(accioCodi);
		consultaMock.setEntornId(1l);
		consultaMock.setTipus(TipusAccio.ENVIAMENT);
		consultaMock.setPage(0);
		consultaMock.setSize(10);
		
		eventMock = IntegracioEvent.builder().codi(CodiIntegracio.ARXIU)
				.entornId(1l).tipus(TipusAccio.ENVIAMENT).estat(EstatAccio.OK)
				.descripcio("descripcio mock").build();
		
		pagina.add(eventMock);
		var pageable = PageRequest.of(consultaMock.getPage(), consultaMock.getSize());
		pagedList = new PagedList<IntegracioEvent>(pagina, pageable, pagina.size());
		
		eventsMock = new ArrayList<>();
		for (var foo = 0; foo < 1000; foo++) {
			var event = IntegracioEvent.builder().id(foo + "").codi(accioCodi)
					.entornId(1l).tipus(TipusAccio.ENVIAMENT).build();
			eventsMock.add(event);
		}
	}
	
	@Test
	@DisplayName("[GET] findByFiltresPaginat - success")
	public void test_findByFiltresPaginat_success() throws Exception {

		given(bddService.findByFiltresPaginat(any(Consulta.class))).willReturn(pagedList);

		var consultaJson = asJsonString(consultaMock);
		mockMvc.perform(get("/api/v1/monitor/events/paginats").content(consultaJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).param("entornId", "1")
				.param("codi", "ARXIU").param("page", "0").param("size", "10")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.content", hasSize(1))).andExpect(jsonPath("$.content[0]").value(eventMock));
	}

	@Test
	@DisplayName("[GET] findByFiltresPaginat_error - Error paràmetres incorrectes")
	public void test_findByFiltresPaginat_error() throws Exception {

		var consultaJson = asJsonString(consultaMock);
		mockMvc.perform(get("/api/v1/monitor/events/paginats").content(consultaJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}
	
	// -------------------------

	@Test
	@DisplayName("[GET] findByFiltres - success")
	public void test_findByFiltres_success() throws Exception {
		
		given(bddService.findByFiltres(any(Consulta.class))).willReturn(eventsMock);
		
		var consultaJson = asJsonString(consultaMock);
		mockMvc.perform(get("/api/v1/monitor/events").content(consultaJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).param("entornId", "1")
				.param("codi", "ARXIU")).andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$").exists())
		.andExpect(jsonPath("$", hasSize(1000))).andExpect(jsonPath("$[0].entornId").value(1));
	}
	
	@Test
	@DisplayName("[GET] findByFiltres_error - Error paràmetres incorrectes")
	public void test_findByFiltres_error() throws Exception {
		
		var consultaJson = asJsonString(consultaMock);
		mockMvc.perform(get("/api/v1/monitor/events").content(consultaJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isInternalServerError());
	}

	// -------------------------
	
	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
