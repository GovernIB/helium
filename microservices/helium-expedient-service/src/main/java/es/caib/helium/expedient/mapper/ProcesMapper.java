package es.caib.helium.expedient.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.caib.helium.expedient.domain.Proces;
import es.caib.helium.expedient.model.ProcesDto;
import es.caib.helium.ms.mapper.BaseMapper;

@Mapper
public interface ProcesMapper extends BaseMapper<Proces, ProcesDto> {

    @Override
    @Mapping(source = "expedient.id", target = "expedientId")
    @Mapping(source = "procesArrel.id", target = "procesArrelId")
    @Mapping(source = "procesPare.id", target = "procesPareId")
    ProcesDto entityToDto(Proces proces);
}
