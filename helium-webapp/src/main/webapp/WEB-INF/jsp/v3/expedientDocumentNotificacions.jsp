<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title>
		<c:choose>
			<c:when test="${document.adjunt}">
				<spring:message code="expedient.document.notificacions.titol.adjunt" arguments="${document.adjuntTitol}"/>
			</c:when>
			<c:otherwise>
				<spring:message code="expedient.document.notificacions.titol.document" arguments="${document.documentNom}"/>
			</c:otherwise>
		</c:choose>
	</title>
	<hel:modalHead/>
</head>
<body>


	<c:choose>
		<c:when test="${not empty notificacions}">
			<table id="notificacions_${expedient.id}" class="table tableNotificacions table-bordered">
				<thead>
					<tr>
						<th><spring:message code="expedient.notificacio.data_enviament"/></th>
						<th><spring:message code="expedient.notificacio.enviamentTipus"/></th>
						<th><spring:message code="expedient.notificacio.concepte"/></th>
						<th><spring:message code="expedient.notificacio.estat.notificacio"/></th>
						<th><spring:message code="expedient.notificacio.estat.enviament"/></th>
						<th><spring:message code="expedient.notificacio.titular"/></th>
						<th><spring:message code="expedient.notificacio.destinatari"/></th>
						<th style="width:20%"><spring:message code="expedient.notificacio.document"/></th>
						<th><spring:message code="expedient.notificacio.justificant"/></th>
	 					<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${notificacions}" var="notificacio">
						<tr id="tr_notificacio_${notificacio.id}">
							<td><fmt:formatDate value="${notificacio.enviatData}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate></td>
						    <td><spring:message code="notifica.enviament.tipus.enum.${notificacio.enviamentTipus}"/></td>
						    <td>${notificacio.concepte}</td>
							<td>
								<span title="<spring:message code="notificacio.etst.enum.${notificacio.estat}.info"/>">
									<c:choose>
									<c:when test="${notificacio.estat == 'PENDENT'}">
										<span class="fa fa-clock-o"></span>
									</c:when>
									<c:when test="${notificacio.estat == 'ENVIADA'}">
										<span class="fa fa-send-o"></span>
										<span class="label label-warning"></span>
									</c:when>
									<c:when test="${notificacio.estat == 'REGISTRADA'}">
										<span class="fa fa-file-o"></span>
									</c:when>
									<c:when test="${notificacio.estat == 'FINALITZADA'}">
										<span class="fa fa-check"></span>
									</c:when>							
									<c:when test="${notificacio.estat == 'PROCESSADA'}">
										<span class="fa fa-check-circle"></span>
									</c:when>
									</c:choose>
									<spring:message code="notificacio.etst.enum.${notificacio.estat}"/>
								</span>
							</td>
							<td>
								<c:if test="${notificacio.error }">
									<span class="fa fa-warning text-danger" title="${notificacio.errorDescripcio}"></span>
								</c:if>
								<c:if test="${not empty notificacio.enviaments[0].estat}">
									<spring:message code="notificacio.enviament.estat.enum.${notificacio.enviaments[0].estat}"/>
								</c:if>
								<c:if test="${notificacio.enviaments[0].estatData != null}">
									<br/><span class="text-muted small">
											<fmt:formatDate value="${notificacio.enviaments[0].estatData}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate>
										</span>
								</c:if>
							</td>
							<td>${notificacio.enviaments[0].titular.dni} - ${notificacio.enviaments[0].titular.nomSencer}</td>
							<td>
								<c:if test="${not empty notificacio.enviaments[0].destinataris[0]}">
									${notificacio.enviaments[0].destinataris[0].dni} - ${notificacio.enviaments[0].destinataris[0].nomSencer}
								</c:if>
							</td>
							<td>
								<a href="<c:url value="/v3/expedient/${expedient.id}/proces/${expedient.processInstanceId}/document/${notificacio.documentId}/descarregar"/>"
									title="<spring:message code="expedient.notificacio.descarregar.doc"/> ${notificacio.documentArxiuNom}">
									${notificacio.documentNom != null? notificacio.documentNom : notificacio.documentArxiuNom}
									<span class="fa fa-download fa-lg"></span>
								</a>
								<c:if test="${not empty notificacio.documentsDinsZip}">
								<br/><span class="">Conté:</span>
									<c:forEach items="${notificacio.documentsDinsZip}" var="docContingut">
										<div id="docContingut_${docContingut.id}" class="">	
												-<a href="<c:url value="/v3/expedient/${expedient.id}/proces/${expedient.processInstanceId}/document/${docContingut.id}/descarregar"/>"
												title="<spring:message code="expedient.notificacio.descarregar.doc"/> ${docContingut.nom}">
												${docContingut.nom != null? docContingut.nom : docContingut.id}
												<span class="fa fa-download fa-lg"></span>
												</a>			
										</div>
									</c:forEach>
								</c:if>					
							</td>
							<td>
								<c:if test="${not empty notificacio.justificantId}">
									<center>
									<a href="<c:url value="/v3/expedient/${expedient.id}/proces/${expedient.processInstanceId}/document/${notificacio.justificantId}/descarregar"/>">
										<span class="fa fa-download fa-lg" title="${notificacio.justificantArxiuNom}"></span>
									</a>
									</center>
								</c:if>
							</td>
							<td>
								<a id="refrescar_estat_enviament_${notificacio.id}" 
									href="<c:url value="/v3/expedient/${expedient.id}/notificacions/${notificacio.id}/consultarEstat"/>" 
									class="btn-refrescar-estat-enviament btn btn-default"
									title="<spring:message code="expedient.notificacio.consultar.estat.info"/>">
										<span class="fa fa-refresh"></span>
								</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:when>
		<c:otherwise>
			<div class="well well-small"><spring:message code='expedient.notificacio.expedient.cap' /></div>
		</c:otherwise>
	</c:choose>
	<script type="text/javascript">
		// <![CDATA[
		$(document).ready(function() {
			$('.btn-refrescar-estat-enviament').click(function(e) {
				$(this).find('span').addClass('fa-spin');
			})
		});
		//]]>
	</script>



	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.tancar"/></button>
	</div>
</body>
</html>
