/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * DTO amb informació del resultat del mapeig de dades, documents i adjunts per una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AnotacioMapeigResultatDto implements Serializable {

	private static final long serialVersionUID = 1889459814094093493L;

	private String anotacioNumero = null;
	
	// Conjunts on guardar el resultat del mapeig. 
	Map<String, Object> dades = null;
	Map<String, DadesDocumentDto> documents = null;
	List<DadesDocumentDto> adjunts = null;
	List<AnotacioInteressatDto> interessats = null;
	
	// Conjunts d'errors amb el codi SISTRA com a clau i la descripció de l'error com a valor
	
	public Map<String, String> errorsDades = new HashMap<String, String>();
	public Map<String, String> errorsDocuments = new HashMap<String, String>();
	public Map<String, String> errorsAdjunts = new HashMap<String, String>();
	public Map<String, String> errorsInteressats = new HashMap<String, String>();

	public void setAnotacioNumero(String anotacioNumero) {
		this.anotacioNumero = anotacioNumero;
	}
	
	public String getAnotacioNumero() {
		return anotacioNumero;
	}
	
	public boolean isError() {
		return !errorsDades.isEmpty() || !errorsDocuments.isEmpty() || !errorsAdjunts.isEmpty() || !errorsInteressats.isEmpty();
	}
	
	public Map<String, Object> getDades() {
		return dades;
	}

	public void setDades(Map<String, Object> dades) {
		this.dades = dades;
	}

	public Map<String, DadesDocumentDto> getDocuments() {
		return documents;
	}

	public void setDocuments(Map<String, DadesDocumentDto> documents) {
		this.documents = documents;
	}

	public List<DadesDocumentDto> getAdjunts() {
		return adjunts;
	}

	public void setAdjunts(List<DadesDocumentDto> adjunts) {
		this.adjunts = adjunts;
	}

	public List<AnotacioInteressatDto> getInteressats() {
		return interessats;
	}

	public void setInteressats(List<AnotacioInteressatDto> interessats) {
		this.interessats = interessats;
	}

	public String getMissatgeAlerta() {
		StringBuilder msg = new StringBuilder();
		if (this.isError()) {
			msg.append("S'han produït errors en el mapeig " + (this.anotacioNumero != null ? "de l'anotació" + this.anotacioNumero : "")).append(" :");
			if (!this.getErrorsDades().isEmpty()) {
				msg.append(this.getErrorsDades().size()).append(" errors de dades");
				if (!this.getErrorsDocuments().isEmpty() && !this.getErrorsAdjunts().isEmpty()) {
					msg.append(", ");
				} else if (!this.getErrorsDocuments().isEmpty() || !this.getErrorsAdjunts().isEmpty()) {
					msg.append(" i ");
				}
			}
			if (!this.getErrorsDocuments().isEmpty()) {
				msg.append(this.getErrorsDocuments().size()).append(" errors de documents");
				if (!this.getErrorsAdjunts().isEmpty()) {
					msg.append(" i ");
				}
			}
			if (!this.getErrorsAdjunts().isEmpty()) {
				msg.append(this.getErrorsDocuments().size()).append(" errors d'adjunts");
			}
			if (!this.getErrorsInteressats().isEmpty()) {
				msg.append(this.getErrorsInteressats().size()).append(" errors d'interessats");
			}
			msg.append(". Es recomana reprocessar el mapeig de l'anotació i revisar els documents de l'expedient.");
		} else {
			msg.append("No s'ha produït cap error en el mapeig.");
		}
		return msg.toString();
	}
	
	/** Retorna una llista separada per comes dels mapejos que han fallat a partir de les claus de
	 * les llistes.
	 * @return
	 */
	public String getMapejosErrors() {
		List<String> mapejos = new ArrayList<String>();
		mapejos.addAll(this.errorsDades.keySet());
		mapejos.addAll(this.errorsDocuments.keySet());
		mapejos.addAll(this.errorsAdjunts.keySet());
		mapejos.addAll(this.errorsInteressats.keySet());
		return StringUtils.join(mapejos, ", ");
	}
	
	/** Retorna el missatge d'alerta amb els mapejos. */
	public String getMissatgeAlertaErrors() {
		StringBuilder errMsg = new StringBuilder();
		errMsg.append(this.getMissatgeAlerta())
			  .append(" Els mapejos que han fallat són: ")
			  .append(this.getMapejosErrors());
		return errMsg.toString();
	}

	public Map<String, String> getErrorsDades() {
		return errorsDades;
	}

	public Map<String, String> getErrorsDocuments() {
		return errorsDocuments;
	}

	public Map<String, String> getErrorsAdjunts() {
		return errorsAdjunts;
	}
	
	public Map<String, String> getErrorsInteressats() {
		return errorsInteressats;
	}
	
}
