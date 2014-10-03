/**
 * BantelV2BackofficeService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.ws.backoffice;

public interface BantelV3BackofficeService extends javax.xml.rpc.Service {
    
	public java.lang.String getBantelV3BackofficePortAddress();

    public services.v2.ws.bantel.caib.es.BantelFacade getBantelV3BackofficePort() throws javax.xml.rpc.ServiceException;

    public services.v2.ws.bantel.caib.es.BantelFacade getBantelV3BackofficePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
