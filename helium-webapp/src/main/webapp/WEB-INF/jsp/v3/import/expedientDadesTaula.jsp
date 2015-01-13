<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<style>
	a, a:HOVER, a:FOCUS {text-decoration: none;}
	div.grup:hover {background-color: #e5e5e5 !important;border-color: #ccc !important;}
	div.grup .panel-body-grup {padding-bottom: 0px !important;}
	.panel-body-grup {margin: -1px;}
	.extensionIcon {color: white;font-size: 10px;font-weight: bold;margin-left: -38px;}
	.adjuntIcon {color: white !important; float: left; font-size: 20px; margin-left: 7px; margin-top: -51px; position: relative;}
	.signature {margin-top: -5px;}
	.table.table-bordered {margin-bottom: 0px !important;}
	.tableDocuments .left {padding-left: 10px;padding-right: 10px;}
	.tableDocuments .right {padding-left: 3px;padding-right: 10px;width: 100%;}
	.tableDocumentsTd {font-size: 10px;}
	.nom_document {padding-left: 5px;}
	.icon.modificar {padding-left: 5px;padding-right: 5px;}
	.icon.registre {padding-right: 5px;}
	.icon, .icon:hover, .icon:focus {text-decoration: none;color: #2a6496;}
	.icon {color: #428bca;}
	.icon {background: none repeat scroll 0 0 rgba(0, 0, 0, 0);}
	.fa-stack-2x {font-size: 1.7em;margin-top: 2px;}
	.fa.fa-certificate.fa-stack-1x { margin-top: -1px;}
</style>

<c:set var="grupId" value="grup-default"/>
<c:if test="${not empty param.id}"><c:set var="grupId" value="grup-${param.id}"/></c:if>
<c:set var="paramDades" value="${requestScope[param.dadesAttribute]}"/>
<c:set var="paramCount" value="${param.count}"/>
<c:if test="${not empty param.titol}"><c:set var="paramTitol" value="${param.titol}"/></c:if>
<c:set var="paramNumColumnes" value="${3}"/>
<c:if test="${not empty param.numColumnes}"><c:set var="paramNumColumnes" value="${param.numColumnes}"/></c:if>
<c:set var="paramDesplegat" value="${true}"/>
<c:if test="${not empty param.desplegat}"><c:set var="paramDesplegat" value="${param.desplegat}"/></c:if>
<c:set var="paramMostrarCapsalera" value="${true}"/>
<c:if test="${not empty param.mostrarCapsalera}"><c:set var="paramMostrarCapsalera" value="${param.mostrarCapsalera}"/></c:if>
<c:if test="${not empty param.condicioCamp}"><c:set var="paramCondicioCamp" value="${param.condicioCamp}"/></c:if>
<c:if test="${not empty param.condicioValor}"><c:set var="paramCondicioValor" value="${param.condicioValor}"/></c:if>
<c:if test="${not empty param.condicioEmpty}"><c:set var="paramCondicioEmpty" value="${param.condicioEmpty}"/></c:if>
<c:if test="${not empty param.desplegadorClass}"><c:set var="paramDesplegadorClass" value="${param.desplegadorClass}"/></c:if>

<div class="panel panel-default">
	<c:if test="${paramMostrarCapsalera}">
		<div id="${grupId}-titol" class="panel-heading clicable grup tauladades" data-toggle="collapse" data-target="#${grupId}-dades">
			${paramTitol}
			<c:if test="${not empty paramCount}"><span class="badge">${paramCount}</span></c:if>
			<div class="pull-right"><span class="icona-collapse fa fa-chevron-<c:if test="${paramDesplegat}">up</c:if><c:if test="${not paramDesplegat}">down</c:if>"></span></div>
		</div>
	</c:if>
	<div id="${grupId}-dades" class="clear collapse panel-body-grup<c:if test="${paramDesplegat}"> in</c:if>">
		<table class="table table-bordered">
			<colgroup>
				<c:forEach begin="0" end="${paramNumColumnes - 1}">
					<col width="${100 / paramNumColumnes}%"/>
				</c:forEach>
			</colgroup>
			<tbody>
				<c:set var="index" value="${0}"/>
				<c:set var="posicioOffset" value="${0}"/>
				<c:forEach var="dada" items="${paramDades}">
					<c:set var="posicioActual" value="${(index + posicioOffset) % paramNumColumnes}"/>
					<c:set var="condicioValor" value="${true}"/>
					<c:choose>
						<c:when test="${not empty paramCondicioCamp and not empty paramCondicioEmpty}">
							<c:set var="condicioValor" value="${empty dada[paramCondicioCamp]}"/>
						</c:when>
						<c:when test="${not empty paramCondicioCamp and empty paramCondicioValor}">
							<c:set var="condicioValor" value="${dada[paramCondicioCamp]}"/>
						</c:when>
						<c:when test="${not empty paramCondicioCamp and not empty paramCondicioValor}">
							<c:set var="condicioValor" value="${dada[paramCondicioCamp] == paramCondicioValor}"/>
						</c:when>
					</c:choose>
					<c:if test="${condicioValor}">
						<c:if test="${posicioActual == 0}"><tr></c:if>
						<c:set var="dadaTipusRegistre" value="${false}"/>
						<c:if test="${fn:endsWith(dada.class.name, 'DadaDto')}">
							<c:set var="dadaTipusRegistre" value="${dada.campTipusRegistre}"/>
						</c:if>
						<c:if test="${dadaTipusRegistre}">
							<c:if test="${posicioActual > 0}"><td colspan="${paramNumColumnes - posicioActual}">&nbsp;</td></tr><tr></c:if>
							<c:set var="posicioOffset" value="${posicioOffset + (paramNumColumnes - posicioActual) - 1}"/>
							<c:set var="posicioActual" value="${0}"/>
						</c:if>
						<c:choose>
							<c:when test="${fn:endsWith(dada.class.name, 'DadaDto')}">
								<td id="cela-${procesId}-${dada.varCodi}"<c:if test="${dadaTipusRegistre}"> colspan="${paramNumColumnes}"</c:if><c:if test="${dada.campOcult}"> class="campOcult"</c:if><c:if test="${not empty dada.error}"> style="background-color:#f2dede"</c:if>>
									<address>
										${dada.campEtiqueta}<br/>
										<c:if test="${not empty dada.varValor}">
											<c:choose>
												<c:when test="${dadaTipusRegistre}">
													<c:set var="registreFilesTaula" value="${dada.dadesRegistrePerTaula}"/>
													<table class="table table-bordered table-condensed marTop6">
														<thead>
															<tr>
																<c:forEach var="dadaFila0" items="${registreFilesTaula[0].registreDades}">
																	<th>${dadaFila0.campEtiqueta}</th>
																</c:forEach>
															</tr>
														</thead>
														<tbody>
															<c:forEach var="registreFila" items="${registreFilesTaula}">
																<tr>
																	<c:forEach var="registreDada" items="${registreFila.registreDades}">
																		<td>${registreDada.text}</td>
																	</c:forEach>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</c:when>
												<c:otherwise>
													<c:choose>
														<c:when test="${not empty dada.error}"><strong><i class="icon-warning-sign" title="${dada.error}"></i></strong></c:when>
														<c:otherwise><strong>${dada.text}</strong></c:otherwise>
													</c:choose>
												</c:otherwise>
											</c:choose>
										</c:if>
									</address>
								</td>
							</c:when>
							<c:when test="${fn:endsWith(dada.class.name, 'DocumentDto')}">
								<c:set var="document" value="${dada}"/>
								<td id="cela-${expedientId}-${document.id}">									
									<c:choose>
										<c:when test="${not empty document.error}">
											<span class="fa fa-warning fa-2x" title="${document.error}"></span>
										</c:when>
										<c:otherwise>
											<table id="document_${document.id}" class="table-condensed marTop6 tableDocuments">
												<thead>
													<tr>
														<td class="left">
															<a href="<c:url value="/v3/expedient/${expedientId}/document/${document.id}/descarregar"/>" title="Descarregar document">
																<span class="fa fa-file fa-4x"></span>
																<span class="extensionIcon">
																	${fn:toUpperCase(document.arxiuExtensio)}
																</span>
																<c:if test="${document.adjunt}">
																	<span class="adjuntIcon icon fa fa-paperclip fa-2x"></span>
																</c:if>
															</a>
														</td>
														<td class="right">
															<c:if test="${not empty document.id}">
																<table class="marTop6 tableDocuments">
																	<thead>
																		<tr>
																			<td class="tableDocumentsTd">
																				<c:if test="${!document.signat}">
																					<a 	data-rdt-link-modal="true" 
																						data-rdt-link-modal-min-height="190" 
																						data-rdt-link-callback="recargarPanel(${document.processInstanceId});"
																						class="icon modificar" 
																						href="<c:url value='../../v3/expedient/${expedientId}/documentModificar/${document.id}/${document.documentCodi}'/>">
																						<span class="fa fa-2x fa-pencil" title="<spring:message code='expedient.document.modificar' />"></span>
																					</a>
																				</c:if>
																				
																				<c:if test="${document.signat}">																					
																					<a 	data-rdt-link-modal="true" 
																						<c:if test="${not empty document.urlVerificacioCustodia}">data-rdt-link-modal-min-height="400"</c:if>
																						class="icon signature" 
																						href="<c:url value='../../v3/expedient/${expedientId}/verificarSignatura/${document.id}/${document.documentCodi}'/>?urlVerificacioCustodia=${document.urlVerificacioCustodia}">
																						<span class="fa fa-2x fa-certificate" title="<spring:message code='expedient.document.signat' />"></span>
																					</a>
																					<a 	class="icon signature fa-stack fa-2x" 
																						data-rdt-link-confirm="<spring:message code='expedient.document.confirm_esborrar_signatures' />"
																						data-rdt-link-ajax=true
																						href='<c:url value="../../v3/expedient/${expedientId}/signaturaEsborrar/${document.id}"/>' 
																						data-rdt-link-callback="esborrarSignatura(${document.id});">
																						<i class="fa fa-certificate fa-stack-1x"></i>
																					  	<i class="fa fa-ban fa-stack-2x text-danger"></i>
																					</a>
																				</c:if>
																				
																				<c:if test="${document.registrat}">
																					<a 	data-rdt-link-modal="true" 
																						class="icon registre" 
																						href="<c:url value='../../v3/expedient/${expedientId}/verificarRegistre/${document.id}/${document.documentCodi}'/>">
																						<span class="fa fa-book fa-2x" title="<spring:message code='expedient.document.registrat' />"></span>
																					</a>
																				</c:if>
																				
																				<a 	class="icon fa fa-trash-o fa-2x" 
																					data-rdt-link-confirm="<spring:message code='expedient.document.confirm_esborrar_proces' />"
																					data-rdt-link-ajax=true
																					href='<c:url value="../../v3/expedient/${expedientId}/documentEsborrar/${document.id}/${document.documentCodi}"/>' 
																					data-rdt-link-callback="recargarPanel(${document.processInstanceId});">
																				</a>																				
																				
																				<%--
																				<c:if test="${not empty psignaPendentActual}">
																					<c:choose>
																						<c:when test="${psignaPendentActual.error}"><img src="<c:url value="/img/exclamation.png"/>" alt="<fmt:message key="expedient.document.pendent.psigna.error"/>" title="<fmt:message key="expedient.document.pendent.psigna.error"/>" border="0" style="cursor:pointer" onclick="infoPsigna(${documentActual.id})"/></c:when>
																						<c:otherwise><img src="<c:url value="/img/clock_red.png"/>" alt="<fmt:message key="expedient.document.pendent.psigna"/>" title="<fmt:message key="expedient.document.pendent.psigna"/>" border="0" style="cursor:pointer" onclick="infoPsigna(${documentActual.id})"/></c:otherwise>
																					</c:choose>
																					<div id="psigna_${documentActual.id}" style="display:none">
																						<dl class="form-info">
																							<dt><fmt:message key="common.icones.doc.psigna.id"/></dt><dd>${psignaPendentActual.documentId}&nbsp;</dd>
																							<dt><fmt:message key="common.icones.doc.psigna.data.enviat"/></dt><dd><fmt:formatDate value="${psignaPendentActual.dataEnviat}" pattern="dd/MM/yyyy HH:mm"/>&nbsp;</dd>
																							<dt><fmt:message key="common.icones.doc.psigna.estat"/></dt><dd>${psignaPendentActual.estat}&nbsp;</dd>
																							<c:if test="${not empty psignaPendentActual.dataProcesPrimer}">
																								<dt><fmt:message key="common.icones.doc.psigna.data.proces.primer"/></dt><dd><fmt:formatDate value="${psignaPendentActual.dataProcesPrimer}" pattern="dd/MM/yyyy HH:mm"/>&nbsp;</dd>
																							</c:if>
																							<c:if test="${not empty psignaPendentActual.dataProcesDarrer}">
																								<dt><fmt:message key="common.icones.doc.psigna.data.proces.darrer"/></dt><dd><fmt:formatDate value="${psignaPendentActual.dataProcesDarrer}" pattern="dd/MM/yyyy HH:mm"/>&nbsp;</dd>
																							</c:if>
																							<c:if test="${psignaPendentActual.error}">
																								<dt><fmt:message key="common.icones.doc.psigna.error.processant"/></dt><dd>${psignaPendentActual.errorProcessant}&nbsp;</dd>
																								<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
																									<form action="<c:url value="/expedient/documentPsignaReintentar.html"/>">
																										<input type="hidden" name="id" value="${instanciaProces.id}"/>
																										<input type="hidden" name="psignaId" value="${psignaPendentActual.documentId}"/>
																										<button class="submitButtonImage" type="submit">
																											<span class="nova-variable"></span><fmt:message key="common.icones.doc.psigna.reintentar"/>
																										</button>
																									</form>
																								</security:accesscontrollist>
																							</c:if>
																						</dl>
																					</div>
																				</c:if>
																				 --%>
																				
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<fmt:formatDate value="${document.dataDocument}" pattern="dd/MM/yyyy"/>
																			</td>
																		</tr>
																	</thead>
																</table>
															</c:if>
														</td>
													</tr>
												</thead>
												<tbody>
													<tr>
														<td colspan="2">
															<strong class="nom_document">${document.documentNom}</strong><br/>
														</td>
													</tr>
												</tbody>
											</table>
										</c:otherwise>
									</c:choose>
								</td>
							</c:when>
							<c:when test="${fn:endsWith(dada.class.name, 'TerminiDto')}">
								<c:set var="termini" value="${dada}"/>
								<c:if test="${index == 0}">
								<table id="termini_${termini.id}" class="table tableTerminis table-bordered">
									<thead>
										<tr>
											<th><spring:message code="expedient.termini.nom"/></th>
											<th><spring:message code="expedient.termini.durada"/></th>
											<th><spring:message code="expedient.termini.iniciat.el"/></th>
											<th><spring:message code="expedient.termini.aturat.el"/></th>
											<th><spring:message code="expedient.termini.data.de.fi"/></th>
											<th><spring:message code="expedient.termini.estat"/></th>
											<th></th>
										</tr>
									</thead>
									<tbody>
								</c:if>
										<c:set var="iniciat" value=""/>
										<c:forEach var="ini" items="${iniciats_termini}">
											<c:if test="${termini.id == ini.termini.id and empty inidataCancelacio}">
												<c:set var="iniciat" value="${ini}"/>
											</c:if>
										</c:forEach>
										<tr>
											<td>${termini.nom}</td>
											<td>
												<c:choose>
													<c:when test="${not empty iniciat}">${iniciat.durada}</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${termini.duradaPredefinida}">${termini.durada}</c:when>
															<c:otherwise>Sense especificar</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</td>
											<td>
												<c:if test="${not empty iniciat}"><fmt:formatDate value="${iniciat.dataInici}" pattern="dd/MM/yyyy"/></c:if>
											</td>
											<td>
												<c:if test="${not empty iniciat and not empty iniciat.dataAturada}"><fmt:formatDate value="${iniciat.dataAturada}" pattern="dd/MM/yyyy"/></c:if>
											</td>
											<td>
												<c:if test="${not empty iniciat}"><fmt:formatDate value="${iniciat.dataFi}" pattern="dd/MM/yyyy"/></c:if>
											</td>
											<td>
												<c:choose>
													<c:when test="${empty iniciat}">
														<c:set var="trobat" value="${false}"/>
														<c:forEach var="ini" items="${iniciats.value}">
															<c:if test="${termini.id == ini.termini.id and not empty ini.dataCancelacio}">
																<spring:message code="expedient.termini.estat.cancelat"/>
																<c:set var="trobat" value="${true}"/>
															</c:if>
														</c:forEach>
														<c:if test="${not trobat}"><spring:message code="expedient.termini.estat.pendent"/></c:if>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${not empty iniciat.dataAturada}"><spring:message code="expedient.termini.estat.aturat"/></c:when>
															<c:otherwise><spring:message code="expedient.termini.estat.actiu"/></c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</td>
											<td class="termini_options">
												<c:choose>
													<c:when test="${not termini.manual or not empty iniciat}">
														<c:choose>
															<c:when test="${not termini.manual or empty iniciat.dataAturada}">
																<i class="fa fa-play" alt="<spring:message code="expedient.termini.accio.iniciar"/>" title="<spring:message code="expedient.termini.accio.iniciar"/>" border="0"/>
															</c:when>
															<c:otherwise>
																<a  class="icon" 
																	data-rdt-link-confirm="<spring:message code="expedient.termini.confirmar.continuar" />"
																	data-rdt-link-ajax=true
																	href='<c:url value="../../v3/expedient/${expedientId}/${iniciat.id}/terminiContinuar"/>' 
																	data-rdt-link-callback="recargarPanelTermini(${procesId});">
																	<i class="fa fa-play" alt="<spring:message code="expedient.termini.accio.continuar"/>" title="<spring:message code="expedient.termini.accio.continuar"/>" border="0"/>
																</a>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<a  class="icon" 
															data-rdt-link-confirm="<spring:message code="expedient.termini.confirmar.iniciar" />"
															data-rdt-link-ajax=true
															href='<c:url value="../../v3/expedient/${expedientId}/${iniciat.id}/terminiIniciar"/>' 
															data-rdt-link-callback="recargarPanelTermini(${procesId});">
															<i class="fa fa-play" alt="<spring:message code="expedient.termini.accio.iniciar"/>" title="<spring:message code="expedient.termini.accio.iniciar"/>" border="0"/>
														</a>
													</c:otherwise>
												</c:choose>
												
												<c:choose>
													<c:when test="${not termini.manual or empty iniciat or not empty iniciat.dataAturada}">
														<i class="fa fa-pause" alt="<spring:message code="expedient.termini.accio.aturar"/>" title="<spring:message code="expedient.termini.accio.aturar"/>" border="0"/>
													</c:when>
													<c:otherwise>
														<a  class="icon" 
															data-rdt-link-confirm="<spring:message code="expedient.termini.confirmar.aturar" />"
															data-rdt-link-ajax=true
															href='<c:url value="../../v3/expedient/${expedientId}/${iniciat.id}/terminiPausar"/>' 
															data-rdt-link-callback="recargarPanelTermini(${procesId});">
															<i class="fa fa-pause" alt="<spring:message code="expedient.termini.accio.aturar"/>" title="<spring:message code="expedient.termini.accio.aturar"/>" border="0"/>
														</a>
													</c:otherwise>
												</c:choose>
												
												<c:choose>
													<c:when test="${empty iniciat}">
														<i class="fa fa-stop" alt="<spring:message code="expedient.termini.accio.cancelar"/>" title="<spring:message code="expedient.termini.accio.cancelar"/>" border="0"/>
													</c:when>
													<c:otherwise>
														<a  class="icon" 
															data-rdt-link-confirm="<spring:message code='expedient.termini.confirmar.cancelar' />"
															data-rdt-link-ajax=true
															href='<c:url value="../../v3/expedient/${expedientId}/${iniciat.id}/terminiCancelar"/>' 
															data-rdt-link-callback="recargarPanelTermini(${procesId});">
															<i class="fa fa-stop" alt="<spring:message code="expedient.termini.accio.cancelar"/>" title="<spring:message code="expedient.termini.accio.cancelar"/>" border="0"/>
														</a>
													</c:otherwise>
												</c:choose>
												
												<c:if test="${not empty iniciat}">
													<a class="icon" 
														data-rdt-link-callback="recargarPanelTermini(${procesId});" 
														data-rdt-link-modal="true" 
														href="<c:url value="../../v3/expedient/${expedientId}/${iniciat.id}/terminiModificar"/>"><i class="fa fa-pencil-square-o" alt="<spring:message code="expedient.termini.accio.modificar"/>" title="<spring:message code="expedient.termini.accio.modificar"/>" border="0"/></a>
												</c:if>
											</td>
										</tr>
										
								<c:if test="${index == (paramCount-1)}">
									</tbody>
								</table>
								</c:if>
							</c:when>
							<c:otherwise><td>[Tipus desconegut]</td></c:otherwise>
						</c:choose>
						<c:if test="${(index == paramCount - 1) and posicioActual != (paramNumColumnes - 1) and not dadaTipusRegistre}"><td colspan="${paramNumColumnes - posicioActual - 1}">&nbsp;</td></c:if>
						<c:if test="${(index == paramCount - 1) or dadaTipusRegistre or (index != 0 and posicioActual == (paramNumColumnes - 1))}"></tr></c:if>
						<c:set var="index" value="${index + 1}"/>
					</c:if>
				</c:forEach>
			</tbody>
		</table>				
	</div>
	<div class="clear"></div>
</div>

<script type="text/javascript">
// <![CDATA[			
$(document).ready(function() {
	$('#${grupId}-dades').on('shown.bs.collapse', function() {
		$('#${grupId}-titol .icona-collapse').toggleClass('fa-chevron-down');
		$('#${grupId}-titol .icona-collapse').toggleClass('fa-chevron-up');
	});
	$('#${grupId}-dades').on('hidden.bs.collapse', function() {
		$('#${grupId}-titol .icona-collapse').toggleClass('fa-chevron-down');
		$('#${grupId}-titol .icona-collapse').toggleClass('fa-chevron-up');
	});
	$('#${grupId}-dades address').find('a').attr('target', 'BLANK');
});
//]]>
</script>
