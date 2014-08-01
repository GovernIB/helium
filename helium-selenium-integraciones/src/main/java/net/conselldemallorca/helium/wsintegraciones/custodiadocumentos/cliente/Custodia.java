/**
 * Custodia.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.wsintegraciones.custodiadocumentos.cliente;

public interface Custodia extends java.rmi.Remote {
    public byte[] verificarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public byte[] obtenerInformeDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public byte[] consultarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public byte[] custodiarPDFFirmado(java.lang.String in0) throws java.rmi.RemoteException;
    public byte[] custodiarPDFFirmado_v2(byte[] in0) throws java.rmi.RemoteException;
    public byte[] custodiarDocumentoSMIME(java.lang.String in0) throws java.rmi.RemoteException;
    public byte[] custodiarDocumentoSMIME_v2(byte[] in0) throws java.rmi.RemoteException;
    public byte[] custodiarDocumento(java.lang.String in0) throws java.rmi.RemoteException;
    public byte[] custodiarDocumento_v2(byte[] in0) throws java.rmi.RemoteException;
    public byte[] custodiarDocumentoXAdES(byte[] in0) throws java.rmi.RemoteException;
    public byte[] purgarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public byte[] purgarDocumento_v2(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public byte[] recuperarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public byte[] recuperarDocumento_v2(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public byte[] eliminarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public byte[] eliminarDocumento_v2(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public byte[] verificarDocumento_v2(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public byte[] obtenerInformeDocumento_v2(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public byte[] consultarDocumento_v2(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public byte[] reservarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public byte[] reservarDocumento_v2(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
    public byte[] consultarReservaDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException;
}
