/**
 * Cws.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente;

public interface Cws extends java.rmi.Remote {
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadResponse uploadDocument(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadRequest uploadRequest) throws java.rmi.RemoteException;
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadResponse downloadDocument(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadRequest downloadRequest) throws java.rmi.RemoteException;
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateResponse updateDocument(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateRequest updateRequest) throws java.rmi.RemoteException;
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteResponse deleteDocuments(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteRequest deleteRequest) throws java.rmi.RemoteException;
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListResponse listDocuments(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListRequest listRequest) throws java.rmi.RemoteException;
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchResponse searchDocuments(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchRequest searchRequest) throws java.rmi.RemoteException;
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListTypeResponse listTypeDocuments(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListTypeRequest listTypeRequest) throws java.rmi.RemoteException;
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListServerSignersResponse listServerSigners(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListServerSignersRequest listServerSignersRequest) throws java.rmi.RemoteException;
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadFileResponse downloadFile(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadFileRequest downloadFileRequest) throws java.rmi.RemoteException;
}
