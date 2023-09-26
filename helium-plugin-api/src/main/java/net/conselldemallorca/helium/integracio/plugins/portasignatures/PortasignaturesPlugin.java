package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.util.Date;
import java.util.List;
import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;


/**
 * Plugin per a la integració amb portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PortasignaturesPlugin {

	/**
	 * Puja un document al Portasignatures.
	 * 
	 * @param persona
	 * @param documentDto
	 * @param expedient
	 * @param importancia
	 * @param dataLimit
	 * 
	 * @return Id del document al Portasignatures
	 * 
	 * @throws Exception
	 */
	public Integer uploadDocument (
			DocumentPortasignatures document,
			List<DocumentPortasignatures> annexos,
			boolean isSignarAnnexos,
			List<PortafirmesFluxBloc> blocList,
			String remitent,
			String importancia,
			Date dataLimit,
			String fluxId) throws PortasignaturesPluginException;

	/**
	 * Descarrega un document del Portasignatures
	 * 
	 * @param documentId
	 * 
	 * @return document
	 * 
	 * @throws Exception
	 */
	public List<byte[]> obtenirSignaturesDocument(Integer documentId) throws PortasignaturesPluginException;

	/**
	 * Elimina el document del portasignatures
	 * 
	 * @param document Referència a la petició al portasignatures.
	 * 
	 * @throws PortasignaturesPluginException
	 */
	public void deleteDocuments (Integer document) throws PortasignaturesPluginException;
	
	/**
	 * Inicia un nou flux de firma a partir dels paràmetres d'entrada
	 * proporcionats.
	 *
	 * @param idioma
	 * 			Idioma per crear el flux.
	 * @param isPlantilla
	 * 			Indicar si guardar una plantilla de flux.
	 * @param nom
	 * 			Nom de flux a crear.
	 * @param descripcio
	 * 			Una descripció del flux a crear.
	 * @param descripcioVisible
	 * 			Indicar si visualitzar la descripció.
	 * @param returnUrl
	 * 			Url redirecció on redirigir la resposta una vegada s'ha iniciat
	 * 			la transacció.
	 * @return la informació del flux de firma creat (idTransacció i urlRedirecció).
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public PortafirmesIniciFluxResposta iniciarFluxDeFirma(
			String idioma,
			boolean isPlantilla,
			String nom,
			String descripcio,
			boolean descripcioVisible,
			String returnUrl) throws SistemaExternException;

	/**
	 * Recupera el id del flux de firma creat.
	 *
	 * @param idTransaccio
	 * 				Id de la transacció creada anteriorment.
	 * @return el id del flux de firma o error.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public PortafirmesFluxResposta recuperarFluxDeFirmaByIdTransaccio(
			String idTransaccio) throws SistemaExternException;
	
	/**
	 * Recupera un llistat de les plantilles disponibles per un usuari aplicació.
	 *
	 * @param idioma
	 * 				idioma plantilles.
	 * @return el id del flux de firma o error.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public List<PortafirmesFluxResposta> recuperarPlantillesDisponibles(String idioma) 
			throws SistemaExternException;
	
	/**
	 * Recupera el detall d'un flux de firma creat (nom + descripció).
	 *
	 * @param idPlantilla
	 * 				Id de la plantilla.
	 * @param idioma
	 * 				Idioma flux.
	 * @return la informació del flux de firma.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public PortafirmesFluxInfo recuperarFluxDeFirmaByIdPlantilla(
			String idTransaccio,
			String idioma) throws SistemaExternException;
	
	/**
	 * Recupera una url per mostrar/editar la informació de la plantilal de forma gràfica.
	 *
	 * @param idPlantilla
	 * 				Id de la plantilla.
	 * @param idioma
	 * 				Idioma plantilla/flux.
	 * @param edicio
	 * 		  		Indicar si recuperar url per visualitzar o editar plantilla.
	 * @return la url de Portafirmes.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public String recuperarUrlViewEditPlantilla(
			String idPlantilla,
			String idioma,
			String urlReturn,
			boolean edicio) throws SistemaExternException;
	
	/**
	 * Recupera una url per mostrar/editar la informació de la plantilal de forma gràfica.
	 *
	 * @param idioma
	 * 				Idioma plantilla/flux.
	 * @param plantillaFluxId
	 * 		  		Id de la plantilla a esborrar.
	 * @return true = esborrat / false = no trobada.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public boolean esborrarPlantillaFirma(
			String idioma,
			String plantillaFluxId) throws SistemaExternException;

	/**
	 * Tanca una transacció.
	 *
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public void tancarTransaccioFlux(
			String idTransaccio) throws SistemaExternException;

	
	public List<PortafirmesFluxResposta> recuperarPlantillesPerFiltre(
			String idioma,
			String descripcio) throws SistemaExternException;
	
	/**
	 * Recupera url per visualitzar l'estat d'un flux de firmes d'una petició.
	 *
	 * @param portafirmesId
	 * 				Id de la petició de firma.
	 * @param idioma
	 * 				Idioma plantilla/flux.
	 * @return la url de Portafirmes.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public String recuperarUrlViewEstatFluxDeFirmes(
			long portafirmesId,
			String idioma) throws SistemaExternException;

	/**
	 * Recupera els càrrecs disponibles per un usuari aplicació
	 * 
	 * @return Una llista dels càrrecs
	 * @throws SistemaExternException
	 * 			Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public List<PortafirmesCarrec> recuperarCarrecs() throws SistemaExternException;
	
	/**
	 * Recupera la informació d'un càrrec a partir del seu id
	 * 
	 * @param carrecId
	 * 				ID del càrrec a consultar
	 * @return La informació del càrrec
	 * @throws SistemaExternException
	 * 			Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public PortafirmesCarrec recuperarCarrec(String carrecId) throws SistemaExternException;
}
