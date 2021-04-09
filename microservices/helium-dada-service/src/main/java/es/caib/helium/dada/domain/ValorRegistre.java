package es.caib.helium.dada.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document
public class ValorRegistre extends Valor {

	private List<Dada> camps;
}
