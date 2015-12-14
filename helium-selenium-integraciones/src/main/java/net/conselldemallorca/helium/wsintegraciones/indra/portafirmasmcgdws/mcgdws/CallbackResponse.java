/**
 * CallbackResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws;

import net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.CallbackResponse;

public class CallbackResponse  implements java.io.Serializable {
    private net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.LogMessage[] logMessages;

    private double _return;

    private java.lang.String version;  // attribute

    public CallbackResponse() {
    }

    public CallbackResponse(
           net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.LogMessage[] logMessages,
           double _return,
           java.lang.String version) {
           this.logMessages = logMessages;
           this._return = _return;
           this.version = version;
    }


    /**
     * Gets the logMessages value for this CallbackResponse.
     * 
     * @return logMessages
     */
    public net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.LogMessage[] getLogMessages() {
        return logMessages;
    }


    /**
     * Sets the logMessages value for this CallbackResponse.
     * 
     * @param logMessages
     */
    public void setLogMessages(net.conselldemallorca.helium.wsintegraciones.indra.portafirmasmcgdws.mcgdws.LogMessage[] logMessages) {
        this.logMessages = logMessages;
    }


    /**
     * Gets the _return value for this CallbackResponse.
     * 
     * @return _return
     */
    public double get_return() {
        return _return;
    }


    /**
     * Sets the _return value for this CallbackResponse.
     * 
     * @param _return
     */
    public void set_return(double _return) {
        this._return = _return;
    }


    /**
     * Gets the version value for this CallbackResponse.
     * 
     * @return version
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this CallbackResponse.
     * 
     * @param version
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CallbackResponse)) return false;
        CallbackResponse other = (CallbackResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.logMessages==null && other.getLogMessages()==null) || 
             (this.logMessages!=null &&
              java.util.Arrays.equals(this.logMessages, other.getLogMessages()))) &&
            this._return == other.get_return() &&
            ((this.version==null && other.getVersion()==null) || 
             (this.version!=null &&
              this.version.equals(other.getVersion())));
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
        if (getLogMessages() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLogMessages());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLogMessages(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += new Double(get_return()).hashCode();
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CallbackResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", ">callback-response"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("version");
        attrField.setXmlName(new javax.xml.namespace.QName("", "version"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("logMessages");
        elemField.setXmlName(new javax.xml.namespace.QName("", "logMessages"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "LogMessage"));
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("", "item"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_return");
        elemField.setXmlName(new javax.xml.namespace.QName("", "return"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
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
