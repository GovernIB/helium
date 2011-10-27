/**
 * VerificationCode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl;

@SuppressWarnings({"serial", "unused", "rawtypes"})
public class VerificationCode  implements java.io.Serializable {
    private java.lang.String providerId;

    private java.lang.String value;

    private PdfPosition pdfPosition;

    public VerificationCode() {
    }

    public VerificationCode(
           java.lang.String providerId,
           java.lang.String value,
           PdfPosition pdfPosition) {
           this.providerId = providerId;
           this.value = value;
           this.pdfPosition = pdfPosition;
    }


    /**
     * Gets the providerId value for this VerificationCode.
     * 
     * @return providerId
     */
    public java.lang.String getProviderId() {
        return providerId;
    }


    /**
     * Sets the providerId value for this VerificationCode.
     * 
     * @param providerId
     */
    public void setProviderId(java.lang.String providerId) {
        this.providerId = providerId;
    }


    /**
     * Gets the value value for this VerificationCode.
     * 
     * @return value
     */
    public java.lang.String getValue() {
        return value;
    }


    /**
     * Sets the value value for this VerificationCode.
     * 
     * @param value
     */
    public void setValue(java.lang.String value) {
        this.value = value;
    }


    /**
     * Gets the pdfPosition value for this VerificationCode.
     * 
     * @return pdfPosition
     */
    public PdfPosition getPdfPosition() {
        return pdfPosition;
    }


    /**
     * Sets the pdfPosition value for this VerificationCode.
     * 
     * @param pdfPosition
     */
    public void setPdfPosition(PdfPosition pdfPosition) {
        this.pdfPosition = pdfPosition;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VerificationCode)) return false;
        VerificationCode other = (VerificationCode) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.providerId==null && other.getProviderId()==null) || 
             (this.providerId!=null &&
              this.providerId.equals(other.getProviderId()))) &&
            ((this.value==null && other.getValue()==null) || 
             (this.value!=null &&
              this.value.equals(other.getValue()))) &&
            ((this.pdfPosition==null && other.getPdfPosition()==null) || 
             (this.pdfPosition!=null &&
              this.pdfPosition.equals(other.getPdfPosition())));
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
        if (getProviderId() != null) {
            _hashCode += getProviderId().hashCode();
        }
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        if (getPdfPosition() != null) {
            _hashCode += getPdfPosition().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VerificationCode.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "VerificationCode"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("providerId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "provider-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("", "value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pdfPosition");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pdf-position"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "PdfPosition"));
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
