package es.caib.helium.client.expedient.tasca.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * Informació de la tasca amb les propietats necessàries pel llistat filtrat i paginat de
 * tasques.
 * 
 */
@Data
public class TascaDto {

	private String id;
	private Long expedientId;
	private String procesId;
	private String nom;
	private String titol;
	private boolean afagada = false;
	private boolean cancelada = false;
	private boolean suspesa = false;
	private boolean completada = false;
	private boolean assignada = false;
	private boolean marcadaFinalitzar = false;
	private boolean errorFinalitzacio = false;
	private Date dataFins;
	private Date dataFi;
	private Date iniciFinalitzacio;
	private Date dataCreacio;
	private String usuariAssignat;
	private String grupAssignat;
	private Integer prioritat;
	private List<ResponsableDto> responsables;
	
}
