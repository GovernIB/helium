/**
 * PdfPosition.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.indra.www.portafirmasws.cws;

public class PdfPosition  implements java.io.Serializable {
    private es.indra.www.portafirmasws.cws.Format format;

    private java.lang.String sheet;

    private java.lang.String x_0;

    private java.lang.String y_0;

    private java.lang.String x_1;

    private java.lang.String y_1;

    public PdfPosition() {
    }

    public PdfPosition(
           es.indra.www.portafirmasws.cws.Format format,
           java.lang.String sheet,
           java.lang.String x_0,
           java.lang.String y_0,
           java.lang.String x_1,
           java.lang.String y_1) {
           this.format = format;
           this.sheet = sheet;
           this.x_0 = x_0;
           this.y_0 = y_0;
           this.x_1 = x_1;
           this.y_1 = y_1;
    }


    /**
     * Gets the format value for this PdfPosition.
     * 
     * @return format
     */
    public es.indra.www.portafirmasws.cws.Format getFormat() {
        return format;
    }


    /**
     * Sets the format value for this PdfPosition.
     * 
     * @param format
     */
    public void setFormat(es.indra.www.portafirmasws.cws.Format format) {
        this.format = format;
    }


    /**
     * Gets the sheet value for this PdfPosition.
     * 
     * @return sheet
     */
    public java.lang.String getSheet() {
        return sheet;
    }


    /**
     * Sets the sheet value for this PdfPosition.
     * 
     * @param sheet
     */
    public void setSheet(java.lang.String sheet) {
        this.sheet = sheet;
    }


    /**
     * Gets the x_0 value for this PdfPosition.
     * 
     * @return x_0
     */
    public java.lang.String getX_0() {
        return x_0;
    }


    /**
     * Sets the x_0 value for this PdfPosition.
     * 
     * @param x_0
     */
    public void setX_0(java.lang.String x_0) {
        this.x_0 = x_0;
    }


    /**
     * Gets the y_0 value for this PdfPosition.
     * 
     * @return y_0
     */
    public java.lang.String getY_0() {
        return y_0;
    }


    /**
     * Sets the y_0 value for this PdfPosition.
     * 
     * @param y_0
     */
    public void setY_0(java.lang.String y_0) {
        this.y_0 = y_0;
    }


    /**
     * Gets the x_1 value for this PdfPosition.
     * 
     * @return x_1
     */
    public java.lang.String getX_1() {
        return x_1;
    }


    /**
     * Sets the x_1 value for this PdfPosition.
     * 
     * @param x_1
     */
    public void setX_1(java.lang.String x_1) {
        this.x_1 = x_1;
    }


    /**
     * Gets the y_1 value for this PdfPosition.
     * 
     * @return y_1
     */
    public java.lang.String getY_1() {
        return y_1;
    }


    /**
     * Sets the y_1 value for this PdfPosition.
     * 
     * @param y_1
     */
    public void setY_1(java.lang.String y_1) {
        this.y_1 = y_1;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PdfPosition)) return false;
        PdfPosition other = (PdfPosition) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.format==null && other.getFormat()==null) || 
             (this.format!=null &&
              this.format.equals(other.getFormat()))) &&
            ((this.sheet==null && other.getSheet()==null) || 
             (this.sheet!=null &&
              this.sheet.equals(other.getSheet()))) &&
            ((this.x_0==null && other.getX_0()==null) || 
             (this.x_0!=null &&
              this.x_0.equals(other.getX_0()))) &&
            ((this.y_0==null && other.getY_0()==null) || 
             (this.y_0!=null &&
              this.y_0.equals(other.getY_0()))) &&
            ((this.x_1==null && other.getX_1()==null) || 
             (this.x_1!=null &&
              this.x_1.equals(other.getX_1()))) &&
            ((this.y_1==null && other.getY_1()==null) || 
             (this.y_1!=null &&
              this.y_1.equals(other.getY_1())));
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
        if (getFormat() != null) {
            _hashCode += getFormat().hashCode();
        }
        if (getSheet() != null) {
            _hashCode += getSheet().hashCode();
        }
        if (getX_0() != null) {
            _hashCode += getX_0().hashCode();
        }
        if (getY_0() != null) {
            _hashCode += getY_0().hashCode();
        }
        if (getX_1() != null) {
            _hashCode += getX_1().hashCode();
        }
        if (getY_1() != null) {
            _hashCode += getY_1().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PdfPosition.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "PdfPosition"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("format");
        elemField.setXmlName(new javax.xml.namespace.QName("", "format"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Format"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sheet");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sheet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("x_0");
        elemField.setXmlName(new javax.xml.namespace.QName("", "x_0"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("y_0");
        elemField.setXmlName(new javax.xml.namespace.QName("", "y_0"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("x_1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "x_1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("y_1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "y_1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
