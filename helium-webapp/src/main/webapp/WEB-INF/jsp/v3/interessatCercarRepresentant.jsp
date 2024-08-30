<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="interessat.form.titol.cercar.representant"/></c:set>
<c:set var="formAction">search</c:set>


<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/helium.modal.js"/>"></script>

<style type="text/css">
body {
	min-height: 400px;
}
.tab-content {
    margin-top: 0.8em;
}
.icona-doc {
	color: #666666
}
.file-dt {
	margin-top: 9px;
}
.file-dd {
	margin-top: 3px;
}
tr.odd {
	background-color: #f9f9f9;
}
tr.detall {
/* 	background-color: cornsilk; */
}
tr.clicable {
	cursor: pointer;
}
</style>
<script>
//<![CDATA[ 
$(document).ready(function() {
	$(".desplegable").click(function(){
		$(this).find("span").toggleClass("fa-caret-up");
		$(this).find("span").toggleClass("fa-caret-down");
	});
});
function confirmarSeleccionarRepresentant(e, idRepresentant, idExpedient) {
	var idRow= 'row_'+idRepresentant;
	//alert(idRow);	
	//var repId = document.getElementById("representantSeleccionatId_"+idRepresentant).value;
	$('#representantSeleccionatId').val(idRepresentant);

	if (confirm("<spring:message code='interessat.controller.representant.confirmacio.seleccionar'/>")) {
		this.form.submit();
	}
}
//]]>
</script>
</head>

<body>
	<form:form cssClass="form-horizontal" action="${formAction}"  method="post" commandName="interessatCommand">
		<c:choose>
			<c:when test="${not empty representantsExpedient}">
					<input type="hidden" id="representantSeleccionatId" name="representantSeleccionatId">
			
					<table class="table table-bordered">
						<thead>
							<tr>
								<th style="width: 50px;"></th>
								<th style="width: 150px;"><spring:message code="interessat.form.camp.codi"/></th>
								<th style="width: 150px;"><spring:message code="interessat.form.camp.tipus"/></th>
								<th style="width: 150px;"><spring:message code="interessat.form.camp.document.identificatiu"/></th>
								<th style="width: 150px;"><spring:message code="interessat.form.camp.nom.rao.social"/></th>
								<th style="width: 150px;"><spring:message code="interessat.form.camp.dir3codi"/></th>
								<th style="width: 50px;"></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="interessat" items="${representantsExpedient}" varStatus="status">
								<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if> id="row_${interessat.id}">
									
									<td>
										<button type="submit" class="btn btn-success right" onclick="return confirmarSeleccionarRepresentant(event,'${interessat.id}','${expedientId}')">
												<spring:message code="comu.boto.seleccionar"/>
										</button>
									</td>
									<td>
										${interessat.codi}
									</td>
									<td>
										<spring:message code="interessat.tipus.enum.${interessat.tipus}"/>
									</td>
									<td>${interessat.documentIdent}</td>
									<c:choose>
										<c:when test="${interessat.tipus == 'FISICA'}">
											<td>${interessat.nom} ${interessat.llinatge1} ${interessat.llinatge2}</td>
										</c:when>
										<c:otherwise>
											<td>${interessat.raoSocial}</td>
										</c:otherwise>
									</c:choose>
									<td>${interessat.dir3Codi}</td>
									<td>
										<c:if test="${interessat.tipus != 'ADMINISTRACIO'}">
											<button type="button" class="btn btn-default desplegable" href="#detalls_${status.index}" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}">
												<span class="fa fa-caret-down"></span>
											</button>
										</c:if>
									</td>
									
									
								</tr>
								<tr class="collapse detall" id="detalls_${status.index}">
									<td colspan="6">
										<div class="row">
											<div class="col-xs-6">
												<dl class="dl-horizontal">
													<dt><spring:message code="interessat.form.camp.pais"/></dt><dd>${interessat.pais}</dd>
													<dt><spring:message code="interessat.form.camp.provincia"/></dt><dd>${interessat.provincia}</dd>											
													<dt><spring:message code="interessat.form.camp.municipi"/></dt><dd>${interessat.municipi}</dd>
													<dt><spring:message code="interessat.form.camp.direccio"/></dt><dd>${interessat.direccio}</dd>
													<dt><spring:message code="interessat.form.camp.codipostal"/></dt><dd>${interessat.codiPostal}</dd>
												</dl>
											</div>
											<div class="col-xs-6">
												<dl class="dl-horizontal">
													<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${interessat.email}</dd>
													<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${interessat.telefon}</dd>
													<dt><spring:message code="interessat.form.camp.observacions"/></dt><dd>${interessat.observacions}</dd>
												</dl>
											</div>
											
										
										</div>
									</td>						
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:otherwise>
					<div class="row col-xs-12">
						<div class="well">
							<spring:message code="interessat.controller.buit"/>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true">
				<spring:message code="comu.boto.cancelar"/>
			</button>
		</div>
	</form:form>
</body>
</html>
