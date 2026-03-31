package es.caib.helium.integracio.plugins.procediment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Rolsac2FiltreOrden {
	String campo = "codigo";
	Rolsac2TipusOrdre tipoOrden = Rolsac2TipusOrdre.DESC;
	
	public enum Rolsac2TipusOrdre {ASC, DESC}
}
