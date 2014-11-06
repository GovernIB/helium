<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<style>
div.grup:hover {
	background-color: #e5e5e5 !important;
	border-color: #ccc !important;
}
div.grup .panel-body-grup {
	padding-bottom: 0px !important;
}
.panel-body-grup {
	margin: -1px;
}
</style>

<c:set var="paramCount" value="${param.count}"/>
<div class="missatgesBlau">		
	<div class="panel-group">
		<div class="panel panel-default">
		    <div id="grup-titol" data-toggle="collapse" class="panel-heading clicable grup" >
		    	<spring:message code="tasca.info.massiva"/>
				<div class="pull-right">
					<span class="icona-collapse fa fa-chevron-down"></span>
				</div>
				<c:if test="${not empty paramCount}"><span class="badge">${paramCount}</span></c:if>
		    </div>
		    <div id="grup-dades" class="panel-collapse collapse">
				<div class="panel-body">
					<div id="tabla"></div>
				</div>
		    </div>
		</div>
	</div>
</div>
<script>
$(document).ready(function() {
	$("#grup-titol").click(function(event) {
		$("#tabla").load('<c:url value="/nodeco/v3/expedient/massivaTramitacioTasca/taula"/>');
		$("#grup-dades").slideToggle("slow", function() {
			$('#grup-titol .icona-collapse').toggleClass('fa-chevron-down');
			$('#grup-titol .icona-collapse').toggleClass('fa-chevron-up');
		});
	});
});
</script>
