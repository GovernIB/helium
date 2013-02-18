/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
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
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.v3.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.v3.core.helper.MailHelper;
import net.conselldemallorca.helium.v3.core.helper.PluginHelper;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.PortasignaturesRepository;

/**
 * Servei per a accedir a la funcionalitat dels plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("pluginServiceV3")
public class PluginServiceImpl implements PluginService {

	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private PortasignaturesRepository portasignaturesRepository;

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private DocumentHelperV3 documentHelperV3;
	@Resource
	private MailHelper mailHelper;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;



	public void zonaperExpedientCrear(
			String processInstanceId,
			ZonaperExpedientDto dadesExpedient) throws PluginException {
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		String identificador = expedient.getNumeroIdentificador();
		String clau = new Long(System.currentTimeMillis()).toString();
		dadesExpedient.setExpedientIdentificador(identificador);
		dadesExpedient.setExpedientClau(clau);
		try {
			pluginHelper.zonaperExpedientCrear(dadesExpedient);
			expedient.setTramitExpedientIdentificador(identificador);
			expedient.setTramitExpedientClau(clau);
		} catch (Exception ex) {
			throw new PluginException(ex);
		}
	}
	public void zonaperEventCrear(
			String processInstanceId,
			ZonaperEventDto dadesEvent)
			throws PluginException {
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		try {
			pluginHelper.zonaperEventCrear(
					expedient,
					dadesEvent);
		} catch (Exception ex) {
			throw new PluginException(ex);
		}
	}

	public boolean isRegistreActiu() {
		return pluginHelper.isRegistrePluginActiu();
	}
	public RegistreIdDto registreAnotacioEntrada(
			RegistreAnotacioDto anotacio) throws PluginException {
		try {
			return pluginHelper.registreAnotacioEntrada(anotacio);
		} catch (Exception ex) {
			throw new PluginException(ex);
		}
	}
	public RegistreIdDto registreAnotacioSortida(
			RegistreAnotacioDto anotacio) throws PluginException {
		try {
			return pluginHelper.registreAnotacioSortida(anotacio);
		} catch (Exception ex) {
			throw new PluginException(ex);
		}
	}
	public RegistreIdDto registreNotificacio(
			RegistreNotificacioDto notificacio) throws PluginException {
		try {
			return pluginHelper.registreNotificacio(notificacio);
		} catch (Exception ex) {
			throw new PluginException(ex);
		}
	}
	public Date registreNotificacioComprovarRecepcio(
			String registreNumero) throws PluginException {
		try {
			return pluginHelper.registreDataJustificantRecepcio(registreNumero);
		} catch (Exception ex) {
			throw new PluginException(ex);
		}
	}
	public String getRegistreOficinaNom(
			String oficinaCodi) throws PluginException {
		try {
			return pluginHelper.registreOficinaNom(oficinaCodi);
		} catch (Exception ex) {
			throw new PluginException(ex);
		}
	}

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
			String transicioKO) throws PluginException {
		DocumentDto document = documentHelperV3.getDocumentVista(
				documentId,
				true,
				true);
		List<DocumentDto> annexos = null;
		if (annexosId != null) {
			annexos = new ArrayList<DocumentDto>();
			for (Long docId: annexosId) {
				annexos.add(documentHelperV3.getDocumentVista(
						docId,
						false,
						false));
			}
		}
		Expedient expedient = expedientRepository.findOne(expedientId);
		Portasignatures portasignatures = new Portasignatures();
		portasignatures.setTokenId(tokenId);
		portasignatures.setDataEnviat(new Date());
		portasignatures.setEstat(TipusEstat.PENDENT);
		portasignatures.setDocumentStoreId(documentId);
		portasignatures.setTransicioOK(transicioOK);
		portasignatures.setTransicioKO(transicioKO);
		portasignatures.setExpedient(expedient);
		portasignatures.setProcessInstanceId(processInstanceId.toString());
		try {
			Integer psignaId = pluginHelper.portasignaturesEnviarDocument(
					document,
					annexos,
					persona,
					personesPas1,
					minSignatarisPas1,
					personesPas2,
					minSignatarisPas2,
					personesPas3,
					minSignatarisPas3,
					expedient,
					importancia,
					dataLimit);
			portasignatures.setDocumentId(psignaId);
			portasignaturesRepository.save(portasignatures);
		} catch (Exception ex) {
			throw new PluginException(ex);
		}
	}

	public ArxiuDto gestioDocumentalArxiu(
			String id) throws ArxiuNotFoundException {
		try {
			ArxiuDto arxiu = new ArxiuDto();
			arxiu.setContingut(
					pluginHelper.gestioDocumentalObtenirDocument(id));
			return arxiu;
		} catch (Exception ex) {
			throw new PluginException(ex);
		}
	}

	public void emailSend(
			String fromAddress,
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text,
			List<ArxiuDto> attachments) throws PluginException {
		try {
			mailHelper.send(
					fromAddress,
					recipients,
					ccRecipients,
					bccRecipients,
					subject,
					text,
					attachments);
		} catch (Exception ex) {
			throw new PluginException(ex);
		}
	}

	public TramitDto getTramit(
			String numero,
			String clau) throws PluginException {
		try {
			return pluginHelper.obtenirDadesTramit(numero, clau);
		} catch (Exception ex) {
			throw new PluginException(ex);
		}
	}

}
