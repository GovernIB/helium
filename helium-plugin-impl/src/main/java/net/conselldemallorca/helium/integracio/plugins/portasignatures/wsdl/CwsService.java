/**
 * CwsService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl;

public interface CwsService extends javax.xml.rpc.Service {
    public java.lang.String getCWSAddress();

    public Cws getCWS() throws javax.xml.rpc.ServiceException;

    public Cws getCWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
