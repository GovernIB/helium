/**
 * ArchiveOptions.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.indra.www.portafirmasws.cws;

public class ArchiveOptions  implements java.io.Serializable {
    private es.indra.www.portafirmasws.cws.ArchiveLocator[] sourceLocators;

    private es.indra.www.portafirmasws.cws.ArchiveLocator[] destinationLocators;

    private es.indra.www.portafirmasws.cws.ArchiveMetadata[] archiveMetadatas;

    public ArchiveOptions() {
    }

    public ArchiveOptions(
           es.indra.www.portafirmasws.cws.ArchiveLocator[] sourceLocators,
           es.indra.www.portafirmasws.cws.ArchiveLocator[] destinationLocators,
           es.indra.www.portafirmasws.cws.ArchiveMetadata[] archiveMetadatas) {
           this.sourceLocators = sourceLocators;
           this.destinationLocators = destinationLocators;
           this.archiveMetadatas = archiveMetadatas;
    }


    /**
     * Gets the sourceLocators value for this ArchiveOptions.
     * 
     * @return sourceLocators
     */
    public es.indra.www.portafirmasws.cws.ArchiveLocator[] getSourceLocators() {
        return sourceLocators;
    }


    /**
     * Sets the sourceLocators value for this ArchiveOptions.
     * 
     * @param sourceLocators
     */
    public void setSourceLocators(es.indra.www.portafirmasws.cws.ArchiveLocator[] sourceLocators) {
        this.sourceLocators = sourceLocators;
    }


    /**
     * Gets the destinationLocators value for this ArchiveOptions.
     * 
     * @return destinationLocators
     */
    public es.indra.www.portafirmasws.cws.ArchiveLocator[] getDestinationLocators() {
        return destinationLocators;
    }


    /**
     * Sets the destinationLocators value for this ArchiveOptions.
     * 
     * @param destinationLocators
     */
    public void setDestinationLocators(es.indra.www.portafirmasws.cws.ArchiveLocator[] destinationLocators) {
        this.destinationLocators = destinationLocators;
    }


    /**
     * Gets the archiveMetadatas value for this ArchiveOptions.
     * 
     * @return archiveMetadatas
     */
    public es.indra.www.portafirmasws.cws.ArchiveMetadata[] getArchiveMetadatas() {
        return archiveMetadatas;
    }


    /**
     * Sets the archiveMetadatas value for this ArchiveOptions.
     * 
     * @param archiveMetadatas
     */
    public void setArchiveMetadatas(es.indra.www.portafirmasws.cws.ArchiveMetadata[] archiveMetadatas) {
        this.archiveMetadatas = archiveMetadatas;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArchiveOptions)) return false;
        ArchiveOptions other = (ArchiveOptions) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.sourceLocators==null && other.getSourceLocators()==null) || 
             (this.sourceLocators!=null &&
              java.util.Arrays.equals(this.sourceLocators, other.getSourceLocators()))) &&
            ((this.destinationLocators==null && other.getDestinationLocators()==null) || 
             (this.destinationLocators!=null &&
              java.util.Arrays.equals(this.destinationLocators, other.getDestinationLocators()))) &&
            ((this.archiveMetadatas==null && other.getArchiveMetadatas()==null) || 
             (this.archiveMetadatas!=null &&
              java.util.Arrays.equals(this.archiveMetadatas, other.getArchiveMetadatas())));
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
        if (getSourceLocators() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSourceLocators());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSourceLocators(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDestinationLocators() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDestinationLocators());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDestinationLocators(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getArchiveMetadatas() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getArchiveMetadatas());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getArchiveMetadatas(), i);
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
        new org.apache.axis.description.TypeDesc(ArchiveOptions.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ArchiveOptions"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sourceLocators");
        elemField.setXmlName(new javax.xml.namespace.QName("", "source-locators"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ArchiveLocator"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("", "archive-locator"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("destinationLocators");
        elemField.setXmlName(new javax.xml.namespace.QName("", "destination-locators"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ArchiveLocator"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("", "archive-locator"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("archiveMetadatas");
        elemField.setXmlName(new javax.xml.namespace.QName("", "archive-metadatas"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ArchiveMetadata"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("", "archive-metadata"));
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
