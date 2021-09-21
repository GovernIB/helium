package es.caib.helium.expedient.repository;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.JoinType;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.data.jpa.domain.Specification;

import es.caib.helium.expedient.domain.Expedient;
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

    public static Specification<Expedient> inTipusIdPermesos(Collection<Long> tipusIdPermesos) {
    	return (expedient, cq, cb) -> expedient.get("expedientTipusId").in(tipusIdPermesos);
    }

    public static Specification<Expedient> titolLike(String titol) {
        return (expedient, cq, cb) -> cb.like(cb.lower(expedient.get("titol")), "%" + titol.toLowerCase() + "%");
    }

    public static Specification<Expedient> numeroLike(String numero) {
        return (expedient, cq, cb) -> cb.like(expedient.get("numero"), "%" + numero + "%");
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
    
    public static Specification<Expedient> estatIs(
    		boolean nomesIniciats,
    		boolean nomesFinalitzats,
    		Long estatId) {
    	Specification<Expedient> specs = null;
    	if (nomesIniciats) {
    		
    	} else if (nomesFinalitzats) {
    		
    	} else if (estatId != null) {
    		
    	}
    	if (estatId != null) {
    		specs = (expedient, cq, cb) -> cb.equal(expedient.get("estatId"), estatId);
    	} else if (nomesIniciats || nomesFinalitzats) {
    		ExpedientEstatTipusEnum estatTipus = nomesIniciats ? 
    				ExpedientEstatTipusEnum.INICIAT 
    				: ExpedientEstatTipusEnum.FINALITZAT;
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
    public static Specification<Expedient> nomesTasquesGrup(String usuariCodi) {
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
    
	public static Specification<Expedient> nomesTasquesMevesPersonalsGrup(
			String usuariCodi, 
			boolean nomesTasquesMeves,
			boolean nomesTasquesPersonals, 
			boolean nomesTasquesGrup) {
		Specification<Expedient> ret;
		if (!nomesTasquesMeves) {
			usuariCodi = null;
		}
		if (nomesTasquesPersonals) {
			ret = nomesTasquesPersonals(usuariCodi);
		} else {
			ret = nomesTasquesGrup(usuariCodi);
		}
		return ret;
	}


    public static Specification<Expedient> nomesErrors() {
		return (expedient, cq, cb) -> cb.isTrue(expedient.get("ambErrors"));
	}

    public static Specification<Expedient> nomesAlertes() {
		return (expedient, cq, cb) -> cb.gt(expedient.get("alertesTotals"), 0L);
	}

    public static Specification<Expedient> mostrarNomesAnulats() {
		return (expedient, cq, cb) -> cb.isTrue(expedient.get("anulat"));
	}
	
    public static Specification<Expedient> noMostrarAnulats() {
		return (expedient, cq, cb) -> cb.isFalse(expedient.get("anulat"));			
	}

    
    
    public static Specification<Expedient> expedientsList(
            String usuariCodi,
            List<String> grups,
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
            boolean nomesTasquesMeves,
            boolean nomesTasquesGrup, 
            boolean nomesAlertes, 
            boolean nomesErrors, 
            boolean mostrarAnulats,
            boolean mostrarNomesAnulats) {
    	Specification<Expedient> spec  = belongsToEntorn(entornId);
    	if (expedientTipusId != null) {
    		spec = spec.and(belongsToExpedientTipus(expedientTipusId));
    	} else if (tipusIdPermesos != null && tipusIdPermesos.size() > 0) {
    		spec = spec.and(inTipusIdPermesos(tipusIdPermesos));
    	}
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
    	if (nomesIniciats || nomesFinalitzats || estatId != null) {
    		spec = spec.and(estatIs(nomesIniciats, nomesFinalitzats, estatId));
    	}
    	if (nomesTasquesPersonals || nomesTasquesGrup) {
    		spec = spec.and(nomesTasquesMevesPersonalsGrup(
			    				usuariCodi, 
			    				nomesTasquesMeves, 
			    				nomesTasquesPersonals, 
			    				nomesTasquesGrup));
    	}
    	if (nomesAlertes) {
    		spec = spec.and(nomesAlertes());
    	}
    	if (nomesErrors) {
    		spec = spec.and(nomesErrors());
    	}
    	if (mostrarAnulats || mostrarNomesAnulats) {
    		if (mostrarNomesAnulats) {
        		spec = spec.and(mostrarNomesAnulats());
    		}
    	} else {
    		spec = spec.and(noMostrarAnulats());
    	}
    	return spec;
    }
}
