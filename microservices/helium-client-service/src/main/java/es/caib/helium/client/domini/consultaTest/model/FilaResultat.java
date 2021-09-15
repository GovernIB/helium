package es.caib.helium.client.domini.consultaTest.model;

import es.caib.helium.client.model.ParellaCodiValor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

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
