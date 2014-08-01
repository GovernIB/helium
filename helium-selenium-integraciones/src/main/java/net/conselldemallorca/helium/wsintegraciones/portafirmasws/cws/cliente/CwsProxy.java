package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente;

public class CwsProxy implements net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Cws {
  private String _endpoint = null;
  private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Cws cws = null;
  
  public CwsProxy() {
    _initCwsProxy();
  }
  
  public CwsProxy(String endpoint) {
    _endpoint = endpoint;
    _initCwsProxy();
  }
  
  private void _initCwsProxy() {
    try {
      cws = (new net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.CwsServiceLocator()).getCWS();
      if (cws != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)cws)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)cws)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (cws != null)
      ((javax.xml.rpc.Stub)cws)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Cws getCws() {
    if (cws == null)
      _initCwsProxy();
    return cws;
  }
  
  public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadResponse uploadDocument(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadRequest uploadRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.uploadDocument(uploadRequest);
  }
  
  public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadResponse downloadDocument(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadRequest downloadRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.downloadDocument(downloadRequest);
  }
  
  public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateResponse updateDocument(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateRequest updateRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.updateDocument(updateRequest);
  }
  
  public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteResponse deleteDocuments(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteRequest deleteRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.deleteDocuments(deleteRequest);
  }
  
  public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListResponse listDocuments(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListRequest listRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.listDocuments(listRequest);
  }
  
  public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchResponse searchDocuments(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchRequest searchRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.searchDocuments(searchRequest);
  }
  
  public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListTypeResponse listTypeDocuments(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListTypeRequest listTypeRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.listTypeDocuments(listTypeRequest);
  }
  
  public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListServerSignersResponse listServerSigners(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListServerSignersRequest listServerSignersRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.listServerSigners(listServerSignersRequest);
  }
  
  public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadFileResponse downloadFile(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadFileRequest downloadFileRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.downloadFile(downloadFileRequest);
  }
  
  
}