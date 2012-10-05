package net.conselldemallorca.helium.webapp.dwr;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;

/**
 * Servei DWR per a la gestió dels camps associats al processos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CampsProcesDwrService implements MessageSourceAware {

	private DissenyService dissenyService;

	private MessageSource messageSource;



	@Autowired
	public CampsProcesDwrService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}
	
	public List<Object[]> llistaCampsPerProces(Long consultaId, String defprocJbpmKey) {
		List<Object[]> llista = new ArrayList<Object[]>();
		if (defprocJbpmKey == null || defprocJbpmKey.length() == 0) {
			Object[] obj = new Object[3];
			obj[0] = ExpedientCamps.EXPEDIENT_CAMP_ID;
			obj[1] = getMessage("etiqueta.exp.id");
			obj[2] = null;
			llista.add(obj);
			obj = new Object[3];
			obj[0] = ExpedientCamps.EXPEDIENT_CAMP_NUMERO;
			obj[1] = getMessage("etiqueta.exp.numero");
			obj[2] = null;
			llista.add(obj);
			obj = new Object[3];
			obj[0] = ExpedientCamps.EXPEDIENT_CAMP_TITOL;
			obj[1] = getMessage("etiqueta.exp.titol");
			obj[2] = null;
			llista.add(obj);
			/*obj = new Object[3];
			obj[0] = ExpedientCamps.EXPEDIENT_CAMP_COMENTARI;
			obj[1] = getMessage("etiqueta.exp.comentari");
			obj[2] = null;
			llista.add(obj);
			obj = new Object[3];
			obj[0] = ExpedientCamps.EXPEDIENT_CAMP_INICIADOR;
			obj[1] = getMessage("etiqueta.exp.iniciador");
			obj[2] = null;
			llista.add(obj);
			obj = new Object[3];
			obj[0] = ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE;
			obj[1] = getMessage("etiqueta.exp.responsable");
			obj[2] = null;
			llista.add(obj);*/
			obj = new Object[3];
			obj[0] = ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI;
			obj[1] = getMessage("etiqueta.exp.data_ini");
			obj[2] = null;
			llista.add(obj);
			obj = new Object[3];
			obj[0] = ExpedientCamps.EXPEDIENT_CAMP_ESTAT;
			obj[1] = getMessage("etiqueta.exp.estat");
			obj[2] = null;
			llista.add(obj);
			if (isGeorefActiu()) {
				if (isGeorefAmbReferencia()) {
					obj = new Object[3];
					obj[0] = ExpedientCamps.EXPEDIENT_CAMP_GEOREF;
					obj[1] = getMessage("comuns.georeferencia.codi");
					obj[2] = null;
					llista.add(obj);
				} else {
					obj = new Object[3];
					obj[0] = ExpedientCamps.EXPEDIENT_CAMP_GEOX;
					obj[1] = getMessage("comuns.georeferencia.coordenadaX");
					obj[2] = null;
					llista.add(obj);
					obj = new Object[3];
					obj[0] = ExpedientCamps.EXPEDIENT_CAMP_GEOY;
					obj[1] = getMessage("comuns.georeferencia.coordenadaY");
					obj[2] = null;
					llista.add(obj);
				}
			}
		} else {
			List<Camp> list = dissenyService.findCampsProces(consultaId, defprocJbpmKey);
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
		}
		return llista;
	}



	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	
//	public void goToCampTasca(Long IdTasca, int pos) {
//		dissenyService.goToCampTasca(IdTasca, pos);		
//	}
//
//	public void goToCampEstat(Long tipusExpId, int pos) {
//		dissenyService.goToCampEstat(tipusExpId, pos);		
//	}
//	
//	public void goToCampAgrupacio(Long tipusExpId, int pos) {
//		dissenyService.goToCampAgrupacio(tipusExpId, pos);		
//	}
//	public void goToCampConsLlistat(Long tipusExpId, int pos) {
//		dissenyService.goToCampConsLlistat(tipusExpId, pos);		
//	}
//	
	
	public void updateCampTasca(Long IdTasca,Long campId, boolean readFrom, boolean writeTo, boolean required, boolean readOnly) {
		dissenyService.addCampTasca(IdTasca, campId, readFrom, writeTo, required, readOnly);		
	}
	
	public void updateDocumentsTasca(Long IdTasca,Long campId, boolean required, boolean readOnly) {
		dissenyService.addDocumentTasca(IdTasca, campId, required, readOnly);		
	}
	

	protected String getMessage(String key) {
		return getMessage(key, null);
	}
	protected String getMessage(String key, Object[] vars) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}

	private boolean isGeorefActiu() {
		return "true".equalsIgnoreCase(GlobalProperties.getInstance().getProperty("app.georef.actiu"));
	}
	private boolean isGeorefAmbReferencia() {
		return "ref".equalsIgnoreCase(GlobalProperties.getInstance().getProperty("app.georef.tipus"));
	}

}
