package es.caib.helium.expedient.repository;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.criteria.JoinType;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.data.jpa.domain.Specification;

import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.model.ExpedientEstatTipusEnum;
import es.caib.helium.expedient.model.MostrarAnulatsEnum;

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

    /** L'usuari assignat es correspon amb el passat per paràmetre. */
    public static Specification<Expedient> nomesTasquesPersonals(String usuariCodi) {            	
    	if (usuariCodi != null && !usuariCodi.isBlank()) {
	        return (expedient, qb, cb) -> {
	        	return cb.equal(expedient.join("tasques", JoinType.LEFT).get("usuariAssignat"), usuariCodi);
	        };
    	} else {
	        return (expedient, qb, cb) -> {
	        	return expedient.join("tasques", JoinType.LEFT).get("usuariAssignat").isNotNull();
	        };
    	}
    }

    /** La tasca no té usuari assignat però sí està assignat com a un dels responsables. */
	private static Specification<Expedient> nomesTasquesGrup(String usuariCodi) {
    	if (usuariCodi != null && !usuariCodi.isBlank()) {
            return (expedient, qb, cb) -> {
            	return cb.and(
    	        			expedient.join("tasques", JoinType.LEFT).get("usuariAssignat").isNull(),
    	        			cb.equal(expedient.join("tasques", JoinType.LEFT).join("responsables").get("usuariCodi"), usuariCodi)
            			);
            };
    	} else {
            return (expedient, qb, cb) -> {
            	return cb.and(
    	        			expedient.join("tasques", JoinType.LEFT).get("usuariAssignat").isNull(),
    	        			expedient.join("tasques", JoinType.LEFT).joinList("responsables").isNotNull()
            			);
            };
    	}

	}

	private static Specification<Expedient> nomesErrors() {
		return (expedient, cq, cb) -> cb.isTrue(expedient.get("ambErrors"));
	}

	private static Specification<Expedient> nomesAlertes() {
		return (expedient, cq, cb) -> cb.gt(expedient.get("alertesTotals"), 0L);
	}

	private static Specification<Expedient> mostrarNomesAnulats() {
		return (expedient, cq, cb) -> cb.isTrue(expedient.get("anulat"));
	}
	
	private static Specification<Expedient> noMostrarAnulats() {
		return (expedient, cq, cb) -> cb.isFalse(expedient.get("anulat"));			
	}

    
    
    public static Specification<Expedient> expedientsList(
            String usuariCodi,
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
            MostrarAnulatsEnum mostrarAnulats) {
    	Specification<Expedient> spec  = belongsToEntorn(entornId);
    	if (expedientTipusId != null)
    		spec = spec.and(belongsToExpedientTipus(expedientTipusId));
    	if (titol != null) {
    		spec = spec.and(titolLike(titol));
    	}
    	if (numero != null) {
    		spec = spec.and(numeroLike(numero));
    	}
    	if (dataInici1 != null || dataInici2 != null) {
    		spec = spec.and(dataInici(dataInici1, dataInici2));
    	}
    	if (dataFi1 != null || dataFi2 != null) {
    		spec = spec.and(dataFi(dataFi1, dataFi2));
    	}
    	if (estatTipus != null) {
    		spec = spec.and(estatTipusIs(estatTipus, estatId));
    	}
    	if (nomesTasquesPersonals) {
    		spec = spec.and(nomesTasquesPersonals(usuariCodi));
    	}
    	if (nomesTasquesGrup) {
    		spec = spec.and(nomesTasquesGrup(usuariCodi));
    	}
    	if (nomesAlertes) {
    		spec = spec.and(nomesAlertes());
    	}
    	if (nomesErrors) {
    		spec = spec.and(nomesErrors());
    	}
    	if (mostrarAnulats == null) {
    		spec = spec.and(noMostrarAnulats());
    	} else if (MostrarAnulatsEnum.NOMES_ANULATS.equals(mostrarAnulats)) {
    		spec = spec.and(mostrarNomesAnulats());
    	}
    	return spec;
    }

}
