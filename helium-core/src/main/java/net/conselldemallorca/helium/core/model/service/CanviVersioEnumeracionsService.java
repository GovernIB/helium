/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.List;

import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar la inicialització del sistema i les
 * actualitzacions automàtiques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class CanviVersioEnumeracionsService {

	private DissenyService dissenyService;

	public void canviarVersioEnumeracions() throws Exception {
		List<Enumeracio> enumeracions = dissenyService.findEnumeracions();
		if (enumeracions.size() > 0) {
			for (Enumeracio enumeracio : enumeracions) {
				if ((enumeracio.getValors() != null) && (!enumeracio.getValors().equals(""))) {
					List<ParellaCodiValor> valors = enumeracio.getLlistaValors();
					for (ParellaCodiValor parella : valors) {
						EnumeracioValors enumeracioValors = new EnumeracioValors();
						enumeracioValors.setCodi(parella.getCodi());
						enumeracioValors.setNom((String)parella.getValor());
						enumeracioValors.setEnumeracio(enumeracio);
						dissenyService.createEnumeracioValors(enumeracioValors);
					}
				}
			}
		}
	}

	@Autowired
	public void setDissenyService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}
	
	private static final Log logger = LogFactory.getLog(CanviVersioEnumeracionsService.class);

}
