/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;

/**
 * Classe del model de dades que representa una relació entre un tipus d'expedient i una unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "hel_expedient_tipus_unitat_org")
public class ExpedientTipusUnitatOrganitzativa implements Serializable, GenericEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_expedient_tipus_unitat_org")
	@TableGenerator(name="gen_expedient_tipus_unitat_org", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;
	
	@ManyToOne(
			optional = false,
			fetch = FetchType.EAGER)
	@JoinColumn(name = "expedient_tipus_id")
	@ForeignKey(name = "hel_exp_tipus_exptipus_uo_fk")
	private ExpedientTipus expedientTipus;
	
	@ManyToOne(
			optional = false,
			fetch = FetchType.EAGER)
	@JoinColumn(name = "unitat_organitzativa_id")
	@ForeignKey(name = "hel_unit_org_exptipus_uo_fk")
	private UnitatOrganitzativa unitatOrganitzativa;


	public UnitatOrganitzativa getUnitatOrganitzativa() {
		return unitatOrganitzativa;
	}

	public void setUnitatOrganitzativa(UnitatOrganitzativa unitatOrganitzativa) {
		this.unitatOrganitzativa = unitatOrganitzativa;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}

	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}

	/**
	 * Obté el Builder per a crear objectes de tipus expedient.
	 * 
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			ExpedientTipus expedientTipus,
			UnitatOrganitzativa unitatOrganitzativa) {
		return new Builder(
				expedientTipus,
				unitatOrganitzativa);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		ExpedientTipusUnitatOrganitzativa built;
		Builder(
				ExpedientTipus expedientTipus,
				UnitatOrganitzativa unitatOrganitzativa) {
			built = new ExpedientTipusUnitatOrganitzativa();
			built.expedientTipus = expedientTipus;
			built.unitatOrganitzativa = unitatOrganitzativa;
		}
		public ExpedientTipusUnitatOrganitzativa build() {
			return built;
		}
	}


	
	private static final long serialVersionUID = 1L;

}
