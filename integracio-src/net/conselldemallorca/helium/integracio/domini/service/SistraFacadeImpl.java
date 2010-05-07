/**
 * 
 */
package net.conselldemallorca.helium.integracio.domini.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import net.conselldemallorca.helium.integracio.domini.FilaResultat;
import net.conselldemallorca.helium.integracio.domini.ParellaCodiValor;
import net.conselldemallorca.helium.integracio.domini.service.wsdl.Columna;
import net.conselldemallorca.helium.integracio.domini.service.wsdl.DocumentosConsulta;
import net.conselldemallorca.helium.integracio.domini.service.wsdl.Fila;
import net.conselldemallorca.helium.integracio.domini.service.wsdl.Filas;
import net.conselldemallorca.helium.integracio.domini.service.wsdl.FormulariosConsulta;
import net.conselldemallorca.helium.integracio.domini.service.wsdl.ParametrosDominio;
import net.conselldemallorca.helium.integracio.domini.service.wsdl.SistraFacade;
import net.conselldemallorca.helium.integracio.domini.service.wsdl.SistraFacadeException_Exception;
import net.conselldemallorca.helium.integracio.domini.service.wsdl.ValoresDominio;
import net.conselldemallorca.helium.model.hibernate.Domini;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.EntornService;

/**
 * Implementació del web service que permet a SISTRA accedir als dominis
 * i fer consultes
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@WebService(endpointInterface = "net.conselldemallorca.helium.integracio.domini.service.wsdl.SistraFacade")
public class SistraFacadeImpl implements SistraFacade {

	private EntornService entornService;
	private DissenyService dissenyService;



	public ValoresDominio obtenerDominio(String id, ParametrosDominio parametros) throws SistraFacadeException_Exception {
		ValoresDominio resposta = new ValoresDominio();
		String[] parts = id.split(".");
		if (parts.length != 2)
			throw new SistraFacadeException_Exception("El format del id del domini no és correcte");
		Entorn entorn = entornService.findAmbCodi(parts[0]);
		if (entorn == null)
			throw new SistraFacadeException_Exception("No s'ha trobat cap domini amb aquest id");
		Domini domini = dissenyService.findDominiAmbEntornICodi(entorn.getId(), parts[1]);
		if (domini == null)
			throw new SistraFacadeException_Exception("No s'ha trobat cap domini amb aquest id");
		resposta.setFilas(getResultatConsulta(domini, parametros));
		return resposta;
	}

	public DocumentosConsulta realizarConsulta(FormulariosConsulta forms) throws SistraFacadeException_Exception {
		return null;
	}



	private Filas getResultatConsulta(Domini domini, ParametrosDominio parametros) throws SistraFacadeException_Exception {
		List<FilaResultat> resultatConsulta = dissenyService.consultaDomini(
				domini.getId(),
				parseSistraParams(domini, parametros));
		Filas filas = new Filas();
		for (FilaResultat fr: resultatConsulta) {
			Fila fila = new Fila();
			for (ParellaCodiValor pcv: fr.getColumnes()) {
				Columna columna = new Columna();
				columna.setCodigo(pcv.getCodi());
				columna.setValor(pcv.getValor().toString());
				fila.getColumna().add(columna);
			}
			filas.getFila().add(fila);
		}
		return filas;
	}

	private Map<String, Object> parseSistraParams(Domini domini, ParametrosDominio parametros) throws SistraFacadeException_Exception {
		if (parametros == null)
			return null;
		Map<String, Object> resposta = new HashMap<String, Object>();
		if (domini.getOrdreParams() != null) {
			String[] parts = domini.getOrdreParams().split(",");
			if (parametros.getParametro().size() != parts.length)
				throw new SistraFacadeException_Exception("Nombre de paràmetres incorrecte");
			for (int i = 0; i < parts.length; i++) {
				resposta.put(parts[i], parametros.getParametro().get(i));
			}
		} else {
			throw new SistraFacadeException_Exception("No s'ha pogut obtenir l'ordre dels paràmetres");
		}
		return resposta;
	}

}
