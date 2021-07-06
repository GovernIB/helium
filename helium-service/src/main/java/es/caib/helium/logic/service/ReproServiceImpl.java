package es.caib.helium.logic.service;

import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.helper.UsuariActualHelper;
import es.caib.helium.logic.intf.dto.ReproDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.ValidacioException;
import es.caib.helium.logic.intf.service.ReproService;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.Repro;
import es.caib.helium.persist.entity.Tasca;
import es.caib.helium.persist.repository.ExpedientTipusRepository;
import es.caib.helium.persist.repository.ReproRepository;
import es.caib.helium.persist.repository.TascaRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("reproServiceV3")
public class ReproServiceImpl implements ReproService {
	
	private static final int MAX_VALOR_LENGTH = 20000;
	@Resource
	private ReproRepository reproRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private UsuariActualHelper usuariActualHelper;
	@Resource
	private TascaRepository tascaRepository;

	@Transactional(readOnly=true)
	@Override
	public List<ReproDto> findReprosByUsuariTipusExpedient(Long expedientTipusId, String tascaCodi) {
		
		ExpedientTipus expedientTipus = expedientTipusRepository.getById(expedientTipusId);
		
		List<Repro> repros = reproRepository.findByUsuariAndExpedientTipusIdAndTascaCodiOrderByIdDesc(
																									usuariActualHelper.getUsuariActual(), 
																									expedientTipus, 
																									tascaCodi);
		return conversioTipusHelper.convertirList(
				repros,
				ReproDto.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	public ReproDto findById(Long id) {
		Repro repro = reproRepository.findById(id)
				.orElseThrow(()-> new NoTrobatException(Repro.class, id));
		
		return conversioTipusHelper.convertir(reproRepository.getById(id), ReproDto.class);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	@Override
	public Map<String,Object> findValorsById(Long id) {
		Repro repro = reproRepository.getById(id);
		if (repro == null) {
			logger.warn("No s'ha trobat la repro amb id " + id + " a findValorsById");
			return null;	
		}
		byte[] bytes = DatatypeConverter.parseBase64Binary(repro.getValors());
		ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
        ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(bytesIn);
			Object valors = ois.readObject();
	        
	        bytesIn.close();
	        ois.close();
	        
	        return (Map<String,Object>)valors;
		} catch (Exception e) {
			String errMsg = "Error recuperant els valors de la repro " + repro.getNom() + ": " + e.getMessage();
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg, e);
		}
	}
	
	@Transactional
	@Override
	public ReproDto create(
			Long expedientTipusId, 
			String nom, 
			Map<String, Object> valors) {
		
		return this.createRepro(expedientTipusId,
				null,
				nom,
				valors);
	}
	
	@Transactional
	@Override
	public String deleteById(Long id) {
		Repro repro = reproRepository.findById(id)
				.orElseThrow(() -> new NoTrobatException(Repro.class, id));
		
		String nom = repro.getNom();
		reproRepository.deleteById(id);
		return nom;
	}

	@Transactional
	@Override
	public ReproDto createTasca(
			Long expedientTipusId, 
			Long tascaId, 
			String nom, 
			Map<String, Object> valors) throws NoTrobatException, ValidacioException {
		
		return this.createRepro(expedientTipusId,
				tascaId,
				nom,
				valors);
	}
	
	/** Mètode comú per a la creació de repros de formularis inicials i tasques.
	 * 
	 * @param expedientTipusId
	 * @param tascaId
	 * @param nom
	 * @param valors
	 * @return
	 */
	private ReproDto createRepro(
			Long expedientTipusId, 
			Long tascaId, 
			String nom, 
			Map<String, Object> valors) {
		
		Repro repro = null;
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId)
				.orElseThrow(() -> new NoTrobatException(ExpedientTipus.class, expedientTipusId));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(valors);
		    byte[] buf = baos.toByteArray();
		    baos.close();
	        oos.close();

	        String valors_s = DatatypeConverter.printBase64Binary(buf);
	        String tascaCodi = null;
	        if (tascaId != null) {
		        Tasca tasca = tascaRepository.getById(tascaId);
		        tascaCodi = tasca.getJbpmName();
	        }			
			if (valors_s.getBytes("UTF-8").length > ReproServiceImpl.MAX_VALOR_LENGTH)
				throw new ValidacioException("El contingut del formulari és massa llarg");
			String usuariActual = usuariActualHelper.getUsuariActual();
			repro = new Repro(usuariActual, expedientTipus, nom, valors_s, tascaCodi);		
			reproRepository.save(repro);
		} catch (Exception e) {
			String errMsg = "Error guardant la repro " + nom + ": " + e.getMessage();
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg, e);
		}
		return conversioTipusHelper.convertir(repro, ReproDto.class);
	}

	private static final Log logger = LogFactory.getLog(ReproServiceImpl.class);
}
