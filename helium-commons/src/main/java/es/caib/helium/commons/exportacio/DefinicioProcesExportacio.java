/**
 *
 */
package es.caib.helium.commons.exportacio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import es.caib.helium.commons.dto.DefinicioProcesDto;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO amb informació per exportar/importar una definició de procés
 * cap a /des de un altre Helium.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class DefinicioProcesExportacio implements Serializable {

	private DefinicioProcesDto definicioProcesDto;
	private List<TascaExportacio> tasques = new ArrayList<TascaExportacio>();
	private List<CampExportacio> camps = new ArrayList<CampExportacio>();
	private List<DocumentExportacio> documents = new ArrayList<DocumentExportacio>();
	private List<TerminiExportacio> terminis = new ArrayList<TerminiExportacio>();
	private List<AgrupacioExportacio> agrupacions = new ArrayList<AgrupacioExportacio>();
	private List<AccioExportacio> accions = new ArrayList<AccioExportacio>();
	private String nomDeploy;

	private byte[] contingutDeploy;

	private String etiqueta;
	private boolean hasStartTask;
	private boolean actualitzarExpedientsActius;

	private static final long serialVersionUID = 1L;

}
