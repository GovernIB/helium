package es.caib.helium.client.domini.domini.model;

import es.caib.helium.client.model.PagedSortedRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDominisDades extends PagedSortedRequest {

    @NotNull
    private Long entornId;
    private Long expedientTipusId;
    private Long expedientTipusPareId;
    private String filtre;

}