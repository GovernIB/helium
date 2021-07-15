/**
 * 
 */
package es.caib.helium.logic.helper;

import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto.OrdreDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper per a convertir les dades de paginació entre el DTO
 * i Spring-Data.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PaginacioHelper {

	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;

	public boolean esPaginacioActivada(PaginacioParamsDto dto) {
		return dto.getPaginaTamany() > 0;
	}

	public <T> Pageable toSpringDataPageable(
			PaginacioParamsDto dto,
			Map<String, String> mapeigPropietatsOrdenacio) {
		return PageRequest.of(
				dto.getPaginaNum(),
				dto.getPaginaTamany(),
				toSpringDataSort(dto, mapeigPropietatsOrdenacio));
	}
	public <T> Pageable toSpringDataPageable(
			PaginacioParamsDto dto) {
		return toSpringDataPageable(dto, null);
	}
	public <T> Sort toSpringDataSort(
			PaginacioParamsDto dto) {
		return toSpringDataSort(dto, null);
	}
	public <T> Sort toSpringDataSort(
			PaginacioParamsDto dto,
			Map<String, String> mapeigPropietatsOrdenacio) {
		List<Order> orders = new ArrayList<Order>();
		if (dto.getOrdres() != null && !dto.getOrdres().isEmpty()) {
			for (OrdreDto ordre: dto.getOrdres()) {
				Direction direccio = OrdreDireccioDto.DESCENDENT.equals(ordre.getDireccio()) ? Sort.Direction.DESC : Sort.Direction.ASC;
				String propietat = ordre.getCamp();
				if (mapeigPropietatsOrdenacio != null) {
					String mapeig = mapeigPropietatsOrdenacio.get(ordre.getCamp());
					if (mapeig != null)
						propietat = mapeig;
				} else {
					propietat = ordre.getCamp();
				}
				orders.add(new Order(
						direccio,
						propietat));
			}
		}
		return Sort.by(orders);
	}

	public <S, T> PaginaDto<T> toPaginaDto(
			Page<S> page,
			Class<T> targetType) {
		return toPaginaDto(page, targetType, null);
	}
	public <S, T> PaginaDto<T> toPaginaDto(
			Page<S> page,
			Class<T> targetType,
			Converter<S, T> converter) {
		PaginaDto<T> dto = new PaginaDto<T>();
		dto.setNumero(page.getNumber());
		dto.setTamany(page.getSize());
		dto.setTotal(page.getTotalPages());
		dto.setElementsTotal(page.getTotalElements());
		dto.setAnteriors(page.hasPrevious());
		dto.setPrimera(page.isFirst());
		dto.setPosteriors(page.hasNext());
		dto.setDarrera(page.isLast());
		if (page.hasContent()) {
			if (converter == null) {
				dto.setContingut(
						conversioTipusServiceHelper.convertirList(
								page.getContent(),
								targetType));
			} else {
				List<T> contingut = new ArrayList<T>();
				for (S element: page.getContent()) {
					contingut.add(
							converter.convert(element));
				}
				dto.setContingut(contingut);
			}
		}
		return dto;
	}
	public <T> PaginaDto<T> toPaginaDto(
			List<?> llista,
			Class<T> targetType) {
		PaginaDto<T> dto = new PaginaDto<T>();
		dto.setNumero(0);
		dto.setTamany(llista.size());
		dto.setTotal(1);
		dto.setElementsTotal(llista.size());
		dto.setAnteriors(false);
		dto.setPrimera(true);
		dto.setPosteriors(false);
		dto.setDarrera(true);
		if (targetType != null) {
			dto.setContingut(
					conversioTipusServiceHelper.convertirList(
							llista,
							targetType));
		}
		return dto;
	}

	public <T> PaginaDto<T> toPaginaDto(
            List<T> elements,
            long elementsTotal,
            PaginacioParamsDto paginacioParams) {
		PaginaDto<T> dto = new PaginaDto<T>();
		long paginesTotal = elementsTotal / paginacioParams.getPaginaTamany();
		dto.setNumero(paginacioParams.getPaginaNum());
		dto.setTamany(paginacioParams.getPaginaTamany());
		dto.setTotal(paginesTotal);
		dto.setElementsTotal(elementsTotal);
		dto.setAnteriors(paginacioParams.getPaginaNum() > 0);
		dto.setPrimera(paginacioParams.getPaginaNum() == 0);
		dto.setPosteriors(paginacioParams.getPaginaNum() < paginesTotal - 1);
		dto.setDarrera(paginacioParams.getPaginaNum() == paginesTotal - 1);
		if (elements != null && elements.size() > 0) {
			dto.setContingut(elements);
		}
		return dto;
	}

	public <S, T> PaginaDto<T> toPaginaDto(
            List<S> elements,
            long elementsTotal,
            PaginacioParamsDto paginacioParams,
            Converter<S, T> converter) {
		PaginaDto<T> dto = new PaginaDto<T>();
		long paginesTotal = elementsTotal / paginacioParams.getPaginaTamany();
		dto.setNumero(paginacioParams.getPaginaNum());
		dto.setTamany(paginacioParams.getPaginaTamany());
		dto.setTotal(paginesTotal);
		dto.setElementsTotal(elementsTotal);
		dto.setAnteriors(paginacioParams.getPaginaNum() > 0);
		dto.setPrimera(paginacioParams.getPaginaNum() == 0);
		dto.setPosteriors(paginacioParams.getPaginaNum() < paginesTotal - 1);
		dto.setDarrera(paginacioParams.getPaginaNum() == paginesTotal - 1);
		if (elements != null && elements.size() > 0) {
			List<T> contingut = new ArrayList<T>();
			for (S element: elements) {
				contingut.add(
						converter.convert(element));
			}
			dto.setContingut(contingut);
		}
		return dto;
	}

	public <T> PaginaDto<T> getPaginaDtoBuida(
			Class<T> targetType) {
		PaginaDto<T> dto = new PaginaDto<T>();
		dto.setNumero(0);
		dto.setTamany(0);
		dto.setTotal(1);
		dto.setElementsTotal(0);
		dto.setAnteriors(false);
		dto.setPrimera(true);
		dto.setPosteriors(false);
		dto.setDarrera(true);
		return dto;
	}

	public interface Converter<S, T> {
		T convert(S source);
	}

}
