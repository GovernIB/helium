/**
 * 
 */
package es.caib.helium.integracio.plugins.tramitacio;

import java.util.List;


/**
 * 
 * @author Limit Tecnologies
 */
public class PublicarExpedientRequest {

	protected String expedientIdentificador;
	protected String expedientClau;
	protected long unitatAdministrativa;
	protected String idioma;
	protected String descripcio;
	protected boolean autenticat;
	protected String representantNif;
	protected String representatNif;
	protected String representatNom;
	protected String representatApe1;
	protected String representatApe2;
	protected String tramitNumero;
	protected boolean avisosHabilitat;
	protected String avisosSMS;
	protected String avisosEmail;
	protected List<Event> events;
	protected String codiProcediment;

	public String getExpedientIdentificador() {
		return expedientIdentificador;
	}
	public void setExpedientIdentificador(String expedientIdentificador) {
		this.expedientIdentificador = expedientIdentificador;
	}
	public String getExpedientClau() {
		return expedientClau;
	}
	public void setExpedientClau(String expedientClau) {
		this.expedientClau = expedientClau;
	}
	public long getUnitatAdministrativa() {
		return unitatAdministrativa;
	}
	public void setUnitatAdministrativa(long unitatAdministrativa) {
		this.unitatAdministrativa = unitatAdministrativa;
	}
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public boolean isAutenticat() {
		return autenticat;
	}
	public void setAutenticat(boolean autenticat) {
		this.autenticat = autenticat;
	}
	public String getRepresentantNif() {
		return representantNif;
	}
	public void setRepresentantNif(String representantNif) {
		this.representantNif = representantNif;
	}
	public String getRepresentatNif() {
		return representatNif;
	}
	public void setRepresentatNif(String representatNif) {
		this.representatNif = representatNif;
	}
	public String getRepresentatNom() {
		return representatNom;
	}
	public void setRepresentatNom(String representatNom) {
		this.representatNom = representatNom;
	}
	public String getTramitNumero() {
		return tramitNumero;
	}
	public void setTramitNumero(String tramitNumero) {
		this.tramitNumero = tramitNumero;
	}
	public boolean isAvisosHabilitat() {
		return avisosHabilitat;
	}
	public void setAvisosHabilitat(boolean avisosHabilitat) {
		this.avisosHabilitat = avisosHabilitat;
	}
	public String getAvisosSMS() {
		return avisosSMS;
	}
	public void setAvisosSMS(String avisosSMS) {
		this.avisosSMS = avisosSMS;
	}
	public String getAvisosEmail() {
		return avisosEmail;
	}
	public void setAvisosEmail(String avisosEmail) {
		this.avisosEmail = avisosEmail;
	}
	public String getCodiProcediment() {
		return codiProcediment;
	}
	public void setCodiProcediment(String codiProcediment) {
		this.codiProcediment = codiProcediment;
	}
	public List<Event> getEvents() {
		return events;
	}
	public void setEvents(List<Event> events) {
		this.events = events;
	}
	public String getRepresentatApe1() {
		return representatApe1;
	}
	public void setRepresentatApe1(String representatApe1) {
		this.representatApe1 = representatApe1;
	}
	public String getRepresentatApe2() {
		return representatApe2;
	}
	public void setRepresentatApe2(String representatApe2) {
		this.representatApe2 = representatApe2;
	}
	@Override
	public String toString() {
		return "PublicarExpedientRequest [expedientIdentificador=" + expedientIdentificador + ", expedientClau=" + expedientClau + ", unitatAdministrativa=" + unitatAdministrativa + ", idioma=" + idioma + ", descripcio=" + descripcio + ", autenticat=" + autenticat + ", representantNif=" + representantNif + ", representatNif=" + representatNif + ", representatNom=" + representatNom + ", representatApe1=" + representatApe1 + ", representatApe2=" + representatApe2 + ", tramitNumero=" + tramitNumero + ", avisosHabilitat=" + avisosHabilitat + ", avisosSMS=" + avisosSMS + ", avisosEmail=" + avisosEmail + ", events=" + events + "]";
	}
}
