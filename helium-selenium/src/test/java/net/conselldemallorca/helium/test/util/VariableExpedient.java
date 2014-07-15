package net.conselldemallorca.helium.test.util;

import java.util.ArrayList;
import java.util.List;

public class VariableExpedient {
	private String codi;
	private String etiqueta;
	private String tipo;
	private String observaciones;
	private boolean oculta;
	private boolean obligatorio;
	private boolean multiple;
	private boolean readOnly;
	
	List<VariableExpedient> registro = new ArrayList<VariableExpedient>();

	public List<VariableExpedient> getRegistro() {
		return registro;
	}

	public void setRegistro(List<VariableExpedient> registro) {
		this.registro = registro;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public boolean isOculta() {
		return oculta;
	}

	public void setOculta(boolean oculta) {
		this.oculta = oculta;
	}

	public boolean isObligatorio() {
		return obligatorio;
	}

	public void setObligatorio(boolean obligatorio) {
		this.obligatorio = obligatorio;
	}

	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
}
