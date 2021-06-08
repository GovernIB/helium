package es.caib.helium.integracio.service.validacio;

import org.apache.ws.security.util.Base64;

import es.caib.helium.integracio.domini.validacio.AfirmaUtils;
import es.caib.helium.integracio.domini.validacio.RespostaValidacioSignatura;
import es.caib.helium.integracio.domini.validacio.ValidarSignaturaResponse;
import es.caib.helium.integracio.excepcions.validacio.ValidacioFirmaException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SignaturaPluginAfirma implements SignaturaPlugin {

	@Setter
	private AfirmaUtils afirmaUtils;

	@Override
	public RespostaValidacioSignatura verificarSignatura(
			byte[] document,
			byte[] signatura,
			boolean obtenirDadesCertificat) throws ValidacioFirmaException {
		
		try {
			ValidarSignaturaResponse response = afirmaUtils.validarSignatura(
					Base64.encode(document),
					Base64.encode(signatura),
					obtenirDadesCertificat);
			RespostaValidacioSignatura resposta = new RespostaValidacioSignatura();
			if (response.isEstatOk()) {
				resposta.setEstat(RespostaValidacioSignatura.ESTAT_OK);
				if (response.getDadesCertificat() != null) {
					
//					List<DadesCertificat> dadesCertificats = new ArrayList<DadesCertificat>();
//					for (net.conselldemallorca.helium.integracio.plugins.signatura.afirma.DadesCertificat dc: response.getDadesCertificat()) {
//						DadesCertificat dadesCertificat = new DadesCertificat();
//						dadesCertificat.setTipoCertificado(dc.getTipoCertificado());
//						dadesCertificat.setSubject(dc.getSubject());
//						dadesCertificat.setNombreResponsable(dc.getNombreResponsable());
//						dadesCertificat.setPrimerApellidoResponsable(dc.getPrimerApellidoResponsable());
//						dadesCertificat.setSegundoApellidoResponsable(dc.getSegundoApellidoResponsable());
//						dadesCertificat.setNifResponsable(dc.getNifResponsable());
//						dadesCertificat.setIdEmisor(dc.getIdEmisor());
//						dadesCertificat.setNifCif(dc.getNifCif());
//						dadesCertificat.setEmail(dc.getEmail());
//						dadesCertificat.setFechaNacimiento(dc.getFechaNacimiento());
//						dadesCertificat.setRazonSocial(dc.getRazonSocial());
//						dadesCertificat.setClasificacion(dc.getClasificacion());
//						dadesCertificat.setNumeroSerie(dc.getNumeroSerie());
//						dadesCertificats.add(dadesCertificat);
//					}
					resposta.setDadesCertificat(response.getDadesCertificat());
				}
			} else {
				resposta.setEstat(RespostaValidacioSignatura.ESTAT_ERROR);
				resposta.setErrorDescripcio(response.getErrorDescripcio());
			}
			return resposta;
		} catch (Exception ex) {
			log.error("Error verificant la signatura", ex);
			throw new ValidacioFirmaException("Error verificant la signatura", ex);
		}
	}
}
