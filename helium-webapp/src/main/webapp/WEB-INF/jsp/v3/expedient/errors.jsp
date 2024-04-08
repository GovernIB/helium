<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="expedient.accio.errors.titol"/></title>
	<hel:modalHead/>
	
	<style type="text/css">
		.margin-content {
			margin-top: 10px;
		}
	</style>
	
	<script type="text/javascript">
	
		$(document).ready(function() {
			
// 			$("#btnNetejarErrors").heliumEvalLink({
// 				refrescarAlertes: true,
// 				refrescarPagina: false,
// 				alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
// 			});
			
			$("#btnNetejarErrors").click(function(event) {
				netejarErrorsExp();
			});
			
		});
	
		function netejarErrorsExp() {
			if (confirm("<spring:message code="boto.eliminar.errors.confirm"/>")) {
				$.ajax({
					url: "<c:url value="/v3/expedient/${expedientId}/netejarErrorsExp"/>",
				    type:'GET',
				    dataType: 'text',
				    cache: false,
				    beforeSend: function(msg){
				    	var options = '<option value=""><fmt:message key="js.helforms.carreg_dades"/></option>';
				    	$("select#estat0").html(options).attr('class', 'inlineLabels');
					},
				    success: function(json) {
				    	window.parent.modalTancar(window.frameElement, true);
				    },
				    error: function(jqXHR, textStatus, errorThrown) {
				    	console.log("Error al actualitzar la llista d'estats: [" + textStatus + "] " + errorThrown);
	
				    	var options = '<option value="">&lt;&lt; <fmt:message key="expedient.consulta.select.estat"/> &gt;&gt;</option>';
				    	$("select#estat0").html(options).attr('class', 'inlineLabels');
				    }
				});
			}
		}
	</script>
	
</head>
<body>
	<div>
	  <!-- Nav tabs -->
	  <ul class="nav nav-tabs" role="tablist">
	    <li role="presentation" class="active"><a href="#basic" aria-controls="basic" role="tab" data-toggle="tab"><spring:message code="error.tipus.basic"/></a></li>
	    <li role="presentation"><a href="#integracions" aria-controls="integracions" role="tab" data-toggle="tab"><spring:message code="error.tipus.integracions"/></a></li>
	  </ul>
	  
	  <!-- Tab panes -->
	  <div class="tab-content margin-content">
	    <div role="tabpanel" class="tab-pane active" id="basic">
	    	<c:choose>
				<c:when test="${not empty errors_bas}">
			    	<c:forEach var="error_bas" items="${errors_bas}">
			    		<h5>${error_bas.desc}</h5>
						<div class="well">${error_bas.text}</div>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<div class="well"><spring:message code="error.tipus.basic.no"/>...</div>
				</c:otherwise>
			</c:choose>
	    </div>
	    <div role="tabpanel" class="tab-pane" id="integracions">
			<c:choose>
				<c:when test="${not empty errors_int}">
			    	<c:forEach var="error_int" items="${errors_int}">
						<div class="well">${error_int.text}</div>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<div class="well"><spring:message code="error.tipus.integracions.no"/>...</div>
				</c:otherwise>
			</c:choose>
	    </div>
	    <div id="modal-botons">
	    	<button class="btn btn-primary" id="btnNetejarErrors" ><span class="fa fa-eraser"></span>&nbsp;<spring:message code="boto.eliminar.errors"/></button>
	    </div>
	  </div>
	</div>
</body>
</html>