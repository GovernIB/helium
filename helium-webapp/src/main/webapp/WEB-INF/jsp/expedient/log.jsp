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
<script type="text/javascript">
// <![CDATA[
function confirmarRetrocedir(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key="expedient.log.confirm.retrocedir"/>");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="registre"/>
	</c:import>

	<h3 class="titol-tab titol-registre">
		<fmt:message key="expedient.registre"/>
	</h3>
	<c:set var="numBloquejos" value="${0}"/>
	<c:forEach var="log" items="${logs}">
		<c:if test="${log.estat == 'BLOCAR'}"><c:set var="numBloquejos" value="${numBloquejos + 1}"/></c:if>
	</c:forEach>
	<display:table name="logs" id="registre" class="displaytag">
		<c:set var="cellStyle" value=""/>
		<c:if test="${registre.estat == 'RETROCEDIT'}"><c:set var="cellStyle" value="text-decoration:line-through"/></c:if>
		<display:column property="data" titleKey="expedient.document.data" format="{0,date,dd/MM/yyyy'&nbsp;'HH:mm:ss}" style="${cellStyle}"/>
		<display:column property="usuari" titleKey="expedient.editar.responsable" style="${cellStyle}"/>
		<display:column property="accioTipus" titleKey="expedient.registre.accio" style="${cellStyle}"/>
		<display:column titleKey="expedient.log.accio.objecte" style="${cellStyle}">
			<c:choose>
				<c:when test="${registre.targetTasca}">TASCA(${registre.targetId})</c:when>
				<c:when test="${registre.targetProces}">PROCES(${registre.targetId})</c:when>
				<c:when test="${registre.targetExpedient}">EXPEDIENT(${registre.targetId})</c:when>
				<c:otherwise>DESCONEGUT(${registre.targetId})</c:otherwise>
			</c:choose>
		</display:column>
		<display:column property="accioParams" titleKey="expedient.log.accio.params" style="${cellStyle}"/>
		<display:column>
			<c:if test="${registre.estat == 'NORMAL' && numBloquejos == 0}">
				<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
					<a href="<c:url value="/expedient/retrocedir.html"><c:param name="id" value="${param.id}"/><c:param name="logId" value="${registre.id}"/></c:url>" onclick="return confirmarRetrocedir(event)"><img src="<c:url value="/img/arrow_undo.png"/>" alt="<fmt:message key="expedient.log.retrocedir"/>" title="<fmt:message key="expedient.log.retrocedir"/>" border="0"/></a>
				</security:accesscontrollist>
			</c:if>
			<c:if test="${numBloquejos gt 0}">B</c:if>
		</display:column>
		<c:if test="${registre.estat == 'BLOCAR'}"><c:set var="numBloquejos" value="${numBloquejos - 1}"/></c:if>
	</display:table>

</body>
</html>
