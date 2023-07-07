package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.fundaciobit.apisib.apifirmaasyncsimple.v2.ApiFirmaAsyncSimple;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleAnnex;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleDocumentTypeInformation;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleExternalSigner;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleFile;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleReviser;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignature;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureBlock;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureRequestInfo;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureRequestWithFlowTemplateCode;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureRequestWithSignBlockList;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignedFile;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSigner;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.jersey.ApiFirmaAsyncSimpleJersey;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.ApiFlowTemplateSimple;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleBlock;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleEditFlowTemplateRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleFlowTemplate;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleFlowTemplateList;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleFlowTemplateRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleGetTransactionIdRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleKeyValue;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleReviser;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleSignature;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleStartTransactionRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleViewFlowTemplateRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.jersey.ApiFlowTemplateSimpleJersey;

import es.caib.portafib.ws.api.v1.WsI18NException;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.OpenOfficeUtils;
import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;

/**
 * Implementació del plugin de portasignatures per l'API REST Simple del PortaFIB.
 * Data: 31/01/2022
 * Després de la implementació per passarela WS SOAP es crea el plugin
 * per a les peticions per API REST i callback per API REST.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortasignaturesPluginPortafibSimple implements PortasignaturesPlugin {

	private OpenOfficeUtils openOfficeUtils;

	@Override
	public Integer uploadDocument(
			DocumentPortasignatures document, 
			List<DocumentPortasignatures> annexos,
			boolean isSignarAnnexos, 
			PasSignatura[] passesSignatura, 
			String remitent, 
			String importancia,
			Date dataLimit,
			String plantillaFluxId) throws PortasignaturesPluginException {

		try {
			long peticioDeFirmaId = 0;
			FirmaAsyncSimpleSignatureRequestWithSignBlockList signatureRequest = new FirmaAsyncSimpleSignatureRequestWithSignBlockList();

			signatureRequest.setTitle(document.getTitol());
			signatureRequest.setDescription(document.getDescripcio());
			signatureRequest.setReason("Firma de document");
			signatureRequest.setSenderName(remitent);
			if (importancia != null) {
				if ("hight".equals(importancia)) {
					signatureRequest.setPriority(9);
				} else if ("low".equals(importancia)) {
					signatureRequest.setPriority(0);
				} else {
					// normal
					signatureRequest.setPriority(5);
				}
			}
			if (dataLimit != null) {
				signatureRequest.setAdditionalInformation("Data límit: " + new SimpleDateFormat("dd/MM/yyyy").format(dataLimit));
			} else {
				signatureRequest.setAdditionalInformation(null);
			}

			signatureRequest.setFileToSign(toFirmaAsyncSimpleFile(document));

			if (document.getTipus() != null) {
				signatureRequest.setDocumentType(
						new Long(document.getTipus().intValue()).longValue());
			} else {
				throw new PortasignaturesPluginException(
						"El tipus de document not pot estar buit." +
						" Els tipus permesos son: " + getDocumentTipusStr());
			}
			signatureRequest.setLanguageUI("ca");
			signatureRequest.setLanguageDoc("ca");
			signatureRequest.setProfileCode(getPerfil());
			
			if (annexos != null) {
				List<FirmaAsyncSimpleAnnex> portafirmesAnnexos = new ArrayList<FirmaAsyncSimpleAnnex>();
				
				FirmaAsyncSimpleAnnex portafirmesAnnex;
				for (DocumentPortasignatures annex : annexos) {
					portafirmesAnnex = new FirmaAsyncSimpleAnnex();
					portafirmesAnnex.setAttach(false);
					portafirmesAnnex.setSign(false);
											
					FirmaAsyncSimpleFile fitxer = new FirmaAsyncSimpleFile();
					fitxer.setNom(annex.getArxiuNom());
					fitxer.setData(annex.getArxiuContingut());
					fitxer.setMime(getOpenOfficeUtils().getArxiuMimeType(annex.getArxiuNom()));
					portafirmesAnnex.setAnnex(fitxer);
					
					portafirmesAnnexos.add(portafirmesAnnex);
				}
				signatureRequest.setAnnexs(portafirmesAnnexos);
			}
			FirmaAsyncSimpleSignatureBlock[] signatureBlocks  = null;
			if (plantillaFluxId != null /* || idTransaccio != null */) {//MARTA mirar aquí! plantillaFluxId i blocs
				FirmaAsyncSimpleSignatureRequestWithFlowTemplateCode signatureRequestAmbPlantilla;
				
				
				//				### convertir en blocs de portafirmes a partir d'un id de transacció o d'una plantilla
//				signatureBlocks = idTransaccio != null ? recuperarFluxDeFirma(idTransaccio) : toFirmaAsyncSimpleSignatureBlockFromId(plantillaFluxId, "ca");
				signatureBlocks = toFirmaAsyncSimpleSignatureBlockFromId(plantillaFluxId, "ca");
				signatureRequest.setSignatureBlocks(signatureBlocks);
				signatureRequestAmbPlantilla = new FirmaAsyncSimpleSignatureRequestWithFlowTemplateCode(
			            signatureRequest, plantillaFluxId);
				peticioDeFirmaId = getFirmaAsyncSimpleApi().createAndStartSignatureRequestWithFlowTemplateCode(signatureRequestAmbPlantilla);
			} else if (passesSignatura != null && passesSignatura.length > 0) {
				signatureBlocks  = toFluxDeFirmes(Arrays.asList(passesSignatura));
				signatureRequest.setSignatureBlocks(signatureBlocks);
				peticioDeFirmaId = getFirmaAsyncSimpleApi().createAndStartSignatureRequestWithSignBlockList(signatureRequest);
			}
