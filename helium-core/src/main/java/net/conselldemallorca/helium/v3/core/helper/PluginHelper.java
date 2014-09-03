/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.custodia.CustodiaPlugin;
import net.conselldemallorca.helium.integracio.plugins.gesdoc.GestioDocumentalPlugin;
import net.conselldemallorca.helium.integracio.plugins.persones.DadesPersona;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPlugin;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.DocumentPortasignatures;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PasSignatura;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PortasignaturesPlugin;
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
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreSortida;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.TramitSubsanacio;
import net.conselldemallorca.helium.integracio.plugins.registre.TramitSubsanacioParametre;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DocumentTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.Event;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.Signatura;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPlugin;
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
import net.conselldemallorca.helium.v3.core.repository.PortasignaturesRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * Helper per a accedir a la funcionalitat dels plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component("pluginHelperV3")
public class PluginHelper {

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
	
	@Resource
	private PortasignaturesRepository portasignaturesRepository;



	public PersonaDto findPersonaAmbCodi(String codi) throws Exception {
		DadesPersona dadesPersona = getPersonesPlugin().findAmbCodi(codi);
		return conversioTipusHelper.convertir(
				dadesPersona,
				PersonaDto.class);
	}
	public boolean isPersonesPluginActiu() {
		try {
			return getPersonesPlugin() != null;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el plugin de dades de persones", ex);
			return false;
		}
	}
	public boolean isPersonesSyncActiu() {
		String syncActiu = GlobalProperties.getInstance().getProperty("app.persones.plugin.sync.actiu");
		return "true".equalsIgnoreCase(syncActiu);
	}

	public void zonaperExpedientCrear(
			ExpedientDto expedient,
			ZonaperExpedientDto dadesExpedient) throws Exception {
		PublicarExpedientRequest request = conversioTipusHelper.convertir(
				dadesExpedient,
				PublicarExpedientRequest.class);
		
		if (request.getRepresentatNif() == null || request.getRepresentatNif().isEmpty())
			request.setRepresentatNif(expedient.getInteressatNif());
		if (request.getRepresentatNom() == null || request.getRepresentatNom().isEmpty()) {
			request.setRepresentatNom(expedient.getInteressatNom());
		}
		getTramitacioPlugin().publicarExpedient(request);
	}
	public void zonaperEventCrear(
			Expedient expedient,
			ZonaperEventDto dadesEvent) throws Exception {
		if (expedient.getTramitExpedientIdentificador() == null || expedient.getTramitExpedientClau() == null)
			throw new Exception("Abans s'ha de crear un expedient per a poder publicar un event");
		PublicarEventRequest request = new PublicarEventRequest();
		request.setExpedientIdentificador(
				expedient.getTramitExpedientIdentificador());
		request.setExpedientClau(
				expedient.getTramitExpedientClau());
		request.setUnitatAdministrativa(
				expedient.getUnitatAdministrativa());
		request.setEvent(
				conversioTipusHelper.convertir(
						dadesEvent,
						Event.class));
		
		if (expedient.getRepresentantNif() == null || expedient.getRepresentantNif().isEmpty())
			request.setRepresentantNif(expedient.getInteressatNif());
		if (expedient.getRepresentantNom() == null || expedient.getRepresentantNom().isEmpty()) {
			request.setRepresentatNom(expedient.getInteressatNom());
		}
		
		getTramitacioPlugin().publicarEvent(request);
	}
	public TramitDto obtenirDadesTramit(
			String numero,
			String clau) throws Exception {
		ObtenirDadesTramitRequest request = new ObtenirDadesTramitRequest();
		request.setNumero(numero);
		request.setClau(clau);
		DadesTramit dadesTramit = getTramitacioPlugin().obtenirDadesTramit(request);
		return toTramitDto(dadesTramit);
	}

