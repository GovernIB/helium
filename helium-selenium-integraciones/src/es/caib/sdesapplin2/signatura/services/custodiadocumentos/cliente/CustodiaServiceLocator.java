/**
 * CustodiaServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.caib.sdesapplin2.signatura.services.custodiadocumentos.cliente;

public class CustodiaServiceLocator extends org.apache.axis.client.Service implements es.caib.sdesapplin2.signatura.services.custodiadocumentos.cliente.CustodiaService {

    public CustodiaServiceLocator() {
    }


    public CustodiaServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CustodiaServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CustodiaDocumentos
    private java.lang.String CustodiaDocumentos_address = "http://sdesapplin2.caib.es/signatura/services/CustodiaDocumentos";

    public java.lang.String getCustodiaDocumentosAddress() {
        return CustodiaDocumentos_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CustodiaDocumentosWSDDServiceName = "CustodiaDocumentos";

    public java.lang.String getCustodiaDocumentosWSDDServiceName() {
        return CustodiaDocumentosWSDDServiceName;
    }

    public void setCustodiaDocumentosWSDDServiceName(java.lang.String name) {
        CustodiaDocumentosWSDDServiceName = name;
    }

    public es.caib.sdesapplin2.signatura.services.custodiadocumentos.cliente.Custodia getCustodiaDocumentos() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CustodiaDocumentos_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCustodiaDocumentos(endpoint);
    }

    public es.caib.sdesapplin2.signatura.services.custodiadocumentos.cliente.Custodia getCustodiaDocumentos(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            es.caib.sdesapplin2.signatura.services.custodiadocumentos.cliente.CustodiaDocumentosSoapBindingStub _stub = new es.caib.sdesapplin2.signatura.services.custodiadocumentos.cliente.CustodiaDocumentosSoapBindingStub(portAddress, this);
            _stub.setPortName(getCustodiaDocumentosWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCustodiaDocumentosEndpointAddress(java.lang.String address) {
        CustodiaDocumentos_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (es.caib.sdesapplin2.signatura.services.custodiadocumentos.cliente.Custodia.class.isAssignableFrom(serviceEndpointInterface)) {
                es.caib.sdesapplin2.signatura.services.custodiadocumentos.cliente.CustodiaDocumentosSoapBindingStub _stub = new es.caib.sdesapplin2.signatura.services.custodiadocumentos.cliente.CustodiaDocumentosSoapBindingStub(new java.net.URL(CustodiaDocumentos_address), this);
                _stub.setPortName(getCustodiaDocumentosWSDDServiceName());
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
        if ("CustodiaDocumentos".equals(inputPortName)) {
            return getCustodiaDocumentos();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://sdesapplin2.caib.es/signatura/services/CustodiaDocumentos", "CustodiaService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://sdesapplin2.caib.es/signatura/services/CustodiaDocumentos", "CustodiaDocumentos"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("CustodiaDocumentos".equals(portName)) {
            setCustodiaDocumentosEndpointAddress(address);
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
