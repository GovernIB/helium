package net.conselldemallorca.helium.ms.domini;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import net.conselldemallorca.helium.ms.HeliumMsPropietats;
import net.conselldemallorca.helium.ms.domini.client.DominiMsClient;
import net.conselldemallorca.helium.ms.domini.client.model.DominiPagedList;

/** Bean per interactuar amb el Micro Servei de Dominis. Encapsula un client
 * de l'API REST pel disseny i consulta dels dominis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Service
public class DominiMs {
	
	@Autowired
	private HeliumMsPropietats heliumMsPropietats;

	/** Referència a la instànca de client de l'API REST de Dominis. */
	private DominiMsClient dominiClient;
			
	/** Mètode per configurar el client de l'API REST de dominis.
	 */
	@PostConstruct
    public void init() {
		this.dominiClient = new DominiMsClient(
				heliumMsPropietats.getBaseUrl(), 
				heliumMsPropietats.getUsuari(), 
				heliumMsPropietats.getPassword());
    }
		
	/**
	 * Cerca de dominis Obté una página de la cerca dels **dominis** del
	 * sistema. La cerca pot rebre paràmetres per - ordenar - paginar - filtrar
	 * (utilitzant sintaxi rsql)
	 * <p>
	 * <b>200</b> - Ok
	 * <p>
	 * <b>204</b> - No content
	 * <p>
	 * <b>400</b> - Bad input parameter
	 * 
	 * @param entornId
	 *            Identificador de l&#x27;entorn
	 * @param filtre
	 *            cadena amb format rsql per a definir el filtre a aplicar a la
	 *            consulta
	 * @param expedientTipusId
	 *            Identificador del tipus d&#x27;expedient al que pertany el
	 *            domini
	 * @param expedientTipusPareId
	 *            Identificador del tipus d&#x27;expedient pare al que pertany
	 *            el domini (en cas d\\&#x27;herència)
	 * @param page
	 *            número de pagina a retornar en cas de desitjar paginació
	 * @param size
	 *            mida de la pàgina a retornat en cas de desitjar paginació
	 * @param sort
	 *            ordre a aplicar a la consulta
	 * @return DominiPagedList
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 */
	public DominiPagedList listDominisV1(Long entornId, String filtre, Long expedientTipusId, Long expedientTipusPareId,
			Integer page, Integer size, String sort) {
		
		return this.dominiClient.listDominisV1(entornId, filtre, expedientTipusId, expedientTipusPareId, page, size, sort);
	}

	
	/// Mètode de Disseny
	
	/// Mètodes de Consulta
}
