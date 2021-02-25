package es.caib.helium.domini.service;

import es.caib.helium.domini.model.DominiDto;
import es.caib.helium.domini.model.PagedList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface DominiService {

    public DominiDto createDomini(DominiDto domini);

    public void updateDomini(
            Long entornId,
            Long dominiId,
            DominiDto domini);

    public void delete(
            Long entornId,
            Long dominiId);

    public DominiDto getById(
            Long entornId,
            Long dominiId);

    public PagedList<DominiDto> listDominis(
            Long entornId,
            Long expedientTipusId,
            Long expedientTipusPareId,
            String filtre,
            Pageable pageable,
            Sort sort);

//    public DominiDto getByEntornAndCodi(Long entorn, String codi);

    // Dominis per tipus d'expedient

    // //////////////////////////////////////////////

    public DominiDto getByExpedientTipusAndCodi(
            Long expedientTipus,
            Long expedientTipusPare,
            String codi);


    public PagedList<DominiDto> listDominisByExpedientTipus(
            Long expedientTipusId,
            String filtreRsql,
            Pageable pageable,
            Sort sort);
}
