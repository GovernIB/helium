package es.caib.helium.integracio.service.custodia;

import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.custodia.CustodiaRequest;
import es.caib.helium.integracio.domini.validacio.RespostaValidacioSignatura;
import es.caib.helium.integracio.excepcions.custodia.CustodiaException;

@Service
public interface CustodiaService {

	public String addSignature(CustodiaRequest request) throws CustodiaException;

	public List<byte[]> getSignatures(String docId) throws CustodiaException;
	
	public boolean deleteSignatures(String docId) throws CustodiaException;
	
	public byte[] getSignaturesAmbArxiu(String docId) throws CustodiaException;
	
	public List<RespostaValidacioSignatura> dadesValidacioSignatura(String id) throws CustodiaException;
	
	public String getUrlComprovacioSignatura(String docId) throws CustodiaException;
}
