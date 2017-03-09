/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.ChannelSftp;

import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Notificacio;
import net.conselldemallorca.helium.core.model.hibernate.Remesa;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.NotificacioRepository;
import net.conselldemallorca.helium.v3.core.repository.RemesaRepository;

/**
 * Helper per a gestionar els entorns.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificacioHelper {

	@Resource
	private NotificacioRepository notificacioRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private SftpClientHelper sftpClientHelper;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private RemesaRepository remesaRepository;


	public Notificacio create(
			ExpedientDto expedient,
			NotificacioDto notificacioDto) {
		Notificacio notificacio = conversioTipusHelper.convertir(notificacioDto, Notificacio.class);
		notificacio.setExpedient(expedientRepository.findOne(expedient.getId()));
		notificacio.setDocument(documentStoreRepository.findById(notificacioDto.getDocument().getId()));
		
		List<DocumentStore> annexos = new ArrayList<DocumentStore>();
		for (DocumentNotificacioDto annex: notificacioDto.getAnnexos()) {
			annexos.add(documentStoreRepository.findById(annex.getId()));
		}
		notificacio.setAnnexos(annexos);
		
		return notificacioRepository.save(notificacio);
	}
	
	public List<Notificacio> findNotificacionsPerExpedientId(Long expedientId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId, 
				true, 
				false, 
				false, 
				false);
		
		return notificacioRepository.findByExpedientOrderByDataEnviamentDesc(expedient);
	}
	
	public List<Notificacio> findNotificacionsPerExpedientIdITipus(Long expedientId, DocumentNotificacioTipusEnumDto tipus) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId, 
				true, 
				false, 
				false, 
				false);
		
		return notificacioRepository.findByExpedientAndTipusOrderByDataEnviamentDesc(expedient, tipus);
	}
	
	public void obtenirJustificantNotificacio(Notificacio notificacio) {
		try {
			RespostaJustificantRecepcio resposta = pluginHelper.tramitacioObtenirJustificant(notificacio.getRegistreNumero());
			if (resposta != null && resposta.isOk()) {
				if (resposta.getData() != null) {
					notificacio.setEstat(DocumentEnviamentEstatEnumDto.PROCESSAT_OK);
				} else {
					notificacio.setEstat(DocumentEnviamentEstatEnumDto.ENVIAT);
				}
				notificacio.setDataRecepcio(resposta.getData());
				notificacio.setError(null);
			} else {
				notificacio.setError(resposta.getErrorDescripcio());
				notificacio.setEstat(DocumentEnviamentEstatEnumDto.PROCESSAT_ERROR);
			}
		} catch (Exception ex) {
			logger.error(
					"Error actualitzant estat notificacio " + notificacio.getRegistreNumero(),
					ex);
			notificacio.setError(ex.getMessage());
			notificacio.setEstat(DocumentEnviamentEstatEnumDto.PROCESSAT_ERROR);
		}
	}

	public boolean delete(
			String numero,
			String clave,
			Long codigo) {
		Notificacio notificacio = notificacioRepository.findByRegistreNumeroAndRdsCodiAndRdsClau(
				numero,
				codigo,
				clave);
		if (notificacio != null) {
			notificacioRepository.delete(notificacio);
			return true;
		}
		return false;
	}
	
	
	public void enviarRemesa(
			String remesaCodi, 
			Date dataEmisio, 
			Date dataPrevistaDeposit, 
			Long expedientTipusId, 
			List<Long> expedientIds) throws Exception {
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		
		String codiProducte = fragmentFitxer(expedientTipus.getSicerProducteCodi(), 2, false);
		String codiClient = fragmentFitxer(expedientTipus.getSicerClientCodi(), 8, false);
		String codiRemesa = fragmentFitxer(remesaCodi, 4, false);
		
		Date dataFitxerEnviament = new Date();
	    Calendar fitxerData = Calendar.getInstance();
	    fitxerData.setTime(dataFitxerEnviament);
	    String fitxerAnyMesDia = anyMesDiaData(fitxerData);
		
		String nomFitxer = 
				codiProducte + 
				codiClient + 
				fitxerAnyMesDia +
				"." +
				fragmentFitxer(fitxerData.get(Calendar.HOUR_OF_DAY), 2, true) + fragmentFitxer(fitxerData.get(Calendar.MINUTE), 2, true);
		
		PrintWriter fitxer = null;
		File file = null;
		
		try {
			file = new File(nomFitxer);
			fitxer = new PrintWriter(file);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	    
		Calendar emisioData = Calendar.getInstance();
		emisioData.setTime(dataEmisio);
		String emisioAnyMesDia = anyMesDiaData(emisioData);
		    
		Calendar previstaDepositData = Calendar.getInstance();
		previstaDepositData.setTime(dataPrevistaDeposit);
		String previstaDepositAnyMesDia = anyMesDiaData(previstaDepositData);
		
		String headerFitxer = "FN";
		headerFitxer += codiProducte;
		headerFitxer += codiClient;
		headerFitxer += fragmentFitxer(expedientTipus.getSicerPuntAdmissioCodi(), 7, false);
		headerFitxer += fitxerAnyMesDia;
		headerFitxer += fragmentFitxer(fitxerData.get(Calendar.HOUR_OF_DAY), 2, true) + ":" + fragmentFitxer(fitxerData.get(Calendar.MINUTE), 2, true);
		headerFitxer = String.format("%1$-" + 315 + "s", headerFitxer);
		fitxer.println(headerFitxer);
		
		String headerRemesa = "C";
		headerRemesa += codiProducte;
		headerRemesa += codiClient;
		headerRemesa += codiRemesa;
		headerRemesa += emisioAnyMesDia;
		headerRemesa += previstaDepositAnyMesDia;
		headerRemesa = String.format("%1$-" + 315 + "s", headerRemesa);
		fitxer.println(headerRemesa);
		
		List<Notificacio> notificacionsPerEnviar = new ArrayList<Notificacio>();
		
		int countDetalls = 0;
		for (Long expeidentId: expedientIds) {
				
			Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
					expeidentId, 
					true, 
					false, 
					false, 
					false);
			
			List<Notificacio> notificacionsSicer = findNotificacionsPerExpedientIdITipus(expedient.getId(), DocumentNotificacioTipusEnumDto.SICER);
			for (Notificacio notificacio: notificacionsSicer) {
				String detall = "D";
				detall += codiProducte;
				detall += codiClient;
				detall += codiRemesa;
				detall += (codiRemesa + String.format("%05d", countDetalls));
				detall += fragmentFitxer(expedientTipus.getSicerNomLlinatges(), 50, false);
				detall += String.format("%" + 50 + "s", "");
				detall += fragmentFitxer(expedientTipus.getSicerDireccio(), 50, false);
				detall += fragmentFitxer(expedientTipus.getSicerPoblacio(), 40, false);
				detall += fragmentFitxer(expedientTipus.getSicerCodiPostal(), 5, true);
				detall += String.format("%" + 46 + "s", "");
				detall += fragmentFitxer(notificacio.getExpedient().getId(), 41, false);
				detall = String.format("%1$-" + 315 + "s", detall);
				fitxer.println(detall);
				countDetalls++;
				
				notificacionsPerEnviar.add(notificacio);
			}
		}
		
		String totalDetalls = fragmentFitxer(countDetalls, 9, true);
		
		String footerRemesa = "c";
		footerRemesa += codiProducte;
		footerRemesa += codiClient;
		footerRemesa += codiRemesa;
		footerRemesa += totalDetalls;
		footerRemesa = String.format("%1$-" + 315 + "s", footerRemesa);
		fitxer.println(footerRemesa);
		
		String footerFitxer = "f";
		footerFitxer += codiProducte;
		footerFitxer += codiClient;
		footerFitxer += String.format("%03d", 1);
		footerFitxer += totalDetalls;
		footerFitxer = String.format("%1$-" + 315 + "s", footerFitxer);
		fitxer.println(footerFitxer);
		
		fitxer.close();
		
		/**enviem el fitxer i creem la remesa**/
		String errorEnviant = null;
		boolean fitxerEnviat = false;
		
		try {
			enviamentFitxerSftpSicer(file);
			fitxerEnviat = true;
		} catch(Exception ex) {
			errorEnviant = ExceptionUtils.getRootCauseMessage(ex);
		}
		
		
		Remesa remesa = remesaRepository.findByCodiAndExpedientTipus(codiRemesa, expedientTipus);
		
		if (remesa == null) {
			remesa = new Remesa();
			remesa.setCodi(codiRemesa);
			remesa.setProducteCodi(expedientTipus.getSicerProducteCodi());
			remesa.setClientCodi(expedientTipus.getSicerClientCodi());
			remesa.setDataCreacio(dataFitxerEnviament);
			remesa.setDataEmisio(dataEmisio);
			remesa.setDataPrevistaDeposit(dataPrevistaDeposit);
			remesa.setExpedientTipus(expedientTipus);
		}
		
		if (fitxerEnviat) {
			remesa.setEstat(DocumentEnviamentEstatEnumDto.ENVIAT);
		} else {
			remesa.setEstat(DocumentEnviamentEstatEnumDto.ENVIAT_ERROR);
		}
		remesa.setDataEnviament(dataFitxerEnviament);
		
		remesaRepository.save(remesa);
		/********************/
		countDetalls = 0;
		for (Notificacio notificacio: notificacionsPerEnviar) {
			notificacio.setRemesa(remesa);
			notificacio.setRegistreNumero((codiRemesa + String.format("%05d", countDetalls)));
			if (fitxerEnviat) {
				notificacio.setDataEnviament(dataFitxerEnviament);
				notificacio.setEstat(DocumentEnviamentEstatEnumDto.ENVIAT);
				notificacio.setError(null);
			} else {
				notificacio.setEstat(DocumentEnviamentEstatEnumDto.ENVIAT_ERROR);
				notificacio.setError("No s'ha pogut enviar el fitxer al SICER: " + errorEnviant );
			}
			countDetalls++;
		}		
	}
	
	public void comprovarRemesaEnviada(Remesa remesa) {
	    Calendar fitxerData = Calendar.getInstance();
	    fitxerData.setTime(remesa.getDataEnviament());
	    String fitxerAnyMesDia = anyMesDiaData(fitxerData);
		
		String nomFitxer = 
				remesa.getProducteCodi() + 
				remesa.getClientCodi() + 
				fitxerAnyMesDia +
				"." +
				fragmentFitxer(fitxerData.get(Calendar.HOUR_OF_DAY), 2, true) + fragmentFitxer(fitxerData.get(Calendar.MINUTE), 2, true) + 
				"-resultado.txt";
		
		BufferedReader br = obtenirFitxerSftpSicer("/home/limit/sftp-proves/Respostes", nomFitxer);
		DocumentEnviamentEstatEnumDto estatRemesa = remesa.getEstat();
		String sCurrentLine;
		boolean remesaCorrecte = false;
		String errorValidacio = null;
		if (br != null) {
			try {
				String firstLine = br.readLine();
				remesaCorrecte = "Si".equalsIgnoreCase(firstLine.substring(11,13));
				if (remesaCorrecte) {
					while ((sCurrentLine = br.readLine()) != null) {
						System.out.println(sCurrentLine);
					}
					estatRemesa = DocumentEnviamentEstatEnumDto.VALIDAT;
				} else {
					estatRemesa = DocumentEnviamentEstatEnumDto.VALIDAT_ERROR;
					errorValidacio = "ERROR VALIDACIO, FITXER INCORRECTE: ";
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				sftpClientHelper.closeConnection();
			}
			
			if (!remesaCorrecte)
				errorValidacio += obtenirErrorValidacio(remesa);
			
			actualitzarEstatRemesa(remesa, estatRemesa, errorValidacio);
		} else {
			sftpClientHelper.closeConnection();
		}
	}
	
	public void comprovarRemesaValidada(Remesa remesa) {
		Calendar fitxerData = Calendar.getInstance();
	    fitxerData.setTime(remesa.getDataEnviament());
	    String fitxerAnyMesDia = anyMesDiaData(fitxerData);
		
		String nomFitxer = 
				remesa.getProducteCodi() + 
				remesa.getClientCodi() + 
				fitxerAnyMesDia +
				"." +
				fragmentFitxer(fitxerData.get(Calendar.HOUR_OF_DAY), 2, true) + fragmentFitxer(fitxerData.get(Calendar.MINUTE), 2, true);
		
		BufferedReader br = obtenirFitxerSftpSicer("/home/limit/sftp-proves/Entregues", nomFitxer);
		String sCurrentLine;
		if (br != null) {
			try {
				while ((sCurrentLine = br.readLine()) != null) {
					if ("D".equalsIgnoreCase(sCurrentLine.substring(0, 1))) {
						String registreNumero = sCurrentLine.substring(16, 25);
						Notificacio notificacioValidada = notificacioRepository.findByRemesaAndRegistreNumero(remesa, registreNumero);
						
						String codiSituacio =  sCurrentLine.substring(25, 27);
						if ("01".equalsIgnoreCase(codiSituacio)) {
							notificacioValidada.setEstat(DocumentEnviamentEstatEnumDto.PROCESSAT_OK);
						} else {
							notificacioValidada.setEstat(DocumentEnviamentEstatEnumDto.PROCESSAT_OK);
						}
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				sftpClientHelper.closeConnection();
			}
			
			remesa.setEstat(DocumentEnviamentEstatEnumDto.PROCESSAT_OK);
		} else {
			sftpClientHelper.closeConnection();
		}
		
	}
	
	
	private void enviamentFitxerSftpSicer(File fitxer) throws Exception {
        String SFTPWORKINGDIR = "/";

        try {
        	ChannelSftp channelSftp = sftpClientHelper.openSicerSftpConnection();
       
            channelSftp.cd(SFTPWORKINGDIR);
            channelSftp.put(new FileInputStream(fitxer), fitxer.getName());
            System.out.println("File transfered successfully to host.");
        } catch (Exception ex) {
            throw ex;
        }
        finally{
        	sftpClientHelper.closeConnection();
        }
	}
	
	private BufferedReader obtenirFitxerSftpSicer(String path, String fileName) {
		BufferedReader br = null;
		try {
			String SFTPWORKINGDIR = path;
			ChannelSftp channelSftp = sftpClientHelper.openSicerSftpConnection();
       
            channelSftp.cd(SFTPWORKINGDIR);
            InputStream stream = channelSftp.get(fileName);
            br = new BufferedReader(new InputStreamReader(stream));
        } catch (Exception ex) {
            System.out.println("Exception found while tranfer the response.");
            ex.printStackTrace();
            sftpClientHelper.closeConnection();
        }
        return br;
	}
	
	private String obtenirErrorValidacio(Remesa remesa) {
		Calendar fitxerData = Calendar.getInstance();
	    fitxerData.setTime(remesa.getDataEnviament());
	    String fitxerAnyMesDia = anyMesDiaData(fitxerData);
		
		String nomFitxer = 
				remesa.getProducteCodi() + 
				remesa.getClientCodi() + 
				fitxerAnyMesDia +
				"." +
				fragmentFitxer(fitxerData.get(Calendar.HOUR_OF_DAY), 2, true) + fragmentFitxer(fitxerData.get(Calendar.MINUTE), 2, true) + 
				".1.bad";
		
		String texteError = "";
		
		BufferedReader br = obtenirFitxerSftpSicer("/home/limit/sftp-proves/Respostes", nomFitxer);
		String sCurrentLine;
		if (br != null) {
			try {
				while ((sCurrentLine = br.readLine()) != null) {
					if ("D".equalsIgnoreCase(sCurrentLine.substring(0, 1))) {
						texteError += "Error " + sCurrentLine.substring(1, 6) + ":";
						texteError += " " + sCurrentLine.substring(6, 321);
						texteError += "\n";
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				sftpClientHelper.closeConnection();
			}
			
		} else {
			sftpClientHelper.closeConnection();
		}
		
		return texteError;
	}
	
	private String fragmentFitxer(Object value, int maxLength, boolean isNumeric) {
		if (String.valueOf(value).length() > maxLength) {
			value = String.valueOf(value).substring(0, maxLength);
		} else if (String.valueOf(value).length() < maxLength) {
			if (isNumeric) {
				value = (Integer)value;
				value = String.format("%0" + maxLength + "d", value);
			} else {
				value = String.format("%1$-" + maxLength + "s", value);
			}
		}
		return String.valueOf(value);
	}

	private void actualitzarEstatRemesa(Remesa remesa, DocumentEnviamentEstatEnumDto estat, String error) {
		remesa.setEstat(estat);
		List<Notificacio> notificacionsPerValidar = notificacioRepository.findByRemesa(remesa);
		for (Notificacio notificacioPerValidar: notificacionsPerValidar) {
			notificacioPerValidar.setEstat(estat);
			notificacioPerValidar.setError(null);
			if (estat == DocumentEnviamentEstatEnumDto.VALIDAT_ERROR)
				notificacioPerValidar.setError(error);
		}
	}
	
	private String anyMesDiaData(Calendar data) {
		return fragmentFitxer(data.get(Calendar.YEAR), 4, true) + fragmentFitxer((data.get(Calendar.MONTH) + 1), 2, true) + fragmentFitxer(data.get(Calendar.DAY_OF_MONTH), 2, true);
	}
	
	private static final Log logger = LogFactory.getLog(NotificacioHelper.class);
}
