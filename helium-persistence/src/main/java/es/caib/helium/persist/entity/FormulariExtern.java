/**
 * 
 */
package es.caib.helium.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Objecte de domini que representa un formulari extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_formext")
public class FormulariExtern implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@Size(max = 255)
	private String taskId;
	@NotBlank
	@Size(max = 255)
	private String formulariId;
	@NotBlank
	@Size(max = 1024)
	private String url;
	@NotBlank
	private Date dataInici = new Date();
	private Date dataRecepcioDades;
	private Date dataDarreraPeticio;
	private int formWidth = 800;
	private int formHeight = 600;



	public FormulariExtern() {}
	public FormulariExtern(String taskId, String formulariId, String url) {
		this.taskId = taskId;
		this.formulariId = formulariId;
		this.url = url;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_formext")
	@TableGenerator(name="gen_formext", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="taskid", length=255, nullable=false)
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@Column(name="formid", length=255, nullable=false, unique=true)
	public String getFormulariId() {
		return formulariId;
	}
	public void setFormulariId(String formulariId) {
		this.formulariId = formulariId;
	}

	@Column(name="url", length=1024, nullable=false)
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name="data_inici", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}

	@Column(name="data_recdades")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataRecepcioDades() {
		return dataRecepcioDades;
	}
	public void setDataRecepcioDades(Date dataRecepcioDades) {
		this.dataRecepcioDades = dataRecepcioDades;
	}

	@Column(name="data_darrpet")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataDarreraPeticio() {
		return dataDarreraPeticio;
	}
	public void setDataDarreraPeticio(Date dataDarreraPeticio) {
		this.dataDarreraPeticio = dataDarreraPeticio;
	}

	@Column(name="data_formwidth")
	public int getFormWidth() {
		return formWidth;
	}
	public void setFormWidth(int formWidth) {
		this.formWidth = formWidth;
	}

	@Column(name="data_formheight")
	public int getFormHeight() {
		return formHeight;
	}
	public void setFormHeight(int formHeight) {
		this.formHeight = formHeight;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormulariExtern other = (FormulariExtern) obj;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
