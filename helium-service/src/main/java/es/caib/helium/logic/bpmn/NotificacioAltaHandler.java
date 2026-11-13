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
import org.flowable.common.engine.api.delegate.Expression;
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

	private Expression caducitat;
	private Expression varCaducitat;

	private Expression concepte;
	private Expression varConcepte;

	private Expression serveiTipus;
	private Expression varServeiTipus;

	private Expression grupCodi;
	private Expression varGrupCodi;

	private Expression descripcio;
	private Expression varDescripcio;

	private Expression document;
	private Expression varDocument;

	private Expression emisorDir3Codi;
	private Expression varEmisorDir3Codi;

	private Expression destinatariDir3Codi;
	private Expression varDestinatariDir3Codi;

	private Expression enviamentTipus;	// Possibles valors: [NOTIFICACIO, COMUNICACIO]
	private Expression varEnviamentTipus;

	private Expression enviamentDataProgramada;
	private Expression varEnviamentDataProgramada;

	private Expression retard;
	private Expression varRetard;


	private Expression procedimentCodi;
	private Expression varProcedimentCodi;

	private Expression idioma;	// Possibles valors [ES, CA]
	private Expression varIdioma;


	// ENVIAMENT
	// Titular
	private Expression titularNif;
	private Expression varTitularNif;

	private Expression titularNom;
	private Expression varTitularNom;

	private Expression titularLlin1;
	private Expression varTitularLlin1;

	private Expression titularLlin2;
	private Expression varTitularLlin2;

	private Expression titularEmail;
	private Expression varTitularEmail;

	private Expression titularMobil;
	private Expression varTitularMobil;

	private Expression titularCodiDir3;
	private Expression varTitularCodiDir3;

	private Expression titularTipus;
	private Expression varTitularTipus;

	// Destinatari
	private Expression destinatariNif;
	private Expression varDestinatariNif;

	private Expression destinatariNom;
	private Expression varDestinatariNom;

	private Expression destinatariLlin1;
	private Expression varDestinatariLlin1;

	private Expression destinatariLlin2;
	private Expression varDestinatariLlin2;

	private Expression destinatariEmail;
	private Expression varDestinatariEmail;

	private Expression destinatariMobil;
	private Expression varDestinatariMobil;

	private Expression destinatariCodiDir3;
	private Expression varDestinatariCodiDir3;

	private Expression destinatariTipus;
	private Expression varDestinatariTipus;

	// Dades d'entrega
	private Expression entregaPostalActiva;
	private Expression varEntregaPostalActiva;

	private Expression entregaPostalTipus; // Possibles valors: [NACIONAL, ESTRANGER, APARTAT_CORREUS, SENSE_NORMALITZAR]
	private Expression varEntregaPostalTipus;

	private Expression  entregaPostalViaTipus; // Possibles valors: [ALAMEDA, CALLE, CAMINO, CARRER, CARRETERA, GLORIETA, KALEA, PASAJE, PASEO, PLAÇA, PLAZA, RAMBLA, RONDA,	RUA, SECTOR, TRAVESIA, URBANIZACION, AVENIDA, AVINGUDA, BARRIO, CALLEJA, CAMI, CAMPO, CARRERA, CUESTA, EDIFICIO, ENPARANTZA, ESTRADA, JARDINES, JARDINS, PARQUE, PASSEIG, PRAZA, PLAZUELA, PLACETA, POBLADO, VIA, TRAVESSERA, PASSATGE, BULEVAR, POLIGONO, OTROS]
	private Expression  varEntregaPostalViaTipus;

	private Expression entregaPostalViaNom;
	private Expression varEntregaPostalViaNom;

	private Expression entregaPostalNumeroCasa;
	private Expression varEntregaPostalNumeroCasa;

	private Expression entregaPostalNumeroQualificador;
	private Expression varEntregaPostalNumeroQualificador;

	private Expression entregaPostalPuntKm;
	private Expression varEntregaPostalPuntKm;

	private Expression entregaPostalApartatCorreus;
	private Expression varEntregaPostalApartatCorreus;

	private Expression entregaPostalPortal;
	private Expression varEntregaPostalPortal;

	private Expression entregaPostalEscala;
	private Expression varEntregaPostalEscala;

	private Expression entregaPostalPlanta;
	private Expression varEntregaPostalPlanta;

	private Expression entregaPostalPorta;
	private Expression varEntregaPostalPorta;

	private Expression entregaPostalBloc;
	private Expression varEntregaPostalBloc;

	private Expression entregaPostalComplement;
	private Expression varEntregaPostalComplement;

	private Expression entregaPostalCodiPostal;
	private Expression varEntregaPostalCodiPostal;

	private Expression entregaPostalPoblacio;
	private Expression varEntregaPostalPoblacio;

	private Expression entregaPostalMunicipiCodi;
	private Expression varEntregaPostalMunicipiCodi;

	private Expression entregaPostalProvinciaCodi;
	private Expression varEntregaPostalProvinciaCodi;

	private Expression entregaPostalPaisCodi;
	private Expression varEntregaPostalPaisCodi;

	private Expression entregaPostalLinea1;
	private Expression varEntregaPostalLinea1;

	private Expression entregaPostalLinea2;
	private Expression varEntregaPostalLinea2;

	private Expression entregaPostalCie;
	private Expression varEntregaPostalCie;

	private Expression entregaPostalFormatSobre;
	private Expression varEntregaPostalFormatSobre;

	private Expression entregaPostalFormatFulla;
	private Expression varEntregaPostalFormatFulla;

	private Expression entregaDehActiva;
	private Expression varEntregaDehActiva;

	private Expression entregaDehObligat;
	private Expression varEntregaDehObligat;

	private Expression entregaDehProcedimentCodi;
	private Expression varEntregaDehProcedimentCodi;


	public void execute(HeliumApi heliumApi) throws BpmnException {
		ExpedientInfo expedient = heliumApi.getExpedientInfo();
		ExpedientTipusInfo expedientTipus = heliumApi.getExpedientTipusInfo();
		String notibEmisor = expedientTipus.getNotibEmisor();
		String notibProcedimentCodi = expedientTipus.getNotibCodiProcediment();

		DadesNotificacio dadesNotificacio = new DadesNotificacio();

		String emisorCodi = getValue(emisorDir3Codi);
		dadesNotificacio.setEmisorDir3Codi(emisorCodi != null ? emisorCodi : notibEmisor);

		String strTipusEnviament = getValue(enviamentTipus);

		DadesNotificacio.EnviamentTipus enviamentTipus =
				"COMUNICACIO".equalsIgnoreCase(strTipusEnviament) ?
						DadesNotificacio.EnviamentTipus.COMUNICACIO
						: DadesNotificacio.EnviamentTipus.NOTIFICACIO;

		dadesNotificacio.setEnviamentTipus(enviamentTipus);

		dadesNotificacio.setConcepte(heliumApi.getVariableDefaultValue(getValue(varConcepte), getValue(concepte)));

		String serveiTipusStr = heliumApi.getVariableDefaultValue(getValue(varServeiTipus), getValue(serveiTipus));
		if(serveiTipusStr != null)
			dadesNotificacio.setServeiTipus(serveiTipusStr);

		dadesNotificacio.setGrupCodi(heliumApi.getVariableDefaultValue(getValue(varGrupCodi), getValue(grupCodi)));

		dadesNotificacio.setDescripcio(heliumApi.getVariableDefaultValue(getValue(varDescripcio), getValue(descripcio)));

		dadesNotificacio.setEnviamentDataProgramada(getValueAsDate(enviamentDataProgramada));
		dadesNotificacio.setRetard(heliumApi.getVariableDefaultValueAsInteger(getValue(varRetard), getValueAsInteger(retard)));
		dadesNotificacio.setCaducitat(heliumApi.getVariableDefaultValueAsDate(getValue(varCaducitat), getValueAsDate(caducitat)));

		String idioma = getValue(this.idioma);
		if (idioma != null) {
			try {
				dadesNotificacio.setIdioma(DadesNotificacio.Idioma.valueOf(idioma));
			} catch(Exception e) {
				throw new BpmnException("No es reconeix l'idioma \"" + idioma.toString() + "\" per la notificació, els únics valors admesos són \"ES\" i \"CA\".");
			}
		}
		DocumentInfo documentInfo = null;
		List<DocumentInfo> annexos_notificacio = new ArrayList<DocumentInfo>();

		String doc = getValue(document);
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

		String codiProcediment = getValue(procedimentCodi);
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
		titular.setNom(getValue(titularNom));
		titular.setLlinatge1(getValue(titularLlin1));
		titular.setLlinatge2(getValue(titularLlin2));
		titular.setDni(getValue(titularNif));
		titular.setCodiDir3(getValue(titularCodiDir3));

		titular.setTipus(getValue(titularTipus));
		String mobil = getValue(titularMobil);
		if (mobil == null || mobil.isEmpty())
			mobil = expedient.getAvisosMobil();
		titular.setTelefon(mobil);
		String email = getValue(titularEmail);
		if (email == null || email.isEmpty())
			email = expedient.getAvisosEmail();
		titular.setEmail(email);
		enviament.setTitular(titular);

		List<PersonaInfo> destinataris = new ArrayList<PersonaInfo>();
		PersonaInfo destinatari = new PersonaInfo();
		destinatari.setNom(getValue(destinatariNom));
		destinatari.setLlinatge1(getValue(destinatariLlin1));
		destinatari.setLlinatge2(getValue(destinatariLlin2));
		destinatari.setDni(getValue(destinatariNif));
		destinatari.setTelefon(getValue(destinatariMobil));
		destinatari.setEmail(getValue(destinatariEmail));
		destinatari.setCodiDir3(getValue(destinatariCodiDir3));
		destinatari.setTipus(getValue(destinatariTipus));
		// El destinatari no és obligatori, s'afegeix si s'ha escollit el tipus
		if (destinatari.getTipus() != null) {
			destinataris.add(destinatari);
		}
		enviament.setDestinataris(destinataris);

		String strEntragaPostalTipus = getValue(entregaPostalTipus);

		if (strEntragaPostalTipus != null && !strEntragaPostalTipus.isEmpty())
			enviament.setEntregaPostalTipus(DadesEnviament.EntregaPostalTipus.valueOf(strEntragaPostalTipus));

		Boolean entregaPostalActivaVal = getValueAsBoolean(entregaPostalActiva);
		enviament.setEntregaPostalActiva(false); //(entregaPostalActivaVal != null ? entregaPostalActivaVal.booleanValue() : false); //Forcem false issue #1675
		String strEntregaPostalViaTipus = getValue(entregaPostalViaTipus);
		if (strEntregaPostalViaTipus != null && !strEntregaPostalViaTipus.isEmpty())
			enviament.setEntregaPostalViaTipus(DadesEnviament.EntregaPostalViaTipus.valueOf(strEntregaPostalViaTipus));
		enviament.setEntregaPostalViaNom(getValue(entregaPostalViaNom));
		enviament.setEntregaPostalNumeroCasa(getValue(entregaPostalNumeroCasa));
		enviament.setEntregaPostalNumeroQualificador(getValue(entregaPostalNumeroQualificador));
		enviament.setEntregaPostalPuntKm(getValue(entregaPostalPuntKm));
		enviament.setEntregaPostalApartatCorreus(getValue(entregaPostalApartatCorreus));
		enviament.setEntregaPostalPortal(getValue(entregaPostalPortal));
		enviament.setEntregaPostalEscala(getValue(entregaPostalEscala));
		enviament.setEntregaPostalPlanta(getValue(entregaPostalPlanta));
		enviament.setEntregaPostalPorta(getValue(entregaPostalPorta));
		enviament.setEntregaPostalBloc(getValue(entregaPostalBloc));
		enviament.setEntregaPostalComplement(getValue(entregaPostalComplement));
		enviament.setEntregaPostalCodiPostal(getValue(entregaPostalCodiPostal));
		enviament.setEntregaPostalPoblacio(getValue(entregaPostalPoblacio));
		enviament.setEntregaPostalMunicipiCodi(getValue(entregaPostalMunicipiCodi));
		enviament.setEntregaPostalProvinciaCodi(getValue(entregaPostalProvinciaCodi));
		enviament.setEntregaPostalPaisCodi(getValue(entregaPostalPaisCodi));
		enviament.setEntregaPostalLinea1(getValue(entregaPostalLinea1));
		enviament.setEntregaPostalLinea2(getValue(entregaPostalLinea2));
		enviament.setEntregaPostalCie(getValueAsInteger(entregaPostalCie));
		enviament.setEntregaPostalFormatSobre(getValue(entregaPostalFormatSobre));
		enviament.setEntregaPostalFormatFulla(getValue(entregaPostalFormatFulla));
		Boolean entregaDehActivaVal = getValueAsBoolean(entregaDehActiva);
		enviament.setEntregaDehActiva(entregaDehActivaVal != null? entregaDehActivaVal.booleanValue() : false );
		Boolean entregaDehObligatVal = getValueAsBoolean(entregaDehObligat);
		enviament.setEntregaDehObligat(entregaDehObligatVal != null ? entregaDehObligatVal : false);
		enviament.setEntregaDehProcedimentCodi(getValue(entregaDehProcedimentCodi));

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
