package es.caib.helium.expedient.mapper;

import org.mapstruct.Mapper;

import es.caib.helium.expedient.domain.Tasca;
import es.caib.helium.expedient.model.TascaDto;
import es.caib.helium.ms.mapper.BaseMapper;
import org.mapstruct.Mapping;

@Mapper //(uses = DateMapper.class)
public interface TascaMapper extends BaseMapper<Tasca, TascaDto> {

    @Override
    @Mapping(source = "expedient.id", target = "expedientId")
    TascaDto entityToDto(Tasca tasca);

}
