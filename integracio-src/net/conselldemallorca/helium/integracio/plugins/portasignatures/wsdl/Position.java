/**
 * Position.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl;

@SuppressWarnings({"serial", "unchecked"})
public class Position  implements java.io.Serializable {
    private TypeEnum source;

    private java.lang.String reference;

    private PdfTypeEnum type;

    private PdfPosition pdfPosition;

    private SignatureImageDimensions signatureImageDimensions;

    public Position() {
    }

    public Position(
           TypeEnum source,
           java.lang.String reference,
           PdfTypeEnum type,
           PdfPosition pdfPosition,
           SignatureImageDimensions signatureImageDimensions) {
           this.source = source;
           this.reference = reference;
           this.type = type;
           this.pdfPosition = pdfPosition;
           this.signatureImageDimensions = signatureImageDimensions;
    }


    /**
     * Gets the source value for this Position.
     * 
     * @return source
     */
    public TypeEnum getSource() {
        return source;
    }


    /**
     * Sets the source value for this Position.
     * 
     * @param source
     */
    public void setSource(TypeEnum source) {
        this.source = source;
    }


    /**
     * Gets the reference value for this Position.
     * 
     * @return reference
     */
    public java.lang.String getReference() {
        return reference;
    }


    /**
     * Sets the reference value for this Position.
     * 
     * @param reference
     */
    public void setReference(java.lang.String reference) {
        this.reference = reference;
    }


    /**
     * Gets the type value for this Position.
     * 
     * @return type
     */
    public PdfTypeEnum getType() {
        return type;
    }


    /**
     * Sets the type value for this Position.
     * 
     * @param type
     */
    public void setType(PdfTypeEnum type) {
        this.type = type;
    }


    /**
     * Gets the pdfPosition value for this Position.
     * 
     * @return pdfPosition
     */
    public PdfPosition getPdfPosition() {
        return pdfPosition;
    }


    /**
     * Sets the pdfPosition value for this Position.
     * 
     * @param pdfPosition
     */
    public void setPdfPosition(PdfPosition pdfPosition) {
        this.pdfPosition = pdfPosition;
    }


    /**
     * Gets the signatureImageDimensions value for this Position.
     * 
     * @return signatureImageDimensions
     */
    public SignatureImageDimensions getSignatureImageDimensions() {
        return signatureImageDimensions;
    }


    /**
     * Sets the signatureImageDimensions value for this Position.
     * 
     * @param signatureImageDimensions
     */
    public void setSignatureImageDimensions(SignatureImageDimensions signatureImageDimensions) {
        this.signatureImageDimensions = signatureImageDimensions;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.source==null && other.getSource()==null) || 
             (this.source!=null &&
              this.source.equals(other.getSource()))) &&
            ((this.reference==null && other.getReference()==null) || 
             (this.reference!=null &&
              this.reference.equals(other.getReference()))) &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.pdfPosition==null && other.getPdfPosition()==null) || 
             (this.pdfPosition!=null &&
              this.pdfPosition.equals(other.getPdfPosition()))) &&
            ((this.signatureImageDimensions==null && other.getSignatureImageDimensions()==null) || 
             (this.signatureImageDimensions!=null &&
              this.signatureImageDimensions.equals(other.getSignatureImageDimensions())));
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
        if (getSource() != null) {
            _hashCode += getSource().hashCode();
        }
        if (getReference() != null) {
            _hashCode += getReference().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getPdfPosition() != null) {
            _hashCode += getPdfPosition().hashCode();
        }
        if (getSignatureImageDimensions() != null) {
            _hashCode += getSignatureImageDimensions().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Position.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Position"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("source");
        elemField.setXmlName(new javax.xml.namespace.QName("", "source"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "TypeEnum"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reference");
        elemField.setXmlName(new javax.xml.namespace.QName("", "reference"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "PdfTypeEnum"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pdfPosition");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pdf-position"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "PdfPosition"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signatureImageDimensions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "signature-image-dimensions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignatureImageDimensions"));
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
