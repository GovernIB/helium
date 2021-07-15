package es.caib.helium.client.engine.areaCarrec;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AreaCarrecClient {

    List<String> findAreesByFiltre(String filtre);

    List<String> findAreesByPersona(String personaCodi);

    List<String> findRolsByPersona(String personaCodi);

    List<String> findCarrecsByFiltre(String filtre);

    List<String> findPersonesByGrupAndCarrec(String grupCodi, String carrecCodi);

    List<String> findCarrecsByPersonaAndGrup(String personaCodi, String grupCodi);

    List<String> findPersonesByCarrec(String carrecCodi);

    List<String> findPersonesByGrup(String grupCodi);
}
