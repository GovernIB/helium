/**
 * Signer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente;

public class Signer  implements java.io.Serializable {
    private java.lang.String id;

    private java.lang.String name;

    private java.lang.String email;

    private java.lang.Boolean checkCert;

    private java.util.Calendar date;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Delegate[] delegates;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Substitute[] substitutes;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Job job;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Certificate certificate;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Rejection rejection;

    private int[] signatureFiles;

    private java.lang.String idUpdate;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.PdfAppearance pdfAppearance;

    public Signer() {
    }

    public Signer(
           java.lang.String id,
           java.lang.String name,
           java.lang.String email,
           java.lang.Boolean checkCert,
           java.util.Calendar date,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Delegate[] delegates,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Substitute[] substitutes,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Job job,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Certificate certificate,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Rejection rejection,
           int[] signatureFiles,
           java.lang.String idUpdate,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.PdfAppearance pdfAppearance) {
           this.id = id;
           this.name = name;
           this.email = email;
           this.checkCert = checkCert;
           this.date = date;
           this.delegates = delegates;
           this.substitutes = substitutes;
           this.job = job;
           this.certificate = certificate;
           this.rejection = rejection;
           this.signatureFiles = signatureFiles;
           this.idUpdate = idUpdate;
           this.pdfAppearance = pdfAppearance;
    }


    /**
     * Gets the id value for this Signer.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this Signer.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the name value for this Signer.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this Signer.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the email value for this Signer.
     * 
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this Signer.
     * 
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }


    /**
     * Gets the checkCert value for this Signer.
     * 
     * @return checkCert
     */
    public java.lang.Boolean getCheckCert() {
        return checkCert;
    }


    /**
     * Sets the checkCert value for this Signer.
     * 
     * @param checkCert
     */
    public void setCheckCert(java.lang.Boolean checkCert) {
        this.checkCert = checkCert;
    }


    /**
     * Gets the date value for this Signer.
     * 
     * @return date
     */
    public java.util.Calendar getDate() {
        return date;
    }


    /**
     * Sets the date value for this Signer.
     * 
     * @param date
     */
    public void setDate(java.util.Calendar date) {
        this.date = date;
    }


    /**
     * Gets the delegates value for this Signer.
     * 
     * @return delegates
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Delegate[] getDelegates() {
        return delegates;
    }


    /**
     * Sets the delegates value for this Signer.
     * 
     * @param delegates
     */
    public void setDelegates(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Delegate[] delegates) {
        this.delegates = delegates;
    }


    /**
     * Gets the substitutes value for this Signer.
     * 
     * @return substitutes
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Substitute[] getSubstitutes() {
        return substitutes;
    }


    /**
     * Sets the substitutes value for this Signer.
     * 
     * @param substitutes
     */
    public void setSubstitutes(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Substitute[] substitutes) {
        this.substitutes = substitutes;
    }


    /**
     * Gets the job value for this Signer.
     * 
     * @return job
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Job getJob() {
        return job;
    }


    /**
     * Sets the job value for this Signer.
     * 
     * @param job
     */
    public void setJob(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Job job) {
        this.job = job;
    }


    /**
     * Gets the certificate value for this Signer.
     * 
     * @return certificate
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Certificate getCertificate() {
        return certificate;
    }


    /**
     * Sets the certificate value for this Signer.
     * 
     * @param certificate
     */
    public void setCertificate(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Certificate certificate) {
        this.certificate = certificate;
    }


    /**
     * Gets the rejection value for this Signer.
     * 
     * @return rejection
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Rejection getRejection() {
        return rejection;
    }


    /**
     * Sets the rejection value for this Signer.
     * 
     * @param rejection
     */
    public void setRejection(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Rejection rejection) {
        this.rejection = rejection;
    }


    /**
     * Gets the signatureFiles value for this Signer.
     * 
     * @return signatureFiles
     */
    public int[] getSignatureFiles() {
        return signatureFiles;
    }


    /**
     * Sets the signatureFiles value for this Signer.
     * 
     * @param signatureFiles
     */
    public void setSignatureFiles(int[] signatureFiles) {
        this.signatureFiles = signatureFiles;
    }


    /**
     * Gets the idUpdate value for this Signer.
     * 
     * @return idUpdate
     */
    public java.lang.String getIdUpdate() {
        return idUpdate;
    }


    /**
     * Sets the idUpdate value for this Signer.
     * 
     * @param idUpdate
     */
    public void setIdUpdate(java.lang.String idUpdate) {
        this.idUpdate = idUpdate;
    }


    /**
     * Gets the pdfAppearance value for this Signer.
     * 
     * @return pdfAppearance
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.PdfAppearance getPdfAppearance() {
        return pdfAppearance;
    }


    /**
     * Sets the pdfAppearance value for this Signer.
     * 
     * @param pdfAppearance
     */
    public void setPdfAppearance(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.PdfAppearance pdfAppearance) {
        this.pdfAppearance = pdfAppearance;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Signer)) return false;
        Signer other = (Signer) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.email==null && other.getEmail()==null) || 
             (this.email!=null &&
              this.email.equals(other.getEmail()))) &&
            ((this.checkCert==null && other.getCheckCert()==null) || 
             (this.checkCert!=null &&
              this.checkCert.equals(other.getCheckCert()))) &&
            ((this.date==null && other.getDate()==null) || 
             (this.date!=null &&
              this.date.equals(other.getDate()))) &&
            ((this.delegates==null && other.getDelegates()==null) || 
             (this.delegates!=null &&
              java.util.Arrays.equals(this.delegates, other.getDelegates()))) &&
            ((this.substitutes==null && other.getSubstitutes()==null) || 
             (this.substitutes!=null &&
              java.util.Arrays.equals(this.substitutes, other.getSubstitutes()))) &&
            ((this.job==null && other.getJob()==null) || 
             (this.job!=null &&
              this.job.equals(other.getJob()))) &&
            ((this.certificate==null && other.getCertificate()==null) || 
             (this.certificate!=null &&
              this.certificate.equals(other.getCertificate()))) &&
            ((this.rejection==null && other.getRejection()==null) || 
             (this.rejection!=null &&
              this.rejection.equals(other.getRejection()))) &&
            ((this.signatureFiles==null && other.getSignatureFiles()==null) || 
             (this.signatureFiles!=null &&
              java.util.Arrays.equals(this.signatureFiles, other.getSignatureFiles()))) &&
            ((this.idUpdate==null && other.getIdUpdate()==null) || 
             (this.idUpdate!=null &&
              this.idUpdate.equals(other.getIdUpdate()))) &&
            ((this.pdfAppearance==null && other.getPdfAppearance()==null) || 
             (this.pdfAppearance!=null &&
              this.pdfAppearance.equals(other.getPdfAppearance())));
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
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        if (getCheckCert() != null) {
            _hashCode += getCheckCert().hashCode();
        }
        if (getDate() != null) {
            _hashCode += getDate().hashCode();
        }
        if (getDelegates() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDelegates());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDelegates(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSubstitutes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSubstitutes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSubstitutes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getJob() != null) {
            _hashCode += getJob().hashCode();
        }
        if (getCertificate() != null) {
            _hashCode += getCertificate().hashCode();
        }
        if (getRejection() != null) {
            _hashCode += getRejection().hashCode();
        }
        if (getSignatureFiles() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSignatureFiles());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSignatureFiles(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getIdUpdate() != null) {
            _hashCode += getIdUpdate().hashCode();
        }
        if (getPdfAppearance() != null) {
            _hashCode += getPdfAppearance().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Signer.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Signer"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("", "email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkCert");
        elemField.setXmlName(new javax.xml.namespace.QName("", "check-cert"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("date");
        elemField.setXmlName(new javax.xml.namespace.QName("", "date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("delegates");
        elemField.setXmlName(new javax.xml.namespace.QName("", "delegates"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Delegate"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "delegate"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("substitutes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "substitutes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Substitute"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "substitute"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("job");
        elemField.setXmlName(new javax.xml.namespace.QName("", "job"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Job"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("certificate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "certificate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Certificate"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rejection");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rejection"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Rejection"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signatureFiles");
        elemField.setXmlName(new javax.xml.namespace.QName("", "signature-files"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("", "file"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idUpdate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id-update"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pdfAppearance");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pdf-appearance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "PdfAppearance"));
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
