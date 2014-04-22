/**
 * CWSSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.indra.www.portafirmasws.cws;

public class CWSSoapBindingSkeleton implements es.indra.www.portafirmasws.cws.Cws, org.apache.axis.wsdl.Skeleton {
    private es.indra.www.portafirmasws.cws.Cws impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "upload-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">upload-request"), es.indra.www.portafirmasws.cws.UploadRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("uploadDocument", _params, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "upload-response"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">upload-response"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "UploadDocument"));
        _oper.setSoapAction("UploadDocument");
        _myOperationsList.add(_oper);
        if (_myOperations.get("uploadDocument") == null) {
            _myOperations.put("uploadDocument", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("uploadDocument")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "download-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">download-request"), es.indra.www.portafirmasws.cws.DownloadRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("downloadDocument", _params, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "download-response"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">download-response"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "DownloadDocument"));
        _oper.setSoapAction("DownloadDocument");
        _myOperationsList.add(_oper);
        if (_myOperations.get("downloadDocument") == null) {
            _myOperations.put("downloadDocument", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("downloadDocument")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "update-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">update-request"), es.indra.www.portafirmasws.cws.UpdateRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("updateDocument", _params, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "update-response"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">update-response"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "UpdateDocument"));
        _oper.setSoapAction("UpdateDocument");
        _myOperationsList.add(_oper);
        if (_myOperations.get("updateDocument") == null) {
            _myOperations.put("updateDocument", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("updateDocument")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "delete-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">delete-request"), es.indra.www.portafirmasws.cws.DeleteRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteDocuments", _params, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "delete-response"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">delete-response"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "DeleteDocuments"));
        _oper.setSoapAction("DeleteDocuments");
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteDocuments") == null) {
            _myOperations.put("deleteDocuments", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteDocuments")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "list-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">list-request"), es.indra.www.portafirmasws.cws.ListRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("listDocuments", _params, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "list-response"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">list-response"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "ListDocuments"));
        _oper.setSoapAction("ListDocuments");
        _myOperationsList.add(_oper);
        if (_myOperations.get("listDocuments") == null) {
            _myOperations.put("listDocuments", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("listDocuments")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "search-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">search-request"), es.indra.www.portafirmasws.cws.SearchRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("searchDocuments", _params, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "search-response"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">search-response"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "SearchDocuments"));
        _oper.setSoapAction("SearchDocuments");
        _myOperationsList.add(_oper);
        if (_myOperations.get("searchDocuments") == null) {
            _myOperations.put("searchDocuments", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("searchDocuments")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "listType-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">listType-request"), es.indra.www.portafirmasws.cws.ListTypeRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("listTypeDocuments", _params, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "listType-response"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">listType-response"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "ListTypeDocuments"));
        _oper.setSoapAction("ListTypeDocuments");
        _myOperationsList.add(_oper);
        if (_myOperations.get("listTypeDocuments") == null) {
            _myOperations.put("listTypeDocuments", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("listTypeDocuments")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "listServerSigners-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">listServerSigners-request"), es.indra.www.portafirmasws.cws.ListServerSignersRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("listServerSigners", _params, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "listServerSigners-response"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">listServerSigners-response"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "ListServerSigners"));
        _oper.setSoapAction("ListServerSigners");
        _myOperationsList.add(_oper);
        if (_myOperations.get("listServerSigners") == null) {
            _myOperations.put("listServerSigners", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("listServerSigners")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "download-file-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">download-file-request"), es.indra.www.portafirmasws.cws.DownloadFileRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("downloadFile", _params, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "download-file-response"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">download-file-response"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "DownloadFile"));
        _oper.setSoapAction("DownloadFile");
        _myOperationsList.add(_oper);
        if (_myOperations.get("downloadFile") == null) {
            _myOperations.put("downloadFile", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("downloadFile")).add(_oper);
    }

    public CWSSoapBindingSkeleton() {
        this.impl = new es.indra.www.portafirmasws.cws.CWSSoapBindingImpl();
    }

    public CWSSoapBindingSkeleton(es.indra.www.portafirmasws.cws.Cws impl) {
        this.impl = impl;
    }
    public es.indra.www.portafirmasws.cws.UploadResponse uploadDocument(es.indra.www.portafirmasws.cws.UploadRequest uploadRequest) throws java.rmi.RemoteException
    {
        es.indra.www.portafirmasws.cws.UploadResponse ret = impl.uploadDocument(uploadRequest);
        return ret;
    }

    public es.indra.www.portafirmasws.cws.DownloadResponse downloadDocument(es.indra.www.portafirmasws.cws.DownloadRequest downloadRequest) throws java.rmi.RemoteException
    {
        es.indra.www.portafirmasws.cws.DownloadResponse ret = impl.downloadDocument(downloadRequest);
        return ret;
    }

    public es.indra.www.portafirmasws.cws.UpdateResponse updateDocument(es.indra.www.portafirmasws.cws.UpdateRequest updateRequest) throws java.rmi.RemoteException
    {
        es.indra.www.portafirmasws.cws.UpdateResponse ret = impl.updateDocument(updateRequest);
        return ret;
    }

    public es.indra.www.portafirmasws.cws.DeleteResponse deleteDocuments(es.indra.www.portafirmasws.cws.DeleteRequest deleteRequest) throws java.rmi.RemoteException
    {
        es.indra.www.portafirmasws.cws.DeleteResponse ret = impl.deleteDocuments(deleteRequest);
        return ret;
    }

    public es.indra.www.portafirmasws.cws.ListResponse listDocuments(es.indra.www.portafirmasws.cws.ListRequest listRequest) throws java.rmi.RemoteException
    {
        es.indra.www.portafirmasws.cws.ListResponse ret = impl.listDocuments(listRequest);
        return ret;
    }

    public es.indra.www.portafirmasws.cws.SearchResponse searchDocuments(es.indra.www.portafirmasws.cws.SearchRequest searchRequest) throws java.rmi.RemoteException
    {
        es.indra.www.portafirmasws.cws.SearchResponse ret = impl.searchDocuments(searchRequest);
        return ret;
    }

    public es.indra.www.portafirmasws.cws.ListTypeResponse listTypeDocuments(es.indra.www.portafirmasws.cws.ListTypeRequest listTypeRequest) throws java.rmi.RemoteException
    {
        es.indra.www.portafirmasws.cws.ListTypeResponse ret = impl.listTypeDocuments(listTypeRequest);
        return ret;
    }

    public es.indra.www.portafirmasws.cws.ListServerSignersResponse listServerSigners(es.indra.www.portafirmasws.cws.ListServerSignersRequest listServerSignersRequest) throws java.rmi.RemoteException
    {
        es.indra.www.portafirmasws.cws.ListServerSignersResponse ret = impl.listServerSigners(listServerSignersRequest);
        return ret;
    }

    public es.indra.www.portafirmasws.cws.DownloadFileResponse downloadFile(es.indra.www.portafirmasws.cws.DownloadFileRequest downloadFileRequest) throws java.rmi.RemoteException
    {
        es.indra.www.portafirmasws.cws.DownloadFileResponse ret = impl.downloadFile(downloadFileRequest);
        return ret;
    }

}
