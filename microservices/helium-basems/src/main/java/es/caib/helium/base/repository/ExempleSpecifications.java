package es.caib.helium.base.repository;

import es.caib.helium.base.domain.Exemple;
import org.springframework.data.jpa.domain.Specification;

public class ExempleSpecifications {

    public static Specification<Exemple> isNomLike(String nom) {
        return (base, cq, cb) -> cb.like(cb.lower(base.get("nom")), "%" + nom.toLowerCase() + "%");
    }

    public static Specification<Exemple> isNomLikeAndCodiEquals(String nom, String codi) {
        return (base, cq, cb) -> cb.and(
                isNomLike(nom).toPredicate(base, cq, cb),
                cb.equal(base.get("codi"), codi)
        );
    }
    
}
