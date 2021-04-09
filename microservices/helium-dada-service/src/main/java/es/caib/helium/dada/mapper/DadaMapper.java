package es.caib.helium.dada.mapper;

import es.caib.helium.dada.domain.Dada;
import es.caib.helium.dada.model.DadaDto;

import org.mapstruct.Mapper;

@Mapper(uses = DateMapper.class)
public interface DadaMapper extends ServeiMapper<Dada, DadaDto> {

}
