package es.caib.helium.client.expedient.tasca.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Informació de la tasca amb les propietats necessàries pel llistat filtrat i paginat de
 * tasques.
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TascaDto {

	private String id;
	private Long expedientId;
	private String procesId;
	private String nom;
	private String titol;
	private boolean agafada = false;
	private boolean cancelada = false;
	private boolean suspesa = false;
	private boolean completada = false;
	private boolean assignada = false;
	private Date marcadaFinalitzar;
	private String errorFinalitzacio;
	private Date dataFins;
	private Date dataFi;
	private Date iniciFinalitzacio;
	private Date dataCreacio;
	private String usuariAssignat;
	private List<String> grups;
	private Integer prioritat;
	private List<ResponsableDto> responsables;
	
	
	private String processDefinitionId;	
}
