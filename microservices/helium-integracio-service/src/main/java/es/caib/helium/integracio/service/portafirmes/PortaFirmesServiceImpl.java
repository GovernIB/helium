package es.caib.helium.integracio.service.portafirmes;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.portafirmes.PortaFirma;
import es.caib.helium.integracio.domini.portafirmes.PortaFirmesFlux;
import es.caib.helium.integracio.domini.portafirmes.PortaFirma.TipusEstat;
import es.caib.helium.integracio.excepcions.portafirmes.PortaFirmesException;
import es.caib.helium.integracio.repository.portafirmes.PortaFirmesRepository;

@Service
public class PortaFirmesServiceImpl implements PortaFirmesService {

	@Autowired
	private PortaFirmesRepository portaFirmesRepository;

	@Override
	public List<PortaFirma> getPendentsFirmar(String filtre) throws PortaFirmesException {
		try {
			return portaFirmesRepository.findPendents(TipusEstat.PENDENT, TipusEstat.REBUTJAT);
		} catch (Exception e) {
			throw new PortaFirmesException("[PortaFirmesServiceImpl.getPendentsFirmar] Error inesperat al repository de portafirmes", e);
		}
	}

	@Override
	public boolean enviarPortaFirmes(PortaFirmesFlux document) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<PortaFirma> getPendentsFirmarByProcessInstance(Integer processInstance) throws PortaFirmesException {
		try {
			return portaFirmesRepository.findPendentsByProcessInstance(TipusEstat.PENDENT, TipusEstat.REBUTJAT, processInstance);
		} catch (Exception e) {
			throw new PortaFirmesException("[PortaFirmesServiceImpl.getPendentsFirmarByProcessInstance] Error inesperat al repository de portafirmes", e);
		}
	}
}
