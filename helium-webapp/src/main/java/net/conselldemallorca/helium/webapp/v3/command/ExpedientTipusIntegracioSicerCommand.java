/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Command per modificar les dades d'integració dels tipus d'expedient amb Sistra.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusIntegracioSicerCommand {

	@NotNull(groups = {Modificacio.class})
	private Long id;
	private boolean sicerIntegracioActiva;
	@Size(max = 2, groups = {Modificacio.class})
	private String sicerProducteCodi;
	@Size(max = 8, groups = {Modificacio.class})
	private String sicerClientCodi;
	@Size(max = 7, groups = {Modificacio.class})
	private String sicerPuntAdmissioCodi;
	@Size(max = 50, groups = {Modificacio.class})
	private String sicerNomLlinatges;
	@Size(max = 50, groups = {Modificacio.class})
	private String sicerDireccio;
	@Size(max = 40, groups = {Modificacio.class})
	private String sicerPoblacio;
	@Size(max = 5, groups = {Modificacio.class})
	private String sicerCodiPostal;
	private String sicerSftpUser;
	private String sicerSftpPassword;
	

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public boolean isSicerIntegracioActiva() {
		return sicerIntegracioActiva;
	}
	public void setSicerIntegracioActiva(boolean sicerIntegracioActiva) {
		this.sicerIntegracioActiva = sicerIntegracioActiva;
	}

	public String getSicerProducteCodi() {
		return sicerProducteCodi;
	}
	public void setSicerProducteCodi(String sicerProducteCodi) {
		this.sicerProducteCodi = sicerProducteCodi;
	}

	public String getSicerClientCodi() {
		return sicerClientCodi;
	}
	public void setSicerClientCodi(String sicerClientCodi) {
		this.sicerClientCodi = sicerClientCodi;
	}

	public String getSicerPuntAdmissioCodi() {
		return sicerPuntAdmissioCodi;
	}
	public void setSicerPuntAdmissioCodi(String sicerPuntAdmissioCodi) {
		this.sicerPuntAdmissioCodi = sicerPuntAdmissioCodi;
	}

	public String getSicerNomLlinatges() {
		return sicerNomLlinatges;
	}
	public void setSicerNomLlinatges(String sicerNomLlinatges) {
		this.sicerNomLlinatges = sicerNomLlinatges;
	}

	public String getSicerDireccio() {
		return sicerDireccio;
	}
	public void setSicerDireccio(String sicerDireccio) {
		this.sicerDireccio = sicerDireccio;
	}

	public String getSicerPoblacio() {
		return sicerPoblacio;
	}
	public void setSicerPoblacio(String sicerPoblacio) {
		this.sicerPoblacio = sicerPoblacio;
	}

	public String getSicerCodiPostal() {
		return sicerCodiPostal;
	}
	public void setSicerCodiPostal(String sicerCodiPostal) {
		this.sicerCodiPostal = sicerCodiPostal;
	}

	public String getSicerSftpUser() {
		return sicerSftpUser;
	}
	public void setSicerSftpUser(String sicerSftpUser) {
		this.sicerSftpUser = sicerSftpUser;
	}

	public String getSicerSftpPassword() {
		return sicerSftpPassword;
	}
	public void setSicerSftpPassword(String sicerSftpPassword) {
		this.sicerSftpPassword = sicerSftpPassword;
	}

	public interface Modificacio {}
}
