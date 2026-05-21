package es.caib.helium.integracio.plugins.portasignatures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import es.caib.helium.commons.config.PropertyConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.ApiFirmaAsyncSimple;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleAnnex;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleExternalSigner;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleFile;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleReviser;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignature;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureBlock;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureRequestInfo;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureRequestWithSignBlockList;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSigner;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.jersey.ApiFirmaAsyncSimpleJersey;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.ApiFlowTemplateSimple;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleBlock;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleFlowTemplate;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleFlowTemplateList;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleFlowTemplateRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleGetFlowResultResponse;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleGetTransactionIdRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleKeyValue;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleReviser;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleSignature;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleSigner;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleStartTransactionRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleStatus;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.jersey.ApiFlowTemplateSimpleJersey;
import org.fundaciobit.apisib.core.exceptions.ApisIBClientException;
import org.fundaciobit.apisib.core.exceptions.ApisIBServerException;
import org.fundaciobit.apisib.core.exceptions.ApisIBTimeOutException;

import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.commons.utils.GlobalProperties;
import es.caib.portafib.ws.api.v1.CarrecWs;
import es.caib.portafib.ws.api.v1.PortaFIBUsuariEntitatWs;
import es.caib.portafib.ws.api.v1.PortaFIBUsuariEntitatWsService;
import es.caib.portafib.ws.api.v1.UsuariEntitatBean;
import es.caib.portafib.ws.api.v1.UsuariPersonaBean;

