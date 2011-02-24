/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.signatura.afirma.AfirmaUtils;
import net.conselldemallorca.helium.integracio.plugins.signatura.afirma.ValidarSignaturaResponse;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.util.Base64;


/**
 * Implementació del plugin de signatura emprant els
 * serveis de @Firma.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class SignaturaPluginAfirma implements SignaturaPlugin {

	private AfirmaUtils afirmaUtils;



	public RespostaValidacioSignatura verificarSignatura(
			byte[] document,
			byte[] signatura,
			boolean obtenirDadesCertificat) throws SignaturaPluginException {
		try {
			ValidarSignaturaResponse response = getAfirmaUtils().validarSignatura(
					Base64.encode(document),
					Base64.encode(signatura),
					obtenirDadesCertificat);
			RespostaValidacioSignatura resposta = new RespostaValidacioSignatura();
			InfoSignatura infoSignatura = new InfoSignatura(signatura);
			if (response.isEstatOk()) {
				resposta.setEstat(RespostaValidacioSignatura.ESTAT_OK);
				if (response.getDadesCertificat() != null) {
					List<DadesCertificat> dadesCertificats = new ArrayList<DadesCertificat>();
					for (net.conselldemallorca.helium.integracio.plugins.signatura.afirma.DadesCertificat dc: response.getDadesCertificat()) {
						DadesCertificat dadesCertificat = new DadesCertificat();
						dadesCertificat.setTipoCertificado(dc.getTipoCertificado());
						dadesCertificat.setSubject(dc.getSubject());
						dadesCertificat.setNombreResponsable(dc.getNombreResponsable());
						dadesCertificat.setPrimerApellidoResponsable(dc.getPrimerApellidoResponsable());
						dadesCertificat.setSegundoApellidoResponsable(dc.getSegundoApellidoResponsable());
						dadesCertificat.setNifResponsable(dc.getNifResponsable());
						dadesCertificat.setIdEmisor(dc.getIdEmisor());
						dadesCertificat.setNifCif(dc.getNifCif());
						dadesCertificat.setEmail(dc.getEmail());
						dadesCertificat.setFechaNacimiento(dc.getFechaNacimiento());
						dadesCertificat.setRazonSocial(dc.getRazonSocial());
						dadesCertificat.setClasificacion(dc.getClasificacion());
						dadesCertificat.setNumeroSerie(dc.getNumeroSerie());
						dadesCertificats.add(dadesCertificat);
					}
					resposta.setDadesCertificat(dadesCertificats);
				}
			} else {
				infoSignatura.setValida(false);
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("Error verificant la signatura", ex);
			throw new SignaturaPluginException("Error verificant la signatura", ex);
		}
	}



	private AfirmaUtils getAfirmaUtils() {
		if (afirmaUtils == null) {
			String url = GlobalProperties.getInstance().getProperty(
					"app.signatura.plugin.afirma.urlbase");
			String idAplicacio = GlobalProperties.getInstance().getProperty(
					"app.signatura.plugin.afirma.appid");
			String usuari = GlobalProperties.getInstance().getProperty(
					"app.signatura.plugin.afirma.usuari");
			String contrasenya = GlobalProperties.getInstance().getProperty(
					"app.signatura.plugin.afirma.contrasenya");
			if (usuari != null && usuari.length() > 0) {
				afirmaUtils = new AfirmaUtils(
						url,
						idAplicacio,
						usuari,
						contrasenya);
			} else {
				afirmaUtils = new AfirmaUtils(
						url,
						idAplicacio);
			}
			//afirmaUtils.setLogMissatges(true);
		}
		return afirmaUtils;
	}

	private static final Log logger = LogFactory.getLog(SignaturaPluginAfirma.class);

}
