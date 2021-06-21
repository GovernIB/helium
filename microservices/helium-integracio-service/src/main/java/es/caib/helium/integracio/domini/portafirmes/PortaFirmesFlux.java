package es.caib.helium.integracio.domini.portafirmes;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PortaFirmesFlux {
	
	@NotNull
	private Long entornId;

	private Long documentId;
	@NotNull @Valid
	private DocumentDto document;
	@NotNull
	private List<DocumentDto> anexos;
	private String codiPersona;	//	codi de la persona signants
	@NotNull
	private List<String> signatarisPas1; // codis de les persones signants en el pas 1
	@NotNull
	private Integer minSignatarisPas1;
	@NotNull
	private List<String> signatarisPas2; // codis de les persones signants en el pas 2
	@NotNull
	private Integer minSignatarisPas2;
	@NotNull
	private List<String> signatarisPas3; // codis de les persones signants en el pas 3
	@NotNull
	private Integer minSignatarisPas3;
	@NotNull
	private Long expedientId;
	@NotNull	
	private String expedientIdentificador;
	private String importancia;
	private Date dataLimit;
	private Long execucioId;
	@NotNull	
	private Long processInstanceId;
	private String transicioOK;
	private String transicioKO;
	@NotNull
	private Long tokenId;
	
	private String codiUsuari;
	private PersonaDto persona;
	@NotNull @NotEmpty @Valid
	private List<PersonaDto> personesPas1;
	@NotNull
	private List<PersonaDto> personesPas2;
	@NotNull
	private List<PersonaDto> personesPas3;
}
