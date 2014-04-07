<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><spring:message code='expedient.termini.modificar' />: ${termini.nom}</title>
	<meta name="titolcmp" content="<spring:message code='comuns.consultes' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
	<script type="text/javascript">
	// <![CDATA[
		function mostrar(objid) {
			var obj = document.getElementById(objid);
			obj.style.display = "block";
		}
		function ocultar(objid) {
			var obj = document.getElementById(objid);
			obj.style.display = "none";
		}
		function canviTipus(select) {
			ocultar("terminiDurada");
			ocultar("terminiDataInici");
			ocultar("terminiDataFi");
			if ("DURADA" == select.value) {
				mostrar("terminiDurada");
			} else if ("DATA_INICI" == select.value) {
				mostrar("terminiDataInici");
			} else if ("DATA_FI" == select.value) {
				mostrar("terminiDataFi");
			}
		}
		function onLoad() {
			canviTipus(document.getElementById("tipus0"));
		}
		window.onload = onLoad;
	// ]]>
	</script>
</head>
<body>
	<form:form action="terminiModificar" cssClass="uniForm" commandName="expedientTerminiModificarCommand">
		<div class="inlineLabels">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="tipus"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="tipus"/>
				<c:param name="label"><spring:message code="expedient.termini.modificar.tipus"/></c:param>
				<c:param name="onchange" value="canviTipus(this)"/>
			</c:import>
			<div id="terminiDurada" style="display:none">
				<c:import url="../common/formElement.jsp">
					<c:param name="property">anys</c:param>
					<c:param name="required" value="true"/>
					<c:param name="type" value="custom"/>
					<c:param name="label"><spring:message code="expedient.termini.durada"/></c:param>
					<c:param name="content">
						<ul class="alternate alt_termini">
							<spring:bind path="anys">
								<li>
									<label for="anys" class="blockLabel">
										<span><spring:message code="common.camptasca.anys"/></span>
										<select id="anys" name="anys">
											<c:forEach var="index" begin="0" end="12">
												<option value="${index}"<c:if test="${status.value==index}"> selected="selected"</c:if>>${index}</option>
											</c:forEach>
										</select>
									</label>
								</li>
							</spring:bind>
							<spring:bind path="mesos">
								<li>
									<label for="${codiActual}_mesos" class="blockLabel">
										<span><spring:message code="common.camptasca.mesos"/></span>
										<select id="mesos" name="mesos">
											<c:forEach var="index" begin="0" end="12">
												<option value="${index}"<c:if test="${status.value==index}"> selected="selected"</c:if>>${index}</option>
											</c:forEach>
										</select>
									</label>
								</li>
							</spring:bind>
							<spring:bind path="dies">
								<li>
									<label for="dies" class="blockLabel">
										<span><spring:message code="common.camptasca.dies"/></span>
										<input id="dies" name="dies" value="${status.value}" class="textInput" onchange="canviTermini(this)"/>
									</label>
								</li>
							</spring:bind>
						</ul>
					</c:param>
					<c:param name="comment">${campActual.observacions}</c:param>
					<c:param name="iterateOn"><c:if test="${campActual.multiple}">valorActual</c:if></c:param>
					<c:param name="multipleIcons"><c:if test="${campActual.multiple}">true</c:if></c:param>
				</c:import>
			</div>
			<div id="terminiDataInici" style="display:none">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="dataInici"/>
					<c:param name="required" value="true"/>
					<c:param name="type" value="date"/>
					<c:param name="label"><spring:message code="expedient.termini.data.inici"/></c:param>
				</c:import>
			</div>
			<div id="terminiDataFi" style="display:none">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="dataFi"/>
					<c:param name="required" value="true"/>
					<c:param name="type" value="date"/>
					<c:param name="label"><spring:message code="expedient.termini.data.fi"/></c:param>
				</c:import>
			</div>
		</div>
		<div id="formButtons" class="pull-right">
			<button type="submit" class="btn btn-primary" name="submit" value="<spring:message code='comuns.modificar' />">
				<spring:message code='comuns.modificar' />
			</button>
		</div>
	</form:form>
</body>
</html>
