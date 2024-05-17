/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.pinbal;

/**
 * Interfície per accedir a la funcionalitat de pinbal.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PinbalPluginInterface {

//	public Object peticioClientPinbal(DadesConsultaPinbal dadesConsultaPinbal) throws Exception;
	public Object peticioAsincronaClientPinbalGeneric(DadesConsultaPinbal dadesConsultaPinbal) throws Exception;
	
	
	
	public Object peticionSincronaClientPinbalGeneric(DadesConsultaPinbal dadesConsultaPinbal) throws Exception;

	public Object peticionSincronaClientPinbalSvddgpciws02(DadesConsultaPinbal dadesConsultaPinbal) throws Exception;
	
	public Object peticionSincronaClientPinbalSvddgpviws02(DadesConsultaPinbal dadesConsultaPinbal) throws Exception;

	public Object peticionSincronaClientPinbalSvdccaacpasws01(DadesConsultaPinbal dadesConsultaPinbal) throws Exception;

	public Object getRespuestaPinbal(String peticioId) throws Exception;
	

	public Object getJustificantPinbal(String peticioId) throws Exception;
	
	
}
