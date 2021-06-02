package es.caib.helium.expedient.repository;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.criteria.Join;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.data.jpa.domain.Specification;

import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.domain.Tasca;
import es.caib.helium.expedient.model.ExpedientEstatTipusEnum;

/** Especificacions pel filtre en les consultes d'expedients per poder filtrar.
 * 
 */
public class ExpedientSpecifications {

    public static Specification<Expedient> belongsToEntorn(Long entornId) {
        return (expedient, cq, cb) -> cb.equal(expedient.get("entornId"), entornId);
    }
    
    public static Specification<Expedient> belongsToExpedientTipus(Long expedientTipusId) {
        return (expedient, cq, cb) -> cb.equal(expedient.get("expedientTipusId"), expedientTipusId);
    }
    
    public static Specification<Expedient> titolLike(String titol) {
        return (expedient, cq, cb) -> cb.like(expedient.get("titol"), titol);
    }

    public static Specification<Expedient> numeroLike(String numero) {
        return (expedient, cq, cb) -> cb.like(expedient.get("numero"), numero);
    }

    public static Specification<Expedient> dataInici(Date dataInici1, Date dataInici2) {
    	if (dataInici1 != null)
    		dataInici1 = DateUtils.truncate(dataInici1, Calendar.DATE);
    	if (dataInici2 != null) {
    		dataInici2 = DateUtils.truncate(DateUtils.addDays(dataInici2, 1), Calendar.DATE);
    	}    			
    	final Date dataInici = dataInici1;
    	final Date dataFi = dataInici2;
    	
    	Specification<Expedient> specs = null;
    	if (dataInici != null && dataFi != null)
    		specs = (expedient, cq, cb) -> cb.between(expedient.get("dataInici"), dataInici, dataFi);
		else if (dataInici != null)
			specs = (expedient, cq, cb) -> cb.greaterThanOrEqualTo(expedient.get("dataInici"), dataInici);
		else
			specs = (expedient, cq, cb) -> cb.lessThanOrEqualTo(expedient.get("dataInici"), dataFi);
    	return specs;
    }

    public static Specification<Expedient> dataFi(Date dataFi1, Date dataFi2) {
    	if (dataFi1 != null)
    		dataFi1 = DateUtils.truncate(dataFi1, Calendar.DATE);
    	if (dataFi2 != null) {
    		dataFi2 = DateUtils.truncate(DateUtils.addDays(dataFi2, 1), Calendar.DATE);
    	}    			
    	final Date dataInici = dataFi1;
    	final Date dataFi = dataFi2;
    	
    	Specification<Expedient> specs = null;
    	if (dataInici != null && dataFi != null)
    		specs = (expedient, cq, cb) -> cb.between(expedient.get("dataInici"), dataInici, dataFi);
		else if (dataInici != null)
			specs = (expedient, cq, cb) -> cb.greaterThanOrEqualTo(expedient.get("dataInici"), dataInici);
		else
			specs = (expedient, cq, cb) -> cb.lessThanOrEqualTo(expedient.get("dataInici"), dataFi);
    	return specs;
    }
    
    public static Specification<Expedient> estatTipusIs(ExpedientEstatTipusEnum estatTipus, Long estatId) {
    	Specification<Expedient> specs = null;
    	if (estatTipus.equals(ExpedientEstatTipusEnum.CUSTOM)) {
    		specs = (expedient, cq, cb) -> cb.equal(expedient.get("estatId"), estatId);
    	} else {
    		specs = (expedient, cq, cb) -> cb.equal(expedient.get("estatTipus"), estatTipus);    		
    	}
    	return specs;
    }

    public static Specification<Expedient> nomesTasquesPersonals() {        
        return (expedient, query, criteriaBuilder) -> {
            Join<Tasca, Expedient> tascaJoin = query.from(Tasca.class).join("tasques");
            return criteriaBuilder.isNotNull(tascaJoin.get("usuariAssignat"));
        };        
    }

	private static Specification<Expedient> nomesTasquesGrup() {
    	//TODO DANIEL: relacionar amb les tasques i construir la especificació correcta
        return (expedient, cq, cb) -> {
            Join<Tasca, Expedient> tascaJoin = cq.from(Tasca.class).join("tasques");
            return cb.isNull(tascaJoin.get("usuariAssignat"));
        };        
	}

	private static Specification<Expedient> nomesErrors() {
		return (expedient, cq, cb) -> cb.isTrue(expedient.get("ambErrors"));
	}

	private static Specification<Expedient> nomesAlertes() {
		return (expedient, cq, cb) -> cb.gt(expedient.get("alertesTotals"), 0L);
	}

	private static Specification<Expedient> mostrarAnulats(Boolean mostrarAnulats) {
		if (mostrarAnulats.booleanValue()) {
			return (expedient, cq, cb) -> cb.isTrue(expedient.get("anulat"));
		} else {
			return (expedient, cq, cb) -> cb.isFalse(expedient.get("anulat"));			
		}
	}

    
    
    public static Specification<Expedient> expedientsList(
            Long entornId,
            Long expedientTipusId, 
            String titol, 
            String numero, 
            Date dataInici1, 
            Date dataInici2, 
            Date dataFi1, 
            Date dataFi2, 
            ExpedientEstatTipusEnum estatTipus, 
            Long estatId, 
            boolean nomesTasquesPersonals, 
            boolean nomesTasquesGrup, 
            boolean nomesAlertes, 
            boolean nomesErrors, 
            Boolean mostrarAnulats) {
    	Specification<Expedient> spec  = belongsToEntorn(entornId);
    	if (expedientTipusId != null)
    		spec = spec.and(belongsToExpedientTipus(expedientTipusId));
    	if (titol != null) {
    		spec = spec.and(titolLike(titol));
    	}
    	if (numero != null) {
    		spec = spec.and(numeroLike(numero));
    	}
    	if (dataInici1 != null || dataInici2 != null)
    		spec = spec.and(dataInici(dataInici1, dataInici2));
    	if (dataFi1 != null || dataFi2 != null)
    		spec = spec.and(dataFi(dataFi1, dataFi2));
    	if (estatTipus != null)
    		spec = spec.and(estatTipusIs(estatTipus, estatId));
    	if (nomesTasquesPersonals)
    		spec = spec.and(nomesTasquesPersonals());
    	if (nomesTasquesGrup)
    		spec = spec.and(nomesTasquesGrup());
    	if (nomesAlertes)
    		spec = spec.and(nomesAlertes());
    	if (nomesErrors)
    		spec = spec.and(nomesErrors());
    	if (mostrarAnulats != null)
    		spec = spec.and(mostrarAnulats(mostrarAnulats));
    	
    	return spec;
    }

}
