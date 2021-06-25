package es.caib.helium.camunda.service;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class CacheHelper {

    private final RepositoryService repositoryService;


    // ProcessDefinition
    // -----------------------------------------------------------------------------------------------------------------

    @Cacheable(value = "processDefinitionCache", key = "#processDefinitionId")
    public ProcessDefinition getDefinicioProces(String processDefinitionId) {
        ProcessDefinition processDefinition = null;
        try {
            processDefinition = repositoryService.getProcessDefinition(processDefinitionId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error obtenint la definició de procés amb id: " + processDefinitionId, ex);
        }
        if (processDefinition == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Definició de procés: " + processDefinitionId);
        return processDefinition;
    }


}
