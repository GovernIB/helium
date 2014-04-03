package net.conselldemallorca.helium.webapp.v3.command;

/**
 * Command pel manteniment de meu perfil
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PersonaUsuariCommand {
	private Long expedientTipusId;
	private int listado;
	private String entornCodi;
	private boolean cabeceraReducida;
	private Long consultaId;
	private Long numElementosPagina;
	private boolean filtroExpedientesActivos;
	
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedienTipusId) {
		this.expedientTipusId = expedienTipusId;
	}
	public String getEntornCodi() {
		return entornCodi;
	}
	public void setEntornCodi(String entornCodi) {
		this.entornCodi = entornCodi;
	}
	public Long getConsultaId() {
		return consultaId;
	}
	public void setConsultaId(Long consultaId) {
		this.consultaId = consultaId;
	}
	public Long getNumElementosPagina() {
		return numElementosPagina;
	}
	public void setNumElementosPagina(Long numElementosPagina) {
		this.numElementosPagina = numElementosPagina;
	}
	public boolean isFiltroExpedientesActivos() {
		return filtroExpedientesActivos;
	}
	public void setFiltroExpedientesActivos(boolean filtroExpedientesActivos) {
		this.filtroExpedientesActivos = filtroExpedientesActivos;
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
}
