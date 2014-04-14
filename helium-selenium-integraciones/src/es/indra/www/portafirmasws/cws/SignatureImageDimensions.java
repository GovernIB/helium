/**
 * SignatureImageDimensions.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.indra.www.portafirmasws.cws;

public class SignatureImageDimensions  implements java.io.Serializable {
    private java.lang.String width;

    private java.lang.String height;

    private java.lang.String x_padding;

    private java.lang.String y_padding;

    public SignatureImageDimensions() {
    }

    public SignatureImageDimensions(
           java.lang.String width,
           java.lang.String height,
           java.lang.String x_padding,
           java.lang.String y_padding) {
           this.width = width;
           this.height = height;
           this.x_padding = x_padding;
           this.y_padding = y_padding;
    }


    /**
     * Gets the width value for this SignatureImageDimensions.
     * 
     * @return width
     */
    public java.lang.String getWidth() {
        return width;
    }


    /**
     * Sets the width value for this SignatureImageDimensions.
     * 
     * @param width
     */
    public void setWidth(java.lang.String width) {
        this.width = width;
    }


    /**
     * Gets the height value for this SignatureImageDimensions.
     * 
     * @return height
     */
    public java.lang.String getHeight() {
        return height;
    }


    /**
     * Sets the height value for this SignatureImageDimensions.
     * 
     * @param height
     */
    public void setHeight(java.lang.String height) {
        this.height = height;
    }


    /**
     * Gets the x_padding value for this SignatureImageDimensions.
     * 
     * @return x_padding
     */
    public java.lang.String getX_padding() {
        return x_padding;
    }


    /**
     * Sets the x_padding value for this SignatureImageDimensions.
     * 
     * @param x_padding
     */
    public void setX_padding(java.lang.String x_padding) {
        this.x_padding = x_padding;
    }


    /**
     * Gets the y_padding value for this SignatureImageDimensions.
     * 
     * @return y_padding
     */
    public java.lang.String getY_padding() {
        return y_padding;
    }


    /**
     * Sets the y_padding value for this SignatureImageDimensions.
     * 
     * @param y_padding
     */
    public void setY_padding(java.lang.String y_padding) {
        this.y_padding = y_padding;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SignatureImageDimensions)) return false;
        SignatureImageDimensions other = (SignatureImageDimensions) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.width==null && other.getWidth()==null) || 
             (this.width!=null &&
              this.width.equals(other.getWidth()))) &&
            ((this.height==null && other.getHeight()==null) || 
             (this.height!=null &&
              this.height.equals(other.getHeight()))) &&
            ((this.x_padding==null && other.getX_padding()==null) || 
             (this.x_padding!=null &&
              this.x_padding.equals(other.getX_padding()))) &&
            ((this.y_padding==null && other.getY_padding()==null) || 
             (this.y_padding!=null &&
              this.y_padding.equals(other.getY_padding())));
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
        if (getWidth() != null) {
            _hashCode += getWidth().hashCode();
        }
        if (getHeight() != null) {
            _hashCode += getHeight().hashCode();
        }
        if (getX_padding() != null) {
            _hashCode += getX_padding().hashCode();
        }
        if (getY_padding() != null) {
            _hashCode += getY_padding().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SignatureImageDimensions.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignatureImageDimensions"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("width");
        elemField.setXmlName(new javax.xml.namespace.QName("", "width"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("height");
        elemField.setXmlName(new javax.xml.namespace.QName("", "height"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("x_padding");
        elemField.setXmlName(new javax.xml.namespace.QName("", "x_padding"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("y_padding");
        elemField.setXmlName(new javax.xml.namespace.QName("", "y_padding"));
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
