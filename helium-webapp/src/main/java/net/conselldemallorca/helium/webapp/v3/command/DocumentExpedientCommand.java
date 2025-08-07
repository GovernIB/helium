/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.Date;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import net.conselldemallorca.helium.v3.core.api.dto.DocumentTipusFirmaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Create;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Massiu;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Update;
import net.conselldemallorca.helium.webapp.v3.validator.DocumentExpedient;

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
