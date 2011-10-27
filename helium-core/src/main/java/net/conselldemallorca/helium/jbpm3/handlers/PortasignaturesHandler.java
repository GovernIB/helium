package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.service.TascaService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per fer la integració amb portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class PortasignaturesHandler extends AbstractHeliumActionHandler implements PortasignaturesHandlerInterface {

	private String varResponsableCodi;
	private String responsableCodi;
	private String pas1Responsables;
	private String varPas1Responsables;
	private String pas1MinSignataris;
	private String varPas1MinSignataris;
	private String pas2Responsables;
	private String varPas2Responsables;
	private String pas2MinSignataris;
	private String varPas2MinSignataris;
	private String pas3Responsables;
	private String varPas3Responsables;
	private String pas3MinSignataris;
	private String varPas3MinSignataris;
	private String varDocument;
	private String document;
	private String varAnnexos;
	private String annexos;
	private String varImportancia;
	private String importancia;
	private String varDataLimit;
	private String dataLimit;
	private String varTransicioOK;
	private String transicioOK;
	private String varTransicioKO;
	private String transicioKO;

	public void execute(ExecutionContext executionContext) throws Exception {
		try {
			String personaCodi = (String)getValorOVariable(executionContext, responsableCodi, varResponsableCodi);
			PersonaDto persona = null;
			if (personaCodi != null) {
				persona = getPersonaAmbCodi(personaCodi);
			} else {
				throw new JbpmException("No s'ha pogut trobar la persona amb codi '" + getValorOVariable(executionContext, responsableCodi, varResponsableCodi) + "'");
			}
			String documentCodi = (String)getValorOVariable(executionContext, document, varDocument);
			Long documentStoreId = null;
			if (documentCodi != null) {
				documentStoreId = (Long)executionContext.getVariable(
						TascaService.PREFIX_DOCUMENT + documentCodi);
			} else {
				throw new JbpmException("No s'ha pogut trobar el document '" + getValorOVariable(executionContext, document, varDocument) + "'.");
			}
			List<Long> anxs = null;
			String anxsCodis = (String)getValorOVariable(executionContext, annexos, varAnnexos);
			if (anxsCodis != null) {
				anxs = new ArrayList<Long>();
				String[] codis = anxsCodis.split(",");
				for (String codi: codis) {
					anxs.add(
							(Long)executionContext.getVariable(
									TascaService.PREFIX_DOCUMENT + codi.trim()));
				}
			}
			getPluginService().enviarPortasignatures(
					documentStoreId,
					anxs,
					persona,
					getPersonesPas(executionContext, 1),
					getMinSignatarisPas(executionContext, 1),
					getPersonesPas(executionContext, 2),
					getMinSignatarisPas(executionContext, 2),
					getPersonesPas(executionContext, 3),
					getMinSignatarisPas(executionContext, 3),
					this.getExpedient(executionContext),
					(String)getValorOVariable(executionContext, importancia, varImportancia),
					(Date)getValorOVariable(executionContext, dataLimit, varDataLimit),
					executionContext.getToken().getId(),
					(String)getValorOVariable(executionContext, transicioOK, varTransicioOK),
					(String)getValorOVariable(executionContext, transicioKO, varTransicioKO));
		} catch (Exception e) {
			logger.error("Error PortasignaturesHandler. ", e);
			throw new JbpmException("No s'ha pogut enviar el document al portasignatures", e);
		}
	}

	public void setVarResponsableCodi(String varResponsableCodi) {
		this.varResponsableCodi = varResponsableCodi;
	}
	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}
	public void setPas1Responsables(String pas1Responsables) {
		this.pas1Responsables = pas1Responsables;
	}
	public void setVarPas1Responsables(String varPas1Responsables) {
		this.varPas1Responsables = varPas1Responsables;
	}
	public void setPas1MinSignataris(String pas1MinSignataris) {
		this.pas1MinSignataris = pas1MinSignataris;
	}
	public void setVarPas1MinSignataris(String varPas1MinSignataris) {
		this.varPas1MinSignataris = varPas1MinSignataris;
	}
	public void setPas2Responsables(String pas2Responsables) {
		this.pas2Responsables = pas2Responsables;
	}
	public void setVarPas2Responsables(String varPas2Responsables) {
		this.varPas2Responsables = varPas2Responsables;
	}
	public void setPas2MinSignataris(String pas2MinSignataris) {
		this.pas2MinSignataris = pas2MinSignataris;
	}
	public void setVarPas2MinSignataris(String varPas2MinSignataris) {
		this.varPas2MinSignataris = varPas2MinSignataris;
	}
	public void setPas3Responsables(String pas3Responsables) {
		this.pas3Responsables = pas3Responsables;
	}
	public void setVarPas3Responsables(String varPas3Responsables) {
		this.varPas3Responsables = varPas3Responsables;
	}
	public void setPas3MinSignataris(String pas3MinSignataris) {
		this.pas3MinSignataris = pas3MinSignataris;
	}
	public void setVarPas3MinSignataris(String varPas3MinSignataris) {
		this.varPas3MinSignataris = varPas3MinSignataris;
	}
	public void setVarDocument(String varDocument) {
		this.varDocument = varDocument;
	}
	public void setVarAnnexos(String varAnnexos) {
		this.varAnnexos = varAnnexos;
	}
	public void setAnnexos(String annexos) {
		this.annexos = annexos;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public void setVarImportancia(String varImportancia) {
		this.varImportancia = varImportancia;
	}
	public void setImportancia(String importancia) {
		this.importancia = importancia;
	}
	public void setVarDataLimit(String varDataLimit) {
		this.varDataLimit = varDataLimit;
	}
	public void setDataLimit(String dataLimit) {
		this.dataLimit = dataLimit;
	}
	public void setVarTransicioOK(String varTransicioOK) {
		this.varTransicioOK = varTransicioOK;
	}
	public void setTransicioOK(String transicioOK) {
		this.transicioOK = transicioOK;
	}
	public void setVarTransicioKO(String varTransicioKO) {
		this.varTransicioKO = varTransicioKO;
	}
	public void setTransicioKO(String transicioKO) {
		this.transicioKO = transicioKO;
	}

	private List<PersonaDto> getPersonesPas(ExecutionContext executionContext, int pas) {
		List<PersonaDto> resposta = null;
		String responsables = null;
		if (pas == 1) {
			responsables = (String)getValorOVariable(executionContext, pas1Responsables, varPas1Responsables);
		} else if (pas == 2) {
			responsables = (String)getValorOVariable(executionContext, pas2Responsables, varPas2Responsables);
		} else if (pas == 3) {
			responsables = (String)getValorOVariable(executionContext, pas3Responsables, varPas3Responsables);
		}
		if (responsables != null) {
			resposta = new ArrayList<PersonaDto>();
			String[] codis = responsables.split(",");
			for (String personaCodi: codis) {
				PersonaDto persona = getPersonaAmbCodi(personaCodi.trim());
				if (persona != null)
					resposta.add(persona);
			}
		}
		return resposta;
	}
	private Integer getMinSignatarisPas(ExecutionContext executionContext, int pas) {
		Integer minSignataris = null;
		if (pas == 1) {
			minSignataris = getValorOVariableInteger(executionContext, pas1MinSignataris, varPas1MinSignataris);
		} else if (pas == 2) {
			minSignataris = getValorOVariableInteger(executionContext, pas2MinSignataris, varPas2MinSignataris);
		} else if (pas == 3) {
			minSignataris = getValorOVariableInteger(executionContext, pas3MinSignataris, varPas3MinSignataris);
		}
		return minSignataris;
	}

	private static final Log logger = LogFactory.getLog(PortasignaturesHandler.class);

}
