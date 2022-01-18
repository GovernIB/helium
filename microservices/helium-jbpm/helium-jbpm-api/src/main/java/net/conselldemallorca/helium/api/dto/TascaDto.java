package net.conselldemallorca.helium.api.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Informació de la tasca amb les propietats necessàries pel llistat filtrat i paginat de
 * tasques.
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TascaDto {

	private Long id;
	private String tascaId;
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
	private Integer prioritat;
	private List<String> responsables;
	private List<String> grups;
		
	private String processDefinitionId;
	private Long expedientId;
}