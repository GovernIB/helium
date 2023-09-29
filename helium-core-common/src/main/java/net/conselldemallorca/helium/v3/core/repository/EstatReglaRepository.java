/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QueEnum;
import net.conselldemallorca.helium.core.model.hibernate.EstatRegla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un Repro que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EstatReglaRepository extends JpaRepository<EstatRegla, Long> {

	public List<EstatRegla> findByEstat(Estat estat);

	public List<EstatRegla> findByExpedientTipusAndEstat(ExpedientTipus expedientTipus, Estat estat);

	public List<EstatRegla> findByEstatOrderByOrdreAsc(Estat estat);

	@Query("select count(er) from EstatRegla er where er.estat.id = :estatId ")
	public Long countByEstatId(@Param("estatId") Long estatId);


	@Query("select max(r.ordre) "
			+ "from EstatRegla r "
			+ "where r.estat.id=:estatId")
	public Integer getSeguentOrdre(@Param("estatId") Long estatId);

	@Query("from EstatRegla r where r.estat.id=:estatId and r.nom = :nom")
	EstatRegla findByNom(@Param("estatId") Long estatId, @Param("nom") String nom);


	/** Troba totes les regles que contenenun valor en concret per un tipus d'expedient
	 * 
	 * @param dada Tipus de valor
	 * @param expedientTipusId
	 * @param estatReglaValor Valor a cercar
	 * @return
	 */
	@Query(
			"from EstatRegla regla " +
			"	inner join regla.queValor as valor " +
			"where regla.expedientTipus = :expedientTipus" +
			"		and regla.que =  :que " +
			"		and valor like :estatReglaValor ")
	public List<EstatRegla> findByExpedientTipusAndValor(
			@Param("expedientTipus") ExpedientTipus expedientTipus, 
			@Param("que") QueEnum que, 
			@Param("estatReglaValor") String estatReglaValor);
}
