/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;

/**
 * DTO amb informació d'una àrea.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AlertaDto {

	public enum AlertaPrioritatDto {
		MOLT_BAIXA,
		BAIXA,
		NORMAL,
		ALTA,
		MOLT_ALTA
	}

	private Long id;
	private Date dataCreacio;
	private String destinatari;
	private String causa;
	private String text;
	private Date dataLectura;
	private Date dataEliminacio;
	private AlertaPrioritatDto prioritat;
	private ExpedientDto expedient;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}
	public String getDestinatari() {
		return destinatari;
	}
	public void setDestinatari(String destinatari) {
		this.destinatari = destinatari;
	}
	public String getCausa() {
		return causa;
	}
	public void setCausa(String causa) {
		this.causa = causa;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Date getDataLectura() {
		return dataLectura;
	}
	public void setDataLectura(Date dataLectura) {
		this.dataLectura = dataLectura;
	}
	public Date getDataEliminacio() {
		return dataEliminacio;
	}
	public void setDataEliminacio(Date dataEliminacio) {
		this.dataEliminacio = dataEliminacio;
	}
	public AlertaPrioritatDto getPrioritat() {
		return prioritat;
	}
	public void setPrioritat(AlertaPrioritatDto prioritat) {
		this.prioritat = prioritat;
	}
	public ExpedientDto getExpedient() {
		return expedient;
	}
	public void setExpedient(ExpedientDto expedient) {
		this.expedient = expedient;
	}

}
