/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioNotibCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioNotibCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusIntegracioNotib;

/**
 * Command per modificar les dades d'integració dels tipus d'expedient amb Notib.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusIntegracioNotib(groups = {Creacio.class, Modificacio.class})
public class ExpedientTipusIntegracioNotibCommand {
	
	private String notibEmisor;
	private String notibCodiProcediment;
	private String notibSeuUnitatAdministrativa;
	private String notibSeuCodiProcediment;
	private String notibSeuOficina;
	private String notibSeuLlibre;
	private String notibSeuOrgan;
	
	private String notibSeuIdioma;
	
	@Size(max = 100, groups = {Modificacio.class})
	private String notibAvisTitol;
	
	@Size(max = 1024, groups = {Modificacio.class})
	private String notibAvisText;
	
	@Size(max = 200, groups = {Modificacio.class})
	private String notibAvisTextSms;
	
	@Size(max = 256, groups = {Modificacio.class})
	private String notibOficiTitol;
	
	@Size(max = 1024, groups = {Modificacio.class})
	private String notibOficiText;
	
	private boolean notibActiu;
	private boolean ntiActiu;
	
	
	public String getNotibEmisor() {
		return notibEmisor;
	}

	public void setNotibEmisor(String notibEmisor) {
		this.notibEmisor = notibEmisor;
	}

	public String getNotibCodiProcediment() {
		return notibCodiProcediment;
	}

	public void setNotibCodiProcediment(String notibCodiProcediment) {
		this.notibCodiProcediment = notibCodiProcediment;
	}

	public String getNotibSeuUnitatAdministrativa() {
		return notibSeuUnitatAdministrativa;
	}

	public void setNotibSeuUnitatAdministrativa(String notibSeuUnitatAdministrativa) {
		this.notibSeuUnitatAdministrativa = notibSeuUnitatAdministrativa;
	}

	public String getNotibSeuCodiProcediment() {
		return notibSeuCodiProcediment;
	}

	public void setNotibSeuCodiProcediment(String notibSeuCodiProcediment) {
		this.notibSeuCodiProcediment = notibSeuCodiProcediment;
	}

	public String getNotibSeuOficina() {
		return notibSeuOficina;
	}

	public void setNotibSeuOficina(String notibSeuOficina) {
		this.notibSeuOficina = notibSeuOficina;
	}

	public String getNotibSeuLlibre() {
		return notibSeuLlibre;
	}

	public void setNotibSeuLlibre(String notibSeuLlibre) {
		this.notibSeuLlibre = notibSeuLlibre;
	}

	public String getNotibSeuOrgan() {
		return notibSeuOrgan;
	}

	public void setNotibSeuOrgan(String notibSeuOrgan) {
		this.notibSeuOrgan = notibSeuOrgan;
	}

	public String getNotibSeuIdioma() {
		return notibSeuIdioma;
	}

	public void setNotibSeuIdioma(String notibSeuIdioma) {
		this.notibSeuIdioma = notibSeuIdioma;
	}

	public String getNotibAvisTitol() {
		return notibAvisTitol;
	}

	public void setNotibAvisTitol(String notibAvisTitol) {
		this.notibAvisTitol = notibAvisTitol;
	}

	public String getNotibAvisText() {
		return notibAvisText;
	}

	public void setNotibAvisText(String notibAvisText) {
		this.notibAvisText = notibAvisText;
	}

	public String getNotibAvisTextSms() {
		return notibAvisTextSms;
	}

	public void setNotibAvisTextSms(String notibAvisTextSms) {
		this.notibAvisTextSms = notibAvisTextSms;
	}

	public String getNotibOficiTitol() {
		return notibOficiTitol;
	}

	public void setNotibOficiTitol(String notibOficiTitol) {
		this.notibOficiTitol = notibOficiTitol;
	}

	public String getNotibOficiText() {
		return notibOficiText;
	}

	public void setNotibOficiText(String notibOficiText) {
		this.notibOficiText = notibOficiText;
	}

	public boolean isNotibActiu() {
		return notibActiu;
	}

	public void setNotibActiu(boolean notibActiu) {
		this.notibActiu = notibActiu;
	}

	public boolean isNtiActiu() {
		return ntiActiu;
	}

	public void setNtiActiu(boolean ntiActiu) {
		this.ntiActiu = ntiActiu;
	}

	public interface Creacio {}
	public interface Modificacio {}
}
