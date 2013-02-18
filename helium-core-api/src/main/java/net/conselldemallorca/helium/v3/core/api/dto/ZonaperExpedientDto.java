/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.List;

/**
 * DTO amb informació d'un expedient de la zonaper.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ZonaperExpedientDto {

	protected String expedientIdentificador;
	protected String expedientClau;
	protected long unitatAdministrativa;
	protected String idioma;
	protected String descripcio;
	protected boolean autenticat;
	protected String representantNif;
	protected String representatNif;
	protected String representatNom;
	protected String tramitNumero;
	protected boolean avisosHabilitat;
	protected String avisosSMS;
	protected String avisosEmail;
	protected List<ZonaperEventDto> events;

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
	public List<ZonaperEventDto> getEvents() {
		return events;
	}
	public void setEvents(List<ZonaperEventDto> events) {
		this.events = events;
	}

}
