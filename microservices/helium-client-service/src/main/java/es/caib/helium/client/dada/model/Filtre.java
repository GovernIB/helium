package es.caib.helium.client.dada.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

/**
 * Classe utilitzada per serielitzar el tipus de filtre utilitzat en la {@link es.caib.helium.dada.model.Consulta}
 */
@JsonTypeInfo (use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "tipus_filtre")
@JsonSubTypes ({@Type (value = FiltreCapcalera.class, name = "filtreCapcalera"), 
@Type (value = FiltreValor.class, name = "filtreValor")})
public abstract class Filtre {

}
