/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Festiu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un festiu que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FestiuRepository extends JpaRepository<Festiu, Long> {
	Festiu findByData(Date data);
	
	@Query("from Festiu f where year(f.data) = :any")
	List<Festiu> findByAny(@Param("any") int any);
}
