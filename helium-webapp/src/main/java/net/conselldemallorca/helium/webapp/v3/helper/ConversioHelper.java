/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import java.util.List;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Helper per a conversió de tipus.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConversioHelper {

	private static MapperFactory mapperFactory;

	public static <T> T convertir(Object source, Class<T> targetType) {
		return getMapperFacade().map(source, targetType);
	}
	public static <T> List<T> convertirLlista(List<?> items, Class<T> targetType) {
		return getMapperFacade().mapAsList(items, targetType);
	}



	private static MapperFacade getMapperFacade() {
		if (mapperFactory == null) {
			mapperFactory = new DefaultMapperFactory.Builder().build();
		}
		return mapperFactory.getMapperFacade();
	}

}
