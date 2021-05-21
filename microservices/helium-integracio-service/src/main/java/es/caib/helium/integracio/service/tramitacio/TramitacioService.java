package es.caib.helium.integracio.service.tramitacio;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.tramitacio.DadesTramit;
import es.caib.helium.integracio.domini.tramitacio.RespostaJustificantRecepcio;
import es.caib.helium.integracio.excepcions.tramitacio.TramitacioException;

@Service
public interface TramitacioService {
	
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(String numeroRegistre) throws TramitacioException;
	
	public DadesTramit obtenirDadesTramit(String numero, String clau) throws TramitacioException;
//	public void comunicarResultatProcesTramit(ResultatProcesTramitRequest request) throws TramitacioException;

}
