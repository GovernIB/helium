package net.conselldemallorca.helium.wsintegraciones.formulari;

import java.util.List;

import javax.jws.WebService;

import net.conselldemallorca.helium.core.extern.formulari.ParellaCodiValor;
import net.conselldemallorca.helium.core.extern.formulari.RespostaIniciFormulari;

@WebService(
        serviceName = "FormsService",
        portName = "IniciFormulari",
        targetNamespace = "http://forms.integracio.helium.conselldemallorca.net/",
        endpointInterface = "net.conselldemallorca.helium.core.extern.formulari.IniciFormulari")

public class IniciFormulariImpl implements net.conselldemallorca.helium.core.extern.formulari.IniciFormulari {

	@Override
	public RespostaIniciFormulari iniciFormulari(String codi, String taskId,
			List<ParellaCodiValor> valors) {
		System.out.println(">>> Rebut taskId: " + taskId);
		RespostaIniciFormulari resposta = new RespostaIniciFormulari();
		resposta.setWidth(640);
		resposta.setHeight(480);
		resposta.setUrl("https://proves.caib.es/helium/enumeracio/form.html");
		resposta.setFormulariId(taskId);
		System.out.println(">>> Enviant cap a la URL: " + resposta.getUrl());
		return resposta;
	}

}
