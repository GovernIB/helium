<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script type="text/javascript">
// <![CDATA[
function confirmarDelegacioCancel(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='common.tabstasca.confirmacio' />");
}
// ]]>
</script>

<c:if test="${not empty seleccioMassiva}">
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<div class="missatgesBlau">
		<h3 class="titol-tab titol-massiva"><fmt:message key="tasca.info.massiva"/> <img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="<fmt:message key="tasca.info.mos_ocul"/>" title="<fmt:message key="tasca.info.mos_ocul"/>" border="0" onclick="mostrarOcultar(this,'info-massiva')"/></h3>
		<div id="info-massiva" style="display:none">
			<display:table name="seleccioMassiva" id="registre" requestURI="" class="displaytag selectable">
				<display:column property="titol" titleKey="tasca.pllistat.tasca"/>
				<display:column sortProperty="expedientTitol" titleKey="tasca.pllistat.expedient" sortable="true">
					<a href="<c:url value="/expedient/info.html"><c:param name="id" value="${registre.expedientProcessInstanceId}"/></c:url>">${registre.expedientTitol}</a>
				</display:column>
				<display:column property="expedientTipusNom" titleKey="comuns.tipus_exp" sortable="true"/>
				<display:column property="dataCreacio" titleKey="tasca.pllistat.creada_el" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true"/>
				<display:column titleKey="tasca.pllistat.prioritat" sortable="true">
					<c:choose>
						<c:when test="${registre.prioritat == 2}"><fmt:message key='tasca.pllistat.m_alta' /></c:when>
						<c:when test="${registre.prioritat == 1}"><fmt:message key='tasca.pllistat.alta' /></c:when>
						<c:when test="${registre.prioritat == 0}"><fmt:message key='tasca.pllistat.normal' /></c:when>
						<c:when test="${registre.prioritat == -1}"><fmt:message key='tasca.pllistat.baixa' /></c:when>
						<c:when test="${registre.prioritat == -2}"><fmt:message key='tasca.pllistat.m_baixa' /></c:when>
						<c:otherwise>${registre.prioritat}</c:otherwise>
					</c:choose>
				</display:column>
				<c:choose>
					<c:when test="${not empty terminisIniciats[registre_rowNum - 1] and terminisIniciats[registre_rowNum - 1].estat == 'NORMAL'}"><c:set var="estilData">color:white;background-color:green</c:set></c:when>
					<c:when test="${not empty terminisIniciats[registre_rowNum - 1] and terminisIniciats[registre_rowNum - 1].estat == 'AVIS'}"><c:set var="estilData">color:white;background-color:orange</c:set></c:when>
					<c:when test="${not empty terminisIniciats[registre_rowNum - 1] and terminisIniciats[registre_rowNum - 1].estat == 'CADUCAT'}"><c:set var="estilData">color:white;background-color:red</c:set></c:when>
					<c:otherwise><c:set var="estilData"></c:set></c:otherwise>
				</c:choose>
				<display:column property="dataLimit" titleKey="tasca.pllistat.data_limit" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true" style="${estilData}"/>
			</display:table>
			<form action="massivaSeleccio.html" class="uniForm">
				<input type="hidden" name="id" value="${param.id}"/>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit</c:param>
					<c:param name="titles"><fmt:message key="common.tabstasca.massiva.canvi"/></c:param>
				</c:import>
			</form>
		</div>
	</div>
</c:if>

<c:if test="${tasca.delegada}">
	<div class="missatgesGris">
		<h3 class="titol-tab titol-delegacio">
			<c:choose>
				<c:when test="${tasca.delegacioOriginal}"><fmt:message key='common.tabstasca.heu_delegat' /></c:when>
				<c:otherwise><fmt:message key='common.tabstasca.delegada' />'</c:otherwise>
			</c:choose>
		</h3>
		<dl class="form-info">
			<c:choose>
				<c:when test="${tasca.delegacioOriginal}">
					<dt><fmt:message key='common.tabstasca.destinatari' /></dt><dd>${tasca.delegacioPersona.nomSencer}</dd>
				</c:when>
				<c:otherwise>
					<dt><fmt:message key='common.tabstasca.remitent' /></dt><dd>${tasca.delegacioPersona.nomSencer}</dd>
				</c:otherwise>
			</c:choose>
			<dt><fmt:message key='common.tabstasca.data' /></dt><dd><fmt:formatDate value="${tasca.delegacioData}" pattern="dd/MM/yyyy HH:mm"/></dd>
			<dt><fmt:message key='common.tabstasca.comentari' /></dt><dd>${tasca.delegacioComentari}<c:if test="${empty tasca.delegacioComentari}">(<fmt:message key='common.tabstasca.sense_com' />)</c:if></dd>
			<dt><fmt:message key='common.tabstasca.supervisada' /></dt><dd><c:choose><c:when test="${tasca.delegacioSupervisada}"><fmt:message key='comuns.si' /></c:when><c:otherwise><fmt:message key='comuns.no' /></c:otherwise></c:choose></dd>
		</dl>
		<c:if test="${tasca.delegacioOriginal}">
			<form:form action="delegacioCancelar.html" cssClass="uniForm" onsubmit="return confirmarDelegacioCancel(event)">
				<form:hidden path="taskId"/>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit</c:param>
					<c:param name="titles"><fmt:message key='common.tabstasca.cancelar' /></c:param>
				</c:import>
			</form:form>
		</c:if>
	</div>
</c:if>
<c:if test="${not tasca.delegada or not tasca.delegacioOriginal}">
	<ul id="tabnav">
		<li<c:if test="${param.tabActiu == 'info'}"> class="active"</c:if>><a href="<c:url value="/tasca/info.html"><c:param name="id" value="${param.id}"/></c:url>"><fmt:message key='common.tabstasca.tasca' /></a></li>
		<c:if test="${not empty tasca.camps}">
			<li class="<c:if test="${not tasca.validada}">warn</c:if> <c:if test="${param.tabActiu == 'form'}">active</c:if>"><a href="<c:url value="/tasca/form.html"><c:param name="id" value="${param.id}"/></c:url>"><fmt:message key='comuns.dades' /></a></li>
		</c:if>
		<c:if test="${not empty tasca.documents and tasca.documentsNotReadOnly}">
			<li class="<c:if test="${not tasca.documentsComplet}">warn</c:if> <c:if test="${param.tabActiu == 'documents'}">active</c:if>"><a href="<c:url value="/tasca/documents.html"><c:param name="id" value="${param.id}"/></c:url>"><fmt:message key='comuns.documents' /></a></li>
		</c:if>
		<c:if test="${not empty tasca.signatures}">
			<li class="<c:if test="${not tasca.signaturesComplet}">warn</c:if> <c:if test="${param.tabActiu == 'firmes'}">active</c:if>"><a href="<c:url value="/tasca/signatures.html"><c:param name="id" value="${param.id}"/></c:url>"><fmt:message key='common.tabstasca.signatures' /></a></li>
		</c:if>
	</ul>
</c:if>
