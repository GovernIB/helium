package net.conselldemallorca.helium.v3.core.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.model.hibernate.ExplotacioDimensio;
import net.conselldemallorca.helium.core.model.hibernate.ExplotacioFets;
import net.conselldemallorca.helium.core.model.hibernate.ExplotacioTemps;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.DiaSetmanaEnum;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.DimEnum;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.Dimensio;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.DimensioDesc;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.DimensioHelium;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.ExplotacioDimensioDto;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.ExplotacioFetsDto;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.Fet;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.FetEnum;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.FetHelium;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.Format;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.IndicadorDesc;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.RegistreEstadistic;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.RegistresEstadistics;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.Temps;
import net.conselldemallorca.helium.v3.core.api.service.EstadisticaService;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.ExplotacioDimensioRepository;
import net.conselldemallorca.helium.v3.core.repository.ExplotacioFetsRepository;
import net.conselldemallorca.helium.v3.core.repository.ExplotacioTempsRepository;
import net.conselldemallorca.helium.v3.core.repository.UnitatOrganitzativaRepository;

@Service
public class EstadisticaServiceImpl implements EstadisticaService {

	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Resource
	private EntornRepository entornRepository;
	@Resource
	private ExplotacioTempsRepository explotacioTempsRepository;
	@Resource
	private ExplotacioDimensioRepository explotacioDimensioRepository;
	@Resource
	private ExplotacioFetsRepository explotacioFetsRepository;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;

	@Override
	@Transactional
	public boolean generarDadesExplotacio() {
		return generarDadesExplotacio(ahir());
	}

