package es.caib.helium.client.tramitacio.v1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TramitacioClientTest {

	Calendar c = new GregorianCalendar();
	private final String ENTORN = "limit";
	private final String EXP_TIPUS_COD = "test_comu";
	private final String EXP_TITOL = "EXP-" + c.getTimeInMillis();
	private final String EXP_NUMERO = c.get(Calendar.YEAR) + "/" + c.get(Calendar.DAY_OF_YEAR);
	private final String USER = "helium";
	private final String PASSWORD = "*****";

	private static final String BASE_URL = "http://localhost:8180/helium2api/interna";

	private TramitacioClient clientUnderTest;

	@BeforeEach
	void setUp() {
		clientUnderTest = new TramitacioClient(BASE_URL, USER, PASSWORD);
	}

	@Test
	void iniciExpedient() throws Exception {
		TramitacioExpedient expedient = TramitacioExpedient.builder()
			.expedientTipus(EXP_TIPUS_COD)
			.numero(EXP_NUMERO)
			.titol(EXP_TITOL)
			.build();

		Long expedientId = clientUnderTest.iniciExpedient(ENTORN, expedient);

		assertNotNull(expedientId);
		findExpedients();
		getExpedientInfo(expedientId);

		consultaTasquesPersonals();
		deleteExpedient(expedientId);
	}

	void findExpedients() throws Exception {
		List<TramitacioExpedientInfo> result = clientUnderTest.findExpedients(
			ENTORN,
			EXP_TIPUS_COD,
			EXP_TITOL,
			EXP_NUMERO,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			0,
			10);

		assertEquals(1, result.size());
		assertEquals(EXP_NUMERO, result.get(0).getNumero());
	}

	void getExpedientInfo(Long expedientId) throws Exception {
		TramitacioExpedientInfo result = clientUnderTest.getExpedientInfo(ENTORN, expedientId);
		assertEquals(EXP_NUMERO, result.getNumero());
		List<TramitacioTasca> tasques = clientUnderTest.consultaTasques(ENTORN, expedientId, false, false);
		assertFalse(tasques.isEmpty(), "No s'han trobat tasques");

		TramitacioTasca tasca = tasques.stream()
									.filter(t -> !t.isCompleted())
									.findFirst()
									.orElse(null);

		String tascaId = tasca.getId();
		String sortida = tasca.getTransicionsSortida()
							.stream()
							.filter(o -> o.toUpperCase().startsWith("FI"))
							.findFirst()
							.orElse(null);

		clientUnderTest.agafaTasca(ENTORN, expedientId, tascaId);
		List<TramitacioTasca> tasquesPersonals = clientUnderTest.consultaTasques(ENTORN, expedientId, true, false);
		assertFalse(tasquesPersonals.isEmpty(), "No s'han trobat tasques personals");

		clientUnderTest.alliberaTasca(ENTORN, expedientId, tascaId);
		clientUnderTest.agafaTasca(ENTORN, expedientId, tascaId);
		guardarFormulariTasca(expedientId, tascaId);

		clientUnderTest.completarTasca(ENTORN, expedientId, tascaId, sortida);
		dadesProces(expedientId, result.getProcessInstanceId());
	}

	void deleteExpedient(Long expedientId) throws Exception {
		clientUnderTest.deleteExpedient(ENTORN, expedientId);
	}

	void dadesProces(Long expedientId, String processInstanceId) throws Exception {
		final String CAMP_CODI = "codi-1";
		TramitacioCodiValor dada = TramitacioCodiValor.builder().codi(CAMP_CODI).valor("Valor").build();
		clientUnderTest.guardarDadaProces(ENTORN, expedientId, processInstanceId, dada);
		List<TramitacioCamp> dades = clientUnderTest.consultaDadesProces(ENTORN, expedientId, processInstanceId);

		TramitacioCamp dadaCreada = dades.stream()
									.filter(tc -> tc.getCodi().equals(CAMP_CODI))
									.findFirst()
									.orElse(null);
		assertNotNull(dadaCreada, "No s'ha recuperat la dada creada " + CAMP_CODI);
		clientUnderTest.esborrarDadesProces(ENTORN, expedientId, processInstanceId, CAMP_CODI);
	}

	void guardarFormulariTasca(Long expedientId, String tascaId) throws Exception {
		TramitacioCodiValor valor = TramitacioCodiValor.builder().codi("c").valor("v").build();
		clientUnderTest.guardarFormulariTasca(ENTORN, expedientId, tascaId, Collections.singletonList(valor));
	}

	void consultaTasquesPersonals() throws Exception {
		clientUnderTest.consultaTasquesPersonals(ENTORN);
	}
}
