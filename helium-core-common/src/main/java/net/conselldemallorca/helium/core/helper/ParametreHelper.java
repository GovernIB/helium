/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import net.conselldemallorca.helium.core.model.hibernate.Parametre;
import net.conselldemallorca.helium.v3.core.api.service.ParametreService;
import net.conselldemallorca.helium.v3.core.repository.ParametreRepository;

/**
 * Classe helper per consultar paràmetres d'Helium guardats a la taula de paràmetres.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ParametreHelper {
	
	@Autowired
	private ParametreRepository parametreRepository;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	@Transactional
	public Date getDataSincronitzacioUos() {
		return this.getValorData(parametreRepository.findByCodi(ParametreService.APP_CONFIGURACIO_DATA_SINCRONITZACIO_UO));
	}
	@Transactional
	public void setDataSincronitzacioUos(Date date) {
		this.setValorData(ParametreService.APP_CONFIGURACIO_DATA_SINCRONITZACIO_UO, date);
	}
	public Date getDataActualitzacioUos() {
		return this.getValorData(parametreRepository.findByCodi(ParametreService.APP_CONFIGURACIO_DATA_ACTUALITZACIO_UO));
	}
	public void setDataActualitzacioUos(Date date) {
		this.setValorData(ParametreService.APP_CONFIGURACIO_DATA_ACTUALITZACIO_UO, date);
	}
	public String getArrelUos() {
		Parametre parametre = parametreRepository.findByCodi(ParametreService.APP_CONFIGURACIO_CODI_ARREL_UO);
		return parametre != null ? parametre.getValor() : null; 
	}
	
	public String getMidaMaximaFitxer() {
		Parametre param = parametreRepository.findByCodi(ParametreService.APP_CONFIGURACIO_FITXER_MIDA_MAXIM);
		return param.getValor();
	}
	
	
	public Long getMidaMaximaFitxerInBytes() {
		Parametre param = parametreRepository.findByCodi(ParametreService.APP_CONFIGURACIO_FITXER_MIDA_MAXIM);
		
		if(param == null || param.getValor() == null || StringUtils.isEmpty(param.getValor()))
			return null;
		
		String valor = param.getValor();
		
		String unit = valor.replaceAll("\\d|\\W", "");
		Double amount = Double.parseDouble(valor.replaceAll("[a-zA-Z]", ""));
	    
	    if(unit.toUpperCase().equals("MB")) {
	        return Math.round(amount * 1000000);
	    } else if(unit.toUpperCase().equals("KB")) {
	        return Math.round(amount * 1000);
	    }
		
		return Math.round(amount);
	}

	/** Llegeix el valor com a Date o el fixa a null si no es pot parsejar. */
	private Date getValorData(Parametre parametre) {
		Date data = null;
		if (parametre != null) {
			String valor = parametre.getValor();
			if (valor != null) {
				try {
					data = sdf.parse(valor);
				} catch(Exception e) {
					logger.error("Error transformant el valor \"" + valor + "\" a data, es fixa a null");
					parametre.setValor(null);
				}
			}
		}
		return data;
	}

	/** Fixa el valor Date al paràmetre amb el codi passat per paràmetre, si no existeix el paràmetre el crea. */
	private void setValorData(String codi, Date date) {
		Parametre parametre = parametreRepository.findByCodi(codi);
		String valor = date != null ? sdf.format(date) : null;
		logger.debug((parametre != null ? "Modificant " : "Creant ") + "el paràmetre " + codi + " amb el valor de data " + valor );
		if (parametre == null) {
			parametre = Parametre.getBuilder(
					codi, 
					codi,
					"Paràmetre creat el " + sdf.format(new Date()),
					valor).build();
			parametreRepository.save(parametre);
		} else {
			parametre.setValor(valor);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ParametreHelper.class);
}
