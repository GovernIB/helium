/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persist.entity.Consulta;
import es.caib.helium.persist.entity.ConsultaCamp;
import es.caib.helium.persist.entity.ConsultaCamp.TipusConsultaCamp;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un camp que està emmagatzemada a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConsultaCampRepository extends JpaRepository<ConsultaCamp, Long> {

	@Query("select cc " +
			"from ConsultaCamp cc " +
			"where cc.consulta.id = :consultaId " +
			"and cc.tipus = :tipus " +
			"order by cc.ordre asc ")
	List<ConsultaCamp> findCampsConsulta(
			@Param("consultaId") Long consultaId, 
			@Param("tipus") TipusConsultaCamp tipus);

	@Query("from ConsultaCamp cc " +
			"where " +
			"    cc.consulta.id = :consultaId " +
			" and cc.tipus = :tipus " +
			"order by " +
			"    cc.ordre")
	List<ConsultaCamp> findAmbConsultaIdOrdenats(
			@Param("consultaId") Long consultaId, 
			@Param("tipus") TipusConsultaCamp tipus);

	
	@Query(	"from ConsultaCamp cc " +
			"where " +
			"   cc.consulta.id = :consultaId " +
			"   and cc.tipus = :tipus ")
	Page<ConsultaCamp> findByFiltrePaginat(
			@Param("consultaId") Long consultaId,
			@Param("tipus") TipusConsultaCamp tipus,
			Pageable pageable);

	/** Consulta el següent valor per a ordre de les agrupacions. */
	@Query(	"select coalesce( max( cc.ordre), -1) + 1 " +
			"from ConsultaCamp cc " +
			"where " +
			"    cc.consulta.id = :consultaId " +
			"   and cc.tipus = :tipus " )
	Integer getNextOrdre(@Param("consultaId") Long consultaId,
			@Param("tipus") TipusConsultaCamp tipus);

	
	/** Mètode per trobar un camp de la consulta per consulta, codi i tipus per validar la repetició. */
	ConsultaCamp findByConsultaAndTipusAndCampCodi(
			Consulta consulta,
			TipusConsultaCamp tipus, 
			String campCodi);	
	
	/** Mètode per consultar totes les consultes que contenen un camp determinat per un tipus d'expedient
	 * 
	 * @param expedientTipusId
	 * @param campCodi
	 * @param defProckey
	 * @param defProcVersio
	 * @return
	 */
	@Query(	"from ConsultaCamp cc " +
			"where cc.consulta.expedientTipus.id = :expedientTipusId " +
			"	and cc.campCodi = :campCodi " +
			"	and ((cc.defprocJbpmKey is null and :defProckey is null) or (cc.defprocJbpmKey = :defProckey)) " +
			" 	and cc.defprocVersio = :defProcVersio ")
	List<ConsultaCamp> findPerCamp(
			@Param("expedientTipusId") Long expedientTipusId, 
			@Param("campCodi") String campCodi,
			@Param("defProckey") String defProckey,
			@Param("defProcVersio") int defProcVersio);
}
