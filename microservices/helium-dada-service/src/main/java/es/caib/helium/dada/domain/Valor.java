package es.caib.helium.dada.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo (use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "tipusValor")
@JsonSubTypes ({@Type (value = ValorRegistre.class, name = "valorRegistre"), 
	@Type (value = ValorSimple.class, name = "valorSimple")})
@Document
public abstract class Valor {

}
