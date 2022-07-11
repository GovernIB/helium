/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.pinbal;

import java.io.IOException;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * Interfície per accedir a la funcionalitat de pinbal.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PinbalPluginInterface {

	public Object peticionSincronaClientPinbalGeneric(DadesConsultaPinbal dadesConsultaPinbal) throws UniformInterfaceException, ClientHandlerException, IOException;

	public Object peticionSincronaClientPinbalSvddgpciws02(DadesConsultaPinbal dadesConsultaPinbal) throws UniformInterfaceException, ClientHandlerException, IOException;
	
	public Object peticionSincronaClientPinbalSvddgpviws02(DadesConsultaPinbal dadesConsultaPinbal) throws UniformInterfaceException, ClientHandlerException, IOException;

	public Object peticionSincronaClientPinbalSvdccaacpasws01(DadesConsultaPinbal dadesConsultaPinbal) throws UniformInterfaceException, ClientHandlerException, IOException;

	public Object getRespuestaPinbal(String peticioId) throws IOException;
	

	public Object getJustificantPinbal(String peticioId) throws IOException;
	
	
}
