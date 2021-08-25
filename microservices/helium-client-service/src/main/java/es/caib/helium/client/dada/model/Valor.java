package es.caib.helium.client.dada.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

/**
 * Classe utilitzada per serialitzar els elements l'atribut valor de {@link es.caib.helium.client.dada.model.Dada}
 */
@JsonTypeInfo (use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "tipusValor")
@JsonSubTypes ({@Type (value = ValorRegistre.class, name = "valorRegistre"), 
	@Type (value = ValorSimple.class, name = "valorSimple")})
public abstract class Valor {

}