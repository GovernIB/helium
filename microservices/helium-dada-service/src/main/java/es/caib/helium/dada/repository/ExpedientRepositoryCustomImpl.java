package es.caib.helium.dada.repository;

import es.caib.helium.client.dada.dades.enums.ColleccionsMongo;
import es.caib.helium.client.dada.dades.enums.DireccioOrdre;
import es.caib.helium.client.dada.dades.enums.Tipus;
import es.caib.helium.client.dada.dades.enums.TipusFiltre;
import es.caib.helium.dada.enums.Capcalera;
import es.caib.helium.dada.enums.Dada;
import es.caib.helium.dada.exception.DadaException;
import es.caib.helium.dada.model.Columna;
import es.caib.helium.dada.model.Consulta;
import es.caib.helium.dada.model.Expedient;
import es.caib.helium.dada.model.FiltreCapcalera;
import es.caib.helium.dada.model.FiltreValor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnsetOperation;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Classe dedicada a ampliar les funcionalitats de ExpedientRepository en funció de les necessitats del servei
 */
public class ExpedientRepositoryCustomImpl implements ExpedientRepositoryCustom {

	private final MongoTemplate mongoTemplate;

	@Autowired
	public ExpedientRepositoryCustomImpl(MongoTemplate mongoTemplate) {

		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * Busca la informació de l'expedient (capçalera i dades) filtrada segons la consulta.
	 * Si la consulta no conté filtres tornarà tots els Expedients (dades capçalera) on l'atribut dades contindrà la llista de Dada asociades a l'expedientId
	 * @param consulta Objecte consulta que conté els paràmetres de cerca
	 * @return Retorna la llista d'expedients filtrada
	 */
	@Override
	public List<Expedient> findByFiltres(Consulta consulta) {
		
		if (consulta == null) {
			return new ArrayList<>();
		}
		var collation = Collation.of("es").numericOrdering(true);
		var options = AggregationOptions.builder().collation(collation).build();
		var aggregation = Aggregation.newAggregation(prepararFiltres(consulta)).withOptions(options);
		return mongoTemplate.aggregate(aggregation, Expedient.class, Expedient.class).getMappedResults();
	}

	/**
	 * Esborra la informació de la capçalera de l'expedient i les dades relacionades amb l'expedientId
	 * @param expedientId identificador de l'expedient a esborrar
	 * @return Retorna el número d'expedients esborrats
	 */
	@Override
	@Transactional
	public Long esborrarExpedientCascade(Long expedientId) throws DadaException {
		if (expedientId == null) {
			return 0l;
		}
		List<Long> expedients = new ArrayList<>();
		expedients.add(expedientId);
		return esborrarExpedientsCascade(expedients);
	}

	/**
	 * Esborra per cada expedientId la informació de la capçalera de l'expedient i les dades relacionades amb l'expedientId
	 * @param expedients llista de identifacadors expedientId
	 * @return Retorna el número d'expedients esborrats.
	 */
	@Override
	@Transactional
	public Long esborrarExpedientsCascade(List<Long> expedients) throws DadaException {
		var query = new Query();
		var criteria = new Criteria();
		criteria.and(Capcalera.EXPEDIENT_ID.getCamp()).in(expedients);
		query.addCriteria(criteria);
		var nEsborrats = mongoTemplate.remove(query, Expedient.class, ColleccionsMongo.EXPEDIENT.getNom());
		if (nEsborrats == null || nEsborrats.getDeletedCount() != expedients.size()) {
			throw new DadaException("No s'han pogut esborrar tots els expedients " + expedients.toString());
		}
		mongoTemplate.remove(query, Dada.class, ColleccionsMongo.DADA.getNom());
		return nEsborrats.getDeletedCount();
	}

	@Override
	public List<Expedient> getExpedientIdProcesPrincipalIdByExpedientIds(List<Long> ids) {

		if (ids == null || ids.isEmpty()) {
			return new ArrayList<>();
		}

		var criteria = new Criteria();
		criteria.and(Capcalera.EXPEDIENT_ID.getCamp()).in(ids);
		var match = Aggregation.match(criteria);
		var projection = Aggregation.project(Capcalera.EXPEDIENT_ID.getCamp(), Capcalera.PROCES_PRINCIPAL_ID.getCamp());
		var operations = new ArrayList<AggregationOperation>();
		operations.add(match);
		operations.add(projection);
		return mongoTemplate
				.aggregate(Aggregation.newAggregation(operations), Expedient.class, Expedient.class)
				.getMappedResults();
	}

	@Override
	public Expedient getExpedientIdProcesPrincipalIdByExpedientId(Long id) throws DadaException {

		if (id == null) {
			return null;
		}

		var criteria = new Criteria();
		criteria.and(Capcalera.EXPEDIENT_ID.getCamp()).is(id);
		var match = Aggregation.match(criteria);
		var projection = Aggregation.project(Capcalera.EXPEDIENT_ID.getCamp(), Capcalera.PROCES_PRINCIPAL_ID.getCamp());
		var operations = new ArrayList<AggregationOperation>();
		operations.add(match);
		operations.add(projection);
		var results = mongoTemplate
				.aggregate(Aggregation.newAggregation(operations), Expedient.class, Expedient.class)
				.getMappedResults();

		if (results.size() > 1) {
			throw new DadaException("Error obtinguent l'expedient amb id " + id + " hi ha " + results.size() + " expedients amb el mateix id");
		}

		return results.get(0);
	}

	/**
	 * Prepara la pipeline de AggregationOperation per l'agregació (db.expedient.aggregate([]). 
	 * Sempre inclou la operació {$lookup: {from: "dada", localField:"expedientId", foreignField: "expedientId", as: "dades"}ds
	 * @param consulta Objecte Consulta d'on d'extreu la informació per preparar les diferents AggregationOperation
	 * @return Retorna una llista d'AggregationOperation apunt per ser executada amb mongoTemplate.aggregate
	 */
	private List<AggregationOperation> prepararFiltres(Consulta consulta) {

		// Pipeline per l'agregació (db.expedient.aggregate([])
		// L'ordre de les operacions afecta l'eficiencia de la cerca. Els més restrictius primer.
		List<AggregationOperation> operations = new ArrayList<>();
		if (consulta.getEntornId() != null && consulta.getExpedientTipusId() != null) {
		 	operations.add(crearFiltreEntornIdTipusId(consulta.getEntornId(), consulta.getExpedientTipusId()));
		}

		List<FiltreValor> filtresValor = new ArrayList<>();
		if (consulta.getFiltreValors() != null) {
			var valors = consulta.getFiltreValors().values();
			var filtreCapcaleraCreat = false;
			for (var filtre : valors) {
				// Si hi han 2 filtres capçalera, només es queda el primer que li passa
				if (filtre instanceof FiltreCapcalera && !filtreCapcaleraCreat) {
					// $match filtresCapcalera
					operations.add(crearFiltreCapcalera((FiltreCapcalera) filtre));
					filtreCapcaleraCreat = true;
				} else if (filtre instanceof FiltreValor) {
					filtresValor.add((FiltreValor) filtre); // Es guarden per pasar a la pipeline després del $lookup
				}
			}
		}
		// $lookup
		operations.add(Aggregation.lookup(ColleccionsMongo.DADA.getNom(), Capcalera.EXPEDIENT_ID.getCamp(),
				Capcalera.EXPEDIENT_ID.getCamp(), Capcalera.DADES.getCamp()));

		// $match filtresValor
		if (filtresValor.size() > 0) {
			var matchOperation = crearFiltresValor(filtresValor);
			if (matchOperation != null) {
				operations.add(matchOperation);
			}
		}

		// $project $sort
		if (consulta.getColumnes() != null && consulta.getColumnes().size() > 0) {
			//Per defecte es retornaran l'identificador de l'expedient, el número, el títol, la data d'alta i l'estat.
			prepararColumnes(consulta.getColumnes(), operations);
		}

		// $skip $limit
		if (consulta.getPage() != null && consulta.getSize() != null) {
			operations.add(
					Aggregation.skip(consulta.getPage() > 0 ? ((consulta.getPage() - 1) * consulta.getSize()) : 0l));
			operations.add(Aggregation.limit(consulta.getSize()));
		}

		return operations;
	}

	/**
	 * Afegeix a la llista operations la projecció del es columnes demanades.
	 * També s'encarrega de crear la AggregationOperation referent al Sort
	 * @param columnes llista de columnes que es volen al resultat
	 * @param operations llista on s'afegirà la Projection i el Sort
	 */
	private void prepararColumnes(List<Columna> columnes, List<AggregationOperation> operations) {

		var cols = new ArrayList<String>();
		var ordres = new ArrayList<Columna>();
		var codisDadaValor = new ArrayList<String>();
		cols.add(Capcalera.ID.getCamp());
		var hiHaOrdenacioDeDades = false;
		for (var columna : columnes) {
			// Columna de capçalera
			if (ObjectUtils.containsConstant(Capcalera.values(), columna.getNom())) {
				var col = Capcalera.valueOf(columna.getNom().toUpperCase()).getCamp();
				cols.add(col);
				if (columna.getOrdre() != null) {
					columna.setNom(col);
					ordres.add(columna);
				}
				continue;
			}
			// La columna és del tipus Dada. columna.getNom() ha de contenir el codi a buscar
			codisDadaValor.add(columna.getNom());
			if (columna.getOrdre() != null) {
				columna.setNom(Dada.CODI.getCamp());
				ordres.add(columna);
				hiHaOrdenacioDeDades = ColleccionsMongo.DADA.equals(columna.getOrdre().getTipus());
			}
		}
		if (!codisDadaValor.isEmpty()) {
			cols.add(Capcalera.DADES.getCamp());
		}
		var projection = Aggregation.project(Fields.fields(cols.toArray(new String[cols.size()])));
		ProjectionOperation projectionDades = null;
		if (!codisDadaValor.isEmpty()) { // Es filtraran les dades en funcio del seu codi
			projectionDades = projection.and(ArrayOperators.Filter.filter("$" + Capcalera.DADES.getCamp()).as(Capcalera.DADES.getCamp())
			.by(ArrayOperators.In.arrayOf(codisDadaValor).containsValue("$$" + Capcalera.DADES.getCamp() + "." + Dada.CODI.getCamp()))).as(Capcalera.DADES.getCamp());
		}
		operations.add(projectionDades != null ? projectionDades : projection);

		if (ordres.isEmpty()) {
			return;
		}
		if (hiHaOrdenacioDeDades) {
			// Afegir $addFields per ordenar pels valors de les dades
			operations.add(AddFieldsOperation.addField("ordre").withValue("$dades.valor.valor").build());
		}
		// TODO MS: ACAVAR D'IMPLEMENTAR I PROVAR EL SORT AMB ELS DIFERENTS TIPUS DE DADES. CAS DE VARIABLES MULTIPLES I ALGUN POSSIBLE CAS NO CONTEMPLAT.
		// TODO MS: EL ADD FIELDS NO FUNCIONARÀ BÉ SI HI HA MÉS D'UNA VARIABLE TIPUS DADA A FILTRAR,
		operations.add(crearSortOperation(ordres));
		if (hiHaOrdenacioDeDades) {
			// Treure el camp afegit amb $addFields per ordenar per els valors de les dades.
			operations.add(UnsetOperation.unset("ordre"));
		}
	}

	/**
	 * Crea la $sort per la llista de columnes demanades
	 * Actualment només ordena per els camps de capçalera. Falta completar el TODO
	 * @param columnes llista de columnes amb l'ordre que han de tenir
	 * @return Retorna la AggregationOperation Sort amb els ordres desitjats.
	 */
	private SortOperation crearSortOperation(List<Columna> columnes) {

		columnes.sort((foo, bar) -> Integer.compare(foo.getOrdre().getOrdre(), bar.getOrdre().getOrdre()));
		List<Order> ordres = new ArrayList<>();
		for (var columna : columnes) {
			if (columna.getNom() == null || columna.getNom().isEmpty() || columna.getNom().equals("_id")
					|| columna.getNom().equals("id")) {
				continue;
			}
			if (columna.getOrdre().getTipus().equals(ColleccionsMongo.EXPEDIENT)) {
				var nom = columna.getNom().equals(Capcalera.DADES.getCamp()) ? "$$dades." + Dada.VALOR.getCamp() : columna.getNom();
				ordres.add(new Order(DireccioOrdre.ASC.equals(columna.getOrdre().getDireccio()) ? Direction.ASC : Direction.DESC, nom));
				continue;
			}

			// TODO falta la part que no son camps de capçalera
			ordres.add(new Order(DireccioOrdre.ASC.equals(columna.getOrdre().getDireccio()) ? Direction.ASC : Direction.DESC,"ordre"));
		}
		if (ordres.isEmpty()) { // Si no hi ha ordenacions, ordena per expedientId
			ordres.add(new Order(Direction.DESC, Capcalera.EXPEDIENT_ID.getCamp()));
		}
		return Aggregation.sort(Sort.by(ordres));
	}

	/**
	 * Crea un $match segons l'identificador de l'entorn i el tipus d'expedient
	 * @param entornId identificador de l'entorn
	 * @param expedientTipusId identificador del tipus d'expedient
	 * @return Retorna una AggregationOperation tipus Match amb els criteris per filtrar
	 */
	private MatchOperation crearFiltreEntornIdTipusId(Integer entornId, Integer expedientTipusId) {

		var criteria = new Criteria();
		criteria.and(Capcalera.ENTORN_ID.getCamp()).is(entornId).and(Capcalera.TIPUS_ID.getCamp()).is(expedientTipusId);
		return Aggregation.match(criteria);
	}

	/**
	 * Crea el $match per filtrar segons els camps que poden tenir les dades de capçalera
	 * @param filtreCapcalera conté els possibles valors a filtrar
	 * @return Retorna uan AggregationOperation tipus Match amb els criteris filtrar
	 */
	private MatchOperation crearFiltreCapcalera(FiltreCapcalera filtreCapcalera) {

		var criteria = new Criteria();
		if (filtreCapcalera.getExpedientId() != null) {
			criteria.and(Capcalera.EXPEDIENT_ID.getCamp()).is(filtreCapcalera.getExpedientId());
		}
		if (filtreCapcalera.getNumero() != null) {
			criteria.and(Capcalera.NUMERO.getCamp()).regex(filtreCapcalera.getNumero());
		}
		if (filtreCapcalera.getTitol() != null) {
			criteria.and(Capcalera.TITOL.getCamp()).regex(filtreCapcalera.getTitol());
		}
		if (filtreCapcalera.getProcesPrincipalId() != null) {
			criteria.and(Capcalera.PROCES_PRINCIPAL_ID.getCamp()).is(filtreCapcalera.getProcesPrincipalId());
		}
		if (filtreCapcalera.getEstatId() != null) {
			criteria.and(Capcalera.ESTAT_ID.getCamp()).is(filtreCapcalera.getEstatId());
		}

		prepararCriteriaDataCapcalera(filtreCapcalera.getDataIniciInicial(), filtreCapcalera.getDataIniciFinal(), criteria,
				Capcalera.DATA_INICI.getCamp());
		prepararCriteriaDataCapcalera(filtreCapcalera.getDataFiInicial(), filtreCapcalera.getDataFiFinal(), criteria,
				Capcalera.DATA_FI.getCamp());

		return Aggregation.match(criteria);
	}

	/**
	 * Crea la part del $match segons els filtresValor que servirà per filtrar els resultats del $lookup previ.
	 * 
	 * per exemple:
	 *  {$match: { "dades": {$elemMatch: {codi: "codi1", tipus: "Boolean", valor: {$elemMatch: {valor: "prova3"}}}}}}])
	 *  
	 * @param filtresValor llista d'objectes FiltreValor que servirà per montar el $match. No pot ser null.
	 * @return Retorna AggregationOperation tipus Match amb els criteris per filtrar
	 */
	private MatchOperation crearFiltresValor(List<FiltreValor> filtresValor) {

		List<Criteria> filtres = new ArrayList<>();
		for (var filtre : filtresValor) {
			var filtreValor = crearFiltreValor(filtre);
			if (filtreValor != null) {
				filtres.add(filtreValor);
			}
		}
		if (filtres.isEmpty()) {
			return null;
		}
		var criteria = new Criteria();
		criteria.andOperator(filtres.toArray(new Criteria[filtres.size()]));
		return Aggregation.match(criteria);
	}

	
	/**
	 * Prepara el criteri a filtrar contingut en filtreValor.
	 * @param filtreValor conté els valors pels quals es filtrarà. No pot ser null
	 * @return Retorna el Criteria amb el que es farà el filtre. Null si no hi han valors a filtrar.
	 */
	private Criteria crearFiltreValor(FiltreValor filtreValor) {

		var filtreCriteria = new Criteria();
		if (filtreValor.getCodi() != null && !filtreValor.getCodi().isEmpty()) {
			filtreCriteria.and(Dada.CODI.getCamp()).is(filtreValor.getCodi());
		}
		if (filtreValor.getTipus() != null) {
			filtreCriteria.and(Dada.TIPUS.getCamp()).is(filtreValor.getTipus());
		}
		if (filtreValor.getValor() != null && !filtreValor.getValor().isEmpty()) {
			var criterias = crearCriteriaPerFiltre(filtreValor);
			if (criterias == null || criterias.isEmpty()) {
				return null;
			}
			var orValors = new Criteria();
			orValors.orOperator(criterias.toArray(new Criteria[criterias.size()]));
			filtreCriteria.and(Dada.VALOR.getCamp()).elemMatch(orValors);
		}
		var criteria = new Criteria();
		criteria.and(Capcalera.DADES.getCamp()).elemMatch(filtreCriteria);
		return criteria;
	}

	private List<Criteria> crearCriteriaPerFiltre(FiltreValor filtreValor) {

		var valors = filtreValor.getValor();
		List<Criteria> criterias = new ArrayList<>();
		if (valors == null || valors.isEmpty()) {
			return criterias;
		}
		//TODO MS: !!! ATENCIO PER LES DATE S'HA DE VEURE EL CAS DEL DIA ACTUAL !!!!
		//Si el filtre es un rang i es del tipus interval
		if (filtreValor.getTipusFiltre() != null
			&& filtreValor.getTipusFiltre().equals(TipusFiltre.RANG)
			&& isCampInterval(filtreValor)
			&& valors.size() == 2 ) {

			var min = valors.get(0).getValor();
			var max = valors.get(1).getValor();
			if (min == null && max == null) {
				return criterias; //Si no hi ha valors no cal fer res
			}
			var criteria = new Criteria();
			if (filtreValor.getTipus().equals(Tipus.DATE)) {
				max += " 24:59:59";
			}
			prepararCriteriaInterval(min, max, criteria, Dada.VALOR.getCamp());
			criterias.add(criteria);
			return criterias;
		}
		//Si no es interval afegeix regex per cada element (normalment sera un i prou)
		for (var valor : valors) { //TODO MS: Comprovar quins casos es necessita realment el for, si es necessita.
			var criteria = new Criteria();
			criteria.and(Dada.VALOR.getCamp()).regex(valor.getValor() != null ? valor.getValor() : "");
			criterias.add(criteria);
		}
		return criterias;
	}

	private boolean isCampInterval(FiltreValor filtreValor) {

		return filtreValor.getTipus().equals(Tipus.DATE) || filtreValor.getTipus().equals(Tipus.PRICE)
				|| filtreValor.getTipus().equals(Tipus.INTEGER) || filtreValor.getTipus().equals(Tipus.FLOAT);
	}

	private void prepararCriteriaInterval(String min, String max, Criteria criteria, String nomCamp) {

		if (min != null && max != null) {
			criteria.and(nomCamp).gte(min).lte(max);
		}

		if (min != null && max == null) {
			criteria.and(nomCamp).gte(min);
		}

		if (min == null && max != null) {
			criteria.and(nomCamp).lte(max);
		}
	}


	/**
	 * Emplena el Criteria per filtrar per dates segons rang 
	 * @param rangInicial Data inicial
	 * @param rangFinal Data final
	 * @param criteria Criteria a emplenar
	 * @param nomCamp Nom del camp del document que conté la data
	 */
	private void prepararCriteriaDataCapcalera(Date rangInicial, Date rangFinal, Criteria criteria, String nomCamp) {

		if (criteria == null || nomCamp == null || nomCamp.isEmpty()) {
			return;
		}
		
		if (rangFinal != null) {
			var now = Calendar.getInstance();
			now.setTime(rangFinal);
			now.set(Calendar.HOUR_OF_DAY, 23);
			now.set(Calendar.MINUTE, 59);
			now.set(Calendar.SECOND, 59);
			rangFinal = now.getTime();
		}

		if (rangInicial != null && rangFinal != null) {
			criteria.and(nomCamp).gte(rangInicial).lte(rangFinal);
		}

		if (rangInicial != null && rangFinal == null) {
			criteria.and(nomCamp).gte(rangInicial);
		}

		if (rangInicial == null && rangFinal != null) {
			criteria.and(nomCamp).lte(rangFinal);
		}
	}
}
