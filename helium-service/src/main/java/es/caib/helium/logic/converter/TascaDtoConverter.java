package es.caib.helium.logic.converter;

import es.caib.helium.logic.helper.PaginacioHelper.Converter;
import es.caib.helium.logic.intf.dto.CampTascaDto;
import es.caib.helium.logic.intf.dto.DocumentTascaDto;
import es.caib.helium.logic.intf.dto.FirmaTascaDto;
import es.caib.helium.logic.intf.dto.TascaDto;
import es.caib.helium.persist.entity.Tasca;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public class TascaDtoConverter implements Converter<Tasca, TascaDto> {

    String startTaskName;
    boolean heretada;
    Long expedientTipusId;

    @Override
    public TascaDto convert(Tasca source) {

        List<CampTascaDto> camps = new ArrayList<>();
        List<DocumentTascaDto> documents = new ArrayList<>();
        List<FirmaTascaDto> firmes = new ArrayList<>();

        return TascaDto.builder()
                .id(source.getId())
                .nom(source.getNom())
                .jbpmName(source.getJbpmName())
                .inicial(source.getNom().equals(startTaskName))
                .heretat(heretada)
                .camps(source.getCamps()
                        .stream()
                        .filter(c -> expedientTipusId == null || c.getExpedientTipus() == null || c.getExpedientTipus().getId().equals(expedientTipusId))
                        .map(c -> CampTascaDto.builder().id(c.getId()).build())
                        .collect(Collectors.toList()))
                .documents(source.getDocuments()
                        .stream()
                        .filter(d -> expedientTipusId == null || d.getExpedientTipus() == null || d.getExpedientTipus().getId().equals(expedientTipusId))
                        .map(d -> DocumentTascaDto.builder().id(d.getId()).build())
                        .collect(Collectors.toList()))
                .firmes(source.getFirmes()
                        .stream()
                        .filter(f -> expedientTipusId == null || f.getExpedientTipus() == null || f.getExpedientTipus().getId().equals(expedientTipusId))
                        .map(f -> FirmaTascaDto.builder().id(f.getId()).build())
                        .collect(Collectors.toList()))
                .build();
    }
}
