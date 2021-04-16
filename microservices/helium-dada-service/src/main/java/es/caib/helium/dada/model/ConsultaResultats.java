package es.caib.helium.dada.model;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ConsultaResultats {

	private List<Ordre> ordre;
	private Map<String,Filtre> filtreValors;
}
