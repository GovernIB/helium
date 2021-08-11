/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  Dto pel llistat paginat de tasques.
 *  
 * @author Limit Tecnologies
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@SuppressWarnings("serial")
public class TascaLlistatDto extends ControlPermisosDto implements Comparable<TascaLlistatDto> {

	private String id;
	private String nom;
	private String titol;
	private String jbpmName;
	private String description;
	private String assignee;
	private Set<String> pooledActors;
	private Date createTime;
	private Date startTime;
	private Date endTime;
	private Date dueDate;
	private int priority;
	private boolean open;
	private boolean completed;
	private boolean cancelled;
	private boolean suspended;

	private boolean assignadaUsuariActual;
	private boolean ambRepro;
	private boolean mostrarAgrupacions;
	private boolean agafada;

	private String processInstanceId;
	private String expedientTipusNom;
	private Long definicioProcesId;

	private Long expedientId;
	private Long expedientTipusId;
	private String expedientIdentificador;
	private String expedientNumero;

	// Dades finalització den segon pla
	private Date marcadaFinalitzar;
	private Date iniciFinalitzacio;
	private String errorFinalitzacio;

	private PersonaDto responsable;
	private List<PersonaDto> responsables;


	@Override
	public int compareTo(TascaLlistatDto aThat) {
	    if (this == aThat) return 0;
    	return this.getCreateTime().compareTo(aThat.getCreateTime());
	}

}
