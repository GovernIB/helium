package es.caib.helium.expedient.service;

import java.util.Date;
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
import es.caib.helium.expedient.domain.Tasca;
import es.caib.helium.expedient.mapper.TascaMapper;
import es.caib.helium.expedient.model.TascaDto;
import es.caib.helium.expedient.repository.ExpedientRepository;
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
    private final ExpedientRepository expedientRepository;
    private final TascaMapper tascaMapper;

    @Override
    @Transactional
    public TascaDto createTasca(
            TascaDto tascaDto) {
//            throws PermisDenegatException {

        // TODO: Comprovar permisos sobre tipus d' tasca i entorn ??

        log.debug("[SRV] Creant nou tasca (tasca=" + tascaDto + ")");
        Tasca tasca = tascaMapper.dtoToEntity(tascaDto);
        if (tascaDto.getExpedientId() != null) {
        	Optional<Expedient> expedientOptional = expedientRepository.findById(tascaDto.getExpedientId());
        	if (expedientOptional.isPresent())
        		tasca.setExpedient(expedientOptional.get());
        }
        log.debug("[SRV] Validant tasca");
        validateTasca(tasca);
        return tascaMapper.entityToDto(tascaRepository.save(tasca));

    }

    @Override
    @Transactional
    public TascaDto updateTasca(
            Long tascaId,
            TascaDto tascaDto) {
//            throws NoTrobatException, PermisDenegatException {

        log.debug("[SRV] Modificant la tasca existent (" +
                "tascaId=" + tascaId + ", " +
                "tasca =" + tascaDto + ")");

        // TODO: Comprovar permisos sobre tipus d'expedient i entorn ??

        Tasca tasca = getTascaById(tascaId);
        
        if (tascaDto.getExpedientId() != null 
        		&& (tasca.getExpedient() == null || tasca.getExpedient().getId() != tascaDto.getExpedientId())) {
        	Optional<Expedient> expedientOptional = expedientRepository.findById(tascaDto.getExpedientId());
        	if (expedientOptional.isPresent())
        		tasca.setExpedient(expedientOptional.get());
        }
        tasca.setNom(tascaDto.getNom());
        tasca.setTitol(tascaDto.getTitol());
        tasca.setDataCreacio(tascaDto.getDataCreacio());
        tasca.setDataFins(tascaDto.getDataFins());
        tasca.setUsuariAssignat(tascaDto.getUsuariAssignat());
        tasca.setIniciFinalitzacio(tascaDto.getIniciFinalitzacio());        
        tasca.setAfagada(tascaDto.isAfagada());
        tasca.setCancelada(tascaDto.isCancelada());
        tasca.setSuspesa(tascaDto.isSuspesa());
        tasca.setCompletada(tascaDto.isCompletada());
        tasca.setAssignada(tascaDto.isAssignada());
        tasca.setMarcadaFinalitzar(tascaDto.isMarcadaFinalitzar());
        tasca.setErrorFinalitzacio(tascaDto.isErrorFinalitzacio());
        tasca.setTascaTramitacioMassiva(tascaDto.isTascaTramitacioMassiva());

        log.debug("[SRV] Validant tasca");
        validateTasca(tasca);

        return tascaMapper.entityToDto(tascaRepository.save(tasca));

    }

    @Override
    @Transactional
    public void delete(
            Long tascaId) {

        log.debug("[SRV] Esborrant la tasca (tascaId=" + tascaId +  ")");

        // TODO: Comprovar permisos sobre tipus d'expedient i entorn ??
        // TODO: Comprovar si el expedient està sent utilitzat en algun camp
        getTascaById(tascaId);
        tascaRepository.delete(tascaId);
    }

    @Override
    @Transactional(readOnly = true)
    public TascaDto getById(
            Long tascaId) {

        log.debug("[SRV] Obtenint tasca amb Id: " + tascaId);

        Tasca tasca = getTascaById(tascaId);
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

    private Tasca getTascaById(Long tascaId) {
        log.debug("Obtenint tasca per id: " + tascaId);
        Optional<Tasca> tascaOptional = tascaRepository.findById(tascaId);

        if (tascaOptional.isPresent()) {
            log.debug("Trobada tasca amb id: " + tascaId);
            return tascaOptional.get();
        } else {
            log.error("[SRV] Delete: No existeix cap tasca amb id=" + tascaId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + tascaId);
        }
    }

    
    private void validateTasca(Tasca tasca) throws ValidationException {
        Map<String, String> errors = new HashMap<>();

        if (tasca.getId() == null || tasca.getId() <= 0L) {
            errors.put("id", "El camp no pot ser null");
        }
        if (tasca.getExpedient() == null) {
            errors.put("expedient", "El camp no pot ser null");        	
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
        if (parametres == null)
            return "";
        return parametres.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", ", "{ ", " }"));
    }

}
