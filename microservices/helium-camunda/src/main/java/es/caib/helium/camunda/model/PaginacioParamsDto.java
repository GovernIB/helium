/**
 * 
 */
package es.caib.helium.camunda.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.logstash.logback.encoder.org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Dto amb els paràmetres per a paginar i ordenar els
 * resultats d'una consulta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class PaginacioParamsDto implements Serializable {

	private int paginaNum = 0;
	private int paginaTamany = -1;
	private String filtre;
	private List<FiltreDto> filtres = new ArrayList<FiltreDto>();
	private List<OrdreDto> ordres = new ArrayList<OrdreDto>();

	public void afegirFiltre(
			String camp,
			String valor) {
		getFiltres().add(
				new FiltreDto(
						camp,
						valor));
	}
	public void afegirOrdre(
			String camp,
			OrdreDireccioDto direccio) {
		getOrdres().add(
				new OrdreDto(
						camp,
						direccio));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Getter @Setter
	@AllArgsConstructor
	public class FiltreDto implements Serializable {
		private String camp;
		private String valor;

		private static final long serialVersionUID = -139254994389509932L;
	}

	public enum OrdreDireccioDto {
		ASCENDENT,
		DESCENDENT
	}

	@Getter @Setter
	@AllArgsConstructor
	@ToString
	public class OrdreDto implements Serializable {
		private String camp;
		private OrdreDireccioDto direccio;

		private static final long serialVersionUID = -139254994389509932L;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
