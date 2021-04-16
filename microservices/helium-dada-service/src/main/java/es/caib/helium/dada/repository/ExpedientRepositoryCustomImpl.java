package es.caib.helium.dada.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Query;

import es.caib.helium.dada.domain.Expedient;
import es.caib.helium.dada.model.Filtre;
import es.caib.helium.dada.model.FiltreCapcalera;
import es.caib.helium.dada.model.FiltreValor;
import es.caib.helium.dada.model.Ordre;
import es.caib.helium.enums.Capcalera;
import es.caib.helium.enums.Coleccions;
import es.caib.helium.enums.Dada;
import es.caib.helium.enums.DireccioOrdre;

public class ExpedientRepositoryCustomImpl implements ExpedientRepositoryCustom {

	private final MongoTemplate mongoTemplate;

	@Autowired
	public ExpedientRepositoryCustomImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<Expedient> findByFiltres(Map<String, Filtre> filtres, Integer entornId, Integer expedientTipusId,
			List<Ordre> ordre, Integer page, Integer size) {
		return mongoTemplate.aggregate(
				Aggregation.newAggregation(prepararFiltres(filtres, entornId, expedientTipusId, ordre, page, size)),
				Expedient.class, Expedient.class).getMappedResults();
	}

	private List<AggregationOperation> prepararFiltres(Map<String, Filtre> filtres, Integer entornId,
			Integer expedientTipusId, List<Ordre> ordre, Integer page, Integer size) {
//
//		if (filtres.isEmpty()) {
//			return new ArrayList<>();
//		}

		// Pipeline per l'agregació (db.expedient.aggregate([])
		List<AggregationOperation> operations = new ArrayList<>();
		operations.add(crearFiltreEntornIdTipusId(entornId, expedientTipusId));

		var valors = filtres.values();
		var filtreCapcaleraCreat = false;
		List<FiltreValor> filtresValor = new ArrayList<>();
		for (var filtre : valors) {
			// Si hi han 2 filtres capçalera, només es queda el primer que li passa
			if (filtre instanceof FiltreCapcalera && !filtreCapcaleraCreat) {
				operations.add(crearFiltreCapcalera((FiltreCapcalera) filtre));
				filtreCapcaleraCreat = true;
			} else if (filtre instanceof FiltreValor) {
				filtresValor.add((FiltreValor) filtre); // Es guarden per pasar a la pipeline després del $lookup
			}
		}

		operations.add(Aggregation.lookup(Coleccions.DADA.getNom(), Capcalera.EXPEDIENT_ID.getCamp(), Capcalera.EXPEDIENT_ID.getCamp(),
				Capcalera.DADES.getCamp()));

		if (filtresValor.size() > 0) {
			operations.add(crearFiltresValor(filtresValor));
		}

		if (ordre.size() > 0) {
			operations.add(crearSortOperation(ordre));
		}

		if (page != null && size != null) {
			operations.add(Aggregation.skip(page > 0 ? ((page - 1) * size) : 0l));
			operations.add(Aggregation.limit(size));
		}
		return operations;
	}

	private SortOperation crearSortOperation(List<Ordre> ordres) {

		ordres.sort((foo, bar) -> Integer.compare(foo.getOrdre(), bar.getOrdre()));
		List<Order> orders = new ArrayList<>();
		for (var ordre : ordres) {
			if (ordre.getColumna() == null || ordre.getColumna().isEmpty()) { // TODO comprovar que no vingui la id
				continue;
			}
			if (ordre.getTipus().equals(Coleccions.EXPEDIENT)) {
				orders.add(new Order(ordre.getDireccio().equals(DireccioOrdre.ASC) ? Direction.ASC : Direction.DESC,
						ordre.getColumna()));
			}
			
			// TODO falta la part que no son camps de capçalera
//			db.expedient.aggregate([
//			                        {$sort: {"expedientId": -1, "_id": 1}},
//			                        {$match: {entornId: 1}},
//			                        {$match: {tipusId: 3}}
//			                    ])
		}
		orders.add(new Order(Direction.ASC, "_id"));
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
