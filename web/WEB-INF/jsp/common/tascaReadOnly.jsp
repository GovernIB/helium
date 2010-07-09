<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
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
					<a href="<c:url value="/tasca/documentDescarregar.html"><c:param name="id" value="${tasca.id}"/><c:param name="codi" value="${document.document.codi}"/></c:url>"><img src="<c:url value="/img/page_white_put.png"/>" alt="Descarregar" title="Descarregar" border="0"/></a>
				</c:if>
				</h4><br/>
			</c:if>
		</c:forEach>
	</c:if>
	<c:if test="${hiHaCampsReadOnly}">
		<h4 class="titol-missatge">Dades de referència</h4>
		<form:form cssClass="uniForm" commandName="commandReadOnly">
			<div class="inlineLabels">
				<c:if test="${not empty tasca.camps}">
					<c:forEach var="camp" items="${tasca.camps}">
						<c:if test="${camp.readOnly}">
							<c:set var="campTascaActual" value="${camp}" scope="request"/>
							<c:import url="../common/campTasca.jsp"/>
						</c:if>
					</c:forEach>
				</c:if>
			</div>
		</form:form>
	</c:if>
</div>
</c:if>
