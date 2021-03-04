package es.caib.helium.base.service;

import es.caib.helium.base.model.BaseDto;
import es.caib.helium.base.model.PagedList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface BaseService {

    BaseDto createBase(BaseDto Base);

    void updateBase(
            Long BaseId,
            BaseDto Base);

    void delete(
            Long BaseId);

    BaseDto getById(
            Long BaseId);

    PagedList<BaseDto> listBases(
            String filtre,
            Pageable pageable,
            Sort sort);

}
