package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto.TipusConsultaCamp;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEstadisticaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesCarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxInfoDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesIniciFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.exception.ExportException;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.exportacio.ExpedientTipusExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.ExpedientTipusExportacioCommandDto;;

/**
 * Servei per al manteniment dels fluxes de firmes del portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PortafirmesFluxService {

	/**
	 * Inicia un flux de firma
	 * 
	 * @param urlReturn
	 * 				Url on es retornarà la cridada de Portafib.
	 * @param tipusDocumentNom
	 * 				El nom del tipus de document per definir nom flux.
	 * @return El id de la transacció i la url de redirecció.
	 */
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(
			String urlReturn,
			boolean isPlantilla) throws SistemaExternException;
	
	/**
	 * Recupera un flux de firma creat (id)
	 * 
	 * @param transaccioId
	 * 				Id de la transacció.
	 * @return La el id del flux de firma o error.
	 */
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String transactionId);
	
	/**
	 * Tanca un transacció.
	 * 
	 * @param transaccioId
	 * 				Id de la transacció.
	 */
	public void tancarTransaccio(String idTransaccio);
	
	/**
	 * Recupera el detall d'un flux de firma creat anteriorment.
	 * 
	 * @param plantillaFluxId
	 * 				Id de la plantilla.
	 * @return Informació bàsica del flux de firma.
	 */
	public PortafirmesFluxInfoDto recuperarDetallFluxFirma(String plantillaFluxId);
	
	/**
	 * Recupera un llistat de les plantilles disponibles per un usuari aplicació
	 * @param filtrar 
	 * @param transaccioId
	 * 				Id de la transacció.
	 * 
	 * @return La el id del flux de firma o error.
	 */
	public List<PortafirmesFluxRespostaDto> recuperarPlantillesDisponibles(boolean filtrar);
	
	/**
	 * Recupera un llistat de les plantilles disponibles per un usuari aplicació
	 * 
	 * @param transaccioId
	 * 				Id de la transacció.
	 * @return La el id del flux de firma o error.
	 */
	public boolean esborrarPlantilla(String plantillaFluxId);
	
	/**
	 * Recupera una url per mostrar la informació de una plantilla.
	 * 
	 * @param plantillaFluxId
	 * 				Id de la plantilla.
	 * @return Informació bàsica del flux de firma.
	 */

	public String recuperarUrlMostrarPlantilla(String plantillaFluxId);
	
	/**
	 * Recupera una url per editar una plantilla creada.
	 * 
	 * @param plantillaFluxId
	 * 				Id de la plantilla.
	 * @return Informació bàsica del flux de firma.
	 */
	public String recuperarUrlEdicioPlantilla(
			String plantillaFluxId,
			String returnUrl);
	
	/**
	 * Recupera un llistat dels càrrecs disponibles per l'usuari aplicació configurat
	 * 
	 * @return Els càrrecs
	 */
	public List<PortafirmesCarrecDto> recuperarCarrecs();

}