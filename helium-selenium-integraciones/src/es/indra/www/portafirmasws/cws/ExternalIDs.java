/**
 * ExternalIDs.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.indra.www.portafirmasws.cws;

public class ExternalIDs  implements java.io.Serializable {
    private java.lang.String logicalId;

    private es.indra.www.portafirmasws.cws.VerificationCode verificationCode;

    public ExternalIDs() {
    }

    public ExternalIDs(
           java.lang.String logicalId,
           es.indra.www.portafirmasws.cws.VerificationCode verificationCode) {
           this.logicalId = logicalId;
           this.verificationCode = verificationCode;
    }


    /**
     * Gets the logicalId value for this ExternalIDs.
     * 
     * @return logicalId
     */
    public java.lang.String getLogicalId() {
        return logicalId;
    }


    /**
     * Sets the logicalId value for this ExternalIDs.
     * 
     * @param logicalId
     */
    public void setLogicalId(java.lang.String logicalId) {
        this.logicalId = logicalId;
    }


    /**
     * Gets the verificationCode value for this ExternalIDs.
     * 
     * @return verificationCode
     */
    public es.indra.www.portafirmasws.cws.VerificationCode getVerificationCode() {
        return verificationCode;
    }


    /**
     * Sets the verificationCode value for this ExternalIDs.
     * 
     * @param verificationCode
     */
    public void setVerificationCode(es.indra.www.portafirmasws.cws.VerificationCode verificationCode) {
        this.verificationCode = verificationCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExternalIDs)) return false;
        ExternalIDs other = (ExternalIDs) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.logicalId==null && other.getLogicalId()==null) || 
             (this.logicalId!=null &&
              this.logicalId.equals(other.getLogicalId()))) &&
            ((this.verificationCode==null && other.getVerificationCode()==null) || 
             (this.verificationCode!=null &&
              this.verificationCode.equals(other.getVerificationCode())));
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
        if (getLogicalId() != null) {
            _hashCode += getLogicalId().hashCode();
        }
        if (getVerificationCode() != null) {
            _hashCode += getVerificationCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ExternalIDs.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ExternalIDs"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("logicalId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "logical-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("verificationCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "verification-code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "VerificationCode"));
        elemField.setMinOccurs(0);
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
