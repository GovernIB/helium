<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
	<title><fmt:message key='comuns.def_proces' />: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<c:import url="../common/formIncludes.jsp"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function disable(blocid) {
	$("#" + blocid).find("input,select,textarea").attr("disabled", "disabled");
}
function enable(blocid) {
	$("#" + blocid).find("input,select,textarea").removeAttr("disabled");
}
function canviPredef(input) {
	if (input.checked)
		enable("durada");
	else
		disable("durada");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="terminis"/>
	</c:import>

	<form:form action="terminiForm.html" cssClass="uniForm">
		<div class="inlineLabels col first">
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<form:hidden path="definicioProces"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='comuns.codi' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='comuns.nom' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="descripcio"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label"><fmt:message key='comuns.descripcio' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="duradaPredefinida"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.termform.durada_predef' /></c:param>
				<c:param name="onchange">canviPredef(this)</c:param>
				<c:param name="comment"><fmt:message key='defproc.termform.si_no_esta' /></c:param>
			</c:import>
			<div id="durada">
			<c:import url="../common/formElement.jsp">
				<c:param name="property">dies</c:param>
				<c:param name="type" value="custom"/>
				<c:param name="label"><fmt:message key='defproc.termform.durada' /></c:param>
				<c:param name="content">
					<ul class="alternate alt_termini">
						<spring:bind path="anys">
							<li>
								<label for="anys" class="blockLabel">
									<span><fmt:message key='defproc.termform.anys' /></span>
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
								<label for="mesos" class="blockLabel">
									<span><fmt:message key='defproc.termform.mesos' /></span>
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
									<span><fmt:message key='defproc.termform.dies' /></span>
									<input id="dies" name="dies" value="${status.value}" class="textInput"/>
								</label>
							</li>
						</spring:bind>
					</ul>
				</c:param>
			</c:import>
			</div>
			<script type="text/javascript">canviPredef(document.getElementById('duradaPredefinida0'))</script>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="laborable"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.termform.dies_lab' /></c:param>
			</c:import>
		</div>
		<div class="inlineLabels col last">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="manual"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.termform.permet_ctrl' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="diesPrevisAvis"/>
				<c:param name="label"><fmt:message key='defproc.termform.dies_previs' /></c:param>
				<c:param name="comment"><fmt:message key='defproc.termform.es_generara' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="alertaPrevia"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.termform.gen_alert_previa' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="alertaFinal"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.termform.gen_alert_final' /></c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}"><fmt:message key='comuns.crear' />,<fmt:message key='comuns.cancelar' /></c:when><c:otherwise><fmt:message key='comuns.modificar' />,<fmt:message key='comuns.cancelar' /></c:otherwise></c:choose></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>

</body>
</html>
