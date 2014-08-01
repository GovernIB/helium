/**
 * ListRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente;

public class ListRequest  implements java.io.Serializable {
    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Application application;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListRequestDocument[] documents;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Field[] searchCriterias;

    private java.lang.String version;  // attribute

    public ListRequest() {
    }

    public ListRequest(
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Application application,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListRequestDocument[] documents,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Field[] searchCriterias,
           java.lang.String version) {
           this.application = application;
           this.documents = documents;
           this.searchCriterias = searchCriterias;
           this.version = version;
    }


    /**
     * Gets the application value for this ListRequest.
     * 
     * @return application
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Application getApplication() {
        return application;
    }


    /**
     * Sets the application value for this ListRequest.
     * 
     * @param application
     */
    public void setApplication(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Application application) {
        this.application = application;
    }


    /**
     * Gets the documents value for this ListRequest.
     * 
     * @return documents
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListRequestDocument[] getDocuments() {
        return documents;
    }


    /**
     * Sets the documents value for this ListRequest.
     * 
     * @param documents
     */
    public void setDocuments(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListRequestDocument[] documents) {
        this.documents = documents;
    }


    /**
     * Gets the searchCriterias value for this ListRequest.
     * 
     * @return searchCriterias
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Field[] getSearchCriterias() {
        return searchCriterias;
    }


    /**
     * Sets the searchCriterias value for this ListRequest.
     * 
     * @param searchCriterias
     */
    public void setSearchCriterias(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Field[] searchCriterias) {
        this.searchCriterias = searchCriterias;
    }


    /**
     * Gets the version value for this ListRequest.
     * 
     * @return version
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this ListRequest.
     * 
     * @param version
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ListRequest)) return false;
        ListRequest other = (ListRequest) obj;
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
            ((this.documents==null && other.getDocuments()==null) || 
             (this.documents!=null &&
              java.util.Arrays.equals(this.documents, other.getDocuments()))) &&
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
        if (getDocuments() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDocuments());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDocuments(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
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
        new org.apache.axis.description.TypeDesc(ListRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">list-request"));
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
        elemField.setFieldName("documents");
        elemField.setXmlName(new javax.xml.namespace.QName("", "documents"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ListRequestDocument"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "document"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchCriterias");
        elemField.setXmlName(new javax.xml.namespace.QName("", "search-criterias"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Field"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("", "search-criteria"));
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
