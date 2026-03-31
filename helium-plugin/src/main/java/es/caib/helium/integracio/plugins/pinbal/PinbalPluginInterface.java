package es.caib.helium.integracio.plugins.pinbal;

import es.caib.helium.commons.dto.ScspRespostaPinbal;

/**
 * Interfície per accedir a la funcionalitat de pinbal.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PinbalPluginInterface {

	public Object peticioClientPinbalGeneric(DadesConsultaPinbal dadesConsultaPinbal) throws Exception;
	
	public Object peticioClientPinbalSvddgpciws02(DadesConsultaPinbal dadesConsultaPinbal) throws Exception;
	
	public Object peticioClientPinbalSvddgpviws02(DadesConsultaPinbal dadesConsultaPinbal) throws Exception;

	public Object peticioClientPinbalSvdccaacpasws01(DadesConsultaPinbal dadesConsultaPinbal) throws Exception;

	public ScspRespostaPinbal getRespuestaPinbal(String peticioId) throws Exception;

	public Object getJustificantPinbal(String peticioId) throws Exception;
}