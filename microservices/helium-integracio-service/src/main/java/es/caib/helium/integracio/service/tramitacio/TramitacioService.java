package es.caib.helium.integracio.service.tramitacio;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.registre.RegistreNotificacio;
import es.caib.helium.integracio.domini.registre.RespostaAnotacioRegistre;
import es.caib.helium.integracio.domini.tramitacio.DadesTramit;
import es.caib.helium.integracio.domini.tramitacio.DadesVistaDocument;
import es.caib.helium.integracio.domini.tramitacio.ObtenirVistaDocumentRequest;
import es.caib.helium.integracio.domini.tramitacio.PublicarEventRequest;
import es.caib.helium.integracio.domini.tramitacio.PublicarExpedientRequest;
import es.caib.helium.integracio.domini.tramitacio.RespostaJustificantDetallRecepcio;
import es.caib.helium.integracio.domini.tramitacio.RespostaJustificantRecepcio;
import es.caib.helium.integracio.domini.tramitacio.ResultatProcesTramitRequest;
import es.caib.helium.integracio.excepcions.tramitacio.TramitacioException;

@Service
public interface TramitacioService {
	
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(String numeroRegistre) throws TramitacioException;
	public RespostaJustificantDetallRecepcio obtenirJustificantDetallRecepcio(String numeroRegistre) throws TramitacioException;
	public DadesVistaDocument obtenirVistaDocument(ObtenirVistaDocumentRequest request) throws TramitacioException;
	
	public DadesTramit obtenirDadesTramit(String numero, String clau) throws TramitacioException;
	public boolean comunicarResultatProcesTramit(ResultatProcesTramitRequest request) throws TramitacioException;
	
	public boolean crearExpedientZonaPersonal(PublicarExpedientRequest request) throws TramitacioException;
	public boolean crearEventZonaPersonal(PublicarEventRequest request) throws TramitacioException;
	public RespostaAnotacioRegistre registrarNotificacio(RegistreNotificacio registreNotificacio) throws TramitacioException;
	public boolean existeixExpedient(Long unidadAdministrativa, String identificadorExpediente) throws TramitacioException;
}
