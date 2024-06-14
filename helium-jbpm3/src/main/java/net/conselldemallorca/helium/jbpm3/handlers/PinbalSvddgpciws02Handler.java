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
import org.springframework.security.crypto.codec.Base64;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesConsultaPinbal;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesEnviament;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesNotificacio.Idioma;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Interessat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.PersonaInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Titular;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaNotificacio;

/**
 * Handler de consulta específica Pinbal al servei SVDDGPCIWS02 de CONSULTA de dades d'indentitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings({"serial", "unused"})
public class PinbalSvddgpciws02Handler extends PinbalConsultaGenericaHandler {

	/**En el cas del handler específic PinbalSvddgpciws02Handler per a la CONSULTA de les dades personals ja no
	necessitaria ni el codi del servei ni les dades específiques**/
	
	private static final String serveiCodiEspecific = "SVDDGPCIWS02";
	
	/**Codi de la variable que conté l'any de naixement (és específic d'aquest servei SVDDGPCIWS02 de consulta de dades d'identitat).**/
	protected String varAnyNaixement;
	protected String anyNaixement;
	
	@Override
	public void execute(ExecutionContext executionContext) throws Exception {		
		logger.debug("Inici execució handler de consulta a Pinbal");
		ExpedientDto expedient = getExpedientActual(executionContext);
	
		DadesConsultaPinbal dadesConsultaPinbal = obtenirDadesConsulta(executionContext);
		
		if(dadesConsultaPinbal.getTitular()!=null) {
			dadesConsultaPinbal.getTitular().setNombreCompleto((String)getValorOVariable(
					executionContext,
					this.titularNom,
					this.varTitularNom));
		}
		
		
		//Al ser un handler específic li setegem el codi del servei
		dadesConsultaPinbal.setServeiCodi(serveiCodiEspecific);
		dadesConsultaPinbal.setTransicioOK(varTransicioOK);
		dadesConsultaPinbal.setTransicioKO(varTransicioKO);
		dadesConsultaPinbal.setAnyNaixement(anyNaixement);
		
		Object resposta = consultaPinbalSvddgpciws02(
				dadesConsultaPinbal,
				expedient.getId(),
				expedient.getProcessInstanceId(),
				(executionContext!=null && executionContext.getToken()!=null)?executionContext.getToken().getId():null);

	}
	
	private String formatIdentificadorEni(String identificador) {
		String identificadorEni = String.format("%30s", identificador).replace(' ', '0').toUpperCase();
		if (identificadorEni.length() > 30) {
			return identificadorEni.substring(0, 12) + "..." + identificadorEni.substring(identificadorEni.length() - 13);
		}
		return identificadorEni;
	}
    
	private Object fromString(String s) throws IOException, ClassNotFoundException {
		byte[] data = Base64.decode(s.getBytes());
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return o;
	}

	private String toString(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		return new String(Base64.encode(baos.toByteArray()));
	}

	
}