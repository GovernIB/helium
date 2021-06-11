package es.caib.helium.expedient.mapper;

import org.mapstruct.Mapper;

import es.caib.helium.expedient.domain.Responsable;
import es.caib.helium.expedient.model.ResponsableDto;
import es.caib.helium.ms.mapper.BaseMapper;

@Mapper //(uses = DateMapper.class)
public interface ResponsableMapper extends BaseMapper<Responsable, ResponsableDto> {

}
