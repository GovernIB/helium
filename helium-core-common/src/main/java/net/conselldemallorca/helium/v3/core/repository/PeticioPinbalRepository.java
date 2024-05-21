package net.conselldemallorca.helium.v3.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.PeticioPinbal;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalEstatEnum;

public interface PeticioPinbalRepository extends JpaRepository<PeticioPinbal, Long> {
	
	public PeticioPinbal findByPinbalId(String pinbalId);
	
	public List<PeticioPinbal> findByExpedientId(Long expedientId);
	
	@Query(	"select pi from PeticioPinbal pi where pi.asincrona=true and pi.estat='PENDENT' and pi.dataPrevista>sysdate")
	public List<PeticioPinbal> findAsincronesPendents();
	
	@Query(	"select pi from PeticioPinbal pi INNER JOIN pi.expedient as ex  " +
			"where  "+
			" (:esNullEntorn = true or pi.entorn.id IN (:entornsId)) " +
			" and (:esNullExpTipus = true or pi.tipus.id = :expTipus) " +
			" and (:esNullExpId = true or ex.id = :expId) " +
			" and (:esNullNumExp = true or lower(ex.numero) like lower('%'||:numExp||'%') or lower(ex.titol) like lower('%'||:numExp||'%')) " +
			" and (:esNullProcediment = true or lower(pi.procediment) like lower('%'||:procediment||'%')) "+
			" and (:esNullUsuari = true or lower(pi.usuari) like lower('%'||:usuari||'%')) "+
			" and (:esNullEstat = true or pi.estat = :estat) " +
			" and (:esNullDataIni = true or pi.dataPeticio >= :dataIni) " +
			" and (:esNullDataFi = true  or pi.dataPeticio <= :dataFi) " +
			" and (:esNullFiltre = true or (lower(pi.usuari) like lower('%'||:filtre||'%')) or lower(pi.procediment) like lower('%'||:filtre||'%') or lower(pi.estat) like lower('%'||:filtre||'%') or lower(pi.errorMsg) like lower('%'||:filtre||'%'))")
	public Page<PeticioPinbal> findByFiltrePaginat(
			@Param("esNullEntorn") boolean esNullEntorn,
			@Param("entornsId") List<Long> entornsId,			
			@Param("esNullExpTipus") boolean esNullExpTipus,
			@Param("expTipus") Long expTipus,
			@Param("esNullExpId") boolean esNullExpId,
			@Param("expId") Long expId,
			@Param("esNullNumExp") boolean esNullNumExp,
			@Param("numExp") String numExp,	
			@Param("esNullProcediment") boolean esNullProcediment,
			@Param("procediment") String procediment,
			@Param("esNullUsuari") boolean esNullUsuari,
			@Param("usuari") String usuari,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") PeticioPinbalEstatEnum estat,
			@Param("esNullDataIni") boolean esNullDataIni,
			@Param("dataIni") Date dataIni,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,			
			Pageable pageable);
}