package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.time.DateUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.DiaSetmanaEnum;

@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "HEL_EXPLOT_TEMPS")
public class ExplotacioTemps implements Serializable, GenericEntity<Long> {

	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_explot_temps")
	@TableGenerator(name="gen_explot_temps", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	private Long id;

	@Column(name = "DATA", nullable = false)
	private Date data;

	@Column(name = "ANUALITAT", nullable = false)
	private Integer anualitat;

	@Column(name = "MES", nullable = false)
	private Integer mes;

	@Column(name = "TRIMESTRE", nullable = false)
	private Integer trimestre;

	@Column(name = "SETMANA", nullable = false)
	private Integer setmana;

	@Column(name = "DIA", nullable = false)
	private Integer dia;

	@Column(name = "DIA_SETMANA", length = 2)
	@Enumerated(EnumType.STRING)
	private DiaSetmanaEnum diaSetmana;

	private static final long serialVersionUID = 9173465155192120109L;
	
	public ExplotacioTemps() {
		emplenarCamps(new Date());
	}

	public ExplotacioTemps(Date date) {
		emplenarCamps(date);
	}

	private void emplenarCamps(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		this.data = DateUtils.truncate(data, Calendar.DATE);
		this.dia = c.get(Calendar.DAY_OF_MONTH);
		this.diaSetmana = DiaSetmanaEnum.valueOfData(c.get(Calendar.DAY_OF_WEEK));
		this.setmana = c.get(Calendar.WEEK_OF_YEAR);
		this.mes = c.get(Calendar.MONTH) + 1;
		this.trimestre = this.mes / 3;
		this.anualitat = c.get(Calendar.YEAR);
	}
}
