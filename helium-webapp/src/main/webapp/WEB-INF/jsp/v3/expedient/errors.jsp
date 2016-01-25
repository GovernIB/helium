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
		.min-body {
			height:600px;
		}
		.margin-content {
			margin-top: 10px;
		}
	</style>
</head>
<body>
	<div class=min-body>
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
	  </div>
	</div>
</body>
</html>