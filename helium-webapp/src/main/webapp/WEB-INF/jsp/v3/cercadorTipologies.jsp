<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<head>
	<title><spring:message code="expedient.tipus.cercador.tipologies.titol"/></title>
	<meta name="capsaleraTipus" content="llistat"/>
	<meta name="title" content="<spring:message code='expedient.tipus.cercador.tipologies.titol'/>"/>
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
	
});
</script>
</head>
<c:if test="${dadesPersona.admin || potAdministrarEntorn}">
<form:form action="" id="filtre" method="post" cssClass="well" commandName="expedientTipusAdminCommand">
		<div class="row">
			<div class="col-md-3">
				<hel:inputText name="codiTipologia" textKey="expedient.tipus.cercador.tipologies.codi.tipologia" placeholderKey="expedient.tipus.cercador.tipologies.codi.tipologia" inline="true"/>
			</div>
			<div class="col-md-3">
				<hel:inputText name="codiSIA" textKey="expedient.tipus.cercador.tipologies.codi.sia" placeholderKey="expedient.tipus.cercador.tipologies.codi.sia" inline="true"/>
			</div>
			<div class="col-md-3">
				<hel:inputText name="numRegistreAnotacio" textKey="expedient.tipus.cercador.tipologies.numero.registre.anotacio" placeholderKey="expedient.tipus.cercador.tipologies.numero.registre.anotacio" inline="true"/>
			</div>
			<div class="col-md-12 d-flex align-items-end">
				<div class="pull-right">
					<button id="consultar" type="submit" name="accio" value="consultar" class="btn btn-primary" url="" style="display: none;"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
					<button id="netejar" type="submit" name="accio" value="netejar" class="btn btn-default" url=""><spring:message code="comu.filtre.netejar"/></button>
					<button id="consultar" type="submit" name="accio" value="consultar" class="btn btn-primary" url=""><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
				</div>
			</div>
		</div>
</form:form>

<div class="col-12" style="overflow: auto;">
	<c:if test="${ empty llistatExpedients}">
		<div class="alert alert-warning">
		  <spring:message code='expedient.tipus.taula.estadistica.warning.senseresultats'/>
		</div>
	</c:if>

	<c:if test="${ not empty llistatExpedients}">
		<table id="tipologia" class="table table-striped table-bordered">
		<thead>
			<tr>
				<th><spring:message code='expedient.tipus.cercador.tipologies.codi.tipologia'/></th>
				<th><spring:message code='expedient.tipus.cercador.tipologies.tipus.expedient.titol'/></th>
				<th><spring:message code='expedient.tipus.cercador.tipologies.codi.sia'/></th>
			</tr>	
		 </thead>
		 
		 <c:forEach items="${expedientsTipusDto}" var="items">
		  	<!-- 	<c:if test="${not empty codisTipologia[items.codi] }"> -->
					<tr>
						<th class="text-left">
							${ codisTipologia[items.codi] } 
						</th>
						<th class="text-left">
							${ titols[items.codi] } 
						</th>
						<th class="text-left">
							${ codisSia[items.codi] } 
						</th>
					</tr>
				<!--</c:if>	-->
					
			</c:forEach>
						
		</table>
	</c:if>
</div>
</c:if>


