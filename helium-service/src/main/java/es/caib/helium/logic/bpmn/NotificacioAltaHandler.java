package es.caib.helium.logic.bpmn;

import es.caib.helium.commons.dto.*;
import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.BpmnException;
import es.caib.helium.disseny.model.DocumentInfo;
import es.caib.helium.disseny.model.ExpedientInfo;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.crypto.codec.Base64;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Handler per a interactuar amb el registre de sortida.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class NotificacioAltaHandler implements es.caib.helium.disseny.handler.NotificacioAltaHandler {

	private String caducitat;
	private String varCaducitat;

	private String concepte;
	private String varConcepte;

	private String serveiTipus;
	private String varServeiTipus;

	private String grupCodi;
	private String varGrupCodi;

	private String descripcio;
	private String varDescripcio;

	private String document;
	private String varDocument;

	private String emisorDir3Codi;
	private String varEmisorDir3Codi;

	private String destinatariDir3Codi;
	private String varDestinatariDir3Codi;

	private String enviamentTipus;	// Possibles valors: [NOTIFICACIO, COMUNICACIO]
	private String varEnviamentTipus;

	private String enviamentDataProgramada;
	private String varEnviamentDataProgramada;

	private String retard;
	private String varRetard;


	private String procedimentCodi;
	private String varProcedimentCodi;

	private String idioma;	// Possibles valors [ES, CA]
	private String varIdioma;


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

	private String titularCodiDir3;
	private String varTitularCodiDir3;

	private String titularTipus;
	private String varTitularTipus;

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

	private String destinatariCodiDir3;
	private String varDestinatariCodiDir3;

	private String destinatariTipus;
	private String varDestinatariTipus;

	// Dades d'entrega
	private String entregaPostalActiva;
	private String varEntregaPostalActiva;

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

	private String entregaDehActiva;
	private String varEntregaDehActiva;

	private String entregaDehObligat;
	private String varEntregaDehObligat;

	private String entregaDehProcedimentCodi;
	private String varEntregaDehProcedimentCodi;


	private String varsServeiTipus;
	private String titularDir3Codi;
	private String varTitularDir3Codi;
	private String vardestinatariNif;
	private String vardestinatariDir3Codi;
	private String vardestinatariNom;
	private String vardestinatariLlin1;
	private String vardestinatariLlin2;
	private String vardestinatariEmail;
	private String vardestinatariMobil;


	public void execute(HeliumApi heliumApi) throws BpmnException {
		ExpedientInfo expedient = heliumApi.getExpedientInfo();

		String expedientTitol = expedient.getTitol();
		String notibEmisor = expedient.getNotibEmisor();
		String notibProcedimentCodi = expedient.getNotibCodiProcediment();

		DadesNotificacioDto dadesNotificacio = new DadesNotificacioDto();

		String emisorCodi = (String) heliumApi.getVariableDefaultValue(varEmisorDir3Codi, emisorDir3Codi);
		dadesNotificacio.setEmisorDir3Codi(emisorCodi != null ? emisorCodi : notibEmisor);

		String strTipusEnviament = (String) heliumApi.getVariableDefaultValue(varEnviamentTipus, enviamentTipus);

		EnviamentTipusEnumDto enviamentTipus = "COMUNICACIO".equalsIgnoreCase(strTipusEnviament) ?
			EnviamentTipusEnumDto.COMUNICACIO : EnviamentTipusEnumDto.NOTIFICACIO;

		dadesNotificacio.setEnviamentTipus(enviamentTipus);

		dadesNotificacio.setConcepte((String) heliumApi.getVariableDefaultValue(
			varConcepte,
			concepte));

		String serveiTipusStr = (String) heliumApi.getVariableDefaultValue(
			varServeiTipus,
			serveiTipus);
		if(serveiTipusStr != null)
			dadesNotificacio.setServeiTipusEnum(ServeiTipusEnumDto.valueOf(serveiTipusStr));

		dadesNotificacio.setGrupCodi((String) heliumApi.getVariableDefaultValue(
			varGrupCodi,
			grupCodi));

		dadesNotificacio.setDescripcio((String) heliumApi.getVariableDefaultValue(
			varDescripcio,
			descripcio));

		dadesNotificacio.setEnviamentDataProgramada(heliumApi.getVariableDefaultValueAsDate(
			varEnviamentDataProgramada,
			enviamentDataProgramada));
		dadesNotificacio.setRetard(heliumApi.getVariableDefaultValueAsInteger(
			varRetard,
			retard));
		dadesNotificacio.setCaducitat(heliumApi.getVariableDefaultValueAsDate(
			varCaducitat,
			caducitat));

		String idioma = (String) heliumApi.getVariableDefaultValue(
			varIdioma,
			this.idioma);
		if (idioma != null) {
			try {
				dadesNotificacio.setIdioma(IdiomaEnumDto.valueOf(idioma));
			} catch(Exception e) {
				throw new BpmnException("No es reconeix l'idioma \"" + idioma.toString() + "\" per la notificació, els únics valors admesos són \"ES\" i \"CA\".");
			}
		}
		DocumentInfo documentInfo = null;
		List<DocumentInfo> annexos_notificacio = new ArrayList<DocumentInfo>();

		String doc = (String)heliumApi.getVariableDefaultValue(
			varDocument,
			document);
		boolean multiDocumentsZip = false;
		if (doc != null && !doc.isEmpty()) {
			List<DocumentInfo> documentsPerNotificar = new ArrayList<DocumentInfo>();
			String[] codis = doc.split(",");
			if(codis.length > 1) {
				multiDocumentsZip = true;
				for (String codi: codis) {
					codi = StringUtils.replace(StringUtils.trim(codi), "\\t", "");
						DocumentInfo docInfo = heliumApi.getDocumentInfo(codi, true);
						documentsPerNotificar.add(docInfo);
					}

				Long documentStoreId = null;
				byte[] contingut = null;
				contingut = getZipPerNotificar(expedient, documentsPerNotificar);
				String documentCodi = "Notificació " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
				String arxiuNom = documentCodi+".zip";
				DocumentInfo document = heliumApi.setDocument(
					null,
					arxiuNom,
					contingut,
					new Date(),
					false,
		false,
		null);
//				ExpedientDocumentDto expDocDto = Jbpm3HeliumBridge.getInstanceService().findOneAmbInstanciaProces(expedient.getId(), expedient.getProcessInstanceId(), documentStoreId);
//				ArxiuDto arxiu = Jbpm3HeliumBridge.getInstanceService().arxiuFindAmbDocument(
//					expedient.getId(),
//					expedient.getProcessInstanceId(),
//					documentStoreId);
				dadesNotificacio.setDocumentId(documentStoreId);
				dadesNotificacio.setDocumentArxiuNom(arxiuNom);
				dadesNotificacio.setDocumentArxiuContingut(contingut);
				dadesNotificacio.setDocumentArxiuUuid(document.getArxiuUuid());
			} else {
				// Un sol document
				documentInfo = heliumApi.getDocumentInfo(doc);
				try {
					dadesNotificacio.setDocumentId(documentInfo.getId());
				} catch (Exception e) {
					throw new BpmnException("No existeix cap document amb documentCodi: " + doc + ".");
				}
				dadesNotificacio.setDocumentArxiuNom(documentInfo.getArxiuNom());
				dadesNotificacio.setDocumentArxiuContingut(documentInfo.getArxiuContingut());
				dadesNotificacio.setDocumentArxiuUuid(documentInfo.getArxiuUuid());
			}
		} else {
			throw new BpmnException("No s'ha informat cap document per notificar.");
		}

		String codiProcediment = (String) heliumApi.getVariableDefaultValue(
			varProcedimentCodi,
			procedimentCodi);
		dadesNotificacio.setProcedimentCodi(codiProcediment != null ? codiProcediment : notibProcedimentCodi);



		// Identificador expedient
		String identificador = expedient.getId().toString();
		String identificadorEni = expedient.getTramitExpedientIdentificador();

		if (identificadorEni == null || identificadorEni.isEmpty()) {
			identificadorEni = expedient.getNtiIdentificador();
		}
		if (identificadorEni == null || identificadorEni.isEmpty()) {
			DateFormat sdf = new SimpleDateFormat("YYYY");
			identificadorEni = 	"ES_"
				+ notibProcedimentCodi + "_"
				+ (expedient.getDataInici() != null ? sdf.format(expedient.getDataInici()) : sdf.format(new Date())) +
				"_EXP_"
				+ formatIdentificadorEni(expedient.getNumeroIdentificador());
		}



		List<DadesEnviamentDto> enviaments = new ArrayList<DadesEnviamentDto>();
		DadesEnviamentDto enviament = new DadesEnviamentDto();

		PersonaDto titular = new PersonaDto();
		titular.setNom((String)heliumApi.getVariableDefaultValue(
			varTitularNom,
			titularNom));
		titular.setLlinatge1((String)heliumApi.getVariableDefaultValue(
			varTitularLlin1,
			titularLlin1));
		titular.setLlinatge2((String)heliumApi.getVariableDefaultValue(
			varTitularLlin2,
			titularLlin2));
		titular.setDni((String)heliumApi.getVariableDefaultValue(
			varTitularNif,
			titularNif));
		titular.setCodiDir3((String)heliumApi.getVariableDefaultValue(
			varTitularCodiDir3,
			titularCodiDir3));
		titular.setTipus(InteressatTipusEnumDto.valueOf((String)heliumApi.getVariableDefaultValue(
			varTitularTipus,
			titularTipus)));
		String mobil = (String)heliumApi.getVariableDefaultValue(
			varTitularMobil,
			titularMobil);
		if (mobil == null || mobil.isEmpty())
			mobil = expedient.getAvisosMobil();
		titular.setTelefon(mobil);
		String email = (String)heliumApi.getVariableDefaultValue(
			varTitularEmail,
			titularEmail);
		if (email == null || email.isEmpty())
			email = expedient.getAvisosEmail();
		titular.setEmail(email);
		enviament.setTitular(titular);

		List<PersonaDto> destinataris = new ArrayList<PersonaDto>();
		PersonaDto destinatari = new PersonaDto();
		destinatari.setNom((String)heliumApi.getVariableDefaultValue(
			varDestinatariNom,
			destinatariNom));
		destinatari.setLlinatge1((String)heliumApi.getVariableDefaultValue(
			varDestinatariLlin1,
			destinatariLlin1));
		destinatari.setLlinatge2((String)heliumApi.getVariableDefaultValue(
			varDestinatariLlin2,
			destinatariLlin2));
		destinatari.setDni((String)heliumApi.getVariableDefaultValue(
			varDestinatariNif,
			destinatariNif));
		destinatari.setTelefon((String)heliumApi.getVariableDefaultValue(
			varDestinatariMobil,
			destinatariMobil));
		destinatari.setEmail((String)heliumApi.getVariableDefaultValue(
			varDestinatariEmail,
			destinatariEmail));
		destinatari.setCodiDir3((String)heliumApi.getVariableDefaultValue(
			varDestinatariCodiDir3,
			destinatariCodiDir3));

		String destinatariTipusVal = (String)heliumApi.getVariableDefaultValue(
			varDestinatariTipus,
			destinatariTipus);

		destinatari.setTipus(destinatariTipusVal != null? InteressatTipusEnumDto.valueOf(destinatariTipusVal) : null);
		// El destinatari no és obligatori, s'afegeix si s'ha escollit el tipus
		if (destinatari.getTipus() != null) {
			destinataris.add(destinatari);
		}
		enviament.setDestinataris(destinataris);

		String strEntragaPostalTipus = (String)heliumApi.getVariableDefaultValue(
			varEntregaPostalTipus,
			entregaPostalTipus);

		if (strEntragaPostalTipus != null && !strEntragaPostalTipus.isEmpty())
			enviament.setEntregaPostalTipus(DadesEnviamentDto.EntregaPostalTipus.valueOf(strEntragaPostalTipus));

		Boolean entregaPostalActivaVal = heliumApi.getVariableDefaultValueAsBoolean(
			varEntregaPostalActiva,
			entregaPostalActiva);
		enviament.setEntregaPostalActiva(false); //(entregaPostalActivaVal != null ? entregaPostalActivaVal.booleanValue() : false); //Forcem false issue #1675
		String strEntregaPostalViaTipus = (String)heliumApi.getVariableDefaultValue(
			varEntregaPostalViaTipus,
			entregaPostalViaTipus);
		if (strEntregaPostalViaTipus != null && !strEntregaPostalViaTipus.isEmpty())
			enviament.setEntregaPostalViaTipus( DadesEnviamentDto.EntregaPostalViaTipus.valueOf(strEntregaPostalViaTipus));
		enviament.setEntregaPostalViaNom((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalViaNom,
			entregaPostalViaNom));
		enviament.setEntregaPostalNumeroCasa((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalNumeroCasa,
			entregaPostalNumeroCasa));
		enviament.setEntregaPostalNumeroQualificador((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalNumeroQualificador,
			entregaPostalNumeroQualificador));
		enviament.setEntregaPostalPuntKm((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalPuntKm,
			entregaPostalPuntKm));
		enviament.setEntregaPostalApartatCorreus((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalApartatCorreus,
			entregaPostalApartatCorreus));
		enviament.setEntregaPostalPortal((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalPortal,
			entregaPostalPortal));
		enviament.setEntregaPostalEscala((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalEscala,
			entregaPostalEscala));
		enviament.setEntregaPostalPlanta((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalPlanta,
			entregaPostalPlanta));
		enviament.setEntregaPostalPorta((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalPorta,
			entregaPostalPorta));
		enviament.setEntregaPostalBloc((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalBloc,
			entregaPostalBloc));
		enviament.setEntregaPostalComplement((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalComplement,
			entregaPostalComplement));
		enviament.setEntregaPostalCodiPostal((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalCodiPostal,
			entregaPostalCodiPostal));
		enviament.setEntregaPostalPoblacio((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalPoblacio,
			entregaPostalPoblacio));
		enviament.setEntregaPostalMunicipiCodi((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalMunicipiCodi,
			entregaPostalMunicipiCodi));
		enviament.setEntregaPostalProvinciaCodi((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalProvinciaCodi,
			entregaPostalProvinciaCodi));
		enviament.setEntregaPostalPaisCodi((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalPaisCodi,
			entregaPostalPaisCodi));
		enviament.setEntregaPostalLinea1((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalLinea1,
			entregaPostalLinea1));
		enviament.setEntregaPostalLinea2((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalLinea2,
			entregaPostalLinea2));
		enviament.setEntregaPostalCie(heliumApi.getVariableDefaultValueAsInteger(
			varEntregaPostalCie,
			entregaPostalCie));
		enviament.setEntregaPostalFormatSobre((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalFormatSobre,
			entregaPostalFormatSobre));
		enviament.setEntregaPostalFormatFulla((String)heliumApi.getVariableDefaultValue(
			varEntregaPostalFormatFulla,
			entregaPostalFormatFulla));
		Boolean entregaDehActivaVal = heliumApi.getVariableDefaultValueAsBoolean(
			varEntregaDehActiva,
			entregaDehActiva);
		enviament.setEntregaDehActiva(entregaDehActivaVal != null? entregaDehActivaVal.booleanValue() : false );
		Boolean entregaDehObligatVal = heliumApi.getVariableDefaultValueAsBoolean(
			varEntregaDehObligat,
			entregaDehObligat);
		enviament.setEntregaDehObligat(entregaDehObligatVal != null ? entregaDehObligatVal : false);
		enviament.setEntregaDehProcedimentCodi((String)heliumApi.getVariableDefaultValue(
			varEntregaDehProcedimentCodi,
			entregaDehProcedimentCodi));

		enviaments.add(enviament);
		dadesNotificacio.setEnviaments(enviaments);

		heliumApi.notificacioCrear(dadesNotificacio);

		if(multiDocumentsZip) {//Si és un zip el signem
			try {
//				heliumApi.documentFirmaServidor(
//					heliumApi.getProcessInstance().getId(),
//					null,
//					dadesNotificacio.getDocumentArxiuNom(),
//					dadesNotificacio.getDocumentArxiuContingut(),
//					dadesNotificacio.getDocumentId());
			} catch(Exception e) {
				//posar alerta a l'expedient
				heliumApi.alertaCrear(null, "No s'ha pogut signar el zip notificat " + dadesNotificacio.getDocumentArxiuNom());
				throw new BpmnException("No s'ha pogut completar la notificació i signatura del zip " + dadesNotificacio.getDocumentArxiuNom() + ".");
			}
		}
	}

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

	private byte[] getZipPerNotificar(ExpedientInfo expedient, List<DocumentInfo> documentsPerAfegir) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(baos);
		ZipEntry ze;
		try {
			// Consulta l'arbre de processos
			// Llistat de noms dins del zip per no repetir-los.
			Set<String> nomsArxius = new HashSet<String>();
				// Per cada instancia de proces consulta els documents.
			for (DocumentInfo document : documentsPerAfegir) {
				// Consulta l'arxiu del document
				// Crea l'entrada en el zip
				String recursNom = this.getZipRecursNom(
					expedient,
					document,
					nomsArxius);
				ze = new ZipEntry(recursNom);
				out.putNextEntry(ze);
				out.write(document.getArxiuContingut());
				out.closeEntry();
			}
			out.close();
		} catch (Exception e) {
			String errMsg = "Error construint el zip dels documents per notificar " + expedient.getNumeroIdentificador() + ": " + e.getMessage();
			throw new RuntimeException(errMsg, e);
		}
		return baos.toByteArray();
	}

	private String getZipRecursNom(ExpedientInfo expedient,
								   DocumentInfo document,
								   Set<String> nomsArxius) {
		String recursNom;
		// Nom
		String nom = document.getTitol().replaceAll("/", "_");
		// Carpeta
		String carpeta = null;
		if (!document.getProcessInstanceId().equals(expedient.getProcessInstanceId())) {
			// Carpeta per un altre procés
			carpeta = (document.getProcessInstanceId() + " - " + document.getProcessInstanceTitol())
						.replaceAll("/", "_");
		}
		// Extensió
		String extensio = document.getArxiuExtensio();

		// Vigila que no es repeteixi
		int comptador = 0;
		do {
			recursNom = (carpeta != null ? carpeta + "/" : "") +
				nom +
				(comptador > 0 ? " (" + comptador + ")" : "") +
				"." + extensio;
			comptador++;
		} while (nomsArxius.contains(recursNom));

		// Guarda en nom com a utiltizat
		nomsArxius.add(recursNom);
		return recursNom;
	}
}
