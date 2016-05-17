<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key='entorn.llistat.netejar.titol' /></title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='defproc.llistat.confirmacio' />");
}
function selecTots() {
	var ch = $("#selTots:checked").val();
	$("#registre input[type='checkbox'][name='dpId']").each(function() {
		if(!this.disabled) {
			this.checked = ch;
		}
	});
}
// ]]>
</script>
</head>
<body>
	<c:set var="msg_afectats"><fmt:message key='defproc.llistat.llistar.afectats'/> <img src='/helium/img/bullet_error.png' title='Aquesta consulta pot resultar molt costosa!'/></c:set>
	<form action="netejar_df.html" method="post">
		<input type="hidden" name="expedientTipusId" value="${expedientTipusId}"/>
		<div class="buttonHolder" style="margin-bottom:10px;">
			<button type="submit" class="submitButton"><fmt:message key='entorn.llistat.netejar.seleccionats' /></button>
			<a href='<c:url value="/expedientTipus/llistat.html"/>'>
				<button type="button" class="submitButton"><fmt:message key='comuns.tornar' /></button>
			</a>
		</div>
		<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
			<display:column title="<input id='selTots' type='checkbox' value='false' onclick='selecTots()'>"  style="width:1%;">
				<input type="checkbox" name="dpId" value="${registre.id}"/>
			</display:column>
			<display:column property="jbpmName" titleKey="comuns.nom" sortable="true" url="/definicioProces/info.html" paramId="definicioProcesId" paramProperty="id"/>
			<display:column property="versio" titleKey="defproc.llistat.versions" sortable="true"/>
			<display:column property="expedientTipus.nom" titleKey="comuns.tipus_exp" sortable="true"/>
<%-- 			<display:column> --%>
<%-- 				<a href="<c:url value="/definicioProces/delete.html"><c:param name="definicioProcesId" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='defproc.llistat.esborrar_darrera' />" title="<fmt:message key='defproc.llistat.esborrar_darrera' />" border="0"/></a> --%>
<%-- 			</display:column> --%>
			<display:column title="${msg_afectats}" style="width:10%;">
				<form action="afectats_df.html"> <%-- onsubmit="return confirmarNetejarDf(event)"> --%>
					<input type="hidden" name="expedientTipusId" value="${expedientTipusId}"/>
					<input type="hidden" name="definicioProcesId" value="${registre.id}"/>
					<button type="submit" class="submitButton"><span><img src="/helium/img/magnifier.png"/></span></button>
				</form>
			</display:column>
		</display:table>
	<%-- 	<script type="text/javascript">initSelectable();</script> --%>
	
		<div style="clear: both"></div>
		
		<div class="buttonHolder">
			<button type="submit" class="submitButton"><fmt:message key='entorn.llistat.netejar.seleccionats' /></button>
			<a href='<c:url value="/expedientTipus/llistat.html"/>'>
				<button type="button" class="submitButton"><fmt:message key='comuns.tornar' /></button>
			</a>
		</div>
	</form>	
</body>
</html>