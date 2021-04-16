package es.caib.helium.dada.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo (use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "tipus_filtre")
@JsonSubTypes ({@Type (value = FiltreCapcalera.class, name = "filtreCapcalera"), 
	@Type (value = FiltreValor.class, name = "filtreValor")})
public abstract class Filtre {

}
