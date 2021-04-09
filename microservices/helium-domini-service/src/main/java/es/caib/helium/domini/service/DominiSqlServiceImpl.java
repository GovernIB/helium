package es.caib.helium.domini.service;

import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.model.FilaResultat;
import es.caib.helium.domini.model.ParellaCodiValor;
import es.caib.helium.domini.model.ResultatDomini;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.naming.NamingException;
import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class DominiSqlServiceImpl implements DominiSqlService {

    private final Environment environment;

    @Setter
    private Map<Long, NamedParameterJdbcTemplate> jdbcTemplates = new HashMap<>();

    @Override
    public ResultatDomini consultaDomini(
            Domini domini,
            Map<String, String> parametres) throws NamingException {

        NamedParameterJdbcTemplate jdbcTemplate = getJdbcTemplateFromDomini(domini);
        MapSqlParameterSource parameterSource = new MapSqlParameterSource(getParametersMap(parametres)) {
            public boolean hasValue(String paramName) {
                return true;
            }
        };
        
        ResultatDomini resultat = new ResultatDomini();
        List<FilaResultat> files = jdbcTemplate.query(
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
        if (files != null)
            resultat.addAll(files);
        return resultat;
    }

    private Map<String,Object> getParametersMap(Map<String, String> parametres) {
        Map<String, Object> resposta = new HashMap<>();
        if (parametres != null) {
            for(Map.Entry<String, String> parametre: parametres.entrySet()) {
                if (!parametre.getKey().endsWith("-type")) {

                    String clau = parametre.getKey();
                    String valor = parametre.getValue();
                    String tipus = parametres.get(parametre.getKey() + "-type");

                    resposta.put(clau, convertirValor(valor, tipus));
                }
            }
        }
        return resposta;
    }

    private Object convertirValor(String valor, String tipus) {
        try {
            if (tipus != null) {
                switch (tipus) {
                    case "int":
                        return Long.parseLong(valor);
                    case "float":
                        return Double.parseDouble(valor);
                    case "boolean":
                        return Boolean.valueOf(valor);
                    case "date":
                        String[] dataSplit = valor.split("/");
                        Calendar data = new GregorianCalendar();
                        data.set(Integer.parseInt(dataSplit[2]),Integer.parseInt(dataSplit[1]) - 1,Integer.parseInt(dataSplit[0]));
                        return data.getTime();
                    case "price":
                        return new BigDecimal(Double.parseDouble(valor));
                    default:
                        return valor;
                }
            } else {
                return valor;
            }
        } catch (Exception ex) {
            log.error("Error al convertir [valor: " + valor + ", tipus: " + tipus + "]");
            return valor;
        }
    }

    private NamedParameterJdbcTemplate getJdbcTemplateFromDomini(Domini domini) throws NamingException {
        NamedParameterJdbcTemplate jdbcTemplate = jdbcTemplates.get(domini.getId());
        if (jdbcTemplate == null) {

            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(getDriverClassName(domini.getUrl()));
            dataSource.setUrl(domini.getUrl());
            dataSource.setUsername(domini.getUsuari());
            dataSource.setPassword(domini.getContrasenya());

            JdbcTemplate template = new JdbcTemplate(dataSource);
            template.setQueryTimeout(getDominiTimeout(domini));
            jdbcTemplate = new NamedParameterJdbcTemplate(template);
            jdbcTemplates.put(domini.getId(), jdbcTemplate);
        }
        return jdbcTemplate;
    }

    private String getDriverClassName(String url) {
        if (url.startsWith("jdbc:oracle")) {
            return "oracle.jdbc.OracleDriver";
        } else if (url.startsWith("jdbc:postgresql")) {
            return "org.postgresql.Driver";
        } else if (url.startsWith("jdbc:mysql")) {
            return "com.mysql.jdbc.Driver";
        } else if (url.startsWith("jdbc:sqlserver")) {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        } else if (url.startsWith("jdbc:mariadb")) {
            return "org.mariadb.jdbc.Driver";
        }
        return "oracle.jdbc.OracleDriver";
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
