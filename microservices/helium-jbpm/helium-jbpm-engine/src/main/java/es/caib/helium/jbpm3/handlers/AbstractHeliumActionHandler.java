/**
 * 
 */
package es.caib.helium.jbpm3.handlers;

import java.text.SimpleDateFormat;
import java.util.Date;

import es.caib.helium.api.dto.ArxiuDto;
import es.caib.helium.api.dto.DefinicioProcesDto;
import es.caib.helium.api.dto.DocumentDto;
import es.caib.helium.api.dto.ExpedientDto;
import es.caib.helium.api.dto.TerminiDto;
import es.caib.helium.api.dto.TerminiIniciatDto;
import es.caib.helium.api.exception.NoTrobatException;
import es.caib.helium.jbpm3.integracio.DominiCodiDescripcio;
import es.caib.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import org.jbpm.JbpmException;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;

/**
 * Handler base amb accés a la funcionalitat de Helium
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
abstract class AbstractHeliumActionHandler implements ActionHandler {

	public abstract void execute(ExecutionContext executionContext)
			throws Exception;

	ExpedientDto getExpedientActual(ExecutionContext executionContext) {
		ExpedientDto expedient = Jbpm3HeliumBridge.getInstanceService().getExpedientIniciant();
		if (expedient == null) {
			expedient = Jbpm3HeliumBridge.getInstanceService().getExpedientArrelAmbProcessInstanceId(
					getProcessInstanceId(executionContext));
		}
		return expedient;
	}

	ExpedientDto findExpedientAmbMateixTipusINumero(
			ExecutionContext executionContext,
			String numero) {
		ExpedientDto expedient = getExpedientActual(executionContext);
		return Jbpm3HeliumBridge.getInstanceService().findExpedientAmbMateixTipusINumero(
				expedient.getEntorn().getId(),
				expedient.getTipus().getId(),
				numero);
	}

	DefinicioProcesDto getDefinicioProces(ExecutionContext executionContext) {
		return Jbpm3HeliumBridge.getInstanceService().getDefinicioProcesPerProcessInstanceId(
				getProcessInstanceId(executionContext));
	}

	TerminiDto getTerminiAmbCodi(ExecutionContext executionContext, String codi) {
		try {
			return Jbpm3HeliumBridge.getInstanceService().getTerminiAmbProcessInstanceICodi(
					getProcessInstanceId(executionContext),
					codi);
		} catch (Exception ex) {
			throw new JbpmException("No s'ha trobat el termini amb codi: " + codi);
		}
	}
	TerminiIniciatDto getTerminiIniciatAmbCodi(
			ExecutionContext executionContext,
			String codi) {
		TerminiDto termini = getTerminiAmbCodi(
				executionContext,
				codi);
		if (termini != null) {
			return Jbpm3HeliumBridge.getInstanceService().getTerminiIniciatAmbProcessInstanceITerminiCodi(
					getProcessInstanceId(executionContext),
					termini.getCodi());
		} else {
			return null;
		}
	}

	DocumentInfo getDocumentInfo(
			ExecutionContext executionContext,
			String documentCodi,
			boolean ambArxiu) {
		String varCodi = Jbpm3HeliumBridge.getInstanceService().getCodiVariablePerDocumentCodi(documentCodi);
		Object valor = executionContext.getVariable(varCodi);
		if (valor == null)
			return null;
		if (valor instanceof Long) {
			Long id = (Long) valor;
			DocumentDto document = Jbpm3HeliumBridge.getInstanceService().getDocumentInfo(id);
			if (document == null)
				return null;
			DocumentInfo resposta = new DocumentInfo();
			resposta.setId(id);
			resposta.setCodiDocument(document.getDocumentCodi());
			if (document.isAdjunt()) {
				resposta.setTitol(document.getAdjuntTitol());
			} else {
				resposta.setTitol(document.getDocumentNom());
			}
			resposta.setDataCreacio(document.getDataCreacio());
			resposta.setDataDocument(document.getDataDocument());
			resposta.setSignat(document.isSignat());
			if (document.isRegistrat()) {
				resposta.setRegistrat(true);
				resposta.setRegistreNumero(document.getRegistreNumero());
				resposta.setRegistreData(document.getRegistreData());
				resposta.setRegistreOficinaCodi(document.getRegistreOficinaCodi());
				resposta.setRegistreOficinaNom(document.getRegistreOficinaNom());
				resposta.setRegistreEntrada(document.isRegistreEntrada());
			}
			if (ambArxiu) {
				ArxiuDto arxiu = Jbpm3HeliumBridge.getInstanceService().getArxiuPerMostrar(id);
				resposta.setArxiuNom(arxiu.getNom());
				resposta.setArxiuContingut(arxiu.getContingut());
			}
			return resposta;
		} else {
			throw new JbpmException("La referencia al document \""
					+ documentCodi + "\" no es del tipus correcte");
		}
	}

	protected String getProcessInstanceId(ExecutionContext executionContext) {
		return new Long(executionContext.getProcessInstance().getId()).toString();
	}
	protected String getTaskInstanceId(ExecutionContext executionContext) {
		return new Long(executionContext.getTaskInstance().getId()).toString();
	}
	
	protected Date getVariableComData(ExecutionContext executionContext,
			String var) {
		Object obj = executionContext.getVariable(var);
		if (obj == null)
			throw new NoTrobatException(Date.class, var);
		if (obj instanceof Date)
			return (Date) obj;
		throw new JbpmException("La variable amb el codi '" + var + "' no és de tipus Date");
	}

	protected Object getValorOVariable(ExecutionContext executionContext,
			Object value, String var) {
		if (value != null)
			return value;
		if (var != null && var.length() > 0) {
			Object returnVal;
			if (executionContext.getVariable(var) instanceof DominiCodiDescripcio)
				returnVal = ((DominiCodiDescripcio)executionContext.getVariable(var)).getCodi();
			else
				returnVal = executionContext.getVariable(var);
			return returnVal;
		}
		return null;
	}

	protected Date getValorOVariableData(ExecutionContext executionContext,
			Object value, String var) {
		if (value != null) {
			if (value instanceof Date) {
				return (Date) value;
			} else {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					return sdf.parse(value.toString());
				} catch (Exception ignored) {
				}
			}
		}
		if (var != null && var.length() > 0) {
			Object valor = executionContext.getVariable(var);
			if (valor != null) {
				if (valor instanceof Date) {
					return (Date) valor;
				} else {
					try {
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						return sdf.parse(valor.toString());
					} catch (Exception ignored) {
					}
				}
			}
		}
		return null;
	}

	protected Integer getValorOVariableInteger(
			ExecutionContext executionContext, Object value, String var) {
		if (value != null) {
			if (value instanceof Integer) {
				return (Integer) value;
			} else {
				return new Integer(value.toString());
			}
		}
		if (var != null && var.length() > 0) {
			Object valor = executionContext.getVariable(var);
			if (valor != null) {
				if (valor instanceof Integer) {
					return (Integer) valor;
				} else {
					return new Integer(valor.toString());
				}
			}
		}
		return null;
	}
	
	protected Boolean getValorOVariableBoolean(
			ExecutionContext executionContext, Object value, String var) {
		if (value != null) {
			if (value instanceof Boolean) {
				return (Boolean) value;
			} else {
				return new Boolean(value.toString());
			}
		}
		if (var != null && var.length() > 0) {
			Object valor = executionContext.getVariable(var);
			if (valor != null) {
				if (valor instanceof Boolean) {
					return (Boolean) valor;
				} else {
					return new Boolean(valor.toString());
				}
			}
		}
		return null;
	}
}
