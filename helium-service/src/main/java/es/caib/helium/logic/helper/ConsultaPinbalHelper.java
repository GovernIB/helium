package es.caib.helium.logic.helper;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.model.ConsultaPinbal;
import es.caib.helium.commons.dto.DadesDocumentDto;
import es.caib.helium.commons.dto.PeticioPinbalEstatEnum;
import es.caib.helium.commons.dto.PersonaDto;
import es.caib.helium.commons.dto.PinbalConsentimentEnum;
import es.caib.helium.commons.dto.ScspAtributosPinbal;
import es.caib.helium.commons.dto.ScspConfirmacioPeticioPinbal;
import es.caib.helium.commons.utils.StringUtilsHelium;
import es.caib.helium.integracio.plugins.pinbal.DadesConsultaPinbal;
import es.caib.helium.integracio.plugins.pinbal.Funcionari;
import es.caib.helium.integracio.plugins.pinbal.Titular;
import es.caib.helium.persistence.entity.DocumentStore;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.entity.Interessat;
import es.caib.helium.persistence.entity.UnitatOrganitzativa;
import es.caib.helium.persistence.repository.UnitatOrganitzativaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import es.caib.helium.commons.dto.ScspRespostaPinbal;
import es.caib.helium.bpmn.engine.WToken;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import es.caib.helium.persistence.entity.PeticioPinbal;
import es.caib.helium.persistence.repository.PeticioPinbalRepository;

@Component
public class ConsultaPinbalHelper {

	@Resource private PeticioPinbalRepository peticioPinbalRepository;
	@Resource private ExceptionHelper exceptionHelper;
	@Resource private DocumentHelperV3 documentHelperV3;
	@Resource private PluginHelper pluginHelper;
	@Resource private WorkflowEngineApi workflowEngineApi;
	@Resource private ExpedientInteressatHelper expedientInteressatHelper;
	@Resource private PlantillaHelper plantillaHelper;
	@Resource private UnitatOrganitzativaRepository unitatOrganitzativaRepository;

	/**
	 * Consulta PINBAL des d'un handler (com {@code Jbpm3HeliumHelper.consultaPinbal} a Helium 3). Desa el justificant
	 * al document indicat (un document provisional si la consulta és asíncrona) i registra la petició a
	 * HEL_PETICIO_PINBAL.
	 *
	 * @param processInstanceId instància de procés on es desa el justificant.
	 * @param tokenId execució que espera la resposta asíncrona (null si no n'hi ha cap).
	 * @param servei servei específic de {@link PluginHelper} o null per a la consulta genèrica
	 *               ({@code consulta.getServeiCodi()}).
	 */
	public void consultaDesDeHandler(
			Expedient expedient,
			String processInstanceId,
			String tokenId,
			ConsultaPinbal consulta,
			String servei) {
		if (consulta.getDocumentCodi() == null) {
			throw new HeliumHandlerException("No s'ha especificat cap codi de document per associar el justificant de la petició PINBAL");
		}
		if (documentHelperV3.findDocumentDisseny(expedient.getId(), processInstanceId, consulta.getDocumentCodi()) == null) {
			throw new HeliumHandlerException("No s'ha trobat el document " + consulta.getDocumentCodi() + " per desar el justificant de la petició PINBAL");
		}
		DadesConsultaPinbal dades = toDadesConsultaPinbal(expedient, consulta);
		Date dataPeticio = new Date();
		String idPeticio;
		Date dataPrevista = null;
		DocumentStore justificant = null;
		if (dades.isAsincrona()) {
			ScspConfirmacioPeticioPinbal confirmacio = (ScspConfirmacioPeticioPinbal) pluginHelper.consultaPinbal(dades, expedient, servei);
			ScspAtributosPinbal atributs = confirmacio != null ? confirmacio.getAtributos() : null;
			if (atributs == null || atributs.getIdPeticion() == null) {
				throw new HeliumHandlerException("PINBAL no ha retornat l'identificador de la petició asíncrona");
			}
			idPeticio = atributs.getIdPeticion();
			if (atributs.getCodigoCertificado() != null) {
				dades.setCodiProcediment(atributs.getCodigoCertificado());
			}
			Calendar cal = Calendar.getInstance();
			if (atributs.getEstado() != null && atributs.getEstado().getTiempoEstimadoRespuesta() != null) {
				cal.add(Calendar.HOUR, atributs.getEstado().getTiempoEstimadoRespuesta());
			}
			dataPrevista = cal.getTime();
			// Fins que arribi la resposta, el document és un justificant provisional
			DadesDocumentDto provisional = plantillaHelper.generaJustificantTemporalPinbal(
					dades.getCodiProcediment(),
					idPeticio,
					dataPrevista);
			justificant = crearJustificant(processInstanceId, dades.getDocumentCodi(), provisional.getArxiuNom(), provisional.getArxiuContingut());
		} else {
			ScspRespostaPinbal resposta = (ScspRespostaPinbal) pluginHelper.consultaPinbal(dades, expedient, servei);
			if (resposta == null) {
				throw new HeliumHandlerException("PINBAL no ha retornat cap resposta a la petició síncrona");
			}
			idPeticio = resposta.getIdPeticion();
			if (resposta.getJustificant() != null) {
				justificant = crearJustificant(
						processInstanceId,
						dades.getDocumentCodi(),
						resposta.getJustificant().getNom(),
						resposta.getJustificant().getContingut());
			}
		}
		PeticioPinbal peticio = new PeticioPinbal();
		peticio.setDataPeticio(dataPeticio);
		peticio.setAsincrona(dades.isAsincrona());
		if (dades.isAsincrona()) {
			peticio.setEstat(PeticioPinbalEstatEnum.PENDENT);
			peticio.setDataPrevista(dataPrevista);
		} else {
			peticio.setEstat(PeticioPinbalEstatEnum.TRAMITADA);
		}
		peticio.setExpedient(expedient);
		peticio.setTipus(expedient.getTipus());
		peticio.setEntorn(expedient.getTipus().getEntorn());
		peticio.setProcediment(dades.getCodiProcediment());
		peticio.setServeiCodi(dades.getServeiCodi());
		peticio.setUsuari(usuariActual(expedient));
		peticio.setPinbalId(idPeticio);
		peticio.setDocument(justificant);
		peticio.setTokenId(tokenId);
		peticio.setTransicioOK(consulta.getTransicioOK());
		peticio.setTransicioKO(consulta.getTransicioKO());
		peticioPinbalRepository.save(peticio);
	}

