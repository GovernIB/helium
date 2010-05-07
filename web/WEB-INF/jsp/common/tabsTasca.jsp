<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script type="text/javascript">
// <![CDATA[
function confirmarDelegacioCancel(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu cancel·lar la delegació?");
}
// ]]>
</script>

<c:if test="${tasca.delegada}">
	<div class="missatgesGris">
		<h3 class="titol-tab titol-delegacio">
			<c:choose>
				<c:when test="${tasca.delegacioOriginal}">Heu delegat aquesta tasca</c:when>
				<c:otherwise>Aquesta tasca vos ha estat delegada</c:otherwise>
			</c:choose>
		</h3>
		<dl class="form-info">
			<c:choose>
				<c:when test="${tasca.delegacioOriginal}">
					<dt>Destinatari</dt><dd>${tasca.delegacioPersona.nomSencer}</dd>
				</c:when>
				<c:otherwise>
					<dt>Remitent</dt><dd>${tasca.delegacioPersona.nomSencer}</dd>
				</c:otherwise>
			</c:choose>
			<dt>Data</dt><dd><fmt:formatDate value="${tasca.delegacioData}" pattern="dd/MM/yyyy HH:mm"/></dd>
			<dt>Comentari</dt><dd>${tasca.delegacioComentari}<c:if test="${empty comentari}">(Sense comentari)</c:if></dd>
			<dt>Supervisada</dt><dd><c:choose><c:when test="${tasca.delegacioSupervisada}">Si</c:when><c:otherwise>No</c:otherwise></c:choose></dd>
		</dl>
		<c:if test="${tasca.delegacioOriginal}">
			<form:form action="delegacioCancelar.html" cssClass="uniForm" onsubmit="return confirmarDelegacioCancel(event)">
				<form:hidden path="taskId"/>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit</c:param>
					<c:param name="titles">Cancel·lar delegació</c:param>
				</c:import>
			</form:form>
		</c:if>
	</div>
</c:if>
<c:if test="${not tasca.delegada or not tasca.delegacioOriginal}">
	<ul id="tabnav">
		<li<c:if test="${param.tabActiu == 'info'}"> class="active"</c:if>><a href="<c:url value="/tasca/info.html"><c:param name="id" value="${param.id}"/></c:url>">Tasca</a></li>
		<c:if test="${not empty tasca.camps}">
			<li class="<c:if test="${not tasca.validada}">warn</c:if> <c:if test="${param.tabActiu == 'form'}">active</c:if>"><a href="<c:url value="/tasca/form.html"><c:param name="id" value="${param.id}"/></c:url>">Dades</a></li>
		</c:if>
		<c:if test="${not empty tasca.documents and tasca.documentsNotReadOnly}">
			<li class="<c:if test="${not tasca.documentsComplet}">warn</c:if> <c:if test="${param.tabActiu == 'documents'}">active</c:if>"><a href="<c:url value="/tasca/documents.html"><c:param name="id" value="${param.id}"/></c:url>">Documents</a></li>
		</c:if>
		<c:if test="${not empty tasca.signatures}">
			<li class="<c:if test="${not tasca.signaturesComplet}">warn</c:if> <c:if test="${param.tabActiu == 'firmes'}">active</c:if>"><a href="<c:url value="/tasca/signatures.html"><c:param name="id" value="${param.id}"/></c:url>">Signatures</a></li>
		</c:if>
	</ul>
</c:if>
