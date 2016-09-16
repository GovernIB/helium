/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;

/**
 * DTO amb informació per exportar/importar una definició de procés
 * cap a /des de un altre Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DefinicioProcesExportacio implements Serializable {

	private DefinicioProcesDto definicioProcesDto;
	private List<TascaExportacio> tasques = new ArrayList<TascaExportacio>();
	private String nomDeploy;
	private byte[] contingutDeploy;

	public DefinicioProcesDto getDefinicioProcesDto() {
		return definicioProcesDto;
	}
	public void setDefinicioProcesDto(DefinicioProcesDto definicioProcesDto) {
		this.definicioProcesDto = definicioProcesDto;
	}
	public List<TascaExportacio> getTasques() {
		return tasques;
	}
	public void setTasques(List<TascaExportacio> tasques) {
		this.tasques = tasques;
	}
	public String getNomDeploy() {
		return nomDeploy;
	}
	public void setNomDeploy(String nomDeploy) {
		this.nomDeploy = nomDeploy;
	}
	public byte[] getContingutDeploy() {
		return contingutDeploy;
	}
	public void setContingutDeploy(byte[] contingutDeploy) {
		this.contingutDeploy = contingutDeploy;
	}
	private static final long serialVersionUID = 1L;

}
