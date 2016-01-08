/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.custodia.CustodiaPlugin;
import net.conselldemallorca.helium.integracio.plugins.custodia.CustodiaPluginException;
import net.conselldemallorca.helium.integracio.plugins.gesdoc.GestioDocumentalPlugin;
import net.conselldemallorca.helium.integracio.plugins.gesdoc.GestioDocumentalPluginException;
import net.conselldemallorca.helium.integracio.plugins.persones.DadesPersona;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPlugin;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPluginException;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.DocumentPortasignatures;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PasSignatura;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PortasignaturesPlugin;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PortasignaturesPluginException;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesAssumpte;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesExpedient;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesInteressat;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesOficina;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesRepresentat;
import net.conselldemallorca.helium.integracio.plugins.registre.DocumentRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreEntrada;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistrePlugin;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistrePluginException;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreSortida;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantDetallRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.TramitSubsanacio;
import net.conselldemallorca.helium.integracio.plugins.registre.TramitSubsanacioParametre;
import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;
import net.conselldemallorca.helium.integracio.plugins.signatura.SignaturaPlugin;
import net.conselldemallorca.helium.integracio.plugins.signatura.SignaturaPluginException;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.Event;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.Signatura;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPlugin;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginException;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreAnnexDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreAnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreIdDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreNotificacioDto.RegistreNotificacioTramitSubsanacioParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDocumentDto.TramitDocumentSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDto;
import net.conselldemallorca.helium.v3.core.api.dto.ZonaperEventDto;
import net.conselldemallorca.helium.v3.core.api.dto.ZonaperExpedientDto;
import net.conselldemallorca.helium.v3.core.api.exception.PluginException;
import net.conselldemallorca.helium.v3.core.repository.PortasignaturesRepository;

