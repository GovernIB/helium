/**
 * MCGDwsServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws;

public class MCGDwsServiceLocator extends org.apache.axis.client.Service implements net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.MCGDwsService {

    public MCGDwsServiceLocator() {
    }


    public MCGDwsServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MCGDwsServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MCGDWS
    private java.lang.String MCGDWS_address = "http://localhost:8080/helium/services/MCGDWS";

    public java.lang.String getMCGDWSAddress() {
        return MCGDWS_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MCGDWSWSDDServiceName = "MCGDWS";

    public java.lang.String getMCGDWSWSDDServiceName() {
        return MCGDWSWSDDServiceName;
    }

    public void setMCGDWSWSDDServiceName(java.lang.String name) {
        MCGDWSWSDDServiceName = name;
    }

    public net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.MCGDws getMCGDWS() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MCGDWS_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMCGDWS(endpoint);
    }

    public net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.MCGDws getMCGDWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.MCGDWSSoapBindingStub _stub = new net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.MCGDWSSoapBindingStub(portAddress, this);
            _stub.setPortName(getMCGDWSWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMCGDWSEndpointAddress(java.lang.String address) {
        MCGDWS_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.MCGDws.class.isAssignableFrom(serviceEndpointInterface)) {
                net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.MCGDWSSoapBindingStub _stub = new net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.MCGDWSSoapBindingStub(new java.net.URL(MCGDWS_address), this);
                _stub.setPortName(getMCGDWSWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("MCGDWS".equals(inputPortName)) {
            return getMCGDWS();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "MCGDwsService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "MCGDWS"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MCGDWS".equals(portName)) {
            setMCGDWSEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
