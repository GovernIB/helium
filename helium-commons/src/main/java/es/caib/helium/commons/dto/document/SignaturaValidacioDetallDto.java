package es.caib.helium.commons.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignaturaValidacioDetallDto {
    private boolean signat;
    private  String urlVerificacio;
    private String tokenSignatura;
    private List<SignaturaDetallDto> signatures;
}
