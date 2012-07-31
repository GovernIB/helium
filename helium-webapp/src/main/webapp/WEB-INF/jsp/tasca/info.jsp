<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<c:set var="usuarisEntorn" value="${sessionScope.commandUsuarisEntorn}"/>
<html>
<head>
	<title>${tasca.nomLimitat}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.tasques' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function mostrarOcultar(img, objid) {
	var obj = document.getElementById(objid);
	if (obj.style.display=="none") {
		obj.style.display = "block";
		img.src = '<c:url value="/img/magnifier_zoom_out.png"/>';
	} else {
		obj.style.display = "none";
		img.src = '<c:url value="/img/magnifier_zoom_in.png"/>';
	}
	
}
function confirmarDelegar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='tasca.info.confirmacio' />");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsTasca.jsp">
		<c:param name="tabActiu" value="info"/>
	</c:import>

	<c:if test="${not empty tasca.missatgeInfo}">
		<div class="missatgesInfo">
			<p>${tasca.missatgeInfo}</p>
		</div>
	</c:if>
	<c:if test="${not empty tasca.missatgeWarn}">
		<div class="missatgesWarn">
			<p>${tasca.missatgeWarn}</p>
		</div>
	</c:if>

	<h3 class="titol-tab titol-info"><fmt:message key='tasca.info.info_tasca' /></h3>
	<dl class="form-info">
		<dt><fmt:message key='comuns.titol' /></dt><dd>${tasca.nom}</dd>
		<c:if test="${empty seleccioMassiva}">
			<dt><fmt:message key='tasca.info.expedient' /></dt><dd><a href="<c:url value="/expedient/info.html"><c:param name="id" value="${tasca.expedient.processInstanceId}"/></c:url>" title="<fmt:message key='tasca.info.detalls_exp' />">${tasca.expedient.identificador}</a></dd>
		</c:if>
		<dt><fmt:message key='comuns.tipus_exp' /></dt><dd>${tasca.expedient.tipus.nom}</dd>
		<dt><fmt:message key='tasca.info.data_creacio' /></dt><dd><fmt:formatDate value="${tasca.createTime}" pattern="dd/MM/yyyy HH:mm"/></dd>
		<dt><fmt:message key='tasca.info.prioritat' /></dt><dd>
			<c:choose>
				<c:when test="${tasca.priority == 2}"><fmt:message key='tasca.info.m_alta' /></c:when>
				<c:when test="${tasca.priority == 1}"><fmt:message key='tasca.info.alta' /></c:when>
				<c:when test="${tasca.priority == 0}"><fmt:message key='tasca.info.normal' /></c:when>
				<c:when test="${tasca.priority == -1}"><fmt:message key='tasca.info.baixa' /></c:when>
				<c:when test="${tasca.priority == -2}"><fmt:message key='tasca.info.m_baixa' /></c:when>
				<c:otherwise>${tasca.priority}</c:otherwise>
			</c:choose>
		</dd>
		<c:if test="${not empty tasca.dueDate}"><dt><fmt:message key='tasca.info.data_limit' /></dt><dd><fmt:formatDate value="${tasca.dueDate}" pattern="dd/MM/yyyy HH:mm"/></dd></c:if>
	</dl>
	<br style="clear: both"/><br/>

	<c:if test="${not tasca.delegada}">
		<c:if test="${tasca.delegable}">
			<div class="missatgesGris">
				<h3 class="titol-tab titol-delegacio"><fmt:message key='tasca.info.del_tasca' /> <img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="<fmt:message key='tasca.info.mos_ocul' />" title="<fmt:message key='tasca.info.mos_ocul' />" border="0" onclick="mostrarOcultar(this,'form-delegacio')"/></h3>
				<div id="form-delegacio" style="display:none">
					<form:form action="delegacioCrear.html" cssClass="uniForm" onsubmit="return confirmarDelegar(event)">
						<div class="inlineLabels">
							<form:hidden path="taskId"/>
							<c:import url="../common/formElement.jsp">
								<c:param name="property" value="actorId"/>
								<c:param name="type" value="select"/>
								<c:param name="items" value="usuaris"/>
								<c:param name="itemLabel" value="nomSencer"/>
								<c:param name="itemValue" value="id"/>
								<c:param name="itemBuit">&lt;&lt; <fmt:message key="tasca.delegar.select"/> &gt;&gt;</c:param>
								<c:param name="label"><fmt:message key='tasca.info.destinatari'/></c:param>
							</c:import>
								
							
							<c:import url="../common/formElement.jsp">
								<c:param name="property" value="comentari"/>
								<c:param name="type" value="textarea"/>
								<c:param name="label"><fmt:message key='tasca.info.comentari' /></c:param>
							</c:import>
							<c:import url="../common/formElement.jsp">
								<c:param name="property" value="supervisada"/>
								<c:param name="type" value="checkbox"/>
								<c:param name="label"><fmt:message key='tasca.info.supervisarq' /></c:param>
								<c:param name="checkAsText" value="off"/>
							</c:import>
							<c:import url="../common/formElement.jsp">
								<c:param name="type" value="buttons"/>
								<c:param name="values">submit</c:param>
								<c:param name="titles"><fmt:message key='tasca.info.delegar' /></c:param>
							</c:import>
						</div>
					</form:form>
				</div>
			</div>
		</c:if>
	</c:if>
	<c:import url="../common/tramitacioTasca.jsp"/>
	<%--c:if test="${not tasca.delegada or not tasca.delegacioOriginal}">
		<div class="missatgesGris">
			<h4 class="titol-missatge">Finalitzar la tasca</h4>
			<c:set var="outcomes"><c:forEach var="outcome" items="${tasca.outcomes}" varStatus="status"><c:choose><c:when test="${not empty outcome}">${outcome}</c:when><c:otherwise>Finalitzar</c:otherwise></c:choose><c:if test="${not status.last}">,</c:if></c:forEach></c:set>
			<form action="completar.html" method="post" class="uniForm" onsubmit="return confirmarFinalitzar(event)">
				<input type="hidden" name="id" value="${tasca.id}"/>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">${outcomes}</c:param>
					<c:param name="titles">${outcomes}</c:param>
				</c:import>
			</form>
		</div>
	</c:if--%>

</body>
</html>
