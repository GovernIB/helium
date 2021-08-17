package es.caib.helium.expedient.service;

import java.util.Collection;
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
import es.caib.helium.expedient.mapper.ExpedientMapper;
import es.caib.helium.expedient.model.ExpedientDto;
import es.caib.helium.expedient.model.ExpedientEstatTipusEnum;
import es.caib.helium.expedient.repository.ExpedientRepository;
import es.caib.helium.expedient.repository.ExpedientSpecifications;
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
public class ExpedientServiceImpl implements ExpedientService {

    private final ExpedientRepository expedientRepository;
    private final ExpedientMapper expedientMapper;

    @Override
    public void importarExpedients(List<Expedient> expedients) throws Exception {

        try {
            //expedientRepository.saveAll(expedients);
            log.info("Important " + expedients.size() + " expedients");
            List<Expedient> expsError = new ArrayList<>();
            for (var exp : expedients) {
                //log.info("Important l'expedient " + exp.toString());
                try {
                    expedientRepository.save(exp);
                } catch (Exception ex) {
                    log.error("Error important l'expedient " + exp, ex);
                    log.info("------------------------------------------------------");
                    expsError.add(exp);
                }
            }
            log.info("Importats " + expedients.size() + " expedients");
            log.info(expsError.size() + " expedients amb error");
        } catch (Exception ex) {
            throw new Exception("Error important expedients", ex);
        }
    }

    @Override
    @Transactional
    public ExpedientDto createExpedient(ExpedientDto expedientDto) {
//            throws PermisDenegatException {

        // TODO: Comprovar permisos sobre tipus d' expedient i entorn ??

        log.debug("[SRV] Creant nou expedient (expedient=" + expedientDto + ")");
        Expedient expedient = expedientMapper.dtoToEntity(expedientDto);
        log.debug("[SRV] Validant expedient");
        validateExpedient(expedient);
        return expedientMapper.entityToDto(expedientRepository.save(expedient));
    }

    @Override
    @Transactional
    public ExpedientDto updateExpedient(
            Long expedientId,
            ExpedientDto expedientDto) {
//            throws NoTrobatException, PermisDenegatException {

        log.debug("[SRV] Modificant el expedient existent (" +
                "expedientId=" + expedientId + ", " +
                "expedient =" + expedientDto + ")");

        // TODO: Comprovar permisos sobre tipus d'expedient i entorn ??

        Expedient expedient = getExpedientById(expedientId);

//        Optional<Expedient> expedientOptional = expedientRepository.findById(expedientId);
//        expedientOptional.ifPresentOrElse(
//                expedient -> {
//                }, () -> {
//                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + expedientId);
//                }
//        );

        expedient.setEntornId(expedientDto.getEntornId());
        expedient.setExpedientTipusId(expedientDto.getExpedientTipusId());
        expedient.setProcessInstanceId(expedientDto.getProcessInstanceId());
        expedient.setNumero(expedientDto.getNumero());
        expedient.setTitol(expedientDto.getTitol());
        expedient.setDataInici(expedientDto.getDataInici());
        expedient.setDataFi(expedientDto.getDataFi());
        expedient.setEstatTipus(expedientDto.getEstatTipus());
        expedient.setEstatId(expedientDto.getEstatId());
        expedient.setAturat(expedientDto.isAturat());
        expedient.setInfoAturat(expedientDto.getInfoAturat());
        expedient.setAnulat(expedientDto.isAnulat());
        expedient.setComentariAnulat(expedientDto.getComentariAnulat());
        expedient.setAlertesTotals(expedientDto.getAlertesTotals());
        expedient.setAlertesPendents(expedientDto.getAlertesPendents());
        expedient.setAmbErrors(expedientDto.isAmbErrors());

        log.debug("[SRV] Validant expedient");
        validateExpedient(expedient);

        return expedientMapper.entityToDto(expedientRepository.save(expedient));

    }

