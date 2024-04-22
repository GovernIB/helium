package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;


/**
 * Implementació Mock del plugin de portasignatures.
 * Per simular que guarda el contingut crea el fitxer amb el contingut al directori temporal
 * on s'estigui executant helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortasignaturesPluginMock implements PortasignaturesPlugin {
	
	private static String tempDir = System.getProperty("java.io.tmpdir") + File.separator + "portasignaturesPluginMoch";
	/**
	 * Puja un document al Portasignatures i el guarda en memòria.
	 * 
	 * @param persona
	 * @param documentDto
	 * @param expedient
	 * @param importancia
	 * @param dataLimit
	 * 
	 * @return Id del document al Portasignatures
	 * 
	 * @throws Exception
	 */
	public Integer uploadDocument (
			DocumentPortasignatures document,
			List<DocumentPortasignatures> annexos,
			boolean isSignarAnnexos,
			List<PortafirmesFluxBloc> blocList,
			String remitent,
			String importancia,
			Date dataLimit,
			String fluxId,
			String fluxTipus) throws PortasignaturesPluginException {
		
		// Calcula un nou id pel document dins custòdia
		Integer newDocumentId = new Integer(new Long(System.currentTimeMillis()).intValue());
		
		// Ruta al directori pel fitxer
		File path = new File(tempDir);
		path.mkdirs();
		// Ruta destí a la carpeta selenium del directori temporal
		String filePath = tempDir + File.separator + newDocumentId.toString();		
		// Escriu el contingut a l'arxiu
		InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
        	stream = new ByteArrayInputStream(document.getArxiuContingut());
            int readBytes;
	        byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(filePath);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
        	try {
        		if (stream != null)
        			stream.close();
        		if (resStreamOut != null)
        			resStreamOut.close();
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
		}
		return newDocumentId;
	}

	/**
	 * Descarrega un document del Portasignatures
	 * 
	 * @param documentId
	 * 
	 * @return document
	 * 
	 * @throws Exception
	 */
	public List<byte[]> obtenirSignaturesDocument(
			Integer documentId) throws PortasignaturesPluginException {
		List<byte[]> result = new ArrayList<byte[]>();
		String filePath = tempDir + File.separator + documentId.toString();
		File file = new File(filePath);
		if (file.exists()) 
			try {
				result.add(Files.readAllBytes(file.toPath()));
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error llegint el contingut del document pel portasignaturesPuginMock amb documentId " + documentId);
			}
		return result;
	}
	
	public void deleteDocuments (
			Integer document) throws PortasignaturesPluginException {
		String filePath = tempDir + File.separator + document.toString();
		File file = new File(filePath);
		if (file.exists())
			file.delete();
	}

	@Override
	public PortafirmesIniciFluxResposta iniciarFluxDeFirma(String idioma, boolean isPlantilla, String nom,
			String descripcio, boolean descripcioVisible, String returnUrl) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PortafirmesFluxResposta recuperarFluxDeFirmaByIdTransaccio(String idTransaccio)
			throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PortafirmesFluxResposta> recuperarPlantillesDisponibles(String idioma) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PortafirmesFluxInfo recuperarFluxDeFirmaByIdPlantilla(String idTransaccio, String idioma)
			throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String recuperarUrlViewEditPlantilla(String idPlantilla, String idioma, String urlReturn, boolean edicio)
			throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean esborrarPlantillaFirma(String idioma, String plantillaFluxId) throws SistemaExternException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void tancarTransaccioFlux(String idTransaccio) throws SistemaExternException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<PortafirmesFluxResposta> recuperarPlantillesPerFiltre(String idioma, String descripcio)
			throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String recuperarUrlViewEstatFluxDeFirmes(long portafirmesId, String idioma) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PortafirmesCarrec> recuperarCarrecs() throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PortafirmesCarrec recuperarCarrec(String carrecId) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

}
