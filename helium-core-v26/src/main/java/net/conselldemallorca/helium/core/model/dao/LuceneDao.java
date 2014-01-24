/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.core.model.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.service.LuceneHelper;
import net.conselldemallorca.helium.core.util.ExpedientCamps;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.springframework.stereotype.Component;
import org.springmodules.lucene.search.core.HitExtractor;

/**
 * Dao per a indexar i consultar expedients emprant lucene
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class LuceneDao extends LuceneHelper {

	public List<Map<String, DadaIndexadaDto>> getDadesExpedient(String entornCodi, Expedient expedient, List<Camp> informeCamps) {
		mesuresTemporalsHelper.mesuraIniciar("Lucene: getDadesExpedient", "lucene", expedient.getTipus().getNom());
		checkIndexOk();
		Query query = queryFromCampFiltre(ExpedientCamps.EXPEDIENT_CAMP_ID, expedient.getId().toString(), null);
		List<Map<String, DadaIndexadaDto>> resultat = getDadesExpedientPerConsulta(entornCodi, query, informeCamps, false, ExpedientCamps.EXPEDIENT_CAMP_ID, true, 0, -1);
		mesuresTemporalsHelper.mesuraCalcular("Lucene: getDadesExpedient", "lucene", expedient.getTipus().getNom());
		return resultat;
	}

	public List<Map<String, DadaIndexadaDto>> findAmbDadesExpedient(String entornCodi, String tipusCodi, List<Camp> filtreCamps, Map<String, Object> filtreValors, List<Camp> informeCamps, String sort, boolean asc, int firstRow, int maxResults) {
		mesuresTemporalsHelper.mesuraIniciar("Lucene: findAmbDadesExpedient", "lucene");
		checkIndexOk();
		Query query = queryPerFiltre(entornCodi, tipusCodi, filtreCamps, filtreValors);
		List<Map<String, DadaIndexadaDto>> resultat = getDadesExpedientPerConsulta(entornCodi, query, informeCamps, true, sort, asc, firstRow, maxResults);
		mesuresTemporalsHelper.mesuraCalcular("Lucene: findAmbDadesExpedient", "lucene");
		return resultat;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, DadaIndexadaDto>> getDadesExpedientPerConsulta(final String entornCodi, Query query, List<Camp> campsInforme, boolean incloureId, String sort, boolean asc, final int firstRow, final int maxResults) {
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
					if (sort.endsWith(camp.getCodi()) && (camp.getTipus().equals(TipusCamp.STRING) || camp.getTipus().equals(TipusCamp.TEXTAREA))) {
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
		List<Map<String, DadaIndexadaDto>> resposta = new ArrayList<Map<String, DadaIndexadaDto>>();
		if (resultats.size() > 0) {
			Set<String> clausAmbValorMultiple = new HashSet<String>();
			for (Map<String, List<String>> fila : resultats) {
				if (fila != null) {
					List<DadaIndexadaDto> dadesFila = new ArrayList<DadaIndexadaDto>();
					for (String codi : fila.keySet()) {
						for (Camp camp : campsInforme) {
							boolean coincideix;
							String[] partsCodi = codi.split("\\.");
							if (codi.startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
								coincideix = codi.equals(camp.getCodi());
							} else {
								coincideix = camp.getDefinicioProces() != null && partsCodi[0].equals(camp.getDefinicioProces().getJbpmKey()) && partsCodi[1].equals(camp.getCodi());
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
}
