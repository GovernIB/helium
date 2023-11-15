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
	
//	List<UnitatOrganitzativa> findByCodiDir3Entitat(String codi);
	
	List<UnitatOrganitzativa> findByCodiOrderByDenominacioAsc(String codi);
	
	UnitatOrganitzativa findByCodi(String codi);
	
	@Query(	"from UnitatOrganitzativa uo " +
			"where " +
			"    (:esNullFiltre = true "
			+ "or lower(uo.denominacio) like lower('%'||:filtre||'%') "
			+ "or lower(uo.codi) like lower('%'||:filtre||'%')  "
			+ "or lower(uo.estat) like lower('%'||:filtre||'%')) ")
	Page<UnitatOrganitzativa> findByFiltrePaginat(
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
			"    uo.codi = :codi " +
			"and uo.estat='V') ")
	List<UnitatOrganitzativa> findByCodiAndEstatV(
			@Param("codi") String codi);
	
	
	@Query(	"from " +
			"    UnitatOrganitzativa uo " +
			"where " +
			"    uo.codi = :codi " +
			"and (:ambArrel = true or uo.codi != :codiDir3Entitat) " +
			"and ((:esNullFiltre = true or lower(uo.codi) like lower('%'||:filtre||'%')) " +
			"or (:esNullFiltre = true or lower(uo.denominacio) like lower('%'||:filtre||'%'))) " +
//			"and uo.id in (select distinct b.unitatOrganitzativa.id from BustiaEntity b)" +
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
//			 "and uo.id in (select distinct b.unitatOrganitzativa.id from BustiaEntity b)"
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
