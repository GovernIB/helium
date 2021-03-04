package es.caib.helium.domini.service;

import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.model.FilaResultat;
import es.caib.helium.domini.model.ParellaCodiValor;
import es.caib.helium.domini.model.ResultatDomini;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class DominiSqlServiceImpl implements DominiSqlService {

    private final Environment environment;

    private final Map<Long, NamedParameterJdbcTemplate> jdbcTemplates = new HashMap<>();

    @Override
    public ResultatDomini consultaDomini(
            Domini domini,
            Map<String, Object> parametres) throws NamingException {

        NamedParameterJdbcTemplate jdbcTemplate = getJdbcTemplateFromDomini(domini);
        MapSqlParameterSource parameterSource = new MapSqlParameterSource(parametres) {
            public boolean hasValue(String paramName) {
                return true;
            }
        };
        return (ResultatDomini) jdbcTemplate.query(
                domini.getSql(),
                parameterSource,
                (rs, rowNum) -> {
                    FilaResultat fr = new FilaResultat();
                    ResultSetMetaData rsm = rs.getMetaData();
                    for (int i = 1; i <= rsm.getColumnCount(); i++) {
                        fr.getColumnes().add(ParellaCodiValor.builder().codi(rsm.getColumnName(i)).valor(rs.getObject(i)).build());
                    }
                    return fr;
                });
    }

    private NamedParameterJdbcTemplate getJdbcTemplateFromDomini(Domini domini) throws NamingException {
        NamedParameterJdbcTemplate jdbcTemplate = jdbcTemplates.get(domini.getId());
        if (jdbcTemplate == null) {
            Context initContext = new InitialContext();
            String dataSourceJndi = domini.getJndiDatasource();
            if (isDesplegamentTomcat() && dataSourceJndi.endsWith("java:/es.caib.helium.db"))
                dataSourceJndi = "java:/comp/env/jdbc/HeliumDS";

            // TODO: Obtenir el jndi de Helium
            DataSource ds = (DataSource)initContext.lookup(dataSourceJndi);
            JdbcTemplate template = new JdbcTemplate(ds);
            template.setQueryTimeout(getDominiTimeout(domini));
            jdbcTemplate = new NamedParameterJdbcTemplate(template);
            jdbcTemplates.put(domini.getId(), jdbcTemplate);
        }
        return jdbcTemplate;
    }

    private boolean isDesplegamentTomcat() {
        String desplegamentTomcat = environment.getProperty("es.caib.helium.desplegament.tomcat", "false");
        return "true".equalsIgnoreCase(desplegamentTomcat);
    }

    private Integer getDominiTimeout (Domini domini) {
        int timeout = 10000; //valor per defecte
        if (domini.getTimeout() != null && domini.getTimeout() > 0)
            timeout = domini.getTimeout() * 1000; //valor específic de timeout del domini
        else if (environment.getProperty("app.domini.timeout") != null)
            timeout = Integer.parseInt(environment.getProperty("app.domini.timeout")); //valor global de timeout pels dominis
        return timeout;
    }
}
