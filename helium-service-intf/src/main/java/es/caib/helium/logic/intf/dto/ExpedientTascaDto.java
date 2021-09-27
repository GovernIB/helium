/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.commons.text.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * DTO amb informació d'una tasca de l'expedient pel llistat de tasques i pel formulari
 * de tramitació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@SuppressWarnings("serial")
public class ExpedientTascaDto extends ControlPermisosDto implements Comparable<ExpedientTascaDto> {

	public enum TascaTipusDto {
		ESTAT,
		FORM,
		SIGNATURA
	}
	public enum TascaEstatDto {
		PENDENT,
		PDT_DADES,
		PDT_DOCUMENTS,
		PDT_SIGNATURES,
		FINALITZADA,
		CANCELADA,
		SUSPESA
	}
	public enum TascaPrioritatDto {
		MOLT_ALTA,
		ALTA,
		NORMAL,
		BAIXA,
		MOLT_BAIXA
	}

	private String id;
	private String nom;
	private String titol;
	private String jbpmName;
	private String description;
	private String assignee;
	private Set<String> pooledActors;
	private Set<String> grups;
	private Date createTime;
	private Date startTime;
	private Date endTime;
	private Date dueDate;
	private int priority;
	private boolean open;
	private boolean completed;
	private boolean cancelled;
	private boolean suspended;
	private List<String> outcomes;

	private Long tascaId;
	private String tascaNom;
	private TascaTipusDto tascaTipus;
	private String tascaMissatgeInfo;
	private String tascaMissatgeWarn;
	private String tascaRecursForm;
	private String tascaFormExternCodi;
	private boolean tascaDelegable;
	private Boolean tascaTramitacioMassiva;
	private boolean tascaFinalitzacioSegonPla;

	// Dades de tramitació en 2n pla
	private Date marcadaFinalitzar;
	private Date iniciFinalitzacio;
	private String errorFinalitzacio;

	private boolean validada;
	private boolean documentsComplet;
	private boolean signaturesComplet;
	private boolean agafada;

	// Dades de delegació
	private boolean delegada;
	private boolean delegacioOriginal;
	private Date delegacioData;
	private String delegacioComentari;
	private boolean delegacioSupervisada;
	private PersonaDto delegacioPersona;

	private Long expedientId;
	private Long expedientTipusId;
	private String expedientIdentificador;
	private String expedientNumero;

	private String processInstanceId;
	private String expedientTipusNom;
	private Long definicioProcesId;

	
	private PersonaDto responsable;
	private List<PersonaDto> responsables;

	private boolean assignadaUsuariActual;
	private boolean ambRepro;
	private boolean mostrarAgrupacions;


	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	

	public boolean isTascaTramitacioMassiva() {
		return tascaTramitacioMassiva != null ? tascaTramitacioMassiva.booleanValue() : false;
	}
	public String getMarcadaFinalitzarFormat() {
		if (marcadaFinalitzar != null) {
			return formatter.format(marcadaFinalitzar).toString();
		} else {
			return "";
		}
	}
	public String getIniciFinalitzacioFormat() {
		if (iniciFinalitzacio != null) {
			return formatter.format(iniciFinalitzacio).toString();
		} else {
			return "";
		}
	}

	public Set<String> getResponsablesString() {
		Set<String> responsablesString = new HashSet<String>();
		if (this.getResponsables() != null)
			for (PersonaDto responsable : this.getResponsables())
				responsablesString.add(responsable.toString());
		return responsablesString;
	}

	public TascaEstatDto getEstat() {
		if (cancelled) {
			return TascaEstatDto.CANCELADA;
		} else if (suspended) {
			return TascaEstatDto.SUSPESA;
		} else if (completed) {
			return TascaEstatDto.FINALITZADA;
		} else {
			return TascaEstatDto.PENDENT;
		}
	}

	public TascaPrioritatDto getPrioritat() {
		if (priority <= -2) {
			return TascaPrioritatDto.MOLT_BAIXA;
		} else if (priority == -1) {
			return TascaPrioritatDto.BAIXA;
		} else if (priority == 1) {
			return TascaPrioritatDto.ALTA;
		} else if (priority >= 2) {
			return TascaPrioritatDto.MOLT_ALTA;
		} else {
			return TascaPrioritatDto.NORMAL;
		}
	}

	public String getResponsableString() {
		if ((responsables == null || responsables.isEmpty()) || agafada)
			return responsable == null ? "" : responsable.toString();
		return responsables.toString().replace("[", "").replace("]", "").replaceAll(", $", "");
	}

	@Override
	public int compareTo(ExpedientTascaDto aThat) {
	    if (this == aThat) return 0;
    	return this.getCreateTime().compareTo(aThat.getCreateTime());
	}

	public String getTitolLimitat() {
		if (titol.length() > 100)
			return titol.substring(0, 100) + " (...)";
		else
			return titol;
	}
	public String getTitolEscaped() {
		return StringEscapeUtils.escapeEcmaScript(titol);
	}
	public String getExpedientIdentificadorEscaped() {
		return StringEscapeUtils.escapeEcmaScript(expedientIdentificador);
	}
	
	private static final String PREFIX_TASCA_INICIAL = "TIE_";
	public boolean isInicial() {
		return id.startsWith(PREFIX_TASCA_INICIAL);
	}

	public boolean isTransicioPerDefecte() {
		if (outcomes != null && !outcomes.isEmpty() && outcomes.size() == 1) {
			return outcomes.get(0) == null || outcomes.get(0).isEmpty();
		} else {
			return false;
		}
	}

	public boolean isFormExtern() {
		return (tascaFormExternCodi != null && !tascaFormExternCodi.isEmpty());
	}

	private static final long serialVersionUID = 127420079220181365L;
}
