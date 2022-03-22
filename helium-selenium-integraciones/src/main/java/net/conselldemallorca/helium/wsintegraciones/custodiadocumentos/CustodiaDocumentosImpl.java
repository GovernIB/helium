
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package net.conselldemallorca.helium.wsintegraciones.custodiadocumentos;

import java.util.logging.Logger;

/**
 * This class was generated by Apache CXF 3.0.0-milestone2
 * 2014-05-27T16:12:27.044+02:00
 * Generated source version: 3.0.0-milestone2
 * 
 */

@javax.jws.WebService(
                      serviceName = "CustodiaService",
                      portName = "CustodiaDocumentos",
                      targetNamespace = "https://proves.caib.es/signatura/services/CustodiaDocumentos",
                      endpointInterface = "net.conselldemallorca.helium.wsintegraciones.custodiadocumentos.Custodia")
                      
public class CustodiaDocumentosImpl implements Custodia {

    private static final Logger LOG = Logger.getLogger(CustodiaDocumentosImpl.class.getName());

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#obtenerInformeDocumentoV2(java.lang.String  in0 ,)java.lang.String  in1 ,)java.lang.String  in2 )*
     */
    public byte[] obtenerInformeDocumentoV2(java.lang.String in0,java.lang.String in1,java.lang.String in2) { 
        LOG.info("Executing operation obtenerInformeDocumentoV2");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#verificarDocumentoV2(java.lang.String  in0 ,)java.lang.String  in1 ,)java.lang.String  in2 )*
     */
    public byte[] verificarDocumentoV2(java.lang.String in0,java.lang.String in1,java.lang.String in2) { 
        LOG.info("Executing operation verificarDocumentoV2");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#verificarDocumento(java.lang.String  in0 ,)java.lang.String  in1 ,)java.lang.String  in2 )*
     */
    public byte[] verificarDocumento(java.lang.String in0,java.lang.String in1,java.lang.String in2) { 
        LOG.info("Executing operation verificarDocumento");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#custodiarDocumentoV2(byte[]  in0 )*
     */
    public byte[] custodiarDocumentoV2(byte[] in0) { 
        LOG.info("Executing operation custodiarDocumentoV2");
        try {
        	byte[] _return = "<CustodiaResponse><VerifyResponse><Result><ResultMajor>2</ResultMajor><ResultMinor>1</ResultMinor><ResultMessage>Mensaje</ResultMessage></Result></VerifyResponse></CustodiaResponse>".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#purgarDocumento(java.lang.String  in0 ,)java.lang.String  in1 ,)java.lang.String  in2 )*
     */
    public byte[] purgarDocumento(java.lang.String in0,java.lang.String in1,java.lang.String in2) { 
        LOG.info("Executing operation purgarDocumento");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#reservarDocumento(java.lang.String  in0 ,)java.lang.String  in1 ,)java.lang.String  in2 )*
     */
    public byte[] reservarDocumento(java.lang.String in0,java.lang.String in1,java.lang.String in2) { 
        LOG.info("Executing operation reservarDocumento");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#obtenerInformeDocumento(java.lang.String  in0 ,)java.lang.String  in1 ,)java.lang.String  in2 )*
     */
    public byte[] obtenerInformeDocumento(java.lang.String in0,java.lang.String in1,java.lang.String in2) { 
        LOG.info("Executing operation obtenerInformeDocumento");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#custodiarDocumentoXAdES(byte[]  in0 )*
     */
    public byte[] custodiarDocumentoXAdES(byte[] in0) { 
        LOG.info("Executing operation custodiarDocumentoXAdES");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#reservarDocumentoV2(java.lang.String  in0 ,)java.lang.String  in1 ,)java.lang.String  in2 )*
     */
    public byte[] reservarDocumentoV2(java.lang.String in0,java.lang.String in1,java.lang.String in2) { 
        LOG.info("Executing operation reservarDocumentoV2");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#eliminarDocumentoV2(java.lang.String  in0 ,)java.lang.String  in1 ,)java.lang.String  in2 )*
     */
    public byte[] eliminarDocumentoV2(java.lang.String in0,java.lang.String in1,java.lang.String in2) { 
        LOG.info("Executing operation eliminarDocumentoV2");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#custodiarDocumentoSMIMEV2(byte[]  in0 )*
     */
    public byte[] custodiarDocumentoSMIMEV2(byte[] in0) { 
        LOG.info("Executing operation custodiarDocumentoSMIMEV2");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#recuperarDocumento(java.lang.String  in0 ,)java.lang.String  in1 ,)java.lang.String  in2 )*
     */
    public byte[] recuperarDocumento(java.lang.String in0,java.lang.String in1,java.lang.String in2) { 
        LOG.info("Executing operation recuperarDocumento");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#recuperarDocumentoV2(java.lang.String  in0 ,)java.lang.String  in1 ,)java.lang.String  in2 )*
     */
    public byte[] recuperarDocumentoV2(java.lang.String in0,java.lang.String in1,java.lang.String in2) { 
        LOG.info("Executing operation recuperarDocumentoV2");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#eliminarDocumento(java.lang.String  in0 ,)java.lang.String  in1 ,)java.lang.String  in2 )*
     */
    public byte[] eliminarDocumento(java.lang.String in0,java.lang.String in1,java.lang.String in2) { 
        LOG.info("Executing operation eliminarDocumento");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#custodiarDocumento(java.lang.String  in0 )*
     */
    public byte[] custodiarDocumento(java.lang.String in0) { 
        LOG.info("Executing operation custodiarDocumento");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#purgarDocumentoV2(java.lang.String  in0 ,)java.lang.String  in1 ,)java.lang.String  in2 )*
     */
    public byte[] purgarDocumentoV2(java.lang.String in0,java.lang.String in1,java.lang.String in2) { 
        LOG.info("Executing operation purgarDocumentoV2");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#consultarReservaDocumento(java.lang.String  in0 ,)java.lang.String  in1 ,)java.lang.String  in2 )*
     */
    public byte[] consultarReservaDocumento(java.lang.String in0,java.lang.String in1,java.lang.String in2) { 
        LOG.info("Executing operation consultarReservaDocumento");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#custodiarDocumentoSMIME(java.lang.String  in0 )*
     */
    public byte[] custodiarDocumentoSMIME(java.lang.String in0) { 
        LOG.info("Executing operation custodiarDocumentoSMIME");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#custodiarPDFFirmadoV2(byte[]  in0 )*
     */
    public byte[] custodiarPDFFirmadoV2(byte[] in0) { 
        LOG.info("Executing operation custodiarPDFFirmadoV2");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#consultarDocumentoV2(java.lang.String  in0 ,)java.lang.String  in1 ,)java.lang.String  in2 )*
     */
    public byte[] consultarDocumentoV2(java.lang.String in0,java.lang.String in1,java.lang.String in2) { 
        LOG.info("Executing operation consultarDocumentoV2");
        try {
        	byte[] _return = "<ConsultaResponse><Result><ResultMajor>2</ResultMajor><ResultMinor>1</ResultMinor><ResultMessage>Mensaje</ResultMessage></Result></ConsultaResponse>".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#custodiarPDFFirmado(java.lang.String  in0 )*
     */
    public byte[] custodiarPDFFirmado(java.lang.String in0) { 
        LOG.info("Executing operation custodiarPDFFirmado");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#consultarDocumento(java.lang.String  in0 ,)java.lang.String  in1 ,)java.lang.String  in2 )*
     */
    public byte[] consultarDocumento(java.lang.String in0,java.lang.String in1,java.lang.String in2) { 
        LOG.info("Executing operation consultarDocumento");
        try {
            byte[] _return = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y3VzOkN1c3RvZGlhUmVzcG9uc2UgeG1sbnM6Y3VzPSJodHRwOi8vd3d3LmNhaWIuZXMuc2lnbmF0dXJhLmN1c3RvZGlhIj48ZHNzOlZlcmlmeVJlc3BvbnNlIHhtbG5zOmRzcz0idXJuOm9hc2lzOm5hbWVzOnRjOmRzczoxLjA6Y29yZTpzY2hlbWEiIFByb2ZpbGU9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOnByb2ZpbGVzOkNBZEVTIj48ZHNzOlJlc3VsdCB4bWxuczpkc3M9InVybjpvYXNpczpuYW1lczp0Yzpkc3M6MS4wOmNvcmU6c2NoZW1hIj48ZHNzOlJlc3VsdE1ham9yPlJlcXVlc3RlckVycm9yPC9kc3M6UmVzdWx0TWFqb3I+PGRzczpSZXN1bHRNaW5vcj5FUlJPUl9QQVJTRVJfWE1MPC9kc3M6UmVzdWx0TWlub3I+PGRzczpSZXN1bHRNZXNzYWdlIHhtbDpsYW5nPSJlcyI+Q29udGVudCBpcyBub3QgYWxsb3dlZCBpbiBwcm9sb2cuPC9kc3M6UmVzdWx0TWVzc2FnZT48L2RzczpSZXN1bHQ+PGRzczpPcHRpb25hbE91dHB1dHMvPjwvZHNzOlZlcmlmeVJlc3BvbnNlPjwvY3VzOkN1c3RvZGlhUmVzcG9uc2U+".getBytes();
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}