package net.conselldemallorca.helium.presentacio.dwr;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.service.DissenyService;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Servei DWR per a la gestió dels camps associats al processos.
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */
public class CampsProcesDwrService {

	private DissenyService dissenyService;
	
	@Autowired
	public CampsProcesDwrService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}
	
	public List<Object[]> llistaCampsPerProces(Long consultaId, String defprocJbpmKey) {
		List<Camp> list = dissenyService.findCampsProces(consultaId, defprocJbpmKey);
		List<Object[]> llista = new ArrayList<Object[]>();
		for (Camp c : list) {
			String text = c.getCodi() + " / " + c.getEtiqueta();
			text += " (v." + c.getDefinicioProces().getVersio() + ")";
			text += " - " + c.getTipus();
			
			Object[] obj = new Object[3];
			obj[0] = c.getCodi();
			obj[1] = text;
			obj[2] = c.getDefinicioProces().getVersio();
			llista.add(obj);
		}
		return llista;
	}
}
