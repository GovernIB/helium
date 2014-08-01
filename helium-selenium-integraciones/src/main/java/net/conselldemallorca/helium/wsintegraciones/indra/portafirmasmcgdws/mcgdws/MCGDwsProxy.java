package net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws;

public class MCGDwsProxy implements net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.MCGDws {
  private String _endpoint = null;
  private net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.MCGDws mCGDws = null;
  
  public MCGDwsProxy() {
    _initMCGDwsProxy();
  }
  
  public MCGDwsProxy(String endpoint) {
    _endpoint = endpoint;
    _initMCGDwsProxy();
  }
  
  private void _initMCGDwsProxy() {
    try {
      mCGDws = (new net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.MCGDwsServiceLocator()).getMCGDWS();
      if (mCGDws != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)mCGDws)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)mCGDws)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (mCGDws != null)
      ((javax.xml.rpc.Stub)mCGDws)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.MCGDws getMCGDws() {
    if (mCGDws == null)
      _initMCGDwsProxy();
    return mCGDws;
  }
  
  public net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.CallbackResponse callback(net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.CallbackRequest callbackRequest) throws java.rmi.RemoteException{
    if (mCGDws == null)
      _initMCGDwsProxy();
    return mCGDws.callback(callbackRequest);
  }
  
  
}