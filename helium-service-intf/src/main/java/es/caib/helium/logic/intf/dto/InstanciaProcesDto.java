package es.caib.helium.logic.intf.dto;

import java.util.Date;


/**
 * DTO amb informació d'una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InstanciaProcesDto {

	private String id;
	private String instanciaProcesPareId;
	private String titol;
	private Date dataFi;
	private DefinicioProcesDto definicioProces;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInstanciaProcesPareId() {
		return instanciaProcesPareId;
	}
	public void setInstanciaProcesPareId(String instanciaProcesPareId) {
		this.instanciaProcesPareId = instanciaProcesPareId;
	}
	public String getTitol() {
		if (titol != null)
			return titol;
		return definicioProces.getJbpmKey() + " " + id;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public DefinicioProcesDto getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProcesDto definicioProces) {
		this.definicioProces = definicioProces;
	}
	public boolean isFinalitzat() {
		return dataFi != null;
	}


}
