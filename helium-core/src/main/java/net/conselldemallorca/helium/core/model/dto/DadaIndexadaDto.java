/**
 * 
 */
package net.conselldemallorca.helium.core.model.dto;

import java.util.ArrayList;
import java.util.List;



/**
 * DTO amb informació d'un camp indexat per Lucene
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadaIndexadaDto {

	private String definicioProcesCodi;
	private String campCodi;
	private String etiqueta;
	private Object valor;
	private String valorIndex;
	private String valorMostrar;

	private boolean multiple;
	private List<Object> valorMultiple;
	private List<String> valorIndexMultiple;
	private List<String> valorMostrarMultiple;



	public DadaIndexadaDto(
			String campCodi,
			String etiqueta) {
		this.campCodi = campCodi;
		this.etiqueta = etiqueta;
	}
	public DadaIndexadaDto(
			String definicioProcesCodi,
			String campCodi,
			String etiqueta) {
		this.definicioProcesCodi = definicioProcesCodi;
		this.campCodi = campCodi;
		this.etiqueta = etiqueta;
	}

	public String getDefinicioProcesCodi() {
		return definicioProcesCodi;
	}
	public void setDefinicioProcesCodi(String definicioProcesCodi) {
		this.definicioProcesCodi = definicioProcesCodi;
	}
	public String getCampCodi() {
		return campCodi;
	}
	public void setCampCodi(String campCodi) {
		this.campCodi = campCodi;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public Object getValor() {
		return valor;
	}
	public void setValor(Object valor) {
		this.valor = valor;
	}
	public String getValorIndex() {
		return valorIndex;
	}
	public void setValorIndex(String valorIndex) {
		this.valorIndex = valorIndex;
	}
	public String getValorMostrar() {
		return valorMostrar;
	}
	public void setValorMostrar(String valorMostrar) {
		this.valorMostrar = valorMostrar;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
	public List<Object> getValorMultiple() {
		return valorMultiple;
	}
	public void setValorMultiple(List<Object> valorMultiple) {
		this.valorMultiple = valorMultiple;
	}
	public List<String> getValorIndexMultiple() {
		return valorIndexMultiple;
	}
	public void setValorIndexMultiple(List<String> valorIndexMultiple) {
		this.valorIndexMultiple = valorIndexMultiple;
	}
	public List<String> getValorMostrarMultiple() {
		return valorMostrarMultiple;
	}
	public void setValorMostrarMultiple(List<String> valorMostrarMultiple) {
		this.valorMostrarMultiple = valorMostrarMultiple;
	}

	public void addValorMultiple(Object valor) {
		if (valorMultiple == null)
			valorMultiple = new ArrayList<Object>();
		valorMultiple.add(valor);
	}
	public void addValorIndexMultiple(String valor) {
		if (valorIndexMultiple == null)
			valorIndexMultiple = new ArrayList<String>();
		valorIndexMultiple.add(valor);
	}
	public void addValorMostrarMultiple(String valor) {
		if (valorMostrarMultiple == null)
			valorMostrarMultiple = new ArrayList<String>();
		valorMostrarMultiple.add(valor);
	}

	public boolean isDadaExpedient() {
		return definicioProcesCodi == null;
	}

	public String getReportFieldName() {
		if (definicioProcesCodi != null)
			return definicioProcesCodi + "/" + campCodi;
		else
			return campCodi;
	}

	public String getValorMostrarMultipleComText() {
		if (getValorMostrarMultiple() == null && getValorMultiple() == null)
			return null;
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (getValorMostrarMultiple() != null) {
			for (int i = 0; i < getValorMostrarMultiple().size(); i++) {
				sb.append(getValorMostrarMultiple().get(i));
				if (i < getValorMostrarMultiple().size() - 1)
					sb.append(", ");
			}
		} else if (getValorMultiple() != null) {
			for (int i = 0; i < getValorMultiple().size(); i++) {
				sb.append(getValorMultiple().get(i));
				if (i < getValorMultiple().size() - 1)
					sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public String toString() {
		if (isMultiple()) {
			return getValorMostrarMultipleComText();
		} else {
			if (valorMostrar != null)
				return valorMostrar;
			else if (valor != null)
				return valor.toString();
			else
				return null;
		}
	}

}
