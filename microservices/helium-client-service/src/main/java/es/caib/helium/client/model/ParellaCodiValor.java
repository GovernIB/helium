package es.caib.helium.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParellaCodiValor implements Serializable {

  private String codi = null;
  private Object valor = null;

  public ParellaCodiValor(
          String codiValor) {
    this.codi = codiValor;
    this.valor = codiValor;
  }

}
