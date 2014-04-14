/**
 * SignatureFile.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.indra.www.portafirmasws.cws;

public class SignatureFile  implements java.io.Serializable {
    private int index;

    private es.indra.www.portafirmasws.cws.TypeEnum type;

    private int numberSignatures;

    private java.lang.String reference;

    private es.indra.www.portafirmasws.cws.ExternalIDs externalIds;

    private es.indra.www.portafirmasws.cws.ArchiveOptions archiveOptions;

    public SignatureFile() {
    }

    public SignatureFile(
           int index,
           es.indra.www.portafirmasws.cws.TypeEnum type,
           int numberSignatures,
           java.lang.String reference,
           es.indra.www.portafirmasws.cws.ExternalIDs externalIds,
           es.indra.www.portafirmasws.cws.ArchiveOptions archiveOptions) {
           this.index = index;
           this.type = type;
           this.numberSignatures = numberSignatures;
           this.reference = reference;
           this.externalIds = externalIds;
           this.archiveOptions = archiveOptions;
    }


    /**
     * Gets the index value for this SignatureFile.
     * 
     * @return index
     */
    public int getIndex() {
        return index;
    }


    /**
     * Sets the index value for this SignatureFile.
     * 
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }


    /**
     * Gets the type value for this SignatureFile.
     * 
     * @return type
     */
    public es.indra.www.portafirmasws.cws.TypeEnum getType() {
        return type;
    }


    /**
     * Sets the type value for this SignatureFile.
     * 
     * @param type
     */
    public void setType(es.indra.www.portafirmasws.cws.TypeEnum type) {
        this.type = type;
    }


    /**
     * Gets the numberSignatures value for this SignatureFile.
     * 
     * @return numberSignatures
     */
    public int getNumberSignatures() {
        return numberSignatures;
    }


    /**
     * Sets the numberSignatures value for this SignatureFile.
     * 
     * @param numberSignatures
     */
    public void setNumberSignatures(int numberSignatures) {
        this.numberSignatures = numberSignatures;
    }


    /**
     * Gets the reference value for this SignatureFile.
     * 
     * @return reference
     */
    public java.lang.String getReference() {
        return reference;
    }


    /**
     * Sets the reference value for this SignatureFile.
     * 
     * @param reference
     */
    public void setReference(java.lang.String reference) {
        this.reference = reference;
    }


    /**
     * Gets the externalIds value for this SignatureFile.
     * 
     * @return externalIds
     */
    public es.indra.www.portafirmasws.cws.ExternalIDs getExternalIds() {
        return externalIds;
    }


    /**
     * Sets the externalIds value for this SignatureFile.
     * 
     * @param externalIds
     */
    public void setExternalIds(es.indra.www.portafirmasws.cws.ExternalIDs externalIds) {
        this.externalIds = externalIds;
    }


    /**
     * Gets the archiveOptions value for this SignatureFile.
     * 
     * @return archiveOptions
     */
    public es.indra.www.portafirmasws.cws.ArchiveOptions getArchiveOptions() {
        return archiveOptions;
    }


    /**
     * Sets the archiveOptions value for this SignatureFile.
     * 
     * @param archiveOptions
     */
    public void setArchiveOptions(es.indra.www.portafirmasws.cws.ArchiveOptions archiveOptions) {
        this.archiveOptions = archiveOptions;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SignatureFile)) return false;
        SignatureFile other = (SignatureFile) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.index == other.getIndex() &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            this.numberSignatures == other.getNumberSignatures() &&
            ((this.reference==null && other.getReference()==null) || 
             (this.reference!=null &&
              this.reference.equals(other.getReference()))) &&
            ((this.externalIds==null && other.getExternalIds()==null) || 
             (this.externalIds!=null &&
              this.externalIds.equals(other.getExternalIds()))) &&
            ((this.archiveOptions==null && other.getArchiveOptions()==null) || 
             (this.archiveOptions!=null &&
              this.archiveOptions.equals(other.getArchiveOptions())));
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
        _hashCode += getIndex();
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        _hashCode += getNumberSignatures();
        if (getReference() != null) {
            _hashCode += getReference().hashCode();
        }
        if (getExternalIds() != null) {
            _hashCode += getExternalIds().hashCode();
        }
        if (getArchiveOptions() != null) {
            _hashCode += getArchiveOptions().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SignatureFile.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignatureFile"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("index");
        elemField.setXmlName(new javax.xml.namespace.QName("", "index"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "TypeEnum"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberSignatures");
        elemField.setXmlName(new javax.xml.namespace.QName("", "number-signatures"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
        elemField.setFieldName("externalIds");
        elemField.setXmlName(new javax.xml.namespace.QName("", "external-ids"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ExternalIDs"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("archiveOptions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "archive-options"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ArchiveOptions"));
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
