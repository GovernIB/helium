/**
 * 
 */
package net.conselldemallorca.helium.ms.helper;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Helper per a convertir entre diferents formats d'objectes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConversioTipusHelperMs {

	private MapperFactory mapperFactory;

	public ConversioTipusHelperMs() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
	}

	public <T> T convertir(Object source, Class<T> targetType) {
		if (source == null)
			return null;
		return getMapperFacade().map(source, targetType);
	}
	public <T> List<T> convertirList(List<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsList(items, targetType);
	}
	public <T> Set<T> convertirSet(Set<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsSet(items, targetType);
	}

	private MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}

}
