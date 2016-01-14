/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.helper.WsClientHelper.WsClientAuth;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FormulariExtern;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.FormulariExternDto;
import net.conselldemallorca.helium.v3.core.repository.FormulariExternRepository;
import net.conselldemallorca.helium.v3.core.ws.formext.IniciFormulari;
import net.conselldemallorca.helium.v3.core.ws.formext.ParellaCodiValor;
import net.conselldemallorca.helium.v3.core.ws.formext.RespostaIniciFormulari;

/**
 * Helper per a accedir a la funcionalitat dels plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class FormulariExternHelper {

	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private FormulariExternRepository formulariExternRepository;
	@Autowired
	private WsClientHelper wsClientHelper;
	

	public FormulariExternDto iniciar(
			String taskId,
			Map<String, Object> valors) {
		ExpedientTipus expedientTipus = expedientTipusHelper.findAmbTaskId(
				taskId);
		Tasca tasca = tascaHelper.findTascaByJbpmTaskId(taskId);
		return iniciar(
				taskId,
				tasca,
				expedientTipus,
				false);
	}
	
	public FormulariExternDto iniciar(
		String taskId,
		Tasca tasca,
		ExpedientTipus expedientTipus,
		boolean tascaIniciExpedient) {
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
			url = GlobalProperties.getInstance().getProperty(
					"app.forms.service.url");
			username = GlobalProperties.getInstance().getProperty(
					"app.forms.service.username");
			password = GlobalProperties.getInstance().getProperty(
					"app.forms.service.password");
		}
		IniciFormulari clientWs = wsClientHelper.getIniciFormulariService(
				url,
				WsClientAuth.NONE,
				username,
				password);
		RespostaIniciFormulari resposta = clientWs.iniciFormulari(
				tasca.getFormExtern(),
				taskId,
				(!tascaIniciExpedient) ? getVariablesPerIniciFormulari(taskId) : null);
		/*RespostaIniciFormulari resposta = new RespostaIniciFormulari();
		resposta.setFormulariId(taskId);
		resposta.setUrl("http://oficina.limit.es");
		resposta.setWidth(800);
		resposta.setHeight(600);*/
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
		FormulariExternDto dto = new FormulariExternDto();
		dto.setFormulariId(resposta.getFormulariId());
		dto.setUrl(resposta.getUrl());
		dto.setWidth(resposta.getWidth());
		dto.setHeight(resposta.getHeight());
		return dto;
	}



	private List<ParellaCodiValor> getVariablesPerIniciFormulari(
			String taskId) {
		Map<String, Object> varsTasca = variableHelper.getVariablesJbpmTascaValor(taskId);
		List<ParellaCodiValor> varsForm = new ArrayList<ParellaCodiValor>();
		if (varsTasca != null) {
			for (String key: varsTasca.keySet()) {
				if (!key.startsWith(JbpmVars.VAR_TASCA_PREFIX))
					varsForm.add(new ParellaCodiValor(key, varsTasca.get(key)));
			}
		}
		return varsForm;
	}

}
