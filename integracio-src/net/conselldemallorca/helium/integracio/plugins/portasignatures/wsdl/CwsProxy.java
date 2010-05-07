package net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl;

public class CwsProxy implements Cws {
  private String _endpoint = null;
  private Cws cws = null;
  
  public CwsProxy() {
    _initCwsProxy();
  }
  
  public CwsProxy(String endpoint) {
    _endpoint = endpoint;
    _initCwsProxy();
  }
  
  private void _initCwsProxy() {
    try {
      cws = (new CwsServiceLocator()).getCWS();
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
  
  public Cws getCws() {
    if (cws == null)
      _initCwsProxy();
    return cws;
  }
  
  public UploadResponse uploadDocument(UploadRequest uploadRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.uploadDocument(uploadRequest);
  }
  
  public DownloadResponse downloadDocument(DownloadRequest downloadRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.downloadDocument(downloadRequest);
  }
  
  public UpdateResponse updateDocument(UpdateRequest updateRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.updateDocument(updateRequest);
  }
  
  public DeleteResponse deleteDocuments(DeleteRequest deleteRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.deleteDocuments(deleteRequest);
  }
  
  public ListResponse listDocuments(ListRequest listRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.listDocuments(listRequest);
  }
  
  public SearchResponse searchDocuments(SearchRequest searchRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.searchDocuments(searchRequest);
  }
  
  public ListTypeResponse listTypeDocuments(ListTypeRequest listTypeRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.listTypeDocuments(listTypeRequest);
  }
  
  public ListServerSignersResponse listServerSigners(ListServerSignersRequest listServerSignersRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.listServerSigners(listServerSignersRequest);
  }
  
  public DownloadFileResponse downloadFile(DownloadFileRequest downloadFileRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.downloadFile(downloadFileRequest);
  }
  
  
}