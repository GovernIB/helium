package es.caib.helium.integracio.service.arxiu;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.arxiu.ExpedientArxiu;
import es.caib.helium.integracio.excepcions.arxiu.ArxiuServiceException;
import es.caib.plugins.arxiu.api.Expedient;

@Service
public interface ArxiuService {
	
	public Expedient getExpedientByUuId(String uuId);
	public boolean crearExpedient(ExpedientArxiu expedient) throws ArxiuServiceException;
}
