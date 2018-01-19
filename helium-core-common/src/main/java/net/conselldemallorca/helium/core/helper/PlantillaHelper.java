/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.core.NonStringException;
import freemarker.ext.beans.ArrayModel;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BooleanModel;
import freemarker.ext.beans.DateModel;
import freemarker.ext.beans.NumberModel;
import freemarker.log.Logger;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.model.hibernate.Area;
import net.conselldemallorca.helium.core.model.hibernate.AreaJbpmId;
import net.conselldemallorca.helium.core.model.hibernate.Carrec;
import net.conselldemallorca.helium.core.model.hibernate.CarrecJbpmId;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Persona;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.NombreEnCastella;
import net.conselldemallorca.helium.core.util.NombreEnCatala;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto.Sexe;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.repository.AreaJbpmIdRepository;
import net.conselldemallorca.helium.v3.core.repository.AreaRepository;
import net.conselldemallorca.helium.v3.core.repository.CarrecJbpmIdRepository;
import net.conselldemallorca.helium.v3.core.repository.CarrecRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.DominiRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateException;
import net.sf.jooreports.templates.DocumentTemplateFactory;

/**
 * Helper per a generar documents mitjançant plantilles fetes amb ODT
 * i Freemarker.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component("PlantillaHelperV3")
public class PlantillaHelper {

	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private DominiHelper dominiHelper;
	@Resource
	private EntornRepository entornRepository;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private AreaRepository areaRepository;
	@Resource
	private AreaJbpmIdRepository areaJbpmIdRepository;
	@Resource
	private CarrecRepository carrecRepository;
	@Resource
	private CarrecJbpmIdRepository carrecJbpmIdRepository;
	@Resource
	private DominiRepository dominiRepository;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private JbpmHelper jbpmhelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private TascaHelper tascaHelper;



	public ArxiuDto generarDocumentPlantilla(
			Expedient expedient,
			Document document,
			String taskInstanceId,
			String processInstanceId,
			Date dataDocument) {
		try {
			deshabilitarLogging();
			byte[] contingut = null;
			if (document.isPlantilla()) {
				// Obtenir tasca, expedient, responsable i omplir el model						
				ExpedientDto expedientDto;
				ExpedientTascaDto tasca = null;
				String responsableCodi;
				Map<String, Object> model = new HashMap<String, Object>();
				afegirDadesProcesAlModel(processInstanceId, model);
				if (taskInstanceId != null) {
					JbpmTask task = jbpmhelper.getTaskById(taskInstanceId);
					expedientDto = expedientHelper.toExpedientDto(expedient);
					tasca = tascaHelper.toExpedientTascaDto(
							task,
							expedient,
							true,
							false);
					afegirDadesTascaAlModel(task, model);
					responsableCodi = task.getAssignee();
				} else {
					expedientDto = expedientHelper.toExpedientDto(expedient);
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
					responsableCodi = auth.getName();
				}
				afegirContextAlModel(
						responsableCodi,
						expedientDto,
						tasca,
						dataDocument,
						model);
				afegirFuncionsAlModel(
						expedient.getEntorn(),
						expedient.getTipus(),
						taskInstanceId,
						processInstanceId,
						model);
				contingut = generarAmbJooReports(
						document.getArxiuContingut(),
						model);
			} else {
				contingut = document.getArxiuContingut();
			}
			return new ArxiuDto(
					getArxiuNomPerDocumentGenerat(
							expedient,
							document),
					contingut);
		} catch (Exception ex) {
			throw SistemaExternException.tractarSistemaExternException(
					expedient.getEntorn().getId(),
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"(DOCUMENT PLANTILLA. Error al generar el document)", 
					ex);
		}
	}



	private void afegirDadesProcesAlModel(
			String processInstanceId,
			Map<String, Object> model) {
		List<ExpedientDadaDto> dades = variableHelper.findDadesPerInstanciaProces(processInstanceId);
		for (ExpedientDadaDto dada: dades) {
			if (dada.isCampMultiple()) {
				if (dada.isCampTipusRegistre()) {
					Object[] multipleTexts = new Object[dada.getMultipleDades().size()];
					int index = 0;
					for (ExpedientDadaDto multipleDada: dada.getMultipleDades()) {
						String[] registreTexts = new String[multipleDada.getRegistreDades().size()];
						int indexreg = 0;
						for (ExpedientDadaDto registreDada: multipleDada.getRegistreDades()) {
							registreTexts[indexreg++] = registreDada.getPlantillaText();
						}
						multipleTexts[index++] = registreTexts;
					}
					model.put(
							dada.getVarCodi(),
							multipleTexts);
				} else {
					String[] multipleTexts = new String[dada.getMultipleDades().size()];
					int index = 0;
					for (ExpedientDadaDto multipleDada: dada.getMultipleDades()) {
						multipleTexts[index++] = multipleDada.getPlantillaText();
					}
					model.put(
							dada.getVarCodi(),
							multipleTexts);
				}
			} else if (dada.isCampTipusRegistre()) {
				String[] registreTexts = new String[dada.getRegistreDades().size()];
				int index = 0;
				for (ExpedientDadaDto registreDada: dada.getRegistreDades()) {
					registreTexts[index++] = registreDada.getPlantillaText();
				}
				model.put(
						dada.getVarCodi(),
						new Object[] {registreTexts});
			} else {
				model.put(
						dada.getVarCodi(),
						dada.getPlantillaText());
			}
		}
	}

	private void afegirDadesTascaAlModel(
			JbpmTask task,
			Map<String, Object> model) {
		List<TascaDadaDto> dades = variableHelper.findDadesPerInstanciaTasca(task);
		for (TascaDadaDto dada: dades) {
			if (dada.isCampMultiple()) {
				if (dada.isCampTipusRegistre()) {
					Object[] multipleTexts = new Object[dada.getMultipleDades().size()];
					int index = 0;
					for (TascaDadaDto multipleDada: dada.getMultipleDades()) {
						String[] registreTexts = new String[multipleDada.getRegistreDades().size()];
						int indexreg = 0;
						for (TascaDadaDto registreDada: multipleDada.getRegistreDades()) {
							registreTexts[indexreg++] = registreDada.getPlantillaText();
						}
						multipleTexts[index++] = registreTexts;
					}
					model.put(
							dada.getVarCodi(),
							multipleTexts);
				} else {
					String[] multipleTexts = new String[dada.getMultipleDades().size()];
					int index = 0;
					for (TascaDadaDto multipleDada: dada.getMultipleDades()) {
						multipleTexts[index++] = multipleDada.getPlantillaText();
					}
					model.put(
							dada.getVarCodi(),
							multipleTexts);
				}
			} else if (dada.isCampTipusRegistre()) {
				String[] registreTexts = new String[dada.getRegistreDades().size()];
				int index = 0;
				for (TascaDadaDto registreDada: dada.getRegistreDades()) {
					registreTexts[index++] = registreDada.getPlantillaText();
				}
				model.put(
						dada.getVarCodi(),
						new Object[] {registreTexts});
			} else {
				model.put(
						dada.getVarCodi(),
						dada.getPlantillaText());
			}
		}
	}

	private void afegirContextAlModel(
			String responsableCodi,
			ExpedientDto expedient,
			ExpedientTascaDto tasca,
			Date dataDocument,
			Map<String, Object> model) {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("responsable", pluginHelper.personaFindAmbCodi(responsableCodi));
		context.put("expedient", expedient);
		context.put("tasca", tasca);
		context.put("dataActual", new Date());
		context.put("dataDocument", dataDocument);
		model.put("context", context);
	}

	@SuppressWarnings("rawtypes")
	private void afegirFuncionsAlModel(
			final Entorn entorn,
			final ExpedientTipus expedientTipus,
			final String taskId,
			final String processInstanceId,
			Map<String, Object> model) {
		model.put(
				"valor",
				new TemplateMethodModel() {
					public TemplateModel exec(List args) throws TemplateModelException {
						if (args.size() == 1) {
							Object arg0 = args.get(0);
							if (arg0 != null && arg0 instanceof String) {
								String codi = (String)arg0;
								Object valor = null;
								if (taskId != null)
									valor = jbpmhelper.getTaskInstanceVariable(taskId, codi);
								if (valor == null)
									valor = jbpmhelper.getProcessInstanceVariable(processInstanceId, codi);
								if (valor == null)
									return new SimpleScalar(null);
								if (valor instanceof Object[])
									return new ArrayModel(valor, new DefaultObjectWrapper());
								else if (valor instanceof String)
									return new SimpleScalar((String)valor);
								else if (valor instanceof Boolean)
									return new BooleanModel((Boolean)valor, new DefaultObjectWrapper());
								else if (valor instanceof Date)
									return new DateModel((Date)valor, new DefaultObjectWrapper());
								else if (valor instanceof BigDecimal)
									return new NumberModel((BigDecimal)valor, new DefaultObjectWrapper());
								else if (valor instanceof DominiCodiDescripcio)
									return new SimpleScalar(((DominiCodiDescripcio)valor).getCodi());
								else
									return new BeanModel(valor, new DefaultObjectWrapper());
							}
						}
						return new SimpleScalar("[Arguments incorrectes]");
					}
				});
		model.put(
				"persona",
				new TemplateMethodModel() {
					public TemplateModel exec(List args) throws TemplateModelException {
						if (args.size() == 1) {
							Object arg0 = args.get(0);
							if (arg0 != null && arg0 instanceof String) {
								String codi = (String)arg0;
								PersonaDto persona = pluginHelper.personaFindAmbCodi(codi);
								if (persona == null)
									return new BeanModel(
											new PersonaDto("???", "???", "???", Sexe.SEXE_HOME),
											new DefaultObjectWrapper());
								else
									return new BeanModel(
											persona,
											new DefaultObjectWrapper());
							}
						}
						return new SimpleScalar("[Arguments incorrectes]");
					}
				});
		model.put(
				"area",
				new TemplateMethodModel() {
					public TemplateModel exec(List args) throws TemplateModelException {
						if (args.size() == 1) {
							Object arg0 = args.get(0);
							if (arg0 != null && arg0 instanceof String) {
								String codi = (String)arg0;
								if (esIdentitySourceHelium()) {
									Area area = areaRepository.findByEntornAndCodi(entorn, codi);
									if (area == null)
										area = new Area("???", "???", new Entorn());
									return new BeanModel(
											area,
											new DefaultObjectWrapper());
								} else {
									AreaJbpmId area = areaJbpmIdRepository.findByCodi(codi);
									if (area == null)
										area = new AreaJbpmId("???", "???");
									return new BeanModel(
											area,
											new DefaultObjectWrapper());
								}
							}
						}
						return new SimpleScalar("[Arguments incorrectes]");
					}
				});
		model.put(
				"carrec",
				new TemplateMethodModel() {
					public TemplateModel exec(List args) throws TemplateModelException {
						if (args.size() == 1 || args.size() == 2 ) {
							Object arg0 = args.get(0);
							if (arg0 != null && arg0 instanceof String) {
								String carrecCodi = (String)arg0;
								if (esIdentitySourceHelium()) {
									if (args.size() == 1) {
										Carrec carrec = carrecRepository.findByEntornAndCodi(entorn, carrecCodi);
										if (carrec == null)
											carrec = new Carrec("???", "???", "???", "???", "???", new Entorn());
										return new BeanModel(
												carrec,
												new DefaultObjectWrapper());
									}
								} else {
									CarrecJbpmId carrec = null;
									if (args.size() == 1) {
										carrec = carrecJbpmIdRepository.findByCodi(carrecCodi);
									} else {
										Object arg1 = args.get(1);
										String areaCodi = (String)arg1;
										carrec = carrecJbpmIdRepository.findByCodiAndGrup(carrecCodi, areaCodi);
									}
									if (carrec == null)
										carrec = new CarrecJbpmId("???", "???", "???", "???", "???", Persona.Sexe.SEXE_HOME);
									return new BeanModel(
											carrec,
											new DefaultObjectWrapper());
								}
							}
						}
						return new SimpleScalar("[Arguments incorrectes]");
					}
				});
		model.put(
				"personaAmbCarrecArea",
				new TemplateMethodModel() {
					public TemplateModel exec(List args) throws TemplateModelException {
						if (args.size() == 2) {
							Object arg0 = args.get(0);
							Object arg1 = args.get(1);
							if (arg0 != null && arg0 instanceof String && arg1 != null && arg1 instanceof String) {
								String codiCarrec = (String)arg0;
								String codiArea = (String)arg1;
								if (esIdentitySourceHelium()) {
									Carrec carrec = carrecRepository.findByEntornAndAreaAndCodi(
											entorn,
											areaRepository.findByEntornAndCodi(entorn, codiArea),
											codiCarrec);
									if (carrec != null) {
										if (carrec.getPersonaCodi() != null) {
											PersonaDto persona = pluginHelper.personaFindAmbCodi(carrec.getPersonaCodi());
											return new BeanModel(
													persona,
													new DefaultObjectWrapper());
										}
									}
								} else {
									List<String> personaCodi = carrecJbpmIdRepository.findPersonaCodiByGrupCodiAndCarrecCodi(
											codiArea,
											codiCarrec);
									if (personaCodi != null && !personaCodi.isEmpty()) {
										PersonaDto persona = pluginHelper.personaFindAmbCodi(personaCodi.get(0));
										if (persona != null)
											return new BeanModel(
													persona,
													new DefaultObjectWrapper());
									}
								}
								return new BeanModel(
										new PersonaDto("???", "???", "???", Sexe.SEXE_HOME),
										new DefaultObjectWrapper());
							}
						}
						return new SimpleScalar("[Arguments incorrectes]");
					}
				});
		model.put(
				"carrecsAmbPersonaArea",
				new TemplateMethodModel() {
					public TemplateModel exec(List args) throws TemplateModelException {
						if (args.size() == 2) {
							Object arg0 = args.get(0);
							Object arg1 = args.get(1);
							if (arg0 != null && arg0 instanceof String && arg1 != null && arg1 instanceof String) {
								String codiPersona = (String)arg0;
								String codiArea = (String)arg1;
								if (esIdentitySourceHelium()) {
									Area area = areaRepository.findByEntornAndCodi(entorn, codiArea);
									List<Carrec> carrecs = new ArrayList<Carrec>();
									for (Carrec car: area.getCarrecs()) {
										if (codiPersona.equals(car.getPersonaCodi()))
											carrecs.add(car);
									}
									if (carrecs.size() > 0) {
										Carrec[] array = new Carrec[carrecs.size()];
										for (int i = 0; i < carrecs.size(); i++)
											array[i] = carrecs.get(i);
										return new ArrayModel(
												array,
												new DefaultObjectWrapper());
									} else {
										return new ArrayModel(
												new Carrec[0],
												new DefaultObjectWrapper());
									}
								} else {
									List<String> carrecCodis = carrecJbpmIdRepository.findCarrecsCodiByPersonaCodiAndGrupCodi(
											codiPersona,
											codiArea);
									if (carrecCodis != null && carrecCodis.size() > 0) {
										CarrecJbpmId[] array = new CarrecJbpmId[carrecCodis.size()];
										for (int i = 0; i < carrecCodis.size(); i++) {
											CarrecJbpmId carrec = carrecJbpmIdRepository.findByCodiAndGrup(carrecCodis.get(i), codiArea);
											if (carrec == null)
												carrec = new CarrecJbpmId("???", "???", "???", "???", "???", Persona.Sexe.SEXE_HOME);
											array[i] = carrec;
										}
										return new ArrayModel(
												array,
												new DefaultObjectWrapper());
									} else {
										return new ArrayModel(
												new CarrecJbpmId[0],
												new DefaultObjectWrapper());
									}
								}
							}
						}
						return new SimpleScalar("[Arguments incorrectes]");
					}
				});
		/* Per suprimir */
		model.put(
				"personaAmbCarrec",
				new TemplateMethodModel() {
					public TemplateModel exec(List args) throws TemplateModelException {
						if (args.size() == 1) {
							Object arg0 = args.get(0);
							if (arg0 != null && arg0 instanceof String) {
								String codi = (String)arg0;
								if (esIdentitySourceHelium()) {
									Carrec carrec = carrecRepository.findByEntornAndCodi(entorn, codi);
									if (carrec != null) {
										if (carrec.getPersonaCodi() != null) {
											PersonaDto persona = pluginHelper.personaFindAmbCodi(carrec.getPersonaCodi());
											return new BeanModel(
													persona,
													new DefaultObjectWrapper());
										}
									}
								} else {
									CarrecJbpmId carrec = carrecJbpmIdRepository.findByCodi(codi);
									if (carrec != null) {
										List<String> persones = carrecJbpmIdRepository.findPersonesCodiByCarrecCodi(codi);
										if (persones != null && persones.size() > 0) {
											PersonaDto persona = pluginHelper.personaFindAmbCodi(persones.get(0));
											if (persona == null)
												persona = new PersonaDto("???", "???", "???", Sexe.SEXE_HOME);
											return new BeanModel(
													persona,
													new DefaultObjectWrapper());
										}
									}
								}
								return new BeanModel(
										new PersonaDto("???", "???", "???", Sexe.SEXE_HOME),
										new DefaultObjectWrapper());
							}
						}
						return new SimpleScalar("[Arguments incorrectes]");
					}
				});
		model.put(
				"importEnLletres",
				new TemplateMethodModelEx() {
					public TemplateModel exec(List args) throws TemplateModelException {
						if (args.size() == 3) {
							Object arg0 = args.get(0); // quantitat
							Object arg1 = args.get(1); // moneda
							Object arg2 = args.get(2); // idioma
							BigDecimal numbd;
							if (arg0 instanceof SimpleScalar) {
								String strnum = ((SimpleScalar)arg0).getAsString();
								BigDecimal b = new BigDecimal(strnum);
								numbd = b;
							} else {
								Number num = ((SimpleNumber)args.get(0)).getAsNumber();
								numbd = (BigDecimal)num;
							}
							String idioma = arg2.toString();
							if ("ca".equals(idioma)) {
								String moneda = arg1.toString();
								int valorMoneda;
								if ("EUR".equals(moneda)) {
									valorMoneda = NombreEnCatala.MONEDA_EURO;
								} else if ("ESP".equals(moneda)) {
									valorMoneda = NombreEnCatala.MONEDA_PESETA;
								} else if ("USD".equals(moneda)) {
									valorMoneda = NombreEnCatala.MONEDA_DOLAR;
								} else {
									return new SimpleScalar("[Moneda no suportada]");
								}
								String text = NombreEnCatala.escriurePreu(
										numbd,
										2,
										valorMoneda);
								return new SimpleScalar(text);
							} else if ("es".equals(idioma)) {
								String moneda = arg1.toString();
								int valorMoneda;
								if ("EUR".equals(moneda)) {
									valorMoneda = NombreEnCastella.MONEDA_EURO;
								} else if ("ESP".equals(moneda)) {
									valorMoneda = NombreEnCastella.MONEDA_PESETA;
								} else if ("USD".equals(moneda)) {
									valorMoneda = NombreEnCastella.MONEDA_DOLAR;
								} else {
									return new SimpleScalar("[Moneda no suportada]");
								}
								String text = NombreEnCastella.escriurePreu(
										numbd,
										2,
										valorMoneda);
								return new SimpleScalar(text);
							} else {
								return new SimpleScalar("[Idioma no suportat]");
							}
						}
						return new SimpleScalar("[Nombre d'arguments incorrecte]");
					}
				});
		model.put(
				"consultaDomini",
				new TemplateMethodModel() {
					public TemplateModel exec(List args) throws TemplateModelException {
						if (args.size() >= 1) {
							Object arg0 = args.get(0);
							String arg1 = "";
							if ((arg0 != null) && (arg0 instanceof String)) {
								String codi = (String)arg0;
								Domini domini = dominiRepository.findByExpedientTipusAndCodi(
										expedientTipus, 
										codi);
								if (domini == null)
									domini = dominiRepository.findByEntornAndCodi(
										entorn,
										codi);
								if (domini != null) {
									try {
										Map<String, Object> parametres = new HashMap<String, Object>();
										if (args.size() > 1) {
											arg1 = args.get(1).toString();
											if (args.size() > 2) {
												int i = 2;
												for (int n = 0; n < ((args.size()-2)/2); n++) {
													String cod = args.get(i++).toString();
													Object val = args.get(i++);
													parametres.put(cod, val);
												}
											}
										}
										List<FilaResultat> files = dominiHelper.consultar(
												domini,
												arg1,
												parametres);
										Object[] resultat = new Object[files.size()];
										for (int i = 0; i < resultat.length; i++) {
											Object[] columnes = new Object[files.get(i).getColumnes().size()];
											for (int j = 0; j < columnes.length; j++) {
												Object[] parella = new Object[2];
												parella[0] = files.get(i).getColumnes().get(j).getCodi();
												parella[1] = files.get(i).getColumnes().get(j).getValor();
												columnes[j] = parella;
											}
											resultat[i] = columnes;
										}
										return new ArrayModel(
												resultat,
												new DefaultObjectWrapper());
									} catch (Exception e) {
										throw new TemplateModelException(e);
									}
								}
							}
						}
						return new SimpleScalar("[Arguments incorrectes]");
					}
				});
		model.put(
				"documentInfo",
				new TemplateMethodModel() {
					public TemplateModel exec(List args) throws TemplateModelException {
						if (args.size() >= 1) {
							Object arg0 = args.get(0);
							if ((arg0 != null) && (arg0 instanceof String)) {
								String codi = (String)arg0;
								List<DocumentStore> documents = documentStoreRepository.findByProcessInstanceId(processInstanceId);
								DocumentDto resposta = null;
								for (int i = 0; i < documents.size(); i++) {
									if (documents.get(i).getJbpmVariable().equals(JbpmVars.PREFIX_DOCUMENT + codi))
										resposta = documentHelper.getDocumentSenseContingut(documents.get(i).getId());
								}
								return new BeanModel(
										resposta,
										new DefaultObjectWrapper());
							}
						}
						return new SimpleScalar("[Arguments incorrectes]");
					}
				});
		model.put(
				"documentsProces",
				new TemplateMethodModel() {
					public TemplateModel exec(List args) throws TemplateModelException {
						if (args.size() <= 1) {
							String pid = processInstanceId;
							if (args.size() == 1) {
								Object arg0 = args.get(0);
								if (arg0 != null && arg0 instanceof String)
									pid = (String)arg0;
							}
							List<DocumentStore> documents = documentStoreRepository.findByProcessInstanceId(pid);
							DocumentDto[] resposta = new DocumentDto[documents.size()];
							for (int i = 0; i < resposta.length; i++) {
								resposta[i] = documentHelper.getDocumentSenseContingut(documents.get(i).getId());
							}
							return new ArrayModel(
									resposta,
									new DefaultObjectWrapper());
						}
						return new SimpleScalar("[Arguments incorrectes]");
					}
				});
	}

	private byte[] generarAmbJooReports(
			byte[] plantillaContingut,
			Map<String, Object> model) throws IOException, DocumentTemplateException {
		DocumentTemplateFactory documentTemplateFactory = new DocumentTemplateFactory();
		documentTemplateFactory.getFreemarkerConfiguration().setTemplateExceptionHandler(new TemplateExceptionHandler() {
		    public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
		        try {
		        	if (te instanceof TemplateModelException || te instanceof NonStringException) {
		        		out.write("[exception]");
		        	} else {
		        		out.write("[???]");
		        	}
		        	out.write("<office:annotation><dc:creator>Helium</dc:creator><dc:date>" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()) + "</dc:date><text:p><![CDATA[" + te.getFTLInstructionStack() + "\n" + te.getMessage() + "]]></text:p></office:annotation>");
		        } catch (IOException e) {
		            throw new TemplateException("Failed to print error message. Cause: " + e, env);
		        }
		    }
		});
		documentTemplateFactory.getFreemarkerConfiguration().setLocale(new Locale("ca", "ES"));
		DocumentTemplate template = documentTemplateFactory.getTemplate(
				new ByteArrayInputStream(plantillaContingut));
		ByteArrayOutputStream resultat = new ByteArrayOutputStream();
		template.setContentWrapper(new DocumentTemplate.ContentWrapper() {
			public String wrapContent(String content) {
				return "[#ftl]\n"
						+ "[#escape any as any?xml?replace(\"[\\n|\\r|\\r\\n|\\n\\r]\",\"</text:p> <text:p>\")]\n"
						+ content
						+ "[/#escape]";
			}
		});
		template.createDocument(model, resultat);
		return resultat.toByteArray();
	}

	private void deshabilitarLogging() throws ClassNotFoundException {
		Logger.selectLoggerLibrary(Logger.LIBRARY_NONE);
	}

	private boolean esIdentitySourceHelium() {
		String identitySource = GlobalProperties.getInstance().getProperty("app.jbpm.identity.source");
		return (identitySource.equalsIgnoreCase("helium"));
	}

	private String getArxiuNomPerDocumentGenerat(
			Expedient expedient,
			Document document) {
		String numExpedient = "";
		if (expedient.getNumero() != null) {
			numExpedient = expedient.getNumero() + "_";
		}
		String titol = numExpedient + document.getNom() + ".odt";
		String carRemp = "\\/:*?\"<>|";
		titol.replaceAll(carRemp, "_");		
		return titol;
	}

}
