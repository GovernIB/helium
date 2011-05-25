/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springmodules.lucene.index.core.DocumentModifier;
import org.springmodules.lucene.index.support.LuceneIndexSupport;
import org.springmodules.lucene.search.core.HitExtractor;
import org.springmodules.lucene.search.core.LuceneSearchTemplate;

/**
 * Dao per a indexar i consultar expedients emprant lucene
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class LuceneDao extends LuceneIndexSupport {

	private static final String EXPEDIENT_CAMP_ID = "expedient.id";
	private static final String EXPEDIENT_CAMP_NUMERO = "expedient.numero";
	private static final String EXPEDIENT_CAMP_TITOL = "expedient.titol";
	private static final String EXPEDIENT_CAMP_COMENTARI = "expedient.comentari";
	private static final String EXPEDIENT_CAMP_INICIADOR = "expedient.iniciador";
	private static final String EXPEDIENT_CAMP_RESPONSABLE = "expedient.responsable";
	private static final String EXPEDIENT_CAMP_DATA_INICI = "expedient.dataInici";
	private static final String EXPEDIENT_CAMP_TIPUS = "expedient.tipus";
	private static final String EXPEDIENT_CAMP_ESTAT = "expedient.estat";

	private static final String ESTAT_EXPEDIENT_INICIAT = "H3l1um#estat.iniciat";
	private static final String ESTAT_EXPEDIENT_FINALITZAT = "H3l1um#estat.finalitzat";

	private static final int NUMDIGITS_PART_SENCERA = 15;
	private static final int NUMDIGITS_PART_DECIMAL = 6;

	private static final String VALOR_CAMP_BUIT = "H3l1um#camp.buit";

	private LuceneSearchTemplate searchTemplate;



	public synchronized void createExpedient(
			Expedient expedient,
			Map<String, DefinicioProces> definicionsProces,
			Map<String, Set<Camp>> camps,
			Map<String, Map<String, Object>> valors) {
		checkIndexOk();
		Document document = createDocumentFromExpedient(
				expedient,
				definicionsProces,
				camps,
				valors);
		getLuceneIndexTemplate().addDocument(document);
	}
	@SuppressWarnings("unchecked")
	public synchronized boolean updateExpedient(
			final Expedient expedient,
			final Map<String, DefinicioProces> definicionsProces,
			final Map<String, Set<Camp>> camps,
			final Map<String, Map<String, Object>> valors) {
		checkIndexOk();
		try {
			List<Long> resposta = searchTemplate.search(
					new TermQuery(termIdFromExpedient(expedient)),
					new HitExtractor() {
					    public Object mapHit(int id, Document document, float score) {
				    		return new Long(document.get(EXPEDIENT_CAMP_ID));
					    }
					});
			if (resposta.size() > 0) {
				getLuceneIndexTemplate().updateDocument(
						termIdFromExpedient(expedient),
						new DocumentModifier() {
							public Document updateDocument(Document document) {
								return createDocumentFromExpedient(
										expedient,
										definicionsProces,
										camps,
										valors);
							}
						});
			} else {
				createExpedient(
						expedient,
						definicionsProces,
						camps,
						valors);
			}
			return true;
		} catch (Exception ex) {
			logger.error("Error actualitzant l'índex per l'expedient " + expedient.getId(), ex);
			return false;
		}
	}
	public synchronized void deleteDocument(Expedient expedient) {
		checkIndexOk();
		getLuceneIndexTemplate().deleteDocuments(
				termIdFromExpedient(expedient));
	}
	@SuppressWarnings("unchecked")
	public synchronized void deleteAll() {
		checkIndexOk();
		List<Integer> documentsTots = searchTemplate.search(
				new MatchAllDocsQuery(),
				new HitExtractor() {
				    public Object mapHit(int id, Document document, float score) {
				    	return new Integer(id);
				    }
				});
		for (Integer id: documentsTots)
			getLuceneIndexTemplate().deleteDocument(id.intValue());
		getLuceneIndexTemplate().optimize();
	}

	@SuppressWarnings("unchecked")
	public List<Long> find(
			Map<String, Object> filtre,
			List<Camp> camps) {
		BooleanQuery bquery = new BooleanQuery();
		for (String clau: filtre.keySet()) {
			Query query = queryFromCampFiltre(
					clau,
					filtre.get(clau),
					camps);
			if (query != null)
				bquery.add(new BooleanClause(query, BooleanClause.Occur.MUST));
		}
		Query query = (bquery.getClauses().length > 0) ? bquery : new MatchAllDocsQuery();
		List<Long> resposta = searchTemplate.search(
				query,
				new HitExtractor() {
				    public Object mapHit(int id, Document document, float score) {
			    		return new Long(document.get(EXPEDIENT_CAMP_ID));
				    }
				});
		return resposta;
	}



	@Autowired
	public void setSearchTemplate(LuceneSearchTemplate searchTemplate) {
		this.searchTemplate = searchTemplate;
	}



	private Document createDocumentFromExpedient(
			Expedient expedient,
			Map<String, DefinicioProces> definicionsProces,
			Map<String, Set<Camp>> camps,
			Map<String, Map<String, Object>> valors) {
		Document doc = new Document();
		doc.add(new Field(
				EXPEDIENT_CAMP_ID,
	    		expedient.getId().toString(),
				Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		doc.add(new Field(
				EXPEDIENT_CAMP_NUMERO,
	    		(expedient.getNumero() != null) ? expedient.getNumero() : VALOR_CAMP_BUIT,
				Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		doc.add(new Field(
				EXPEDIENT_CAMP_TITOL,
				(expedient.getTitol() != null) ? normalitzarILlevarAccents(expedient.getTitol()) : VALOR_CAMP_BUIT,
				Field.Store.YES,
				(expedient.getTitol() != null) ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED));
		doc.add(new Field(
				EXPEDIENT_CAMP_COMENTARI,
				(expedient.getComentari() != null) ? normalitzarILlevarAccents(expedient.getComentari()) : VALOR_CAMP_BUIT,
				Field.Store.YES,
				(expedient.getComentari() != null) ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED));
		doc.add(new Field(
				EXPEDIENT_CAMP_INICIADOR,
	    		expedient.getIniciadorCodi(),
				Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		doc.add(new Field(
				EXPEDIENT_CAMP_RESPONSABLE,
	    		expedient.getResponsableCodi(),
				Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		doc.add(new Field(
				EXPEDIENT_CAMP_DATA_INICI,
	    		dataPerIndexar(expedient.getDataInici()),
				Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		doc.add(new Field(
				EXPEDIENT_CAMP_TIPUS,
	    		expedient.getTipus().getCodi(),
				Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		if (expedient.getEstat() != null) {
			doc.add(new Field(
					EXPEDIENT_CAMP_ESTAT,
		    		expedient.getEstat().getCodi(),
					Field.Store.YES,
					Field.Index.NOT_ANALYZED));
		} else {
			if (expedient.getDataFi() == null)
				doc.add(new Field(
						EXPEDIENT_CAMP_ESTAT,
			    		ESTAT_EXPEDIENT_INICIAT,
						Field.Store.YES,
						Field.Index.NOT_ANALYZED));
			else
				doc.add(new Field(
						EXPEDIENT_CAMP_ESTAT,
			    		ESTAT_EXPEDIENT_FINALITZAT,
						Field.Store.YES,
						Field.Index.NOT_ANALYZED));
		}
		for (String clau: definicionsProces.keySet()) {
			DefinicioProces definicioProces = definicionsProces.get(clau);
			Map<String, Object> valorsProces = valors.get(clau);
			if (valorsProces != null) {
				for (Camp camp: camps.get(clau)) {
					addFieldToDocument(
							doc,
							definicioProces,
							camp,
							valorsProces.get(camp.getCodi()),
							true);
				}
			}
		}
		return doc;
	}

	private Query queryFromCampFiltre(
			String codiCamp,
			Object valorFiltre,
			List<Camp> camps) {
		if (valorFiltre != null) {
			if (EXPEDIENT_CAMP_ID.equals(codiCamp)) {
				return new TermQuery(new Term(
						codiCamp,
						(String)valorFiltre));
			} else if (EXPEDIENT_CAMP_NUMERO.equals(codiCamp)) {
				return queryPerStringAmbWildcards(
						codiCamp,
						(String)valorFiltre);
			} else if (EXPEDIENT_CAMP_TITOL.equals(codiCamp)) {
				return queryPerStringAmbWildcards(
						codiCamp,
						(String)valorFiltre);
			} else if (EXPEDIENT_CAMP_COMENTARI.equals(codiCamp)) {
				return queryPerStringAmbWildcards(
						codiCamp,
						(String)valorFiltre);
			} else if (EXPEDIENT_CAMP_INICIADOR.equals(codiCamp)) {
				return new TermQuery(new Term(
						codiCamp,
						(String)valorFiltre));
			} else if (EXPEDIENT_CAMP_RESPONSABLE.equals(codiCamp)) {
				return new TermQuery(new Term(
						codiCamp,
						(String)valorFiltre));
			} else if (EXPEDIENT_CAMP_DATA_INICI.equals(codiCamp)) {
				return new TermRangeQuery(
						codiCamp,
						dataPerIndexar(((Date[])valorFiltre)[0]),
						dataPerIndexar(((Date[])valorFiltre)[1]),
						true,
						true);
			} else if (EXPEDIENT_CAMP_TIPUS.equals(codiCamp)) {
				return new TermQuery(new Term(
						codiCamp,
						(String)valorFiltre));
			} else if (EXPEDIENT_CAMP_ESTAT.equals(codiCamp)) {
				return new TermQuery(new Term(
						codiCamp,
						(String)valorFiltre));
			} else {
				String[] parts = codiCamp.split("\\.");
				if (parts.length == 2) {
					Camp camp = null;
					for (Camp c: camps) {
						if (parts[1].equals(c.getCodi()) && parts[0].equals(c.getDefinicioProces().getJbpmKey())) {
							camp = c;
							break;
						}
					}
					if (camp != null) {
						if (	camp.getTipus().equals(TipusCamp.INTEGER) ||
								camp.getTipus().equals(TipusCamp.FLOAT) ||
								camp.getTipus().equals(TipusCamp.DATE) ||
								camp.getTipus().equals(TipusCamp.PRICE)) {
							Object valorInicial = ((Object[])valorFiltre)[0];
							Object valorFinal = ((Object[])valorFiltre)[1];
							if (valorInicial != null && valorFinal != null) {
								return new TermRangeQuery(
										codiCamp,
										valorIndexPerCamp(camp, valorInicial),
										valorIndexPerCamp(camp, valorFinal),
										true,
										true);
							}
						} else if (	camp.getTipus().equals(TipusCamp.STRING) ||
									camp.getTipus().equals(TipusCamp.TEXTAREA)) {
							String valorIndex = valorIndexPerCamp(camp, valorFiltre);
							if (!"".equals(valorIndex)) {
								return queryPerStringAmbWildcards(
										codiCamp,
										valorIndex);
							}
						} else {
							return new TermQuery(new Term(
									codiCamp,
									valorIndexPerCamp(camp, valorFiltre)));
						}
					}
				}
			}
		}
		return null;
	}

	private Query queryPerStringAmbWildcards(String codi, String termes) {
		BooleanQuery query = new BooleanQuery();
		String[] termesTots = normalitzarILlevarAccents(termes).split(" ");
		for (int i = 0; i < termesTots.length; i++) {
			if (!"".equals(termesTots[i])) {
				query.add(
						new WildcardQuery(new Term(
								codi,
								"*" + termesTots[i] + "*")),
						BooleanClause.Occur.MUST);
			}
		}
		return query;
	}

	private Term termIdFromExpedient(Expedient expedient) {
		return new Term(
				EXPEDIENT_CAMP_ID,
				expedient.getId().toString());
	}

	private synchronized void checkIndexOk() {
		getLuceneIndexTemplate().addDocuments(new ArrayList<Document>());
	}

	private String normalitzarILlevarAccents(String str) {
		String resultat = str.toLowerCase().
	    replaceAll("[àâ]","a").
		replaceAll("[èéêë]","e").
		replaceAll("[ïî]","i").
	    replaceAll("Ô","o").
	    replaceAll("[ûù]","u").
	    replaceAll("[ÀÂ]","A").
	    replaceAll("[ÈÉÊË]","E").
	    replaceAll("[ÏÎ]","I").
	    replaceAll("Ô","O").
	    replaceAll("[ÛÙ]","U");
		return resultat; 
	}

	private void addFieldToDocument(
			Document document,
			DefinicioProces definicioProces,
			Camp camp,
			Object valor,
			boolean checkMultiple) {
		if (valor != null) {
			if (checkMultiple && camp.isMultiple()) {
				Object[] valors = (Object[])valor;
				for (Object o: valors) {
					addFieldToDocument(
							document,
							definicioProces,
							camp,
							o,
							false);
				}
			} else {
				String clauIndex = definicioProces.getJbpmKey() + "." + camp.getCodi();
				String valorIndex = valorIndexPerCamp(camp, valor);
				boolean analyzed = 
					camp.getTipus().equals(TipusCamp.STRING) ||
					camp.getTipus().equals(TipusCamp.TEXTAREA);
				document.add(new Field(
						clauIndex,
						valorIndex,
						Field.Store.YES,
						(analyzed) ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED));
			}
		}
	}
	private String valorIndexPerCamp(Camp camp, Object valor) {
		if (camp.getTipus().equals(TipusCamp.INTEGER)) {
			return numberPerIndexar((Long)valor);
		} else if (camp.getTipus().equals(TipusCamp.FLOAT)) {
			return numberPerIndexar((Double)valor);
		} else if (camp.getTipus().equals(TipusCamp.BOOLEAN)) {
			return ((Boolean)valor) ? "S" : "N";
		} else if (camp.getTipus().equals(TipusCamp.DATE)) {
			return dataPerIndexar((Date)valor);
		} else if (camp.getTipus().equals(TipusCamp.PRICE)) {
			return numberPerIndexar((BigDecimal)valor);
		} else if (camp.getTipus().equals(TipusCamp.TERMINI)) {
			Termini term = (Termini)valor;
			return term.getAnys() + "/" + term.getMesos() + "/" + term.getDies();
		} else if (camp.getTipus().equals(TipusCamp.SELECCIO)) {
			return (String)valor;
		} else if (camp.getTipus().equals(TipusCamp.SUGGEST)) {
			return (String)valor;
		} else if (camp.getTipus().equals(TipusCamp.STRING)) {
			return normalitzarILlevarAccents((String)valor);
		} else if (camp.getTipus().equals(TipusCamp.TEXTAREA)) {
			return normalitzarILlevarAccents((String)valor);
		} else {
			if (valor == null)
				return null;
			return valor.toString();
		}
	}

	private String numberPerIndexar(Number number) {
		String[] parts = number.toString().split("\\.");
		StringBuffer partSencera = new StringBuffer(parts[0]);
		while (partSencera.length() < NUMDIGITS_PART_SENCERA)
			partSencera.insert(0, "0");
		StringBuffer partDecimal = new StringBuffer();
		if (parts.length > 1)
			partDecimal.append(parts[1]);
		while (partDecimal.length() < NUMDIGITS_PART_DECIMAL)
			partDecimal.append("0");
		if (partDecimal.length() > NUMDIGITS_PART_DECIMAL)
			return partSencera.toString() + "." + partDecimal.substring(0, NUMDIGITS_PART_DECIMAL).toString();
		else
			return partSencera.toString() + "." + partDecimal.toString();
	}

	private String dataPerIndexar(Date data) {
		DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSSS");
		return sdf.format(data);
	}

	private static Log logger = LogFactory.getLog(LuceneDao.class);

}
