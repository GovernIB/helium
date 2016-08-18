/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

/**
 * Helper per a enviament de correus
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component("DissenyHelper")
public class DissenyHelper {

	@Resource
	private TerminiRepository terminiRepository;

	public void deleteTermini(Long id) {
		Termini vell = terminiRepository.findOne(id);
		if (vell != null) {
			for (TerminiIniciat iniciat: vell.getIniciats()) {
				vell.removeIniciat(iniciat);
				iniciat.setTermini(null);
			}
			terminiRepository.delete(id);
		}
	}
}