	/** Sempre un document nou: es conserven els justificants de les peticions successives. */
	private DocumentStore crearJustificant(String processInstanceId, String documentCodi, String arxiuNom, byte[] contingut) {
		return documentHelperV3.crearDocument(
				null,
				processInstanceId,
				documentCodi,
				new Date(),
				false,
				null,
				arxiuNom,
				contingut,
				null,
				null,
				null,
				null);
	}

	private DadesConsultaPinbal toDadesConsultaPinbal(Expedient expedient, ConsultaPinbal consulta) {
		Titular titular = new Titular();
		if (!StringUtilsHelium.isEmpty(consulta.getInteressatCodi())) {
			Interessat interessat = expedientInteressatHelper.findByExpedientAndCodi(expedient, consulta.getInteressatCodi());
			if (interessat == null) {
				throw new HeliumHandlerException("No s'ha trobat cap interessat amb el codi " + consulta.getInteressatCodi());
			}
			titular.setDocumentacion(interessat.getDocumentIdent());
			titular.setNombre(interessat.getNom());
			titular.setApellido1(interessat.getLlinatge1());
			titular.setApellido2(interessat.getLlinatge2());
		} else {
			titular.setDocumentacion(consulta.getTitularDocumentacio());
			titular.setNombre(consulta.getTitularNom());
			titular.setApellido1(consulta.getTitularLlinatge1());
			titular.setApellido2(consulta.getTitularLlinatge2());
			titular.setNombreCompleto(consulta.getTitularNomComplet());
		}
		titular.setTipusDocumentacion(consulta.getTitularTipusDocumentacio());

		Funcionari funcionari = new Funcionari();
		PersonaDto persona = pluginHelper.personaFindAmbCodi(usuariActual(expedient));
		if (persona != null) {
			funcionari.setNifFuncionario(persona.getDni());
			funcionari.setNombreCompletoFuncionario(persona.getNomSencer());
			funcionari.setSeudonimo(persona.getCodi());
		}

		ExpedientTipus tipus = expedient.getTipus();
		DadesConsultaPinbal dades = new DadesConsultaPinbal(
				titular,
				funcionari,
				consulta.getDadesEspecifiques(),
				consulta.getServeiCodi(),
				consulta.getDocumentCodi(),
				consulta.getFinalitat(),
				toConsentiment(consulta.getConsentiment()),
				consulta.getInteressatCodi(),
				tipus.getNtiClasificacion(),
				tipus.getPinbalNifCif(),
				unitatTramitadora(expedient),
				consulta.isAsincrona(),
				consulta.getAnyNaixement());
		return dades;
	}

