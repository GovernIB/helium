/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import net.conselldemallorca.helium.model.dto.ArxiuDto;
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.exception.IllegalArgumentsException;
import net.conselldemallorca.helium.util.DocumentTokenUtils;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Servei per a gestionar les descàrregues d'arxius
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Service
public class DocumentService {

	private DocumentTokenUtils documentTokenUtils;
	private DtoConverter dtoConverter;



	public DocumentDto arxiuDocumentInfo(String token) {
		try {
			String tokenDesxifrat = getDocumentTokenUtils().desxifrarToken(token);
			return getDocumentInfo(Long.parseLong(tokenDesxifrat));
		} catch (Exception ex) {
			logger.error("Error al obtenir el document amb token " + token, ex);
			throw new IllegalArgumentsException("Error al obtenir el document amb token " + token);
		}
	}

	public ArxiuDto arxiuDocumentPerMostrar(Long documentStoreId) {
		DocumentDto document = getDocumentInfo(documentStoreId);
		if (document == null)
			return null;
		if (document.isSignat() || document.isRegistrat()) {
			return getArxiuDocumentVista(documentStoreId);
		} else {
			return getArxiuDocumentOriginal(documentStoreId);
		}
	}
	public ArxiuDto arxiuDocumentPerMostrar(String token) {
		try {
			String tokenDesxifrat = getDocumentTokenUtils().desxifrarToken(token);
			return arxiuDocumentPerMostrar(Long.parseLong(tokenDesxifrat));
		} catch (Exception ex) {
			logger.error("Error al obtenir el document amb token " + token, ex);
			throw new IllegalArgumentsException("Error al obtenir el document amb token " + token);
		}
	}

	public ArxiuDto arxiuDocumentPerSignar(String token, boolean estampar) {
		try {
			String tokenDesxifrat = getDocumentTokenUtils().desxifrarToken(token);
			DocumentDto document = dtoConverter.toDocumentDto(
					Long.parseLong(tokenDesxifrat),
					false,
					false,
					true,
					true,
					estampar);
			if (document == null)
				return null;
			return new ArxiuDto(
					document.getVistaNom(),
					document.getVistaContingut());
		} catch (Exception ex) {
			logger.error("Error al obtenir el document amb token " + token, ex);
			throw new IllegalArgumentsException("Error al obtenir el document amb token " + token);
		}
	}



	@Autowired
	public void setDtoConverter(DtoConverter dtoConverter) {
		this.dtoConverter = dtoConverter;
	}



	private DocumentDto getDocumentInfo(Long documentStoreId) {
		return dtoConverter.toDocumentDto(
				documentStoreId,
				false,
				false,
				false,
				false,
				false);
	}
	private ArxiuDto getArxiuDocumentOriginal(Long documentStoreId) {
		DocumentDto document = dtoConverter.toDocumentDto(
				documentStoreId,
				true,
				false,
				false,
				false,
				false);
		if (document == null)
			return null;
		return new ArxiuDto(
				document.getArxiuNom(),
				document.getArxiuContingut());
	}
	private ArxiuDto getArxiuDocumentVista(Long documentStoreId) {
		DocumentDto document = dtoConverter.toDocumentDto(
				documentStoreId,
				false,
				false,
				true,
				false,
				false);
		if (document == null)
			return null;
		return new ArxiuDto(
				document.getVistaNom(),
				document.getVistaContingut());
	}

	private DocumentTokenUtils getDocumentTokenUtils() {
		if (documentTokenUtils == null)
			documentTokenUtils = new DocumentTokenUtils(
					(String)GlobalProperties.getInstance().get("app.encriptacio.clau"));
		return documentTokenUtils;
	}

	private static final Log logger = LogFactory.getLog(DocumentService.class);

}
