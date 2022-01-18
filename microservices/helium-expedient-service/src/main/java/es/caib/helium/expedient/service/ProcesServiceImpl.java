package es.caib.helium.expedient.service;

import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.domain.Proces;
import es.caib.helium.expedient.mapper.ProcesMapper;
import es.caib.helium.expedient.model.ProcesDto;
import es.caib.helium.expedient.repository.ExpedientRepository;
import es.caib.helium.expedient.repository.ProcesRepository;
import es.caib.helium.expedient.repository.ProcesSpecifications;
import es.caib.helium.expedient.repository.TascaRepository;
import es.caib.helium.expedient.repository.TascaSpecifications;
import es.caib.helium.ms.helper.ServiceHelper;
import es.caib.helium.ms.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcesServiceImpl implements ProcesService {

    private final ProcesRepository procesRepository;    
    private final ExpedientRepository expedientRepository;
    private final TascaRepository tascaRepository;
    private final ProcesMapper procesMapper;

    @Override
    @Transactional
    public ProcesDto createProces(
            ProcesDto procesDto) {
//            throws PermisDenegatException {

        // TODO: Comprovar permisos sobre tipus d' proces i entorn ??

        log.debug("[SRV] Creant nou proces (proces=" + procesDto + ")");
                
        Proces proces = procesMapper.dtoToEntity(procesDto);
        // Referències
        if (procesDto.getExpedientId() != null) {
        	Optional<Expedient> expedientOptional = expedientRepository.findById(procesDto.getExpedientId());
        	if (expedientOptional.isPresent()) {
        		proces.setExpedient(expedientOptional.get());
        	}
        }
        if (procesDto.getProcesArrelId() != null) {
        	if (procesDto.getProcesArrelId().equals(procesDto.getProcesId())) {
        		proces.setProcesArrel(proces);
        	} else {
            	Optional<Proces> procesArrelOptional = procesRepository.findByProcesId(procesDto.getProcesArrelId());
            	if (procesArrelOptional.isPresent()) {
            		proces.setProcesArrel(procesArrelOptional.get());
            	} else {
                    throw new ValidationException("No s'ha trobat el proces arrel: " + procesDto.getProcesArrelId());
            	}
        	}
        }
        if (procesDto.getProcesPareId() != null) {
        	Optional<Proces> procesPareOptional = procesRepository.findByProcesId(procesDto.getProcesPareId());
        	if (procesPareOptional.isPresent()) {
        		proces.setProcesPare(procesPareOptional.get());
        	} else {
                throw new ValidationException("No s'ha trobat el proces pare: " + procesDto.getProcesArrelId());
        	}
        }
        log.debug("[SRV] Validant proces");
        validateProces(proces);
                
        // Guarda la proces
        proces = procesRepository.save(proces);
		return procesMapper.entityToDto(proces);
    }

    @Override
    @Transactional
    public ProcesDto updateProces(
            String procesId,
            ProcesDto procesDto) {
//            throws NoTrobatException, PermisDenegatException {

        log.debug("[SRV] Modificant la proces existent (" +
                "procesId=" + procesId + ", " +
                "proces =" + procesDto + ")");

        // TODO: Comprovar permisos sobre tipus d'expedient i entorn ??
        Proces proces = getProcesById(procesId);
        
        proces.setSuspes( procesDto.isSuspes() );
        proces.setDataInici( procesDto.getDataInici() );
        proces.setDataFi( procesDto.getDataFi() );

        log.debug("[SRV] Validant proces");
        validateProces(proces);

        return procesMapper.entityToDto(procesRepository.save(proces));
    }

    @Override
    @Transactional
    public void delete(
            String procesId) {
        log.debug("[SRV] Esborrant la proces (procesId=" + procesId +  ")");
        // TODO: Comprovar permisos sobre tipus d'expedient i entorn ??

        Proces procesPare = getProcesById(procesId);

        // Eliminam tasques i processos fills
        List<Proces> processos = new ArrayList<>();
        List<Proces> processosFills = List.of(procesPare);
        do {
            processos.addAll(processosFills);
            var processosDescendents = new ArrayList<Proces>();
            for (var procesFill: processosFills) {
                processosDescendents.addAll(procesRepository.findAll(ProcesSpecifications.processPareIdLike(procesFill.getProcesId())));
            }
            processosFills = processosDescendents;
        } while (!processosFills.isEmpty());

        var tasques = tascaRepository.findAll(TascaSpecifications.inProcesses(
                processos.stream().map(p -> p.getId()).collect(Collectors.toList())));
        if (tasques != null) {
            tascaRepository.deleteAll(tasques);
        }
        procesRepository.deleteAll(processos);
    }

    @Override
    @Transactional(readOnly = true)
    public ProcesDto getById(
            String procesId) {
        log.debug("[SRV] Obtenint proces amb Id: " + procesId);
        var proces = getProcesById(procesId);
        return procesMapper.entityToDto(proces);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PagedList<ProcesDto> listProcessos(
			String processDefinitionId,
			String procesArrelId,
            String filtreRsql,
            final Pageable pageable,
            final Sort sort) {

        log.debug("[SRV] Obtenint llistat de processos. \n" +
                "processDefinitionId: " + processDefinitionId +
                "filtreRsql:" + filtreRsql);

        Specification<Proces> spec = ProcesSpecifications.procesList(
        		processDefinitionId,
        		procesArrelId
        	);

        PagedList<ProcesDto> pagedList = ServiceHelper.getDtoPage(
                procesRepository,
                spec,
                filtreRsql,
                pageable,
                sort,
                ProcesDto.class,
                procesMapper);
        
        return pagedList;
    }

    private Proces getProcesById(String procesId) {
        log.debug("Obtenint proces per id: " + procesId);
        Optional<Proces> procesOptional = procesRepository.findByProcesId(procesId);
        if (procesOptional.isPresent()) {
            log.debug("Trobada proces amb proces id: " + procesId);
            return procesOptional.get();
        }        
        log.error("[SRV] No existeix cap proces amb id=" + procesId);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + procesId);
    }

    
    private void validateProces(Proces proces) throws ValidationException {
        Map<String, String> errors = new HashMap<>();

        if (proces.getProcesId() == null || proces.getProcesId().isEmpty()) {
            errors.put("procesId", "El camp no pot ser null");
        }
        if (proces.getProcessDefinitionId() == null || proces.getProcessDefinitionId().isEmpty()) {
            errors.put("processDefinitionId", "El camp no pot ser null");        	
        }
        if (proces.getProcesArrel() == null) {
            errors.put("procesArrel", "El camp no pot ser null");
        }
        if (proces.getDataInici() == null) {
            errors.put("dataInici", "El camp no pot ser null");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Error de validació de la proces: " + parametresToString(errors));
        }
    }

    private String parametresToString(Map<String, String> parametres) {
        if (parametres == null) {
            return "";
        }
        return parametres.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", ", "{ ", " }"));
    }

}