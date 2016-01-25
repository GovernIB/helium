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
				<th><spring:message code="alerta.llistat.expedient"/></th>
				<th><spring:message code="alerta.llistat.causa"/></th>
				<th><spring:message code="alerta.llistat.prioritat"/></th>
				<th> </th>
				<th> </th>
			</tr>
		</thead>
		<tbody>
		<c:forEach var="alerta" items="${alertes}">
			<tr>
				<td><fmt:formatDate value="${alerta.dataCreacio}" pattern="dd/MM/yyyy"/></td>
				<td>${alerta.expedient.numero}</td>
				<td>${alerta.causa}</td>
				<td>${alerta.prioritat}</td>
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