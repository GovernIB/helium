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
	.panel {width: calc(100% - 15px);}
	.campOcult {background-color: lightgray;color: white;}
	.var_dades {float: left;width:calc(100% - 15px);}
	.var_botons {float: right;color: #428bca;}
	.var_botons span {display: block;padding-top: 5px;}
	.var_botons span:hover {color: #3071a9}
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
<c:if test="${not empty param.procesId}"><c:set var="procesId" value="${param.procesId}"/></c:if>


<div class="panel panel-default">
	<c:choose>
		<c:when test="${not empty paramCount and ambOcults}">
			<c:set var="nombre" value="${paramCount}"/>
		</c:when>
		<c:otherwise>
			<c:set var="nombre" value="${0}"/>
			<c:forEach var="dada" items="${paramDades}">
				<c:set var="mostrardada" value="true"/>
				<c:if test="${fn:endsWith(dada.class.name, 'DadaDto')}">
					<c:set var="mostrardada" value="${not dada.campOcult or ambOcults}"/>
				</c:if>
				<c:if test="${mostrardada}"><c:set var="nombre" value="${nombre + 1}"/></c:if>
			</c:forEach>
		</c:otherwise>
	</c:choose>
	<c:if test="${paramMostrarCapsalera}">
		<div id="${grupId}-titol" class="panel-heading clicable grup tauladades" data-toggle="collapse" data-target="#${grupId}-dades">
			${paramTitol}
			<span class="badge">${nombre}</span>
			<div class="pull-right"><span class="icona-collapse fa fa-chevron-<c:if test="${paramDesplegat}">up</c:if><c:if test="${not paramDesplegat}">down</c:if>"></span></div>
		</div>
	</c:if>
	<c:if test="${not paramMostrarCapsalera}">
		<span class="badge-nombre" data-nombre="${nombre}"></span>
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
					<c:set var="mostrardada" value="true"/>
					<c:if test="${fn:endsWith(dada.class.name, 'DadaDto')}">
						<c:set var="mostrardada" value="${not dada.campOcult or (dada.campOcult and ambOcults)}"/>
					</c:if>
					<c:if test="${mostrardada}">
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
										<address class=var_dades>
											<c:if test="${dada.campOcult}"><span class="fa fa-eye-slash"></span></c:if>
											${dada.campEtiqueta}<br/>
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
													<c:if test="${not empty dada.varValor}">
														<c:choose>
															<c:when test="${not empty dada.error}"><strong><i class="icon-warning-sign" title="${dada.error}"></i></strong></c:when>
															<c:otherwise><strong>${dada.textMultiple}</strong></c:otherwise>
														</c:choose>
													</c:if>
												</c:otherwise>
											</c:choose>
										</address>
										<c:if test="${isAdmin}">
										<div class=var_botons>
											<a 	class="var-edit" 
												data-rdt-link-modal=true
												href='<c:url value="../../v3/expedient/${expedientId}/dades/${procesId}/edit/${dada.varCodi}"/>' 
												data-rdt-link-callback="reestructura(${procesId});"
												data-rdt-link-modal-min-height="300">
												<span class="fa fa-pencil"></span>
											</a>
											<a 	class="var-delete" 
												data-rdt-link-confirm="<spring:message code='expedient.info.confirm.dada.esborrar'/>"
												data-rdt-link-ajax=true
												href='<c:url value="../../v3/expedient/${expedientId}/dades/${procesId}/delete/${dada.varCodi}"/>' 
												data-rdt-link-callback="reestructura(${procesId});">
												<span class="fa fa-trash-o"></span>
											</a>
										</div>
										</c:if>
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
								<c:otherwise>[Tipus desconegut]</c:otherwise>
							</c:choose>
							<c:if test="${(index == paramCount - 1) and posicioActual != (paramNumColumnes - 1) and not dadaTipusRegistre}"><td colspan="${paramNumColumnes - posicioActual - 1}">&nbsp;</td></c:if>
							<c:if test="${(index == paramCount - 1) or dadaTipusRegistre or (index != 0 and posicioActual == (paramNumColumnes - 1))}"></tr></c:if>
							<c:set var="index" value="${index + 1}"/>
						</c:if>
					</c:if>
				</c:forEach>
				<c:if test="${index == 0}">
					<div class="well well-small"><spring:message code='expedient.dada.agrupacio.cap'/></div>
				</c:if>
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
	//$('.panel.panel-default').find('a').attr('target', 'BLANK');
	$('#${grupId}-dades address').find('a').attr('target', 'BLANK');

	$("table tr:last").each(function(){
		var cols = $(this).find("td").size();
		if (cols == 0)
			$(this).remove();
//		else if (cols < 3)
//			$(this).append("<td colspan='" + (3 - cols) + "'></td>");
	});
});

var desplegats;
var panell;

function updatePanell () {
	if (desplegats != null) {
		desplegats.each(function(){
			$("#" + $(this).attr("id")).addClass("in");
		});
	}
	desplegats = null;

	$('#' + panell.attr("id") + ' .var-delete').heliumEvalLink({
		refrescarAlertes: true,
		refrescarPagina: false,
		ajaxRefrescarAlertes: true,
		alertesRefreshUrl: '<c:url value="/nodeco/v3/missatges"/>'
	});
	$('#' + panell.attr("id") + ' .var-edit').heliumEvalLink({
		refrescarAlertes: true,
		refrescarPagina: false,
		ajaxRefrescarAlertes: true,
		alertesRefreshUrl: '<c:url value="/nodeco/v3/missatges"/>'
	});

	updateBadges();
}

function reestructura (proces, correcte) {
	if (correcte) {
		panell = $('#panel_' + proces);
		desplegats = panell.find(".collapse.in");
		var ambOcults = "";
		if ($("#ambOcults").length)
			ambOcults = $("#ambOcults").prop('checked');
		panell.load('<c:url value="/nodeco/v3/expedient/${expedientId}/dades/"/>' + proces, {"ambOcults": ambOcults}, updatePanell);
	}
};

// function refresca (proces) {
// 	alert(proces);
// }

function refrescarAlertas(e) {
	$.ajax({
		url: "<c:url value="/nodeco/v3/missatges"/>",
		async: false,
		timeout: 20000,
		success: function (data) {
			$('#contingut-alertes').html(data);
		}
	});
}
//]]>
</script>
