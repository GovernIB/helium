/**
 * SearchResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente;

public class SearchResponse  implements java.io.Serializable {
    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Result result;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchResult searchResult;

    private java.lang.String version;  // attribute

    public SearchResponse() {
    }

    public SearchResponse(
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Result result,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchResult searchResult,
           java.lang.String version) {
           this.result = result;
           this.searchResult = searchResult;
           this.version = version;
    }


    /**
     * Gets the result value for this SearchResponse.
     * 
     * @return result
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Result getResult() {
        return result;
    }


    /**
     * Sets the result value for this SearchResponse.
     * 
     * @param result
     */
    public void setResult(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Result result) {
        this.result = result;
    }


    /**
     * Gets the searchResult value for this SearchResponse.
     * 
     * @return searchResult
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchResult getSearchResult() {
        return searchResult;
    }


    /**
     * Sets the searchResult value for this SearchResponse.
     * 
     * @param searchResult
     */
    public void setSearchResult(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SearchResult searchResult) {
        this.searchResult = searchResult;
    }


    /**
     * Gets the version value for this SearchResponse.
     * 
     * @return version
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this SearchResponse.
     * 
     * @param version
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SearchResponse)) return false;
        SearchResponse other = (SearchResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
            ((this.searchResult==null && other.getSearchResult()==null) || 
             (this.searchResult!=null &&
              this.searchResult.equals(other.getSearchResult()))) &&
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
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        if (getSearchResult() != null) {
            _hashCode += getSearchResult().hashCode();
        }
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SearchResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", ">search-response"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("version");
        attrField.setXmlName(new javax.xml.namespace.QName("", "version"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Result"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchResult");
        elemField.setXmlName(new javax.xml.namespace.QName("", "search-result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SearchResult"));
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
