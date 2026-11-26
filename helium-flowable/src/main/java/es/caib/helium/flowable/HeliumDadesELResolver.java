package es.caib.helium.flowable;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.javax.el.ELContext;
import org.flowable.common.engine.impl.javax.el.ELResolver;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.task.api.TaskInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * Fa que les expressions que avalua Flowable ({@code ${nomDada}}) vegin les dades de l'expedient.
 *
 * <p>A Helium les dades de l'expedient no són variables del motor: es desen a {@code ExpedientDades}
 * per instància de procés (veure {@code ExpedientDadaHelper}). Si la instància de procés de l'execució té una
 * dada amb el nom de l'identificador, en retorna el valor; si no, deixa que el resolguin els altres resolutors.</p>
 *
 * <p>Ordre de resolució (el fixa Flowable): primer les variables del motor, després les dades de l'expedient
 * (aquest resolutor) i després els beans. A Helium les variables del motor són internes, així que en la
 * pràctica els noms de les dades de l'expedient no hi coincideixen.</p>
 *
 * <p>Afecta totes les expressions: paràmetres dels handlers, condicions dels fluxos, tasques d'expressió,
 * assignacions, etc. Només resol identificadors de primer nivell i només lectura: les dades s'escriuen amb
 * {@code HeliumApi.setVariable}.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class HeliumDadesELResolver extends ELResolver {

	/** Dades de la instància de procés indicada (nom → valor). */
	private final Function<String, Map<String, Object>> dadesProces;

	public HeliumDadesELResolver(Function<String, Map<String, Object>> dadesProces) {
		this.dadesProces = dadesProces;
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property) {
		if (base != null || property == null) {
			return null;
		}
		Map<String, Object> dades = dades(context);
		String nom = property.toString();
		if (dades != null && dades.containsKey(nom)) {
			context.setPropertyResolved(true);
			return dades.get(nom);
		}
		return null;
	}

	@Override
	public Class<?> getType(ELContext context, Object base, Object property) {
		if (base != null || property == null) {
			return null;
		}
		Map<String, Object> dades = dades(context);
		String nom = property.toString();
		if (dades != null && dades.containsKey(nom)) {
			context.setPropertyResolved(true);
			Object valor = dades.get(nom);
			return valor != null ? valor.getClass() : Object.class;
		}
		return null;
	}

	@Override
	public void setValue(ELContext context, Object base, Object property, Object value) {
		// Només lectura: les dades s'escriuen amb HeliumApi.setVariable
	}

	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) {
		return true;
	}

	@Override
	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		return base == null ? Object.class : null;
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}

	private Map<String, Object> dades(ELContext context) {
		String processInstanceId = processInstanceId(context.getContext(VariableContainer.class));
		if (processInstanceId == null) {
			return null;
		}
		try {
			return dadesProces.apply(processInstanceId);
		} catch (RuntimeException ex) {
			// P. ex. un procés que no és d'un expedient: es deixa resoldre a Flowable
			log.debug("No s'han pogut obtenir les dades de Helium de la instància de procés {}", processInstanceId, ex);
			return null;
		}
	}

	private static String processInstanceId(Object contenidor) {
		if (contenidor instanceof DelegateExecution) {
			return ((DelegateExecution) contenidor).getProcessInstanceId();
		}
		if (contenidor instanceof TaskInfo) {
			return ((TaskInfo) contenidor).getProcessInstanceId();
		}
		return null;
	}

}
