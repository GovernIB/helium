package es.caib.helium.expedient.repository;

import org.springframework.data.jpa.domain.Specification;

import es.caib.helium.expedient.domain.Expedient;

/** Especificacions pel filtre en les consultes d'expedients.
 * 
 */
public class ExpedientSpecifications {

    public static Specification<Expedient> belongsToEntorn(Long entornId) {
        return (expedient, cq, cb) -> cb.equal(expedient.get("entornId"), entornId);
    }

    public static Specification<Expedient> belongsToExpedientTipus(Long expedientTipus) {
        return (expedient, cq, cb) -> cb.equal(expedient.get("expedientTipus"), expedientTipus);
    }

    public static Specification<Expedient> belongsToExpedientTipus(Long entornId, Long expedientTipusId) {
        return (expedient, cq, cb) -> cb.and(
                cb.equal(expedient.get("entornId"), entornId),
                cb.equal(expedient.get("expedientTipusId"), expedientTipusId)
        );
    }
    
    public static Specification<Expedient> expedients(
            Long entornId,
            Long expedientTipusId) {
    	return belongsToEntorn(entornId);
    }

    public static Specification<Expedient> expedientsList(
            Long entorn,
            Long expedientTipus) {
    	return expedients(entorn, expedientTipus);
    }

}
