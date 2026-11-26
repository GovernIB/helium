package es.caib.helium.flowable;

import java.util.LinkedHashMap;
import java.util.Map;

import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.common.engine.impl.javax.el.PropertyNotFoundException;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.BpmnException;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.HandlerDefinicio;
import es.caib.helium.bpmn.handler.HandlerParametres;
import es.caib.helium.bpmn.handler.HeliumBpmnHandler;
import es.caib.helium.bpmn.handler.ParametresVar;
import es.caib.helium.logic.intf.bpmn.ExecucioHandler;
import es.caib.helium.logic.intf.bpmn.MotorHeliumService;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de Flowable per executar els handlers de Helium.
 *
 * <p>És l'única peça que coneix tant Flowable com els handlers: llegeix la definició
 * {@code <helium:handler>} de la service task que s'està executant, avalua les expressions dels
 * paràmetres amb el gestor d'expressions de Flowable, crea una instància nova del handler amb
 * {@link MotorHeliumService#resoldreClasseHandler} i l'executa amb la {@link HeliumApi}.</p>
 *
 * <p>Els antics paràmetres {@code varXXX} de les definicions ja desplegades es tradueixen a expressions
 * ({@link ParametresVar}).</p>
 *
 * <p>{@link HeliumServiceTaskXMLConverter} fa que les service tasks amb {@code <helium:handler>}
 * apuntin a aquest bean ({@value #DELEGATE_EXPRESSION}).</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component(HeliumHandlerJavaDelegate.BEAN_NAME)
public class HeliumHandlerJavaDelegate implements JavaDelegate {

	public static final String BEAN_NAME = "heliumHandlerJavaDelegate";
	public static final String DELEGATE_EXPRESSION = "${" + BEAN_NAME + "}";

	private final MotorHeliumService motorHeliumService;

	@Autowired
	public HeliumHandlerJavaDelegate(@Lazy MotorHeliumService motorHeliumService) {
		this.motorHeliumService = motorHeliumService;
	}

	@Override
	public void execute(DelegateExecution execution) {
		String expressio = HeliumServiceTaskXMLConverter.expressioHelium(execution.getCurrentFlowElement());
		if (expressio != null) {
			executarExpressio(expressio, execution);
			return;
		}
		HandlerDefinicio definicio = FlowableHeliumBpmn.llegirHandler(execution.getCurrentFlowElement());
		if (definicio == null) {
			throw new FlowableException(
				"La tasca " + execution.getCurrentActivityId() + " no té cap handler de Helium definit");
		}
		log.debug("Executant el handler {} a la tasca {} (processInstanceId={})",
			definicio.isPerExpressio() ? definicio.getExpressio() : definicio.getClasse(), execution.getCurrentActivityId(), execution.getProcessInstanceId());
		HeliumBpmnHandler handler;
		if (definicio.isPerExpressio()) {
			handler = handlerDeLExpressio(definicio, execution);
		} else {
			Class<? extends HeliumBpmnHandler> handlerClass = motorHeliumService.resoldreClasseHandler(
				definicio.getClasse(),
				execution.getProcessInstanceId(),
				execution.getProcessDefinitionId());
			handler = crearHandler(
				handlerClass,
				resoldreParametres(ParametresVar.migrar(definicio, handlerClass), execution));
		}
		HeliumApi heliumApi = motorHeliumService.getHeliumApi(execution.getProcessInstanceId());
		ExecucioHandler execucio = ExecucioHandler.obrir(execution.getId(), execution.getProcessInstanceId());
		try {
			handler.execute(heliumApi);
		} catch (BpmnException ex) {
			throw new HeliumHandlerException(
				"Error executant el handler " + (definicio.isPerExpressio() ? definicio.getExpressio() : definicio.getClasse()) +
					" a la tasca " + execution.getCurrentActivityId(),
				ex);
		} finally {
			execucio.tancar();
		}
	}

	/**
	 * Service task de tipus expressió: s'avalua l'expressió i, si la tasca té {@code resultVariableName}, el
	 * resultat es desa a les dades de l'expedient amb {@link HeliumApi#setVariable(String, Object)}.
	 */
	private void executarExpressio(String expressio, DelegateExecution execution) {
		log.debug("Avaluant l'expressió {} a la tasca {} (processInstanceId={})",
			expressio, execution.getCurrentActivityId(), execution.getProcessInstanceId());
		Object resultat = CommandContextUtil.getProcessEngineConfiguration().getExpressionManager()
			.createExpression(expressio)
			.getValue(execution);
		String resultVariableName = HeliumServiceTaskXMLConverter.resultVariableNameHelium(execution.getCurrentFlowElement());
		if (resultVariableName != null) {
			motorHeliumService.getHeliumApi(execution.getProcessInstanceId())
				.setVariable(resultVariableName, resultat);
		}
	}

	/**
	 * Handler obtingut avaluant una expressió (p. ex. un bean). Els paràmetres s'injecten a l'objecte retornat: si
	 * és una instància compartida (un singleton), els paràmetres també ho són entre execucions concurrents.
	 */
	private HeliumBpmnHandler handlerDeLExpressio(HandlerDefinicio definicio, DelegateExecution execution) {
		Object valor = CommandContextUtil.getProcessEngineConfiguration().getExpressionManager()
			.createExpression(definicio.getExpressio())
			.getValue(execution);
		if (!(valor instanceof HeliumBpmnHandler)) {
			throw new HeliumHandlerException(
				"L'expressió " + definicio.getExpressio() + " de la tasca " + execution.getCurrentActivityId() +
					" no retorna un " + HeliumBpmnHandler.class.getName() +
					" (retorna " + (valor != null ? valor.getClass().getName() : "null") + ")");
		}
		HeliumBpmnHandler handler = (HeliumBpmnHandler) valor;
		HandlerParametres.injectar(handler, resoldreParametres(ParametresVar.migrar(definicio, handler.getClass()), execution));
		return handler;
	}

	private Map<String, Object> resoldreParametres(HandlerDefinicio definicio, DelegateExecution execution) {
		Map<String, Object> valors = new LinkedHashMap<>();
		ExpressionManager expressionManager = null;
		for (HandlerDefinicio.Parametre parametre : definicio.getParametres()) {
			if (parametre.isExpressio()) {
				if (expressionManager == null) {
					expressionManager = CommandContextUtil.getProcessEngineConfiguration().getExpressionManager();
				}
				Expression expression = expressionManager.createExpression(parametre.getExpressio());
				Object valor = avaluar(expression, parametre, execution);
				valors.put(parametre.getNom(), valor != null ? valor : parametre.getValor());
			} else {
				valors.put(parametre.getNom(), parametre.getValor());
			}
		}
		return valors;
	}

	/**
	 * Avalua l'expressió d'un paràmetre. Si fa referència a una dada de l'expedient que no té valor (o a qualsevol
	 * identificador que no es pot resoldre), el paràmetre val null, com passava amb els antics paràmetres
	 * {@code varXXX}; si el paràmetre té un valor literal, s'utilitza com a valor per defecte.
	 */
	private static Object avaluar(Expression expression, HandlerDefinicio.Parametre parametre, DelegateExecution execution) {
		try {
			return expression.getValue(execution);
		} catch (FlowableException ex) {
			if (!esIdentificadorNoResolt(ex)) {
				throw ex;
			}
			log.debug("El paràmetre {} de la tasca {} fa referència a una dada sense valor ({}): s'utilitza {}",
				parametre.getNom(), execution.getCurrentActivityId(), parametre.getExpressio(),
				parametre.getValor() != null ? "el valor per defecte" : "null");
			return null;
		}
	}

	private static boolean esIdentificadorNoResolt(Throwable ex) {
		for (Throwable t = ex; t != null; t = t.getCause()) {
			if (t instanceof PropertyNotFoundException) {
				return true;
			}
		}
		return false;
	}

	/** Crea una instància nova del handler (mai compartida entre execucions) i li injecta els paràmetres. */
	private static HeliumBpmnHandler crearHandler(Class<? extends HeliumBpmnHandler> handlerClass, Map<String, ?> parametres) {
		HeliumBpmnHandler handler;
		try {
			handler = handlerClass.getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException ex) {
			throw new HeliumHandlerException("No s'ha pogut crear la instància del handler " + handlerClass.getName(), ex);
		}
		HandlerParametres.injectar(handler, parametres);
		return handler;
	}


}
