/**
 * BantelV3BackofficeServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.ws.backoffice;

public class BantelV3BackofficeServiceLocator extends org.apache.axis.client.Service implements net.conselldemallorca.helium.ws.backoffice.BantelV3BackofficeService {

    public BantelV3BackofficeServiceLocator() {
    }


    public BantelV3BackofficeServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public BantelV3BackofficeServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BantelV3BackofficePort
    private java.lang.String BantelV3BackofficePort_address = "https://proves.caib.es/helium/ws/NotificacioEntradaV3";

    public java.lang.String getBantelV3BackofficePortAddress() {
        return BantelV3BackofficePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BantelV3BackofficePortWSDDServiceName = "BantelV3BackofficePort";

    public java.lang.String getBantelV3BackofficePortWSDDServiceName() {
        return BantelV3BackofficePortWSDDServiceName;
    }

    public void setBantelV3BackofficePortWSDDServiceName(java.lang.String name) {
        BantelV3BackofficePortWSDDServiceName = name;
    }

    public services.v2.ws.bantel.caib.es.BantelFacade getBantelV3BackofficePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BantelV3BackofficePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBantelV3BackofficePort(endpoint);
    }

    public services.v2.ws.bantel.caib.es.BantelFacade getBantelV3BackofficePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            net.conselldemallorca.helium.ws.backoffice.BantelV3BackofficeServiceSoapBindingStub _stub = new net.conselldemallorca.helium.ws.backoffice.BantelV3BackofficeServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getBantelV3BackofficePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBantelV3BackofficePortEndpointAddress(java.lang.String address) {
        BantelV3BackofficePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (services.v2.ws.bantel.caib.es.BantelFacade.class.isAssignableFrom(serviceEndpointInterface)) {
                net.conselldemallorca.helium.ws.backoffice.BantelV3BackofficeServiceSoapBindingStub _stub = new net.conselldemallorca.helium.ws.backoffice.BantelV3BackofficeServiceSoapBindingStub(new java.net.URL(BantelV3BackofficePort_address), this);
                _stub.setPortName(getBantelV3BackofficePortWSDDServiceName());
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
        if ("BantelV3BackofficePort".equals(inputPortName)) {
            return getBantelV3BackofficePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://backoffice.ws.helium.conselldemallorca.net/", "BantelV3BackofficeService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://backoffice.ws.helium.conselldemallorca.net/", "BantelV3BackofficePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("BantelV3BackofficePort".equals(portName)) {
            setBantelV3BackofficePortEndpointAddress(address);
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
