/**
 * 
 */
package es.caib.helium.client.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Objecte de domini que representa un camp de la definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampRest implements Serializable {

	private static final long serialVersionUID = 3201107603266308582L;
	
	private Long id;
	private String codi;
	private CampTipus tipus;
	private boolean multiple;
	private boolean ocult;
	private boolean ignored;

	private List<CampRegistreRest> registreMembres = new ArrayList<>();

	// Herencia
	protected boolean heretat = false;
	protected boolean sobreescriu = false;
	
	@SuppressWarnings("rawtypes")
	public Class getJavaClass() {
		if (CampTipus.STRING.equals(tipus)) {
			return String.class;
		} else if (CampTipus.INTEGER.equals(tipus)) {
			return Long.class;
		} else if (CampTipus.FLOAT.equals(tipus)) {
			return Double.class;
		} else if (CampTipus.BOOLEAN.equals(tipus)) {
			return Boolean.class;
		} else if (CampTipus.TEXTAREA.equals(tipus)) {
			return String.class;
		} else if (CampTipus.DATE.equals(tipus)) {
			return Date.class;
		} else if (CampTipus.PRICE.equals(tipus)) {
			return BigDecimal.class;
		} else if (CampTipus.TERMINI.equals(tipus)) {
			return Termini.class;
		} else if (CampTipus.REGISTRE.equals(tipus)) {
			return Object[].class;
		} else {
			return String.class;
		}
	}

	public static String getComText(
			CampTipus tipus,
			Object valor,
			String valorDomini) {
		if (valor == null)
			return null;
		try {
			String text = null;
			if (tipus.equals(CampTipus.INTEGER)) {
				text = new DecimalFormat("#").format((Long)valor);
			} else if (tipus.equals(CampTipus.FLOAT)) {
				text = new DecimalFormat("#.##########").format((Double)valor);
			} else if (tipus.equals(CampTipus.PRICE)) {
				text = new DecimalFormat("#,##0.00").format((BigDecimal)valor);
			} else if (tipus.equals(CampTipus.DATE)) {
				text = new SimpleDateFormat("dd/MM/yyyy").format((Date)valor);
			} else if (tipus.equals(CampTipus.BOOLEAN)) {
				text = (((Boolean)valor).booleanValue()) ? "Si" : "No";
			} else if (tipus.equals(CampTipus.SELECCIO)) {
				text = valorDomini;
			} else if (tipus.equals(CampTipus.SUGGEST)) {
				text = valorDomini;
			} else if (tipus.equals(CampTipus.TERMINI)) {
				Termini termini = ((Termini)valor);
				text = termini.getAnys()+"/"+termini.getMesos()+"/"+termini.getDies();
			} else {
				text = valor.toString();
			}
			return text;
		} catch (Exception ex) {
			return valor.toString();
		}
	}
	
	public static Object getComObject(
			CampTipus tipus,
			String text) {
		if (text == null)
			return null;
		try {
			Object obj = null;
			if (tipus.equals(CampTipus.INTEGER)) {
				obj = Long.parseLong(text);
			} else if (tipus.equals(CampTipus.FLOAT)) {
				obj = Double.parseDouble(text);
			} else if (tipus.equals(CampTipus.PRICE)) {
				obj = new BigDecimal(text);
			} else if (tipus.equals(CampTipus.DATE)) {
				obj = new SimpleDateFormat("dd/MM/yyyy").parse(text);
			} else if (tipus.equals(CampTipus.BOOLEAN)) {
				obj = Boolean.valueOf("S".equals(text));
			} else if (tipus.equals(CampTipus.SELECCIO)) {
				obj = text;
			} else if (tipus.equals(CampTipus.SUGGEST)) {
				obj = text;
			} else if (tipus.equals(CampTipus.TERMINI)) {
				String[] parts = text.split("/");
				Termini termini = new Termini();
				if (parts.length == 3) {
					termini.setAnys(new Integer(parts[0]));
					termini.setMesos(new Integer(parts[1]));
					termini.setDies(new Integer(parts[2]));
				}
				obj = termini;
			} else {
				obj = text;
			}
			return obj;
		} catch (Exception ex) {
			return text;
		}
	}
}
