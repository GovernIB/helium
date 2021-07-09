/**
 * 
 */
package es.caib.helium.back.command;

import es.caib.helium.back.validator.Codi;
import es.caib.helium.back.validator.ExpedientTipusConsulta;
import es.caib.helium.logic.intf.dto.ConsultaDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.IOException;
/**
 * Command per editar la informació de les consultes dels tipus d'expedient 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusConsulta(groups = {ExpedientTipusConsultaCommand.Creacio.class, ExpedientTipusConsultaCommand.Modificacio.class})
public class ExpedientTipusConsultaCommand {
	
	private Long expedientTipusId;
	private Long id;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 64, groups = {Creacio.class, Modificacio.class})
	@Codi(groups = {Creacio.class, Modificacio.class})
	private String codi;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String nom;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String descripcio;
	private String informeNom;
	private byte[] informeContingut;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	private String formatExport;
	@Size(max = 1024, groups = {Creacio.class, Modificacio.class})
	private String valorsPredefinits;
	private boolean exportarActiu;
	private boolean ocultarActiu;
	
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getInformeNom() {
		return informeNom;
	}
	public void setInformeNom(String informeNom) {
		this.informeNom = informeNom;
	}
	public byte[] getInformeContingut() {
		return informeContingut;
	}
	public void setInformeContingut(byte[] informeContingut) {
		this.informeContingut = informeContingut;
	}
	public String getFormatExport() {
		return formatExport;
	}
	public void setFormatExport(String formatExport) {
		this.formatExport = formatExport;
	}
	public String getValorsPredefinits() {
		return valorsPredefinits;
	}
	public void setValorsPredefinits(String valorsPredefinits) {
		this.valorsPredefinits = valorsPredefinits;
	}
	public boolean isExportarActiu() {
		return exportarActiu;
	}
	public void setExportarActiu(boolean exportarActiu) {
		this.exportarActiu = exportarActiu;
	}
	public boolean isOcultarActiu() {
		return ocultarActiu;
	}
	public void setOcultarActiu(boolean ocultarActiu) {
		this.ocultarActiu = ocultarActiu;
	}
	
	public static ConsultaDto asConsultaDto(ExpedientTipusConsultaCommand command) throws IOException {
		ConsultaDto dto = new ConsultaDto();
		dto.setId(command.getId());
		dto.setCodi(command.getCodi());
		dto.setNom(command.getNom());
		dto.setDescripcio(command.getDescripcio());
		dto.setFormatExport(command.getFormatExport());
		dto.setValorsPredefinits(command.getValorsPredefinits());
		dto.setExportarActiu(command.isExportarActiu());
		dto.setOcultarActiu(command.isOcultarActiu());
		dto.setInformeNom(command.getInformeNom());
		dto.setInformeContingut(command.getInformeContingut());
		return dto;
	}
	public interface Creacio {}
	public interface Modificacio {}
}
