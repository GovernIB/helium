package es.caib.helium.integracio.domini.notificacio;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import es.caib.helium.integracio.enums.notificacio.EnviamentTipus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notificacio {

	private Date caducitat;
	@NotNull @NotEmpty
	private String concepte;
	private String descripcio;
	@NotNull @NotEmpty
	private String documentArxiuNom;
	private byte[] documentArxiuContingut;
	private String documentArxiuUuid;
	private String documentArxiuCsv;
	@NotNull @NotEmpty
	private String emisorDir3Codi;
	private Date enviamentDataProgramada;
	@NotNull
	private EnviamentTipus enviamentTipus;
	@NotNull @NotEmpty //TODO QUEDA PENDENT VALIDAR ELS ENVIAMENTS I ELS SEUS SUBTIPUS
	private List<Enviament> enviaments;
	private String grupCodi;
	@NotNull @NotEmpty
	private String procedimentCodi;
	private Integer retard;
	@NotNull @NotEmpty
	private String usuariCodi;
	private String numExpedient;
	
	public Notificacio() {
		
	}

	public Notificacio(DadesNotificacioDto dto) {
		
		caducitat = dto.getCaducitat();
		concepte = dto.getConcepte();
		descripcio = dto.getDescripcio();
		documentArxiuNom = dto.getDocumentArxiuNom();
		documentArxiuContingut = dto.getDocumentArxiuContingut();
		documentArxiuUuid = dto.getDocumentArxiuUuid();
		documentArxiuCsv = dto.getDocumentArxiuCsv();
		emisorDir3Codi = dto.getEmisorDir3Codi();
		enviamentDataProgramada = dto.getEnviamentDataProgramada();
		enviamentTipus = dto.getEnviamentTipus();
		enviaments = dto.getEnviaments();
		grupCodi = dto.getGrupCodi();
		procedimentCodi = dto.getProcedimentCodi(); 
		retard = dto.getRetard();
		usuariCodi = dto.getUsuariCodi();
		numExpedient = dto.getNumExpedient();
	}
	
}
