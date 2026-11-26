package es.caib.helium.flowable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.HeliumBpmnHandler;
import es.caib.helium.logic.intf.bpmn.MotorHeliumService;

/**
 * {@link MotorHeliumService} simulat per als tests del bpmn: retorna la {@link HeliumApi} indicada i resol les classes
 * dels handlers al classpath dels tests. Les interfícies dels handlers predefinits de {@code helium-disseny} es resolen
 * a implementacions de prova (a Helium les implementa helium-service).
 */
final class MotorHeliumProva {

	private static final Map<String, Class<? extends HeliumBpmnHandler>> PREDEFINITS = Map.of(
		"es.caib.helium.disseny.handler.ExpedientComentariModificarHandler", ExpedientComentariModificarHandlerProva.class,
		"es.caib.helium.disseny.handler.ExpedientAturarHandler", ExpedientAturarHandlerProva.class);

	private MotorHeliumProva() {
	}

	static MotorHeliumService crear(HeliumApi heliumApi) {
		MotorHeliumService motor = mock(MotorHeliumService.class);
		configurar(motor, heliumApi);
		return motor;
	}

	/** Configura (o torna a configurar, després d'un reset) el servei simulat. */
	static void configurar(MotorHeliumService motor, HeliumApi heliumApi) {
		when(motor.getHeliumApi(anyString())).thenReturn(heliumApi);
		when(motor.resoldreClasseHandler(anyString(), any(), any())).thenAnswer(i -> resoldre(i.getArgument(0)));
	}

	private static Class<? extends HeliumBpmnHandler> resoldre(String classe) {
		Class<? extends HeliumBpmnHandler> predefinit = PREDEFINITS.get(classe);
		if (predefinit != null) {
			return predefinit;
		}
		try {
			return Class.forName(classe).asSubclass(HeliumBpmnHandler.class);
		} catch (ClassNotFoundException ex) {
			throw new HeliumHandlerException("No s'ha trobat la classe del handler " + classe);
		}
	}

}
