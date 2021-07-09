/**
 * 
 */
package es.caib.helium.back.command;

import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Command per editar la informació dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientEditarCommand {

	private Long expedientId;
	private String numero;
	@Size( max = 255, groups = {Editar.class})
	private String titol;
	private String iniciadorCodi;
	private String responsableCodi;
	private String responsableNomSencer;
	private Date dataInici;
	private String comentari;
	private Long estatId;
	private Double geoPosX;
	private Double geoPosY;
	private String geoReferencia;
	private String grupCodi;

	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getIniciadorCodi() {
		return iniciadorCodi;
	}
	public void setIniciadorCodi(String iniciadorCodi) {
		this.iniciadorCodi = iniciadorCodi;
	}
	public String getResponsableCodi() {
		return responsableCodi;
	}
	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public String getComentari() {
		return comentari;
	}
	public void setComentari(String comentari) {
		this.comentari = comentari;
	}
	public Long getEstatId() {
		return estatId;
	}
	public void setEstatId(Long estatId) {
		this.estatId = estatId;
	}
	public Double getGeoPosX() {
		return geoPosX;
	}
	public void setGeoPosX(Double geoPosX) {
		this.geoPosX = geoPosX;
	}
	public Double getGeoPosY() {
		return geoPosY;
	}
	public void setGeoPosY(Double geoPosY) {
		this.geoPosY = geoPosY;
	}
	public String getGeoReferencia() {
		return geoReferencia;
	}
	public void setGeoReferencia(String geoReferencia) {
		this.geoReferencia = geoReferencia;
	}
	public String getGrupCodi() {
		return grupCodi;
	}
	public void setGrupCodi(String grupCodi) {
		this.grupCodi = grupCodi;
	}
	public String getResponsableNomSencer() {
		return responsableNomSencer;
	}
	public void setResponsableNomSencer(String responsableNomSencer) {
		this.responsableNomSencer = responsableNomSencer;
	}
	
	public interface Editar {};

}
