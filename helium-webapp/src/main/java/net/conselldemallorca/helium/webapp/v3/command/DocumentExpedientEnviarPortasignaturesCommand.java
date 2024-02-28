/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesPrioritatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesSimpleTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesTipusEnumDto;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientEnviarPortasignaturesCommand.EnviarPortasignatures;
import net.conselldemallorca.helium.webapp.v3.validator.DocumentEnviarPortasignatures;

/**
 * Command pel formulari d'enviament al portasignatures dels documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@DocumentEnviarPortasignatures(groups = {EnviarPortasignatures.class})
public class DocumentExpedientEnviarPortasignaturesCommand {

	
	private Long id;
	@NotEmpty(groups = {EnviarPortasignatures.class}) 
	@Size(max=256, groups = {EnviarPortasignatures.class})
	private String motiu;
	@Size(max = 64, groups = {EnviarPortasignatures.class})
	private String portafirmesEnviarFluxId;
	@Size(max = 255, groups = {EnviarPortasignatures.class})
	private String nom;
	private List<Long> annexos = new ArrayList<Long>();
	private boolean portafirmesActiu = false;
	@NotNull(groups = {EnviarPortasignatures.class})
	private PortafirmesTipusEnumDto portafirmesFluxTipus;
	@NotNull(groups = {EnviarPortasignatures.class})
	private PortafirmesPrioritatEnumDto portafirmesPrioritatTipus;

	private PortafirmesSimpleTipusEnumDto portafirmesSequenciaTipus;
	
	private String[] portafirmesResponsables;

	/** Nou flux ID en el cas que l'usuari el creï des de la modal d'enviament. 
	 * En crear-se el flux es fixarà des de portafirmesModalTancar. */
	private String portafirmesNouFluxId = null;
	
	public boolean isPortafirmesActiu() {
		return portafirmesActiu;
	}
	public void setPortafirmesActiu(boolean portafirmesActiu) {
		this.portafirmesActiu = portafirmesActiu;
	}
	public PortafirmesTipusEnumDto getPortafirmesFluxTipus() {
		return portafirmesFluxTipus;
	}
	public void setPortafirmesFluxTipus(PortafirmesTipusEnumDto portafirmesFluxTipus) {
		this.portafirmesFluxTipus = portafirmesFluxTipus;
	}
	public PortafirmesSimpleTipusEnumDto getPortafirmesSequenciaTipus() {
		return portafirmesSequenciaTipus;
	}
	public void setPortafirmesSequenciaTipus(PortafirmesSimpleTipusEnumDto portafirmesSequenciaTipus) {
		this.portafirmesSequenciaTipus = portafirmesSequenciaTipus;
	}
	public String[] getPortafirmesResponsables() {
		return portafirmesResponsables;
	}
	public void setPortafirmesResponsables(String[] portafirmesResponsables) {
		this.portafirmesResponsables = portafirmesResponsables;
	}
	
	public String getMotiu() {
		return motiu;
	}
	public void setMotiu(String motiu) {
		this.motiu = motiu;
	}

	public List<Long> getAnnexos() {
		return annexos;
	}
	public PortafirmesPrioritatEnumDto getPortafirmesPrioritatTipus() {
		return portafirmesPrioritatTipus;
	}
	public void setPortafirmesPrioritatTipus(PortafirmesPrioritatEnumDto portafirmesPrioritatTipus) {
		this.portafirmesPrioritatTipus = portafirmesPrioritatTipus;
	}
	public void setAnnexos(List<Long> annexos) {
		this.annexos = annexos;
	}
	public String getPortafirmesEnviarFluxId() {
		return portafirmesEnviarFluxId;
	}
	public void setPortafirmesEnviarFluxId(String portafirmesFluxId) {
		this.portafirmesEnviarFluxId = portafirmesFluxId;
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


	public String getPortafirmesNouFluxId() {
		return portafirmesNouFluxId;
	}
	public void setPortafirmesNouFluxId(String portafirmesNouFluxId) {
		this.portafirmesNouFluxId = portafirmesNouFluxId;
	}


	public interface EnviarPortasignatures {}

}
