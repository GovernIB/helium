/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

/**
 * DTO amb informació de preferències d'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariPreferenciesDto {

	private String codi;
	private String defaultEntornCodi;
	private String idioma;
	private boolean cabeceraReducida;
	private int listado;
	private Long consultaId;
	private Long expedientTipusDefecteId;
	private boolean filtroTareasActivas;
	private Long numElementosPagina;
	private boolean correusBustia;
	private boolean correusBustiaAgrupatsDia;
	private String emailAlternatiu;

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getDefaultEntornCodi() {
		return defaultEntornCodi;
	}
	public void setDefaultEntornCodi(String defaultEntornCodi) {
		this.defaultEntornCodi = defaultEntornCodi;
	}
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	public boolean isCabeceraReducida() {
		return cabeceraReducida;
	}
	public void setCabeceraReducida(boolean cabeceraReducida) {
		this.cabeceraReducida = cabeceraReducida;
	}
	public int getListado() {
		return listado;
	}
	public void setListado(int listado) {
		this.listado = listado;
	}
	public Long getConsultaId() {
		return consultaId;
	}
	public void setConsultaId(Long consultaId) {
		this.consultaId = consultaId;
	}
	public boolean isFiltroTareasActivas() {
		return filtroTareasActivas;
	}
	public void setFiltroTareasActivas(boolean filtroTareasActivas) {
		this.filtroTareasActivas = filtroTareasActivas;
	}
	public Long getNumElementosPagina() {
		return numElementosPagina;
	}
	public void setNumElementosPagina(Long numElementosPagina) {
		this.numElementosPagina = numElementosPagina;
	}
	public Long getExpedientTipusDefecteId() {
		return expedientTipusDefecteId;
	}
	public void setExpedientTipusDefecteId(Long expedientTipusDefecteId) {
		this.expedientTipusDefecteId = expedientTipusDefecteId;
	}
	public boolean isCorreusBustia() {
		return correusBustia;
	}
	public void setCorreusBustia(boolean correusBustia) {
		this.correusBustia = correusBustia;
	}
	public boolean isCorreusBustiaAgrupatsDia() {
		return correusBustiaAgrupatsDia;
	}
	public void setCorreusBustiaAgrupatsDia(boolean correusBustiaAgrupatsDia) {
		this.correusBustiaAgrupatsDia = correusBustiaAgrupatsDia;
	}
	public String getEmailAlternatiu() {
		return emailAlternatiu;
	}
	public void setEmailAlternatiu(String emailAlternatiu) {
		this.emailAlternatiu = emailAlternatiu;
	}
	
}
