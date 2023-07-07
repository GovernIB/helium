/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Command pel formulari d'enviament al portasignatures dels documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentExpedientEnviarPortasignaturesCommand {

	@NotEmpty(groups = {EnviarPortasignatures.class}) 
	@Size(max=256, groups = {EnviarPortasignatures.class})
	private Long id;
	private String motiu;
	private String prioritat;
	private String portafirmesFluxId;
	private String nom;
	
	
	private List<Long> annexos = new ArrayList<Long>();
	
	public String getMotiu() {
		return motiu;
	}
	public void setMotiu(String motiu) {
		this.motiu = motiu;
	}

	public String getPrioritat() {
		return prioritat;
	}
	public void setPrioritat(String prioritat) {
		this.prioritat = prioritat;
	}
	public List<Long> getAnnexos() {
		return annexos;
	}
	public void setAnnexos(List<Long> annexos) {
		this.annexos = annexos;
	}
	public String getPortafirmesFluxId() {
		return portafirmesFluxId;
	}
	public void setPortafirmesFluxId(String portafirmesFluxId) {
		this.portafirmesFluxId = portafirmesFluxId;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}


	public interface EnviarPortasignatures {}

}
