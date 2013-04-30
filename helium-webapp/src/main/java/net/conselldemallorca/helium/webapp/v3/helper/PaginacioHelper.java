/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.webapp.v3.datatables.DatatablesInfo;
import net.conselldemallorca.helium.webapp.v3.datatables.DatatablesPagina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitats per a la paginació de consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PaginacioHelper {

	public static PaginacioParamsDto getPaginacioDtoFromDatatable(
			HttpServletRequest request,
			String[] nomColumnes,
			Map<String, String[]> mapeigOrdenacions) {
		DatatablesInfo dtInfo = new DatatablesInfo(request);
		LOGGER.debug("Informació de la pàgina obtingudes de datatables (displayStart=" + dtInfo.getDisplayStart() + ", displayLength=" + dtInfo.getDisplayLength() + ", echo=" + dtInfo.getEcho() + ")");
		PaginacioParamsDto paginacio = new PaginacioParamsDto();
		int paginaNum = dtInfo.getDisplayStart() / dtInfo.getDisplayLength();
		paginacio.setPaginaNum(paginaNum);
		paginacio.setPaginaTamany(dtInfo.getDisplayLength());
		for (int i = 0; i < dtInfo.getSortingCols(); i++) {
			String columna = nomColumnes[dtInfo.getSortCol()[i]];
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
			String[] columnes,
			PaginaDto<T> pagina) {
		LOGGER.debug("Informació de la pàgina (numero=" + pagina.getNumero() + ", tamany=" + pagina.getTamany() + ", total=" + pagina.getTotal() + ", elementsTotal=" + pagina.getElementsTotal() + ")");
		DatatablesInfo dtInfo = new DatatablesInfo(request);
		DatatablesPagina<T> resposta = new DatatablesPagina<T>();
		resposta.setTotalRecords(pagina.getElementsTotal());
		resposta.setTotalDisplayRecords(pagina.getElementsTotal());
		resposta.setColumnNames(columnes);
		resposta.setEcho(dtInfo.getEcho());
		resposta.setData(pagina.getContingut());
		LOGGER.debug("Informació per a datatables (totalRecords=" + resposta.getTotalRecords() + ", totalDisplayRecords=" + resposta.getTotalDisplayRecords() + ", echo=" + resposta.getEcho() + ")");
		return resposta;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PaginacioHelper.class);

}
