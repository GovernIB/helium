/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.webapp.v3.command.DefinicioProcesExportarCommand.Exportacio;
import net.conselldemallorca.helium.webapp.v3.command.DefinicioProcesExportarCommand.Importacio;
import net.conselldemallorca.helium.webapp.v3.command.DefinicioProcesExportarCommand.Upload;
import net.conselldemallorca.helium.webapp.v3.validator.DefinicioProcesExportar;
import net.conselldemallorca.helium.webapp.v3.validator.DefinicioProcesImportar;
import net.conselldemallorca.helium.webapp.v3.validator.DefinicioProcesUpload;

/**
 * Command per seleccionar la informació a exportar d'una definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@DefinicioProcesExportar(groups = {Exportacio.class})
@DefinicioProcesUpload(groups = {Upload.class})
@DefinicioProcesImportar(groups = {Importacio.class})
public class DefinicioProcesExportarCommand {

	/** Id de la definició de procés sobre la que s'importa la informació. */
	@NotNull(groups = {Exportacio.class})
	private Long id = null;	
	/** Id de l'entorn on es desplega la definició de procés. */
	private Long entornId;
	/** Id del tipus d'expedient on es desplega la definició de procés. */
	private Long expedientTipusId;

	// Camps per a la importació
	private boolean dadesBasiques;
	private boolean sobreEscriure;
	/** Codi per a la importació sobre una nova definició de procés. */
	private String codi;
	/** Per mostrar la versió que s'està important. */
	private Integer versio;
	// Camps comuns per exportació i importació
	private List<String> tasques;
	private List<String> variables;
	private List<String> agrupacions;
	private List<String> documents;
	private List<String> terminis;
	private List<String> accions;
	private MultipartFile file;

	public DefinicioProcesExportarCommand() {
		this.tasques = new ArrayList<String>();
		this.variables = new ArrayList<String>();
		this.agrupacions = new ArrayList<String>();
		this.documents = new ArrayList<String>();
		this.terminis = new ArrayList<String>();
		this.accions = new ArrayList<String>();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	public Long getEntornId() {
		return entornId;
	}
	public void setEntornId(Long entornId) {
		this.entornId = entornId;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public boolean isDadesBasiques() {
		return dadesBasiques;
	}
	public void setDadesBasiques(boolean dadesBasiques) {
		this.dadesBasiques = dadesBasiques;
	}
	public boolean isSobreEscriure() {
		return sobreEscriure;
	}
	public void setSobreEscriure(boolean sobreEscriure) {
		this.sobreEscriure = sobreEscriure;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public Integer getVersio() {
		return versio;
	}

	public void setVersio(Integer versio) {
		this.versio = versio;
	}

	public List<String> getTasques() {
		return tasques;
	}
	public void setTasques(List<String> tasques) {
		this.tasques = tasques;
	}

	public List<String> getVariables() {
		return variables;
	}
	public void setVariables(List<String> variables) {
		this.variables = variables;
	}

	public List<String> getAgrupacions() {
		return agrupacions;
	}
	public void setAgrupacions(List<String> agrupacions) {
		this.agrupacions = agrupacions;
	}
	public List<String> getDocuments() {
		return documents;
	}
	public void setDocuments(List<String> documents) {
		this.documents = documents;
	}
	public List<String> getTerminis() {
		return terminis;
	}
	public void setTerminis(List<String> terminis) {
		this.terminis = terminis;
	}
	public List<String> getAccions() {
		return accions;
	}
	public void setAccions(List<String> accions) {
		this.accions = accions;
	}	
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	/** Serveix per guardar el resultat del processament del fitxer després de la validació
	 * del POST de la comanda d'importació per no haver de processar el fitxer dues vegades.
	 */
	private DefinicioProcesExportacio exportacio = null;

	public DefinicioProcesExportacio getExportacio() {
		return exportacio;
	}
	public void setExportacio(DefinicioProcesExportacio exportacio) {
		this.exportacio = exportacio;
	}
	
	public interface Exportacio {}
	public interface Upload{}	
	public interface Importacio {}
}
