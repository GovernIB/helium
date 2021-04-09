package es.caib.helium.dada.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

/**
 * Domini
 */
@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@DefaultSort(sortFields = @DefaultOrder(field = "nom"))
public class DadaDto {

	@Null
	@JsonProperty("id")
	@Schema(example = "1234", readOnly = true, description = "")
	private Long id = null;

	@JsonProperty("codi")
	@Schema(example = "codi_domini", required = true, description = "")
	@Size(max = 64)
	private String codi = null;

	@NotBlank
	@JsonProperty("nom")
	@Schema(example = "nom_domini", required = true, description = "")
	@Size(max = 255)
	private String nom = null;

}
