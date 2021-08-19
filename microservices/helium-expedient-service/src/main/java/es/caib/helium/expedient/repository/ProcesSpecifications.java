package es.caib.helium.expedient.repository;

import org.springframework.data.jpa.domain.Specification;

import es.caib.helium.expedient.domain.Proces;

/** Especificacions pel filtre en les consultes de processos per poder filtrar.
 * 
 */
public class ProcesSpecifications {


	public static Specification<Proces> processDefinitionIdLike(String processDefinitionId) {
        return (proces, cq, cb) -> cb.like(proces.get("processDefinitionId"), processDefinitionId);
	}

	public static Specification<Proces> processArrelIdLike(String procesArrelId) {
        return (proces, cq, cb) -> cb.like(proces.get("procesArrel").get("id"), procesArrelId);
	}

	public static Specification<Proces> procesList(
			String processDefinitionId,
			String procesArrelId) {
		
    	Specification<Proces> spec  = Specification.where(null);
    	if (processDefinitionId != null) {
    		spec = spec.and(processDefinitionIdLike(processDefinitionId));
    	}
    	if (procesArrelId != null) {
    		spec = spec.and(processArrelIdLike(procesArrelId));
    	}
    	return spec;	
    }

}
