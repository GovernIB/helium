package es.caib.helium.persist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persist.entity.AreaTipus;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa als tipus d'àrea que estan emmagatzemats
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TipusAreaRepository extends JpaRepository<AreaTipus, Long> {

	@Query(	"from AreaTipus t " +
			"where " +
			"    (:esNullFiltre = true or lower(t.codi) like lower('%'||:filtre||'%') "
			+ "		or lower(t.nom) like lower('%'||:filtre||'%'))"
			+ " 	or lower(t.descripcio) like lower('%'||:filtre||'%') ")
	Page<AreaTipus> findByFiltrePaginat(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
	
	public List<AreaTipus> findByEntornId(Long entornId);
	
	public AreaTipus findByCodi(String codi);
	
	public AreaTipus findByEntornIdAndId(Long entornId, Long id);
}
