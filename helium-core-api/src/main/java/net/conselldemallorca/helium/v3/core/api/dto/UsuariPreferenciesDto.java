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
	private boolean filtroTareasActivas;
	private Long numElementosPagina;

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
}