//			signatureRequest.setSignatureBlocks(signatureBlocks);
//			peticioDeFirmaId = getFirmaAsyncSimpleApi().createAndStartSignatureRequestWithSignBlockList(signatureRequest);//MARTA peta aquí!  
			//no pot transformar el json a un objecte, mira si tenim les mateixes dependències que a Ripea....
			return new Long(peticioDeFirmaId).intValue();
		} catch (Exception ex) {
			throw new PortasignaturesPluginException(
					"No s'ha pogut pujar el document al portafirmes (" +
					"titol=" + document.getTitol() + ", " +
					"descripcio=" + document.getDescripcio() + ", " +
					"arxiuNom=" + document.getArxiuNom() + ")" +
					" error= " + ex.getMessage(),
					ex);
		}
	}
	
	private FirmaAsyncSimpleSignatureBlock[] toFirmaAsyncSimpleSignatureBlockFromId(
			String plantillaFluxId,
			String idioma) throws SistemaExternException {
		FirmaAsyncSimpleSignatureBlock[] blocsAsyncs = null;
		List<FlowTemplateSimpleBlock> blocks = null;
		try {
			FlowTemplateSimpleFlowTemplateRequest request = new FlowTemplateSimpleFlowTemplateRequest();

			request.setFlowTemplateId(plantillaFluxId);
			request.setLanguageUI(idioma);

			FlowTemplateSimpleFlowTemplate result = getFluxDeFirmaClient().getFlowInfoByFlowTemplateID(request);

			if (result != null) {
				blocks = result.getBlocks();
			}
		
			blocsAsyncs = toFirmaAsyncSimpleSignatureBlock(blocks);
		} catch (Exception ex) {
			throw new SistemaExternException(ex);
		}
		return blocsAsyncs;
	}
	
	private FirmaAsyncSimpleSignatureBlock[] toFirmaAsyncSimpleSignatureBlock(List<FlowTemplateSimpleBlock> blocks) throws SistemaExternException {
		FirmaAsyncSimpleSignatureBlock[] blocsAsyncs = null;
		int i = 0;
		
		try {
			if (blocks != null) {
				blocsAsyncs = new FirmaAsyncSimpleSignatureBlock[blocks.size()];
		
				for (FlowTemplateSimpleBlock flowTemplateSimpleBlock : blocks) {
					FirmaAsyncSimpleSignatureBlock blocAsync = new FirmaAsyncSimpleSignatureBlock();
					//firmes mínimes
					blocAsync.setMinimumNumberOfSignaturesRequired(flowTemplateSimpleBlock.getSignatureMinimum());
		
					//Firmants
					List<FirmaAsyncSimpleSignature> signatures = new ArrayList<FirmaAsyncSimpleSignature>();
		
					for (FlowTemplateSimpleSignature flowTemplateSimpleSignature : flowTemplateSimpleBlock.getSignatures()) {
						FirmaAsyncSimpleSignature signature = new FirmaAsyncSimpleSignature();
						signature.setMinimumNumberOfRevisers(flowTemplateSimpleSignature.getMinimumNumberOfRevisers());
						signature.setReason(flowTemplateSimpleSignature.getReason());
						signature.setRequired(flowTemplateSimpleSignature.isRequired());
		
						//Revisor
						if (flowTemplateSimpleSignature.getRevisers() != null) {
							List<FirmaAsyncSimpleReviser> revisers = new ArrayList<FirmaAsyncSimpleReviser>();
							for (FlowTemplateSimpleReviser flowTemplateSimpleReviser : flowTemplateSimpleSignature.getRevisers()) {
								FirmaAsyncSimpleReviser reviser = new FirmaAsyncSimpleReviser();
								
								String intermediateServerUsername = flowTemplateSimpleReviser.getIntermediateServerUsername();
								String positionInTheCompany = flowTemplateSimpleReviser.getPositionInTheCompany();
								if (intermediateServerUsername != null)
									reviser.setIntermediateServerUsername(intermediateServerUsername);
								if (positionInTheCompany != null)
									reviser.setPositionInTheCompany(positionInTheCompany);
								reviser.setAdministrationID(flowTemplateSimpleReviser.getAdministrationID());
								reviser.setRequired(flowTemplateSimpleReviser.isRequired());
								reviser.setUsername(flowTemplateSimpleReviser.getUsername());
		
								revisers.add(reviser);
							}
							signature.setRevisers(revisers);
						}
						//Firmant
						FirmaAsyncSimpleSigner signer = new FirmaAsyncSimpleSigner();
		
						if (flowTemplateSimpleSignature.getSigner() != null) {
							signer.setAdministrationID(flowTemplateSimpleSignature.getSigner().getAdministrationID());
		
							if (flowTemplateSimpleSignature.getSigner().getExternalSigner() != null) {
								FirmaAsyncSimpleExternalSigner externalSigner = new FirmaAsyncSimpleExternalSigner();
		
								externalSigner.setAdministrationId(flowTemplateSimpleSignature.getSigner().getExternalSigner().getAdministrationId());
								externalSigner.setEmail(flowTemplateSimpleSignature.getSigner().getExternalSigner().getEmail());
								externalSigner.setLanguage(flowTemplateSimpleSignature.getSigner().getExternalSigner().getLanguage());
								externalSigner.setName(flowTemplateSimpleSignature.getSigner().getExternalSigner().getName());
								externalSigner.setSecurityLevel(flowTemplateSimpleSignature.getSigner().getExternalSigner().getSecurityLevel());
								externalSigner.setSurnames(flowTemplateSimpleSignature.getSigner().getExternalSigner().getSurnames());
		
								signer.setExternalSigner(externalSigner);
							}
							String intermediateServerUsername = flowTemplateSimpleSignature.getSigner().getIntermediateServerUsername();
							String positionInTheCompany = flowTemplateSimpleSignature.getSigner().getPositionInTheCompany();
							if (intermediateServerUsername != null)
								signer.setIntermediateServerUsername(intermediateServerUsername);
							if (positionInTheCompany != null)
								signer.setPositionInTheCompany(positionInTheCompany);
							signer.setUsername(flowTemplateSimpleSignature.getSigner().getUsername());
							
							signature.setSigner(signer);
						}
						signatures.add(signature);
					}
		
					blocAsync.setSigners(signatures);
					blocsAsyncs[i] = blocAsync;
					i++;
				}
			}
		} catch (Exception ex) {
			throw new SistemaExternException(ex);
		}
		return blocsAsyncs;
	}


	@Override
	public List<byte[]> obtenirSignaturesDocument(Integer documentId) throws PortasignaturesPluginException {
		
		List<byte[]> contingut = new ArrayList<byte[]>();
		try {
			FirmaAsyncSimpleSignatureRequestInfo requestInfo = new FirmaAsyncSimpleSignatureRequestInfo(new Long(documentId).longValue(), "ca");
			FirmaAsyncSimpleSignedFile fitxerDescarregat = getFirmaAsyncSimpleApi().getSignedFileOfSignatureRequest(requestInfo);
			contingut.add(fitxerDescarregat.getSignedFile().getData());			
			return contingut;
		} catch (Exception ex) {
			throw new PortasignaturesPluginException(
					"No s'ha pogut descarregar el document del portafirmes (documentId=" + documentId + ")",
					ex);
		}
	}

	@Override
	public void deleteDocuments(Integer document) throws PortasignaturesPluginException {
		try {
			FirmaAsyncSimpleSignatureRequestInfo requestInfo = new FirmaAsyncSimpleSignatureRequestInfo(new Long(document).longValue(), "ca");
			getFirmaAsyncSimpleApi().deleteSignatureRequest(requestInfo);
		} catch (Exception ex) {
			throw new PortasignaturesPluginException(
					"No s'ha pogut esborrar el document del portafirmes (id=" + document + ")",
					ex);
		}
	}


	private ApiFirmaAsyncSimple getFirmaAsyncSimpleApi() throws MalformedURLException {
		String apiRestUrl = getBaseUrl() + "/common/rest/apifirmaasyncsimple/v2";
		ApiFirmaAsyncSimple api = new ApiFirmaAsyncSimpleJersey(
				apiRestUrl,
				getUserName(),
				getPassword());
		return api;
	}

	private String getBaseUrl() {
		return (String)GlobalProperties.getInstance().getProperty(
				"app.portasignatures.plugin.portafib.base.url");
	}

	private String getUserName() {
		return GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.portafib.username");
	}
	private String getPassword() {
		return GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.portafib.password");
	}
	private String getPerfil() {
		return GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.portafib.perfil");
	}

	private FirmaAsyncSimpleFile toFirmaAsyncSimpleFile(
			DocumentPortasignatures document) throws Exception {
		if (!document.getArxiuNom().endsWith(".pdf")) {
			throw new PortasignaturesPluginException(
					"Els arxius per firmar han de ser de tipus PDF");
		}
		FirmaAsyncSimpleFile fitxer = new FirmaAsyncSimpleFile();
		fitxer.setNom(document.getArxiuNom());
		fitxer.setMime("application/pdf");
		fitxer.setData(document.getArxiuContingut());
		return fitxer;
	}


	public String getDocumentTipusStr() throws MalformedURLException, WsI18NException {
		StringBuilder sb = new StringBuilder();
		
		try {
			List<FirmaAsyncSimpleDocumentTypeInformation> tipusLlistat = getFirmaAsyncSimpleApi().getAvailableTypesOfDocuments("ca");
			for (FirmaAsyncSimpleDocumentTypeInformation tipusDocumentWs: tipusLlistat) {
				sb.append("[tipusId=");
				sb.append(tipusDocumentWs.getDocumentType());
				sb.append(", nom=");
				sb.append(tipusDocumentWs.getName());
				sb.append("]");
			}
		} catch (Exception ex) {
			sb.append("[No s'han pogut consultar els diferents tipus de documents. Error: " + ex.getMessage() + "]");
		}
		return sb.toString();
	}
	
	private FirmaAsyncSimpleSignatureBlock[] toFluxDeFirmes(
			List<PasSignatura> passes) throws Exception {
		FirmaAsyncSimpleSignatureBlock[] blocsAsyncs = null;
		if (passes == null || passes.isEmpty()) {
			throw new PortasignaturesPluginException(
					"És necessari configurar algun responsable de firmar el document");
		}
		try {
			blocsAsyncs = new FirmaAsyncSimpleSignatureBlock[passes.size()];
			for (int i = 0; i < passes.size(); i++) {
				PasSignatura pas = passes.get(i);
				FirmaAsyncSimpleSignatureBlock blocAsync = new FirmaAsyncSimpleSignatureBlock();
				
				blocAsync.setMinimumNumberOfSignaturesRequired(pas.getMinSignataris());
			    String signatari;
				List<FirmaAsyncSimpleSignature> signatures = new ArrayList<FirmaAsyncSimpleSignature>();
			    for (int j = 0; j<pas.getSignataris().length; j++) {
					FirmaAsyncSimpleSignature signature = new FirmaAsyncSimpleSignature();
					signature.setRequired(true);
					
			    	signatari = pas.getSignataris()[j];
					//Firmant
					FirmaAsyncSimpleSigner signer = new FirmaAsyncSimpleSigner();
					if (signatari.startsWith("CARREC")) {
						String carrecName = signatari.substring(signatari.indexOf("[") + 1, signatari.indexOf("]"));
						signer.setPositionInTheCompany(carrecName);
					} else {
						signer.setUsername(signatari);
					}
					signature.setSigner(signer);
					signatures.add(signature);
			    }
				blocAsync.setSigners(signatures);
				blocsAsyncs[i] = blocAsync;
			}
		} catch (Exception e) {
			throw new PortasignaturesPluginException("Hi ha hagut un error construint el flux", e);
		}
		return blocsAsyncs;
	}

	private OpenOfficeUtils getOpenOfficeUtils() {
		if (openOfficeUtils == null) {
			openOfficeUtils = new OpenOfficeUtils();
		}
		return openOfficeUtils;
	}
	
	@Override
	public PortafirmesIniciFluxResposta iniciarFluxDeFirma(String idioma, boolean isPlantilla, String nom,
			String descripcio, boolean descripcioVisible, String urlReturn) throws SistemaExternException {
		PortafirmesIniciFluxResposta transaccioResponse = new PortafirmesIniciFluxResposta();
		try {
			String idTransaccio = getTransaction(
					idioma,
					isPlantilla,
					nom,
					descripcio,
					descripcioVisible);

			String urlRedireccio = startTransaction(
					idTransaccio,
					urlReturn + idTransaccio);
			transaccioResponse.setIdTransaccio(idTransaccio);
			transaccioResponse.setUrlRedireccio(urlRedireccio);

		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error iniciant la transacció: " + ex.getCause(),
					ex);
		}

		return transaccioResponse;
	}
	
	private String startTransaction(
			String idTransaccio,
			String urlReturn) throws SistemaExternException {
		String urlRedireccio = null;
		try {
			FlowTemplateSimpleStartTransactionRequest transactionRequest = new FlowTemplateSimpleStartTransactionRequest(
					idTransaccio,
					urlReturn);

			urlRedireccio = getFluxDeFirmaClient().startTransaction(transactionRequest);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut iniciar la transacció (" +
					"portafib=" + getBaseUrl() + ", " +			
					"transactionId=" + idTransaccio + ", " +
					"returnUrl=" + urlReturn + ")",
					ex);
		}
		return urlRedireccio;
	}

	@Override
	public PortafirmesFluxResposta recuperarFluxDeFirmaByIdTransaccio(String idTransaccio)
			throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PortafirmesFluxResposta> recuperarPlantillesDisponibles(String idioma) throws SistemaExternException {
		List<PortafirmesFluxResposta> plantilles = new ArrayList<PortafirmesFluxResposta>();
		try {
			FlowTemplateSimpleFlowTemplateList resposta = getFluxDeFirmaClient().getAllFlowTemplates("ca");
			
			for (FlowTemplateSimpleKeyValue flowTemplate : resposta.getList()) {
				PortafirmesFluxResposta plantilla = new PortafirmesFluxResposta();
				plantilla.setFluxId(flowTemplate.getKey());
				plantilla.setNom(flowTemplate.getValue());
				plantilles.add(plantilla);
//				System.out.println("plantilla = " + flowTemplate.getKey() +"_"+ flowTemplate.getValue());
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut recuperar les plantilles per l'usuari aplicació actual",
					ex);
		}
		return plantilles;
	}

	@Override
	public PortafirmesFluxInfo recuperarFluxDeFirmaByIdPlantilla(String plantillaFluxId, String idioma)
			throws SistemaExternException {
		PortafirmesFluxInfo info = null;
		try {
			FlowTemplateSimpleFlowTemplateRequest request = new FlowTemplateSimpleFlowTemplateRequest(idioma, plantillaFluxId);

			FlowTemplateSimpleFlowTemplate result = getFluxDeFirmaClient().getFlowInfoByFlowTemplateID(request);
			
			if (result != null) {
				info = new PortafirmesFluxInfo();
				info.setNom(result.getName());
				info.setDescripcio(result.getDescription());
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error recuperant el detall del flux de firmes",
					ex);
		}
		return info;
	}

	@Override
	public String recuperarUrlViewEditPlantilla(String idPlantilla, String idioma, String urlReturn, boolean edicio)
			throws SistemaExternException {
		String urlPlantilla;
		try {
			if (!edicio) {
				FlowTemplateSimpleViewFlowTemplateRequest request = new FlowTemplateSimpleViewFlowTemplateRequest(idioma, idPlantilla);
				urlPlantilla = getFluxDeFirmaClient().getUrlToViewFlowTemplate(request);
			} else {
				FlowTemplateSimpleEditFlowTemplateRequest request = new FlowTemplateSimpleEditFlowTemplateRequest(idioma, idPlantilla, urlReturn);
				urlPlantilla = getFluxDeFirmaClient().getUrlToEditFlowTemplate(request);
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut recuperar la url per visualitzar el flux de firmes",
					ex);
		}
		return urlPlantilla;
	}

	@Override
	public boolean esborrarPlantillaFirma(String idioma, String plantillaFluxId) throws SistemaExternException {
		boolean esborrat = false;
		try {
			FlowTemplateSimpleFlowTemplateRequest request = new FlowTemplateSimpleFlowTemplateRequest(idioma, plantillaFluxId);
			esborrat = getFluxDeFirmaClient().deleteFlowTemplate(request);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Hi ha hagut un problema esborrant el flux de firmes",
					ex);
		}
		return esborrat;
	}

	@Override
	public void tancarTransaccioFlux(String idTransaccio) throws SistemaExternException {
		try {
			closeTransaction(idTransaccio);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error tancant la transacció",
					ex);
		}
	}

	@Override
	public List<PortafirmesCarrec> recuperarCarrecs() throws SistemaExternException {
//		List<PortafirmesCarrec> carrecs = new ArrayList<PortafirmesCarrec>();
//		try {
//			List<CarrecWs> carrecsWs = getUsuariEntitatWs().getCarrecsOfMyEntitat();
//			if (carrecsWs != null) {
//				for (CarrecWs carrecWs : carrecsWs) {
//					PortafirmesCarrec carrec = new PortafirmesCarrec();
//					carrec.setCarrecId(carrecWs.getCarrecID());
//					carrec.setCarrecName(carrecWs.getCarrecName());
//					carrec.setEntitatId(carrecWs.getEntitatID());
//					carrec.setUsuariPersonaId(carrecWs.getUsuariPersonaID());
//					if (carrecMostrarPersona()) {
//						UsuariPersonaBean usuariPersona = getUsuariEntitatWs().getUsuariPersona(carrecWs.getUsuariPersonaID());
//						if (usuariPersona != null) {
//							carrec.setUsuariPersonaNif(usuariPersona.getNif());
//							carrec.setUsuariPersonaEmail(usuariPersona.getEmail());
//							carrec.setUsuariPersonaNom(usuariPersona.getNom());
//						} else {
//							throw new SistemaExternException("No s'ha trobat cap usuari persona amb id " + carrecWs.getUsuariPersonaID() + " relacionat amb aquest càrrec");
//						}
//					}
//					carrecs.add(carrec);
//				}
//			}
//			return carrecs;
//		} catch (Exception ex) {
//			throw new SistemaExternException("Hi ha hagut un problema recuperant els càrrecs per l'usuari aplicació " + getUsername(), ex);
//		}
		return null;
	}

	@Override
	public PortafirmesCarrec recuperarCarrec(String carrecId) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PortafirmesFluxResposta> recuperarPlantillesPerFiltre(String idioma, String descripcio)
			throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String recuperarUrlViewEstatFluxDeFirmes(long portafirmesId, String idioma) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String getTransaction(
			String idioma,
			boolean isPlantilla,
			String nom,
			String descripcio,
			boolean descripcioVisible) throws SistemaExternException {
		String transactionId = null;
		try {
			FlowTemplateSimpleGetTransactionIdRequest transactionRequest = new FlowTemplateSimpleGetTransactionIdRequest(
					idioma,
					isPlantilla,
					nom,
					descripcio,
					descripcioVisible);

			transactionId = getFluxDeFirmaClient().getTransactionID(transactionRequest);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut recuperar el id de la transacció (" +
					"portafib=" + getBaseUrl() + ", " +					
					"nom=" + nom + ", " +
					"descripcio=" + descripcio + ")",
					ex);
		}
		return transactionId;
	}
	
	private void closeTransaction(
			String transactionID) throws SistemaExternException {
		try {
			getFluxDeFirmaClient().closeTransaction(transactionID);
		} catch (Exception ex) {
			throw new SistemaExternException("", ex);
		}
	}
	
	private ApiFlowTemplateSimple getFluxDeFirmaClient() throws MalformedURLException {
		String apiRestUrl = getFluxUrl();
		ApiFlowTemplateSimple api = new ApiFlowTemplateSimpleJersey(
				apiRestUrl,
				getFluxUserName(),
				getFluxPassword());
		return api;
	}
	
	private String getFluxUrl() {
		return (String)GlobalProperties.getInstance().getProperty(
				"app.portafirmes.plugin.flux.firma.url");
	}

	private String getFluxUserName() {
		return GlobalProperties.getInstance().getProperty("app.portafirmes.plugin.flux.firma.usuari");
	}
	private String getFluxPassword() {
		return GlobalProperties.getInstance().getProperty("app.portafirmes.plugin.flux.firma.password");
	}
	

}
