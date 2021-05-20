/**
 * 
 */
package es.caib.helium.api.dto;


import lombok.Getter;
import lombok.Setter;

/**
 * DTO amb informació d'un document de disseny.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class DocumentDissenyDto {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private boolean plantilla;
	private String contentType;
	private String custodiaCodi;
	private Integer tipusDocPortasignatures;
	private boolean adjuntarAuto;
	private String convertirExtensio;
	private String extensionsPermeses;

}
