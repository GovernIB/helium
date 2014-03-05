/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaDefaultAny;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.SequenciaAnyDto;
import net.conselldemallorca.helium.v3.core.api.dto.SequenciaDefaultAnyDto;

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
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientTipus, ExpedientTipusDto>() {

					@Override
					public ExpedientTipusDto convert(ExpedientTipus source, Type<? extends ExpedientTipusDto> destinationType) {
						ExpedientTipusDto target = new ExpedientTipusDto();
						target.setAnyActual(source.getAnyActual());
						target.setCodi(source.getCodi());
						target.setDemanaNumero(source.getDemanaNumero());
						target.setDemanaTitol(source.getDemanaTitol());
						target.setEntorn(convertir(source.getEntorn(), EntornDto.class));
						target.setEstats(convertirList(source.getEstats(), EstatDto.class));
						target.setExpressioNumero(source.getExpressioNumero());
						target.setId(source.getId());
						target.setJbpmProcessDefinitionKey(source.getJbpmProcessDefinitionKey());
						target.setNom(source.getNom());
						target.setReiniciarCadaAny(source.isReiniciarCadaAny());
						target.setResponsableDefecteCodi(source.getResponsableDefecteCodi());
						target.setRestringirPerGrup(source.isRestringirPerGrup());
						target.setSeleccionarAny(source.isSeleccionarAny());
						target.setSequencia(source.getSequencia());
						target.setSequenciaDefault(source.getSequenciaDefault());
						target.setTeNumero(source.getTeNumero());
						target.setTeTitol(source.getTeTitol());
						target.setTramitacioMassiva(source.isTramitacioMassiva());						

						Map<Integer,SequenciaAnyDto> sequenciaAnyMap = new HashMap<Integer, SequenciaAnyDto>();
						for (Entry<Integer, SequenciaAny> entry : source.getSequenciaAny().entrySet()) {
							SequenciaAny value = entry.getValue();
							SequenciaAnyDto valueDto = new SequenciaAnyDto();
							valueDto.setAny(value.getAny());
							valueDto.setId(value.getId());
							valueDto.setSequencia(value.getSequencia());
							sequenciaAnyMap.put(entry.getKey(), valueDto);
						}					
						target.setSequenciaAny(sequenciaAnyMap);
						
						Map<Integer,SequenciaDefaultAnyDto> sequenciaAnyDefaultMap = new HashMap<Integer, SequenciaDefaultAnyDto>();
						for (Entry<Integer, SequenciaDefaultAny> entry : source.getSequenciaDefaultAny().entrySet()) {
							SequenciaDefaultAny value = entry.getValue();
							SequenciaDefaultAnyDto valueDto = new SequenciaDefaultAnyDto();
							valueDto.setAny(value.getAny());
							valueDto.setId(value.getId());
							valueDto.setSequenciaDefault(value.getSequenciaDefault());							
							sequenciaAnyDefaultMap.put(entry.getKey(), valueDto);
						}					    
						target.setSequenciaDefaultAny(sequenciaAnyDefaultMap);
						
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
