/**
 * Annex.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.indra.www.portafirmasws.cws;

public class Annex  implements java.io.Serializable {
    private java.lang.String description;

    private java.lang.String extension;

    private java.lang.String reference;

    private java.lang.String url;

    private java.lang.Boolean signAnnex;

    private es.indra.www.portafirmasws.cws.Sender sender;

    private es.indra.www.portafirmasws.cws.ExternalIDs externalIds;

    private es.indra.www.portafirmasws.cws.ArchiveOptions archiveOptions;

    private java.lang.Integer typeSign;

    private java.lang.Boolean isFileSign;

    public Annex() {
    }

    public Annex(
           java.lang.String description,
           java.lang.String extension,
           java.lang.String reference,
           java.lang.String url,
           java.lang.Boolean signAnnex,
           es.indra.www.portafirmasws.cws.Sender sender,
           es.indra.www.portafirmasws.cws.ExternalIDs externalIds,
           es.indra.www.portafirmasws.cws.ArchiveOptions archiveOptions,
           java.lang.Integer typeSign,
           java.lang.Boolean isFileSign) {
           this.description = description;
           this.extension = extension;
           this.reference = reference;
           this.url = url;
           this.signAnnex = signAnnex;
           this.sender = sender;
           this.externalIds = externalIds;
           this.archiveOptions = archiveOptions;
           this.typeSign = typeSign;
           this.isFileSign = isFileSign;
    }


    /**
     * Gets the description value for this Annex.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this Annex.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the extension value for this Annex.
     * 
     * @return extension
     */
    public java.lang.String getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this Annex.
     * 
     * @param extension
     */
    public void setExtension(java.lang.String extension) {
        this.extension = extension;
    }


    /**
     * Gets the reference value for this Annex.
     * 
     * @return reference
     */
    public java.lang.String getReference() {
        return reference;
    }


    /**
     * Sets the reference value for this Annex.
     * 
     * @param reference
     */
    public void setReference(java.lang.String reference) {
        this.reference = reference;
    }


    /**
     * Gets the url value for this Annex.
     * 
     * @return url
     */
    public java.lang.String getUrl() {
        return url;
    }


    /**
     * Sets the url value for this Annex.
     * 
     * @param url
     */
    public void setUrl(java.lang.String url) {
        this.url = url;
    }


    /**
     * Gets the signAnnex value for this Annex.
     * 
     * @return signAnnex
     */
    public java.lang.Boolean getSignAnnex() {
        return signAnnex;
    }


    /**
     * Sets the signAnnex value for this Annex.
     * 
     * @param signAnnex
     */
    public void setSignAnnex(java.lang.Boolean signAnnex) {
        this.signAnnex = signAnnex;
    }


    /**
     * Gets the sender value for this Annex.
     * 
     * @return sender
     */
    public es.indra.www.portafirmasws.cws.Sender getSender() {
        return sender;
    }


    /**
     * Sets the sender value for this Annex.
     * 
     * @param sender
     */
    public void setSender(es.indra.www.portafirmasws.cws.Sender sender) {
        this.sender = sender;
    }


    /**
     * Gets the externalIds value for this Annex.
     * 
     * @return externalIds
     */
    public es.indra.www.portafirmasws.cws.ExternalIDs getExternalIds() {
        return externalIds;
    }


    /**
     * Sets the externalIds value for this Annex.
     * 
     * @param externalIds
     */
    public void setExternalIds(es.indra.www.portafirmasws.cws.ExternalIDs externalIds) {
        this.externalIds = externalIds;
    }


    /**
     * Gets the archiveOptions value for this Annex.
     * 
     * @return archiveOptions
     */
    public es.indra.www.portafirmasws.cws.ArchiveOptions getArchiveOptions() {
        return archiveOptions;
    }


    /**
     * Sets the archiveOptions value for this Annex.
     * 
     * @param archiveOptions
     */
    public void setArchiveOptions(es.indra.www.portafirmasws.cws.ArchiveOptions archiveOptions) {
        this.archiveOptions = archiveOptions;
    }


    /**
     * Gets the typeSign value for this Annex.
     * 
     * @return typeSign
     */
    public java.lang.Integer getTypeSign() {
        return typeSign;
    }


    /**
     * Sets the typeSign value for this Annex.
     * 
     * @param typeSign
     */
    public void setTypeSign(java.lang.Integer typeSign) {
        this.typeSign = typeSign;
    }


    /**
     * Gets the isFileSign value for this Annex.
     * 
     * @return isFileSign
     */
    public java.lang.Boolean getIsFileSign() {
        return isFileSign;
    }


    /**
     * Sets the isFileSign value for this Annex.
     * 
     * @param isFileSign
     */
    public void setIsFileSign(java.lang.Boolean isFileSign) {
        this.isFileSign = isFileSign;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Annex)) return false;
        Annex other = (Annex) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.extension==null && other.getExtension()==null) || 
             (this.extension!=null &&
              this.extension.equals(other.getExtension()))) &&
            ((this.reference==null && other.getReference()==null) || 
             (this.reference!=null &&
              this.reference.equals(other.getReference()))) &&
            ((this.url==null && other.getUrl()==null) || 
             (this.url!=null &&
              this.url.equals(other.getUrl()))) &&
            ((this.signAnnex==null && other.getSignAnnex()==null) || 
             (this.signAnnex!=null &&
              this.signAnnex.equals(other.getSignAnnex()))) &&
            ((this.sender==null && other.getSender()==null) || 
             (this.sender!=null &&
              this.sender.equals(other.getSender()))) &&
            ((this.externalIds==null && other.getExternalIds()==null) || 
             (this.externalIds!=null &&
              this.externalIds.equals(other.getExternalIds()))) &&
            ((this.archiveOptions==null && other.getArchiveOptions()==null) || 
             (this.archiveOptions!=null &&
              this.archiveOptions.equals(other.getArchiveOptions()))) &&
            ((this.typeSign==null && other.getTypeSign()==null) || 
             (this.typeSign!=null &&
              this.typeSign.equals(other.getTypeSign()))) &&
            ((this.isFileSign==null && other.getIsFileSign()==null) || 
             (this.isFileSign!=null &&
              this.isFileSign.equals(other.getIsFileSign())));
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
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getExtension() != null) {
            _hashCode += getExtension().hashCode();
        }
        if (getReference() != null) {
            _hashCode += getReference().hashCode();
        }
        if (getUrl() != null) {
            _hashCode += getUrl().hashCode();
        }
        if (getSignAnnex() != null) {
            _hashCode += getSignAnnex().hashCode();
        }
        if (getSender() != null) {
            _hashCode += getSender().hashCode();
        }
        if (getExternalIds() != null) {
            _hashCode += getExternalIds().hashCode();
        }
        if (getArchiveOptions() != null) {
            _hashCode += getArchiveOptions().hashCode();
        }
        if (getTypeSign() != null) {
            _hashCode += getTypeSign().hashCode();
        }
        if (getIsFileSign() != null) {
            _hashCode += getIsFileSign().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Annex.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Annex"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
        elemField.setFieldName("url");
        elemField.setXmlName(new javax.xml.namespace.QName("", "url"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signAnnex");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sign-annex"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sender");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sender"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Sender"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("typeSign");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type-sign"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isFileSign");
        elemField.setXmlName(new javax.xml.namespace.QName("", "is-file-sign"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
