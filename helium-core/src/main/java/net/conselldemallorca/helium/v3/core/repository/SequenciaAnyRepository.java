/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;

/**
 * Repositori de la classe SequenciaAny que defineix una seqüència per als 
 * expedients d'un tipus d'expedient per a un any concret.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SequenciaAnyRepository extends JpaRepository<SequenciaAny, Long> {

//	List<SequenciaAny> findByEntorn(Entorn entorn);
//	
//	List<Long> findIdByEntornId(Long entornId);
//	
//	List<SequenciaAny> findByEntornOrderByNomAsc(Entorn entorn);
//	
//	List<SequenciaAny> findByEntornOrderByCodiAsc(Entorn entorn);
//
//	SequenciaAny findByEntornAndCodi(Entorn entorn, String codi);
//
//	SequenciaAny findByEntornAndId(Entorn entorn, Long id);
//
//	SequenciaAny findById(Long expedientTipusId);
//
//	/*Entitat findByCif(String cif);
//
//	@Query(	"select " +
//			"    eu " +
//			"from " +
//			"    EntitatUsuari eu " +
//			"where " +
//			"    eu.entitat.id = ?1 " +
//			"and eu.usuari.nif = ?2")
//	EntitatUsuari findUsuariAmbNif(Long id, String nif);*/
//	
//	@Query(	"from SequenciaAny e " +
//			"where " +
//			"    e.entorn = :entorn " +
//			"and e.id in (:tipusPermesosIds) " +
//			"and (:esNullFiltre = true or lower(e.nom) like lower('%'||:filtre||'%') or lower(e.codi) like lower('%'||:filtre||'%')) ")
//	Page<Expedient> findByFiltreGeneralPaginat(
//			@Param("entorn") Entorn entorn,
//			@Param("tipusPermesosIds") Collection<Long> tipusPermesosIds,
//			@Param("esNullFiltre") boolean esNullFiltre,
//			@Param("filtre") String filtre,		
//			Pageable pageable);

}
