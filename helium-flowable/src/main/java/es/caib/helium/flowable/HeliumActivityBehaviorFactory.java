package es.caib.helium.flowable;

import java.util.function.Supplier;

import org.flowable.bpmn.model.CallActivity;
import org.flowable.engine.impl.bpmn.behavior.CallActivityBehavior;
import org.flowable.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;

import es.caib.helium.logic.intf.bpmn.MotorHeliumService;

/**
 * Fàbrica dels comportaments de les activitats: la de Flowable, excepte la crida a subprocés, que també passa dades
 * de l'expedient entre el procés i el subprocés (veure {@link HeliumCallActivityBehavior}).
 */
public class HeliumActivityBehaviorFactory extends DefaultActivityBehaviorFactory {

	private final Supplier<MotorHeliumService> motorHeliumService;

	public HeliumActivityBehaviorFactory(Supplier<MotorHeliumService> motorHeliumService) {
		this.motorHeliumService = motorHeliumService;
	}

	@Override
	public CallActivityBehavior createCallActivityBehavior(CallActivity callActivity) {
		return new HeliumCallActivityBehavior(callActivity, motorHeliumService);
	}

}
