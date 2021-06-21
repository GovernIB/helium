package es.caib.helium.integracio.service.portafirmes;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.persones.Persona;
import es.caib.helium.integracio.domini.portafirmes.DocumentDto;
import es.caib.helium.integracio.domini.portafirmes.DocumentPortaFirmes;
import es.caib.helium.integracio.domini.portafirmes.PasSignatura;
import es.caib.helium.integracio.domini.portafirmes.PersonaDto;
import es.caib.helium.integracio.domini.portafirmes.PortaFirma;
import es.caib.helium.integracio.domini.portafirmes.PortaFirmesFlux;
import es.caib.helium.integracio.enums.portafirmes.TipusEstat;
import es.caib.helium.integracio.excepcions.persones.PersonaException;
import es.caib.helium.integracio.excepcions.portafirmes.PortaFirmesException;
import es.caib.helium.integracio.service.persones.PersonaService;
import es.caib.helium.integracio.utils.portafirmes.OpenOfficeUtils;
import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.enums.TipusAccio;
import es.caib.helium.jms.events.IntegracioEvent;
import es.caib.portafib.ws.api.v1.AnnexBean;
import es.caib.portafib.ws.api.v1.BlocDeFirmesWs;
import es.caib.portafib.ws.api.v1.FirmaBean;
import es.caib.portafib.ws.api.v1.FitxerBean;
import es.caib.portafib.ws.api.v1.FluxDeFirmesWs;
import es.caib.portafib.ws.api.v1.PeticioDeFirmaWs;
import es.caib.portafib.ws.api.v1.PortaFIBPeticioDeFirmaWs;
import es.caib.portafib.ws.api.v1.PortaFIBUsuariEntitatWs;
import es.caib.portafib.ws.api.v1.TipusDocumentInfoWs;
import es.caib.portafib.ws.api.v1.WsI18NException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.ImportanceEnum;

@Slf4j
@Service
public class PortaFirmaServicePortaFibImpl extends PortaFirmesServiceImpl {
	
	@Autowired
	private PersonaService personaService;
	
	// Llargada màxima acceptada pel WS del Portasignatures pel camp del remitent.
	private static final int PORTASIGNATURES_REMITENT_MAX_LENGTH = 100;
	@Setter
	private String usuariId;
	@Setter
	private PortaFIBPeticioDeFirmaWs peticioApi;
	@Setter
	private PortaFIBUsuariEntitatWs usuariEntitatApi;
	@Setter
	private OpenOfficeUtils openOfficeUtils;
	
	@Override
	public boolean cancelarEnviament(List<Long> documents, Long entornId) throws PortaFirmesException {
		
		var descripcio = "Cancel·lació d'enviaments de documents";
		var t0 = System.currentTimeMillis();
		try {
			if (documents == null || documents.isEmpty()) {
				return false;
			}
			for (var documentId : documents) {
				try {
					peticioApi.deletePeticioDeFirma(documentId);
					var portaFirma = portaFirmesRepository.getByDocumentId(documentId);
					if (portaFirma == null) {
						continue;
					}
					portaFirma.setEstat(TipusEstat.CANCELAT);
					portaFirmesRepository.save(portaFirma); //TODO a Helium 3.2 nomes actualitza l'estat no ho guarda a bdd?
				} catch (Exception ex) {
					throw new PortaFirmesException("No s'ha pogut esborrar el document del portafirmes (id=" + documentId + ")", ex);
				}
			}
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.PFIRMA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			log.info("Enviaments cancelats");
			return true;
		} catch (Exception ex) {
			var error = "Error esborrant els documents del portafirmes";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PFIRMA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new PortaFirmesException(error, ex);
		}
	}

