package es.caib.helium.expedient.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.caib.helium.ms.model.DefaultOrder;
import es.caib.helium.ms.model.DefaultSort;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Informació de la tasca amb les propietats necessàries pel llistat filtrat i paginat de
 * tasques.
 * 
 */
@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DefaultSort(sortFields = @DefaultOrder(field = "usuariCodi"))
public class ResponsableDto {

	@Null
	@JsonProperty("id")
	@Schema(example = "1234", accessMode = AccessMode.READ_ONLY, description = "Id intern del responsable")
	private Long id;
	

	@JsonProperty("usuariCodi")
	@Schema(example = "admin", required = true, description = "Codi de l'usuari del responsable.")
	@Size(max = 255)
	@NotEmpty
	private String usuariCodi;	
}
