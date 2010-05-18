/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.integracio.plugins.persones.Persona.Sexe;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.hibernate.Area;
import net.conselldemallorca.helium.model.hibernate.Carrec;
import net.conselldemallorca.helium.model.hibernate.CarrecJbpmId;
import net.conselldemallorca.helium.model.hibernate.Document;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.util.GlobalProperties;
import net.conselldemallorca.helium.util.NombreEnCastella;
import net.conselldemallorca.helium.util.NombreEnCatala;
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import freemarker.core.Environment;
import freemarker.core.NonStringException;
import freemarker.ext.beans.ArrayModel;
import freemarker.ext.beans.BeanModel;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Dao per a la generació de plantilles
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class PlantillaDocumentDao {

	private PluginPersonaDao pluginPersonaDao;
	private CarrecDao carrecDao;
	private CarrecJbpmIdDao carrecJbpmIdDao;
	private AreaDao areaDao;
	private JbpmDao jbpmDao;



	public byte[] generarDocumentAmbPlantilla(
			Long entornId,
			Document document,
			String responsableCodi,
			ExpedientDto expedient,
			String processInstanceId,
			TascaDto tasca,
			Date dataDocument,
			Map<String, Object> model) throws Exception {
		afegirContextAlModel(
				responsableCodi,
				expedient,
				tasca,
				dataDocument,
				model);
		afegirFuncionsAlModel(
				entornId,
				tasca.getId(),
				processInstanceId,
				model);
		DocumentTemplateFactory documentTemplateFactory = new DocumentTemplateFactory();
		documentTemplateFactory.getFreemarkerConfiguration().setTemplateExceptionHandler(new TemplateExceptionHandler() {
		    public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
		        try {
		        	if (te instanceof TemplateModelException || te instanceof NonStringException)
		        		out.write("[exception]");
		        	else
		        		out.write("[???]");
		        } catch (IOException e) {
		            throw new TemplateException("Failed to print error message. Cause: " + e, env);
		        }
		    }
		});
		documentTemplateFactory.getFreemarkerConfiguration().setLocale(new Locale("ca", "ES"));
		DocumentTemplate template = documentTemplateFactory.getTemplate(
				new ByteArrayInputStream(document.getArxiuContingut()));
		ByteArrayOutputStream resultat = new ByteArrayOutputStream();
		template.createDocument(model, resultat);
		return resultat.toByteArray();
	}



	@Autowired
	public void setPluginPersonaDao(PluginPersonaDao pluginPersonaDao) {
		this.pluginPersonaDao = pluginPersonaDao;
	}
	@Autowired
	public void setCarrecDao(CarrecDao carrecDao) {
		this.carrecDao = carrecDao;
	}
	@Autowired
	public void setCarrecJbpmIdDao(CarrecJbpmIdDao carrecJbpmIdDao) {
		this.carrecJbpmIdDao = carrecJbpmIdDao;
	}
	@Autowired
	public void setAreaDao(AreaDao areaDao) {
		this.areaDao = areaDao;
	}
	@Autowired
	public void setJbpmDao(JbpmDao jbpmDao) {
		this.jbpmDao = jbpmDao;
	}



	private void afegirContextAlModel(
			String responsableCodi,
			ExpedientDto expedient,
			TascaDto tasca,
			Date dataDocument,
			Map<String, Object> model) {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("responsable", pluginPersonaDao.findAmbCodiPlugin(responsableCodi));
		context.put("expedient", expedient);
		context.put("tasca", tasca);
		context.put("dataActual", new Date());
		context.put("dataDocument", dataDocument);
		model.put("context", context);
	}
	@SuppressWarnings("unchecked")
	private void afegirFuncionsAlModel(
			final Long entornId,
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
								//System.out.println(">>> valor " + codi);
								Object valor = jbpmDao.getTaskInstanceVariable(taskId, codi);
								if (valor == null)
									valor = jbpmDao.getProcessInstanceVariable(processInstanceId, codi);
								if (valor == null)
									return new SimpleScalar(null);
								if (valor instanceof Object[])
									return new ArrayModel(valor, new DefaultObjectWrapper());
								else if (valor instanceof String)
									return new SimpleScalar((String)valor);
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
								//System.out.println(">>> persona " + codi);
								Persona persona = pluginPersonaDao.findAmbCodiPlugin(codi);
								if (persona == null)
									return new BeanModel(
											new Persona("???", "???", "???", Sexe.SEXE_HOME),
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
				"personaAmbCarrec",
				new TemplateMethodModel() {
					public TemplateModel exec(List args) throws TemplateModelException {
						if (args.size() == 1) {
							Object arg0 = args.get(0);
							if (arg0 != null && arg0 instanceof String) {
								String codi = (String)arg0;
								//System.out.println(">>> personaAmbCarrec " + codi);
								if (esIdentitySourceHelium()) {
									Carrec carrec = carrecDao.findAmbEntornICodi(entornId, codi);
									if (carrec != null) {
										if (carrec.getPersonaCodi() != null) {
											Persona persona = pluginPersonaDao.findAmbCodiPlugin(carrec.getPersonaCodi());
											return new BeanModel(
													persona,
													new DefaultObjectWrapper());
										}
									}
								} else {
									CarrecJbpmId carrec = carrecJbpmIdDao.findAmbCodi(codi);
									if (carrec != null) {
										List<String> persones = carrecJbpmIdDao.findPersonesAmbCarrecCodi(codi);
										if (persones != null && persones.size() > 0) {
											Persona persona = pluginPersonaDao.findAmbCodiPlugin(persones.get(0));
											if (persona == null)
												persona = new Persona("???", "???", "???", Sexe.SEXE_HOME);
											return new BeanModel(
													persona,
													new DefaultObjectWrapper());
										}
									}
								}
								return new BeanModel(
										new Persona("???", "???", "???", Sexe.SEXE_HOME),
										new DefaultObjectWrapper());
							}
						}
						return new SimpleScalar("[Arguments incorrectes]");
					}
				});
		model.put(
				"carrec",
				new TemplateMethodModel() {
					public TemplateModel exec(List args) throws TemplateModelException {
						if (args.size() == 1) {
							Object arg0 = args.get(0);
							if (arg0 != null && arg0 instanceof String) {
								String codi = (String)arg0;
								//System.out.println(">>> carrec " + codi);
								if (esIdentitySourceHelium()) {
									Carrec carrec = carrecDao.findAmbEntornICodi(entornId, codi);
									if (carrec == null)
										carrec = new Carrec("???", "???", "???", "???", "???", new Entorn());
									return new BeanModel(
											carrec,
											new DefaultObjectWrapper());
								} else {
									CarrecJbpmId carrec = carrecJbpmIdDao.findAmbCodi(codi);
									if (carrec == null)
										carrec = new CarrecJbpmId("???", "???", "???", "???", "???", net.conselldemallorca.helium.model.hibernate.Persona.Sexe.SEXE_HOME);
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
				"carrecsAmbPersonaArea",
				new TemplateMethodModel() {
					public TemplateModel exec(List args) throws TemplateModelException {
						if (args.size() == 2) {
							Object arg0 = args.get(0);
							Object arg1 = args.get(1);
							if (arg0 != null && arg0 instanceof String && arg1 != null && arg1 instanceof String) {
								String codiPersona = (String)arg0;
								String codiArea = (String)arg1;
								//System.out.println(">>> carrecsAmbPersonaArea " + codiPersona + " " + codiArea);
								if (esIdentitySourceHelium()) {
									Area area = areaDao.findAmbEntornICodi(entornId, codiArea);
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
									List<String> carrecCodis = carrecJbpmIdDao.findCarrecsCodiAmbPersonaArea(
											codiPersona,
											codiArea);
									if (carrecCodis != null && carrecCodis.size() > 0) {
										CarrecJbpmId[] array = new CarrecJbpmId[carrecCodis.size()];
										for (int i = 0; i < carrecCodis.size(); i++) {
											CarrecJbpmId carrec = carrecJbpmIdDao.findAmbCodi(carrecCodis.get(i));
											if (carrec == null)
												carrec = new CarrecJbpmId("???", "???", "???", "???", "???", net.conselldemallorca.helium.model.hibernate.Persona.Sexe.SEXE_HOME);
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
	}

	private boolean esIdentitySourceHelium() {
		String identitySource = GlobalProperties.getInstance().getProperty("app.jbpm.identity.source");
		return (identitySource.equalsIgnoreCase("helium"));
	}

}
