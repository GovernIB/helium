/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una alerta que està emmagatzemada a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AlertaRepository extends JpaRepository<Alerta, Long> {	
	@Query(	"from " +
			"    Alerta al " +
			"where " +
			"    al.terminiIniciat.id=:id " +
			"and al.dataEliminacio is null")
	List<Alerta> findActivesAmbTerminiIniciatId(@Param("id") Long id);
	
	@Query(	"select al.expedient.id, " +
			"sum(case when al.dataEliminacio is null then 1 else 0 end) as total, " +
			"sum(case when al.dataEliminacio is null and al.dataLectura is null then 1 else 0 end) as pendents " +
			"from " +
			"    Alerta al " +
			"where " +
			"    al.expedient.id in (:ids) " +
			"group by al.expedient.id" )
	List<Object[]> findByExpedientIds(@Param("ids") List<Long> ids);
	
	List<Alerta> findByExpedientAndDataEliminacioNull(Expedient expedient);

	@Query(	"select count(al)" +
			"from " +
			"    Alerta al " +
			"where " +
			"    al.entorn.id = :entornId " +
			"and al.destinatari = :destinatari " +
			"and al.dataEliminacio is null " +
			"and al.expedient.dataFi is null " +
			"and (:llegides = true and dataLectura is null) " +
			"and (:noLlegides = true and dataLectura is not null) ")
	public int countAmbEntornAndDestinatari(
			@Param("entornId") Long entornId,
			@Param("destinatari") String destinatari,
			@Param("llegides") boolean llegides,
			@Param("noLlegides") boolean noLlegides);

}
