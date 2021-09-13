package es.caib.helium.expedient.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import java.util.List;

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
	@Schema(example = "1234", required = false, accessMode = AccessMode.READ_ONLY, description = "Identificador intern de la tasca al MS")
	private Long id;

	@JsonProperty("tascaId")
	@Schema(example = "1234", required = true, description = "Id de la tasca a Helium.")
	@Size(max = 64)
	@NotEmpty
	private String tascaId;
	
	@JsonProperty("procesId")
	@Schema(example = "1234", required = true, description = "Id del procés al qual pertany la tasca.")
	@Size(max = 64)
	private String procesId;	
	
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
	@Schema(example = "2021-01-31 22:33:44.555", required = false, description = "Conté el valor de la data si la tasca està marcada per finalitzar.")
	private Date marcadaFinalitzar;

	@JsonProperty("errorFinalitzacio")
	@Schema(example = "Error descripció", required = false, description = "Conté la descripció de l'error de finalització en cas d'error a l'hora de finalitzar la tasca")
	private String errorFinalitzacio;
			
	@JsonProperty("dataFins")
	@Schema(example = "2021-01-31 22:33:44.555", required = false, description = "Data límit de la tasca.")
	private Date dataFins;

	@JsonProperty("dataFi")
	@Schema(example = "2021-01-31 22:33:44.555", required = false, description = "Data de finalització la tasca")
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

	@JsonProperty("prioritat")
	@Schema(example = "3", required = false, description = "Prioritat assignada a la tasca.")
	private Integer prioritat;
	
	@JsonProperty("responsables")
	@Schema(example = "{'usuari1', 'usuari2'}", required = false, description = "Llista d'usuaris responsables.")
	private List<String> responsables;

	@JsonProperty("grups")
	@Schema(example = "{'ROL1', 'ROL2'}", required = false, description = "Llista de grups assignats a la tasca.")
	private List<String> grups;

	@JsonProperty("processDefinitionId")
	@Schema(example = "1234abcd", required = false, accessMode = AccessMode.READ_ONLY, description = "Identificador de la definició de procés del procés relacionat a la tasca"
			+ "informat en les consultes de tasques. És un atribut de només lectura")
	@Size(max = 255)
	private String processDefinitionId;	

	@JsonProperty("expedientId")
	@Schema(example = "1234", required = false, accessMode = AccessMode.READ_ONLY, description = "Identificador de l'expedient de la definició de procés del procés relacionat a la tasca"
			+ "informat en les consultes de tasques. És un atribut de només lectura i pot ser null si el procés s'ha creat i encara no s'ha associat l'expedient inicial.")
	private Long expedientId;	
}
