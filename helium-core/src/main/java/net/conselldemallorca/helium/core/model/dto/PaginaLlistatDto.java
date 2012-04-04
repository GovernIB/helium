/**
 * 
 */
package net.conselldemallorca.helium.core.model.dto;

import java.util.List;

/**
 * Un paràmetre per a cridar al web service d'un domini
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("rawtypes")
public class PaginaLlistatDto {

	private int count;
	private List llistat;

	public PaginaLlistatDto(
			int count,
			List llistat) {
		this.count = count;
		this.llistat = llistat;
	}

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	public List getLlistat() {
		return llistat;
	}
	public void setLlistat(List llistat) {
		this.llistat = llistat;
	}

}
