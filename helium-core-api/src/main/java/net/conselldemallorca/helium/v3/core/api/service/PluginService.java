/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreAnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreIdDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDto;
import net.conselldemallorca.helium.v3.core.api.dto.ZonaperEventDto;
import net.conselldemallorca.helium.v3.core.api.dto.ZonaperExpedientDto;
import net.conselldemallorca.helium.v3.core.api.exception.ArxiuNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.PluginException;


/**
 * Servei per a accedir a la funcionalitat dels plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PluginService {

	public void zonaperExpedientCrear(
			String processInstanceId,
			ZonaperExpedientDto dadesExpedient) throws PluginException;
	public void zonaperEventCrear(
			String processInstanceId,
			ZonaperEventDto dadesEvent) throws PluginException;

	public boolean isRegistreActiu();
	public RegistreIdDto registreAnotacioEntrada(
			RegistreAnotacioDto anotacio) throws PluginException;
	public RegistreIdDto registreAnotacioSortida(
			RegistreAnotacioDto anotacio) throws PluginException;
	public RegistreIdDto registreNotificacio(
			RegistreNotificacioDto notificacio) throws PluginException;
	public Date registreNotificacioComprovarRecepcio(
			String registreNumero) throws PluginException;
	public String getRegistreOficinaNom(String oficinaCodi) throws PluginException;

	public void portasignaturesEnviar(
			Long documentId,
			List<Long> annexosId,
			PersonaDto persona,
			List<PersonaDto> personesPas1,
			int minSignatarisPas1,
			List<PersonaDto> personesPas2,
			int minSignatarisPas2,
			List<PersonaDto> personesPas3,
			int minSignatarisPas3,
			Long expedientId,
			String importancia,
			Date dataLimit,
			Long tokenId,
			Long processInstanceId,
			String transicioOK,
			String transicioKO) throws PluginException;

	public ArxiuDto gestioDocumentalArxiu(
			String id) throws ArxiuNotFoundException;

	public void emailSend(
			String fromAddress,
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text,
			List<ArxiuDto> attachments) throws PluginException;

	public TramitDto getTramit(
			String numero,
			String clau) throws PluginException;

}
