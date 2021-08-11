package es.caib.helium.logic.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import es.caib.helium.client.model.PagedList;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto.OrdreDto;

/** Classe helper amb mètodes per a la crida i tractament de la resposta amb 
 * els micro serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Component
public class MsHelper {

	@Autowired
	protected ConversioTipusServiceHelper conversioTipusServiceHelper;

	/** Retorna un string amb les propietats i ordenacions.
	 * 
	 * @param paginacioParams
	 * @return
	 */
	public Sort getSort(PaginacioParamsDto paginacioParams) {
		
		List<Order> ordres = new ArrayList<Order>();
		if (paginacioParams.getOrdres() != null && paginacioParams.getOrdres().size() > 0) {
			for (OrdreDto ordreDto : paginacioParams.getOrdres()) {
				String propietat = ordreDto.getCamp();
				Direction direccio = OrdreDireccioDto.DESCENDENT.equals(ordreDto.getDireccio()) ? Sort.Direction.DESC : Sort.Direction.ASC;
				ordres.add(new Order(
						direccio,
						propietat));
			}
		}
		return Sort.by(ordres);
	}

	/** Retorna una llista d'ordenacions en la forma "propietat:direccio"
	 * 
	 * @param paginacioParams
	 * @return
	 */
	public List<String> getSortList(PaginacioParamsDto paginacioParams) {
		List<String> ordres = new ArrayList<String>();
		if (paginacioParams.getOrdres() != null && paginacioParams.getOrdres().size() > 0) {
			for (OrdreDto ordreDto : paginacioParams.getOrdres()) {
				String propietat = ordreDto.getCamp();
				Direction direccio = OrdreDireccioDto.DESCENDENT.equals(ordreDto.getDireccio()) ? Sort.Direction.DESC : Sort.Direction.ASC;
				ordres.add(propietat + ":" + direccio.toString() );
			}
		}
		return ordres;
	}

	/** Transformació de PagedList a PaginaDto
	 * 
	 * @param <T>
	 * @param page
	 * @param classT
	 * @return
	 */
	public <T> PaginaDto<T> toPaginaDto(PagedList page, Class<T> classT) {
	
		PaginaDto<T> dto = new PaginaDto<T>();
		if (page != null) {
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
						this.conversioTipusServiceHelper.convertirList(
								page.getContent(),
								classT));
			}
		} else {
			// Pàgina buida
			dto.setNumero(0);
			dto.setTamany(0);
			dto.setTotal(1);
			dto.setElementsTotal(0);
			dto.setAnteriors(false);
			dto.setPrimera(true);
			dto.setPosteriors(false);
			dto.setDarrera(true);
			dto.setContingut(null);
		}
		return dto;
	}
}
