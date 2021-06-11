package es.caib.helium.expedient.model;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.caib.helium.ms.model.DefaultOrder;
import es.caib.helium.ms.model.DefaultSort;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@DefaultSort(sortFields = @DefaultOrder(field = "dataCreacio"))
public class TascaDto {

	@JsonProperty("id")
	@Schema(example = "1234", required = true, description = "Id de la tasca a Helium.")
	private Long id;
	
	@JsonProperty("expedientId")
	@Schema(example = "1234", required = true, description = "Id de l'expedient de la tasca.")
	private Long expedientId;

	@JsonProperty("nom")
	@Schema(example = "nom tasca", required = true, description = "Nom de la tasca en el flux")
	@Size(max = 255)
	@NotEmpty
	private String nom;

	@JsonProperty("titol")
	@Schema(example = "Títol tasca", required = true, description = "Títol de la tasca a l'expedient. Pot coincidir amb el nom.")
	@Size(max = 255)
	@NotEmpty
	private String titol;
	
	@JsonProperty("afagada")
	@Schema(example = "true", required = false, description = "Indica si la tasca està agafada.")
	@Builder.Default
	private boolean afagada = false;

	@JsonProperty("cancelada")
	@Schema(example = "true", required = false, description = "Indica si la tasca està cancel·lada.")
	@Builder.Default
	private boolean cancelada = false;

	@JsonProperty("suspesa")
	@Schema(example = "true", required = false, description = "Indica si la tasca està suspesa.")
	@Builder.Default
	private boolean suspesa = false;

	@JsonProperty("completada")
	@Schema(example = "true", required = false, description = "Indica si la tasca està completada.")
	@Builder.Default
	private boolean completada = false;

	@JsonProperty("assignada")
	@Schema(example = "true", required = false, description = "Indica si la tasca està assignada.")
	@Builder.Default
	private boolean assignada = false;

	@JsonProperty("marcadaFinalitzar")
	@Schema(example = "true", required = false, description = "Indica si la tasca està marcada per finalitzar.")
	@Builder.Default
	private boolean marcadaFinalitzar = false;

	@JsonProperty("errorFinalitzacio")
	@Schema(example = "true", required = false, description = "Indica si la tasca ha tingut errors de finalització.")
	@Builder.Default
	private boolean errorFinalitzacio = false;

	@JsonProperty("tascaTramitacioMassiva")
	@Schema(example = "true", required = false, description = "Indica si la tasca es pot tramitar massivament.")
	@Builder.Default
	private boolean tascaTramitacioMassiva = false;
			
	@JsonProperty("dataFins")
	@Schema(example = "2021-01-31 22:33:44.555", required = false, description = "Data límit de la tasca.")
	private Date dataFins;

	@JsonProperty("dataFi")
	@Schema(example = "2021-01-31 22:33:44.555", required = false, description = "Data de finalització l'expedient.")
	private Date dataFi;

	@JsonProperty("iniciFinalitzacio")
	@Schema(example = "2021-01-31 22:33:44.555", required = false, description = "Data d'inici de la finalització de la tasca.")
	private Date iniciFinalitzacio;

	@JsonProperty("dataCreacio")
	@Schema(example = "2021-01-31 22:33:44.555", required = true, description = "Data de creació de la tasca.")
	private Date dataCreacio;

	@JsonProperty("usuariAssignat")
	@Schema(example = "user", required = false, description = "Codi de l'usuari assignat a la tasca.")
	@Size(max = 255)
	private String usuariAssignat;

	@JsonProperty("grupAssignat")
	@Schema(example = "role", required = false, description = "Codi del grup assignat a la tasca.")
	@Size(max = 255)
	private String grupAssignat;

	
	@JsonProperty("responsables")
	@Schema(example = "{{'usuariCodi':'usuari1'}, {'usuariCodi':'usuari2'}}", required = false, description = "Conjunt d'usuaris responsables.")
	@Size(max = 255)
	private List<ResponsableDto> responsables;
	
}
