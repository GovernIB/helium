/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.List;
import java.util.Set;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;

import org.springframework.stereotype.Component;

/**
 * Helper per a convertir entre diferents formats de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConversioTipusHelper {

	private MapperFactory mapperFactory;

	public ConversioTipusHelper() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<CampTasca, CampTascaDto>() {
					public CampTascaDto convert(CampTasca source, Type<? extends CampTascaDto> destinationClass) {
						CampTascaDto target = new CampTascaDto();
						target.setId(source.getId());
						target.setReadFrom(source.isReadFrom());
						target.setWriteTo(source.isWriteTo());
						target.setRequired(source.isRequired());
						target.setReadOnly(source.isReadOnly());
						target.setOrder(source.getOrder());
						if (source.getCamp() != null) {
							CampDto camp = new CampDto();
							camp.setId(source.getCamp().getId());
							camp.setCodi(source.getCamp().getCodi());
							camp.setEtiqueta(source.getCamp().getEtiqueta());
							camp.setObservacions(source.getCamp().getObservacions());
							camp.setTipus(
									CampTipusDto.valueOf(
											source.getCamp().getTipus().toString()));
							target.setCamp(camp);
						}
						return target;
					}
				});
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
