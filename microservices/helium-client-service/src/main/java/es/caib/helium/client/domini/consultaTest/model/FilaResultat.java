package es.caib.helium.client.domini.consultaTest.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class FilaResultat  {

    private List<ParellaCodiValor> columnes = null;

    public List<ParellaCodiValor> getColumnes() {
        if (columnes == null) {
            columnes = new ArrayList<ParellaCodiValor>();
        }
        return this.columnes;
    }
}
