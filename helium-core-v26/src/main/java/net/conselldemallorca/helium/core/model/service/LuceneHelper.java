/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springmodules.lucene.index.core.DocumentModifier;
import org.springmodules.lucene.index.support.LuceneIndexSupport;
import org.springmodules.lucene.search.core.HitExtractor;
import org.springmodules.lucene.search.core.LuceneSearchTemplate;

import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDto;

/**
 * Helper per a gestionar la informació dels expedients emprant Lucene.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class LuceneHelper extends LuceneIndexSupport {
	public static final String CLAU_EXPEDIENT_ID = "H3l1um#expedient.id";

	private static final int NUMDIGITS_PART_SENCERA = 15;
	private static final int NUMDIGITS_PART_DECIMAL = 6;
	private static final String PATRO_DATES_INDEX = "yyyyMMddHHmmSS";

	private static final String VALOR_CAMP_BUIT = "H3l1um#camp.buit";
	protected static final String VALOR_DOMINI_SUFIX = "@text@";
	
	private static final String MIN_VALUE = "0000";
	private static final String MAX_VALUE = "99999999999999999999999999999999999999999999999";
	
	protected LuceneSearchTemplate searchTemplate;
	
	private static final String LUCENE_ESCAPE_CHARS = " |\\+|\'|\\(|\\)|\\[|\\]|\\&|\\!|\\*|\\{|\\}|\\?|\\:|\\^|\\~|\"|\\\\";

	@Resource
	protected MesuresTemporalsHelper mesuresTemporalsHelper;



	// TODO Ha d'estar actiu mentre els expedients no es reindexin totalment
	// si es desactiva abans de la reindexació total aleshores hi haura expedients
	// que no sortiran als resultats de les consultes per tipus.
	protected static final boolean PEGAT_ENTORN_ACTIU = true;

	public synchronized void createExpedientAsync(
			final Expedient expedient,
			final Map<String, DefinicioProces> definicionsProces,
			final Map<String, Set<Camp>> camps,
			final Map<String, Map<String, Object>> valors,
			final Map<String, Map<String, String>> textDominis,
			final boolean finalitzat,
			final boolean comprovarIniciant) {
		logger.debug("Creant expedient a l'index Lucene (ASYNC) (" +
				"id=" + expedient.getId() + ")");
		Thread thread = new Thread() {
			public void run() {
				createExpedient(expedient, definicionsProces, camps, valors, textDominis, finalitzat, comprovarIniciant);
			}
		};
		thread.start();
	}
	public synchronized void updateExpedientCapsaleraAsync(
			final Expedient expedient,
			final boolean finalitzat) {
		logger.debug("Actualitzant capsalera expedient a l'index Lucene (ASYNC) (" +
				"id=" + expedient.getId() + ")");
		Thread thread = new Thread() {
			public void run() {
				updateExpedientCapsalera(expedient, finalitzat);
			}
		};
		thread.start();
	}
	public synchronized void updateExpedientCampsAsync(
			final Expedient expedient,
			final Map<String, DefinicioProces> definicionsProces,
			final Map<String, Set<Camp>> camps,
			final Map<String, Map<String, Object>> valors,
			final Map<String, Map<String, String>> textDominis,
			final boolean finalitzat) {
		logger.debug("Actualitzant camps de l'expedient a l'index Lucene (ASYNC) (" +
				"id=" + expedient.getId() + ")");
		Thread thread = new Thread() {
			public void run() {
				updateExpedientCamps(expedient, definicionsProces, camps, valors, textDominis, finalitzat);
			}
		};
		thread.start();
	}
	public synchronized void deleteExpedientAsync(
			final Expedient expedient) {
		logger.debug("Esborrant expedient de l'index Lucene (ASYNC) (" +
				"id=" + expedient.getId() + ")");
		Thread thread = new Thread() {
			public void run() {
				deleteExpedient(expedient);
			}
		};
		thread.start();
	}
	public synchronized void deleteAllAsync() {
		logger.debug("Esborrant tota la informació de l'index Lucene (ASYNC)");
		Thread thread = new Thread() {
			public void run() {
				deleteAll();
			}
		};
		thread.start();
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
		logger.debug("Creant expedient a l'index Lucene (" +
				"id=" + expedient.getId() + ")");
		// Si l'expedient s'està iniciant no l'indexa per evitar possibles duplicitats
		// al reindexar des dels handlers de modificar dades de l'expedient
		boolean indexarExpedient = true;
		if (comprovarIniciant) {
			Expedient expedientIniciant = ExpedientIniciantDto.getExpedient();
			logger.debug("Creant expedient a l'index Lucene: mirant si indexar (" +
					"id=" + expedient.getId() + ", " +
					"expedientIniciant=" + expedientIniciant + ", " +
					"expedientIniciant.id=" + ((expedientIniciant != null) ? expedientIniciant.getId() : null) + ")");
			indexarExpedient = (expedientIniciant == null || !expedientIniciant.getId().equals(expedient.getId()));
		}
		if (indexarExpedient) {
			logger.debug("Creant expedient a l'index Lucene: indexació realitzada (id=" + expedient.getId() + ")");
			mesuresTemporalsHelper.mesuraIniciar("Lucene: createExpedient", "lucene", expedient.getTipus().getNom());
			checkIndexOk();
			Document document = updateDocumentFromExpedient(null, expedient, definicionsProces, camps, valors, textDominis, finalitzat);
			getLuceneIndexTemplate().addDocument(document);
			mesuresTemporalsHelper.mesuraCalcular("Lucene: createExpedient", "lucene", expedient.getTipus().getNom());
		} else {
			logger.debug("Creant expedient a l'index Lucene: indexació abortada (id=" + expedient.getId() + ")");
		}
	}

	public synchronized boolean updateExpedientCapsalera(
			final Expedient expedient,
			final boolean finalitzat) {
		logger.debug("Actualitzant capsalera expedient a l'index Lucene (" +
				"id=" + expedient.getId() + ")");
		mesuresTemporalsHelper.mesuraIniciar("Lucene: updateExpedientCapsalera", "lucene", expedient.getTipus().getNom());
		boolean resultat = updateExpedientCamps(expedient, null, null, null, null, finalitzat);
		mesuresTemporalsHelper.mesuraCalcular("Lucene: updateExpedientCapsalera", "lucene", expedient.getTipus().getNom());
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public synchronized boolean updateExpedientCamps(
			final Expedient expedient,
			final Map<String, DefinicioProces> definicionsProces,
			final Map<String, Set<Camp>> camps,
			final Map<String, Map<String, Object>> valors,
			final Map<String, Map<String, String>> textDominis,
			final boolean finalitzat) {
		logger.debug("Actualitzant informació de l'expedient a l'index Lucene (" +
				"id=" + expedient.getId() + ")");
		mesuresTemporalsHelper.mesuraIniciar("Lucene: updateExpedientCamps", "lucene", expedient.getTipus().getNom());
		checkIndexOk();
		try {
			List<Long> resposta = searchTemplate.search(new TermQuery(termIdFromExpedient(expedient)), new HitExtractor() {
				public Object mapHit(int id, Document document, float score) {
					return new Long(document.get(ExpedientCamps.EXPEDIENT_CAMP_ID));
				}
			});
			if (resposta.size() > 0) {
				getLuceneIndexTemplate().updateDocument(termIdFromExpedient(expedient), new DocumentModifier() {
					public Document updateDocument(Document document) {
						return updateDocumentFromExpedient(document, expedient, definicionsProces, camps, valors, textDominis, finalitzat);
					}
				});
			} else {
				createExpedient(
						expedient,
						definicionsProces,
						camps,
						valors,
						textDominis,
						finalitzat,
						true);
			}
			mesuresTemporalsHelper.mesuraCalcular("Lucene: updateExpedientCamps", "lucene", expedient.getTipus().getNom());
			return true;
		} catch (Exception ex) {
			logger.error("Error actualitzant l'índex per l'expedient " + expedient.getId(), ex);
			mesuresTemporalsHelper.mesuraCalcular("Lucene: updateExpedientCamps", "lucene", expedient.getTipus().getNom());
			return false;
		}
	}

	public synchronized void deleteExpedient(Expedient expedient) {
		logger.debug("Esborrant informació de l'expedient de l'index Lucene (" +
				"id=" + expedient.getId() + ")");
		mesuresTemporalsHelper.mesuraIniciar("Lucene: deleteExpedient", "lucene", expedient.getTipus().getNom());
		checkIndexOk();
		getLuceneIndexTemplate().deleteDocuments(termIdFromExpedient(expedient));
		mesuresTemporalsHelper.mesuraCalcular("Lucene: deleteExpedient", "lucene", expedient.getTipus().getNom());
	}

	@SuppressWarnings("unchecked")
	public synchronized void deleteAll() {
		logger.debug("Esborrant tota la informació de l'index Lucene");
		mesuresTemporalsHelper.mesuraIniciar("Lucene: deleteAll", "lucene");
		checkIndexOk();
		List<Integer> documentsTots = searchTemplate.search(new MatchAllDocsQuery(), new HitExtractor() {
			public Object mapHit(int id, Document document, float score) {
				return new Integer(id);
			}
		});
		for (Integer id : documentsTots)
			getLuceneIndexTemplate().deleteDocument(id.intValue());
		getLuceneIndexTemplate().optimize();
		mesuresTemporalsHelper.mesuraCalcular("Lucene: deleteAll", "lucene");
	}

	@SuppressWarnings("unchecked")
	public List<Long> findNomesIds(
			final Entorn entorn,
			ExpedientTipus expedientTipus,
			List<Camp> filtreCamps,
			Map<String, Object> filtreValors) {
		logger.debug("Consulta d'index Lucene només ids (" +
				"entornCodi=" + entorn.getCodi() + ", " +
				"tipusCodi=" + expedientTipus.getCodi() + ")");
		mesuresTemporalsHelper.mesuraIniciar("Lucene: findNomesIds", "lucene");
		checkIndexOk();
		Query query = getLuceneQuery(entorn, expedientTipus, filtreCamps, filtreValors);
		List<Long> resposta = searchTemplate.search(query, new HitExtractor() {
			public Object mapHit(int id, Document document, float score) {
				boolean ignorar = false;
				if (PEGAT_ENTORN_ACTIU) {
					Field campEntorn = document.getField(ExpedientCamps.EXPEDIENT_CAMP_ENTORN);
					ignorar = campEntorn != null && !campEntorn.stringValue().equals(entorn.getCodi());
				}
				if (!ignorar) {
					return new Long(document.get(ExpedientCamps.EXPEDIENT_CAMP_ID));
				} else {
					return null;
				}
			}
		}, new Sort(new SortField(ExpedientCamps.EXPEDIENT_CAMP_ID, SortField.STRING, true)));
		if (PEGAT_ENTORN_ACTIU) {
			Iterator<Long> it = resposta.iterator();
			while (it.hasNext()) {
				Long valor = it.next();
				if (valor == null)
					it.remove();
			}
		}
		mesuresTemporalsHelper.mesuraCalcular("Lucene: findNomesIds", "lucene");
		return resposta;
	}

	@SuppressWarnings("unchecked")
	public List<Long> findIdsAmbDadesExpedientPaginatV3(
			final Entorn entorn,
			ExpedientTipus expedientTipus,
			List<Camp> filtreCamps,
			List<Camp> campsInforme,
			Map<String, Object> filtreValors,
			String sort,
			boolean asc,
			final int firstRow,
			final int maxResults) {
		logger.debug("Consulta paginada d'index Lucene (1) (" +
				"entornCodi=" + entorn.getCodi() + ", " +
				"tipusCodi=" + expedientTipus.getCodi() + ")");
		mesuresTemporalsHelper.mesuraIniciar("Lucene: findAmbDadesExpedientPaginatV3", "lucene");
		checkIndexOk();
		Query query = getLuceneQuery(
				entorn,
				expedientTipus,
				filtreCamps,
				filtreValors);
		Sort luceneSort = getLuceneSort(
				sort,
				asc,
				campsInforme);
		final List<Long> resposta = searchTemplate.search(query, new HitExtractor() {
			private int count = 0;
			
			public Long mapHit(int id, Document document, float score) {
				Long valorsDocument = null;
				boolean ignorar = false;
				if (PEGAT_ENTORN_ACTIU) {
					Field campEntorn = document.getField(ExpedientCamps.EXPEDIENT_CAMP_ENTORN);
					ignorar = campEntorn != null && !campEntorn.stringValue().equals(entorn.getCodi());
				}
				if (!ignorar) {
					if (maxResults == -1 || (count >= firstRow && count < firstRow + maxResults)) {
						valorsDocument = new Long(document.get(ExpedientCamps.EXPEDIENT_CAMP_ID));
						count++;
					}
				}
				return valorsDocument;
			}
		}, luceneSort);
		if (PEGAT_ENTORN_ACTIU) {
			Iterator<Long> it = resposta.iterator();
			while (it.hasNext()) {
				Long valor = it.next();
				if (valor == null)
					it.remove();
			}
		}
		
		mesuresTemporalsHelper.mesuraCalcular("Lucene: findAmbDadesExpedientPaginatV3", "lucene");
		return resposta;
	}

	public List<Map<String, DadaIndexadaDto>> findAmbDadesExpedientPaginatV3(
			String entornCodi,
			List<Long> llistaExpedientIds,
			List<Camp> informeCamps,
			String sort,
			boolean asc,
			int firstRow,
			int maxResults) {
		logger.debug("Consulta paginada d'index Lucene donats ids (" +
				"entornCodi=" + entornCodi + ", " +
				"llistaExpedientIds=" + llistaExpedientIds.size() + ")");
		mesuresTemporalsHelper.mesuraIniciar("Lucene: findAmbDadesExpedientV3", "lucene");
		checkIndexOk();
		BooleanQuery bquery = new BooleanQuery();
		for (Long id : llistaExpedientIds) {
			bquery.add(new TermQuery(new Term(ExpedientCamps.EXPEDIENT_CAMP_ID, id.toString())), BooleanClause.Occur.SHOULD);
		}
		Query query = (bquery.getClauses().length > 0) ? bquery : new MatchAllDocsQuery();
		List<Map<String, DadaIndexadaDto>> resultat = getDadesExpedientPerConsulta(entornCodi, query, informeCamps, true, sort, asc, firstRow, maxResults);
		mesuresTemporalsHelper.mesuraCalcular("Lucene: findAmbDadesExpedientV3", "lucene");
		return resultat;
	}

	public Object[] findPaginatAmbDadesV3(
			final Entorn entorn,
			ExpedientTipus expedientTipus,
			final Collection<Long> expedientIds,
			List<Camp> filtreCamps,
			Map<String, Object> filtreValors,
			List<Camp> informeCamps,
			PaginacioParamsDto paginacioParams) {
		checkIndexOk();
		Query query = getLuceneQuery(
				entorn,
				expedientTipus,
				filtreCamps,
				filtreValors);
		final int firstRow;
		final int maxResults;
		if (paginacioParams != null) {
			firstRow = paginacioParams.getPaginaNum() * paginacioParams.getPaginaTamany();
			maxResults = paginacioParams.getPaginaTamany();
		} else {
			firstRow = 0;
			maxResults = -1;
		}
		Sort luceneSort = getLuceneSort(
				paginacioParams,
				informeCamps);
		final long[] count = new long[1];
		HitExtractor hitExtractor = new HitExtractor() {
			@SuppressWarnings("unchecked")
			public Map<String, List<String>> mapHit(
					int id,
					Document document,
					float score) {
				Map<String, List<String>> valorsDocument = null;
				boolean ignorar = false;
				if (PEGAT_ENTORN_ACTIU) {
					Field campEntorn = document.getField(ExpedientCamps.EXPEDIENT_CAMP_ENTORN);
					ignorar = campEntorn != null && !campEntorn.stringValue().equals(entorn.getCodi());
				}
				if (expedientIds != null) {
					Long expedientId = new Long(document.getField(ExpedientCamps.EXPEDIENT_CAMP_ID).stringValue());
					ignorar = !expedientIds.contains(expedientId);
				}
				if (!ignorar) {
					if (maxResults == -1 || (count[0] >= firstRow && count[0] < firstRow + maxResults)) {
						valorsDocument = new HashMap<String, List<String>>();
						for (Field field: (List<Field>)document.getFields()) {
							if (valorsDocument.get(field.name()) == null) {
								List<String> valors = new ArrayList<String>();
								valors.add(field.stringValue());
								valorsDocument.put(field.name(), valors);
							} else {
								List<String> valors = valorsDocument.get(field.name());
								valors.add(field.stringValue());
							}
						}
					}
					count[0]++;
				}
				return valorsDocument;
			}
		};
		@SuppressWarnings("unchecked")
		final List<Map<String, List<String>>> resultats = searchTemplate.search(
				query,
				hitExtractor,
				luceneSort);
		if (PEGAT_ENTORN_ACTIU) {
			Iterator<Map<String, List<String>>> it = resultats.iterator();
			while (it.hasNext()) {
				Map<String, List<String>> valor = it.next();
				if (valor == null)
					it.remove();
			}
		}
		List<Map<String, DadaIndexadaDto>> resposta = toDadesIndexadesDto(
				resultats,
				informeCamps,
				true);
		return new Object[] {
				resposta,
				new Long(count[0])
		};
	}

	public Object[] findPaginatNomesIdsV3(
			final Entorn entorn,
			ExpedientTipus expedientTipus,
			final Collection<Long> expedientIds,
			List<Camp> filtreCamps,
			Map<String, Object> filtreValors,
			PaginacioParamsDto paginacioParams) {
		checkIndexOk();
		Query query = getLuceneQuery(
				entorn,
				expedientTipus,
				filtreCamps,
				filtreValors);
		final int firstRow;
		final int maxResults;
		if (paginacioParams != null) {
			firstRow = paginacioParams.getPaginaNum() * paginacioParams.getPaginaTamany();
			maxResults = paginacioParams.getPaginaTamany();
		} else {
			firstRow = 0;
			maxResults = -1;
		}
		final long[] count = new long[1];
		HitExtractor hitExtractor = new HitExtractor() {
			public Long mapHit(int id, Document document, float score) {
				Long valorsDocument = null;
				boolean ignorar = false;
				if (PEGAT_ENTORN_ACTIU) {
					Field campEntorn = document.getField(ExpedientCamps.EXPEDIENT_CAMP_ENTORN);
					ignorar = campEntorn != null && !campEntorn.stringValue().equals(entorn.getCodi());
				}
				if (expedientIds != null) {
					Long expedientId = new Long(document.getField(ExpedientCamps.EXPEDIENT_CAMP_ID).stringValue());
					ignorar = !expedientIds.contains(expedientId);
				}
				if (!ignorar) {
					if (maxResults == -1 || (count[0] >= firstRow && count[0] < firstRow + maxResults)) {
						valorsDocument = new Long(document.get(ExpedientCamps.EXPEDIENT_CAMP_ID));
						count[0]++;
					}
				}
				return valorsDocument;
			}
		};
		@SuppressWarnings("unchecked")
		final List<Long> resultats = searchTemplate.search(
				query,
				hitExtractor,
				(Sort)null);
		if (PEGAT_ENTORN_ACTIU) {
			Iterator<Long> it = resultats.iterator();
			while (it.hasNext()) {
				Long valor = it.next();
				if (valor == null)
					it.remove();
			}
		}
		return new Object[] {
				resultats,
				new Long(count[0])
		};
	}

	@Autowired
	public void setSearchTemplate(LuceneSearchTemplate searchTemplate) {
		this.searchTemplate = searchTemplate;
	}



	private Document updateDocumentFromExpedient(Document docLucene, Expedient expedient, Map<String, DefinicioProces> definicionsProces, Map<String, Set<Camp>> camps, Map<String, Map<String, Object>> valors, Map<String, Map<String, String>> textDominis, boolean finalitzat) {
		boolean isUpdate = (docLucene != null);
		Document document = (docLucene != null) ? docLucene : new Document();
		createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_ENTORN, expedient.getEntorn().getCodi(), Field.Store.YES, Field.Index.NOT_ANALYZED), isUpdate);
		createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_ID, expedient.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED), isUpdate);
		createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_NUMERO, (expedient.getNumero() != null) ? expedient.getNumero() : VALOR_CAMP_BUIT, Field.Store.YES, Field.Index.ANALYZED), isUpdate);
		createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_NUMERO + "_no_analyzed", (expedient.getNumero() != null) ? expedient.getNumero() : VALOR_CAMP_BUIT, Field.Store.NO, Field.Index.NOT_ANALYZED), isUpdate);
		createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_TITOL, (expedient.getTitol() != null) ? (expedient.getTitol()) : VALOR_CAMP_BUIT, Field.Store.YES, (expedient.getTitol() != null) ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED), isUpdate);
		createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_TITOL + "_no_analyzed", (expedient.getTitol() != null) ? (expedient.getTitol()) : VALOR_CAMP_BUIT, Field.Store.NO, Field.Index.NOT_ANALYZED), isUpdate);
		createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_COMENTARI, (expedient.getComentari() != null) ? (expedient.getComentari()) : VALOR_CAMP_BUIT, Field.Store.YES, (expedient.getComentari() != null) ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED), isUpdate);
		createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_COMENTARI + "_no_analyzed", (expedient.getComentari() != null) ? (expedient.getComentari()) : VALOR_CAMP_BUIT, Field.Store.NO, Field.Index.NOT_ANALYZED), isUpdate);
		createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_INICIADOR, (expedient.getIniciadorCodi() != null) ? expedient.getIniciadorCodi() : VALOR_CAMP_BUIT, Field.Store.YES, Field.Index.NOT_ANALYZED), isUpdate);
		createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE, (expedient.getResponsableCodi() != null) ? expedient.getResponsableCodi() : VALOR_CAMP_BUIT, Field.Store.YES, Field.Index.NOT_ANALYZED), isUpdate);
		createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI, dataPerIndexar(expedient.getDataInici()), Field.Store.YES, Field.Index.NOT_ANALYZED), isUpdate);
		createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_TIPUS, expedient.getTipus().getCodi(), Field.Store.YES, Field.Index.NOT_ANALYZED), isUpdate);
		if (finalitzat) {
			createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_ESTAT, "-1", Field.Store.YES, Field.Index.NOT_ANALYZED), isUpdate);
		} else if (expedient.getEstat() != null) {
			createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_ESTAT, expedient.getEstat().getCodi(), Field.Store.YES, Field.Index.NOT_ANALYZED), isUpdate);
			createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_ESTAT + VALOR_DOMINI_SUFIX + expedient.getEstat().getCodi(), expedient.getEstat().getCodi(), Field.Store.YES, Field.Index.NOT_ANALYZED), isUpdate);
		} else {
			createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_ESTAT, "0", Field.Store.YES, Field.Index.NOT_ANALYZED), isUpdate);
		}
		if (definicionsProces != null) {
			for (String clau : definicionsProces.keySet()) {
				DefinicioProces definicioProces = definicionsProces.get(clau);
				Map<String, Object> valorsProces = valors.get(clau);
				if (valorsProces != null) {
					for (Camp camp : camps.get(clau)) {
						try {
							updateDocumentCamp(document, definicioProces, camp, valorsProces.get(camp.getCodi()), textDominis.get(clau), true, isUpdate, new HashSet<String>());
						} catch (Exception ex) {
							StringBuilder sb = new StringBuilder();
							getClassAsString(sb, valorsProces.get(camp.getCodi()));
							logger.error("No s'ha pogut indexar el camp (definicioProces=" + definicioProces.getJbpmKey() + "(v." + definicioProces.getVersio() + ")" + ", camp=" + camp.getCodi() + ", tipus=" + camp.getTipus() + ", multiple=" + camp.isMultiple() + ") amb un valor (tipus=" + sb.toString() + ")", ex);
						}
					}
				}
			}
		}
		return document;
	}

	private void createOrUpdateDocumentField(Document document, Field field, boolean isUpdate) {
		if (isUpdate)
			document.removeFields(field.name());
		document.add(field);
	}

	protected Query getLuceneQuery(
			Entorn entorn,
			ExpedientTipus expedientTipus,
			List<Camp> filtreCamps,
			Map<String, Object> filtreValors) {
		BooleanQuery bquery = new BooleanQuery();
		if (!PEGAT_ENTORN_ACTIU) {
			bquery.add(new BooleanClause(queryFromCampFiltre(ExpedientCamps.EXPEDIENT_CAMP_ENTORN, entorn.getCodi(), null), BooleanClause.Occur.MUST));
		}
		bquery.add(new BooleanClause(queryFromCampFiltre(ExpedientCamps.EXPEDIENT_CAMP_TIPUS, expedientTipus.getCodi(), null), BooleanClause.Occur.MUST));
		for (String clau : filtreValors.keySet()) {
			Query query = queryFromCampFiltre(clau, filtreValors.get(clau), filtreCamps);
			if (query != null) {
				bquery.add(new BooleanClause(query, BooleanClause.Occur.MUST));
			}
		}
		return (bquery.getClauses().length > 0) ? bquery : new MatchAllDocsQuery();
	}
	protected Query getLuceneQuery(
			Entorn entorn,
			ExpedientTipus expedientTipus,
			List<Camp> filtreCamps,
			Map<String, Object> filtreValors,
			List<Long> ids) {
		BooleanQuery bquery = new BooleanQuery();
		if (!PEGAT_ENTORN_ACTIU) {
			bquery.add(new BooleanClause(queryFromCampFiltre(ExpedientCamps.EXPEDIENT_CAMP_ENTORN, entorn.getCodi(), null), BooleanClause.Occur.MUST));
		}
		bquery.add(new BooleanClause(queryFromCampFiltre(ExpedientCamps.EXPEDIENT_CAMP_TIPUS, expedientTipus.getCodi(), null), BooleanClause.Occur.MUST));
		for (String clau : filtreValors.keySet()) {
			Query query = queryFromCampFiltre(clau, filtreValors.get(clau), filtreCamps);
			if (query != null)
				bquery.add(new BooleanClause(query, BooleanClause.Occur.MUST));
		}
		if (ids != null && !ids.isEmpty()) {
			BooleanQuery nested = new BooleanQuery();
			for (Long id: ids) {
				nested.add(new BooleanClause(new TermQuery(new Term(ExpedientCamps.EXPEDIENT_CAMP_ID, id.toString())), BooleanClause.Occur.SHOULD));
			}
			bquery.add(nested, BooleanClause.Occur.MUST);
		}
		return (bquery.getClauses().length > 0) ? bquery : new MatchAllDocsQuery();
	}

	protected Sort getLuceneSort(
			PaginacioParamsDto paginacioParams,
			List<Camp> informeCamps) {
		String sort = "expedient$identificador";
		boolean asc = false;
		if (paginacioParams != null) {
			for (OrdreDto ordre: paginacioParams.getOrdres()) {
				asc = ordre.getDireccio().equals(OrdreDireccioDto.ASCENDENT);
				String clau = ordre.getCamp().replace(
						net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps.EXPEDIENT_PREFIX_JSP,
						net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps.EXPEDIENT_PREFIX);
				if (ordre.getCamp().contains("dadesExpedient")) {
					sort = clau.replace("/", ".").replace("dadesExpedient.", "").replace(".valorMostrar", "");
				} else {
					sort = clau.replace(".", net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps.EXPEDIENT_PREFIX_SEPARATOR);
				}
				break;
			}
		}
		return getLuceneSort(
				sort,
				asc,
				informeCamps);
	}
	protected Sort getLuceneSort(
			String sort,
			boolean asc,
			List<Camp> informeCamps) {
		Sort luceneSort = null;
		if (sort != null && sort.length() > 0) {
			if (ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(sort)) {
				sort = sort + "_no_analyzed";
			} else if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(sort)) {
				sort = sort + "_no_analyzed";
			} else if (ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(sort)) {
				sort = sort + "_no_analyzed";
			} else {
				for (Camp camp: informeCamps) {
					if (camp != null && sort.endsWith(camp.getCodi()) && (camp.getTipus().equals(TipusCamp.STRING) || camp.getTipus().equals(TipusCamp.TEXTAREA))) {
						sort = sort + "_no_analyzed";
						break;
					}
				}
				String campOrdenacio = null;
				if ("expedient$identificador".equals(sort)) {
					campOrdenacio = ExpedientCamps.EXPEDIENT_CAMP_ID;
				} else {
					campOrdenacio = sort;
				}
				luceneSort = new Sort(new SortField(campOrdenacio, SortField.STRING, !asc));
			}
		} else {
			luceneSort = new Sort(new SortField(ExpedientCamps.EXPEDIENT_CAMP_ID, SortField.STRING, !asc));
		}
		return luceneSort;
	}

	protected Query queryFromCampFiltre(
			String codiCamp,
			Object valorFiltre,
			List<Camp> camps) {
		try {
			if (valorFiltre != null && valorFiltre != "") {
				if (codiCamp.startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
					if (ExpedientCamps.EXPEDIENT_CAMP_ID.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_ENTORN.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_INICIADOR.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_GEOX.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_GEOY.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_GEOREF.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_REGNUM.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_REGDATA.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_UNIADM.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_IDIOMA.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_TRAMIT.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_TIPUS.equals(codiCamp)
							|| ExpedientCamps.EXPEDIENT_CAMP_ESTAT.equals(codiCamp)) {
						String valorIndex = valorFiltre.toString();
						if (valorIndex != null && valorIndex.length() > 0) {
							return new TermQuery(new Term(codiCamp, valorIndex));
						}
					} else if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_INFOATUR.equals(codiCamp)) {
						String valorIndex = ((String) valorFiltre).toLowerCase();
						if (valorIndex != null && valorIndex.length() > 0) {
							return queryPerStringAmbWildcards(codiCamp, valorIndex);
						}
					} else if (ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI.equals(codiCamp)) {
						Date valorInicial = ((Date[]) valorFiltre)[0];
						Date valorFinal = ((Date[]) valorFiltre)[1];
						if (valorInicial != null && valorFinal != null) {
							Calendar calFinal = Calendar.getInstance();
							calFinal.setTime(valorFinal);
							calFinal.set(Calendar.HOUR, 23);
							calFinal.set(Calendar.MINUTE, 59);
							calFinal.set(Calendar.SECOND, 99);
							return new TermRangeQuery(codiCamp, dataPerIndexar(valorInicial), dataPerIndexar(calFinal.getTime()), true, true);
						} else if (valorInicial != null) {
							return new TermRangeQuery(codiCamp, dataPerIndexar(valorInicial), MAX_VALUE, true, true);
						} else if (valorFinal != null) {
							Calendar calFinal = Calendar.getInstance();
							calFinal.setTime(valorFinal);
							calFinal.set(Calendar.HOUR, 23);
							calFinal.set(Calendar.MINUTE, 59);
							calFinal.set(Calendar.SECOND, 99);
							return new TermRangeQuery(codiCamp, MIN_VALUE, dataPerIndexar(calFinal.getTime()), true, true);
						}
					}
				} else {
					String[] parts = codiCamp.split("\\.");
					if (parts.length == 2) {
						Camp camp = null;
						for (Camp c : camps) {
							if (parts[1].equals(c.getCodi()) && parts[0].equals(c.getDefinicioProces().getJbpmKey())) {
								camp = c;
								break;
							}
						}
						if (camp != null) {
							if (camp.getTipus().equals(TipusCamp.INTEGER) || camp.getTipus().equals(TipusCamp.FLOAT) || camp.getTipus().equals(TipusCamp.DATE) || camp.getTipus().equals(TipusCamp.PRICE)) {
								Object valorInicial = ((Object[]) valorFiltre)[0];
								Object valorFinal = ((Object[]) valorFiltre)[1];
								if (valorInicial != null && valorFinal != null) {
									return new TermRangeQuery(codiCamp, valorIndexPerCamp(camp, valorInicial), valorIndexPerCamp(camp, valorFinal), true, true);
								} else if (valorInicial != null) {
									return new TermRangeQuery(codiCamp, valorIndexPerCamp(camp, valorInicial), MAX_VALUE, true, true);
								} else if (valorFinal != null) {
									return new TermRangeQuery(codiCamp, MIN_VALUE, valorIndexPerCamp(camp, valorFinal), true, true);
								}
							} else if (camp.getTipus().equals(TipusCamp.STRING) || camp.getTipus().equals(TipusCamp.TEXTAREA)) {
								String valorIndex = valorIndexPerCamp(camp, valorFiltre).toLowerCase();
								if (valorIndex != null && valorIndex.length() > 0) {
									return queryPerStringAmbWildcards(codiCamp, valorIndex);
								}
							} else {
								String valorIndex = valorIndexPerCamp(camp, valorFiltre);
								if (valorIndex != null && valorIndex.length() > 0) {
									return new TermQuery(new Term(codiCamp, valorIndexPerCamp(camp, valorFiltre)));
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut afegir el camp " + codiCamp + " al filtre", ex);
		}
		return null;
	}
	
	private Query queryPerStringAmbWildcards(String codi, String termes) {		
		BooleanQuery booleanQuery = new BooleanQuery();
		
		String[] termesTots = termes.trim().split(LUCENE_ESCAPE_CHARS);
		for (String terme : termesTots) {
			if (terme.equals(termesTots[0])) {
				booleanQuery.add(
						new WildcardQuery(new Term(
								codi,
								"*" + terme)),
						BooleanClause.Occur.MUST
				);
			} else if (terme.equals(termesTots[termesTots.length-1])) {
				booleanQuery.add(
						new WildcardQuery(new Term(
								codi,
								terme + "*")),
						BooleanClause.Occur.MUST
				);
			} else {
				booleanQuery.add(
						new TermQuery(new Term(
								codi,
								terme)),
						BooleanClause.Occur.MUST
				);					
			}
		}
		
		return booleanQuery;
	}

	private Term termIdFromExpedient(Expedient expedient) {
		return new Term(ExpedientCamps.EXPEDIENT_CAMP_ID, expedient.getId().toString());
	}

	protected synchronized void checkIndexOk() {
		getLuceneIndexTemplate().addDocuments(new ArrayList<Document>());
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, DadaIndexadaDto>> getDadesExpedientPerConsulta(
			final String entornCodi,
			Query query,
			List<Camp> campsInforme,
			boolean incloureId,
			String sort,
			boolean asc,
			final int firstRow,
			final int maxResults) {
		Sort luceneSort = null;
		if (sort != null && sort.length() > 0) {
			if (ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(sort)) {
				sort = sort + "_no_analyzed";
			} else if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(sort)) {
				sort = sort + "_no_analyzed";
			} else if (ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(sort)) {
				sort = sort + "_no_analyzed";
			} else {
				for (Camp camp : campsInforme) {
					if (camp != null && sort.endsWith(camp.getCodi()) && (camp.getTipus().equals(TipusCamp.STRING) || camp.getTipus().equals(TipusCamp.TEXTAREA))) {
						sort = sort + "_no_analyzed";
						break;
					}
				}
				String campOrdenacio = null;
				if ("expedient$identificador".equals(sort)) {
					campOrdenacio = ExpedientCamps.EXPEDIENT_CAMP_ID;
				} else {
					campOrdenacio = sort;
				}
				luceneSort = new Sort(new SortField(campOrdenacio, SortField.STRING, !asc));
			}
		} else
			luceneSort = new Sort(new SortField(ExpedientCamps.EXPEDIENT_CAMP_ID, SortField.STRING, !asc));
		final List<Map<String, List<String>>> resultats = searchTemplate.search(query, new HitExtractor() {
			private int count = 0;
			public Object mapHit(int id, Document document, float score) {
				Map<String, List<String>> valorsDocument = null;
				boolean ignorar = false;
				if (PEGAT_ENTORN_ACTIU) {
					Field campEntorn = document.getField(ExpedientCamps.EXPEDIENT_CAMP_ENTORN);
					ignorar = campEntorn != null && !campEntorn.stringValue().equals(entornCodi);
				}
				if (!ignorar) {
					if (maxResults == -1 || (count >= firstRow && count < firstRow + maxResults)) {
						valorsDocument = new HashMap<String, List<String>>();
						for (Field field : (List<Field>) document.getFields()) {
							if (valorsDocument.get(field.name()) == null) {
								List<String> valors = new ArrayList<String>();
								valors.add(field.stringValue());
								valorsDocument.put(field.name(), valors);
							} else {
								List<String> valors = valorsDocument.get(field.name());
								valors.add(field.stringValue());
							}
						}
					}
					count++;
				}
				return valorsDocument;
			}
		}, luceneSort);
		return toDadesIndexadesDto(
				resultats,
				campsInforme,
				incloureId);
	}

	private List<Map<String, DadaIndexadaDto>> toDadesIndexadesDto(
			List<Map<String, List<String>>> resultats,
			List<Camp> informeCamps,
			boolean incloureId) {
		List<Map<String, DadaIndexadaDto>> resposta = new ArrayList<Map<String, DadaIndexadaDto>>();
		if (resultats.size() > 0) {
			Set<String> clausAmbValorMultiple = new HashSet<String>();
			for (Map<String, List<String>> fila : resultats) {
				if (fila != null) {
					List<DadaIndexadaDto> dadesFila = new ArrayList<DadaIndexadaDto>();
					for (String codi : fila.keySet()) {
						for (Camp camp: informeCamps) {
							boolean coincideix = false;
							String[] partsCodi = codi.split("\\.");
							if (camp != null) {
								if (codi.startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
									coincideix = codi.equals(camp.getCodi());
								} else {
									coincideix = camp.getDefinicioProces() != null && partsCodi[0].equals(camp.getDefinicioProces().getJbpmKey()) && partsCodi[1].equals(camp.getCodi());
								}
							}
							if (coincideix) {
								for (String valorIndex : fila.get(codi)) {
									try {
										Object valor = valorCampPerIndex(camp, valorIndex);
										if (valor != null) {
											DadaIndexadaDto dadaCamp;
											if (codi.startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
												dadaCamp = new DadaIndexadaDto(camp.getCodi(), camp.getEtiqueta());
											} else {
												dadaCamp = new DadaIndexadaDto(partsCodi[0], partsCodi[1], camp.getEtiqueta());
											}
											if (camp.getTipus().equals(TipusCamp.SELECCIO) || camp.getTipus().equals(TipusCamp.SUGGEST))
												dadaCamp.setOrdenarPerValorMostrar(true);
											dadaCamp.setMultiple(false);
											dadaCamp.setValorIndex(valorIndex);
											dadaCamp.setValor(valor);
											String textDomini = null;
											List<String> textDominiIndex = fila.get(codi + VALOR_DOMINI_SUFIX + valor);
											if (textDominiIndex != null)
												textDomini = textDominiIndex.get(0);
											if (textDomini == null)
												textDomini = (valor != null && valor.toString().length() > 0) ? "¿" + valor.toString() + "?" : null;
											dadaCamp.setValorMostrar(Camp.getComText(camp.getTipus(), valor, textDomini));
											dadesFila.add(dadaCamp);
										}
									} catch (Exception ex) {
										logger.error("Error al obtenir el valor de l'índex pel camp " + codi, ex);
									}
								}
								break;
							}
						}
					}
					Map<String, DadaIndexadaDto> mapFila = new HashMap<String, DadaIndexadaDto>();
					if (incloureId) {
						/* Incorpora l'id de l'expedient */
						DadaIndexadaDto dadaExpedientId = new DadaIndexadaDto(ExpedientCamps.EXPEDIENT_CAMP_ID, "expedientId");
						dadaExpedientId.setValorIndex(fila.get(ExpedientCamps.EXPEDIENT_CAMP_ID).get(0));
						mapFila.put(CLAU_EXPEDIENT_ID, dadaExpedientId);
					}
					for (DadaIndexadaDto dada : dadesFila) {
						if (mapFila.containsKey(dada.getReportFieldName())) {
							DadaIndexadaDto dadaMultiple = mapFila.get(dada.getReportFieldName());
							if (!dadaMultiple.isMultiple()) {
								clausAmbValorMultiple.add(dada.getReportFieldName());
								dadaMultiple.addValorMultiple(dadaMultiple.getValor());
								dadaMultiple.addValorIndexMultiple(dadaMultiple.getValorIndex());
								dadaMultiple.addValorMostrarMultiple(dadaMultiple.getValorMostrar());
								dadaMultiple.setValor(null);
								dadaMultiple.setValorIndex(null);
								dadaMultiple.setValorMostrar(null);
								dadaMultiple.setMultiple(true);
							}
							dadaMultiple.addValorMultiple(dada.getValor());
							dadaMultiple.addValorIndexMultiple(dada.getValorIndex());
							dadaMultiple.addValorMostrarMultiple(dada.getValorMostrar());
						} else {
							mapFila.put(dada.getReportFieldName(), dada);
						}
					}
					resposta.add(mapFila);
				}
			}
			// Revisa les variables de tipus registre que només
			// ténen 1 fila per a marcar-les com a múltiples
			for (Map<String, DadaIndexadaDto> dadesExpedient : resposta) {
				for (String clauMultiple : clausAmbValorMultiple) {
					DadaIndexadaDto dadaMultiple = dadesExpedient.get(clauMultiple);
					if (dadaMultiple != null && !dadaMultiple.isMultiple()) {
						dadaMultiple.addValorMultiple(dadaMultiple.getValor());
						dadaMultiple.addValorIndexMultiple(dadaMultiple.getValorIndex());
						dadaMultiple.addValorMostrarMultiple(dadaMultiple.getValorMostrar());
						dadaMultiple.setValor(null);
						dadaMultiple.setValorIndex(null);
						dadaMultiple.setValorMostrar(null);
						dadaMultiple.setMultiple(true);
					}
				}
			}
		}
		return resposta;
	}

	private void updateDocumentCamp(Document document, DefinicioProces definicioProces, Camp camp, Object valor, Map<String, String> textDominis, boolean checkMultiple, boolean isUpdate, Set<String> campsActualitzats) {
		if (valor != null) {
			if (checkMultiple && camp.isMultiple()) {
				Object[] valors = (Object[]) valor;
				for (Object o : valors) {
					updateDocumentCamp(document, definicioProces, camp, o, textDominis, false, isUpdate, campsActualitzats);
				}
			} else if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
				Object[] valorsMembres = (Object[]) valor;
				int index = 0;
				for (CampRegistre campRegistre : camp.getRegistreMembres()) {
					Camp membre = campRegistre.getMembre();
					if (index < valorsMembres.length)
						updateDocumentCamp(document, definicioProces, membre, valorsMembres[index++], textDominis, false, isUpdate, campsActualitzats);
				}
			} else {
				String clauIndex = definicioProces.getJbpmKey() + "." + camp.getCodi();
				String valorIndex = valorIndexPerCamp(camp, valor);
				boolean analyzed = camp.getTipus().equals(TipusCamp.STRING) || camp.getTipus().equals(TipusCamp.TEXTAREA);
				boolean update = isUpdate && !campsActualitzats.contains(clauIndex);
				campsActualitzats.add(clauIndex);
				createOrUpdateDocumentField(document, new Field(clauIndex, valorIndex, Field.Store.YES, (analyzed) ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED), update);
				update = isUpdate && !campsActualitzats.contains(clauIndex + "_no_analyzed");
				campsActualitzats.add(clauIndex + "_no_analyzed");
				createOrUpdateDocumentField(document, new Field(clauIndex + "_no_analyzed", valorIndex, Field.Store.NO, Field.Index.NOT_ANALYZED), update);
				String textDomini = textDominis.get(camp.getCodi() + "@" + valorIndex);
				if (textDomini != null && (camp.getTipus().equals(TipusCamp.SELECCIO) || camp.getTipus().equals(TipusCamp.SUGGEST)) && document.get(clauIndex + VALOR_DOMINI_SUFIX + valorIndex) == null) {
					update = isUpdate && !campsActualitzats.contains(clauIndex + VALOR_DOMINI_SUFIX + valorIndex);
					campsActualitzats.add(clauIndex + VALOR_DOMINI_SUFIX + valorIndex);
					createOrUpdateDocumentField(document, new Field(clauIndex + VALOR_DOMINI_SUFIX + valorIndex, textDomini, Field.Store.YES, Field.Index.ANALYZED), update);
				}
			}
		}
	}

	protected String valorIndexPerCamp(Camp camp, Object valor) {
		if (camp.getTipus().equals(TipusCamp.INTEGER)) {
			return numberPerIndexar((Long) valor);
		} else if (camp.getTipus().equals(TipusCamp.FLOAT)) {
			return numberPerIndexar((Double) valor);
		} else if (camp.getTipus().equals(TipusCamp.BOOLEAN)) {
			return ((Boolean) valor) ? "S" : "N";
		} else if (camp.getTipus().equals(TipusCamp.DATE)) {
			return dataPerIndexar((Date) valor);
		} else if (camp.getTipus().equals(TipusCamp.PRICE)) {
			return numberPerIndexar((BigDecimal) valor);
		} else if (camp.getTipus().equals(TipusCamp.TERMINI)) {
			if (valor instanceof Termini) {
				Termini term = (Termini) valor;
				return term.getAnys() + "/" + term.getMesos() + "/" + term.getDies();
			} else if (valor instanceof String) { 
				return (String) valor;
			} else {
				return valor.toString();
			}
		} else if (camp.getTipus().equals(TipusCamp.SELECCIO)) {
			return (String) valor;
		} else if (camp.getTipus().equals(TipusCamp.SUGGEST)) {
			return (String) valor;
		} else if (camp.getTipus().equals(TipusCamp.STRING)) {
			return ((String) valor);
		} else if (camp.getTipus().equals(TipusCamp.TEXTAREA)) {
			return ((String) valor);
		} else {
			if (valor == null)
				return null;
			return valor.toString();
		}
	}

	protected Object valorCampPerIndex(Camp camp, String valor) throws Exception {
		if (camp.getTipus().equals(TipusCamp.INTEGER)) {
			return Long.parseLong(valor.split("\\.")[0]);
		} else if (camp.getTipus().equals(TipusCamp.FLOAT)) {
			return Double.parseDouble(valor);
		} else if (camp.getTipus().equals(TipusCamp.BOOLEAN)) {
			return new Boolean("S".equals(valor));
		} else if (camp.getTipus().equals(TipusCamp.DATE)) {
			return new SimpleDateFormat(PATRO_DATES_INDEX).parse(valor);
		} else if (camp.getTipus().equals(TipusCamp.PRICE)) {
			return new BigDecimal(valor);
		} else if (camp.getTipus().equals(TipusCamp.TERMINI)) {
			String[] parts = valor.split("/");
			Termini term = new Termini();
			term.setAnys(Integer.parseInt(parts[0]));
			term.setMesos(Integer.parseInt(parts[0]));
			term.setDies(Integer.parseInt(parts[0]));
			return term;
		} else if (camp.getTipus().equals(TipusCamp.SELECCIO)) {
			return (String) valor;
		} else if (camp.getTipus().equals(TipusCamp.SUGGEST)) {
			return (String) valor;
		} else {
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

	private String dataPerIndexar(Date data) {
		DateFormat sdf = new SimpleDateFormat(PATRO_DATES_INDEX);
		return sdf.format(data);
	}

	private void getClassAsString(StringBuilder sb, Object o) {
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

	protected static final Log logger = LogFactory.getLog(LuceneHelper.class);
}
