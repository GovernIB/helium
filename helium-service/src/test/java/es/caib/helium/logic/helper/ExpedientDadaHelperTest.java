package es.caib.helium.logic.helper;

import com.google.common.collect.Lists;
import es.caib.helium.commons.dto.CampTipusDto;
import es.caib.helium.persistence.entity.*;
import es.caib.helium.persistence.repository.CampRepository;
import es.caib.helium.persistence.repository.ExpedientDadesRepository;
import es.caib.helium.persistence.repository.ExpedientRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class ExpedientDadaHelperTest {

	@Mock
	private ExpedientDadesRepository expedientDadesRepository;
	@Mock
	private CampRepository campRepository;
	@Mock
	private ExpedientRepository expedientRepository;
	@Mock
	private VariableHelper variableHelper;
	@Mock
	private JdbcTemplate jdbcTemplate;

	private ExpedientDadaHelper expedientDadaHelper;

	private AtomicReference<ExpedientDades> expedientDades;

	private final String CAMP_STRING = "varString";
	private final String CAMP_INTEGER = "varInteger";
	private final String CAMP_FLOAT = "varFloat";
	private final String CAMP_BOOLEAN = "varBoolean";
	private final String CAMP_TEXTAREA = "varTextarea";
	private final String CAMP_DATE = "varDate";
	private final String CAMP_PRICE = "varPrice";
	private final String CAMP_TERMINI = "varTermini";
	private final String CAMP_SELECCIO = "varSeleccio";
	private final String CAMP_SUGGEST = "varSuggest";
	private final String CAMP_REGISTRE = "varRegistre";
	private final String CAMP_MULTIPLE_INT = "varMultipleInt";
	private final String CAMP_MULTIPLE_STR = "varMultipleStr";
	private final String CAMP_NULL_STR = "varNullStr";

	private final Camp campString = new Camp(CAMP_STRING, CampTipusDto.STRING, "Var String");
	private final Camp campInteger = new Camp(CAMP_INTEGER, CampTipusDto.INTEGER, "Var Integer");
	private final Camp campFloat = new Camp(CAMP_FLOAT, CampTipusDto.FLOAT, "Var Float");
	private final Camp campBoolean = new Camp(CAMP_BOOLEAN, CampTipusDto.BOOLEAN, "Var Boolean");
	private final Camp campTextarea = new Camp(CAMP_TEXTAREA, CampTipusDto.TEXTAREA, "Var Textarea");
	private final Camp campDate = new Camp(CAMP_DATE, CampTipusDto.DATE, "Var Date");
	private final Camp campPrice = new Camp(CAMP_PRICE, CampTipusDto.PRICE, "Var Price");
	private final Camp campTermini = new Camp(CAMP_TERMINI, CampTipusDto.TERMINI, "Var Termini");
	private final Camp campSeleccio = new Camp(CAMP_SELECCIO, CampTipusDto.SELECCIO, "Var Selecció");
	private final Camp campSuggest = new Camp(CAMP_SUGGEST, CampTipusDto.SUGGEST, "Var Suggest");
	private final Camp campRegistre = new Camp(CAMP_REGISTRE, CampTipusDto.REGISTRE, "Var Registre");
	private final Camp campMultipleInt = new Camp(CAMP_MULTIPLE_INT, CampTipusDto.INTEGER, "Var Multiple integer");
	private final Camp campMultipleStr = new Camp(CAMP_MULTIPLE_STR, CampTipusDto.STRING, "Var Multiple string");
	private final Camp campNullStr = new Camp(CAMP_NULL_STR, CampTipusDto.STRING, "Var Null string");


	@BeforeEach
	public void setUp() {
		expedientDadaHelper =  new ExpedientDadaHelper(
			expedientDadesRepository,
			campRepository,
			expedientRepository,
			variableHelper,
			jdbcTemplate);

		initStubs();
	}

	private Expedient buildExpedient() {
		ExpedientTipus expedientTipus = new ExpedientTipus();

		expedientTipus.setId(1L);
		expedientTipus.setCodi("TIPUS_001");
		expedientTipus.setNom("Tipus d'Expedient de Prova");

		expedientTipus.setJbpmProcessDefinitionKey("process-definition-key");

		expedientTipus.setTeNumero(true);
		expedientTipus.setTeTitol(true);
		expedientTipus.setDemanaNumero(false);
		expedientTipus.setDemanaTitol(false);

		expedientTipus.setExpressioNumero("[Año]-[Numero]");
		expedientTipus.setSequencia(1L);
		expedientTipus.setSequenciaDef(1L);
		expedientTipus.setReiniciarCadaAny(false);

		expedientTipus.setAnyActual(java.time.Year.now().getValue());
		expedientTipus.setRestringirPerGrup(false);

		expedientTipus.setTramitacioMassiva(false);
		expedientTipus.setSeleccionarAny(false);
		expedientTipus.setAmbRetroaccio(false);
		expedientTipus.setReindexacioAsincrona(false);
		expedientTipus.setEnviarCorreuAnotacions(false);
		expedientTipus.setResponsableDefecteCodi("ADMIN");


		Expedient expedient = new Expedient();
		expedient.setTipus(expedientTipus);

		return expedient;
	}

	private void initStubs() {

		campMultipleInt.setMultiple(true);
		campMultipleStr.setMultiple(true);

		campRegistre.setRegistreMembres(Lists.newArrayList(
			new CampRegistre(
				campRegistre,
				campString,
				1),
			new CampRegistre(
				campRegistre,
				campInteger,
				2),
			new CampRegistre(
				campRegistre,
				campPrice,
				3)
		));

		lenient().doAnswer(invocation -> {
			String codi = invocation.getArgument(1).toString();
			switch (codi) {
				case CAMP_STRING:
					return campString;
				case CAMP_INTEGER:
					return campInteger;
				case CAMP_FLOAT:
					return campFloat;
				case CAMP_BOOLEAN:
					return campBoolean;
				case CAMP_TEXTAREA:
					return campTextarea;
				case CAMP_DATE:
					return campDate;
				case CAMP_PRICE:
					return campPrice;
				case CAMP_TERMINI:
					return campTermini;
				case CAMP_SELECCIO:
					return campSeleccio;
				case CAMP_SUGGEST:
					return campSuggest;
				case CAMP_REGISTRE:
					return campRegistre;
				case CAMP_MULTIPLE_INT:
					return campMultipleInt;
				case CAMP_MULTIPLE_STR:
					return campMultipleStr;
				default:
					return null;
			}
		}).when(campRepository).findByExpedientTipusAndCodi(any(), anyString(), anyBoolean());

		lenient().doAnswer(invocation -> {
			String codi = invocation.getArgument(1).toString();
			switch (codi) {
				case CAMP_STRING:
					return campString;
				case CAMP_INTEGER:
					return campInteger;
				case CAMP_FLOAT:
					return campFloat;
				case CAMP_BOOLEAN:
					return campBoolean;
				case CAMP_TEXTAREA:
					return campTextarea;
				case CAMP_DATE:
					return campDate;
				case CAMP_PRICE:
					return campPrice;
				case CAMP_TERMINI:
					return campTermini;
				case CAMP_SELECCIO:
					return campSeleccio;
				case CAMP_SUGGEST:
					return campSuggest;
				case CAMP_REGISTRE:
					return campRegistre;
				case CAMP_MULTIPLE_INT:
					return campMultipleInt;
				case CAMP_MULTIPLE_STR:
					return campMultipleStr;
				case CAMP_NULL_STR:
					return campNullStr;
				default:
					return null;
			}
		})
			.when(campRepository)
			.findByDefinicioProcesAndCodi(
				any(),
				any());

		lenient().doAnswer(invocation -> invocation.getArgument(1)).
			when(variableHelper)
			.getTextPerCamp(
				any(),
				any(),
				any(),
				any(),
				any());

		expedientDades = new AtomicReference<ExpedientDades>();

		when(expedientDadesRepository.save(any())).
			thenAnswer(invocation -> {
				ExpedientDades x = (ExpedientDades) invocation.getArgument(0);
				expedientDades.set(x);
				return x;
			});

		lenient().doAnswer(invocation -> expedientDades.get())
			.when(expedientDadesRepository).
			findByExpedientAndTaskId(any(), any());
		lenient().doAnswer(invocation -> expedientDades.get())
			.when(expedientDadesRepository)
			.findByExpedientAndProcessIdAndTaskIdIsNull(any(), any());
		lenient().doAnswer(invocation -> Lists.newArrayList(expedientDades.get()))
			.when(expedientDadesRepository)
			.findByExpedient(any());
		lenient().doAnswer(invocation -> expedientDades.get())
			.when(expedientDadesRepository)
			.findByExpedientAndPrincipal(any(), anyBoolean());
	}

	/**
	 * Crea dades d'expedient amb Camp
	 */
	@Test
	public void dadesAmbCamp() {

		log.info("Iniciant test d'insert de dades d'expedient amb Camp...");

		Expedient expedient = buildExpedient();

		final String processId = null;
		final String taskId = null;

		final String varString = "Texte de prova";
		final Integer varInteger = 1;
		final Double varFloat = 1.1423d;
		final Boolean varBoolean = true;
		final String varTextarea = "Texte llarg de prova per camp tipus textarea";
		final Date varDate = new Date();
		final BigDecimal varPrice = BigDecimal.valueOf(12.345d);
		final Termini varTermini = new Termini(new DefinicioProces(), "codi", "nom", 2, 4, 23, true);
		final String strTermini = varTermini.getAnys() + "/" + varTermini.getMesos() + "/" + varTermini.getDies();
		final String varSeleccio = "cod_seleccio";
		final String varSuggest = "cod_suggest";
		final Object[] varRegistre = new Object[]{
			new Object[]{"texte", 1, 3.4d},
			new Object[]{"texte2", 3, 7.2d}
		};

		final Integer[] varMultipleInt = new Integer[] {1, 2};
		final String[] varMultipleStr = new String[] {"1l", "2l", "atest"};

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_STRING, varString);
		Object dadaString = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_STRING);
		assertEquals(varString, dadaString);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_INTEGER, varInteger);
		Object dadaInteger = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_INTEGER);
		assertEquals(varInteger, dadaInteger);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_FLOAT, varFloat);
		Object dadaFloat = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_FLOAT);
		assertEquals(varFloat, dadaFloat);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_BOOLEAN, varBoolean);
		Object dadaBoolean = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_BOOLEAN);
		assertEquals(varBoolean, dadaBoolean);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_TEXTAREA, varTextarea);
		Object dadaTextarea = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_TEXTAREA);
		assertEquals(varTextarea, dadaTextarea);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_DATE, varDate);
		Object dadaDate = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_DATE);
		assertTrue(varDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(((Date) dadaDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()), "La data guardada i recuperada no coincideixen");

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_PRICE, varPrice);
		Object dadaPrice = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_PRICE);
		assertTrue(varPrice.compareTo((BigDecimal) dadaPrice) == 0, "El preu guardat i recuperat no coincideixen");

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_TERMINI, varTermini);
		Object dadaTermini = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_TERMINI);
		assertEquals(strTermini, dadaTermini);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_SELECCIO, varSeleccio);
		Object dadaSeleccio = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_SELECCIO);
		assertEquals(varSeleccio, dadaSeleccio);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_SUGGEST, varSuggest);
		Object dadaSuggest = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_SUGGEST);
		assertEquals(varSuggest, dadaSuggest);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_REGISTRE, varRegistre);
		Object dadaRegistre = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_REGISTRE);
		assertArrayEquals(varRegistre, (Object[]) dadaRegistre);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_MULTIPLE_INT, varMultipleInt);
		Object dadaMultipleInt = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_MULTIPLE_INT);
		assertArrayEquals(varMultipleInt, (Object[]) dadaMultipleInt);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_MULTIPLE_STR, varMultipleStr);
		Object dadaMultipleStr = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_MULTIPLE_STR);
		assertArrayEquals(varMultipleStr, (Object[]) dadaMultipleStr);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_NULL_STR, null);
		Object dadaNullStr = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_NULL_STR);
		assertNull(dadaNullStr);



		// Map<String, Object> dades = expedientDadaHelper.getDadesValors(expedient, processId, taskId);

		log.info("Finalitzat test d'insert de dades d'expedient amb Camp");
	}

	/**
	 * Crea dades d'expedient sense Camp
	 */
	@Test
	public void dadesSenseCamp() {

		log.info("Iniciant test d'insert de dades d'expedient sense Camp...");

		Expedient expedient = buildExpedient();

		final String processId = null;
		final String taskId = null;

		final String varString = "Texte de prova";
		final Integer varInteger = 1;
		final Double varFloat = 1.1423d;
		final Boolean varBoolean = true;
		final String varTextarea = "Texte llarg de prova per camp tipus textarea";
		final Date varDate = new Date();
		final BigDecimal varPrice = BigDecimal.valueOf(12.345d);
		final Termini varTermini = new Termini(new DefinicioProces(), "codi", "nom", 2, 4, 23, true);
		final String strTermini = varTermini.getAnys() + "/" + varTermini.getMesos() + "/" + varTermini.getDies();
		final String varSeleccio = "cod_seleccio";
		final String varSuggest = "cod_suggest";
		final Object[] varRegistre = new Object[]{
			new Object[]{"texte", 1, 3.4d},
			new Object[]{"texte2", 3, 7.2d}
		};

		final Integer[] varMultipleInt = new Integer[] {1, 2};
		final String[] varMultipleStr = new String[] {"1l", "2l", "atest"};

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_STRING + "_x", varString);
		Object dadaString = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_STRING + "_x");
		assertEquals(varString, dadaString);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_INTEGER + "_x", varInteger);
		Object dadaInteger = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_INTEGER + "_x");
		assertEquals(varInteger, dadaInteger);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_FLOAT + "_x", varFloat);
		Object dadaFloat = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_FLOAT + "_x");
		assertEquals(varFloat, dadaFloat);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_BOOLEAN + "_x", varBoolean);
		Object dadaBoolean = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_BOOLEAN + "_x");
		assertEquals(varBoolean, dadaBoolean);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_TEXTAREA + "_x", varTextarea);
		Object dadaTextarea = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_TEXTAREA + "_x");
		assertEquals(varTextarea, dadaTextarea);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_DATE + "_x", varDate);
		Object dadaDate = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_DATE + "_x");
		assertTrue(varDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(((Date) dadaDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()), "La data guardada i recuperada no coincideixen");

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_PRICE + "_x", varPrice);
		Object dadaPrice = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_PRICE + "_x");
		assertTrue(varPrice.compareTo((BigDecimal) dadaPrice) == 0, "El preu guardat i recuperat no coincideixen");

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_TERMINI + "_x", varTermini);
		Object dadaTermini = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_TERMINI + "_x");
		assertEquals(strTermini, dadaTermini);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_SELECCIO + "_x", varSeleccio);
		Object dadaSeleccio = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_SELECCIO + "_x");
		assertEquals(varSeleccio, dadaSeleccio);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_SUGGEST + "_x", varSuggest);
		Object dadaSuggest = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_SUGGEST + "_x");
		assertEquals(varSuggest, dadaSuggest);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_REGISTRE + "_x", varRegistre);
		Object dadaRegistre = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_REGISTRE + "_x");
		assertArrayEquals(varRegistre, (Object[]) dadaRegistre);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_MULTIPLE_INT + "_x", varMultipleInt);
		Object dadaMultipleInt = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_MULTIPLE_INT + "_x");
		assertArrayEquals(varMultipleInt, (Object[]) dadaMultipleInt);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_MULTIPLE_STR + "_x", varMultipleStr);
		Object dadaMultipleStr = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_MULTIPLE_STR + "_x");
		assertArrayEquals(varMultipleStr, (Object[]) dadaMultipleStr);

		expedientDadaHelper.setDada(expedient, processId, taskId, CAMP_NULL_STR + "_x", null);
		Object dadaNullString = expedientDadaHelper.getDada(expedient, processId, taskId, CAMP_NULL_STR + "_x");
		assertNull(dadaNullString);

		log.info("Finalitzat test d'insert de dades d'expedient sense Camp");
	}

}
