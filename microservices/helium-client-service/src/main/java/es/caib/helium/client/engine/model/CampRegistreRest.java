package es.caib.helium.client.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampRegistreRest implements Serializable {

    private static final long serialVersionUID = -5661097787644711457L;

    private Long id;
    private boolean obligatori = true;
    private CampRest membre;

}
