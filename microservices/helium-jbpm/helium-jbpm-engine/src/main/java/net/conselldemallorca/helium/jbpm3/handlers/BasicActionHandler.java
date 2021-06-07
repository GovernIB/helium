/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import es.caib.helium.api.dto.*;
import es.caib.helium.jbpm3.helper.ConversioTipusHelper;
import es.caib.helium.jbpm3.integracio.DominiCodiDescripcio;
import es.caib.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import es.caib.helium.api.dto.Termini;
import org.jbpm.JbpmException;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import net.conselldemallorca.helium.jbpm3.handlers.exception.HeliumHandlerException;
import net.conselldemallorca.helium.jbpm3.handlers.exception.ValidationException;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreEntrada;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreSortida;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentDisseny;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.FilaResultat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Interessat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ParellaCodiValor;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ReferenciaRDSJustificante;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.RespostaEnviar;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.RespostaRegistre;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Tramit;



/**
 * Handler que pot servir com a base per als handlers que s'hagin
 * d'implementar a dins les definicions de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class BasicActionHandler extends AbstractHeliumActionHandler implements ActionHandler {

	public static final String PARAMS_RETROCEDIR_VARIABLE_PREFIX = "H3l1um#params.retroces.";
	public static final String PARAMS_RETROCEDIR_SEPARADOR = "#@#";

	public abstract void execute(ExecutionContext executionContext) throws Exception;

	

	/**
	 * Retorna l'expedient associat al procés actual
	 * 
	 * @param executionContext
	 * @return
	 */
	public ExpedientInfo getExpedient(ExecutionContext executionContext) {
		return ConversioTipusHelper.toExpedientInfo(getExpedientActual(executionContext));
	}

	/**
	 * Llança un error de validació
	 * 
	 * @param error descripció de l'error
	 */
	public void errorValidacio(String error) {
		throw new ValidationException(error);
	}

	/**
	 * Realitza una consulta a un domini
	 * 
	 * @param executionContext
	 * @param codiDomini
	 * @param parametres
	 * @return
	 */
	public List<FilaResultat> consultaDomini(
			ExecutionContext executionContext,
			String codiDomini,
			String id,
			Map<String, Object> parametres) {
		List<FilaResultat> resposta = new ArrayList<FilaResultat>();
		List<DominiRespostaFilaDto> files = Jbpm3HeliumBridge.getInstanceService().dominiConsultar(
				getProcessInstanceId(executionContext),
				codiDomini,
				id,
				parametres);
		if (files != null) {
			for (DominiRespostaFilaDto fila: files) {
				FilaResultat fres = new FilaResultat();
				for (DominiRespostaColumnaDto columna: fila.getColumnes()) {
					fres.addColumna(
							new ParellaCodiValor(
									columna.getCodi(),
									columna.getValor()));
				}
				resposta.add(fres);
			}
		}
		return resposta;
	}

	/**
	 * Retorna tots els valors d'una enumeració
	 * 
	 * @param executionContext
	 * @param codiEnumeracio
	 * @return
	 */
	public List<ParellaCodiValor> consultaEnumeracio(
			ExecutionContext executionContext,
			String codiEnumeracio) {
		List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
		List<EnumeracioValorDto> valors = Jbpm3HeliumBridge.getInstanceService().enumeracioConsultar(
				getProcessInstanceId(executionContext),
				codiEnumeracio);
		if (valors != null) {
			for (EnumeracioValorDto valor: valors) {
				resposta.add(
						new ParellaCodiValor(
								valor.getCodi(),
								valor.getNom()));
			}
		}
		return resposta;
	}
	
	/** Consulta el valor text per a un codi d'una enumeració
	 * 
	 * @param executionContext
	 * @param codiEnumeracio
	 * 			Codi de l'enumeració per trobar l'enumeració.
	 * @param codi
	 * 			Codi de la parella codi-valor de l'enumeració.
	 * @return
	 * 			Retorna el text del camp valor de l'enumeració o null si no s'ha trobat.
	 */
	public String enumeracioGetValor(
			ExecutionContext executionContext,
			String codiEnumeracio,
			String codi) {
			
		String valor = null;
		List<ParellaCodiValor> valorsEnumeracio = this.consultaEnumeracio(executionContext, codiEnumeracio);
		for (ParellaCodiValor p : valorsEnumeracio) {
			if (p.getCodi().equals(codi)) {
				valor = p.getValor() != null ? p.getValor().toString() : null;
				break;
			}
		}
		return valor;
	}
	
	/** Modifica el valor text per a un codi d'una enumeració.
	 * 
	 * @param executionContext
	 * @param codiEnumeracio
	 * 			Codi de l'enumeració per trobar l'enumeració.
	 * @param codi
	 * 			Codi de la parella codi-valor de l'enumeració.
	 * @param valor
	 * 			Cadena de text pel nom de l'enumeració corresponent al codi.
	 */
	public void enumeracioSetValor(
			ExecutionContext executionContext,
			String codiEnumeracio,
			String codi,
			String valor) {
		Jbpm3HeliumBridge.getInstanceService().enumeracioSetValor(
						getProcessInstanceId(executionContext),
						codiEnumeracio,
						codi,
						valor);
	}

	/**
	 * Retorna el resultat d'una consulta d'expedients
	 * 
	 * @param executionContext
	 * @param titol
	 * @param numero
	 * @param dataInici1
	 * @param dataInici2
	 * @param expedientTipusCodi
	 * @param estatCodi
	 * @param iniciat
	 * @param finalitzat
	 * @return
	 */
	public List<ExpedientInfo> consultaExpedients(
			ExecutionContext executionContext,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			String expedientTipusCodi,
			String estatCodi,
			boolean iniciat,
			boolean finalitzat) {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				ExpedientDto expedient = getExpedientActual(executionContext);
				EstatDto estat = Jbpm3HeliumBridge.getInstanceService().findEstatAmbEntornIExpedientTipusICodi(
						expedient.getEntorn().getId(),
						expedientTipusCodi,
						estatCodi);
				// Consulta d'expedients
				List<ExpedientDto> resultats = Jbpm3HeliumBridge.getInstanceService().findExpedientsConsultaGeneral(
						expedient.getEntorn().getId(),
						titol,
						numero,
						dataInici1,
						dataInici2,
						expedient.getTipus().getId(),
						estat == null ? null : estat.getId(),
						iniciat,
						finalitzat);
				// Construcció de la resposta
				List<ExpedientInfo> resposta = new ArrayList<ExpedientInfo>();
				for (ExpedientDto dto: resultats)
					resposta.add(ConversioTipusHelper.toExpedientInfo(dto));
				return resposta;
			} else {
				throw new JbpmException("No hi ha usuari autenticat");
			}
		} catch (Exception ex) {
			throw new JbpmException("Error en la consulta d'expedients", ex);
		}
	}
	/**
	 * Retorna el resultat d'una consulta d'expedients a partir del filtre de dades de Lucene.
	 * 
	 * @param executionContext
	 * @param expedientTipusCodi
	 * @param filtreValors
	 * @return
	 * @throws JbpmException En cas de validació errònia de paràmetres.
	 */
	public List<ExpedientInfo> consultaExpedientsDadesIndexades(
			ExecutionContext executionContext,
			String expedientTipusCodi,
			Map<String, Object> filtreValors) {
		
		// Validacions
		if(expedientTipusCodi == null) throw new JbpmException("El paràmetre expedientTipusCodi no pot ser null");
		if(filtreValors == null || filtreValors.isEmpty()) throw new JbpmException("Els paràmetres del filtre no poden ser null o estar buits");
		
		List<ExpedientInfo> resposta = new ArrayList<ExpedientInfo>();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				ExpedientDto expedient = getExpedientActual(executionContext);

				// Consulta d'expedients
				List<ExpedientDto> resultats = Jbpm3HeliumBridge.getInstanceService().findExpedientsConsultaDadesIndexades(
						expedient.getEntorn().getId(),
						expedientTipusCodi,
						filtreValors);
				// Construcció de la resposta
				for (ExpedientDto dto: resultats)
					resposta.add(ConversioTipusHelper.toExpedientInfo(dto));				
			} else {
				throw new JbpmException("No hi ha usuari autenticat");
			}
		} catch (Exception ex) {
			throw new JbpmException("Error en la consulta d'expedients", ex);
		}
		return resposta;
	}

	/**
	 * Obté el document al que fa referència una variable
	 * 
	 * @param executionContext
	 * @param documentCodi
	 * @return
	 */
	public DocumentInfo getDocument(
			ExecutionContext executionContext,
			String documentCodi) {
		return getDocumentInfo(
				executionContext,
				documentCodi,
				true);
	}

	/**
	 * Obté la informació de disseny del document
	 * 
	 * @param executionContext
	 * @param codiDocument
	 * @return
	 */
	public DocumentDisseny getDocumentDisseny(
			ExecutionContext executionContext,
			String codiDocument) {
		DefinicioProcesDto definicioProces = getDefinicioProces(executionContext);
		DocumentDissenyDto document = Jbpm3HeliumBridge.getInstanceService().getDocumentDisseny(
				definicioProces.getId(),
				String.valueOf(executionContext.getProcessInstance().getId()),
				codiDocument);
		if (document == null)
			return null;
		DocumentDisseny resposta = new DocumentDisseny();
		resposta.setId(document.getId());
		resposta.setCodi(document.getCodi());
		resposta.setNom(document.getNom());
		resposta.setDescripcio(document.getDescripcio());
		resposta.setPlantilla(document.isPlantilla());
		resposta.setContentType(document.getContentType());
		resposta.setCustodiaCodi(document.getCustodiaCodi());
		resposta.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
		return resposta;
	}

	/**
	 * Enllaça un document d'una instancia de procés pare. Si el document
	 * no existeix no el copia i no produeix cap error.
	 * 
	 * @param executionContext
	 * @param varDocument
	 */
	public void crearReferenciaDocumentInstanciaProcesPare(
			ExecutionContext executionContext,
			String varDocument) {
		Token tokenPare = executionContext.getProcessInstance().getRootToken().getParent();
		if (tokenPare != null) {
			String varCodi = Jbpm3HeliumBridge.getInstanceService().getCodiVariablePerDocumentCodi(varDocument);
			Object valor = tokenPare.getProcessInstance().getContextInstance().getVariable(varCodi);
			if (valor != null) {
				if (valor instanceof Long) {
					long lv = ((Long)valor).longValue();
					executionContext.setVariable(varCodi, new Long(-lv));
				} else {
					throw new JbpmException("El contingut del document '" + varDocument + "' no és del tipus correcte");
				}
			}
		} else {
			throw new JbpmException("Aquesta instància de procés no té pare");
		}
	}

	/**
	 * Envia un email amb possibilitat d'adjuntar documents de l'expedient.
	 * 
	 * @param recipients
	 * @param ccRecipients
	 * @param bccRecipients
	 * @param subject
	 * @param text
	 * @param attachments
	 */
	public void enviarEmail(
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text,
			List<Long> attachments) {
		try {
			List<ArxiuDto> documents = null;
			if (attachments != null) {
				documents = new ArrayList<ArxiuDto>();
				for (Long id: attachments) {
					ArxiuDto arxiu = Jbpm3HeliumBridge.getInstanceService().getArxiuPerMostrar(id);
					if (arxiu != null) {
						documents.add(arxiu);
					}
				}
			}
			Jbpm3HeliumBridge.getInstanceService().emailSend(
					Jbpm3HeliumBridge.getInstanceService().getHeliumProperty("app.correu.remitent"),
					recipients,
					ccRecipients,
					bccRecipients,
					subject,
					text,
					documents);
		} catch (Exception ex) {
			throw new JbpmException("No s'ha pogut enviar el missatge", ex);
		}
	}

	/**
	 * Registra un document d'entrada
	 * 
	 * @param dadesEntrada
	 * @param executionContext
	 * @return
	 */
	public RespostaRegistre registreEntrada(
			ExecutionContext executionContext,
			DadesRegistreEntrada dadesEntrada,
			List<DocumentInfo> documentsEntrada) {
		RegistreAnotacioDto anotacio = new RegistreAnotacioDto();
		anotacio.setOrganCodi(dadesEntrada.getOrganCodi());
		anotacio.setOficinaCodi(dadesEntrada.getOficinaCodi());
		anotacio.setEntitatCodi(dadesEntrada.getInteressatEntitatCodi());
		anotacio.setUnitatAdministrativa(dadesEntrada.getAnotacioUnitatAdministrativa());
		anotacio.setInteressatAutenticat(true);
		anotacio.setInteressatNif(dadesEntrada.getInteressatNif());
		anotacio.setInteressatNomAmbCognoms(dadesEntrada.getInteressatNomAmbCognoms());
		anotacio.setInteressatPaisCodi(dadesEntrada.getInteressatPaisCodi());
		anotacio.setInteressatPaisNom(dadesEntrada.getInteressatPaisNom());
		anotacio.setInteressatProvinciaCodi(dadesEntrada.getInteressatProvinciaCodi());
		anotacio.setInteressatProvinciaNom(dadesEntrada.getInteressatProvinciaNom());
		anotacio.setInteressatMunicipiCodi(dadesEntrada.getInteressatMunicipiCodi());
		anotacio.setInteressatMunicipiNom(dadesEntrada.getInteressatMunicipiNom());
		anotacio.setRepresentatNif(dadesEntrada.getRepresentatNif());
		anotacio.setRepresentatNomAmbCognoms(dadesEntrada.getRepresentatNomAmbCognoms());
		anotacio.setAssumpteIdiomaCodi(dadesEntrada.getAnotacioIdiomaCodi());
		anotacio.setAssumpteTipus(dadesEntrada.getAnotacioTipusAssumpte());
		anotacio.setAssumpteExtracte(dadesEntrada.getAnotacioAssumpte());
		List<RegistreAnnexDto> annexos = new ArrayList<RegistreAnnexDto>();
		for (DocumentInfo document: documentsEntrada) {
			RegistreAnnexDto annex = new RegistreAnnexDto();
			annex.setNom(document.getTitol());
			annex.setData(document.getDataDocument());
			annex.setIdiomaCodi("ca");
			annex.setArxiuNom(document.getArxiuNom());
			annex.setArxiuContingut(document.getArxiuContingut());
			annexos.add(annex);
		}
		anotacio.setAnnexos(annexos);
		RegistreIdDto anotacioId = Jbpm3HeliumBridge.getInstanceService().registreAnotacioEntrada(anotacio,executionContext.getProcessInstance().getExpedient().getId());
		RespostaRegistre resposta = new RespostaRegistre();
		resposta.setNumero(anotacioId.getNumero());
		resposta.setData(anotacioId.getData());
		return resposta;
	}
	/**
	 * Registra un document de sortida
	 * 
	 * @param dadesSortida
	 * @param executionContext
	 * @return
	 */
	public RespostaRegistre registreSortida(
			ExecutionContext executionContext,
			DadesRegistreSortida dadesSortida,
			List<DocumentInfo> documentsSortida) {
		RegistreAnotacioDto anotacio = new RegistreAnotacioDto();
		anotacio.setOrganCodi(dadesSortida.getOrganCodi());
		anotacio.setOficinaCodi(dadesSortida.getOficinaCodi());
		anotacio.setEntitatCodi(dadesSortida.getInteressatEntitatCodi());
		anotacio.setUnitatAdministrativa(dadesSortida.getAnotacioUnitatAdministrativa());
		anotacio.setInteressatAutenticat(true);
		anotacio.setInteressatNif(dadesSortida.getInteressatNif());
		anotacio.setInteressatNomAmbCognoms(dadesSortida.getInteressatNomAmbCognoms());
		anotacio.setInteressatPaisCodi(dadesSortida.getInteressatPaisCodi());
		anotacio.setInteressatPaisNom(dadesSortida.getInteressatPaisNom());
		anotacio.setInteressatProvinciaCodi(dadesSortida.getInteressatProvinciaCodi());
		anotacio.setInteressatProvinciaNom(dadesSortida.getInteressatProvinciaNom());
		anotacio.setInteressatMunicipiCodi(dadesSortida.getInteressatMunicipiCodi());
		anotacio.setInteressatMunicipiNom(dadesSortida.getInteressatMunicipiNom());
		anotacio.setRepresentatNif(dadesSortida.getRepresentatNif());
		anotacio.setRepresentatNomAmbCognoms(dadesSortida.getRepresentatNomAmbCognoms());
		anotacio.setAssumpteIdiomaCodi(dadesSortida.getAnotacioIdiomaCodi());
		anotacio.setAssumpteTipus(dadesSortida.getAnotacioTipusAssumpte());
		anotacio.setAssumpteExtracte(dadesSortida.getAnotacioAssumpte());
		List<RegistreAnnexDto> annexos = new ArrayList<RegistreAnnexDto>();
		for (DocumentInfo document: documentsSortida) {
			RegistreAnnexDto annex = new RegistreAnnexDto();
			annex.setNom(document.getTitol());
			annex.setData(document.getDataDocument());
			annex.setIdiomaCodi("ca");
			annex.setArxiuNom(document.getArxiuNom());
			annex.setArxiuContingut(document.getArxiuContingut());
			annexos.add(annex);
		}
		anotacio.setAnnexos(annexos);
		RegistreIdDto anotacioId = Jbpm3HeliumBridge.getInstanceService().registreAnotacioSortida(anotacio, getExpedient(executionContext).getId());
		RespostaRegistre resposta = new RespostaRegistre();
		resposta.setNumero(anotacioId.getNumero());
		resposta.setData(anotacioId.getData());
		return resposta;
	}

	/**
	 * Fa una notificació telemàtica al ciutadà.
	 * 
	 * @param dadesNotificacio
	 * @param executionContext
	 * @return
	 */
	public RespostaRegistre registreNotificacio(
			ExecutionContext executionContext,
			DadesRegistreNotificacio dadesNotificacio,
			List<DocumentInfo> documentsNotificacio) throws JbpmException {
		RegistreNotificacioDto notificacio = new RegistreNotificacioDto();
		notificacio.setExpedientIdentificador(dadesNotificacio.getExpedientIdentificador());
		notificacio.setExpedientClau(dadesNotificacio.getExpedientClau());
		notificacio.setExpedientUnitatAdministrativa(dadesNotificacio.getExpedientUnitatAdministrativa());
		notificacio.setOrganCodi(dadesNotificacio.getOrganCodi());
		notificacio.setOficinaCodi(dadesNotificacio.getOficinaCodi());
		notificacio.setEntitatCodi(dadesNotificacio.getInteressatEntitatCodi());
		notificacio.setInteressatAutenticat(dadesNotificacio.isInteressatAutenticat());
		notificacio.setInteressatNif(dadesNotificacio.getInteressatNif());
		notificacio.setInteressatNomAmbCognoms(dadesNotificacio.getInteressatNomAmbCognoms());
		notificacio.setInteressatNom(dadesNotificacio.getInteressatNom());
		notificacio.setInteressatCognom1(dadesNotificacio.getInteressatCognom1());
		notificacio.setInteressatCognom2(dadesNotificacio.getInteressatCognom2());
		notificacio.setInteressatPaisCodi(dadesNotificacio.getInteressatPaisCodi());
		notificacio.setInteressatPaisNom(dadesNotificacio.getInteressatPaisNom());
		notificacio.setInteressatProvinciaCodi(dadesNotificacio.getInteressatProvinciaCodi());
		notificacio.setInteressatProvinciaNom(dadesNotificacio.getInteressatProvinciaNom());
		notificacio.setInteressatMunicipiCodi(dadesNotificacio.getInteressatMunicipiCodi());
		notificacio.setInteressatMunicipiNom(dadesNotificacio.getInteressatMunicipiNom());
		if (dadesNotificacio.getRepresentatNif() != null || dadesNotificacio.getRepresentatNomAmbCognoms() != null) {
			notificacio.setRepresentatNif(dadesNotificacio.getRepresentatNif());
			notificacio.setRepresentatNomAmbCognoms(dadesNotificacio.getRepresentatNomAmbCognoms());
			notificacio.setRepresentatNom(dadesNotificacio.getRepresentatNom());
			notificacio.setRepresentatCognom1(dadesNotificacio.getRepresentatCognom1());
			notificacio.setRepresentatCognom2(dadesNotificacio.getRepresentatCognom2());
		}
		notificacio.setInteressatEmail(dadesNotificacio.getInteressatEmail());
		notificacio.setInteressatMobil(dadesNotificacio.getInteressatMobil());
		notificacio.setAssumpteIdiomaCodi(dadesNotificacio.getAnotacioIdiomaCodi());
		notificacio.setAssumpteTipus(dadesNotificacio.getAnotacioTipusAssumpte());
		notificacio.setAssumpteExtracte(dadesNotificacio.getAnotacioAssumpte());
		notificacio.setNotificacioJustificantRecepcio(dadesNotificacio.isNotificacioJustificantRecepcio());
		notificacio.setNotificacioAvisTitol(dadesNotificacio.getNotificacioAvisTitol());
		notificacio.setNotificacioAvisText(dadesNotificacio.getNotificacioAvisText());
		notificacio.setNotificacioAvisTextSms(dadesNotificacio.getNotificacioAvisTextSms());
		notificacio.setNotificacioOficiTitol(dadesNotificacio.getNotificacioOficiTitol());
		notificacio.setNotificacioOficiText(dadesNotificacio.getNotificacioOficiText());
		if (dadesNotificacio.getNotificacioSubsanacioTramitIdentificador() != null) {
			notificacio.setTramitSubsanacioIdentificador(
					dadesNotificacio.getNotificacioSubsanacioTramitIdentificador());
			notificacio.setTramitSubsanacioVersio(
					dadesNotificacio.getNotificacioSubsanacioTramitVersio());
			notificacio.setTramitSubsanacioDescripcio(
					dadesNotificacio.getNotificacioSubsanacioTramitDescripcio());
			if (dadesNotificacio.getNotificacioSubsanacioParametres() != null) {
				for (String key: dadesNotificacio.getNotificacioSubsanacioParametres().keySet()) {
					notificacio.afegirTramitSubsanacioParametre(
							key,
							dadesNotificacio.getNotificacioSubsanacioParametres().get(key));
				}
			}
		}
		List<RegistreAnnexDto> annexos = new ArrayList<RegistreAnnexDto>();
		for (DocumentInfo document: documentsNotificacio) {
			RegistreAnnexDto annex = new RegistreAnnexDto();
			annex.setNom(document.getTitol());
			annex.setData(document.getDataDocument());
			annex.setIdiomaCodi("ca");
			annex.setArxiuNom(document.getArxiuNom());
			annex.setArxiuContingut(document.getArxiuContingut());
			annexos.add(annex);
		}
		notificacio.setAnnexos(annexos);
		RegistreIdDto anotacioId = Jbpm3HeliumBridge.getInstanceService().notificacioCrear(notificacio, getExpedient(executionContext).getId(), dadesNotificacio.isNotificacioCrearExpedient());
		RespostaRegistre resposta = new RespostaRegistre();
		resposta.setNumero(anotacioId.getNumero());
		resposta.setData(anotacioId.getData());
		ReferenciaRDSJustificante referenciaRDSJustificante = new ReferenciaRDSJustificante();
		referenciaRDSJustificante.setClave(anotacioId.getReferenciaRDSJustificante().getClave());
		referenciaRDSJustificante.setCodigo(anotacioId.getReferenciaRDSJustificante().getCodigo());
		resposta.setReferenciaRDSJustificante(referenciaRDSJustificante);
		return resposta;
	}

	/**
	 * Consulta la data del justificant de recepció d'una notificació.
	 * 
	 * @param registreNumero
	 * @return
	 * @throws Exception 
	 */
	public RespostaJustificantRecepcioDto obtenirJustificantRecepcio(String registreNumero) throws Exception {
		return Jbpm3HeliumBridge.getInstanceService().notificacioElectronicaJustificant(
				registreNumero);
	}

	/**
	 * Consulta la data del justificant de recepció d'una notificació.
	 * 
	 * @param registreNumero
	 * @return
	 * @throws Exception 
	 */
	public RespostaJustificantDetallRecepcioDto obtenirJustificantDetallRecepcio(String registreNumero) throws Exception {
		return Jbpm3HeliumBridge.getInstanceService().notificacioElectronicaJustificantDetall(
				registreNumero);
	}

	/**
	 * Consulta la data del justificant de recepció d'una notificació.
	 * 
	 * @param registreNumero
	 * @return
	 */
	public Date registreObtenirJustificantRecepcio(String registreNumero) {
		return Jbpm3HeliumBridge.getInstanceService().registreNotificacioComprovarRecepcio(
				registreNumero, null);
	}
	
	
	/**
	 * Crea un interessat
	 * 
	 * @param interessat
	 * @return
	 */
	public void interessatCrear(
			Interessat interessat) {
		
		InteressatDto interessatDto = ConversioTipusHelper.toInteressatDto(interessat);
		Jbpm3HeliumBridge.getInstanceService().interessatCrear(interessatDto);
	}
	
	/**
	 * Modifica un interessat
	 * 
	 * @param interessat
	 * @return
	 */
	public void interessatModificar(
			Interessat interessat) {
		
		InteressatDto interessatDto = ConversioTipusHelper.toInteressatDto(interessat);
		Jbpm3HeliumBridge.getInstanceService().interessatModificar(interessatDto);
	}

	/**
	 * ELimina un interessat
	 * 
	 * @param codi
	 * @param expedientId
	 * @return
	 */
	public void interessatEliminar(
			String codi,
			Long expedientId) {
		
		Jbpm3HeliumBridge.getInstanceService().interessatEliminar(codi, expedientId);
		
	}
	
	
	
	// NOTIB -- Inici
	
	/**
	 * Fa una notificació telemàtica al ciutadà mitjançant el servei de Notib.
	 * 
	 * @param dadesNotificacio
	 * @param expedientId
	 * @return
	 */
	public RespostaEnviar altaNotificacio(
			DadesNotificacio dadesNotificacio,
			Long expedientId) throws JbpmException {
		
		RespostaNotificacio respostaNotificacio = Jbpm3HeliumBridge.getInstanceService().altaNotificacio(
				ConversioTipusHelper.toDadesNotificacioDto(expedientId, dadesNotificacio));
		
		return ConversioTipusHelper.toRespostaEnviar(respostaNotificacio);		
	}
	
	
	// NOTIB -- Fi

	/**
	 * Retorna el valor d'una variable
	 * 
	 * @param executionContext
	 * @param varCodi
	 * @return
	 */
	public Object getVariableValor(
			ExecutionContext executionContext,
			String varCodi) {
		return getVarValue(executionContext.getVariable(varCodi));
	}

	/**
	 * Retorna el text d'una variable per a mostrar a l'usuari
	 * 
	 * @param executionContext
	 * @param varCodi
	 * @return
	 */
	public String getVariableText(
			ExecutionContext executionContext,
			String varCodi) {
		String text = null;
		if (isTaskInstanceExecution(executionContext)) {
			text = getVariableInstanciaTascaText(executionContext, varCodi);
		} 
		if (!isTaskInstanceExecution(executionContext) || text == null) {
			text = getVariableInstanciaProcesText(executionContext, varCodi);
		}
		return text;
	}
	
	/** Consulta en el executionContext si i ha cap instància de tasca activa i per tant si s'està
	 * executant dins del context d'una tasca.
	 * 
	 * @param executionContext
	 * @return
	 */
	public boolean isTaskInstanceExecution(ExecutionContext executionContext) {
		return executionContext.getTaskInstance() != null;
	}
	
	/** Retorna el valor text d'una variable cercant només en el context de la tasca.
	 * 
	 * @param executionContext
	 * @param varCodi
	 * @return
	 * @throws HeliumHandlerException
	 */
	public String getVariableInstanciaTascaText(
			ExecutionContext executionContext, 
			String varCodi) throws HeliumHandlerException {
		if (!isTaskInstanceExecution(executionContext)) {
			throw new HeliumHandlerException("No taskInstance execution context.");
		}
		TascaDadaDto dto = Jbpm3HeliumBridge.getInstanceService().getDadaPerTaskInstance(
				getProcessInstanceId(executionContext),
				getTaskInstanceId(executionContext),
				varCodi);
		if (dto == null)
			return null;
		else
			return dto.getText();
	}
	
	/** Retorna el valor text d'una variable cercant només en el context del procés.
	 * 
	 * @param executionContext
	 * @param varCodi
	 * @return
	 */
	public String getVariableInstanciaProcesText(
			ExecutionContext executionContext,
			String varCodi) {
		return Jbpm3HeliumBridge.getInstanceService().getDadaPerProcessInstance(
				getProcessInstanceId(executionContext),
				varCodi);
	}
	
	public String getTextPerVariableAmbDomini(
			ExecutionContext executionContext,
			String varCodi) {
		return getVariableText(executionContext, varCodi);
	}

	/**
	 * Guarda un termini a dins una variable
	 * 
	 * @param executionContext
	 * @param anys
	 * @param mesos
	 * @param dies
	 */
	public void terminiGuardar(
			ExecutionContext executionContext,
			String varName,
			int anys,
			int mesos,
			int dies) {
		Termini termini = new Termini();
		termini.setAnys(anys);
		termini.setMesos(mesos);
		termini.setDies(dies);
		executionContext.setVariable(varName, termini);
	}

	/**
	 * Consulta les dades d'un tràmit
	 * 
	 * @param numero
	 * @param clau
	 */
	public Tramit consultaTramit(
			String numero,
			String clau) {
		try {
			return ConversioTipusHelper.toTramit(
					Jbpm3HeliumBridge.getInstanceService().getTramit(numero, clau));
		} catch (Exception ex) {
			throw new JbpmException("No s'ha pogut obtenir el tràmit (numero=" + numero + ", clau=" + clau + ")", ex);
		}
	}

	/**
	 * Relaciona aquest expedient amb un altre
	 * 
	 * @param executionContext
	 * @param expedientId
	 */
	public void expedientRelacionar(
			ExecutionContext executionContext,
			long expedientId) {
		ExpedientInfo expedient = getExpedient(executionContext);
		Jbpm3HeliumBridge.getInstanceService().expedientRelacionar(
				expedient.getId(),
				expedientId);
	}

	/**
	 * Redirigeix un token cap a un altre node.
	 * 
	 * @param tokenId
	 * @param nodeName
	 * @param cancelarTasques
	 */
	public void tokenRedirigir(
			long tokenId,
			String nodeName,
			boolean cancelarTasques) {
		Jbpm3HeliumBridge.getInstanceService().tokenRedirigir(
				tokenId,
				nodeName,
				cancelarTasques);
	}

	/**
	 * Emmagatzema els paràmetres per a retrocedir l'acció.
	 * 
	 * @param executionContext
	 * @param parametres
	 */
	public void guardarParametresPerRetrocedir(
			ExecutionContext executionContext,
			List<String> parametres) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parametres.size(); i++) {
			sb.append(parametres.get(i));
			if (i < parametres.size() - 1)
				sb.append(PARAMS_RETROCEDIR_SEPARADOR);
		}
		// Variable "prefix" + codi definicio de procés + codi acció + id dels logs
		executionContext.setVariable(
				PARAMS_RETROCEDIR_VARIABLE_PREFIX + executionContext.getAction().getName(),
				sb.toString());
	}

	/**
	 * Reindexa l'expedient actual.
	 * 
	 * @param executionContext
	 */
	public void instanciaProcesReindexar(ExecutionContext executionContext) {
		instanciaProcesReindexar(executionContext.getProcessInstance().getId());
	}

	/**
	 * Reindexa l'expedient corresponent a una instància de procés.
	 * 
	 * @param processInstanceId
	 */
	public boolean instanciaProcesReindexar(long processInstanceId) {
		return Jbpm3HeliumBridge.getInstanceService().expedientReindexar(
				new Long(processInstanceId).toString());
	}

	/**
	 * Activa o desactiva un token
	 * 
	 * @param tokenId
	 * @param activar
	 * @return
	 */
	public boolean tokenActivar(long tokenId, boolean activar) {
		return Jbpm3HeliumBridge.getInstanceService().tokenActivar(tokenId, activar);
	}

	/**
	 * Reprendre un expedient
	 * 
	 * @param processInstanceId
	 * @throws Exception
	 */
	public void reprendreExpedient(String processInstanceId) throws Exception {
		// 1 Reprenem l'expedient
		Jbpm3HeliumBridge.getInstanceService().expedientReprendre(processInstanceId);
		// 2 Desfinalitzem l'expedient
		Jbpm3HeliumBridge.getInstanceService().desfinalitzarExpedient(processInstanceId);
	}

	/**
	 * Obté el contingut de l'arxiu directament de la gestió documental.
	 * 
	 * @param id
	 * @return
	 */
	public byte[] obtenirArxiuGestorDocumental(String id) {
		ArxiuDto arxiu = Jbpm3HeliumBridge.getInstanceService().getArxiuGestorDocumental(id);
		if (arxiu != null)
			return arxiu.getContingut();
		else
			return null;
	}

	/**
	 * Emmagatzema un document a dins l'expedient.
	 * 
	 * @param executionContext
	 * @param documentCodi
	 * @param data
	 * @param arxiuNom
	 * @param arxiuContingut
	 */
	public void documentGuardar(
			ExecutionContext executionContext,
			String documentCodi,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut) {
		Jbpm3HeliumBridge.getInstanceService().documentExpedientGuardar(
				getProcessInstanceId(executionContext),
				documentCodi,
				data,
				arxiuNom,
				arxiuContingut);
	}

	/**
	 * Guarda un document adjunt.
	 * 
	 * @param executionContext
	 * @param nomDocument
	 * @param data
	 * @param arxiuNom
	 * @param arxiuContingut
	 */
	public void adjuntGuardar(
            ExecutionContext executionContext,
            String nomDocument,
            Date data,
            String arxiuNom,
            byte[] arxiuContingut) {
		Jbpm3HeliumBridge.getInstanceService().documentExpedientAdjuntar(
				getProcessInstanceId(executionContext),
				null, // adjuntId
				nomDocument,
				data,
				arxiuNom,
				arxiuContingut);
    }

	/**
	 * Retorna el valor d'una variable global.
	 * 
	 * @param executionContext
	 * @param varCodi
	 * @return
	 */
	public Object getVariableGlobal(
			ExecutionContext executionContext,
			String varCodi) {
		ContextInstance ci = getContextInstanceGlobal(executionContext);
		return ci.getVariable(varCodi);
	}
	/**
	 * Modifica el valor d'una variable global.
	 * 
	 * @param executionContext
	 * @param varCodi
	 * @return
	 */
	public void setVariableGlobal(
			ExecutionContext executionContext,
			String varCodi,
			Object varValor) {
		ContextInstance ci = getContextInstanceGlobal(executionContext);
		ci.setVariable(varCodi, varValor);
	}
	/**
	 * Retorna el valor d'una variable global.
	 * 
	 * @param executionContext
	 * @param varCodi
	 * @return
	 */
	public Object getVariableGlobalValor(
			ExecutionContext executionContext,
			String varCodi) {
		ContextInstance ci = getContextInstanceGlobal(executionContext);
		return getVarValue(ci.getVariable(varCodi));
	}

	/**
	 * Insereix el @missatge amb la data actual com a 
	 * informació d'execució de la tasca
	 * en segón pla.
	 * 
	 * @param executionContext
	 * @param missatge
	 */
	public void desarInformacioExecucio(
			ExecutionContext executionContext, 
			String missatge) throws Exception {
		  ClassLoader surroundingClassLoader = Thread.currentThread().getContextClassLoader();
		  try {
			Long tokenId = executionContext.getToken().getId();
			Long taskId = Jbpm3HeliumBridge.getInstanceService().getTaskInstanceIdByExecutionTokenId(tokenId);
			boolean isTascaEnSegonPla =  Jbpm3HeliumBridge.getInstanceService().isTascaEnSegonPla(taskId);
			
			if (isTascaEnSegonPla) {
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				String dataHandler = df.format(new Date());
				Jbpm3HeliumBridge.getInstanceService().addMissatgeExecucioTascaSegonPla(taskId, new String[]{dataHandler, missatge});
			}
		  } finally {
			  Thread.currentThread().setContextClassLoader(surroundingClassLoader);
		  }
	  }


	private ContextInstance getContextInstanceGlobal(
			ExecutionContext executionContext) {
		ContextInstance ci = executionContext.getContextInstance();
		if (executionContext.getToken() != null) {
			ci = executionContext.getToken().getProcessInstance().getContextInstance();
			while (ci.getProcessInstance().getSuperProcessToken() != null) {
				ci = ci.getProcessInstance().getSuperProcessToken().getProcessInstance().getContextInstance();
			}
		}
		return ci;
	}
	private Object getVarValue(Object valor) {
		if (valor instanceof DominiCodiDescripcio) {
			return ((DominiCodiDescripcio)valor).getCodi();
		} else {
			return valor;
		}
	}
	
	static final long serialVersionUID = 1L;

}
