/**
 * 
 */
package net.conselldemallorca.helium.report;

import java.util.ArrayList;
import java.util.List;



/**
 * Camp d'un report de JasperReports
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FieldValue implements Comparable<FieldValue> {

	private String definicioProcesCodi;
	private String campCodi;
	private String etiqueta;
	private Object valor;
	private String valorMostrar;
	private String valorOrdre;

	private boolean multiple;
	private List<Object> valorMultiple;
	private List<String> valorMostrarMultiple;
	private List<String> valorOrdreMultiple;



	public FieldValue(
			String campCodi,
			String etiqueta) {
		this.campCodi = campCodi;
		this.etiqueta = etiqueta;
	}
	public FieldValue(
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
	public String getValorMostrar() {
		return valorMostrar;
	}
	public void setValorMostrar(String valorMostrar) {
		this.valorMostrar = valorMostrar;
	}
	public String getValorOrdre() {
		return valorOrdre;
	}
	public void setValorOrdre(String valorOrdre) {
		this.valorOrdre = valorOrdre;
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
	public List<String> getValorMostrarMultiple() {
		return valorMostrarMultiple;
	}
	public void setValorMostrarMultiple(List<String> valorMostrarMultiple) {
		this.valorMostrarMultiple = valorMostrarMultiple;
	}
	public List<String> getValorOrdreMultiple() {
		return valorOrdreMultiple;
	}
	public void setValorOrdreMultiple(List<String> valorOrdreMultiple) {
		this.valorOrdreMultiple = valorOrdreMultiple;
	}

	public void addValorMultiple(Object valor) {
		if (valorMultiple == null)
			valorMultiple = new ArrayList<Object>();
		valorMultiple.add(valor);
	}
	public void addValorMostrarMultiple(String valor) {
		if (valorMostrarMultiple == null)
			valorMostrarMultiple = new ArrayList<String>();
		valorMostrarMultiple.add(valor);
	}
	public void addValorOrdreMultiple(String valor) {
		if (valorOrdreMultiple == null)
			valorOrdreMultiple = new ArrayList<String>();
		valorOrdreMultiple.add(valor);
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
		if (getValorMostrarMultiple() != null) {
			for (int i = 0; i < getValorMostrarMultiple().size(); i++) {
				sb.append(getValorMostrarMultiple().get(i));
				if (i < getValorMostrarMultiple().size() - 1)
					sb.append("\n");
			}
		} else if (getValorMultiple() != null) {
			for (int i = 0; i < getValorMultiple().size(); i++) {
				sb.append(getValorMultiple().get(i));
				if (i < getValorMultiple().size() - 1)
					sb.append("\n");
			}
		}
		return sb.toString();
	}

	public int count() {
		int count = 0;
		if (isMultiple() && valorMultiple != null)
			count = valorMultiple.size();
		else if (!isMultiple() && valor != null)
			count = 1;
		return count;
	}
	public int countEquals(Object value) {
		int count = 0;
		if (isMultiple() && valorMultiple != null) {
			for (Object obj: valorMultiple) {
				if (obj == null && value == null)
					count++;
				else if (obj != null && obj.equals(value))
					count++;
			}
		} else if (!isMultiple()) {
			if (valor == null && value == null)
				count++;
			else if (valor != null && valor.equals(value))
				count++;
		}
		return count;
	}
	public int countNotEquals(Object value) {
		int count = 0;
		if (isMultiple() && valorMultiple != null) {
			for (Object obj: valorMultiple) {
				if ((obj == null && value != null) || (obj != null && value == null))
					count++;
				else if (obj != null && !obj.equals(value))
					count++;
			}
		} else if (!isMultiple()) {
			if ((valor == null && value != null) || (valor != null && value == null))
				count++;
			else if (valor != null && !valor.equals(value))
				count++;
			if (!valor.equals(value))
				count++;
		}
		return count;
	}

	public int compareTo(FieldValue other) {
		int retval = 0;
		if (other == null) {
			retval = 1;
		} else {
			String thisValorOrdre = getValorOrdre();
			if (isMultiple()) {
				if (getValorOrdreMultiple() != null && getValorOrdreMultiple().size() > 0)
					thisValorOrdre = getValorOrdreMultiple().get(0);
			}
			String otherValorOrdre = other.getValorOrdre();
			if (other.isMultiple()) {
				if (other.getValorOrdreMultiple() != null && other.getValorOrdreMultiple().size() > 0)
					otherValorOrdre = other.getValorOrdreMultiple().get(0);
			}
			if (thisValorOrdre == null) {
				if (otherValorOrdre == null)
					retval = 0;
				else
					retval = -1;
			} else if (otherValorOrdre == null) {
				retval = 1;
			} else {
				retval = thisValorOrdre.compareTo(otherValorOrdre);
			}
		}
		return retval;
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
