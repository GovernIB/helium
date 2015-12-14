package services.v2.ws.bantel.caib.es;

import BantelFacade.model.v2.ws.bantel.caib.es.BantelFacadeException;

public class BantelFacadeProxy implements services.v2.ws.bantel.caib.es.BantelFacade {
  private String _endpoint = null;
  private services.v2.ws.bantel.caib.es.BantelFacade bantelFacade = null;
  
  public BantelFacadeProxy() {
    _initBantelFacadeProxy();
  }
  
  public BantelFacadeProxy(String endpoint) {
    _endpoint = endpoint;
    _initBantelFacadeProxy();
  }
  
  private void _initBantelFacadeProxy() {
    try {
      bantelFacade = (new net.conselldemallorca.helium.ws.backoffice.BantelV3BackofficeServiceLocator()).getBantelV3BackofficePort();
      if (bantelFacade != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)bantelFacade)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)bantelFacade)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (bantelFacade != null)
      ((javax.xml.rpc.Stub)bantelFacade)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public services.v2.ws.bantel.caib.es.BantelFacade getBantelFacade() {
    if (bantelFacade == null)
      _initBantelFacadeProxy();
    return bantelFacade;
  }
  
  public void avisoEntradas(ReferenciaEntrada.model.v2.ws.bantel.caib.es.ReferenciaEntrada[] numeroEntradas) throws java.rmi.RemoteException, BantelFacadeException {
    if (bantelFacade == null)
      _initBantelFacadeProxy();
    bantelFacade.avisoEntradas(numeroEntradas);
  }
  
  
}