/**
 * CWSSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente;

public class CWSSoapBindingStub extends org.apache.axis.client.Stub implements net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Cws {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[9];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("UploadDocument");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "upload-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">upload-request"), net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">upload-response"));
        oper.setReturnClass(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "upload-response"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("DownloadDocument");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "download-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">download-request"), net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">download-response"));
        oper.setReturnClass(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "download-response"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("UpdateDocument");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "update-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">update-request"), net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">update-response"));
        oper.setReturnClass(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "update-response"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("DeleteDocuments");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "delete-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">delete-request"), net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">delete-response"));
        oper.setReturnClass(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "delete-response"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ListDocuments");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "list-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">list-request"), net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">list-response"));
        oper.setReturnClass(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "list-response"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SearchDocuments");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "search-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">search-request"), net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">search-response"));
        oper.setReturnClass(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "search-response"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ListTypeDocuments");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "listType-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">listType-request"), net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListTypeRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">listType-response"));
        oper.setReturnClass(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListTypeResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "listType-response"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ListServerSigners");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "listServerSigners-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">listServerSigners-request"), net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListServerSignersRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">listServerSigners-response"));
        oper.setReturnClass(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListServerSignersResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "listServerSigners-response"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("DownloadFile");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "download-file-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">download-file-request"), net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadFileRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">download-file-response"));
        oper.setReturnClass(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadFileResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "download-file-response"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

    }

    public CWSSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public CWSSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public CWSSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">delete-request");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">delete-response");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">download-file-request");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadFileRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">download-file-response");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadFileResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">download-request");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">download-response");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">list-request");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">list-response");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">listServerSigners-request");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListServerSignersRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">listServerSigners-response");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListServerSignersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">listType-request");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListTypeRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">listType-response");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListTypeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">search-request");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">search-response");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">update-request");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">update-response");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">upload-request");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">upload-response");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Annex");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Annex.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Annexes");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Annex[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Annex");
            qName2 = new javax.xml.namespace.QName("", "annex");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Application");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Application.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ArchiveLocator");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ArchiveLocator.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ArchiveMetadata");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ArchiveMetadata.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ArchiveMetadatas");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ArchiveMetadata[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ArchiveMetadata");
            qName2 = new javax.xml.namespace.QName("", "archive-metadata");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ArchiveOptions");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ArchiveOptions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Certificate");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Certificate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "CodificationTypeEnum");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.CodificationTypeEnum.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ConditionEnum");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ConditionEnum.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "CriteriaEnum");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.CriteriaEnum.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Delegate");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Delegate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Delegates");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Delegate[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Delegate");
            qName2 = new javax.xml.namespace.QName("", "delegate");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DeleteRequestDocument");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteRequestDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DeleteRequestDocuments");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteRequestDocument[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DeleteRequestDocument");
            qName2 = new javax.xml.namespace.QName("", "document");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DeleteResponseDocument");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteResponseDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DeleteResponseDocuments");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteResponseDocument[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DeleteResponseDocument");
            qName2 = new javax.xml.namespace.QName("", "document");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DestinationLocators");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ArchiveLocator[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ArchiveLocator");
            qName2 = new javax.xml.namespace.QName("", "archive-locator");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DocumentAttributes");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DocumentAttributes.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DownloadFileRequestDocument");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadFileRequestDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DownloadFileResponseDocument");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadFileResponseDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DownloadOptions");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadOptions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DownloadRequestDocument");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadRequestDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DownloadResponseDocument");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadResponseDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DownloadStep");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadStep.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ExternalIDs");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ExternalIDs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Field");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Field.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "File");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.File.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Files");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.File[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "File");
            qName2 = new javax.xml.namespace.QName("", "file");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Format");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Format.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ImportanceEnum");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ImportanceEnum.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Job");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Job.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ListRequestDocument");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListRequestDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ListRequestDocuments");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListRequestDocument[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ListRequestDocument");
            qName2 = new javax.xml.namespace.QName("", "document");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ListResponseDocument");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListResponseDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ListResponseDocuments");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListResponseDocument[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ListResponseDocument");
            qName2 = new javax.xml.namespace.QName("", "document");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ListStep");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListStep.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ModeTypeEnum");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ModeTypeEnum.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "PdfAppearance");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.PdfAppearance.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "PdfPosition");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.PdfPosition.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "PdfTypeEnum");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.PdfTypeEnum.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "PendingDocuments");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.PendingDocuments.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "PendingResult");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.PendingResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Position");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Position.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Positions");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Position[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Position");
            qName2 = new javax.xml.namespace.QName("", "positions");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ProfileEnum");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ProfileEnum.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Rejection");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Rejection.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Result");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Result.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SearchCriterias");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Field[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Field");
            qName2 = new javax.xml.namespace.QName("", "search-criteria");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SearchResult");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Sender");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Sender.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ServerSigner");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ServerSigner.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ServerSigners");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ServerSigner[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ServerSigner");
            qName2 = new javax.xml.namespace.QName("", "serverSigner");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignatureFile");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignatureFile.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignatureFiles");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignatureFile[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignatureFile");
            qName2 = new javax.xml.namespace.QName("", "file");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignatureImage");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignatureImage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignatureImageDimensions");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignatureImageDimensions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Signer");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Signer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignerID");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignerID.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Signers");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Signer[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Signer");
            qName2 = new javax.xml.namespace.QName("", "signer");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignersID");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignerID[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignerID");
            qName2 = new javax.xml.namespace.QName("", "id");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignerSignatureFiles");
            cachedSerQNames.add(qName);
            cls = int[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int");
            qName2 = new javax.xml.namespace.QName("", "file");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignModeEnum");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignModeEnum.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SourceLocators");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ArchiveLocator[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ArchiveLocator");
            qName2 = new javax.xml.namespace.QName("", "archive-locator");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "StateEnum");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.StateEnum.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Step");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Step.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Steps");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Steps.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Substitute");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Substitute.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Substitutes");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Substitute[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Substitute");
            qName2 = new javax.xml.namespace.QName("", "substitute");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "TypeDocument");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.TypeDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "TypeDocuments");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.TypeDocument[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "TypeDocument");
            qName2 = new javax.xml.namespace.QName("", "type");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "TypeEnum");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.TypeEnum.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "UpdateRequestDocument");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateRequestDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "UpdateResponseDocument");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateResponseDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "UpdateStep");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateStep.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "UploadRequestDocument");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadRequestDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "UploadResponseDocument");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadResponseDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "UploadStep");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadStep.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "VerificationCode");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.VerificationCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "VisualFile");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.VisualFile.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "VisualFiles");
            cachedSerQNames.add(qName);
            cls = net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.VisualFile[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "VisualFile");
            qName2 = new javax.xml.namespace.QName("", "file");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadResponse uploadDocument(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadRequest uploadRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("UploadDocument");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "UploadDocument"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {uploadRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadResponse) org.apache.axis.utils.JavaUtils.convert(_resp, net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadResponse downloadDocument(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadRequest downloadRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("DownloadDocument");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "DownloadDocument"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {downloadRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadResponse) org.apache.axis.utils.JavaUtils.convert(_resp, net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateResponse updateDocument(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateRequest updateRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("UpdateDocument");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "UpdateDocument"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {updateRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateResponse) org.apache.axis.utils.JavaUtils.convert(_resp, net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UpdateResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteResponse deleteDocuments(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteRequest deleteRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("DeleteDocuments");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "DeleteDocuments"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {deleteRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteResponse) org.apache.axis.utils.JavaUtils.convert(_resp, net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListResponse listDocuments(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListRequest listRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("ListDocuments");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "ListDocuments"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {listRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListResponse) org.apache.axis.utils.JavaUtils.convert(_resp, net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchResponse searchDocuments(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchRequest searchRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("SearchDocuments");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "SearchDocuments"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {searchRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchResponse) org.apache.axis.utils.JavaUtils.convert(_resp, net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListTypeResponse listTypeDocuments(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListTypeRequest listTypeRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("ListTypeDocuments");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "ListTypeDocuments"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {listTypeRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListTypeResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListTypeResponse) org.apache.axis.utils.JavaUtils.convert(_resp, net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListTypeResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListServerSignersResponse listServerSigners(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListServerSignersRequest listServerSignersRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("ListServerSigners");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "ListServerSigners"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {listServerSignersRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListServerSignersResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListServerSignersResponse) org.apache.axis.utils.JavaUtils.convert(_resp, net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListServerSignersResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadFileResponse downloadFile(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadFileRequest downloadFileRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("DownloadFile");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "DownloadFile"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {downloadFileRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadFileResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadFileResponse) org.apache.axis.utils.JavaUtils.convert(_resp, net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadFileResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
