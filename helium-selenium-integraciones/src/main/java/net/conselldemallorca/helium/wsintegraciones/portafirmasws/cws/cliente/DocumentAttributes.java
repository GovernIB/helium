/**
 * DocumentAttributes.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente;

public class DocumentAttributes  implements java.io.Serializable {
    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.StateEnum state;

    private java.lang.String title;

    private java.lang.Integer type;

    private java.lang.String subject;

    private java.lang.String description;

    private java.lang.String extension;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Sender sender;

    private java.lang.String url;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ImportanceEnum importance;

    private java.util.Calendar dateEntry;

    private java.util.Calendar dateLimit;

    private java.util.Calendar dateLastUpdate;

    private java.util.Calendar dateNotice;

    private java.lang.Integer numberAnnexes;

    private java.lang.Boolean signAnnexes;

    private java.lang.String externalData;

    private java.lang.Integer typeSign;

    private java.lang.Boolean isFileSign;

    private java.lang.Boolean generateVisuals;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ExternalIDs externalIds;

    public DocumentAttributes() {
    }

    public DocumentAttributes(
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.StateEnum state,
           java.lang.String title,
           java.lang.Integer type,
           java.lang.String subject,
           java.lang.String description,
           java.lang.String extension,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Sender sender,
           java.lang.String url,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ImportanceEnum importance,
           java.util.Calendar dateEntry,
           java.util.Calendar dateLimit,
           java.util.Calendar dateLastUpdate,
           java.util.Calendar dateNotice,
           java.lang.Integer numberAnnexes,
           java.lang.Boolean signAnnexes,
           java.lang.String externalData,
           java.lang.Integer typeSign,
           java.lang.Boolean isFileSign,
           java.lang.Boolean generateVisuals,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ExternalIDs externalIds) {
           this.state = state;
           this.title = title;
           this.type = type;
           this.subject = subject;
           this.description = description;
           this.extension = extension;
           this.sender = sender;
           this.url = url;
           this.importance = importance;
           this.dateEntry = dateEntry;
           this.dateLimit = dateLimit;
           this.dateLastUpdate = dateLastUpdate;
           this.dateNotice = dateNotice;
           this.numberAnnexes = numberAnnexes;
           this.signAnnexes = signAnnexes;
           this.externalData = externalData;
           this.typeSign = typeSign;
           this.isFileSign = isFileSign;
           this.generateVisuals = generateVisuals;
           this.externalIds = externalIds;
    }


    /**
     * Gets the state value for this DocumentAttributes.
     * 
     * @return state
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.StateEnum getState() {
        return state;
    }


    /**
     * Sets the state value for this DocumentAttributes.
     * 
     * @param state
     */
    public void setState(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.StateEnum state) {
        this.state = state;
    }


    /**
     * Gets the title value for this DocumentAttributes.
     * 
     * @return title
     */
    public java.lang.String getTitle() {
        return title;
    }


    /**
     * Sets the title value for this DocumentAttributes.
     * 
     * @param title
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }


    /**
     * Gets the type value for this DocumentAttributes.
     * 
     * @return type
     */
    public java.lang.Integer getType() {
        return type;
    }


    /**
     * Sets the type value for this DocumentAttributes.
     * 
     * @param type
     */
    public void setType(java.lang.Integer type) {
        this.type = type;
    }


    /**
     * Gets the subject value for this DocumentAttributes.
     * 
     * @return subject
     */
    public java.lang.String getSubject() {
        return subject;
    }


    /**
     * Sets the subject value for this DocumentAttributes.
     * 
     * @param subject
     */
    public void setSubject(java.lang.String subject) {
        this.subject = subject;
    }


    /**
     * Gets the description value for this DocumentAttributes.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this DocumentAttributes.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the extension value for this DocumentAttributes.
     * 
     * @return extension
     */
    public java.lang.String getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this DocumentAttributes.
     * 
     * @param extension
     */
    public void setExtension(java.lang.String extension) {
        this.extension = extension;
    }


    /**
     * Gets the sender value for this DocumentAttributes.
     * 
     * @return sender
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Sender getSender() {
        return sender;
    }


    /**
     * Sets the sender value for this DocumentAttributes.
     * 
     * @param sender
     */
    public void setSender(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Sender sender) {
        this.sender = sender;
    }


    /**
     * Gets the url value for this DocumentAttributes.
     * 
     * @return url
     */
    public java.lang.String getUrl() {
        return url;
    }


    /**
     * Sets the url value for this DocumentAttributes.
     * 
     * @param url
     */
    public void setUrl(java.lang.String url) {
        this.url = url;
    }


    /**
     * Gets the importance value for this DocumentAttributes.
     * 
     * @return importance
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ImportanceEnum getImportance() {
        return importance;
    }


    /**
     * Sets the importance value for this DocumentAttributes.
     * 
     * @param importance
     */
    public void setImportance(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ImportanceEnum importance) {
        this.importance = importance;
    }


    /**
     * Gets the dateEntry value for this DocumentAttributes.
     * 
     * @return dateEntry
     */
    public java.util.Calendar getDateEntry() {
        return dateEntry;
    }


    /**
     * Sets the dateEntry value for this DocumentAttributes.
     * 
     * @param dateEntry
     */
    public void setDateEntry(java.util.Calendar dateEntry) {
        this.dateEntry = dateEntry;
    }


    /**
     * Gets the dateLimit value for this DocumentAttributes.
     * 
     * @return dateLimit
     */
    public java.util.Calendar getDateLimit() {
        return dateLimit;
    }


    /**
     * Sets the dateLimit value for this DocumentAttributes.
     * 
     * @param dateLimit
     */
    public void setDateLimit(java.util.Calendar dateLimit) {
        this.dateLimit = dateLimit;
    }


    /**
     * Gets the dateLastUpdate value for this DocumentAttributes.
     * 
     * @return dateLastUpdate
     */
    public java.util.Calendar getDateLastUpdate() {
        return dateLastUpdate;
    }


    /**
     * Sets the dateLastUpdate value for this DocumentAttributes.
     * 
     * @param dateLastUpdate
     */
    public void setDateLastUpdate(java.util.Calendar dateLastUpdate) {
        this.dateLastUpdate = dateLastUpdate;
    }


    /**
     * Gets the dateNotice value for this DocumentAttributes.
     * 
     * @return dateNotice
     */
    public java.util.Calendar getDateNotice() {
        return dateNotice;
    }


    /**
     * Sets the dateNotice value for this DocumentAttributes.
     * 
     * @param dateNotice
     */
    public void setDateNotice(java.util.Calendar dateNotice) {
        this.dateNotice = dateNotice;
    }


    /**
     * Gets the numberAnnexes value for this DocumentAttributes.
     * 
     * @return numberAnnexes
     */
    public java.lang.Integer getNumberAnnexes() {
        return numberAnnexes;
    }


    /**
     * Sets the numberAnnexes value for this DocumentAttributes.
     * 
     * @param numberAnnexes
     */
    public void setNumberAnnexes(java.lang.Integer numberAnnexes) {
        this.numberAnnexes = numberAnnexes;
    }


    /**
     * Gets the signAnnexes value for this DocumentAttributes.
     * 
     * @return signAnnexes
     */
    public java.lang.Boolean getSignAnnexes() {
        return signAnnexes;
    }


    /**
     * Sets the signAnnexes value for this DocumentAttributes.
     * 
     * @param signAnnexes
     */
    public void setSignAnnexes(java.lang.Boolean signAnnexes) {
        this.signAnnexes = signAnnexes;
    }


    /**
     * Gets the externalData value for this DocumentAttributes.
     * 
     * @return externalData
     */
    public java.lang.String getExternalData() {
        return externalData;
    }


    /**
     * Sets the externalData value for this DocumentAttributes.
     * 
     * @param externalData
     */
    public void setExternalData(java.lang.String externalData) {
        this.externalData = externalData;
    }


    /**
     * Gets the typeSign value for this DocumentAttributes.
     * 
     * @return typeSign
     */
    public java.lang.Integer getTypeSign() {
        return typeSign;
    }


    /**
     * Sets the typeSign value for this DocumentAttributes.
     * 
     * @param typeSign
     */
    public void setTypeSign(java.lang.Integer typeSign) {
        this.typeSign = typeSign;
    }


    /**
     * Gets the isFileSign value for this DocumentAttributes.
     * 
     * @return isFileSign
     */
    public java.lang.Boolean getIsFileSign() {
        return isFileSign;
    }


    /**
     * Sets the isFileSign value for this DocumentAttributes.
     * 
     * @param isFileSign
     */
    public void setIsFileSign(java.lang.Boolean isFileSign) {
        this.isFileSign = isFileSign;
    }


    /**
     * Gets the generateVisuals value for this DocumentAttributes.
     * 
     * @return generateVisuals
     */
    public java.lang.Boolean getGenerateVisuals() {
        return generateVisuals;
    }


    /**
     * Sets the generateVisuals value for this DocumentAttributes.
     * 
     * @param generateVisuals
     */
    public void setGenerateVisuals(java.lang.Boolean generateVisuals) {
        this.generateVisuals = generateVisuals;
    }


    /**
     * Gets the externalIds value for this DocumentAttributes.
     * 
     * @return externalIds
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ExternalIDs getExternalIds() {
        return externalIds;
    }


    /**
     * Sets the externalIds value for this DocumentAttributes.
     * 
     * @param externalIds
     */
    public void setExternalIds(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ExternalIDs externalIds) {
        this.externalIds = externalIds;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DocumentAttributes)) return false;
        DocumentAttributes other = (DocumentAttributes) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.state==null && other.getState()==null) || 
             (this.state!=null &&
              this.state.equals(other.getState()))) &&
            ((this.title==null && other.getTitle()==null) || 
             (this.title!=null &&
              this.title.equals(other.getTitle()))) &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.subject==null && other.getSubject()==null) || 
             (this.subject!=null &&
              this.subject.equals(other.getSubject()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.extension==null && other.getExtension()==null) || 
             (this.extension!=null &&
              this.extension.equals(other.getExtension()))) &&
            ((this.sender==null && other.getSender()==null) || 
             (this.sender!=null &&
              this.sender.equals(other.getSender()))) &&
            ((this.url==null && other.getUrl()==null) || 
             (this.url!=null &&
              this.url.equals(other.getUrl()))) &&
            ((this.importance==null && other.getImportance()==null) || 
             (this.importance!=null &&
              this.importance.equals(other.getImportance()))) &&
            ((this.dateEntry==null && other.getDateEntry()==null) || 
             (this.dateEntry!=null &&
              this.dateEntry.equals(other.getDateEntry()))) &&
            ((this.dateLimit==null && other.getDateLimit()==null) || 
             (this.dateLimit!=null &&
              this.dateLimit.equals(other.getDateLimit()))) &&
            ((this.dateLastUpdate==null && other.getDateLastUpdate()==null) || 
             (this.dateLastUpdate!=null &&
              this.dateLastUpdate.equals(other.getDateLastUpdate()))) &&
            ((this.dateNotice==null && other.getDateNotice()==null) || 
             (this.dateNotice!=null &&
              this.dateNotice.equals(other.getDateNotice()))) &&
            ((this.numberAnnexes==null && other.getNumberAnnexes()==null) || 
             (this.numberAnnexes!=null &&
              this.numberAnnexes.equals(other.getNumberAnnexes()))) &&
            ((this.signAnnexes==null && other.getSignAnnexes()==null) || 
             (this.signAnnexes!=null &&
              this.signAnnexes.equals(other.getSignAnnexes()))) &&
            ((this.externalData==null && other.getExternalData()==null) || 
             (this.externalData!=null &&
              this.externalData.equals(other.getExternalData()))) &&
            ((this.typeSign==null && other.getTypeSign()==null) || 
             (this.typeSign!=null &&
              this.typeSign.equals(other.getTypeSign()))) &&
            ((this.isFileSign==null && other.getIsFileSign()==null) || 
             (this.isFileSign!=null &&
              this.isFileSign.equals(other.getIsFileSign()))) &&
            ((this.generateVisuals==null && other.getGenerateVisuals()==null) || 
             (this.generateVisuals!=null &&
              this.generateVisuals.equals(other.getGenerateVisuals()))) &&
            ((this.externalIds==null && other.getExternalIds()==null) || 
             (this.externalIds!=null &&
              this.externalIds.equals(other.getExternalIds())));
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
        if (getState() != null) {
            _hashCode += getState().hashCode();
        }
        if (getTitle() != null) {
            _hashCode += getTitle().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getSubject() != null) {
            _hashCode += getSubject().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getExtension() != null) {
            _hashCode += getExtension().hashCode();
        }
        if (getSender() != null) {
            _hashCode += getSender().hashCode();
        }
        if (getUrl() != null) {
            _hashCode += getUrl().hashCode();
        }
        if (getImportance() != null) {
            _hashCode += getImportance().hashCode();
        }
        if (getDateEntry() != null) {
            _hashCode += getDateEntry().hashCode();
        }
        if (getDateLimit() != null) {
            _hashCode += getDateLimit().hashCode();
        }
        if (getDateLastUpdate() != null) {
            _hashCode += getDateLastUpdate().hashCode();
        }
        if (getDateNotice() != null) {
            _hashCode += getDateNotice().hashCode();
        }
        if (getNumberAnnexes() != null) {
            _hashCode += getNumberAnnexes().hashCode();
        }
        if (getSignAnnexes() != null) {
            _hashCode += getSignAnnexes().hashCode();
        }
        if (getExternalData() != null) {
            _hashCode += getExternalData().hashCode();
        }
        if (getTypeSign() != null) {
            _hashCode += getTypeSign().hashCode();
        }
        if (getIsFileSign() != null) {
            _hashCode += getIsFileSign().hashCode();
        }
        if (getGenerateVisuals() != null) {
            _hashCode += getGenerateVisuals().hashCode();
        }
        if (getExternalIds() != null) {
            _hashCode += getExternalIds().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DocumentAttributes.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DocumentAttributes"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("state");
        elemField.setXmlName(new javax.xml.namespace.QName("", "state"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "StateEnum"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("title");
        elemField.setXmlName(new javax.xml.namespace.QName("", "title"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subject");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sender");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sender"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Sender"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("url");
        elemField.setXmlName(new javax.xml.namespace.QName("", "url"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("importance");
        elemField.setXmlName(new javax.xml.namespace.QName("", "importance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ImportanceEnum"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateEntry");
        elemField.setXmlName(new javax.xml.namespace.QName("", "date-entry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateLimit");
        elemField.setXmlName(new javax.xml.namespace.QName("", "date-limit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateLastUpdate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "date-last-update"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateNotice");
        elemField.setXmlName(new javax.xml.namespace.QName("", "date-notice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberAnnexes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "number-annexes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signAnnexes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sign-annexes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("externalData");
        elemField.setXmlName(new javax.xml.namespace.QName("", "external-data"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("typeSign");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type-sign"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isFileSign");
        elemField.setXmlName(new javax.xml.namespace.QName("", "is-file-sign"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("generateVisuals");
        elemField.setXmlName(new javax.xml.namespace.QName("", "generate-visuals"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("externalIds");
        elemField.setXmlName(new javax.xml.namespace.QName("", "external-ids"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ExternalIDs"));
        elemField.setMinOccurs(0);
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
