package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

public class JustificantRecepcioInfo {

	private Date data;
	protected TipoEstadoNotificacion estado;
	protected XMLGregorianCalendar fechaAcuseRecibo;
	protected String ficheroAcuseReciboClave;
	protected Long ficheroAcuseReciboCodigo;
	protected List<DetalleAviso> avisos;

	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public TipoEstadoNotificacion getEstado() {
		return estado;
	}
	public void setEstado(TipoEstadoNotificacion estado) {
		this.estado = estado;
	}
	public XMLGregorianCalendar getFechaAcuseRecibo() {
		return fechaAcuseRecibo;
	}
	public void setFechaAcuseRecibo(XMLGregorianCalendar fechaAcuseRecibo) {
		this.fechaAcuseRecibo = fechaAcuseRecibo;
	}
	public String getFicheroAcuseReciboClave() {
		return ficheroAcuseReciboClave;
	}
	public void setFicheroAcuseReciboClave(String ficheroAcuseReciboClave) {
		this.ficheroAcuseReciboClave = ficheroAcuseReciboClave;
	}
	public Long getFicheroAcuseReciboCodigo() {
		return ficheroAcuseReciboCodigo;
	}
	public void setFicheroAcuseReciboCodigo(Long ficheroAcuseReciboCodigo) {
		this.ficheroAcuseReciboCodigo = ficheroAcuseReciboCodigo;
	}
	public List<DetalleAviso> getAvisos() {
		return avisos;
	}
	public void setAvisos(List<DetalleAviso> avisos) {
		this.avisos = avisos;
	}
}
