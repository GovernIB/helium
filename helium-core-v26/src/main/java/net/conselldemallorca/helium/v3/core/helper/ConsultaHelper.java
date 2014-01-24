/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.v3.core.repository.ConsultaRepository;

import org.springframework.stereotype.Component;

/**
 * Helper per a convertir entre diferents formats de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConsultaHelper {
	@Resource
	private ConsultaRepository consultaRepository;

	public Consulta findById(Long consultaId) {
		return consultaRepository.findById(consultaId);
	}
}
