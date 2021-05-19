package net.conselldemallorca.helium.ms;

import javax.annotation.Resource;

import net.conselldemallorca.helium.ms.domini.client.model.DominiPagedList;
import net.conselldemallorca.helium.ms.helper.ConversioTipusHelperMs;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;

/** Classe base dels microserveis amb mètodes comuns.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public class BaseMs {
	
	@Resource
	protected ConversioTipusHelperMs conversioTipusHelperMs;


	public BaseMs() {
		super();
	}

	protected <T> PaginaDto<T> toPaginaDto(DominiPagedList page, Class<T> classT) {

		PaginaDto<T> dto = new PaginaDto<T>();
		dto.setNumero(page.getNumber());
		dto.setTamany(page.getSize());
		dto.setTotal(page.getTotalPages());
		dto.setElementsTotal(page.getTotalElements());
		dto.setAnteriors(page.getNumber() < page.getTotalPages());
		dto.setPrimera(page.getNumber() == 0);
		dto.setPosteriors(page.getNumber() < page.getTotalPages());
		dto.setDarrera(page.getNumber() == page.getTotalPages() - 1);
		if (page.getContent() != null) {
			dto.setContingut(
					conversioTipusHelperMs.convertirList(
							page.getContent(),
							classT));
		}
		return dto;
	}
}