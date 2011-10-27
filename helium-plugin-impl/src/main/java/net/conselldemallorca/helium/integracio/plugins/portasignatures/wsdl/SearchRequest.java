/**
 * SearchRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl;

@SuppressWarnings({"serial", "unused", "rawtypes"})
public class SearchRequest  implements java.io.Serializable {
    private Application application;

    private Field[] searchCriterias;

    private java.lang.String version;  // attribute

    public SearchRequest() {
    }

    public SearchRequest(
           Application application,
           Field[] searchCriterias,
           java.lang.String version) {
           this.application = application;
           this.searchCriterias = searchCriterias;
           this.version = version;
    }


    /**
     * Gets the application value for this SearchRequest.
     * 
     * @return application
     */
    public Application getApplication() {
        return application;
    }


    /**
     * Sets the application value for this SearchRequest.
     * 
     * @param application
     */
    public void setApplication(Application application) {
        this.application = application;
    }


    /**
     * Gets the searchCriterias value for this SearchRequest.
     * 
     * @return searchCriterias
     */
    public Field[] getSearchCriterias() {
        return searchCriterias;
    }


    /**
     * Sets the searchCriterias value for this SearchRequest.
     * 
     * @param searchCriterias
     */
    public void setSearchCriterias(Field[] searchCriterias) {
        this.searchCriterias = searchCriterias;
    }


    /**
     * Gets the version value for this SearchRequest.
     * 
     * @return version
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this SearchRequest.
     * 
     * @param version
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SearchRequest)) return false;
        SearchRequest other = (SearchRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.application==null && other.getApplication()==null) || 
             (this.application!=null &&
              this.application.equals(other.getApplication()))) &&
            ((this.searchCriterias==null && other.getSearchCriterias()==null) || 
             (this.searchCriterias!=null &&
              java.util.Arrays.equals(this.searchCriterias, other.getSearchCriterias()))) &&
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
        if (getApplication() != null) {
            _hashCode += getApplication().hashCode();
        }
        if (getSearchCriterias() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSearchCriterias());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSearchCriterias(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SearchRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">search-request"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("version");
        attrField.setXmlName(new javax.xml.namespace.QName("", "version"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("application");
        elemField.setXmlName(new javax.xml.namespace.QName("", "application"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Application"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchCriterias");
        elemField.setXmlName(new javax.xml.namespace.QName("", "search-criterias"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Field"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("", "item"));
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
