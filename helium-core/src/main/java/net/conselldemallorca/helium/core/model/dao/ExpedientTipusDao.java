/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.hibernate.criterion.Restrictions;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.ExpressionEvaluator;
import org.jbpm.jpdl.el.VariableResolver;
import org.jbpm.jpdl.el.impl.ExpressionEvaluatorImpl;
import org.springframework.stereotype.Component;

/**
 * Dao pels objectes tipus d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientTipusDao extends HibernateGenericDao<ExpedientTipus, Long> {

	public ExpedientTipusDao() {
		super(ExpedientTipus.class);
	}

	public List<ExpedientTipus> findAmbEntorn(Long entornId) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId));
	}

	public List<ExpedientTipus> findAmbSistraTramitCodi(String tramitCodi) {
		return findByCriteria(
				Restrictions.eq("sistraTramitCodi", tramitCodi));
	}

	public ExpedientTipus findAmbEntornICodi(
			Long entornId,
			String codi) {
		List<ExpedientTipus> tipus = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("codi", codi));
		if (tipus.size() > 0)
			return tipus.get(0);
		return null;
	}

	public String getNumeroExpedientActual(
			Long expedientTipusId,
			long increment) {
		ExpedientTipus expedientTipus = getById(expedientTipusId, false);
		long seq = expedientTipus.getSequencia();
		return getNumeroExpedientExpressio(
				expedientTipus,
				expedientTipus.getExpressioNumero(),
				seq + increment,
				expedientTipus.isReiniciarCadaAny());
	}
	
	public String getNumeroExpedientDefaultActual(
			Long expedientTipusId,
			long increment) {
		ExpedientTipus expedientTipus = getById(expedientTipusId, false);
		long seq = expedientTipus.getSequenciaDefault();
		return getNumeroExpedientExpressio(
				expedientTipus,
				getNumexpExpression(),
				seq + increment,
				true);
	}



	private String getNumeroExpedientExpressio(
			ExpedientTipus expedientTipus,
			String expressio,
			long seq, 
			boolean reiniciarCadaAny) {
		if (expressio != null) {
			try {
				final Map<String, Object> context = new HashMap<String, Object>();
				context.put("entorn_cod", expedientTipus.getEntorn().getCodi());
				context.put("tipexp_cod", expedientTipus.getCodi());
				int any = Calendar.getInstance().get(Calendar.YEAR);
				context.put("any", any);
				if (any != 0 && any != expedientTipus.getAnyActual() && reiniciarCadaAny)
					seq = 1;
				context.put("seq", seq);
				ExpressionEvaluator evaluator = new ExpressionEvaluatorImpl();
				String resultat = (String)evaluator.evaluate(
						expressio,
						String.class,
						new VariableResolver() {
							public Object resolveVariable(String name)
									throws ELException {
								return context.get(name);
							}
						},
						null);
				return resultat;
			} catch (Exception ex) {
				logger.error("Error evaluant l'expressió per calcular el número d'expedient", ex);
				return "#invalid expression#";
			}
		} else {
			return new Long(expedientTipus.getSequencia()).toString();
		}
	}

	private String getNumexpExpression() {
		return GlobalProperties.getInstance().getProperty("app.numexp.expression");
	}

}
