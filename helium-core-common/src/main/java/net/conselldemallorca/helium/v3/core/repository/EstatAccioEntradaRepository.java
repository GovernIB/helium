/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.EstatAccioEntrada;
import net.conselldemallorca.helium.core.model.hibernate.Validacio;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a la relació entre estats i les acccions d'entrada per
 * expedients basats en l'execució per estats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EstatAccioEntradaRepository extends JpaRepository<EstatAccioEntrada, Long> {

	@Query("select count(ea) from EstatAccioEntrada ea where ea.estat.id = :estatId ")
	public Long countByEstatId(@Param("estatId") Long estatId);

	@Query(	"from EstatAccioEntrada eae " +
			"where " +
			"   eae.estat.id = :estatId " +
			"	and (:esNullFiltre = true " +
			"			or lower(eae.estat.nom) like lower('%'||:filtre||'%') " +
			"			or lower(eae.accio.nom) like lower('%'||:filtre||'%')) ")
	Page<Validacio> findByFiltrePaginat(
			@Param("estatId") Long estatId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);

	@Query("select max(ea.ordre) "
			+ "from EstatAccioEntrada ea "
			+ "where ea.estat.id=:estatId")
	public Integer getSeguentOrdre(@Param("estatId") Long estatId);

	public List<EstatAccioEntrada> findByEstatOrderByOrdreAsc(Estat estat);
}