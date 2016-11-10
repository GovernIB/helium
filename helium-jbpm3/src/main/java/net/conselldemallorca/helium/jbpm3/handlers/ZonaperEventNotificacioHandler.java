package net.conselldemallorca.helium.jbpm3.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.security.crypto.codec.Base64;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesNotificacioElectronica;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesRegistreNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.RespostaRegistre;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDocumentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatIdiomaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;

/**
 * Handler per a interactuar amb el registre de sortida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings({"serial", "unused"})
public class ZonaperEventNotificacioHandler extends BasicActionHandler implements RegistreSortidaHandlerInterface, AccioExternaRetrocedirHandler {

	private String representatNif;
	private String varRepresentatNif;
	private String representatNom;
	private String varRepresentatNom;
	private String representatLlin1;
	private String varRepresentatLlin1;
	private String representatLlin2;
	private String varRepresentatLlin2;
	private String oficina;
	private String varOficina;
	private String oficinaFisica;
	private String varOficinaFisica;
	private String remitentCodiEntitat;
	private String varRemitentCodiEntitat;
	private String remitentNomEntitat;
	private String varRemitentNomEntitat;
	private String remitentCodiGeografic;
	private String varRemitentCodiGeografic;
	private String remitentNomGeografic;
	private String varRemitentNomGeografic;
	private String remitentRegistreNumero;
	private String varRemitentRegistreNumero;
	private String remitentRegistreAny;
	private String varRemitentRegistreAny;
	private String destinatariCodiEntitat;
	private String varDestinatariCodiEntitat;
	private String destinatariNomEntitat;
	private String varDestinatariNomEntitat;
	
	private String destinatariCodiGeografic;
	private String varDestinatariCodiGeografic;
	private String destinatariNomGeografic;
	private String varDestinatariNomGeografic;
	private String destinatariCodiProvincia;
	private String varDestinatariCodiProvincia;
	private String destinatariNomProvincia;
	private String varDestinatariNomProvincia;
	private String destinatariCodiPais;
	private String varDestinatariCodiPais;
	private String destinatariNomPais;
	private String varDestinatariNomPais;
	
	private String destinatariRegistreNumero;
	private String varDestinatariRegistreNumero;
	private String destinatariRegistreAny;
	private String varDestinatariRegistreAny;
	private String documentTipus;
	private String varDocumentTipus;
	private String documentIdiomaDocument;
	private String varDocumentIdiomaDocument;
	private String documentIdiomaExtracte;
	private String varDocumentIdiomaExtracte;
	private String document;
	private String varDocument;
	private String annexos;
	private String varAnnexos;
	private String varNumeroRegistre;
	private String varNumeroAnyRegistre;
	private String varDataRegistre;
	private String varData;
	private String varReferenciaRDSJustificanteClave;
	private String varReferenciaRDSJustificanteCodigo;
	private String unitatAdministrativa;
	private String varUnitatAdministrativa;
	private String avisTitol;
	private String varAvisTitol;
	private String avisText;
	private String varAvisText;
	private String avisTextSms;
	private String varAvisTextSms;

	private String notificacioOficiTitol;
	private String varNotificacioOficiTitol;
	private String notificacioOficiText;
	private String varNotificacioOficiText;
	
	private String notificacioSubsanacioTramitIdentificador;
	private String varNotificacioSubsanacioTramitIdentificador;
	private int notificacioSubsanacioTramitVersio;
	private String varNotificacioSubsanacioTramitVersio;
	private String notificacioSubsanacioTramitDescripcio;
	private String varNotificacioSubsanacioTramitDescripcio;
	
	private boolean notificacioCrearExpedient;
	private String varNotificacioCrearExpedient;
	
	public void execute(ExecutionContext executionContext) throws Exception {		
		ExpedientDto expedient = getExpedientActual(executionContext);
		ExpedientTipusDto expedientTipus = expedient.getTipus();
		
		expedient.setTramitExpedientIdentificador(expedient.getIdentificador());
		if (expedient.getTramitExpedientIdentificador() == null)
			throw new JbpmException(
					  "El expediente " + expedient.getIdentificador() + " no tiene número de sistra asociado."
					+ "Una notificación tiene que generarse dentro de un expediente, por tanto un paso "
					+ "previo a generar una notificación es haber publicado el expediente en la zona "
					+ "personal.");
		
		DadesRegistreNotificacio anotacio = new DadesRegistreNotificacio();
		anotacio.setData(new Date());
		
		String identificador = expedient.getNumeroIdentificador();
		
		String clau = new Long(System.currentTimeMillis()).toString();
		if (expedient.getTramitExpedientClau() != null && !expedient.getTramitExpedientClau().isEmpty())
			clau = expedient.getTramitExpedientClau();
		
		anotacio.setExpedientIdentificador(identificador);
		anotacio.setExpedientClau(clau);
		
		String ua = expedientTipus.getNotificacioUnitatAdministrativa();
		if (ua != null)
			anotacio.setExpedientUnitatAdministrativa(ua);
		else if (expedient.getUnitatAdministrativa() != null)
			anotacio.setExpedientUnitatAdministrativa(String.valueOf(expedient.getUnitatAdministrativa()));
		
		anotacio.setOrganCodi(expedientTipus.getNotificacioOrganCodi());		
		anotacio.setOficinaCodi(expedientTipus.getNotificacioOficinaCodi());
		
		
		/*
		 * DADES INTERESSAT
		 */
		anotacio.setInteressatNif((String)getValorOVariable(
				executionContext,
				representatNif,
				varRepresentatNif));
		
		
		String interessatNom = (String)getValorOVariable(
				executionContext,
				representatNom,
				varRepresentatNom);
		String interessatLlinatge1 = (String)getValorOVariable(
				executionContext,
				representatLlin1,
				varRepresentatLlin1);
		String interessatLlinatge2 = (String)getValorOVariable(
				executionContext,
				representatLlin2,
				varRepresentatLlin2);
		anotacio.setInteressatNomAmbCognoms(
				interessatNom + " " +
				interessatLlinatge1 + " " +
				interessatLlinatge2);
		
		anotacio.setInteressatEntitatCodi(
				(String)getValorOVariable(
						executionContext,
						destinatariCodiEntitat,
						varDestinatariCodiEntitat));
		anotacio.setInteressatMunicipiCodi(
				(String)getValorOVariable(
						executionContext,
						destinatariCodiGeografic,
						varDestinatariCodiGeografic));
		anotacio.setInteressatMunicipiNom(
				(String)getValorOVariable(
						executionContext,
						destinatariNomGeografic,
						varDestinatariNomGeografic));
		
		anotacio.setInteressatProvinciaCodi(
				(String)getValorOVariable(
						executionContext,
						destinatariCodiProvincia,
						varDestinatariCodiProvincia));
		anotacio.setInteressatProvinciaNom(
				(String)getValorOVariable(
						executionContext,
						destinatariNomProvincia,
						varDestinatariNomProvincia));
		
		anotacio.setInteressatPaisCodi(
				(String)getValorOVariable(
						executionContext,
						destinatariCodiPais,
						varDestinatariCodiPais));
		anotacio.setInteressatPaisNom(
				(String)getValorOVariable(
						executionContext,
						destinatariNomPais,
						varDestinatariNomPais));
		
		////////////////////////
		
		
		String idiomaExtracte = (String)getValorOVariable(
				executionContext,
				documentIdiomaExtracte,
				varDocumentIdiomaExtracte);
		anotacio.setAnotacioIdiomaCodi(
				(idiomaExtracte != null) ? idiomaExtracte : "ca");
		anotacio.setAnotacioTipusAssumpte(
				(String)getValorOVariable(
						executionContext,
						documentTipus,
						varDocumentTipus));
		anotacio.setNotificacioJustificantRecepcio(true);
		
		
		/*
		 * DOCUMENT I ANNEXOS
		 */
		DocumentInfo documentInfo = null;
		List<DocumentInfo> annexos_notificacio = new ArrayList<DocumentInfo>();
		String doc = (String)getValorOVariable(
				executionContext,
				document,
				varDocument);
		if (doc != null && !doc.isEmpty()) {
			documentInfo = getDocumentInfo(executionContext, doc, true);
			if (documentInfo != null) {
				anotacio.setAnotacioAssumpte(expedient.getIdentificador() + ": " + documentInfo.getTitol());
				annexos_notificacio.add(documentInfo);
			} else {
				throw new JbpmException("No existia ningún documento con documentCodi: " + varDocument + ".");
			}
		}
		
		
		List<Long> anxs = null;
		List<DocumentInfo> annexos_expedient = new ArrayList<DocumentInfo>();
		String anxsCodis = (String)getValorOVariable(
				executionContext, 
				annexos,
				varAnnexos);
		if (anxsCodis != null) {
			anxs = new ArrayList<Long>();
			String[] codis = anxsCodis.split(",");
			for (String codi: codis) {
				DocumentInfo annexInfo = getDocumentInfo(executionContext, codi, true);
				if (annexInfo != null) {
					annexos_expedient.add(annexInfo);
				} else {
					throw new JbpmException("No existia cap annex amb documentCodi: " + codi + ".");
				}
			}
		}
		annexos_notificacio.addAll(annexos_expedient);
		
		//////////////
		
		/*
		 * SEGMENT PER A TITOLS I TEXTES
		 */
		String valorOficiTitol = null;
		if (expedientTipus.getNotificacioOficiTitol() != null && !expedientTipus.getNotificacioOficiTitol().isEmpty())
			valorOficiTitol = expedientTipus.getNotificacioOficiTitol();
		else
			valorOficiTitol = (String)getValorOVariable(
					executionContext,
					notificacioOficiTitol,
					varNotificacioOficiTitol);
		anotacio.setNotificacioOficiTitol(valorOficiTitol);
		
		
		String valorOficiText = null;
		if (expedientTipus.getNotificacioOficiText() != null && !expedientTipus.getNotificacioOficiText().isEmpty())
			valorOficiText = expedientTipus.getNotificacioOficiText();
		else
			valorOficiText = (String)getValorOVariable(
					executionContext,
					notificacioOficiText,
					varNotificacioOficiText);
		anotacio.setNotificacioOficiText(valorOficiText);
		
		
		String valorAvisTitol = null;
		if (expedientTipus.getNotificacioAvisTitol() != null && !expedientTipus.getNotificacioAvisTitol().isEmpty())
			valorAvisTitol = expedientTipus.getNotificacioAvisTitol();
		else
			valorAvisTitol = (String)getValorOVariable(
					executionContext,
					avisTitol,
					varAvisTitol);
		anotacio.setNotificacioAvisTitol(valorAvisTitol);
		
		String valorAvisText = null;
		if (expedientTipus.getNotificacioAvisText() != null && !expedientTipus.getNotificacioAvisText().isEmpty())
			valorAvisText = expedientTipus.getNotificacioAvisText();
		else
			valorAvisText = (String)getValorOVariable(
					executionContext,
					avisText,
					varAvisText);
		anotacio.setNotificacioAvisText(valorAvisText);
		
		String valorAvisTextSms = null;
		if (expedientTipus.getNotificacioAvisTextSms() != null && !expedientTipus.getNotificacioAvisTextSms().isEmpty())
			valorAvisTextSms = expedientTipus.getNotificacioAvisTextSms();
		else
			valorAvisTextSms = (String)getValorOVariable(
					executionContext,
					avisTextSms,
					varAvisTextSms);
		anotacio.setNotificacioAvisTextSms(valorAvisTextSms);
		//////////////////////////////////////////////////////////////////////
		
		
		boolean crearExpedient = (Boolean)getValorOVariable(
				executionContext,
				notificacioCrearExpedient,
				varNotificacioCrearExpedient);
		anotacio.setNotificacioCrearExpedient(crearExpedient);
		
		
		if (varNotificacioSubsanacioTramitIdentificador != null && !varNotificacioSubsanacioTramitIdentificador.isEmpty()) {
			anotacio.setNotificacioSubsanacioTramitIdentificador(
					(String)getValorOVariable(
							executionContext,
							notificacioSubsanacioTramitIdentificador,
							varNotificacioSubsanacioTramitIdentificador));
			
			anotacio.setNotificacioSubsanacioTramitVersio(
					getValorOVariableInteger(
							executionContext,
							notificacioSubsanacioTramitVersio,
							varNotificacioSubsanacioTramitVersio));
			
			anotacio.setNotificacioSubsanacioTramitDescripcio(
					(String)getValorOVariable(
							executionContext,
							notificacioSubsanacioTramitDescripcio,
							varNotificacioSubsanacioTramitDescripcio));
		}
		
		RespostaRegistre resposta = registreNotificacio(executionContext,anotacio,annexos_notificacio);
		if (resposta != null) {			
			
			NotificacioDto notificacio = new NotificacioDto();
			notificacio.setEstat(DocumentEnviamentEstatEnumDto.ENVIAT);
			notificacio.setAssumpte(anotacio.getAnotacioAssumpte());
			notificacio.setRegistreNumero(resposta.getNumero());
			notificacio.setDataEnviament(anotacio.getData());
			notificacio.setDataRecepcio(resposta.getData());
			
			DocumentNotificacioDto documentNotificacio = new DocumentNotificacioDto();
			documentNotificacio.setId(documentInfo.getId());
			notificacio.setDocument(documentNotificacio);
			
			for (DocumentInfo annex_exp: annexos_expedient) {
				DocumentNotificacioDto documentAnnex = new DocumentNotificacioDto();
				documentAnnex.setId(annex_exp.getId());
				notificacio.getAnnexos().add(documentAnnex);
			}
			
			notificacio.setTipus(DocumentNotificacioTipusEnumDto.ELECTRONICA);
			notificacio.setInteressatDocumentTipus(InteressatDocumentTipusEnumDto.NIF); //pendent definir form
			notificacio.setInteressatDocumentNum(anotacio.getInteressatNif());
			notificacio.setInteressatNom(interessatNom);
			notificacio.setInteressatLlinatge1(interessatLlinatge1);
			notificacio.setInteressatLlinatge2(interessatLlinatge2);
			notificacio.setInteressatPaisCodi(anotacio.getInteressatPaisCodi());
			notificacio.setInteressatProvinciaCodi(anotacio.getInteressatProvinciaCodi());
			notificacio.setInteressatMunicipiCodi(anotacio.getInteressatMunicipiCodi());
			notificacio.setInteressatEmail(""); //pendent definir form
			notificacio.setUnitatAdministrativa(anotacio.getExpedientUnitatAdministrativa());
			notificacio.setOrganCodi(anotacio.getOrganCodi());
			notificacio.setOficinaCodi(anotacio.getOficinaCodi());
			notificacio.setAvisTitol(anotacio.getNotificacioAvisTitol());
			notificacio.setAvisText(anotacio.getNotificacioAvisText());
			notificacio.setAvisTextSms(anotacio.getNotificacioAvisTextSms());
			notificacio.setOficiTitol(anotacio.getNotificacioOficiTitol());
			notificacio.setOficiText(anotacio.getNotificacioOficiText());
			notificacio.setIdioma(InteressatIdiomaEnumDto.valueOf(anotacio.getAnotacioIdiomaCodi().toUpperCase()));
			notificacio.setEnviamentData(null);
			notificacio.setEnviamentCount(1);
			notificacio.setEnviamentError(false);
			notificacio.setEnviamentErrorDescripcio(null);
			notificacio.setProcessamentData(null); //pendent definir
			notificacio.setProcessamentCount(0);
			notificacio.setProcessamentError(false);
			notificacio.setProcessamentErrorDescripcio(null);
			notificacio.setRdsCodi(resposta.getReferenciaRDSJustificante().getCodigo());
			notificacio.setRdsClau(resposta.getReferenciaRDSJustificante().getClave());
			
			Jbpm3HeliumBridge.getInstanceService().notificacioGuardar(
					expedient, 
					notificacio);
			List<String> parametres = new ArrayList<String>();
			
			DadesNotificacioElectronica dadesNotificacioElectronica = new DadesNotificacioElectronica();
			dadesNotificacioElectronica.setAnotacio(anotacio);
			dadesNotificacioElectronica.setAnnexos(annexos_notificacio);			
			parametres.add(toString(dadesNotificacioElectronica));
			
			guardarParametresPerRetrocedir(executionContext,parametres);

			if (varNumeroRegistre != null)
				executionContext.setVariable(
						varNumeroRegistre,
						resposta.getNumero());
			if (varDataRegistre != null)
				executionContext.setVariable(
						varDataRegistre,
						resposta.getData());		
			if (varReferenciaRDSJustificanteClave != null)
				executionContext.setVariable(
						varReferenciaRDSJustificanteClave,
						resposta.getReferenciaRDSJustificante().getClave());
			if (varReferenciaRDSJustificanteCodigo != null)
				executionContext.setVariable(
						varReferenciaRDSJustificanteCodigo,
						resposta.getReferenciaRDSJustificante().getCodigo());
		}
	}

	@Override
	public void retrocedir(ExecutionContext executionContext, List<String> parametres) throws Exception {
		try {
			DadesNotificacioElectronica dadesNotificacioElectronica = (DadesNotificacioElectronica) fromString(parametres.get(0));			
			dadesNotificacioElectronica.getAnotacio().setNotificacioAvisText("La notificación que se realizó anteriormente no es correcta.");
			RespostaRegistre resposta = registreNotificacio(executionContext,dadesNotificacioElectronica.getAnotacio(),dadesNotificacioElectronica.getAnnexos());
			if (resposta != null) {
				boolean borrado = Jbpm3HeliumBridge.getInstanceService().notificacioEsborrar(
						resposta.getNumero(),
						resposta.getReferenciaRDSJustificante().getClave(),
						resposta.getReferenciaRDSJustificante().getCodigo());
				if (!borrado)
					throw new JbpmException("No se ha podido borrar la notificación electrónica del expediente");
			}
		} catch (Exception ex) {
			throw new JbpmException("No se ha podido retroceder la notificación electrónica del expediente", ex);
		}
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

	public void setNotificacioOficiTitol(String notificacioOficiTitol) {
		this.notificacioOficiTitol = notificacioOficiTitol;
	}
	public void setVarNotificacioOficiTitol(String varNotificacioOficiTitol) {
		this.varNotificacioOficiTitol = varNotificacioOficiTitol;
	}
	public void setNotificacioOficiText(String notificacioOficiText) {
		this.notificacioOficiText = notificacioOficiText;
	}
	public void setVarNotificacioOficiText(String varNotificacioOficiText) {
		this.varNotificacioOficiText = varNotificacioOficiText;
	}
	public void setNotificacioSubsanacioTramitIdentificador(String notificacioSubsanacioTramitIdentificador) {
		this.notificacioSubsanacioTramitIdentificador = notificacioSubsanacioTramitIdentificador;
	}
	public void setVarNotificacioSubsanacioTramitIdentificador(String varNotificacioSubsanacioTramitIdentificador) {
		this.varNotificacioSubsanacioTramitIdentificador = varNotificacioSubsanacioTramitIdentificador;
	}
	public void setNotificacioSubsanacioTramitVersio(int notificacioSubsanacioTramitVersio) {
		this.notificacioSubsanacioTramitVersio = notificacioSubsanacioTramitVersio;
	}
	public void setVarUnitatAdministrativa(String varUnitatAdministrativa) {
		this.varUnitatAdministrativa = varUnitatAdministrativa;
	}
	public void setUnitatAdministrativa(String unitatAdministrativa) {
		this.unitatAdministrativa = unitatAdministrativa;
	}
	public void setVarNotificacioSubsanacioTramitVersio(String varNotificacioSubsanacioTramitVersio) {
		this.varNotificacioSubsanacioTramitVersio = varNotificacioSubsanacioTramitVersio;
	}
	public void setNotificacioSubsanacioTramitDescripcio(String notificacioSubsanacioTramitDescripcio) {
		this.notificacioSubsanacioTramitDescripcio = notificacioSubsanacioTramitDescripcio;
	}
	public void setVarNotificacioSubsanacioTramitDescripcio(String varNotificacioSubsanacioTramitDescripcio) {
		this.varNotificacioSubsanacioTramitDescripcio = varNotificacioSubsanacioTramitDescripcio;
	}
	public void setRepresentatNif(String representatNif) {
		this.representatNif = representatNif;
	}
	public void setVarAvisTitol(String varAvisTitol) {
		this.varAvisTitol = varAvisTitol;
	}
	public void setVarAvisText(String varAvisText) {
		this.varAvisText = varAvisText;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public void setVarRepresentatNif(String varRepresentatNif) {
		this.varRepresentatNif = varRepresentatNif;
	}
	public String getRepresentatNom() {
		return representatNom;
	}
	public void setRepresentatNom(String representatNom) {
		this.representatNom = representatNom;
	}
	public String getVarRepresentatNom() {
		return varRepresentatNom;
	}
	public void setVarRepresentatNom(String varRepresentatNom) {
		this.varRepresentatNom = varRepresentatNom;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public void setVarOficina(String varOficina) {
		this.varOficina = varOficina;
	}
	public void setOficinaFisica(String oficinaFisica) {
		this.oficinaFisica = oficinaFisica;
	}
	public void setVarOficinaFisica(String varOficinaFisica) {
		this.varOficinaFisica = varOficinaFisica;
	}
	public void setRemitentCodiEntitat(String remitentCodiEntitat) {
		this.remitentCodiEntitat = remitentCodiEntitat;
	}
	public void setVarRemitentCodiEntitat(String varRemitentCodiEntitat) {
		this.varRemitentCodiEntitat = varRemitentCodiEntitat;
	}
	public void setRemitentNomEntitat(String remitentNomEntitat) {
		this.remitentNomEntitat = remitentNomEntitat;
	}
	public void setVarRemitentNomEntitat(String varRemitentNomEntitat) {
		this.varRemitentNomEntitat = varRemitentNomEntitat;
	}
	public void setRemitentCodiGeografic(String remitentCodiGeografic) {
		this.remitentCodiGeografic = remitentCodiGeografic;
	}
	public void setVarRemitentCodiGeografic(String varRemitentCodiGeografic) {
		this.varRemitentCodiGeografic = varRemitentCodiGeografic;
	}
	public void setRemitentNomGeografic(String remitentNomGeografic) {
		this.remitentNomGeografic = remitentNomGeografic;
	}
	public void setVarRemitentNomGeografic(String varRemitentNomGeografic) {
		this.varRemitentNomGeografic = varRemitentNomGeografic;
	}
	public void setRemitentRegistreNumero(String remitentRegistreNumero) {
		this.remitentRegistreNumero = remitentRegistreNumero;
	}
	public void setVarRemitentRegistreNumero(String varRemitentRegistreNumero) {
		this.varRemitentRegistreNumero = varRemitentRegistreNumero;
	}
	public void setRemitentRegistreAny(String remitentRegistreAny) {
		this.remitentRegistreAny = remitentRegistreAny;
	}
	public void setVarRemitentRegistreAny(String varRemitentRegistreAny) {
		this.varRemitentRegistreAny = varRemitentRegistreAny;
	}
	public void setDestinatariCodiEntitat(String destinatariCodiEntitat) {
		this.destinatariCodiEntitat = destinatariCodiEntitat;
	}
	public void setVarDestinatariCodiEntitat(String varDestinatariCodiEntitat) {
		this.varDestinatariCodiEntitat = varDestinatariCodiEntitat;
	}
	public void setDestinatariNomEntitat(String destinatariNomEntitat) {
		this.destinatariNomEntitat = destinatariNomEntitat;
	}
	public void setVarDestinatariNomEntitat(String varDestinatariNomEntitat) {
		this.varDestinatariNomEntitat = varDestinatariNomEntitat;
	}
	public void setDestinatariCodiGeografic(String destinatariCodiGeografic) {
		this.destinatariCodiGeografic = destinatariCodiGeografic;
	}
	public void setVarDestinatariCodiGeografic(String varDestinatariCodiGeografic) {
		this.varDestinatariCodiGeografic = varDestinatariCodiGeografic;
	}
	public void setDestinatariNomGeografic(String destinatariNomGeografic) {
		this.destinatariNomGeografic = destinatariNomGeografic;
	}
	public void setVarDestinatariNomGeografic(String varDestinatariNomGeografic) {
		this.varDestinatariNomGeografic = varDestinatariNomGeografic;
	}
	public void setAvisTitol(String avisTitol) {
		this.avisTitol = avisTitol;
	}
	public void setAvisText(String avisText) {
		this.avisText = avisText;
	}
	public void setDestinatariRegistreNumero(String destinatariRegistreNumero) {
		this.destinatariRegistreNumero = destinatariRegistreNumero;
	}
	public void setVarDestinatariRegistreNumero(String varDestinatariRegistreNumero) {
		this.varDestinatariRegistreNumero = varDestinatariRegistreNumero;
	}
	public void setDestinatariRegistreAny(String destinatariRegistreAny) {
		this.destinatariRegistreAny = destinatariRegistreAny;
	}
	public void setVarDestinatariRegistreAny(String varDestinatariRegistreAny) {
		this.varDestinatariRegistreAny = varDestinatariRegistreAny;
	}
	public void setDocumentTipus(String documentTipus) {
		this.documentTipus = documentTipus;
	}
	public void setVarDocumentTipus(String varDocumentTipus) {
		this.varDocumentTipus = varDocumentTipus;
	}
	public void setDocumentIdiomaDocument(String documentIdiomaDocument) {
		this.documentIdiomaDocument = documentIdiomaDocument;
	}
	public void setVarDocumentIdiomaDocument(String varDocumentIdiomaDocument) {
		this.varDocumentIdiomaDocument = varDocumentIdiomaDocument;
	}
	public void setDocumentIdiomaExtracte(String documentIdiomaExtracte) {
		this.documentIdiomaExtracte = documentIdiomaExtracte;
	}
	public void setVarDocumentIdiomaExtracte(String varDocumentIdiomaExtracte) {
		this.varDocumentIdiomaExtracte = varDocumentIdiomaExtracte;
	}
	public void setVarDocument(String varDocument) {
		this.varDocument = varDocument;
	}
	public void setVarNumeroRegistre(String varNumeroRegistre) {
		this.varNumeroRegistre = varNumeroRegistre;
	}
	public void setVarNumeroAnyRegistre(String varNumeroAnyRegistre) {
		this.varNumeroAnyRegistre = varNumeroAnyRegistre;
	}
	public void setVarDataRegistre(String varDataRegistre) {
		this.varDataRegistre = varDataRegistre;
	}
	public void setVarData(String varData) {
		this.varData = varData;
	}
	public void setVarReferenciaRDSJustificanteClave(String varReferenciaRDSJustificanteClave) {
		this.varReferenciaRDSJustificanteClave = varReferenciaRDSJustificanteClave;
	}
	public void setVarReferenciaRDSJustificanteCodigo(String varReferenciaRDSJustificanteCodigo) {
		this.varReferenciaRDSJustificanteCodigo = varReferenciaRDSJustificanteCodigo;
	}
	public void setRepresentatLlin1(String representatLlin1) {
		this.representatLlin1 = representatLlin1;
	}
	public void setVarRepresentatLlin1(String varRepresentatLlin1) {
		this.varRepresentatLlin1 = varRepresentatLlin1;
	}
	public void setRepresentatLlin2(String representatLlin2) {
		this.representatLlin2 = representatLlin2;
	}
	public void setVarRepresentatLlin2(String varRepresentatLlin2) {
		this.varRepresentatLlin2 = varRepresentatLlin2;
	}
	public void setAvisTextSms(String avisTextSms) {
		this.avisTextSms = avisTextSms;
	}
	public void setVarAvisTextSms(String varAvisTextSms) {
		this.varAvisTextSms = varAvisTextSms;
	}
	public void setDestinatariCodiProvincia(String destinatariCodiProvincia) {
		this.destinatariCodiProvincia = destinatariCodiProvincia;
	}
	public void setVarDestinatariCodiProvincia(String varDestinatariCodiProvincia) {
		this.varDestinatariCodiProvincia = varDestinatariCodiProvincia;
	}
	public void setDestinatariNomProvincia(String destinatariNomProvincia) {
		this.destinatariNomProvincia = destinatariNomProvincia;
	}
	public void setVarDestinatariNomProvincia(String varDestinatariNomProvincia) {
		this.varDestinatariNomProvincia = varDestinatariNomProvincia;
	}
	public void setDestinatariCodiPais(String destinatariCodiPais) {
		this.destinatariCodiPais = destinatariCodiPais;
	}
	public void setVarDestinatariCodiPais(String varDestinatariCodiPais) {
		this.varDestinatariCodiPais = varDestinatariCodiPais;
	}
	public void setDestinatariNomPais(String destinatariNomPais) {
		this.destinatariNomPais = destinatariNomPais;
	}
	public void setVarDestinatariNomPais(String varDestinatariNomPais) {
		this.varDestinatariNomPais = varDestinatariNomPais;
	}
	public void setAnnexos(String annexos) {
		this.annexos = annexos;
	}
	public void setVarAnnexos(String varAnnexos) {
		this.varAnnexos = varAnnexos;
	}
	public void setNotificacioCrearExpedient(boolean notificacioCrearExpedient) {
		this.notificacioCrearExpedient = notificacioCrearExpedient;
	}
	public void setVarNotificacioCrearExpedient(String varNotificacioCrearExpedient) {
		this.varNotificacioCrearExpedient = varNotificacioCrearExpedient;
	}
}