	@Override
	@Transactional
	public boolean generarDadesExplotacio(Date data) {
		if (data == null)
			data = ahir();
		try {
			ExplotacioTemps explotacioTemps = explotacioTempsRepository.findFirstByData(DateUtils.truncate(data, Calendar.DATE));
			if (explotacioTemps == null) {
				explotacioTemps = new ExplotacioTemps(data);
				explotacioTemps = explotacioTempsRepository.save(explotacioTemps);
			}

			List<ExplotacioDimensio> dimensions = obtenirDimensions();
			actualitzarDadesEstadistiques(explotacioTemps, dimensions);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional
	@Override
	public void generarDadesExplotacio(Date dataInici, Date dataFi) {
		if (dataFi == null) {
			dataFi = ahir();
		}

		if (isDayAfter(dataInici, dataFi)) {
			Date temp = dataInici;
			dataInici = dataFi;
			dataFi = temp;
		}

		try {
			List<Date> missingDates = geMissingExplotTempsEntities(dataInici, dataFi);
			if (!missingDates.isEmpty()) {
				generateExplotacioTemps(dataInici);
			}

			List<ExplotacioDimensio> dimensions = obtenirDimensions();
			List<ExplotacioTemps> etes = explotacioTempsRepository.findByDataIn(missingDates);
			if (etes == null || etes.isEmpty()) {
				return;
			}
			for(ExplotacioTemps ete : etes) {
				actualitzarDadesEstadistiques(ete, dimensions);
			}
		} catch (Exception ex) {
			// TODO: Controlar excepció
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void generateExplotacioTemps(Date data) {
		Date yesterday = ahir();
		// Obtenim totes les dates per al rang corresponent
		List<Date> existingDates = explotacioTempsRepository.findDatesBetween(data, yesterday);
		List<Date> missingDates = new ArrayList<Date>();
		Date day = truncDay(data);
		// Iterem només pels dies que no estan a la base de dades
		while (!isDayAfter(day, yesterday)) {
			if (!existingDates.contains(day)) {
				missingDates.add(day);
			}
			
			day = DateUtils.addDays(day, 1);
		}

		if (!missingDates.isEmpty()) {
			List<ExplotacioTemps> temsEntities = new ArrayList<ExplotacioTemps>();
			for(Date d : missingDates) {
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				temsEntities.add(
					ExplotacioTemps.builder()
						.data(d)
						.anualitat(c.get(Calendar.YEAR))
						.mes(c.get(Calendar.MONTH) + 1)
						.trimestre((c.get(Calendar.MONTH) + 1) / 3)
						.setmana(c.get(Calendar.WEEK_OF_YEAR))
						.dia(c.get(Calendar.DAY_OF_MONTH))
						.diaSetmana(DiaSetmanaEnum.valueOfData(c.get(Calendar.DAY_OF_WEEK)))
						.build());
			}
			
			explotacioTempsRepository.save(temsEntities);
			explotacioTempsRepository.flush();
		}
	}

	@Override
	public RegistresEstadistics consultaDarreresEstadistiques() {
		return consultaEstadistiques(ahir());
	}

	@Transactional
	@Override
	public RegistresEstadistics consultaEstadistiques(Date data) {
		Date dia = truncDay(data);
		ExplotacioTemps temps = explotacioTempsRepository.findFirstByData(dia);
		if (temps == null) {
			// Si no existeixen dades, les generam
			Date now = new Date();
			Date ahir = DateUtils.addDays(now, -1);
			Date dema = DateUtils.addDays(now, 1);
			if (!isDayAfter(dia, ahir)) {
				generarDadesExplotacio(data);
			} else if (isDayBefore(dia, dema)) {
				// data es el dia d'avui
				// generarDadesExplotacioBasiques(ahir, data);
			}
		}
		return getRegistresEstadistics(data);
	}
	
	@Transactional
	private RegistresEstadistics getRegistresEstadistics(Date data) {
		ExplotacioTemps temps = explotacioTempsRepository.findFirstByData(DateUtils.truncate(data, Calendar.DATE));
		if (temps == null) {
			Date dia = truncDay(data);
			return RegistresEstadistics.builder().temps(Temps.builder().data(dia).build()).fets(new ArrayList<RegistreEstadistic>()).build();
		}
		List<ExplotacioFets> fets = explotacioFetsRepository.findByTempsId(temps.getId());
		return RegistresEstadistics.builder()
				.temps(Temps
						.builder()
						.data(temps.getData())
						.anualitat(temps.getAnualitat())
						.trimestre(temps.getTrimestre())
						.build())
				.fets(toRegistreEstadistic(fets))
				.build();
	}
	
	private RegistreEstadistic toRegistreEstadistic(ExplotacioFets expFets) {
			List<Dimensio> dimensions = new ArrayList<Dimensio>();
			dimensions.add(new DimensioHelium(DimEnum.UOR.name(), expFets.getDimensio().getUnitatOrganitzativaCodi()));
			dimensions.add(new DimensioHelium(DimEnum.TIP.name(), expFets.getDimensio().getTipusCodi()));
			dimensions.add(new DimensioHelium(DimEnum.ENT.name(), expFets.getDimensio().getEntornCodi()));
			
			List<Fet> fets = new ArrayList<Fet>();
			fets.add(FetHelium.builder().codi(FetEnum.EXP_TOT.name()).valor(expFets.getExpedientsTotals().doubleValue()).build());
			fets.add(FetHelium.builder().codi(FetEnum.EXP_OBE.name()).valor(expFets.getExpedientsOberts().doubleValue()).build());
			fets.add(FetHelium.builder().codi(FetEnum.EXP_TAN.name()).valor(expFets.getExpedientsTancats().doubleValue()).build());
			fets.add(FetHelium.builder().codi(FetEnum.EXP_ANUL.name()).valor(expFets.getExpedientsAnulats().doubleValue()).build());
			fets.add(FetHelium.builder().codi(FetEnum.EXP_NO_ANUL.name()).valor(expFets.getExpedientsNoAnulats().doubleValue()).build());
			fets.add(FetHelium.builder().codi(FetEnum.EXP_ARX.name()).valor(expFets.getExpedientsArxiu().doubleValue()).build());
			fets.add(FetHelium.builder().codi(FetEnum.TAS_PEN.name()).valor(expFets.getTasquesPendents().doubleValue()).build());
			fets.add(FetHelium.builder().codi(FetEnum.TAS_FIN.name()).valor(expFets.getTasquesFinalitzades().doubleValue()).build());
			fets.add(FetHelium.builder().codi(FetEnum.AN_PEN.name()).valor(expFets.getAnotacionPendents().doubleValue()).build());
			fets.add(FetHelium.builder().codi(FetEnum.AN_PROC.name()).valor(expFets.getAnotacionProcessades().doubleValue()).build());
			fets.add(FetHelium.builder().codi(FetEnum.CO_PINBAL.name()).valor(expFets.getPeticionsPinbal().doubleValue()).build());
			fets.add(FetHelium.builder().codi(FetEnum.CO_NOTIB.name()).valor(expFets.getPeticionsNotib().doubleValue()).build());
			fets.add(FetHelium.builder().codi(FetEnum.CO_PORTAFIB.name()).valor(expFets.getPeticionsPortafib().doubleValue()).build());
			
			return RegistreEstadistic.builder()
						.dimensions(dimensions)
						.fets(fets)
						.build();
	}
	
	private List<RegistreEstadistic> toRegistreEstadistic(List<ExplotacioFets> listFets) {
		List<RegistreEstadistic> result = new ArrayList<RegistreEstadistic>();
		for(ExplotacioFets expFets : listFets) {
			result.add(toRegistreEstadistic(expFets));
		}
		return result;
	}

	@Transactional
	@Override
	public List<RegistresEstadistics> consultaEstadistiques(Date dataInici, Date dataFi) {
		generarDadesExplotacio(dataInici, dataFi);
		Date dia = truncDay(dataInici);
		List<RegistresEstadistics> registres = new ArrayList<RegistresEstadistics>();
		while(DateUtils.truncatedCompareTo(dia, dataFi, Calendar.DATE) <= 0) {
			registres.add(getRegistresEstadistics(dia));
			dia = DateUtils.addDays(dia, 1);
		}
		return registres;
	}
	
	// Obtenir dates sense dades estadístiques
	private List<Date> geMissingExplotTempsEntities(Date fromDate, Date toDate) {

		List<Date> missingDates = new ArrayList<Date>();
		if (toDate == null) {
			toDate = ahir();
		}
		toDate = truncDay(toDate);
		fromDate = truncDay(fromDate);
		
		// Obtenim totes les dates per al rang corresponent
		List<Date> existingDates = explotacioTempsRepository.findDatesBetween(fromDate, toDate);

		// Iterem només pels dies que no estan a la base de dades
		Date currentDate = fromDate;
		while (DateUtils.truncatedCompareTo(currentDate, toDate, Calendar.DATE) <= 0) {
			if (!existingDates.contains(currentDate)) {
				missingDates.add(currentDate);
			}
			currentDate = DateUtils.addDays(currentDate, 1);
		}
		return missingDates;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DimensioDesc> getDimensions() {
		List<String> tipusExpedient = expedientTipusRepository.findAllCodis();
		List<String> unitatsOrganitzatives = unitatOrganitzativaRepository.findAllCodis();
		List<String> entorns = entornRepository.findAllCodis();
		return Lists.newArrayList(
				DimensioDesc.builder().codi(DimEnum.UOR.name()).nom(DimEnum.UOR.getNom())
						.descripcio(DimEnum.UOR.getDescripcio()).valors(unitatsOrganitzatives).build(),
				DimensioDesc.builder().codi(DimEnum.TIP.name()).nom(DimEnum.TIP.getNom())
						.descripcio(DimEnum.TIP.getDescripcio()).valors(tipusExpedient).build(),
				DimensioDesc.builder().codi(DimEnum.ENT.name()).nom(DimEnum.ENT.getNom())
						.descripcio(DimEnum.ENT.getDescripcio()).valors(entorns).build());
	}

	@Override
	public List<IndicadorDesc> getIndicadors() {
		List<IndicadorDesc> indicadors = new ArrayList<IndicadorDesc>();
		for (FetEnum fet : FetEnum.values()) {
			indicadors.add(IndicadorDesc.builder().codi(fet.name()).nom(fet.getNom()).descripcio(fet.getDescripcio())
					.format(Format.LONG).build());
		}
		return indicadors;
	}

	private Date ahir() {
		return truncDay(DateUtils.addDays(new Date(), -1));
	}
	
	@Transactional
	private List<ExplotacioDimensio> obtenirDimensions() {
		List<ExplotacioDimensioDto> dimensionsPerEstadistiques = explotacioDimensioRepository
				.getDimensionsPerEstadistiques();
		List<ExplotacioDimensio> dimensionsEnDb = explotacioDimensioRepository.findAllOrdered();
		return actualitzarDimensions(dimensionsEnDb, dimensionsPerEstadistiques);
	}

	@Transactional
	private List<ExplotacioDimensio> actualitzarDimensions(List<ExplotacioDimensio> dimensionsEnDb,
			List<ExplotacioDimensioDto> dimensionsPerEstadistiques) {
		List<ExplotacioDimensio> dimensions = new ArrayList<ExplotacioDimensio>();
		for (ExplotacioDimensioDto dimensioConsulta : dimensionsPerEstadistiques) {
			// Comprova si existeix a la base de dades, si no, la guarda
			ExplotacioDimensio dimensioEntity = explotacioDimensioRepository.findFirstByEntornIdAndTipusIdAndUnitatOrganitzativaIdOrderById(
					dimensioConsulta.getEntornId(), 
					dimensioConsulta.getTipusId(), 
					dimensioConsulta.getUnitatOrganitzativaId(),
					dimensioConsulta.getUnitatOrganitzativaId() == null);
			if (dimensioEntity == null) {
				dimensioEntity = ExplotacioDimensio.builder()
						.unitatOrganitzativaId(dimensioConsulta.getUnitatOrganitzativaId())
						.unitatOrganitzativaCodi(dimensioConsulta.getUnitatOrganitzativaCodi())
						.entornId(dimensioConsulta.getEntornId()).entornCodi(dimensioConsulta.getEntornCodi())
						.tipusId(dimensioConsulta.getTipusId()).tipusCodi(dimensioConsulta.getTipusCodi()).build();
				dimensioEntity = explotacioDimensioRepository.save(dimensioEntity);
				dimensionsEnDb.add(dimensioEntity);
				dimensions.add(dimensioEntity);
			} else {
				dimensions.add(dimensioEntity);
			}
		}
		return dimensions;
	}

	@Transactional
	private void actualitzarDadesEstadistiques(ExplotacioTemps ete, List<ExplotacioDimensio> dimensions) {
		// Eliminam les dades d'explotació de la data
		List<ExplotacioFets> ef = explotacioFetsRepository.findByTempsId(ete.getId());
		explotacioFetsRepository.delete(ef);
		//explotacioFetsRepository.deleteByTempsId(ete.getId());
		Date data = DateUtils.truncate(DateUtils.addDays(ete.getData(), 1), Calendar.DATE);
		List<ExplotacioFetsDto> estadistiques = getFetsPerEstadistiques(data);
		
		int dadaIndex = 0;
		int dimensionIndex = 0;
		// Les dues llistes estan ordenades
		while (dadaIndex < estadistiques.size() && dimensionIndex < dimensions.size()) {
			ExplotacioFetsDto estadistica = estadistiques.get(dadaIndex);
			ExplotacioDimensio dimension = dimensions.get(dimensionIndex);
			
			int comparison = compareEstadistiquesAndDimensions(estadistica, dimension);
			if (comparison == 0) {
				saveToFetsEntity(estadistica, dimension, ete);
				dadaIndex++;
			} else if (comparison < 0) {
				dadaIndex++;
			} else {
				dimensionIndex++;
			}
		}
	}
	
	@Transactional
	private List<ExplotacioFetsDto> getFetsPerEstadistiques(Date data) {
		List<ExplotacioFetsDto> estadistiques = new ArrayList<ExplotacioFetsDto>();
		List<Object[]> estadistiquesRe = explotacioFetsRepository.getFetsPerEstadistiques(
				data == null,
				data);
		for(Object[] e : estadistiquesRe) {
			estadistiques.add(new ExplotacioFetsDto(
					e[0] == null? null : ((BigDecimal) e[0]).longValue(),	// entornId
					e[1] == null? null : ((BigDecimal) e[1]).longValue(),	// tipusExpedientId
					e[2] == null? null : ((BigDecimal) e[2]).longValue(),	// unitatOrganitzativaId
					e[3] == null? null : ((BigDecimal) e[3]).longValue(),	// expedientsOberts
					e[4] == null? null : ((BigDecimal) e[4]).longValue(),	// expedientsTancats
					e[5] == null? null : ((BigDecimal) e[5]).longValue(),	// expedientsAnulats
					e[6] == null? null : ((BigDecimal) e[6]).longValue(),	// expedientsArxiu
					e[7] == null? null : ((BigDecimal) e[7]).longValue(),	// expedientsNoAnulats
					e[8] == null? null : ((BigDecimal) e[8]).longValue(),	// expedientsTotals
					e[9] == null? null : ((BigDecimal) e[9]).longValue(),	// tasquesPendents
					e[10] == null? null : ((BigDecimal) e[10]).longValue(),	// tasquesFinalitzades
					e[11] == null? null : ((BigDecimal) e[11]).longValue(),	// anotacionPendents
					e[12] == null? null : ((BigDecimal) e[12]).longValue(),	// anotacionProcessades
					e[13] == null? null : ((BigDecimal) e[13]).longValue(),	// peticionsPinbal
					e[14] == null? null : ((BigDecimal) e[14]).longValue(),	// peticionsPortafib
					e[15] == null? null : ((BigDecimal) e[15]).longValue()	// peticionsNotib
			));
		}
		return estadistiques;
	}
	
	@Transactional
	private void saveToFetsEntity(ExplotacioFetsDto estadistiques, ExplotacioDimensio dimensio, ExplotacioTemps ete) {
		ExplotacioFets fetsEntity = ExplotacioFets
									.builder()
									.dimensio(dimensio)
									.temps(ete)
									.expedientsTotals(estadistiques.getExpedientsTotals())
									.expedientsOberts(estadistiques.getExpedientsOberts())
									.expedientsTancats(estadistiques.getExpedientsTancats())
									.expedientsAnulats(estadistiques.getExpedientsAnulats())
									.expedientsNoAnulats(estadistiques.getExpedientsNoAnulats())
									.expedientsArxiu(estadistiques.getExpedientsArxiu())
									.expedientsNoAnulats(estadistiques.getExpedientsNoAnulats())
									.expedientsTotals(estadistiques.getExpedientsTotals())
									.tasquesPendents(estadistiques.getTasquesPendents())
									.tasquesFinalitzades(estadistiques.getTasquesFinalitzades())
									.anotacionPendents(estadistiques.getAnotacionPendents())
									.anotacionProcessades(estadistiques.getAnotacionProcessades())
									.peticionsPinbal(estadistiques.getPeticionsPinbal())
									.peticionsNotib(estadistiques.getPeticionsNotib())
									.peticionsPortafib(estadistiques.getPeticionsPortafib())
									.build();
		explotacioFetsRepository.save(fetsEntity);
	}

	private int compareEstadistiquesAndDimensions(ExplotacioFetsDto estadistiques, ExplotacioDimensio dimension) {
		int result = compareFields(estadistiques.getEntornId(), dimension.getEntornId());
		if (result != 0)
			return result;

		result = compareFields(estadistiques.getTipusExpedientId(), dimension.getTipusId());
		if (result != 0)
			return result;
		
		return compareFields(estadistiques.getUnitatOrganitzativaId(), dimension.getUnitatOrganitzativaId());
	}
	
	private <T extends Comparable<T>> int compareFields(T field1, T field2) {
		if (field1 == null && field2 == null) return 0;
		if (field1 == null) return -1;
		if (field2 == null) return 1;
		return field1.compareTo(field2);
	}
	
	private boolean isDayAfter(Date date1, Date date2) {
		return DateUtils.truncatedCompareTo(date1, date2, Calendar.DATE) > 0;
	}
	
	private boolean isDayBefore(Date date1, Date date2) {
		return DateUtils.truncatedCompareTo(date1, date2, Calendar.DATE) > 0;
	}
	
	private Date truncDay(Date date) {
		return DateUtils.truncate(date, Calendar.DATE);
	}
	
}
