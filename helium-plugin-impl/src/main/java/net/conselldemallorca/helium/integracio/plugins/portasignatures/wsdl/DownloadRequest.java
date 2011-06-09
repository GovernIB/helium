/**
 * DownloadRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl;

@SuppressWarnings({"serial", "unchecked"})
public class DownloadRequest  implements java.io.Serializable {
    private Application application;

    private java.lang.Boolean downloadDocuments;

    private java.lang.Boolean additionalInfo;

    private java.lang.Boolean archiveInfo;

    private DownloadRequestDocument document;

    private java.lang.String version;  // attribute

    public DownloadRequest() {
    }

    public DownloadRequest(
           Application application,
           java.lang.Boolean downloadDocuments,
           java.lang.Boolean additionalInfo,
           java.lang.Boolean archiveInfo,
           DownloadRequestDocument document,
           java.lang.String version) {
           this.application = application;
           this.downloadDocuments = downloadDocuments;
           this.additionalInfo = additionalInfo;
           this.archiveInfo = archiveInfo;
           this.document = document;
           this.version = version;
    }


    /**
     * Gets the application value for this DownloadRequest.
     * 
     * @return application
     */
    public Application getApplication() {
        return application;
    }


    /**
     * Sets the application value for this DownloadRequest.
     * 
     * @param application
     */
    public void setApplication(Application application) {
        this.application = application;
    }


    /**
     * Gets the downloadDocuments value for this DownloadRequest.
     * 
     * @return downloadDocuments
     */
    public java.lang.Boolean getDownloadDocuments() {
        return downloadDocuments;
    }


    /**
     * Sets the downloadDocuments value for this DownloadRequest.
     * 
     * @param downloadDocuments
     */
    public void setDownloadDocuments(java.lang.Boolean downloadDocuments) {
        this.downloadDocuments = downloadDocuments;
    }


    /**
     * Gets the additionalInfo value for this DownloadRequest.
     * 
     * @return additionalInfo
     */
    public java.lang.Boolean getAdditionalInfo() {
        return additionalInfo;
    }


    /**
     * Sets the additionalInfo value for this DownloadRequest.
     * 
     * @param additionalInfo
     */
    public void setAdditionalInfo(java.lang.Boolean additionalInfo) {
        this.additionalInfo = additionalInfo;
    }


    /**
     * Gets the archiveInfo value for this DownloadRequest.
     * 
     * @return archiveInfo
     */
    public java.lang.Boolean getArchiveInfo() {
        return archiveInfo;
    }


    /**
     * Sets the archiveInfo value for this DownloadRequest.
     * 
     * @param archiveInfo
     */
    public void setArchiveInfo(java.lang.Boolean archiveInfo) {
        this.archiveInfo = archiveInfo;
    }


    /**
     * Gets the document value for this DownloadRequest.
     * 
     * @return document
     */
    public DownloadRequestDocument getDocument() {
        return document;
    }


    /**
     * Sets the document value for this DownloadRequest.
     * 
     * @param document
     */
    public void setDocument(DownloadRequestDocument document) {
        this.document = document;
    }


    /**
     * Gets the version value for this DownloadRequest.
     * 
     * @return version
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this DownloadRequest.
     * 
     * @param version
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DownloadRequest)) return false;
        DownloadRequest other = (DownloadRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.application==null && other.getApplication()==null) || 
             (this.application!=null &&
              this.application.equals(other.getApplication()))) &&
            ((this.downloadDocuments==null && other.getDownloadDocuments()==null) || 
             (this.downloadDocuments!=null &&
              this.downloadDocuments.equals(other.getDownloadDocuments()))) &&
            ((this.additionalInfo==null && other.getAdditionalInfo()==null) || 
             (this.additionalInfo!=null &&
              this.additionalInfo.equals(other.getAdditionalInfo()))) &&
            ((this.archiveInfo==null && other.getArchiveInfo()==null) || 
             (this.archiveInfo!=null &&
              this.archiveInfo.equals(other.getArchiveInfo()))) &&
            ((this.document==null && other.getDocument()==null) || 
             (this.document!=null &&
              this.document.equals(other.getDocument()))) &&
            ((this.version==null && other.getVersion()==null) || 
             (this.version!=null &&
              this.version.equals(other.getVersion())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getApplication() != null) {
            _hashCode += getApplication().hashCode();
        }
        if (getDownloadDocuments() != null) {
            _hashCode += getDownloadDocuments().hashCode();
        }
        if (getAdditionalInfo() != null) {
            _hashCode += getAdditionalInfo().hashCode();
        }
        if (getArchiveInfo() != null) {
            _hashCode += getArchiveInfo().hashCode();
        }
        if (getDocument() != null) {
            _hashCode += getDocument().hashCode();
        }
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DownloadRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">download-request"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("version");
        attrField.setXmlName(new javax.xml.namespace.QName("", "version"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("application");
        elemField.setXmlName(new javax.xml.namespace.QName("", "application"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Application"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("downloadDocuments");
        elemField.setXmlName(new javax.xml.namespace.QName("", "download-documents"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("additionalInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "additional-info"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("archiveInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "archive-info"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("document");
        elemField.setXmlName(new javax.xml.namespace.QName("", "document"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DownloadRequestDocument"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
