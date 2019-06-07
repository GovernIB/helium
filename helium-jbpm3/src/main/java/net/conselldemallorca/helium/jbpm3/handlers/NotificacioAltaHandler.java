package net.conselldemallorca.helium.jbpm3.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.security.crypto.codec.Base64;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesEnviament;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.PersonaInfo;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaNotificacio;

/**
 * Handler per a interactuar amb el registre de sortida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings({"serial", "unused"})
public class NotificacioAltaHandler extends BasicActionHandler implements NotificacioAltaHandlerInterface { //, AccioExternaRetrocedirHandler {

	private String emisorDir3Codi;
	private String varEmisorDir3Codi;

	private String enviamentTipus;	// Possibles valors: [NOTIFICACIO, COMUNICACIO]
	private String varEnviamentTipus;
	
	private String concepte;
	private String varConcepte;
	
	private String descripcio;
	private String varDescripcio;
	
	private String enviamentDataProgramada;
	private String varEnviamentDataProgramada;
	
	private String retard;
	private String varRetard;
	
	private String caducitat;
	private String varCaducitat;
	
	private String document;
	private String varDocument;
	
	private String annexos;
	private String varAnnexos;
	
	private String procedimentCodi;
	private String varProcedimentCodi;
	
	private String pagadorPostalDir3Codi;
	private String varPagadorPostalDir3Codi;
	
	private String pagadorPostalContracteNum;
	private String varPagadorPostalContracteNum;
	
	private String pagadorPostalContracteDataVigencia;
	private String varPagadorPostalContracteDataVigencia;
	
	private String pagadorPostalFacturacioClientCodi;
	private String varPagadorPostalFacturacioClientCodi;
	
	private String pagadorCieDir3Codi;
	private String varPagadorCieDir3Codi;
	
	private String pagadorCieContracteDataVigencia;
	private String varPagadorCieContracteDataVigencia;
	
	private String seuProcedimentCodi;
	private String varSeuProcedimentCodi;
	
	private String seuExpedientSerieDocumental;
	private String varSeuExpedientSerieDocumental;
	
	private String seuExpedientUnitatOrganitzativa; // UNITAT_ADMINISTRATIVA_SISTRA
	private String varSeuExpedientUnitatOrganitzativa;
	
	private String seuExpedientIdentificadorEni;
	private String varSeuExpedientIdentificadorEni;
	
	private String seuExpedientTitol; 
	private String varSeuExpedientTitol;
	
	private String seuRegistreOficina; // OFICINA
	private String varSeuRegistreOficina;
	
	private String seuRegistreLlibre; // LLIBRE
	private String varSeuRegistreLlibre;
	
	private String seuRegistreOrgan; // ORGAN_CODI (falta)
	private String varSeuRegistreOrgan;
	
	private String seuIdioma;
	private String varSeuIdioma;
	
	private String seuAvisTitol;
	private String varSeuAvisTitol;
	
	private String seuAvisText;
	private String varSeuAvisText;
	
	private String seuAvisTextMobil;
	private String varSeuAvisTextMobil;
	
	private String seuOficiTitol;
	private String varSeuOficiTitol;
	
	private String seuOficiText;
	private String varSeuOficiText;
	
	// ENVIAMENT
	
	// Titular
	private String titularNif;
	private String varTitularNif;
	
	private String titularNom;
	private String varTitularNom;
	
	private String titularLlin1;
	private String varTitularLlin1;
	
	private String titularLlin2;
	private String varTitularLlin2;
	
	private String titularEmail;
	private String varTitularEmail;
	
	private String titularMobil;
	private String varTitularMobil;
	
	// Destinatari
	private String destinatariNif;
	private String varDestinatariNif;
	
	private String destinatariNom;
	private String varDestinatariNom;
	
	private String destinatariLlin1;
	private String varDestinatariLlin1;
	
	private String destinatariLlin2;
	private String varDestinatariLlin2;
	
	private String destinatariEmail;
	private String varDestinatariEmail;
	
	private String destinatariMobil;
	private String varDestinatariMobil;

	// Dades d'entrega
	private String entregaPostalTipus; // Possibles valors: [NACIONAL, ESTRANGER, APARTAT_CORREUS, SENSE_NORMALITZAR]
	private String varEntregaPostalTipus;
	
	private String  entregaPostalViaTipus; // Possibles valors: [ALAMEDA, CALLE, CAMINO, CARRER, CARRETERA, GLORIETA, KALEA, PASAJE, PASEO, PLAÇA, PLAZA, RAMBLA, RONDA,	RUA, SECTOR, TRAVESIA, URBANIZACION, AVENIDA, AVINGUDA, BARRIO, CALLEJA, CAMI, CAMPO, CARRERA, CUESTA, EDIFICIO, ENPARANTZA, ESTRADA, JARDINES, JARDINS, PARQUE, PASSEIG, PRAZA, PLAZUELA, PLACETA, POBLADO, VIA, TRAVESSERA, PASSATGE, BULEVAR, POLIGONO, OTROS]
	private String  varEntregaPostalViaTipus;
	
	private String entregaPostalViaNom;
	private String varEntregaPostalViaNom;
	
	private String entregaPostalNumeroCasa;
	private String varEntregaPostalNumeroCasa;
	
	private String entregaPostalNumeroQualificador;
	private String varEntregaPostalNumeroQualificador;
	
	private String entregaPostalPuntKm;
	private String varEntregaPostalPuntKm;
	
	private String entregaPostalApartatCorreus;
	private String varEntregaPostalApartatCorreus;
	
	private String entregaPostalPortal;
	private String varEntregaPostalPortal;
	
	private String entregaPostalEscala;
	private String varEntregaPostalEscala;
	
	private String entregaPostalPlanta;
	private String varEntregaPostalPlanta;
	
	private String entregaPostalPorta;
	private String varEntregaPostalPorta;
	
	private String entregaPostalBloc;
	private String varEntregaPostalBloc;
	
	private String entregaPostalComplement;
	private String varEntregaPostalComplement;
	
	private String entregaPostalCodiPostal;
	private String varEntregaPostalCodiPostal;
	
	private String entregaPostalPoblacio;
	private String varEntregaPostalPoblacio;
	
	private String entregaPostalMunicipiCodi;
	private String varEntregaPostalMunicipiCodi;
	
	private String entregaPostalProvinciaCodi;
	private String varEntregaPostalProvinciaCodi;
	
	private String entregaPostalPaisCodi;
	private String varEntregaPostalPaisCodi;
	
	private String entregaPostalLinea1;
	private String varEntregaPostalLinea1;
	
	private String entregaPostalLinea2;
	private String varEntregaPostalLinea2;
	
	private String entregaPostalCie;
	private String varEntregaPostalCie;
	
	private String entregaPostalFormatSobre;
	private String varEntregaPostalFormatSobre;
	
	private String entregaPostalFormatFulla;
	private String varEntregaPostalFormatFulla;
	
	private String entregaDehObligat;
	private String varEntregaDehObligat;
	
	private String entregaDehProcedimentCodi;
	private String varEntregaDehProcedimentCodi;
	
//	private boolean notibActiu;
//	private String notibSeuUnitatAdministrativa;
//	private String notibSeuOficina;
//	private String notibSeuLlibre;
//	private String notibSeuOrgan;
//	private String notibSeuIdioma;
//	private String notibAvisTitol;
//	private String notibAvisText;
//	private String notibAvisTextSms;
//	private String notibOficiTitol;
//	private String notibOficiText;
	
	public void execute(ExecutionContext executionContext) throws Exception {		
		ExpedientDto expedient = getExpedientActual(executionContext);
		ExpedientTipusDto expedientTipus = expedient.getTipus();
		
//		if (expedient.getTramitExpedientIdentificador() == null || expedient.getTramitExpedientIdentificador().isEmpty()) {
//			expedient.setTramitExpedientIdentificador(expedient.getId().toString());
//			if (expedient.getTramitExpedientIdentificador() == null)
//				throw new JbpmException(
//						  "El expediente " + expedient.getIdentificador() + " no tiene número de sistra asociado."
//						+ "Una notificación tiene que generarse dentro de un expediente, por tanto un paso "
//						+ "previo a generar una notificación es haber publicado el expediente en la zona "
//						+ "personal.");
//		}
		
		String expedientTitol = expedient.getTitol();
		String notibEmisor = expedientTipus.getNtiOrgano();
		String notibSeuUnitatAdministrativa = expedientTipus.getNotibSeuUnitatAdministrativa();
		if (notibSeuUnitatAdministrativa == null && expedient.getUnitatAdministrativa() != null)
			notibSeuUnitatAdministrativa = String.valueOf(expedient.getUnitatAdministrativa());
		String notibSeuOficina = expedientTipus.getNotibSeuOficina();
		String notibSeuLlibre = expedientTipus.getNotibSeuLlibre();
		String notibSeuOrgan = expedientTipus.getNotibSeuOrgan();
		String notibSeuIdioma = expedientTipus.getNotibSeuIdioma();
		if (notibSeuIdioma == null)
			notibSeuIdioma = "ca";
		String notibAvisTitol = expedientTipus.getNotibAvisTitol();
		String notibAvisText = expedientTipus.getNotibAvisText();
		String notibAvisTextSms = expedientTipus.getNotibAvisTextSms();
		String notibOficiTitol = expedientTipus.getNotibOficiTitol();
		String notibOficiText = expedientTipus.getNotibOficiText();
		String notibSerieDocumental = expedientTipus.getNtiSerieDocumental();
		String notibProcedimentCodi = expedientTipus.getNtiClasificacion();
		String notibSeuProcedimentCodi = expedientTipus.getNotibSeuCodiProcediment();
		
		DadesNotificacio dadesNotificacio = new DadesNotificacio();
		
		String emisorCodi = (String)getValorOVariable(
				executionContext,
				emisorDir3Codi,
				varEmisorDir3Codi);
		dadesNotificacio.setEmisorDir3Codi(emisorCodi != null ? emisorCodi : notibEmisor);

		String strTipusEnviament = (String)getValorOVariable(
				executionContext,
				enviamentTipus,
				varEnviamentTipus);
		
		DadesNotificacio.EnviamentTipus enviamentTipus = "COMUNICACIO".equalsIgnoreCase(strTipusEnviament) ? 
				DadesNotificacio.EnviamentTipus.COMUNICACIO : DadesNotificacio.EnviamentTipus.NOTIFICACIO; 
		
		dadesNotificacio.setEnviamentTipus(enviamentTipus);

		dadesNotificacio.setConcepte((String)getValorOVariable(
				executionContext,
				concepte,
				varConcepte));
		dadesNotificacio.setDescripcio((String)getValorOVariable(
				executionContext,
				descripcio,
				varDescripcio));
		
		dadesNotificacio.setEnviamentDataProgramada(getValorOVariableData(
				executionContext,
				enviamentDataProgramada,
				varEnviamentDataProgramada));
		dadesNotificacio.setRetard(getValorOVariableInteger(
				executionContext,
				retard,
				varRetard));
		dadesNotificacio.setCaducitat(getValorOVariableData(
				executionContext,
				caducitat,
				varCaducitat));
		
		DocumentInfo documentInfo = null;
		List<DocumentInfo> annexos_notificacio = new ArrayList<DocumentInfo>();
		
		String doc = (String)getValorOVariable(
				executionContext,
				document,
				varDocument);
		if (doc != null && !doc.isEmpty()) {
			documentInfo = getDocumentInfo(executionContext, doc, true);
			if (documentInfo != null) {
				dadesNotificacio.setDocumentId(documentInfo.getId());
				dadesNotificacio.setDocumentArxiuNom(documentInfo.getArxiuNom());
				dadesNotificacio.setDocumentArxiuContingut(documentInfo.getArxiuContingut());
			} else {
				throw new JbpmException("No existia ningún documento con documentCodi: " + varDocument + ".");
			}
		}
		
// 		Actualment no enviem annexos
		
//		List<Long> anxs = null;
//		String anxsCodis = (String)getValorOVariable(
//				executionContext, 
//				annexos,
//				varAnnexos);
//		if (anxsCodis != null) {
//			anxs = new ArrayList<Long>();
//			String[] codis = anxsCodis.split(",");
//			for (String codi: codis) {
//				DocumentInfo annexInfo = getDocumentInfo(executionContext, codi, true);
//				if (annexInfo != null) {
//					annexos_notificacio.add(annexInfo);
//				} else {
//					throw new JbpmException("No existeix cap annex amb documentCodi: " + codi + ".");
//				}
//			}
//		}
//		dadesNotificacio.setAnnexos(annexos_notificacio);
		
		String codiProcediment = (String)getValorOVariable(
				executionContext,
				procedimentCodi,
				varProcedimentCodi);
		dadesNotificacio.setProcedimentCodi(codiProcediment != null ? codiProcediment : notibProcedimentCodi);
		dadesNotificacio.setPagadorPostalDir3Codi((String)getValorOVariable(
				executionContext,
				pagadorPostalDir3Codi,
				varPagadorPostalDir3Codi));
		dadesNotificacio.setPagadorPostalContracteNum((String)getValorOVariable(
				executionContext,
				pagadorPostalContracteNum,
				varPagadorPostalContracteNum));
		dadesNotificacio.setPagadorPostalContracteDataVigencia(getValorOVariableData(
				executionContext,
				pagadorPostalContracteDataVigencia,
				varPagadorPostalContracteDataVigencia));
		dadesNotificacio.setPagadorPostalFacturacioClientCodi((String)getValorOVariable(
				executionContext,
				pagadorPostalFacturacioClientCodi,
				varPagadorPostalFacturacioClientCodi));
		dadesNotificacio.setPagadorCieDir3Codi((String)getValorOVariable(
				executionContext,
				pagadorCieDir3Codi,
				varPagadorCieDir3Codi));
		dadesNotificacio.setPagadorCieContracteDataVigencia(getValorOVariableData(
				executionContext,
				pagadorCieContracteDataVigencia,
				varPagadorCieContracteDataVigencia));
		
		String seuCodiProcediment = (String)getValorOVariable(
				executionContext,
				seuProcedimentCodi,
				varSeuProcedimentCodi);
		dadesNotificacio.setSeuProcedimentCodi(seuCodiProcediment != null ? seuCodiProcediment: notibSeuProcedimentCodi);
		String seuSerieDocumental = (String)getValorOVariable(
				executionContext,
				seuExpedientSerieDocumental,
				varSeuExpedientSerieDocumental);
		dadesNotificacio.setSeuExpedientSerieDocumental(seuSerieDocumental != null ? seuSerieDocumental: notibSerieDocumental);
		String seuUnitatOrganitzativa = (String)getValorOVariable(
				executionContext,
				seuExpedientUnitatOrganitzativa,
				varSeuExpedientUnitatOrganitzativa);
		dadesNotificacio.setSeuExpedientUnitatOrganitzativa(seuUnitatOrganitzativa != null ? seuUnitatOrganitzativa : notibSeuUnitatAdministrativa);
		
		// Identificador expedient
		String identificador = expedient.getId().toString();
		String identificadorEni = expedient.getTramitExpedientIdentificador();
		if (identificadorEni == null || identificadorEni.isEmpty())
			identificadorEni = (String)getValorOVariable(
					executionContext,
					seuExpedientIdentificadorEni,
					varSeuExpedientIdentificadorEni);
		if (identificadorEni == null || identificadorEni.isEmpty()) {
			identificadorEni = expedient.getNtiIdentificador();
		}
		if (identificadorEni == null || identificadorEni.isEmpty()) {
			DateFormat sdf = new SimpleDateFormat("YYYY");
			identificadorEni = 	"ES_" 
								+ notibSeuOrgan + "_" 
								+ (expedient.getDataInici() != null ? sdf.format(expedient.getDataInici()) : sdf.format(new Date())) + 
								"_EXP_" 
								+ formatIdentificadorEni(expedient.getNumeroIdentificador());
		}
//		expedient.setTramitExpedientIdentificador(identificadorEni);
		dadesNotificacio.setSeuExpedientIdentificadorEni(identificadorEni);
		String expTitol = (String)getValorOVariable(
				executionContext,
				seuExpedientTitol,
				varSeuExpedientTitol);
		dadesNotificacio.setSeuExpedientTitol(expTitol != null ? expTitol : expedientTitol);
		String seuOficina = (String)getValorOVariable(
				executionContext,
				seuRegistreOficina,
				varSeuRegistreOficina);
		dadesNotificacio.setSeuRegistreOficina(seuOficina != null ? seuOficina: notibSeuOficina);
		String seuLlibre = (String)getValorOVariable(
				executionContext,
				seuRegistreLlibre,
				varSeuRegistreLlibre);
		dadesNotificacio.setSeuRegistreLlibre(seuLlibre != null ? seuLlibre : notibSeuLlibre);
		String seuOrgan = (String)getValorOVariable(
				executionContext,
				seuRegistreOrgan,
				varSeuRegistreOrgan);
		dadesNotificacio.setSeuRegistreOrgan(seuOrgan != null? seuOrgan : notibSeuOrgan);
		String idioma = (String)getValorOVariable(
				executionContext,
				seuIdioma,
				varSeuIdioma);
		dadesNotificacio.setSeuIdioma(idioma != null ? idioma : notibSeuIdioma);
		String seuAvisTitolVal = (String)getValorOVariable(
				executionContext,
				seuAvisTitol,
				varSeuAvisTitol);
		dadesNotificacio.setSeuAvisTitol(seuAvisTitolVal != null ? seuAvisTitolVal : notibAvisTitol);
		String seuAvisTextVal = (String)getValorOVariable(
				executionContext,
				seuAvisText,
				varSeuAvisText);
		dadesNotificacio.setSeuAvisText(seuAvisTextVal != null ? seuAvisTextVal : notibAvisText);
		String seuAvisTextMobilVal = (String)getValorOVariable(
				executionContext,
				seuAvisTextMobil,
				varSeuAvisTextMobil);
		dadesNotificacio.setSeuAvisTextMobil(seuAvisTextMobilVal != null ? seuAvisTextMobilVal : notibAvisTextSms);
		String seuOficiTitolVal = (String)getValorOVariable(
				executionContext,
				seuOficiTitol,
				varSeuOficiTitol);
		dadesNotificacio.setSeuOficiTitol(seuOficiTitolVal != null ? seuOficiTitolVal : notibOficiTitol);
		String seuOficiTextVal = (String)getValorOVariable(
				executionContext,
				seuOficiText,
				varSeuOficiText);
		dadesNotificacio.setSeuOficiText(seuOficiTextVal != null ? seuOficiTextVal : notibOficiText);
		
		List<DadesEnviament> enviaments = new ArrayList<DadesEnviament>();
		DadesEnviament enviament = new DadesEnviament();
			
		PersonaInfo titular = new PersonaInfo();
		titular.setNom((String)getValorOVariable(
				executionContext,
				titularNom,
				varTitularNom));
		titular.setLlinatge1((String)getValorOVariable(
				executionContext,
				titularLlin1,
				varTitularLlin1));
		titular.setLlinatge2((String)getValorOVariable(
				executionContext,
				titularLlin2,
				varTitularLlin2));
		titular.setDni((String)getValorOVariable(
				executionContext,
				titularNif,
				varTitularNif));
		String mobil = (String)getValorOVariable(
				executionContext,
				titularMobil,
				varTitularMobil);
		if (mobil == null || mobil.isEmpty()) 
			mobil = expedient.getAvisosMobil();
		titular.setTelefon(mobil);
		String email = (String)getValorOVariable(
				executionContext,
				titularEmail,
				varTitularEmail);
		if (email == null || email.isEmpty())
			email = expedient.getAvisosEmail();
		titular.setEmail(email);
		enviament.setTitular(titular);
		
		List<PersonaInfo> destinataris = new ArrayList<PersonaInfo>();
				
		PersonaInfo destinatari = new PersonaInfo();
		destinatari.setNom((String)getValorOVariable(
				executionContext,
				destinatariNom,
				varDestinatariNom));
		destinatari.setLlinatge1((String)getValorOVariable(
				executionContext,
				destinatariLlin1,
				varDestinatariLlin1));
		destinatari.setLlinatge2((String)getValorOVariable(
				executionContext,
				destinatariLlin2,
				varDestinatariLlin2));
		destinatari.setDni((String)getValorOVariable(
				executionContext,
				destinatariNif,
				varDestinatariNif));
		destinatari.setTelefon((String)getValorOVariable(
				executionContext,
				destinatariMobil,
				varDestinatariMobil));
		destinatari.setEmail((String)getValorOVariable(
				executionContext,
				destinatariEmail,
				varDestinatariEmail));
		destinataris.add(destinatari);
		enviament.setDestinataris(destinataris);
			
		String strEntragaPostalTipus = (String)getValorOVariable(
				executionContext,
				entregaPostalTipus,
				varEntregaPostalTipus);
		
		if (strEntragaPostalTipus != null && !strEntragaPostalTipus.isEmpty())
			enviament.setEntregaPostalTipus(DadesEnviament.EntregaPostalTipus.valueOf(strEntragaPostalTipus));
		
		String strEntregaPostalViaTipus = (String)getValorOVariable(
				executionContext,
				entregaPostalViaTipus,
				varEntregaPostalViaTipus);
		if (strEntregaPostalViaTipus != null && !strEntregaPostalViaTipus.isEmpty())
			enviament.setEntregaPostalViaTipus(DadesEnviament.EntregaPostalViaTipus.valueOf(strEntregaPostalViaTipus));
		enviament.setEntregaPostalViaNom((String)getValorOVariable(
				executionContext,
				entregaPostalViaNom,
				varEntregaPostalViaNom));
		enviament.setEntregaPostalNumeroCasa((String)getValorOVariable(
				executionContext,
				entregaPostalNumeroCasa,
				varEntregaPostalNumeroCasa));
		enviament.setEntregaPostalNumeroQualificador((String)getValorOVariable(
				executionContext,
				entregaPostalNumeroQualificador,
				varEntregaPostalNumeroQualificador));
		enviament.setEntregaPostalPuntKm((String)getValorOVariable(
				executionContext,
				entregaPostalPuntKm,
				varEntregaPostalPuntKm));
		enviament.setEntregaPostalApartatCorreus((String)getValorOVariable(
				executionContext,
				entregaPostalApartatCorreus,
				varEntregaPostalApartatCorreus));
		enviament.setEntregaPostalPortal((String)getValorOVariable(
				executionContext,
				entregaPostalPortal,
				varEntregaPostalPortal));
		enviament.setEntregaPostalEscala((String)getValorOVariable(
				executionContext,
				entregaPostalEscala,
				varEntregaPostalEscala));
		enviament.setEntregaPostalPlanta((String)getValorOVariable(
				executionContext,
				entregaPostalPlanta,
				varEntregaPostalPlanta));
		enviament.setEntregaPostalPorta((String)getValorOVariable(
				executionContext,
				entregaPostalPorta,
				varEntregaPostalPorta));
		enviament.setEntregaPostalBloc((String)getValorOVariable(
				executionContext,
				entregaPostalBloc,
				varEntregaPostalBloc));
		enviament.setEntregaPostalComplement((String)getValorOVariable(
				executionContext,
				entregaPostalComplement,
				varEntregaPostalComplement));
		enviament.setEntregaPostalCodiPostal((String)getValorOVariable(
				executionContext,
				entregaPostalCodiPostal,
				varEntregaPostalCodiPostal));
		enviament.setEntregaPostalPoblacio((String)getValorOVariable(
				executionContext,
				entregaPostalPoblacio,
				varEntregaPostalPoblacio));
		enviament.setEntregaPostalMunicipiCodi((String)getValorOVariable(
				executionContext,
				entregaPostalMunicipiCodi,
				varEntregaPostalMunicipiCodi));
		enviament.setEntregaPostalProvinciaCodi((String)getValorOVariable(
				executionContext,
				entregaPostalProvinciaCodi,
				varEntregaPostalProvinciaCodi));
		enviament.setEntregaPostalPaisCodi((String)getValorOVariable(
				executionContext,
				entregaPostalPaisCodi,
				varEntregaPostalPaisCodi));
		enviament.setEntregaPostalLinea1((String)getValorOVariable(
				executionContext,
				entregaPostalLinea1,
				varEntregaPostalLinea1));
		enviament.setEntregaPostalLinea2((String)getValorOVariable(
				executionContext,
				entregaPostalLinea2,
				varEntregaPostalLinea2));
		enviament.setEntregaPostalCie(getValorOVariableInteger(
				executionContext,
				entregaPostalCie,
				varEntregaPostalCie));
		enviament.setEntregaPostalFormatSobre((String)getValorOVariable(
				executionContext,
				entregaPostalFormatSobre,
				varEntregaPostalFormatSobre));
		enviament.setEntregaPostalFormatFulla((String)getValorOVariable(
				executionContext,
				entregaPostalFormatFulla,
				varEntregaPostalFormatFulla));
		Boolean dehObligat = getValorOVariableBoolean(
				executionContext,
				entregaDehObligat,
				varEntregaDehObligat);
		enviament.setEntregaDehObligat(dehObligat != null ? dehObligat : false);
		enviament.setEntregaDehProcedimentCodi((String)getValorOVariable(
				executionContext,
				entregaDehProcedimentCodi,
				varEntregaDehProcedimentCodi));
		
		enviaments.add(enviament);
		dadesNotificacio.setEnviaments(enviaments);
		
//		RespostaNotificacio resposta = 
		altaNotificacio(dadesNotificacio, expedient.getId());
		
//		if (resposta != null) {			
//			
//			NotificacioDto notificacio = new NotificacioDto();
//			notificacio.setEstat(DocumentEnviamentEstatEnumDto.ENVIAT);
//			notificacio.setAssumpte(anotacio.getAnotacioAssumpte());
//			notificacio.setRegistreNumero(resposta.getNumero());
//			notificacio.setDataEnviament(resposta.getData());
//			notificacio.setDataRecepcio(null);
//			
//			DocumentNotificacioDto documentNotificacio = new DocumentNotificacioDto();
//			documentNotificacio.setId(documentInfo.getId());
//			notificacio.setDocument(documentNotificacio);
//			
//			for (DocumentInfo annex_exp: annexos_expedient) {
//				DocumentNotificacioDto documentAnnex = new DocumentNotificacioDto();
//				documentAnnex.setId(annex_exp.getId());
//				notificacio.getAnnexos().add(documentAnnex);
//			}
//			
//			notificacio.setTipus(DocumentNotificacioTipusEnumDto.ELECTRONICA);
//			notificacio.setInteressatDocumentTipus(InteressatDocumentTipusEnumDto.NIF); //pendent definir form
//			notificacio.setInteressatDocumentNum(anotacio.getInteressatNif());
//			notificacio.setInteressatNom(interessatNom);
//			notificacio.setInteressatLlinatge1(interessatLlinatge1);
//			notificacio.setInteressatLlinatge2(interessatLlinatge2);
//			notificacio.setInteressatPaisCodi(anotacio.getInteressatPaisCodi());
//			notificacio.setInteressatProvinciaCodi(anotacio.getInteressatProvinciaCodi());
//			notificacio.setInteressatMunicipiCodi(anotacio.getInteressatMunicipiCodi());
//			notificacio.setInteressatEmail(anotacio.getInteressatEmail());
//			notificacio.setUnitatAdministrativa(anotacio.getExpedientUnitatAdministrativa());
//			notificacio.setOrganCodi(anotacio.getOrganCodi());
//			notificacio.setOficinaCodi(anotacio.getOficinaCodi());
//			notificacio.setAvisTitol(anotacio.getNotificacioAvisTitol());
//			notificacio.setAvisText(anotacio.getNotificacioAvisText());
//			notificacio.setAvisTextSms(anotacio.getNotificacioAvisTextSms());
//			notificacio.setOficiTitol(anotacio.getNotificacioOficiTitol());
//			notificacio.setOficiText(anotacio.getNotificacioOficiText());
//			notificacio.setIdioma(InteressatIdiomaEnumDto.valueOf(anotacio.getAnotacioIdiomaCodi().toUpperCase()));
//			notificacio.setEnviamentData(null);
//			notificacio.setEnviamentCount(1);
//			notificacio.setEnviamentError(false);
//			notificacio.setEnviamentErrorDescripcio(null);
//			notificacio.setProcessamentData(null); //pendent definir
//			notificacio.setProcessamentCount(0);
//			notificacio.setProcessamentError(false);
//			notificacio.setProcessamentErrorDescripcio(null);
//			notificacio.setRdsCodi(resposta.getReferenciaRDSJustificante().getCodigo());
//			notificacio.setRdsClau(resposta.getReferenciaRDSJustificante().getClave());
//			
//			Jbpm3HeliumBridge.getInstanceService().notificacioGuardar(
//					expedient, 
//					notificacio);
//			List<String> parametres = new ArrayList<String>();
//			
//			DadesNotificacioElectronica dadesNotificacioElectronica = new DadesNotificacioElectronica();
//			dadesNotificacioElectronica.setAnotacio(anotacio);
//			dadesNotificacioElectronica.setAnnexos(annexos_notificacio);			
//			parametres.add(toString(dadesNotificacioElectronica));
//			
//			guardarParametresPerRetrocedir(executionContext,parametres);
//
//			if (varNumeroRegistre != null)
//				executionContext.setVariable(
//						varNumeroRegistre,
//						resposta.getNumero());
//			if (varDataRegistre != null)
//				executionContext.setVariable(
//						varDataRegistre,
//						resposta.getData());		
//			if (varReferenciaRDSJustificanteClave != null)
//				executionContext.setVariable(
//						varReferenciaRDSJustificanteClave,
//						resposta.getReferenciaRDSJustificante().getClave());
//			if (varReferenciaRDSJustificanteCodigo != null)
//				executionContext.setVariable(
//						varReferenciaRDSJustificanteCodigo,
//						resposta.getReferenciaRDSJustificante().getCodigo());
//		}
	}

//	@Override
//	public void retrocedir(ExecutionContext executionContext, List<String> parametres) throws Exception {
//		try {
//			DadesNotificacioElectronica dadesNotificacioElectronica = (DadesNotificacioElectronica) fromString(parametres.get(0));			
//			dadesNotificacioElectronica.getAnotacio().setNotificacioAvisText("La notificación que se realizó anteriormente no es correcta.");
//			RespostaRegistre resposta = registreNotificacio(executionContext,dadesNotificacioElectronica.getAnotacio(),dadesNotificacioElectronica.getAnnexos());
//			if (resposta != null) {
//				boolean borrado = Jbpm3HeliumBridge.getInstanceService().notificacioEsborrar(
//						resposta.getNumero(),
//						resposta.getReferenciaRDSJustificante().getClave(),
//						resposta.getReferenciaRDSJustificante().getCodigo());
//				if (!borrado)
//					throw new JbpmException("No se ha podido borrar la notificación electrónica del expediente");
//			}
//		} catch (Exception ex) {
//			throw new JbpmException("No se ha podido retroceder la notificación electrónica del expediente", ex);
//		}
//	}
	
	private String formatIdentificadorEni(String identificador) {
		String identificadorEni = String.format("%30s", identificador).replace(' ', '0').toUpperCase();
		if (identificadorEni.length() > 30) {
			return identificadorEni.substring(0, 12) + "..." + identificadorEni.substring(identificadorEni.length() - 13);
		}
		return identificadorEni;
	}
    
	private Object fromString(String s) throws IOException, ClassNotFoundException {
		byte[] data = Base64.decode(s.getBytes());
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return o;
	}

	private String toString(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		return new String(Base64.encode(baos.toByteArray()));
	}
	
	
	
	

	public void setEmisorDir3Codi(String emisorDir3Codi) {
		this.emisorDir3Codi = emisorDir3Codi;
	}

	public void setVarEmisorDir3Codi(String varEmisorDir3Codi) {
		this.varEmisorDir3Codi = varEmisorDir3Codi;
	}

	public void setEnviamentTipus(String enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
	}

	public void setVarEnviamentTipus(String varEnviamentTipus) {
		this.varEnviamentTipus = varEnviamentTipus;
	}

	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}

	public void setVarConcepte(String varConcepte) {
		this.varConcepte = varConcepte;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public void setVarDescripcio(String varDescripcio) {
		this.varDescripcio = varDescripcio;
	}

	public void setEnviamentDataProgramada(String enviamentDataProgramada) {
		this.enviamentDataProgramada = enviamentDataProgramada;
	}

	public void setVarEnviamentDataProgramada(String varEnviamentDataProgramada) {
		this.varEnviamentDataProgramada = varEnviamentDataProgramada;
	}

	public void setRetard(String retard) {
		this.retard = retard;
	}

	public void setVarRetard(String varRetard) {
		this.varRetard = varRetard;
	}

	public void setCaducitat(String caducitat) {
		this.caducitat = caducitat;
	}

	public void setVarCaducitat(String varCaducitat) {
		this.varCaducitat = varCaducitat;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public void setVarDocument(String varDocument) {
		this.varDocument = varDocument;
	}

	public void setAnnexos(String annexos) {
		this.annexos = annexos;
	}

	public void setVarAnnexos(String varAnnexos) {
		this.varAnnexos = varAnnexos;
	}

	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}

	public void setVarProcedimentCodi(String varProcedimentCodi) {
		this.varProcedimentCodi = varProcedimentCodi;
	}

	public void setPagadorPostalDir3Codi(String pagadorPostalDir3Codi) {
		this.pagadorPostalDir3Codi = pagadorPostalDir3Codi;
	}

	public void setVarPagadorPostalDir3Codi(String varPagadorPostalDir3Codi) {
		this.varPagadorPostalDir3Codi = varPagadorPostalDir3Codi;
	}

	public void setPagadorPostalContracteNum(String pagadorPostalContracteNum) {
		this.pagadorPostalContracteNum = pagadorPostalContracteNum;
	}

	public void setVarPagadorPostalContracteNum(String varPagadorPostalContracteNum) {
		this.varPagadorPostalContracteNum = varPagadorPostalContracteNum;
	}

	public void setPagadorPostalContracteDataVigencia(String pagadorPostalContracteDataVigencia) {
		this.pagadorPostalContracteDataVigencia = pagadorPostalContracteDataVigencia;
	}

	public void setVarPagadorPostalContracteDataVigencia(String varPagadorPostalContracteDataVigencia) {
		this.varPagadorPostalContracteDataVigencia = varPagadorPostalContracteDataVigencia;
	}

	public void setPagadorPostalFacturacioClientCodi(String pagadorPostalFacturacioClientCodi) {
		this.pagadorPostalFacturacioClientCodi = pagadorPostalFacturacioClientCodi;
	}

	public void setVarPagadorPostalFacturacioClientCodi(String varPagadorPostalFacturacioClientCodi) {
		this.varPagadorPostalFacturacioClientCodi = varPagadorPostalFacturacioClientCodi;
	}

	public void setPagadorCieDir3Codi(String pagadorCieDir3Codi) {
		this.pagadorCieDir3Codi = pagadorCieDir3Codi;
	}

	public void setVarPagadorCieDir3Codi(String varPagadorCieDir3Codi) {
		this.varPagadorCieDir3Codi = varPagadorCieDir3Codi;
	}

	public void setPagadorCieContracteDataVigencia(String pagadorCieContracteDataVigencia) {
		this.pagadorCieContracteDataVigencia = pagadorCieContracteDataVigencia;
	}

	public void setVarPagadorCieContracteDataVigencia(String varPagadorCieContracteDataVigencia) {
		this.varPagadorCieContracteDataVigencia = varPagadorCieContracteDataVigencia;
	}

	public void setSeuProcedimentCodi(String seuProcedimentCodi) {
		this.seuProcedimentCodi = seuProcedimentCodi;
	}

	public void setVarSeuProcedimentCodi(String varSeuProcedimentCodi) {
		this.varSeuProcedimentCodi = varSeuProcedimentCodi;
	}

	public void setSeuExpedientSerieDocumental(String seuExpedientSerieDocumental) {
		this.seuExpedientSerieDocumental = seuExpedientSerieDocumental;
	}

	public void setVarSeuExpedientSerieDocumental(String varSeuExpedientSerieDocumental) {
		this.varSeuExpedientSerieDocumental = varSeuExpedientSerieDocumental;
	}

	public void setSeuExpedientUnitatOrganitzativa(String seuExpedientUnitatOrganitzativa) {
		this.seuExpedientUnitatOrganitzativa = seuExpedientUnitatOrganitzativa;
	}

	public void setVarSeuExpedientUnitatOrganitzativa(String varSeuExpedientUnitatOrganitzativa) {
		this.varSeuExpedientUnitatOrganitzativa = varSeuExpedientUnitatOrganitzativa;
	}

	public void setSeuExpedientIdentificadorEni(String seuExpedientIdentificadorEni) {
		this.seuExpedientIdentificadorEni = seuExpedientIdentificadorEni;
	}

	public void setVarSeuExpedientIdentificadorEni(String varSeuExpedientIdentificadorEni) {
		this.varSeuExpedientIdentificadorEni = varSeuExpedientIdentificadorEni;
	}

	public void setSeuExpedientTitol(String seuExpedientTitol) {
		this.seuExpedientTitol = seuExpedientTitol;
	}

	public void setVarSeuExpedientTitol(String varSeuExpedientTitol) {
		this.varSeuExpedientTitol = varSeuExpedientTitol;
	}

	public void setSeuRegistreOficina(String seuRegistreOficina) {
		this.seuRegistreOficina = seuRegistreOficina;
	}

	public void setVarSeuRegistreOficina(String varSeuRegistreOficina) {
		this.varSeuRegistreOficina = varSeuRegistreOficina;
	}

	public void setSeuRegistreLlibre(String seuRegistreLlibre) {
		this.seuRegistreLlibre = seuRegistreLlibre;
	}

	public void setVarSeuRegistreLlibre(String varSeuRegistreLlibre) {
		this.varSeuRegistreLlibre = varSeuRegistreLlibre;
	}

	public void setSeuRegistreOrgan(String seuRegistreOrgan) {
		this.seuRegistreOrgan = seuRegistreOrgan;
	}

	public void setVarSeuRegistreOrgan(String varSeuRegistreOrgan) {
		this.varSeuRegistreOrgan = varSeuRegistreOrgan;
	}

	public void setSeuIdioma(String seuIdioma) {
		this.seuIdioma = seuIdioma;
	}

	public void setVarSeuIdioma(String varSeuIdioma) {
		this.varSeuIdioma = varSeuIdioma;
	}

	public void setSeuAvisTitol(String seuAvisTitol) {
		this.seuAvisTitol = seuAvisTitol;
	}

	public void setVarSeuAvisTitol(String varSeuAvisTitol) {
		this.varSeuAvisTitol = varSeuAvisTitol;
	}

	public void setSeuAvisText(String seuAvisText) {
		this.seuAvisText = seuAvisText;
	}

	public void setVarSeuAvisText(String varSeuAvisText) {
		this.varSeuAvisText = varSeuAvisText;
	}

	public void setSeuAvisTextMobil(String seuAvisTextMobil) {
		this.seuAvisTextMobil = seuAvisTextMobil;
	}

	public void setVarSeuAvisTextMobil(String varSeuAvisTextMobil) {
		this.varSeuAvisTextMobil = varSeuAvisTextMobil;
	}

	public void setSeuOficiTitol(String seuOficiTitol) {
		this.seuOficiTitol = seuOficiTitol;
	}

	public void setVarSeuOficiTitol(String varSeuOficiTitol) {
		this.varSeuOficiTitol = varSeuOficiTitol;
	}

	public void setSeuOficiText(String seuOficiText) {
		this.seuOficiText = seuOficiText;
	}

	public void setVarSeuOficiText(String varSeuOficiText) {
		this.varSeuOficiText = varSeuOficiText;
	}

	public void setTitularNif(String titularNif) {
		this.titularNif = titularNif;
	}

	public void setVarTitularNif(String varTitularNif) {
		this.varTitularNif = varTitularNif;
	}

	public void setTitularNom(String titularNom) {
		this.titularNom = titularNom;
	}

	public void setVarTitularNom(String varTitularNom) {
		this.varTitularNom = varTitularNom;
	}

	public void setTitularLlin1(String titularLlin1) {
		this.titularLlin1 = titularLlin1;
	}

	public void setVarTitularLlin1(String varTitularLlin1) {
		this.varTitularLlin1 = varTitularLlin1;
	}

	public void setTitularLlin2(String titularLlin2) {
		this.titularLlin2 = titularLlin2;
	}

	public void setVarTitularLlin2(String varTitularLlin2) {
		this.varTitularLlin2 = varTitularLlin2;
	}

	public void setTitularEmail(String titularEmail) {
		this.titularEmail = titularEmail;
	}

	public void setVarTitularEmail(String varTitularEmail) {
		this.varTitularEmail = varTitularEmail;
	}

	public void setTitularMobil(String titularMobil) {
		this.titularMobil = titularMobil;
	}

	public void setVarTitularMobil(String varTitularMobil) {
		this.varTitularMobil = varTitularMobil;
	}

	public void setDestinatariNif(String destinatariNif) {
		this.destinatariNif = destinatariNif;
	}

	public void setVarDestinatariNif(String varDestinatariNif) {
		this.varDestinatariNif = varDestinatariNif;
	}

	public void setDestinatariNom(String destinatariNom) {
		this.destinatariNom = destinatariNom;
	}

	public void setVarDestinatariNom(String varDestinatariNom) {
		this.varDestinatariNom = varDestinatariNom;
	}

	public void setDestinatariLlin1(String destinatariLlin1) {
		this.destinatariLlin1 = destinatariLlin1;
	}

	public void setVarDestinatariLlin1(String varDestinatariLlin1) {
		this.varDestinatariLlin1 = varDestinatariLlin1;
	}

	public void setDestinatariLlin2(String destinatariLlin2) {
		this.destinatariLlin2 = destinatariLlin2;
	}

	public void setVarDestinatariLlin2(String varDestinatariLlin2) {
		this.varDestinatariLlin2 = varDestinatariLlin2;
	}

	public void setDestinatariEmail(String destinatariEmail) {
		this.destinatariEmail = destinatariEmail;
	}

	public void setVarDestinatariEmail(String varDestinatariEmail) {
		this.varDestinatariEmail = varDestinatariEmail;
	}

	public void setDestinatariMobil(String destinatariMobil) {
		this.destinatariMobil = destinatariMobil;
	}

	public void setVarDestinatariMobil(String varDestinatariMobil) {
		this.varDestinatariMobil = varDestinatariMobil;
	}

	public void setEntregaPostalTipus(String entregaPostalTipus) {
		this.entregaPostalTipus = entregaPostalTipus;
	}

	public void setVarEntregaPostalTipus(String varEntregaPostalTipus) {
		this.varEntregaPostalTipus = varEntregaPostalTipus;
	}

	public void setEntregaPostalViaTipus(String entregaPostalViaTipus) {
		this.entregaPostalViaTipus = entregaPostalViaTipus;
	}

	public void setVarEntregaPostalViaTipus(String varEntregaPostalViaTipus) {
		this.varEntregaPostalViaTipus = varEntregaPostalViaTipus;
	}

	public void setEntregaPostalViaNom(String entregaPostalViaNom) {
		this.entregaPostalViaNom = entregaPostalViaNom;
	}

	public void setVarEntregaPostalViaNom(String varEntregaPostalViaNom) {
		this.varEntregaPostalViaNom = varEntregaPostalViaNom;
	}

	public void setEntregaPostalNumeroCasa(String entregaPostalNumeroCasa) {
		this.entregaPostalNumeroCasa = entregaPostalNumeroCasa;
	}

	public void setVarEntregaPostalNumeroCasa(String varEntregaPostalNumeroCasa) {
		this.varEntregaPostalNumeroCasa = varEntregaPostalNumeroCasa;
	}

	public void setEntregaPostalNumeroQualificador(String entregaPostalNumeroQualificador) {
		this.entregaPostalNumeroQualificador = entregaPostalNumeroQualificador;
	}

	public void setVarEntregaPostalNumeroQualificador(String varEntregaPostalNumeroQualificador) {
		this.varEntregaPostalNumeroQualificador = varEntregaPostalNumeroQualificador;
	}

	public void setEntregaPostalPuntKm(String entregaPostalPuntKm) {
		this.entregaPostalPuntKm = entregaPostalPuntKm;
	}

	public void setVarEntregaPostalPuntKm(String varEntregaPostalPuntKm) {
		this.varEntregaPostalPuntKm = varEntregaPostalPuntKm;
	}

	public void setEntregaPostalApartatCorreus(String entregaPostalApartatCorreus) {
		this.entregaPostalApartatCorreus = entregaPostalApartatCorreus;
	}

	public void setVarEntregaPostalApartatCorreus(String varEntregaPostalApartatCorreus) {
		this.varEntregaPostalApartatCorreus = varEntregaPostalApartatCorreus;
	}

	public void setEntregaPostalPortal(String entregaPostalPortal) {
		this.entregaPostalPortal = entregaPostalPortal;
	}

	public void setVarEntregaPostalPortal(String varEntregaPostalPortal) {
		this.varEntregaPostalPortal = varEntregaPostalPortal;
	}

	public void setEntregaPostalEscala(String entregaPostalEscala) {
		this.entregaPostalEscala = entregaPostalEscala;
	}

	public void setVarEntregaPostalEscala(String varEntregaPostalEscala) {
		this.varEntregaPostalEscala = varEntregaPostalEscala;
	}

	public void setEntregaPostalPlanta(String entregaPostalPlanta) {
		this.entregaPostalPlanta = entregaPostalPlanta;
	}

	public void setVarEntregaPostalPlanta(String varEntregaPostalPlanta) {
		this.varEntregaPostalPlanta = varEntregaPostalPlanta;
	}

	public void setEntregaPostalPorta(String entregaPostalPorta) {
		this.entregaPostalPorta = entregaPostalPorta;
	}

	public void setVarEntregaPostalPorta(String varEntregaPostalPorta) {
		this.varEntregaPostalPorta = varEntregaPostalPorta;
	}

	public void setEntregaPostalBloc(String entregaPostalBloc) {
		this.entregaPostalBloc = entregaPostalBloc;
	}

	public void setVarEntregaPostalBloc(String varEntregaPostalBloc) {
		this.varEntregaPostalBloc = varEntregaPostalBloc;
	}

	public void setEntregaPostalComplement(String entregaPostalComplement) {
		this.entregaPostalComplement = entregaPostalComplement;
	}

	public void setVarEntregaPostalComplement(String varEntregaPostalComplement) {
		this.varEntregaPostalComplement = varEntregaPostalComplement;
	}

	public void setEntregaPostalCodiPostal(String entregaPostalCodiPostal) {
		this.entregaPostalCodiPostal = entregaPostalCodiPostal;
	}

	public void setVarEntregaPostalCodiPostal(String varEntregaPostalCodiPostal) {
		this.varEntregaPostalCodiPostal = varEntregaPostalCodiPostal;
	}

	public void setEntregaPostalPoblacio(String entregaPostalPoblacio) {
		this.entregaPostalPoblacio = entregaPostalPoblacio;
	}

	public void setVarEntregaPostalPoblacio(String varEntregaPostalPoblacio) {
		this.varEntregaPostalPoblacio = varEntregaPostalPoblacio;
	}

	public void setEntregaPostalMunicipiCodi(String entregaPostalMunicipiCodi) {
		this.entregaPostalMunicipiCodi = entregaPostalMunicipiCodi;
	}

	public void setVarEntregaPostalMunicipiCodi(String varEntregaPostalMunicipiCodi) {
		this.varEntregaPostalMunicipiCodi = varEntregaPostalMunicipiCodi;
	}

	public void setEntregaPostalProvinciaCodi(String entregaPostalProvinciaCodi) {
		this.entregaPostalProvinciaCodi = entregaPostalProvinciaCodi;
	}

	public void setVarEntregaPostalProvinciaCodi(String varEntregaPostalProvinciaCodi) {
		this.varEntregaPostalProvinciaCodi = varEntregaPostalProvinciaCodi;
	}

	public void setEntregaPostalPaisCodi(String entregaPostalPaisCodi) {
		this.entregaPostalPaisCodi = entregaPostalPaisCodi;
	}

	public void setVarEntregaPostalPaisCodi(String varEntregaPostalPaisCodi) {
		this.varEntregaPostalPaisCodi = varEntregaPostalPaisCodi;
	}

	public void setEntregaPostalLinea1(String entregaPostalLinea1) {
		this.entregaPostalLinea1 = entregaPostalLinea1;
	}

	public void setVarEntregaPostalLinea1(String varEntregaPostalLinea1) {
		this.varEntregaPostalLinea1 = varEntregaPostalLinea1;
	}

	public void setEntregaPostalLinea2(String entregaPostalLinea2) {
		this.entregaPostalLinea2 = entregaPostalLinea2;
	}

	public void setVarEntregaPostalLinea2(String varEntregaPostalLinea2) {
		this.varEntregaPostalLinea2 = varEntregaPostalLinea2;
	}

	public void setEntregaPostalCie(String entregaPostalCie) {
		this.entregaPostalCie = entregaPostalCie;
	}

	public void setVarEntregaPostalCie(String varEntregaPostalCie) {
		this.varEntregaPostalCie = varEntregaPostalCie;
	}

	public void setEntregaPostalFormatSobre(String entregaPostalFormatSobre) {
		this.entregaPostalFormatSobre = entregaPostalFormatSobre;
	}

	public void setVarEntregaPostalFormatSobre(String varEntregaPostalFormatSobre) {
		this.varEntregaPostalFormatSobre = varEntregaPostalFormatSobre;
	}

	public void setEntregaPostalFormatFulla(String entregaPostalFormatFulla) {
		this.entregaPostalFormatFulla = entregaPostalFormatFulla;
	}

	public void setVarEntregaPostalFormatFulla(String varEntregaPostalFormatFulla) {
		this.varEntregaPostalFormatFulla = varEntregaPostalFormatFulla;
	}

	public void setEntregaDehObligat(String entregaDehObligat) {
		this.entregaDehObligat = entregaDehObligat;
	}

	public void setVarEntregaDehObligat(String varEntregaDehObligat) {
		this.varEntregaDehObligat = varEntregaDehObligat;
	}

	public void setEntregaDehProcedimentCodi(String entregaDehProcedimentCodi) {
		this.entregaDehProcedimentCodi = entregaDehProcedimentCodi;
	}

	public void setVarEntregaDehProcedimentCodi(String varEntregaDehProcedimentCodi) {
		this.varEntregaDehProcedimentCodi = varEntregaDehProcedimentCodi;
	}

}