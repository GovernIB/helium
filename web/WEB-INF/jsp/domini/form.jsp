<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><c:choose><c:when test="${empty command.id}">Crear nou domini</c:when><c:otherwise>Modificar domini</c:otherwise></c:choose></title>
	<meta name="titolcmp" content="Disseny">
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function disable(blocid) {
	$("#" + blocid).find("input,select,textarea").attr("disabled", "disabled");
}
function enable(blocid) {
	$("#" + blocid).find("input,select,textarea").removeAttr("disabled");
}
function canviTipus(input) {
	if (input.value == "CONSULTA_WS") {
		enable("camps_ws");
		disable("camps_sql");
	} else if (input.value == "CONSULTA_SQL") {
		enable("camps_sql");
		disable("camps_ws");
	} else {
		disable("camps_ws");
		disable("camps_sql");
	}
}
// ]]>
</script>
</head>
<body>

	<form:form action="form.html" cssClass="uniForm">
		<div class="inlineLabels col first">
			<h3>Dades del domini</h3>
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Codi</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Nom</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="tipus"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="tipusDomini"/>
				<c:param name="itemBuit" value="<< Seleccioni un tipus >>"/>
				<c:param name="label">Tipus</c:param>
				<c:param name="onchange">canviTipus(this)</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="cacheSegons"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Temps en cache</c:param>
				<c:param name="comment">En segons (0 = no cache)</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="descripcio"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label">Descripció</c:param>
			</c:import>
		</div>
		<div class="inlineLabels col last">
			<h3>Dades connexió WS</h3>
			<div id="camps_ws">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="url"/>
					<c:param name="type" value="textarea"/>
					<c:param name="label">URL</c:param>
				</c:import>
			</div>
			<h3>Dades connexió SQL</h3>
			<div id="camps_sql">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="jndiDatasource"/>
					<c:param name="label">JNDI del datasource</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="sql"/>
					<c:param name="type" value="textarea"/>
					<c:param name="label">SQL</c:param>
				</c:import>
			</div>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}">Crear,Cancel·lar</c:when><c:otherwise>Modificar,Cancel·lar</c:otherwise></c:choose></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>

	<script type="text/javascript">$(document).ready(canviTipus(document.getElementById("tipus0")));</script>

</body>
</html>
