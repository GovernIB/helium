/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaDefaultAny;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampRegistreDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.SequenciaAnyDto;
import net.conselldemallorca.helium.v3.core.api.dto.SequenciaDefaultAnyDto;

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
				new CustomConverter<Document, DocumentDto>() {
					@Override
					public DocumentDto convert(Document source, Type<? extends DocumentDto> destinationClass) {
						DocumentDto target = new DocumentDto();
						target.setId(source.getId());
						target.setCodi(source.getCodi());
						target.setArxiuNom(source.getArxiuNom());
						target.setDocumentNom(source.getNom());
						target.setNom(source.getNom());
						target.setDescripcio(source.getDescripcio());
						target.setPlantilla(source.isPlantilla());
						target.setExpedientTipus(mapperFacade.map(
								source.getExpedientTipus(), 
								ExpedientTipusDto.class));
						if (source.getCampData() != null)
							target.setCampData(mapperFacade.map(
									source.getCampData(), 
									CampDto.class));
						target.setConvertirExtensio(source.getConvertirExtensio());
						target.setAdjuntarAuto(source.isAdjuntarAuto());
						target.setExtensionsPermeses(source.getExtensionsPermeses());
						target.setContentType(source.getContentType());
						target.setCustodiaCodi(source.getCustodiaCodi());
						target.setTipusDocPortasignatures(source.getTipusDocPortasignatures());
						return target;
					}
		});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<Camp, CampDto>() {
					@Override
					public CampDto convert(Camp source, Type<? extends CampDto> destinationClass) {
						CampDto target = new CampDto();
						target.setId(source.getId());
						target.setCodi(source.getCodi());
						target.setEtiqueta(source.getEtiqueta());
						target.setObservacions(source.getObservacions());
						target.setTipus(
								CampTipusDto.valueOf(
										source.getTipus().toString()));	
						target.setAgrupacio(
								mapperFacade.map(
										source.getAgrupacio(), 
										CampAgrupacioDto.class)
								);
						target.setMultiple(source.isMultiple());
						target.setOcult(source.isOcult());
						target.setIgnored(source.isIgnored());
						
						if (source.getExpedientTipus() != null) {
							target.setExpedientTipus(convertir(source.getExpedientTipus(), ExpedientTipusDto.class));
						}
						
						// Dades consulta
						target.setEnumeracio(
								mapperFacade.map(
										source.getEnumeracio(),
										EnumeracioDto.class));
						target.setDomini(
								mapperFacade.map(
										source.getDomini(),
										DominiDto.class));
						target.setConsulta(
								mapperFacade.map(
										source.getConsulta(),
										ConsultaDto.class));
						target.setDominiIntern(source.isDominiIntern());

						// Paràmetres del domini
						target.setDominiIdentificador(source.getDominiId());
						target.setDominiParams(source.getDominiParams());
						target.setDominiCampValor(source.getDominiCampValor());
						target.setDominiCampText(source.getDominiCampText());
						
						// Paràmetres de la consulta
						target.setConsultaParams(source.getConsultaParams());
						target.setConsultaCampValor(source.getConsultaCampValor());
						target.setConsultaCampText(source.getConsultaCampText());
						
						// Dades accio
						target.setDefprocJbpmKey(source.getDefprocJbpmKey());
						target.setJbpmAction(source.getJbpmAction());
						
						target.setDominiCacheText(source.isDominiCacheText());
						
						target.setOrdre(source.getOrdre());
						
						return target;
					}
		});
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
						target.setAmpleCols(source.getAmpleCols());
						target.setBuitCols(source.getBuitCols());
						if (source.getCamp() != null) {
							target.setCamp(convertir(source.getCamp(), CampDto.class));
						}
						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<Consulta, ConsultaDto>() {
					@Override
					public ConsultaDto convert(Consulta source, Type<? extends ConsultaDto> destinationType) {
						ConsultaDto target = new ConsultaDto();
						target.setCodi(source.getCodi());
						target.setDescripcio(source.getDescripcio());
						target.setExpedientTipus(convertir(source.getExpedientTipus(), ExpedientTipusDto.class));
						target.setExportarActiu(source.isExportarActiu());
						target.setFormatExport(source.getFormatExport());
						target.setGenerica(source.isGenerica());
						target.setId(source.getId());
						target.setInformeContingut(source.getInformeContingut());
						target.setInformeNom(source.getInformeNom());
						target.setNom(source.getNom());
						target.setOcultarActiu(source.isOcultarActiu());
						target.setOrdre(source.getOrdre());
						target.setValorsPredefinits(source.getValorsPredefinits());
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
						if (source.getEstats() != null)
							target.setEstats(convertirList(source.getEstats(), EstatDto.class));
						target.setExpressioNumero(source.getExpressioNumero());
						target.setId(source.getId());
						for (Consulta consulta : source.getConsultes()) {
							if (!consulta.isOcultarActiu()) {
								ConsultaDto consulte = new ConsultaDto();
								consulte.setId(consulta.getId());
								consulte.setNom(consulta.getNom());
								consulte.setCodi(consulta.getCodi());
								target.getConsultes().add(consulte);
							}
						}
						target.setJbpmProcessDefinitionKey(source.getJbpmProcessDefinitionKey());
						target.setNom(source.getNom());
						target.setReiniciarCadaAny(source.isReiniciarCadaAny());
						target.setResponsableDefecteCodi(source.getResponsableDefecteCodi());
						target.setRestringirPerGrup(source.isRestringirPerGrup());
						target.setSeleccionarAny(source.isSeleccionarAny());
						target.setAmbRetroaccio(source.isAmbRetroaccio());
						target.setAmbInfoPropia(source.isAmbInfoPropia());
						target.setReindexacioAsincrona(source.isReindexacioAsincrona());
						target.setDiesNoLaborables(source.getDiesNoLaborables());
						target.setNotificacionsActivades(source.isNotificacionsActivades());
						target.setNotificacioOrganCodi(source.getNotificacioOrganCodi());
						target.setNotificacioOficinaCodi(source.getNotificacioOficinaCodi());
						target.setNotificacioUnitatAdministrativa(source.getNotificacioUnitatAdministrativa());
						target.setNotificacioCodiProcediment(source.getNotificacioCodiProcediment());
						target.setNotificacioAvisTitol(source.getNotificacioAvisTitol());
						target.setNotificacioAvisText(source.getNotificacioAvisText());
						target.setNotificacioAvisTextSms(source.getNotificacioAvisTextSms());
						target.setNotificacioOficiTitol(source.getNotificacioOficiTitol());
						target.setNotificacioOficiText(source.getNotificacioOficiText());
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
						
						target.setFormextUrl(source.getFormextUrl());
						target.setFormextUsuari(source.getFormextUsuari());
						target.setFormextContrasenya(source.getFormextContrasenya());
						
						target.setSistraTramitCodi(source.getSistraTramitCodi());

						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientTipusDto, ExpedientTipus>() {
					@Override
					public ExpedientTipus convert(ExpedientTipusDto source, Type<? extends ExpedientTipus> destinationType) {
						ExpedientTipus target = new ExpedientTipus();
						target.setAnyActual(source.getAnyActual());
						target.setCodi(source.getCodi());
						target.setDemanaNumero(source.isDemanaNumero());
						target.setDemanaTitol(source.isDemanaTitol());
						target.setEntorn(convertir(source.getEntorn(), Entorn.class));
						target.setEstats(convertirList(source.getEstats(), Estat.class));
						target.setExpressioNumero(source.getExpressioNumero());
						target.setId(source.getId());
						target.setJbpmProcessDefinitionKey(source.getJbpmProcessDefinitionKey());
						target.setNom(source.getNom());
						target.setReiniciarCadaAny(source.isReiniciarCadaAny());
						target.setResponsableDefecteCodi(source.getResponsableDefecteCodi());
						target.setRestringirPerGrup(source.isRestringirPerGrup());
						target.setSeleccionarAny(source.isSeleccionarAny());
						target.setAmbRetroaccio(source.isAmbRetroaccio());
						target.setAmbInfoPropia(source.isAmbInfoPropia());
						target.setReindexacioAsincrona(source.isReindexacioAsincrona());
						target.setDiesNoLaborables(source.getDiesNoLaborables());
						target.setNotificacionsActivades(source.isNotificacionsActivades());
						target.setNotificacioOrganCodi(source.getNotificacioOrganCodi());
						target.setNotificacioOficinaCodi(source.getNotificacioOficinaCodi());
						target.setNotificacioUnitatAdministrativa(source.getNotificacioUnitatAdministrativa());
						target.setNotificacioCodiProcediment(source.getNotificacioCodiProcediment());
						target.setNotificacioAvisTitol(source.getNotificacioAvisTitol());
						target.setNotificacioAvisText(source.getNotificacioAvisText());
						target.setNotificacioAvisTextSms(source.getNotificacioAvisTextSms());
						target.setNotificacioOficiTitol(source.getNotificacioOficiTitol());
						target.setNotificacioOficiText(source.getNotificacioOficiText());
						target.setSequencia(source.getSequencia());
						target.setSequenciaDefault(source.getSequenciaDefault());
						target.setTeNumero(source.isTeNumero());
						target.setTeTitol(source.isTeTitol());
						target.setTramitacioMassiva(source.isTramitacioMassiva());						
						SortedMap<Integer, SequenciaAny> sequenciaAnySorted = new TreeMap<Integer, SequenciaAny>();
						for (Entry<Integer, SequenciaAnyDto> entry : source.getSequenciaAny().entrySet()) {
							SequenciaAnyDto valueDto = entry.getValue();
							SequenciaAny value = new SequenciaAny();
							value.setAny(valueDto.getAny());
							value.setId(valueDto.getId());
							value.setSequencia(valueDto.getSequencia());
							sequenciaAnySorted.put(entry.getKey(), value);
						}					
						target.setSequenciaAny(sequenciaAnySorted);
						SortedMap<Integer, SequenciaDefaultAny> sequenciaAnyDefaultSorted = new TreeMap<Integer, SequenciaDefaultAny>();
						for (Entry<Integer, SequenciaDefaultAnyDto> entry : source.getSequenciaDefaultAny().entrySet()) {
							SequenciaDefaultAnyDto valueDto = entry.getValue();
							SequenciaDefaultAny value = new SequenciaDefaultAny();
							value.setAny(valueDto.getAny());
							value.setId(valueDto.getId());
							value.setSequenciaDefault(valueDto.getSequenciaDefault());
							sequenciaAnyDefaultSorted.put(entry.getKey(), value);
						}					
						target.setSequenciaDefaultAny(sequenciaAnyDefaultSorted);
						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<Enumeracio, EnumeracioDto>() {
					@Override
					public EnumeracioDto convert(Enumeracio source, Type<? extends EnumeracioDto> destinationClass) {
						EnumeracioDto target = new EnumeracioDto();
						target.setId(source.getId());
						target.setCodi(source.getCodi());
						target.setNom(source.getNom());
						return target;
					}
		});		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<CampRegistre, CampRegistreDto>() {
					@Override
					public CampRegistreDto convert(CampRegistre source, Type<? extends CampRegistreDto> destinationClass) {
						CampRegistreDto target = new CampRegistreDto();
						target.setId(source.getId());
						target.setMembreId(source.getMembre().getId());
						target.setMembreCodi(source.getMembre().getCodi());
						target.setMembreEtiqueta(source.getMembre().getEtiqueta());
						target.setMembreTipus(  
								CampTipusDto.valueOf(source.getMembre().getTipus().toString()));
						target.setOrdre(source.getOrdre());
						target.setObligatori(source.isObligatori());
						target.setLlistar(source.isLlistar());
						return target;
					}
		});		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ConsultaCamp, ConsultaCampDto>() {
					@Override
					public ConsultaCampDto convert(ConsultaCamp source, Type<? extends ConsultaCampDto> destinationClass) {
						ConsultaCampDto target = new ConsultaCampDto();
						target.setId(source.getId());
						target.setCampCodi(source.getCampCodi());
						target.setCampDescripcio(source.getCampDescripcio());
						if (source.getParamTipus() != null)
							target.setParamTipus(ConsultaCampDto.TipusParamConsultaCamp.valueOf(source.getParamTipus().toString()));
						target.setDefprocJbpmKey(source.getDefprocJbpmKey());
						target.setDefprocVersio(source.getDefprocVersio());
						target.setOrdre(source.getOrdre());
						target.setAmpleCols(source.getAmpleCols());
						target.setBuitCols(source.getBuitCols());
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
