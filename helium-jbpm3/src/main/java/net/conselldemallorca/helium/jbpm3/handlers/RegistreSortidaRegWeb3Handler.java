/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.RespostaRegistre;
import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreIdDto;
import net.conselldemallorca.helium.v3.core.api.registre.RegistreAnnex;
import net.conselldemallorca.helium.v3.core.api.registre.RegistreAnotacio;
import net.conselldemallorca.helium.v3.core.api.registre.RegistreInteressat;
import net.conselldemallorca.helium.v3.core.api.registre.RegistreInteressatDocumentTipusEnum;
import net.conselldemallorca.helium.v3.core.api.registre.RegistreInteressatTipusEnum;

/**
 * Handler per a interactuar amb el registre de sortida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreSortidaRegWeb3Handler extends AbstractHeliumActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3311948968448865730L;
	
//	Info de l'Assentament
	
	private String entitatCodi;
	private String varEntitatCodi;
	private String organOrigen;
	private String varOrganOrigen;
	private String oficinaCodi;
	private String varOficinaCodi;
	private String llibreCodi;
	private String varLlibreCodi;
	
	private String extracte;
	private String varExtracte;
	private String documentacioFisicaCodi;
	private String varDocumentacioFisicaCodi;
	private String assumpteTipusCodi;
	private String varAssumpteTipusCodi;
	private String assumpteCodi;
	private String varAssumpteCodi;
	
	private String usuariCodi;
	private String varUsuariCodi;
	
//	Info de l'interessat
	private String interessatTipus;
	private String varInteressatTipus;
	private String interessatDocumentTipus;
	private String varInteressatDocumentTipus;
	private String interessatDocumentNum;
	private String varInteressatDocumentNum;
	private String interessatEmail;
	private String varInteressatEmail;
	private String interessatEmailHabilitat;
	private String varInteressatEmailHabilitat;
	private String interessatTelefon;
	private String varInteressatTelefon;
	private String interessatNom;
	private String varInteressatNom;
	private String interessatLlinatge1;
	private String varInteressatLlinatge1;
	private String interessatLlinatge2;
	private String varInteressatLlinatge2;
	private String interessatRaoSocial;
	private String varInteressatRaoSocial;
	private String interessatPais;
	private String varInteressatPais;
	private String interessatProvincia;
	private String varInteressatProvincia;
	private String interessatMunicipi;
	private String varInteressatMunicipi;
	private String interessatCodiPostal;
	private String varInteressatCodiPostal;
	private String interessatAdresa;
	private String varInteressatAdresa;
	private String interessatCanalPreferent;
	private String varInteressatCanalPreferent;
	private String interessatObservacions;
	private String varInteressatObservacions;
	
//	Info del representant
	private String representantTipus;
	private String varRepresentantTipus;
	private String representantDocumentTipus;
	private String varRepresentantDocumentTipus;
	private String representantDocumentNum;
	private String varRepresentantDocumentNum;
	private String representantEmail;
	private String varRepresentantEmail;
	private String representantEmailHabilitat;
	private String varRepresentantEmailHabilitat;
	private String representantTelefon;
	private String varRepresentantTelefon;
	private String representantNom;
	private String varRepresentantNom;
	private String representantLlinatge1;
	private String varRepresentantLlinatge1;
	private String representantLlinatge2;
	private String varRepresentantLlinatge2;
	private String representantRaoSocial;
	private String varRepresentantRaoSocial;
	private String representantPais;
	private String varRepresentantPais;
	private String representantProvincia;
	private String varRepresentantProvincia;
	private String representantMunicipi;
	private String varRepresentantMunicipi;
	private String representantCodiPostal;
	private String varRepresentantCodiPostal;
	private String representantAdresa;
	private String varRepresentantAdresa;
	private String representantCanalPreferent;
	private String varRepresentantCanalPreferent;
	private String representantObservacions;
	private String varRepresentantObservacions;
	
	private String idiomaCodi;
	private String varIdiomaCodi;
	
	private String codiDocument;
	private String varCodiDocument;
	
	private String documentValidesa;
	private String varDocumentValidesa;
	private String documentTipus;
	private String varDocumentTipus;
	private String documentTipusDocumental;
	private String varDocumentTipusDocumental;
	private String documentOrigen;
	private String varDocumentOrigen;
	private String documentObservacions;
	private String varDocumentObservacions;
	private String documentModeFirma;
	private String varDocumentModeFirma;
	
	//	variables per guardar número i data de registre
	private String varNumeroRegistre;
	private String varDataRegistre;
	

	public void execute(ExecutionContext executionContext) throws Exception {
		if (!Jbpm3HeliumBridge.getInstanceService().isRegistreRegWeb3Actiu())
			throw new JbpmException("El plugin de registre no està configurat");
		RegistreAnotacio anotacio = new RegistreAnotacio();
		
		ExpedientDto expedient = getExpedientActual(executionContext);
		anotacio.setExpedientNumero(expedient.getNumero());

		String documentCodi = (String)getValorOVariable(
				executionContext,
				codiDocument,
				varCodiDocument);
		
		DocumentInfo documentInfo = getDocumentInfo(
				executionContext,
				documentCodi,
				true);

		// Info de l'Assentament
		anotacio.setEntitatCodi((String)getValorOVariable(
				executionContext,
				entitatCodi,
				varEntitatCodi));
		anotacio.setOrgan((String)getValorOVariable(
				executionContext,
				organOrigen,
				varOrganOrigen));
		anotacio.setOficinaCodi((String)getValorOVariable(
				executionContext,
				oficinaCodi,
				varOficinaCodi));
		anotacio.setLlibreCodi((String)getValorOVariable(
				executionContext,
				llibreCodi,
				varLlibreCodi));
		
		// Extracte
		String anotacioExtracte = (String)getValorOVariable(
				executionContext,
				extracte,
				varExtracte);
		if (anotacioExtracte == null || anotacioExtracte.isEmpty())
			anotacioExtracte = 	this.buildExtracte(expedient, documentInfo);
		
		anotacio.setExtracte(anotacioExtracte);
		anotacio.setDocumentacioFisicaCodi((String)getValorOVariable(
				executionContext,
				documentacioFisicaCodi,
				varDocumentacioFisicaCodi));
		anotacio.setAssumpteTipusCodi((String)getValorOVariable(
				executionContext,
				assumpteTipusCodi,
				varAssumpteTipusCodi));
		anotacio.setAssumpteCodi((String)getValorOVariable(
				executionContext,
				assumpteCodi,
				varAssumpteCodi));
		
		String usuari = (String)getValorOVariable(
				executionContext,
				usuariCodi,
				varUsuariCodi);
		if (usuari == null)
			usuari = Jbpm3HeliumBridge.getInstanceService().getUsuariCodiActual();
		anotacio.setUsuariCodi(usuari);
		
		
//		Info de l'interessat
		RegistreInteressat interessat = new RegistreInteressat();
		interessat.setTipus(getValorOVariable(
				executionContext,
				interessatTipus,
				varInteressatTipus) != null ? RegistreInteressatTipusEnum.valueOf((String)getValorOVariable(
				executionContext,
				interessatTipus,
				varInteressatTipus)) : null);
		interessat.setDocumentTipus(getValorOVariable(
				executionContext,
				interessatDocumentTipus,
				varInteressatDocumentTipus) != null ? RegistreInteressatDocumentTipusEnum.valueOf((String)getValorOVariable(
				executionContext,
				interessatDocumentTipus,
				varInteressatDocumentTipus)) : null);
		interessat.setDocumentNum((String)getValorOVariable(
				executionContext,
				interessatDocumentNum,
				varInteressatDocumentNum));
		interessat.setEmail((String)getValorOVariable(
				executionContext,
				interessatEmail,
				varInteressatEmail));
		interessat.setEmailHabilitat((String)getValorOVariable(
				executionContext,
				interessatEmailHabilitat,
				varInteressatEmailHabilitat));
		interessat.setNom((String)getValorOVariable(
				executionContext,
				interessatNom,
				varInteressatNom));
		interessat.setLlinatge1((String)getValorOVariable(
				executionContext,
				interessatLlinatge1,
				varInteressatLlinatge1));
		interessat.setLlinatge2((String)getValorOVariable(
				executionContext,
				interessatLlinatge2,
				varInteressatLlinatge2));
		interessat.setRaoSocial((String)getValorOVariable(
				executionContext,
				interessatRaoSocial,
				varInteressatRaoSocial));
		interessat.setPais((String)getValorOVariable(
				executionContext,
				interessatPais,
				varInteressatPais));
		interessat.setProvincia((String)getValorOVariable(
				executionContext,
				interessatProvincia,
				varInteressatProvincia));
		interessat.setTelefon((String)getValorOVariable(
				executionContext,
				interessatTelefon,
				varInteressatTelefon));
		interessat.setMunicipi((String)getValorOVariable(
				executionContext,
				interessatMunicipi,
				varInteressatMunicipi));
		interessat.setCodiPostal((String)getValorOVariable(
				executionContext,
				interessatCodiPostal,
				varInteressatCodiPostal));
		interessat.setAdresa((String)getValorOVariable(
				executionContext,
				interessatAdresa,
				varInteressatAdresa));
		interessat.setCanalPreferent((String)getValorOVariable(
				executionContext,
				interessatCanalPreferent,
				varInteressatCanalPreferent));
		interessat.setObservacions((String)getValorOVariable(
				executionContext,
				interessatObservacions,
				varInteressatObservacions));
		
//		Info del representant
		RegistreInteressat representant = new RegistreInteressat();
		representant.setTipus(getValorOVariable(
				executionContext,
				representantTipus,
				varRepresentantTipus) != null ? RegistreInteressatTipusEnum.valueOf((String)getValorOVariable(
				executionContext,
				representantTipus,
				varRepresentantTipus)) : null);
		representant.setDocumentTipus(getValorOVariable(
				executionContext,
				representantDocumentTipus,
				varRepresentantDocumentTipus) != null ? RegistreInteressatDocumentTipusEnum.valueOf((String)getValorOVariable(
				executionContext,
				representantDocumentTipus,
				varRepresentantDocumentTipus)) : null);
		representant.setDocumentNum((String)getValorOVariable(
				executionContext,
				representantDocumentNum,
				varRepresentantDocumentNum));
		representant.setEmail((String)getValorOVariable(
				executionContext,
				representantEmail,
				varRepresentantEmail));
		representant.setEmailHabilitat((String)getValorOVariable(
				executionContext,
				representantEmailHabilitat,
				varRepresentantEmailHabilitat));
		representant.setNom((String)getValorOVariable(
				executionContext,
				representantNom,
				varRepresentantNom));
		representant.setLlinatge1((String)getValorOVariable(
				executionContext,
				representantLlinatge1,
				varRepresentantLlinatge1));
		representant.setLlinatge2((String)getValorOVariable(
				executionContext,
				representantLlinatge2,
				varRepresentantLlinatge2));
		representant.setRaoSocial((String)getValorOVariable(
				executionContext,
				representantRaoSocial,
				varRepresentantRaoSocial));
		representant.setPais((String)getValorOVariable(
				executionContext,
				representantPais,
				varRepresentantPais));
		representant.setProvincia((String)getValorOVariable(
				executionContext,
				representantProvincia,
				varRepresentantProvincia));
		representant.setTelefon((String)getValorOVariable(
				executionContext,
				representantTelefon,
				varRepresentantTelefon));
		representant.setMunicipi((String)getValorOVariable(
				executionContext,
				representantMunicipi,
				varRepresentantMunicipi));
		representant.setCodiPostal((String)getValorOVariable(
				executionContext,
				representantCodiPostal,
				varRepresentantCodiPostal));
		representant.setAdresa((String)getValorOVariable(
				executionContext,
				representantAdresa,
				varRepresentantAdresa));
		representant.setCanalPreferent((String)getValorOVariable(
				executionContext,
				representantCanalPreferent,
				varRepresentantCanalPreferent));
		representant.setObservacions((String)getValorOVariable(
				executionContext,
				representantObservacions,
				varRepresentantObservacions));
		
		if (representant.getTipus() != null)
			interessat.setRepresentant(representant);
		else
			interessat.setRepresentant(null);
		
		List<RegistreInteressat> interessats = new ArrayList<RegistreInteressat>();
		interessats.add(interessat);
		
		anotacio.setInteressats(interessats);
		
		String idiomaExtracte = (String)getValorOVariable(
				executionContext,
				idiomaCodi,
				varIdiomaCodi);
		anotacio.setIdiomaCodi((idiomaExtracte != null) ? idiomaExtracte : "ca");
				
		/**Dades complementàries de document**/
		documentInfo.setTipusDocument((String)getValorOVariable(
				executionContext,
				documentTipus,
				varDocumentTipus));
		documentInfo.setTipusDocumental((String)getValorOVariable(
				executionContext,
				documentTipusDocumental,
				varDocumentTipusDocumental));
		documentInfo.setValidesa((String)getValorOVariable(
				executionContext,
				documentValidesa,
				varDocumentValidesa));
		documentInfo.setOrigen(getValorOVariableInteger(
				executionContext,
				documentOrigen,
				varDocumentOrigen));
		documentInfo.setObservacions((String)getValorOVariable(
				executionContext,
				documentObservacions,
				varDocumentObservacions));
		documentInfo.setModeFirma(getValorOVariableInteger(
				executionContext,
				documentModeFirma,
				varDocumentModeFirma));
		/*************************************/
		
		List<DocumentInfo> annexos = new ArrayList<DocumentInfo>();
		annexos.add(documentInfo);
		RespostaRegistre resposta = registreSortida(
				executionContext,
				anotacio,
				annexos);
		
		if (varNumeroRegistre != null && !varNumeroRegistre.isEmpty())
			executionContext.setVariable(varNumeroRegistre, resposta.getNumero());
		
		if (varDataRegistre != null && !varDataRegistre.isEmpty())
			executionContext.setVariable(varDataRegistre, resposta.getData());
		
		Jbpm3HeliumBridge.getInstanceService().documentExpedientGuardarDadesRegistre(
				documentInfo.getId(),
				resposta.getNumero(),
				resposta.getData(),
				anotacio.getOficinaCodi(),
				Jbpm3HeliumBridge.getInstanceService().registreObtenirOficinaNom(
						resposta.getNumero(),
						anotacio.getUsuariCodi(),
						Jbpm3HeliumBridge.getInstanceService().getHeliumProperty("app.registre.codi.entitat"),
						expedient.getId()),
				false);
		
	}

	
	/** Construeix un extracte del tipus: 
	 * 	"[259/2019] Títol llarg...: Títol del document que ..."
	 * 
	 * @param expedient
	 * @param documentInfo
	 * @return
	 */
	private String buildExtracte(ExpedientDto expedient, DocumentInfo documentInfo) {
		StringBuilder extracte = new StringBuilder();
		// Número i títol
		if (expedient.getNumero() != null && ! expedient.getNumero().isEmpty())
			extracte.append("[").append(expedient.getNumero()).append("]");
		if (expedient.getTitol() != null && ! expedient.getTitol().isEmpty()) {
			if (extracte.length() > 0)
				extracte.append(" ");
			extracte.append(this.abreuja(expedient.getTitol(), 50));
		}
		if (extracte.length() > 0)
			extracte.append(": ");
		if (documentInfo.getTitol() != null )
			extracte.append(documentInfo.getTitol());
		else if (documentInfo.getCodiDocument() != null)
			extracte.append(documentInfo.getCodiDocument());
		
		return this.abreuja(extracte.toString(), 250);
	}
	
	/** Escurça un text. */
	private String abreuja(String text, int maxim) {
		if (text.length() > maxim && maxim - 3 > 0) {
			text = text.substring(0, maxim - 3) + "...";
		}
		return text;
	}


	/**
	 * Registra un document de sortida
	 * 
	 * @param dadesRegistre
	 * @param executionContext
	 * @return
	 */
	public RespostaRegistre registreSortida(
			ExecutionContext executionContext,
			RegistreAnotacio anotacio,
			List<DocumentInfo> documentsSortida) {
		
		List<RegistreAnnex> annexos = new ArrayList<RegistreAnnex>();
		for (DocumentInfo document: documentsSortida) {
			RegistreAnnex annex = new RegistreAnnex();
			annex.setTitol(document.getTitol());
			annex.setDataCaptura(document.getDataDocument());
			annex.setFitxerNom(document.getArxiuNom());
			annex.setFitxerContingut(document.getArxiuContingut());
			annex.setTipusDocument(document.getTipusDocument());
			annex.setTipusDocumental(document.getTipusDocumental());
			annex.setOrigen(document.getOrigen());
			annex.setFirmaMode(document.getModeFirma());
			annex.setObservacions(document.getObservacions());
			annex.setValidesa(document.getValidesa());
			annexos.add(annex);
		}
		anotacio.setAnnexos(annexos);
		RegistreIdDto anotacioId = Jbpm3HeliumBridge.getInstanceService().registreAnotacioSortida(
				anotacio,
				ConversioTipusHelper.toExpedientInfo(getExpedientActual(executionContext)).getId());
		RespostaRegistre resposta = new RespostaRegistre();
		resposta.setNumero(anotacioId.getNumero());
		resposta.setData(anotacioId.getData());
		return resposta;
	}


	public void setOrganOrigen(String organOrigen) {
		this.organOrigen = organOrigen;
	}


	public String getEntitatCodi() {
		return entitatCodi;
	}


	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}


	public String getVarEntitatCodi() {
		return varEntitatCodi;
	}


	public void setVarEntitatCodi(String varEntitatCodi) {
		this.varEntitatCodi = varEntitatCodi;
	}


	public void setVarOrganOrigen(String varOrganOrigen) {
		this.varOrganOrigen = varOrganOrigen;
	}


	public void setOficinaCodi(String oficinaCodi) {
		this.oficinaCodi = oficinaCodi;
	}


	public void setVarOficinaCodi(String varOficinaCodi) {
		this.varOficinaCodi = varOficinaCodi;
	}


	public void setLlibreCodi(String llibreCodi) {
		this.llibreCodi = llibreCodi;
	}


	public void setVarLlibreCodi(String varLlibreCodi) {
		this.varLlibreCodi = varLlibreCodi;
	}


	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}


	public void setVarExtracte(String varExtracte) {
		this.varExtracte = varExtracte;
	}


	public void setDocumentacioFisicaCodi(String documentacioFisicaCodi) {
		this.documentacioFisicaCodi = documentacioFisicaCodi;
	}


	public void setVarDocumentacioFisicaCodi(String varDocumentacioFisicaCodi) {
		this.varDocumentacioFisicaCodi = varDocumentacioFisicaCodi;
	}


	public void setAssumpteTipusCodi(String assumpteTipusCodi) {
		this.assumpteTipusCodi = assumpteTipusCodi;
	}


	public void setVarAssumpteTipusCodi(String varAssumpteTipusCodi) {
		this.varAssumpteTipusCodi = varAssumpteTipusCodi;
	}


	public void setAssumpteCodi(String assumpteCodi) {
		this.assumpteCodi = assumpteCodi;
	}


	public void setVarAssumpteCodi(String varAssumpteCodi) {
		this.varAssumpteCodi = varAssumpteCodi;
	}


	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}


	public void setVarUsuariCodi(String varUsuariCodi) {
		this.varUsuariCodi = varUsuariCodi;
	}


	public void setInteressatTipus(String interessatTipus) {
		this.interessatTipus = interessatTipus;
	}


	public void setVarInteressatTipus(String varInteressatTipus) {
		this.varInteressatTipus = varInteressatTipus;
	}


	public void setInteressatDocumentTipus(String interessatDocumentTipus) {
		this.interessatDocumentTipus = interessatDocumentTipus;
	}


	public void setVarInteressatDocumentTipus(String varInteressatDocumentTipus) {
		this.varInteressatDocumentTipus = varInteressatDocumentTipus;
	}


	public void setInteressatDocumentNum(String interessatDocumentNum) {
		this.interessatDocumentNum = interessatDocumentNum;
	}


	public void setVarInteressatDocumentNum(String varInteressatDocumentNum) {
		this.varInteressatDocumentNum = varInteressatDocumentNum;
	}


	public void setInteressatEmail(String interessatEmail) {
		this.interessatEmail = interessatEmail;
	}


	public void setVarInteressatEmail(String varInteressatEmail) {
		this.varInteressatEmail = varInteressatEmail;
	}


	public void setInteressatTelefon(String interessatTelefon) {
		this.interessatTelefon = interessatTelefon;
	}


	public void setVarInteressatTelefon(String varInteressatTelefon) {
		this.varInteressatTelefon = varInteressatTelefon;
	}


	public void setInteressatNom(String interessatNom) {
		this.interessatNom = interessatNom;
	}


	public void setVarInteressatNom(String varInteressatNom) {
		this.varInteressatNom = varInteressatNom;
	}


	public void setInteressatLlinatge1(String interessatLlinatge1) {
		this.interessatLlinatge1 = interessatLlinatge1;
	}


	public void setVarInteressatLlinatge1(String varInteressatLlinatge1) {
		this.varInteressatLlinatge1 = varInteressatLlinatge1;
	}


	public void setInteressatLlinatge2(String interessatLlinatge2) {
		this.interessatLlinatge2 = interessatLlinatge2;
	}


	public void setVarInteressatLlinatge2(String varInteressatLlinatge2) {
		this.varInteressatLlinatge2 = varInteressatLlinatge2;
	}


	public void setInteressatPais(String interessatPais) {
		this.interessatPais = interessatPais;
	}


	public void setVarInteressatPais(String varInteressatPais) {
		this.varInteressatPais = varInteressatPais;
	}


	public void setInteressatProvincia(String interessatProvincia) {
		this.interessatProvincia = interessatProvincia;
	}


	public void setVarInteressatProvincia(String varInteressatProvincia) {
		this.varInteressatProvincia = varInteressatProvincia;
	}


	public void setInteressatMunicipi(String interessatMunicipi) {
		this.interessatMunicipi = interessatMunicipi;
	}


	public void setVarInteressatMunicipi(String varInteressatMunicipi) {
		this.varInteressatMunicipi = varInteressatMunicipi;
	}


	public void setInteressatCodiPostal(String interessatCodiPostal) {
		this.interessatCodiPostal = interessatCodiPostal;
	}


	public void setVarInteressatCodiPostal(String varInteressatCodiPostal) {
		this.varInteressatCodiPostal = varInteressatCodiPostal;
	}


	public void setInteressatAdresa(String interessatAdresa) {
		this.interessatAdresa = interessatAdresa;
	}


	public void setVarInteressatAdresa(String varInteressatAdresa) {
		this.varInteressatAdresa = varInteressatAdresa;
	}


	public void setRepresentantTipus(String representantTipus) {
		this.representantTipus = representantTipus;
	}


	public void setVarRepresentantTipus(String varRepresentantTipus) {
		this.varRepresentantTipus = varRepresentantTipus;
	}


	public void setRepresentantDocumentTipus(String representantDocumentTipus) {
		this.representantDocumentTipus = representantDocumentTipus;
	}


	public void setVarRepresentantDocumentTipus(String varRepresentantDocumentTipus) {
		this.varRepresentantDocumentTipus = varRepresentantDocumentTipus;
	}


	public void setRepresentantDocumentNum(String representantDocumentNum) {
		this.representantDocumentNum = representantDocumentNum;
	}


	public void setVarRepresentantDocumentNum(String varRepresentantDocumentNum) {
		this.varRepresentantDocumentNum = varRepresentantDocumentNum;
	}


	public void setRepresentantEmail(String representantEmail) {
		this.representantEmail = representantEmail;
	}


	public void setVarRepresentantEmail(String varRepresentantEmail) {
		this.varRepresentantEmail = varRepresentantEmail;
	}


	public void setRepresentantTelefon(String representantTelefon) {
		this.representantTelefon = representantTelefon;
	}


	public void setVarRepresentantTelefon(String varRepresentantTelefon) {
		this.varRepresentantTelefon = varRepresentantTelefon;
	}


	public void setRepresentantNom(String representantNom) {
		this.representantNom = representantNom;
	}


	public void setVarRepresentantNom(String varRepresentantNom) {
		this.varRepresentantNom = varRepresentantNom;
	}


	public void setRepresentantLlinatge1(String representantLlinatge1) {
		this.representantLlinatge1 = representantLlinatge1;
	}


	public void setVarRepresentantLlinatge1(String varRepresentantLlinatge1) {
		this.varRepresentantLlinatge1 = varRepresentantLlinatge1;
	}


	public void setRepresentantLlinatge2(String representantLlinatge2) {
		this.representantLlinatge2 = representantLlinatge2;
	}


	public void setVarRepresentantLlinatge2(String varRepresentantLlinatge2) {
		this.varRepresentantLlinatge2 = varRepresentantLlinatge2;
	}


	public void setRepresentantPais(String representantPais) {
		this.representantPais = representantPais;
	}


	public void setVarRepresentantPais(String varRepresentantPais) {
		this.varRepresentantPais = varRepresentantPais;
	}


	public void setRepresentantProvincia(String representantProvincia) {
		this.representantProvincia = representantProvincia;
	}


	public void setVarRepresentantProvincia(String varRepresentantProvincia) {
		this.varRepresentantProvincia = varRepresentantProvincia;
	}


	public void setRepresentantMunicipi(String representantMunicipi) {
		this.representantMunicipi = representantMunicipi;
	}


	public void setVarRepresentantMunicipi(String varRepresentantMunicipi) {
		this.varRepresentantMunicipi = varRepresentantMunicipi;
	}


	public void setRepresentantCodiPostal(String representantCodiPostal) {
		this.representantCodiPostal = representantCodiPostal;
	}


	public void setVarRepresentantCodiPostal(String varRepresentantCodiPostal) {
		this.varRepresentantCodiPostal = varRepresentantCodiPostal;
	}


	public void setRepresentantAdresa(String representantAdresa) {
		this.representantAdresa = representantAdresa;
	}


	public void setVarRepresentantAdresa(String varRepresentantAdresa) {
		this.varRepresentantAdresa = varRepresentantAdresa;
	}


	public void setIdiomaCodi(String idiomaCodi) {
		this.idiomaCodi = idiomaCodi;
	}


	public void setVarIdiomaCodi(String varIdiomaCodi) {
		this.varIdiomaCodi = varIdiomaCodi;
	}


	


	public void setDocumentValidesa(String documentValidesa) {
		this.documentValidesa = documentValidesa;
	}


	public void setVarDocumentValidesa(String varDocumentValidesa) {
		this.varDocumentValidesa = varDocumentValidesa;
	}


	public void setDocumentTipus(String documentTipus) {
		this.documentTipus = documentTipus;
	}


	public void setVarDocumentTipus(String varDocumentTipus) {
		this.varDocumentTipus = varDocumentTipus;
	}


	public void setDocumentTipusDocumental(String documentTipusDocumental) {
		this.documentTipusDocumental = documentTipusDocumental;
	}


	public void setVarDocumentTipusDocumental(String varDocumentTipusDocumental) {
		this.varDocumentTipusDocumental = varDocumentTipusDocumental;
	}


	public void setDocumentOrigen(String documentOrigen) {
		this.documentOrigen = documentOrigen;
	}


	public void setVarDocumentOrigen(String varDocumentOrigen) {
		this.varDocumentOrigen = varDocumentOrigen;
	}


	public void setDocumentObservacions(String documentObservacions) {
		this.documentObservacions = documentObservacions;
	}


	public void setVarDocumentObservacions(String varDocumentObservacions) {
		this.varDocumentObservacions = varDocumentObservacions;
	}


	public void setDocumentModeFirma(String documentModeFirma) {
		this.documentModeFirma = documentModeFirma;
	}


	public void setVarDocumentModeFirma(String varDocumentModeFirma) {
		this.varDocumentModeFirma = varDocumentModeFirma;
	}


	public void setVarNumeroRegistre(String varNumeroRegistre) {
		this.varNumeroRegistre = varNumeroRegistre;
	}


	public void setVarDataRegistre(String varDataRegistre) {
		this.varDataRegistre = varDataRegistre;
	}


	public void setCodiDocument(String codiDocument) {
		this.codiDocument = codiDocument;
	}


	public void setVarCodiDocument(String varCodiDocument) {
		this.varCodiDocument = varCodiDocument;
	}


	public void setInteressatEmailHabilitat(String interessatEmailHabilitat) {
		this.interessatEmailHabilitat = interessatEmailHabilitat;
	}


	public void setVarInteressatEmailHabilitat(String varInteressatEmailHabilitat) {
		this.varInteressatEmailHabilitat = varInteressatEmailHabilitat;
	}


	public void setInteressatRaoSocial(String interessatRaoSocial) {
		this.interessatRaoSocial = interessatRaoSocial;
	}


	public void setVarInteressatRaoSocial(String varInteressatRaoSocial) {
		this.varInteressatRaoSocial = varInteressatRaoSocial;
	}


	public void setInteressatCanalPreferent(String interessatCanalPreferent) {
		this.interessatCanalPreferent = interessatCanalPreferent;
	}


	public void setVarInteressatCanalPreferent(String varInteressatCanalPreferent) {
		this.varInteressatCanalPreferent = varInteressatCanalPreferent;
	}


	public void setInteressatObservacions(String interessatObservacions) {
		this.interessatObservacions = interessatObservacions;
	}


	public void setVarInteressatObservacions(String varInteressatObservacions) {
		this.varInteressatObservacions = varInteressatObservacions;
	}


	public void setRepresentantEmailHabilitat(String representantEmailHabilitat) {
		this.representantEmailHabilitat = representantEmailHabilitat;
	}


	public void setVarRepresentantEmailHabilitat(String varRepresentantEmailHabilitat) {
		this.varRepresentantEmailHabilitat = varRepresentantEmailHabilitat;
	}


	public void setRepresentantRaoSocial(String representantRaoSocial) {
		this.representantRaoSocial = representantRaoSocial;
	}


	public void setVarRepresentantRaoSocial(String varRepresentantRaoSocial) {
		this.varRepresentantRaoSocial = varRepresentantRaoSocial;
	}


	public void setRepresentantCanalPreferent(String representantCanalPreferent) {
		this.representantCanalPreferent = representantCanalPreferent;
	}


	public void setVarRepresentantCanalPreferent(String varRepresentantCanalPreferent) {
		this.varRepresentantCanalPreferent = varRepresentantCanalPreferent;
	}


	public void setRepresentantObservacions(String representantObservacions) {
		this.representantObservacions = representantObservacions;
	}


	public void setVarRepresentantObservacions(String varRepresentantObservacions) {
		this.varRepresentantObservacions = varRepresentantObservacions;
	}
}
