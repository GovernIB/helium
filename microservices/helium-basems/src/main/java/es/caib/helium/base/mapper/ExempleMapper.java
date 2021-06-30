package es.caib.helium.base.mapper;

import org.mapstruct.Mapper;

import es.caib.helium.base.domain.Exemple;
import es.caib.helium.base.model.ExempleDto;

@Mapper (uses = DateMapper.class)
public interface ExempleMapper extends BaseMapper<Exemple, ExempleDto> {

}
