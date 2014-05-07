<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ page import="java.util.List" %>
<html>
<head>
	<title><fmt:message key='expedient.consulta.tasques' /></title>
	<meta name="titolcmp" content="<fmt:message key='comuns.consultes' />" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function confirmarSuspendre(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu suspendre aquesta tasca?");
}
function confirmarReprendre(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu reprendre aquesta tasca?");
}
function confirmarCancelar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu cancel·lar aquesta tasca? Aquesta acció no es podrà desfer.");
}
function confirmarAlliberar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu alliberar aquesta tasca?");
}
function clicCheckMassiu(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	var ids = new Array();
	ids.push((e.target || e.srcElement).value);
	$.post("massivaIds.html", {
		expedientTipusId: "${expedientTipus.id}",
		tasquesId: ids,
		checked: (e.target || e.srcElement).checked
	});
}
function selTots(){
	var ch = $("#selTots:checked").val();
	var ids = new Array();
	$("#registre input[type='checkbox'][name='tascaId']").each(function() {
		ids.push($(this).val());
	}).attr("checked", (!ch) ? false : true);
	$.post("massivaIds.html", {
		expedientTipusId: "${expedientTipus.id}",
		tasquesId: ids,
		checked: (!ch) ? false : true
	});
}
// ]]>
</script>
</head>
<body>

	<div class="missatgesGris">
		<c:choose>
			<c:when test="${empty expedientTipus}">
				<h4 class="titol-consulta"><fmt:message key='expedient.consulta.select.tipusexpedient' /></h4>
				<form:form action="consultaTasques.html" commandName="commandSelConsulta" cssClass="uniForm">
					<input type="hidden" name="expedientTipId" id="expedientTipId" value="${expedientTipus.id}">
					<div class="inlineLabels col first">
						<input type="hidden" name="canviar" id="canviar" value="true"/>
						<c:import url="../common/formElement.jsp">
							<c:param name="property" value="expedientTipusId"/>
							<c:param name="type" value="select"/>
							<c:param name="items" value="expedientsTipus"/>
							<c:param name="itemLabel" value="nom"/>
							<c:param name="itemValue" value="id"/>
							<c:param name="itemBuit">&lt;&lt; <fmt:message key='expedient.consulta.select.tipusexpedient'/> &gt;&gt;</c:param>
							<c:param name="label"><fmt:message key='expedient.consulta.tipusexpedient' /></c:param>
							<c:param name="onchange">this.form.submit()</c:param>
						</c:import>
					</div>
				</form:form>
			</c:when>
			<c:otherwise>
				<h4 class="titol-consulta" style="display:inline">${expedientTipus.nom}</h4>&nbsp;&nbsp;&nbsp;
				<form action="consultaTasques.html" method="post" style="display:inline">
					<input type="hidden" name="canviar" id="canviar" value="true"/>
					<button type="submit" class="submitButton"><fmt:message key='expedient.consulta.canviar' /></button>
				</form>
			</c:otherwise>
		</c:choose>
	</div>

	<c:if test="${not empty expedientTipus}">
		<form:form action="consultaTasquesResultat.html" commandName="commandFiltre" cssClass="uniForm" method="post">
			<input type="hidden" name="texpedientId" id="texpedientId" value="${expedientTipus.id}">
			<div class="inlineLabels col first">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="tascaId"/>
					<c:param name="type" value="select"/>
					<c:param name="classHolder" value="reduit"/>
					<c:param name="items" value="tasques"/>
					<c:param name="itemLabel" value="nom"/>
					<c:param name="itemValue" value="id"/>
					<c:param name="itemBuit">&lt;&lt; <fmt:message key='comuns.tasques' /> &gt;&gt;</c:param>
					<c:param name="label"><fmt:message key='common.filtres.tasca' /></c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="titol"/>
					<c:param name="classHolder" value="reduit"/>
					<c:param name="label"><fmt:message key='comuns.titol' /></c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="responsable"/>
					<c:param name="type" value="suggest"/>
					<c:param name="classHolder" value="reduit"/>
					<c:param name="label"><fmt:message key='expedient.consulta.responsable.tasca' /></c:param>
					<c:param name="suggestUrl"><c:url value="/persona/suggest.html"/></c:param>
					<c:param name="suggestText">${responsable.nomSencer}</c:param>
				</c:import>
			</div>
			<div class="inlineLabels col last">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="dataCreacioInici"/>
					<c:param name="type" value="custom"/>
					<c:param name="label"><fmt:message key='common.filtres.data_creacio' /></c:param>
					<c:param name="customClass">customField</c:param>
					<c:param name="content">
						<spring:bind path="dataCreacioInici">
							<label for="dataCreacioInici" class="blockLabel">
								<span><fmt:message key='common.filtres.entre' /></span>
								<input id="dataCreacioInici" name="dataCreacioInici" value="${status.value}" type="text" class="textInput"/>
								<script type="text/javascript">
									// <![CDATA[
									$(function() {
										$.datepicker.setDefaults($.extend({
											dateFormat: 'dd/mm/yy',
											changeMonth: true,
											changeYear: true
										}));
										$("#dataCreacioInici").datepicker();
									});
									// ]]>
								</script>
							</label>
						</spring:bind>
						<spring:bind path="dataCreacioFi">
							<label for="dataCreacioFi" class="blockLabel blockLabelLast">
								<span><fmt:message key='common.filtres.i' /></span>
								<input id="dataCreacioFi" name="dataCreacioFi" value="${status.value}" type="text" class="textInput"/>
								<script type="text/javascript">
									// <![CDATA[
									$(function() {
										$.datepicker.setDefaults($.extend({
											dateFormat: 'dd/mm/yy',
											changeMonth: true,
											changeYear: true
										}));
										$("#dataCreacioFi").datepicker();
									});
									// ]]>
								</script>
							</label>
						</spring:bind>
					</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="mostrarTasquesGrup"/>
					<c:param name="type" value="checkbox"/>
					<c:param name="label"><fmt:message key='expedient.consulta.mostrar.tasques.grup' /></c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,clean<c:if test="${not empty llistat}">,massiu</c:if></c:param>
					<c:param name="titles"><fmt:message key='common.filtres.consultar' />,<fmt:message key='common.filtres.netejar' /><c:if test="${not empty llistat}">,<fmt:message key='expedient.consulta.massiva.accions' /></c:if></c:param>
				</c:import>
			</div>
			<div class="ctrlHolder" align="right">
				<c:set var="opp"><c:if test='${empty objectsPerPage}'>20</c:if><c:if test='${not empty objectsPerPage}'>${objectsPerPage}</c:if></c:set>
				<select id="objectsPerPage" name="objectsPerPage" class="objectsPerPage<c:if test='${not empty consulta}'> ${copp}</c:if>">
					<option value="10"<c:if test='${opp == "10"}'> selected="selected"</c:if>>10</option>
					<option value="20"<c:if test='${opp == "20"}'> selected="selected"</c:if>>20</option>
					<option value="50"<c:if test='${opp == "50"}'> selected="selected"</c:if>>50</option>
					<option value="100"<c:if test='${opp == "100"}'> selected="selected"</c:if>>100</option>
					<option value="999999999"<c:if test='${opp == "999999999"}'> selected="selected"</c:if>>Tots</option>
				</select>
				<label for="objectsPerPage" class="objectsPerPage<c:if test='${not empty consulta}'> ${copp}</c:if>"><fmt:message key="comuns.objectsPerPage"/></label>
			</div>
		</form:form>
	</c:if>
	
	<c:if test="${not empty llistat}">
		<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable" sort="external" defaultorder="descending">
			<display:column title="<input id='selTots' type='checkbox' value='false' onclick='selTots()'>" style="${filaStyle}" >
				<c:set var="tascaSeleccionada" value="${false}"/>
				<c:forEach var="tid" items="${sessionScope.TascaConsultaIdsMassius}" varStatus="status">
					<c:if test="${status.index gt 0 and tid == registre.id}"><c:set var="tascaSeleccionada" value="${true}"/></c:if>
				</c:forEach>
				<input type="checkbox" name="tascaId" value="${registre.id}"<c:if test="${tascaSeleccionada}"> checked="checked"</c:if> onclick="clicCheckMassiu(event)"/>
			</display:column>
			<display:column property="id" title="Id" sortable="true" sortName="id" sortProperty="id"/>
			<display:column property="titol" titleKey="tasca.gllistat.tasca" sortable="true" sortName="titol" sortProperty="titol"/>
			<display:column title="Responsable">
				<c:choose>
					<c:when test="${empty registre.responsable}">
						<c:forEach var="actor" items="${registre.responsables}" varStatus="status">
							${registre.personesMap[actor].nomSencer}<c:if test="${not status.last}">, </c:if>
						</c:forEach>
					</c:when>
					<c:otherwise>${registre.personesMap[registre.responsable].nomSencer}</c:otherwise>
				</c:choose>
			</display:column>
			<display:column sortProperty="expedientTitol" titleKey="tasca.gllistat.expedient" sortable="true" sortName="expedientTitol">
				<a href="<c:url value="/expedient/info.html"><c:param name="id" value="${registre.expedientProcessInstanceId}"/></c:url>">${registre.expedientTitol}</a>
			</display:column>
			<display:column property="dataCreacio" titleKey="tasca.gllistat.creada_el" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true" sortName="dataCreacio" sortProperty="dataCreacio"/>
			<display:column title="Flags">
				<c:if test="${registre.cancelada}">C</c:if>
				<c:if test="${registre.suspesa}">S</c:if>
			</display:column>
			<display:column>
			<c:if test="${registre.oberta}">
				<security:accesscontrollist domainObject="${expedientTipus}" hasPermission="16,2,512">
					<a href="<c:url value="/expedient/tascaReassignar.html"><c:param name="id" value="${registre.expedientProcessInstanceId}"/><c:param name="taskId" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/user_go.png"/>" alt="Reassignar" title="Reassignar" border="0"/></a>
				</security:accesscontrollist>
			</c:if>
			</display:column>
			<display:column>
				<c:if test="${registre.agafada and registre.oberta}">
					<security:accesscontrollist domainObject="${expedientTipus}" hasPermission="16,2,512">
						<a href="<c:url value="/expedient/tascaAlliberar.html"><c:param name="id" value="${registre.expedientProcessInstanceId}"/><c:param name="taskId" value="${registre.id}"/></c:url>" onclick="return confirmarAlliberar(event)"><img src="<c:url value="/img/link_break.png"/>" alt="<fmt:message key="tasca.pllistat.alliberar"/>" title="<fmt:message key="tasca.pllistat.alliberar"/>" border="0"/></a>
					</security:accesscontrollist>
				</c:if>
			</display:column>
			<display:column>
				<c:if test="${registre.oberta}">
					<security:accesscontrollist domainObject="${expedientTipus}" hasPermission="16,2">
						<c:choose>
							<c:when test="${not registre.suspesa}">
								<a href="<c:url value="/expedient/tascaSuspendre.html"><c:param name="id" value="${registre.expedientProcessInstanceId}"/><c:param name="taskId" value="${registre.id}"/></c:url>" onclick="return confirmarSuspendre(event)"><img src="<c:url value="/img/control_pause_blue.png"/>" alt="Suspendre" title="Suspendre" border="0"/></a>
							</c:when>
							<c:otherwise>
								<a href="<c:url value="/expedient/tascaReprendre.html"><c:param name="id" value="${registre.expedientProcessInstanceId}"/><c:param name="taskId" value="${registre.id}"/></c:url>" onclick="return confirmarReprendre(event)"><img src="<c:url value="/img/control_play_blue.png"/>" alt="Reprendre" title="Reprendre" border="0"/></a>
							</c:otherwise>
						</c:choose>
					</security:accesscontrollist>
				</c:if>
			</display:column>
			<display:column>
				<c:if test="${registre.oberta}">
					<security:accesscontrollist domainObject="${expedientTipus}" hasPermission="16,2">
						<a href="<c:url value="/expedient/tascaCancelar.html"><c:param name="id" value="${registre.expedientProcessInstanceId}"/><c:param name="taskId" value="${registre.id}"/></c:url>" onclick="return confirmarCancelar(event)"><img src="<c:url value="/img/stop.png"/>" alt="Cancel·lar" title="Cancel·lar" border="0"/></a>
					</security:accesscontrollist>
				</c:if>
			</display:column>
		</display:table>
	</c:if>
</body>
</html>
