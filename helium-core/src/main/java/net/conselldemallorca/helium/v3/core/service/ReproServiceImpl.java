package net.conselldemallorca.helium.v3.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.DatatypeConverter;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Repro;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.v3.core.api.dto.ReproDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.ReproService;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.ReproRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("reproServiceV3")
public class ReproServiceImpl implements ReproService {
	
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
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId);
		
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
		Repro repro = reproRepository.findOne(id);
		if (repro == null)
			throw new NoTrobatException(Repro.class, id);
		
		return conversioTipusHelper.convertir(reproRepository.findOne(id), ReproDto.class);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	@Override
	public Map<String,Object> findValorsById(Long id) {
		Repro repro = reproRepository.findOne(id);
		if (repro == null)
			return null;
			//throw new NoTrobatException(Repro.class, id);
		
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
			throw new ValidacioException("Error recuperant els valros de la repro " + repro.getNom(), e);
		}
	}
	
	@Transactional
	@Override
	public ReproDto create(Long expedientTipusId, String nom, Map<String, Object> valors) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(valors);
		    byte[] buf = baos.toByteArray();
		    baos.close();
	        oos.close();
	        
	        String valors_s = DatatypeConverter.printBase64Binary(buf);
			
			if (valors_s.getBytes("UTF-8").length > 4000)
				throw new ValidacioException("El contingut del formulari és massa llarg");
			String usuariActual = usuariActualHelper.getUsuariActual();
			Repro repro = new Repro(usuariActual, expedientTipus, nom, valors_s);
			reproRepository.saveAndFlush(repro);
			return conversioTipusHelper.convertir(repro, ReproDto.class);
		} catch (Exception e) {
			throw new ValidacioException("Error recuperant els valros de la repro " + nom, e);
		}
	}
	
	@Transactional
	@Override
	public String deleteById(Long id) {
		Repro repro = reproRepository.findOne(id);
		if (repro == null)
			throw new NoTrobatException(Repro.class, id);
		
		String nom = repro.getNom();
		reproRepository.delete(id);
		return nom;
	}

	@Override
	public ReproDto createTasca(Long expedientTipusId, Long tascaId, String nom, Map<String, Object> valors)
			throws NoTrobatException, ValidacioException {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(valors);
		    byte[] buf = baos.toByteArray();
		    baos.close();
	        oos.close();
	        
//	        Tasca tasca = tascaRepository.findById(tascaId);
	        
	        Tasca tasca = tascaRepository.findOne(tascaId);
	        
	        String valors_s = DatatypeConverter.printBase64Binary(buf);
			
			if (valors_s.getBytes("UTF-8").length > 4000)
				throw new ValidacioException("El contingut del formulari és massa llarg");
			String usuariActual = usuariActualHelper.getUsuariActual();
			Repro repro = new Repro(usuariActual, expedientTipus, nom, valors_s, tasca.getJbpmName());
			Repro repro2 =  reproRepository.saveAndFlush(repro);
			return conversioTipusHelper.convertir(repro2, ReproDto.class);
		} catch (Exception e) {
			throw new ValidacioException("Error recuperant els valros de la repro " + nom, e);
		}
	}
}
