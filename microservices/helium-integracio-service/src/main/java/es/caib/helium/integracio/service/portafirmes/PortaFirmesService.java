package es.caib.helium.integracio.service.portafirmes;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import es.caib.helium.integracio.domini.portafirmes.PortaFirma;
import es.caib.helium.integracio.domini.portafirmes.PortaFirmesFlux;

public interface PortaFirmesService {

	public List<PortaFirma> getPendentsFirmar(String filtre) throws Exception;
	public boolean enviarPortaFirmes(@RequestBody PortaFirmesFlux document);
	public List<PortaFirma> getPendentsFirmarByProcessInstance(Integer processInstance) throws Exception;
}
