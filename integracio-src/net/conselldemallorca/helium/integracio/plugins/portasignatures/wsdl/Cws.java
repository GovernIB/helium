/**
 * Cws.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl;

public interface Cws extends java.rmi.Remote {
    public UploadResponse uploadDocument(UploadRequest uploadRequest) throws java.rmi.RemoteException;
    public DownloadResponse downloadDocument(DownloadRequest downloadRequest) throws java.rmi.RemoteException;
    public UpdateResponse updateDocument(UpdateRequest updateRequest) throws java.rmi.RemoteException;
    public DeleteResponse deleteDocuments(DeleteRequest deleteRequest) throws java.rmi.RemoteException;
    public ListResponse listDocuments(ListRequest listRequest) throws java.rmi.RemoteException;
    public SearchResponse searchDocuments(SearchRequest searchRequest) throws java.rmi.RemoteException;
    public ListTypeResponse listTypeDocuments(ListTypeRequest listTypeRequest) throws java.rmi.RemoteException;
    public ListServerSignersResponse listServerSigners(ListServerSignersRequest listServerSignersRequest) throws java.rmi.RemoteException;
    public DownloadFileResponse downloadFile(DownloadFileRequest downloadFileRequest) throws java.rmi.RemoteException;
}
