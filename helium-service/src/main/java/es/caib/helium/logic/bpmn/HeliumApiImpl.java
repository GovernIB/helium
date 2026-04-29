package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.model.DocumentInfo;
import es.caib.helium.commons.dto.DocumentDto;
import es.caib.helium.commons.dto.PortafirmesFluxBlocDto;
import es.caib.helium.commons.dto.PortafirmesSimpleTipusEnumDto;
import es.caib.helium.commons.dto.PortafirmesTipusEnumDto;
import es.caib.helium.logic.helper.ExpedientDadaHelper;
import es.caib.helium.logic.helper.PluginHelper;
import es.caib.helium.persistence.entity.Estat;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.repository.EstatRepository;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;

/**
 * Implementació de HeliumApi per a passar com a argument a l'execució de handlers BPMN.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
public class HeliumApiImpl implements HeliumApi {

	private final Expedient expedient;
	private final String processId;
	private final EstatRepository estatRepository;
	private final PluginHelper pluginHelper;
	private final ExpedientDadaHelper expedientDadaHelper;

	@Override
	public Object getVariable(String codi) {
		return expedientDadaHelper.getDada(
			expedient,
			processId,
			null,
			codi);
	}

	@Override
	public void setVariable(String codi, Object valor) {
		expedientDadaHelper.setDada(
			expedient,
			processId,
			null,
			codi,
			valor);
	}

	@Override
	public <T> T getVariableDefaultValue(String codi, T defaultValue) {
		if (codi != null) {
			Object value = expedientDadaHelper.getDada(
				expedient,
				processId,
				null,
				codi);
			return value != null ? (T)value : defaultValue;
		} else {
			return defaultValue;
		}
	}

	@Override
	public DocumentInfo getDocument(String documentCodi) {
		/*String varCodi = Jbpm3HeliumBridge.getInstanceService().getCodiVariablePerDocumentCodi(documentCodi);
		Object valor = getVariable(varCodi);
		if (valor instanceof Long) {
			Long documentStoreId = (Long)valor;
			DocumentDto document = Jbpm3HeliumBridge.getInstanceService().getDocumentInfo(documentStoreId);
			if (document != null) {
				DocumentInfo documentInfo = new DocumentInfo();
				return documentInfo;
			}
		}*/
		return null;
	}

	@Override
	public void expedientEstatModificar(String codi) {
		Estat estat = estatRepository.findByExpedientTipusIdAndCodi(
			expedient.getTipus().getId(),
			codi);
		if (estat != null) {
			expedient.setEstat(estat);
		} else {
			throw new EntityNotFoundException(
				"Estat no trobat (" +
					"expedientTipusId=" + expedient.getTipus().getId() + ", " +
					"codi=" + codi + ")");
		}
	}

	@Override
	public void expedientComentariModificar(String comentari) {
		expedient.setComentari(comentari);
	}

	public Integer portasignaturesEnviar(
		DocumentDto document,
		List<DocumentDto> annexos,
		List<PortafirmesFluxBlocDto> blocList,
		Expedient expedient,
		String importancia,
		Date dataLimit,
		Long tokenId,
		Long processInstanceId,
		String transicioOK,
		String transicioKO,
		PortafirmesSimpleTipusEnumDto portafirmesTipus,
		String portafirmesFluxId,
		PortafirmesTipusEnumDto fluxTipus) {
		return pluginHelper.portasignaturesEnviar(
			document,
			annexos,
			blocList,
			expedient,
			importancia,
			dataLimit,
			tokenId,
			processInstanceId,
			transicioOK,
			transicioKO,
			portafirmesTipus,
			portafirmesFluxId,
			fluxTipus);
	}

}