	public boolean isRegistrePluginActiu() {
		try {
			return getRegistrePlugin() != null;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el plugin de registre", ex);
			return false;
		}
	}
	public RegistreIdDto registreAnotacioEntrada(
			RegistreAnotacioDto anotacio) throws Exception {
		RespostaAnotacioRegistre resposta = getRegistrePlugin().registrarEntrada(
				toRegistreEntrada(anotacio));
		if (resposta.getErrorCodi() != null) {
			throw new Exception("Error al registrar document d'entrada (codi=" + resposta.getErrorCodi() + ", descripcio=" + resposta.getErrorDescripcio() + ")");
		} else {
			RegistreIdDto registreId = new RegistreIdDto();
			registreId.setNumero(resposta.getNumero());
			registreId.setData(resposta.getData());
			return registreId;
		}
	}
	public RegistreIdDto registreAnotacioSortida(
			RegistreAnotacioDto anotacio) throws Exception {
		RespostaAnotacioRegistre resposta = getRegistrePlugin().registrarSortida(
				toRegistreSortida(anotacio));
		if (resposta.getErrorCodi() != null) {
			throw new Exception("Error al registrar document de sortida (codi=" + resposta.getErrorCodi() + ", descripcio=" + resposta.getErrorDescripcio() + ")");
		} else {
			RegistreIdDto registreId = new RegistreIdDto();
			registreId.setNumero(resposta.getNumero());
			registreId.setData(resposta.getData());
			return registreId;
		}
	}
	public RegistreIdDto registreNotificacio(
			RegistreNotificacioDto notificacio) throws Exception {
		RespostaAnotacioRegistre resposta = getRegistrePlugin().registrarNotificacio(
				toRegistreNotificacio(notificacio));
		if (resposta.getErrorCodi() != null) {
			throw new Exception("Error al notificar via registre (codi=" + resposta.getErrorCodi() + ", descripcio=" + resposta.getErrorDescripcio() + ")");
		} else {
			RegistreIdDto registreId = new RegistreIdDto();
			registreId.setNumero(resposta.getNumero());
			registreId.setData(resposta.getData());
			return registreId;
		}
	}
	public Date registreDataJustificantRecepcio(
			String numeroRegistre) throws Exception {
		RespostaJustificantRecepcio resposta = getRegistrePlugin().obtenirJustificantRecepcio(numeroRegistre);
		return resposta.getData();
	}
	public String registreOficinaNom(
			String codi) throws Exception {
		return getRegistrePlugin().obtenirNomOficina(codi);
	}

	public byte[] gestioDocumentalObtenirDocument(String id) throws Exception {
		return getGestioDocumentalPlugin().retrieveDocument(id);
	}

	public Integer portasignaturesEnviarDocument(
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
			Date dataLimit) throws Exception {
		return getPortasignaturesPlugin().uploadDocument(
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
	}

	public byte[] custodiaObtenirSignaturesAmbArxiu(
			String documentId) throws Exception {
		return getCustodiaPlugin().getSignaturesAmbArxiu(documentId);
	}

	public String custodiaObtenirUrlComprovacioSignatura(
			String documentId) throws Exception {
		return getCustodiaPlugin().getUrlComprovacioSignatura(documentId);
	}


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
	private boolean isIdUsuariPerDni() {
		return "dni".equalsIgnoreCase(GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.usuari.id"));
	}
	private boolean isIdUsuariPerCodi() {
		return "codi".equalsIgnoreCase(GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.usuari.id"));
	}

	private PersonesPlugin getPersonesPlugin() throws Exception {
		if (!personesPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.persones.plugin.class");
			if (pluginClass != null) {
				Class<?> clazz = Class.forName(pluginClass);
				personesPlugin = (PersonesPlugin)clazz.newInstance();
			}
			personesPluginEvaluat = true;
		}
		return personesPlugin;
	}
	private TramitacioPlugin getTramitacioPlugin() throws Exception {
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
					Class<?> clazz = Class.forName(pluginClass);
					tramitacioPlugin = (TramitacioPlugin)clazz.newInstance();
			}
		}
		return tramitacioPlugin;
	}
	private RegistrePlugin getRegistrePlugin() throws Exception {
		if (!registrePluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.registre.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
					Class<?> clazz = Class.forName(pluginClass);
					registrePlugin = (RegistrePlugin)clazz.newInstance();
			}
		}
		return registrePlugin;
	}
	private GestioDocumentalPlugin getGestioDocumentalPlugin() throws Exception {
		if (!gestioDocumentalPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
					Class<?> clazz = Class.forName(pluginClass);
					gestioDocumentalPlugin = (GestioDocumentalPlugin)clazz.newInstance();
			}
		}
		return gestioDocumentalPlugin;
	}

	public List<Portasignatures> findPendentsPortasignaturesPerProcessInstanceId(String processInstanceId) {		
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
	}
	
	private PortasignaturesPlugin getPortasignaturesPlugin() throws Exception {
		if (!portasignaturesPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.class");
			if ((pluginClass != null) && (pluginClass.length() > 0)) {
				Class<?> clazz = Class.forName(pluginClass);
				portasignaturesPlugin = (PortasignaturesPlugin)clazz.newInstance();
			}
		}
		return portasignaturesPlugin;
	}
	private CustodiaPlugin getCustodiaPlugin() throws Exception {
		if (!custodiaPluginEvaluat) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.custodia.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
					Class<?> clazz = Class.forName(pluginClass);
					custodiaPlugin = (CustodiaPlugin)clazz.newInstance();
			}
		}
		return custodiaPlugin;
	}

	private static final Log logger = LogFactory.getLog(PluginHelper.class);
}
