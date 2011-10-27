/**
 * ConditionEnum.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl;

@SuppressWarnings({"serial", "unchecked", "rawtypes"})
public class ConditionEnum implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ConditionEnum(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _in = "in";
    public static final java.lang.String _between = "between";
    public static final java.lang.String _equal = "equal";
    public static final java.lang.String _notEqual = "notEqual";
    public static final java.lang.String _greater = "greater";
    public static final java.lang.String _greaterEqual = "greaterEqual";
    public static final java.lang.String _less = "less";
    public static final java.lang.String _lessEqual = "lessEqual";
    public static final ConditionEnum in = new ConditionEnum(_in);
    public static final ConditionEnum between = new ConditionEnum(_between);
    public static final ConditionEnum equal = new ConditionEnum(_equal);
    public static final ConditionEnum notEqual = new ConditionEnum(_notEqual);
    public static final ConditionEnum greater = new ConditionEnum(_greater);
    public static final ConditionEnum greaterEqual = new ConditionEnum(_greaterEqual);
    public static final ConditionEnum less = new ConditionEnum(_less);
    public static final ConditionEnum lessEqual = new ConditionEnum(_lessEqual);
    public java.lang.String getValue() { return _value_;}
    public static ConditionEnum fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        ConditionEnum enumeration = (ConditionEnum)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ConditionEnum fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConditionEnum.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ConditionEnum"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
