/**
 * 
 */
package es.caib.helium.logic.service;

import es.caib.helium.client.integracio.persones.model.Persona;
import es.caib.helium.logic.helper.ConversioTipusServiceHelper;
import es.caib.helium.logic.helper.PluginHelper;
import es.caib.helium.logic.helper.UsuariActualHelper;
import es.caib.helium.logic.intf.dto.DocumentConversioDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.dto.UsuariPreferenciesDto;
import es.caib.helium.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.service.AplicacioService;
import es.caib.helium.logic.intf.util.GlobalProperties;
import es.caib.helium.logic.util.OpenOfficeUtils;
import es.caib.helium.persist.repository.UsuariPreferenciesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Implementació dels mètodes de AplicacioService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AplicacioServiceImpl implements AplicacioService {

	@Autowired
	private UsuariPreferenciesRepository usuariPreferenciesRepository;

	@Autowired
	private UsuariActualHelper usuariActualHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Autowired
	private ConversioTipusServiceHelper conversioTipusServiceHelper;

	@Resource
	private OpenOfficeUtils openOfficeUtils;
	@Resource
	private GlobalProperties globalProperties;



	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public UsuariPreferenciesDto getUsuariPreferencies() {
		String usuariActual = usuariActualHelper.getUsuariActual();
		logger.debug("Consulta de les preferències per a l'usuari actual ("
				+ "usuariActual=" + usuariActual + ")");
		return conversioTipusServiceHelper.convertir(
				usuariPreferenciesRepository.findByCodi(usuariActual),
				UsuariPreferenciesDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PersonaDto findPersonaAmbCodi(String codi) {
		return pluginHelper.personaFindAmbCodi(codi);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PersonaDto findPersonaActual() {
		String usuariActual = usuariActualHelper.getUsuariActual();
		return pluginHelper.personaFindAmbCodi(usuariActual);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Persona> findPersonaLikeNomSencer(String text) {
		return pluginHelper.personaFindLikeNomSencer(text);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PersonaDto> findPersonesAmbGrup(String grup) throws SistemaExternException {
		return pluginHelper.personesAmbGrup(grup);
	}

    @Override
    public List<PersonaDto> findPersonaAll() throws SistemaExternException {
        return pluginHelper.personaFindAll();
    }


    @Override
	public GlobalProperties getGlobalProperties() {
		return globalProperties;
	}

    @Override
    public DocumentConversioDto convertFile(String arxiuNom, byte[] arxiuContingut, String extensioSortida) throws Exception {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();

		String extensioOriginal = openOfficeUtils.getArxiuExtensio(arxiuNom);
		String nom = openOfficeUtils.nomArxiuConvertit(arxiuNom, extensioSortida);
		String mediaType = openOfficeUtils.getArxiuMimeType(nom);
		byte[] contingut = arxiuContingut;
		if (!extensioSortida.equalsIgnoreCase(extensioOriginal)) {
			openOfficeUtils.convertir(arxiuNom, arxiuContingut, extensioSortida, bo);
			contingut = bo.toByteArray();
		}

		return DocumentConversioDto.builder()
				.nom(nom)
				.contingut(contingut)
				.mediaType(mediaType)
				.extensio(extensioSortida)
				.extensioOrignal(extensioOriginal)
				.build();
    }

	@Override
	public String getArxiuMediaType(String nomFitxer) {
		return openOfficeUtils.getArxiuMimeType(nomFitxer);
	}


	private static final Logger logger = LoggerFactory.getLogger(AplicacioServiceImpl.class);

}
