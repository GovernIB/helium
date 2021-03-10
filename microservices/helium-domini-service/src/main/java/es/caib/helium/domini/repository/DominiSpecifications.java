package es.caib.helium.domini.repository;

import es.caib.helium.domini.domain.Domini;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

public class DominiSpecifications {

    public static Specification<Domini> belongsToEntorn(Long entorn) {
        return (domini, cq, cb) -> cb.equal(domini.get("entorn"), entorn);
    }

    public static Specification<Domini> belongsToExpedientTipus(Long expedientTipus) {
        return (domini, cq, cb) -> cb.equal(domini.get("expedientTipus"), expedientTipus);
    }

    public static Specification<Domini> belongsToExpedientTipus(Long entorn, Long expedientTipus) {
        return (domini, cq, cb) -> cb.and(
                cb.equal(domini.get("entorn"), entorn),
                cb.equal(domini.get("expedientTipus"), expedientTipus)
        );
    }
    
//    public static Specification<Domini> isGlobal() {
//        return (domini, cq, cb) -> cb.isNull(domini.get("expedientTipus"));
//    }

    public static Specification<Domini> isGlobal(Long entorn) {
        return (domini, cq, cb) -> cb.and(
                cb.equal(domini.get("entorn"), entorn),
                cb.isNull(domini.get("expedientTipus"))
        );
    }

    public static Specification<Domini> belongsToExpedientTipusOrIsGlobal(Long entorn, Long expedientTipus) {
        return (domini, cq, cb) ->
                cb.and(
                        cb.equal(domini.get("entorn"), entorn),
                        cb.or(
                                cb.equal(domini.get("expedientTipus"), expedientTipus),
                                cb.isNull(domini.get("expedientTipus"))
                        )
                );
    }

//    public static Specification<Domini> isSobreescrit(Long expedientTipus, Long expedientTipusPare) {
//        return (domini, cq, cb) -> {
//            Root<Domini> domini2 = cq.from(Domini.class);
//            return cb.and(
//                    cb.equal(domini.get("codi"), domini2.get("codi")),
//                    cb.equal(domini.get("expedientTipus"), expedientTipus),
//                    cb.equal(domini2.get("expedientTipus"), expedientTipusPare)
//            );
//        };
//    }

    public static Specification<Domini> dominisAmbHerencia(
            Long entorn,
            Long expedientTipus,
            Long expedientTipusPare,
            boolean incloureGlobals) {
        return (domini, cq, cb) -> {
            Subquery<Domini> sq = cq.subquery(Domini.class);
            Root<Domini> sqDomini1 = sq.from(Domini.class);
            Root<Domini> sqDomini2 = sq.from(Domini.class);

            sq.select(sqDomini2.get("id"));
            Predicate sobreescrits = cb.and(
                    cb.equal(sqDomini1.get("codi"), sqDomini2.get("codi")),
                    cb.equal(sqDomini1.get("expedientTipus"), expedientTipus),
                    cb.equal(sqDomini2.get("expedientTipus"), expedientTipusPare)
            );
            sq.where(sobreescrits);
            Predicate dominiId = cb.not(domini.get("id").in(sq));
            Predicate expTipus = cb.or(
                    cb.equal(domini.get("expedientTipus"), expedientTipus),
                    cb.equal(domini.get("expedientTipus"), expedientTipusPare));
            if (incloureGlobals)
                expTipus = cb.or(expTipus, cb.isNull(domini.get("expedientTipus")));

            return cb.and(
                    cb.equal(domini.get("entorn"), entorn),
                    dominiId,
                    expTipus
            );
        };
    }

    public static Specification<Domini> dominisSenseHerencia(
            Long entorn,
            Long expedientTipus,
            boolean incloureGlobals) {
        if (incloureGlobals) {
            if (expedientTipus == null)
                return belongsToEntorn(entorn);
            return belongsToExpedientTipusOrIsGlobal(entorn, expedientTipus);
        } else if (expedientTipus == null) {
            return isGlobal(entorn);
        } else {
            return belongsToExpedientTipus(entorn, expedientTipus);
        }
    }

    public static Specification<Domini> dominisList(
            Long entorn,
            Long expedientTipus,
            Long expedientTipusPare,
            boolean incloureGlobals) {
        if (expedientTipusPare != null)
            return dominisAmbHerencia(entorn, expedientTipus, expedientTipusPare, incloureGlobals);
        else
            return dominisSenseHerencia(entorn, expedientTipus, incloureGlobals);
    }

}
