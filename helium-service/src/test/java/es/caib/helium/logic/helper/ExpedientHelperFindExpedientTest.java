package es.caib.helium.logic.helper;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.caib.helium.bpmn.engine.WProcessInstance;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import es.caib.helium.persistence.common.ThreadLocalInfo;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.repository.ExpedientRepository;

/**
 * Tests de la cerca de l'expedient a partir d'una instància de procés (també de subprocessos i
 * d'expedients que s'estan iniciant).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExtendWith(MockitoExtension.class)
public class ExpedientHelperFindExpedientTest {

	private static final String ARREL = "pid-arrel";
	private static final String SUBPROCES = "pid-subproces";

	@Mock
	private ExpedientRepository expedientRepository;
	@Mock
	private WorkflowEngineApi workflowEngineApi;
	@InjectMocks
	private ExpedientHelper expedientHelper;

	@AfterEach
	void netejar() {
		ThreadLocalInfo.setExpedient(null);
	}

	@Test
	void trobaLExpedientDesatPerLaInstanciaArrelDelSubproces() {
		Expedient expedient = expedient(ARREL);
		when(expedientRepository.findByProcessInstanceId(SUBPROCES)).thenReturn(null);
		when(workflowEngineApi.findRootProcessInstance(SUBPROCES)).thenReturn(instancia(ARREL));
		when(expedientRepository.findByProcessInstanceId(ARREL)).thenReturn(expedient);
		assertSame(expedient, expedientHelper.findExpedientByProcessInstanceId(SUBPROCES));
	}

	@Test
	void trobaLExpedientQueSEstaIniciantDesDUnSubproces() {
		Expedient iniciant = expedient(ARREL);
		ThreadLocalInfo.setExpedient(iniciant);
		when(expedientRepository.findByProcessInstanceId(SUBPROCES)).thenReturn(null);
		when(workflowEngineApi.findRootProcessInstance(SUBPROCES)).thenReturn(instancia(ARREL));
		when(expedientRepository.findByProcessInstanceId(ARREL)).thenReturn(null);
		assertSame(iniciant, expedientHelper.findExpedientByProcessInstanceId(SUBPROCES));
	}

	@Test
	void trobaLExpedientQueSEstaIniciantDesDeLaInstanciaArrel() {
		Expedient iniciant = expedient(ARREL);
		ThreadLocalInfo.setExpedient(iniciant);
		when(expedientRepository.findByProcessInstanceId(ARREL)).thenReturn(null);
		when(workflowEngineApi.findRootProcessInstance(ARREL)).thenReturn(instancia(ARREL));
		assertSame(iniciant, expedientHelper.findExpedientByProcessInstanceId(ARREL));
	}

	@Test
	void noRetornaUnExpedientQueSEstaIniciantDUnAltreProces() {
		ThreadLocalInfo.setExpedient(expedient("pid-altre"));
		when(expedientRepository.findByProcessInstanceId(SUBPROCES)).thenReturn(null);
		when(workflowEngineApi.findRootProcessInstance(SUBPROCES)).thenReturn(instancia(ARREL));
		when(expedientRepository.findByProcessInstanceId(ARREL)).thenReturn(null);
		assertThrows(NoTrobatException.class, () -> expedientHelper.findExpedientByProcessInstanceId(SUBPROCES));
	}

	private static Expedient expedient(String processInstanceId) {
		Expedient expedient = new Expedient();
		expedient.setProcessInstanceId(processInstanceId);
		return expedient;
	}

	private static WProcessInstance instancia(String id) {
		WProcessInstance pi = new WProcessInstance();
		pi.setId(id);
		return pi;
	}

}
