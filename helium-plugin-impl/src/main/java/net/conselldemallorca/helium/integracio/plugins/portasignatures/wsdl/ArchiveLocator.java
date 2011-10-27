/**
 * ArchiveLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl;

@SuppressWarnings({"serial", "unused", "rawtypes"})
public class ArchiveLocator  implements java.io.Serializable {
    private java.lang.String repositoryId;

    private java.lang.String archiveUri;

    private java.lang.String archiveVersion;

    private java.lang.String repositoryBase;

    private java.lang.String folderName;

    private java.lang.String filePathName;

    private java.lang.String signatureFilesPathPattern;

    private java.lang.String visualFilePathName;

    private java.lang.String retentionPolicy;

    private java.lang.Boolean signatureCustody;

    public ArchiveLocator() {
    }

    public ArchiveLocator(
           java.lang.String repositoryId,
           java.lang.String archiveUri,
           java.lang.String archiveVersion,
           java.lang.String repositoryBase,
           java.lang.String folderName,
           java.lang.String filePathName,
           java.lang.String signatureFilesPathPattern,
           java.lang.String visualFilePathName,
           java.lang.String retentionPolicy,
           java.lang.Boolean signatureCustody) {
           this.repositoryId = repositoryId;
           this.archiveUri = archiveUri;
           this.archiveVersion = archiveVersion;
           this.repositoryBase = repositoryBase;
           this.folderName = folderName;
           this.filePathName = filePathName;
           this.signatureFilesPathPattern = signatureFilesPathPattern;
           this.visualFilePathName = visualFilePathName;
           this.retentionPolicy = retentionPolicy;
           this.signatureCustody = signatureCustody;
    }


    /**
     * Gets the repositoryId value for this ArchiveLocator.
     * 
     * @return repositoryId
     */
    public java.lang.String getRepositoryId() {
        return repositoryId;
    }


    /**
     * Sets the repositoryId value for this ArchiveLocator.
     * 
     * @param repositoryId
     */
    public void setRepositoryId(java.lang.String repositoryId) {
        this.repositoryId = repositoryId;
    }


    /**
     * Gets the archiveUri value for this ArchiveLocator.
     * 
     * @return archiveUri
     */
    public java.lang.String getArchiveUri() {
        return archiveUri;
    }


    /**
     * Sets the archiveUri value for this ArchiveLocator.
     * 
     * @param archiveUri
     */
    public void setArchiveUri(java.lang.String archiveUri) {
        this.archiveUri = archiveUri;
    }


    /**
     * Gets the archiveVersion value for this ArchiveLocator.
     * 
     * @return archiveVersion
     */
    public java.lang.String getArchiveVersion() {
        return archiveVersion;
    }


    /**
     * Sets the archiveVersion value for this ArchiveLocator.
     * 
     * @param archiveVersion
     */
    public void setArchiveVersion(java.lang.String archiveVersion) {
        this.archiveVersion = archiveVersion;
    }


    /**
     * Gets the repositoryBase value for this ArchiveLocator.
     * 
     * @return repositoryBase
     */
    public java.lang.String getRepositoryBase() {
        return repositoryBase;
    }


    /**
     * Sets the repositoryBase value for this ArchiveLocator.
     * 
     * @param repositoryBase
     */
    public void setRepositoryBase(java.lang.String repositoryBase) {
        this.repositoryBase = repositoryBase;
    }


    /**
     * Gets the folderName value for this ArchiveLocator.
     * 
     * @return folderName
     */
    public java.lang.String getFolderName() {
        return folderName;
    }


    /**
     * Sets the folderName value for this ArchiveLocator.
     * 
     * @param folderName
     */
    public void setFolderName(java.lang.String folderName) {
        this.folderName = folderName;
    }


    /**
     * Gets the filePathName value for this ArchiveLocator.
     * 
     * @return filePathName
     */
    public java.lang.String getFilePathName() {
        return filePathName;
    }


    /**
     * Sets the filePathName value for this ArchiveLocator.
     * 
     * @param filePathName
     */
    public void setFilePathName(java.lang.String filePathName) {
        this.filePathName = filePathName;
    }


    /**
     * Gets the signatureFilesPathPattern value for this ArchiveLocator.
     * 
     * @return signatureFilesPathPattern
     */
    public java.lang.String getSignatureFilesPathPattern() {
        return signatureFilesPathPattern;
    }


    /**
     * Sets the signatureFilesPathPattern value for this ArchiveLocator.
     * 
     * @param signatureFilesPathPattern
     */
    public void setSignatureFilesPathPattern(java.lang.String signatureFilesPathPattern) {
        this.signatureFilesPathPattern = signatureFilesPathPattern;
    }


    /**
     * Gets the visualFilePathName value for this ArchiveLocator.
     * 
     * @return visualFilePathName
     */
    public java.lang.String getVisualFilePathName() {
        return visualFilePathName;
    }


    /**
     * Sets the visualFilePathName value for this ArchiveLocator.
     * 
     * @param visualFilePathName
     */
    public void setVisualFilePathName(java.lang.String visualFilePathName) {
        this.visualFilePathName = visualFilePathName;
    }


    /**
     * Gets the retentionPolicy value for this ArchiveLocator.
     * 
     * @return retentionPolicy
     */
    public java.lang.String getRetentionPolicy() {
        return retentionPolicy;
    }


    /**
     * Sets the retentionPolicy value for this ArchiveLocator.
     * 
     * @param retentionPolicy
     */
    public void setRetentionPolicy(java.lang.String retentionPolicy) {
        this.retentionPolicy = retentionPolicy;
    }


    /**
     * Gets the signatureCustody value for this ArchiveLocator.
     * 
     * @return signatureCustody
     */
    public java.lang.Boolean getSignatureCustody() {
        return signatureCustody;
    }


    /**
     * Sets the signatureCustody value for this ArchiveLocator.
     * 
     * @param signatureCustody
     */
    public void setSignatureCustody(java.lang.Boolean signatureCustody) {
        this.signatureCustody = signatureCustody;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArchiveLocator)) return false;
        ArchiveLocator other = (ArchiveLocator) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.repositoryId==null && other.getRepositoryId()==null) || 
             (this.repositoryId!=null &&
              this.repositoryId.equals(other.getRepositoryId()))) &&
            ((this.archiveUri==null && other.getArchiveUri()==null) || 
             (this.archiveUri!=null &&
              this.archiveUri.equals(other.getArchiveUri()))) &&
            ((this.archiveVersion==null && other.getArchiveVersion()==null) || 
             (this.archiveVersion!=null &&
              this.archiveVersion.equals(other.getArchiveVersion()))) &&
            ((this.repositoryBase==null && other.getRepositoryBase()==null) || 
             (this.repositoryBase!=null &&
              this.repositoryBase.equals(other.getRepositoryBase()))) &&
            ((this.folderName==null && other.getFolderName()==null) || 
             (this.folderName!=null &&
              this.folderName.equals(other.getFolderName()))) &&
            ((this.filePathName==null && other.getFilePathName()==null) || 
             (this.filePathName!=null &&
              this.filePathName.equals(other.getFilePathName()))) &&
            ((this.signatureFilesPathPattern==null && other.getSignatureFilesPathPattern()==null) || 
             (this.signatureFilesPathPattern!=null &&
              this.signatureFilesPathPattern.equals(other.getSignatureFilesPathPattern()))) &&
            ((this.visualFilePathName==null && other.getVisualFilePathName()==null) || 
             (this.visualFilePathName!=null &&
              this.visualFilePathName.equals(other.getVisualFilePathName()))) &&
            ((this.retentionPolicy==null && other.getRetentionPolicy()==null) || 
             (this.retentionPolicy!=null &&
              this.retentionPolicy.equals(other.getRetentionPolicy()))) &&
            ((this.signatureCustody==null && other.getSignatureCustody()==null) || 
             (this.signatureCustody!=null &&
              this.signatureCustody.equals(other.getSignatureCustody())));
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
        if (getRepositoryId() != null) {
            _hashCode += getRepositoryId().hashCode();
        }
        if (getArchiveUri() != null) {
            _hashCode += getArchiveUri().hashCode();
        }
        if (getArchiveVersion() != null) {
            _hashCode += getArchiveVersion().hashCode();
        }
        if (getRepositoryBase() != null) {
            _hashCode += getRepositoryBase().hashCode();
        }
        if (getFolderName() != null) {
            _hashCode += getFolderName().hashCode();
        }
        if (getFilePathName() != null) {
            _hashCode += getFilePathName().hashCode();
        }
        if (getSignatureFilesPathPattern() != null) {
            _hashCode += getSignatureFilesPathPattern().hashCode();
        }
        if (getVisualFilePathName() != null) {
            _hashCode += getVisualFilePathName().hashCode();
        }
        if (getRetentionPolicy() != null) {
            _hashCode += getRetentionPolicy().hashCode();
        }
        if (getSignatureCustody() != null) {
            _hashCode += getSignatureCustody().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ArchiveLocator.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ArchiveLocator"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repositoryId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "repository-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("archiveUri");
        elemField.setXmlName(new javax.xml.namespace.QName("", "archive-uri"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("archiveVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("", "archive-version"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repositoryBase");
        elemField.setXmlName(new javax.xml.namespace.QName("", "repository-base"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("folderName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "folder-name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("filePathName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "file-path-name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signatureFilesPathPattern");
        elemField.setXmlName(new javax.xml.namespace.QName("", "signature-files-path-pattern"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("visualFilePathName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "visual-file-path-name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("retentionPolicy");
        elemField.setXmlName(new javax.xml.namespace.QName("", "retention-policy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signatureCustody");
        elemField.setXmlName(new javax.xml.namespace.QName("", "signature-custody"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
