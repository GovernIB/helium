package es.caib.helium.client.domini.entorn.model;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsultaDominiDada {
    private Long dominiId;
    private Map<String, String> parametres;
}