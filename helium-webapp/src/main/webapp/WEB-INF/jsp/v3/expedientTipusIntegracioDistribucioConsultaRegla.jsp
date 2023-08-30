<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>


<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="expedient.tipus.integracio.distribucio.consulta.regla.titol" arguments="${codiProcediment}"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>

	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>

	<hel:modalHead/>
</head>
<body>			
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
	</div>
	
	<div style="height: 300px;">
		<dl class="dl-horizontal">

			<dt><spring:message code="expedient.tipus.integracio.distribucio.consulta.regla.camp.creada"/></dt>
			<dd>
				<c:choose>
					<c:when test="${not empty regla}">
						<span class="fa fa-check" style="color:green;"></span>
					</c:when>
					<c:otherwise>
						<span class="fa fa-close" style="color:red;"></span>
					</c:otherwise>
				</c:choose>
			</dd>

			<c:if test="${not empty regla}">
				<dt><spring:message code="expedient.tipus.integracio.distribucio.consulta.regla.camp.id"/></dt>
				<dd>${regla.id}</dd>
				<dt><spring:message code="expedient.tipus.integracio.distribucio.consulta.regla.camp.nom"/></dt>
				<dd>${regla.nom}</dd>
				<dt><spring:message code="expedient.tipus.integracio.distribucio.consulta.regla.camp.entitat"/></dt>
				<dd>${regla.entitat}</dd>
				<dt><spring:message code="expedient.tipus.integracio.distribucio.consulta.regla.camp.backofficeDesti"/></dt>
				<dd>${regla.backofficeDesti}</dd>
				<dt><spring:message code="expedient.tipus.integracio.distribucio.consulta.regla.camp.presencial"/></dt>
				<dd>
					<c:choose>
						<c:when test="${regla.presencial == true}">
							Sí
						</c:when>
						<c:when test="${regla.presencial == false}">
							No
						</c:when>
						<c:otherwise>
							-
						</c:otherwise>
					</c:choose>
				</dd>
				<dt><spring:message code="expedient.tipus.integracio.distribucio.consulta.regla.camp.data"/></dt>
				<dd><fmt:formatDate value="${regla.data}" pattern="dd/MM/yyyy HH:mm:ss" /></dd>
				<dt><spring:message code="expedient.tipus.integracio.distribucio.consulta.regla.camp.activa"/></dt>
				<dd>
					<c:choose>
						<c:when test="${regla.activa}">
							<span class="fa fa-check" style="color:green;"></span>
							<a id="canviEstatReglaBtn" href="<c:url value="/v3/expedientTipus/${expedientTipusId}/integracioDistribucio/canviEstatRegla?codiProcediment=${codiProcediment}&activa=false"></c:url>" class="btn btn-xs btn-default btn-load" style="margin-left: 10px;">
								<span class="fa fa-close"></span> <spring:message code="expedient.tipus.integracio.distribucio.consulta.regla.accio.desactivar"/>
								<span id="accioCaviEstatReglaSpin" class="fa fa-cog fa-spin text-secondary" style="visibility: hidden; color: gray;"></span>
							</a>
						</c:when>
						<c:otherwise>
							<span class="fa fa-close" style="color:red;"></span>
							<a id="canviEstatReglaBtn" href="<c:url value="/v3/expedientTipus/${expedientTipusId}/integracioDistribucio/canviEstatRegla?codiProcediment=${codiProcediment}&activa=true"></c:url>" class="btn btn-xs btn-default btn-load" style="margin-left: 10px;">
								<span class="fa fa-check"></span> <spring:message code="expedient.tipus.integracio.distribucio.consulta.regla.accio.activar"/>
								<span id="accioCaviEstatReglaSpin" class="fa fa-cog fa-spin text-secondary" style="visibility: hidden; color: gray;"></span>
							</a>
						</c:otherwise>
					</c:choose>
				</dd>
			</c:if>			
		</dl>
		
	</div>
	
	<script type="text/javascript">
	// <![CDATA[
	
	$(document).ready(function() {
		$('#canviEstatReglaBtn').click(function(e){
	        $('#accioCaviEstatReglaSpin').css("visibility", "visible");
			var url = $(this).attr('href');
		    $.ajax({
		           type: "POST",
		           url: url,
					complete: function(){
						// Redirecciona a la finestra modal novament
						window.location.href=$(location).attr('href');
					}
		         });
			e.preventDefault();
			return false;
		});
	});
	
	// ]]>
	</script>	
</body>
		
