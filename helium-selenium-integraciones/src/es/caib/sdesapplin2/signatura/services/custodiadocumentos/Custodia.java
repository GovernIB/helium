/**
 * Custodia.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.caib.sdesapplin2.signatura.services.custodiadocumentos;

public interface Custodia extends java.rmi.Remote {
    public byte[] verificarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2);
    public byte[] obtenerInformeDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2);
    public byte[] consultarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2);
    public byte[] custodiarPDFFirmado(java.lang.String in0);
    public byte[] custodiarPDFFirmadoV2(byte[] in0);
    public byte[] custodiarDocumentoSMIME(java.lang.String in0);
    public byte[] custodiarDocumentoSMIMEV2(byte[] in0);
    public byte[] custodiarDocumento(java.lang.String in0);
    public byte[] custodiarDocumentoV2(byte[] in0);
    public byte[] custodiarDocumentoXAdES(byte[] in0);
    public byte[] purgarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2);
    public byte[] purgarDocumentoV2(java.lang.String in0, java.lang.String in1, java.lang.String in2);
    public byte[] recuperarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2);
    public byte[] recuperarDocumentoV2(java.lang.String in0, java.lang.String in1, java.lang.String in2);
    public byte[] eliminarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2);
    public byte[] eliminarDocumentoV2(java.lang.String in0, java.lang.String in1, java.lang.String in2);
    public byte[] verificarDocumentoV2(java.lang.String in0, java.lang.String in1, java.lang.String in2);
    public byte[] obtenerInformeDocumentoV2(java.lang.String in0, java.lang.String in1, java.lang.String in2);
    public byte[] consultarDocumentoV2(java.lang.String in0, java.lang.String in1, java.lang.String in2);
    public byte[] reservarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2);
    public byte[] reservarDocumentoV2(java.lang.String in0, java.lang.String in1, java.lang.String in2);
    public byte[] consultarReservaDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2);
}
