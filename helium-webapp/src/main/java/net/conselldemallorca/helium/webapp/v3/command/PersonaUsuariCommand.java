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

	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String dni;
	private String email;
	private boolean sexe;

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
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
	public boolean isSexe() {
		return sexe;
	}
	public void setSexe(boolean sexe) {
		this.sexe = sexe;
	}
}
