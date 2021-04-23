package es.caib.helium.dada.repository;

import java.util.ArrayList;
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
import es.caib.helium.dada.model.Columna;
import es.caib.helium.dada.model.Consulta;
import es.caib.helium.dada.model.FiltreCapcalera;
import es.caib.helium.dada.model.FiltreValor;
import es.caib.helium.enums.Capcalera;
import es.caib.helium.enums.Collections;
import es.caib.helium.enums.Dada;
import es.caib.helium.enums.DireccioOrdre;

public class ExpedientRepositoryCustomImpl implements ExpedientRepositoryCustom {

	private final MongoTemplate mongoTemplate;

	@Autowired
	public ExpedientRepositoryCustomImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<Expedient> findByFiltres(Consulta consulta) {
		return mongoTemplate
				.aggregate(Aggregation.newAggregation(prepararFiltres(consulta)), Expedient.class, Expedient.class)
				.getMappedResults();
	}

	@Override
	@Transactional
	public void esborrarExpedientCascade(Long expedientId) {
		List<Long> expedients = new ArrayList<>();
		expedients.add(expedientId);
		esborrarExpedientsCascade(expedients);
	}

	@Override
	@Transactional
	public void esborrarExpedientsCascade(List<Long> expedients) {
		var query = new Query();
		var criteria = new Criteria();
		criteria.and(Capcalera.EXPEDIENT_ID.getCamp()).in(expedients);
		query.addCriteria(criteria);
		mongoTemplate.remove(query, Expedient.class, Collections.EXPEDIENT.getNom());
		mongoTemplate.remove(query, Dada.class, Collections.DADA.getNom());
	}

	private List<AggregationOperation> prepararFiltres(Consulta consulta) {

		// Pipeline per l'agregació (db.expedient.aggregate([])
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

	private MatchOperation crearFiltreEntornIdTipusId(Integer entornId, Integer expedientTipusId) {

		var criteria = new Criteria();
		criteria.and(Capcalera.ENTORN_ID.getCamp()).is(entornId).and(Capcalera.TIPUS_ID.getCamp()).is(expedientTipusId);
		return Aggregation.match(criteria);
	}

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

	/*
	 * {$match: { "dades": {$elemMatch: {codi: {$in: ["var_titol", "cody"]}}}}}
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

	/*
	 * db.expedient.aggregate([ {$lookup: {from: "dada", localField: "expedientId",
	 * foreignField: "expedientId", as: "dades"}}, {$match: { "dades": {$elemMatch:
	 * {codi: "var_titol"}}}} ])
	 */
	private Criteria crearFiltreValor(FiltreValor filtreValor) {

		var filtreCriteria = new Criteria();
		if (filtreValor.getCodi() != null && !filtreValor.getCodi().isEmpty()) {
			filtreCriteria.and(Dada.CODI.getCamp()).is(filtreValor.getCodi());
		}
		if (filtreValor.getTipus() != null && !filtreValor.getTipus().isEmpty()) {
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

	private void prepararData(Date rangInicial, Date rangFinal, Criteria criteria, String nomCamp) {

		if (criteria == null || nomCamp == null || nomCamp.isEmpty()) {
			return;
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
