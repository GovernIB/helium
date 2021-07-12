/**
 * 
 */
package es.caib.helium.logic.helper;

import es.caib.helium.logic.intf.dto.FormulariExternDto;
import es.caib.helium.logic.intf.extern.formulari.IniciFormulari;
import es.caib.helium.logic.intf.extern.formulari.ParellaCodiValor;
import es.caib.helium.logic.intf.extern.formulari.RespostaIniciFormulari;
import es.caib.helium.logic.intf.util.JbpmVars;
import es.caib.helium.logic.util.GlobalPropertiesImpl;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.FormulariExtern;
import es.caib.helium.persist.entity.Tasca;
import es.caib.helium.persist.repository.FormulariExternRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Helper per a accedir a la funcionalitat dels plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class FormulariExternHelper {

//	@Resource
//	private ExpedientTipusHelper expedientTipusHelper;
//	@Resource
//	private TascaHelper tascaHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private FormulariExternRepository formulariExternRepository;
	@Autowired
	private WsClientHelper wsClientHelper;
	@Resource
	private MessageServiceHelper messageHelper;

	public FormulariExternDto iniciar(
		String taskId,
		Tasca tasca,
		ExpedientTipus expedientTipus,
		boolean tascaIniciExpedient) {
		FormulariExternDto dto = new FormulariExternDto();
		String url;
		String username = null;
		String password = null;
		if (expedientTipus.getFormextUrl() != null) {
			url = expedientTipus.getFormextUrl();
			if (expedientTipus.getFormextUsuari() != null)
				username = expedientTipus.getFormextUsuari();
			if (expedientTipus.getFormextContrasenya() != null)
				password = expedientTipus.getFormextContrasenya();
		} else {
			url = GlobalPropertiesImpl.getInstance().getProperty(
					"app.forms.service.url");
			username = GlobalPropertiesImpl.getInstance().getProperty(
					"app.forms.service.username");
			password = GlobalPropertiesImpl.getInstance().getProperty(
					"app.forms.service.password");
		}
		
//		RespostaIniciFormulari resposta = new RespostaIniciFormulari();
//		resposta.setFormulariId(taskId);
//		resposta.setUrl("http://oficina.limit.es");
//		resposta.setWidth(800);
//		resposta.setHeight(600);

		RespostaIniciFormulari resposta = null;
		try {
			IniciFormulari clientWs = wsClientHelper.getIniciFormulariService(
					url,
					WsClientHelper.WsClientAuth.NONE,
					username,
					password);
			resposta = clientWs.iniciFormulari(
					tasca.getFormExtern(),
					taskId,
					(!tascaIniciExpedient) ? getVariablesPerIniciFormulari(taskId) : null);
		
		} catch (Exception e) {
			logger.error("Error iniciant el formulari extern ( " +
					"taskId=" + taskId  +
					", tascaId=" + tasca.getId()  +
					", expedientTipusId=" + expedientTipus.getId()  +
					", tascaIniciExpedient=" + tascaIniciExpedient  + ")");
			dto.setError(messageHelper.getMessage("error.tascaService.formExtern.inici", new Object[] {getRootCause(e)}));
			return dto;
		}
		
		FormulariExtern fext = formulariExternRepository.findByFormulariId(resposta.getFormulariId());
		if (fext == null) {
			fext = new FormulariExtern(
					taskId,
					resposta.getFormulariId(),
					resposta.getUrl());
			formulariExternRepository.save(fext);
		} else {
			fext.setUrl(resposta.getUrl());
			fext.setDataDarreraPeticio(new Date());
			if (resposta.getWidth() != -1)
				fext.setFormWidth(resposta.getWidth());
			if (resposta.getHeight() != -1)
				fext.setFormHeight(resposta.getHeight());
		}
		dto.setFormulariId(resposta.getFormulariId());
		dto.setUrl(resposta.getUrl());
		dto.setWidth(resposta.getWidth());
		dto.setHeight(resposta.getHeight());
		return dto;
	}


	/** Troba el missatge de la causa inicial de la excepció. */
	private String getRootCause(Throwable t) {
		Throwable cause = t;
		while (cause.getCause() != null)
			cause = cause.getCause();
		return cause.getLocalizedMessage();
	}



	private List<ParellaCodiValor> getVariablesPerIniciFormulari(
			String taskId) {
		Map<String, Object> varsTasca = variableHelper.getVariablesJbpmTascaValor(taskId);
		List<ParellaCodiValor> varsForm = new ArrayList<ParellaCodiValor>();
		if (varsTasca != null) {
			for (String key: varsTasca.keySet()) {
				if (!key.startsWith(JbpmVars.VAR_PREFIX))
					varsForm.add(new ParellaCodiValor(key, varsTasca.get(key)));
			}
		}
		return varsForm;
	}

	private static final Logger logger = LoggerFactory.getLogger(TascaHelper.class);
}
