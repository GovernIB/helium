/**
 * 
 */
package es.caib.helium.back.command;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import es.caib.helium.back.command.DocumentExpedientCommand.Create;
import es.caib.helium.back.command.DocumentExpedientCommand.Massiu;
import es.caib.helium.back.command.DocumentExpedientCommand.Update;
import es.caib.helium.back.validator.DocumentExpedient;
import es.caib.helium.commons.dto.DocumentTipusFirmaEnumDto;
import es.caib.helium.commons.dto.NtiEstadoElaboracionEnumDto;
import es.caib.helium.commons.dto.NtiOrigenEnumDto;
import es.caib.helium.commons.dto.NtiTipoDocumentalEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Command per gestionar els documents d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DocumentExpedient(groups = {Create.class, Update.class, Massiu.class})
public class DocumentExpedientCommand {
	
	/** Cadena per diferenciar quan s'adjunta un document adjunt en comptes d'un document definit al tipus d'expedient. */
	public static String ADJUNTAR_ARXIU_CODI = "##adjuntar_arxiu##";

	@NotNull(groups = {Create.class, Update.class})
	private Long expedientId;
	private Long docId;
	private String documentCodi;
	private String codi;
	private String nom;
	@NotNull(groups = {Create.class, Update.class})
	private Date data;
	private NtiOrigenEnumDto ntiOrigen;
	private NtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	private NtiTipoDocumentalEnumDto ntiTipoDocumental;
	private String ntiIdOrigen;
	private String arxiuNom;
	private boolean generarPlantilla = false;
	private MultipartFile arxiu;
	private boolean ntiActiu;
	private boolean ambFirma = false;
	private MultipartFile firma;
	private DocumentTipusFirmaEnumDto tipusFirma = DocumentTipusFirmaEnumDto.ADJUNT;
	/** Indica al validador si ha de validar que els arxius s'hagin adjuntat */
	private boolean validarArxius = false;
	
	private boolean clearFirmes = false;

	public interface Create {}
	public interface Update {}
	public interface Massiu {}
}
