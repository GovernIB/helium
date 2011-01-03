<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<c:set var="hiHaCampsReadOnly" value="${false}"/>
<c:forEach var="camp" items="${tasca.camps}"><c:if test="${camp.readOnly}"><c:set var="hiHaCampsReadOnly" value="${true}"/></c:if></c:forEach>
<c:set var="hiHaDocumentsReadOnly" value="${false}"/>
<c:forEach var="document" items="${tasca.documents}"><c:if test="${document.readOnly}"><c:set var="hiHaDocumentsReadOnly" value="${true}"/></c:if></c:forEach>
<c:if test="${hiHaCampsReadOnly or hiHaDocumentsReadOnly}">
<div class="missatgesBlau">
	<c:if test="${hiHaDocumentsReadOnly}">
		<c:forEach var="document" items="${tasca.documents}">
			<c:if test="${document.readOnly}">
				<h4 class="titol-missatge">
				${document.document.nom}&nbsp;&nbsp;
				<c:if test="${not empty tasca.varsDocuments[document.document.codi]}">
					<c:set var="tascaActual" value="${tasca}" scope="request"/>
					<c:set var="documentActual" value="${tasca.varsDocuments[document.document.codi]}" scope="request"/>
					<c:set var="codiDocumentActual" value="${document.document.codi}" scope="request"/>
					<c:import url="../common/iconesConsultaDocument.jsp"/>
				</c:if>
				</h4><br/>
			</c:if>
		</c:forEach>
	</c:if>
	<c:if test="${hiHaCampsReadOnly}">
		<h4 class="titol-missatge">Dades de referència</h4>
		<form:form cssClass="uniForm tascaForm" commandName="commandReadOnly">
			<div class="inlineLabels">
				<c:if test="${not empty tasca.camps}">
					<c:forEach var="camp" items="${tasca.camps}">
						<c:if test="${camp.readOnly}">
							<c:set var="campTascaActual" value="${camp}" scope="request"/>
							<c:import url="../common/campTasca.jsp"/>
							<div style="clear:both"></div>
						</c:if>
					</c:forEach>
				</c:if>
			</div>
		</form:form>
	</c:if>
</div>
</c:if>
