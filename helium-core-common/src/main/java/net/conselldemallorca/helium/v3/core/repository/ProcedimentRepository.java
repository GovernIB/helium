package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Procediment;
import net.conselldemallorca.helium.core.model.hibernate.UnitatOrganitzativa;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentEstatEnumDto;


/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public interface ProcedimentRepository extends JpaRepository<Procediment, Long>{
		
	@Query( "from " + 
			"Procediment pro " + 
			"where (:isNullUnitatOrganitzativa = true or pro.unitatOrganitzativa = :unitatOrganitzativa) " + 
			"and (:isCodiNull = true or lower(pro.codi) like lower('%'||:codi||'%')) " + 
			"and (:isNomNull = true or lower(pro.nom) like lower('%'||:nom||'%')) " + 
			"and (:isCodiSiaNull = true or lower(pro.codiSia) like lower('%'||:codiSia||'%'))" + 
			"and (:isEstatNull = true or pro.estat = :estat)")
	Page<Procediment> findAmbFiltrePaginat(
			@Param("isNullUnitatOrganitzativa") boolean isNullUnitatOrganitzativa, 
			@Param("unitatOrganitzativa") UnitatOrganitzativa unitatorganitzativa, 
			@Param("isCodiNull") boolean isCodiNull, 
			@Param("codi") String codi,
			@Param("isNomNull") boolean isNomNull,
			@Param("nom") String nom, 
			@Param("isCodiSiaNull") boolean isCodiSiaNull, 
			@Param("codiSia") String codiSia, 
			@Param("isEstatNull") boolean isEstatNull, 
			@Param("estat") ProcedimentEstatEnumDto estat,
			Pageable pageable);
	
	
	@Query( "from " + 
			"Procediment pro " + 
			"where pro.codi = :codi ")
	Procediment findByCodi(
			@Param("codi") String codi);
	
	
	@Query( "from " + 
			"Procediment pro " + 
			"where (pro.unitatOrganitzativa.codi = :codiDir3) ")
	List<Procediment> findByCodiDir3(
			@Param("codiDir3") String codiDir3);
	
	
	@Query( "from " + 
			"Procediment pro " + 
			"where (pro.codiSia like :codiSia )")
	Procediment findByCodiSia(
			@Param("codiSia") String codiSia);
	
	
	@Query( "from " + 
			"Procediment pro " + 
			"where (:esNullNom = true or pro.nom like '%'||:nom||'%' )")
	List<Procediment> findByNom(
			@Param("esNullNom") boolean esNullNom, 
			@Param("nom") String nom);
	
	
	@Query( "from " + 
			"Procediment pro " + 
			"where ((pro.codiSia like '%'||:search||'%') " +
			"		or (lower(pro.nom) like lower('%'||:search||'%')))")
	List<Procediment> findByNomOrCodiSia(
			@Param("search") String search);

	
	@Query( "from " + 
			"Procediment pro " + 
			"where pro.unitatOrganitzativa.codi = :unitatOrganitzativaCodi")
	List<Procediment> findByCodiUnitatOrganitzativa(
			@Param("unitatOrganitzativaCodi") String unitatOrganitzativaCodi);

}
