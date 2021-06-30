/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import java.io.Serializable;



/**
 * Un paràmetre per a cridar al web service d'un domini
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ParellaCodiValorDto implements Serializable {

	private String codi;
	private Object valor;


	public ParellaCodiValorDto() {
	}
	public ParellaCodiValorDto(
			String codiValor) {
		this.codi = codiValor;
		this.valor = codiValor;
	}
	public ParellaCodiValorDto(
			String codi,
			Object valor) {
		this.codi = codi;
		this.valor = valor;
	}



	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public Object getValor() {
		return valor;
	}
	public void setValor(Object valor) {
		this.valor = valor;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParellaCodiValorDto other = (ParellaCodiValorDto) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
