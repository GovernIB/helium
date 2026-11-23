package es.caib.helium.logic.bpmn;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.model.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.crypto.codec.Base64;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.BpmnException;
import lombok.Setter;

/**
 * Handler per donar d'alta una notificació al Notib.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class NotificacioAltaHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.NotificacioAltaHandler {

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


	public void execute(HeliumApi heliumApi) throws BpmnException {
		ExpedientInfo expedient = heliumApi.getExpedientInfo();
		ExpedientTipusInfo expedientTipus = heliumApi.getExpedientTipusInfo();
		String notibEmisor = expedientTipus.getNotibEmisor();
		String notibProcedimentCodi = expedientTipus.getNotibCodiProcediment();

		DadesNotificacio dadesNotificacio = new DadesNotificacio();

		String emisorCodi = emisorDir3Codi;
		dadesNotificacio.setEmisorDir3Codi(emisorCodi != null ? emisorCodi : notibEmisor);

		String strTipusEnviament = enviamentTipus;

		DadesNotificacio.EnviamentTipus enviamentTipus =
				"COMUNICACIO".equalsIgnoreCase(strTipusEnviament) ?
						DadesNotificacio.EnviamentTipus.COMUNICACIO
						: DadesNotificacio.EnviamentTipus.NOTIFICACIO;

		dadesNotificacio.setEnviamentTipus(enviamentTipus);

		dadesNotificacio.setConcepte(heliumApi.getVariableDefaultValue(varConcepte, concepte));

		String serveiTipusStr = heliumApi.getVariableDefaultValue(varServeiTipus, serveiTipus);
		if(serveiTipusStr != null)
			dadesNotificacio.setServeiTipus(serveiTipusStr);

		dadesNotificacio.setGrupCodi(heliumApi.getVariableDefaultValue(varGrupCodi, grupCodi));

		dadesNotificacio.setDescripcio(heliumApi.getVariableDefaultValue(varDescripcio, descripcio));

		dadesNotificacio.setEnviamentDataProgramada(asDate(enviamentDataProgramada));
		dadesNotificacio.setRetard(heliumApi.getVariableDefaultValueAsInteger(varRetard, asInteger(retard)));
		dadesNotificacio.setCaducitat(heliumApi.getVariableDefaultValueAsDate(varCaducitat, asDate(caducitat)));

		String idioma = this.idioma;
		if (idioma != null) {
			try {
				dadesNotificacio.setIdioma(DadesNotificacio.Idioma.valueOf(idioma));
			} catch(Exception e) {
				throw new BpmnException("No es reconeix l'idioma \"" + idioma.toString() + "\" per la notificació, els únics valors admesos són \"ES\" i \"CA\".");
			}
		}
		DocumentInfo documentInfo = null;
		List<DocumentInfo> annexos_notificacio = new ArrayList<DocumentInfo>();

		String doc = document;
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

		String codiProcediment = procedimentCodi;
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
				+ formatIdentificadorEni(expedient.getNumero());
		}

		List<DadesEnviament> enviaments = new ArrayList<DadesEnviament>();
		DadesEnviament enviament = new DadesEnviament();

		PersonaInfo titular = new PersonaInfo();
		titular.setNom(titularNom);
		titular.setLlinatge1(titularLlin1);
		titular.setLlinatge2(titularLlin2);
		titular.setDni(titularNif);
		titular.setCodiDir3(titularCodiDir3);

		titular.setTipus(titularTipus);
		String mobil = titularMobil;
		if (mobil == null || mobil.isEmpty())
			mobil = expedient.getAvisosMobil();
		titular.setTelefon(mobil);
		String email = titularEmail;
		if (email == null || email.isEmpty())
			email = expedient.getAvisosEmail();
		titular.setEmail(email);
		enviament.setTitular(titular);

		List<PersonaInfo> destinataris = new ArrayList<PersonaInfo>();
		PersonaInfo destinatari = new PersonaInfo();
		destinatari.setNom(destinatariNom);
		destinatari.setLlinatge1(destinatariLlin1);
		destinatari.setLlinatge2(destinatariLlin2);
		destinatari.setDni(destinatariNif);
		destinatari.setTelefon(destinatariMobil);
		destinatari.setEmail(destinatariEmail);
		destinatari.setCodiDir3(destinatariCodiDir3);
		destinatari.setTipus(destinatariTipus);
		// El destinatari no és obligatori, s'afegeix si s'ha escollit el tipus
		if (destinatari.getTipus() != null) {
			destinataris.add(destinatari);
		}
		enviament.setDestinataris(destinataris);

		String strEntragaPostalTipus = entregaPostalTipus;

		if (strEntragaPostalTipus != null && !strEntragaPostalTipus.isEmpty())
			enviament.setEntregaPostalTipus(DadesEnviament.EntregaPostalTipus.valueOf(strEntragaPostalTipus));

		Boolean entregaPostalActivaVal = asBoolean(entregaPostalActiva);
		enviament.setEntregaPostalActiva(false); //(entregaPostalActivaVal != null ? entregaPostalActivaVal.booleanValue() : false); //Forcem false issue #1675
		String strEntregaPostalViaTipus = entregaPostalViaTipus;
		if (strEntregaPostalViaTipus != null && !strEntregaPostalViaTipus.isEmpty())
			enviament.setEntregaPostalViaTipus(DadesEnviament.EntregaPostalViaTipus.valueOf(strEntregaPostalViaTipus));
		enviament.setEntregaPostalViaNom(entregaPostalViaNom);
		enviament.setEntregaPostalNumeroCasa(entregaPostalNumeroCasa);
		enviament.setEntregaPostalNumeroQualificador(entregaPostalNumeroQualificador);
		enviament.setEntregaPostalPuntKm(entregaPostalPuntKm);
		enviament.setEntregaPostalApartatCorreus(entregaPostalApartatCorreus);
		enviament.setEntregaPostalPortal(entregaPostalPortal);
		enviament.setEntregaPostalEscala(entregaPostalEscala);
		enviament.setEntregaPostalPlanta(entregaPostalPlanta);
		enviament.setEntregaPostalPorta(entregaPostalPorta);
		enviament.setEntregaPostalBloc(entregaPostalBloc);
		enviament.setEntregaPostalComplement(entregaPostalComplement);
		enviament.setEntregaPostalCodiPostal(entregaPostalCodiPostal);
		enviament.setEntregaPostalPoblacio(entregaPostalPoblacio);
		enviament.setEntregaPostalMunicipiCodi(entregaPostalMunicipiCodi);
		enviament.setEntregaPostalProvinciaCodi(entregaPostalProvinciaCodi);
		enviament.setEntregaPostalPaisCodi(entregaPostalPaisCodi);
		enviament.setEntregaPostalLinea1(entregaPostalLinea1);
		enviament.setEntregaPostalLinea2(entregaPostalLinea2);
		enviament.setEntregaPostalCie(asInteger(entregaPostalCie));
		enviament.setEntregaPostalFormatSobre(entregaPostalFormatSobre);
		enviament.setEntregaPostalFormatFulla(entregaPostalFormatFulla);
		Boolean entregaDehActivaVal = asBoolean(entregaDehActiva);
		enviament.setEntregaDehActiva(entregaDehActivaVal != null? entregaDehActivaVal.booleanValue() : false );
		Boolean entregaDehObligatVal = asBoolean(entregaDehObligat);
		enviament.setEntregaDehObligat(entregaDehObligatVal != null ? entregaDehObligatVal : false);
		enviament.setEntregaDehProcedimentCodi(entregaDehProcedimentCodi);

		enviaments.add(enviament);
		dadesNotificacio.setEnviaments(enviaments);

		heliumApi.notificacioCrear(expedient.getId(), dadesNotificacio);

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
			String errMsg = "Error construint el zip dels documents per notificar " + expedient.getNumero() + ": " + e.getMessage();
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
