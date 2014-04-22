
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package net.conselldemallorca.helium.integracio.tramitacio;

import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.0.0-milestone2
 * 2014-04-22T09:15:26.260+02:00
 * Generated source version: 3.0.0-milestone2
 * 
 */

@javax.jws.WebService(
                      serviceName = "TramitacioService",
                      portName = "TramitacioPort",
                      targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/",
                      wsdlLocation = "file:/C:/Users/javierg/workspace_helium_30/pru/WebContent/TramitacioService.wsdl",
                      endpointInterface = "net.conselldemallorca.helium.integracio.tramitacio.TramitacioService")
                      
public class TramitacioPortImpl implements TramitacioService {

    private static final Logger LOG = Logger.getLogger(TramitacioPortImpl.class.getName());

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#executarAccioProces(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 ,)java.lang.String  arg3 )*
     */
    public void executarAccioProces(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2,java.lang.String arg3) throws TramitacioException_Exception    { 
        LOG.info("Executing operation executarAccioProces");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        System.out.println(arg3);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#esborrarDocumentTasca(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 ,)java.lang.String  arg3 )*
     */
    public void esborrarDocumentTasca(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2,java.lang.String arg3) throws TramitacioException_Exception    { 
        LOG.info("Executing operation esborrarDocumentTasca");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        System.out.println(arg3);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#consultaFormulariTasca(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 )*
     */
    public java.util.List<net.conselldemallorca.helium.integracio.tramitacio.CampTasca> consultaFormulariTasca(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2) throws TramitacioException_Exception    { 
        LOG.info("Executing operation consultaFormulariTasca");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        try {
            java.util.List<net.conselldemallorca.helium.integracio.tramitacio.CampTasca> _return = new java.util.ArrayList<net.conselldemallorca.helium.integracio.tramitacio.CampTasca>();
            net.conselldemallorca.helium.integracio.tramitacio.CampTasca _returnVal1 = new net.conselldemallorca.helium.integracio.tramitacio.CampTasca();
            _returnVal1.setCodi("Codi1576418544");
            _returnVal1.setDominiCampText("DominiCampText-983937645");
            _returnVal1.setDominiCampValor("DominiCampValor-1624941529");
            _returnVal1.setDominiId("DominiId-1710395866");
            _returnVal1.setDominiParams("DominiParams-45574532");
            _returnVal1.setEtiqueta("Etiqueta137064324");
            _returnVal1.setJbpmAction("JbpmAction44611193");
            _returnVal1.setMultiple(true);
            _returnVal1.setObservacions("Observacions1193699814");
            _returnVal1.setOcult(false);
            _returnVal1.setReadFrom(false);
            _returnVal1.setReadOnly(false);
            _returnVal1.setRequired(false);
            _returnVal1.setTipus("Tipus-1299184001");
            java.lang.Object _returnVal1Valor = null;
            _returnVal1.setValor(_returnVal1Valor);
            _returnVal1.setWriteTo(true);
            _return.add(_returnVal1);
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#consultarVariablesProces(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 )*
     */
    public java.util.List<net.conselldemallorca.helium.integracio.tramitacio.CampProces> consultarVariablesProces(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2) throws TramitacioException_Exception    { 
        LOG.info("Executing operation consultarVariablesProces");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        try {
            java.util.List<net.conselldemallorca.helium.integracio.tramitacio.CampProces> _return = new java.util.ArrayList<net.conselldemallorca.helium.integracio.tramitacio.CampProces>();
            net.conselldemallorca.helium.integracio.tramitacio.CampProces _returnVal1 = new net.conselldemallorca.helium.integracio.tramitacio.CampProces();
            _returnVal1.setCodi("Codi240213844");
            _returnVal1.setDominiCampText("DominiCampText186278628");
            _returnVal1.setDominiCampValor("DominiCampValor855589299");
            _returnVal1.setDominiId("DominiId-437824414");
            _returnVal1.setDominiParams("DominiParams1973113995");
            _returnVal1.setEtiqueta("Etiqueta-2091728908");
            _returnVal1.setJbpmAction("JbpmAction-57812549");
            _returnVal1.setMultiple(true);
            _returnVal1.setObservacions("Observacions-1082689223");
            _returnVal1.setOcult(true);
            _returnVal1.setTipus("Tipus1709779710");
            java.lang.Object _returnVal1Valor = null;
            _returnVal1.setValor(_returnVal1Valor);
            _return.add(_returnVal1);
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#setVariableProces(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 ,)java.lang.String  arg3 ,)java.lang.Object  arg4 )*
     */
    public void setVariableProces(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2,java.lang.String arg3,java.lang.Object arg4) throws TramitacioException_Exception    { 
        LOG.info("Executing operation setVariableProces");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        System.out.println(arg3);
        System.out.println(arg4);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#agafarTasca(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 )*
     */
    public void agafarTasca(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2) throws TramitacioException_Exception    { 
        LOG.info("Executing operation agafarTasca");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#consultaExpedients(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 ,)java.lang.String  arg3 ,)javax.xml.datatype.XMLGregorianCalendar  arg4 ,)javax.xml.datatype.XMLGregorianCalendar  arg5 ,)java.lang.String  arg6 ,)java.lang.String  arg7 ,)boolean  arg8 ,)boolean  arg9 ,)java.lang.Double  arg10 ,)java.lang.Double  arg11 ,)java.lang.String  arg12 )*
     */
    public java.util.List<net.conselldemallorca.helium.integracio.tramitacio.ExpedientInfo> consultaExpedients(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2,java.lang.String arg3,javax.xml.datatype.XMLGregorianCalendar arg4,javax.xml.datatype.XMLGregorianCalendar arg5,java.lang.String arg6,java.lang.String arg7,boolean arg8,boolean arg9,java.lang.Double arg10,java.lang.Double arg11,java.lang.String arg12) throws TramitacioException_Exception    { 
        LOG.info("Executing operation consultaExpedients");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        System.out.println(arg3);
        System.out.println(arg4);
        System.out.println(arg5);
        System.out.println(arg6);
        System.out.println(arg7);
        System.out.println(arg8);
        System.out.println(arg9);
        System.out.println(arg10);
        System.out.println(arg11);
        System.out.println(arg12);
        try {
            java.util.List<net.conselldemallorca.helium.integracio.tramitacio.ExpedientInfo> _return = new java.util.ArrayList<net.conselldemallorca.helium.integracio.tramitacio.ExpedientInfo>();
            net.conselldemallorca.helium.integracio.tramitacio.ExpedientInfo _returnVal1 = new net.conselldemallorca.helium.integracio.tramitacio.ExpedientInfo();
            _returnVal1.setAutenticat(false);
            _returnVal1.setAvisosEmail("AvisosEmail-871217128");
            _returnVal1.setAvisosHabilitats(true);
            _returnVal1.setAvisosMobil("AvisosMobil-1169406583");
            _returnVal1.setComentari("Comentari-1646938930");
            _returnVal1.setDataFi(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.300+02:00"));
            _returnVal1.setDataInici(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.301+02:00"));
            _returnVal1.setEntornCodi("EntornCodi-996982805");
            _returnVal1.setEstatCodi("EstatCodi-1955675561");
            _returnVal1.setExpedientTipusCodi("ExpedientTipusCodi598170859");
            _returnVal1.setGeoPosX(Double.valueOf(0.5317897343285694));
            _returnVal1.setGeoPosY(Double.valueOf(0.43563929298848536));
            _returnVal1.setGeoReferencia("GeoReferencia348265867");
            _returnVal1.setIdioma("Idioma728058143");
            _returnVal1.setInfoAturat("InfoAturat2139601763");
            _returnVal1.setIniciadorCodi("IniciadorCodi-1413726928");
            net.conselldemallorca.helium.integracio.tramitacio.IniciadorTipus _returnVal1IniciadorTipus = net.conselldemallorca.helium.integracio.tramitacio.IniciadorTipus.SISTRA;
            _returnVal1.setIniciadorTipus(_returnVal1IniciadorTipus);
            _returnVal1.setInteressatNif("InteressatNif-1327532502");
            _returnVal1.setInteressatNom("InteressatNom-1376331809");
            _returnVal1.setNotificacioTelematicaHabilitada(false);
            _returnVal1.setNumero("Numero506319174");
            _returnVal1.setNumeroDefault("NumeroDefault981385674");
            _returnVal1.setProcessInstanceId(7264951200940876907l);
            _returnVal1.setRegistreData(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.304+02:00"));
            _returnVal1.setRegistreNumero("RegistreNumero-1484589073");
            _returnVal1.setRepresentantNif("RepresentantNif-1998337091");
            _returnVal1.setRepresentantNom("RepresentantNom102560349");
            _returnVal1.setResponsableCodi("ResponsableCodi-727113549");
            _returnVal1.setTitol("Titol-1799101132");
            _returnVal1.setTramitExpedientClau("TramitExpedientClau840667816");
            _returnVal1.setTramitExpedientIdentificador("TramitExpedientIdentificador-634771846");
            _returnVal1.setTramitadorNif("TramitadorNif1487607451");
            _returnVal1.setTramitadorNom("TramitadorNom1831135290");
            _returnVal1.setUnitatAdministrativa(Long.valueOf(6383595268860627649l));
            _return.add(_returnVal1);
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#consultaTasquesPersonalsByCodi(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 )*
     */
    public java.util.List<net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio> consultaTasquesPersonalsByCodi(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2) throws TramitacioException_Exception    { 
        LOG.info("Executing operation consultaTasquesPersonalsByCodi");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        try {
            java.util.List<net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio> _return = new java.util.ArrayList<net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio>();
            net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio _returnVal1 = new net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio();
            _returnVal1.setCancelled(false);
            _returnVal1.setCodi("Codi194200923");
            _returnVal1.setCompleted(false);
            _returnVal1.setDataCreacio(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.308+02:00"));
            _returnVal1.setDataFi(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.309+02:00"));
            _returnVal1.setDataInici(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.320+02:00"));
            _returnVal1.setDataLimit(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.321+02:00"));
            _returnVal1.setExpedient("Expedient-1522374062");
            _returnVal1.setId("Id1166658469");
            _returnVal1.setMissatgeInfo("MissatgeInfo589974587");
            _returnVal1.setMissatgeWarn("MissatgeWarn1886907132");
            _returnVal1.setOpen(true);
            _returnVal1.setPrioritat(-353509834);
            _returnVal1.setProcessInstanceId("ProcessInstanceId-1784988903");
            _returnVal1.setResponsable("Responsable-1908156995");
            java.util.List<java.lang.String> _returnVal1Responsables = new java.util.ArrayList<java.lang.String>();
            java.lang.String _returnVal1ResponsablesVal1 = "_returnVal1ResponsablesVal2022293757";
            _returnVal1Responsables.add(_returnVal1ResponsablesVal1);
            _returnVal1.getResponsables().addAll(_returnVal1Responsables);
            _returnVal1.setSuspended(false);
            _returnVal1.setTitol("Titol-606108825");
            java.util.List<java.lang.String> _returnVal1TransicionsSortida = new java.util.ArrayList<java.lang.String>();
            java.lang.String _returnVal1TransicionsSortidaVal1 = "_returnVal1TransicionsSortidaVal1509825870";
            _returnVal1TransicionsSortida.add(_returnVal1TransicionsSortidaVal1);
            _returnVal1.getTransicionsSortida().addAll(_returnVal1TransicionsSortida);
            _return.add(_returnVal1);
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#consultarDocumentsProces(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 )*
     */
    public java.util.List<net.conselldemallorca.helium.integracio.tramitacio.DocumentProces> consultarDocumentsProces(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2) throws TramitacioException_Exception    { 
        LOG.info("Executing operation consultarDocumentsProces");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        try {
            java.util.List<net.conselldemallorca.helium.integracio.tramitacio.DocumentProces> _return = new java.util.ArrayList<net.conselldemallorca.helium.integracio.tramitacio.DocumentProces>();
            net.conselldemallorca.helium.integracio.tramitacio.DocumentProces _returnVal1 = new net.conselldemallorca.helium.integracio.tramitacio.DocumentProces();
            _returnVal1.setArxiu("Arxiu-1047397601");
            _returnVal1.setCodi("Codi1685781264");
            _returnVal1.setData(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.325+02:00"));
            _returnVal1.setDescripcio("Descripcio-1225636965");
            _returnVal1.setId(Long.valueOf(-2209881212846514365l));
            _returnVal1.setNom("Nom1687667785");
            _return.add(_returnVal1);
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#setDocumentProces(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 ,)java.lang.String  arg3 ,)java.lang.String  arg4 ,)javax.xml.datatype.XMLGregorianCalendar  arg5 ,)byte[]  arg6 )*
     */
    public java.lang.Long setDocumentProces(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2,java.lang.String arg3,java.lang.String arg4,javax.xml.datatype.XMLGregorianCalendar arg5,byte[] arg6) throws TramitacioException_Exception    { 
        LOG.info("Executing operation setDocumentProces");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        System.out.println(arg3);
        System.out.println(arg4);
        System.out.println(arg5);
        System.out.println(arg6);
        try {
            java.lang.Long _return = Long.valueOf(4379480462989187425l);
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#setDadesFormulariTasca(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 ,)java.util.List<net.conselldemallorca.helium.integracio.tramitacio.ParellaCodiValor>  arg3 )*
     */
    public void setDadesFormulariTasca(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2,java.util.List<net.conselldemallorca.helium.integracio.tramitacio.ParellaCodiValor> arg3) throws TramitacioException_Exception    { 
        LOG.info("Executing operation setDadesFormulariTasca");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        System.out.println(arg3);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#finalitzarTasca(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 ,)java.lang.String  arg3 )*
     */
    public void finalitzarTasca(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2,java.lang.String arg3) throws TramitacioException_Exception    { 
        LOG.info("Executing operation finalitzarTasca");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        System.out.println(arg3);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#setDocumentTasca(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 ,)java.lang.String  arg3 ,)java.lang.String  arg4 ,)javax.xml.datatype.XMLGregorianCalendar  arg5 ,)byte[]  arg6 )*
     */
    public void setDocumentTasca(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2,java.lang.String arg3,java.lang.String arg4,javax.xml.datatype.XMLGregorianCalendar arg5,byte[] arg6) throws TramitacioException_Exception    { 
        LOG.info("Executing operation setDocumentTasca");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        System.out.println(arg3);
        System.out.println(arg4);
        System.out.println(arg5);
        System.out.println(arg6);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#consultaTasquesGrupByCodi(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 )*
     */
    public java.util.List<net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio> consultaTasquesGrupByCodi(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2) throws TramitacioException_Exception    { 
        LOG.info("Executing operation consultaTasquesGrupByCodi");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        try {
            java.util.List<net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio> _return = new java.util.ArrayList<net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio>();
            net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio _returnVal1 = new net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio();
            _returnVal1.setCancelled(false);
            _returnVal1.setCodi("Codi900695723");
            _returnVal1.setCompleted(false);
            _returnVal1.setDataCreacio(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.335+02:00"));
            _returnVal1.setDataFi(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.336+02:00"));
            _returnVal1.setDataInici(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.338+02:00"));
            _returnVal1.setDataLimit(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.339+02:00"));
            _returnVal1.setExpedient("Expedient1299930760");
            _returnVal1.setId("Id1020841569");
            _returnVal1.setMissatgeInfo("MissatgeInfo-211310688");
            _returnVal1.setMissatgeWarn("MissatgeWarn558926073");
            _returnVal1.setOpen(false);
            _returnVal1.setPrioritat(-952119619);
            _returnVal1.setProcessInstanceId("ProcessInstanceId1903971449");
            _returnVal1.setResponsable("Responsable1284291539");
            java.util.List<java.lang.String> _returnVal1Responsables = new java.util.ArrayList<java.lang.String>();
            java.lang.String _returnVal1ResponsablesVal1 = "_returnVal1ResponsablesVal-2001137112";
            _returnVal1Responsables.add(_returnVal1ResponsablesVal1);
            _returnVal1.getResponsables().addAll(_returnVal1Responsables);
            _returnVal1.setSuspended(true);
            _returnVal1.setTitol("Titol352603721");
            java.util.List<java.lang.String> _returnVal1TransicionsSortida = new java.util.ArrayList<java.lang.String>();
            java.lang.String _returnVal1TransicionsSortidaVal1 = "_returnVal1TransicionsSortidaVal-554487376";
            _returnVal1TransicionsSortida.add(_returnVal1TransicionsSortidaVal1);
            _returnVal1.getTransicionsSortida().addAll(_returnVal1TransicionsSortida);
            _return.add(_returnVal1);
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#consultaTasquesGrup(java.lang.String  arg0 ,)java.lang.String  arg1 )*
     */
    public java.util.List<net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio> consultaTasquesGrup(java.lang.String arg0,java.lang.String arg1) throws TramitacioException_Exception    { 
        LOG.info("Executing operation consultaTasquesGrup");
        System.out.println(arg0);
        System.out.println(arg1);
        try {
            java.util.List<net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio> _return = new java.util.ArrayList<net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio>();
            net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio _returnVal1 = new net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio();
            _returnVal1.setCancelled(false);
            _returnVal1.setCodi("Codi987453462");
            _returnVal1.setCompleted(false);
            _returnVal1.setDataCreacio(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.343+02:00"));
            _returnVal1.setDataFi(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.345+02:00"));
            _returnVal1.setDataInici(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.346+02:00"));
            _returnVal1.setDataLimit(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.347+02:00"));
            _returnVal1.setExpedient("Expedient-947448949");
            _returnVal1.setId("Id31380850");
            _returnVal1.setMissatgeInfo("MissatgeInfo1801804124");
            _returnVal1.setMissatgeWarn("MissatgeWarn113795869");
            _returnVal1.setOpen(true);
            _returnVal1.setPrioritat(-946841383);
            _returnVal1.setProcessInstanceId("ProcessInstanceId-795171167");
            _returnVal1.setResponsable("Responsable-274872915");
            java.util.List<java.lang.String> _returnVal1Responsables = new java.util.ArrayList<java.lang.String>();
            java.lang.String _returnVal1ResponsablesVal1 = "_returnVal1ResponsablesVal367274767";
            _returnVal1Responsables.add(_returnVal1ResponsablesVal1);
            _returnVal1.getResponsables().addAll(_returnVal1Responsables);
            _returnVal1.setSuspended(true);
            _returnVal1.setTitol("Titol-2030654347");
            java.util.List<java.lang.String> _returnVal1TransicionsSortida = new java.util.ArrayList<java.lang.String>();
            java.lang.String _returnVal1TransicionsSortidaVal1 = "_returnVal1TransicionsSortidaVal-291715114";
            _returnVal1TransicionsSortida.add(_returnVal1TransicionsSortidaVal1);
            _returnVal1.getTransicionsSortida().addAll(_returnVal1TransicionsSortida);
            _return.add(_returnVal1);
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#esborrarDocumentProces(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 ,)java.lang.Long  arg3 )*
     */
    public void esborrarDocumentProces(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2,java.lang.Long arg3) throws TramitacioException_Exception    { 
        LOG.info("Executing operation esborrarDocumentProces");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        System.out.println(arg3);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#consultaDocumentsTasca(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 )*
     */
    public java.util.List<net.conselldemallorca.helium.integracio.tramitacio.DocumentTasca> consultaDocumentsTasca(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2) throws TramitacioException_Exception    { 
        LOG.info("Executing operation consultaDocumentsTasca");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        try {
            java.util.List<net.conselldemallorca.helium.integracio.tramitacio.DocumentTasca> _return = new java.util.ArrayList<net.conselldemallorca.helium.integracio.tramitacio.DocumentTasca>();
            net.conselldemallorca.helium.integracio.tramitacio.DocumentTasca _returnVal1 = new net.conselldemallorca.helium.integracio.tramitacio.DocumentTasca();
            _returnVal1.setArxiu("Arxiu-2115247278");
            _returnVal1.setCodi("Codi-1417979075");
            _returnVal1.setData(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.353+02:00"));
            _returnVal1.setDescripcio("Descripcio1475517666");
            _returnVal1.setId(Long.valueOf(-5920875983960144846l));
            _returnVal1.setNom("Nom-1382428893");
            _return.add(_returnVal1);
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#consultaTasquesPersonals(java.lang.String  arg0 ,)java.lang.String  arg1 )*
     */
    public java.util.List<net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio> consultaTasquesPersonals(java.lang.String arg0,java.lang.String arg1) throws TramitacioException_Exception    { 
        LOG.info("Executing operation consultaTasquesPersonals");
        System.out.println(arg0);
        System.out.println(arg1);
        try {
            java.util.List<net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio> _return = new java.util.ArrayList<net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio>();
            net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio _returnVal1 = new net.conselldemallorca.helium.integracio.tramitacio.TascaTramitacio();
            _returnVal1.setCancelled(true);
            _returnVal1.setCodi("Codi-875885162");
            _returnVal1.setCompleted(true);
            _returnVal1.setDataCreacio(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.356+02:00"));
            _returnVal1.setDataFi(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.357+02:00"));
            _returnVal1.setDataInici(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.358+02:00"));
            _returnVal1.setDataLimit(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-04-22T09:15:26.360+02:00"));
            _returnVal1.setExpedient("Expedient-1203156644");
            _returnVal1.setId("Id98873103");
            _returnVal1.setMissatgeInfo("MissatgeInfo476986891");
            _returnVal1.setMissatgeWarn("MissatgeWarn1236757404");
            _returnVal1.setOpen(true);
            _returnVal1.setPrioritat(969563296);
            _returnVal1.setProcessInstanceId("ProcessInstanceId628522337");
            _returnVal1.setResponsable("Responsable-342085300");
            java.util.List<java.lang.String> _returnVal1Responsables = new java.util.ArrayList<java.lang.String>();
            java.lang.String _returnVal1ResponsablesVal1 = "_returnVal1ResponsablesVal72957735";
            _returnVal1Responsables.add(_returnVal1ResponsablesVal1);
            _returnVal1.getResponsables().addAll(_returnVal1Responsables);
            _returnVal1.setSuspended(false);
            _returnVal1.setTitol("Titol611313911");
            java.util.List<java.lang.String> _returnVal1TransicionsSortida = new java.util.ArrayList<java.lang.String>();
            java.lang.String _returnVal1TransicionsSortidaVal1 = "_returnVal1TransicionsSortidaVal-1225064631";
            _returnVal1TransicionsSortida.add(_returnVal1TransicionsSortidaVal1);
            _returnVal1.getTransicionsSortida().addAll(_returnVal1TransicionsSortida);
            _return.add(_returnVal1);
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#executarScriptProces(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 ,)java.lang.String  arg3 )*
     */
    public void executarScriptProces(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2,java.lang.String arg3) throws TramitacioException_Exception    { 
        LOG.info("Executing operation executarScriptProces");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        System.out.println(arg3);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#iniciExpedient(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 ,)java.lang.String  arg3 ,)java.lang.String  arg4 ,)java.util.List<net.conselldemallorca.helium.integracio.tramitacio.ParellaCodiValor>  arg5 )*
     */
    public java.lang.String iniciExpedient(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2,java.lang.String arg3,java.lang.String arg4,java.util.List<net.conselldemallorca.helium.integracio.tramitacio.ParellaCodiValor> arg5) throws TramitacioException_Exception    { 
        LOG.info("Executing operation iniciExpedient");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        System.out.println(arg3);
        System.out.println(arg4);
        System.out.println(arg5);
        try {
            java.lang.String _return = "_return-243639164";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

    /* (non-Javadoc)
     * @see net.conselldemallorca.helium.integracio.tramitacio.TramitacioService#esborrarVariableProces(java.lang.String  arg0 ,)java.lang.String  arg1 ,)java.lang.String  arg2 ,)java.lang.String  arg3 )*
     */
    public void esborrarVariableProces(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2,java.lang.String arg3) throws TramitacioException_Exception    { 
        LOG.info("Executing operation esborrarVariableProces");
        System.out.println(arg0);
        System.out.println(arg1);
        System.out.println(arg2);
        System.out.println(arg3);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new TramitacioException_Exception("TramitacioException...");
    }

}
