package es.caib.helium.integracio.service.persones;

import es.caib.helium.integracio.domini.persones.Persona;
import es.caib.helium.integracio.enums.persones.Sexe;
import es.caib.helium.integracio.excepcions.persones.PersonaException;
import es.caib.helium.integracio.service.monitor.MonitorIntegracionsService;
import es.caib.helium.jms.domini.Parametre;
import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.enums.TipusAccio;
import es.caib.helium.jms.events.IntegracioEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.keycloak.KeyCloakUserInformationPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PersonaServiceGoibImpl implements PersonaService {

    @Autowired
    private KeyCloakUserInformationPlugin goibPersones;
    @Autowired
    private MonitorIntegracionsService monitor;

    @Override
    public List<Persona> getPersones(String textSearch, Long entornId) throws PersonaException {
        // TODO MS: PENDENDT DE IMPLEMENTACIO
        try {
            var users = goibPersones.getAllUsernames();
            var persones = new ArrayList<Persona>();
            for (var user : users) {
                persones.add(buildPersona(goibPersones.getUserInfoByUserName(user)));
            }
            return persones;

        } catch (Exception ex) {
            var error = "No s'ha pogut trobar cap usuari amb el filtre " + textSearch + " entornId " + entornId ;
            log.error(error);
            throw new PersonaException(error, ex);
        }
    }

    @Override
    public Persona getPersonaByCodi(String codi, Long entornId) throws PersonaException {

        List<Parametre> parametres = new ArrayList<>();
        parametres.add(new Parametre("codi", codi));
        var descripcio = "Consulta d'usuari amb codi " + codi;
        var t0 = System.currentTimeMillis();
        try {
            var userInfo = goibPersones.getUserInfoByUserName(codi);
           var persona = buildPersona(userInfo);

            monitor.enviarEvent(IntegracioEvent.builder()
                    .codi(CodiIntegracio.PERSONA)
                    .entornId(entornId)
                    .descripcio(descripcio)
                    .data(new Date())
                    .tipus(TipusAccio.ENVIAMENT)
                    .estat(EstatAccio.OK)
                    .parametres(parametres)
                    .tempsResposta(System.currentTimeMillis() - t0).build());

            log.debug("Consulta ok d'usuaris amb codi" + codi);
            return persona;
        } catch (Exception ex) {
            var error = "No s'ha pogut trobar cap usuari amb codi " + codi + " entornId " + entornId ;
            log.error(error);
            monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PERSONA)
                    .entornId(entornId)
                    .descripcio(descripcio)
                    .tipus(TipusAccio.ENVIAMENT)
                    .estat(EstatAccio.ERROR)
                    .parametres(parametres)
                    .tempsResposta(System.currentTimeMillis() - t0)
                    .errorDescripcio(error)
                    .excepcioMessage(ex.getMessage())
                    .excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
            throw new PersonaException(error, ex);
        }
    }

    @Override
    public List<String> getPersonaRolsByCodi(String codi, Long entornId) throws PersonaException {

        List<Parametre> params = new ArrayList<>();
        params.add(new Parametre("codi", codi));
        var t0 = System.currentTimeMillis();
        var descripcio = "Consulta de rols de l'usuari amb codi " + codi;
        try {
            var rols = Arrays.asList(goibPersones.getRolesByUsername(codi).getRoles());
            monitor.enviarEvent(IntegracioEvent.builder()
                    .codi(CodiIntegracio.PERSONA)
                    .entornId(entornId)
                    .descripcio(descripcio)
                    .data(new Date())
                    .tipus(TipusAccio.ENVIAMENT)
                    .estat(EstatAccio.OK)
                    .parametres(params)
                    .tempsResposta(System.currentTimeMillis() - t0).build());
            log.debug("Consulta dels rols de les persones ok pel codi " + codi);
            return rols;
        } catch (Exception ex) {
            var error = "Consulta erronia dels rols de les persones amb codi " + codi;
            log.error(error, ex);
            monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PERSONA)
                    .entornId(entornId)
                    .descripcio(descripcio)
                    .tipus(TipusAccio.ENVIAMENT)
                    .estat(EstatAccio.ERROR)
                    .parametres(params)
                    .tempsResposta(System.currentTimeMillis() - t0)
                    .errorDescripcio(error)
                    .excepcioMessage(ex.getMessage())
                    .excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
            throw new PersonaException(error, ex);
        }
    }

    @Override
    public List<String> getPersonesCodiByRol(String rol, Long entornId) throws PersonaException {

        List<Parametre> params = new ArrayList<>();
        params.add(new Parametre("grup", rol));
        var t0 = System.currentTimeMillis();
        var descripcio = "Consulta de rols de l'usuari amb codi " + rol;
        try {
            var persones = new ArrayList<String>();
            var usuaris = goibPersones.getUsernamesByRol(rol);
            // cridar a helium i que 
            for (var usuari : usuaris) {

                var userInfo = goibPersones.getUserInfoByUserName(usuari);
                persones.add(userInfo.getFullName());
            }
            monitor.enviarEvent(IntegracioEvent.builder()
                    .codi(CodiIntegracio.PERSONA)
                    .entornId(entornId)
                    .descripcio(descripcio)
                    .data(new Date())
                    .tipus(TipusAccio.ENVIAMENT)
                    .estat(EstatAccio.OK)
                    .parametres(params)
                    .tempsResposta(System.currentTimeMillis() - t0).build());
            log.debug("Consulta de persones amb grup " + rol);
            return Arrays.asList(usuaris); //TODO MS:
        } catch (Exception ex) {
            var error = "Consulta erronia de les persones amb grup " + rol;
            log.error(error, ex);
            monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PERSONA)
                    .entornId(entornId)
                    .descripcio(descripcio)
                    .tipus(TipusAccio.ENVIAMENT)
                    .estat(EstatAccio.ERROR)
                    .parametres(params)
                    .tempsResposta(System.currentTimeMillis() - t0)
                    .errorDescripcio(error)
                    .excepcioMessage(ex.getMessage())
                    .excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
            throw new PersonaException(error, ex);
        }
    }

    @Override
    public List<Persona> getPersonesByCodi(List<String> codis, Long entornId) throws PersonaException {

        List<Parametre> params = new ArrayList<>();
        params.add(new Parametre("codis", codis.toString()));
        var t0 = System.currentTimeMillis();
        var descripcio = "Consulta de persones per codis " + codis;
        try {
            var persones = new ArrayList<Persona>();
            for (var codi : codis) {
                persones.add(buildPersona(goibPersones.getUserInfoByUserName(codi)));
            }
            return persones;
        } catch (Exception ex) {
            var error = "Consulta erronia de la info de les persones amb codis " + codis;
            log.error(error, ex);
            monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PERSONA)
                    .entornId(entornId)
                    .descripcio(descripcio)
                    .tipus(TipusAccio.ENVIAMENT)
                    .estat(EstatAccio.ERROR)
                    .parametres(params)
                    .tempsResposta(System.currentTimeMillis() - t0)
                    .errorDescripcio(error)
                    .excepcioMessage(ex.getMessage())
                    .excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
            throw new PersonaException(error, ex);
        }
    }

    private Persona buildPersona(UserInfo userInfo) {
        // TODO MS: EL DNI NO VE DINS UserInfo
        // TODO MS: EL PLUGIN CONTEMPLA SEXE UKNOWN I HELIUM NO. QUE ASSIGNAR A UKNOWN?
        return Persona.builder()
                .codi(userInfo.getUsername()).nom(userInfo.getName())
                .llinatge1(userInfo.getSurname1()).llinatge2(userInfo.getSurname2())
                .email(userInfo.getEmail())
                .sexe(userInfo.getGender() != null ? userInfo.getGender().equals(UserInfo.Gender.MALE) ? Sexe.SEXE_HOME : Sexe.SEXE_DONA : null)
                .contrasenya(userInfo.getPassword()).build();
    }
}
