/**
 * File.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente;

public class File  implements java.io.Serializable {
    private int index;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.TypeEnum type;

    private java.lang.String reference;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ProfileEnum profile;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.CodificationTypeEnum fileFormat;

    private java.lang.String extension;

    private java.lang.String mimeType;

    private java.lang.String base64Data;

    private java.lang.String numberSignatures;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignerID[] signersId;

    public File() {
    }

    public File(
           int index,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.TypeEnum type,
           java.lang.String reference,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ProfileEnum profile,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.CodificationTypeEnum fileFormat,
           java.lang.String extension,
           java.lang.String mimeType,
           java.lang.String base64Data,
           java.lang.String numberSignatures,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignerID[] signersId) {
           this.index = index;
           this.type = type;
           this.reference = reference;
           this.profile = profile;
           this.fileFormat = fileFormat;
           this.extension = extension;
           this.mimeType = mimeType;
           this.base64Data = base64Data;
           this.numberSignatures = numberSignatures;
           this.signersId = signersId;
    }


    /**
     * Gets the index value for this File.
     * 
     * @return index
     */
    public int getIndex() {
        return index;
    }


    /**
     * Sets the index value for this File.
     * 
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }


    /**
     * Gets the type value for this File.
     * 
     * @return type
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.TypeEnum getType() {
        return type;
    }


    /**
     * Sets the type value for this File.
     * 
     * @param type
     */
    public void setType(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.TypeEnum type) {
        this.type = type;
    }


    /**
     * Gets the reference value for this File.
     * 
     * @return reference
     */
    public java.lang.String getReference() {
        return reference;
    }


    /**
     * Sets the reference value for this File.
     * 
     * @param reference
     */
    public void setReference(java.lang.String reference) {
        this.reference = reference;
    }


    /**
     * Gets the profile value for this File.
     * 
     * @return profile
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ProfileEnum getProfile() {
        return profile;
    }


    /**
     * Sets the profile value for this File.
     * 
     * @param profile
     */
    public void setProfile(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ProfileEnum profile) {
        this.profile = profile;
    }


    /**
     * Gets the fileFormat value for this File.
     * 
     * @return fileFormat
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.CodificationTypeEnum getFileFormat() {
        return fileFormat;
    }


    /**
     * Sets the fileFormat value for this File.
     * 
     * @param fileFormat
     */
    public void setFileFormat(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.CodificationTypeEnum fileFormat) {
        this.fileFormat = fileFormat;
    }


    /**
     * Gets the extension value for this File.
     * 
     * @return extension
     */
    public java.lang.String getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this File.
     * 
     * @param extension
     */
    public void setExtension(java.lang.String extension) {
        this.extension = extension;
    }


    /**
     * Gets the mimeType value for this File.
     * 
     * @return mimeType
     */
    public java.lang.String getMimeType() {
        return mimeType;
    }


    /**
     * Sets the mimeType value for this File.
     * 
     * @param mimeType
     */
    public void setMimeType(java.lang.String mimeType) {
        this.mimeType = mimeType;
    }


    /**
     * Gets the base64Data value for this File.
     * 
     * @return base64Data
     */
    public java.lang.String getBase64Data() {
        return base64Data;
    }


    /**
     * Sets the base64Data value for this File.
     * 
     * @param base64Data
     */
    public void setBase64Data(java.lang.String base64Data) {
        this.base64Data = base64Data;
    }


    /**
     * Gets the numberSignatures value for this File.
     * 
     * @return numberSignatures
     */
    public java.lang.String getNumberSignatures() {
        return numberSignatures;
    }


    /**
     * Sets the numberSignatures value for this File.
     * 
     * @param numberSignatures
     */
    public void setNumberSignatures(java.lang.String numberSignatures) {
        this.numberSignatures = numberSignatures;
    }


    /**
     * Gets the signersId value for this File.
     * 
     * @return signersId
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignerID[] getSignersId() {
        return signersId;
    }


    /**
     * Sets the signersId value for this File.
     * 
     * @param signersId
     */
    public void setSignersId(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignerID[] signersId) {
        this.signersId = signersId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof File)) return false;
        File other = (File) obj;
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
            ((this.reference==null && other.getReference()==null) || 
             (this.reference!=null &&
              this.reference.equals(other.getReference()))) &&
            ((this.profile==null && other.getProfile()==null) || 
             (this.profile!=null &&
              this.profile.equals(other.getProfile()))) &&
            ((this.fileFormat==null && other.getFileFormat()==null) || 
             (this.fileFormat!=null &&
              this.fileFormat.equals(other.getFileFormat()))) &&
            ((this.extension==null && other.getExtension()==null) || 
             (this.extension!=null &&
              this.extension.equals(other.getExtension()))) &&
            ((this.mimeType==null && other.getMimeType()==null) || 
             (this.mimeType!=null &&
              this.mimeType.equals(other.getMimeType()))) &&
            ((this.base64Data==null && other.getBase64Data()==null) || 
             (this.base64Data!=null &&
              this.base64Data.equals(other.getBase64Data()))) &&
            ((this.numberSignatures==null && other.getNumberSignatures()==null) || 
             (this.numberSignatures!=null &&
              this.numberSignatures.equals(other.getNumberSignatures()))) &&
            ((this.signersId==null && other.getSignersId()==null) || 
             (this.signersId!=null &&
              java.util.Arrays.equals(this.signersId, other.getSignersId())));
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
        if (getReference() != null) {
            _hashCode += getReference().hashCode();
        }
        if (getProfile() != null) {
            _hashCode += getProfile().hashCode();
        }
        if (getFileFormat() != null) {
            _hashCode += getFileFormat().hashCode();
        }
        if (getExtension() != null) {
            _hashCode += getExtension().hashCode();
        }
        if (getMimeType() != null) {
            _hashCode += getMimeType().hashCode();
        }
        if (getBase64Data() != null) {
            _hashCode += getBase64Data().hashCode();
        }
        if (getNumberSignatures() != null) {
            _hashCode += getNumberSignatures().hashCode();
        }
        if (getSignersId() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSignersId());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSignersId(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(File.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "File"));
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
        elemField.setFieldName("reference");
        elemField.setXmlName(new javax.xml.namespace.QName("", "reference"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("profile");
        elemField.setXmlName(new javax.xml.namespace.QName("", "profile"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ProfileEnum"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fileFormat");
        elemField.setXmlName(new javax.xml.namespace.QName("", "file-format"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "CodificationTypeEnum"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mimeType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mime-type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("base64Data");
        elemField.setXmlName(new javax.xml.namespace.QName("", "base64-data"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberSignatures");
        elemField.setXmlName(new javax.xml.namespace.QName("", "number-signatures"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signersId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "signers-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignerID"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("", "id"));
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
