<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<c:if test="${errorMsg != null }">
	<div class="alert alert-danger" role="alert">
		<span class="fa fa-exclamation-triangle"></span> ${errorMsg}
	</div>
</c:if>

<c:forEach var="firma" items="${firmes}" varStatus="status">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">
				<spring:message code="anotacio.annex.detalls.camp.firma"/> ${status.index + 1}
				<c:if test="${firma.autofirma}">
					(<spring:message code="anotacio.annex.detalls.camp.firma.autoFirma"/> 
						<span class="fa fa-info-circle" title="<spring:message code="anotacio.annex.detalls.camp.firma.autoFirma.info" />"></span>)
				</c:if>
			</h3>
		</div>
		<table class="table table-bordered">
		<tbody>
			<tr>
				<td><strong><spring:message code="anotacio.annex.detalls.camp.firmaTipus"/></strong></td>
				<td><spring:message code="nti.tipo.firma.${firma.tipus}"/></td>
			</tr>
			<tr>
				<td><strong><spring:message code="anotacio.annex.detalls.camp.firmaPerfil"/></strong></td>
				<td>${firma.perfil}</td>
			</tr>
			<c:if test="${firma.tipus != 'PADES' and firma.tipus != 'CADES_ATT' and firma.tipus != 'XADES_ENV'}">
				<tr>
					<td><strong><spring:message code="anotacio.annex.detalls.camp.fitxer"/></strong></td>
					<td>
						${firma.fitxerNom}
						<a href="<c:url value="/modal/expedientPeticio/descarregarFirma/${annexId}"/>" class="btn btn-default btn-sm pull-right">
							<span class="fa fa-download"  title="<spring:message code="anotacio.annex.detalls.camp.fitxer.descarregar"/>"></span>
						</a>
					</td>
				</tr>
			</c:if>
			<c:if test="${not empty firma.csvRegulacio}">
				<tr>
					<td><strong><spring:message code="anotacio.annex.detalls.camp.firmaCsvRegulacio"/></strong></td>
					<td>${firma.csvRegulacio}</td>
				</tr>
			</c:if>
			<c:if test="${not empty firma.detalls}">
				<tr>
					<td><strong><spring:message code="anotacio.annex.detalls.camp.firmaDetalls"/></strong></td>
					<td>
						<table class="table teble-striped table-bordered">
						<thead>
							<tr>
								<th><spring:message code="anotacio.annex.detalls.camp.firmaDetalls.data"/></th>
								<th><spring:message code="anotacio.annex.detalls.camp.firmaDetalls.nif"/></th>
								<th><spring:message code="anotacio.annex.detalls.camp.firmaDetalls.nom"/></th>
								<th><spring:message code="anotacio.annex.detalls.camp.firmaDetalls.emissor"/></th>
							</tr>
						<tbody>
						<c:forEach var="detall" items="${firma.detalls}">	
							<tr>
								<td>
									<c:if test="${not empty detall.data}"><fmt:formatDate value="${detall.data}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if>
									<c:if test="${empty detall.data}"><spring:message code="anotacio.annex.detalls.camp.firmaDetalls.data.nd"/></c:if>
								</td>
								<td>${detall.responsableNif}</td>
								<td>${detall.responsableNom}</td>
								<td>${detall.emissorCertificat}</td>
							</tr>
						</c:forEach>
						</tbody>
						</table>
					</td>
				</tr>
			</c:if>
		</tbody>
		</table>
	</div>
</c:forEach>