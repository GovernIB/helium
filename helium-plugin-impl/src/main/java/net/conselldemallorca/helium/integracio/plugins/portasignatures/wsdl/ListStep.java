/**
 * ListStep.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl;

@SuppressWarnings({"serial", "unused", "rawtypes"})
public class ListStep  extends Step  implements java.io.Serializable {
    private java.lang.Integer minimalSigners;

    private Signer[] signersAction;

    private Signer[] signersReject;

    private Signer[] signersNone;

    public ListStep() {
    }

    public ListStep(
           java.lang.Integer minimalSigners,
           Signer[] signersAction,
           Signer[] signersReject,
           Signer[] signersNone) {
        this.minimalSigners = minimalSigners;
        this.signersAction = signersAction;
        this.signersReject = signersReject;
        this.signersNone = signersNone;
    }


    /**
     * Gets the minimalSigners value for this ListStep.
     * 
     * @return minimalSigners
     */
    public java.lang.Integer getMinimalSigners() {
        return minimalSigners;
    }


    /**
     * Sets the minimalSigners value for this ListStep.
     * 
     * @param minimalSigners
     */
    public void setMinimalSigners(java.lang.Integer minimalSigners) {
        this.minimalSigners = minimalSigners;
    }


    /**
     * Gets the signersAction value for this ListStep.
     * 
     * @return signersAction
     */
    public Signer[] getSignersAction() {
        return signersAction;
    }


    /**
     * Sets the signersAction value for this ListStep.
     * 
     * @param signersAction
     */
    public void setSignersAction(Signer[] signersAction) {
        this.signersAction = signersAction;
    }


    /**
     * Gets the signersReject value for this ListStep.
     * 
     * @return signersReject
     */
    public Signer[] getSignersReject() {
        return signersReject;
    }


    /**
     * Sets the signersReject value for this ListStep.
     * 
     * @param signersReject
     */
    public void setSignersReject(Signer[] signersReject) {
        this.signersReject = signersReject;
    }


    /**
     * Gets the signersNone value for this ListStep.
     * 
     * @return signersNone
     */
    public Signer[] getSignersNone() {
        return signersNone;
    }


    /**
     * Sets the signersNone value for this ListStep.
     * 
     * @param signersNone
     */
    public void setSignersNone(Signer[] signersNone) {
        this.signersNone = signersNone;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ListStep)) return false;
        ListStep other = (ListStep) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.minimalSigners==null && other.getMinimalSigners()==null) || 
             (this.minimalSigners!=null &&
              this.minimalSigners.equals(other.getMinimalSigners()))) &&
            ((this.signersAction==null && other.getSignersAction()==null) || 
             (this.signersAction!=null &&
              java.util.Arrays.equals(this.signersAction, other.getSignersAction()))) &&
            ((this.signersReject==null && other.getSignersReject()==null) || 
             (this.signersReject!=null &&
              java.util.Arrays.equals(this.signersReject, other.getSignersReject()))) &&
            ((this.signersNone==null && other.getSignersNone()==null) || 
             (this.signersNone!=null &&
              java.util.Arrays.equals(this.signersNone, other.getSignersNone())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getMinimalSigners() != null) {
            _hashCode += getMinimalSigners().hashCode();
        }
        if (getSignersAction() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSignersAction());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSignersAction(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSignersReject() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSignersReject());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSignersReject(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSignersNone() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSignersNone());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSignersNone(), i);
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
        new org.apache.axis.description.TypeDesc(ListStep.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ListStep"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("minimalSigners");
        elemField.setXmlName(new javax.xml.namespace.QName("", "minimal-signers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signersAction");
        elemField.setXmlName(new javax.xml.namespace.QName("", "signers-action"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Signer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("", "signer"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signersReject");
        elemField.setXmlName(new javax.xml.namespace.QName("", "signers-reject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Signer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("", "signer"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signersNone");
        elemField.setXmlName(new javax.xml.namespace.QName("", "signers-none"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Signer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("", "signer"));
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
