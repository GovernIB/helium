/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.webapp.v3.datatables.DatatablesInfo;
import net.conselldemallorca.helium.webapp.v3.datatables.DatatablesPagina;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

/**
 * Utilitats per a la paginació de consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PaginacioHelper {

	public static PaginacioParamsDto getPaginacioDtoFromDatatable(
			HttpServletRequest request,
			Map<String, String[]> mapeigOrdenacions) {
		DatatablesInfo dtInfo = new DatatablesInfo(request);
		LOGGER.debug("Informació de la pàgina obtingudes de datatables (displayStart=" + dtInfo.getDisplayStart() + ", displayLength=" + dtInfo.getDisplayLength() + ", echo=" + dtInfo.getEcho() + ")");
		PaginacioParamsDto paginacio = new PaginacioParamsDto();
		int paginaNum = dtInfo.getDisplayStart() / dtInfo.getDisplayLength();
		paginacio.setPaginaNum(paginaNum);
		paginacio.setPaginaTamany(dtInfo.getDisplayLength());
		for (int i = 0; i < dtInfo.getSortingCols(); i++) {
			String columna = dtInfo.getProperty()[i];
			OrdreDireccioDto direccio;
			if ("asc".equals(dtInfo.getSortDir()[i]))
				direccio = OrdreDireccioDto.ASCENDENT;
			else
				direccio = OrdreDireccioDto.DESCENDENT;
			String[] columnes = new String[] {columna};
			if (mapeigOrdenacions != null && mapeigOrdenacions.get(columna) != null) {
				columnes = mapeigOrdenacions.get(columna);
			}
			for (String col: columnes) {
				paginacio.afegirOrdre(col, direccio);
				LOGGER.debug("Afegida ordenació a la paginació (columna=" + columna + ", direccio=" + direccio + ")");
			}
		}
		/*System.out.println(">>> sortingCols: " + dtInfo.getSortingCols());
		System.out.println(">>> sortCol: " + dtInfo.getSortCol());
		for (int i = 0; i < dtInfo.getSortCol().length; i++)
			System.out.println(">>>    " + dtInfo.getSortCol()[i]);
		System.out.println(">>> sortDir: " + dtInfo.getSortDir());
		for (int i = 0; i < dtInfo.getSortDir().length; i++)
			System.out.println(">>>    " + dtInfo.getSortDir()[i]);
		paginacio.afegirOrdre(
				columnes[0],
				OrdreDireccioDto.ASCENDENT);*/
		LOGGER.debug("Informació de la pàgina sol·licitada (paginaNum=" + paginacio.getPaginaNum() + ", paginaTamany=" + paginacio.getPaginaTamany() + ")");
		return paginacio;
	}

	public static <T> DatatablesPagina<T> getPaginaPerDatatables(
			HttpServletRequest request,
			PaginaDto<T> pagina) {
		LOGGER.debug("Informació de la pàgina (numero=" + pagina.getNumero() + ", tamany=" + pagina.getTamany() + ", total=" + pagina.getTotal() + ", elementsTotal=" + pagina.getElementsTotal() + ")");
		DatatablesInfo dtInfo = new DatatablesInfo(request);
		DatatablesPagina<T> resposta = new DatatablesPagina<T>();
		resposta.setiTotalRecords(pagina.getElementsTotal());
		resposta.setiTotalDisplayRecords(pagina.getElementsTotal());
		resposta.setsEcho(dtInfo.getEcho());
		List<String[]> aaData = new ArrayList<String[]>();
		for (T registre: pagina.getContingut()) {
			String[] dadesRegistre = new String[dtInfo.getProperty().length];
			for (int i = 0; i < dtInfo.getProperty().length; i++) {
				try {
					dadesRegistre[i] = BeanUtils.getProperty(registre, dtInfo.getProperty()[i]);
				} catch (Exception ex) {
//					dadesRegistre[i] = "!!!";
					LOGGER.error("No s'ha pogut llegir la propietat de l'objecte (propietat=" + dtInfo.getProperty()[i] + ")");
				}
			}
			aaData.add(dadesRegistre);
		}
		resposta.setAaData(aaData);
		LOGGER.debug("Informació per a datatables (totalRecords=" + resposta.getiTotalRecords() + ", totalDisplayRecords=" + resposta.getiTotalDisplayRecords() + ", echo=" + resposta.getsEcho() + ")");
		return resposta;
	}
	public static <T> DatatablesPagina<T> getPaginaPerDatatables(
			HttpServletRequest request,
			List<T> llista) {
		LOGGER.debug("Informació de la llista (tamany=" + llista.size() + ")");
		PaginaDto<T> dto = new PaginaDto<T>();
		dto.setNumero(0);
		dto.setTamany(llista.size());
		dto.setTotal(1);
		dto.setElementsTotal(llista.size());
		dto.setAnteriors(false);
		dto.setPrimera(true);
		dto.setPosteriors(false);
		dto.setDarrera(true);
		dto.setContingut(llista);
		return getPaginaPerDatatables(request, dto);
	}

	public static <T> PaginaDto<T> toPaginaDto(Page<?> page) {
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
			dto.setContingut((List<T>) page.getContent());
		}
		return dto;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PaginacioHelper.class);

}
