/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioValorsRepository;

import org.springframework.stereotype.Component;

/**
 * Helper per a gestionar les tasques dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EnumeracioValorsHelper {
	@Resource
	private EnumeracioValorsRepository enumeracioValorsRepository;
	
	public List<ParellaCodiValor> getLlistaValors(Long enumeracioId) {
		List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
		List<EnumeracioValors> valors = enumeracioValorsRepository.findByEnumeracioOrdenat(enumeracioId);
		if (valors != null) {
			for (int i = 0; i < valors.size(); i++) {
				EnumeracioValors enumValors = valors.get(i);
				if (enumValors != null)
					resposta.add(new ParellaCodiValor(enumValors.getCodi(), enumValors.getNom()));
			}
		}
		return resposta;
	}
}
