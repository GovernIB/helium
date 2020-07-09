/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO amb informació d'una dada de d'una tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaDto extends HeretableDto implements Serializable {
	
	private static final long serialVersionUID = 3685209683973264851L;

	public enum TipusTascaDto {
		ESTAT,
		FORM,
		SIGNATURA
	}
	private Long id;
	private String nom;
	private Long definicioProcesId;
	private TipusTascaDto tipus;
	private String missatgeInfo;
	private String missatgeWarn;
	private String nomScript;
	private String expressioDelegacio; // "" / "on"
	private String recursForm;
	private String formExtern;
	private boolean tramitacioMassiva = false;
	private boolean finalitzacioSegonPla = false;
	private boolean ambRepro = false;
	private boolean mostrarAgrupacions = false;

	private String jbpmName;
	private List<CampTascaDto> camps = new ArrayList<CampTascaDto>();
	private List<DocumentTascaDto> documents = new ArrayList<DocumentTascaDto>();
	private List<FirmaTascaDto> firmes = new ArrayList<FirmaTascaDto>();
	
	
	/** Atribut que indica si la tasca és inicial. S'ha de fixar manualment aquest valor a les cerques. */
	private boolean inicial;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public Long getDefinicioProcesId() {
		return definicioProcesId;
	}
	public void setDefinicioProcesId(Long definicioProcesId) {
		this.definicioProcesId = definicioProcesId;
	}
	public TipusTascaDto getTipus() {
		return tipus;
	}
	public void setTipus(TipusTascaDto tipus) {
		this.tipus = tipus;
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
	public String getExpressioDelegacio() {
		return expressioDelegacio;
	}
	public void setExpressioDelegacio(String expressioDelegacio) {
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
	public String getJbpmName() {
		return jbpmName;
	}
	public void setJbpmName(String jbpmName) {
		this.jbpmName = jbpmName;
	}
	public List<CampTascaDto> getCamps() {
		return camps;
	}
	public void setCamps(List<CampTascaDto> camps) {
		this.camps = camps;
	}
	public List<DocumentTascaDto> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentTascaDto> documents) {
		this.documents = documents;
	}
	public List<FirmaTascaDto> getFirmes() {
		return firmes;
	}
	public void setFirmes(List<FirmaTascaDto> firmes) {
		this.firmes = firmes;
	}
	public int getCampsCount() {
		return camps.size();
	}
	public int getDocumentsCount() {
		return documents.size();
	}
	public int getFirmesCount() {
		return firmes.size();
	}
	public boolean isInicial() {
		return inicial;
	}
	public void setInicial(boolean inicial) {
		this.inicial = inicial;
	}
	public boolean isAmbRepro() {
		return ambRepro;
	}
	public void setAmbRepro(boolean ambRepros) {
		this.ambRepro = ambRepros;
	}
	public boolean isMostrarAgrupacions() {
		return mostrarAgrupacions;
	}
	public void setMostrarAgrupacions(boolean mostrarAgrupacions) {
		this.mostrarAgrupacions = mostrarAgrupacions;
	}
	
}
