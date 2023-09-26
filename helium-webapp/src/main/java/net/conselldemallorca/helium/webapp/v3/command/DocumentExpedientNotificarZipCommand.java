/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
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
public class DocumentExpedientNotificarZipCommand {

	
	private Long id;
	@NotEmpty(groups = {NotificarZip.class}) 
	@Size(max=256, groups = {NotificarZip.class})
	private String titol;
	@NotEmpty(groups = {NotificarZip.class}) 
	private List<Long> annexos = new ArrayList<Long>();
	@NotNull
	private NtiOrigenEnumDto ntiOrigen;
	@NotNull
	private NtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	@NotNull
	private NtiTipoDocumentalEnumDto ntiTipoDocumental;
	


	public List<Long> getAnnexos() {
		return annexos;
	}

	public void setAnnexos(List<Long> annexos) {
		this.annexos = annexos;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getTitol() {
		return titol;
	}

	public void setTitol(String titol) {
		this.titol = titol;
	}

	public NtiOrigenEnumDto getNtiOrigen() {
		return ntiOrigen;
	}

	public void setNtiOrigen(NtiOrigenEnumDto ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}

	public NtiEstadoElaboracionEnumDto getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}

	public void setNtiEstadoElaboracion(NtiEstadoElaboracionEnumDto ntiEstadoElaboracion) {
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
	}

	public NtiTipoDocumentalEnumDto getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}

	public void setNtiTipoDocumental(NtiTipoDocumentalEnumDto ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental;
	}




	public interface NotificarZip {}

}
