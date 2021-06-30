/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import es.caib.emiserv.logic.intf.exportacio.ExpedientTipusExportacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusExportarCommand.Exportacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusExportarCommand.Importacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusExportarCommand.Upload;
import net.conselldemallorca.helium.webapp.v3.validator.Codi;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusExportar;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusImportar;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusUpload;

/**
 * Command per seleccionar la informació a exportar d'un tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusExportar(groups = {Exportacio.class})
@ExpedientTipusUpload(groups = {Upload.class})
@ExpedientTipusImportar(groups = {Importacio.class})
public class ExpedientTipusExportarCommand {

	@NotNull(groups = {Exportacio.class})
	private Long entornId = null;
	@NotNull(groups = {Exportacio.class})
	private Long id = null;	
	

	// Camps per a la importació
	/** Incloure o no dades bàsiques com títol, seqüències i altres dades de capçalera del tipus d'expedient. */
	private boolean dadesBasiques;
	/** Assenyala si sobreescriure les dades que ja existeixin amb el mateix codi */
	private boolean sobreEscriure;
	/** Determina si s'han de desplegar el .par de les definicions de procés quan aquestes ja existeixen. */
	private boolean desplegarDefinicions;
	@Codi(groups = {Importacio.class})
	/** Codi per a la importació sobre un nou tipus d'expedient. */
	private String codi;
	
	// Camps comuns per exportació i importació
	private List<String> estats;
	private List<String> variables;
	private List<String> agrupacions;
	private List<String> definicionsProces;
	private Map<String, Integer> definicionsVersions;
	private boolean integracioSistra;
	private boolean integracioForms;
	private List<String> enumeracions;
	private List<String> documents;
	private List<String> terminis;
	private List<String> accions;
	private List<String> dominis;
	private List<String> redireccions;
	private List<String> consultes;
	private MultipartFile file;
	
	/** Indica el codi de l'expedient tipus pare del qual hereta */
	private String expedientTipusPare;
	/** Indica si exportar les dades de relación amb tasques heretades i variables*/
	private boolean tasquesHerencia;
	
	public ExpedientTipusExportarCommand() {
		this.estats = new ArrayList<String>();
		this.variables = new ArrayList<String>();
		this.agrupacions = new ArrayList<String>();
		this.definicionsProces = new ArrayList<String>();
		this.setDefinicionsVersions(new HashMap<String, Integer>());
		this.enumeracions = new ArrayList<String>();
		this.documents = new ArrayList<String>();
		this.terminis = new ArrayList<String>();
		this.accions = new ArrayList<String>();
		this.dominis = new ArrayList<String>();
		this.redireccions = new ArrayList<String>();
		this.consultes = new ArrayList<String>();
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

	public boolean isDesplegarDefinicions() {
		return desplegarDefinicions;
	}

	public void setDesplegarDefinicions(boolean desplegarDefinicions) {
		this.desplegarDefinicions = desplegarDefinicions;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

	public List<String> getEstats() {
		return estats;
	}
	public void setEstats(List<String> estats) {
		this.estats = estats;
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
	public List<String> getDefinicionsProces() {
		return definicionsProces;
	}
	public void setDefinicionsProces(List<String> definicionsProces) {
		this.definicionsProces = definicionsProces;
	}
	public Map<String, Integer> getDefinicionsVersions() {
		return definicionsVersions;
	}

	public void setDefinicionsVersions(Map<String, Integer> definicionsVersions) {
		this.definicionsVersions = definicionsVersions;
	}

	public boolean isIntegracioSistra() {
		return integracioSistra;
	}
	public void setIntegracioSistra(boolean integracioSistra) {
		this.integracioSistra = integracioSistra;
	}
	public boolean isIntegracioForms() {
		return integracioForms;
	}
	public void setIntegracioForms(boolean integracioForms) {
		this.integracioForms = integracioForms;
	}
	public List<String> getEnumeracions() {
		return enumeracions;
	}
	public void setEnumeracions(List<String> enumeracions) {
		this.enumeracions = enumeracions;
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
	public List<String> getDominis() {
		return dominis;
	}
	public void setDominis(List<String> dominis) {
		this.dominis = dominis;
	}
	public List<String> getRedireccions() {
		return redireccions;
	}
	public void setRedireccions(List<String> redireccions) {
		this.redireccions = redireccions;
	}
	public List<String> getConsultes() {
		return consultes;
	}
	public void setConsultes(List<String> consultes) {
		this.consultes = consultes;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	public boolean isTasquesHerencia() {
		return tasquesHerencia;
	}
	public void setTasquesHerencia(boolean tasquesHerencia) {
		this.tasquesHerencia = tasquesHerencia;
	}

	public String getExpedientTipusPare() {
		return expedientTipusPare;
	}

	public void setExpedientTipusPare(String expedientTipusPare) {
		this.expedientTipusPare = expedientTipusPare;
	}

	/** Serveix per guardar el resultat del processament del fitxer després de la validació
	 * del POST de la comanda d'importació per no haver de processar el fitxer dues vegades.
	 */
	private ExpedientTipusExportacio exportacio = null;

	public ExpedientTipusExportacio getExportacio() {
		return exportacio;
	}
	public void setExportacio(ExpedientTipusExportacio exportacio) {
		this.exportacio = exportacio;
	}
	
	public interface Exportacio {}
	public interface Upload{}	
	public interface Importacio {}	
}
