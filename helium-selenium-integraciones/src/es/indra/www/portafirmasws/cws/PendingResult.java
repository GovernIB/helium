/**
 * PendingResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.indra.www.portafirmasws.cws;

public class PendingResult  extends es.indra.www.portafirmasws.cws.SearchResult  implements java.io.Serializable {
    private es.indra.www.portafirmasws.cws.PendingDocuments pendingDocuments;

    public PendingResult() {
    }

    public PendingResult(
           es.indra.www.portafirmasws.cws.PendingDocuments pendingDocuments) {
        this.pendingDocuments = pendingDocuments;
    }


    /**
     * Gets the pendingDocuments value for this PendingResult.
     * 
     * @return pendingDocuments
     */
    public es.indra.www.portafirmasws.cws.PendingDocuments getPendingDocuments() {
        return pendingDocuments;
    }


    /**
     * Sets the pendingDocuments value for this PendingResult.
     * 
     * @param pendingDocuments
     */
    public void setPendingDocuments(es.indra.www.portafirmasws.cws.PendingDocuments pendingDocuments) {
        this.pendingDocuments = pendingDocuments;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PendingResult)) return false;
        PendingResult other = (PendingResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.pendingDocuments==null && other.getPendingDocuments()==null) || 
             (this.pendingDocuments!=null &&
              this.pendingDocuments.equals(other.getPendingDocuments())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getPendingDocuments() != null) {
            _hashCode += getPendingDocuments().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PendingResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "PendingResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pendingDocuments");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pending-documents"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "PendingDocuments"));
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
