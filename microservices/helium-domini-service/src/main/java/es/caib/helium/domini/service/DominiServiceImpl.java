package es.caib.helium.domini.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.mapper.DominiMapper;
import es.caib.helium.domini.model.DominiDto;
import es.caib.helium.domini.model.OrigenCredencialsEnum;
import es.caib.helium.domini.model.PagedList;
import es.caib.helium.domini.model.ResultatDomini;
import es.caib.helium.domini.model.ResultatDominiCache;
import es.caib.helium.domini.model.TipusAuthEnum;
import es.caib.helium.domini.model.TipusDominiEnum;
import es.caib.helium.domini.repository.DominiRepository;
import es.caib.helium.domini.repository.DominiSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "es.caib.helium") //, ignoreUnknownFields = true)
@Service
public class DominiServiceImpl implements DominiService {

    private static final String CACHE_DOMINI_ID = "dominiCache";
    private static final String CACHE_KEY_SEPARATOR = "#";

    private final DominiRepository dominiRepository;
    private final DominiMapper dominiMapper;
    private final HazelcastInstance hazelcastInstance;
    private final DominiInternService dominiInternService;
    private final DominiWsService dominiWsService;
    private final DominiSqlService dominiSqlService;
    private final DominiRestService dominiRestService;
    private final Environment environment;

//    @Value("${es.caib.helium.domini.intern.user}") String domini_intern_user;
//    @Value("${es.caib.helium.domini.intern.password}") String domini_intern_pass;
    @Setter
    @Value("${es.caib.helium.domini.intern.service.host}")
    private String domini_intern_url;

    @Override
    @Transactional
    public DominiDto createDomini(
            DominiDto dominiDto) {
//            throws PermisDenegatException {

        // TODO: Comprovar permisos sobre tipus d' expedient i entorn ??

        log.debug("[SRV] Creant nou domini (domini=" + dominiDto + ")");
        Domini domini = dominiMapper.dtoToEntity(dominiDto);
        log.debug("[SRV] Validant domini");
        validateDomini(domini);
        return dominiMapper.entityToDto(dominiRepository.save(domini));

    }

    @Override
    @Transactional
    public DominiDto updateDomini(
            Long dominiId,
            DominiDto dominiDto) {
//            throws NoTrobatException, PermisDenegatException {

        log.debug("[SRV] Modificant el domini existent (" +
                "dominiId=" + dominiId + ", " +
                "domini =" + dominiDto + ")");

        // TODO: Comprovar permisos sobre tipus d'expedient i entorn ??

        Domini domini = getDominiById(dominiId);

//        Optional<Domini> dominiOptional = dominiRepository.findById(dominiId);
//        dominiOptional.ifPresentOrElse(
//                domini -> {
//                }, () -> {
//                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + dominiId);
//                }
//        );

        domini.setCodi(dominiDto.getCodi());
        domini.setNom(dominiDto.getNom());
        domini.setDescripcio(dominiDto.getDescripcio());
        if (dominiDto.getTipus() != null)
            domini.setTipus(TipusDominiEnum.valueOf(dominiDto.getTipus().name()));
        domini.setUrl(dominiDto.getUrl());
        if (dominiDto.getTipusAuth() != null)
            domini.setTipusAuth(TipusAuthEnum.valueOf(dominiDto.getTipusAuth().name()));
        if (dominiDto.getOrigenCredencials() != null)
            domini.setOrigenCredencials(OrigenCredencialsEnum.valueOf(dominiDto.getOrigenCredencials().name()));
        domini.setUsuari(dominiDto.getUsuari());
        domini.setContrasenya(dominiDto.getContrasenya());
        domini.setSql(dominiDto.getSql());
        domini.setJndiDatasource(dominiDto.getJndiDatasource());
        domini.setCacheSegons(dominiDto.getCacheSegons());
        domini.setTimeout(dominiDto.getTimeout());
        domini.setOrdreParams(dominiDto.getOrdreParams());

        log.debug("[SRV] Validant domini");
        validateDomini(domini);

        return dominiMapper.entityToDto(dominiRepository.save(domini));

    }

