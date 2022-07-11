package net.conselldemallorca.helium.jbpm3.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;


import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesEnviament;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesNotificacio.Idioma;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.PersonaInfo;


/**
 * Handler de consulta específica Pinbal al servei SVDDGPVIWS02 de VERIFICACIÓ de dades d'identitat. 
 * Conté dades específiques de data, número, província o país.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings({"serial", "unused"})
public class PinbalSvddgpviws02Handler extends net.conselldemallorca.helium.jbpm3.handlers.PinbalConsultaGenericaHandler {

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {		
		
	}
	
	
}