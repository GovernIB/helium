package es.caib.helium.dada.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Document
@JsonTypeName("valorPreu")
public class ValorPreu extends Valor {

    private BigDecimal valor;
    private String valorText;
}
