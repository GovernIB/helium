package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.v3.core.api.dto.AreaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiRespostaFilaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.FestiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.exception.AreaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.DominiNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EnumeracioNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EstatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.PersonaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TerminiIniciatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TerminiNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.ConfigService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.OrganitzacioService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;

/**
 * Classe que fa de pont entre jBPM i Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public final class Jbpm3HeliumBridge {

	private static final Jbpm3HeliumBridge INSTANCE = new Jbpm3HeliumBridge();

	private Jbpm3HeliumBridge() {
		if (INSTANCE != null) {
			throw new IllegalStateException("Ja hi ha una instància creada");
		}
	}
	public static Jbpm3HeliumBridge getInstance() {
		return INSTANCE;
	}

	private ExpedientService expedientService;
	private DocumentService documentService;
	private PluginService pluginService;
	private DissenyService dissenyService;
	private TerminiService terminiService;
	private OrganitzacioService organitzacioService;
	private ConfigService configService;
	private TascaService tascaService;

	private boolean configured = false;



	public EntornDto getEntornActual() {
		return getConfigService().getEntornActual();
	}
	public ExpedientDto getExpedientIniciant() {
		return getExpedientService().getExpedientIniciant();
	}
	public String getUsuariCodiActual() {
		return getConfigService().getUsuariCodiActual();
	}

	public ExpedientDto findExpedientAmbEntornITipusINumero(
			Long entornId,
			String expedientTipusCodi,
			String numero) throws EntornNotFoundException, ExpedientTipusNotFoundException {
		return getExpedientService().findAmbEntornITipusINumero(
				entornId,
				expedientTipusCodi,
				numero);
	}
	public DefinicioProcesDto getDefinicioProcesAmbJbpmKeyIVersio(
			String jbpmKey,
			int version) {
		return getDissenyService().getDefinicioProcesAmbJbpmKeyIVersio(
				jbpmKey,
				version);
	}
	public DefinicioProcesDto getDarreraVersioAmbEntornIJbpmKey(
			Long entornId,
			String jbpmKey) throws EntornNotFoundException {
		return getDissenyService().getDarreraVersioAmbEntornIJbpmKey(
				entornId,
				jbpmKey);
	}
	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(
			String processInstanceId) {
		return getDissenyService().getDefinicioProcesPerProcessInstanceId(
				processInstanceId);
	}
	public PersonaDto getPersonaAmbCodi(String codi) {
		return getConfigService().getPersonaAmbCodi(codi);
	}
	public AreaDto getAreaAmbEntornICodi(
			Long entornId,
			String codi) throws EntornNotFoundException {
		return getOrganitzacioService().getAreaAmbEntornICodi(entornId, codi);
	}
	public CarrecDto getCarrecAmbEntornIAreaICodi(
			Long entornId,
			String areaCodi,
			String carrecCodi) throws EntornNotFoundException, AreaNotFoundException {
		return getOrganitzacioService().getCarrecAmbEntornIAreaICodi(
				entornId,
				areaCodi,
				carrecCodi);
	}
	public List<FestiuDto> findFestiusAll() {
		return getConfigService().findFestiusAll();
	}
	public ReassignacioDto findReassignacioActivaPerUsuariOrigen(
			String usuariCodi) throws PersonaNotFoundException {
		return getConfigService().findReassignacioActivaPerUsuariOrigen(usuariCodi);
	}
	public void alertaEsborrarAmbTaskInstanceId(
			long taskInstanceId) throws TaskInstanceNotFoundException {
		getExpedientService().alertaEsborrarAmbTaskInstanceId(taskInstanceId);
	}
	public void expedientModificarEstat(
			String processInstanceId,
			String estatCodi) throws ExpedientNotFoundException, EstatNotFoundException {
		ExpedientDto expedient = getExpedientService().findAmbProcessInstanceId(processInstanceId);
		getExpedientService().modificar(
				expedient.getId(),
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getCodi() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi(),
				true);
	}
	public void expedientModificarComentari(
			String processInstanceId,
			String comentari) throws ExpedientNotFoundException {
		ExpedientDto expedient = getExpedientService().findAmbProcessInstanceId(processInstanceId);
		getExpedientService().modificar(
				expedient.getId(),
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				comentari,
				(expedient.getEstat() != null) ? expedient.getEstat().getCodi() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi(),
				true);
	}
	public void expedientModificarGeoref(
			String processInstanceId,
			Double posx,
			Double posy,
			String referencia) throws ExpedientNotFoundException {
		ExpedientDto expedient = getExpedientService().findAmbProcessInstanceId(processInstanceId);
		getExpedientService().modificar(
				expedient.getId(),
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getCodi() : null,
				posx,
				posy,
				referencia,
				expedient.getGrupCodi(),
				true);
	}
	public void expedientModificarGrup(
			String processInstanceId,
			String grupCodi) throws ExpedientNotFoundException {
		ExpedientDto expedient = getExpedientService().findAmbProcessInstanceId(processInstanceId);
		getExpedientService().modificar(
				expedient.getId(),
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getCodi() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				grupCodi,
				true);
	}
	public void expedientModificarNumero(
			String processInstanceId,
			String numero) throws ExpedientNotFoundException {
		ExpedientDto expedient = getExpedientService().findAmbProcessInstanceId(processInstanceId);
		getExpedientService().modificar(
				expedient.getId(),
				numero,
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getCodi() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi(),
				true);
	}
	public void expedientModificarResponsable(
			String processInstanceId,
			String responsableCodi) throws ExpedientNotFoundException, PersonaNotFoundException {
		ExpedientDto expedient = getExpedientService().findAmbProcessInstanceId(processInstanceId);
		getExpedientService().modificar(
				expedient.getId(),
				expedient.getNumero(),
				expedient.getTitol(),
				responsableCodi,
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getCodi() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi(),
				true);
	}
	public void expedientModificarTitol(
			String processInstanceId,
			String titol) throws ExpedientNotFoundException {
		ExpedientDto expedient = getExpedientService().findAmbProcessInstanceId(processInstanceId);
		getExpedientService().modificar(
				expedient.getId(),
				expedient.getNumero(),
				titol,
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getCodi() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi(),
				true);
	}
	public void expedientAturar(
			Long expedientId,
			String motiu) throws ExpedientNotFoundException {
		getExpedientService().aturar(expedientId, motiu);
	}
	public void expedientReprendre(Long expedientId) throws ExpedientNotFoundException {
		getExpedientService().reprendre(expedientId);
	}
	public void expedientLuceneIndexUpdate(String processInstanceId) throws ExpedientNotFoundException {
		ExpedientDto expedient = getExpedientService().findAmbProcessInstanceId(processInstanceId);
		getExpedientService().luceneIndexUpdate(expedient.getId());
	}

	public void documentGenerarAmbPlantilla(
			String processInstanceId,
			String documentCodi,
			Date dataDocument) {
		getDocumentService().generarAmbPlantilla(
				null,
				processInstanceId,
				documentCodi,
				dataDocument,
				true);
	}

	public TerminiIniciatDto getTerminiIniciat(
			String processInstanceId,
			String terminiCodi) throws TerminiNotFoundException {
		return getTerminiService().findIniciatAmbCodiIProcessInstance(
				terminiCodi,
				processInstanceId);
	}
	public void configurarTerminiIniciatAmbDadesJbpm(
			Long terminiIniciatId,
			String taskInstanceId,
			Long timerId) throws TerminiIniciatNotFoundException {
		getTerminiService().configurarIniciatAmbDadesJbpm(
				terminiIniciatId,
				taskInstanceId,
				timerId);
	}

	public List<DominiRespostaFilaDto> dominiConsultar(
			String processInstanceId,
			String dominiCodi,
			String dominiId,
			Map<String, Object> parametres) throws DominiNotFoundException {
		return getExpedientService().dominiConsultar(
				processInstanceId,
				dominiCodi,
				dominiId,
				parametres);
	}

	public List<EnumeracioValorDto> enumeracioConsultar(
			String processInstanceId,
			String enumeracioCodi) throws EnumeracioNotFoundException {
		return getExpedientService().enumeracioConsultar(
				processInstanceId,
				enumeracioCodi);
	}

	public String getHeliumProperty(String propertyName) {
		return getConfigService().getHeliumProperty(propertyName);
	}

	public boolean isHeliumAssignmentActive() {
		String organigramaActiu = getHeliumProperty("app.jbpm.identity.source");
		return "helium".equalsIgnoreCase(organigramaActiu);
	}

	public void configServices(
			ExpedientService expedientService,
			DocumentService documentService,
			PluginService pluginService,
			DissenyService dissenyService,
			TerminiService terminiService,
			OrganitzacioService organitzacioService,
			ConfigService configService,
			TascaService tascaService) {
		this.expedientService = expedientService;
		this.documentService = documentService;
		this.pluginService = pluginService;
		this.dissenyService = dissenyService;
		this.terminiService = terminiService;
		this.organitzacioService = organitzacioService;
		this.configService = configService;
		this.tascaService = tascaService;
		configured = true;
	}

	public ExpedientService getExpedientService() {
		return expedientService;
	}
	public DocumentService getDocumentService() {
		return documentService;
	}
	public PluginService getPluginService() {
		return pluginService;
	}
	public DissenyService getDissenyService() {
		return dissenyService;
	}
	public TerminiService getTerminiService() {
		return terminiService;
	}
	public OrganitzacioService getOrganitzacioService() {
		return organitzacioService;
	}
	public ConfigService getConfigService() {
		return configService;
	}
	public TascaService getTascaService() {
		return tascaService;
	}

	public boolean isConfigured() {
		return configured;
	}

}
