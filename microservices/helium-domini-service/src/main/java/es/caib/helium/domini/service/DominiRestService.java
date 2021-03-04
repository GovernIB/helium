package es.caib.helium.domini.service;

import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.model.ResultatDomini;

import java.util.Map;

public interface DominiRestService {

    ResultatDomini consultaDomini(
            Domini domini,
            String identificador,
            Map<String, Object> parametres);

}
