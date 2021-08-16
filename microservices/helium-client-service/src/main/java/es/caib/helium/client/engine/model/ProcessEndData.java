package es.caib.helium.client.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessEndData {

    String[] processInstanceIds;
    private Date dataFinalitzacio;

}
