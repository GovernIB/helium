/**
 * ReferenciaEntrada.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ReferenciaEntrada.model.v2.ws.bantel.caib.es;

public class ReferenciaEntrada  implements java.io.Serializable {
    private java.lang.String numeroEntrada;

    private java.lang.String claveAcceso;

    public ReferenciaEntrada() {
    }

    public ReferenciaEntrada(
           java.lang.String numeroEntrada,
           java.lang.String claveAcceso) {
           this.numeroEntrada = numeroEntrada;
           this.claveAcceso = claveAcceso;
    }


    /**
     * Gets the numeroEntrada value for this ReferenciaEntrada.
     * 
     * @return numeroEntrada
     */
    public java.lang.String getNumeroEntrada() {
        return numeroEntrada;
    }


    /**
     * Sets the numeroEntrada value for this ReferenciaEntrada.
     * 
     * @param numeroEntrada
     */
    public void setNumeroEntrada(java.lang.String numeroEntrada) {
        this.numeroEntrada = numeroEntrada;
    }


    /**
     * Gets the claveAcceso value for this ReferenciaEntrada.
     * 
     * @return claveAcceso
     */
    public java.lang.String getClaveAcceso() {
        return claveAcceso;
    }


    /**
     * Sets the claveAcceso value for this ReferenciaEntrada.
     * 
     * @param claveAcceso
     */
    public void setClaveAcceso(java.lang.String claveAcceso) {
        this.claveAcceso = claveAcceso;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReferenciaEntrada)) return false;
        ReferenciaEntrada other = (ReferenciaEntrada) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.numeroEntrada==null && other.getNumeroEntrada()==null) || 
             (this.numeroEntrada!=null &&
              this.numeroEntrada.equals(other.getNumeroEntrada()))) &&
            ((this.claveAcceso==null && other.getClaveAcceso()==null) || 
             (this.claveAcceso!=null &&
              this.claveAcceso.equals(other.getClaveAcceso())));
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
        if (getNumeroEntrada() != null) {
            _hashCode += getNumeroEntrada().hashCode();
        }
        if (getClaveAcceso() != null) {
            _hashCode += getClaveAcceso().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ReferenciaEntrada.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:es:caib:bantel:ws:v2:model:ReferenciaEntrada", "ReferenciaEntrada"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numeroEntrada");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numeroEntrada"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("claveAcceso");
        elemField.setXmlName(new javax.xml.namespace.QName("", "claveAcceso"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
