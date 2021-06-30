package es.caib.helium.dada.repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.In;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import es.caib.helium.dada.domain.Expedient;
import es.caib.helium.dada.exception.DadaException;
import es.caib.helium.dada.model.Columna;
import es.caib.helium.dada.model.Consulta;
import es.caib.helium.dada.model.FiltreCapcalera;
import es.caib.helium.dada.model.FiltreValor;
import es.caib.helium.enums.Capcalera;
import es.caib.helium.enums.Collections;
import es.caib.helium.enums.Dada;
import es.caib.helium.enums.DireccioOrdre;

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
			return new ArrayList<Expedient>();
		}
		return mongoTemplate
				.aggregate(Aggregation.newAggregation(prepararFiltres(consulta)), Expedient.class, Expedient.class)
				.getMappedResults();
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
		var nEsborrats = mongoTemplate.remove(query, Expedient.class, Collections.EXPEDIENT.getNom());
		if (nEsborrats == null || nEsborrats.getDeletedCount() != expedients.size()) {
			throw new DadaException("No s'han pogut esborrar tots els expedients " + expedients.toString());
		}
		mongoTemplate.remove(query, Dada.class, Collections.DADA.getNom());
		return nEsborrats.getDeletedCount();
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
		var valors = consulta.getFiltreValors().values();
		var filtreCapcaleraCreat = false;
		List<FiltreValor> filtresValor = new ArrayList<>();
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

		// $lookup
		operations.add(Aggregation.lookup(Collections.DADA.getNom(), Capcalera.EXPEDIENT_ID.getCamp(),
				Capcalera.EXPEDIENT_ID.getCamp(), Capcalera.DADES.getCamp()));

		// $match filtresValor
		if (filtresValor.size() > 0) {
			operations.add(crearFiltresValor(filtresValor));
		}

		// $project $sort
		if (consulta.getColumnes() != null && consulta.getColumnes().size() > 0) {
			//TODO FALTA AFEGIR LES COLUMNES PER DEFECTE 
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

		List<String> cols = new ArrayList<>();
		List<Columna> ordres = new ArrayList<>();
		List<String> codisDadaValor = new ArrayList<>();
		cols.add(Capcalera.ID.getCamp());
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

			// La columna és tipus DadaValor es busca pel seu codi
			codisDadaValor.add(columna.getNom());
			if (columna.getOrdre() != null) {
				columna.setNom(Dada.CODI.getCamp());
				ordres.add(columna);
			}
		}
		ProjectionOperation projection = Aggregation.project(Fields.fields(cols.toArray(new String[cols.size()])))
				.and(Filter.filter("dades").as("dades")
						.by(In.arrayOf(codisDadaValor).containsValue("$$dades." + Dada.CODI.getCamp())))
				.as("dades");
		operations.add(projection);
		operations.add(crearSortOperation(ordres));
	}

	/**
	 * Crea la $sort per la llista de columnes demanades
	 * Actualment només ordena per els camps de capçalera. Falta completar el TO DO si s'escau
	 * @param ordres llista de columnes i l'ordre que han de tenir 
	 * @return Retorna la AggregationOperation Sort amb els ordres desitjats.
	 */
	private SortOperation crearSortOperation(List<Columna> ordres) {

		ordres.sort((foo, bar) -> Integer.compare(foo.getOrdre().getOrdre(), bar.getOrdre().getOrdre()));
		List<Order> orders = new ArrayList<>();
		for (var ordre : ordres) {
			if (ordre.getNom() == null || ordre.getNom().isEmpty() || ordre.getNom().equals("_id")
					|| ordre.getNom().equals("id")) {
				continue;
			}
			if (ordre.getOrdre().getTipus().equals(Collections.EXPEDIENT)) {
				orders.add(new Order(
						ordre.getOrdre().getDireccio().equals(DireccioOrdre.ASC) ? Direction.ASC : Direction.DESC,
						ordre.getNom()));
			} else {
				System.out.println("ORDE TIPUS DADA ---> " + ordre.getNom());
				// TODO falta la part que no son camps de capçalera
//			db.expedient.aggregate([
//			                        {$sort: {"expedientId": -1, "_id": 1}},
//			                        {$match: {entornId: 1}},
//			                        {$match: {tipusId: 3}}
//			                    ])
			}
		}
		orders.add(new Order(Direction.ASC, Capcalera.ID.getCamp()));
		return Aggregation.sort(Sort.by(orders));
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
			criteria.and(Capcalera.NUMERO.getCamp()).is(filtreCapcalera.getNumero());
		}
		if (filtreCapcalera.getTitol() != null) {
			criteria.and(Capcalera.TITOL.getCamp()).is(filtreCapcalera.getTitol());
		}
		if (filtreCapcalera.getProcesPrincipalId() != null) {
			criteria.and(Capcalera.PROCES_PRINCIPAL_ID.getCamp()).is(filtreCapcalera.getProcesPrincipalId());
		}
		if (filtreCapcalera.getEstatId() != null) {
			criteria.and(Capcalera.ESTAT_ID.getCamp()).is(filtreCapcalera.getEstatId());
		}

		prepararData(filtreCapcalera.getDataIniciInicial(), filtreCapcalera.getDataIniciFinal(), criteria,
				Capcalera.DATA_INICI.getCamp());
		prepararData(filtreCapcalera.getDataFiInicial(), filtreCapcalera.getDataFiFinal(), criteria,
				Capcalera.DATA_FI.getCamp());

		return Aggregation.match(criteria);
	}

	/**
	 * Crea la part del $match segons els filtresValor que servirà per filtrà els resultats del $lookup previ.
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
			filtres.add(crearFiltreValor(filtre));
		}
		var criteria = new Criteria();
		criteria.orOperator(filtres.toArray(new Criteria[filtres.size()]));
		return Aggregation.match(criteria);
	}

	
	/**
	 * Prepara el criteri a filtrar contingut en filtreValor.
	 * @param filtreValor conté els valors pels quals es filtrarà. No pot ser null
	 * @return Retorna el Criteria amb el que es farà el filtre.
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
			var criteria = new Criteria();
			var valors = filtreValor.getValor();
			List<Criteria> criterias = new ArrayList<>();
			for (var valor : valors) {
				criteria.and(Dada.VALOR.getCamp()).is(valor.getValor());
				criterias.add(criteria);
			}
			filtreCriteria.and(Dada.VALOR.getCamp()).orOperator(criterias.toArray(new Criteria[criterias.size()]));
		}
		var criteria = new Criteria();
		criteria.and(Capcalera.DADES.getCamp()).elemMatch(filtreCriteria);
		return criteria;
	}

	/**
	 * Emplena el Criteria per filtrar per dates segons rang 
	 * @param rangInicial Data inicial
	 * @param rangFinal Data final
	 * @param criteria Criteria a emplenar
	 * @param nomCamp Nom del camp del document que conté la data
	 */
	private void prepararData(Date rangInicial, Date rangFinal, Criteria criteria, String nomCamp) {

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
