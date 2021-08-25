package es.caib.helium.expedient.repository;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.criteria.JoinType;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.data.jpa.domain.Specification;

import es.caib.helium.expedient.domain.Tasca;

/** Especificacions pel filtre en les consultes de tasques per poder filtrar.
 * 
 */
public class TascaSpecifications {

    public static Specification<Tasca> belongsToEntorn(Long entornId) {
        return (tasca, cq, cb) -> cb.equal(tasca.<Long> get("expedient").get("entornId"), entornId);
    }
    
    public static Specification<Tasca> belongsToExpedientTipus(Long expedientTipusId) {
        return (tasca, cq, cb) -> cb.equal(tasca.<Long> get("expedient").get("expedientTipusId"), expedientTipusId);
    }
    
	public static Specification<Tasca> usuariAssignatIs(String usuariAssignat) {
        return (tasca, cq, cb) -> cb.equal(tasca.get("usuariAssignat"), usuariAssignat);
	}

	public static Specification<Tasca> nomLike(String nom) {
        return (tasca, cq, cb) -> cb.like(tasca.get("nom"), "%" + nom + "%");
	}
	
	public static Specification<Tasca> titolLike(String titol) {
        return (tasca, cq, cb) -> cb.like(tasca.get("titol"), "%" + titol + "%");
	}

    public static Specification<Tasca> belongsToExpedient(Long expedientId) {
        return (tasca, cq, cb) -> cb.equal(tasca.<Long> get("expedient").get("id"), expedientId);
    }

    
    public static Specification<Tasca> expedientTitolLike(String expedientTitol) {
        
    	return (tasca, cq, cb) -> cb.like(cb.lower(tasca.<Long> get("expedient").get("titol")), "%" + expedientTitol.toLowerCase() + "%" );
    }

    public static Specification<Tasca> expedientNumeroLike(String expedientNumero) {
    	return (tasca, cq, cb) -> cb.like(cb.lower(tasca.<Long> get("expedient").get("numero")), "%" + expedientNumero.toLowerCase() + "%" );
    }

    public static Specification<Tasca> dataCreacio(Date dataCreacioInici, Date dataCreacioFi) {
    	if (dataCreacioInici != null)
    		dataCreacioInici = DateUtils.truncate(dataCreacioInici, Calendar.DATE);
    	if (dataCreacioFi != null) {
    		dataCreacioFi = DateUtils.truncate(DateUtils.addDays(dataCreacioFi, 1), Calendar.DATE);
    	}    			
    	final Date dataInici = dataCreacioInici;
    	final Date dataFi = dataCreacioFi;
    	
    	Specification<Tasca> specs = null;
    	if (dataInici != null && dataFi != null)
    		specs = (tasca, cq, cb) -> cb.between(tasca.get("dataCreacio"), dataInici, dataFi);
		else if (dataInici != null)
			specs = (tasca, cq, cb) -> cb.greaterThanOrEqualTo(tasca.get("dataCreacio"), dataInici);
		else
			specs = (tasca, cq, cb) -> cb.lessThanOrEqualTo(tasca.get("dataCreacio"), dataFi);
    	return specs;
    }
    
    public static Specification<Tasca> dataLimit(Date dataLimitInici, Date dataLimitFi) {
    	if (dataLimitInici != null)
    		dataLimitInici = DateUtils.truncate(dataLimitInici, Calendar.DATE);
    	if (dataLimitFi != null) {
    		dataLimitFi = DateUtils.truncate(DateUtils.addDays(dataLimitFi, 1), Calendar.DATE);
    	}    			
    	final Date dataInici = dataLimitInici;
    	final Date dataFi = dataLimitFi;
    	
    	Specification<Tasca> specs = null;
    	if (dataInici != null && dataFi != null)
    		specs = (tasca, cq, cb) -> cb.between(tasca.get("dataLimit"), dataInici, dataFi);
		else if (dataInici != null)
			specs = (tasca, cq, cb) -> cb.greaterThanOrEqualTo(tasca.get("dataLimit"), dataInici);
		else
			specs = (tasca, cq, cb) -> cb.lessThanOrEqualTo(tasca.get("dataLimit"), dataFi);
    	return specs;
    }

	public static Specification<Tasca> mostrarAssignadesUsuari(String usuariAssignat) {
		return (tasca, cq, cb) -> cb.equal(tasca.get("usuariAssignat"), usuariAssignat);
	}

	/** Busca l'usuari assignat com a un dels responsables */
	public static Specification<Tasca> mostrarAssignadesGrup(String usuariCodi) {
    	if (usuariCodi != null && !usuariCodi.isBlank()) {
	        return (tasca, qb, cb) -> {
	        	return cb.equal(tasca.join("responsables", JoinType.LEFT).get("usuariCodi"), usuariCodi);
	        };
    	} else {
	        return (tasca, qb, cb) -> {
	        	return tasca.join("responsables", JoinType.LEFT).isNotNull();
	        };
    	}
	}

	/** Tasques no suspeses i obertes */
	public static Specification<Tasca> nomesPendents() {
		return (tasca, cq, cb) -> cb.and(
						cb.isFalse(tasca.get("suspesa")),
						cb.isFalse(tasca.get("completada")));
	}


	public static Specification<Tasca> tasquesList(
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
			boolean nomesPendents) {
		
    	Specification<Tasca> spec  = belongsToEntorn(entornId);
    	
    	if (expedientTipusId != null) 
    		spec = spec.and(belongsToExpedientTipus(expedientTipusId));
    	if (titol != null) 
    		spec = spec.and(titolLike(titol));
//    	if (usuariAssignat != null) 
//    		spec = spec.and(usuariAssignatIs(usuariAssignat));
    	if (nom != null) 
    		spec = spec.and(nomLike(nom));
    	if (titol != null) 
    		spec = spec.and(titolLike(titol));
    	if (expedientId != null) 
    		spec = spec.and(belongsToExpedient(expedientId));
    	if (expedientTitol != null) 
    		spec = spec.and(expedientTitolLike(expedientTitol));
    	if (expedientNumero != null) 
    		spec = spec.and(expedientNumeroLike(expedientNumero));
    	if (dataCreacioInici != null || dataCreacioFi != null)
    		spec = spec.and(dataCreacio(dataCreacioInici, dataCreacioFi));
    	if (dataLimitInici != null || dataLimitFi != null)
    		spec = spec.and(dataCreacio(dataLimitInici, dataLimitFi));
//    	if (mostrarAssignadesUsuari)
//    		spec = spec.and(mostrarAssignadesUsuari(usuariAssignat));
//    	if (mostrarAssignadesGrup)
//    		spec = spec.and(mostrarAssignadesGrup(usuariAssignat));
    	if (nomesPendents)
    		spec = spec.and(nomesPendents());
    	
    	return spec;	
    }
}