	@Override
	public boolean enviarPortaFirmes(PortaFirmesFlux document) throws PortaFirmesException {

		var t0 = System.currentTimeMillis();
		var descripcio = "Enviant document al portafirmes";
		try {
			var docPortaFirmes = getDocumentPortasignatures(document.getDocument(), document.getExpedientIdentificador());
			var anexos = getAnnexosPortasignatures(document.getAnexos(), document.getExpedientIdentificador());
			var passesSignatura = getPassesSignatura(
					getSignatariIdPerPersona(document.getPersona()), 
					document.getPersonesPas1(), document.getMinSignatarisPas1(), 
					document.getPersonesPas2(), document.getMinSignatarisPas2(),
					document.getPersonesPas3(), document.getMinSignatarisPas3());
			var remitentNom = getRemitentNom(document.getExpedientIdentificador(), document.getCodiUsuari(), document.getEntornId());
		
			var resposta = uploadDocument(docPortaFirmes, anexos, false, passesSignatura, remitentNom,	 document.getImportancia(), document.getDataLimit());
			var firma = new PortaFirma();
			firma.setDocumentId(resposta);
			firma.setTokenId(document.getTokenId());
			firma.setDataEnviat(Calendar.getInstance().getTime());
			firma.setEstat(TipusEstat.PENDENT);
			firma.setDocumentStoreId(document.getDocument().getId());
			firma.setTransicioOK(document.getTransicioOK());
			firma.setTransicioKO(document.getTransicioKO());
			firma.setExpedientId(document.getExpedientId());
			firma.setProcessInstanceId(document.getProcessInstanceId().toString());
			
			guardar(firma, document.getEntornId());
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.PFIRMA)
					.entornId(document.getEntornId())
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			return true;
		} catch (Exception ex) {
			
			var error = "Error al enviar document al portafirmes ";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PFIRMA) 
					.entornId(document.getEntornId()) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			
			throw new PortaFirmesException(error, ex);
		}
	}
	
	private Long uploadDocument(
			DocumentPortaFirmes document, 
			List<DocumentPortaFirmes> annexos,
			boolean isSignarAnnexos,
			PasSignatura[] passesSignatura,
			String remitent,
			String importancia, 
			Date dataLimit
			) throws PortaFirmesException {
		
		try {
			var requestPeticioDeFirmaWs = new PeticioDeFirmaWs();
			requestPeticioDeFirmaWs.setTitol(
					StringUtils.abbreviate(document.getTitol(), 100));
			/*requestPeticioDeFirmaWs.setDescripcio(
					document.getDescripcio());*/
			requestPeticioDeFirmaWs.setMotiu("Firma de document");
			if (document.getTipus() != null) {
				requestPeticioDeFirmaWs.setTipusDocumentID(
						Long.valueOf(document.getTipus().intValue()));
			} else {
				throw new PortaFirmesException("El tipus de document not pot estar buit." + " Els tipus permesos son: " + getDocumentTipusStr());
			}
			requestPeticioDeFirmaWs.setRemitentNom(remitent);
			requestPeticioDeFirmaWs.setDescripcio(document.getDescripcio());
			if (importancia != null) {
				ImportanceEnum importance = ImportanceEnum.fromString(importancia);
				if (ImportanceEnum.low.equals(importance)) {
					requestPeticioDeFirmaWs.setPrioritatID(0);
				} else if (ImportanceEnum.normal.equals(importance)) {
					requestPeticioDeFirmaWs.setPrioritatID(5);
				} else if (ImportanceEnum.high.equals(importance)) {
					requestPeticioDeFirmaWs.setPrioritatID(9);
				}
			}
			if (dataLimit != null) {
				GregorianCalendar gcal = new GregorianCalendar();
				gcal.setTime(dataLimit);
				DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
				requestPeticioDeFirmaWs.setDataCaducitat(
						new java.sql.Timestamp(dataLimit.getTime()));
			} else {
				GregorianCalendar gcal = new GregorianCalendar();
				gcal.add(Calendar.DAY_OF_MONTH, 5);
				DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
				requestPeticioDeFirmaWs.setDataCaducitat(
						new java.sql.Timestamp(gcal.getTime().getTime()));
			}
			requestPeticioDeFirmaWs.setFluxDeFirmes(toFluxDeFirmes(Arrays.asList(passesSignatura),	null));
			requestPeticioDeFirmaWs.setFitxerAFirmar(toFitxerBean(document));
			
			requestPeticioDeFirmaWs.setModeDeFirma(false);
			requestPeticioDeFirmaWs.setIdiomaID("ca");

			// Afegeix els annexos
			if (annexos != null) {
				AnnexBean a;
				FitxerBean f;
				for (var annex: annexos) {
					a = new AnnexBean();
					a.setAdjuntar(false);
					a.setFirmar(isSignarAnnexos);
					f = new FitxerBean();
					f.setNom(annex.getArxiuNom());
					f.setDescripcio(annex.getTitol());
					f.setMime(openOfficeUtils.getArxiuMimeType(annex.getArxiuNom()));
					f.setData(annex.getArxiuContingut());
					f.setTamany(annex.getArxiuContingut().length);
					a.setFitxer(f);
					requestPeticioDeFirmaWs.getAnnexs().add(a);
				}
			}

			PeticioDeFirmaWs responsePeticioDeFirmaWs = peticioApi.createAndStartPeticioDeFirma(
					requestPeticioDeFirmaWs);
			return Long.valueOf(responsePeticioDeFirmaWs.getPeticioDeFirmaID());
		} catch (Exception ex) {
			if (ex instanceof PortaFirmesException) {
				throw (PortaFirmesException)ex;
			} else {
				throw new PortaFirmesException("Error al enviar el document al portasignatures", ex);
			}
		}
	}
	
	private FitxerBean toFitxerBean(DocumentPortaFirmes document) throws Exception {
		
		String arxiuExtensio = getDocumentArxiuExtensio(document.getArxiuNom());
		if (!"pdf".equalsIgnoreCase(arxiuExtensio)) {
			throw new PortaFirmesException(
					"Els arxius per firmar han de ser de tipus PDF");
		}
		FitxerBean fitxer = new FitxerBean();
		fitxer.setNom(document.getArxiuNom());
		fitxer.setMime("application/pdf");
		fitxer.setTamany(document.getArxiuContingut().length);
		fitxer.setData(document.getArxiuContingut());
		return fitxer;
	}
	
	private String getDocumentArxiuExtensio(String arxiuNom) {
		
		int index = arxiuNom.lastIndexOf("."); 
		return index != -1 ? arxiuNom.substring(index + 1) : "" ;
	}

	private FluxDeFirmesWs toFluxDeFirmes(List<PasSignatura> passes, String plantillaFluxId) throws Exception {
		
		if (passes == null && plantillaFluxId == null) {
			throw new PortaFirmesException("És necessari configurar algun responsable de firmar el document");
		}
		
		FluxDeFirmesWs fluxWs;
		if (plantillaFluxId != null && passes == null) {
			return  peticioApi.instantiatePlantillaFluxDeFirmes(Long.valueOf(plantillaFluxId));
		} 
		
		fluxWs = new FluxDeFirmesWs();
		fluxWs.setNom("Flux Helium " + System.nanoTime());

		PasSignatura pas;
	    BlocDeFirmesWs bloc;
	    String signatari;
	    String usuariEntitat;
		for(int i = 0; i < passes.size(); i++) {
			pas = passes.get(i);
			bloc = new BlocDeFirmesWs();
		    bloc.setMinimDeFirmes(pas.getMinSignataris());
		    bloc.setOrdre(i);
		    for (int j = 0; j<pas.getSignataris().length; j++) {
		    	signatari = pas.getSignataris()[j];
		    	// Cercar usuariEntitat associat al nif
		    	usuariEntitat = usuariEntitatApi.getUsuariEntitatIDInMyEntitatByAdministrationID(signatari);
			    FirmaBean firma = new FirmaBean();
			    firma.setDestinatariID(usuariEntitat);
			    bloc.getFirmes().add(firma);
		    }
		    fluxWs.getBlocsDeFirmes().add(bloc);
		}
		return fluxWs;
	}

	
	private String getDocumentTipusStr() throws MalformedURLException, WsI18NException {
		
		StringBuilder sb = new StringBuilder();
		List<TipusDocumentInfoWs> tipusLlistat = peticioApi.getTipusDeDocuments("ca");
		for (TipusDocumentInfoWs tipusDocumentWs: tipusLlistat) {
			sb.append("[tipusId=");
			sb.append(tipusDocumentWs.getTipusDocumentID());
			sb.append(", nom=");
			sb.append(tipusDocumentWs.getNom());
			sb.append("]");
		}
		return sb.toString();
	}
	
	private String getRemitentNom(String expedient, String codiUsuari, Long entornId) {
		
		String remitent = "Helium. " + expedient;
		try {
			if (codiUsuari != null) {
				PersonaDto persona = this.personaFindAmbCodi(codiUsuari, entornId);
				if (persona != null && persona.getNomSencer() != null && !"".equals(persona.getNomSencer())) {
					remitent = persona.getNomSencer();
				} else {
					remitent = codiUsuari;
				}
			}
		} catch (Exception e) {
			log.error("Error consultant el nom per l'usuari actual:" + e.getMessage(), e);
		}
		return StringUtils.abbreviate(remitent, PORTASIGNATURES_REMITENT_MAX_LENGTH);
	}
	
	private PersonaDto personaFindAmbCodi(String codi, Long entornId) throws PortaFirmesException {

		// TODO FALTA IMPLEMENTAR LA CACHE A PERSONES !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//		Cache personaCache = cacheManager.getCache(CACHE_PERSONA_ID);
//		if (personaCache.get(codi) != null) {
//			return (PersonaDto) personaCache.get(codi).get();
//		}
		
		try {
			Persona dadesPersona = personaService.getPersonaByCodi(codi, entornId);
//			monitorIntegracioHelper.addAccioOk(MonitorIntegracioHelper.INTCODI_PERSONA,
//					"Consulta d'usuari amb codi", IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0, new IntegracioParametreDto("codi", codi));
			if (dadesPersona == null) {
//				throw new NoTrobatException(DadesPersona.class, codi);
				return null;
			}
			PersonaDto dto = new PersonaDto(dadesPersona);
//				personaCache.put(codi, dto); // TODO FALTA IMPLEMENTAR CACHE DE PERSONES!!!!!!!!!
			return dto;
		} catch (PersonaException ex) {
//			monitorIntegracioHelper.addAccioError(MonitorIntegracioHelper.INTCODI_PERSONA,
//					"Consulta d'usuari amb codi", IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0, "El plugin ha retornat una excepció", ex,
//					new IntegracioParametreDto("codi", codi));
			log.error("No s'han pogut consultar persones amb el codi (codi=" + codi + ")", ex);
			throw new PortaFirmesException("No s'han pogut consultar persones amb el codi (codi=" + codi + ")", ex);
		}
	}
	
	private PasSignatura[] getPassesSignatura(String signatariId, List<PersonaDto> personesPas1, int minSignatarisPas1,
			List<PersonaDto> personesPas2, int minSignatarisPas2, List<PersonaDto> personesPas3,
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
		} 
		
		if (signatariId != null) {
			PasSignatura[] passes = new PasSignatura[1];
			PasSignatura pas = new PasSignatura();
			pas.setMinSignataris(1);
			pas.setSignataris(new String[] { signatariId });
			passes[0] = pas;
			return passes;
		} 
		
		PasSignatura[] passes = new PasSignatura[0];
		return passes;
	}

	
	private List<String> getSignatariIdsPerPersones(List<PersonaDto> persones) {
		
		List<String> signatariIds = new ArrayList<String>();
		for (PersonaDto persona : persones) {
			String signatariId = getSignatariIdPerPersona(persona);
			if (signatariId != null) {
				signatariIds.add(signatariId);
			}
		}
		return signatariIds;
	}
	
	private String getSignatariIdPerPersona(PersonaDto persona) {
		
		if (persona == null) {
			return null;
		}
//		String signatariId = persona.getDni();
//		if (isIdUsuariPerCodi()) {
//			signatariId = persona.getCodi();
//		}
//		if (isIdUsuariPerDni()) {
//			signatariId = persona.getDni();
//		}
		return usuariId != null && usuariId.equals("codi") ? persona.getCodi() : persona.getDni();
	}
	
	private List<DocumentPortaFirmes> getAnnexosPortasignatures(List<DocumentDto> annexos, String expedientIdentificador) {

		if (annexos == null || annexos.isEmpty()) {
			return null;
		}
		List<DocumentPortaFirmes> resposta = new ArrayList<DocumentPortaFirmes>();
		for (var document : annexos) {
			resposta.add(getDocumentPortasignatures(document, expedientIdentificador));
		}
		return resposta;
	}
	
	private DocumentPortaFirmes getDocumentPortasignatures(DocumentDto document, String expedientIdentificador) {
		
		var documentPs = new DocumentPortaFirmes();
		// Llargada màxima pel títol 255. Abreuja l'identificador de l'expedient a 90 i
		// tot plegat a 255
		documentPs.setTitol(StringUtils.abbreviate(
				StringUtils.abbreviate(expedientIdentificador, 90) + ": " + document.getDocumentNom(), 254));
		documentPs.setArxiuNom(document.getVistaNom());
		documentPs.setArxiuContingut(document.getVistaContingut());
		documentPs.setTipus(document.getTipusDocPortasignatures());
		documentPs.setSignat(document.isSignat());
		documentPs.setReference(document.getId().toString());
		// Llargada màxima per la descripciól 255. Abreuja l'identificador de
		// l'expedient a 90 i tot plegat a 255
		documentPs.setDescripcio(StringUtils.abbreviate(String.format("Document \"%s\" de l'expedient \"%s\"",
				document.getDocumentNom(), StringUtils.abbreviate(expedientIdentificador, 90)), 254));
		log.debug("Afegit document portafirmes (" + "arxiuNom=" + document.getVistaNom() + ", " + "arxiuContingut="
				+ document.getVistaContingut().length + ", " + "tipus=" + document.getTipusDocPortasignatures() + ", "
				+ "signat=" + document.isSignat() + ")");
		return documentPs;
	}

}
