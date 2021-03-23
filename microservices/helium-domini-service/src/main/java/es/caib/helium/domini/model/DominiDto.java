package es.caib.helium.domini.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class DominiDto {

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

	@NotNull
	@JsonProperty("tipus")
	@Schema(example = "CONSULTA_REST", description = "")
	private TipusDominiEnum tipus = null;

	@JsonProperty("url")
	@Schema(example = "http://example.com/nom_domini", description = "")
	@Size(max = 255)
	private String url = null;

	@JsonProperty("tipusAuth")
	@Schema(example = "HTTP_BASIC", description = "")
	private TipusAuthEnum tipusAuth = null;

	@JsonProperty("origenCredencials")
	@Schema(example = "PROPERTIES", description = "")
	private OrigenCredencialsEnum origenCredencials = null;

	@JsonProperty("usuari")
	@Schema(example = "codi_usuari", description = "")
	@Size(max = 255)
	private String usuari = null;

	@JsonProperty("contrasenya")
	@Schema(example = "password", description = "")
	private String contrasenya = null;

	@JsonProperty("sql")
	@Schema(example = "select camp_1, camp_2, camp_3 from taula where camp_4 = :parametre", description = "")
	@Size(max = 1024)
	private String sql = null;

	@JsonProperty("jndiDatasource")
	@Schema(example = "java:/es.caib.example.db", description = "")
	@Size(max = 255)
	private String jndiDatasource = null;

	@JsonProperty("descripcio")
	@Schema(example = "Text descriptiu del domini", description = "")
	@Size(max = 255)
	private String descripcio = null;

	@JsonProperty("cacheSegons")
	@Schema(description = "En cas de posar el valor 0 ???")
	private Integer cacheSegons = null;

	@JsonProperty("timeout")
	@Schema(description = "En cas de posar el valor 0 ???")
	private Integer timeout = null;

	@JsonProperty("ordreParams")
	@Schema(description = "")
	@Size(max = 255)
	private String ordreParams = null;

	@NotNull
	@JsonProperty("entorn")
	@Schema(description = "Identificador de l'entorn al que pertany el domini")
	private Long entorn = null;

	@JsonProperty("expedientTipus")
	@Schema(description = "Identificador del tipus d'expedient al que pertany el domini. Si el domini és global aquest identificador serà null.")
	private Long expedientTipus = null;

//  public DominiDto id(Long id) {
//    this.id = id;
//    return this;
//  }

}
