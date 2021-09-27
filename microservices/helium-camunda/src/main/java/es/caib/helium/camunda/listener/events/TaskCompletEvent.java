package es.caib.helium.camunda.listener.events;

import com.fasterxml.jackson.databind.JsonNode;
import es.caib.helium.client.dada.dades.model.Dada;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCompletEvent {

    private String taskId;
    private String procesId;
    private List<Dada> dades;
    private JsonNode taskPatch;

}
