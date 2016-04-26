/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.core.util.TimestampCodec;

/**
 * Helper per a gestionar la informació dels expedients emprant Lucene.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MongoDBHelper {

	private static final String MONGODB_USER = "limit";
	private static final String MONGODB_PASSWORD = "tecnologies";
	private static final String MONGODB_HOST = "localhost";
	private static final int MONGODB_PORT = 27017;
	private static final String MONGODB_AUTH_DATABASE = "admin";
	private static final String MONGODB_DATABASE = "helium";
	
	private static final String MONGODB_DOMINI_CODI = "codi";
	private static final String MONGODB_DOMINI_VALOR = "valor";
	private static final String MONGODB_TERMINI_ANYS = "anys";
	private static final String MONGODB_TERMINI_DIES = "dies";
	private static final String MONGODB_TERMINI_MESOS = "mesos";
	
	private static final int NUMDIGITS_PART_SENCERA = 15;
	private static final int NUMDIGITS_PART_DECIMAL = 6;
//	private static final String PATRO_DATES_INDEX = "yyyyMMddHHmmSS";
	
	@Resource
	protected MesuresTemporalsHelper mesuresTemporalsHelper;
	
	MongoClientURI mongoClientUri;
	MongoClient mongoClient;
	MongoDatabase mongoDb;
	
	DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("ca", "ES"));
	
	private void initializeMongoConnection() {
		if (mongoClient == null) {

			CodecRegistry registry = CodecRegistries.fromRegistries(
					CodecRegistries.fromCodecs(new TimestampCodec()),
					MongoClient.getDefaultCodecRegistry());
			
//			MongoClientURI mongoClientUri = new MongoClientURI("mongodb://" + MONGODB_USER + ":" + MONGODB_PASSWORD + "@" + MONGODB_HOST + ":" + MONGODB_PORT + "/?authSource=" + MONGODB_AUTH_DATABASE);
//			mongoClient = new MongoClient(mongoClientUri);
			
			MongoClientOptions mongoClientOptions = new MongoClientOptions.Builder()
					.codecRegistry(registry)
//			    	.connectionsPerHost(100)
//			    	.threadsAllowedToBlockForConnectionMultiplier(5)
//			    	.maxWaitTime(1000 * 60 * 2)
//			    	.connectTimeout(1000 * 10)
//			    	.socketTimeout(0)
//			    	.socketKeepAlive(false)
//			    	.readPreference(ReadPreference.primary())
//			    	.writeConcern(WriteConcern.ACKNOWLEDGED)
			    	.build();

			List<ServerAddress> mongoAddresses = new ArrayList<ServerAddress>();
		    mongoAddresses.add(new ServerAddress(MONGODB_HOST, MONGODB_PORT));
	
			List<MongoCredential> mongoCredentials = new ArrayList<MongoCredential>();
			mongoCredentials.add(MongoCredential.createCredential(MONGODB_USER, MONGODB_AUTH_DATABASE, MONGODB_PASSWORD.toCharArray()));
	
			mongoClient = new MongoClient(mongoAddresses, mongoCredentials, mongoClientOptions);
			mongoDb = mongoClient.getDatabase(MONGODB_DATABASE);
		}
	}
	
	public synchronized void deleteExpedient(Expedient expedient) {
		try {
			initializeMongoConnection();
			mongoDb.getCollection("expedients").deleteOne(new Document("_id", expedient.getId()));
		} catch (Exception ex) {
			logger.error("No s'ha pogut eliminar l'expedient al MongoDB", ex);
		}
	}
	
	public synchronized void createExpedient(
			Expedient expedient,
			Map<String, DefinicioProces> definicionsProces,
			Map<String, Set<Camp>> camps,
			Map<String,
			Map<String, Object>> valors,
			Map<String,
			Map<String, String>> textDominis,
			boolean finalitzat,
			boolean comprovarIniciant) {
		
		Document document = new Document()
					.append("_id", expedient.getId())
					.append(ExpedientCamps.EXPEDIENT_CAMP_ENTORN, expedient.getEntorn().getCodi())
					.append(ExpedientCamps.EXPEDIENT_CAMP_NUMERO, expedient.getNumero())
					.append(ExpedientCamps.EXPEDIENT_CAMP_TITOL, expedient.getTitol())
					.append(ExpedientCamps.EXPEDIENT_CAMP_COMENTARI, expedient.getComentari())
					.append(ExpedientCamps.EXPEDIENT_CAMP_INICIADOR, expedient.getIniciadorCodi())
					.append(ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE, expedient.getResponsableCodi())
					.append(ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI, new Date(expedient.getDataInici().getTime()))
					.append(ExpedientCamps.EXPEDIENT_CAMP_TIPUS, expedient.getTipus().getCodi())
					.append(ExpedientCamps.EXPEDIENT_CAMP_ESTAT, finalitzat ? -1 : expedient.getEstat() != null ? expedient.getEstat().getCodi() : 0);
					
		if (definicionsProces != null) {
			for (String clau : definicionsProces.keySet()) {
				DefinicioProces definicioProces = definicionsProces.get(clau);
				Map<String, Object> valorsProces = valors.get(clau);
				if (valorsProces != null) {
					for (Camp camp : camps.get(clau)) {
						try {
							String clauIndex = definicioProces.getJbpmKey() + ExpedientCamps.EXPEDIENT_PREFIX_SEPARADOR + camp.getCodi();
							Object valorIndex = getDocumentCamp(camp, valorsProces.get(camp.getCodi()), textDominis.get(clau), new HashSet<String>());
							document.append(clauIndex, valorIndex);
						} catch (Exception ex) {
							StringBuilder sb = new StringBuilder();
							getClassAsString(sb, valorsProces.get(camp.getCodi()));
							logger.error("No s'ha pogut indexar el camp (definicioProces=" + definicioProces.getJbpmKey() + "(v." + definicioProces.getVersio() + ")" + ", camp=" + camp.getCodi() + ", tipus=" + camp.getTipus() + ", multiple=" + camp.isMultiple() + ") amb un valor (tipus=" + sb.toString() + ")", ex);
						}
					}
				}
			}
		}
//		mongoDb.getCollection("expedients").insertOne(document);
		
		MongoCollection<Document> expedients = mongoDb.getCollection("expedients");
		try {
			initializeMongoConnection();
			expedients.insertOne(document);
//			FindIterable<Document> iterable = expedients.find();
//			iterable.forEach(new Block<Document>() {
//				@Override
//			    public void apply(final Document document) {
//			        System.out.println(">>> Document:  \n" +
//			        		"-------------------------------------------------------------\n" +
//			        		document +
//			        		"\n-------------------------------------------------------------");
//			    }
//			});
		} catch (Exception ex) {
			logger.error("No s'ha pogut desar l'expedient al MongoDB", ex);
		}
		
		
	}
	
	private Object getDocumentCamp(Camp camp, Object valor, Map<String, String> textDominis, Set<String> campsActualitzats) {
		if (valor != null) {
			if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
				return getDocumentCampRegistreValor(camp, valor, textDominis);
			} else {
				return getDocumentCampValor(camp, valor, textDominis);
			}
		}
		return null;
	}
			
	private Object getDocumentCampValor(Camp camp, Object valor, Map<String, String> textDominis) {
		if (camp.isMultiple()) {
			Object[] valors = (Object[]) valor;
			List<Object> llistaValors = new ArrayList<Object>();
			for (Object o : valors) {
				llistaValors.add(getValorDomini(camp, o, textDominis));
			}
			return llistaValors;
		} else {
			return getValorDomini(camp, valor, textDominis);
		}
	}
	
	private Object getDocumentCampRegistreValor(Camp camp, Object valor, Map<String, String> textDominis) {
		
		if (camp.isMultiple()) {
			Object[] valors = (Object[]) valor;
			List<Document> llistaValors = new ArrayList<Document>();
			for (Object o : valors) {
				llistaValors.add(getDocumentRegistre(camp, o, textDominis));
			}
			return llistaValors;
		} else {
			return getDocumentRegistre(camp, valor, textDominis);
		}
	}
	
	private Object getValorDomini(Camp camp, Object valor, Map<String, String> textDominis) {
		if (valor != null) {
			Object valorIndex = valorIndexPerCamp(camp, valor);
			String textDomini = textDominis.get(camp.getCodi() + "@" + valorIndex);
			if (textDomini != null && (camp.getTipus().equals(TipusCamp.SELECCIO) || camp.getTipus().equals(TipusCamp.SUGGEST))) {
				return new Document()
						.append(MONGODB_DOMINI_CODI, valorIndex)
						.append(MONGODB_DOMINI_VALOR, textDomini);
			} else {
				return valorIndex;
			}
		}
		return null;
	}
	
	private Document getDocumentRegistre(Camp camp, Object valor, Map<String, String> textDominis) {
		Document registre = new Document();
		Object[] valorsMembres = (Object[]) valor;
		int index = 0;
		for (CampRegistre campRegistre : camp.getRegistreMembres()) {
			Camp membre = campRegistre.getMembre();
			if (index < valorsMembres.length)
				registre.append(membre.getCodi(), getValorDomini(membre, valorsMembres[index++], textDominis)); //getDocumentCampValor(clauMembre, membre, valorsMembres[index++], textDominis));
		}
		return registre;
	}
	
	protected Object valorIndexPerCamp(Camp camp, Object valor) {
		switch (camp.getTipus()) {
		case PRICE:
			return numberPerIndexar((BigDecimal) valor);
//		case DATE:
//			Long time = ((Date) valor).getTime();
//			return new java.sql.Date(time);
//			return dataPerIndexar((Date)valor);
		case TERMINI:
			Termini term = (Termini) valor;
			return new Document()
					.append(MONGODB_TERMINI_ANYS, term.getAnys())
					.append(MONGODB_TERMINI_MESOS, term.getMesos())
					.append(MONGODB_TERMINI_DIES, term.getDies());
		default:
			return valor;
			
		}
	}
	
	private String numberPerIndexar(Number number) {
		String numberStr = number.toString();
		boolean negative = numberStr.startsWith("-");
		if (negative)
			numberStr = numberStr.substring(1);
		String[] parts = numberStr.split("\\.");
		StringBuffer partSencera = new StringBuffer(parts[0]);
		while (partSencera.length() < NUMDIGITS_PART_SENCERA)
			partSencera.insert(0, "0");
		StringBuffer partDecimal = new StringBuffer();
		if (parts.length > 1)
			partDecimal.append(parts[1]);
		while (partDecimal.length() < NUMDIGITS_PART_DECIMAL)
			partDecimal.append("0");
		if (partDecimal.length() > NUMDIGITS_PART_DECIMAL)
			return ((negative) ? "-" : "") + partSencera.toString() + "." + partDecimal.substring(0, NUMDIGITS_PART_DECIMAL).toString();
		else
			return ((negative) ? "-" : "") + partSencera.toString() + "." + partDecimal.toString();
	}
	
//	private String dataPerIndexar(Date data) {
//		DateFormat sdf = new SimpleDateFormat(PATRO_DATES_INDEX);
//		return sdf.format(data);
//	}
	
	private void getClassAsString(StringBuilder sb, Object o) {
		if (o != null) {
			if (o.getClass().isArray()) {
				sb.append("[");
				int length = Array.getLength(o);
				for (int i = 0; i < length; i++) {
					getClassAsString(sb, Array.get(o, i));
					if (i < length - 1)
						sb.append(", ");
				}
				sb.append("]");
			} else {
				sb.append(o.getClass().getName());
			}
		}
	}
	

	protected static final Log logger = LogFactory.getLog(MongoDBHelper.class);
}
