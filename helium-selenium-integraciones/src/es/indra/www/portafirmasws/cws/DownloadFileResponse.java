/**
 * DownloadFileResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.indra.www.portafirmasws.cws;

public class DownloadFileResponse  implements java.io.Serializable {
    private es.indra.www.portafirmasws.cws.Result result;

    private es.indra.www.portafirmasws.cws.DownloadFileResponseDocument document;

    private java.lang.String version;  // attribute

    public DownloadFileResponse() {
    }

    public DownloadFileResponse(
           es.indra.www.portafirmasws.cws.Result result,
           es.indra.www.portafirmasws.cws.DownloadFileResponseDocument document,
           java.lang.String version) {
           this.result = result;
           this.document = document;
           this.version = version;
    }


    /**
     * Gets the result value for this DownloadFileResponse.
     * 
     * @return result
     */
    public es.indra.www.portafirmasws.cws.Result getResult() {
        return result;
    }


    /**
     * Sets the result value for this DownloadFileResponse.
     * 
     * @param result
     */
    public void setResult(es.indra.www.portafirmasws.cws.Result result) {
        this.result = result;
    }


    /**
     * Gets the document value for this DownloadFileResponse.
     * 
     * @return document
     */
    public es.indra.www.portafirmasws.cws.DownloadFileResponseDocument getDocument() {
        return document;
    }


    /**
     * Sets the document value for this DownloadFileResponse.
     * 
     * @param document
     */
    public void setDocument(es.indra.www.portafirmasws.cws.DownloadFileResponseDocument document) {
        this.document = document;
    }


    /**
     * Gets the version value for this DownloadFileResponse.
     * 
     * @return version
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this DownloadFileResponse.
     * 
     * @param version
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DownloadFileResponse)) return false;
        DownloadFileResponse other = (DownloadFileResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
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
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
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
        new org.apache.axis.description.TypeDesc(DownloadFileResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">download-file-response"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("version");
        attrField.setXmlName(new javax.xml.namespace.QName("", "version"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Result"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("document");
        elemField.setXmlName(new javax.xml.namespace.QName("", "document"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DownloadFileResponseDocument"));
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