	private static PinbalConsentimentEnum toConsentiment(String consentiment) {
		if (StringUtilsHelium.isEmpty(consentiment)) {
			return null;
		}
		try {
			return PinbalConsentimentEnum.valueOf(consentiment.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new HeliumHandlerException("Consentiment de PINBAL no vàlid: " + consentiment + " (ha de ser SI o LLEI)");
		}
	}

	/** Denominació (abreujada) de la unitat tramitadora, com a la consulta PINBAL des del formulari de documents. */
	private String unitatTramitadora(Expedient expedient) {
		ExpedientTipus tipus = expedient.getTipus();
		String codiUO = (tipus.isProcedimentComu() && expedient.getUnitatOrganitzativa() != null)
				? expedient.getUnitatOrganitzativa().getCodi()
				: tipus.getNtiOrgano();
		UnitatOrganitzativa uo = codiUO != null ? unitatOrganitzativaRepository.findByCodi(codiUO) : null;
		return uo != null ? StringUtilsHelium.abreuja(uo.getDenominacio(), 64) : codiUO;
	}

	/** Usuari que fa la consulta: l'autenticat o, si el procés s'executa sense usuari (jobs), el responsable. */
	private static String usuariActual(Expedient expedient) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null ? auth.getName() : expedient.getResponsableCodi();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ScspRespostaPinbal tractamentPeticioAsincronaPendentPinbal(Long peticioPinbalId) {

		PeticioPinbal pi = peticioPinbalRepository.findById(peticioPinbalId).orElse(null);
		ScspRespostaPinbal resultat = new ScspRespostaPinbal();

		try {
			// Consulta l'estat
			resultat = pluginHelper.consultaEstatPeticioPinbal(pi.getPinbalId());
			if (PeticioPinbalEstatEnum.TRAMITADA.equals(resultat.getEstatAsincron())) {
				documentHelperV3.crearActualitzarDocument(
						null,
						pi.getDocument().getProcessInstanceId(),
						pi.getDocument().getCodiDocument(),
						Calendar.getInstance().getTime(),
						resultat.getJustificant().getNom(),
						resultat.getJustificant().getContingut(),
						null,
						null,
						null,
						null);

				// Segons el resultat de la resposta avança l'expedient
				if (pi.getTokenId() != null) {
					// Execució del handler de PINBAL: només es reprèn si està aturada a una receive task
					WToken token = workflowEngineApi.getTokenById(pi.getTokenId());
					if (token != null && token.isReceiveTask()) {
						if (pi.getTransicioOK() != null && token.getSortides().contains(pi.getTransicioOK())) {
							workflowEngineApi.signalToken(pi.getTokenId(), pi.getTransicioOK());
						} else {
							workflowEngineApi.signalToken(pi.getTokenId(), null);
						}
					}
				}
			}
		} catch (Exception ex) {
			resultat.setEstatAsincron(PeticioPinbalEstatEnum.ERROR_PROCESSANT);
			String error = ex.getMessage() + ": " + exceptionHelper.getMissageFinalCadenaExcepcions(ex);
			if (error!=null && error.length()>4000) {
				error = error.substring(0, 4000);
			}
			resultat.setErrorProcessament(error);
		}

		//Actualitzam la registre de la BBDD
		pi.setEstat(resultat.getEstatAsincron());
		pi.setErrorProcessament(resultat.getErrorProcessament());
		if (pi.getDataProcessamentPrimer()==null) {
			pi.setDataProcessamentPrimer(Calendar.getInstance().getTime());
		} else {
			pi.setDataProcessamentDarrer(Calendar.getInstance().getTime());
		}

		//Retornam el objecte amb la informació per si es vol utilitzar per mostrar-la per pantalla
		return resultat;
	}
}
