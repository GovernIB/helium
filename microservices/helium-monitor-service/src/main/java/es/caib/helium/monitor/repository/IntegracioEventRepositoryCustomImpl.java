package es.caib.helium.monitor.repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.netflix.servo.util.Strings;

import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.events.IntegracioEvent;
import es.caib.helium.monitor.domini.Consulta;
import es.caib.helium.monitor.enums.IntegracioEventBdd;

public class IntegracioEventRepositoryCustomImpl implements IntegracioEventRepositoryCustom {

	private final MongoTemplate mongoTemplate;
	
	@Autowired
	public IntegracioEventRepositoryCustomImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<IntegracioEvent> findByFiltres(Consulta consulta) throws Exception {
		
		if (consulta == null) {
			return new ArrayList<IntegracioEvent>();
		}
		return mongoTemplate
				.aggregate(Aggregation.newAggregation(prepararFiltres(consulta)), IntegracioEvent.class, IntegracioEvent.class)
				.getMappedResults();
	}
	
	private List<AggregationOperation> prepararFiltres(Consulta consulta) throws Exception {
		
		// Pipeline per l'agregació (db.integracioEvent.aggregate([])
		// L'ordre de les operacions afecta l'eficiencia de la cerca. Els més restrictius primer.
		List<AggregationOperation> operations = new ArrayList<>();
		
		if (consulta.getEntornId() == null) {
			throw new Exception("No es pot fer la consulta sense entorn");
		}
		operations.add(filtreCampByValorLong(IntegracioEventBdd.ENTORN_ID.getCamp(), consulta.getEntornId()));
		
		if (consulta.getCodi() != null) {
			operations.add(filtreCampByValor(IntegracioEventBdd.CODI.getCamp(), consulta.getCodi().name()));
		}
		
		if (consulta.getTipus() != null) {
			operations.add(filtreCampByValor(IntegracioEventBdd.TIPUS.getCamp(), consulta.getTipus().name()));
		}
		
		if (consulta.getError() != null) {
			operations.add(filtreEstat(consulta.getError()));
		}

		if (!Strings.isNullOrEmpty(consulta.getDescripcio())) {
			operations.add(filtreDescripcio(consulta.getDescripcio()));
		}
		
		if (consulta.getDataInicial() != null || consulta.getDataFinal() != null) {
			operations.add(filtreData(consulta.getDataInicial(), consulta.getDataFinal(), IntegracioEventBdd.DATA.getCamp()));
		}
		
		// $skip $limit
		if (consulta.getPage() != null && consulta.getSize() != null) {
			operations.add(
					Aggregation.skip(consulta.getPage() > 0 ? ((consulta.getPage() - 1) * consulta.getSize()) : 0l));
			operations.add(Aggregation.limit(consulta.getSize()));
		}
		
		return operations;
	}
	
	private MatchOperation filtreCampByValor(String camp, String valor) {
		
		var criteria = new Criteria(); 
		criteria.and(camp).is(valor);
		return Aggregation.match(criteria);
	}

	private MatchOperation filtreCampByValorLong(String camp, Long valor) {
		
		var criteria = new Criteria(); 
		criteria.and(camp).is(valor);
		return Aggregation.match(criteria);
	}
	
	private MatchOperation filtreEstat(boolean error) {
		
		var criteria = new Criteria();
		criteria.and(IntegracioEventBdd.ESTAT.getCamp()).is(error ? EstatAccio.ERROR : EstatAccio.OK);
		return Aggregation.match(criteria);
	}
	
	private MatchOperation filtreDescripcio(String desc) {

		return Aggregation.match(Criteria.where(IntegracioEventBdd.DESCRIPCIO.getCamp()).regex(".*" + desc + ".*", "i"));
	}
	
	/**
	 * @param rangInicial Data inicial
	 * @param rangFinal Data final
	 * @param nomCamp Nom del camp del document que conté la data
	 */
	private MatchOperation filtreData(Date rangInicial, Date rangFinal, String nomCamp) {

		var criteria = new Criteria();
		
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
		
		return Aggregation.match(criteria);
	}
}
