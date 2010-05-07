<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Expedient: ${expedient.identificador}</title>
	<meta name="titolcmp" content="Consultes">
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	if ("cancel" == submitAction)
		return true;
	return confirm("Estau segur que voleu retrocedir aquest token?");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="tokens"/>
	</c:import>

	<h3 class="titol-tab titol-tokens">
		Retrocedir token ${token.fullName}
	</h3>

	<form:form action="tokenRetrocedir.html" cssClass="uniForm" onsubmit="return confirmar(event)">
		<div class="inlineLabels">
			<input type="hidden" id="id" name="id" value="${param.id}"/>
			<form:hidden path="tokenId"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nodeName"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="arrivingNodeNames"/>
				<c:param name="itemBuit" value="<< Seleccioni un node >>"/>
				<c:param name="label">Node a on retrocedir</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="cancelTasks"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Cancel·lar tasques?</c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles">Retrocedir,Cancel·lar</c:param>
		</c:import>
	</form:form>

</body>
</html>
