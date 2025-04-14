/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import net.conselldemallorca.helium.core.model.hibernate.UsuariPreferencies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a les preferències d'usuari que estan emmagatzemades
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UsuariPreferenciesRepository extends JpaRepository<UsuariPreferencies, Long> {

	UsuariPreferencies findByCodi(String codi);
	
	
	@Modifying
	@Query("UPDATE UsuariPreferencies up SET up.codi = :codiNou WHERE up.codi = :codiActual")
	public int updateCodiUsuariUsuariPreferencies(@Param("codiActual") String codiActual, @Param("codiNou") String codiNou);

	@Modifying
	@Query("UPDATE ActionLog al SET al.usuari = :codiNou WHERE al.usuari = :codiActual")
	public int updateCodiUsuariActionLog(@Param("codiActual") String codiActual, @Param("codiNou") String codiNou);

	@Modifying
	@Query("UPDATE Alerta al SET al.destinatari = :codiNou WHERE al.destinatari = :codiActual")
	public int updateCodiUsuariAlerta(@Param("codiActual") String codiActual, @Param("codiNou") String codiNou);

	@Modifying
	@Query("UPDATE DocumentNotificacio dn SET dn.usuariCodi = :codiNou WHERE dn.usuariCodi = :codiActual")
	public int updateCodiUsuariDocumentNotificacio(@Param("codiActual") String codiActual, @Param("codiNou") String codiNou);

	@Modifying
	@Query("UPDATE Repro r SET r.usuari = :codiNou WHERE r.usuari = :codiActual")
	public int updateCodiUsuariRepro(@Param("codiActual") String codiActual, @Param("codiNou") String codiNou);

	@Modifying
	@Query("UPDATE ExecucioMassiva em SET em.usuari = :codiNou WHERE em.usuari = :codiActual")
	public int updateCodiUsuariExecucioMassiva(@Param("codiActual") String codiActual, @Param("codiNou") String codiNou);

	@Modifying
	@Query("UPDATE ExpedientLog el SET el.usuari = :codiNou WHERE el.usuari = :codiActual")
	public int updateCodiUsuariExpedientLog(@Param("codiActual") String codiActual, @Param("codiNou") String codiNou);

	@Modifying
	@Query("UPDATE PeticioPinbal pp SET pp.usuari = :codiNou WHERE pp.usuari = :codiActual")
	public int updateCodiUsuariPeticioPinbal(@Param("codiActual") String codiActual, @Param("codiNou") String codiNou);

	@Modifying
	@Query(value = "UPDATE HEL_ACL_SID a SET a.SID = :codiNou WHERE a.SID = :codiActual", nativeQuery = true)
	public int updateCodiUsuariAclSid(@Param("codiActual") String codiActual, @Param("codiNou") String codiNou);

	@Modifying
	@Query(value = "UPDATE JBPM_ID_USER j SET j.NAME_ = :codiNou WHERE j.NAME_ = :codiActual", nativeQuery = true)
	public int updateCodiUsuariJbpmIdUser(@Param("codiActual") String codiActual, @Param("codiNou") String codiNou);

	@Modifying
	@Query(value = "UPDATE JBPM_LOG j SET j.TASKACTORID_ = :codiNou WHERE j.TASKACTORID_ = :codiActual", nativeQuery = true)
	public int updateCodiUsuariJbpmLogTaskActor(@Param("codiActual") String codiActual, @Param("codiNou") String codiNou);

	@Modifying
	@Query(value = "UPDATE JBPM_LOG j SET j.TASKOLDACTORID_ = :codiNou WHERE j.TASKOLDACTORID_ = :codiActual", nativeQuery = true)
	public int updateCodiUsuariJbpmLogTaskOldActor(@Param("codiActual") String codiActual, @Param("codiNou") String codiNou);

	@Modifying
	@Query(value = "UPDATE JBPM_POOLEDACTOR j SET j.ACTORID_ = :codiNou WHERE j.ACTORID_ = :codiActual", nativeQuery = true)
	public int updateCodiUsuariJbpmPoolActor(@Param("codiActual") String codiActual, @Param("codiNou") String codiNou);

	@Modifying
	@Query(value = "UPDATE JBPM_TASKINSTANCE j SET j.ACTORID_ = :codiNou WHERE j.ACTORID_ = :codiActual", nativeQuery = true)
	public int updateCodiUsuariJbpmTaskInstance(@Param("codiActual") String codiActual, @Param("codiNou") String codiNou);
	
}
