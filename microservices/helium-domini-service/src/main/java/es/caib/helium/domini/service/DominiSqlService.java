package es.caib.helium.domini.service;

import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.model.ResultatDomini;

import javax.naming.NamingException;
import java.util.Map;

public interface DominiSqlService {

    ResultatDomini consultaDomini(
            Domini domini,
            Map<String, String> parametres) throws NamingException;

}
