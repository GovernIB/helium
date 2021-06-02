package es.caib.helium.integracio.repository.portafirmes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.caib.helium.integracio.domini.portafirmes.PortaFirma;
import es.caib.helium.integracio.enums.portafirmes.TipusEstat;


public interface PortaFirmesRepository extends JpaRepository<PortaFirma, Long>{

	@Query("from PortaFirma WHERE estat = :pendent OR estat = :rebutjat")
	List<PortaFirma> findPendents(TipusEstat pendent, TipusEstat rebutjat);
	
	@Query("from PortaFirma WHERE process_instance_id = :processInstance AND (estat = :pendent OR estat = :rebutjat)")
	List<PortaFirma> findPendentsByProcessInstance(TipusEstat pendent, TipusEstat rebutjat, String processInstance);
	
	PortaFirma getByDocumentId(Long documentId);
	
	PortaFirma getByProcessInstanceId(String processInstanceId);

	PortaFirma getByProcessInstanceIdAndDocumentStoreId(String processInstanceId, Long documentStoreId);
	
	List<PortaFirma> getByExpedientIdAndEstat(Long expedientId, TipusEstat estat);
	
	List<PortaFirma> getByProcessInstanceIdAndEstatNotIn(String processInstanceId, List<TipusEstat> estats);
}
