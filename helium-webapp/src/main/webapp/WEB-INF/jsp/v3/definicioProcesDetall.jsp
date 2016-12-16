<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>


<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>

<style type="text/css">
	#definicioProces-info dt {
		color: #999;
		font-style: italic;
		font-weight: normal;
		float: left;
		width: 20em;
		margin: 0 0 0 0;
		padding: .2em .5em;
	}
	#definicioProces-info dd {
		font-size: medium;
		font-weight: bold;
		margin: 0 0 0 0;
		padding: .2em .5em;
	}
	#definicioProces-info-accio {
		margin-top: 1em;
	}
</style>

<c:choose>
	<c:when test="${not empty definicioProces}">		
		<form class="well">
			<div id="definicioProces-info" class="row">
				<div class="col-md-12">
					<dl>
						<dt><spring:message code="definicio.proces.detall.camp.id"/></dt>
						<dd>${definicioProces.idPerMostrar}</dd>
						<dt><spring:message code="definicio.proces.detall.camp.codi"/></dt>
						<dd>${definicioProces.jbpmKey}</dd>				
						<dt><spring:message code="definicio.proces.detall.camp.versio"/></dt>
						<dd>${definicioProces.versio}</dd>
						<c:if test="${not empty definicioProces.etiqueta}">
							<dt><spring:message code="definicio.proces.detall.camp.versio"></spring:message></dt>
							<dd>${definicioProces.etiqueta}</dd>
						</c:if>										
						<c:if test="${not empty definicioProces.expedientTipus}">
							<dt><spring:message code="definicio.proces.detall.camp.tipus.expedient"></spring:message></dt>
							<dd><a href="../expedientTipus/${definicioProces.expedientTipus.id}">
									${definicioProces.expedientTipus.codi} - ${definicioProces.expedientTipus.nom}
								</a></dd>
						</c:if>										
						<dt><spring:message code="definicio.proces.detall.camp.dataCreacio"/></dt>
						<dd><fmt:formatDate value="${definicioProces.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></dd>
						<!-- sub definicions de procés -->				
						<c:if test="${fn:length(subDefinicionsProces) gt 0}">
							<dt><spring:message code="definicio.proces.detall.subdef"/></dt>
							<dd>
								<c:forEach var="subdp" items="${subDefinicionsProces}" varStatus="status">${subdp.idPerMostrar}<c:if test="${not status.last}">, </c:if></c:forEach>
							</dd>
						</c:if>
					</dl>
				</div>
			</div>
		</form>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='definicio.proces.detall.cap'/></div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
// <![CDATA[            

$(document).ready(function() {		
});

// ]]>
</script>			
