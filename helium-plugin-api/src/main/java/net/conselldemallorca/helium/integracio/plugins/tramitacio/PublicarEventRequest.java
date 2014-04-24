/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.tramitacio;


/**
 * 
 * @author Limit Tecnologies
 */
public class PublicarEventRequest {

	protected String expedientIdentificador;
	protected String expedientClau;
	protected long unitatAdministrativa;
	protected String representantNif;
	protected String representatNom;
	protected String representatApe1;
	protected String representatApe2;
	protected Event event;

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
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public String getRepresentantNif() {
		return representantNif;
	}
	public void setRepresentantNif(String representantNif) {
		this.representantNif = representantNif;
	}
	public String getRepresentatNom() {
		return representatNom;
	}
	public void setRepresentatNom(String representatNom) {
		this.representatNom = representatNom;
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
}
