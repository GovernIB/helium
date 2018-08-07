/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.notificacio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Implementació Mock del plugin d'enviament de notificacions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioPluginMock implements NotificacioPlugin {

	@Override
	public RespostaEnviar enviar(
			Notificacio notificacio) throws NotificacioPluginException {

		RespostaEnviar resposta = new RespostaEnviar();
		resposta.setEstat(NotificacioEstat.PENDENT);
		resposta.setIdentificador("abcdef1234567890");
		if (notificacio.getEnviaments() != null && !notificacio.getEnviaments().isEmpty()) {
			List<EnviamentReferencia> referencies = new ArrayList<EnviamentReferencia>();
			for (int i = 0; i < notificacio.getEnviaments().size(); i++) {
				EnviamentReferencia referencia = new EnviamentReferencia();
				referencia.setTitularNif(notificacio.getEnviaments().get(i).getTitular().getNif());
				referencia.setReferencia("abcdef123456789" + i);
				referencies.add(referencia);
			}
			resposta.setReferencies(referencies);
		}
		return resposta;
	}

	@Override
	public RespostaConsultaEstatNotificacio consultarNotificacio(
			String identificador) throws NotificacioPluginException {
		RespostaConsultaEstatNotificacio resposta = new RespostaConsultaEstatNotificacio();
		resposta.setEstat(NotificacioEstat.values()[new Random().nextInt(3)]);
		return resposta;
	}

	@Override
	public RespostaConsultaEstatEnviament consultarEnviament(
			String referencia) throws NotificacioPluginException {
		RespostaConsultaEstatEnviament resposta = new RespostaConsultaEstatEnviament();
		resposta.setEstat(EnviamentEstat.values()[new Random().nextInt(21)]);
		resposta.setEstatData(new Date());
		resposta.setEstatDescripcio("Descripció de l'estat");
		resposta.setEstatOrigen("Origen");
		resposta.setReceptorNif("12345678Z");
		resposta.setReceptorNom("Nom receptor");
		return resposta;
	}

}
