package es.caib.helium.expedient.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import es.caib.helium.ms.model.DefaultOrder;
import es.caib.helium.ms.model.DefaultSort;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Informació d'una instància de procés amb les propietats necessàries pel llistat filtrat i paginat de
 * instàncies de processos.
 * 
 */
@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@DefaultSort(sortFields = @DefaultOrder(field = "dataInici"))
public class ProcesDto {

	@JsonProperty("id")
	@Schema(example = "1234", required = false, accessMode = AccessMode.READ_ONLY, description = "Identificador intern del procés al MS")
	private Long id;

	@JsonProperty("motor")
	@Schema(example = "JBPM01", required = true, description = "Codi identificador del motor.")
	@Size(max = 64)
	@NotEmpty
	private String motor;

	@JsonProperty("procesId")
	@Schema(example = "1234", required = true, description = "Id de la instància de procés.")
	@Size(max = 64)
	@NotEmpty
	private String procesId;
	
	@JsonProperty("expedientId")
	@Schema(example = "1234", required = false, description = "Id de l'expedient del procés.")
	private Long expedientId;
	
	@JsonProperty("processDefinitionId")
	@Schema(example = "1234", required = true, description = "Identificador del process definition ID de la instància de procés.")
	private String processDefinitionId;

	@JsonProperty("procesArrelId")
	@Schema(example = "1234", required = true, description = "Id del procés arrel al qual pertany la instància de procés. "
			+ "Si l'expedient només té un procés llavors l'arrel serà el procés mateix.")
	@Size(max = 64)
	@NotEmpty
	private String procesArrelId;

	@JsonProperty("procesPareId")
	@Schema(example = "1234", required = false, description = "Id del procés pare dins l'arbre de processos. Si no té pare i és el "
			+ "procés arrel llavors aquesta propietat serà nula i no tindrà procés pare.")
	@Size(max = 64)
	private String procesPareId;

	@JsonProperty("suspes")
	@Schema(example = "false", required = true, description = "Indica el procés està suspés.")
	@Builder.Default
	private boolean suspes = false;

	@JsonProperty("descripcio")
	@Schema(example = "subproces notificació", required = false, description = "Pot contenir la propietat descripció de la instancia de procés")
	private String descripcio;
	
	@JsonProperty("dataInici")
	@NotNull
	@Schema(example = "2021-01-31 22:33:44.555", required = true, description = "Data de finalització la tasca")
	private Date dataInici;

	@JsonProperty("dataFi")
	@Schema(example = "2021-01-31 22:33:44.555", required = false, description = "Data de finalització del procés")
	private Date dataFi;	
}
