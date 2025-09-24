/**
 * 
 */
package org.jbpm.identity.assignment;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.AreaDto;
import net.conselldemallorca.helium.v3.core.api.dto.AreaMembreDto;
import net.conselldemallorca.helium.v3.core.api.dto.CarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
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
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumExpressionAssignmentHandler implements AssignmentHandler {

	private static final long serialVersionUID = 1L;

	protected String expression;
	protected Long entornId;

	public HeliumExpressionAssignmentHandler() {}

	/**
	 * @see AssignmentHandler#assign(Assignable, ExecutionContext)
	 */
	public void assign(Assignable assignable, ExecutionContext executionContext) {

		String expressio = getExpressio();
		logger.debug("Expresió a analitzar: '" + expressio + "'");
		String processInstanceId = new Long(executionContext.getProcessInstance().getId()).toString();
		if (entornId == null) {
			EntornDto entorn = Jbpm3HeliumBridge.getInstanceService().getEntornActual();
			if (entorn == null)
				throw new RuntimeException("No s'ha trobat l'entorn per la instància de procés " + processInstanceId);
			else 
				entornId = entorn.getId();
		}
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
		} else if (entitat instanceof PersonaDto) {
			PersonaDto p = (PersonaDto)entitat;
			while(p.getRelleu() != null){
				p = getPersonaAmbCodi(p.getRelleu());
			}
			assignable.setActorId(p.getCodi());
		} else if (entitat instanceof AreaDto) {
			AreaDto a = (AreaDto)entitat;
			List<PersonaDto> persones = getPersonesAmbArea(entornId, a.getCodi());
			String[] actors = new String[persones.size()];
			int index = 0;
			for (PersonaDto p: persones)
				actors[index++] = p.getCodi();
	    	if (executionContext.getProcessInstance().getExpedient().getTipus().isProcedimentComu()) {
	        	actorIds = Jbpm3HeliumBridge.getInstanceService().filtrarUsuarisAmbPermisComu(
	        			executionContext.getProcessInstance().getExpedient().getId(), 
	        			actorIds);
	    	}
			assignable.setPooledActors(actors);
		}
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	public void setEntornId(Long entornId) {
		this.entornId = entornId;
	}

	private Object resolPrimerTerme(Long entornId, String terme, ExecutionContext executionContext) {
		logger.debug("Analitzant primer terme: '" + terme + "'");
		Object entitat = null;
		if (terme.equalsIgnoreCase("previous")) {
			//String userName = SecurityHelper.getAuthenticatedActorId();
			String userName = Jbpm3HeliumBridge.getInstanceService().getUsuariCodiActual();
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
		logger.debug("Analitzant següent terme: '" + terme + "'");
		if ((terme.startsWith("member(")) && (terme.endsWith(")")) ) {
			String carrec = terme.substring(7, terme.length() - 1).trim();
			AreaDto a = (AreaDto)entitat;
			entitat = getPersonaAmbAreaICarrec(entornId, a.getCodi(), carrec);
			if (entitat == null)
				throw new ExpressionAssignmentException("No hi ha cap usuari amb el carrec '" + carrec + "' a dins l'àrea '" + a.getNom() + "'");
		} else {
			throw new ExpressionAssignmentException("No es pot interpretar el terme '" + terme + "' de la expressio '" + getExpressio() + "'");
		}
	    return entitat;
	}

	private PersonaDto getPersonaAmbCodi(String codi) {
		PersonaDto p = Jbpm3HeliumBridge.getInstanceService().getPersonaAmbCodi(codi);
		if (p == null)
			logger.warn("No s'ha pogut trobar la persona amb el codi \"" + codi + "\"");
		return p;
	}

	private AreaDto getAreaAmbCodi(Long entornId, String codi) {
		try {
			AreaDto area = Jbpm3HeliumBridge.getInstanceService().getAreaAmbEntornICodi(
					entornId,
					codi);
			if (area == null)
				logger.warn("No s'ha pogut trobar l'àrea amb el codi \"" + codi + "\"");
			return area;
		} catch (NoTrobatException ex) {
			logger.error("No s'ha pogut trobar l'entorn (id=" + entornId + ")", ex);
			return null;
		}
	}

	private List<PersonaDto> getPersonesAmbArea(Long entornId, String codi) {
		AreaDto area = getAreaAmbCodi(entornId, codi);
		List<PersonaDto> persones = new ArrayList<PersonaDto>();
		if (area.getMembres() != null) {
			for (AreaMembreDto membre: area.getMembres()) {
				PersonaDto p = getPersonaAmbCodi(membre.getCodi());
				if (p != null)
					persones.add(p);
			}
		}
		if (personesAmbAreaRecursiu() && area.getFills() != null) {
			for (AreaDto fill: area.getFills())
				persones.addAll(getPersonesAmbArea(entornId, fill.getCodi()));
		}
		return persones;
	}

	private PersonaDto getPersonaAmbAreaICarrec(
			Long entornId,
			String areaCodi,
			String carrecCodi) {
		try {
			CarrecDto carrec = Jbpm3HeliumBridge.getInstanceService().getCarrecAmbEntornIAreaICodi(entornId, areaCodi, carrecCodi);
			if (carrec != null && carrec.getPersonaCodi() != null)
				return getPersonaAmbCodi(carrec.getPersonaCodi());
			return null;
		} catch (NoTrobatException ex) {
			logger.error("No s'ha pogut trobar " + ex.getClass().getName() + " (id/codi=" + ex.getObjectId().toString() + ")", ex);
			return null;
		}
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
		String esRecursiu = Jbpm3HeliumBridge.getInstanceService().getHeliumProperty("app.jbpm.identity.recursiu");
		return "true".equalsIgnoreCase(esRecursiu);
	}

	private static final Log logger = LogFactory.getLog(HeliumExpressionAssignmentHandler.class);

}
