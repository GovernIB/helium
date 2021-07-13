package es.caib.helium.camunda.service;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaCarrecServiceImpl implements AreaCarrecService {

    private final ProcessEngine processEngine;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final RepositoryService repositoryService;


    // TODO: Implementar els mètodes de àrees i càrrecs

    @Override
    public List<String> findAreesByFiltre(String filtre) {
        return null;
    }

    @Override
    public List<String> findAreesByPersona(String personaCodi) {
        return null;
    }

    @Override
    public List<String> findRolsByPersona(String personaCodi) {
        return null;
    }

    @Override
    public List<String[]> findCarrecsByFiltre(String filtre) {
        return null;
    }

    @Override
    public List<String> findPersonesByGrupAndCarrec(String grupCodi, String carrecCodi) {
        return null;
    }

    @Override
    public List<String> findCarrecsByPersonaAndGrup(String personaCodi, String grupCodi) {
        return null;
    }

    @Override
    public List<String> findPersonesByCarrec(String carrecCodi) {
        return null;
    }

    @Override
    public List<String> findPersonesByGrup(String grupCodi) {
        return null;
    }
}