/**
 * Implementació del plugin de portafirmes emprant el portafirmes de la CAIB
 * desenvolupat per l'IBIT (PortaFIB).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortasignaturesAsyncPlugin implements PortasignaturesPlugin {

	@Override
	public Integer uploadDocument(
				DocumentPortasignatures document,
				List<DocumentPortasignatures> annexos,
				boolean isSignarAnnexos,
				List<PortafirmesFluxBloc> blocList,
				String remitent,
				String importancia,
				Date dataLimit,
				String fluxId,
				String fluxTipus) throws PortasignaturesPluginException {
		try {

			long peticioDeFirmaId = 0;
			FirmaAsyncSimpleSignatureRequestWithSignBlockList signatureRequest = new FirmaAsyncSimpleSignatureRequestWithSignBlockList();

			signatureRequest.setTitle(document.getTitol());
			signatureRequest.setDescription(document.getDescripcio());
			signatureRequest.setSenderName(remitent);
			signatureRequest.setPriority(0);
			signatureRequest.setAdditionalInformation(null);
			signatureRequest.setFileToSign(toFirmaAsyncSimpleFile(document));
			signatureRequest.setDocumentType(Long.valueOf(document.getTipus()));
			signatureRequest.setLanguageUI("ca");
			signatureRequest.setLanguageDoc("ca");
			signatureRequest.setProfileCode(getPerfil());

			if (annexos != null) {
				List<FirmaAsyncSimpleAnnex> portafirmesAnnexos = new ArrayList<FirmaAsyncSimpleAnnex>();

				for (DocumentPortasignatures annex : annexos) {
					FirmaAsyncSimpleAnnex portafirmesAnnex = new FirmaAsyncSimpleAnnex();
					portafirmesAnnex.setAnnex(toFirmaAsyncSimpleFile(annex));
					portafirmesAnnex.setAttach(false);
					portafirmesAnnex.setSign(false);
					portafirmesAnnexos.add(portafirmesAnnex);
				}
				signatureRequest.setAnnexs(portafirmesAnnexos);
			}
			FirmaAsyncSimpleSignatureBlock[] signatureBlocks = null;
			if (fluxId != null) {
//				### convertir en blocs de portafirmes a partir d'un id de transacció o d'una plantilla
				signatureBlocks = toFirmaAsyncSimpleSignatureBlockFromId(fluxId, "ca");
			} else if(blocList != null) {
				signatureBlocks = new FirmaAsyncSimpleSignatureBlock[blocList.size()];
				for(PortafirmesFluxBloc bloc : blocList) {
					bloc.getDestinataris();
					FirmaAsyncSimpleSignature fass = new FirmaAsyncSimpleSignature();
					fass.setMinimumNumberOfRevisers(0);
					fass.setReason(fluxTipus);
					fass.setRequired(isSignarAnnexos);
					fass.setRevisers(null);
					fass.setSigner(null);

					List<FirmaAsyncSimpleSignature> fassList = new ArrayList<FirmaAsyncSimpleSignature>();
					fassList.add(fass);
					FirmaAsyncSimpleSignatureBlock b = new FirmaAsyncSimpleSignatureBlock(bloc.getMinSignataris(), fassList);
					b.setSigners(null);
				}
			}
			signatureRequest.setSignatureBlocks(signatureBlocks);
			peticioDeFirmaId = getFirmaAsyncSimpleApi()
					.createAndStartSignatureRequestWithSignBlockList(signatureRequest);
			return Long.valueOf(peticioDeFirmaId).intValue();
		} catch (Exception ex) {
			throw new PortasignaturesPluginException("No s'ha pogut pujar el document al portafirmes (" + "titol="
					+ document.getTitol() + ", " + "descripcio=" + document.getDescripcio() + ", " + "arxiuNom="
					+ document.getArxiuNom() + ")" + " error= " + ex.getMessage(), ex);
		}
	}

	@Override
	public List<byte[]> obtenirSignaturesDocument(Integer documentId) throws PortasignaturesPluginException {
		return null;
	}

	@Override
	public void deleteDocuments(Integer id) throws PortasignaturesPluginException {
		try {
			FirmaAsyncSimpleSignatureRequestInfo requestInfo = new FirmaAsyncSimpleSignatureRequestInfo(Long.valueOf(id).longValue(), "ca");
			getFirmaAsyncSimpleApi().deleteSignatureRequest(requestInfo);
		} catch (Exception ex) {
			throw new PortasignaturesPluginException(
					"No s'ha pogut esborrar el document del portafirmes (id=" + id + ")",
					ex);
		}
	}

	@Override
	public PortafirmesIniciFluxResposta iniciarFluxDeFirma(
			String idioma,
			boolean isPlantilla,
			String nom,
			String descripcio,
			boolean descripcioVisible,
			String returnUrl) throws SistemaExternException {
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
					returnUrl + idTransaccio);
			transaccioResponse.setIdTransaccio(idTransaccio);
			transaccioResponse.setUrlRedireccio(urlRedireccio);

		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error iniciant la transacció: " + ex.getCause(),
					ex);
		}

		return transaccioResponse;
	}

	@Override
	public PortafirmesFluxResposta recuperarFluxDeFirmaByIdTransaccio(String idTransaccio)
			throws SistemaExternException {
		PortafirmesFluxResposta resposta = new PortafirmesFluxResposta();
		try {
			FlowTemplateSimpleGetFlowResultResponse result = getFlowTemplateResult(idTransaccio);
			FlowTemplateSimpleStatus transactionStatus = result.getStatus();
			int status = transactionStatus.getStatus();

			switch (status) {
				case FlowTemplateSimpleStatus.STATUS_INITIALIZING:
						resposta.setError(true);
						resposta.setEstat(PortafirmesFluxEstat.INITIALIZING);
						logger.error("S'ha rebut un estat inconsistent del procés de construcció del flux. (Inialitzant). Consulti amb el seu administrador.");
						return resposta;
				case FlowTemplateSimpleStatus.STATUS_IN_PROGRESS:
						resposta.setError(true);
						resposta.setEstat(PortafirmesFluxEstat.IN_PROGRESS);
						logger.error("S'ha rebut un estat inconsistent de construcció del flux (En Progrés). Consulti amb el seu administrador.");
						return resposta;
				case FlowTemplateSimpleStatus.STATUS_FINAL_ERROR:
						String desc = transactionStatus.getErrorStackTrace();
						resposta.setError(true);
						resposta.setEstat(PortafirmesFluxEstat.FINAL_ERROR);
						if (desc != null) {
							logger.error(desc);
						}
						logger.error("Error durant la construcció del flux: " + transactionStatus.getErrorMessage());
						return resposta;
				case FlowTemplateSimpleStatus.STATUS_CANCELLED:
						resposta.setError(true);
						resposta.setEstat(PortafirmesFluxEstat.CANCELLED);
						logger.error("L'usuari ha cancelat la construcció del flux");
						return resposta;
				case FlowTemplateSimpleStatus.STATUS_FINAL_OK:
						FlowTemplateSimpleFlowTemplate flux = result.getFlowInfo();

						resposta.setError(false);
						resposta.setEstat(PortafirmesFluxEstat.FINAL_OK);
						resposta.setFluxId(flux.getIntermediateServerFlowTemplateId());
						resposta.setNom(flux.getName());
						resposta.setDescripcio(flux.getDescription());
					break;
				default: {
					throw new Exception("Codi d'estat desconegut (" + status + ")");
				}
			}
		} catch (ApisIBClientException ex) {
			throw new SistemaExternException(
					"S'ha produït un error en el ConnectionManager del Client",
					ex);
		} catch (ApisIBServerException ex) {
			throw new SistemaExternException(
					"S'ha produït un error indeterminat al Servidor",
					ex);
		} catch (ApisIBTimeOutException ex) {
			throw new SistemaExternException(
					"Problemes de comunicació amb el servidor intermedi",
					ex);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error en el ConnectionManager del Client",
					ex);
		} finally {
			try {
				if (resposta.getFluxId() != null)
					closeTransaction(idTransaccio);
			} catch (Exception ex) {
				throw new SistemaExternException(
						"S'ha produït un error tancant la transacció",
						ex);
			}
		}
		return resposta;
	}

	@Override
	public List<PortafirmesFluxResposta> recuperarPlantillesDisponibles(String idioma) throws SistemaExternException {
		List<PortafirmesFluxResposta> plantilles = new ArrayList<PortafirmesFluxResposta>();
		try {
			FlowTemplateSimpleFlowTemplateList resposta = getFluxDeFirmaClient().getAllFlowTemplates(idioma);

			for (FlowTemplateSimpleKeyValue flowTemplate : resposta.getList()) {
				PortafirmesFluxResposta plantilla = new PortafirmesFluxResposta();
				plantilla.setFluxId(flowTemplate.getKey());
				plantilla.setNom(flowTemplate.getValue());
				plantilles.add(plantilla);
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut recuperar les plantilles per l'usuari aplicació actual",
					ex);
		}
		return plantilles;
	}

	@Override
	public PortafirmesFluxInfo recuperarFluxDeFirmaByIdPlantilla(String idPlantilla, String idioma)
			throws SistemaExternException {
		PortafirmesFluxInfo info = null;
		try {
			FlowTemplateSimpleFlowTemplateRequest request = new FlowTemplateSimpleFlowTemplateRequest(idioma, idPlantilla);

			FlowTemplateSimpleFlowTemplate result = getFluxDeFirmaClient().getFlowInfoByFlowTemplateID(request);

			if (result != null) {
				info = new PortafirmesFluxInfo();
				info.setNom(result.getName());
				info.setDescripcio(result.getDescription());
			}

			List<FlowTemplateSimpleBlock> blocks = result.getBlocks();

			for (FlowTemplateSimpleBlock block : blocks) {
				List<FlowTemplateSimpleSignature> signatures = block.getSignatures();

				for (FlowTemplateSimpleSignature signature: signatures) {
					PortafirmesFluxSigner signerFlux = new PortafirmesFluxSigner();
					UsuariPersonaBean detallSigner = null;

					FlowTemplateSimpleSigner signer = signature.getSigner();

					signerFlux.setObligat(signature.isRequired());

					if (signer.getIntermediateServerUsername() != null) {
						UsuariEntitatBean usuariEntitat = getUsuariEntitatWs().getUsuariEntitat(signer.getIntermediateServerUsername());
						detallSigner = getUsuariEntitatWs().getUsuariPersona(usuariEntitat.getUsuariPersonaID());

						signerFlux.setNom(detallSigner.getNom());
						signerFlux.setLlinatges(detallSigner.getLlinatges());
						signerFlux.setNif(detallSigner.getNif());

					} else if (signer.getPositionInTheCompany() != null) {
						CarrecWs carrecWs = getUsuariEntitatWs().getCarrec(signer.getPositionInTheCompany());
						detallSigner = getUsuariEntitatWs().getUsuariPersona(carrecWs.getUsuariPersonaID());

						String carrecNom = carrecWs.getCarrecName() + " (" + detallSigner.getNom() + " " + detallSigner.getLlinatges() + ")";
						signerFlux.setNom(carrecNom);
						signerFlux.setNif(detallSigner.getNif());
					}

					List<FlowTemplateSimpleReviser> revisers = signature.getRevisers();

					if (revisers != null) {
						for (FlowTemplateSimpleReviser reviser: revisers) {
							PortafirmesFluxReviser reviserFlux = new PortafirmesFluxReviser();

							reviserFlux.setObligat(reviser.isRequired());

							if (reviser.getIntermediateServerUsername() != null) {
								UsuariEntitatBean usuariEntitat = getUsuariEntitatWs().getUsuariEntitat(reviser.getIntermediateServerUsername());
								detallSigner = getUsuariEntitatWs().getUsuariPersona(usuariEntitat.getUsuariPersonaID());

								reviserFlux.setNom(detallSigner.getNom());
								reviserFlux.setLlinatges(detallSigner.getLlinatges());
								reviserFlux.setNif(detallSigner.getNif());
							} else if (reviser.getPositionInTheCompany() != null) {
								CarrecWs carrecWs = getUsuariEntitatWs().getCarrec(reviser.getPositionInTheCompany());
								detallSigner = getUsuariEntitatWs().getUsuariPersona(carrecWs.getUsuariPersonaID());

								String carrecNom = carrecWs.getCarrecName() + " (" + detallSigner.getNom() + detallSigner.getLlinatges() + ")";
								reviserFlux.setNom(carrecNom);
								reviserFlux.setNif(detallSigner.getNif());
							}
							signerFlux.getRevisors().add(reviserFlux);
						}
					}
					info.getSigners().add(signerFlux);
				}
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean esborrarPlantillaFirma(String idioma, String plantillaFluxId) throws SistemaExternException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void tancarTransaccioFlux(String idTransaccio) throws SistemaExternException {
		// TODO Auto-generated method stub

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

	@Override
	public List<PortafirmesCarrec> recuperarCarrecs() throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PortafirmesCarrec recuperarCarrec(String carrecId) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	private FirmaAsyncSimpleFile toFirmaAsyncSimpleFile(DocumentPortasignatures document) throws SistemaExternException {
		if (!"pdf".endsWith(document.getArxiuNom().toLowerCase())) {
			throw new SistemaExternException(
					"Els arxius per firmar han de ser de tipus PDF");
		}
		FirmaAsyncSimpleFile fitxer = new FirmaAsyncSimpleFile();
		fitxer.setNom(document.getArxiuNom());
		fitxer.setMime("application/pdf");
		fitxer.setData(document.getArxiuContingut());
		return fitxer;
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

	private ApiFlowTemplateSimple getFluxDeFirmaClient() throws MalformedURLException {
		String apiRestUrl = getUrlFirmaSimpleFlux();
		ApiFlowTemplateSimple api = new ApiFlowTemplateSimpleJersey(
				apiRestUrl,
				getUsernameFirmaSimpleFlux(),
				getPasswordFirmaSimpleFlux());
		return api;
	}

	private ApiFirmaAsyncSimple getFirmaAsyncSimpleApi() throws MalformedURLException {
		String apiRestUrl = getUrlFirmaSimpleAsync();
		ApiFirmaAsyncSimple api = new ApiFirmaAsyncSimpleJersey(
				apiRestUrl,
				getUsernameFirmaSimpleAsync(),
				getPasswordFirmaSimpleAsync());
		return api;
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
					"portafib=" + getUrlFirmaSimpleFlux() + ", " +
					"nom=" + nom + ", " +
					"descripcio=" + descripcio + ")",
					ex);
		}
		return transactionId;
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
					"portafib=" + getUrlFirmaSimpleFlux() + ", " +
					"transactionId=" + idTransaccio + ", " +
					"returnUrl=" + urlReturn + ")",
					ex);
		}
		return urlRedireccio;
	}

	private FlowTemplateSimpleGetFlowResultResponse getFlowTemplateResult(
			String transactionID) throws SistemaExternException {
		FlowTemplateSimpleGetFlowResultResponse result = null;

		try {
			result = getFluxDeFirmaClient().getFlowTemplateResult(transactionID);
		} catch (Exception ex) {
			throw new SistemaExternException("", ex);
		}
		return result;
	}

	private PortaFIBUsuariEntitatWs getUsuariEntitatWs() throws MalformedURLException {
		String webServiceUrl = getUrlUsuariEntitatWS();
		URL wsdlUrl = new URL(webServiceUrl + "?wsdl");
		PortaFIBUsuariEntitatWsService service = new PortaFIBUsuariEntitatWsService(wsdlUrl);
		PortaFIBUsuariEntitatWs api = service.getPortaFIBUsuariEntitatWs();
		BindingProvider bp = (BindingProvider)api;
		Map<String, Object> reqContext = bp.getRequestContext();
		reqContext.put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				webServiceUrl);
		reqContext.put(
				BindingProvider.USERNAME_PROPERTY,
				getUsernameUsuariEntitatWS());
		reqContext.put(
				BindingProvider.PASSWORD_PROPERTY,
				getPasswordUsuariEntitatWS());
		return api;
	}

	private void closeTransaction(
			String transactionID) throws SistemaExternException {
		try {
			getFluxDeFirmaClient().closeTransaction(transactionID);
		} catch (Exception ex) {
			throw new SistemaExternException("", ex);
		}
	}

	private String getUrlFirmaSimpleAsync() {
		return GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_PORTASIGNATURES_PLUGIN_PORTAFIB_URL);
	}
	private String getUsernameFirmaSimpleAsync() {
		return GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_PORTASIGNATURES_PLUGIN_PORTAFIB_USERNAME);
	}
	private String getPasswordFirmaSimpleAsync() {
		return GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_PORTASIGNATURES_PLUGIN_PORTAFIB_PASSWORD);
	}
	private String getUrlFirmaSimpleFlux() {
		return GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_PORTAFIRMES_PLUGIN_FLUX_FIRMA_URL);
	}
	private String getUsernameFirmaSimpleFlux() {
		return GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_PORTAFIRMES_PLUGIN_FLUX_FIRMA_USUARI);
	}
	private String getPasswordFirmaSimpleFlux() {
		return GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_PORTAFIRMES_PLUGIN_FLUX_FIRMA_PASSWORD);
	}
	private String getPerfil() {
		return GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_PORTASIGNATURES_PLUGIN_PORTAFIB_PERFIL);
	}
	private String getUrlUsuariEntitatWS() {
		return GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_PORTAFIRMES_PLUGIN_FLUX_ENTITATWS_URL);
	}
	private String getUsernameUsuariEntitatWS() {
		return GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_PORTAFIRMES_PLUGIN_FLUX_ENTITATWS_USERNAME);
	}
	private String getPasswordUsuariEntitatWS() {
		return GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_PORTAFIRMES_PLUGIN_FLUX_ENTITATWS_PASSWORD);
	}
	private static final Log logger = LogFactory.getLog(PortasignaturesAsyncPlugin.class);

}
