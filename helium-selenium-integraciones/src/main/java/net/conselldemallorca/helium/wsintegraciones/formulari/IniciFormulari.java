package net.conselldemallorca.helium.wsintegraciones.formulari;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(targetNamespace = "https://proves.caib.es/signatura/services/IniciFormulari", name = "IniciFormulari")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IniciFormulari {

	@WebMethod(operationName = "iniciFormulari", action = "IniciFormulari")
	@WebResult(name = "iniciFormulariResponse", targetNamespace = "https://proves.caib.es/signatura/services/IniciFormulari", partName = "iniciFormulariResponse")
	public RespostaIniciFormulari iniciFormulari(
			@WebParam(partName = "in0", name = "in0")			
			String codi,
			@WebParam(partName = "in1", name = "in1")
			String taskId,
			@WebParam(partName = "in2", name = "in2")
			List<ParellaCodiValor> valors);
	
}
