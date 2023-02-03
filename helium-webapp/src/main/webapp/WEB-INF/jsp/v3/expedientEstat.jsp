<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!-- Contingut de la pipella "Estat" per expedients basats en estats. Mostra els logs de
tipus de canvi d'estat ordenat per dada. -->
<style>
</style>

<script type="text/javascript">
//<![CDATA[
           $(document).ready(function(){
        	  $('#taulaEstats').dataTable(); 
           });
//]]>
</script>


	<table id="taulaEstats_${expedient.id}" class="table tableNotificacions table-bordered">
	<thead>
		<tr>		
			<th><spring:message code="expedient.estat.data"/></th>
			<th><spring:message code="expedient.estat.estat"/></th>
			<th><spring:message code="expedient.estat.responsable"/></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="log" items="${logs}">
		<tr id="tr_log_${log.id}">
		<tr>
			<td><fmt:formatDate value="${log.data}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate></td>
			<td>${log.accioParams}</td>
			<td>${log.usuari}</td>
		</tr>
		</c:forEach>
	</tbody>
	</table>
