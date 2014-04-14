/**
 * Signer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.indra.www.portafirmasmcgdws.mcgdws;

public class Signer  implements java.io.Serializable {
    private java.lang.String id;

    private java.util.Calendar date;

    private es.indra.www.portafirmasmcgdws.mcgdws.Delegate delegate;

    private es.indra.www.portafirmasmcgdws.mcgdws.Certificate certificate;

    private es.indra.www.portafirmasmcgdws.mcgdws.Rejection rejection;

    public Signer() {
    }

    public Signer(
           java.lang.String id,
           java.util.Calendar date,
           es.indra.www.portafirmasmcgdws.mcgdws.Delegate delegate,
           es.indra.www.portafirmasmcgdws.mcgdws.Certificate certificate,
           es.indra.www.portafirmasmcgdws.mcgdws.Rejection rejection) {
           this.id = id;
           this.date = date;
           this.delegate = delegate;
           this.certificate = certificate;
           this.rejection = rejection;
    }


    /**
     * Gets the id value for this Signer.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this Signer.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the date value for this Signer.
     * 
     * @return date
     */
    public java.util.Calendar getDate() {
        return date;
    }


    /**
     * Sets the date value for this Signer.
     * 
     * @param date
     */
    public void setDate(java.util.Calendar date) {
        this.date = date;
    }


    /**
     * Gets the delegate value for this Signer.
     * 
     * @return delegate
     */
    public es.indra.www.portafirmasmcgdws.mcgdws.Delegate getDelegate() {
        return delegate;
    }


    /**
     * Sets the delegate value for this Signer.
     * 
     * @param delegate
     */
    public void setDelegate(es.indra.www.portafirmasmcgdws.mcgdws.Delegate delegate) {
        this.delegate = delegate;
    }


    /**
     * Gets the certificate value for this Signer.
     * 
     * @return certificate
     */
    public es.indra.www.portafirmasmcgdws.mcgdws.Certificate getCertificate() {
        return certificate;
    }


    /**
     * Sets the certificate value for this Signer.
     * 
     * @param certificate
     */
    public void setCertificate(es.indra.www.portafirmasmcgdws.mcgdws.Certificate certificate) {
        this.certificate = certificate;
    }


    /**
     * Gets the rejection value for this Signer.
     * 
     * @return rejection
     */
    public es.indra.www.portafirmasmcgdws.mcgdws.Rejection getRejection() {
        return rejection;
    }


    /**
     * Sets the rejection value for this Signer.
     * 
     * @param rejection
     */
    public void setRejection(es.indra.www.portafirmasmcgdws.mcgdws.Rejection rejection) {
        this.rejection = rejection;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Signer)) return false;
        Signer other = (Signer) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.date==null && other.getDate()==null) || 
             (this.date!=null &&
              this.date.equals(other.getDate()))) &&
            ((this.delegate==null && other.getDelegate()==null) || 
             (this.delegate!=null &&
              this.delegate.equals(other.getDelegate()))) &&
            ((this.certificate==null && other.getCertificate()==null) || 
             (this.certificate!=null &&
              this.certificate.equals(other.getCertificate()))) &&
            ((this.rejection==null && other.getRejection()==null) || 
             (this.rejection!=null &&
              this.rejection.equals(other.getRejection())));
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
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getDate() != null) {
            _hashCode += getDate().hashCode();
        }
        if (getDelegate() != null) {
            _hashCode += getDelegate().hashCode();
        }
        if (getCertificate() != null) {
            _hashCode += getCertificate().hashCode();
        }
        if (getRejection() != null) {
            _hashCode += getRejection().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Signer.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "Signer"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("date");
        elemField.setXmlName(new javax.xml.namespace.QName("", "date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("delegate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "delegate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "Delegate"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("certificate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "certificate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "Certificate"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rejection");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rejection"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "Rejection"));
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
