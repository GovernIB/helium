package es.caib.helium.commons.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistreDetallDto {
    private String registreOficinaNom;
    private Date registreData;
    private boolean registreEntrada;
    private String registreNumero;
}
