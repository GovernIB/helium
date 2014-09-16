package net.conselldemallorca.helium.wsintegraciones.formulari;

import java.util.List;
import javax.jws.WebService;

@WebService(
        serviceName = "FormsService",
        portName = "IniciFormulari",
        targetNamespace = "https://proves.caib.es/signatura/services/IniciFormulari",
        endpointInterface = "net.conselldemallorca.helium.wsintegraciones.formulari.IniciFormulari")

public class IniciFormulariImpl implements IniciFormulari {

	public RespostaIniciFormulari iniciFormulari(
			String codi,
			String taskId,
			List<ParellaCodiValor> valors) {
		System.out.println(">>> Rebut taskId: " + taskId);
		RespostaIniciFormulari resposta = new RespostaIniciFormulari();
		resposta.setWidth(640);
		resposta.setHeight(480);
		resposta.setUrl("http://localhost:8080/helium/enumeracio/form.html");
		resposta.setFormulariId(taskId);
		System.out.println(">>> Enviant cap a la URL: " + resposta.getUrl());
		return resposta;
	}

}
