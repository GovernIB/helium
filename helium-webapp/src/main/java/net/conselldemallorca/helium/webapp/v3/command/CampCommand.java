/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.webapp.v3.command.CampCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.CampCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.Codi;
import net.conselldemallorca.helium.webapp.v3.validator.Camp;

/**
 * Command per editar la informació de les varialbes dels tipus d'expedient 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Camp(groups = {Creacio.class, Modificacio.class})
public class CampCommand {
	
	private Long expedientTipusId;
	private Long definicioProcesId;
	private Long id;
	private Long agrupacioId;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 64, groups = {Creacio.class})
	@Codi(groups = {Creacio.class, Modificacio.class})
	private String codi;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private CampTipusDto tipus;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String etiqueta;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String observacions;
	private boolean multiple;
	private boolean ocult;
	/** No retrocedir valor */
	private boolean ignored;

	// Dades consulta
	private Long enumeracioId;
	private Long dominiId;
	private Long consultaId;
	boolean dominiIntern;
	
	// Paràmetres del domini
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String dominiIdentificador;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String dominiParams;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String dominiCampValor;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String dominiCampText;
	
	// Paràmetres de la consulta
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String consultaParams;
	@Size(max = 64, groups = {Creacio.class, Modificacio.class})
	private String consultaCampText;
	@Size(max = 64, groups = {Creacio.class, Modificacio.class})
	private String consultaCampValor;
	
	// Dades de la acció
	//@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String defprocJbpmKey;
	//@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String jbpmAction;
	
	boolean dominiCacheText;


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getAgrupacioId() {
		return agrupacioId;
	}
	public void setAgrupacioId(Long agrupacioId) {
		this.agrupacioId = agrupacioId;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public CampTipusDto getTipus() {
		return tipus;
	}
	public void setTipus(CampTipusDto tipus) {
		this.tipus = tipus;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public Long getDominiId() {
		return dominiId;
	}
	public void setDominiId(Long dominiId) {
		this.dominiId = dominiId;
	}
	public Long getEnumeracioId() {
		return enumeracioId;
	}
	public void setEnumeracioId(Long enumeracioId) {
		this.enumeracioId = enumeracioId;
	}
	public Long getConsultaId() {
		return consultaId;
	}
	public void setConsultaId(Long consultaId) {
		this.consultaId = consultaId;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public boolean isOcult() {
		return ocult;
	}
	public void setOcult(boolean ocult) {
		this.ocult = ocult;
	}
	public boolean isIgnored() {
		return ignored;
	}
	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}
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
	public boolean isDominiIntern() {
		return dominiIntern;
	}
	public void setDominiIntern(boolean dominiIntern) {
		this.dominiIntern = dominiIntern;
	}
	public String getDominiIdentificador() {
		return dominiIdentificador;
	}
	public void setDominiIdentificador(String dominiIdentificador) {
		this.dominiIdentificador = dominiIdentificador;
	}
	public String getDominiParams() {
		return dominiParams;
	}
	public void setDominiParams(String dominiParams) {
		this.dominiParams = dominiParams;
	}
	public String getDominiCampValor() {
		return dominiCampValor;
	}
	public void setDominiCampValor(String dominiCampValor) {
		this.dominiCampValor = dominiCampValor;
	}
	public String getDominiCampText() {
		return dominiCampText;
	}
	public void setDominiCampText(String dominiCampText) {
		this.dominiCampText = dominiCampText;
	}
	public String getConsultaParams() {
		return consultaParams;
	}
	public void setConsultaParams(String consultaParams) {
		this.consultaParams = consultaParams;
	}
	public String getConsultaCampText() {
		return consultaCampText;
	}
	public void setConsultaCampText(String consultaCampText) {
		this.consultaCampText = consultaCampText;
	}
	public String getConsultaCampValor() {
		return consultaCampValor;
	}
	public void setConsultaCampValor(String consultaCampValor) {
		this.consultaCampValor = consultaCampValor;
	}
	public String getDefprocJbpmKey() {
		return defprocJbpmKey;
	}
	public void setDefprocJbpmKey(String defprocJbpmKey) {
		this.defprocJbpmKey = defprocJbpmKey;
	}
	public String getJbpmAction() {
		return jbpmAction;
	}
	public void setJbpmAction(String jbpmAction) {
		this.jbpmAction = jbpmAction;
	}
	public boolean isDominiCacheText() {
		return dominiCacheText;
	}
	public void setDominiCacheText(boolean dominiCacheText) {
		this.dominiCacheText = dominiCacheText;
	}
	public static CampDto asCampDto(CampCommand command) {
		CampDto dto = new CampDto();
		dto.setId(command.getId());
		if(command.getAgrupacioId() != null) {
			CampAgrupacioDto agrupacioDto = new CampAgrupacioDto();
			agrupacioDto.setId(command.getAgrupacioId());
			dto.setAgrupacio(agrupacioDto);
		}
		dto.setCodi(command.getCodi());
		dto.setEtiqueta(command.getEtiqueta());
		dto.setTipus(command.getTipus());
		dto.setObservacions(command.getObservacions());
		dto.setDominiIdentificador(command.getDominiIdentificador());
		dto.setMultiple(command.isMultiple());
		dto.setOcult(command.isOcult());
		dto.setIgnored(command.isIgnored());		
		
		// Dades consulta
		if(command.getEnumeracioId() != null) {
			EnumeracioDto enumeracioDto = new EnumeracioDto();
			enumeracioDto.setId(command.getEnumeracioId());
			dto.setEnumeracio(enumeracioDto);
		}
		if(command.getDominiId() != null) {
			DominiDto dominiDto = new DominiDto();
			dominiDto.setId(command.getDominiId());
			dto.setDomini(dominiDto);
		}
		if(command.getConsultaId() != null) {
			ConsultaDto consultaDto = new ConsultaDto();
			consultaDto.setId(command.getConsultaId());
			dto.setConsulta(consultaDto);
		}
		dto.setDominiIntern(command.isDominiIntern());
		
		// Paràmetres del domini
		dto.setDominiIdentificador(command.getDominiIdentificador());
		dto.setDominiParams(command.getDominiParams());
		dto.setDominiCampValor(command.getDominiCampValor());
		dto.setDominiCampText(command.getDominiCampText());
		
		// Paràmetres de la consulta
		dto.setConsultaParams(command.getConsultaParams());
		dto.setConsultaCampText(command.getConsultaCampText());
		dto.setConsultaCampValor(command.getConsultaCampValor());
		
		// Dades de la acció
		dto.setDefprocJbpmKey(command.getDefprocJbpmKey());
		dto.setJbpmAction(command.getJbpmAction());
		
		dto.setDominiCacheText(command.isDominiCacheText());		
		
		return dto;
	}

	public interface Creacio {}
	public interface Modificacio {}
}
