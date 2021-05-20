/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * DTO amb informació d'un document d'una tasca de la
 * definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class DocumentNotificacioDto {

	private Long id;
	private String processInstanceId;
	private String documentNom;
	private String arxiuNom;
	private String arxiuExtensio;
	private Date dataCreacio;
	private Date dataModificacio;
	private Date dataDocument;
	
}
