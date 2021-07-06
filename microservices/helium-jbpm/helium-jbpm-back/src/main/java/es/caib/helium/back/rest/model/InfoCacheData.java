package es.caib.helium.back.rest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfoCacheData {

    private String titol;
    private String info;

}
