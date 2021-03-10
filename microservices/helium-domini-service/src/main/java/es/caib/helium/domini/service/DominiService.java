package es.caib.helium.domini.service;

import es.caib.helium.domini.model.DominiDto;
import es.caib.helium.domini.model.PagedList;
import es.caib.helium.domini.model.ResultatDomini;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

public interface DominiService {

    DominiDto createDomini(DominiDto domini);

    void updateDomini(
            Long dominiId,
            DominiDto domini);

    void delete(
            Long dominiId);

    DominiDto getById(
            Long dominiId);

    PagedList<DominiDto> listDominis(
            Long entornId,
            Long expedientTipusId,
            Long expedientTipusPareId,
            String filtre,
            Pageable pageable,
            Sort sort);

    // Dominis per entorn
    // //////////////////////////////////////////////

    DominiDto getByEntornAndCodi(
            Long entorn,
            String codi);

    PagedList<DominiDto> listDominisByEntorn(
            Long entornId,
            String filtre,
            Pageable pageable,
            Sort sort);

    // Dominis per tipus d'expedient
    // //////////////////////////////////////////////

    DominiDto getByExpedientTipusAndCodi(
            Long expedientTipus,
            Long expedientTipusPare,
            String codi);


    PagedList<DominiDto> listDominisByExpedientTipus(
            Long expedientTipusId,
            String filtreRsql,
            Pageable pageable,
            Sort sort);


    ResultatDomini consultaDomini(
            Long dominiId,
            String identificador,
            Map<String, String> parametres);
}
