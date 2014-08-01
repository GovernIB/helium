package net.conselldemallorca.helium.wsintegraciones.custodiadocumentos.cliente;

public class CustodiaProxy implements net.conselldemallorca.helium.wsintegraciones.custodiadocumentos.cliente.Custodia {
  private String _endpoint = null;
  private net.conselldemallorca.helium.wsintegraciones.custodiadocumentos.cliente.Custodia custodia = null;
  
  public CustodiaProxy() {
    _initCustodiaProxy();
  }
  
  public CustodiaProxy(String endpoint) {
    _endpoint = endpoint;
    _initCustodiaProxy();
  }
  
  private void _initCustodiaProxy() {
    try {
      custodia = (new net.conselldemallorca.helium.wsintegraciones.custodiadocumentos.cliente.CustodiaServiceLocator()).getCustodiaDocumentos();
      if (custodia != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)custodia)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)custodia)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (custodia != null)
      ((javax.xml.rpc.Stub)custodia)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public net.conselldemallorca.helium.wsintegraciones.custodiadocumentos.cliente.Custodia getCustodia() {
    if (custodia == null)
      _initCustodiaProxy();
    return custodia;
  }
  
  public byte[] verificarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.verificarDocumento(in0, in1, in2);
  }
  
  public byte[] obtenerInformeDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.obtenerInformeDocumento(in0, in1, in2);
  }
  
  public byte[] consultarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.consultarDocumento(in0, in1, in2);
  }
  
  public byte[] custodiarPDFFirmado(java.lang.String in0) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.custodiarPDFFirmado(in0);
  }
  
  public byte[] custodiarPDFFirmado_v2(byte[] in0) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.custodiarPDFFirmado_v2(in0);
  }
  
  public byte[] custodiarDocumentoSMIME(java.lang.String in0) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.custodiarDocumentoSMIME(in0);
  }
  
  public byte[] custodiarDocumentoSMIME_v2(byte[] in0) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.custodiarDocumentoSMIME_v2(in0);
  }
  
  public byte[] custodiarDocumento(java.lang.String in0) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.custodiarDocumento(in0);
  }
  
  public byte[] custodiarDocumento_v2(byte[] in0) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.custodiarDocumento_v2(in0);
  }
  
  public byte[] custodiarDocumentoXAdES(byte[] in0) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.custodiarDocumentoXAdES(in0);
  }
  
  public byte[] purgarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.purgarDocumento(in0, in1, in2);
  }
  
  public byte[] purgarDocumento_v2(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.purgarDocumento_v2(in0, in1, in2);
  }
  
  public byte[] recuperarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.recuperarDocumento(in0, in1, in2);
  }
  
  public byte[] recuperarDocumento_v2(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.recuperarDocumento_v2(in0, in1, in2);
  }
  
  public byte[] eliminarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.eliminarDocumento(in0, in1, in2);
  }
  
  public byte[] eliminarDocumento_v2(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.eliminarDocumento_v2(in0, in1, in2);
  }
  
  public byte[] verificarDocumento_v2(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.verificarDocumento_v2(in0, in1, in2);
  }
  
  public byte[] obtenerInformeDocumento_v2(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.obtenerInformeDocumento_v2(in0, in1, in2);
  }
  
  public byte[] consultarDocumento_v2(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.consultarDocumento_v2(in0, in1, in2);
  }
  
  public byte[] reservarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.reservarDocumento(in0, in1, in2);
  }
  
  public byte[] reservarDocumento_v2(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.reservarDocumento_v2(in0, in1, in2);
  }
  
  public byte[] consultarReservaDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException{
    if (custodia == null)
      _initCustodiaProxy();
    return custodia.consultarReservaDocumento(in0, in1, in2);
  }
  
  
}