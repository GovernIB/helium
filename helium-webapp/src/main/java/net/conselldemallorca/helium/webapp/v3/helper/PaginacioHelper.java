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

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitats per a la paginació de consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PaginacioHelper {

	public static PaginacioParamsDto getPaginacioDtoFromDatatable(
			HttpServletRequest request) {
		return getPaginacioDtoFromDatatable(request, null);
	}
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
			String columna = dtInfo.getProperty()[dtInfo.getSortCol()[i]];
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
		LOGGER.debug("Informació de la pàgina sol·licitada (paginaNum=" + paginacio.getPaginaNum() + ", paginaTamany=" + paginacio.getPaginaTamany() + ")");
		return paginacio;
	}

	public static PaginacioParamsDto getPaginacioDtoTotsElsResultats() {
		PaginacioParamsDto paginacio = new PaginacioParamsDto();
		paginacio.setPaginaNum(0);
		paginacio.setPaginaTamany(-1);
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
		List<Object[]> aaData = new ArrayList<Object[]>();
		if (pagina.getContingut() != null) {
			for (T registre: pagina.getContingut()) {
				Object[] dadesRegistre = new Object[dtInfo.getProperty().length];
				for (int i = 0; i < dtInfo.getProperty().length; i++) {
					try {
						String propietatNom = dtInfo.getProperty()[i];
						// Si l'expressió conté múltiples termes separats per punt
						// controla que el valor del primer terme no sigui null a
						// l'objecte per evitar NullPointerException al PropertyUtils
						boolean primerTermeNull = false;
						if (propietatNom.contains(".")) {
							String propietatTerme1 = propietatNom.substring(
									0,
									propietatNom.indexOf("."));
							Object valorTerme1 = PropertyUtils.getProperty(registre, propietatTerme1);
							primerTermeNull = valorTerme1 == null;
						}
						if (!primerTermeNull) {
							Object valor = PropertyUtils.getProperty(registre, propietatNom);
							dadesRegistre[i] = valor;
							/*if (valor instanceof Date) {
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
								dadesRegistre[i] = "new Date(" + sdf.format((Date)valor) + ")";
							} else {
								dadesRegistre[i] = (valor != null) ? valor.toString() : null;
							}*/
						}
					} catch (Exception ex) {
						dadesRegistre[i] = "(!)";
						LOGGER.error(
								"No s'ha pogut llegir la propietat de l'objecte (propietat=" + dtInfo.getProperty()[i] + ")",
								ex);
					}
				}
				aaData.add(dadesRegistre);
			}
		}
		resposta.setAaData(aaData);
		LOGGER.debug("Informació per a datatables (totalRecords=" + resposta.getiTotalRecords() + ", totalDisplayRecords=" + resposta.getiTotalDisplayRecords() + ", echo=" + resposta.getsEcho() + ")");
		return resposta;
	}
	public static <T> DatatablesPagina<T> getPaginaPerDatatables(
			HttpServletRequest request,
			List<T> llista) {
		if (llista != null)
			LOGGER.debug("Informació de la llista (tamany=" + llista.size() + ")");
		else
			LOGGER.debug("Informació de la llista (null)");
		PaginaDto<T> dto = new PaginaDto<T>();
		dto.setNumero(0);
		dto.setTamany((llista != null) ? llista.size() : 0);
		dto.setTotal(1);
		dto.setElementsTotal((llista != null) ? llista.size() : 0);
		dto.setAnteriors(false);
		dto.setPrimera(true);
		dto.setPosteriors(false);
		dto.setDarrera(true);
		dto.setContingut(llista);
		return getPaginaPerDatatables(request, dto);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PaginacioHelper.class);

}
