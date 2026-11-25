package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.model.DadesRegistreInteressat;
import es.caib.helium.bpmn.model.DadesRegistreSortida;
import es.caib.helium.bpmn.model.RespostaRegistre;
import lombok.Setter;

/**
 * Implementació del handler que registra la sortida d'un document de l'expedient (RegWeb3) i en desa les dades de
 * registre al document.
 *
 * <p>Si no s'informa l'extracte, es construeix amb el número i el títol de l'expedient i el títol del document; si no
 * s'informa l'usuari, és l'usuari actual; si no s'informa l'idioma, és el català. El representant només s'afegeix si
 * se n'informa el tipus. {@code varNumeroRegistre} i {@code varDataRegistre} són els codis de les dades de
 * l'expedient on es desen el número i la data de registre.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class RegistreSortidaRegWeb3Handler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.RegistreSortidaRegWeb3Handler {

	private String entitatCodi;
	private String organOrigen;
	private String oficinaCodi;
	private String llibreCodi;
	private String extracte;
	private String documentacioFisicaCodi;
	private String assumpteTipusCodi;
	private String assumpteCodi;
	private String usuariCodi;
	private String interessatTipus;
	private String interessatDocumentTipus;
	private String interessatDocumentNum;
	private String interessatEmail;
	private String interessatTelefon;
	private String interessatNom;
	private String interessatLlinatge1;
	private String interessatLlinatge2;
	private String interessatRaoSocial;
	private String interessatPais;
	private String interessatProvincia;
	private String interessatMunicipi;
	private String interessatCodiPostal;
	private String interessatAdresa;
	private String representantTipus;
	private String representantDocumentTipus;
	private String representantDocumentNum;
	private String representantEmail;
	private String representantTelefon;
	private String representantNom;
	private String representantLlinatge1;
	private String representantLlinatge2;
	private String representantRaoSocial;
	private String representantPais;
	private String representantProvincia;
	private String representantMunicipi;
	private String representantCodiPostal;
	private String representantAdresa;
	private String idiomaCodi;
	private String codiDocument;
	private String documentValidesa;
	private String documentTipus;
	private String documentTipusDocumental;
	private String documentOrigen;
	private String documentObservacions;
	private String documentModeFirma;
	private String varNumeroRegistre;
	private String varDataRegistre;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		DadesRegistreSortida dades = new DadesRegistreSortida();
		dades.setEntitatCodi(text(entitatCodi));
		dades.setOrganOrigen(text(organOrigen));
		dades.setOficinaCodi(text(oficinaCodi));
		dades.setLlibreCodi(text(llibreCodi));
		dades.setExtracte(text(extracte));
		dades.setDocumentacioFisicaCodi(text(documentacioFisicaCodi));
		dades.setAssumpteTipusCodi(text(assumpteTipusCodi));
		dades.setAssumpteCodi(text(assumpteCodi));
		dades.setUsuariCodi(text(usuariCodi));
		dades.setIdiomaCodi(text(idiomaCodi));

		DadesRegistreInteressat interessat = new DadesRegistreInteressat();
		interessat.setTipus(text(interessatTipus));
		interessat.setDocumentTipus(text(interessatDocumentTipus));
		interessat.setDocumentNum(text(interessatDocumentNum));
		interessat.setEmail(text(interessatEmail));
		interessat.setTelefon(text(interessatTelefon));
		interessat.setNom(text(interessatNom));
		interessat.setLlinatge1(text(interessatLlinatge1));
		interessat.setLlinatge2(text(interessatLlinatge2));
		interessat.setRaoSocial(text(interessatRaoSocial));
		interessat.setPais(text(interessatPais));
		interessat.setProvincia(text(interessatProvincia));
		interessat.setMunicipi(text(interessatMunicipi));
		interessat.setCodiPostal(text(interessatCodiPostal));
		interessat.setAdresa(text(interessatAdresa));
		dades.setInteressat(interessat);

		DadesRegistreInteressat representant = new DadesRegistreInteressat();
		representant.setTipus(text(representantTipus));
		representant.setDocumentTipus(text(representantDocumentTipus));
		representant.setDocumentNum(text(representantDocumentNum));
		representant.setEmail(text(representantEmail));
		representant.setTelefon(text(representantTelefon));
		representant.setNom(text(representantNom));
		representant.setLlinatge1(text(representantLlinatge1));
		representant.setLlinatge2(text(representantLlinatge2));
		representant.setRaoSocial(text(representantRaoSocial));
		representant.setPais(text(representantPais));
		representant.setProvincia(text(representantProvincia));
		representant.setMunicipi(text(representantMunicipi));
		representant.setCodiPostal(text(representantCodiPostal));
		representant.setAdresa(text(representantAdresa));
		dades.setRepresentant(representant);

		dades.setDocumentCodi(text(codiDocument));
		dades.setDocumentValidesa(text(documentValidesa));
		dades.setDocumentTipus(text(documentTipus));
		dades.setDocumentTipusDocumental(text(documentTipusDocumental));
		dades.setDocumentOrigen(asInteger(documentOrigen));
		dades.setDocumentObservacions(text(documentObservacions));
		dades.setDocumentModeFirma(asInteger(documentModeFirma));

		RespostaRegistre resposta = heliumApi.registreSortida(dades);
		if (!isBuit(varNumeroRegistre)) {
			heliumApi.setVariable(varNumeroRegistre.trim(), resposta.getNumero());
		}
		if (!isBuit(varDataRegistre)) {
			heliumApi.setVariable(varDataRegistre.trim(), resposta.getData());
		}
	}

	private static String text(String valor) {
		return isBuit(valor) ? null : valor.trim();
	}

}
