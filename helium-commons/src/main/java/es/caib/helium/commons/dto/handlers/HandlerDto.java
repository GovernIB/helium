package es.caib.helium.commons.dto.handlers;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HandlerDto {
    private String classe;
    private String nom;
    private String descripcio;
    private List<HandlerParametreDto> parametres;
    private HandlerAgrupacioEnum agrupacio;
}
