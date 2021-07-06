package es.caib.helium.dada.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Representació de la col·leccio {@link es.caib.helium.enums.Collections#EXPEDIENT}
 * S'utilitza tant com a model per Mongo com per Dto.
 */
@Getter
@Setter
@ToString
@Document
public class Expedient {

	@Id
	private String id;
	@NotNull
	private Long expedientId;
	@NotNull
	private Long entornId;
	@NotNull
	private Long tipusId;
	@Size(max = 64) 
	private String numero;
	@Size(max = 255)
	private String titol;
	@NotNull
	private Long procesPrincipalId;
	private Integer estatId;
	@NotNull
	private Date dataInici;
	private Date dataFi;

	private List<Dada> dades;
	
	@Override
	public boolean equals(Object expedient) {

		if (expedient == this) {
			return true;
		}

		if (!(expedient instanceof Expedient)) {
			return false;
		}
		
		var exp = (Expedient) expedient;
		
		return (expedientId != null && exp.getExpedientId() != null) ? expedientId.equals(exp.getExpedientId()) : true
		&& (entornId != null && exp.getEntornId() != null) ? entornId.equals(exp.getEntornId()) : true
		&& (tipusId != null && exp.getTipusId() != null) ? tipusId.equals(exp.getTipusId()) : true
		&& (numero != null && exp.getNumero() != null) ? numero.equals(exp.getNumero()) : true
		&& (titol != null && exp.getTitol() != null) ? titol.equals(exp.getTitol()) : true
		&& (procesPrincipalId != null && exp.getProcesPrincipalId() != null) 
			? procesPrincipalId.equals(exp.getProcesPrincipalId()) : true
		&& (estatId != null && exp.getEstatId() != null) ? estatId.equals(exp.getEstatId()) : true
		&& (dataInici != null && exp.getDataInici() != null) ? dataInici.equals(exp.getDataInici()) : true
		&& (dataFi != null && exp.getDataFi() != null) ? dataFi.equals(exp.getDataFi()) : true
		&& (dades != null && !dades.isEmpty() && exp.getDades() != null && !exp.getDades().isEmpty())
			? Arrays.equals(dades.toArray(), exp.getDades().toArray()): true ;
	}
}
