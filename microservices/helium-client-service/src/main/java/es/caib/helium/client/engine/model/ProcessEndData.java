package es.caib.helium.client.engine.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class ProcessEndData {

    String[] processInstanceIds;
    private Date dataFinalitzacio;

}
