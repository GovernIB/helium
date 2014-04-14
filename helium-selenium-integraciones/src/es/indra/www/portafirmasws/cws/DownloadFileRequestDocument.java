/**
 * DownloadFileRequestDocument.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.indra.www.portafirmasws.cws;

public class DownloadFileRequestDocument  implements java.io.Serializable {
    private int id;

    private es.indra.www.portafirmasws.cws.DownloadOptions downloadOptions;

    public DownloadFileRequestDocument() {
    }

    public DownloadFileRequestDocument(
           int id,
           es.indra.www.portafirmasws.cws.DownloadOptions downloadOptions) {
           this.id = id;
           this.downloadOptions = downloadOptions;
    }


    /**
     * Gets the id value for this DownloadFileRequestDocument.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the id value for this DownloadFileRequestDocument.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Gets the downloadOptions value for this DownloadFileRequestDocument.
     * 
     * @return downloadOptions
     */
    public es.indra.www.portafirmasws.cws.DownloadOptions getDownloadOptions() {
        return downloadOptions;
    }


    /**
     * Sets the downloadOptions value for this DownloadFileRequestDocument.
     * 
     * @param downloadOptions
     */
    public void setDownloadOptions(es.indra.www.portafirmasws.cws.DownloadOptions downloadOptions) {
        this.downloadOptions = downloadOptions;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DownloadFileRequestDocument)) return false;
        DownloadFileRequestDocument other = (DownloadFileRequestDocument) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.id == other.getId() &&
            ((this.downloadOptions==null && other.getDownloadOptions()==null) || 
             (this.downloadOptions!=null &&
              this.downloadOptions.equals(other.getDownloadOptions())));
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
        _hashCode += getId();
        if (getDownloadOptions() != null) {
            _hashCode += getDownloadOptions().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DownloadFileRequestDocument.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DownloadFileRequestDocument"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("downloadOptions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "download-options"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DownloadOptions"));
        elemField.setNillable(true);
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
