package es.caib.helium.expedient.mapper;

import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.model.ExpedientDto;

import org.mapstruct.Mapper;

@Mapper //(uses = DateMapper.class)
public interface ExpedientMapper extends BaseMapper<Expedient, ExpedientDto> {

}
