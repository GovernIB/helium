package es.caib.helium.domini.service;

import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.model.ResultatDomini;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
//@ConfigurationProperties(prefix = "es.caib.helium", ignoreUnknownFields = true)
@RequiredArgsConstructor
@Component
public class DominiInternServiceImpl implements DominiInternService {

//    public static final String DOMINI_INTERN_PATH = "/api/v1/dominiIntern/";
//    public static final String DOMINI_PERSONA_AMB_CODI = DOMINI_INTERN_PATH + "personaAmbCodi";
//    private final RestTemplate restTemplate;
    private final DominiWsService dominiWsService;

//    @Setter
//    private String dominiInternServiceHost;
//
//    public DominiInternServiceImpl(
//            RestTemplateBuilder restTemplateBuilder,
//            @Value("${es.caib.helium.domini.intern.user}") String user,
//            @Value("${es.caib.helium.domini.intern.password}") String password) {
//        this.restTemplate = restTemplateBuilder.basicAuthentication(user, password).build();
//    }

    public ResultatDomini consultaDomini(
            Domini domini,
            String identificador,
            Map<String, String> parametres) {

        // TODO: Afegir monitor d' integracions

        // TODO: Tenir en compte que s' està enviant l' ID enlloc del codi
        // domini.getEntorn().getCodi())
        if (parametres == null)
            parametres = new HashMap<>();
//        parametres.put("entorn", domini.getEntorn().toString());
        if (parametres.get("entorn") == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Consulta de Domini INTERN: és obligatori informar el codi de l'entorn en un parametre de nom 'entorn'");
        }

        ResultatDomini resposta = new ResultatDomini();

//        if ("PERSONA_AMB_CODI".equals(identificador)) {
//            resposta = personaAmbCodi(parametres);
//        } else if ("PERSONES_AMB_AREA".equals(identificador)) {
//            resposta = personesAmbArea(parametres);
//        } else if ("PERSONA_AMB_CARREC_AREA".equals(identificador)) {
//            resposta = personesAmbCarrecArea(parametres, true);
//        } else if ("PERSONES_AMB_CARREC_AREA".equals(identificador)) {
//            resposta = personesAmbCarrecArea(parametres, false);
//        } else  if ("AREES_AMB_PARE".equals(identificador)) {
//            resposta = areesAmbPare(parametres);
//        } else if ("VARIABLE_REGISTRE".equals(identificador)) {
//            resposta = variableRegistre(parametres);
//        } else if ("AREES_AMB_PERSONA".equals(identificador)) {
//            resposta = areesAmbPersona(parametres);
//        } else if ("ROLS_PER_USUARI".equals(identificador)) {
//            resposta = rolsPerUsuari(parametres);
//        } else if ("USUARIS_PER_ROL".equals(identificador)) {
//            resposta = usuarisPerRol(parametres);
//        } else if ("CARRECS_PER_PERSONA".equals(identificador)) {
//            resposta = carrecsPerPersona(parametres);
//        } else if ("PERSONES_AMB_CARREC".equals(identificador)) {
//            /* Per suprimir */
//            resposta = personesAmbCarrec(parametres);
//        } else if ("UNITAT_PER_CODI".equals(identificador)) {
//            resposta = unitatOrganica(parametres);
//        } else if ("UNITAT_PER_ARREL".equals(identificador)) {
//            resposta = unitatsOrganiques(parametres);
//        }

        resposta = dominiWsService.consultaDomini(domini, identificador, parametres);
        return resposta;
    }


//    // TODO: Implementar crides al servei de dominis interns de Helium --> Plugin Helper??.
//    // Implementar a Helium la API REST de dominis interns
//
//    private ResultatDomini personaAmbCodi(Map<String, String> parametres) {
//        log.debug("Cridant al servei de Dominis Interns de Helium - PersonaAmbCodi");
//        return restTemplate.getForObject(
//                dominiInternServiceHost + DOMINI_PERSONA_AMB_CODI,
//                ResultatDomini.class,
//                parametres);
//    }
//
//    private ResultatDomini personesAmbArea(Map<String, String> parametres) {
//        // TODO
//        return null;
//    }
//
//    private ResultatDomini personesAmbCarrecArea(Map<String, String> parametres, boolean nomesUna) {
//        // TODO
//         return null;
//    }
//
//    private ResultatDomini areesAmbPare(Map<String, String> parametres) {
//        // TODO
//         return null;
//    }
//
//    private ResultatDomini variableRegistre(Map<String, String> parametres) {
//        // TODO
//         return null;
//    }
//
//    private ResultatDomini areesAmbPersona(Map<String, String> parametres) {
//        // TODO
//         return null;
//    }
//
//    private ResultatDomini rolsPerUsuari(Map<String, String> parametres) {
//        // TODO
//        return null;
//    }
//
//    private ResultatDomini usuarisPerRol(Map<String, String> parametres) {
//        // TODO
//        return null;
//    }
//
//    private ResultatDomini carrecsPerPersona(Map<String, String> parametres) {
//        // TODO
//        return null;
//    }
//
//    private ResultatDomini personesAmbCarrec(Map<String, String> parametres) {
//        // TODO
//        return null;
//    }
//
//    private ResultatDomini unitatOrganica(Map<String, String> parametres) {
//        // TODO
//        return null;
//    }
//
//    private ResultatDomini unitatsOrganiques(Map<String, String> parametres) {
//        // TODO
//        return null;
//    }

}
