/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaDefaultAny;
import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.hibernate.criterion.Restrictions;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.ExpressionEvaluator;
import org.jbpm.jpdl.el.VariableResolver;
import org.jbpm.jpdl.el.impl.ExpressionEvaluatorImpl;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes tipus d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class ExpedientTipusDao extends HibernateGenericDao<ExpedientTipus, Long> {

	public ExpedientTipusDao() {
		super(ExpedientTipus.class);
	}

	public List<ExpedientTipus> findAmbEntorn(Long entornId) {
		return findOrderedByCriteria(
				new String[] {"codi"},
				true,
				Restrictions.eq("entorn.id", entornId));
	}
	
	public List<ExpedientTipus> findAmbEntornOrdenat(Long entornId, String ordre) {
		return findOrderedByCriteria(
				new String[] {ordre},
				true,
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
			int any,
			long increment) {
		ExpedientTipus expedientTipus = getById(expedientTipusId, false);
		long seq = expedientTipus.getSequencia();
		return getNumeroExpedientExpressio(
				expedientTipus,
				expedientTipus.getExpressioNumero(),
				seq,
				increment,
				any,
				expedientTipus.isReiniciarCadaAny(),
				false);
	}
	
	public String getNumeroExpedientDefaultActual(
			Long expedientTipusId,
			int any,
			long increment) {
		ExpedientTipus expedientTipus = getById(expedientTipusId, false);
		long seq = expedientTipus.getSequenciaDefault();
		return getNumeroExpedientExpressio(
				expedientTipus,
				getNumexpExpression(),
				seq,
				increment,
				any,
				expedientTipus.isReiniciarCadaAny(), 
				true);
	}



	private String getNumeroExpedientExpressio(
			ExpedientTipus expedientTipus,
			String expressio,
			long seq,
			long increment,
			int any,
			boolean reiniciarCadaAny,
			boolean numeroDefault) {
		if (reiniciarCadaAny) {
			if (any != 0) {
				if (numeroDefault) {
					if (expedientTipus.getSequenciaDefaultAny().containsKey(any)) {
						seq = expedientTipus.getSequenciaDefaultAny().get(any).getSequenciaDefault() + increment;
					} else {
						// TODO: podriem comprovar, segons el format del número per defecte si hi ha expedients ja creats de 
						// l'any, i d'aquesta manera assignar com a número inicial el major utilitzat + 1
						SequenciaDefaultAny sda = new SequenciaDefaultAny(expedientTipus, any, 1L);
						expedientTipus.getSequenciaDefaultAny().put(any, sda);
						saveOrUpdate(expedientTipus);
						seq = 1;
					}
				} else {
					if (expedientTipus.getSequenciaAny().containsKey(any)) {
						seq = expedientTipus.getSequenciaAny().get(any).getSequencia() + increment;
					} else {
						SequenciaAny sa = new SequenciaAny(expedientTipus, any, 1L);
						expedientTipus.getSequenciaAny().put(any, sa);
						saveOrUpdate(expedientTipus);
						seq = 1;
					}
				}
			}
		} else {
			seq = seq + increment;
		}
		if (expressio != null) {
			try {
				final Map<String, Object> context = new HashMap<String, Object>();
				context.put("entorn_cod", expedientTipus.getEntorn().getCodi());
				context.put("tipexp_cod", expedientTipus.getCodi());
				context.put("any", any);
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
			return new Long(seq).toString();
		}
	}

	private String getNumexpExpression() {
		return GlobalProperties.getInstance().getProperty("app.numexp.expression");
	}

}
