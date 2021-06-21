package es.caib.helium.integracio.service.portafirmes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.portafirmes.PortaFirma;
import es.caib.helium.integracio.enums.portafirmes.TipusEstat;
import es.caib.helium.integracio.excepcions.portafirmes.PortaFirmesException;
import es.caib.helium.integracio.repository.portafirmes.PortaFirmesRepository;
import es.caib.helium.integracio.service.monitor.MonitorIntegracionsService;
import es.caib.helium.jms.domini.Parametre;
import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.enums.TipusAccio;
import es.caib.helium.jms.events.IntegracioEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public abstract class PortaFirmesServiceImpl implements PortaFirmesService {

	@Autowired
	protected PortaFirmesRepository portaFirmesRepository;
	@Autowired
	protected MonitorIntegracionsService monitor;
	
	private final String error = "Error inesperat al repository de portafirmes";

	@Override
	public PortaFirma getByDocumentId(Long documentId, Long entornId) throws PortaFirmesException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("documentId", documentId + ""));
		var t0 = System.currentTimeMillis();
		var descripcio = "Consulta al repository de portarfimres by documentId";
		try {
			var portaFirma = portaFirmesRepository.getByDocumentId(documentId);
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.PFIRMA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Consulta ok al repository de portafirmes by documentId");
			return portaFirma;
			
		} catch (Exception ex) {
			
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PFIRMA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new PortaFirmesException(error, ex);
		}
	}
	
	@Override
	public PortaFirma getByProcessInstanceId(String processInstanceId, Long entornId) throws PortaFirmesException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("processInstanceId", processInstanceId + ""));
		var t0 = System.currentTimeMillis();
		var descripcio = "Consulta al repository de portafirmes by processInstanceId";
		try {
			var portaFirma = portaFirmesRepository.getByProcessInstanceId(processInstanceId);
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.PFIRMA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Consulta ok al repository de portafirmes by processInstanceId");
			return portaFirma;
			
		} catch (Exception ex) {
			
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PFIRMA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new PortaFirmesException(error, ex);
		}
	}

	@Override
	public PortaFirma getByProcessInstanceIdAndDocumentStoreId(String processInstanceId, Long documentStoreId, Long entornId) throws PortaFirmesException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("processInstanceId", processInstanceId + ""));
		parametres.add(new Parametre("documentStoreId", documentStoreId + ""));
		var t0 = System.currentTimeMillis();
		var descripcio = "Consulta repository portafirmes by processInstanceId";
		try {
			
			var portaFirma = portaFirmesRepository.getByProcessInstanceIdAndDocumentStoreId(processInstanceId, documentStoreId);
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.PFIRMA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Consulta ok al repository de portafirmes by processInstanceId");
			return portaFirma;
			
		} catch (Exception ex) {
			
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PFIRMA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			
			throw new PortaFirmesException(error, ex);
		}
	}
	
	@Override
	public List<PortaFirma> getByExpedientIdAndEstat(Long expedientId, TipusEstat estat, Long entornId) throws PortaFirmesException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("expedientId", expedientId + ""));
		parametres.add(new Parametre("estat", estat.name()));
		var t0 = System.currentTimeMillis();
		var descripcio = "Consulta al repository de portafirmes by expedientId " + expedientId + " estat " + estat.name();
		try {
			var portaFirma = portaFirmesRepository.getByExpedientIdAndEstat(expedientId, estat);
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.PFIRMA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			log.debug("Consulta ok al repository de portafirmes by expedientId");
			return portaFirma;
		} catch (Exception ex) {
			
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PFIRMA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.tempsResposta(System.currentTimeMillis() - t0)
					.parametres(parametres)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new PortaFirmesException(error, ex);
		}
	}

	@Override
	public List<PortaFirma> getByProcessInstanceIdAndEstatNotIn(String processInstanceId, List<TipusEstat> estats, Long entornId) throws PortaFirmesException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("processInstanceId", processInstanceId + ""));
		parametres.add(new Parametre("estats", estats.toString()));
		var t0 = System.currentTimeMillis();
		var descripcio = "Consulta al repository de portafirmes by processInstanceId " + processInstanceId + " estats not in " + estats.toString();
		try {
			var portaFirma = portaFirmesRepository.getByProcessInstanceIdAndEstatNotIn(processInstanceId, estats);
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.PFIRMA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Consulta ok al repository de portafirmes by processInstanceId " + processInstanceId + " estats not in " + estats.toString());
			return portaFirma;
			
		} catch (Exception ex) {
			
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PFIRMA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new PortaFirmesException(error, ex);
		}
	}
	
	@Override
	public List<PortaFirma> getPendentsFirmar(String filtre, Long entornId) throws PortaFirmesException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("filtre", filtre));
		var t0 = System.currentTimeMillis();
		var descripcio = "Consulta al repository de portafirmes pendents de firmar amb filtre " + filtre;
			try {//TODO FALTA APLICAR EL FILTRE RSQL!!!!!!!!!!!!!!!
			var pendents = portaFirmesRepository.findPendents(TipusEstat.PENDENT, TipusEstat.REBUTJAT);

			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.PFIRMA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Consulta ok al repository de portafirmes pendents de firmar amb filtre " + filtre);
			return pendents;
		} catch (Exception ex) {
			
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PFIRMA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new PortaFirmesException(error, ex);
		}
	}

	@Override
	public List<PortaFirma> getPendentsFirmarByProcessInstance(String processInstance, Long entornId) throws PortaFirmesException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("processInstance", processInstance));
		var t0 = System.currentTimeMillis();
		var descripcio = "Consulta al repository de portafirmes pendents de firmar by proccesInstance " + processInstance;
		try {
			var pendents = portaFirmesRepository.findPendentsByProcessInstance(TipusEstat.PENDENT, TipusEstat.REBUTJAT, processInstance);
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.PFIRMA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Consulta ok al repository de portafirmes pendents de firmar by proccesInstance " + processInstance);
			return pendents;
		} catch (Exception ex) {
			
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PFIRMA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new PortaFirmesException(error, ex);
		}
	}
	
	@Override
	public boolean guardar(PortaFirma firma, Long entornId) throws PortaFirmesException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("documentId", firma.getDocumentId() + ""));
		var t0 = System.currentTimeMillis();
		var descripcio = "Guardant al repository de portafirmes";
		try {
			var firm = portaFirmesRepository.save(firma);
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.PFIRMA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Firma guardada al repository de portafirmes");
			return firm != null;
		} catch (Exception ex) {
			
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PFIRMA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new PortaFirmesException(error, ex);
		}
	}
	
	@Override
	public boolean processarCallBack(Long documentId, boolean rebutjat, String motiu, Long entornId) throws PortaFirmesException {
		
		// TODO FALTA VEURE ON IMPLEMENTAR EL ProcesCallbackHelper A Helium 3.0 ES UNA LLISTA EN MEMORIA 
		
		return false;
//		var t0 = System.currentTimeMillis();
//		var descripcio = "Processar callback portasignatures (documentId=" + documentId + ", rebujat=" + rebutjat + ", motiuRebuig=" + motiu + ")";
//		try {
//			var portasignatures = portaFirmesRepository.getByDocumentId(documentId);
//			if (portasignatures == null) {
//				log.error("El document rebut al callback (documentId=" + documentId + ") no s'ha trobat entre els documents enviats al portasignatures");
//				return false;
//			}
//			
//			if (TipusEstat.ESBORRAT.equals(portasignatures.getEstat()) || TipusEstat.PROCESSAT.equals(portasignatures.getEstat())) {
//				return true;
//			}
//			
//			if (!TipusEstat.PENDENT.equals(portasignatures.getEstat())) {
//				log.error("El document rebut al callback (documentId=" + documentId + ") no està pendent del callback, el seu estat és " + portasignatures.getEstat());
//				return false;
//			}
//			
//			portasignatures.setDataSignatRebutjat(new Date());
//			if (!rebutjat) {
//				portasignatures.setEstat(TipusEstat.SIGNAT);
//				portasignatures.setTransition(Transicio.SIGNAT);
//			} else {
//				portasignatures.setEstat(TipusEstat.REBUTJAT);
//				portasignatures.setTransition(Transicio.REBUTJAT);
//				portasignatures.setMotiuRebuig(motiu);
//			}
//			portaFirmesRepository.save(portasignatures);
//			
//			if (!procesCallbackHelper.isDocumentEnProces(portasignatures.getDocumentId())) {
//				procesCallbackHelper.afegirDocument(portasignatures.getDocumentId());
//				try {
//					processarDocumentPendentPortasignatures(documentId, portasignatures);
//				} finally {
//					if (procesCallbackHelper.isDocumentEnProces(portasignatures.getDocumentId()))
//						procesCallbackHelper.eliminarDocument(portasignatures.getDocumentId());
//				}
//			}
//			return true;
//		} catch (Exception ex) {
//			
//			log.error(error, ex);
//			log.error(error, ex);
//			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PFIRMA) 
//					.entornId(entornId) 
//					.descripcio(descripcio)
//					.tipus(TipusAccio.ENVIAMENT)
//					.estat(EstatAccio.ERROR)
//					.tempsResposta(System.currentTimeMillis() - t0)
//					.errorDescripcio(error)
//					.excepcioMessage(ex.getMessage())
//					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
//			throw new PortaFirmesException(error, ex);
//		}
	}
}
