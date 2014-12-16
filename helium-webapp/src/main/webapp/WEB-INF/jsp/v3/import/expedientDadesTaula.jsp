<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<style>
	div.grup:hover {background-color: #e5e5e5 !important;border-color: #ccc !important;}
	div.grup .panel-body-grup {padding-bottom: 0px !important;}
	.panel-body-grup {margin: -1px;}
	.extensionIcon {color: white;font-size: 10px;font-weight: bold;margin-left: -33px;}
	.signature {margin-top: -5px;}
	a, a:HOVER, a:FOCUS {text-decoration: none;}
	.table.table-bordered {margin-bottom: 0px;}
	.tableDocuments .left {padding: 10px;}
	.tableDocuments .right {padding: 15px;width: 100%;}
	.icon, .icon:hover, .icon:focus {
	    text-decoration: none;
	    color: #2a6496;
	}
	.icon {
	    color: #428bca;
	}
	.icon {
	    background: none repeat scroll 0 0 rgba(0, 0, 0, 0);
	}
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
						<td<c:if test="${dadaTipusRegistre}"> colspan="${paramNumColumnes}"</c:if><c:if test="${not empty dada.error}"> style="background-color:#f2dede"</c:if>>
							<c:choose>
								<c:when test="${fn:endsWith(dada.class.name, 'DadaDto')}">
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
								</c:when>
								<c:when test="${fn:endsWith(dada.class.name, 'DocumentDto')}">
									<c:set var="document" value="${dada}"/>
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
																<span class="fa fa-file fa-3x"></span>
																<span class="extensionIcon">
																	${fn:toUpperCase(document.arxiuExtensio)}
																</span>
															</a>
														</td>
														<td class="right">
															<c:if test="${not empty document.id}">
																<table class="table-condensed marTop6 tableDocuments">
																	<thead>
																		<tr>
																			<td>
																				<fmt:formatDate value="${document.dataDocument}" pattern="dd/MM/yyyy"/>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<c:if test="${document.signat or document.registrat}">
																					<a data-rdt-link-modal="true" class="icon signature" href="<c:url value='../../v3/expedient/${expedientId}/verificarSignatura/${document.id}/${document.documentCodi}'/>">
																						<span class="fa fa-2x fa-certificate" title="Document signat (clic per veure detalls)"></span>
																					</a>
																					<script type="text/javascript">
																						// <![CDATA[
																							$('.icon').heliumEvalLink({
																								refrescarAlertes: true,
																								refrescarPagina: false
																							});
																						//]]>
																					</script>
																				</c:if>
																				
																				<c:if test="${document.signat}">
																					<span class="icon signature fa-stack fa-2x" onclick="return confirmarEsborrarSignatura(event, '${expedientId}', '${document.id}')" style="cursor: pointer">
																					  <i class="fa fa-certificate fa-stack-1x"></i>
																					  <i class="fa fa-ban fa-stack-2x text-danger"></i>
																					</span>
																				</c:if>
																				
																				<c:if test="${!document.registrat}">
																					<span class="icon fa fa-trash-o fa-2x" onclick="return confirmarBorrarExpedient(event, '${expedientId}', '${document.id}', '${document.documentCodi}')" style="cursor: pointer"></span>
																				</c:if>
																				<c:if test="${document.registrat}">
																					<a href="#">
																						<span class="icon fa fa-book fa-2x" title="Document registrat (clic per veure detalls)"></span>
																					</a>
																				</c:if>
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
															<strong>${document.documentNom}</strong><br/>
														</td>
													</tr>
												</tbody>
											</table>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>[Tipus desconegut]</c:otherwise>
							</c:choose>
						</td>
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
<script>
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

function confirmarBorrarExpedient(e, idExpedient, documentStoreId, docCodi) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	if (confirm("<fmt:message key='expedient.document.confirm_esborrar_proces' />")) {
		$.ajax({
            type: 'POST',
            url: idExpedient+"/documentEsborrar/"+documentStoreId+"/"+docCodi,
            success: function(data) {
            	if (data) {
            		$("#document_"+documentStoreId).closest("td").remove();
            	}
            	
            	// Refrescar alertas
            	refrescarAlertas(e);
            }
        });
	}
}

function confirmarEsborrarSignatura(e, idExpedient, documentStoreId) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	if (confirm("<fmt:message key='expedient.document.confirm_esborrar_signatures' />")) {
		$.ajax({
            type: 'POST',
            url: idExpedient+"/signaturaEsborrar/"+documentStoreId,
            success: function(data) {
            	if (data) {
            		$("#document_"+documentStoreId).find(".signature").remove();
            	}
            	
            	// Refrescar alertas
            	refrescarAlertas(e);
            }
        });
	}
}

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
</script>
