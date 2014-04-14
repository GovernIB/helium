/**
 * Steps.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.indra.www.portafirmasws.cws;

public class Steps  implements java.io.Serializable {
    private es.indra.www.portafirmasws.cws.SignModeEnum signMode;

    private es.indra.www.portafirmasws.cws.Step[] step;

    public Steps() {
    }

    public Steps(
           es.indra.www.portafirmasws.cws.SignModeEnum signMode,
           es.indra.www.portafirmasws.cws.Step[] step) {
           this.signMode = signMode;
           this.step = step;
    }


    /**
     * Gets the signMode value for this Steps.
     * 
     * @return signMode
     */
    public es.indra.www.portafirmasws.cws.SignModeEnum getSignMode() {
        return signMode;
    }


    /**
     * Sets the signMode value for this Steps.
     * 
     * @param signMode
     */
    public void setSignMode(es.indra.www.portafirmasws.cws.SignModeEnum signMode) {
        this.signMode = signMode;
    }


    /**
     * Gets the step value for this Steps.
     * 
     * @return step
     */
    public es.indra.www.portafirmasws.cws.Step[] getStep() {
        return step;
    }


    /**
     * Sets the step value for this Steps.
     * 
     * @param step
     */
    public void setStep(es.indra.www.portafirmasws.cws.Step[] step) {
        this.step = step;
    }

    public es.indra.www.portafirmasws.cws.Step getStep(int i) {
        return this.step[i];
    }

    public void setStep(int i, es.indra.www.portafirmasws.cws.Step _value) {
        this.step[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Steps)) return false;
        Steps other = (Steps) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.signMode==null && other.getSignMode()==null) || 
             (this.signMode!=null &&
              this.signMode.equals(other.getSignMode()))) &&
            ((this.step==null && other.getStep()==null) || 
             (this.step!=null &&
              java.util.Arrays.equals(this.step, other.getStep())));
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
        if (getSignMode() != null) {
            _hashCode += getSignMode().hashCode();
        }
        if (getStep() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getStep());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getStep(), i);
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
        new org.apache.axis.description.TypeDesc(Steps.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Steps"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signMode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sign-mode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignModeEnum"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("step");
        elemField.setXmlName(new javax.xml.namespace.QName("", "step"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Step"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
