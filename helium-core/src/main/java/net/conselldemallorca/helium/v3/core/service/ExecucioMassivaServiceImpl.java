/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dao.ExecucioMassivaDao;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva.ExecucioMassivaTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Servei per a gestionar la tramitació massiva d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("execucioMassivaServiceV3")
@Transactional(noRollbackForClassName = "java.lang.Exception")
public class ExecucioMassivaServiceImpl implements ExecucioMassivaService {

	@Resource
	private ExecucioMassivaDao execucioMassivaRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource(name="tascaServiceV3")
	private TascaService tascaService;
	
	@Transactional
	@Override
	public void crearExecucioMassiva(ExecucioMassivaDto dto) throws Exception {
		if ((dto.getExpedientIds() != null && !dto.getExpedientIds().isEmpty()) ||
			(dto.getTascaIds() != null && dto.getTascaIds().length > 0) ||
			(dto.getProcInstIds() != null && !dto.getProcInstIds().isEmpty())) {
			String log = "Creació d'execució massiva (dataInici=" + dto.getDataInici();
			if (dto.getExpedientTipusId() != null) log += ", expedientTipusId=" + dto.getExpedientTipusId();
			log += ", numExpedients=";
			if (dto.getExpedientIds() != null) log += dto.getExpedientIds().size();
			else if (dto.getProcInstIds() != null) log += dto.getProcInstIds().size();
			else log += "0";
			log += ")";
			logger.debug(log);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			ExecucioMassiva execucioMassiva = new ExecucioMassiva(
					auth.getName(),
					ExecucioMassivaTipus.valueOf(dto.getTipus().toString()));
			if (dto.getDataInici() == null) {
				execucioMassiva.setDataInici(new Date());
			} else {
				execucioMassiva.setDataInici(dto.getDataInici());
			}
			execucioMassiva.setEnviarCorreu(dto.getEnviarCorreu());
			execucioMassiva.setParam1(dto.getParam1());
			execucioMassiva.setParam2(dto.getParam2());
			if (dto.getExpedientTipusId() != null) {
				execucioMassiva.setExpedientTipus(
						expedientTipusRepository.findById(dto.getExpedientTipusId())
				);
			}
			int ordre = 0;
			boolean expedients = false;
			if (dto.getExpedientIds() != null) {
				
				for (Long expedientId: dto.getExpedientIds()) {
					Expedient expedient = expedientRepository.findById(expedientId);
					ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(
							execucioMassiva,
							expedient,
							ordre++);
					execucioMassiva.addExpedient(eme);
					expedients = true;
				}
			} else if (dto.getTascaIds() != null) {
				for (String tascaId: dto.getTascaIds()) {
					ExpedientTascaDto tasca = tascaService.getByIdSenseComprovacio(tascaId);
					Expedient expedient = expedientRepository.findById(tasca.getExpedient().getId());
					ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(
							execucioMassiva,
							expedient,
							tascaId,
							ordre++);
					execucioMassiva.addExpedient(eme);
					expedients = true;
				}
			} else if (dto.getProcInstIds() != null) {
				for (String procinstId: dto.getProcInstIds()) {
					ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(
							execucioMassiva,
							procinstId,
							ordre++);
					execucioMassiva.addExpedient(eme);
					expedients = true;
				}
			}
			execucioMassiva.setEntorn(EntornActual.getEntornId());
			if (expedients)
				execucioMassivaRepository.saveOrUpdate(execucioMassiva);
			else 
				throw new Exception("S'ha intentat crear una execució massiva sense assignar expedients.");
		}
	}
	
	@Override
	public Object deserialize(byte[] bytes) throws Exception{
		Object obj = null;
		if (bytes != null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			obj = ois.readObject();
		}
		return obj;
	}
	
	@Override
	public byte[] serialize(Object obj) throws Exception{
		byte[] bytes = null;
		if (obj != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			bos.close();
			bytes = bos.toByteArray();
		}
		return bytes;
	}

	private static final Log logger = LogFactory.getLog(ExecucioMassivaService.class);
}
