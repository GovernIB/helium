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

import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;

/**
 * Repositori per gestionar una entitat de base de dades del tipus unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UnitatOrganitzativaRepository extends JpaRepository<UnitatOrganitzativa, Long> {
	
	List<UnitatOrganitzativa> findByCodiUnitatArrel(String codi);
	
	List<UnitatOrganitzativa> findByCodiUnitatSuperior(String codi);
		
	List<UnitatOrganitzativa> findByCodiOrderByDenominacioAsc(String codi);
	
	UnitatOrganitzativa findByCodi(String codi);
	
	
	@Query("select uo.id " +	
			"from " +
			"    UnitatOrganitzativa uo "
			)
	List<Long> findAllUnitatOrganitzativaIds();
	
	@Query(	"from UnitatOrganitzativa uo " +
			"where  "+
			" (:esNullCodi = true or lower(uo.codi) like lower('%'||:codi||'%')) " +
			" and (:esNullDenominacio = true or lower(uo.denominacio) like lower('%'||:denominacio||'%')) " +
			" and (:esNullCif = true or lower(uo.nifCif) like lower('%'||:cif||'%')) " +
			" and (:esNullCodiUnitatSuperior = true or lower(uo.codiUnitatSuperior) like lower('%'||:codiUnitatSuperior||'%')) "+
			" and (:esNullEstat = true or uo.estat = :estat) " + 
			" and   (:esNullFiltre = true "
			+ "or lower(uo.denominacio) like lower('%'||:filtre||'%') "
			+ "or lower(uo.codi) like lower('%'||:filtre||'%')  "
			+ "or lower(uo.estat) like lower('%'||:filtre||'%')) ")
	Page<UnitatOrganitzativa> findByFiltrePaginat(
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullDenominacio") boolean esNullDenominacio,
			@Param("denominacio") String denominacio,
			@Param("esNullCif") boolean esNullCif,
			@Param("cif") String cif,
			@Param("esNullCodiUnitatSuperior") boolean esNullCodiUnitatSuperior,
			@Param("codiUnitatSuperior") String codiUnitatSuperior,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") String estat,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
	
	
	@Query(	"from " +
			"    UnitatOrganitzativa uo " +
			"where " +
			"    uo.codi = :codi " +
			"and uo.estat!='V') ")
	List<UnitatOrganitzativa> findByCodiAndEstatNotV(
			@Param("codi") String codi);
	
	
	@Query(	"from " +
			"    UnitatOrganitzativa uo " +
			"where " +
			"    uo.codiUnitatArrel = :codiArrel " +
			"and uo.estat='V') ")
	List<UnitatOrganitzativa> findByCodiUnitatArrelAndEstatVigent(
			@Param("codiArrel") String codiArrel);
	
	
	@Query(	"from " +
			"    UnitatOrganitzativa uo " +
			"where " +
			"    uo.codi = :codi " +
			"and (:ambArrel = true or uo.codi != :codiDir3Entitat) " +
			"and ((:esNullFiltre = true or lower(uo.codi) like lower('%'||:filtre||'%')) " +
			"or (:esNullFiltre = true or lower(uo.denominacio) like lower('%'||:filtre||'%'))) " +
			"and codiUnitatSuperior = :codiUnitatSuperior")
	List<UnitatOrganitzativa> findByCodiUnitatAmbCodiUnitatSuperiorAndCodiAndDenominacioFiltreNomesAmbBusties(
			@Param("codi") String codi,
			@Param("codiUnitatSuperior") String codiUnitatSuperior,
			@Param("esNullFiltre") boolean esNullFiltreCodi,
			@Param("filtre") String filtre,
			@Param("ambArrel") boolean ambArrel);
	
	
	@Query(	"from " +
			"    UnitatOrganitzativa uo " +
			"where " +
			"    uo.codi = :codi " +
			"and (:ambArrel = true or uo.codi != :codiDir3Entitat) " +
			"and ((:esNullFiltre = true or lower(uo.codi) like lower('%'||:filtre||'%')) " +
			"or (:esNullFiltre = true or lower(uo.denominacio) like lower('%'||:filtre||'%'))) " +
			"and codiUnitatSuperior = :codiUnitatSuperior")
	List<UnitatOrganitzativa> findByCodiUnitatAmbCodiUnitatSuperiorAndCodiAndDenominacioFiltre(
			@Param("codi") String codi,
			@Param("codiUnitatSuperior") String codiUnitatSuperior,
			@Param("esNullFiltre") boolean esNullFiltreCodi,
			@Param("filtre") String filtre,
			@Param("ambArrel") boolean ambArrel);
	
	
	@Query(	"from " +
			"    UnitatOrganitzativa uo " +
			"where " +
			"    uo.codi = :codi " +
			"and (:ambArrel = true ) " +
			"and ((:esNullFiltre = true or lower(uo.codi) like lower('%'||:filtre||'%')) " +
			"or (:esNullFiltre = true or lower(uo.denominacio) like lower('%'||:filtre||'%'))) "// +
			)
	List<UnitatOrganitzativa> findByCodiUnitatAndCodiAndDenominacioFiltreNomesAmbBusties(
			@Param("codi") String codi,
			@Param("esNullFiltre") boolean esNullFiltreCodi,
			@Param("filtre") String filtre,
			@Param("ambArrel") boolean ambArrel);
	
	
	
	@Query(	"from " +
			"    UnitatOrganitzativa uo " +
			"where " +
			"    uo.codi = :codi " +
			"and (:ambArrel = true or uo.codi != :codiDir3Entitat) " +
			"and ((:esNullFiltre = true or lower(uo.codi) like lower('%'||:filtre||'%')) " +
			"or (:esNullFiltre = true or lower(uo.denominacio) like lower('%'||:filtre||'%'))) ")
	List<UnitatOrganitzativa> findByCodiUnitatAndCodiAndDenominacioFiltre(
			@Param("codi") String codi,
			@Param("esNullFiltre") boolean esNullFiltreCodi,
			@Param("filtre") String filtre,
			@Param("ambArrel") boolean ambArrel);
	
	
	@Query(	"from " +
			"    UnitatOrganitzativa uo " +
			"where " +
			"(:esNullFiltreCodi = true or lower(uo.codi) like lower('%'||:filtre||'%') " +
			"or (:esNullFiltreCodi = true or lower(uo.denominacio) like lower('%'||:filtre||'%'))) ")
	List<UnitatOrganitzativa> findByCodiAndDenominacioFiltre(
			@Param("esNullFiltreCodi") boolean esNullFiltreCodi,
			@Param("filtre") String filtre);
	
	
}
