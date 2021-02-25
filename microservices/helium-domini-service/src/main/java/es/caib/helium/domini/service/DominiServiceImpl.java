package es.caib.helium.domini.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.mapper.DominiMapper;
import es.caib.helium.domini.model.DominiDto;
import es.caib.helium.domini.model.OrigenCredencialsEnum;
import es.caib.helium.domini.model.PagedList;
import es.caib.helium.domini.model.TipusAuthEnum;
import es.caib.helium.domini.model.TipusDominiEnum;
import es.caib.helium.domini.repository.DominiRepository;
import es.caib.helium.domini.repository.DominiSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DominiServiceImpl implements DominiService {

    private final DominiRepository dominiRepository;
    private final DominiMapper dominiMapper;
    private final ObjectMapper objectMapper;


    @Override
    @Transactional
    public DominiDto createDomini(
//            Long entorn,
//            Long expedientTipus,
            DominiDto dominiDto) {
//            throws PermisDenegatException {

        log.debug("Creant nou domini (domini=" + dominiDto + ")");

        // TODO: Comprovar permisos sobre tipus d'expedient i entorn ??

        return dominiMapper.entityToDto(dominiRepository.save(dominiMapper.dtoToEntity(dominiDto)));

    }

    @Override
    @Transactional
    public void updateDomini(
            Long entornId,
            Long dominiId,
            DominiDto dominiDto) {
//            throws NoTrobatException, PermisDenegatException {

        log.debug("Modificant el domini existent (" +
                "entornId=" + entornId + ", " +
                "dominiId=" + dominiId + ", " +
                "domini =" + dominiDto + ")");

        Optional<Domini> dominiOptional = dominiRepository.findByEntornAndId(entornId, dominiId);

        dominiOptional.ifPresentOrElse(
                domini -> {

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
                }, () -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. EntornId: " + entornId + ", Id: " + dominiId);
                }
        );
    }

    @Override
    @Transactional
    public void delete(
            Long entornId,
            Long dominiId) {

        log.debug("Esborrant el domini (entornId=" + entornId + ", dominiId=" + dominiId +  ")");

        Optional<Domini> dominiOptional = dominiRepository.findByEntornAndId(entornId, dominiId);

        dominiOptional.ifPresentOrElse(
                domini -> {

                    // TODO: Comprovar permisos sobre tipus d'expedient i entorn ??
                    // TODO: Comprovar si el domini està sent utilitzat en algun camp

                    dominiRepository.delete(domini);
                }, () -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. EntornId=" + entornId + ", Id: " + dominiId);
                }
        );


    }

    @Override
    @Transactional(readOnly = true)
    public DominiDto getById(
            Long entornId,
            Long dominiId) {

        log.debug("Obtenint domini per id: " + dominiId);
        Optional<Domini> dominiOptional = dominiRepository.findByEntornAndId(entornId, dominiId);

        if (dominiOptional.isPresent()) {
            log.debug("Trobat domini amb entornId=" + entornId + ", id: " + dominiId);
            return dominiMapper.entityToDto(dominiOptional.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. EntornId=" + entornId + ", Id: " + dominiId);
        }

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


//    @Override
//    @Transactional(readOnly = true)
//    public DominiDto getByEntornAndCodi(Long entorn, String codi) {
//
//        log.debug("Obtenint domini amb entorn: " + entorn + ", codi: " + codi);
//        Optional<Domini> dominiOptional = dominiRepository.findByEntornAndCodi(entorn, codi);
//
//        if (dominiOptional.isPresent()) {
//            log.debug("Trobat domini amb entorn: " + entorn + ", codi: " + codi);
//            return dominiMapper.entityToDto(dominiOptional.get());
//        } else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Entorn: " + entorn + ", Codi: " + codi);
//        }
//    }


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
}
