/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	private static final String EXPEDIENT_CAMP_INFOATUR = "expedient.infoatur";
	private static final String EXPEDIENT_CAMP_INICIADOR = "expedient.iniciador";
	private static final String EXPEDIENT_CAMP_RESPONSABLE = "expedient.responsable";
	private static final String EXPEDIENT_CAMP_GEOX = "expedient.geox";
	private static final String EXPEDIENT_CAMP_GEOY = "expedient.geoy";
	private static final String EXPEDIENT_CAMP_GEOREF = "expedient.georef";
	private static final String EXPEDIENT_CAMP_REGNUM = "expedient.regnum";
	private static final String EXPEDIENT_CAMP_REGDATA = "expedient.regdata";
	private static final String EXPEDIENT_CAMP_UNIADM = "expedient.uniadm";
	private static final String EXPEDIENT_CAMP_IDIOMA = "expedient.idioma";
	private static final String EXPEDIENT_CAMP_TRAMIT = "expedient.idioma";
	private static final String EXPEDIENT_CAMP_DATA_INICI = "expedient.dataInici";
	private static final String EXPEDIENT_CAMP_TIPUS = "expedient.tipus";
	private static final String EXPEDIENT_CAMP_ESTAT = "expedient.estat";

	private static final String ESTAT_EXPEDIENT_INICIAT = "H3l1um#estat.iniciat";
	private static final String ESTAT_EXPEDIENT_FINALITZAT = "H3l1um#estat.finalitzat";

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
			Map<String, Map<String, String>> textDominis) {
		checkIndexOk();
		Document document = createDocumentFromExpedient(
				expedient,
				definicionsProces,
				camps,
				valors,
				textDominis);
		getLuceneIndexTemplate().addDocument(document);
	}
	@SuppressWarnings("unchecked")
	public synchronized boolean updateExpedient(
			final Expedient expedient,
			final Map<String, DefinicioProces> definicionsProces,
			final Map<String, Set<Camp>> camps,
			final Map<String, Map<String, Object>> valors,
			final Map<String, Map<String, String>> textDominis) {
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
										valors,
										textDominis);
							}
						});
			} else {
				createExpedient(
						expedient,
						definicionsProces,
						camps,
						valors,
						textDominis);
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
			    		return new Long(document.get(EXPEDIENT_CAMP_ID));
				    }
				});
		return resposta;
	}
	public List<Map<String, DadaIndexadaDto>> findAmbDadesExpedient(
			String tipusCodi,
			List<Camp> filtreCamps,
			Map<String, Object> filtreValors,
			List<Camp> informeCamps) {
		checkIndexOk();
		Query query = queryPerFiltre(
				tipusCodi,
				filtreCamps,
				filtreValors);
		return getDadesExpedientPerConsulta(
				query,
				informeCamps);
	}

	public List<Map<String, DadaIndexadaDto>> getDadesExpedient(
			Expedient expedient,
			List<Camp> informeCamps) {
		checkIndexOk();
		Query query = queryFromCampFiltre(
				EXPEDIENT_CAMP_ID,
				expedient.getId().toString(),
				null);
		return getDadesExpedientPerConsulta(
				query,
				informeCamps);
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
			Map<String, Map<String, String>> textDominis) {
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
	    		(expedient.getIniciadorCodi() != null) ? expedient.getIniciadorCodi() : VALOR_CAMP_BUIT,
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
							textDominis.get(clau),
							true);
				}
			}
		}
		return doc;
	}

	private Query queryPerFiltre(
			String tipusCodi,
			List<Camp> filtreCamps,
			Map<String, Object> filtreValors) {
		BooleanQuery bquery = new BooleanQuery();
		bquery.add(
				new BooleanClause(
						queryFromCampFiltre(
								EXPEDIENT_CAMP_TIPUS,
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
			} else if (EXPEDIENT_CAMP_INFOATUR.equals(codiCamp)) {
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
			} else if (EXPEDIENT_CAMP_GEOX.equals(codiCamp)) {
				return new TermQuery(new Term(
						codiCamp,
						numberPerIndexar((Number)valorFiltre)));
			} else if (EXPEDIENT_CAMP_GEOY.equals(codiCamp)) {
				return new TermQuery(new Term(
						codiCamp,
						numberPerIndexar((Number)valorFiltre)));
			} else if (EXPEDIENT_CAMP_GEOREF.equals(codiCamp)) {
				return new TermQuery(new Term(
						codiCamp,
						(String)valorFiltre));
			} else if (EXPEDIENT_CAMP_REGNUM.equals(codiCamp)) {
				return new TermQuery(new Term(
						codiCamp,
						(String)valorFiltre));
			} else if (EXPEDIENT_CAMP_REGDATA.equals(codiCamp)) {
				return new TermQuery(new Term(
						codiCamp,
						(String)valorFiltre));
			} else if (EXPEDIENT_CAMP_UNIADM.equals(codiCamp)) {
				return new TermQuery(new Term(
						codiCamp,
						(String)valorFiltre));
			} else if (EXPEDIENT_CAMP_IDIOMA.equals(codiCamp)) {
				return new TermQuery(new Term(
						codiCamp,
						(String)valorFiltre));
			} else if (EXPEDIENT_CAMP_TRAMIT.equals(codiCamp)) {
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

	@SuppressWarnings("unchecked")
	private List<Map<String, DadaIndexadaDto>> getDadesExpedientPerConsulta(
			Query query,
			List<Camp> campsInforme) {
		List<Map<String, List<String>>> resultats = searchTemplate.search(
				query,
				new HitExtractor() {
				    public Object mapHit(int id, Document document, float score) {
				    	Map<String, List<String>> valorsDocument = new HashMap<String, List<String>>();
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
			    		return valorsDocument;
				    }
				});
		List<Map<String, DadaIndexadaDto>> resposta = new ArrayList<Map<String, DadaIndexadaDto>>();
		if (resultats.size() > 0) {
			for (Map<String, List<String>> fila: resultats) {
				List<DadaIndexadaDto> dadesFila = new ArrayList<DadaIndexadaDto>();
				for (String codi: fila.keySet()) {
					if (codi.startsWith("expedient")) {
						DadaIndexadaDto dadaCamp = new DadaIndexadaDto(
								codi,
								codi);
						dadaCamp.setMultiple(false);
						dadaCamp.setValor(fila.get(codi));
						dadaCamp.setValorIndex(fila.get(codi).get(0));
						if (!VALOR_CAMP_BUIT.equals(fila.get(codi))) {
							dadaCamp.setValorMostrar(fila.get(codi).get(0));
						}
						dadesFila.add(dadaCamp);
					}
				}
				for (String codi: fila.keySet()) {
					if (!codi.startsWith("expedient")) {
						String[] partsCodi = codi.split("\\.");
						for (Camp camp: campsInforme) {
							if (	partsCodi[0].equals(camp.getDefinicioProces().getJbpmKey()) &&
									partsCodi[1].equals(camp.getCodi())) {
								for (String valorIndex: fila.get(codi)) {
									try {
										Object valor = valorCampPerIndex(camp, valorIndex);
										if (valor != null) {
											DadaIndexadaDto dadaCamp = new DadaIndexadaDto(
													partsCodi[0],
													partsCodi[1],
													camp.getEtiqueta());
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
													camp.getComText(
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
				}
				Map<String, DadaIndexadaDto> mapFila = new HashMap<String, DadaIndexadaDto>();
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
			} else if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
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
