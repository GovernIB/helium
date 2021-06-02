package es.caib.helium.integracio.service.portafirmes;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.portafirmes.PortaFirma;
import es.caib.helium.integracio.enums.portafirmes.TipusEstat;
import es.caib.helium.integracio.excepcions.portafirmes.PortaFirmesException;
import es.caib.helium.integracio.repository.portafirmes.PortaFirmesRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public abstract class PortaFirmesServiceImpl implements PortaFirmesService {

	@Autowired
	private PortaFirmesRepository portaFirmesRepository;

	@Override
	public PortaFirma getByDocumentId(Long documentId) throws PortaFirmesException {
		
		try {
			return portaFirmesRepository.getByDocumentId(documentId);
		} catch (Exception e) {
			throw new PortaFirmesException("[PortaFirmesServiceImpl.getByDocumentId] Error inesperat al repository de portafirmes", e);
		}
	}
	
	@Override
	public PortaFirma getByProcessInstanceId(String processInstanceId) throws PortaFirmesException {
		
		try {
			return portaFirmesRepository.getByProcessInstanceId(processInstanceId);
		} catch (Exception e) {
			throw new PortaFirmesException("[PortaFirmesServiceImpl.getByProcessInstanceId] Error inesperat al repository de portafirmes", e);
		}
	}

	@Override
	public PortaFirma getByProcessInstanceIdAndDocumentStoreId(String processInstanceId, Long documentStoreId) throws PortaFirmesException {
		
		try {
			return portaFirmesRepository.getByProcessInstanceIdAndDocumentStoreId(processInstanceId, documentStoreId);
		} catch (Exception e) {
			throw new PortaFirmesException("[PortaFirmesServiceImpl.getByProcessInstanceIdAndDocumentStoreId] Error inesperat al repository de portafirmes", e);
		}
	}
	
	@Override
	public List<PortaFirma> getByExpedientIdAndEstat(Long expedientId, TipusEstat estat) throws PortaFirmesException {
		
		try {
			return portaFirmesRepository.getByExpedientIdAndEstat(expedientId, estat);
		} catch (Exception e) {
			throw new PortaFirmesException("[PortaFirmesServiceImpl.getByExpedientIdAndEstat] Error inesperat al repository de portafirmes", e);
		}
	}

	@Override
	public List<PortaFirma> getByProcessInstanceIdAndEstatNotIn(String processInstanceId, List<TipusEstat> estats) throws PortaFirmesException {
		
		try {
			return portaFirmesRepository.getByProcessInstanceIdAndEstatNotIn(processInstanceId, estats);
		} catch (Exception e) {
			throw new PortaFirmesException("[PortaFirmesServiceImpl.getByProcessInstanceIdAndEstatNotIn] Error inesperat al repository de portafirmes", e);
		}
	}
	
	@Override
	public List<PortaFirma> getPendentsFirmar(String filtre) throws PortaFirmesException {
		
		try {//TODO FALTA APLICAR EL FILTRE RSQL
			return portaFirmesRepository.findPendents(TipusEstat.PENDENT, TipusEstat.REBUTJAT);
		} catch (Exception e) {
			throw new PortaFirmesException("[PortaFirmesServiceImpl.getPendentsFirmar] Error inesperat al repository de portafirmes", e);
		}
	}

	@Override
	public List<PortaFirma> getPendentsFirmarByProcessInstance(String processInstance) throws PortaFirmesException {
		
		try {
			return portaFirmesRepository.findPendentsByProcessInstance(TipusEstat.PENDENT, TipusEstat.REBUTJAT, processInstance);
		} catch (Exception e) {
			throw new PortaFirmesException("[PortaFirmesServiceImpl.getPendentsFirmarByProcessInstance] Error inesperat al repository de portafirmes", e);
		}
	}
	
	@Override
	public boolean guardar(PortaFirma firma) throws PortaFirmesException {
		
		try {
			return portaFirmesRepository.save(firma) != null;
		} catch (Exception e) {
			throw new PortaFirmesException("[PortaFirmesServiceImpl.guardar] Error inesperat al repository de portafirmes", e);
		}
	}
	
	@Override
	public PortaFirma processarCallBack(Long documentId, boolean rebutjat, String motiu) throws PortaFirmesException {
		
		var portaFirma = portaFirmesRepository.getByDocumentId(documentId);
		if (portaFirma == null) {
			log.error("El document rebut al callback (documentId=" + documentId + ") no s'ha trobat entre els documents enviats al portasignatures");
			return portaFirma;
		}
		return portaFirma;
	}
}
