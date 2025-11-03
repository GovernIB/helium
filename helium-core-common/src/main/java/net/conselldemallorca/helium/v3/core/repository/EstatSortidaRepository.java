/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.EstatSortida;
import net.conselldemallorca.helium.core.model.hibernate.EstatSortidaId;

/**
 * Repositori pels estats de sortida que poden tenir un estat pels expedients de tipus estat. Cada registre
 * especifica l'estat d'origen y l'estat de sortida següent.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EstatSortidaRepository extends JpaRepository<EstatSortida, EstatSortidaId> {

	/** Troba els estats de sortida per un estat específic. */
	@Query("select es.estatSeguent " +
			"from EstatSortida es " +
			"where es.estat.id = :estatId " + 
			"order by es.estatSeguent.ordre asc "
	)
	public List<Estat> findEstatsSortidaByEstatId(@Param("estatId") long estatId);

	/** Cerca una entrada a la taula d'estats de sortida per estat i per l'estat de sortida següent
	 * 
	 * @param estat
	 * @param estatSeguent
	 * @return
	 */
	@Query("select es " +
			"from EstatSortida es " +
			"where es.estat = :estat and es.estatSeguent = :estatSeguent"
	)
	public EstatSortida findByEstatAndEstatSeguent(
			@Param("estat") Estat estat, 
			@Param("estatSeguent") Estat estatSeguent);
}
