/**
 * 
 */
package es.caib.helium.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.commons.dto.regles.QueEnum;
import es.caib.helium.persistence.entity.Estat;
import es.caib.helium.persistence.entity.EstatRegla;
import es.caib.helium.persistence.entity.ExpedientTipus;

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

	public List<EstatRegla> findByExpedientTipusAndEstatIsNullOrderByOrdreAsc(ExpedientTipus expedientTipus);

	public List<EstatRegla> findByEstatOrderByOrdreAsc(Estat estat);

	@Query("select count(er) from EstatRegla er where er.estat.id = :estatId ")
	public Long countByEstatId(@Param("estatId") Long estatId);


	@Query("select max(r.ordre) "
			+ "from EstatRegla r "
			+ "where r.estat.id=:estatId")
	public Integer getSeguentOrdre(@Param("estatId") Long estatId);

	@Query("from EstatRegla r where r.estat.id=:estatId and r.nom = :nom")
	EstatRegla findByNom(@Param("estatId") Long estatId, @Param("nom") String nom);
	
	@Query("from EstatRegla r where r.expedientTipus.id=:expedientTipusId and r.nom = :nom and r.estat is null")
	EstatRegla findByTipusExpedientNomAndEstatIsNull(@Param("expedientTipusId") Long expedientTipusId, @Param("nom") String nom);


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
