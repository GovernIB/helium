package es.caib.helium.back.command;

import es.caib.helium.back.validator.Codi;
import es.caib.helium.back.validator.ExpedientTipusEstat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@ExpedientTipusEstat(groups = {ExpedientTipusEstatCommand.Creacio.class, ExpedientTipusEstatCommand.Modificacio.class})
public class ExpedientTipusEstatCommand {

	private Long expedientTipusId;
	private Long id;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 64, groups = {Creacio.class, Modificacio.class})
	@Codi(groups = {Creacio.class, Modificacio.class})
	private String codi;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String nom;
	private int ordre;

	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	public interface Creacio {}
	public interface Modificacio {}
}
