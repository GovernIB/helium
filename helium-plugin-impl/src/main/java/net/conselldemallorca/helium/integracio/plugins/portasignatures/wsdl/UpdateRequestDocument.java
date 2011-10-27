/**
 * UpdateRequestDocument.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl;

@SuppressWarnings({"serial", "unused", "rawtypes"})
public class UpdateRequestDocument  implements java.io.Serializable {
    private int id;

    private DocumentAttributes attributes;

    private ArchiveOptions archiveOptions;

    private Annex[] annexes;

    private Steps steps;

    public UpdateRequestDocument() {
    }

    public UpdateRequestDocument(
           int id,
           DocumentAttributes attributes,
           ArchiveOptions archiveOptions,
           Annex[] annexes,
           Steps steps) {
           this.id = id;
           this.attributes = attributes;
           this.archiveOptions = archiveOptions;
           this.annexes = annexes;
           this.steps = steps;
    }


    /**
     * Gets the id value for this UpdateRequestDocument.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the id value for this UpdateRequestDocument.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Gets the attributes value for this UpdateRequestDocument.
     * 
     * @return attributes
     */
    public DocumentAttributes getAttributes() {
        return attributes;
    }


    /**
     * Sets the attributes value for this UpdateRequestDocument.
     * 
     * @param attributes
     */
    public void setAttributes(DocumentAttributes attributes) {
        this.attributes = attributes;
    }


    /**
     * Gets the archiveOptions value for this UpdateRequestDocument.
     * 
     * @return archiveOptions
     */
    public ArchiveOptions getArchiveOptions() {
        return archiveOptions;
    }


    /**
     * Sets the archiveOptions value for this UpdateRequestDocument.
     * 
     * @param archiveOptions
     */
    public void setArchiveOptions(ArchiveOptions archiveOptions) {
        this.archiveOptions = archiveOptions;
    }


    /**
     * Gets the annexes value for this UpdateRequestDocument.
     * 
     * @return annexes
     */
    public Annex[] getAnnexes() {
        return annexes;
    }


    /**
     * Sets the annexes value for this UpdateRequestDocument.
     * 
     * @param annexes
     */
    public void setAnnexes(Annex[] annexes) {
        this.annexes = annexes;
    }


    /**
     * Gets the steps value for this UpdateRequestDocument.
     * 
     * @return steps
     */
    public Steps getSteps() {
        return steps;
    }


    /**
     * Sets the steps value for this UpdateRequestDocument.
     * 
     * @param steps
     */
    public void setSteps(Steps steps) {
        this.steps = steps;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UpdateRequestDocument)) return false;
        UpdateRequestDocument other = (UpdateRequestDocument) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.id == other.getId() &&
            ((this.attributes==null && other.getAttributes()==null) || 
             (this.attributes!=null &&
              this.attributes.equals(other.getAttributes()))) &&
            ((this.archiveOptions==null && other.getArchiveOptions()==null) || 
             (this.archiveOptions!=null &&
              this.archiveOptions.equals(other.getArchiveOptions()))) &&
            ((this.annexes==null && other.getAnnexes()==null) || 
             (this.annexes!=null &&
              java.util.Arrays.equals(this.annexes, other.getAnnexes()))) &&
            ((this.steps==null && other.getSteps()==null) || 
             (this.steps!=null &&
              this.steps.equals(other.getSteps())));
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
        _hashCode += getId();
        if (getAttributes() != null) {
            _hashCode += getAttributes().hashCode();
        }
        if (getArchiveOptions() != null) {
            _hashCode += getArchiveOptions().hashCode();
        }
        if (getAnnexes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAnnexes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAnnexes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSteps() != null) {
            _hashCode += getSteps().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UpdateRequestDocument.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "UpdateRequestDocument"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attributes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "attributes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DocumentAttributes"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("archiveOptions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "archive-options"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ArchiveOptions"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("annexes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "annexes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Annex"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "annex"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("steps");
        elemField.setXmlName(new javax.xml.namespace.QName("", "steps"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Steps"));
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
