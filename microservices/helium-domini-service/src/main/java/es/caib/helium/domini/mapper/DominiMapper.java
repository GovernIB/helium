package es.caib.helium.domini.mapper;

import es.caib.helium.domini.domain.Domini;
import es.caib.helium.domini.model.DominiDto;
import org.mapstruct.Mapper;

@Mapper //(uses = DateMapper.class)
public interface DominiMapper extends BaseMapper<Domini, DominiDto> {

}
