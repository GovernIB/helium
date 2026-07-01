package es.caib.helium.commons.dades;

import es.caib.helium.commons.dto.CampTipusDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public enum DadaTipusEnum {
	/**
	 *  Indica que el valor guardat es de tipus String
	 *  */
	s,
	/**
	 *  Indica que el valor guardat es de tipus Integer/Long
	 *  */
	i,
	/**
	 *  Indica que el valor guardat es de tipus Double/float
	 *  */
	f,
	/**
	 *  Indica que el valor guardat es de tipus BigDecimal
	 *  */
	p,
	/**
	 *  Indica que el valor guardat es de tipus Boolean
	 *  */
	b,
	/**
	 *  Indica que el valor guardat es de tipus Date
	 *  */
	d,
	/**
	 *  Indica que el valor guardat es de tipus Object
	 *  */
	o;

	public static DadaTipusEnum getTipusByClass(Class clazz) {
		assert(clazz != null);
		if(clazz.equals(Integer.class) || clazz.equals(Long.class))
			return i;
		if(clazz.equals(Double.class) || clazz.equals(Float.class))
			return f;
		if(clazz.equals(BigDecimal.class))
			return p;
		if(clazz.equals(Date.class)  || clazz.equals(LocalDate.class))
			return d;
		if (clazz.equals(Boolean.class))
			return b;
		if (clazz.equals(String.class))
			return s;
		return o;
	}

	public static DadaTipusEnum getTipusByCampTipus(CampTipusDto tipus) {
		assert(tipus != null);
		switch(tipus) {
			case STRING:
			case TEXTAREA:
			case SUGGEST:
			case SELECCIO:
			case TERMINI:
				return s;
			case DATE:
				return d;
			case FLOAT:
			case PRICE:
				return f;
			case INTEGER:
				return i;
			case BOOLEAN:
				return b;
			case REGISTRE:
			default:
				return o;
		}
	}
}
