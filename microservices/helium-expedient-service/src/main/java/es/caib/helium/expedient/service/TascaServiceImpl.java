package es.caib.helium.expedient.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import es.caib.helium.expedient.domain.Responsable;
import es.caib.helium.expedient.domain.Tasca;
import es.caib.helium.expedient.mapper.ResponsableMapper;
import es.caib.helium.expedient.mapper.TascaMapper;
import es.caib.helium.expedient.model.ResponsableDto;
import es.caib.helium.expedient.model.TascaDto;
import es.caib.helium.expedient.repository.ExpedientRepository;
import es.caib.helium.expedient.repository.ResponsableRepository;
import es.caib.helium.expedient.repository.TascaRepository;
import es.caib.helium.expedient.repository.TascaSpecifications;
import es.caib.helium.ms.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TascaServiceImpl implements TascaService {

    private final TascaRepository tascaRepository;
    private final ResponsableRepository responsableRepository;
    private final ExpedientRepository expedientRepository;
    
    private final TascaMapper tascaMapper;
    private final ResponsableMapper responsableMapper;

    @Override
    @Transactional
    public TascaDto createTasca(
            TascaDto tascaDto) {
//            throws PermisDenegatException {

        // TODO: Comprovar permisos sobre tipus d' tasca i entorn ??

        log.debug("[SRV] Creant nou tasca (tasca=" + tascaDto + ")");
        
        // Guarda la llista de responsables
        List<String> responsables = new ArrayList<String>();
        if (tascaDto.getResponsables() != null) {
            for(ResponsableDto responsable : tascaDto.getResponsables()) {
            	responsables.add(responsable.getUsuariCodi());
            }
        }
        
        Tasca tasca = tascaMapper.dtoToEntity(tascaDto);
        if (tasca.getResponsables() != null) { // TODO sense if passava validacio i petava.
        	tasca.getResponsables().clear();
        }
        
        if (tascaDto.getExpedientId() != null) {
        	Optional<Expedient> expedientOptional = expedientRepository.findById(tascaDto.getExpedientId());
        	if (expedientOptional.isPresent()) {
        		tasca.setExpedient(expedientOptional.get());
        	}
        }        
        log.debug("[SRV] Validant tasca");
        validateTasca(tasca);
                
        // Guarda la tasca
        tasca = tascaRepository.save(tasca);
        // Crea els responsables
        if (responsables.size() > 0) {
        	Responsable responsable;
            for (String usuariCodi : responsables ) {
            	responsable = Responsable.builder()
            			.usuariCodi(usuariCodi)
            			.tasca(tasca)
            			.build();
            	responsable = responsableRepository.save(responsable);
            	tasca.getResponsables().add(responsable);
            }           
        }
        tascaDto = tascaMapper.entityToDto(tasca);
        tascaDto.setExpedientId(tasca.getExpedient().getId());
		return tascaDto;
    }

    @Override
    @Transactional
    public TascaDto updateTasca(
            String tascaId,
            TascaDto tascaDto) {
//            throws NoTrobatException, PermisDenegatException {

        log.debug("[SRV] Modificant la tasca existent (" +
                "tascaId=" + tascaId + ", " +
                "tasca =" + tascaDto + ")");

        // TODO: Comprovar permisos sobre tipus d'expedient i entorn ??

        Tasca tasca = getTascaById(tascaId);
        
        tasca.setNom( tascaDto.getNom() );
        tasca.setTitol( tascaDto.getTitol() );
        tasca.setAfagada( tascaDto.isAfagada() );
        tasca.setCancelada( tascaDto.isCancelada() );
        tasca.setSuspesa( tascaDto.isSuspesa() );
        tasca.setCompletada( tascaDto.isCompletada() );
        tasca.setAssignada( tascaDto.isAssignada() );
        tasca.setMarcadaFinalitzar( tascaDto.isMarcadaFinalitzar() );
        tasca.setErrorFinalitzacio( tascaDto.isErrorFinalitzacio() );
        tasca.setDataFins( tascaDto.getDataFins() );
        tasca.setDataFi( tascaDto.getDataFi() );
        tasca.setIniciFinalitzacio( tascaDto.getIniciFinalitzacio() );
        tasca.setDataCreacio( tascaDto.getDataCreacio() );
        tasca.setUsuariAssignat( tascaDto.getUsuariAssignat() );
        tasca.setGrupAssignat( tascaDto.getGrupAssignat() );

        log.debug("[SRV] Validant tasca");
        validateTasca(tasca);

        return tascaMapper.entityToDto(tascaRepository.save(tasca));

    }

    @Override
    @Transactional
    public void delete(
            String tascaId) {

        log.debug("[SRV] Esborrant la tasca (tascaId=" + tascaId +  ")");

        // TODO: Comprovar permisos sobre tipus d'expedient i entorn ??
        deleteResponsables(tascaId);
        tascaRepository.delete(tascaId);
    }

    @Override
    @Transactional(readOnly = true)
    public TascaDto getById(
            String tascaId) {

        log.debug("[SRV] Obtenint tasca amb Id: " + tascaId);

        var tasca = getTascaById(tascaId);
        return tascaMapper.entityToDto(tasca);

    }

    @Override
    @Transactional(readOnly = true)
    public PagedList<TascaDto> listTasques(
    		Long entornId,
            Long expedientTipusId,
			String usuariAssignat,
			String nom,
			String titol,
			Long expedientId,
			String expedientTitol,
			String expedientNumero,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Date dataLimitInici,
			Date dataLimitFi,
			boolean mostrarAssignadesUsuari,
			boolean mostrarAssignadesGrup,
			boolean nomesPendents,
            String filtreRsql,
            final Pageable pageable,
            final Sort sort) {

        log.debug("[SRV] Obtenint llistat de tasques. \n" +
                "entornId: " + entornId +
                "expedientTipusId: " + expedientTipusId + "\n" +
                "usuariAssignat: " + usuariAssignat + "\n" +
                "nom: " + nom + "\n" +
                "titol: " + titol + "\n" +
                "expedientId: " + expedientId + "\n" +
                "expedientTitol: " + expedientTitol + "\n" +
                "expedientNumero: " + expedientNumero + "\n" +
                "dataCreacioInici: " + dataCreacioInici + "\n" +
                "dataCreacioFi: " + dataCreacioFi + "\n" +
                "dataLimitInici: " + dataLimitInici + "\n" +
                "dataLimitFi: " + dataLimitFi + "\n" +
                "mostrarAssignadesUsuari: " + mostrarAssignadesUsuari + "\n" +
                "mostrarAssignadesGrup: " + mostrarAssignadesGrup + "\n" +
                "nomesPendents: " + nomesPendents + "\n" +

                "filtreRsql:" + filtreRsql);

        Specification<Tasca> spec = TascaSpecifications.tasquesList(
        		entornId, 
        		expedientTipusId,
        		usuariAssignat, 
        		nom, 
        		titol, 
        		expedientId, 
        		expedientTitol, 
        		expedientNumero, 
        		dataCreacioInici, 
        		dataCreacioFi, 
        		dataLimitInici, 
        		dataLimitFi, 
        		mostrarAssignadesUsuari, 
        		mostrarAssignadesGrup, 
        		nomesPendents
        	);

        PagedList<TascaDto> pagedList = ServiceHelper.getDtoPage(
                tascaRepository,
                spec,
                filtreRsql,
                pageable,
                sort,
                TascaDto.class,
                tascaMapper);
        
        return pagedList;
    }

    private Tasca getTascaById(String tascaId) {
        log.debug("Obtenint tasca per id: " + tascaId);
        Optional<Tasca> tascaOptional = tascaRepository.findById(tascaId);

        if (tascaOptional.isPresent()) {
            log.debug("Trobada tasca amb id: " + tascaId);
            return tascaOptional.get();
        } 
        
        log.error("[SRV] Delete: No existeix cap tasca amb id=" + tascaId);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + tascaId);
    }

    
    private void validateTasca(Tasca tasca) throws ValidationException {
        Map<String, String> errors = new HashMap<>();

        if (tasca.getId() == null || tasca.getId().isEmpty()) {
            errors.put("id", "El camp no pot ser null");
        }
        if (tasca.getExpedient() == null) {
            errors.put("expedient", "El camp no pot ser null");        	
        }
        if (tasca.getProcesId() == null || tasca.getProcesId().isEmpty()) {
            errors.put("procesId", "El camp no pot ser null");
        }
        if (tasca.getNom() == null || tasca.getNom().isBlank())
            errors.put("nom", "El camp no pot ser null");
        if (tasca.getTitol() == null || tasca.getTitol().isBlank())
            errors.put("titol", "El camp no pot ser null");
        if (tasca.getDataCreacio() == null ) {
            errors.put("dataCreacio", "El camp no pot ser null");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Error de validació de la tasca: " + parametresToString(errors));
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

    @Override
    @Transactional(readOnly = true)
	public List<ResponsableDto> getResponsables(String tascaId) {
    	Tasca tasca = getTascaById(tascaId);
    	//List<Responsable> responsables = responsableRepository.findByTascaId(tascaId);
		return tasca.getResponsables()
				.stream()
				.map(e -> responsableMapper.entityToDto(e))
				.collect(Collectors.toList());
	}

    @Override
    @Transactional
	public List<ResponsableDto> setResponsables(String tascaId, List<String> responsables) {
    	List<ResponsableDto> responsablesDto = null;
    	deleteResponsables(tascaId);
    	if (responsables != null && responsables.size() > 0) {
    		responsablesDto = new ArrayList<ResponsableDto>();
    		Tasca tasca = getTascaById(tascaId);
    		Responsable responsable;
    		for (String usuariCodi : responsables) {
    			responsable = Responsable.builder()
    					.usuariCodi(usuariCodi)
    					.tasca(tasca)
    					.build();
    			tasca.getResponsables().add(responsable);
    			responsableRepository.save(responsable);
    			responsablesDto.add(responsableMapper.entityToDto(responsable));
    		}
    	}
    	return responsablesDto;
	}

    @Override
    @Transactional
	public void deleteResponsables(String tascaId) {
    	
    	Tasca tasca = getTascaById(tascaId);
        if (tasca.getResponsables().size() > 0) {
        	for (Responsable responsable : tasca.getResponsables()) {
            	responsableRepository.delete(responsable.getId());
        	}
        	tasca.getResponsables().clear();
        }
	}

}
