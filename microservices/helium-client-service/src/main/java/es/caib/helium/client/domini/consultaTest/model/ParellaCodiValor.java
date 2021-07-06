package es.caib.helium.client.domini.consultaTest.model;

import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParellaCodiValor  {

  private String codi = null;
  private Object valor = null;
}
