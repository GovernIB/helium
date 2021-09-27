package es.caib.helium.expedient.repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.data.jpa.domain.Specification;

import es.caib.helium.expedient.domain.Tasca;

/** Especificacions pel filtre en les consultes de tasques per poder filtrar.
 * 
 */
public class TascaSpecifications {

    public static Specification<Tasca> belongsToEntorn(Long entornId) {
        return (tasca, cq, cb) -> cb.equal(tasca.<Long> get("proces").get("expedient").get("entornId"), entornId);
    }
    
    public static Specification<Tasca> belongsToExpedientTipus(Long expedientTipusId) {
        return (tasca, cq, cb) -> cb.equal(tasca.<Long> get("proces").get("expedient").get("expedientTipusId"), expedientTipusId);
    }
    
    /** L'usuari responsable està directament assignat, o existeix en la llista de responsables o 
     * algun del seus rols està a la llista de grups.
     */
	public static Specification<Tasca> responsableIs(String responsable, List<String> grups) {
		
		return (tasca, cq, cb) -> {
			// Subquery per responsables
            Subquery<Tasca> sq = cq.subquery(Tasca.class);
            Root<Tasca> sqTasca = sq.from(Tasca.class);
            sq.select(sqTasca.get("id"));
            List<Predicate> orPredicates = new ArrayList<Predicate>();
            if (responsable != null) {
            	orPredicates.add(cb.equal(sqTasca.get("usuariAssignat"), responsable));
            	orPredicates.add(cb.equal(sqTasca.join("responsables", JoinType.LEFT).get("usuariCodi"), responsable));
            }
            if (grups != null && !grups.isEmpty()) {
            	orPredicates.add(sqTasca.join("grups", JoinType.LEFT).get("grupCodi").in(grups));
            }
            sq.where(cb.or(orPredicates.toArray(Predicate[]::new)));
			return cb.and(tasca.get("id").in(sq));			
		};	
	}

	public static Specification<Tasca> nomLike(String nom) {
        return (tasca, cq, cb) -> cb.like(tasca.get("nom"), "%" + nom + "%");
	}
	
	public static Specification<Tasca> titolLike(String titol) {
        return (tasca, cq, cb) -> cb.like(tasca.get("titol"), "%" + titol + "%");
	}

    public static Specification<Tasca> belongsToExpedient(Long expedientId) {
        return (tasca, cq, cb) -> cb.equal(tasca.<Long> get("proces").get("expedient").get("id"), expedientId);
    }

    
    public static Specification<Tasca> expedientTitolLike(String expedientTitol) {
        
    	return (tasca, cq, cb) -> cb.like(cb.lower(tasca.<Long> get("proces").get("expedient").get("titol")), "%" + expedientTitol.toLowerCase() + "%" );
    }

    public static Specification<Tasca> expedientNumeroLike(String expedientNumero) {
    	return (tasca, cq, cb) -> cb.like(cb.lower(tasca.<Long> get("proces").get("expedient").get("numero")), "%" + expedientNumero.toLowerCase() + "%" );
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

	public static Specification<Tasca> noMostrarAssignadesUsuari() {
		return (tasca, cq, cb) -> cb.isNull(tasca.get("usuariAssignat"));
	}

	/** Filtra les tasques assignades a diferents usuaris o a diferents grups */
	public static Specification<Tasca> noMostrarAssignadesGrup() {
	    return (tasca, qb, cb) -> cb.and(
	    	    	tasca.join("responsables", JoinType.LEFT).isNull(),
	    	    	tasca.join("grups", JoinType.LEFT).isNull());
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
			boolean nomesPendents) {
		
    	Specification<Tasca> spec  = belongsToEntorn(entornId);
    	
    	if (expedientTipusId != null) 
    		spec = spec.and(belongsToExpedientTipus(expedientTipusId));
    	if (titol != null) 
    		spec = spec.and(titolLike(titol));
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
    	if (responsable != null || (grups != null && !grups.isEmpty())) 
    		spec = spec.and(responsableIs(responsable, grups));
    	if (!mostrarAssignadesUsuari)
    		spec = spec.and(noMostrarAssignadesUsuari());
    	if (!mostrarAssignadesGrup)
    		spec = spec.and(noMostrarAssignadesGrup());
    	if (nomesPendents)
    		spec = spec.and(nomesPendents());
    	
    	return spec;	
    }

    /** Tasques de processos */
	public static Specification<Tasca> inProcesses(List<Long> procesIds) {
		return (tasca, cq, cb) -> tasca.get("proces").get("id").in(procesIds);
	}
}
