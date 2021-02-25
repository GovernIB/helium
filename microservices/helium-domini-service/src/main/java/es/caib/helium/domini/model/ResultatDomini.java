package es.caib.helium.domini.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;

@Validated
@EqualsAndHashCode
@ToString
public class ResultatDomini extends ArrayList<ParellaCodiValor> {
}
