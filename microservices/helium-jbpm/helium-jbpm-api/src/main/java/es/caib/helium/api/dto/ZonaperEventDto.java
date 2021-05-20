/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO amb informació d'un event de la zonaper.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ZonaperEventDto {

	protected String titol;
	protected String text;
	protected String textSMS;
	protected String enllasConsulta;
	protected List<ZonaperDocumentDto> documents;

}
