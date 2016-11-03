/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusDocumentCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusDocumentCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.Codi;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusDocument;

/**
 * Command per editar la informació de les variables dels tipus d'expedient
 * i de les definicions de procés. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusDocument(groups = {Creacio.class, Modificacio.class})
public class ExpedientTipusDocumentCommand {
	
	private Long expedientTipusId;
	private Long definicioProcesId;
	private Long id;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 64, groups = {Creacio.class, Modificacio.class})
	@Codi(groups = {Creacio.class, Modificacio.class})
	private String codi;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String nom;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String descripcio;
	private String arxiuNom;
//	private byte[] arxiuContingut;
	private boolean plantilla;
	private String convertirExtensio;
	private boolean adjuntarAuto;
	private Long campId;
	private String extensionsPermeses;
	private String contentType;
	private String custodiaCodi;
	private Integer tipusDocPortasignatures;
	
	
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public Long getDefinicioProcesId() {
		return definicioProcesId;
	}
	public void setDefinicioProcesId(Long definicioProcesId) {
		this.definicioProcesId = definicioProcesId;
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
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}
//	public byte[] getArxiuContingut() {
//		return arxiuContingut;
//	}
//	public void setArxiuContingut(byte[] arxiuContingut) {
//		this.arxiuContingut = arxiuContingut;
//	}
	public boolean isPlantilla() {
		return plantilla;
	}
	public void setPlantilla(boolean plantilla) {
		this.plantilla = plantilla;
	}
	public String getConvertirExtensio() {
		return convertirExtensio;
	}
	public void setConvertirExtensio(String convertirExtensio) {
		this.convertirExtensio = convertirExtensio;
	}
	public boolean isAdjuntarAuto() {
		return adjuntarAuto;
	}
	public void setAdjuntarAuto(boolean adjuntarAuto) {
		this.adjuntarAuto = adjuntarAuto;
	}
	public Long getCampId() {
		return campId;
	}
	public void setCampId(Long campId) {
		this.campId = campId;
	}
	public String getExtensionsPermeses() {
		return extensionsPermeses;
	}
	public void setExtensionsPermeses(String extensionsPermeses) {
		this.extensionsPermeses = extensionsPermeses;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getCustodiaCodi() {
		return custodiaCodi;
	}
	public void setCustodiaCodi(String custodiaCodi) {
		this.custodiaCodi = custodiaCodi;
	}
	public Integer getTipusDocPortasignatures() {
		return tipusDocPortasignatures;
	}
	public void setTipusDocPortasignatures(Integer tipusDocPortasignatures) {
		this.tipusDocPortasignatures = tipusDocPortasignatures;
	}
	public static DocumentDto asDocumentDto(ExpedientTipusDocumentCommand command) {
		DocumentDto dto = new DocumentDto();
		dto.setId(command.getId());
		dto.setCodi(command.getCodi());
		dto.setNom(command.getNom());
		dto.setDescripcio(command.getDescripcio());
		dto.setArxiuNom(command.getArxiuNom());
		dto.setPlantilla(command.isPlantilla());
		dto.setConvertirExtensio(command.getConvertirExtensio());
		dto.setAdjuntarAuto(command.isAdjuntarAuto());
		if(command.getCampId() != null) {
			CampDto campDto = new CampDto();
			campDto.setId(command.getCampId());
			dto.setCampData(campDto);
		} else {
			dto.setCampData(null);
		}
		dto.setExtensionsPermeses(command.getExtensionsPermeses());
		dto.setContentType(command.getContentType());
		dto.setCustodiaCodi(command.getCustodiaCodi());
		dto.setTipusDocPortasignatures(command.getTipusDocPortasignatures());
		
		return dto;
	}

	public interface Creacio {}
	public interface Modificacio {}
}