    @Override
    @Transactional
    public void delete(
            Long dominiId) {

        log.debug("[SRV] Esborrant el domini (dominiId=" + dominiId +  ")");

        // TODO: Comprovar permisos sobre tipus d'expedient i entorn ??
        // TODO: Comprovar si el domini està sent utilitzat en algun camp
        getDominiById(dominiId);
        dominiRepository.delete(dominiId);
    }

    @Override
    @Transactional(readOnly = true)
    public DominiDto getById(
            Long dominiId) {

        log.debug("[SRV] Obtenint domini amb Id: " + dominiId);

        Domini domini = getDominiById(dominiId);
        return dominiMapper.entityToDto(domini);

    }

    @Override
    @Transactional(readOnly = true)
    public PagedList<DominiDto> listDominis(
            Long entornId,
            Long expedientTipus,
            Long expedientTipusPare,
            String filtreRsql,
            Pageable pageable,
            Sort sort) {

        log.debug("[SRV] Obtenint llistat de dominis. \n" +
                "entornId: " + entornId + "\n" +
                "expedientTipus: " + expedientTipus + "\n" +
                "expedientTipusPare: " + expedientTipusPare + "\n" +
                "filtre: " + filtreRsql + "\n");

        Specification<Domini> spec = DominiSpecifications.dominisList(
                entornId,
                expedientTipus,
                expedientTipusPare,
                true
        );

        return ServiceHelper.getDtoPage(
                dominiRepository,
                spec,
                filtreRsql,
                pageable,
                sort,
                DominiDto.class,
                dominiMapper);

    }

    private void validateDomini(Domini domini) throws ValidationException {
        Map<String, String> errors = new HashMap<>();

        if (domini.getCodi() == null|| domini.getCodi().isBlank())
            errors.put("codi", "El camp no pot ser null");
        if (domini.getNom() == null|| domini.getNom().isBlank())
            errors.put("nom", "El camp no pot ser null");
        if (domini.getEntorn() == null|| domini.getEntorn() <= 0L)
            errors.put("entorn", "El camp no pot ser null");
        if (domini.getUrl() == null || domini.getUrl().isBlank())
            errors.put("url", "El camp no pot ser null");
        if (domini.getTipus() == null)
            errors.put("tipus", "El camp no pot ser null. Valors admesos: [CONSULTA_SQL, CONSULTA_WS, CONSULTA_REST]");
        else if (TipusDominiEnum.CONSULTA_SQL.equals(domini.getTipus())) {
            if (domini.getSql() == null || domini.getSql().isBlank())
                errors.put("sql", "El camp no pot ser null per a dominis de tipus SQL");
        }
        if (domini.getTipusAuth() != null && !TipusAuthEnum.NONE.equals(domini.getTipusAuth())) {
            if (domini.getUsuari() == null || domini.getUsuari().isBlank())
                errors.put("usuari", "El camp no pot ser null per a dominis amb autenticació");
            if (domini.getContrasenya() == null || domini.getContrasenya().isBlank())
                errors.put("contrasenya", "El camp no pot ser null per a dominis amb autenticació");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Error de validació del domini: " + parametresToString(errors));
        }
    }

    // Dominis per entorn
    // //////////////////////////////////////////////

