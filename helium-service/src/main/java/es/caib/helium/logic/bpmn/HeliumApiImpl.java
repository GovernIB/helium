package es.caib.helium.logic.bpmn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.engine.WProcessInstance;
import es.caib.helium.bpmn.engine.WTaskInstance;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.model.ConsultaPinbal;
import es.caib.helium.bpmn.model.DadesNotificacio;
import es.caib.helium.bpmn.model.DadesRegistreInteressat;
import es.caib.helium.bpmn.model.DadesRegistreSortida;
import es.caib.helium.bpmn.model.RespostaRegistre;
import es.caib.helium.bpmn.model.DocumentInfo;
import es.caib.helium.bpmn.model.ExpedientInfo;
import es.caib.helium.bpmn.model.ExpedientTipusInfo;
import es.caib.helium.bpmn.model.TerminiInfo;
import es.caib.helium.commons.dto.ArxiuDto;
import es.caib.helium.commons.dto.DadesEnviamentDto;
import es.caib.helium.commons.dto.DadesNotificacioDto;
import es.caib.helium.commons.dto.DocumentDto;
import es.caib.helium.commons.dto.InteressatDocumentTipusEnumDto;
import es.caib.helium.commons.dto.InteressatDto;
import es.caib.helium.commons.dto.InteressatTipusEnumDto;
import es.caib.helium.commons.dto.PortafirmesFluxBlocDto;
import es.caib.helium.commons.dto.PortafirmesSimpleTipusEnumDto;
import es.caib.helium.commons.dto.PortafirmesTipusEnumDto;
import es.caib.helium.commons.dto.RegistreIdDto;
import es.caib.helium.commons.registre.RegistreAnnex;
import es.caib.helium.commons.registre.RegistreAnotacio;
import es.caib.helium.commons.registre.RegistreInteressat;
import es.caib.helium.commons.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.helium.commons.registre.RegistreInteressatTipusEnum;
import es.caib.helium.commons.utils.GlobalProperties;
import es.caib.helium.commons.utils.StringUtilsHelium;
import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.integracio.plugins.notificacio.RespostaEnviar;
import es.caib.helium.logic.helper.AlertaHelper;
import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.helper.DocumentHelperV3;
import es.caib.helium.logic.helper.ExpedientDadaHelper;
import es.caib.helium.logic.helper.ExpedientDocumentHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.ExpedientInteressatHelper;
import es.caib.helium.logic.helper.MailHelper;
import es.caib.helium.logic.helper.PluginHelper;
import es.caib.helium.persistence.entity.Document;
import es.caib.helium.persistence.entity.DocumentStore;
import es.caib.helium.persistence.entity.Estat;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.entity.Interessat;
import lombok.RequiredArgsConstructor;

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
	private final ExpedientHelper expedientHelper;
	private final ExpedientDadaHelper expedientDadaHelper;
	private final ExpedientDocumentHelper expedientDocumentHelper;
	private final DocumentHelperV3 documentHelperV3;
	private final PluginHelper pluginHelper;
	private final AlertaHelper alertaHelper;
	private final ExpedientInteressatHelper expedientInteressatHelper;
	private final MailHelper mailHelper;
	private final HeliumApiServeis serveis;

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

	public Integer getVariableDefaultValueAsInteger(String codi, Object defaultValue) {
		if (defaultValue != null) {
			if (defaultValue instanceof Integer) {
				return (Integer) defaultValue;
			} else {
				return Integer.parseInt(defaultValue.toString());
			}
		}
		if (codi != null && !codi.isEmpty()) {
			Object valor = getVariable(codi);
			if (valor != null) {
				if (valor instanceof Integer) {
					return (Integer) valor;
				} else {
					return Integer.parseInt(valor.toString());
				}
			}
		}
		return null;
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
	public ExpedientInfo getExpedientInfo() {
		ExpedientInfo expedientInfo = new ExpedientInfo();
		expedientInfo.setId(expedient.getId());
		expedientInfo.setTitol(expedient.getTitol());
		expedientInfo.setNumero(expedient.getNumero());
		expedientInfo.setNumeroDefault(expedient.getNumeroDefault());
		expedientInfo.setDataInici(expedient.getDataInici());
		expedientInfo.setDataFi(expedient.getDataFi());
		expedientInfo.setComentari(expedient.getComentari());
		expedientInfo.setInfoAturat(expedient.getInfoAturat());
		expedientInfo.setComentariAnulat(expedient.getComentariAnulat());
		expedientInfo.setIniciadorTipus(expedient.getIniciadorTipus() != null? ExpedientInfo.IniciadorTipus.valueOf(expedient.getIniciadorTipus().name()) : null);
		expedientInfo.setIniciadorCodi(expedient.getIniciadorCodi());
		expedientInfo.setResponsableCodi(expedient.getResponsableCodi());
		expedientInfo.setGeoPosX(expedient.getGeoPosX());
		expedientInfo.setGeoPosY(expedient.getGeoPosY());
		expedientInfo.setGeoReferencia(expedient.getGeoReferencia());
		expedientInfo.setRegistreNumero(expedient.getRegistreNumero());
		expedientInfo.setRegistreData(expedient.getRegistreData());
		expedientInfo.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
		expedientInfo.setIdioma(expedient.getIdioma());
		expedientInfo.setAutenticat(expedient.isAutenticat());
		expedientInfo.setTramitadorNif(expedient.getTramitadorNif());
		expedientInfo.setTramitadorNom(expedient.getTramitadorNom());
		expedientInfo.setInteressatNif(expedient.getInteressatNif());
		expedientInfo.setInteressatNom(expedient.getInteressatNom());
		expedientInfo.setRepresentantNif(expedient.getRepresentantNif());
		expedientInfo.setRepresentantNom(expedient.getRepresentantNom());
		expedientInfo.setAvisosHabilitats(expedient.isAvisosHabilitats());
		expedientInfo.setAvisosEmail(expedient.getAvisosEmail());
		expedientInfo.setAvisosMobil(expedient.getAvisosMobil());
		expedientInfo.setNotificacioTelematicaHabilitada(expedient.isNotificacioTelematicaHabilitada());
		expedientInfo.setTramitExpedientIdentificador(expedient.getTramitExpedientIdentificador());
		expedientInfo.setTramitExpedientClau(expedient.getTramitExpedientClau());
		expedientInfo.setNtiActiu(expedient.isNtiActiu());
		expedientInfo.setNtiVersion(expedient.getNtiVersion());
		expedientInfo.setNtiIdentificador(expedient.getNtiIdentificador());
		expedientInfo.setNtiOrgano(expedient.getNtiOrgano());
		expedientInfo.setNtiSerieDocumental(expedient.getNtiSerieDocumental());
		expedientInfo.setNtiClasificacion(expedient.getNtiClasificacion());
		expedientInfo.setNtiTipoFirma(expedient.getNtiTipoFirma() != null? expedient.getNtiTipoFirma().name() : null);
		expedientInfo.setNtiCsv(expedient.getNtiCsv());
		expedientInfo.setNtiDefGenCsv(expedient.getNtiDefinicionGenCsv());
		expedientInfo.setArxiuActiu(expedient.isArxiuActiu());
		expedientInfo.setArxiuUuid(expedient.getArxiuUuid());
		expedientInfo.setEstatCodi(expedient.getEstat() != null ? expedient.getEstat().getCodi() : null);
		expedientInfo.setEstatNom(expedient.getEstat() != null ? expedient.getEstat().getNom() : null);
		expedientInfo.setExpedientTipusCodi(expedient.getTipus() != null ? expedient.getTipus().getCodi() : null);
		expedientInfo.setExpedientTipusNom(expedient.getTipus() != null ? expedient.getTipus().getNom() : null);
		expedientInfo.setEntornCodi(expedient.getEntorn() != null ? expedient.getEntorn().getCodi() : null);
		expedientInfo.setEntornNom(expedient.getEntorn() != null ? expedient.getEntorn().getNom() : null);
		expedientInfo.setProcessInstanceId(expedient.getProcessInstanceId());
		expedientInfo.setAmbRetroaccio(expedient.isAmbRetroaccio());
		return expedientInfo;
	}

	@Override
	public DocumentInfo getDocumentInfo(String documentCodi) {
		return getDocumentInfo(documentCodi, false);
	}

	@Override
	public DocumentInfo getDocumentInfo(String documentCodi, boolean ambCongingut) {
		return documentInfo(expedient, processIdDocuments(), taskId, documentCodi, ambCongingut);
	}

	/** Informació d'un document d'una instància de procés de qualsevol expedient. */
	private DocumentInfo documentInfo(
			Expedient expedient,
			String processId,
			String taskId,
			String documentCodi,
			boolean ambCongingut) {
		
		DocumentStore documentStore = expedientDocumentHelper.findDocumentStore(
			expedient.getId(),
			processId,
			taskId,
			documentCodi);
		
		if (documentStore != null) {
			Document documentDisseny = documentHelperV3.findDocumentDisseny(
					expedient.getId(), 
					processId, 
					documentCodi);
			DocumentDto docV3 = documentHelperV3.toDocumentDto(
				documentStore.getId(),
				ambCongingut && !documentStore.isSignat(),
				ambCongingut && documentStore.isSignat(),
				false,
				false,
				true,
				false);
			DocumentInfo resposta = new DocumentInfo();
			resposta.setProcessInstanceId(processId);
			resposta.setArxiuNom(documentStore.getArxiuNom());
			if(documentDisseny.getDefinicioProces() != null)
				resposta.setProcessInstanceTitol(documentDisseny.getDefinicioProces().getEtiqueta());
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
				resposta.setArxiuContingut(docV3.getSignatContingut());
			} else {
				resposta.setArxiuContingut(docV3.getArxiuContingut());
			}
			resposta.setRegistrat(documentStore.isRegistrat());
			resposta.setArxiuUuid(docV3.getArxiuUuid());

			if (documentStore.isRegistrat()) {
				resposta.setRegistreNumero(documentStore.getRegistreNumero());
				resposta.setRegistreData(documentStore.getRegistreData());
				resposta.setRegistreOficinaCodi(documentStore.getRegistreOficinaCodi());
				resposta.setRegistreOficinaNom(documentStore.getRegistreOficinaNom());
				resposta.setRegistreEntrada(documentStore.isRegistreEntrada());
			}
			return resposta;
		} else {
			throw new HeliumHandlerException("Document store no trobat pel codi: " + documentCodi);
		}
	}

	@Override
	public DocumentInfo setDocument(
		String documentCodi,
		String arxiuNom,
		byte[] arxiuContingut,
		Date dataDocument,
		boolean ambFirma,
		boolean firmaSeparada,
		byte[] firmaContingut) {
		DocumentStore documentStore = documentHelperV3.crearActualitzarDocument(
			taskId,
			processIdDocuments(),
			documentCodi,
			dataDocument,
			arxiuNom,
			arxiuContingut,
			null,
			ambFirma,
			firmaSeparada,
			firmaContingut,
			null,
			null,
			null,
			null);

		DocumentInfo resposta = new DocumentInfo();
		resposta.setProcessInstanceId(processIdDocuments());
		resposta.setId(documentStore.getId());
		resposta.setCodiDocument(documentCodi);
		resposta.setTitol(documentStore.getArxiuNom() != null? documentStore.getArxiuNom() : documentStore.getAdjuntTitol());
		resposta.setDataCreacio(documentStore.getDataCreacio());
		resposta.setDataDocument(documentStore.getDataDocument());
		resposta.setSignat(documentStore.isSignat());
		resposta.setRegistrat(documentStore.isRegistrat());
		resposta.setArxiuUuid(documentStore.getArxiuUuid());

		if (documentStore.isRegistrat()) {
			resposta.setRegistreNumero(documentStore.getRegistreNumero());
			resposta.setRegistreData(documentStore.getRegistreData());
			resposta.setRegistreOficinaCodi(documentStore.getRegistreOficinaCodi());
			resposta.setRegistreOficinaNom(documentStore.getRegistreOficinaNom());
			resposta.setRegistreEntrada(documentStore.isRegistreEntrada());
		}
		return resposta;
	}

	@Override
	public void deleteDocument(String documentCodi) {
		documentHelperV3.esborrarDocument(
				taskId, 
				processIdDocuments(), 
				documentCodi);
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
	public void documentAdjuntar(
		String documentOrigen,
		String titol,
		Date data,
		boolean concatenarTitol,
		boolean esborrarDocument) {
		if (documentOrigen == null) {
			throw new HeliumHandlerException("No s'ha especificat el document orígen");
		}
		if (titol == null) {
			throw new HeliumHandlerException("No s'ha especificat el títol del document");
		}
		if (data == null) {
			throw new HeliumHandlerException("No s'ha especificat la data del document");
		}
		DocumentInfo documentInfo = getDocumentInfo(documentOrigen, true);
		String adjuntTitol = concatenarTitol ? documentInfo.getTitol() + " " + titol : titol;
		documentHelperV3.crearDocument(
				taskId,
				processIdDocuments(),
				null, //documentCodi
				data,
				true, //isAdjunt
				adjuntTitol,
				documentInfo.getArxiuNom(),
				documentInfo.getArxiuContingut(),
				null,
				null,
				null,
				null);
		if (esborrarDocument) {
			documentHelperV3.esborrarDocument(
					taskId, 
					processIdDocuments(), 
					documentOrigen);
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
		Estat estat = expedientHelper.expedientEstatModificar(expedient, codi);
		if (estat == null) {
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
			expedient.getUnitatOrganitzativa() != null? expedient.getUnitatOrganitzativa().getCodi() : null,
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
			expedient.getUnitatOrganitzativa() != null? expedient.getUnitatOrganitzativa().getCodi() : null,
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
			expedient.getUnitatOrganitzativa() != null? expedient.getUnitatOrganitzativa().getCodi() : null,
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
			expedient.getUnitatOrganitzativa() != null? expedient.getUnitatOrganitzativa().getCodi() : null,
			false);
	}

	@Override
	public void interessatCrear(
		String codi,
		String nom,
		String tipusDocIdent,
		String documentIdent,
		String dir3Codi,
		String llinatge1,
		String llinatge2,
		String tipus,
		String email,
		String telefon,
		Boolean entregaPostal,
		String entregaTipus,
		String linia1,
		String linia2,
		Boolean entregaDeh,
		Boolean entregaDehObligat,
		String direccio,
		String pais,
		String provincia,
		String municipi,
		String canalNotif,
		String codiPostal) {
		InteressatDto interessat = new InteressatDto();
		interessat.setCodi(codi);
		interessat.setNom(nom);
		interessat.setTipusDocIdent(
			tipusDocIdent != null ? InteressatDocumentTipusEnumDto.valueOf(tipusDocIdent) : null);
		interessat.setDocumentIdent(documentIdent);
		interessat.setDir3Codi(dir3Codi);
		interessat.setLlinatge1(llinatge1);
		interessat.setLlinatge2(llinatge2);
		interessat.setTipus(
			tipus != null ? InteressatTipusEnumDto.valueOf(tipus) : null);
		interessat.setEmail(email);
		interessat.setTelefon(telefon);
		interessat.setEntregaPostal(entregaPostal);
		interessat.setEntregaTipus(
			entregaTipus != null ? DadesEnviamentDto.EntregaPostalTipus.valueOf(entregaTipus) : null);
		interessat.setLinia1(linia1);
		interessat.setLinia2(linia2);
		interessat.setEntregaDeh(entregaDeh);
		interessat.setEntregaDehObligat(entregaDehObligat);
		interessat.setDireccio(direccio);
		interessat.setPais(pais);
		interessat.setProvincia(provincia);
		interessat.setMunicipi(municipi);
		interessat.setCanalNotif(canalNotif);
		interessat.setCodiPostal(codiPostal);
		expedientInteressatHelper.create(expedient, interessat);
	}

	@Override
	public void interessatModificar(
		String codi,
		String nom,
		String tipusDocIdent,
		String documentIdent,
		String dir3Codi,
		String llinatge1,
		String llinatge2,
		String tipus,
		String email,
		String telefon,
		Boolean entregaPostal,
		String entregaTipus,
		String linia1,
		String linia2,
		Boolean entregaDeh,
		Boolean entregaDehObligat,
		String direccio,
		String pais,
		String provincia,
		String municipi,
		String canalNotif,
		String codiPostal) {
		Interessat interessat = expedientInteressatHelper.findByExpedientAndCodi(expedient, codi);

		if(interessat == null)
			throw new HeliumHandlerException("No s'ha trobat cap interessat amb el codi " + codi);

		InteressatDto interessatUpdate = new InteressatDto();
		interessatUpdate.setId(interessat.getId());
		interessatUpdate.setCodi(codi);
		interessatUpdate.setNom(nom);
		interessatUpdate.setTipusDocIdent(
			tipusDocIdent != null ? InteressatDocumentTipusEnumDto.valueOf(tipusDocIdent) : null);
		interessatUpdate.setDocumentIdent(documentIdent);
		interessatUpdate.setDir3Codi(dir3Codi);
		interessatUpdate.setLlinatge1(llinatge1);
		interessatUpdate.setLlinatge2(llinatge2);
		interessatUpdate.setTipus(
			tipus != null ? InteressatTipusEnumDto.valueOf(tipus) : null);
		interessatUpdate.setEmail(email);
		interessatUpdate.setTelefon(telefon);
		interessatUpdate.setEntregaPostal(entregaPostal);
		interessatUpdate.setEntregaTipus(
			entregaTipus != null ? DadesEnviamentDto.EntregaPostalTipus.valueOf(entregaTipus) : null);
		interessatUpdate.setLinia1(linia1);
		interessatUpdate.setLinia2(linia2);
		interessatUpdate.setEntregaDeh(entregaDeh);
		interessatUpdate.setEntregaDehObligat(entregaDehObligat);
		interessatUpdate.setDireccio(direccio);
		interessatUpdate.setPais(pais);
		interessatUpdate.setProvincia(provincia);
		interessatUpdate.setMunicipi(municipi);
		interessatUpdate.setCanalNotif(canalNotif);
		interessatUpdate.setCodiPostal(codiPostal);
		expedientInteressatHelper.update(interessatUpdate);
	}

	@Override
	public void interessatEliminar(String codi) {
		Interessat interessat = expedientInteressatHelper.findByExpedientAndCodi(expedient, codi);
		if (interessat != null) {
			expedientInteressatHelper.delete(expedient, interessat.getId());
		} else {
			throw new HeliumHandlerException("No s'ha trobat cap interessat amb el codi " + codi);
		}
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
		// Es desa l'execució del handler: continua fins a la receive task que hi ha després i el callback la reprèn
		// per transicioOK/transicioKO, com el token de jBPM que s'aturava al node d'estat. Fora del motor (accions)
		// no hi ha cap execució per reprendre.
		ExecucioHandler execucio = ExecucioHandler.actual();
		String executionId = execucio != null ? execucio.getExecutionId() : null;
		String processInstanceId = execucio != null ? execucio.getProcessInstanceId() : null;
		return pluginHelper.portasignaturesEnviar(
			toPortafirmesDocumentDto(documentCodi),
			annexos,
			blocList,
			expedient,
			importancia,
			dataLimit,
			executionId,
			processInstanceId,
			transicioOK,
			transicioKO,
			PortafirmesSimpleTipusEnumDto.SERIE,
			portafirmesFluxId,
			PortafirmesTipusEnumDto.SIMPLE);
	}

	public void notificacioCrear(long expedientId, DadesNotificacio dadesNotificacio) {

		DadesNotificacioDto notificacio = ConversioTipusHelper.toDadesNotificacioDto(expedientId, dadesNotificacio);

		RespostaEnviar respostaPlugin = pluginHelper.altaNotificacio(this.expedient, notificacio);
		if (respostaPlugin.isError()) {
			throw new SistemaExternException(
				expedient.getEntorn().getId(),
				expedient.getEntorn().getCodi(),
				expedient.getEntorn().getNom(),
				expedient.getId(),
				expedient.getTitol(),
				expedient.getNumero(),
				expedient.getTipus().getId(),
				expedient.getTipus().getCodi(),
				expedient.getTipus().getNom(),
				"(Registre data de justificant)",
				respostaPlugin.getErrorDescripcio());
		}
	}

	@Override
	public void enviarEmail(
		List<String> recipients,
		List<String> ccRecipients,
		List<String> bccRecipients,
		String subject,
		String text,
		List<String> attachments) {
		try {
			mailHelper.send(
				recipients,
				ccRecipients,
				bccRecipients,
				subject,
				text,
				attachments != null ?
					attachments.stream().map(this::toEmailAttachment).collect(Collectors.toList()) :
					null);
		} catch (Exception ex) {
			throw new HeliumHandlerException("No s'ha pogut enviar el correu", ex);
		}
	}

	@Override
	public WProcessInstance getProcessInstance() {
		return processId != null ? serveis.getWorkflowEngineApi().getProcessInstance(processId) : null;
	}

	@Override
	public WTaskInstance getTaskInstance() {
		return taskId != null ? serveis.getWorkflowEngineApi().getTaskById(taskId) : null;
	}

	@Override
	public void expedientGeorefModificar(Double posx, Double posy, String referencia) {
		expedientHelper.update(
			expedient,
			expedient.getNumero(),
			expedient.getTitol(),
			expedient.getResponsableCodi(),
			expedient.getDataInici(),
			expedient.getComentari(),
			(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
			posx,
			posy,
			referencia,
			expedient.getGrupCodi(),
			expedient.getUnitatOrganitzativa() != null? expedient.getUnitatOrganitzativa().getCodi() : null,
			false);
	}

	@Override
	public void expedientReprendre() {
		expedientHelper.reprendre(expedient, null);
	}

	@Override
	public void procesTitolModificar(String titol) {
		serveis.getWorkflowEngineApi().setProcessInstanceTitol(processIdObligatori(), titol);
	}

	@Override
	public void documentEscriureProcesPare(String documentCodi) {
		comprovarCodiDocument(documentCodi);
		String processPareId = processPareId();
		DocumentStore documentStore = expedientDocumentHelper.findDocumentStore(
			expedient.getId(),
			processId,
			null,
			documentCodi);
		if (documentStore == null) {
			// Com a Helium 3: si el subprocés no té el document no es fa res
			return;
		}
		expedientDocumentHelper.setDocument(expedient.getId(), processPareId, null, documentStore);
	}

	@Override
	public void documentLlegirProcesPare(String documentCodi) {
		comprovarCodiDocument(documentCodi);
		String processPareId = processPareId();
		DocumentStore documentStore = expedientDocumentHelper.findDocumentStore(
			expedient.getId(),
			processPareId,
			null,
			documentCodi);
		if (documentStore == null) {
			throw new HeliumHandlerException("No s'ha trobat el document " + documentCodi + " al procés pare (id=" + processPareId + ")");
		}
		expedientDocumentHelper.setDocument(expedient.getId(), processIdObligatori(), null, documentStore);
	}

	@Override
	public void documentCopiarAExpedient(
			String documentCodi,
			String expedientTipusCodi,
			String expedientNumero,
			String documentDestiCodi) {
		comprovarCodiDocument(documentCodi);
		DocumentInfo document = getDocumentInfo(documentCodi, true);
		Expedient desti = findExpedientEntorn(expedientTipusCodi, expedientNumero);
		guardarCopiaDocument(
			desti.getProcessInstanceId(),
			documentDestiCodi != null ? documentDestiCodi : documentCodi,
			document);
	}

	@Override
	public void documentCopiarDesDExpedient(
			String expedientTipusCodi,
			String expedientNumero,
			String documentOrigenCodi,
			String documentCodi) {
		comprovarCodiDocument(documentCodi);
		Expedient origen = findExpedientEntorn(expedientTipusCodi, expedientNumero);
		DocumentInfo document = documentInfo(
			origen,
			origen.getProcessInstanceId(),
			null,
			documentOrigenCodi != null ? documentOrigenCodi : documentCodi,
			true);
		guardarCopiaDocument(processIdDocuments(), documentCodi, document);
	}

	@Override
	public DocumentInfo documentGenerar(String documentCodi, Date data) {
		comprovarCodiDocument(documentCodi);
		Document document = documentHelperV3.findDocumentDisseny(expedient.getId(), processIdDocuments(), documentCodi);
		if (document == null) {
			throw new HeliumHandlerException("No s'ha trobat el document " + documentCodi);
		}
		if (!document.isPlantilla()) {
			throw new HeliumHandlerException("El document " + documentCodi + " no té plantilla");
		}
		Date dataDocument = data != null ? data : new Date();
		ArxiuDto generat = documentHelperV3.generarDocumentAmbPlantillaIConvertir(
			expedient,
			document,
			taskId,
			processIdDocuments(),
			dataDocument);
		return setDocument(
			documentCodi,
			generat.getNom(),
			generat.getContingut(),
			dataDocument,
			false,
			false,
			null);
	}

	@Override
	public RespostaRegistre registreSortida(DadesRegistreSortida dades) {
		comprovarCodiDocument(dades.getDocumentCodi());
		DocumentInfo document = getDocumentInfo(dades.getDocumentCodi(), true);
		String usuariCodi = dades.getUsuariCodi() != null ? dades.getUsuariCodi() : usuariActual();

		RegistreAnotacio anotacio = new RegistreAnotacio();
		anotacio.setExpedientNumero(expedient.getNumero());
		anotacio.setEntitatCodi(dades.getEntitatCodi());
		anotacio.setOrgan(dades.getOrganOrigen());
		anotacio.setOficinaCodi(dades.getOficinaCodi());
		anotacio.setLlibreCodi(dades.getLlibreCodi());
		anotacio.setExtracte(dades.getExtracte() != null && !dades.getExtracte().isEmpty()
			? dades.getExtracte()
			: extracteRegistre(document));
		anotacio.setDocumentacioFisicaCodi(dades.getDocumentacioFisicaCodi());
		anotacio.setAssumpteTipusCodi(dades.getAssumpteTipusCodi());
		anotacio.setAssumpteCodi(dades.getAssumpteCodi());
		anotacio.setUsuariCodi(usuariCodi);
		anotacio.setIdiomaCodi(dades.getIdiomaCodi() != null ? dades.getIdiomaCodi() : "ca");
		RegistreInteressat interessat = toRegistreInteressat(dades.getInteressat());
		RegistreInteressat representant = toRegistreInteressat(dades.getRepresentant());
		interessat.setRepresentant(representant.getTipus() != null ? representant : null);
		anotacio.setInteressats(new ArrayList<>(List.of(interessat)));

		RegistreAnnex annex = new RegistreAnnex();
		annex.setTitol(document.getTitol());
		annex.setDataCaptura(document.getDataDocument());
		annex.setFitxerNom(document.getArxiuNom());
		annex.setFitxerContingut(document.getArxiuContingut());
		annex.setTipusDocument(dades.getDocumentTipus());
		annex.setTipusDocumental(dades.getDocumentTipusDocumental());
		annex.setOrigen(dades.getDocumentOrigen());
		annex.setFirmaMode(dades.getDocumentModeFirma());
		annex.setObservacions(dades.getDocumentObservacions());
		annex.setValidesa(dades.getDocumentValidesa());
		anotacio.setAnnexos(new ArrayList<>(List.of(annex)));

		RegistreIdDto registreId = pluginHelper.registreAnotacioSortida(anotacio, expedient);
		String entitatRegistre = GlobalProperties.getInstance().getProperty("app.registre.codi.entitat");
		String oficinaNom = pluginHelper.registreOficinaNom(
			registreId.getNumero(),
			usuariCodi,
			entitatRegistre != null ? entitatRegistre : dades.getEntitatCodi(),
			expedient);
		documentHelperV3.guardarDadesRegistre(
			document.getId(),
			registreId.getNumero(),
			registreId.getData(),
			anotacio.getOficinaCodi(),
			oficinaNom,
			false);
		RespostaRegistre resposta = new RespostaRegistre();
		resposta.setNumero(registreId.getNumero());
		resposta.setData(registreId.getData());
		return resposta;
	}

	@Override
	public void consultaPinbal(ConsultaPinbal consulta) {
		// Els serveis amb client propi al plugin; la resta, pel client genèric
		String serveiEspecific = consulta.getServeiCodi() != null
				&& List.of(
					PluginHelper.serveiConsultaDades,
					PluginHelper.serveiVerificacioDades,
					PluginHelper.serveiObligacionsTributaries).contains(consulta.getServeiCodi())
			? consulta.getServeiCodi()
			: null;
		// Consulta asíncrona: es desa l'execució del handler, que espera la resposta a la receive task que hi ha
		// després; quan arriba la resposta es reprèn per transicioOK
		String tokenId = null;
		ExecucioHandler execucio = ExecucioHandler.actual();
		if (consulta.isAsincrona() && execucio != null) {
			tokenId = execucio.getExecutionId();
		}
		serveis.getConsultaPinbalHelper().consultaDesDeHandler(
			expedient,
			processIdDocuments(),
			tokenId,
			consulta,
			serveiEspecific);
	}

	/** Instància de procés del handler. Les operacions sobre processos no es poden fer en expedients per estats. */
	private String processIdObligatori() {
		if (processId == null) {
			throw new HeliumHandlerException("Aquesta operació només es pot fer en un expedient per flux, dins una instància de procés");
		}
		return processId;
	}

	/**
	 * Instància de procés on són els documents. Les accions dels expedients per estats s'executen sense instància de
	 * procés: els seus documents són a l'identificador de procés de l'expedient, com quan es gestionen des de la
	 * pàgina de l'expedient. Les dades, en canvi, són de l'expedient ({@code processId} null).
	 */
	private String processIdDocuments() {
		return processId != null ? processId : expedient.getProcessInstanceId();
	}

	private String processPareId() {
		WProcessInstance processInstance = serveis.getWorkflowEngineApi().getProcessInstance(processIdObligatori());
		if (processInstance == null || processInstance.getParentProcessInstanceId() == null) {
			throw new HeliumHandlerException("El procés no té pare (id=" + processId + ")");
		}
		return processInstance.getParentProcessInstanceId();
	}

	private static void comprovarCodiDocument(String documentCodi) {
		if (documentCodi == null || documentCodi.isEmpty()) {
			throw new HeliumHandlerException("No s'ha especificat cap codi de document");
		}
	}

	/** Expedient del mateix entorn. Si no s'indica el tipus, el de l'expedient actual. */
	private Expedient findExpedientEntorn(String expedientTipusCodi, String expedientNumero) {
		ExpedientTipus tipus = expedientTipusCodi != null
			? serveis.getExpedientTipusRepository().findByEntornAndCodi(expedient.getEntorn(), expedientTipusCodi)
			: expedient.getTipus();
		if (tipus == null) {
			throw new HeliumHandlerException("No s'ha trobat el tipus d'expedient " + expedientTipusCodi);
		}
		Expedient trobat = serveis.getExpedientRepository().findByTipusAndNumero(tipus, expedientNumero);
		if (trobat == null) {
			throw new HeliumHandlerException("No s'ha trobat l'expedient (expedientTipusCodi=" + tipus.getCodi() + ", expedientNumero=" + expedientNumero + ")");
		}
		return trobat;
	}

	/** Desa una còpia del document (contingut i dades de registre) a la instància de procés indicada. */
	private void guardarCopiaDocument(String processInstanceId, String documentCodi, DocumentInfo document) {
		DocumentStore copia = documentHelperV3.crearActualitzarDocument(
			null,
			processInstanceId,
			documentCodi,
			document.getDataDocument(),
			document.getArxiuNom(),
			document.getArxiuContingut(),
			null,
			document.isSignat(),
			false,
			null,
			null,
			null,
			null,
			null);
		if (document.isRegistrat()) {
			documentHelperV3.guardarDadesRegistre(
				copia.getId(),
				document.getRegistreNumero(),
				document.getRegistreData(),
				document.getRegistreOficinaCodi(),
				document.getRegistreOficinaNom(),
				document.isRegistreEntrada());
		}
	}

	/** Extracte per defecte, com a Helium 3: "[número] títol: document". */
	private String extracteRegistre(DocumentInfo document) {
		StringBuilder extracte = new StringBuilder();
		if (expedient.getNumero() != null && !expedient.getNumero().isEmpty()) {
			extracte.append("[").append(expedient.getNumero()).append("]");
		}
		if (expedient.getTitol() != null && !expedient.getTitol().isEmpty()) {
			if (extracte.length() > 0) {
				extracte.append(" ");
			}
			extracte.append(StringUtilsHelium.abreuja(expedient.getTitol(), 50));
		}
		if (extracte.length() > 0) {
			extracte.append(": ");
		}
		extracte.append(document.getTitol() != null ? document.getTitol() : document.getCodiDocument());
		return StringUtilsHelium.abreuja(extracte.toString(), 250);
	}

	private static RegistreInteressat toRegistreInteressat(DadesRegistreInteressat dades) {
		RegistreInteressat interessat = new RegistreInteressat();
		if (dades == null) {
			return interessat;
		}
		interessat.setTipus(dades.getTipus() != null ? RegistreInteressatTipusEnum.valueOf(dades.getTipus()) : null);
		interessat.setDocumentTipus(dades.getDocumentTipus() != null ? RegistreInteressatDocumentTipusEnum.valueOf(dades.getDocumentTipus()) : null);
		interessat.setDocumentNum(dades.getDocumentNum());
		interessat.setEmail(dades.getEmail());
		interessat.setTelefon(dades.getTelefon());
		interessat.setNom(dades.getNom());
		interessat.setLlinatge1(dades.getLlinatge1());
		interessat.setLlinatge2(dades.getLlinatge2());
		interessat.setRaoSocial(dades.getRaoSocial());
		interessat.setPais(dades.getPais());
		interessat.setProvincia(dades.getProvincia());
		interessat.setMunicipi(dades.getMunicipi());
		interessat.setCodiPostal(dades.getCodiPostal());
		interessat.setAdresa(dades.getAdresa());
		return interessat;
	}

	private String usuariActual() {
		org.springframework.security.core.Authentication auth =
			org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
		return auth != null ? auth.getName() : expedient.getResponsableCodi();
	}

	@Override
	public TerminiInfo getTerminiIniciatAmbProcessInstanceITerminiCodi(String processInstanceId, String codi) {
		return null;
	}

	@Override
	public ExpedientTipusInfo getExpedientTipusInfo() {
		ExpedientTipusInfo info = new ExpedientTipusInfo();
		ExpedientTipus expedientTipus = expedient.getTipus();
		info.setId(expedientTipus.getId());
		info.setCodi(expedientTipus.getCodi());
		info.setNom(expedientTipus.getNom());
		info.setBpmProcessDefinitionKey(expedientTipus.getBpmProcessDefinitionKey());
		info.setTeNumero(expedientTipus.getTeNumero());
		info.setTeTitol(expedientTipus.getTeTitol());
		info.setDemanaNumero(expedientTipus.getDemanaNumero());
		info.setDemanaTitol(expedientTipus.getDemanaTitol());
		info.setExpressioNumero(expedientTipus.getExpressioNumero());
		info.setSequencia(expedientTipus.getSequencia());
		info.setSequenciaDefault(expedientTipus.getSequenciaDef());
		info.setReiniciarCadaAny(expedientTipus.isReiniciarCadaAny());
		info.setAnyActual(expedientTipus.getAnyActual());
		info.setResponsableDefecteCodi(expedientTipus.getResponsableDefecteCodi());
		info.setRestringirPerGrup(expedientTipus.isRestringirPerGrup());
		info.setTramitacioMassiva(expedientTipus.isTramitacioMassiva());
		info.setSeleccionarAny(expedientTipus.isSeleccionarAny());
		info.setAmbRetroaccio(expedientTipus.isAmbRetroaccio());
		info.setTipus(expedientTipus.getTipus().name());
		info.setAmbInfoPropia(expedientTipus.isAmbInfoPropia());
		info.setHeretable(expedientTipus.isHeretable());
		info.setAmbHerencia(expedientTipus.isAmbHerencia());
		info.setDiesNoLaborables(expedientTipus.getDiesNoLaborables());
		info.setNotibActiu(expedientTipus.getNotibActiu());
		info.setNotibEmisor(expedientTipus.getNotibEmisor());
		info.setNotibCodiProcediment(expedientTipus.getNotibCodiProcediment());
		info.setNotificacionsActivades(expedientTipus.isNotificacionsActivades());
		info.setNotificacioOrganCodi(expedientTipus.getNotificacioOrganCodi());
		info.setNotificacioOficinaCodi(expedientTipus.getNotificacioOficinaCodi());
		info.setNotificacioUnitatAdministrativa(expedientTipus.getNotificacioUnitatAdministrativa());
		info.setNotificacioCodiProcediment(expedientTipus.getNotificacioCodiProcediment());
		info.setNotificacioAvisTitol(expedientTipus.getNotificacioAvisTitol());
		info.setNotificacioAvisText(expedientTipus.getNotificacioAvisText());
		info.setNotificacioAvisTextSms(expedientTipus.getNotificacioAvisTextSms());
		info.setNotificacioOficiTitol(expedientTipus.getNotificacioOficiTitol());
		info.setNotificacioOficiText(expedientTipus.getNotificacioOficiText());
		info.setEnviarCorreuAnotacions(expedientTipus.isEnviarCorreuAnotacions());
		return info;
	}

	private DocumentDto toPortafirmesDocumentDto(String documentCodi) {
		DocumentDto document = new DocumentDto();
		Document documentDisseny = documentHelperV3.findDocumentDisseny(
			expedient.getId(),
			processIdDocuments(),
			documentCodi);
		DocumentStore documentStore = expedientDocumentHelper.findDocumentStore(
			expedient.getId(),
			processIdDocuments(),
			taskId,
			documentCodi);

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

	private ArxiuDto toEmailAttachment(String documentCodi) {
		DocumentStore documentStore = expedientDocumentHelper.findDocumentStore(
			expedient.getId(),
			processIdDocuments(),
			taskId,
			documentCodi);
		if (documentStore != null) {
			return documentHelperV3.getArxiuPerDocumentStoreId(
				documentStore.getId(),
				false,
				false,
				true,
				null);
		} else {
			throw new HeliumHandlerException("Document store no trobat pel codi: " + documentCodi);
		}
	}

}
