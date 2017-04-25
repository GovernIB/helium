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
	
//	Info de l'interessat
	private String interessatTipus;
	private String varInteressatTipus;
	private String interessatDocumentTipus;
	private String varInteressatDocumentTipus;
	private String interessatDocumentNum;
	private String varInteressatDocumentNum;
	private String interessatEmail;
	private String varInteressatEmail;
	private String interessatTelefon;
	private String varInteressatTelefon;
	private String interessatNom;
	private String varInteressatNom;
	private String interessatLlinatge1;
	private String varInteressatLlinatge1;
	private String interessatLlinatge2;
	private String varInteressatLlinatge2;
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
	
//	Info del representant
	private String representantTipus;
	private String varRepresentantTipus;
	private String representantDocumentTipus;
	private String varRepresentantDocumentTipus;
	private String representantDocumentNum;
	private String varRepresentantDocumentNum;
	private String representantEmail;
	private String varRepresentantEmail;
	private String representantTelefon;
	private String varRepresentantTelefon;
	private String representantNom;
	private String varRepresentantNom;
	private String representantLlinatge1;
	private String varRepresentantLlinatge1;
	private String representantLlinatge2;
	private String varRepresentantLlinatge2;
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
	
	private String idiomaCodi;
	private String varIdiomaCodi;
	
	private String varDocument;
	

	public void execute(ExecutionContext executionContext) throws Exception {
		if (!Jbpm3HeliumBridge.getInstanceService().isRegistreRegWeb3Actiu())
			throw new JbpmException("El plugin de registre no està configurat");
		RegistreAnotacio anotacio = new RegistreAnotacio();
		
//		Info de l'Assentament
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
		
		anotacio.setExtracte((String)getValorOVariable(
				executionContext,
				extracte,
				varExtracte));
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
		
		anotacio.setUsuariCodi(Jbpm3HeliumBridge.getInstanceService().getUsuariCodiActual());
		
		ExpedientDto expedient = getExpedientActual(executionContext);
		anotacio.setExpedientNumero(expedient.getIdentificador());
		
//		Info de l'interessat
		RegistreInteressat interessat = new RegistreInteressat();
		interessat.setTipus((String)getValorOVariable(
				executionContext,
				interessatTipus,
				varInteressatTipus));
		interessat.setDocumentTipus((String)getValorOVariable(
				executionContext,
				interessatDocumentTipus,
				varInteressatDocumentTipus));
		interessat.setDocumentNum((String)getValorOVariable(
				executionContext,
				interessatDocumentNum,
				varInteressatDocumentNum));
		interessat.setEmail((String)getValorOVariable(
				executionContext,
				interessatEmail,
				varInteressatEmail));
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
		
//		Info del representant
		RegistreInteressat representant = new RegistreInteressat();
		representant.setTipus((String)getValorOVariable(
				executionContext,
				representantTipus,
				varRepresentantTipus));
		representant.setDocumentTipus((String)getValorOVariable(
				executionContext,
				representantDocumentTipus,
				varRepresentantDocumentTipus));
		representant.setDocumentNum((String)getValorOVariable(
				executionContext,
				representantDocumentNum,
				varRepresentantDocumentNum));
		representant.setEmail((String)getValorOVariable(
				executionContext,
				representantEmail,
				varRepresentantEmail));
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
		
		if (representant.getTipus() != null && !representant.getTipus().isEmpty() && !("".equalsIgnoreCase(representant.getTipus())))
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
		
		DocumentInfo documentInfo = getDocumentInfo(
				executionContext,
				varDocument,
				true);
		
		List<DocumentInfo> annexos = new ArrayList<DocumentInfo>();
		annexos.add(documentInfo);
		RespostaRegistre resposta = registreSortida(
				executionContext,
				anotacio,
				annexos);
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


	public void setVarDocument(String varDocument) {
		this.varDocument = varDocument;
	}

}
