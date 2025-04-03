/**
 * 
 */
package net.conselldemallorca.helium.core.helperv26;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import net.conselldemallorca.helium.core.common.ThreadLocalInfo;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
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
	private static final String LUCENE_ESCAPE_CHARS = " |\\+|\\-|\\_|\'|\\(|\\)|\\[|\\]|\\&|\\!|\\*|\\{|\\}|\\?|\\:|\\^|\\~|\"|\\\\";
	private static final String EXPEDIENT_NUMERO_SEPARADOR = ";";
	
	protected LuceneSearchTemplate searchTemplate;

	@Resource
	protected MesuresTemporalsHelper mesuresTemporalsHelper;

	/** Objecte de sincronització per accedir al LuceneIndex. */
	private static Object syncObj = new Object();


	// TODO Ha d'estar actiu mentre els expedients no es reindexin totalment
	// si es desactiva abans de la reindexació total aleshores hi haura expedients
	// que no sortiran als resultats de les consultes per tipus.
	protected static final boolean PEGAT_ENTORN_ACTIU = true;

	public boolean createExpedient(
			final Expedient expedient,
			Map<String, DefinicioProces> definicionsProces,
			Map<String, Set<Camp>> camps,
			Map<String, Map<String, Object>> valors,
			Map<String, Map<String, String>> textDominis,
			boolean finalitzat,
			boolean comprovarIniciant) {
		logger.debug("Creant expedient a l'index Lucene (" +
				"id=" + expedient.getId() + ")");
		mesuresTemporalsHelper.mesuraIniciar("Lucene: createExpedient", "lucene", expedient.getTipus().getNom());
		boolean success = createOrUpdateExpedientIndex(
				expedient,
				definicionsProces,
				camps,
				valors,
				textDominis,
				finalitzat,
				comprovarIniciant);
		mesuresTemporalsHelper.mesuraCalcular("Lucene: createExpedient", "lucene", expedient.getTipus().getNom());
		return success;
	}

	public boolean updateExpedientCapsalera(
			final Expedient expedient,
			final boolean finalitzat) {
		logger.debug("Actualitzant capsalera expedient a l'index Lucene (" +
				"id=" + expedient.getId() + ")");
		mesuresTemporalsHelper.mesuraIniciar("Lucene: updateExpedientCapsalera", "lucene", expedient.getTipus().getNom());
		boolean success;
		try {
			success = createOrUpdateExpedientIndex(
					expedient,
					null,
					null,
					null,
					null,
					finalitzat,
					true);
		} catch (Exception ex) {
			logger.error("Error actualitzant l'índex per l'expedient " + expedient.getId(), ex);
			mesuresTemporalsHelper.mesuraCalcular("Lucene: updateExpedientCapsalera", "lucene", expedient.getTipus().getNom());
			success = false;
		}
		return success;
	}

	public boolean updateExpedientCamps(
			final Expedient expedient,
			final Map<String, DefinicioProces> definicionsProces,
			final Map<String, Set<Camp>> camps,
			final Map<String, Map<String, Object>> valors,
			final Map<String, Map<String, String>> textDominis,
			final boolean finalitzat) {
		logger.debug("Actualitzant informació de l'expedient a l'index Lucene (" +
				"id=" + expedient.getId() + ")");
		mesuresTemporalsHelper.mesuraIniciar("Lucene: updateExpedientCamps", "lucene", expedient.getTipus().getNom());
		boolean success;
		try {
			success = createOrUpdateExpedientIndex(
					expedient,
					definicionsProces,
					camps,
					valors,
					textDominis,
					finalitzat,
					true);
			mesuresTemporalsHelper.mesuraCalcular("Lucene: updateExpedientCamps", "lucene", expedient.getTipus().getNom());
		} catch (Exception ex) {
			logger.error("Error actualitzant l'índex per l'expedient " + expedient.getId(), ex);
			mesuresTemporalsHelper.mesuraCalcular("Lucene: updateExpedientCamps", "lucene", expedient.getTipus().getNom());
			success = false;
		}
		return success;
	}
	
	/** Mètode per esborrar un sol camp de l'índex del document. */
	public boolean deleteExpedientCamp(
			final Expedient expedient, 
			final String camp) {
		logger.debug("Esborrant camp l'expedient a l'index Lucene (" +
				"id=" + expedient.getId() + ", camp=" + camp +")");
		mesuresTemporalsHelper.mesuraIniciar("Lucene: deleteExpedientCamp", "lucene", expedient.getTipus().getNom());
		try {
			synchronized(syncObj) {
				checkIndexOk();
				getLuceneIndexTemplate().updateDocument(termIdFromExpedient(expedient), new DocumentModifier() {
					public Document updateDocument(Document document) {
						removeDocumentField(document, camp);
						return document;
					}
				});
			}
			mesuresTemporalsHelper.mesuraCalcular("Lucene: deleteExpedientCamp", "lucene", expedient.getTipus().getNom());
			return true;
		} catch (Exception ex) {
			logger.error("Error actualitzant l'índex per l'expedient " + expedient.getId(), ex);
			mesuresTemporalsHelper.mesuraCalcular("Lucene: deleteExpedientCamp", "lucene", expedient.getTipus().getNom());
			return false;
		}
	}

	public void deleteExpedient(final Expedient expedient) {
		logger.debug("Esborrant informació de l'expedient de l'index Lucene (" +
				"id=" + expedient.getId() + ")");
		mesuresTemporalsHelper.mesuraIniciar("Lucene: deleteExpedient", "lucene", expedient.getTipus().getNom());
		synchronized(syncObj) {
			checkIndexOk();
			getLuceneIndexTemplate().deleteDocuments(termIdFromExpedient(expedient));
		}
		mesuresTemporalsHelper.mesuraCalcular("Lucene: deleteExpedient", "lucene", expedient.getTipus().getNom());
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
		//checkIndexOk();
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
		//checkIndexOk();
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
		//checkIndexOk();
		BooleanQuery bquery = new BooleanQuery();
		for (Long id : llistaExpedientIds) {
			bquery.add(new TermQuery(new Term(ExpedientCamps.EXPEDIENT_CAMP_ID, id.toString())), BooleanClause.Occur.SHOULD);
		}
		Query query = (bquery.getClauses().length > 0) ? bquery : new MatchAllDocsQuery();
		List<Map<String, DadaIndexadaDto>> resultat = getDadesExpedientPerConsulta(entornCodi, query, informeCamps, true, sort, asc, firstRow, maxResults);
		mesuresTemporalsHelper.mesuraCalcular("Lucene: findAmbDadesExpedientV3", "lucene");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	public Object[] findPaginatAmbDadesV3(
			final Entorn entorn,
			ExpedientTipus expedientTipus,
			final Collection<Long> expedientIds,
			List<Camp> filtreCamps,
			Map<String, Object> filtreValors,
			List<Camp> informeCamps,
			PaginacioParamsDto paginacioParams) {
		//checkIndexOk();
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

	@SuppressWarnings("unchecked")
	public Object[] findPaginatNomesIdsV3(
			final Entorn entorn,
			ExpedientTipus expedientTipus,
			final Collection<Long> expedientIds,
			List<Camp> filtreCamps,
			Map<String, Object> filtreValors,
			PaginacioParamsDto paginacioParams) {
		//checkIndexOk();
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


	@SuppressWarnings("unchecked")
	private boolean createOrUpdateExpedientIndex(
			final Expedient expedient,
			final Map<String, DefinicioProces> definicionsProces,
			final Map<String, Set<Camp>> camps,
			final Map<String, Map<String, Object>> valors,
			final Map<String, Map<String, String>> textDominis,
			final boolean finalitzat,
			final boolean comprovarIniciant) {
		final Map<String, String> errors = new HashMap<String, String>();
		synchronized(syncObj) {
			checkIndexOk();
			List<Long> resposta = searchTemplate.search(new TermQuery(termIdFromExpedient(expedient)), new HitExtractor() {
				public Object mapHit(int id, Document document, float score) {
					return new Long(document.get(ExpedientCamps.EXPEDIENT_CAMP_ID));
				}
			});
			if (resposta.size() == 1) {
				getLuceneIndexTemplate().updateDocument(termIdFromExpedient(expedient), new DocumentModifier() {
					public Document updateDocument(Document document) {
						return updateDocumentFromExpedient(document, expedient, definicionsProces, camps, valors, textDominis, finalitzat, errors);
					}
				});
			} else {
				boolean indexarExpedient = true;
				// Comrpovem si hi ha més d'un resultat
				if (resposta.size() > 1) {
					logger.debug("S'han trobat " + resposta.size() + " entrades a Lucene per l'id " + expedient.getId() + ". Es procedeix a esborrar primer.");
					this.deleteExpedient(expedient);
				}
				if (comprovarIniciant) {
					Expedient expedientIniciant = ThreadLocalInfo.getExpedient();
					logger.debug("Creant expedient a l'index Lucene: mirant si indexar (" +
							"id=" + expedient.getId() + ", " +
							"expedientIniciant=" + expedientIniciant + ", " +
							"expedientIniciant.id=" + ((expedientIniciant != null) ? expedientIniciant.getId() : null) + ")");
					indexarExpedient = (expedientIniciant == null || !expedientIniciant.getId().equals(expedient.getId()));
				}
				if (indexarExpedient) {
					logger.debug("Creant expedient a l'index Lucene: indexació realitzada (id=" + expedient.getId() + ")");
					Document document = updateDocumentFromExpedient(null, expedient, definicionsProces, camps, valors, textDominis, finalitzat, errors);
					getLuceneIndexTemplate().addDocument(document);
				} else {
					logger.debug("Creant expedient a l'index Lucene: indexació abortada (id=" + expedient.getId() + ")");
				}
			}			
		}
		return errors.isEmpty();
	}

	private Document updateDocumentFromExpedient(
			Document docLucene, 
			final Expedient expedient, 
			Map<String, DefinicioProces> definicionsProces, 
			Map<String, Set<Camp>> camps, 
			Map<String, Map<String, Object>> valors, 
			Map<String, Map<String, String>> textDominis, 
			boolean finalitzat,
			Map<String, String> errors) {
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
		if (expedient.getDataFi()!=null) {
			createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_DATA_FI, dataPerIndexar(expedient.getDataFi()), Field.Store.YES, Field.Index.NOT_ANALYZED), isUpdate);
		} else {
			createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_DATA_FI, VALOR_CAMP_BUIT, Field.Store.YES, Field.Index.NOT_ANALYZED), isUpdate);
		}
		String nifsInteressats = expedient.getInteressatsNifs(" ");
		createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_NIF, (nifsInteressats != null) ? nifsInteressats : VALOR_CAMP_BUIT, Field.Store.YES, Field.Index.NOT_ANALYZED), isUpdate);

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
							String errMsg = "No s'ha pogut indexar el camp (definicioProces=" + definicioProces.getJbpmKey() + "(v." + definicioProces.getVersio() + ")" + ", camp=" + camp.getCodi() + ", tipus=" + camp.getTipus() + ", multiple=" + camp.isMultiple() + ") amb un valor (tipus=" + sb.toString() + 
									 ") per l'expedient " + (expedient != null ? expedient.getIdentificador()  + (expedient.getTipus() != null ? " (" + expedient.getTipus().getCodi() + ")" : null ) : null) + ": " + ex.getMessage() ;
							logger.error(errMsg);
							errors.put(camp.getCodi(), errMsg);
						}
					}
				}
			}
		}
		// Crea un camp al documents pels errors
		try {			
			createOrUpdateDocumentField(document, new Field(ExpedientCamps.EXPEDIENT_CAMP_ERRORS_REINDEXACIO, new ObjectMapper().writeValueAsString(errors != null ? errors : new HashMap<String, String>()), Field.Store.YES, Field.Index.NO), isUpdate);
		} catch (Exception e) {
			logger.error("Error guardant errors de reindexació els camps " + errors + " per l'expedient " + expedient.getId() + " " + expedient.getIdentificador());
		}
		
		return document;
	}

	private void createOrUpdateDocumentField(Document document, Field field, boolean isUpdate) {
		if (isUpdate)
			document.removeFields(field.name());
		document.add(field);
	}

	private void removeDocumentField(Document document, String field) {
		document.removeFields(field);
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
//		String sort = "expedient$identificador";
		String[] sort = new String[(paginacioParams.getOrdres().size() == 0)?1:paginacioParams.getOrdres().size()];
		boolean asc = false;
		if (paginacioParams != null) {
			for (OrdreDto ordre: paginacioParams.getOrdres()) {
				asc = ordre.getDireccio().equals(OrdreDireccioDto.ASCENDENT);
				String clau = ordre.getCamp().replace(
						net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps.EXPEDIENT_PREFIX_JSP,
						net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps.EXPEDIENT_PREFIX);
				if (ordre.getCamp().contains("dadesExpedient")) {
					sort[paginacioParams.getOrdres().indexOf(ordre)] = clau.replace("/", ".").replace("dadesExpedient.", "").replace(".valorMostrar", "");
				} else {
					sort[paginacioParams.getOrdres().indexOf(ordre)] = clau.replace(".", net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps.EXPEDIENT_PREFIX_SEPARATOR);
				}
				
			}
			if(sort.length == 0)
				sort[0] = "expedient$identificador";
		}
		return getLuceneSort(
				sort,
				asc,
				informeCamps);
	}
	
	protected Sort getLuceneSort(
			String[] sort,
			boolean asc,
			List<Camp> informeCamps) {
		if(sort.length == 1)
			return getLuceneSort(sort[0],asc, informeCamps);
		SortField[] sortFields = new SortField[sort.length];
		Sort luceneSort = null;
		for(int i = 0; i<sort.length; i++) {
			sortFields[i] = new SortField(sort[i], 3);
		}
		luceneSort = new Sort(sortFields);
		return luceneSort;
	}
	
	protected Sort getLuceneSort(
			String sort,
			boolean asc,
			List<Camp> informeCamps) {
		Sort luceneSort = null;
		if (sort != null && sort.length() > 0) {
			if (ExpedientCamps.EXPEDIENT_CAMP_ID.equals(sort)) {
				luceneSort = new Sort(new SortField(sort, SortField.LONG, !asc));
			} else if (ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(sort)) {
				sort = sort + "_no_analyzed";
				luceneSort = new Sort(new SortField(sort, SortField.STRING, !asc));
			} else if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(sort)) {
				sort = sort + "_no_analyzed";
				luceneSort = new Sort(new SortField(sort, SortField.STRING, !asc));
			} else if (ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(sort)) {
				sort = sort + "_no_analyzed";
				luceneSort = new Sort(new SortField(sort, SortField.STRING, !asc));
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
					luceneSort = new Sort(new SortField(campOrdenacio, SortField.LONG, !asc));
				} else {
					campOrdenacio = sort;
					luceneSort = new Sort(new SortField(campOrdenacio, SortField.STRING, !asc));
				}
			}
		} else {
			luceneSort = new Sort(new SortField(ExpedientCamps.EXPEDIENT_CAMP_ID, SortField.LONG, !asc));
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
					if (ExpedientCamps.EXPEDIENT_CAMP_ID.equals(codiCamp)) {						
						if (valorFiltre.getClass().isArray() ) {
							// Rang d'identificadors
							String valorInicial = ((String[]) valorFiltre)[0];
							String valorFinal = ((String[]) valorFiltre)[1];
							if (valorInicial != null && valorFinal != null) {
								return new TermRangeQuery(codiCamp, valorInicial, valorFinal, true, true);
							} else if (valorInicial != null) {
								return new TermRangeQuery(codiCamp, valorInicial, MAX_VALUE, true, true);
							} else if (valorFinal != null) {
								return new TermRangeQuery(codiCamp, MIN_VALUE, valorFinal, true, true);
							}
						} else {
							// Un únic identificador
							String valorIndex = valorFiltre.toString();
							if (valorIndex != null && valorIndex.length() > 0) {
								return new TermQuery(new Term(codiCamp, valorIndex));
							}
						}
					} else if (	ExpedientCamps.EXPEDIENT_CAMP_ENTORN.equals(codiCamp) || 
								ExpedientCamps.EXPEDIENT_CAMP_INICIADOR.equals(codiCamp) || 
								ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE.equals(codiCamp) || 
								ExpedientCamps.EXPEDIENT_CAMP_GEOX.equals(codiCamp) || 
								ExpedientCamps.EXPEDIENT_CAMP_GEOY.equals(codiCamp) || 
								ExpedientCamps.EXPEDIENT_CAMP_GEOREF.equals(codiCamp) || 
								ExpedientCamps.EXPEDIENT_CAMP_REGNUM.equals(codiCamp) || 
								ExpedientCamps.EXPEDIENT_CAMP_REGDATA.equals(codiCamp) || 
								ExpedientCamps.EXPEDIENT_CAMP_UNIADM.equals(codiCamp) || 
								ExpedientCamps.EXPEDIENT_CAMP_IDIOMA.equals(codiCamp) || 
								ExpedientCamps.EXPEDIENT_CAMP_TRAMIT.equals(codiCamp) || 
								ExpedientCamps.EXPEDIENT_CAMP_TIPUS.equals(codiCamp) ||
								ExpedientCamps.EXPEDIENT_CAMP_ESTAT.equals(codiCamp)) {
						String valorIndex = valorFiltre.toString();
						if (valorIndex != null && valorIndex.length() > 0) {
							return new TermQuery(new Term(codiCamp, valorIndex));
						}
					} else if (	ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(codiCamp) ||
								ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(codiCamp) || 
								ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(codiCamp) ||
								ExpedientCamps.EXPEDIENT_CAMP_NIF.equals(codiCamp) ||
								ExpedientCamps.EXPEDIENT_CAMP_INFOATUR.equals(codiCamp)) {
						String valorIndex = ((String) valorFiltre).toLowerCase();
						if (valorIndex != null && valorIndex.length() > 0) {
							if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(codiCamp) && valorIndex.contains(EXPEDIENT_NUMERO_SEPARADOR)) {
								String[] parts = valorIndex.split(EXPEDIENT_NUMERO_SEPARADOR);
								BooleanQuery bquery = new BooleanQuery();
								for (String part: parts) {
									bquery.add(new BooleanClause(queryPerStringAmbWildcards(codiCamp, part.trim()), BooleanClause.Occur.SHOULD));
								}
								return bquery;
							} else {
								return queryPerStringAmbWildcards(codiCamp, valorIndex);
							}
						}
					} else if (ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI.equals(codiCamp) || ExpedientCamps.EXPEDIENT_CAMP_DATA_FI.equals(codiCamp)) {
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
					Camp camp = null;
					String[] parts = codiCamp.split("\\.");
					if (parts.length == 2) {
						// Definició de procés
						for (Camp c : camps) {
							if (parts[1].equals(c.getCodi()) && parts[0].equals(c.getDefinicioProces().getJbpmKey())) {
								camp = c;
								break;
							}
						}
					} else if(parts.length == 1) {
						// Expedient tipus
						for (Camp c : camps) {
							if (parts[0].equals(c.getCodi())) {
								camp = c;
								break;
							}
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
		} catch (Exception ex) {
			logger.error("No s'ha pogut afegir el camp " + codiCamp + " al filtre", ex);
		}
		return null;
	}
	
	private Query queryPerStringAmbWildcards(String codi, String termes) {		
		BooleanQuery booleanQuery = new BooleanQuery();
		String[] termesTots = termes.trim().split(LUCENE_ESCAPE_CHARS);
		boolean primer, darrer;
		for (String terme : termesTots) {
			primer = terme.equals(termesTots[0]);
			darrer = terme.equals(termesTots[termesTots.length-1]);
			booleanQuery.add(
					new WildcardQuery(new Term(
							codi,
						(primer ? "*" : "") + terme + (darrer? "*" : ""))),
					BooleanClause.Occur.MUST
			);
		}
		return booleanQuery;
	}

	private Term termIdFromExpedient(Expedient expedient) {
		return new Term(ExpedientCamps.EXPEDIENT_CAMP_ID, expedient.getId().toString());
	}

	protected void checkIndexOk() {
		synchronized(syncObj) {
			getLuceneIndexTemplate().addDocuments(new ArrayList<Document>());
		}
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
									if (camp.getDefinicioProces() != null && camp.getExpedientTipus() == null)
										// Definició de procés
										coincideix = partsCodi[0].equals(camp.getDefinicioProces().getJbpmKey()) && partsCodi[1].equals(camp.getCodi());
									else if (camp.getExpedientTipus() != null)
										// Expedient tipus
										coincideix = partsCodi[0].equals(camp.getCodi());
								}
							}
							if (coincideix) {
								List<String> valorsIndex = fila.get(codi);
								int i=0;
								DadaIndexadaDto dadaCamp = null;
								Object valor = null;
								for (String valorIndex : valorsIndex) {//MARTA quan coincideix test-sub.text entra aquí
									try {
										valor = valorCampPerIndex(camp, valorIndex);
										if (valor != null) {
											if (codi.startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
												dadaCamp = new DadaIndexadaDto(camp.getCodi(), camp.getEtiqueta());
											} else {
												if(i==0) {
													if (partsCodi.length == 2)
														dadaCamp = new DadaIndexadaDto(partsCodi[0], partsCodi[1], camp.getEtiqueta());
													else 
														dadaCamp = new DadaIndexadaDto(partsCodi[0], camp.getEtiqueta());
												}
											}
											if (camp.getTipus().equals(TipusCamp.SELECCIO) || camp.getTipus().equals(TipusCamp.SUGGEST))
												dadaCamp.setOrdenarPerValorMostrar(true);
											dadaCamp.setMultiple(false);
											dadaCamp.setValorIndex(valorIndex);
											if(i!=0 && TipusCamp.STRING.equals(camp.getTipus())) {
												Object valorConcat = String.valueOf(valor).concat(" , ").concat(dadaCamp.getValor().toString());
												dadaCamp.setValor(valorConcat);
												String textDomini = null;
												List<String> textDominiIndex = fila.get(codi + VALOR_DOMINI_SUFIX + valorConcat);
												if (textDominiIndex != null)
													textDomini = textDominiIndex.get(0);
												if (textDomini == null)
													textDomini = (valorConcat != null && valorConcat.toString().length() > 0) ? "¿" + valorConcat.toString() + "?" : null;
												dadaCamp.setValorMostrar(Camp.getComText(camp.getTipus(), valorConcat, textDomini));
											} else if(i!=0 && camp.isMultiple()) {
												if(camp.getEnumeracio() != null) {
													String textValue = valor.toString();
													for(EnumeracioValors v : camp.getEnumeracio().getEnumeracioValors()) {
														if(v.getCodi().equals(valor.toString())) {
															textValue = v.getNom();
															break;
														}	
													}
													String valorConcat = textValue.concat(" , ").concat(dadaCamp.getValor().toString());
													dadaCamp.setValor(valorConcat);
													dadaCamp.setValorMostrar(valorConcat);
												} else {
													Object valorConcat = String.valueOf(valor).concat(" , ").concat(dadaCamp.getValor().toString());
													dadaCamp.setValor(valorConcat);
													String textDomini = null;
													List<String> textDominiIndex = fila.get(codi + VALOR_DOMINI_SUFIX + valorConcat);
													if (textDominiIndex != null)
														textDomini = textDominiIndex.get(0);
													if (textDomini == null)
														textDomini = (valorConcat != null && valorConcat.toString().length() > 0) ? "¿" + valorConcat.toString() + "?" : null;
													dadaCamp.setValorMostrar(Camp.getComText(camp.getTipus(), valorConcat, textDomini));
												}
											}
											else {
												if(camp.getEnumeracio() != null) {
													String textValue = valor.toString();
													for(EnumeracioValors v : camp.getEnumeracio().getEnumeracioValors()) {
														if(v.getCodi().equals(valor.toString())) {
															textValue = v.getNom();
															break;
														}	
													}
													dadaCamp.setValor(textValue);
													dadaCamp.setValorMostrar(textValue);
												} else {
													dadaCamp.setValor(valor);
													String textDomini = null;
													List<String> textDominiIndex = fila.get(codi + VALOR_DOMINI_SUFIX + valor);
													if (textDominiIndex != null)
														textDomini = textDominiIndex.get(0);
													if (textDomini == null)
														textDomini = (valor != null && valor.toString().length() > 0) ? "¿" + valor.toString() + "?" : null;
													dadaCamp.setValorMostrar(Camp.getComText(camp.getTipus(), valor, textDomini));
												}
											}
											if(i==0) {
												dadesFila.add(dadaCamp);
											}
										}
									} catch (Exception ex) {
										logger.error("Error al obtenir el valor de l'índex pel camp " + codi, ex);
										if (dadaCamp != null)
											dadaCamp.setError(ex.getMessage());
									}
									i++;
								}
								//break;
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
		String clauIndex;
		if (camp.getExpedientTipus() == null)
			// Definició de procés
			clauIndex = definicioProces.getJbpmKey() + "." + camp.getCodi();
		else
			// Expedient tipus
			clauIndex = camp.getCodi();
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
		} else {
			removeDocumentField(document, clauIndex);
		}
	}

	protected String valorIndexPerCamp(Camp camp, Object valor) {
		if (valor != null) {
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
				return valor.toString();
			}
		} else 
			return "";
	}

	protected Object valorCampPerIndex(Camp camp, String valor) throws Exception {
		if (camp.getTipus().equals(TipusCamp.INTEGER)) {
			return Long.parseLong(valor.split("\\.")[0]);
		} else if (camp.getTipus().equals(TipusCamp.FLOAT)) {
			return Double.parseDouble(valor);
		} else if (camp.getTipus().equals(TipusCamp.BOOLEAN)) {
			return new Boolean("S".equals(valor));
		} else if (camp.getTipus().equals(TipusCamp.DATE)) {
			if (!VALOR_CAMP_BUIT.equals(valor)) {
				return new SimpleDateFormat(PATRO_DATES_INDEX).parse(valor);
			} else {
				return "";
			}
		} else if (camp.getTipus().equals(TipusCamp.PRICE)) {
			return new BigDecimal(valor);
		} else if (camp.getTipus().equals(TipusCamp.TERMINI)) {
			/*String[] parts = valor.split("/");
			Termini term = new Termini();
			term.setAnys(Integer.parseInt(parts[0]));
			term.setMesos(Integer.parseInt(parts[0]));
			term.setDies(Integer.parseInt(parts[0]));*/
			return valor;
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
		} else {
			sb.append("null");
		}
	}
	
	public List<Map<String, DadaIndexadaDto>> expedientIndexLuceneGetDades(
			final Expedient expedient, 
			List<Camp> informeCamps) {
		//checkIndexOk();
			    
		Query query = queryFromCampFiltre(
				ExpedientCamps.EXPEDIENT_CAMP_ID,
				expedient.getId().toString(),
				null);
		
		List<Map<String, DadaIndexadaDto>> resultats = 
				getDadesExpedientPerConsulta(
						expedient.getEntorn().getCodi(), 
						query, 
						informeCamps, 
						true, 
						null, 
						true, 
						0, 
						10);
		return resultats;
	}

	protected static final Log logger = LogFactory.getLog(LuceneHelper.class);
}
