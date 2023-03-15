/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

/**
 * Helper per a convertir les dades de paginació entre el DTO
 * i Spring-Data.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PaginacioHelper {

	@Resource
	private ConversioTipusHelper conversioTipusHelper;



	public <T> Pageable toSpringDataPageable(
			PaginacioParamsDto dto,
			Map<String, String> mapeigPropietatsOrdenacio) {
		List<Order> orders = new ArrayList<Order>();
		if (dto.getOrdres() != null) {
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
		return new PageRequest(
				dto.getPaginaNum(),
				dto.getPaginaTamany(),
				new Sort(orders));
	}
	public <T> Pageable toSpringDataPageable(
			PaginacioParamsDto dto) {
		return toSpringDataPageable(dto, null);
	}

	
	public <T> PaginaDto<T> toPaginaDto(
			Page<?> page,
			Class<T> targetType) {
		PaginaDto<T> dto = new PaginaDto<T>();
		dto.setNumero(page.getNumber());
		dto.setTamany(page.getSize());
		dto.setTotal(page.getTotalPages());
		dto.setElementsTotal(page.getTotalElements());
		dto.setAnteriors(page.hasPreviousPage());
		dto.setPrimera(page.isFirstPage());
		dto.setPosteriors(page.hasNextPage());
		dto.setDarrera(page.isLastPage());
		if (page.hasContent()) {
			dto.setContingut(
					conversioTipusHelper.convertirList(
							page.getContent(),
							targetType));
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
					conversioTipusHelper.convertirList(
							llista,
							targetType));
		}
		return dto;
	}
	
	@SuppressWarnings("unchecked")
	public <T> PaginaDto<T> toPaginaDto(Page<?> page) {
		PaginaDto<T> dto = new PaginaDto<T>();
		dto.setNumero(page.getNumber());
		dto.setTamany(page.getSize());
		dto.setTotal(page.getTotalPages());
		dto.setElementsTotal(page.getTotalElements());
		dto.setAnteriors(page.hasPreviousPage());
		dto.setPrimera(page.isFirstPage());
		dto.setPosteriors(page.hasNextPage());
		dto.setDarrera(page.isLastPage());
		if (page.hasContent()) {
			dto.setContingut((List<T>)page.getContent());
		}
		return dto;
	}

	public <T> PaginaDto<T> toPaginaDto(
			List<T> elements,
			int elementsTotal,
			PaginacioParamsDto paginacioParams) {
		PaginaDto<T> dto = new PaginaDto<T>();
		int paginesTotal = elementsTotal / paginacioParams.getPaginaTamany();
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
			int elementsTotal,
			PaginacioParamsDto paginacioParams,
			Converter<S, T> converter) {
		PaginaDto<T> dto = new PaginaDto<T>();
		int paginesTotal = elementsTotal / paginacioParams.getPaginaTamany();
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
	public <T> PaginaDto<T> toPaginaDto(
			List<T> elements,
			int elementsTotal,
			PaginacioParamsDto paginacioParams,
			Class<T> targetType) {
		PaginaDto<T> dto = new PaginaDto<T>();
		int paginesTotal = elementsTotal / paginacioParams.getPaginaTamany();
		dto.setNumero(paginacioParams.getPaginaNum());
		dto.setTamany(paginacioParams.getPaginaTamany());
		dto.setTotal(paginesTotal);
		dto.setElementsTotal(elementsTotal);
		dto.setAnteriors(paginacioParams.getPaginaNum() > 0);
		dto.setPrimera(paginacioParams.getPaginaNum() == 0);
		dto.setPosteriors(paginacioParams.getPaginaNum() < paginesTotal - 1);
		dto.setDarrera(paginacioParams.getPaginaNum() == paginesTotal - 1);
		if (elements != null && elements.size() > 0) {
			dto.setContingut(
					conversioTipusHelper.convertirList(
							elements,
							targetType));
		}
		return dto;
	}
	public <T> Sort toSpringDataSort(
			PaginacioParamsDto dto) {
		return toSpringDataSort(dto.getOrdres(), null);
	}
	public Sort toSpringDataSort(
			List<OrdreDto> ordres,
			Map<String, String[]> mapeigPropietatsOrdenacio) {
		List<Order> orders = new ArrayList<Order>();
		if (ordres != null) {
			for (OrdreDto ordre: ordres) {
				Direction direccio = OrdreDireccioDto.DESCENDENT.equals(ordre.getDireccio()) ? Sort.Direction.DESC : Sort.Direction.ASC;
				if (mapeigPropietatsOrdenacio != null) {
					String[] mapeig = mapeigPropietatsOrdenacio.get(ordre.getCamp());
					if (mapeig != null) {
						for (String prop: mapeig) {
							orders.add(new Order(
									direccio,
									prop));
						}
					} else {
						orders.add(new Order(
								direccio,
								ordre.getCamp()));
					}
				} else {
					orders.add(new Order(
							direccio,
							ordre.getCamp()));
				}
			}
		}
		if (!orders.isEmpty())
			return new Sort(orders);
		else
			return null;
	}
	public boolean esPaginacioActivada(PaginacioParamsDto dto) {
		return dto.getPaginaTamany() > 0;
	}

	public interface Converter<S, T> {
		T convert(S source);
	}

}
