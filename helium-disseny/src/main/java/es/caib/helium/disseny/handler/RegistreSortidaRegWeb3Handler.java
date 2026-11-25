package es.caib.helium.disseny.handler;

/**
 * Handler per registrar la sortida d'un document de l'expedient (RegWeb3).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreSortidaRegWeb3Handler {
	void setEntitatCodi(String entitatCodi);
	void setOrganOrigen(String organOrigen);
	void setOficinaCodi(String oficinaCodi);
	void setLlibreCodi(String llibreCodi);
	void setExtracte(String extracte);
	void setDocumentacioFisicaCodi(String documentacioFisicaCodi);
	void setAssumpteTipusCodi(String assumpteTipusCodi);
	void setAssumpteCodi(String assumpteCodi);
	void setUsuariCodi(String usuariCodi);
	void setInteressatTipus(String interessatTipus);
	void setInteressatDocumentTipus(String interessatDocumentTipus);
	void setInteressatDocumentNum(String interessatDocumentNum);
	void setInteressatEmail(String interessatEmail);
	void setInteressatTelefon(String interessatTelefon);
	void setInteressatNom(String interessatNom);
	void setInteressatLlinatge1(String interessatLlinatge1);
	void setInteressatLlinatge2(String interessatLlinatge2);
	void setInteressatRaoSocial(String interessatRaoSocial);
	void setInteressatPais(String interessatPais);
	void setInteressatProvincia(String interessatProvincia);
	void setInteressatMunicipi(String interessatMunicipi);
	void setInteressatCodiPostal(String interessatCodiPostal);
	void setInteressatAdresa(String interessatAdresa);
	void setRepresentantTipus(String representantTipus);
	void setRepresentantDocumentTipus(String representantDocumentTipus);
	void setRepresentantDocumentNum(String representantDocumentNum);
	void setRepresentantEmail(String representantEmail);
	void setRepresentantTelefon(String representantTelefon);
	void setRepresentantNom(String representantNom);
	void setRepresentantLlinatge1(String representantLlinatge1);
	void setRepresentantLlinatge2(String representantLlinatge2);
	void setRepresentantRaoSocial(String representantRaoSocial);
	void setRepresentantPais(String representantPais);
	void setRepresentantProvincia(String representantProvincia);
	void setRepresentantMunicipi(String representantMunicipi);
	void setRepresentantCodiPostal(String representantCodiPostal);
	void setRepresentantAdresa(String representantAdresa);
	void setIdiomaCodi(String idiomaCodi);
	void setCodiDocument(String codiDocument);
	void setDocumentValidesa(String documentValidesa);
	void setDocumentTipus(String documentTipus);
	void setDocumentTipusDocumental(String documentTipusDocumental);
	void setDocumentOrigen(String documentOrigen);
	void setDocumentObservacions(String documentObservacions);
	void setDocumentModeFirma(String documentModeFirma);
	void setVarNumeroRegistre(String varNumeroRegistre);
	void setVarDataRegistre(String varDataRegistre);
}
