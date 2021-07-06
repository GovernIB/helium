package es.caib.helium.client.domini.domini.model;

import java.util.ArrayList;

import org.springframework.validation.annotation.Validated;

import es.caib.helium.client.domini.consultaTest.model.FilaResultat;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Validated
@EqualsAndHashCode
@ToString
public class ResultatDomini extends ArrayList<FilaResultat> {
}
