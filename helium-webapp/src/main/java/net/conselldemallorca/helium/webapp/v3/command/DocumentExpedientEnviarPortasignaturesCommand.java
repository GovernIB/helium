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
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusDocumentCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusDocumentCommand.Modificacio;

/**
 * Command pel formulari d'enviament al portasignatures dels documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentExpedientEnviarPortasignaturesCommand {

	
	private Long id;
	@NotEmpty(groups = {EnviarPortasignatures.class}) 
	@Size(max=256, groups = {EnviarPortasignatures.class})
	private String motiu;
//	@Size(max = 64, groups = { Creacio.class, Modificacio.class })
//	private String prioritat;
	@Size(max = 64, groups = { Creacio.class, Modificacio.class })
	private String portafirmesFluxId;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String nom;
	private List<Long> annexos = new ArrayList<Long>();
	private boolean portafirmesActiu = false;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private PortafirmesTipusEnumDto portafirmesFluxTipus;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private PortafirmesPrioritatEnumDto portafirmesPrioritatTipus;
	
	
	
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
	public String getPortafirmesResponsables() {
		return portafirmesResponsables;
	}
	public void setPortafirmesResponsables(String portafirmesResponsables) {
		this.portafirmesResponsables = portafirmesResponsables;
	}


	private PortafirmesSimpleTipusEnumDto portafirmesSequenciaTipus;
	private String portafirmesResponsables;
	
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
