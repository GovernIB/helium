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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "es.caib.helium", ignoreUnknownFields = true)
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

    @Value("${es.caib.helium.domini.intern.user}") String domini_intern_user;
    @Value("${es.caib.helium.domini.intern.password}") String domini_intern_pass;
    @Value("${es.caib.helium.domini.intern.service.host}") String domini_intern_url;

    @Override
    @Transactional
    public DominiDto createDomini(
            DominiDto dominiDto) {
//            throws PermisDenegatException {

        log.debug("Creant nou domini (domini=" + dominiDto + ")");

        // TODO: Comprovar permisos sobre tipus d' expedient i entorn ??

        return dominiMapper.entityToDto(dominiRepository.save(dominiMapper.dtoToEntity(dominiDto)));

    }

    @Override
    @Transactional
    public void updateDomini(
            Long dominiId,
            DominiDto dominiDto) {
//            throws NoTrobatException, PermisDenegatException {

        log.debug("Modificant el domini existent (" +
                "dominiId=" + dominiId + ", " +
                "domini =" + dominiDto + ")");

        Domini domini = getDominiById(dominiId);

//        Optional<Domini> dominiOptional = dominiRepository.findById(dominiId);
//
//        dominiOptional.ifPresentOrElse(
//                domini -> {

                    // TODO: Comprovar permisos sobre tipus d'expedient i entorn ??

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
                    dominiRepository.save(domini);
//                }, () -> {
//                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + dominiId);
//                }
//        );
    }

    @Override
    @Transactional
    public void delete(
            Long dominiId) {

        log.debug("Esborrant el domini (dominiId=" + dominiId +  ")");

        Domini domini = getDominiById(dominiId);
        dominiRepository.delete(domini);

//        Optional<Domini> dominiOptional = dominiRepository.findById(dominiId);
//
//        dominiOptional.ifPresentOrElse(
//                domini -> {
//
//                    // TODO: Comprovar permisos sobre tipus d'expedient i entorn ??
//                    // TODO: Comprovar si el domini està sent utilitzat en algun camp
//
//                    dominiRepository.delete(domini);
//                }, () -> {
//                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + dominiId);
//                }
//        );

    }

    @Override
    @Transactional(readOnly = true)
    public DominiDto getById(
            Long dominiId) {

        Domini domini = getDominiById(dominiId);
        return dominiMapper.entityToDto(domini);

    }

    @Override
    public PagedList<DominiDto> listDominis(
            Long entornId,
            Long expedientTipus,
            Long expedientTipusPare,
            String filtreRsql,
            Pageable pageable,
            Sort sort) {

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


    // Dominis per entorn
    // //////////////////////////////////////////////

//    @Override
//    public DominiDto getByEntornAndId(Long entornId, Long dominiId) {
//
//        log.debug("Obtenint domini per entornId: " + entornId + ", id: " + dominiId);
//        Optional<Domini> dominiOptional = dominiRepository.findByEntornAndId(entornId, dominiId);
//
//        if (dominiOptional.isPresent()) {
//            log.debug("Trobat domini amb entornId=" + entornId + ", id: " + dominiId);
//            return dominiMapper.entityToDto(dominiOptional.get());
//        } else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. EntornId=" + entornId + ", Id: " + dominiId);
//        }
//    }

    @Override
    @Transactional(readOnly = true)
    public DominiDto getByEntornAndCodi(Long entorn, String codi) {

        log.debug("Obtenint domini amb entorn: " + entorn + ", codi: " + codi);
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

        log.debug("Obtenint domini amb ExpedientTipus: " + expedientTipus + ", ExpedientTipusPare: " + expedientTipusPare + ", Codi: " + codi);
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + dominiId);
        }
    }



    // Consulta de Dominis
    // //////////////////////////////////////////////

    @Override
    public ResultatDomini consultaDomini(
            Long dominiId,
            String identificador,
            Map<String, String> parametres) {

        ResultatDomini resultat = null;
        Domini domini;
        if (dominiId == 0)
            domini = Domini.builder()
                    .id(0L)
                    .url(domini_intern_url)
                    .timeout(0)
                    .tipusAuth(TipusAuthEnum.NONE)
                    .usuari(domini_intern_user)
                    .contrasenya(domini_intern_pass)
                    .build();  // Domini intern
        else
            domini = getDominiById(dominiId);

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
        sb.append(identificador != null ? identificador : "<null>");
        sb.append(CACHE_KEY_SEPARATOR);
        if (parametres != null) {
            for (String clau: parametres.keySet()) {
                sb.append(clau);
                sb.append(CACHE_KEY_SEPARATOR);
                String valor = parametres.get(clau);
                if (valor == null)
                    sb.append("<null>");
                else
                    sb.append(valor);
                sb.append(CACHE_KEY_SEPARATOR);
            }
        }
        return sb.toString();
    }

    private String parametresToString(
            Map<String, String> parametres) {
        String separador = ", ";
        StringBuilder sb = new StringBuilder();
        if (parametres != null) {
            for (String key: parametres.keySet()) {
                sb.append(key);
                sb.append(":");
                sb.append(parametres.get(key));
                sb.append(separador);
            }
        }
        if (sb.length() > 0)
            sb.substring(0, sb.length() - separador.length());
        return sb.toString();
    }

}
