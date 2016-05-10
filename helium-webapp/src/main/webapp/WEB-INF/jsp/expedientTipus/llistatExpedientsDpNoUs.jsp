<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<fmt:message var='titol' key='defproc.llistat.llistar.afectats.titol'>
		<fmt:param value="${definicioProces.jbpmName}"/>
		<fmt:param value="${definicioProces.versio}"/>
	</fmt:message>
	<title>${titol}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<c:url value="/js/jquery/jquery.DOMWindow.js"/>"></script>
    <c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
$(document).ready(function() {
	$("#btn_canviVersio").click(function(){
		$("#novaDefinicioProcesId").val($("#definicioProcesId0").val());
		$("#form_afectats").attr("action", "canvi_versio.html").submit();
	});
	$("#btn_borrarLogs").click(function(){
		$("#form_afectats").attr("action", "borrar_logs.html").submit();
	});
});

function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='expedient.iniciar.confirm_esborrar' />");
}

function disabledEventPropagation(event) {
   if (event.stopPropagation){
       event.stopPropagation();
   }
   else if(window.event){
      window.event.cancelBubble=true;
   }
   return true;
}

function selecTots() {
	var ch = $("#selTots:checked").val();
	$("#registre input[type='checkbox'][name='expedientId']").each(function() {
		if(!this.disabled) {
			this.checked = ch;
		}
	});
}
// ]]>
</script>
</head>
<body>
	<form id="form_afectats" method="post">
		<input type="hidden" name="expedientTipusId" value="${expedientTipusId}"/>
		<input type="hidden" name="definicioProcesId" value="${definicioProcesId}"/>
		<input type="hidden" id="novaDefinicioProcesId" name="novaDefinicioProcesId"/>
		<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
			<display:column title="<input id='selTots' type='checkbox' value='false' onclick='selecTots()'>"  style="width:1%;">
				<input type="checkbox" name="expedientId" value="${registre.id}"/>
			</display:column>
	<%-- 		<display:column property="identificador" titleKey="expedient.llistat.expedient" sortable="true" url="/expedient/info.html" paramId="id" paramProperty="processInstanceId"/> --%>
			<display:column titleKey="expedient.llistat.expedient" sortable="true">
			 <a href="<c:url value='/expedient/info.html?id=${registre.processInstanceId}'/>" target="_blank" onclick="return disabledEventPropagation(event);">${registre.identificador}</a>
			 </display:column>
			<display:column property="dataInici" titleKey="expedient.info.iniciat_el" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true"/>
		</display:table>
		<script type="text/javascript">initSelectable();</script>
	</form>		
	<div class="buttonHolder">
		<button href="#canviVersioForm" type="button" class="submitButton canviVersioLink"><fmt:message key='defproc.llistat.llistar.afectats.canviar.versio' /></button>
		<script type="text/javascript">
			$('.canviVersioLink').openDOMWindow({
				eventType: 'click',
				width: 620,
				height: 80,
				loader: 1,
				loaderHeight: 32,
				loaderWidth: 32,
				windowPadding: 10,
				draggable: 1});
		</script>
		<button id="btn_borrarLogs" type="button" class="submitButton"><fmt:message key='defproc.llistat.llistar.afectats.buidar.logs' /></button>
		<form action="netejar_df.html" style="display:inline;">
				<input type="hidden" name="id" value="${expedientTipusId}"/>
				<button type="submit" class="submitButton"><fmt:message key='comuns.tornar'/></button>
		</form>
	</div>
	
	<div id="canviVersioForm" style="display:none">
		<div class="uniForm">
			<div class="inlineLabels">
				<c:set var="definicionsProces" value="${definicioProces.jbpmIdsAmbDescripcio}" scope="request"/>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="definicioProcesId"/>
					<c:param name="type" value="select"/>
					<c:param name="items" value="definicionsProces"/>
					<c:param name="itemLabel" value="descripcio"/>
					<c:param name="itemValue" value="jbpmId"/>
					<c:param name="label"><fmt:message key='expedient.eines.canviar_versio' /></c:param>
				</c:import>
			</div>
			<div class="buttonHolder">
				<button id="btn_canviVersio" type="button" class="submitButton"><fmt:message key='defproc.llistat.llistar.afectats.canviar.versio'/></button>
			</div>
		</div>
	</div>
</body>
</html>
