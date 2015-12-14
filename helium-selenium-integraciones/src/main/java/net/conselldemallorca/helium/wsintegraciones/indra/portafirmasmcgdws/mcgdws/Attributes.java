/**
 * Attributes.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws;

import net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.Attributes;

public class Attributes  implements java.io.Serializable {
    private net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.AttributesState state;

    private java.lang.String title;

    private java.util.Calendar dateLastUpdate;

    private boolean signAnnexes;

    private java.lang.String externalData;

    public Attributes() {
    }

    public Attributes(
           net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.AttributesState state,
           java.lang.String title,
           java.util.Calendar dateLastUpdate,
           boolean signAnnexes,
           java.lang.String externalData) {
           this.state = state;
           this.title = title;
           this.dateLastUpdate = dateLastUpdate;
           this.signAnnexes = signAnnexes;
           this.externalData = externalData;
    }


    /**
     * Gets the state value for this Attributes.
     * 
     * @return state
     */
    public net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.AttributesState getState() {
        return state;
    }


    /**
     * Sets the state value for this Attributes.
     * 
     * @param state
     */
    public void setState(net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.AttributesState state) {
        this.state = state;
    }


    /**
     * Gets the title value for this Attributes.
     * 
     * @return title
     */
    public java.lang.String getTitle() {
        return title;
    }


    /**
     * Sets the title value for this Attributes.
     * 
     * @param title
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }


    /**
     * Gets the dateLastUpdate value for this Attributes.
     * 
     * @return dateLastUpdate
     */
    public java.util.Calendar getDateLastUpdate() {
        return dateLastUpdate;
    }


    /**
     * Sets the dateLastUpdate value for this Attributes.
     * 
     * @param dateLastUpdate
     */
    public void setDateLastUpdate(java.util.Calendar dateLastUpdate) {
        this.dateLastUpdate = dateLastUpdate;
    }


    /**
     * Gets the signAnnexes value for this Attributes.
     * 
     * @return signAnnexes
     */
    public boolean isSignAnnexes() {
        return signAnnexes;
    }


    /**
     * Sets the signAnnexes value for this Attributes.
     * 
     * @param signAnnexes
     */
    public void setSignAnnexes(boolean signAnnexes) {
        this.signAnnexes = signAnnexes;
    }


    /**
     * Gets the externalData value for this Attributes.
     * 
     * @return externalData
     */
    public java.lang.String getExternalData() {
        return externalData;
    }


    /**
     * Sets the externalData value for this Attributes.
     * 
     * @param externalData
     */
    public void setExternalData(java.lang.String externalData) {
        this.externalData = externalData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Attributes)) return false;
        Attributes other = (Attributes) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.state==null && other.getState()==null) || 
             (this.state!=null &&
              this.state.equals(other.getState()))) &&
            ((this.title==null && other.getTitle()==null) || 
             (this.title!=null &&
              this.title.equals(other.getTitle()))) &&
            ((this.dateLastUpdate==null && other.getDateLastUpdate()==null) || 
             (this.dateLastUpdate!=null &&
              this.dateLastUpdate.equals(other.getDateLastUpdate()))) &&
            this.signAnnexes == other.isSignAnnexes() &&
            ((this.externalData==null && other.getExternalData()==null) || 
             (this.externalData!=null &&
              this.externalData.equals(other.getExternalData())));
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
        if (getState() != null) {
            _hashCode += getState().hashCode();
        }
        if (getTitle() != null) {
            _hashCode += getTitle().hashCode();
        }
        if (getDateLastUpdate() != null) {
            _hashCode += getDateLastUpdate().hashCode();
        }
        _hashCode += (isSignAnnexes() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getExternalData() != null) {
            _hashCode += getExternalData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Attributes.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "Attributes"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("state");
        elemField.setXmlName(new javax.xml.namespace.QName("", "state"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", ">Attributes>state"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("title");
        elemField.setXmlName(new javax.xml.namespace.QName("", "title"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateLastUpdate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "date-last-update"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signAnnexes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sign-annexes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("externalData");
        elemField.setXmlName(new javax.xml.namespace.QName("", "external-data"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
