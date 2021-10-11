package es.caib.helium.client.dada.dades.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * Classe utilitzada per gestionar les consultes complexes a la BDD
 */
@Getter
@Setter
@ToString
public class Consulta {
	
	@JsonIgnore
	private Integer entornId;
	@JsonIgnore
	private Integer expedientTipusId;
	@JsonIgnore
	private Integer page;
	@JsonIgnore
	private Integer size;
	
	private Map<String,Filtre> filtreValors;
	private List<Columna> columnes;


}
