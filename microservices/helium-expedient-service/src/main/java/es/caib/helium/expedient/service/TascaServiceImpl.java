package es.caib.helium.expedient.service;

import es.caib.helium.expedient.domain.Grup;
import es.caib.helium.expedient.domain.Proces;
import es.caib.helium.expedient.domain.Responsable;
import es.caib.helium.expedient.domain.Tasca;
import es.caib.helium.expedient.mapper.TascaMapper;
import es.caib.helium.expedient.model.TascaDto;
import es.caib.helium.expedient.repository.GrupRepository;
import es.caib.helium.expedient.repository.ProcesRepository;
import es.caib.helium.expedient.repository.ResponsableRepository;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TascaServiceImpl implements TascaService {

    private final TascaRepository tascaRepository;
    private final ResponsableRepository responsableRepository;
    private final GrupRepository grupRepository;
    private final ProcesRepository procesRepository;
    
    private final TascaMapper tascaMapper;

    @Override
    @Transactional
    public TascaDto createTasca(
            TascaDto tascaDto) {
//            throws PermisDenegatException {

        // TODO: Comprovar permisos sobre tipus d' tasca i entorn ??

        log.debug("[SRV] Creant nou tasca (tasca=" + tascaDto + ")");
        
        // Guarda la llista de responsables i grups
        List<String> responsables = new ArrayList<String>();
        if (tascaDto.getResponsables() != null) {
            for(String responsable : tascaDto.getResponsables()) {
            	responsables.add(responsable);
            }
        }
        List<String> grups = new ArrayList<String>();
        if (tascaDto.getGrups() != null) {
            for(String grup : tascaDto.getGrups()) {
            	grups.add(grup);
            }
        }
        
        Tasca tasca = tascaMapper.dtoToEntity(tascaDto);
        if (tasca.getResponsables() != null) {
        	tasca.getResponsables().clear();
        }
        if (tasca.getGrups() != null) {
        	tasca.getGrups().clear();
        }

        if (tascaDto.getProcesId() != null) {
            Optional<Proces> procesOptional = procesRepository.findByProcesId(tascaDto.getProcesId());
            if (procesOptional.isPresent()) {
                tasca.setProces(procesOptional.get());
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
        // Crea els grups
        if (grups.size() > 0) {
        	Grup grup;
            for (String grupCodi : grups ) {
            	grup = Grup.builder()
            			.grupCodi(grupCodi)
            			.tasca(tasca)
            			.build();
            	grup = grupRepository.save(grup);
            	tasca.getGrups().add(grup);
            }           
        }
        tascaDto = tascaMapper.entityToDto(tasca);

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
        tasca.setAgafada( tascaDto.isAgafada() );
        tasca.setCancelada( tascaDto.isCancelada() );
        tasca.setSuspesa( tascaDto.isSuspesa() );
        tasca.setCompletada( tascaDto.isCompletada() );
        tasca.setAssignada( tascaDto.isAssignada() );
        tasca.setMarcadaFinalitzar( tascaDto.getMarcadaFinalitzar() );
        tasca.setErrorFinalitzacio( tascaDto.getErrorFinalitzacio() );
        tasca.setDataFins( tascaDto.getDataFins() );
        tasca.setDataFi( tascaDto.getDataFi() );
        tasca.setIniciFinalitzacio( tascaDto.getIniciFinalitzacio() );
        tasca.setDataCreacio( tascaDto.getDataCreacio() );
        tasca.setUsuariAssignat( tascaDto.getUsuariAssignat() );

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
			String responsable,
			List<String> grups,
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
                "responsable: " + responsable + "\n" +
                "grups: " + grups + "\n" +
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
        		responsable, 
        		grups,
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
        Optional<Tasca> tascaOptional = tascaRepository.findByTascaId(tascaId);

        if (tascaOptional.isPresent()) {
            log.debug("Trobada tasca amb id: " + tascaId);
            return tascaOptional.get();
        } 
        
        log.error("[SRV] Delete: No existeix cap tasca amb id=" + tascaId);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + tascaId);
    }

    
    private void validateTasca(Tasca tasca) throws ValidationException {
        Map<String, String> errors = new HashMap<>();

        if (tasca.getTascaId() == null || tasca.getTascaId().isEmpty()) {
            errors.put("tascaId", "El camp no pot ser null");
        }
//        if (tasca.getExpedient() == null) {
//            errors.put("expedient", "El camp no pot ser null");
//        }
        if (tasca.getProces() == null) {
            errors.put("proces", "El camp no pot ser null");
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
	public List<String> getResponsables(String tascaId) {
    	Tasca tasca = getTascaById(tascaId);
    	//List<Responsable> responsables = responsableRepository.findByTascaId(tascaId);
		return tasca.getResponsables()
				.stream()
				.map(e -> e.getUsuariCodi())
				.collect(Collectors.toList());
	}

    @Override
    @Transactional
	public void setResponsables(String tascaId, List<String> responsables) {
    	deleteResponsables(tascaId);
    	if (responsables != null && responsables.size() > 0) {
    		Tasca tasca = getTascaById(tascaId);
    		Responsable responsable;
    		for (String usuariCodi : responsables) {
    			responsable = Responsable.builder()
    					.usuariCodi(usuariCodi)
    					.tasca(tasca)
    					.build();
    			tasca.getResponsables().add(responsable);
    			responsableRepository.save(responsable);
    		}
    	}
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

	@Override
    @Transactional(readOnly = true)
	public List<String> getGrups(String tascaId) {
    	Tasca tasca = getTascaById(tascaId);
		return tasca.getGrups()
				.stream()
				.map(e -> e.getGrupCodi())
				.collect(Collectors.toList());
	}

	@Override
    @Transactional
	public void setGrups(String tascaId, List<String> grups) {
    	deleteGrups(tascaId);
    	if (grups != null && grups.size() > 0) {
    		Tasca tasca = getTascaById(tascaId);
    		Grup grup;
    		for (String grupCodi : grups) {
    			grup = Grup.builder()
    					.grupCodi(grupCodi)
    					.tasca(tasca)
    					.build();
    			tasca.getGrups().add(grup);
    			grupRepository.save(grup);
    		}
    	}
	}

	@Override
    @Transactional
	public void deleteGrups(String tascaId) {
    	Tasca tasca = getTascaById(tascaId);
        if (tasca.getGrups().size() > 0) {
        	for (Grup grup : tasca.getGrups()) {
            	grupRepository.delete(grup.getId());
        	}
        	tasca.getGrups().clear();
        }
	}



}
