package es.caib.helium.base.mapper;

import es.caib.helium.base.domain.Base;
import es.caib.helium.base.model.BaseDto;
import org.mapstruct.Mapper;

@Mapper(uses = DateMapper.class)
public interface BaseMapper extends _BaseMapper<Base, BaseDto> {

}
