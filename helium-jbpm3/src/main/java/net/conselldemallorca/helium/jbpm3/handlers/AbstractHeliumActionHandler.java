/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;

import org.jbpm.JbpmException;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

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
		ExpedientDto expedient = Jbpm3HeliumBridge.getInstance().getExpedientIniciant();
		if (expedient == null) {
			expedient = getExpedientService().findAmbProcessInstanceId(
					getProcessInstanceId(executionContext));
		}
		return expedient;
	}

	DefinicioProcesDto getDefinicioProces(ExecutionContext executionContext) {
		return Jbpm3HeliumBridge.getInstance().getDefinicioProcesPerProcessInstanceId(
				getProcessInstanceId(executionContext));
	}

	PersonaDto getPersonaAmbCodi(String codi) {
		return Jbpm3HeliumBridge.getInstance().getPersonaAmbCodi(codi);
	}

	TerminiDto getTerminiAmbCodi(ExecutionContext executionContext, String codi) {
		try {
			return getDissenyService().findTerminiAmbDefinicioProcesICodi(
					getDefinicioProces(executionContext).getId(),
					codi);
		} catch (Exception ex) {
			throw new JbpmException("No s'ha trobat la definició de procés");
		}
	}
	TerminiIniciatDto getTerminiIniciatAmbCodi(
			ExecutionContext executionContext,
			String codi) {
		TerminiDto termini = getTerminiAmbCodi(
				executionContext,
				codi);
		if (termini != null) {
			return getTerminiService().findIniciatAmbTerminiIProcessInstance(
					termini.getId(),
					getProcessInstanceId(executionContext));
		} else {
			return null;
		}
	}

	DocumentInfo getDocumentInfo(
			ExecutionContext executionContext,
			String documentCodi,
			boolean ambArxiu) {
		String varCodi = getVarDocument(documentCodi);
		Object valor = executionContext.getVariable(varCodi);
		if (valor == null)
			return null;
		if (valor instanceof Long) {
			Long id = (Long) valor;
			DocumentDto document = getDocumentService().getInfo(id);
			if (document == null)
				return null;
			DocumentInfo resposta = new DocumentInfo();
			resposta.setId(id);
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
				ArxiuDto arxiu = getDocumentService().getArxiuPerMostrar(id);
				resposta.setArxiuNom(arxiu.getNom());
				resposta.setArxiuContingut(arxiu.getContingut());
			}
			return resposta;
		} else {
			throw new JbpmException("La referencia al document \""
					+ documentCodi + "\" no es del tipus correcte");
		}
	}
	String getVarDocument(String documentCodi) {
		return DocumentService.PREFIX_VAR_DOCUMENT + documentCodi;
	}

	ExpedientService getExpedientService() {
		return Jbpm3HeliumBridge.getInstance().getExpedientService();
	}
	DocumentService getDocumentService() {
		return Jbpm3HeliumBridge.getInstance().getDocumentService();
	}
	PluginService getPluginService() {
		return Jbpm3HeliumBridge.getInstance().getPluginService();
	}
	DissenyService getDissenyService() {
		return Jbpm3HeliumBridge.getInstance().getDissenyService();
	}
	TerminiService getTerminiService() {
		return Jbpm3HeliumBridge.getInstance().getTerminiService();
	}

	protected String getProcessInstanceId(ExecutionContext executionContext) {
		return new Long(executionContext.getProcessInstance().getId()).toString();
	}

	protected Date getVariableComData(ExecutionContext executionContext,
			String var) {
		Object obj = executionContext.getVariable(var);
		if (obj instanceof Date)
			return (Date) obj;
		throw new JbpmException("La variable amb el codi '" + var + "' no és de tipus Date");
	}

	protected Object getValorOVariable(ExecutionContext executionContext,
			Object value, String var) {
		if (value != null)
			return value;
		if (var != null && var.length() > 0)
			return executionContext.getVariable(var);
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

}
