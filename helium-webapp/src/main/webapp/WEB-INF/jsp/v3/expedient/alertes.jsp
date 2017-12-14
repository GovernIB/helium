<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="expedient.consulta.alertes.totals"/></title>
	<hel:modalHead/>
<script type="text/javascript">
	function confirmar(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm('<spring:message code="alerta.llistat.confirmacio"/>');
	}
</script>
</head>
<body>
	<table class="table table-striped table-bordered" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0">
		<thead>
			<tr>
				<th><spring:message code="alerta.llistat.creada_el"/></th>
				<th><spring:message code="alerta.llistat.destinatari"/></th>
				<th><spring:message code="alerta.llistat.expedient"/></th>
				<th><spring:message code="alerta.llistat.causa"/></th>
				<th><spring:message code="alerta.llistat.prioritat"/></th>
				<th><spring:message code="alerta.llistat.data_limit"/></th>
				<th> </th>
				<th> </th>
			</tr>
		</thead>
		<tbody>
		<c:forEach var="alerta" items="${alertes}">
			<tr>
				<td><fmt:formatDate value="${alerta.dataCreacio}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
				<td>${persones[alerta.destinatari]}</td>
				<td>${alerta.expedient.numero}</td>
				<td>
					<c:choose>
						<c:when test="${not empty alerta.terminiIniciat}">
							<c:choose>
								<c:when test="${not empty alerta.terminiIniciat.taskInstanceId
												&& not (alerta.terminiIniciat.estat=='COMPLETAT_TEMPS')
												&& not (alerta.terminiIniciat.estat=='COMPLETAT_FORA')}">
									${alerta.terminiIniciat.termini.nom}
								</c:when>
								<c:otherwise>${alerta.terminiIniciat.termini.nom}</c:otherwise>
							</c:choose>
						</c:when>
						<c:when test="${not empty alerta.causa}">${alerta.causa}</c:when>
						<c:otherwise> - </c:otherwise>
					</c:choose>
				</td>
				
				<td style="<c:choose>
					<c:when test="${empty alerta.terminiIniciat}"></c:when>
					<c:when test="${alerta.terminiIniciat.estat=='AVIS'}">background-color:orange</c:when>
					<c:when test="${alerta.terminiIniciat.estat=='CADUCAT'}">color:white;background-color:darkred</c:when>
					<c:when test="${alerta.terminiIniciat.estat=='COMPLETAT_FORA'}">color:white;background-color:red</c:when>
					<c:otherwise>color:white;background-color:green</c:otherwise>
				</c:choose>">
					<c:choose>
						<c:when test="${not empty alerta.terminiIniciat}"><c:choose>
							<c:when test="${alerta.terminiIniciat.estat=='AVIS'}"><fmt:message key="alerta.llistat.apunt_dexpirar"/></c:when>
							<c:when test="${alerta.terminiIniciat.estat=='CADUCAT'}"><fmt:message key="alerta.llistat.expirat"/></c:when>
							<c:when test="${alerta.terminiIniciat.estat=='COMPLETAT_TEMPS'}"><fmt:message key="alerta.llistat.completat_atemps"/></c:when>
							<c:when test="${alerta.terminiIniciat.estat=='COMPLETAT_FORA'}"><fmt:message key="alerta.llistat.completat_fora"/></c:when>
							<c:otherwise>${alerta.text}</c:otherwise>
						</c:choose></c:when>
						<c:otherwise>${alerta.text}</c:otherwise>
					</c:choose>
				</td>
				<td>
				<c:if test="${not empty alerta.terminiIniciat}">
					<fmt:formatDate value="${alerta.terminiIniciat.dataFi}" pattern="dd/MM/yyyy"/>
				</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${empty alerta.dataLectura}"><a title="<spring:message code="alerta.llistat.marcar_com_llegida"/>" href="../../../v3/alerta/${alerta.id}/llegir"><span class="fa fa-eye fa-lg"></span></a></c:when>
						<c:otherwise><a title="<spring:message code="alerta.llistat.marcar_no_llegida"/>" href="../../../v3/alerta/${alerta.id}/noLlegir"><span class="fa fa-eye-slash fa-lg text-danger"></span></a></c:otherwise>
					</c:choose>
				</td>
				<td><a title="<spring:message code="comuns.esborrar"/>" href="../../../v3/alerta/${alerta.id}/esborrar" onclick="return confirmar(event)"><span class="fa fa-trash fa-lg"></span></a></td>
			</tr>
		</c:forEach>	
		</tbody>			       		
	</table>
</body>
</html>