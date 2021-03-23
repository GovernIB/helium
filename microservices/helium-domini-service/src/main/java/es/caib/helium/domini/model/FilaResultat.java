package es.caib.helium.domini.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Validated
@Data
@EqualsAndHashCode
//@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filaResultat", propOrder = {
        "columnes"
})
public class FilaResultat implements Serializable {

    @XmlElement(nillable = true)
    @JsonProperty("columnes")
    @Valid
    private List<ParellaCodiValor> columnes = null;

    public List<ParellaCodiValor> getColumnes() {
        if (columnes == null) {
            columnes = new ArrayList<ParellaCodiValor>();
        }
        return this.columnes;
    }
}
