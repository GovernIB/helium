/**
 * MCGDwsSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.ws.callback;

@SuppressWarnings(value = {"serial", "unchecked"})
public class MCGDwsSkeleton implements es.indra.www.portafirmasmcgdws.mcgdws.MCGDws, org.apache.axis.wsdl.Skeleton {
    private es.indra.www.portafirmasmcgdws.mcgdws.MCGDws impl;
    
	private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        @SuppressWarnings("unused")
		org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "callback-request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", ">callback-request"), es.indra.www.portafirmasmcgdws.mcgdws.CallbackRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("callback", _params, new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "callback-response"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasmcgdws/mcgdws", ">callback-response"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "Callback"));
        _oper.setSoapAction("Callback");
        _myOperationsList.add(_oper);
        if (_myOperations.get("callback") == null) {
            _myOperations.put("callback", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("callback")).add(_oper);
    }

    public MCGDwsSkeleton() {
        this.impl = new MCGDwsImpl();
    }

    public MCGDwsSkeleton(es.indra.www.portafirmasmcgdws.mcgdws.MCGDws impl) {
        this.impl = impl;
    }
    public es.indra.www.portafirmasmcgdws.mcgdws.CallbackResponse callback(es.indra.www.portafirmasmcgdws.mcgdws.CallbackRequest callbackRequest) throws java.rmi.RemoteException
    {
        es.indra.www.portafirmasmcgdws.mcgdws.CallbackResponse ret = impl.callback(callbackRequest);
        return ret;
    }

}
