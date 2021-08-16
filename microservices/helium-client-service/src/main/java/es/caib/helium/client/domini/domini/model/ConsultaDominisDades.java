package es.caib.helium.client.domini.domini.model;

import es.caib.helium.client.model.PagedSortedRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDominisDades extends PagedSortedRequest {

    @NotNull
    private Long entornId;
    private Long expedientTipusId;
    private Long expedientTipusPareId;
    private String filtre;

}