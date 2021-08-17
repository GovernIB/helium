package es.caib.helium.expedient.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.domain.Proces;
import es.caib.helium.expedient.mapper.ProcesMapper;
import es.caib.helium.expedient.model.ProcesDto;
import es.caib.helium.expedient.repository.ExpedientRepository;
import es.caib.helium.expedient.repository.ProcesRepository;
import es.caib.helium.expedient.repository.ProcesSpecifications;
import es.caib.helium.ms.helper.ServiceHelper;
import es.caib.helium.ms.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcesServiceImpl implements ProcesService {

    private final ProcesRepository procesRepository;    
    private final ExpedientRepository expedientRepository;    
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
        	if (procesDto.getProcesArrelId().equals(procesDto.getId())) {
        		proces.setProcesArrel(proces);
        	} else {
            	Optional<Proces> procesArrelOptional = procesRepository.findById(procesDto.getProcesArrelId());
            	if (procesArrelOptional.isPresent()) {
            		proces.setProcesArrel(procesArrelOptional.get());
            	} else {
                    throw new ValidationException("No s'ha trobat el proces arrel: " + procesDto.getProcesArrelId());
            	}
        	}
        }
        if (procesDto.getProcesPareId() != null) {
        	Optional<Proces> procesPareOptional = procesRepository.findById(procesDto.getProcesPareId());
        	if (procesPareOptional.isPresent()) {
        		proces.setProcesPare(procesPareOptional.get());
        	} else {
                throw new ValidationException("No s'ha trobat el proces arrel: " + procesDto.getProcesArrelId());
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
        procesRepository.delete(procesId);
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
        Optional<Proces> procesOptional = procesRepository.findById(procesId);
        if (procesOptional.isPresent()) {
            log.debug("Trobada proces amb id: " + procesId);
            return procesOptional.get();
        }        
        log.error("[SRV] No existeix cap proces amb id=" + procesId);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + procesId);
    }

    
    private void validateProces(Proces proces) throws ValidationException {
        Map<String, String> errors = new HashMap<>();

        if (proces.getId() == null || proces.getId().isEmpty()) {
            errors.put("id", "El camp no pot ser null");
        }
        if (proces.getProcessDefinitionId() == null || proces.getProcessDefinitionId().isEmpty()) {
            errors.put("processDefinitionId", "El camp no pot ser null");        	
        }
        if (proces.getExpedient() == null) {
            errors.put("expedient", "El camp no pot ser null");        	
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
