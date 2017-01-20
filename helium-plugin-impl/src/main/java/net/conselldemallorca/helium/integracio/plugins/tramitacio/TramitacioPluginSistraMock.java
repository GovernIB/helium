/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.tramitacio;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.caib.bantel.ws.v2.model.referenciaentrada.ReferenciaEntrada;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantDetallRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;

/**
 * Implementació del plugin de tramitacio accedint a la v2
 * dels ws de SISTRA
 * 
 * @author Limit Tecnologies
 */
public class TramitacioPluginSistraMock implements TramitacioPlugin {

	public DadesTramit obtenirDadesTramit(
			ObtenirDadesTramitRequest request) throws TramitacioPluginException {
		try {
			ReferenciaEntrada referenciaEntrada = new ReferenciaEntrada();
			referenciaEntrada.setNumeroEntrada(request.getNumero());
			referenciaEntrada.setClaveAcceso(request.getClau());
			
			DadesTramit dadesTramit = new DadesTramit();
			dadesTramit.setIdentificador("execaction");
			dadesTramit.setData(new Date());
			
			File file = new File("C:/Feina/tramitdocs/ampliacio.xml");
			byte[] bytesArray = new byte[(int) file.length()];
			FileInputStream fis = new FileInputStream(file);
			fis.read(bytesArray); //read file into bytes[]
			fis.close();
			
			DocumentTelematic documentTelematic = new DocumentTelematic();
			documentTelematic.setArxiuExtensio("xml");
			documentTelematic.setArxiuContingut(bytesArray);
			documentTelematic.setArxiuNom("ampliacio");
			
			DocumentTramit documentTramit = new DocumentTramit();
			documentTramit.setInstanciaNumero(1);
			documentTramit.setIdentificador("FORM1");
			documentTramit.setDocumentTelematic(documentTelematic);
			
			//document a modificar
			File fileModificar = new File("C:/Feina/tramitdocs/doc1.pdf");
			byte[] bytesArray2 = new byte[(int) fileModificar.length()];
			FileInputStream fis2 = new FileInputStream(fileModificar);
			fis2.read(bytesArray2); //read file into bytes[]
			fis2.close();
			
			DocumentTelematic docTelModificar = new DocumentTelematic();
			docTelModificar.setArxiuExtensio("pdf");
			docTelModificar.setArxiuContingut(bytesArray2);
			docTelModificar.setArxiuNom("doc1");
			
			DocumentTramit docModificar = new DocumentTramit();
			docModificar.setInstanciaNumero(1);
			docModificar.setIdentificador("SISTRADOC1");
			docModificar.setDocumentTelematic(docTelModificar);
			/////////////////////////
			
			List<DocumentTramit> documentsTramit = new ArrayList<DocumentTramit>();
			documentsTramit.add(documentTramit);
			documentsTramit.add(docModificar);
			
			dadesTramit.setDocuments(documentsTramit);
			
			return dadesTramit;
		} catch (Exception ex) {
			logger.error("Error al obtenir dades del tràmit", ex);
			throw new TramitacioPluginException("Error al obtenir dades del tràmit", ex);
		}
	}

	
	private static final Log logger = LogFactory.getLog(TramitacioPluginSistraMock.class);


	@Override
	public void publicarExpedient(PublicarExpedientRequest request) throws TramitacioPluginException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void publicarEvent(PublicarEventRequest request) throws TramitacioPluginException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void comunicarResultatProcesTramit(ResultatProcesTramitRequest request) throws TramitacioPluginException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public DadesVistaDocument obtenirVistaDocument(ObtenirVistaDocumentRequest request)
			throws TramitacioPluginException {
		
		try {
			File fileModificar = new File("C:/Feina/tramitdocs/doc1.pdf");
			byte[] bytesArray2 = new byte[(int) fileModificar.length()];
			FileInputStream fis2;
			
			fis2 = new FileInputStream(fileModificar);
			
			fis2.read(bytesArray2); //read file into bytes[]
			fis2.close();
			
			DadesVistaDocument resposta = new DadesVistaDocument();
			resposta.setNom("doc1.pdf");
			resposta.setArxiuNom("doc1.pdf");
			resposta.setArxiuContingut(bytesArray2);
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al obtenir dades del tràmit", ex);
			throw new TramitacioPluginException("Error al obtenir dades del tràmit", ex);
		}
	}


	@Override
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(String numeroRegistre)
			throws TramitacioPluginException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public RespostaAnotacioRegistre registrarNotificacio(RegistreNotificacio registreNotificacio)
			throws TramitacioPluginException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public RespostaJustificantDetallRecepcio obtenirJustificantDetallRecepcio(String numeroRegistre)
			throws TramitacioPluginException {
		// TODO Auto-generated method stub
		return null;
	}
}