    @Override
    @Transactional(readOnly = true)
    public DominiDto getByEntornAndCodi(Long entorn, String codi) {

        log.debug("[SRV] Obtenint domini amb entorn: " + entorn + ", codi: " + codi);
        Optional<Domini> dominiOptional = dominiRepository.findByEntornAndCodi(entorn, codi);

        if (dominiOptional.isPresent()) {
            log.debug("Trobat domini amb entorn: " + entorn + ", codi: " + codi);
            return dominiMapper.entityToDto(dominiOptional.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Entorn: " + entorn + ", Codi: " + codi);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PagedList<DominiDto> listDominisByEntorn(
            Long entorn,
            String filtreRsql,
            Pageable pageable,
            Sort sort) {

        log.debug("[SRV] Obtenint llistat de dominis èr entorn. \n" +
                "entornId: " + entorn + "\n" +
                "filtre: " + filtreRsql + "\n");
        Specification<Domini> spec = DominiSpecifications.belongsToEntorn(entorn);

        return ServiceHelper.getDtoPage(
                dominiRepository,
                spec,
                filtreRsql,
                pageable,
                sort,
                DominiDto.class,
                dominiMapper);
    }


    // Dominis per tipus d'expedient
    // //////////////////////////////////////////////

    @Override
    @Transactional(readOnly = true)
    public DominiDto getByExpedientTipusAndCodi(
            Long expedientTipus,
            Long expedientTipusPare,
            String codi) {

        log.debug("[SRV] Obtenint domini amb ExpedientTipus: " + expedientTipus + ", ExpedientTipusPare: " + expedientTipusPare + ", Codi: " + codi);
        Optional<Domini> dominiOptional;

        if (expedientTipusPare != null)
            dominiOptional = dominiRepository.findByExpedientTipusAndCodiAmbHerencia(
                    expedientTipus,
                    expedientTipusPare,
                    codi);
        else
            dominiOptional = dominiRepository.findByExpedientTipusAndCodi(expedientTipus, codi);

        if (dominiOptional.isPresent()) {
            log.debug("Trobat domini amb ExpedientTipus: " + expedientTipus + ", ExpedientTipusPare: " + expedientTipusPare + ", Codi: " + codi);
            return dominiMapper.entityToDto(dominiOptional.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. ExpedientTipus: " + expedientTipus + ", ExpedientTipusPare: " + expedientTipusPare + ", Codi: " + codi);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public PagedList<DominiDto> listDominisByExpedientTipus(
            Long expedientTipus,
            String filtreRsql,
            Pageable pageable,
            Sort sort) {

        log.debug("[SRV] Obtenint llistat de dominis per tipus d'expedient. \n" +
                "expedientTipus: " + expedientTipus + "\n" +
                "filtre: " + filtreRsql + "\n");

        Specification<Domini> spec = DominiSpecifications.belongsToExpedientTipus(expedientTipus);

        return ServiceHelper.getDtoPage(
                dominiRepository,
                spec,
                filtreRsql,
                pageable,
                sort,
                DominiDto.class,
                dominiMapper);
    }

    private Domini getDominiById(Long dominiId) {
        log.debug("Obtenint domini per id: " + dominiId);
        Optional<Domini> dominiOptional = dominiRepository.findById(dominiId);

        if (dominiOptional.isPresent()) {
            log.debug("Trobat domini amb id: " + dominiId);
            return dominiOptional.get();
        } else {
            log.error("[SRV] Delete: No existeix cap domini amb id=" + dominiId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + dominiId);
        }
    }



    // Consulta de Dominis
    // //////////////////////////////////////////////

    @Override
    @Transactional(readOnly = true)
    public ResultatDomini consultaDomini(
            Long dominiId,
            String identificador,
            Map<String, String> parametres) {

        log.debug("[SRV] Realitzant consulta de domini. \n" +
                "dominiId: " + dominiId + "\n" +
                "identificador: " + identificador + "\n" +
                "parametres: " + parametresToString(parametres));
        ResultatDomini resultat = null;
        Domini domini;

        if (dominiId == 0) {
            domini = Domini.builder()
                    .id(0L)
                    .url(domini_intern_url)
                    .timeout(0)
                    .tipusAuth(TipusAuthEnum.NONE)
//                    .usuari(domini_intern_user)
//                    .contrasenya(domini_intern_pass)
                    .build();  // Domini intern
        } else {
            domini = getDominiById(dominiId);
            // Posam l'usuari i contrasenya a utilitzar en la consulta, depenent de l'origen
            if (domini.getTipusAuth() != null && !TipusAuthEnum.NONE.equals(domini.getTipusAuth())) {
                if (OrigenCredencialsEnum.PROPERTIES.equals(domini.getOrigenCredencials())) {
                    domini.setUsuari(environment.getProperty(domini.getUsuari()));
                    domini.setContrasenya(environment.getProperty(domini.getContrasenya()));
                }
            }
        }

        assert domini.getId() != null;

        IMap<String, ResultatDominiCache> dominiCache = hazelcastInstance.getMap(CACHE_DOMINI_ID);
        String cacheKey = getCacheKey(domini.getId(), identificador, parametres);
        ResultatDominiCache resultatCache = getDominiCache(domini, cacheKey, dominiCache);

        if (resultatCache == null) {
            try {
                if (domini.isDominiIntern()) {
                    log.debug("Petició de domini de tipus Intern (id=" + domini.getId() + ", identificador= " + identificador + ", params=" + parametresToString(parametres) + ")");
                    resultat = dominiInternService.consultaDomini(domini, identificador, parametres);
                } else if (domini.getTipus().equals(TipusDominiEnum.CONSULTA_WS)) {
                    log.debug("Petició de domini de tipus WS (id=" + domini.getId() + ", identificador= " + identificador + ", params=" + parametresToString(parametres) + ")");
                    resultat = dominiWsService.consultaDomini(domini, identificador, parametres);
                } else if (domini.getTipus().equals(TipusDominiEnum.CONSULTA_SQL)) {
                    log.debug("Petició de domini de tipus SQL (id=" + domini.getId() + ", identificador= " + identificador + ", params=" + parametresToString(parametres) + ")");
                    resultat = dominiSqlService.consultaDomini(domini, parametres);
                } else if (domini.getTipus().equals(TipusDominiEnum.CONSULTA_REST)) {
                    log.debug("Petició de domini de tipus REST (id=" + domini.getId() + ", identificador= " + identificador + ", params=" + parametresToString(parametres) + ")");
                    resultat = dominiRestService.consultaDomini(domini, identificador, parametres);
                }
            } catch (ResponseStatusException rex) {
                throw rex;
            } catch (Exception ex) {
                String errorMsg = "Error consultat el domini " +
                        "id=" + domini.getId() + ", " +
                        "codi=" + domini.getCodi() + ", " +
                        "identificador=" + identificador + ", " +
                        "params=" + parametresToString(parametres) + "). ";
                log.error(errorMsg, ex);
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        errorMsg + ex.getMessage());
            }

            if (resultat == null)
                resultat = new ResultatDomini();
            if (domini.getCacheSegons() > 0) {
                dominiCache.put(cacheKey, new ResultatDominiCache(resultat));
            }
        } else {
            resultat = resultatCache.getResultatDomini();
        }
        return resultat;
    }

    // Cache
    private ResultatDominiCache getDominiCache(
            Domini domini,
            String cacheKey,
            IMap<String, ResultatDominiCache> dominiCache) {

        ResultatDominiCache resultatCache = dominiCache.get(cacheKey);
        if (resultatCache != null && isCacheDateExpired(domini, resultatCache.getDataCreacio())) {
            dominiCache.evict(cacheKey);
            return null;
        }
        return resultatCache;
    }

    private boolean isCacheDateExpired(Domini domini, LocalDateTime dataCreacio) {
        if (domini.getCacheSegons() > 0) {
            return dataCreacio.isBefore(getValidCacheDate(domini));
        }
        return false;

    }

    private LocalDateTime getValidCacheDate(Domini domini) {
        return LocalDateTime.now().minus(domini.getCacheSegons(), ChronoUnit.SECONDS);
    }

    private String getCacheKey(
            Long dominiId,
            String identificador,
            Map<String, String> parametres) {
        StringBuilder sb = new StringBuilder();
        sb.append(dominiId.toString());
        sb.append(CACHE_KEY_SEPARATOR);
        sb.append(Objects.requireNonNullElse(identificador, "<null>"));
        sb.append(CACHE_KEY_SEPARATOR);
        if (parametres != null) {
            for (String clau: parametres.keySet()) {
                sb.append(clau);
                sb.append(CACHE_KEY_SEPARATOR);
                String valor = parametres.get(clau);
                sb.append(Objects.requireNonNullElse(valor, "<null>"));
                sb.append(CACHE_KEY_SEPARATOR);
            }
        }
        return sb.toString();
    }

    private String parametresToString(Map<String, String> parametres) {
        if (parametres == null)
            return "";
        return parametres.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", ", "{ ", " }"));
    }

}
