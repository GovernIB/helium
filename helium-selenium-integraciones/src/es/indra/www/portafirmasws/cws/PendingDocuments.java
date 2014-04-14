/**
 * PendingDocuments.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.indra.www.portafirmasws.cws;

public class PendingDocuments  implements java.io.Serializable {
    private java.lang.String owned;

    private java.lang.String delegated;

    public PendingDocuments() {
    }

    public PendingDocuments(
           java.lang.String owned,
           java.lang.String delegated) {
           this.owned = owned;
           this.delegated = delegated;
    }


    /**
     * Gets the owned value for this PendingDocuments.
     * 
     * @return owned
     */
    public java.lang.String getOwned() {
        return owned;
    }


    /**
     * Sets the owned value for this PendingDocuments.
     * 
     * @param owned
     */
    public void setOwned(java.lang.String owned) {
        this.owned = owned;
    }


    /**
     * Gets the delegated value for this PendingDocuments.
     * 
     * @return delegated
     */
    public java.lang.String getDelegated() {
        return delegated;
    }


    /**
     * Sets the delegated value for this PendingDocuments.
     * 
     * @param delegated
     */
    public void setDelegated(java.lang.String delegated) {
        this.delegated = delegated;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PendingDocuments)) return false;
        PendingDocuments other = (PendingDocuments) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.owned==null && other.getOwned()==null) || 
             (this.owned!=null &&
              this.owned.equals(other.getOwned()))) &&
            ((this.delegated==null && other.getDelegated()==null) || 
             (this.delegated!=null &&
              this.delegated.equals(other.getDelegated())));
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
        if (getOwned() != null) {
            _hashCode += getOwned().hashCode();
        }
        if (getDelegated() != null) {
            _hashCode += getDelegated().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PendingDocuments.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "PendingDocuments"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("owned");
        elemField.setXmlName(new javax.xml.namespace.QName("", "owned"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("delegated");
        elemField.setXmlName(new javax.xml.namespace.QName("", "delegated"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
