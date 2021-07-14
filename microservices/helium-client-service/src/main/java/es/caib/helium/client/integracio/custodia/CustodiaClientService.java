package es.caib.helium.client.integracio.custodia;

import java.util.List;

import es.caib.helium.client.model.RespostaValidacioSignatura;
import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.custodia.model.CustodiaRequest;

@Service
public interface CustodiaClientService {
	
	public String afegirSignatura(CustodiaRequest request, Long entornId);
	
	public List<byte[]> getSignatures(String documentId, Long entornId);
	
	public void deleteSignatures(String documentId, Long entornId);
	
	public byte[] getSignaturesAmbArxiu(String documentId, Long entornId);
	
	public List<RespostaValidacioSignatura> getDadesValidacioSignatura(String documentId, Long entornId);
	
	public String getUrlComprovacioSignatures(String documentId, Long entornId);
}
