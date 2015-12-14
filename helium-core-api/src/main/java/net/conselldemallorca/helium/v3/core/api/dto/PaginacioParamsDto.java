/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Dto amb els paràmetres per a paginar i ordenar els
 * resultats d'una consulta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PaginacioParamsDto implements Serializable {

	private int paginaNum;
	private int paginaTamany;
	private List<OrdreDto> ordres = new ArrayList<OrdreDto>();

	public int getPaginaNum() {
		return paginaNum;
	}
	public void setPaginaNum(int paginaNum) {
		this.paginaNum = paginaNum;
	}
	public int getPaginaTamany() {
		return paginaTamany;
	}
	public void setPaginaTamany(int paginaTamany) {
		this.paginaTamany = paginaTamany;
	}
	public List<OrdreDto> getOrdres() {
		return ordres;
	}
	public void setOrdres(List<OrdreDto> ordres) {
		this.ordres = ordres;
	}

	public void afegirOrdre(
			String camp,
			OrdreDireccioDto direccio) {
		getOrdres().add(
				new OrdreDto(
						camp,
						direccio));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public enum OrdreDireccioDto {
		ASCENDENT,
		DESCENDENT
	}
	public class OrdreDto implements Serializable {
		private String camp;
		private OrdreDireccioDto direccio;
		public OrdreDto(
				String camp,
				OrdreDireccioDto direccio) {
			this.camp = camp;
			this.direccio = direccio;
		}
		public String getCamp() {
			return camp;
		}
		public void setCamp(String camp) {
			this.camp = camp;
		}
		public OrdreDireccioDto getDireccio() {
			return direccio;
		}
		public void setDireccio(OrdreDireccioDto direccio) {
			this.direccio = direccio;
		}
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
		private static final long serialVersionUID = -139254994389509932L;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
