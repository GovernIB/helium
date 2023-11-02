package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesCarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxInfoDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesIniciFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;;

/**
 * Servei per al manteniment dels fluxes de firmes del portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PortafirmesFluxService {

	/**
	 * Inicia un flux de firma
	 * 
	 * @param expedientTipusId
	 * 				ID del tipus d'expedient pel qual s'està creant el flux. S'usarà per crear el filtre per descripció.
	 * @param definicioProcesId
	 * 				ID de la definició de procés on s'està creant el flux. S'usarà per crear el filtre per descripció en el cas de les definicions de procés globals..
	 * @param usuariCodi
	 * 				Codi de l'usuari quan es crea des de la gestió de documents per poder esborrar-los. Si es crea des del disseny s'ha de passar buit.
	 * @param urlReturn
	 * 				Url on es retornarà la cridada de Portafib.
	 * @param tipusDocumentNom
	 * 				El nom del tipus de document per definir nom flux.
	 * @return El id de la transacció i la url de redirecció.
	 */
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(
			Long expedientTipusId,
			Long definicioProcesId,
			String usuariCodi,
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
	 * Recupera un llistat de les plantilles disponibles per un tipus d'expedient.
	 * 
	 * @param expedientTipusId 
	 * 				Id del tipus d'expedient per filtrar per entorn i tipus d'expedient.
	 * @param definicioProcesId 
	 * 				Id de la definició de procés quan està definida a nivell de definició de procés per filtrar per entorn, tipus d'expedient i definició de procés.
	 * @param usuari 
	 * 				Codi de l'usuari per trobar les plantilles definides per l'usuari. 
	 * 
	 * @return La el id del flux de firma o error.
	 */
	public List<PortafirmesFluxRespostaDto> recuperarPlantillesDisponibles(Long expedientTipusId, Long definicioProcesId, String usuari);
	
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
	 * Recupera url per visualitzar l'estat d'un flux de firmes d'una petició.
	 *
	 * @param portafirmesId
	 * 				Id de la petició de firma.
	 * @return la url de Portafirmes.
	 * @throws SistemaExternException
	 *            Hi ha hagut algun error en la comunicació amb el portafirmes.
	 */
	public String recuperarUrlViewEstatFluxDeFirmes(long portafirmesId);
	
	/**
	 * Recupera un llistat dels càrrecs disponibles per l'usuari aplicació configurat
	 * 
	 * @return Els càrrecs
	 */
	public List<PortafirmesCarrecDto> recuperarCarrecs();

}