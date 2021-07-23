package es.caib.helium.camunda.model.modeler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtomLink {

    private String rel;
    private String href;
    private String method;

}
