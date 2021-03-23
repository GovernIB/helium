package es.caib.helium.domini.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * ParellaCodiValor
 */
@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "parellaCodiValor", propOrder = {
        "codi",
        "valor"
})
public class ParellaCodiValor implements Serializable {

  @JsonProperty("codi")
  private String codi = null;

  @JsonProperty("valor")
  private Object valor = null;

}
