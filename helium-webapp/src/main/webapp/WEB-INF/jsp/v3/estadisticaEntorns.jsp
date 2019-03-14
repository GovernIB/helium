<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<head>
	<title><spring:message code="expedient.tipus.estadistica.titol"/></title>
	<meta name="capsaleraTipus" content="llistat"/>
	<meta name="title" content="<spring:message code='expedient.tipus.estadistica.titol'/>"/>
	<meta name="title-icon-class" content="fa fa-folder"/>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/helium.datatable.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	
	<style type="text/css">
		.col-md-1.btn-group {width: 4.333%;}
		.col-md-6.btn-group {width: 54%;}
		.alert-envelope {
			font-size: 21px;
			position: relative;
			top: 3px;
			margin-left: 3px;
		}
		.sup-count {
			position: relative;
			padding: 2px 5px;
			background-color: red;
			font-size: 11px;
			top: -9px;
			left: -10px;
		}
		.error-triangle {
			color: red;
		    font-size: 18px;
		    top: 4px;
		    position: relative;	
		}
		a.no-deco-link {
			text-decoration: none;
			color: inherit;
		}
		a.no-deco-link:hover {
			text-decoration: none;
			color: inherit;
		}
		.navbar-right {
			margin-right: 0px;
		}
	</style>
<script>
$(document).ready(function() {
		$('.datetimepicker').datetimepicker({
			locale: moment.locale('${idioma}'),
			format: 'YYYY'
	    }).on('dp.show', function() {
			var iframe = $('.modal-body iframe', window.parent.document);
			var divField = $('.modal-body iframe', window.parent.document).contents().find('body>div');
			iframe.height((iframe.height() + 200) + 'px');
			divField.height((divField.height() + 200) + 'px');
		}).on('dp.hide', function() {
			var iframe = $('.modal-body iframe', window.parent.document);
			var divField = $('.modal-body iframe', window.parent.document).contents().find('body>div');
			var height = $('html').height();
			iframe.height((iframe.height() - 200) + 'px');
			divField.height((divField.height() - 200) + 'px');
		});
		$(".btn_date").click(function(){
			$(this).prev(".date").trigger("focus");
		});
		$("#exportar_excel").click(function(event){
			event.preventDefault();
			console.log($(this).attr("url"));
			$("#excel").submit();
		});
});
</script>
</head>
<c:if test="${dadesPersona.admin || potAdministrarEntorn}">
<form:form action="" id="filtre" method="post" cssClass="well" commandName="expedientTipusEstadisticaCommand">
		<div class="row">
			<div class="col-md-3">
				<label><spring:message code="expedient.llistat.filtre.camp.expedient.tipus"/></label>
				<hel:inputSelect emptyOption="true" name="expedientTipusId" textKey="expedient.llistat.filtre.camp.expedient.tipus" placeholderKey="expedient.llistat.filtre.camp.expedient.tipus" optionItems="${expedientsTipus}" optionValueAttribute="id" optionTextAttribute="nom" disabled="${not empty expedientTipusActual}" inline="true"/>
			</div>
			<div class="col-md-6">
				<label><spring:message code="expedient.llistat.filtre.camp.data.inici"/></label>
				<div class="row">
					<div class="col-md-6">
						<div class="input-group date">
							<input class="datetimepicker form-control datetimepicker date" name="anyInicial" placeholder="aaaa" inline=true value="${expedientTipusEstadisticaCommand.anyInicial}">
							<span class="input-group-addon btn_date" style="width:auto"><span class="fa fa-calendar"></span></span>
						</div>
					</div>
					<div class="col-md-6">
						<div class="input-group date">
							<input class="datetimepicker form-control datetimepicker date" name="anyFinal" placeholder="aaaa" inline=true value="${expedientTipusEstadisticaCommand.anyFinal}">
							<span class="input-group-addon btn_date" style="width:auto"><span class="fa fa-calendar"></span></span>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-3">
				<label><spring:message code="expedient.llistat.filtre.camp.anulats"/></label>
				<div class="row">
					<div class="col-md-12">
						<hel:inputSelect inline="true" name="mostrarAnulats" optionItems="${anulats}" optionValueAttribute="valor" optionTextAttribute="codi"/>
					</div>
				</div>
			</div>
			<div class="col-md-12 d-flex align-items-end">
				<div class="pull-right">
					<input type="hidden" name="consultaRealitzada" value="true"/>
					<button id="exportar_excel" class="btn btn-default" url=""><span class="fa fa-download"></span>&nbsp;<spring:message code="expedient.tipus.document.llistat.accio.descarregar"/></button>
					<button id="netejar" type="submit" name="accio" value="netejar" class="btn btn-default" url=""><spring:message code="comu.filtre.netejar"/></button>
					<button id="consultar" type="submit" name="accio" value="consultar" class="btn btn-primary" url=""><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
				</div>
			</div>
		</div>
