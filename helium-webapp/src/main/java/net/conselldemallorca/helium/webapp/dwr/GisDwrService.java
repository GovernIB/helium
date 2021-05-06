//package net.conselldemallorca.helium.webapp.dwr;
//
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
//import net.conselldemallorca.helium.core.model.hibernate.Expedient;
//import net.conselldemallorca.helium.core.model.service.ExpedientService;
//import net.conselldemallorca.helium.integracio.plugins.gis.DadesExpedient;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
///**
// * Servei DWR per a la gestió dels camps associats al processos.
// * 
// * @author Limit Tecnologies <limit@limit.es>
// */
//public class GisDwrService {
//
//	private ExpedientService expedientService;
//
//	@Autowired
//	public GisDwrService(ExpedientService expedientService) {
//		this.expedientService = expedientService;
//	}
//
//	public String xmlExpedients(String[] processInstanceId) {
//		// Passar de processInstanceId a DadesExpedient
//		List<DadesExpedient> expedients = new ArrayList<DadesExpedient>();
//		for (String pid: processInstanceId) {
//			Expedient expedient = expedientService.findExpedientAmbProcessInstanceId(pid);
//			DadesExpedient de = new DadesExpedient(
//					expedient.getGeoReferencia(), 
//					expedient.getIdentificador(), 
//					expedient.getTitol(), 
//					expedient.getTipus().getCodi(), 
//					expedient.getTipus().getNom(), 
//					expedient.getEstat() != null ? expedient.getEstat().getCodi() : null, 
//					expedient.getEstat() != null ? expedient.getEstat().getNom() : null, 
//					pid);
//			expedients.add(de);
//		}
//		
//		String xml = expedientService.getXmlExpedients(expedients); 
//		return xml;
//	}
//
//	public String urlVisor() {
//		URL url = expedientService.getUrlVisor();
//		return url.toString();
//	}
//
//}
