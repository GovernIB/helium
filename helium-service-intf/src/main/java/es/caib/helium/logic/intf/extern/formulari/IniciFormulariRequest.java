package es.caib.helium.logic.intf.extern.formulari;

import lombok.Data;

import java.util.List;

@Data
public class IniciFormulariRequest {
    private String codi;
    private String taskId;
    private List<ParellaCodiValor> valors;
}
