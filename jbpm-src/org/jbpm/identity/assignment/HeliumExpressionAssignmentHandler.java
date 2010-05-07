/**
 * 
 */
package org.jbpm.identity.assignment;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.model.dao.DaoProxy;
import net.conselldemallorca.helium.model.hibernate.Area;
import net.conselldemallorca.helium.model.hibernate.AreaMembre;
import net.conselldemallorca.helium.model.hibernate.Carrec;
import net.conselldemallorca.helium.util.EntornActual;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.security.SecurityHelper;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;

/**
 * Implementa el següent llenguatge i resol expressions per assignar
 * actors a tasques. Està agafat del component d'identitat que du el
 * JBPM per defecte.
 * 
 * <pre>syntax : first-term --> next-term --> next-term --> ... --> next-term
 * 
 * first-term ::= previous |
 *                swimlane(swimlane-name) |
 *                variable(variable-name) |
 *                user(user-name) |
 *                group(group-name)
 * 
 * next-term ::= group(group-type) |
 *               member(role-name)
 * </pre> 
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class HeliumExpressionAssignmentHandler implements AssignmentHandler {

	private static final long serialVersionUID = 1L;

	protected String expression;

	public HeliumExpressionAssignmentHandler() {}

	/**
	 * @see AssignmentHandler#assign(Assignable, ExecutionContext)
	 */
	public void assign(Assignable assignable, ExecutionContext executionContext) {

		String expressio = getExpressio();
		log.debug("Expresió a analitzar: '" + expressio + "'");
		String processInstanceId = new Long(executionContext.getProcessInstance().getId()).toString();
		Long entornId = EntornActual.getEntornId();
		if (entornId == null)
			throw new RuntimeException("No s'ha trobat l'entorn per la instància de procés " + processInstanceId);
		TermTokenizer tokenizer = new TermTokenizer(expressio);
		Object entitat = null;
		if (tokenizer.hasMoreTerms()) {
			entitat = resolPrimerTerme(entornId, tokenizer.nextTerm().trim(), executionContext);
			while (tokenizer.hasMoreTerms() && entitat != null) {
				entitat = resolSeguentTerme(entornId, entitat, tokenizer.nextTerm().trim(), executionContext);
			}
		}
		if (entitat == null) {
			throw new RuntimeException("L'expressió d'assignació '" + expressio + "' no ha produit cap resultat");
		} else if (entitat instanceof Persona) {
			Persona p = (Persona)entitat;
			while(p.getRelleu() != null){
				p = getPersonaAmbCodi(p.getRelleu());
			}
			assignable.setActorId(p.getCodi());
		} else if (entitat instanceof Area) {
			Area a = (Area)entitat;
			List<Persona> persones = getPersonesAmbArea(entornId, a.getCodi());
			String[] actors = new String[persones.size()];
			int index = 0;
			for (Persona p: persones)
				actors[index++] = p.getCodi();
			assignable.setPooledActors(actors);
		}
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}



	private Object resolPrimerTerme(Long entornId, String terme, ExecutionContext executionContext) {
		log.debug("Analitzant primer terme: '" + terme + "'");
		Object entitat = null;
		if (terme.equalsIgnoreCase("previous")) {
			String userName = SecurityHelper.getAuthenticatedActorId();
			entitat = getPersonaAmbCodi(userName);
		} else if ((terme.startsWith("swimlane(")) && (terme.endsWith(")"))) {
			String swimlaneName = terme.substring(9, terme.length()-1).trim();
			String userName = getSwimlaneActorId(swimlaneName, executionContext);
			entitat = getPersonaAmbCodi(userName);
		} else if ((terme.startsWith("variable(")) && (terme.endsWith(")")) ) {
			String variableNom = terme.substring(9, terme.length() - 1).trim();
			Object valor = getVariable(variableNom, executionContext);
			if (valor == null) {
				throw new ExpressionAssignmentException("La variable '" + variableNom + "' és null");
			} else if (valor instanceof String) {
				entitat = getPersonaAmbCodi((String)valor);
				if (entitat == null)
					entitat = getAreaAmbCodi(entornId, (String)valor);
			} else {
				entitat = valor;
			}
		} else if ( (terme.startsWith("user(")) && (terme.endsWith(")")) ) {
			String usuariNom = terme.substring(5, terme.length()-1).trim();
			entitat = getPersonaAmbCodi(usuariNom);
		} else if ( (terme.startsWith("group(")) && (terme.endsWith(")")) ) {
			String grupNom = terme.substring(6, terme.length()-1).trim();
			entitat = getAreaAmbCodi(entornId, grupNom);
		} else {
			throw new ExpressionAssignmentException("No s'ha pogut interpretar el primer terme de l'expressió '" + getExpressio() + "'");
		}
		return entitat;
	}

	private Object resolSeguentTerme(Long entornId, Object entitat, String terme, ExecutionContext executionContext) {
		log.debug("Analitzant següent terme: '" + terme + "'");
		if ((terme.startsWith("member(")) && (terme.endsWith(")")) ) {
			String carrec = terme.substring(7, terme.length() - 1).trim();
			Area a = (Area)entitat;
			entitat = getPersonaAmbAreaICarrec(entornId, a.getCodi(), carrec);
			if (entitat == null)
				throw new ExpressionAssignmentException("No hi ha cap usuari amb el carrec '" + carrec + "' a dins l'àrea '" + a.getNom() + "'");
		} else {
			throw new ExpressionAssignmentException("No es pot interpretar el terme '" + terme + "' de la expressio '" + getExpressio() + "'");
		}
	    return entitat;
	}

	private Persona getPersonaAmbCodi(String codi) {
		return DaoProxy.getInstance().getPluginPersonaDao().findAmbCodiPlugin(codi);
	}

	private Area getAreaAmbCodi(Long entornId, String codi) {
		return DaoProxy.getInstance().getAreaDao().findAmbEntornICodi(entornId, codi);
	}

	private List<Persona> getPersonesAmbArea(Long entornId, String codi) {
		Area area = DaoProxy.getInstance().getAreaDao().findAmbEntornICodi(entornId, codi);
		List<Persona> persones = new ArrayList<Persona>();
		for (AreaMembre membre: area.getMembres()) {
			persones.add(getPersonaAmbCodi(membre.getCodi()));
		}
		if (personesAmbAreaRecursiu() && area.getFills().size() > 0) {
			for (Area fill: area.getFills())
				persones.addAll(getPersonesAmbArea(entornId, fill.getCodi()));
		}
		return persones;
	}

	private Persona getPersonaAmbAreaICarrec(Long entornId, String codiArea, String codiCarrec) {
		Area area = DaoProxy.getInstance().getAreaDao().findAmbEntornICodi(entornId, codiArea);
		Carrec carrec = DaoProxy.getInstance().getCarrecDao().findAmbAreaICodi(area.getId(), codiCarrec);
		if (carrec != null && carrec.getPersonaCodi() != null) {
			return getPersonaAmbCodi(carrec.getPersonaCodi());
		}
		return null;
	}

	private String getSwimlaneActorId(String swimlaneName, ExecutionContext executionContext) {
		SwimlaneInstance swimlaneInstance = executionContext.
				getTaskMgmtInstance().
				getSwimlaneInstance(swimlaneName);
		if (swimlaneInstance == null)
			throw new ExpressionAssignmentException("No s'ha trobat la instancia del swimlane '" + swimlaneName + "'");
		return swimlaneInstance.getActorId();
	}

	private Object getVariable(String variableName, ExecutionContext executionContext) {
		Token token = executionContext.getToken();
	    return executionContext.getContextInstance().getVariable(variableName, token);
	}



	private String getExpressio() {
		String expressio = expression;
		// lleva el tag <expression>...</expression>
		int indexInici = expressio.indexOf(">") + 1;
		int indexFi = expressio.lastIndexOf("<");
		if (indexInici != -1 && indexFi != -1)
			return expressio.substring(indexInici, indexFi);
		return expressio;
	}

	private boolean personesAmbAreaRecursiu() {
		String esRecursiu = GlobalProperties.getInstance().getProperty("app.jbpm.identity.recursiu");
		return "true".equalsIgnoreCase(esRecursiu);
	}

	private static final Log log = LogFactory.getLog(HeliumExpressionAssignmentHandler.class);

}