    @Override
    @Transactional
    public void delete(
            Long expedientId) {

        log.debug("[SRV] Esborrant el expedient (expedientId=" + expedientId +  ")");

        // TODO: Comprovar permisos sobre tipus d'expedient i entorn ??
        // TODO: Comprovar si el expedient està sent utilitzat en algun camp
        getExpedientById(expedientId);
        expedientRepository.delete(expedientId);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpedientDto getById(
            Long expedientId) {

        log.debug("[SRV] Obtenint expedient amb Id: " + expedientId);

        Expedient expedient = getExpedientById(expedientId);
        return expedientMapper.entityToDto(expedient);

    }

    @Override
    @Transactional(readOnly = true)
    public PagedList<ExpedientDto> listExpedients(
    		String usuariCodi,
    		Long entornId,
            Long expedientTipusId,
            Collection<Long> tipusIdPermesos,
            String titol,
            String numero,
            Date dataInici1,
            Date dataInici2,
            Date dataFi1,
            Date dataFi2,
            boolean nomesIniciats,
            boolean nomesFinalitzats,
            Long estatId,
            boolean nomesTasquesPersonals,
            boolean nomesTasquesGrup,
            boolean nomesAlertes,
            boolean nomesErrors,
            boolean mostrarAnulats,
            boolean mostrarNomesAnulats,
            String filtreRsql,
            final Pageable pageable,
            final Sort sort) {

        log.debug("[SRV] Obtenint llistat de expedients. \n" +
                "usuariCodi: " + usuariCodi +
                "entornId: " + entornId + "\n" +
                "expedientTipusId: " + expedientTipusId + "\n" +
                "tipusIdPermesos: " + tipusIdPermesos + "\n" +
                "titol: " + titol + "\n" +
                "numero: " + numero + "\n" +
                "dataInici1: " + dataInici1 + "\n" +
                "dataInici2: " + dataInici2 + "\n" +
                "dataFi1: " + dataFi1 + "\n" +
                "dataFi2: " + dataFi2 + "\n" +
                "nomesIniciats: " + nomesIniciats + "\n" +
                "nomesFinalitzats: " + nomesFinalitzats + "\n" +
                "estatId: " + estatId + "\n" +
                "nomesTasquesPersonals: " + nomesTasquesPersonals + "\n" +
                "nomesTasquesGrup: " + nomesTasquesGrup + "\n" +
                "expedientTipusId: " + nomesAlertes + "\n" +
                "nomesErrors: " + nomesErrors + "\n" +
                "mostrarAnulats: " + mostrarAnulats + "\n" +
                "mostrarNomesAnulats: " + mostrarNomesAnulats + "\n" +
                "filtreRsql:" + filtreRsql);

        Specification<Expedient> spec = ExpedientSpecifications.expedientsList(
        									usuariCodi,
											entornId,
											expedientTipusId,
											tipusIdPermesos,
											titol, 
											numero, 
											dataInici1, 
											dataInici2, 
											dataFi1, 
											dataFi2, 
											nomesIniciats,
											nomesFinalitzats,
											estatId, 
											nomesTasquesPersonals, 
											nomesTasquesGrup, 
											nomesAlertes, 
											nomesErrors, 
											mostrarAnulats,
											mostrarNomesAnulats
        								);

        PagedList<ExpedientDto> pagedList = ServiceHelper.getDtoPage(
                expedientRepository,
                spec,
                filtreRsql,
                pageable,
                sort,
                ExpedientDto.class,
                expedientMapper);
        
        return pagedList;
    }

    private Expedient getExpedientById(Long expedientId) {
        log.debug("Obtenint expedient per id: " + expedientId);
        Optional<Expedient> expedientOptional = expedientRepository.findById(expedientId);

        if (expedientOptional.isPresent()) {
            log.debug("Trobat expedient amb id: " + expedientId);
            return expedientOptional.get();
        } else {
            log.error("[SRV] Delete: No existeix cap expedient amb id=" + expedientId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + expedientId);
        }
    }

    
    private void validateExpedient(Expedient expedient) throws ValidationException {
        Map<String, String> errors = new HashMap<>();

        if (expedient.getId() == null || expedient.getId() <= 0L) {
            errors.put("id", "El camp no pot ser null");
        }
        if (expedient.getEntornId() == null || expedient.getEntornId() <= 0L) {
            errors.put("entornId", "El camp no pot ser null");
        }
        if (expedient.getExpedientTipusId() == null || expedient.getExpedientTipusId() <= 0L) {
            errors.put("expedientTipusId", "El camp no pot ser null");
        }
        if (expedient.getProcessInstanceId() == null || expedient.getProcessInstanceId().isBlank()) {
            errors.put("processInstanceId", "El camp no pot ser null");
        }
        if (expedient.getNumero() == null || expedient.getNumero().isBlank())
            errors.put("numero", "El camp no pot ser null");
        if (expedient.getDataInici() == null ) {
            errors.put("dataInici", "El camp no pot ser null");
        }
        if (expedient.getEstatTipus() == null ) {
            errors.put("estatTipus", "El camp no pot ser null");
        } else if (ExpedientEstatTipusEnum.CUSTOM.equals(expedient.getEstatTipus()) 
        		&& expedient.getEstatId() == null) {
            errors.put("estatId", "L'estat ID no pot ser null si el tipus d'estat és un estat específic.");        	
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Error de validació del expedient: " + parametresToString(errors));
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
