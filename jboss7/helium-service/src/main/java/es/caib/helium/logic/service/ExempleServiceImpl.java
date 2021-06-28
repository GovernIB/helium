/**
 * 
 */
package es.caib.helium.logic.service;

import java.text.SimpleDateFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.logic.intf.service.ExempleService;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class ExempleServiceImpl implements ExempleService {

	private static final SimpleDateFormat scspDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");


	@Transactional
	@Override
	public String hola(String text) {
		log.debug(
				"text (" +
						"text=" + text + ")");
		String resposta;
		try {
			resposta = "Hola " + text;
		} catch (Exception ex) {
			log.error(
					"Error al processar petició de redirecció (" + ex.getMessage() + ")",
					ex);
			resposta = "[HELIUM] Error " + ex.getMessage();
		}
		return resposta;
	}

}
