package net.conselldemallorca.helium.core.model.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.DocumentPortasignatures;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PasSignatura;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PortasignaturesPlugin;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PortasignaturesPluginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao per accedir a la funcionalitat del plugin del portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class PluginPortasignaturesDao extends HibernateGenericDao<Portasignatures, Long> {

	private PortasignaturesPlugin portasignaturesPlugin;



	public PluginPortasignaturesDao() {
		super(Portasignatures.class);
	}

	public Integer uploadDocument(
			DocumentDto document,
			List<DocumentDto> annexos,
			PersonaDto persona,
			List<PersonaDto> personesPas1,
			int minSignatarisPas1,
			List<PersonaDto> personesPas2,
			int minSignatarisPas2,
			List<PersonaDto> personesPas3,
			int minSignatarisPas3,
			Expedient expedient,
			String importancia,
			Date dataLimit) throws Exception {
		try {
			return getPortasignaturesPlugin().uploadDocument(
					getDocumentPortasignatures(document, expedient),
					getAnnexosPortasignatures(annexos, expedient),
					false,
					getPassesSignatura(
							getSignatariIdPerPersona(persona),
							personesPas1,
							minSignatarisPas1,
							personesPas2,
							minSignatarisPas2,
							personesPas3,
							minSignatarisPas3),
					expedient.getIdentificador(),
					importancia,
					dataLimit);
		} catch (PortasignaturesPluginException ex) {
			logger.error("Error al enviar el document al portasignatures", ex);
			throw new PluginException("Error al enviar el document al portasignatures", ex);
		}
	}

	public List<byte[]> obtenirSignaturesDocument(
			Integer documentId) throws Exception {
		try {
			return getPortasignaturesPlugin().obtenirSignaturesDocument(
					documentId);
		} catch (PortasignaturesPluginException ex) {
			//logger.error("Error al rebre el document del portasignatures", ex);
			throw new PluginException("Error al rebre el document del portasignatures", ex);
		}
	}

	@SuppressWarnings("unchecked")
	public Portasignatures findByDocument(Integer id) {
		List<Portasignatures> list = getSession()
			.createCriteria(getPersistentClass())
			.add(Restrictions.eq("documentId", id))
			//.add(Restrictions.eq("estat", TipusEstat.PENDENT))
			.list();
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Portasignatures> findPendents() {
		return getSession()
			.createCriteria(getPersistentClass())
			.add(Restrictions.ne("estat", TipusEstat.SIGNAT))
			.add(Restrictions.ne("estat", TipusEstat.REBUTJAT))
			.list();
	}

	@SuppressWarnings("unchecked")
	public List<Portasignatures> findPendentsPerProcessInstanceId(
			String processInstanceId) {
		List<Portasignatures> psignas = getSession()
			.createCriteria(getPersistentClass())
			.add(Restrictions.eq("processInstanceId", processInstanceId))
			.list();
		Iterator<Portasignatures> it = psignas.iterator();
		while (it.hasNext()) {
			Portasignatures psigna = it.next();
			if (	!TipusEstat.PENDENT.equals(psigna.getEstat()) &&
					!TipusEstat.SIGNAT.equals(psigna.getEstat()) &&
					!TipusEstat.REBUTJAT.equals(psigna.getEstat()) &&
					!TipusEstat.ERROR.equals(psigna.getEstat())) {
				it.remove();
			}
		}
		return psignas;
	}

	@SuppressWarnings("unchecked")
	public List<Portasignatures> findAmbErrorsPerExpedientId(
			Long expedientId) {
		return getSession()
			.createCriteria(getPersistentClass())
			.add(Restrictions.eq("expedient.id", expedientId))
			.add(Restrictions.eq("estat", TipusEstat.ERROR))
			.list();
	}



	@SuppressWarnings("rawtypes")
	private PortasignaturesPlugin getPortasignaturesPlugin() {
		if (portasignaturesPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.class");
			if ((pluginClass != null) && (pluginClass.length() > 0)) {
				try {
					Class clazz = Class.forName(pluginClass);
					portasignaturesPlugin = (PortasignaturesPlugin)clazz.newInstance();
				} catch (Exception ex) {
					logger.error("No s'ha pogut crear la instància del plugin de portasignatures", ex);
					throw new PluginException("No s'ha pogut crear la instància del plugin de portasignatures", ex);
				}
			}
		}
		return portasignaturesPlugin;
	}

	private DocumentPortasignatures getDocumentPortasignatures(
			DocumentDto document,
			Expedient expedient) {
		DocumentPortasignatures documentPs = new DocumentPortasignatures();
		documentPs.setTitol(
				expedient.getIdentificador() + ": " + document.getDocumentNom());
		documentPs.setArxiuNom(document.getVistaNom());
		documentPs.setArxiuContingut(document.getVistaContingut());
		documentPs.setTipus(document.getTipusDocPortasignatures());
		documentPs.setSignat(document.isSignat());
		documentPs.setReference(document.getId().toString());
		return documentPs;
	}
	private List<DocumentPortasignatures> getAnnexosPortasignatures(
			List<DocumentDto> annexos,
			Expedient expedient) {
		if (annexos == null)
			return null;
		List<DocumentPortasignatures> resposta = new ArrayList<DocumentPortasignatures>();
		for (DocumentDto document: annexos)
			resposta.add(getDocumentPortasignatures(document, expedient));
		return resposta;
	}

	private PasSignatura[] getPassesSignatura(
			String signatariId,
			List<PersonaDto> personesPas1,
			int minSignatarisPas1,
			List<PersonaDto> personesPas2,
			int minSignatarisPas2,
			List<PersonaDto> personesPas3,
			int minSignatarisPas3) {
		if (personesPas1 != null && personesPas1.size() > 0) {
			List<PasSignatura> passes = new ArrayList<PasSignatura>();
			PasSignatura pas = new PasSignatura();
			List<String> signataris = getSignatariIdsPerPersones(personesPas1);
			pas.setSignataris(signataris.toArray(new String[signataris.size()]));
			pas.setMinSignataris(minSignatarisPas1);
			passes.add(pas);
			if (personesPas2 != null && personesPas2.size() > 0) {
				pas = new PasSignatura();
				signataris = getSignatariIdsPerPersones(personesPas2);
				pas.setSignataris(signataris.toArray(new String[signataris.size()]));
				pas.setMinSignataris(minSignatarisPas2);
				passes.add(pas);
			}
			if (personesPas3 != null && personesPas3.size() > 0) {
				pas = new PasSignatura();
				signataris = getSignatariIdsPerPersones(personesPas3);
				pas.setSignataris(signataris.toArray(new String[signataris.size()]));
				pas.setMinSignataris(minSignatarisPas3);
				passes.add(pas);
			}
			return passes.toArray(new PasSignatura[passes.size()]);
		} else if (signatariId != null) {
			PasSignatura[] passes = new PasSignatura[1];
			PasSignatura pas = new PasSignatura();
			pas.setMinSignataris(1);
			pas.setSignataris(new String[] {signatariId});
			passes[0] = pas;
			return passes;
		} else {
			PasSignatura[] passes = new PasSignatura[0];
			return passes;
		}
	}

	private List<String> getSignatariIdsPerPersones(List<PersonaDto> persones) {
		List<String> signatariIds = new ArrayList<String>();
		for (PersonaDto persona: persones) {
			String signatariId = getSignatariIdPerPersona(persona);
			if (signatariId != null)
				signatariIds.add(signatariId);
		}
		return signatariIds;
	}
	private String getSignatariIdPerPersona(PersonaDto persona) {
		if (persona == null)
			return null;
		String signatariId = persona.getDni();
		if (isIdUsuariPerCodi())
			signatariId = persona.getCodi();
		if (isIdUsuariPerDni())
			signatariId = persona.getDni();
		return signatariId;
	}
	private boolean isIdUsuariPerDni() {
		return "dni".equalsIgnoreCase(GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.usuari.id"));
	}
	private boolean isIdUsuariPerCodi() {
		return "codi".equalsIgnoreCase(GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.usuari.id"));
	}

	private static final Log logger = LogFactory.getLog(PluginPortasignaturesDao.class);

}
