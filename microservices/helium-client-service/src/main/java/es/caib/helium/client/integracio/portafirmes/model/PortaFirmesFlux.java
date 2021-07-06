package es.caib.helium.client.integracio.portafirmes.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class PortaFirmesFlux {
	
	private Long entornId;
	private Long documentId;
	private DocumentDto document;
	private List<DocumentDto> anexos;
	private String codiPersona;	//	codi de la persona signants
	private List<String> signatarisPas1; // codis de les persones signants en el pas 1
	private Integer minSignatarisPas1;
	private List<String> signatarisPas2; // codis de les persones signants en el pas 2
	private Integer minSignatarisPas2;
	private List<String> signatarisPas3; // codis de les persones signants en el pas 3
	private Integer minSignatarisPas3;
	private Long expedientId;
	private String expedientIdentificador;
	private String importancia;
	private Date dataLimit;
	private Long execucioId;
	private Long processInstanceId;
	private String transicioOK;
	private String transicioKO;
	private Long tokenId;
	
	private String codiUsuari;
	private PersonaDto persona;
	private List<PersonaDto> personesPas1;
	private List<PersonaDto> personesPas2;
	private List<PersonaDto> personesPas3;
}
