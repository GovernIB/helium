package es.caib.helium.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParellaCodiValor {

  private String codi = null;
  private Object valor = null;

  public ParellaCodiValor(
          String codiValor) {
    this.codi = codiValor;
    this.valor = codiValor;
  }

}
