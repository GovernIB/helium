/**
 * Document.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws;

import net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.Document;

public class Document  implements java.io.Serializable {
    private int id;

    private net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.Attributes attributes;

    private net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.Signer signer;

    public Document() {
    }

    public Document(
           int id,
           net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.Attributes attributes,
           net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.Signer signer) {
           this.id = id;
           this.attributes = attributes;
           this.signer = signer;
    }


    /**
     * Gets the id value for this Document.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the id value for this Document.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Gets the attributes value for this Document.
     * 
     * @return attributes
     */
    public net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.Attributes getAttributes() {
        return attributes;
    }


    /**
     * Sets the attributes value for this Document.
     * 
     * @param attributes
     */
    public void setAttributes(net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.Attributes attributes) {
        this.attributes = attributes;
    }


    /**
     * Gets the signer value for this Document.
     * 
     * @return signer
     */
    public net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.Signer getSigner() {
        return signer;
    }


    /**
     * Sets the signer value for this Document.
     * 
     * @param signer
     */
    public void setSigner(net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.Signer signer) {
        this.signer = signer;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Document)) return false;
        Document other = (Document) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.id == other.getId() &&
            ((this.attributes==null && other.getAttributes()==null) || 
             (this.attributes!=null &&
              this.attributes.equals(other.getAttributes()))) &&
            ((this.signer==null && other.getSigner()==null) || 
             (this.signer!=null &&
              this.signer.equals(other.getSigner())));
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
        if (getAttributes() != null) {
            _hashCode += getAttributes().hashCode();
        }
        if (getSigner() != null) {
            _hashCode += getSigner().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Document.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "Document"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attributes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "attributes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "Attributes"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signer");
        elemField.setXmlName(new javax.xml.namespace.QName("", "signer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "Signer"));
        elemField.setMinOccurs(0);
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
