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
	public void eliminaOrdre(String camp) {
		if (this.ordres!=null) {
			for (int o=this.ordres.size()-1; o>=0; o--) {
				if (this.ordres.get(o).getCamp().equals(camp)) {
					this.ordres.remove(o);
				}
			}
		}
	}
	public void eliminaFiltre(String camp) {
		if (this.filtres!=null) {
			for (int o=this.filtres.size()-1; o>=0; o--) {
				if (this.filtres.get(o).getCamp().equals(camp)) {
					this.filtres.remove(o);
				}
			}
		}
	}
	public void canviaCamp(String campOld, String campNew) {
		if (this.ordres!=null) {
			for (int o=this.ordres.size()-1; o>=0; o--) {
				if (this.ordres.get(o).getCamp().equals(campOld)) {
					this.ordres.get(o).setCamp(campNew);
				}
			}
		}
		if (this.filtres!=null) {
			for (int o=this.filtres.size()-1; o>=0; o--) {
				if (this.filtres.get(o).getCamp().equals(campOld)) {
					this.filtres.get(o).setCamp(campNew);
				}
			}
		}
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
