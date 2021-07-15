package es.caib.helium.client.engine.areaCarrec;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AreaCarrecClientImpl implements AreaCarrecClient {

    private final String MISSATGE_LOG = "Cridant Engine Service - Area carrec - ";

    private final AreaCarrecFeignClient areaCarreclient;

    @Override
    public List<String> findAreesByFiltre(String filtre) {

        log.debug(MISSATGE_LOG + " obtinguent arees amb filtre " + filtre);
        var responseEntity = areaCarreclient.findAreesByFiltre(filtre);
        var resultat = Objects.requireNonNull(responseEntity.getBody());
        return resultat;
    }

    @Override
    public List<String> findAreesByPersona(String personaCodi) {

        log.debug(MISSATGE_LOG + " obtinguent arees amb personaCodi " + personaCodi);
        var responseEntity = areaCarreclient.findAreesByPersona(personaCodi);
        var resultat = Objects.requireNonNull(responseEntity.getBody());
        return resultat;
    }

    @Override
    public List<String> findRolsByPersona(String personaCodi) {

        log.debug(MISSATGE_LOG + " obtinguent rols by personaCodi " + personaCodi);
        var responseEntity = areaCarreclient.findRolsByPersona(personaCodi);
        var resultat = Objects.requireNonNull(responseEntity.getBody());
        return resultat;
    }

    @Override
    public List<String> findCarrecsByFiltre(String filtre) {

        log.debug(MISSATGE_LOG + " obtinguent carrecs by filtre " + filtre);
        var responseEntity = areaCarreclient.findRolsByPersona(filtre);
        var resultat = Objects.requireNonNull(responseEntity.getBody());
        return resultat;
    }

    @Override
    public List<String> findPersonesByGrupAndCarrec(String grupCodi, String carrecCodi) {

        log.debug(MISSATGE_LOG + " obtinguent persones by grupCodi " + grupCodi + " i carrec " + carrecCodi);
        var responseEntity = areaCarreclient.findPersonesByGrupAndCarrec(grupCodi, carrecCodi);
        var resultat = Objects.requireNonNull(responseEntity.getBody());
        return resultat;
    }

    @Override
    public List<String> findCarrecsByPersonaAndGrup(String personaCodi, String grupCodi) {

        log.debug(MISSATGE_LOG + " obtinguent carrecs by personaCodi " + personaCodi + " i grupCodi " + grupCodi);
        var responseEntity = areaCarreclient.findPersonesByGrupAndCarrec(personaCodi, grupCodi);
        var resultat = Objects.requireNonNull(responseEntity.getBody());
        return resultat;
    }

    @Override
    public List<String> findPersonesByCarrec(String carrecCodi) {

        log.debug(MISSATGE_LOG + " obtinguent persones by carrecCodi " + carrecCodi);
        var responseEntity = areaCarreclient.findPersonesByCarrec(carrecCodi);
        var resultat = Objects.requireNonNull(responseEntity.getBody());
        return resultat;
    }

    @Override
    public List<String> findPersonesByGrup(String grupCodi) {

        log.debug(MISSATGE_LOG + " obtinguent persones by grupCodi " + grupCodi);
        var responseEntity = areaCarreclient.findPersonesByGrup(grupCodi);
        var resultat = Objects.requireNonNull(responseEntity.getBody());
        return resultat;
    }
}
