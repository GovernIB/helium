package es.caib.helium.expedient.mapper;

import org.mapstruct.Mapper;

import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.model.ExpedientDto;
import es.caib.helium.ms.mapper.BaseMapper;

@Mapper //(uses = DateMapper.class)
public interface ExpedientMapper extends BaseMapper<Expedient, ExpedientDto> {

}
