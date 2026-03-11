package es.caib.helium.commons.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignaturaDetallDto {
    private String nomResponsable;
    private String nifResponsable;
    private boolean estatOk;
}
