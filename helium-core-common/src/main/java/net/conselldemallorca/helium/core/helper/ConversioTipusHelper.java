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
import net.conselldemallorca.helium.core.model.hibernate.AnotacioAnnex;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.EstatRegla;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.Transicio;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaDefaultAny;
import net.conselldemallorca.helium.core.model.hibernate.ServeiPinbalEntity;
import net.conselldemallorca.helium.integracio.plugins.notificacio.Enviament;
import net.conselldemallorca.helium.integracio.plugins.notificacio.InteressatTipusEnum;
import net.conselldemallorca.helium.integracio.plugins.notificacio.Notificacio;
import net.conselldemallorca.helium.integracio.plugins.notificacio.Persona;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampRegistreDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentFinalitzarDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentListDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.FirmaTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesEstatEnum;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.dto.SequenciaAnyDto;
import net.conselldemallorca.helium.v3.core.api.dto.SequenciaDefaultAnyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ServeiPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.EstatReglaDto;

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
						target.setPortafirmesActiu(source.isPortafirmesActiu());
						target.setPortafirmesFluxId(source.getPortafirmesFluxId());
						target.setPortafirmesFluxNom(source.getPortafirmesFluxNom());
						target.setPortafirmesFluxTipus(source.getPortafirmesFluxTipus());
						target.setPortafirmesResponsables(source.getPortafirmesResponsables());
						target.setPortafirmesSequenciaTipus(source.getPortafirmesSequenciaTipus());
						target.setNom(source.getNom());
						target.setDescripcio(source.getDescripcio());
						target.setPlantilla(source.isPlantilla());
						target.setExpedientTipus(mapperFacade.map(
								source.getExpedientTipus(), 
								ExpedientTipusDto.class));
						if (source.getCampData() != null) {
							target.setCampData(mapperFacade.map(
									source.getCampData(), 
									CampDto.class));
						}
						target.setConvertirExtensio(source.getConvertirExtensio());
						target.setAdjuntarAuto(source.isAdjuntarAuto());
						target.setGenerarNomesTasca(source.isGenerarNomesTasca());
						target.setExtensionsPermeses(source.getExtensionsPermeses());
						target.setContentType(source.getContentType());
						target.setCustodiaCodi(source.getCustodiaCodi());
						target.setTipusDocPortasignatures(source.getTipusDocPortasignatures());
						target.setIgnored(source.isIgnored());
						target.setNtiOrigen(source.getNtiOrigen());
						target.setNtiEstadoElaboracion(source.getNtiEstadoElaboracion());
						target.setNtiTipoDocumental(source.getNtiTipoDocumental());
						target.setPinbalActiu(source.isPinbalActiu());
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
						target.setDominiIntern(source.getDominiIntern());

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
						if (source.getExpedientTipus() != null) {
							target.setExpedientTipusId(source.getExpedientTipus().getId());
						}
						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DocumentTasca, DocumentTascaDto>() {
					public DocumentTascaDto convert(DocumentTasca source, Type<? extends DocumentTascaDto> destinationClass) {
						DocumentTascaDto target = new DocumentTascaDto();
						target.setId(source.getId());
						target.setRequired(source.isRequired());
						target.setReadOnly(source.isReadOnly());
						target.setOrder(source.getOrder());
						if (source.getDocument() != null) {
							target.setDocument(convertir(source.getDocument(), DocumentDto.class));
						}
						if (source.getExpedientTipus() != null) {
							target.setExpedientTipusId(source.getExpedientTipus().getId());
						}
						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<FirmaTasca, FirmaTascaDto>() {
					public FirmaTascaDto convert(FirmaTasca source, Type<? extends FirmaTascaDto> destinationClass) {
						FirmaTascaDto target = new FirmaTascaDto();
						target.setId(source.getId());
						target.setRequired(source.isRequired());
						target.setOrder(source.getOrder());
						if (source.getDocument() != null) {
							target.setDocument(convertir(source.getDocument(), DocumentDto.class));
						}
						if (source.getExpedientTipus() != null) {
							target.setExpedientTipusId(source.getExpedientTipus().getId());
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
						target.setFormatExport(source.getFormatExport());
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
						target.setTipus(source.getTipus());
						target.setAmbInfoPropia(source.isAmbInfoPropia());
						target.setHeretable(source.isHeretable());
						target.setExpedientTipusPareId(source.getExpedientTipusPare() != null ? source.getExpedientTipusPare().getId() : null );
						target.setAmbHerencia(source.isAmbHerencia());
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
						target.setNtiActiu(source.isNtiActiu());
						target.setProcedimentComu(source.isProcedimentComu());
						target.setNtiOrgano(source.getNtiOrgano());
						target.setNtiClasificacion(source.getNtiClasificacion());
						target.setNtiSerieDocumental(source.getNtiSerieDocumental());
						target.setArxiuActiu(source.isArxiuActiu());
						target.setProcedimentComu(source.isProcedimentComu());
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
						target.setSistraActiu(source.isSistraActiu());
						target.setSistraTramitCodi(source.getSistraTramitCodi());
						target.setNotibActiu(source.getNotibActiu());
						target.setNotibEmisor(source.getNotibEmisor());
						target.setNotibCodiProcediment(source.getNotibCodiProcediment());
						
						target.setDistribucioActiu(source.isDistribucioActiu());
						target.setDistribucioCodiProcediment(source.getDistribucioCodiProcediment());
						target.setDistribucioCodiAssumpte(source.getDistribucioCodiAssumpte());
						target.setDistribucioProcesAuto(source.isDistribucioProcesAuto());
						target.setDistribucioSistra(source.isDistribucioSistra());
						
						target.setPinbalActiu(source.isPinbalActiu());
						target.setPinbalNifCif(source.getPinbalNifCif());
						target.setTipus(source.getTipus());
						target.setManualAjudaNom(source.getManualAjudaNom());
						// Es comenta per no carregar el DTO amb el contingut del manual
						//target.setManualAjudaContent(source.getManualAjudaContent());
						target.setDistribucioPresencial(source.getDistribucioPresencial());
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
						target.setNtiActiu(source.isNtiActiu());
						target.setProcedimentComu(source.isProcedimentComu());
						target.setNtiOrgano(source.getNtiOrgano());
						target.setNtiClasificacion(source.getNtiClasificacion());
						target.setNtiSerieDocumental(source.getNtiSerieDocumental());
						target.setArxiuActiu(source.isArxiuActiu());
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
						target.setSistraActiu(source.isSistraActiu());
						target.setSistraTramitCodi(source.getSistraTramitCodi());
						target.setNotibActiu(source.getNotibActiu());
						target.setNotibEmisor(source.getNotibEmisor());
						target.setNotibCodiProcediment(source.getNotibCodiProcediment());
						
						target.setDistribucioActiu(source.isDistribucioActiu());
						target.setDistribucioCodiProcediment(source.getDistribucioCodiProcediment());
						target.setDistribucioCodiAssumpte(source.getDistribucioCodiAssumpte());
						target.setDistribucioProcesAuto(source.isDistribucioProcesAuto());
						target.setDistribucioSistra(source.isDistribucioSistra());
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
		// Converteix la informació de PersonaDto de Helium a Persona de Notib per a les notificaciosn
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<PersonaDto, Persona>() {
					@Override
					public Persona convert(PersonaDto source, Type<? extends Persona> destinationClass) {
						Persona target = new Persona();
						if (source.getTipus() != null) {
							target.setTipus(InteressatTipusEnum.valueOf(source.getTipus().name()));
							target.setNif(source.getDni());
							target.setNom(source.getNom());
							target.setCodiDir3(source.getCodiDir3());
							switch(target.getTipus()) {
								case JURIDICA:
									target.setNom(source.getRaoSocial());
									target.setRaoSocial(source.getRaoSocial());
									break;
								case ADMINISTRACIO:
									target.setCodiDir3(source.getCodiDir3());
									break;
								case FISICA:
									target.setLlinatge1(source.getLlinatge1());
									target.setLlinatge2(source.getLlinatge2());
									break;
								default:
									break;							
							}
						}
						target.setTelefon(source.getTelefon());
						target.setEmail(source.getEmail());
						return target;
					}
		});	
		
		mapperFactory.classMap(EstatRegla.class, EstatReglaDto.class)
				.field("expedientTipus.id", "expedientTipusId")
				.field("estat.id", "estatId")
				.byDefault()
				.register();
		
		mapperFactory.classMap(DocumentFinalitzarDto.class, DocumentListDto.class)
		.field("documentStoreId", "id")
		.field("documentCodi", "codi")
		.field("arxiuNom", "nom")
		.field("annexAnotacioId", "anotacioId")
		.field("anotacioDesc", "anotacioIdf")
		.byDefault()
		.register();
		
		mapperFactory.classMap(DocumentFinalitzarDto.class, ExpedientDocumentDto.class)
		.field("documentStoreId", "id")
		.field("documentCodi", "documentNom")
		.field("annexAnotacioId", "anotacioAnnexId")
		.field("anotacioDesc", "anotacioAnnexTitol")
		.byDefault()
		.register();
		
		mapperFactory.classMap(DocumentFinalitzarDto.class, AnotacioAnnex.class)
		.field("dataCreacio", "anotacio.dataRecepcio")
		.field("arxiuNom", "nom")
		.field("annexAnotacioId", "anotacio.id")
		.field("arxiuUuid", "uuid")
		.byDefault()
		.register();
		
		mapperFactory.classMap(DocumentFinalitzarDto.class, DocumentStore.class)
		.field("documentStoreId", "id")
		.field("documentCodi", "jbpmVariable")
		.byDefault()
		.register();
		
		// Converteix la entity Portasignatures a PortasignaturesDto
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<Portasignatures, PortasignaturesDto>() {
					
					@Override
					public PortasignaturesDto convert(
							Portasignatures source,
							Type<? extends PortasignaturesDto> destinationType) {

						PortasignaturesDto target = new PortasignaturesDto();
						
						target.setId(source.getId());
						target.setDocumentId(source.getDocumentId());
						target.setTokenId(source.getTokenId());
						target.setDataEnviat(source.getDataEnviat());
						if (PortafirmesEstatEnum.ERROR.equals(source.getEstat())) {
							if (Transicio.SIGNAT.equals(source.getTransition()))
								target.setEstat(PortafirmesEstatEnum.SIGNAT.toString());
							else
								target.setEstat(PortafirmesEstatEnum.REBUTJAT.toString());
							target.setError(true);
						} else if (PortafirmesEstatEnum.PROCESSAT.equals(source.getEstat()) && Transicio.REBUTJAT.equals(source.getTransition())) {
							 target.setEstat(PortafirmesEstatEnum.PROCESSAT.toString());
							 target.setError(true);
							 target.setRebutjadaProcessada(true);
						} else {
							target.setEstat(source.getEstat().toString());
							target.setError(false);
						}
						if (source.getTransition() != null)
							target.setTransicio(source.getTransition().toString());
						target.setDocumentStoreId(source.getDocumentStoreId());
						target.setMotiuRebuig(source.getMotiuRebuig());
						target.setTransicioOK(source.getTransicioOK());
						target.setTransicioKO(source.getTransicioKO());
						target.setDataProcessamentPrimer(source.getDataProcessamentPrimer());
						target.setDataProcessamentDarrer(source.getDataProcessamentDarrer());
						target.setDataSignatRebutjat(source.getDataSignatRebutjat());
						target.setDataCustodiaIntent(source.getDataCustodiaIntent());
						target.setDataCustodiaOk(source.getDataCustodiaOk());
						target.setDataSignalIntent(source.getDataSignalIntent());
						target.setDataSignalOk(source.getDataSignalOk());
						target.setErrorProcessant(source.getErrorCallbackProcessant());
						target.setProcessInstanceId(source.getProcessInstanceId());
						
						target.setExpedientId(source.getExpedient().getId());
						target.setExpedientNumero(source.getExpedient().getNumero());
						target.setExpedientTitol(source.getExpedient().getTitol());
						target.setEntornId(source.getExpedient().getEntorn().getId());
						target.setEntornNom(source.getExpedient().getEntorn().getNom());
						target.setEntornCodi(source.getExpedient().getEntorn().getCodi());
						target.setTipusExpedientId(source.getExpedient().getTipus().getId());
						target.setTipusExpedientCodi(source.getExpedient().getTipus().getCodi());
						target.setTipusExpedientNom(source.getExpedient().getTipus().getNom());			
						
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