</form:form>

<form:form id="excel" action="/helium/v3/estadistica/excel" method="post" commandName="expedientTipusEstadisticaCommand">
	<input type="hidden" name="expedientTipusId" value="${expedientTipusEstadisticaCommand.expedientTipusId}"/>
	<input type="hidden" name="anyInicial" value="${expedientTipusEstadisticaCommand.anyInicial}">
	<input type="hidden" name="anyFinal" value="${expedientTipusEstadisticaCommand.anyFinal}">
	<input type="hidden" name="mostrarAnulats" value="${expedientTipusEstadisticaCommand.mostrarAnulats}"/>
</form:form>

<div class="col-12" style="overflow: auto;">
	<c:if test="${ empty tableData}">
		<div class="alert alert-warning">
		  <spring:message code='expedient.tipus.taula.estadistica.warning.senseresultats'/>
		</div>
	</c:if>
	<c:if test="${ not empty tableData}">
		<table id="estadistica" class="table table-striped table-bordered">
		<thead>
			<tr>
			<th><spring:message code='expedient.tipus.taula.estadistica.titol.codi'/></th>
			<th><spring:message code='expedient.tipus.taula.estadistica.titol.nom'/></th>
			<c:forEach items="${anys}" var="item">
					<th> ${ item } </th>
			</c:forEach>
				<th><spring:message code='expedient.tipus.taula.estadistica.totaltipus'/></th>
			</tr>
		 </thead>
		  		<c:forEach items="${expedientsTipus}" var="items">
		  			<c:if test="${ not empty tableData[items.codi] }">
					<tr>
						<th>${ items.codi }</th>
						<th>${ items.nom } </th>
						<c:forEach items="${anys}" var="item">
							<td class="text-right">
								<c:choose>
								    <c:when test="${ not empty tableData[items.codi][item] }">
								        ${ tableData[items.codi][item] }
								    </c:when>    
								    <c:otherwise>
								        <span style="color:rgba(0,0,0,0);">0</span>
								    </c:otherwise>
								</c:choose>
							</td>
						</c:forEach>
						<th class="text-right">
							<c:choose>
							    <c:when test="${ not empty totalTipus[items.codi] }">
							        ${ totalTipus[items.codi] }
							    </c:when>    
							    <c:otherwise>
							        0
							    </c:otherwise>
							</c:choose>
						</th>
					</tr>
					</c:if>
				</c:forEach>
						<c:set var = "totalTotal" value = "${0}"/>
					<tr>
					<th><spring:message code='expedient.tipus.taula.estadistica.totalany'/></th>
					<th></th>
						<c:forEach items="${anys}" var="item">
							<th class="text-right">
								<c:choose>
								    <c:when test="${ not empty totalAny[item] }">
								        ${ totalAny[item] }
									<c:set var = "totalTotal" value = "${totalTotal + totalAny[item]}"/>
								    </c:when>    
								    <c:otherwise>
								        0
								    </c:otherwise>
								</c:choose>
							</th>
						</c:forEach>
						<th class="text-right">
							<c:out value = "${totalTotal}"/>
						</th>
					</tr>
		</table>
	</c:if>
</div>
</c:if>


