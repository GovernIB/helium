/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Command per modificar les dades d'integració dels tipus d'expedient amb Notib.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusIntegracioNotibCommand {
	
	@NotNull(groups = {Modificacio.class})
	@Size(max = 9, min = 9, groups = {Modificacio.class})
	private String notificacioEmisor;
	
	@NotNull(groups = {Modificacio.class})
	@Size(max = 6, groups = {Modificacio.class})
	private String notificacioCodiProcediment;
	
	@NotNull(groups = {Modificacio.class})
	@Size(max = 10, groups = {Modificacio.class})
	private String seuExpedientUnitatOrganitzativa;
	
	@NotNull(groups = {Modificacio.class})
	@Size(max = 256, groups = {Modificacio.class})
	private String seuRegistreOficina;
	
	@NotNull(groups = {Modificacio.class})
	@Size(max = 256, groups = {Modificacio.class})
	private String seuRegistreLlibre;
	
	@NotNull(groups = {Modificacio.class})
	@Size(max = 256, groups = {Modificacio.class})
	private String seuRegistreOrgan;
	
	@Size(max = 256, groups = {Modificacio.class})
	private String seuIdioma;
	
	@Size(max = 256, groups = {Modificacio.class})
	private String seuAvisTitol;
	
	@Size(max = 256, groups = {Modificacio.class})
	private String seuAvisText;
	
	@Size(max = 256, groups = {Modificacio.class})
	private String seuAvisTextMobil;
	
	@Size(max = 256, groups = {Modificacio.class})
	private String seuOficiTitol;
	
	@Size(max = 256, groups = {Modificacio.class})
	private String seuOficiText;
	
	private boolean parametresNotibActius;
	private boolean ntiActiu;
	
	
	public String getNotificacioEmisor() {
		return notificacioEmisor;
	}
	public void setNotificacioEmisor(String notificacioEmisor) {
		this.notificacioEmisor = notificacioEmisor;
	}
	
	public String getNotificacioCodiProcediment() {
		return notificacioCodiProcediment;
	}
	public void setNotificacioCodiProcediment(String notificacioCodiProcediment) {
		this.notificacioCodiProcediment = notificacioCodiProcediment;
	}
	
	public String getSeuExpedientUnitatOrganitzativa() {
		return seuExpedientUnitatOrganitzativa;
	}
	public void setSeuExpedientUnitatOrganitzativa(String seuExpedientUnitatOrganitzativa) {
		this.seuExpedientUnitatOrganitzativa = seuExpedientUnitatOrganitzativa;
	}
	
	public String getSeuRegistreOficina() {
		return seuRegistreOficina;
	}
	public void setSeuRegistreOficina(String seuRegistreOficina) {
		this.seuRegistreOficina = seuRegistreOficina;
	}
	
	public String getSeuRegistreLlibre() {
		return seuRegistreLlibre;
	}
	public void setSeuRegistreLlibre(String seuRegistreLlibre) {
		this.seuRegistreLlibre = seuRegistreLlibre;
	}
	
	public String getSeuRegistreOrgan() {
		return seuRegistreOrgan;
	}
	public void setSeuRegistreOrgan(String seuRegistreOrgan) {
		this.seuRegistreOrgan = seuRegistreOrgan;
	}
	
	public String getSeuIdioma() {
		return seuIdioma;
	}
	public void setSeuIdioma(String seuIdioma) {
		this.seuIdioma = seuIdioma;
	}
	
	public String getSeuAvisTitol() {
		return seuAvisTitol;
	}
	public void setSeuAvisTitol(String seuAvisTitol) {
		this.seuAvisTitol = seuAvisTitol;
	}
	
	public String getSeuAvisText() {
		return seuAvisText;
	}
	public void setSeuAvisText(String seuAvisText) {
		this.seuAvisText = seuAvisText;
	}
	
	public String getSeuAvisTextMobil() {
		return seuAvisTextMobil;
	}
	public void setSeuAvisTextMobil(String seuAvisTextMobil) {
		this.seuAvisTextMobil = seuAvisTextMobil;
	}
	
	public String getSeuOficiTitol() {
		return seuOficiTitol;
	}
	public void setSeuOficiTitol(String seuOficiTitol) {
		this.seuOficiTitol = seuOficiTitol;
	}
	
	public String getSeuOficiText() {
		return seuOficiText;
	}
	public void setSeuOficiText(String seuOficiText) {
		this.seuOficiText = seuOficiText;
	}
	
	public boolean isParametresNotibActius() {
		return parametresNotibActius;
	}
	public void setParametresNotibActius(boolean parametresNotibActius) {
		this.parametresNotibActius = parametresNotibActius;
	}

	public boolean isNtiActiu() {
		return ntiActiu;
	}
	public void setNtiActiu(boolean ntiActiu) {
		this.ntiActiu = ntiActiu;
	}

	public interface Modificacio {}
}
