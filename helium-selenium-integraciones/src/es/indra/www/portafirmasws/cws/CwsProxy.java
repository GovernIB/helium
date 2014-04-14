package es.indra.www.portafirmasws.cws;

public class CwsProxy implements es.indra.www.portafirmasws.cws.Cws {
  private String _endpoint = null;
  private es.indra.www.portafirmasws.cws.Cws cws = null;
  
  public CwsProxy() {
    _initCwsProxy();
  }
  
  public CwsProxy(String endpoint) {
    _endpoint = endpoint;
    _initCwsProxy();
  }
  
  private void _initCwsProxy() {
    try {
      cws = (new es.indra.www.portafirmasws.cws.CwsServiceLocator()).getCWS();
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
  
  public es.indra.www.portafirmasws.cws.Cws getCws() {
    if (cws == null)
      _initCwsProxy();
    return cws;
  }
  
  public es.indra.www.portafirmasws.cws.UploadResponse uploadDocument(es.indra.www.portafirmasws.cws.UploadRequest uploadRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.uploadDocument(uploadRequest);
  }
  
  public es.indra.www.portafirmasws.cws.DownloadResponse downloadDocument(es.indra.www.portafirmasws.cws.DownloadRequest downloadRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.downloadDocument(downloadRequest);
  }
  
  public es.indra.www.portafirmasws.cws.UpdateResponse updateDocument(es.indra.www.portafirmasws.cws.UpdateRequest updateRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.updateDocument(updateRequest);
  }
  
  public es.indra.www.portafirmasws.cws.DeleteResponse deleteDocuments(es.indra.www.portafirmasws.cws.DeleteRequest deleteRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.deleteDocuments(deleteRequest);
  }
  
  public es.indra.www.portafirmasws.cws.ListResponse listDocuments(es.indra.www.portafirmasws.cws.ListRequest listRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.listDocuments(listRequest);
  }
  
  public es.indra.www.portafirmasws.cws.SearchResponse searchDocuments(es.indra.www.portafirmasws.cws.SearchRequest searchRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.searchDocuments(searchRequest);
  }
  
  public es.indra.www.portafirmasws.cws.ListTypeResponse listTypeDocuments(es.indra.www.portafirmasws.cws.ListTypeRequest listTypeRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.listTypeDocuments(listTypeRequest);
  }
  
  public es.indra.www.portafirmasws.cws.ListServerSignersResponse listServerSigners(es.indra.www.portafirmasws.cws.ListServerSignersRequest listServerSignersRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.listServerSigners(listServerSignersRequest);
  }
  
  public es.indra.www.portafirmasws.cws.DownloadFileResponse downloadFile(es.indra.www.portafirmasws.cws.DownloadFileRequest downloadFileRequest) throws java.rmi.RemoteException{
    if (cws == null)
      _initCwsProxy();
    return cws.downloadFile(downloadFileRequest);
  }
  
  
}