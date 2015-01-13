<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<script type="text/javascript" src="<c:url value="/js/jquery/jquery.DOMWindow.js"/>"></script>
<style type="text/css">
	.registre_a_retrocedir {background-color: #eeeeee;}
</style>
<script type="text/javascript">
// <![CDATA[
	jQuery(document).ready(function(){
		jQuery("#registro_tasques table").find("tbody > tr > td > a.retroces").each(function(){
			jQuery(this).hover(
				function(){
					var $fil = jQuery(this).parent().parent();					// Fila de la taula
					var element = $fil.find("td:nth-child(3)").html().trim(); 	// Tasca
					var token = $fil.find("td:nth-child(6)").html().trim();		// Token
					var tokens = token.split("/");
					$fil.addClass("registre_a_retrocedir");
					
					if (element.indexOf("Tasca") == 0) {						// És una tasca
						$fil.nextAll().each(function(){
							var elem = jQuery(this).find("td:nth-child(3)").html().trim();
							var tok = jQuery(this).find("td:nth-child(6)").html().trim();
							var toks = tok.split("/");
							
							if (elem.indexOf("Tasca") == 0) {
								var t = "/";
								for (i = 0; i < tokens.length; i++){
									if (tokens[i] != "" && t != "/") t = t + "/";
									t = t + tokens[i];
									subt = t + "/";
									if (tok == t || tok == subt) {
										jQuery(this).addClass("registre_a_retrocedir");
										return;
									}
								}
								var t = "/";
								var subt = "";
								for (i = 0; i < toks.length; i++){
									if (toks[i] != "" && t != "/") t = t + "/";
									t = t + toks[i];
									subt = t + "/";
									if (token == t || token == subt) {
										jQuery(this).addClass("registre_a_retrocedir");
										return;
									}
								}
							}
						});
					}
				},
				function(){
					jQuery("#registro_tasques table").find("tbody > tr").removeClass("registre_a_retrocedir");
				});
		});
	});
	function confirmarRetrocedir(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm("<spring:message code='expedient.log.confirm.retrocedir'/>");
	}
	
	function recargarRegistro() {
		$("#spinner").html('<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>');
		$('#registro_tasques').hide();
		$('#contingut-registre').load(
				'<c:url value="/nodeco/v3/expedient/${expedient.id}/registre?tipus_retroces='+$('#tipus_retroces').val()+'"/>',
				function() {
					$("#spinner").html('');
					$('#registro_tasques').show();
		});
	}
// ]]>
</script>

<div id="spinner"></div>
<c:set var="numBloquejos" value="${0}"/>
<c:forEach var="log" items="${logs}">
	<c:if test="${log.estat == 'BLOCAR'}"><c:set var="numBloquejos" value="${numBloquejos + 1}"/></c:if>
</c:forEach>

<div id="registro_tasques">
	<div class="buttonHolder">
		<button type="button" onclick='recargarRegistro()' class="btn btn-primary">
			<c:if test="${param.tipus_retroces == 0}">
				<input type="hidden" id="tipus_retroces" name="tipus_retroces" value="1"/>
				<spring:message code="expedient.log.tipus.tasca"/>
			</c:if>
			<c:if test="${param.tipus_retroces != 0}">
				<input type="hidden" id="tipus_retroces" name="tipus_retroces" value="0"/>
				<spring:message code="expedient.log.tipus.detall"/>
			</c:if>
		</button>
	</div>
	<br/>
	<c:choose>
		<c:when test="${not empty logs}">
			<table class="table table-bordered">
				<thead>
					<tr>
						<th><spring:message code="expedient.document.data"/></th>
						<th><spring:message code="expedient.editar.responsable"/></th>
						<c:if test="${isAdmin || (param.tipus_retroces == 0 && not isAdmin)}">
							<th><spring:message code="expedient.log.objecte"/></th>
						</c:if>
						<th>
							<c:if test="${isAdmin || (param.tipus_retroces == 0 && not isAdmin)}">
								<spring:message code="expedient.log.accio"/>
							</c:if>
							<c:if test="${not (param.tipus_retroces == 0 && not isAdmin)}">
								<spring:message code="expedient.log.objecte.TASCA"/>
							</c:if>
						</th>
						<c:if test="${isAdmin || (param.tipus_retroces == 0 && not isAdmin)}">
							<th><spring:message code="expedient.log.info"/></th>
							<th><spring:message code="expedient.lot.token"/></th>
						</c:if>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="log" items="${logs}">
						<c:if test="${((log.targetTasca && not isAdmin) || param.tipus_retroces == 0) || isAdmin}">
							<tr>
								<c:set var="cellStyle" value=""/>
								<c:if test="${log.estat == 'RETROCEDIT' or log.estat == 'RETROCEDIT_TASQUES'}">
									<c:set var="cellStyle" value="text-decoration:line-through"/>
								</c:if>
								<td style="${cellStyle}">
									<fmt:formatDate value="${log.data}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate>
								</td>
								<td style="${cellStyle}">
									${log.usuari}
								</td>
								<c:if test="${isAdmin || (param.tipus_retroces == 0 && not isAdmin)}">
									<td style="${cellStyle}">
										<c:choose>
											<c:when test="${log.targetTasca}"><spring:message code="expedient.log.objecte.TASCA"/><c:if test="${param.tipus_retroces == 0}">: ${tasques[log.targetId].nom}</c:if></c:when>
											<c:when test="${log.targetProces}"><spring:message code="expedient.log.objecte.PROCES"/>: ${log.targetId}</c:when>
											<c:when test="${log.targetExpedient}"><spring:message code="expedient.log.objecte.EXPEDIENT"/></c:when>
											<c:otherwise>???: ${log.targetId}</c:otherwise>
										</c:choose>
									</td>
								</c:if>
								<td style="${cellStyle}">
									<c:choose>
										<c:when test="${log.targetTasca and param.tipus_retroces != 0}">
											${tasques[log.targetId].nom}
											<span class="right">
												<a data-rdt-link-modal="true" class="a-modal-registre" href="<c:url value="../../v3/expedient/logAccionsTasca?id=${expedient.id}&targetId=${log.targetId}"/>" ><i  class="fa fa-search"></i></a>
											</span>
										</c:when>
										<c:otherwise>
											<spring:message code="expedient.log.accio.${log.accioTipus}"/>
										</c:otherwise>
									</c:choose>
								</td>
								<c:if test="${isAdmin || (param.tipus_retroces == 0 && not isAdmin)}">
									<td style="${cellStyle}">
										<c:choose>
											<c:when test="${log.accioTipus == 'PROCES_VARIABLE_CREAR'}"><spring:message code="expedient.log.info.variable"/>: ${log.accioParams}</c:when>
											<c:when test="${log.accioTipus == 'PROCES_VARIABLE_MODIFICAR'}"><spring:message code="expedient.log.info.variable"/>: ${log.accioParams}</c:when>
											<c:when test="${log.accioTipus == 'PROCES_VARIABLE_ESBORRAR'}"><spring:message code="expedient.log.info.variable"/>: ${log.accioParams}</c:when>
											<c:when test="${log.accioTipus == 'PROCES_DOCUMENT_AFEGIR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
											<c:when test="${log.accioTipus == 'PROCES_DOCUMENT_MODIFICAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
											<c:when test="${log.accioTipus == 'PROCES_DOCUMENT_ESBORRAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
											<c:when test="${log.accioTipus == 'PROCES_DOCUMENT_ADJUNTAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
											<c:when test="${log.accioTipus == 'PROCES_SCRIPT_EXECUTAR'}">
												<a href="#scriptForm_${log.id}" class="scriptLink_${log.id}"><i class="fa fa-search"></i></a>
												<script type="text/javascript">
													$('.scriptLink_${log.id}').openDOMWindow({
														eventType: 'click',
														width: 620,
														height: 260,
														loader: 1,
														loaderHeight: 50,
														loaderWidth: 100,
														eventType:'click', 
														overlayOpacity: 10,							
														windowPadding: 10,
														draggable: 1});
													$('.closeDOMWindow').closeDOMWindow({
														eventType:'click'
													});
												</script>
												<div id="scriptForm_${log.id}" style="display:none" class="ui-dialog-content ui-widget-content">
													<h3 class="titol-tab titol-script">	
														<spring:message code="expedient.log.accio.${log.accioTipus}"/>
													</h3>
													<p>
														${log.accioParams}
													</p>
												</div>
											</c:when>
											<c:when test="${log.accioTipus == 'TASCA_REASSIGNAR'}"><spring:message code="expedient.log.info.abans"/>: ${fn:split(log.accioParams, "::")[0]}, <spring:message code="expedient.log.info.despres"/>: ${fn:split(log.accioParams, "::")[1]}</c:when>
											<c:when test="${log.accioTipus == 'TASCA_ACCIO_EXECUTAR'}"><spring:message code="expedient.log.info.accio"/>: ${log.accioParams}</c:when>
											<c:when test="${log.accioTipus == 'TASCA_DOCUMENT_AFEGIR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
											<c:when test="${log.accioTipus == 'TASCA_DOCUMENT_MODIFICAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
											<c:when test="${log.accioTipus == 'TASCA_DOCUMENT_ESBORRAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
											<c:when test="${log.accioTipus == 'TASCA_DOCUMENT_SIGNAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
											<c:when test="${log.accioTipus == 'TASCA_COMPLETAR'}"><c:if test="${not empty log.accioParams}"><spring:message code="expedient.log.info.opcio"/>: ${log.accioParams}</c:if></c:when>
											<c:when test="${log.accioTipus == 'EXPEDIENT_ATURAR'}"><spring:message code="expedient.log.info.missatges"/>: ${log.accioParams}</c:when>
											<c:when test="${log.accioTipus == 'EXPEDIENT_ACCIO'}"><spring:message code="expedient.log.info.accio"/>: ${log.accioParams}</c:when>
											<c:when test="${log.accioTipus == 'EXPEDIENT_RETROCEDIR' or log.accioTipus == 'EXPEDIENT_RETROCEDIR_TASQUES'}">
												<a class="a-modal-registre" href="<c:url value="../../v3/expedient/logRetrocedit?id=${expedient.id}&logId=${log.id}"/>" ><i class="fa fa-search"></i></a>
											</c:when>
											<c:otherwise></c:otherwise>
										</c:choose>
									</td>
									<td style="${cellStyle}">
										${log.tokenName}
									</td>
								</c:if>
								<td style="${cellStyle}">
									<c:choose>
										<c:when test="${log.accioTipus == 'PROCES_SCRIPT_EXECUTAR'}"></c:when>
										<c:when test="${log.accioTipus == 'PROCES_LLAMAR_SUBPROCES'}"></c:when>
										<c:when test="${log.estat == 'NORMAL' && numBloquejos == 0}">										
											<c:if test="${isAdmin}">
												<a href="<c:url value="../../v3/expedient/retrocedir">
													<c:param name="id" value="${expedient.id}"/>
													<c:param name="logId" value="${log.id}"/>
													<c:param name="tipus_retroces" value="${param.tipus_retroces}"/>
													<c:param name="retorn" value="r"/>
													</c:url>" onclick="return confirmarRetrocedir(event)" class="retroces">
													<i class="fa fa-reply" 
														alt="<spring:message code="expedient.log.retrocedir"/>" 
														title="<spring:message code="expedient.log.retrocedir"/>" 
														border="0">
													</i>
												</a>
											</c:if>
										</c:when>				
										<c:otherwise></c:otherwise>
									</c:choose>
									<c:if test="${numBloquejos gt 0}">B</c:if>
								</td>
								<c:if test="${log.estat == 'BLOCAR'}"><c:set var="numBloquejos" value="${numBloquejos - 1}"/></c:if>
							</tr>
						</c:if>
					</c:forEach>
				</tbody>
			</table>
		</c:when>
		<c:otherwise>
			<div class="well well-small">No hi ha logs per a mostrar</div>
		</c:otherwise>
	</c:choose>
</div>
	
<script type="text/javascript">
// <![CDATA[
	$(".a-modal-registre").heliumEvalLink({
		refrescarAlertes: true,
		refrescarPagina: false
	});
//]]>
</script>