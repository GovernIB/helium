<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title><fmt:message key="alerta.llistat.expedient"/>: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="<fmt:message key="comuns.consultes"/>" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
	<script type="text/javascript" src="<c:url value="/js/jquery/ui/ui.core.js"/>"></script>
	<script  type="text/javascript" src="<c:url value="/js/jquery/ui/jquery-ui-1.7.2.custom.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.DOMWindow.js"/>"></script>
<script type="text/javascript">
// <![CDATA[
function mostrarLogsRetrocedits(element) {
	var amplada = 960;
	var alcada = 400;
	$('<iframe id="verificacio" src="' + element.href +'"/>').dialog({
		title: "<fmt:message key='expedient.log.retroces.llistat' />",
		autoOpen: true,
		modal: true,
		autoResize: true,
		width: parseInt(amplada),
		height: parseInt(alcada)
	}).width(amplada - 30).height(alcada - 30);
}
function mostrarLogAccionsTasca(element) {
	var amplada = 960;
	var alcada = 400;
	$('<iframe id="verificacio" src="' + element.href +'"/>').dialog({
		title: "<fmt:message key='expedient.log.accions.llistat' />",
		autoOpen: true,
		modal: true,
		autoResize: true,
		width: parseInt(amplada),
		height: parseInt(alcada)
	}).width(amplada - 30).height(alcada - 30);
}
function confirmarRetrocedir(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key="expedient.log.confirm.retrocedir"/>");
}
jQuery(document).ready(function(){
	jQuery("table#registre").find("tbody > tr > td > a.retroces").each(function(){
		jQuery(this).hover(
		<%--c:if test="${param.tipus_retroces != 0}"--%>
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
		<%--/c:if>
		<c:if test="${param.tipus_retroces == 0}"--%>
//			function(){
//				var $fil = jQuery(this).parent().parent();
//				$fil.addClass("registre_a_retrocedir");
//				$fil.nextAll().each(function(){
//					jQuery(this).addClass("registre_a_retrocedir");
//				});
//			},
		<%--/c:if--%>
			function(){
				jQuery("table#registre").find("tbody > tr").removeClass("registre_a_retrocedir");
			});
	});
});
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="registre"/>
	</c:import>

	<h3 class="titol-tab titol-registre">
		<c:if test="${param.tipus_retroces == 0}"><fmt:message key="expedient.log"/></c:if>
		<c:if test="${param.tipus_retroces != 0}"><fmt:message key="expedient.tasques"/></c:if>
	</h3>
	<c:set var="numBloquejos" value="${0}"/>
	<c:forEach var="log" items="${logs}">
		<c:if test="${log.estat == 'BLOCAR'}"><c:set var="numBloquejos" value="${numBloquejos + 1}"/></c:if>
	</c:forEach>
	<form action="<c:url value='/expedient/registre.html'/>">
		<input type="hidden" id="id" name="id" value="${param.id}"/>
		<c:if test="${param.tipus_retroces == 0}">
			<!--input type="hidden" id="tipus_retroces" name="tipus_retroces" value="1"/-->
			<div class="buttonHolder"><button type="submit" class="submitButton"><fmt:message key="expedient.log.tipus.tasca"/></button></div>
		</c:if>
		<c:if test="${param.tipus_retroces != 0}">
			<input type="hidden" id="tipus_retroces" name="tipus_retroces" value="0"/>
			<div class="buttonHolder"><button type="submit" class="submitButton"><fmt:message key="expedient.log.tipus.detall"/></button></div>
		</c:if>
	</form>
	<display:table name="logs" id="registre" class="displaytag">
		<c:set var="cellStyle" value=""/>
		<c:if test="${registre.estat == 'RETROCEDIT' or registre.estat == 'RETROCEDIT_TASQUES'}"><c:set var="cellStyle" value="text-decoration:line-through"/></c:if>
		<display:column property="data" titleKey="expedient.document.data" format="{0,date,dd/MM/yyyy'&nbsp;'HH:mm:ss}" style="${cellStyle}"/>
		<display:column property="usuari" titleKey="expedient.editar.responsable" style="${cellStyle}"/>
		<display:column titleKey="expedient.log.objecte" style="${cellStyle}">
			<c:choose>
				<c:when test="${registre.targetTasca}"><fmt:message key="expedient.log.objecte.TASCA"/><c:if test="${param.tipus_retroces == 0}">: ${tasques[registre.targetId].nom}</c:if></c:when>
				<c:when test="${registre.targetProces}"><fmt:message key="expedient.log.objecte.PROCES"/>: ${registre.targetId}</c:when>
				<c:when test="${registre.targetExpedient}"><fmt:message key="expedient.log.objecte.EXPEDIENT"/></c:when>
				<c:otherwise>???: ${registre.targetId}</c:otherwise>
			</c:choose>
		</display:column>
		<display:column titleKey="expedient.log.accio" style="${cellStyle}">
			<c:choose>
				<c:when test="${registre.targetTasca and param.tipus_retroces != 0}">
					${tasques[registre.targetId].nom}
					<span class="right"><a href="<c:url value="/expedient/logAccionsTasca.html"><c:param name="id" value="${param.id}"/><c:param name="targetId" value="${registre.targetId}"/></c:url>" onclick="mostrarLogAccionsTasca(this); return false"><img src="<c:url value="/img/magnifier.png"/>" alt="<fmt:message key="expedient.log.accions.llistat"/>" title="<fmt:message key="expedient.log.accions.llistat"/>" border="0"/></a></span>
				</c:when>
				<c:otherwise><fmt:message key="expedient.log.accio.${registre.accioTipus}"/></c:otherwise>
			</c:choose>
		</display:column>
		<display:column titleKey="expedient.log.info" style="${cellStyle}">
			<c:choose>
				<c:when test="${registre.accioTipus == 'PROCES_LLAMAR_SUBPROCES'}"><fmt:message key="expedient.log.info.nom"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'PROCES_VARIABLE_CREAR'}"><fmt:message key="expedient.log.info.variable"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'PROCES_VARIABLE_MODIFICAR'}"><fmt:message key="expedient.log.info.variable"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'PROCES_VARIABLE_ESBORRAR'}"><fmt:message key="expedient.log.info.variable"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'PROCES_DOCUMENT_AFEGIR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'PROCES_DOCUMENT_MODIFICAR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'PROCES_DOCUMENT_ESBORRAR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'PROCES_DOCUMENT_ADJUNTAR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'PROCES_SCRIPT_EXECUTAR'}">
					<a href="#scriptForm_${registre.id}" class="scriptLink_${registre.id}"><img src="<c:url value="/img/magnifier.png"/>"/></a>
					<script type="text/javascript">
						$('.scriptLink_${registre.id}').openDOMWindow({
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
					<div id="scriptForm_${registre.id}" style="display:none" class="ui-dialog-content ui-widget-content">
						<h3 class="titol-tab titol-script">	
							<fmt:message key="expedient.log.accio.${registre.accioTipus}"/>
						</h3>
						<p>
							${registre.accioParams}
						</p>
					</div>
				</c:when>
				<c:when test="${registre.accioTipus == 'TASCA_REASSIGNAR'}"><fmt:message key="expedient.log.info.abans"/>: ${fn:split(registre.accioParams, "::")[0]}, <fmt:message key="expedient.log.info.despres"/>: ${fn:split(registre.accioParams, "::")[1]}</c:when>
				<c:when test="${registre.accioTipus == 'TASCA_ACCIO_EXECUTAR'}"><fmt:message key="expedient.log.info.accio"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'TASCA_DOCUMENT_AFEGIR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'TASCA_DOCUMENT_MODIFICAR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'TASCA_DOCUMENT_ESBORRAR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'TASCA_DOCUMENT_SIGNAR'}"><fmt:message key="expedient.log.info.document"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'TASCA_COMPLETAR'}"><c:if test="${not empty registre.accioParams}"><fmt:message key="expedient.log.info.opcio"/>: ${registre.accioParams}</c:if></c:when>
				<c:when test="${registre.accioTipus == 'EXPEDIENT_ATURAR'}"><fmt:message key="expedient.log.info.missatges"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'EXPEDIENT_ACCIO'}"><fmt:message key="expedient.log.info.accio"/>: ${registre.accioParams}</c:when>
				<c:when test="${registre.accioTipus == 'EXPEDIENT_RETROCEDIR' or registre.accioTipus == 'EXPEDIENT_RETROCEDIR_TASQUES'}">
					<a href="<c:url value="/expedient/logRetrocedit.html"><c:param name="id" value="${param.id}"/><c:param name="logId" value="${registre.id}"/></c:url>" onclick="mostrarLogsRetrocedits(this); return false"><img src="<c:url value="/img/magnifier.png"/>" alt="<fmt:message key="expedient.log.retrocedits"/>" title="<fmt:message key="expedient.log.retrocedits"/>" border="0"/></a>
				</c:when>
				<c:otherwise></c:otherwise>
			</c:choose>
		</display:column>
		<%--c:if test="${param.tipus_retroces != 0}"--%>
		<display:column property="tokenName" titleKey="expedient.lot.token" style="${cellStyle}"/>
		<%--/c:if--%>
		<display:column>
			<c:choose>
				<c:when test="${registre.accioTipus == 'PROCES_SCRIPT_EXECUTAR'}"></c:when>
				<c:when test="${registre.accioTipus == 'PROCES_LLAMAR_SUBPROCES'}"></c:when>
				<c:when test="${registre.estat == 'NORMAL' && numBloquejos == 0}">
					<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="128,16">
						<a href="<c:url value="/expedient/retrocedir.html"><c:param name="id" value="${param.id}"/><c:param name="logId" value="${registre.id}"/><c:param name="tipus_retroces" value="${param.tipus_retroces}"/><c:param name="retorn" value="r"/></c:url>" onclick="return confirmarRetrocedir(event)" class="retroces"><img src="<c:url value="/img/arrow_undo.png"/>" alt="<fmt:message key="expedient.log.retrocedir"/>" title="<fmt:message key="expedient.log.retrocedir"/>" border="0"/></a>
					</security:accesscontrollist>
				</c:when>				
				<c:otherwise></c:otherwise>
			</c:choose>
			<c:if test="${numBloquejos gt 0}">B</c:if>
		</display:column>
		<c:if test="${registre.estat == 'BLOCAR'}">Hola<c:set var="numBloquejos" value="${numBloquejos - 1}"/></c:if>
	</display:table>

</body>
</html>
