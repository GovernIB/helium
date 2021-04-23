package es.caib.helium.dada.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document
@JsonTypeName("valorSimple")
public class ValorSimple extends Valor {

	private String valor;
	private String valorText;
}
