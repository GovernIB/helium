package es.caib.helium.dada.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@ToString
@Document
@JsonTypeName("valorDate")
public class ValorDate extends Valor {

    private Date valor;
    private String valorText;
}
