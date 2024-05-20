package net.conselldemallorca.helium.integracio.plugins.pinbal;

import net.conselldemallorca.helium.v3.core.api.dto.ScspRespostaPinbal;

/**
 * Interfície per accedir a la funcionalitat de pinbal.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PinbalPluginInterface {

	public Object peticioAsincronaClientPinbalGeneric(DadesConsultaPinbal dadesConsultaPinbal) throws Exception;
	
	public Object peticionSincronaClientPinbalGeneric(DadesConsultaPinbal dadesConsultaPinbal) throws Exception;

	public Object peticionSincronaClientPinbalSvddgpciws02(DadesConsultaPinbal dadesConsultaPinbal) throws Exception;
	
	public Object peticionSincronaClientPinbalSvddgpviws02(DadesConsultaPinbal dadesConsultaPinbal) throws Exception;

	public Object peticionSincronaClientPinbalSvdccaacpasws01(DadesConsultaPinbal dadesConsultaPinbal) throws Exception;

	public ScspRespostaPinbal getRespuestaPinbal(String peticioId) throws Exception;

	public Object getJustificantPinbal(String peticioId) throws Exception;
}