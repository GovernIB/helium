package es.caib.helium.expedient.model;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Informació de l'expedient amb les propietats necessàries pel llistat filtrat i paginat
 * d'expedients.
 * 
 */
@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@DefaultSort(sortFields = @DefaultOrder(field = "dataInici"))
public class ExpedientDto {

	/** Identificador de l'expedient a Helium */
	@JsonProperty("id")
	@Schema(example = "1234", required = true, description = "Id de l'expedient a Helium.")
	private Long id;
	
	@JsonProperty("entornId")
	@Schema(example = "1234", required = true, description = "Id de l'entonr a Helium.")
	private Long entornId;

	@JsonProperty("expedientTipusId")
	@Schema(example = "1234", required = true, description = "Id del tipus d'expedient a Helium.")
	private Long expedientTipusId;
	

	@JsonProperty("processInstanceId")
	@Schema(example = "\"1234\"", required = true, description = "Id de la instància de procés que es correspon amb l'expedient.")
	private String processInstanceId;
	
	@JsonProperty("numero")
	@Schema(example = "123/2021", required = true, description = "Número de l'expedient")
	@Size(max = 64)
	@NotEmpty
	private String numero;

	@JsonProperty("titol")
	@Schema(example = "Títol expedient", required = false, description = "Títol de l'expedient.")
	@Size(max = 255)
	private String titol;

	@JsonProperty("dataInici")
	@Schema(example = "2021-01-31 22:33:44.555", required = true, description = "Data d'inici de l'expedient.")
	private Date dataInici;
	
	@JsonProperty("dataFi")
	@Schema(example = "2021-01-31 22:33:44.555", required = false, description = "Data de finalització l'expedient.")
	private Date dataFi;
	
	@JsonProperty("estatTipus")
	@Schema(example = "CUSTOM", required = true, description = "Indica el tipus d'estat CUSTOM, INICAT o FINALITZAT.")
	private ExpedientEstatTipusEnum estatTipus;
	
	@JsonProperty("estatId")
	@Schema(example = "2", description = "identificador de l'estat dins de la tipologia. Si no és un estat específic els possibles valors són INICAT o FINALITZAT")
	private Long estatId;

	@JsonProperty("aturat")
	@Schema(example = "true", required = true, description = "Indica si l'expedient està aturat")
	@Builder.Default
	private boolean aturat = false;

	@JsonProperty("infoAturat")
	@Schema(example = "Expedient aturat", required = false, description = "Informació que introdueix l'usuari quan atura un expedient.")
	@Size(max = 1024)
	private String infoAturat;
	
	@JsonProperty("anulat")
	@Schema(example = "true", required = false, description = "Indica si l'expedient està anul·lat.")
	@Builder.Default
	private boolean anulat = false;

	@JsonProperty("comentariAnulat")
	@Schema(example = "Comentari anul·lat", required = false, description = "Comentari que introdueix l'usuari quan anul·la un expedient.")
	@Size(max = 255)
	private String comentariAnulat;

	@JsonProperty("alertesTotals")
	@Schema(example = "1", required = false, description = "Número d'alertes totals que té un expedient")
	@Builder.Default
	private Long alertesTotals = 0L;

	@JsonProperty("alertesPendents")
	@Schema(example = "0", required = false, description = "Número d'alertes pendents per llegir que té un expedient")
	@Builder.Default
	private Long alertesPendents = 0L;

	@JsonProperty("ambErrors")
	@Schema(example = "false", required = false, description = "Indica si l'expedient té algun error per marcarlo en el llistat o filtrar-lo")
	@Builder.Default
	private boolean ambErrors = false;
}
