package es.prova.handler;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.HeliumHandlerException;
import es.caib.helium.disseny.handler.HeliumActionHandler;

import java.io.InputStream;
import java.util.Date;

public class ProvaHandler implements HeliumActionHandler {

	private static final String FILE_NAME = "prova.pdf";

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.setVariable("varString", "Bon dia tot lo dia");
		try (InputStream is = getClass().getResourceAsStream("/" + FILE_NAME)) {
			heliumApi.setDocument(
				"doc1",
				FILE_NAME,
				is.readAllBytes(),
				new Date(),
				false,
				false,
				null);
		} catch (Exception ex) {
			throw new HeliumHandlerException("Error al executar el handler de prova", ex);
		}
	}

}
