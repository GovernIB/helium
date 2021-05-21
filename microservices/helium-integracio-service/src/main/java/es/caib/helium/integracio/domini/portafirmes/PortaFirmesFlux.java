package es.caib.helium.integracio.domini.portafirmes;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortaFirmesFlux {

	private Long documentId;
	private List<Long> anexos;
	private String persona;	//	codi de la persona signants
	private List<String> signatarisPas1; // codis de les persones signants en el pas 1
	private Integer minSignatarisPas1;
	private List<String> signatarisPas2; // codis de les persones signants en el pas 2
	private Integer minSignatarisPas2;
	private List<String> signatarisPas3; // codis de les persones signants en el pas 3
	private Integer minSignatarisPas3;
	private Long expedient;
	private String importancia;
	private Date dataLimit;
	private Long execucioId;
	private Long processInstanceId;
	private String transicioOK;
	private String transicioKO;
}
