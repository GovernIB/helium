/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Dto amb els paràmetres per a paginar i ordenar els
 * resultats d'una consulta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PaginacioParamsDto implements Serializable {

	private int paginaNum = 0;
	private int paginaTamany = -1;
	private String filtre;
	private List<FiltreDto> filtres = new ArrayList<FiltreDto>();
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
	public String getFiltre() {
		return filtre;
	}
	public void setFiltre(String filtre) {
		this.filtre = filtre;
	}
	public List<FiltreDto> getFiltres() {
		return filtres;
	}
	public void setFiltres(List<FiltreDto> filtres) {
		this.filtres = filtres;
	}
	public List<OrdreDto> getOrdres() {
		return ordres;
	}
	public void setOrdres(List<OrdreDto> ordres) {
		this.ordres = ordres;
	}

	public void afegirFiltre(
			String camp,
			String valor) {
		getFiltres().add(
				new FiltreDto(
						camp,
						valor));
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

	public class FiltreDto implements Serializable {
		private String camp;
		private String valor;
		public FiltreDto(
				String camp,
				String valor) {
			this.camp = camp;
			this.valor = valor;
		}
		public String getCamp() {
			return camp;
		}
		public void setCamp(String camp) {
			this.camp = camp;
		}
		public String getValor() {
			return valor;
		}
		public void setValor(String valor) {
			this.valor = valor;
		}
		private static final long serialVersionUID = -139254994389509932L;
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
