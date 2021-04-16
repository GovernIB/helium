package es.caib.helium.dada.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document
@JsonTypeName("valorRegistre")
public class ValorRegistre extends Valor {

	private List<Dada> camps;
}