/**
 * Helper per a accedir a la funcionalitat dels plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component("pluginHelperV3")
public class PluginHelper {

	@Resource
	private PortasignaturesRepository portasignaturesRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;

	private PersonesPlugin personesPlugin;
	private boolean personesPluginEvaluat = false;

	private TramitacioPlugin tramitacioPlugin;
	private boolean tramitacioPluginEvaluat = false;

	private RegistrePlugin registrePlugin;
	private boolean registrePluginEvaluat = false;

	private GestioDocumentalPlugin gestioDocumentalPlugin;
	private boolean gestioDocumentalPluginEvaluat = false;

	private PortasignaturesPlugin portasignaturesPlugin;
	private boolean portasignaturesPluginEvaluat = false;

	private CustodiaPlugin custodiaPlugin;
	private boolean custodiaPluginEvaluat = false;

	private SignaturaPlugin signaturaPlugin;
	private boolean signaturaPluginEvaluat = false;



	public List<PersonaDto> personaFindLikeNomSencer(String text) {
		try {
			List<DadesPersona> persones = getPersonesPlugin().findLikeNomSencer(text);
			if (persones == null)
				return new ArrayList<PersonaDto>();
			return conversioTipusHelper.convertirList(persones, PersonaDto.class);
		} catch (PersonesPluginException ex) {
			logger.error(
					"No s'han pogut consultar persones amb el text (text=" + text + ")",
					ex);
			throw new PluginException(
					"No s'han pogut consultar persones amb el text (text=" + text + ")",
					ex);
		}
	}

	public PersonaDto personaFindAmbCodi(String codi) {
		try {
			DadesPersona dadesPersona = getPersonesPlugin().findAmbCodi(codi);
			return conversioTipusHelper.convertir(
					dadesPersona,
					PersonaDto.class);
		} catch (PersonesPluginException ex) {
			logger.error(
					"No s'han pogut consultar persones amb el codi (codi=" + codi + ")",
					ex);
			throw new PluginException(
					"No s'han pogut consultar persones amb el codi (codi=" + codi + ")",
					ex);
		}
	}

	public boolean personaIsPluginActiu() {
		return getPersonesPlugin() != null;
	}

	public boolean personaIsSyncActiu() {
		String syncActiu = GlobalProperties.getInstance().getProperty("app.persones.plugin.sync.actiu");
		return "true".equalsIgnoreCase(syncActiu);
	}

	public void tramitacioZonaperExpedientCrear(
			ExpedientDto expedient,
			ZonaperExpedientDto dadesExpedient) throws Exception {
		PublicarExpedientRequest request = conversioTipusHelper.convertir(
				dadesExpedient,
				PublicarExpedientRequest.class);
		getTramitacioPlugin().publicarExpedient(request);
	}
	public void tramitacioZonaperEventCrear(
			Expedient expedient,
			ZonaperEventDto dadesEvent) throws Exception {
		if (expedient.getTramitExpedientIdentificador() == null || expedient.getTramitExpedientClau() == null)
			throw new Exception("Abans s'ha de crear un expedient per a poder publicar un event");
		PublicarEventRequest request = new PublicarEventRequest();
		request.setExpedientIdentificador(expedient.getTramitExpedientIdentificador());
		request.setExpedientClau(expedient.getTramitExpedientClau());
		request.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
		request.setExpedientIdentificador(expedient.getTramitExpedientIdentificador());
		request.setRepresentatNif(expedient.getInteressatNif());
		request.setRepresentatNom(expedient.getInteressatNom());	
		request.setEvent(
				conversioTipusHelper.convertir(
						dadesEvent,
						Event.class));		
		getTramitacioPlugin().publicarEvent(request);
	}
	public TramitDto tramitacioObtenirDadesTramit(
			String numero,
			String clau) {
		try {
			ObtenirDadesTramitRequest request = new ObtenirDadesTramitRequest();
			request.setNumero(numero);
			request.setClau(clau);
			DadesTramit dadesTramit = getTramitacioPlugin().obtenirDadesTramit(request);
			return toTramitDto(dadesTramit);
		} catch (TramitacioPluginException ex) {
			logger.error(
					"No s'ha pogut obtenir la informació del tràmit (" +
					"numero=" + numero + ", " +
					"clau=" + clau + ")",
					ex);
			throw new PluginException(
					"No s'ha pogut obtenir la informació del tràmit (" +
					"numero=" + numero + ", " +
					"clau=" + clau + ")",
					ex);
		}
	}

	public RespostaAnotacioRegistre tramitacioRegistrarNotificacio(
			RegistreNotificacio registreNotificacio) {
		try {
			return getTramitacioPlugin().registrarNotificacio(registreNotificacio);
		} catch (TramitacioPluginException ex) {
			logger.error(
					"No s'han pogut registrar la notificació (" +
					"expedientIdentificador=" + registreNotificacio.getDadesExpedient().getIdentificador() + ", " +
					"expedientClau=" + registreNotificacio.getDadesExpedient().getClau() + ", " +
					"oficinaOrganCodi=" + registreNotificacio.getDadesOficina().getOrganCodi() + ", " +
					"oficinaCodi=" + registreNotificacio.getDadesOficina().getOficinaCodi() + ")",
					ex);
			throw new PluginException(
					"No s'han pogut registrar la notificació (" +
					"expedientIdentificador=" + registreNotificacio.getDadesExpedient().getIdentificador() + ", " +
					"expedientClau=" + registreNotificacio.getDadesExpedient().getClau() + ", " +
					"oficinaOrganCodi=" + registreNotificacio.getDadesOficina().getOrganCodi() + ", " +
					"oficinaCodi=" + registreNotificacio.getDadesOficina().getOficinaCodi() + ")",
					ex);
		}
	}

	public RespostaJustificantRecepcio tramitacioObtenirJustificant(
			String registreNumero) {
		try {
			return getTramitacioPlugin().obtenirJustificantRecepcio(registreNumero);
		} catch (TramitacioPluginException ex) {
			logger.error(
					"No s'han pogut obtenir el justificant de recepció (" +
					"registreNumero=" + registreNumero + ")",
					ex);
			throw new PluginException(
					"No s'han pogut obtenir el justificant de recepció (" +
					"registreNumero=" + registreNumero + ")",
					ex);
		}
	}

	public RespostaJustificantDetallRecepcio tramitacioObtenirJustificantDetall(
			String registreNumero) {
		try {
			return getTramitacioPlugin().obtenirJustificantDetallRecepcio(registreNumero);
		} catch (TramitacioPluginException ex) {
			logger.error(
					"No s'han pogut obtenir el detall del justificant de recepció (" +
					"registreNumero=" + registreNumero + ")",
					ex);
			throw new PluginException(
					"No s'han pogut obtenir el detall del justificant de recepció (" +
					"registreNumero=" + registreNumero + ")",
					ex);
		}
	}	

	public RegistreIdDto registreAnotacioEntrada(
			RegistreAnotacioDto anotacio) throws PluginException {
		try {
			RespostaAnotacioRegistre resposta = getRegistrePlugin().registrarEntrada(
					toRegistreEntrada(anotacio));
			if (!resposta.isOk()) {
				throw new PluginException(
						"No s'han pogut registrar l'entrada (" +
						getDescripcioErrorRegistre(anotacio) +
						"errorCodi=" + resposta.getErrorCodi() + ", " +
						"errorDescripcio=" + resposta.getErrorDescripcio() + ")");
			} else {
				RegistreIdDto registreId = new RegistreIdDto();
				registreId.setNumero(resposta.getNumero());
				registreId.setData(resposta.getData());
				return registreId;
			}
		} catch (RegistrePluginException ex) {
			logger.error(
					"No s'ha pogut registrar l'entrada (" +
					getDescripcioErrorRegistre(anotacio) + ")",
					ex);
			throw new PluginException(
					"No s'ha pogut registrar l'entrada (" +
					getDescripcioErrorRegistre(anotacio) + ")",
					ex);
		}
	}
	public RegistreIdDto registreAnotacioSortida(
			RegistreAnotacioDto anotacio) throws PluginException {
		try {
			RespostaAnotacioRegistre resposta = getRegistrePlugin().registrarSortida(
					toRegistreSortida(anotacio));
			if (!resposta.isOk()) {
				throw new PluginException(
						"No s'han pogut registrar la sortida (" +
						getDescripcioErrorRegistre(anotacio) + ", " +
						"errorCodi=" + resposta.getErrorCodi() + ", " +
						"errorDescripcio=" + resposta.getErrorDescripcio() + ")");
			} else {
				RegistreIdDto registreId = new RegistreIdDto();
				registreId.setNumero(resposta.getNumero());
				registreId.setData(resposta.getData());
				return registreId;
			}
		} catch (RegistrePluginException ex) {
			logger.error(
					"No s'ha pogut registrar la sortida (" +
					getDescripcioErrorRegistre(anotacio) + ")",
					ex);
			throw new PluginException(
					"No s'ha pogut registrar la sortida (" +
					getDescripcioErrorRegistre(anotacio) + ")",
					ex);
		}
	}
	public RegistreIdDto registreNotificacio(
			RegistreNotificacioDto notificacio) throws PluginException {
		try {
			RespostaAnotacioRegistre resposta = getRegistrePlugin().registrarNotificacio(
					toRegistreNotificacio(notificacio));
			if (!resposta.isOk()) {
				throw new PluginException(
						"No s'han pogut registrar la notificació (" +
						getDescripcioErrorRegistre(notificacio) + ", " +
						"errorCodi=" + resposta.getErrorCodi() + ", " +
						"errorDescripcio=" + resposta.getErrorDescripcio() + ")");
			} else {
				RegistreIdDto registreId = new RegistreIdDto();
				registreId.setNumero(resposta.getNumero());
				registreId.setData(resposta.getData());
				return registreId;
			}
		} catch (RegistrePluginException ex) {
			logger.error(
					"No s'ha pogut registrar la notificació (" +
						getDescripcioErrorRegistre(notificacio) + ")",
					ex);
			throw new PluginException(
					"No s'ha pogut registrar la notificació (" +
					getDescripcioErrorRegistre(notificacio) + ")",
					ex);
		}
	}
	public Date registreDataJustificantRecepcio(
			String numeroRegistre) {
		try {
			RespostaJustificantRecepcio resposta = getRegistrePlugin().obtenirJustificantRecepcio(numeroRegistre);
			if (!resposta.isOk()) {
				throw new PluginException(
						"No s'han pogut obtenir la data del justificant de la notificació (" +
						"numeroRegistre=" + numeroRegistre + ", " +
						"errorCodi=" + resposta.getErrorCodi() + ", " +
						"errorDescripcio=" + resposta.getErrorDescripcio() + ")");
			} else {
				return resposta.getData();
			}
		} catch (RegistrePluginException ex) {
			logger.error(
					"No s'han pogut obtenir la data del justificant de la notificació (" +
					"numeroRegistre=" + numeroRegistre + ")",
					ex);
			throw new PluginException(
					"No s'han pogut obtenir la data del justificant de la notificació (" +
					"numeroRegistre=" + numeroRegistre + ")",
					ex);
		}
	}
	public String registreOficinaNom(
			String codi) {
		try {
			return getRegistrePlugin().obtenirNomOficina(codi);
		} catch (RegistrePluginException ex) {
			logger.error(
					"No s'ha pogut obtenir el nom de l'oficina de registre (" +
					"codi=" + codi + ")",
					ex);
			throw new PluginException(
					"No s'ha pogut obtenir el nom de l'oficina de registre (" +
					"codi=" + codi + ")",
					ex);
		}
	}
	public boolean registreIsPluginActiu() {
		return getRegistrePlugin() != null;
	}

	public String gestioDocumentalCreateDocument(
			Expedient expedient,
			String documentCodi,
			String documentDescripcio,
			Date documentData,
			String documentArxiuNom,
			byte[] documentArxiuContingut) {
		try {
			String expedientTipus = null;
			if (gestionDocumentalIsTipusExpedientDirecte()) {
				expedientTipus = expedient.getTipus().getCodi();
			} else if (gestionDocumentalIsTipusExpedientNou()) {
				expedientTipus = expedient.getEntorn().getCodi() + "_" + expedient.getTipus().getCodi();
			} else {
				expedientTipus = expedient.getEntorn().getCodi() + "#" + expedient.getTipus().getCodi();
			}
			return getGestioDocumentalPlugin().createDocument(
					expedient.getNumeroIdentificador(),
					expedientTipus,
					documentCodi,
					documentDescripcio,
					documentData,
					documentArxiuNom,
					documentArxiuContingut);
		} catch (GestioDocumentalPluginException ex) {
			logger.error(
					"No s'han pogut guardar el document a la gestió documental (" +
					"expedientIdentificador=" + expedient.getIdentificador() + ", " +
					"documentCodi=" + documentCodi + ", " +
					"documentDescripcio=" + documentDescripcio + ", " +
					"documentData=" + documentData + ", " +
					"documentArxiuNom=" + documentArxiuNom + ")",
					ex);
			throw new PluginException(
					"No s'han pogut guardar el document a la gestió documental (" +
					"expedientIdentificador=" + expedient.getIdentificador() + ", " +
					"documentCodi=" + documentCodi + ", " +
					"documentDescripcio=" + documentDescripcio + ", " +
					"documentData=" + documentData + ", " +
					"documentArxiuNom=" + documentArxiuNom + ")",
					ex);
		}
	}
	public byte[] gestioDocumentalObtenirDocument(
			String documentId) {
		try {
			return getGestioDocumentalPlugin().retrieveDocument(documentId);
		} catch (GestioDocumentalPluginException ex) {
			logger.error(
					"No s'han pogut llegir el document de la gestió documental (documentId=" + documentId + ")",
					ex);
			throw new PluginException(
					"No s'han pogut llegir el document de la gestió documental (documentId=" + documentId + ")",
					ex);
		}
	}
	public void gestioDocumentalDeleteDocument(
			String documentId) {
		try {
			if (getGestioDocumentalPlugin() != null)
				getGestioDocumentalPlugin().deleteDocument(documentId);
		} catch (GestioDocumentalPluginException ex) {
			logger.error(
					"No s'han pogut esborrar el document de la gestió documental (documentId=" + documentId + ")",
					ex);
			throw new PluginException(
					"No s'han pogut esborrar el document de la gestió documental (documentId=" + documentId + ")",
					ex);
		}
	}
	
	public boolean gestioDocumentalIsPluginActiu() {
		return getGestioDocumentalPlugin() != null;
	}

	public Integer portasignaturesEnviar(
			DocumentDto document,
			List<DocumentDto> annexos,
			PersonaDto persona,
			List<PersonaDto> personesPas1,
			int minSignatarisPas1,
			List<PersonaDto> personesPas2,
			int minSignatarisPas2,
			List<PersonaDto> personesPas3,
			int minSignatarisPas3,
			Expedient expedient,
			String importancia,
			Date dataLimit,
			Long tokenId,
			Long processInstanceId,
			String transicioOK,
			String transicioKO) {
		try {
			Integer resposta = getPortasignaturesPlugin().uploadDocument(
					getDocumentPortasignatures(document, expedient),
					getAnnexosPortasignatures(annexos, expedient),
					false,
					getPassesSignatura(
							getSignatariIdPerPersona(persona),
							personesPas1,
							minSignatarisPas1,
							personesPas2,
							minSignatarisPas2,
							personesPas3,
							minSignatarisPas3),
					expedient.getIdentificador(),
					importancia,
					dataLimit);
			Calendar cal = Calendar.getInstance();
			Portasignatures portasignatures = new Portasignatures();
			portasignatures.setDocumentId(resposta);
			portasignatures.setTokenId(tokenId);
			portasignatures.setDataEnviat(cal.getTime());
			portasignatures.setEstat(TipusEstat.PENDENT);
			portasignatures.setDocumentStoreId(document.getId());
			portasignatures.setTransicioOK(transicioOK);
			portasignatures.setTransicioKO(transicioKO);
			portasignatures.setExpedient(expedient);
			portasignatures.setProcessInstanceId(processInstanceId.toString());
			portasignaturesRepository.save(portasignatures);
			return resposta;
		} catch (PortasignaturesPluginException ex) {
			logger.error(
					"No s'han pogut enviar el document al portafirmes (" +
					"documentId=" + document.getId() + ", " +
					"destinatari=" + persona.getCodi() + ", " +
					"expedient=" + expedient.getIdentificador() + ")",
					ex);
			throw new PluginException(
					"No s'han pogut enviar el document al portafirmes (" +
					"documentId=" + document.getId() + ", " +
					"destinatari=" + persona.getCodi() + ", " +
					"expedient=" + expedient.getIdentificador() + ")",
					ex);
		}
	}

	public void portasignaturesCancelar(
			List<Integer> documentIds) {
		try {
			getPortasignaturesPlugin().deleteDocuments(
					documentIds);
			for (Integer documentId: documentIds) {
				Portasignatures portasignatures = portasignaturesRepository.findByDocumentId(documentId);
				if (portasignatures != null) {
					portasignatures.setEstat(TipusEstat.CANCELAT);
				}
			}
		} catch (PortasignaturesPluginException ex) {
			StringBuilder idsStr = new StringBuilder();
			for (Integer documentId: documentIds) {
				idsStr.append(documentId.toString());
				idsStr.append(" ");
			}
			logger.error(
					"No s'han pogut cancel·lar els enviaments al portafirmes (" +
					"ids=" + idsStr + ")",
					ex);
			throw new PluginException(
					"No s'han pogut cancel·lar els enviaments al portafirmes (" +
							"ids=" + idsStr + ")",
					ex);
		}
	}

	public String custodiaAfegirSignatura(
			Long documentId,
			String gesdocId,
			String nomArxiuSignat,
			String codiTipusCustodia,
			byte[] signatura) {
		try {
			return getCustodiaPlugin().addSignature(
					documentId.toString(),
					gesdocId,
					nomArxiuSignat,
					codiTipusCustodia,
					signatura);
		} catch (CustodiaPluginException ex) {
			logger.error(
					"No s'ha pogut afegir la signatura a la custòdia (" +
					"documentId=" + documentId + ", " +
					"gesdocId=" + gesdocId + ", " +
					"nomArxiuSignat=" + nomArxiuSignat + ", " +
					"codiTipusCustodia=" + codiTipusCustodia + ")",
					ex);
			throw new PluginException(
					"No s'ha pogut afegir la signatura a la custòdia (" +
					"documentId=" + documentId + ", " +
					"gesdocId=" + gesdocId + ", " +
					"nomArxiuSignat=" + nomArxiuSignat + ", " +
					"codiTipusCustodia=" + codiTipusCustodia + ")",
					ex);
		}
	}

	public List<RespostaValidacioSignatura> custodiaDadesValidacioSignatura(
			String documentId) {
		try {
			return getCustodiaPlugin().dadesValidacioSignatura(documentId);
		} catch (CustodiaPluginException ex) {
			logger.error(
					"No s'han pogut obtenir les dades de les signatures de la custòdia (documentId=" + documentId + ")",
					ex);
			throw new PluginException(
					"No s'han pogut obtenir les dades de les signatures de la custòdia (documentId=" + documentId + ")",
					ex);
		}
	}

	public List<byte[]> custodiaObtenirSignatures(
			String documentId) {
		try {
			return getCustodiaPlugin().getSignatures(documentId);
		} catch (CustodiaPluginException ex) {
			logger.error(
					"No s'han pogut obtenir les signatures de la custòdia (documentId=" + documentId + ")",
					ex);
			throw new PluginException(
					"No s'han pogut obtenir les signatures de la custòdia (documentId=" + documentId + ")",
					ex);
		}
	}

	public byte[] custodiaObtenirSignaturesAmbArxiu(
			String documentId) {
		try {
			return getCustodiaPlugin().getSignaturesAmbArxiu(documentId);
		} catch (CustodiaPluginException ex) {
			logger.error(
					"No s'han pogut obtenir les signatures amb arxiu de la custòdia (documentId=" + documentId + ")",
					ex);
			throw new PluginException(
					"No s'han pogut obtenir les signatures amb arxiu de la custòdia (documentId=" + documentId + ")",
					ex);
		}
	}

	public void custodiaEsborrarSignatures(String documentId) {
		try {
			getCustodiaPlugin().deleteSignatures(documentId);
		} catch (CustodiaPluginException ex) {
			logger.error(
					"No s'han pogut esborrar les signatures de la custòdia (documentId=" + documentId + ")",
					ex);
			throw new PluginException(
					"No s'han pogut esborrar les signatures de la custòdia (documentId=" + documentId + ")",
					ex);
		}
	}

	public String custodiaObtenirUrlComprovacioSignatura(
			String documentId) {
		try {
			return getCustodiaPlugin().getUrlComprovacioSignatura(documentId);
		} catch (CustodiaPluginException ex) {
			logger.error(
					"No s'ha pogut obtenir url de comprovació de la custòdia (documentId=" + documentId + ")",
					ex);
			throw new PluginException(
					"No s'ha pogut obtenir url de comprovació la custòdia (documentId=" + documentId + ")",
					ex);
		}
	}

	public boolean custodiaPotObtenirInfoSignatures() {
		return getCustodiaPlugin().potObtenirInfoSignatures();
	}

	public boolean custodiaIsValidacioImplicita() {
		return getCustodiaPlugin().isValidacioImplicita();
	}
	
	public boolean custodiaIsPluginActiu() {
		return getCustodiaPlugin() != null;
	}

	public RespostaValidacioSignatura signaturaVerificar(
			byte[] document,
			byte[] signatura,
			boolean obtenirDadesCertificat) throws PluginException {
		try {
			return getSignaturaPlugin().verificarSignatura(
					document,
					signatura,
					obtenirDadesCertificat);
		} catch (SignaturaPluginException ex) {
			logger.error("No s'han pogut verificar la signatura", ex);
			throw new PluginException("No s'han pogut verificar la signatura", ex);
		}
	}

	/*public List<Portasignatures> findPendentsPortasignaturesPerProcessInstanceId(String processInstanceId) {		
		List<Portasignatures> psignas = portasignaturesRepository.findPendentsPerProcessInstanceId(processInstanceId);
		Iterator<Portasignatures> it = psignas.iterator();
		while (it.hasNext()) {
			Portasignatures psigna = it.next();
			if (	!TipusEstat.PENDENT.equals(psigna.getEstat()) &&
					!TipusEstat.SIGNAT.equals(psigna.getEstat()) &&
					!TipusEstat.REBUTJAT.equals(psigna.getEstat()) &&
					!TipusEstat.ERROR.equals(psigna.getEstat())) {
				it.remove();
			}
		}
		return psignas;
	}
	public void saveOrUpdatePortasignatures(Portasignatures psigna) {
		portasignaturesRepository.save(psigna);
	}*/


	private TramitDto toTramitDto(DadesTramit dadesTramit) {
		TramitDto dto = conversioTipusHelper.convertir(
				dadesTramit,
				TramitDto.class);
		if (dto.getDocuments() != null)
			dto.getDocuments().clear();
		if (dadesTramit.getDocuments() != null) {
			List<TramitDocumentDto> documents = new ArrayList<TramitDocumentDto>();
			for (DocumentTramit dt: dadesTramit.getDocuments()) {
				TramitDocumentDto doc = new TramitDocumentDto();
				doc.setNom(dt.getNom());
				doc.setIdentificador(dt.getIdentificador());
				doc.setInstanciaNumero(dt.getInstanciaNumero());
				if (dt.getDocumentTelematic() != null) {
					doc.setTelematic(true);
					doc.setTelematicArxiuNom(dt.getDocumentTelematic().getArxiuNom());
					doc.setTelematicArxiuExtensio(dt.getDocumentTelematic().getArxiuExtensio());
					doc.setTelematicArxiuContingut(dt.getDocumentTelematic().getArxiuContingut());
					doc.setTelematicReferenciaGestorDocumental(dt.getDocumentTelematic().getReferenciaGestorDocumental());
					doc.setTelematicReferenciaCodi(dt.getDocumentTelematic().getReferenciaCodi());
					doc.setTelematicReferenciaClau(dt.getDocumentTelematic().getReferenciaClau());
					doc.setTelematicEstructurat(dt.getDocumentTelematic().getEstructurat());
					if (dt.getDocumentTelematic().getSignatures() != null) {
						List<TramitDocumentSignaturaDto> telematicSignatures = new ArrayList<TramitDocumentSignaturaDto>();
						for (Signatura s: dt.getDocumentTelematic().getSignatures()) {
							telematicSignatures.add(
									doc.newDocumentSignatura(
											s.getSignatura(),
											s.getFormat()));
						}
						doc.setTelematicSignatures(telematicSignatures);
					}
				}
				if (dt.getDocumentPresencial() != null) {
					doc.setPresencial(true);
					doc.setPresencialTipus(dt.getDocumentPresencial().getTipus());
					doc.setPresencialDocumentCompulsar(dt.getDocumentPresencial().getDocumentCompulsar());
					doc.setPresencialFotocopia(dt.getDocumentPresencial().getFotocopia());
					doc.setPresencialSignatura(dt.getDocumentPresencial().getSignatura());
				}
				documents.add(doc);
			}
			dto.setDocuments(documents);
		}
		return dto;
	}

	private RegistreEntrada toRegistreEntrada(RegistreAnotacioDto anotacio) {
		RegistreEntrada registreEntrada = new RegistreEntrada();
		DadesOficina dadesOficina = new DadesOficina();
		dadesOficina.setOrganCodi(anotacio.getOrganCodi());
		dadesOficina.setOficinaCodi(anotacio.getOficinaCodi());
		registreEntrada.setDadesOficina(dadesOficina);
		DadesInteressat dadesInteressat = new DadesInteressat();
		dadesInteressat.setEntitatCodi(anotacio.getEntitatCodi());
		dadesInteressat.setAutenticat(anotacio.isInteressatAutenticat());
		dadesInteressat.setNif(anotacio.getInteressatNif());
		dadesInteressat.setNomAmbCognoms(anotacio.getInteressatNomAmbCognoms());
		dadesInteressat.setPaisCodi(anotacio.getInteressatPaisCodi());
		dadesInteressat.setPaisNom(anotacio.getInteressatPaisNom());
		dadesInteressat.setProvinciaCodi(anotacio.getInteressatProvinciaCodi());
		dadesInteressat.setProvinciaNom(anotacio.getInteressatProvinciaNom());
		dadesInteressat.setMunicipiCodi(anotacio.getInteressatMunicipiCodi());
		dadesInteressat.setMunicipiNom(anotacio.getInteressatMunicipiNom());
		registreEntrada.setDadesInteressat(dadesInteressat);
		DadesRepresentat dadesRepresentat = new DadesRepresentat();
		dadesRepresentat.setNif(anotacio.getRepresentatNif());
		dadesRepresentat.setNomAmbCognoms(anotacio.getRepresentatNomAmbCognoms());
		registreEntrada.setDadesRepresentat(dadesRepresentat);
		DadesAssumpte dadesAssumpte = new DadesAssumpte();
		dadesAssumpte.setIdiomaCodi(anotacio.getAssumpteIdiomaCodi());
		dadesAssumpte.setTipus(anotacio.getAssumpteTipus());
		dadesAssumpte.setAssumpte(anotacio.getAssumpteExtracte());
		dadesAssumpte.setUnitatAdministrativa(anotacio.getUnitatAdministrativa());
		dadesAssumpte.setRegistreNumero(anotacio.getAssumpteRegistreNumero());
		dadesAssumpte.setRegistreAny(anotacio.getAssumpteRegistreAny());
		registreEntrada.setDadesAssumpte(dadesAssumpte);
		if (anotacio.getAnnexos() != null) {
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			for (RegistreAnnexDto annex: anotacio.getAnnexos()) {
				DocumentRegistre document = new DocumentRegistre();
				document.setNom(annex.getNom());
				document.setData(annex.getData());
				document.setIdiomaCodi(annex.getIdiomaCodi());
				document.setArxiuNom(annex.getArxiuNom());
				document.setArxiuContingut(annex.getArxiuContingut());
				documents.add(document);
			}
			registreEntrada.setDocuments(documents);
		}
		return registreEntrada;
	}
	private RegistreSortida toRegistreSortida(RegistreAnotacioDto anotacio) {
		RegistreSortida registreSortida = new RegistreSortida();
		DadesOficina dadesOficina = new DadesOficina();
		dadesOficina.setOrganCodi(anotacio.getOrganCodi());
		dadesOficina.setOficinaCodi(anotacio.getOficinaCodi());
		registreSortida.setDadesOficina(dadesOficina);
		DadesInteressat dadesInteressat = new DadesInteressat();
		dadesInteressat.setEntitatCodi(anotacio.getEntitatCodi());
		dadesInteressat.setAutenticat(anotacio.isInteressatAutenticat());
		dadesInteressat.setNif(anotacio.getInteressatNif());
		dadesInteressat.setNomAmbCognoms(anotacio.getInteressatNomAmbCognoms());
		dadesInteressat.setPaisCodi(anotacio.getInteressatPaisCodi());
		dadesInteressat.setPaisNom(anotacio.getInteressatPaisNom());
		dadesInteressat.setProvinciaCodi(anotacio.getInteressatProvinciaCodi());
		dadesInteressat.setProvinciaNom(anotacio.getInteressatProvinciaNom());
		dadesInteressat.setMunicipiCodi(anotacio.getInteressatMunicipiCodi());
		dadesInteressat.setMunicipiNom(anotacio.getInteressatMunicipiNom());
		registreSortida.setDadesInteressat(dadesInteressat);
		DadesRepresentat dadesRepresentat = new DadesRepresentat();
		dadesRepresentat.setNif(anotacio.getRepresentatNif());
		dadesRepresentat.setNomAmbCognoms(anotacio.getRepresentatNomAmbCognoms());
		registreSortida.setDadesRepresentat(dadesRepresentat);
		DadesAssumpte dadesAssumpte = new DadesAssumpte();
		dadesAssumpte.setIdiomaCodi(anotacio.getAssumpteIdiomaCodi());
		dadesAssumpte.setTipus(anotacio.getAssumpteTipus());
		dadesAssumpte.setAssumpte(anotacio.getAssumpteExtracte());
		dadesAssumpte.setUnitatAdministrativa(anotacio.getUnitatAdministrativa());
		dadesAssumpte.setRegistreNumero(anotacio.getAssumpteRegistreNumero());
		dadesAssumpte.setRegistreAny(anotacio.getAssumpteRegistreAny());
		registreSortida.setDadesAssumpte(dadesAssumpte);
		if (anotacio.getAnnexos() != null) {
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			for (RegistreAnnexDto annex: anotacio.getAnnexos()) {
				DocumentRegistre document = new DocumentRegistre();
				document.setNom(annex.getNom());
				document.setData(annex.getData());
				document.setIdiomaCodi(annex.getIdiomaCodi());
				document.setArxiuNom(annex.getArxiuNom());
				document.setArxiuContingut(annex.getArxiuContingut());
				documents.add(document);
			}
			registreSortida.setDocuments(documents);
		}
		return registreSortida;
	}
	private RegistreNotificacio toRegistreNotificacio(
			RegistreNotificacioDto notificacio) {
		RegistreNotificacio registreNotificacio = new RegistreNotificacio();
		DadesExpedient dadesExpedient = new DadesExpedient();
		dadesExpedient.setIdentificador(notificacio.getExpedientIdentificador());
		dadesExpedient.setClau(notificacio.getExpedientClau());
		dadesExpedient.setUnitatAdministrativa(notificacio.getExpedientUnitatAdministrativa());
		registreNotificacio.setDadesExpedient(dadesExpedient);
		DadesOficina dadesOficina = new DadesOficina();
		dadesOficina.setOrganCodi(notificacio.getOrganCodi());
		dadesOficina.setOficinaCodi(notificacio.getOficinaCodi());
		registreNotificacio.setDadesOficina(dadesOficina);
		DadesInteressat dadesInteressat = new DadesInteressat();
		dadesInteressat.setEntitatCodi(notificacio.getEntitatCodi());
		dadesInteressat.setAutenticat(notificacio.isInteressatAutenticat());
		dadesInteressat.setNif(notificacio.getInteressatNif());
		dadesInteressat.setNomAmbCognoms(notificacio.getInteressatNomAmbCognoms());
		dadesInteressat.setPaisCodi(notificacio.getInteressatPaisCodi());
		dadesInteressat.setPaisNom(notificacio.getInteressatPaisNom());
		dadesInteressat.setProvinciaCodi(notificacio.getInteressatProvinciaCodi());
		dadesInteressat.setProvinciaNom(notificacio.getInteressatProvinciaNom());
		dadesInteressat.setMunicipiCodi(notificacio.getInteressatMunicipiCodi());
		dadesInteressat.setMunicipiNom(notificacio.getInteressatMunicipiNom());
		registreNotificacio.setDadesInteressat(dadesInteressat);
		DadesRepresentat dadesRepresentat = new DadesRepresentat();
		dadesRepresentat.setNif(notificacio.getRepresentatNif());
		dadesRepresentat.setNomAmbCognoms(notificacio.getRepresentatNomAmbCognoms());
		registreNotificacio.setDadesRepresentat(dadesRepresentat);
		DadesNotificacio dadesNotificacio = new DadesNotificacio();
		dadesNotificacio.setJustificantRecepcio(notificacio.isNotificacioJustificantRecepcio());
		dadesNotificacio.setAvisTitol(notificacio.getNotificacioAvisTitol());
		dadesNotificacio.setAvisText(notificacio.getNotificacioAvisText());
		dadesNotificacio.setAvisTextSms(notificacio.getNotificacioAvisTextSms());
		dadesNotificacio.setOficiTitol(notificacio.getNotificacioOficiTitol());
		dadesNotificacio.setOficiText(notificacio.getNotificacioOficiText());
		dadesNotificacio.setIdiomaCodi(notificacio.getAssumpteIdiomaCodi());
		dadesNotificacio.setTipus(notificacio.getAssumpteTipus());
		dadesNotificacio.setAssumpte(notificacio.getAssumpteExtracte());
		dadesNotificacio.setUnitatAdministrativa(notificacio.getUnitatAdministrativa());
		dadesNotificacio.setRegistreNumero(notificacio.getAssumpteRegistreNumero());
		dadesNotificacio.setRegistreAny(notificacio.getAssumpteRegistreAny());
		if (notificacio.getTramitSubsanacioIdentificador() != null) {
			TramitSubsanacio tramitSubsanacio = new TramitSubsanacio();
			tramitSubsanacio.setIdentificador(notificacio.getTramitSubsanacioIdentificador());
			tramitSubsanacio.setVersio(notificacio.getTramitSubsanacioVersio());
			tramitSubsanacio.setDescripcio(notificacio.getTramitSubsanacioDescripcio());
			if (notificacio.getTramitSubsanacioParametres() != null) {
				List<TramitSubsanacioParametre> parametres = new ArrayList<TramitSubsanacioParametre>();
				for (RegistreNotificacioTramitSubsanacioParametreDto param: notificacio.getTramitSubsanacioParametres()) {
					TramitSubsanacioParametre p = new TramitSubsanacioParametre();
					p.setParametre(param.getParametre());
					p.setValor(param.getValor());
					parametres.add(p);
				}
				tramitSubsanacio.setParametres(parametres);
			}
			dadesNotificacio.setOficiTramitSubsanacio(tramitSubsanacio);
		}
		registreNotificacio.setDadesNotificacio(dadesNotificacio);
		if (notificacio.getAnnexos() != null) {
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			for (RegistreAnnexDto annex: notificacio.getAnnexos()) {
				DocumentRegistre document = new DocumentRegistre();
				document.setNom(annex.getNom());
				document.setData(annex.getData());
				document.setIdiomaCodi(annex.getIdiomaCodi());
				document.setArxiuNom(annex.getArxiuNom());
				document.setArxiuContingut(annex.getArxiuContingut());
				documents.add(document);
			}
			registreNotificacio.setDocuments(documents);
		}
		return registreNotificacio;
	}

	private DocumentPortasignatures getDocumentPortasignatures(
			DocumentDto document,
			Expedient expedient) {
		DocumentPortasignatures documentPs = new DocumentPortasignatures();
		documentPs.setTitol(
				expedient.getIdentificador() + ": " + document.getDocumentNom());
		documentPs.setArxiuNom(document.getVistaNom());
		documentPs.setArxiuContingut(document.getVistaContingut());
		documentPs.setTipus(document.getTipusDocPortasignatures());
		documentPs.setSignat(document.isSignat());
		documentPs.setReference(document.getId().toString());
		return documentPs;
	}
	private List<DocumentPortasignatures> getAnnexosPortasignatures(
			List<DocumentDto> annexos,
			Expedient expedient) {
		if (annexos == null)
			return null;
		List<DocumentPortasignatures> resposta = new ArrayList<DocumentPortasignatures>();
		for (DocumentDto document: annexos)
			resposta.add(getDocumentPortasignatures(document, expedient));
		return resposta;
	}
	private PasSignatura[] getPassesSignatura(
			String signatariId,
			List<PersonaDto> personesPas1,
			int minSignatarisPas1,
			List<PersonaDto> personesPas2,
			int minSignatarisPas2,
			List<PersonaDto> personesPas3,
			int minSignatarisPas3) {
		if (personesPas1 != null && personesPas1.size() > 0) {
			List<PasSignatura> passes = new ArrayList<PasSignatura>();
			PasSignatura pas = new PasSignatura();
			List<String> signataris = getSignatariIdsPerPersones(personesPas1);
			pas.setSignataris(signataris.toArray(new String[signataris.size()]));
			pas.setMinSignataris(minSignatarisPas1);
			passes.add(pas);
			if (personesPas2 != null && personesPas2.size() > 0) {
				pas = new PasSignatura();
				signataris = getSignatariIdsPerPersones(personesPas2);
				pas.setSignataris(signataris.toArray(new String[signataris.size()]));
				pas.setMinSignataris(minSignatarisPas2);
				passes.add(pas);
			}
			if (personesPas3 != null && personesPas3.size() > 0) {
				pas = new PasSignatura();
				signataris = getSignatariIdsPerPersones(personesPas3);
				pas.setSignataris(signataris.toArray(new String[signataris.size()]));
				pas.setMinSignataris(minSignatarisPas3);
				passes.add(pas);
			}
			return passes.toArray(new PasSignatura[passes.size()]);
		} else if (signatariId != null) {
			PasSignatura[] passes = new PasSignatura[1];
			PasSignatura pas = new PasSignatura();
			pas.setMinSignataris(1);
			pas.setSignataris(new String[] {signatariId});
			passes[0] = pas;
			return passes;
		} else {
			PasSignatura[] passes = new PasSignatura[0];
			return passes;
		}
	}
	private List<String> getSignatariIdsPerPersones(List<PersonaDto> persones) {
		List<String> signatariIds = new ArrayList<String>();
		for (PersonaDto persona: persones) {
			String signatariId = getSignatariIdPerPersona(persona);
			if (signatariId != null)
				signatariIds.add(signatariId);
		}
		return signatariIds;
	}
	private String getSignatariIdPerPersona(PersonaDto persona) {
		if (persona == null)
			return null;
		String signatariId = persona.getDni();
		if (isIdUsuariPerCodi())
			signatariId = persona.getCodi();
		if (isIdUsuariPerDni())
			signatariId = persona.getDni();
		return signatariId;
	}

	private String getDescripcioErrorRegistre(
			RegistreAnotacioDto anotacio) {
		return "organCodi=" + anotacio.getOrganCodi() + ", " +
				"oficinaCodi=" + anotacio.getOficinaCodi() + ", " +
				"entitatCodi=" + anotacio.getEntitatCodi() + ", " +
				"unitatAdministrativa=" + anotacio.getUnitatAdministrativa() + ", " +
				"unitatAdministrativa=" + anotacio.getAssumpteExtracte() + ", " +
				"unitatAdministrativa=" + anotacio.getUnitatAdministrativa() + ", " +
				"annexos=" + ((anotacio.getAnnexos() != null) ? anotacio.getAnnexos().size() : 0);
	}

	private boolean isIdUsuariPerDni() {
		return "dni".equalsIgnoreCase(GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.usuari.id"));
	}
	private boolean isIdUsuariPerCodi() {
		return "codi".equalsIgnoreCase(GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.usuari.id"));
	}

	private boolean gestionDocumentalIsTipusExpedientNou() {
		return "true".equalsIgnoreCase(
				GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.tipus.nou"));
	}

	private boolean gestionDocumentalIsTipusExpedientDirecte() {
		return "true".equalsIgnoreCase(
				GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.tipus.directe"));
	}

	private PersonesPlugin getPersonesPlugin() {
		if (!personesPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.persones.plugin.class");
			if (pluginClass != null) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					personesPlugin = (PersonesPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new PluginException(
							"No s'ha pogut instanciar el plugin de persones (class=" + pluginClass + ")",
							ex);
				}
			}
			personesPluginEvaluat = true;
		}
		return personesPlugin;
	}
	private TramitacioPlugin getTramitacioPlugin() {
		if (!tramitacioPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.tramitacio.plugin.class");
			if (pluginClass == null) {
				String bantelUrl = GlobalProperties.getInstance().getProperty("app.bantel.entrades.url");
				if (bantelUrl.contains("v1")) {
					pluginClass = "net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginSistrav1";
				} else {
					pluginClass = "net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginSistrav2";
				}
			}
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					tramitacioPlugin = (TramitacioPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new PluginException(
							"No s'ha pogut instanciar el plugin de tramitació (class=" + pluginClass + ")",
							ex);
				}
			}
		}
		return tramitacioPlugin;
	}
	private RegistrePlugin getRegistrePlugin() {
		if (!registrePluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.registre.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					registrePlugin = (RegistrePlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new PluginException(
							"No s'ha pogut instanciar el plugin de registre (class=" + pluginClass + ")",
							ex);
				}
			}
		}
		return registrePlugin;
	}
	private GestioDocumentalPlugin getGestioDocumentalPlugin() {
		if (!gestioDocumentalPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					gestioDocumentalPlugin = (GestioDocumentalPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new PluginException(
							"No s'ha pogut instanciar el plugin de gestió documental (class=" + pluginClass + ")",
							ex);
				}
			}
		}
		return gestioDocumentalPlugin;
	}

	private PortasignaturesPlugin getPortasignaturesPlugin() {
		if (!portasignaturesPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.class");
			if ((pluginClass != null) && (pluginClass.length() > 0)) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					portasignaturesPlugin = (PortasignaturesPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new PluginException(
							"No s'ha pogut instanciar el plugin de portafirmes (class=" + pluginClass + ")",
							ex);
				}
			}
		}
		return portasignaturesPlugin;
	}
	private CustodiaPlugin getCustodiaPlugin() {
		if (!custodiaPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.custodia.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					custodiaPlugin = (CustodiaPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new PluginException(
							"No s'ha pogut instanciar el plugin de custòdia (class=" + pluginClass + ")",
							ex);
				}
			}
		}
		return custodiaPlugin;
	}
	private SignaturaPlugin getSignaturaPlugin() {
		if (!signaturaPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.signatura.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					signaturaPlugin = (SignaturaPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new PluginException(
							"No s'ha pogut instanciar el plugin de signatura (class=" + pluginClass + ")",
							ex);
				}
			}
		}
		return signaturaPlugin;
	}

	private static final Log logger = LogFactory.getLog(PluginHelper.class);
}
