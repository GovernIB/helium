package es.caib.helium.logic.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import es.caib.comanda.model.server.monitoring.*;
import es.caib.helium.logic.intf.util.DatesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import es.caib.helium.commons.dto.ExplotacioDimensioDto;
import es.caib.helium.commons.dto.ExplotacioFetsDto;
import es.caib.helium.commons.dto.comanda.DimEnum;
import es.caib.helium.commons.dto.comanda.FetEnum;
import es.caib.helium.logic.intf.service.EstadisticaService;
import es.caib.helium.persistence.entity.ExplotacioDimensio;
import es.caib.helium.persistence.entity.ExplotacioFets;
import es.caib.helium.persistence.entity.ExplotacioTemps;
import es.caib.helium.persistence.repository.EntornRepository;
import es.caib.helium.persistence.repository.ExpedientTipusRepository;
import es.caib.helium.persistence.repository.ExplotacioDimensioRepository;
import es.caib.helium.persistence.repository.ExplotacioFetsRepository;
import es.caib.helium.persistence.repository.ExplotacioTempsRepository;
import es.caib.helium.persistence.repository.UnitatOrganitzativaRepository;

@Slf4j
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
 			log.error(String.format("Error generant dades d'explotació per data [%s]", data.toString()), e);
		}
		return false;
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
			log.error(String.format("Error generant dades d'explotació per dates [%s - %s]", dataInici.toString(), dataFi.toString()), ex);
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
						.build());
			}

			explotacioTempsRepository.saveAll(temsEntities);
			explotacioTempsRepository.flush();
		}
	}

	@Transactional
	@Override
	public RegistresEstadistics consultaDarreresEstadistiques() {
		return consultaEstadistiques(ahir());
	}

	@Transactional
	@Override
	public RegistresEstadistics consultaEstadistiques(Date data) {
		generarDadesExplotacio(data);
		return getRegistresEstadistics(data);
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

	@Override
	@Transactional(readOnly = true)
	public List<DimensioDesc> getDimensions() {
		List<String> tipusExpedient = expedientTipusRepository.findAllCodis();
		List<String> unitatsOrganitzatives = unitatOrganitzativaRepository.findAllCodis();
		List<String> entorns = entornRepository.findAllCodis();
		return Lists.newArrayList(
				new DimensioDesc().codi(DimEnum.UOR.name()).nom(DimEnum.UOR.getNom())
						.descripcio(DimEnum.UOR.getDescripcio()).valors(unitatsOrganitzatives),
				new DimensioDesc().codi(DimEnum.TIP.name()).nom(DimEnum.TIP.getNom())
						.descripcio(DimEnum.TIP.getDescripcio()).valors(tipusExpedient),
				new DimensioDesc().codi(DimEnum.ENT.name()).nom(DimEnum.ENT.getNom())
						.descripcio(DimEnum.ENT.getDescripcio()).valors(entorns));
	}

	@Override
	public List<IndicadorDesc> getIndicadors() {
		List<IndicadorDesc> indicadors = new ArrayList<IndicadorDesc>();
		for (FetEnum fet : FetEnum.values()) {
			indicadors.add(new IndicadorDesc()
									.codi(fet.name())
									.nom(fet.getNom())
									.descripcio(fet.getDescripcio())
									.format(Format.LONG));
		}
		return indicadors;
	}


	private RegistresEstadistics getRegistresEstadistics(Date data) {
		ExplotacioTemps temps = explotacioTempsRepository.findFirstByData(DateUtils.truncate(data, Calendar.DATE));
		if (temps == null) {
			Date dia = truncDay(data);
			return new RegistresEstadistics()
				.temps(DatesUtils.toOffsetDateTime(dia))
				.fets(new ArrayList<RegistreEstadistic>());
		}
		List<ExplotacioFets> fets = explotacioFetsRepository.findByTempsId(temps.getId());
		return new RegistresEstadistics()
			.temps(DatesUtils.toOffsetDateTime(temps.getData()))
			.fets(toRegistreEstadistic(fets));
	}

	private RegistreEstadistic toRegistreEstadistic(ExplotacioFets expFets) {
		List<Dimensio> dimensions = new ArrayList<Dimensio>();
		dimensions.add(new Dimensio().codi(DimEnum.UOR.name()).valor(expFets.getDimensio().getUnitatOrganitzativaCodi()));
		dimensions.add(new Dimensio().codi(DimEnum.TIP.name()).valor(expFets.getDimensio().getTipusCodi()));
		dimensions.add(new Dimensio().codi(DimEnum.ENT.name()).valor(expFets.getDimensio().getEntornCodi()));

		List<Fet> fets = new ArrayList<Fet>();
		fets.add(new Fet().codi(FetEnum.EXP_TOT.name()).valor(expFets.getExpedientsTotals().doubleValue()));
		fets.add(new Fet().codi(FetEnum.EXP_OBE.name()).valor(expFets.getExpedientsOberts().doubleValue()));
		fets.add(new Fet().codi(FetEnum.EXP_TAN.name()).valor(expFets.getExpedientsTancats().doubleValue()));
		fets.add(new Fet().codi(FetEnum.EXP_ANUL.name()).valor(expFets.getExpedientsAnulats().doubleValue()));
		fets.add(new Fet().codi(FetEnum.EXP_NO_ANUL.name()).valor(expFets.getExpedientsNoAnulats().doubleValue()));
		fets.add(new Fet().codi(FetEnum.EXP_ARX.name()).valor(expFets.getExpedientsArxiu().doubleValue()));
		fets.add(new Fet().codi(FetEnum.TAS_PEN.name()).valor(expFets.getTasquesPendents().doubleValue()));
		fets.add(new Fet().codi(FetEnum.TAS_FIN.name()).valor(expFets.getTasquesFinalitzades().doubleValue()));
		fets.add(new Fet().codi(FetEnum.AN_PEN.name()).valor(expFets.getAnotacionPendents().doubleValue()));
		fets.add(new Fet().codi(FetEnum.AN_PROC.name()).valor(expFets.getAnotacionProcessades().doubleValue()));
		fets.add(new Fet().codi(FetEnum.CO_PINBAL.name()).valor(expFets.getPeticionsPinbal().doubleValue()));
		fets.add(new Fet().codi(FetEnum.CO_NOTIB.name()).valor(expFets.getPeticionsNotib().doubleValue()));
		fets.add(new Fet().codi(FetEnum.CO_PORTAFIB.name()).valor(expFets.getPeticionsPortafib().doubleValue()));

		return new RegistreEstadistic()
			.dimensions(dimensions)
			.fets(fets);
	}

	private List<RegistreEstadistic> toRegistreEstadistic(List<ExplotacioFets> listFets) {
		List<RegistreEstadistic> result = new ArrayList<RegistreEstadistic>();
		for(ExplotacioFets expFets : listFets) {
			result.add(toRegistreEstadistic(expFets));
		}
		return result;
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

	private Date ahir() {
		return truncDay(DateUtils.addDays(new Date(), -1));
	}

	private List<ExplotacioDimensio> obtenirDimensions() {
		List<ExplotacioDimensioDto> dimensionsPerEstadistiques = explotacioDimensioRepository
				.getDimensionsPerEstadistiques();
		List<ExplotacioDimensio> dimensionsEnDb = explotacioDimensioRepository.findAllOrdered();
		return actualitzarDimensions(dimensionsEnDb, dimensionsPerEstadistiques);
	}

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

	private void actualitzarDadesEstadistiques(ExplotacioTemps ete, List<ExplotacioDimensio> dimensions) {
		// Eliminam les dades d'explotació de la data
		List<ExplotacioFets> ef = explotacioFetsRepository.findByTempsId(ete.getId());
		explotacioFetsRepository.deleteAll(ef);
		Date data = DateUtils.truncate(ete.getData(), Calendar.DATE);
		List<ExplotacioFetsDto> estadistiques = getFetsPerEstadistiques(data);

		Map<List<Long>, ExplotacioDimensio> dimensionsMap = new HashMap<List<Long>, ExplotacioDimensio>();
		for(ExplotacioDimensio dimensio : dimensions) {
			dimensionsMap.put(
					Lists.newArrayList(dimensio.getEntornId(), dimensio.getTipusId(), dimensio.getUnitatOrganitzativaId()),
					dimensio);
		}

		for (ExplotacioFetsDto estadistica :  estadistiques) {
			// ExplotacioFetsDto  = estadistiques.get(dadaIndex);
			ExplotacioDimensio dimension = dimensionsMap.get(
					Lists.newArrayList(
							estadistica.getEntornId(),
							estadistica.getTipusExpedientId(),
							estadistica.getUnitatOrganitzativaId()));
			// Si el un tipus mai ha tingut expedients l'ignoram
			if(dimension == null)
				continue;
			saveToFetsEntity(estadistica, dimension, ete);
		}
	}

	private List<ExplotacioFetsDto> getFetsPerEstadistiques(Date data) {
		List<ExplotacioFetsDto> estadistiques = new ArrayList<ExplotacioFetsDto>();
		List<Object[]> estadistiquesRe = explotacioFetsRepository.getFetsPerEstadistiques(
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
