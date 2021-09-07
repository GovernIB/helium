/**
 * 
 */
package es.caib.helium.client.dada.model;

import es.caib.helium.client.dada.enums.Tipus;
import es.caib.helium.client.engine.model.Termini;
import lombok.Data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Data
public class Dada {

	private String id;
	private String codi;
	private Tipus tipus;
	private boolean multiple;
	private List<Valor> valor;
	
	private Long expedientId;
	private String procesId;


	public String getValors() {

			var valorString = "";
			for (var v : valor) {
				valorString += v instanceof ValorSimple ? ((ValorSimple) v).getValor() : "";
			}

			return valorString;
	}

	// Simple --> object
	// Multiple --> object[]
	// Registre --> simple: object[], multiple: object[][]
	public Object getValorsAsObject() {
		if (multiple) {
			List<Object> valorList = new ArrayList<>();
			valor.forEach(v -> valorList.add(getObjectValue(v)));
			return valorList.toArray();
		} else {
			return getObjectValue(valor.get(0));
		}
	}

	private Object getObjectValue(
		Valor valor) {
		if (valor instanceof ValorSimple) {
			return getValue(((ValorSimple) valor).getValor());
		} else {
			List<Object> objList = new ArrayList<>();
			ValorRegistre registre = (ValorRegistre) valor;
			List<Dada> dadesRegistre = registre.getCamps();
			dadesRegistre.stream().forEach(d -> objList.add(d.getValorsAsObject()));
			return objList.toArray();
		}
	}

	private Object getValue(String text) {
		if (text == null) {
			return null;
		}
		try {
			Object obj = null;
			if (tipus.equals(Tipus.INTEGER)) {
				obj = Long.valueOf(text);
			} else if (tipus.equals(Tipus.FLOAT)) {
				obj = Double.valueOf(text);
			} else if (tipus.equals(Tipus.PRICE)) {
				obj = new BigDecimal(text);
			} else if (tipus.equals(Tipus.DATE)) {
				obj = new SimpleDateFormat("dd/MM/yyyy").parse(text);
			} else if (tipus.equals(Tipus.BOOLEAN)) {
				obj = getBooleanValue(text.toUpperCase());
			} else if (tipus.equals(Tipus.SELECCIO)) {
				obj = text;
			} else if (tipus.equals(Tipus.SUGGEST)) {
				obj = text;
			} else if (tipus.equals(Tipus.TERMINI)) {
				String[] parts = text.split("/");
				Termini termini = new Termini();
				if (parts.length == 3) {
					termini.setAnys(Integer.valueOf(parts[0]));
					termini.setMesos(Integer.valueOf(parts[1]));
					termini.setDies(Integer.valueOf(parts[2]));
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

	private Boolean getBooleanValue(String text) {
		return Boolean.valueOf("TRUE".equals(text)) ||
				Boolean.valueOf("S".equals(text)) ||
				Boolean.valueOf("SI".equals(text));
	}
}
