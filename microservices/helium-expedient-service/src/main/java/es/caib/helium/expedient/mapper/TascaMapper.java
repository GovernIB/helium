package es.caib.helium.expedient.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.caib.helium.expedient.domain.Grup;
import es.caib.helium.expedient.domain.Responsable;
import es.caib.helium.expedient.domain.Tasca;
import es.caib.helium.expedient.model.TascaDto;
import es.caib.helium.ms.mapper.BaseMapper;

@Mapper //(uses = DateMapper.class)
public interface TascaMapper extends BaseMapper<Tasca, TascaDto> {

    @Override
    @Mapping(source = "proces.procesId", target = "procesId")
    @Mapping(source = "proces.processDefinitionId", target = "processDefinitionId")
    @Mapping(source = "proces.expedient.id", target = "expedientId")
    TascaDto entityToDto(Tasca tasca);
    
    default public List<String> mapResponsables(List<Responsable> responsables) {
    	List<String> responsablesCodis = responsables != null ?
    			responsables.stream().map(r -> mapResponsable(r)).collect(Collectors.toList())
    			: null;
    	return responsablesCodis;
    }

    default public String mapResponsable(Responsable responsable) {
    	return responsable != null ? 
    			responsable.getUsuariCodi() 
    			: null;
    }

    default public List<Responsable> mapResponsablesStr(List<String> responsablesStr) {
    	List<Responsable> responsables = responsablesStr != null ?
    			responsablesStr.stream().map(r -> mapResponsableStr(r)).collect(Collectors.toList())
    			: null;
    	return responsables;
    }

    default public Responsable mapResponsableStr(String responsableStr) {
    	Responsable responsable = null;
    	if (responsableStr != null && !responsableStr.isEmpty()) {
    		responsable = Responsable.builder()
    				.usuariCodi(responsableStr)
    				.build();
    	}
    	return responsable;
    }

    
    default public List<String> mapGrups(List<Grup> grups) {
    	List<String> grupsCodis = grups != null ?
    			grups.stream().map(r -> mapGrup(r)).collect(Collectors.toList())
    			: null;
    	return grupsCodis;
    }

    default public String mapGrup(Grup grup) {
    	return grup != null ? 
    			grup.getGrupCodi() 
    			: null;
    }

    default public List<Grup> mapGrupsStr(List<String> grupsStr) {
    	List<Grup> grups = grupsStr != null ?
    			grupsStr.stream().map(r -> mapGrupStr(r)).collect(Collectors.toList())
    			: null;
    	return grups;
    }

    default public Grup mapGrupStr(String grupStr) {
    	Grup grup = null;
    	if (grupStr != null && !grupStr.isEmpty()) {
    		grup = Grup.builder()
    				.grupCodi(grupStr)
    				.build();
    	}
    	return grup;
    }
}
