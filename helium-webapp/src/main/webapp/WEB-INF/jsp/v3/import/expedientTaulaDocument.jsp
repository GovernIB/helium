<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="document" value="${dada}"/>
<c:set var="psignaPendentActual" value="${null}" scope="request"/>
<c:forEach var="pendent" items="${portasignaturesPendent}">
	<c:if test="${pendent.documentStoreId == document.id}"><c:set var="psignaPendentActual" value="${pendent}" scope="request"/></c:if>
</c:forEach>
<td id="cela-${expedientId}-${document.id}">									
	<c:choose>
		<c:when test="${not empty document.error}">
			<table id="document_${document.id}" class="table-condensed marTop6 tableDocuments cellDocument">
				<thead>
					<tr>
						<td class="left">
							<a href="<c:url value="/v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/descarregar"/>">
								<span class="fa fa-file fa-4x no-doc" title="Descarregar document"></span>
								<span class="extensionIcon" title="${document.error}"><span class="fa fa-warning fa-lg"></span></span>
							</a>
						</td>
						<td class="right">
							
						</td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td colspan="2">
						</td>
					</tr>
				</tbody>
			</table>
		</c:when>
		<c:otherwise>
			<table id="document_${document.id}" class="table-condensed marTop6 tableDocuments cellDocument hiddenInfoContainer">
				<thead>
					<tr>
						<td class="left">
							<a href="<c:url value="/v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/descarregar"/>">
								<span class="fa fa-file fa-4x" title="Descarregar document"></span>
								<c:if test="${document.adjunt}">
									<span class="adjuntIcon icon fa fa-paperclip fa-2x"></span>
								</c:if>
								<%--c:if test="${document.arxiuActiu and document.signat}">
									<span class="adjuntIcon icon fa fa-certificate fa-2x"></span>
								</c:if--%>
								<span class="extensionIcon">
									${fn:toUpperCase(document.arxiuExtensio)}
								</span>
							</a>
						</td>
						<td class="right">
							<c:if test="${not empty document.id}">
								<table class="marTop6 tableDocuments">
									<thead>
										<tr>
											<td class="tableDocumentsTd">
												<c:if test="${!document.signat && expedient.permisDocManagement}">
													<a 	href="../../v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/update"
														data-rdt-link-modal="true" 
														data-rdt-link-modal-min-height="265" 
														data-rdt-link-callback="recargarPanel(${document.processInstanceId});"
														class="icon modificar" >
															<span class="fa fa-2x fa-pencil" title="<spring:message code='expedient.document.modificar' />"></span>
													</a>
												</c:if>
												
												<c:if test="${document.notificable}">
													<a 	href="../../v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/notificar"
														data-rdt-link-modal="true" 
														data-rdt-link-modal-min-height="265" 
														data-rdt-link-callback="recargarPanel(${document.processInstanceId});"
														class="icon modificar" 
														data-rdt-link-modal-maximize="true">
															<span class="fa fa-2x fa-paper-plane" title="<spring:message code='expedient.document.notificar' />"></span>
													</a>
												</c:if>												

												<c:if test="${document.signat}">
													<c:choose>
														<c:when test="${not document.arxiuActiu}">
															<c:choose>
																<c:when test="${not empty document.signaturaUrlVerificacio}">
																	<a class="icon signature" href="${document.signaturaUrlVerificacio}" target="_blank">
																		<span class="fa fa-2x fa-certificate" title="<spring:message code="expedient.document.signat"/>"></span>
																	</a>
																</c:when>
																<c:otherwise>																			
																	<a 	data-rdt-link-modal="true"
																		class="icon signature" 
																		href="<c:url value="../../v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/signatura/verificar"/>?urlVerificacioCustodia=${document.signaturaUrlVerificacio}">
																		<span class="fa fa-2x fa-certificate" title="<spring:message code='expedient.document.signat' />"></span>
																	</a>
																</c:otherwise>
															</c:choose>
															<c:if test="${expedient.permisDocManagement}">
																<a 	class="icon signature fa-stack fa-2x" 
																	data-rdt-link-confirm="<spring:message code='expedient.document.confirm_esborrar_signatures' />"
																	data-rdt-link-ajax=true
																	href='<c:url value="../../v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/signatura/esborrar"/>' 
																	data-rdt-link-callback="esborrarSignatura(${document.id});"
																	title="<spring:message code='expedient.document.esborrar.signatures' />">
																	<i class="fa fa-certificate fa-stack-1x"></i>
																  	<i class="fa fa-ban fa-stack-2x text-danger"></i>
																</a>
															</c:if>
														</c:when>
														<c:otherwise>
															<!--  Document signat a l'Arxiu -->
															<c:choose>
																<c:when test="${document.ntiCsv != null}">
																	<!--  Url del ConCSV -->
																	<a class="icon signature" href="${document.signaturaUrlVerificacio}" target="_blank">
																		<span class="fa fa-2x fa-certificate" title="<spring:message code="expedient.document.signat"/>"></span>
																	</a>
																</c:when>
																<c:otherwise>
																	<!-- S'ha de consultar el CSV -->																	
																	<a class="icon signature" href="<c:url value="../../v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/signatura/verificarCsv"></c:url>" target="_blank">
																		<span class="fa fa-2x fa-certificate" title="<spring:message code="expedient.document.signat"/>"></span>
																	</a>
																</c:otherwise>
															</c:choose>
														</c:otherwise>
													</c:choose>
												</c:if>
												<c:if test="${document.registrat}">
													<a 	data-rdt-link-modal="true" 
														class="icon registre" 
														href="<c:url value='../../v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/registre/verificar'/>">
														<span class="fa fa-book fa-2x" title="<spring:message code='expedient.document.registrat' />"></span>
													</a>
												</c:if>
												<c:if test="${expedient.permisDocManagement and (not document.signat or (document.signat and not document.arxiuActiu))}">
													<a 	class="icon fa fa-trash-o fa-2x" 
														data-rdt-link-confirm="<spring:message code='expedient.document.confirm_esborrar_proces' />"
														data-rdt-link-ajax=true
														href='<c:url value="../../v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/esborrar"/>' 
														data-rdt-link-callback="recargarPanel(${document.processInstanceId});"
														title="<spring:message code='expedient.document.esborrar'/>">
													</a>
												</c:if>
												<c:if test="${!document.documentValid}">
													<span class="fa fa-exclamation-triangle fa-2x text-danger" title="<spring:message htmlEscape="true" code="expedient.document.invalid" arguments="${document.documentError}"/>""></span>
												</c:if>
												
												<!-- Enllaç per obrir la modal d'informació del portasignatures -->
												<c:if test="${not empty psignaPendentActual}">
													<c:choose>
														<c:when test="${psignaPendentActual.error}">
															<c:set var="iconPendentSignaturaBtn" value="fa-exclamation-triangle"/>
															<c:choose>
																<c:when test="${psignaPendentActual.rebutjadaProcessada}">
																	<c:set var="titlePendentSignaturaBtn" value="expedient.document.rebutjat.psigna.error"/>
																</c:when>
																<c:otherwise>
																	<c:set var="titlePendentSignaturaBtn" value="expedient.document.pendent.psigna.error"/>
																</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
 																<c:set var="titlePendentSignaturaBtn" value="expedient.document.pendent.psigna"/>
 																<c:set var="iconPendentSignaturaBtn" value="fa fa-clock-o"/>
														</c:otherwise>
													</c:choose>
													<a 	href="../../v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/pendentSignatura"
																data-rdt-link-modal="true" 
																data-rdt-link-modal-maximize="false"
																data-rdt-link-modal-min-height="400" 
																data-rdt-link-callback="recargarPanel(${document.processInstanceId});"
																class="icon enviarPortasignatures">
																<span class="icon fa ${iconPendentSignaturaBtn} fa-2x psigna-info" title="<spring:message code='${titlePendentSignaturaBtn}' />"></span>
													</a>
												</c:if>
												
												<c:if test="${!document.signat && expedient.permisDocManagement && (empty psignaPendentActual || psignaPendentActual.rebutjadaProcessada) && document.portafirmesActiu && (! empty expedient.arxiuUuid  || ! empty document.custodiaCodi)}">
													<a 	href="../../v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/enviarPortasignatures"
														data-rdt-link-modal="true" 
														data-rdt-link-modal-maximize="false"
														data-rdt-link-modal-min-height="400" 
														data-rdt-link-callback="recargarPanel(${document.processInstanceId});"
														class="icon enviarPortasignatures">
															<span class="fa fa-2x fa-envelope-o" title="<spring:message code='expedient.document.enviar.portasignatures' />"></span>
													</a>
												</c:if>
												<c:if test="${!document.signat && expedient.permisDocManagement && empty psignaPendentActual && (! empty expedient.arxiuUuid || ! empty document.custodiaCodi)}">
													<a 	href="../../v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/firmaPassarela"
														data-rdt-link-modal="true" 
														data-rdt-link-modal-min-height="400" 
														data-rdt-link-callback="recargarPanel(${document.processInstanceId});"
														class="icon firmaPassarela hiddenInfo">
															<span class="fa fa-2x fa-pencil-square-o" title="<spring:message code='expedient.document.firmaPassarela' />"></span>
													</a>
												</c:if>
												<!-- FI FRAGMENT -->
												<c:if test="${expedient.ntiActiu and expedient.permisRead}">
													<a	href="../../v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/metadadesNti"
														data-rdt-link-modal="true"
														data-rdt-link-modal-min-height="500"
														class="linkNti">
														<span class="label label-info etiqueta-nti-arxiu">
															<c:choose>
																<c:when test="${empty expedient.arxiuUuid}">
																	<spring:message code="expedient.info.etiqueta.nti"/>
																</c:when>
																<c:otherwise>
																	<spring:message code="expedient.info.etiqueta.arxiu"/>
																	<c:if test="${empty document.arxiuUuid}"> 
																		<span class="fa fa-warning text-danger" title="<spring:message code='expedient.document.arxiu.error.uuidnoexistent' />"></span>
																	</c:if>
																</c:otherwise>
															</c:choose>
														</span>
													</a>
												</c:if>
												<c:if test="${document.notificat}">
													<span class="label label-warning etiqueta-nti-arxiu"><spring:message code="expedient.document.info.etiqueta.notificat"/></span>
												</c:if>
												<c:if test="${document.anotacioId != null}">
													<a href="<c:url value="../../v3/anotacio/${document.anotacioId}?annexId=${document.anotacioAnnexId}"/>"
														title="<spring:message code="expedient.document.info.etiqueta.anotacio.title" arguments="${document.anotacioIdentificador},${document.anotacioAnnexTitol}" htmlEscape="true"/>"
														data-rdt-link-modal="true"
														data-rdt-link-modal="true"
														data-rdt-link-modal-maximize="true"
														class="linkNti">
														<span class="label label-warning etiqueta-nti-arxiu"><spring:message code="expedient.document.info.etiqueta.anotacio"/></span>
													</a>													
												</c:if>
												
											</td>
										</tr>
										<tr>
											<td>
												<spring:message code='expedient.document.data' /> <fmt:formatDate value="${document.dataDocument}" pattern="dd/MM/yyyy"/>
											</td>
										</tr>
										<c:if test="${not empty document.dataCreacio}">
											<tr>
												<td>
													<spring:message code='expedient.document.adjuntat' /> <fmt:formatDate value="${document.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/>
												</td>
											</tr>
										</c:if>
									</thead>
								</table>
							</c:if>
						</td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td colspan="2">
							<strong class="nom_document">
								<c:choose>
									<c:when test="${not document.adjunt}">${document.documentNom}</c:when>
									<c:otherwise>${document.adjuntTitol}</c:otherwise>
								</c:choose>
							</strong>
							<br/>
						</td>
					</tr>
				</tbody>
			</table>
		</c:otherwise>
	</c:choose>
</td>
