/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.core.model.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.jbpm3.integracio.Termini;

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

	public static final String CLAU_EXPEDIENT_ID = "H3l1um#expedient.id";

	private static final int NUMDIGITS_PART_SENCERA = 15;
	private static final int NUMDIGITS_PART_DECIMAL = 6;
	private static final String PATRO_DATES_INDEX = "yyyyMMddHHmmSS";

	private static final String VALOR_CAMP_BUIT = "H3l1um#camp.buit";
	private static final String VALOR_DOMINI_SUFIX = "@text@";

	private LuceneSearchTemplate searchTemplate;



	public synchronized void createExpedient(
			Expedient expedient,
			Map<String, DefinicioProces> definicionsProces,
			Map<String, Set<Camp>> camps,
			Map<String, Map<String, Object>> valors,
			Map<String, Map<String, String>> textDominis,
			boolean finalitzat) {
		checkIndexOk();
		Document document = createDocumentFromExpedient(
				expedient,
				definicionsProces,
				camps,
				valors,
				textDominis,
				finalitzat);
		getLuceneIndexTemplate().addDocument(document);
	}
	@SuppressWarnings("unchecked")
	public synchronized boolean updateExpedient(
			final Expedient expedient,
			final Map<String, DefinicioProces> definicionsProces,
			final Map<String, Set<Camp>> camps,
			final Map<String, Map<String, Object>> valors,
			final Map<String, Map<String, String>> textDominis,
			final boolean finalitzat) {
		checkIndexOk();
		try {
			List<Long> resposta = searchTemplate.search(
					new TermQuery(termIdFromExpedient(expedient)),
					new HitExtractor() {
					    public Object mapHit(int id, Document document, float score) {
				    		return new Long(document.get(ExpedientCamps.EXPEDIENT_CAMP_ID));
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
										valors,
										textDominis,
										finalitzat);
							}
						});
			} else {
				createExpedient(
						expedient,
						definicionsProces,
						camps,
						valors,
						textDominis,
						finalitzat);
			}
			return true;
		} catch (Exception ex) {
			logger.error("Error actualitzant l'índex per l'expedient " + expedient.getId(), ex);
			return false;
		}
	}
	public synchronized void deleteExpedient(Expedient expedient) {
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
	public List<Long> findNomesIds(
			String tipusCodi,
			List<Camp> filtreCamps,
			Map<String, Object> filtreValors) {
		checkIndexOk();
		Query query = queryPerFiltre(
				tipusCodi,
				filtreCamps,
				filtreValors);
		List<Long> resposta = searchTemplate.search(
				query,
				new HitExtractor() {
				    public Object mapHit(int id, Document document, float score) {
			    		return new Long(document.get(ExpedientCamps.EXPEDIENT_CAMP_ID));
				    }
				},
				new Sort(new SortField(ExpedientCamps.EXPEDIENT_CAMP_ID, SortField.STRING, true)));
		return resposta;
	}
	public List<Map<String, DadaIndexadaDto>> findAmbDadesExpedient(
			String tipusCodi,
			List<Camp> filtreCamps,
			Map<String, Object> filtreValors,
			List<Camp> informeCamps,
			String sort,
			boolean asc,
			int firstRow,
			int maxResults) {
		checkIndexOk();
		Query query = queryPerFiltre(
				tipusCodi,
				filtreCamps,
				filtreValors);
		return getDadesExpedientPerConsulta(
				query,
				informeCamps,
				true,
				sort,
				asc,
				firstRow,
				maxResults);
	}

	public List<Map<String, DadaIndexadaDto>> getDadesExpedient(
			Expedient expedient,
			List<Camp> informeCamps) {
		checkIndexOk();
		Query query = queryFromCampFiltre(
				ExpedientCamps.EXPEDIENT_CAMP_ID,
				expedient.getId().toString(),
				null);
		return getDadesExpedientPerConsulta(
				query,
				informeCamps,
				false,
				ExpedientCamps.EXPEDIENT_CAMP_ID,
				true,
				0,
				-1);
	}



	@Autowired
	public void setSearchTemplate(LuceneSearchTemplate searchTemplate) {
		this.searchTemplate = searchTemplate;
	}



	private Document createDocumentFromExpedient(
			Expedient expedient,
			Map<String, DefinicioProces> definicionsProces,
			Map<String, Set<Camp>> camps,
			Map<String, Map<String, Object>> valors,
			Map<String, Map<String, String>> textDominis,
			boolean finalitzat) {
		Document document = new Document();
		document.add(new Field(
				ExpedientCamps.EXPEDIENT_CAMP_ID,
	    		expedient.getId().toString(),
				Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		document.add(new Field(
				ExpedientCamps.EXPEDIENT_CAMP_NUMERO,
	    		(expedient.getNumero() != null) ? expedient.getNumero() : VALOR_CAMP_BUIT,
				Field.Store.YES,
				Field.Index.ANALYZED));
		document.add(new Field(
				ExpedientCamps.EXPEDIENT_CAMP_TITOL,
				(expedient.getTitol() != null) ? normalitzarILlevarAccents(expedient.getTitol()) : VALOR_CAMP_BUIT,
				Field.Store.YES,
				(expedient.getTitol() != null) ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED));
		document.add(new Field(
				ExpedientCamps.EXPEDIENT_CAMP_COMENTARI,
				(expedient.getComentari() != null) ? normalitzarILlevarAccents(expedient.getComentari()) : VALOR_CAMP_BUIT,
				Field.Store.YES,
				(expedient.getComentari() != null) ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED));
		document.add(new Field(
				ExpedientCamps.EXPEDIENT_CAMP_INICIADOR,
	    		(expedient.getIniciadorCodi() != null) ? expedient.getIniciadorCodi() : VALOR_CAMP_BUIT,
				Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		document.add(new Field(
				ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE,
	    		expedient.getResponsableCodi(),
				Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		document.add(new Field(
				ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI,
	    		dataPerIndexar(expedient.getDataInici()),
				Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		document.add(new Field(
				ExpedientCamps.EXPEDIENT_CAMP_TIPUS,
	    		expedient.getTipus().getCodi(),
				Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		if (expedient.getEstat() != null) {
			document.add(new Field(
					ExpedientCamps.EXPEDIENT_CAMP_ESTAT,
					expedient.getEstat().getCodi(),
					Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			document.add(new Field(
					ExpedientCamps.EXPEDIENT_CAMP_ESTAT + VALOR_DOMINI_SUFIX + expedient.getEstat().getCodi(),
					expedient.getEstat().getCodi(),
					Field.Store.YES,
					Field.Index.NOT_ANALYZED));
		} else {
			if (!finalitzat) {
				document.add(new Field(
						ExpedientCamps.EXPEDIENT_CAMP_ESTAT,
			    		"0",
						Field.Store.YES,
						Field.Index.NOT_ANALYZED));
			} else {
				document.add(new Field(
						ExpedientCamps.EXPEDIENT_CAMP_ESTAT,
			    		"-1",
						Field.Store.YES,
						Field.Index.NOT_ANALYZED));
			}
		}
		for (String clau: definicionsProces.keySet()) {
			DefinicioProces definicioProces = definicionsProces.get(clau);
			Map<String, Object> valorsProces = valors.get(clau);
			if (valorsProces != null) {
				for (Camp camp: camps.get(clau)) {
					addFieldToDocument(
							document,
							definicioProces,
							camp,
							valorsProces.get(camp.getCodi()),
							textDominis.get(clau),
							true);
				}
			}
		}
		return document;
	}

	private Query queryPerFiltre(
			String tipusCodi,
			List<Camp> filtreCamps,
			Map<String, Object> filtreValors) {
		BooleanQuery bquery = new BooleanQuery();
		bquery.add(
				new BooleanClause(
						queryFromCampFiltre(
								ExpedientCamps.EXPEDIENT_CAMP_TIPUS,
								tipusCodi,
								null),
						BooleanClause.Occur.MUST));
		for (String clau: filtreValors.keySet()) {
			Query query = queryFromCampFiltre(
					clau,
					filtreValors.get(clau),
					filtreCamps);
			if (query != null)
				bquery.add(new BooleanClause(query, BooleanClause.Occur.MUST));
		}
		return (bquery.getClauses().length > 0) ? bquery : new MatchAllDocsQuery();
	}
	private Query queryFromCampFiltre(
			String codiCamp,
			Object valorFiltre,
			List<Camp> camps) {
		try {
			if (valorFiltre != null) {
				if (codiCamp.startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
					if (	ExpedientCamps.EXPEDIENT_CAMP_ID.equals(codiCamp) ||
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
							//System.out.println(">>> TermQuery " + codiCamp + ": " + valorIndex);
							return new TermQuery(new Term(
									codiCamp,
									valorIndex));
						}
					} else if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(codiCamp) ||
							ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(codiCamp) ||
							ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(codiCamp) ||
							ExpedientCamps.EXPEDIENT_CAMP_INFOATUR.equals(codiCamp)
							) {
						String valorIndex = (String)valorFiltre;
						if (valorIndex != null && valorIndex.length() > 0) {
							//System.out.println(">>> WildcardQuery " + codiCamp + ": " + valorIndex);
							return queryPerStringAmbWildcards(
									codiCamp,
									valorIndex);
						}
					} else if (ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI.equals(codiCamp)) {
						Date valorInicial = ((Date[])valorFiltre)[0];
						Date valorFinal = ((Date[])valorFiltre)[1];
						if (valorInicial != null && valorFinal != null) {
							Calendar calFinal = Calendar.getInstance();
							calFinal.setTime(valorFinal);
							calFinal.set(Calendar.HOUR, 23);
							calFinal.set(Calendar.MINUTE, 59);
							calFinal.set(Calendar.SECOND, 99);
							//System.out.println(">>> TermRangeQuery " + codiCamp + ": " + dataPerIndexar(valorInicial) + ", " + dataPerIndexar(calFinal.getTime()));
							return new TermRangeQuery(
									codiCamp,
									dataPerIndexar(valorInicial),
									dataPerIndexar(calFinal.getTime()),
									true,
									true);
						}
					}
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
									//System.out.println(">>> TermRangeQuery " + codiCamp + ": " + valorIndexPerCamp(camp, valorInicial) + ", " + valorIndexPerCamp(camp, valorFinal));
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
								if (valorIndex != null && valorIndex.length() > 0) {
									//System.out.println(">>> WildcardQuery " + codiCamp + ": " + valorIndex);
									return queryPerStringAmbWildcards(
											codiCamp,
											valorIndex);
								}
							} else {
								String valorIndex = valorIndexPerCamp(camp, valorFiltre);
								if (valorIndex != null && valorIndex.length() > 0) {
									//System.out.println(">>> TermQuery " + codiCamp + ": " + valorIndexPerCamp(camp, valorFiltre));
									return new TermQuery(new Term(
											codiCamp,
											valorIndexPerCamp(camp, valorFiltre)));
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
				ExpedientCamps.EXPEDIENT_CAMP_ID,
				expedient.getId().toString());
	}

	private synchronized void checkIndexOk() {
		getLuceneIndexTemplate().addDocuments(new ArrayList<Document>());
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, DadaIndexadaDto>> getDadesExpedientPerConsulta(
			Query query,
			List<Camp> campsInforme,
			boolean incloureId,
			String sort,
			boolean asc,
			final int firstRow,
			final int maxResults) {
		Sort luceneSort = null;
		if (sort != null && sort.length() > 0)
			luceneSort = new Sort(new SortField(sort, SortField.STRING, !asc));
		else
			luceneSort = new Sort(new SortField(ExpedientCamps.EXPEDIENT_CAMP_ID, SortField.STRING, !asc));
		final List<Map<String, List<String>>> resultats = searchTemplate.search(
				query,
				new HitExtractor() {
					private int count = 0;
				    public Object mapHit(int id, Document document, float score) {
				    	Map<String, List<String>> valorsDocument = null;
				    	if (maxResults == -1 || (count >= firstRow && count < firstRow + maxResults)) {
					    	valorsDocument = new HashMap<String, List<String>>();
					    	for (Field field: (List<Field>)document.getFields()) {
					    		if (valorsDocument.get(field.name()) == null) {
					    			List<String> valors = new ArrayList<String>();
					    			valors.add(field.stringValue());
					    			valorsDocument.put(
						    				field.name(),
						    				valors);
					    		} else {
					    			List<String> valors = valorsDocument.get(field.name());
					    			valors.add(field.stringValue());
					    		}
					    	}
				    	}
				    	count++;
				    	return valorsDocument;
				    }
				},
				luceneSort);
		List<Map<String, DadaIndexadaDto>> resposta = new ArrayList<Map<String, DadaIndexadaDto>>();
		if (resultats.size() > 0) {
			for (Map<String, List<String>> fila: resultats) {
				if (fila != null) {
					List<DadaIndexadaDto> dadesFila = new ArrayList<DadaIndexadaDto>();
					for (String codi: fila.keySet()) {
						for (Camp camp: campsInforme) {
							boolean coincideix;
							String[] partsCodi = codi.split("\\.");
							if (codi.startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
								coincideix = codi.equals(camp.getCodi());
							} else {
								coincideix = 
										camp.getDefinicioProces() != null &&
										partsCodi[0].equals(camp.getDefinicioProces().getJbpmKey()) &&
										partsCodi[1].equals(camp.getCodi());
							}
							if (coincideix) {
								for (String valorIndex: fila.get(codi)) {
									try {
										Object valor = valorCampPerIndex(camp, valorIndex);
										if (valor != null) {
											DadaIndexadaDto dadaCamp;
											if (codi.startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
												dadaCamp = new DadaIndexadaDto(
														camp.getCodi(),
														camp.getEtiqueta());
											} else {
												dadaCamp = new DadaIndexadaDto(
														partsCodi[0],
														partsCodi[1],
														camp.getEtiqueta());
											}
											dadaCamp.setMultiple(false);
											dadaCamp.setValorIndex(valorIndex);
											dadaCamp.setValor(valor);
											String textDomini = null;
											List<String> textDominiIndex = fila.get(codi + VALOR_DOMINI_SUFIX + valor);
											if (textDominiIndex != null)
												textDomini = textDominiIndex.get(0);
											if (textDomini == null)
												textDomini = (valor != null && valor.toString().length() > 0) ? "¿" + valor.toString() + "?" : null;
											dadaCamp.setValorMostrar(
													Camp.getComText(
															camp.getTipus(),
															valor,
															textDomini));
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
						DadaIndexadaDto dadaExpedientId = new DadaIndexadaDto(
								ExpedientCamps.EXPEDIENT_CAMP_ID,
								"expedientId");
						dadaExpedientId.setValorIndex(fila.get(ExpedientCamps.EXPEDIENT_CAMP_ID).get(0));
						mapFila.put(CLAU_EXPEDIENT_ID, dadaExpedientId);
					}
					for (DadaIndexadaDto dada: dadesFila) {
						if (mapFila.containsKey(dada.getReportFieldName())) {
							DadaIndexadaDto dadaMultiple = mapFila.get(dada.getReportFieldName());
							if (!dadaMultiple.isMultiple()) {
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
		}
		return resposta;
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
			Map<String, String> textDominis,
			boolean checkMultiple) {
		if (valor != null) {
			if (checkMultiple && camp.isMultiple()) {
				// System.out.println(">>> Multiple " + camp.getCodi());
				Object[] valors = (Object[])valor;
				for (Object o: valors) {
					addFieldToDocument(
							document,
							definicioProces,
							camp,
							o,
							textDominis,
							false);
				}
				// System.out.println(">>> /Multiple " + camp.getCodi());
			} else if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
				// System.out.println(">>> Registre " + camp.getCodi());
				Object[] valorsMembres = (Object[])valor;
				int index = 0;
				for (CampRegistre campRegistre: camp.getRegistreMembres()) {
					Camp membre = campRegistre.getMembre();
					addFieldToDocument(
							document,
							definicioProces,
							membre,
							valorsMembres[index++],
							textDominis,
							false);
				}
				// System.out.println(">>> /Registre " + camp.getCodi());
			} else {
				String clauIndex = definicioProces.getJbpmKey() + "." + camp.getCodi();
				try {
					String valorIndex = valorIndexPerCamp(camp, valor);
					boolean analyzed = 
						camp.getTipus().equals(TipusCamp.STRING) ||
						camp.getTipus().equals(TipusCamp.TEXTAREA);
					// System.out.println(">>>>>> " + clauIndex + ": " + valorIndex);
					document.add(new Field(
							clauIndex,
							valorIndex,
							Field.Store.YES,
							(analyzed) ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED));
					String textDomini = textDominis.get(camp.getCodi() + "@" + valorIndex);
					if (	textDomini != null &&
							(camp.getTipus().equals(TipusCamp.SELECCIO) || camp.getTipus().equals(TipusCamp.SUGGEST)) &&
							document.get(clauIndex + VALOR_DOMINI_SUFIX + valorIndex) == null) {
							document.add(new Field(
									clauIndex + VALOR_DOMINI_SUFIX + valorIndex,
									textDomini,
									Field.Store.YES,
									Field.Index.ANALYZED));
					}
				} catch (Exception ex) {
					logger.error("No s'ha pogut afegir el camp " + clauIndex + " al document per indexar", ex);
				}
			}
		}
	}
	private String valorIndexPerCamp(Camp camp, Object valor) throws Exception {
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

	private Object valorCampPerIndex(Camp camp, String valor) throws Exception {
		if (camp.getTipus().equals(TipusCamp.INTEGER)) {
			return Long.parseLong(valor.split("\\.")[0]);
		} else if (camp.getTipus().equals(TipusCamp.FLOAT)) {
			return Double.parseDouble(valor);
		} else if (camp.getTipus().equals(TipusCamp.BOOLEAN)) {
			return new Boolean("S".equals(valor));
		} else if (camp.getTipus().equals(TipusCamp.DATE)) {
			return new SimpleDateFormat(PATRO_DATES_INDEX).parse(valor);
		} else if (camp.getTipus().equals(TipusCamp.PRICE)) {
			return numberPerIndexar(new BigDecimal(valor));
		} else if (camp.getTipus().equals(TipusCamp.TERMINI)) {
			String[] parts = valor.split("/");
			Termini term = new Termini();
			term.setAnys(Integer.parseInt(parts[0]));
			term.setMesos(Integer.parseInt(parts[0]));
			term.setDies(Integer.parseInt(parts[0]));
			return term;
		} else if (camp.getTipus().equals(TipusCamp.SELECCIO)) {
			return (String)valor;
		} else if (camp.getTipus().equals(TipusCamp.SUGGEST)) {
			return (String)valor;
		} else {
			return valor;
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
		DateFormat sdf = new SimpleDateFormat(PATRO_DATES_INDEX);
		return sdf.format(data);
	}

	private static Log logger = LogFactory.getLog(LuceneDao.class);

}
