package es.caib.helium.camunda.service;

import java.util.List;

public interface AreaCarrecService {

    List<String> findAreesByFiltre(String filtre);

    List<String> findAreesByPersona(String personaCodi);

    List<String> findRolsByPersona(String personaCodi);

    List<String[]> findCarrecsByFiltre(String filtre);

    List<String> findPersonesByGrupAndCarrec(String grupCodi, String carrecCodi);

    List<String> findCarrecsByPersonaAndGrup(String personaCodi, String grupCodi);

    List<String> findPersonesByCarrec(String carrecCodi);

    List<String> findPersonesByGrup(String grupCodi);
}
