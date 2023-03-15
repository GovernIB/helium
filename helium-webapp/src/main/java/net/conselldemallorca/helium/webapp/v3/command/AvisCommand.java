package net.conselldemallorca.helium.webapp.v3.command;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.v3.core.api.dto.AvisDto;
import net.conselldemallorca.helium.v3.core.api.dto.AvisNivellEnumDto;
import net.conselldemallorca.helium.webapp.v3.command.AvisCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.AvisCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.validator.Avis;

/**
 * Command per al manteniment d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Avis(groups = { Creacio.class, Modificacio.class })
public class AvisCommand {
	
	
	private Long id;
	@NotEmpty(groups = {Creacio.class})
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String assumpte;
	@NotEmpty(groups = { Creacio.class, Modificacio.class })
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String missatge;
	@Pattern(groups = { Creacio.class, Modificacio.class }, regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]") //00:00 - 23:59
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String horaInici;
	@Pattern(groups = { Creacio.class, Modificacio.class }, regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]")
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String horaFi;

	@NotNull
	private Date dataInici;
	@NotNull
	private Date dataFinal;
	private Boolean actiu;
	@NotNull
	private AvisNivellEnumDto avisNivell;
	

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAssumpte() {
		return assumpte;
	}
	public void setAssumpte(String assumpte) {
		this.assumpte = assumpte;
	}
	public String getMissatge() {
		return missatge;
	}
	public void setMissatge(String missatge) {
		this.missatge = missatge;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFinal() {
		return dataFinal;
	}
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	
	public String getHoraInici() {
		return horaInici;
	}
	public void setHoraInici(String horaInici) {
		this.horaInici = horaInici;
	}
	public String getHoraFi() {
		return horaFi;
	}
	public void setHoraFi(String horaFi) {
		this.horaFi = horaFi;
	}
	public Boolean getActiu() {
		return actiu;
	}
	public void setActiu(Boolean actiu) {
		this.actiu = actiu;
	}
	public AvisNivellEnumDto getAvisNivell() {
		return avisNivell;
	}
	public void setAvisNivell(AvisNivellEnumDto avisNivell) {
		this.avisNivell = avisNivell;
	}
	public static  AvisCommand asCommand(AvisDto dto) {
			return ConversioTipusHelper.convertir(
					dto,
					AvisCommand.class);
	}
	public static  AvisDto asDto(AvisCommand command) {
			return ConversioTipusHelper.convertir(
					command,
					AvisDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public interface Creacio {};
	public interface Modificacio {};
}
