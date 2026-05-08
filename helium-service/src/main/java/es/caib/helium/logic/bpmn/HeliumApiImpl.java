package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.model.DocumentInfo;
import es.caib.helium.commons.dto.*;
import es.caib.helium.logic.helper.*;
import es.caib.helium.persistence.entity.Document;
import es.caib.helium.persistence.entity.DocumentStore;
import es.caib.helium.persistence.entity.Estat;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.repository.EstatRepository;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementació de HeliumApi per a passar com a argument a l'execució de handlers BPMN.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
public class HeliumApiImpl implements HeliumApi {

	private final Expedient expedient;
	private final String processId;
	private final String taskId;
	private final EstatRepository estatRepository;
	private final ExpedientHelper expedientHelper;
	private final ExpedientDadaHelper expedientDadaHelper;
	private final ExpedientDocumentHelper expedientDocumentHelper;
	private final DocumentHelperV3 documentHelperV3;
	private final PluginHelper pluginHelper;
	private final AlertaHelper alertaHelper;

	@Override
	public <T> T getVariable(String codi) {
		return getVariableValue(codi, null);
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
		return getVariableValue(codi, defaultValue);
	}

	public Date getVariableDefaultValueAsDate(String codi, Object defaultValue) {
		Object value = getVariableValue(codi, defaultValue);
		if (value != null) {
			if (value instanceof Date) {
				return (Date)value;
			} else {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					return sdf.parse(value.toString());
				} catch (ParseException ex) {
					throw new HeliumHandlerException("Couldn't parse date " + value);
				}
			}
		} else {
			return null;
		}
	}

	public Boolean getVariableDefaultValueAsBoolean(String codi, Object defaultValue) {
		Object value = getVariableValue(codi, defaultValue);
		if (value != null) {
			if (value instanceof Boolean) {
				return (Boolean)value;
			} else {
				return Boolean.valueOf(value.toString());
			}
		} else {
			return null;
		}
	}

	@Override
	public DocumentInfo getDocumentInfo(String documentCodi) {
		DocumentStore documentStore = expedientDocumentHelper.findDocumentStore(
			documentCodi,
			expedient.getId(),
			processId,
			null);
		if (documentStore != null) {
			Document documentDisseny = expedientDocumentHelper.findDocument(
				documentCodi,
				expedient.getId(),
				processId,
				null);
			DocumentDto docV3 = documentHelperV3.toDocumentDto(
				documentStore.getId(),
				false,
				false,
				true,
				true,
				false,
				false);
			DocumentInfo resposta = new DocumentInfo();
			resposta.setId(documentStore.getId());
			resposta.setCodiDocument(documentCodi);
			if (documentStore.isAdjunt()) {
				resposta.setTitol(documentStore.getAdjuntTitol());
			} else {
				resposta.setTitol(documentDisseny.getNom());
			}
			resposta.setDataCreacio(documentStore.getDataCreacio());
			resposta.setDataDocument(documentStore.getDataDocument());
			resposta.setSignat(documentStore.isSignat());
			if (documentStore.isSignat()) {
				resposta.setCsv(docV3.getArxiuCsv());
				resposta.setUrlVerificacioSignatures(docV3.getSignaturaUrlVerificacio());
			}
			resposta.setRegistrat(documentStore.isRegistrat());
			if (documentStore.isRegistrat()) {
				resposta.setRegistreNumero(documentStore.getRegistreNumero());
				resposta.setRegistreData(documentStore.getRegistreData());
				resposta.setRegistreOficinaCodi(documentStore.getRegistreOficinaCodi());
				resposta.setRegistreOficinaNom(documentStore.getRegistreOficinaNom());
				resposta.setRegistreEntrada(documentStore.isRegistreEntrada());
			}
			return resposta;
		} else {
			throw new HeliumHandlerException("Document no trobat: " + documentCodi);
		}
	}

	@Override
	public void setDocument(
		String documentCodi,
		String arxiuNom,
		byte[] arxiuContingut,
		Date dataDocument,
		boolean ambFirma,
		boolean firmaSeparada,
		byte[] firmaContingut) {
		expedientDocumentHelper.setDocument(
			expedient.getId(),
			processId,
			documentCodi,
			dataDocument,
			null,
			arxiuNom,
			arxiuContingut,
			null,
			ambFirma,
			firmaSeparada,
			firmaContingut,
			null);
	}

	@Override
	public void alertaCrear(String usuariCodi, String text) {
		alertaHelper.crearAlerta(
			expedient.getEntorn(),
			expedient,
			new Date(),
			usuariCodi,
			text);
	}

	@Override
	public void documentConsultar(
		String documentCodi,
		String varCsv,
		String varUrl) {
		DocumentInfo documentInfo = getDocumentInfo(documentCodi);
		if (varCsv != null && !varCsv.isEmpty()) {
			setVariable(varCsv, documentInfo.getCsv());
		}
		if (varUrl != null && !varUrl.isEmpty()) {
			setVariable(varUrl, documentInfo.getUrlVerificacioSignatures());
		}
	}

	@Override
	public void expedientAturar(String motiu) {
		expedientHelper.aturar(
			expedient,
			motiu,
			null);
	}

	@Override
	public void expedientConsultar(
		String varRegistreNumero,
		String varTitol,
		String varNumero,
		String varDataInici) {
		if (varRegistreNumero != null && !varRegistreNumero.isEmpty()) {
			setVariable(varRegistreNumero, expedient.getRegistreNumero());
		}
		if (varTitol != null && !varTitol.isEmpty()) {
			setVariable(varTitol, expedient.getTitol());
		}
		if (varNumero != null && !varNumero.isEmpty()) {
			setVariable(varNumero, expedient.getNumero());
		}
		if (varDataInici != null && !varDataInici.isEmpty()) {
			setVariable(varDataInici, new java.sql.Timestamp(expedient.getDataInici().getTime()));
		}
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

	@Override
	public void expedientFinalitzar() {
		expedientHelper.finalitzar(expedient.getId(), true);
	}

	@Override
	public void expedientDesfinalitzar(boolean reprendre) {
		expedientHelper.desfinalitzar(expedient, null);
		if (reprendre) {
			expedientHelper.reprendre(
				expedient,
				null);
		}
	}

	@Override
	public void expedientGrupModificar(String grupCodi) {
		expedientHelper.update(
			expedient,
			expedient.getNumero(),
			expedient.getTitol(),
			expedient.getResponsableCodi(),
			expedient.getDataInici(),
			expedient.getComentari(),
			(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
			expedient.getGeoPosX(),
			expedient.getGeoPosY(),
			expedient.getGeoReferencia(),
			grupCodi,
			false);
	}

	@Override
	public void expedientNumeroModificar(String numero) {
		expedientHelper.update(
			expedient,
			numero,
			expedient.getTitol(),
			expedient.getResponsableCodi(),
			expedient.getDataInici(),
			expedient.getComentari(),
			(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
			expedient.getGeoPosX(),
			expedient.getGeoPosY(),
			expedient.getGeoReferencia(),
			expedient.getGrupCodi(),
			false);
	}

	@Override
	public void expedientResponsableModificar(String responsableCodi) {
		expedientHelper.update(
			expedient,
			expedient.getNumero(),
			expedient.getTitol(),
			responsableCodi,
			expedient.getDataInici(),
			expedient.getComentari(),
			(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
			expedient.getGeoPosX(),
			expedient.getGeoPosY(),
			expedient.getGeoReferencia(),
			expedient.getGrupCodi(),
			false);
	}

	@Override
	public void expedientTitolModificar(String titol) {
		expedientHelper.update(
			expedient,
			expedient.getNumero(),
			titol,
			expedient.getResponsableCodi(),
			expedient.getDataInici(),
			expedient.getComentari(),
			(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
			expedient.getGeoPosX(),
			expedient.getGeoPosY(),
			expedient.getGeoReferencia(),
			expedient.getGrupCodi(),
			false);
	}

	@Override
	public Integer portasignaturesEnviar(
		String documentCodi,
		String personaCodi,
		List<String> annexCodis,
		List<String> personaCodisPas1,
		Integer minSignatarisPas1,
		List<String> personaCodisPas2,
		Integer minSignatarisPas2,
		List<String> personaCodisPas3,
		Integer minSignatarisPas3,
		String importancia,
		Date dataLimit,
		String transicioOK,
		String transicioKO,
		String portafirmesFluxId) {
		List<DocumentDto> annexos = null;
		if (annexCodis != null) {
			annexos = annexCodis.stream().map(this::toPortafirmesDocumentDto).collect(Collectors.toList());
		}
		List<PortafirmesFluxBlocDto> blocList = new ArrayList<>();
		if (personaCodisPas1 != null) {
			blocList.add(toPortafirmesFluxBlocDto(personaCodisPas1, minSignatarisPas1));
		}
		if (personaCodisPas2 != null) {
			blocList.add(toPortafirmesFluxBlocDto(personaCodisPas2, minSignatarisPas2));
		}
		if (personaCodisPas3 != null) {
			blocList.add(toPortafirmesFluxBlocDto(personaCodisPas3, minSignatarisPas3));
		}
		return pluginHelper.portasignaturesEnviar(
			toPortafirmesDocumentDto(documentCodi),
			annexos,
			blocList,
			expedient,
			importancia,
			dataLimit,
			null,
			null,
			transicioOK,
			transicioKO,
			PortafirmesSimpleTipusEnumDto.SERIE,
			portafirmesFluxId,
			PortafirmesTipusEnumDto.SIMPLE);
	}

	private DocumentDto toPortafirmesDocumentDto(String documentCodi) {
		DocumentDto document = new DocumentDto();
		Document documentDisseny = expedientDocumentHelper.findDocument(
			documentCodi,
			expedient.getId(),
			processId,
			null);
		DocumentStore documentStore = expedientDocumentHelper.findDocumentStore(
			documentCodi,
			expedient.getId(),
			processId,
			null);
		DocumentDto docV3 = documentHelperV3.toDocumentDto(
			documentStore.getId(),
			false,
			false,
			true,
			true,
			false,
			false);
		document.setId(documentStore.getId());
		document.setDocumentCodi(documentCodi);
		document.setDocumentNom(documentDisseny.getCodi());
		document.setTipusDocPortasignatures(documentDisseny.getTipusDocPortasignatures());
		document.setNtiTipoDocumental(documentStore.getNtiTipoDocumental());
		document.setArxiuNom(documentStore.getArxiuNom());
		document.setSignat(documentStore.isSignat());
		document.setVistaNom(docV3.getVistaNom());
		document.setVistaContingut(docV3.getVistaContingut());
		return document;
	}

	private PortafirmesFluxBlocDto toPortafirmesFluxBlocDto(
		List<String> personaCodis,
		Integer minSignataris) {
		PortafirmesFluxBlocDto bloc = new PortafirmesFluxBlocDto();
		bloc.setDestinataris(personaCodis);
		boolean[] obligatorietats = new boolean[personaCodis.size()];
		Arrays.fill(obligatorietats, true);
		bloc.setObligatorietats(obligatorietats);
		bloc.setMinSignataris(minSignataris != null ? minSignataris : 0);
		return bloc;
	}

	private <T> T getVariableValue(String codi, T defaultValue) {
		T value;
		if (codi != null) {
			Object v = expedientDadaHelper.getDada(
				expedient,
				processId,
				null,
				codi);
			value = v != null ? (T)v : defaultValue;
		} else {
			value = defaultValue;
		}
		return value;
	}

}
