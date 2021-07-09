/**
 * 
 */
package es.caib.helium.back.command;

import es.caib.helium.logic.intf.dto.TascaDto;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.Size;
/**
 * Command per editar la informació de les tasques de les definicions
 * de proces. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DefinicioProcesTascaCommand {
	
	private Long definicioProcesId;
	private Long id;
	/** Per mostrar el nom deshabilitat en el formulari. */ 
	private String jbpmName;
	@NotEmpty(groups = {Modificacio.class})
	@Size(max = 255, groups = {Modificacio.class})
	private String nom;
	@Size(max = 255, groups = {Modificacio.class})
	private String missatgeInfo;
	@Size(max = 255, groups = {Modificacio.class})
	private String missatgeWarn;
	@Size(max = 1024, groups = {Modificacio.class})
	private String nomScript;
	private boolean expressioDelegacio; // "" / "on"
	@Size(max = 255, groups = {Modificacio.class})
	private String recursForm;
	@Size(max = 255, groups = {Modificacio.class})
	private String formExtern;
	private boolean tramitacioMassiva = false;
	private boolean finalitzacioSegonPla = false;
	private boolean ambRepro = false;
	private boolean mostrarAgrupacions = false;
	
	
	public boolean isMostrarAgrupacions() {
		return mostrarAgrupacions;
	}
	public void setMostrarAgrupacions(boolean mostrarAgrupacions) {
		this.mostrarAgrupacions = mostrarAgrupacions;
	}
	public boolean isAmbRepro() {
		return ambRepro;
	}
	public void setAmbRepro(boolean ambRepro) {
		this.ambRepro = ambRepro;
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
	public String getJbpmName() {
		return this.jbpmName;
	}
	public void setJbpmName(String jbpmName) {
		this.jbpmName = jbpmName;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getMissatgeInfo() {
		return missatgeInfo;
	}
	public void setMissatgeInfo(String missatgeInfo) {
		this.missatgeInfo = missatgeInfo;
	}
	public String getMissatgeWarn() {
		return missatgeWarn;
	}
	public void setMissatgeWarn(String missatgeWarn) {
		this.missatgeWarn = missatgeWarn;
	}
	public String getNomScript() {
		return nomScript;
	}
	public void setNomScript(String nomScript) {
		this.nomScript = nomScript;
	}
	public boolean isExpressioDelegacio() {
		return expressioDelegacio;
	}
	public void setExpressioDelegacio(boolean expressioDelegacio) {
		this.expressioDelegacio = expressioDelegacio;
	}
	public String getRecursForm() {
		return recursForm;
	}
	public void setRecursForm(String recursForm) {
		this.recursForm = recursForm;
	}
	public String getFormExtern() {
		return formExtern;
	}
	public void setFormExtern(String formExtern) {
		this.formExtern = formExtern;
	}
	public boolean isTramitacioMassiva() {
		return tramitacioMassiva;
	}
	public void setTramitacioMassiva(boolean tramitacioMassiva) {
		this.tramitacioMassiva = tramitacioMassiva;
	}
	public boolean isFinalitzacioSegonPla() {
		return finalitzacioSegonPla;
	}
	public void setFinalitzacioSegonPla(boolean finalitzacioSegonPla) {
		this.finalitzacioSegonPla = finalitzacioSegonPla;
	}
	
	public static TascaDto asTascaDto(DefinicioProcesTascaCommand command) {
		TascaDto dto = new TascaDto();
		
		dto.setId(command.getId());
		dto.setNom(command.getNom());
		dto.setMissatgeInfo(command.getMissatgeInfo());
		dto.setMissatgeWarn(command.getMissatgeWarn());
		dto.setNomScript(command.getNomScript());
		dto.setExpressioDelegacio(command.isExpressioDelegacio() ? "on" : ""); 
		dto.setRecursForm(command.getRecursForm());
		dto.setFormExtern(command.getFormExtern());
		dto.setTramitacioMassiva(command.isTramitacioMassiva());
		dto.setFinalitzacioSegonPla(command.isFinalitzacioSegonPla());
		dto.setAmbRepro(command.isAmbRepro());
		dto.setMostrarAgrupacions(command.isMostrarAgrupacions());
		return dto;
	}
	
	public static DefinicioProcesTascaCommand toDefinicioProcesTascaCommand(TascaDto dto) {
		DefinicioProcesTascaCommand command = new DefinicioProcesTascaCommand();
		
		command.setId(dto.getId());
		command.setDefinicioProcesId(dto.getDefinicioProcesId());
		command.setJbpmName(dto.getJbpmName());
		command.setNom(dto.getNom());
		command.setMissatgeInfo(dto.getMissatgeInfo());
		command.setMissatgeWarn(dto.getMissatgeWarn());
		command.setNomScript(dto.getNomScript());
		command.setExpressioDelegacio("on".equals(dto.getExpressioDelegacio()));
		command.setRecursForm(dto.getRecursForm());
		command.setFormExtern(dto.getFormExtern());
		command.setTramitacioMassiva(dto.isTramitacioMassiva());
		command.setFinalitzacioSegonPla(dto.isFinalitzacioSegonPla());
		command.setAmbRepro(dto.isAmbRepro());
		command.setMostrarAgrupacions(dto.isMostrarAgrupacions());
		return command;
	}
	public interface Modificacio {}
}
