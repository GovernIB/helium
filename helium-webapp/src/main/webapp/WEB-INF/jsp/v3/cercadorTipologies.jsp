<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="expedient.tipus.cercador.tipologies.titol"/></title>
	<meta name="screen" content="cercadorExpedients">
	<meta name="title" content="<spring:message code='expedient.tipus.cercador.tipologies.titol'/>"/>
	<meta name="title-icon-class" content="fa fa-folder"/>
	
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>
</head>
<body>

<form:form action="" method="post" cssClass="well" commandName="expedientTipusAdminCommand">
		<div class="row">
			<div class="col-md-2">
				<hel:inputText name="codiTipologia" textKey="expedient.tipus.cercador.tipologies.codi.tipologia" placeholderKey="expedient.tipus.cercador.tipologies.codi.tipologia" inline="true"/>
			</div>
			<div class="col-md-3">
				<hel:inputText name="nomTipologia" textKey="expedient.tipus.cercador.tipologies.nom.tipologia" placeholderKey="expedient.tipus.cercador.tipologies.nom.tipologia" inline="true"/>
			</div>
			<div class="col-md-2">
				<hel:inputText name="codiSIA" textKey="expedient.tipus.cercador.tipologies.codi.sia" placeholderKey="expedient.tipus.cercador.tipologies.codi.sia" inline="true"/>
			</div>
			<div class="col-md-3">
				<hel:inputText name="numRegistre" textKey="expedient.tipus.cercador.tipologies.numero.registre.anotacio" placeholderKey="expedient.tipus.cercador.tipologies.numero.registre.anotacio" inline="true"/>
			</div>
			<div class="col-md-2 d-flex align-items-end">
				<div class="pull-right">
					<button id="consultar" type="submit" name="accio" value="consultar" class="btn btn-primary" url="" style="display: none;"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
					<button id="netejar" type="submit" name="accio" value="netejar" class="btn btn-default" url=""><spring:message code="comu.filtre.netejar"/></button>
					<button id="consultar" type="submit" name="accio" value="consultar" class="btn btn-primary" url=""><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
				</div>
			</div>
		</div>
</form:form>

		
		<table id="cercadorTipologies" 
			data-toggle="datatable"
			data-url="<c:url value="cercadorTipologies/datatable"/>"
			data-paging-enabled="true"
			data-ordering="true"
			data-default-order="1"
			data-default-dir="asc"
			data-rowhref-template="#rowhrefTemplate"
			data-rowhref-maximized="true"
			class="table table-striped table-bordered table-hover"
			style="width:100%">	
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"/>
				<th data-col-name="codi"><spring:message code='expedient.tipus.cercador.tipologies.codi.tipologia'/></th>
				<th data-col-name="nom"><spring:message code='expedient.tipus.cercador.tipologies.tipus.expedient.titol'/></th>
				<th data-col-name="ntiClasificacion"><spring:message code='expedient.tipus.cercador.tipologies.codi.sia'/></th>				
				<th data-col-name="entorn.codi" width="20%" data-template="#cellExpedientTipusEntornTemplate">
					<spring:message code="expedient.tipus.cercador.tipologies.entorn"/>
						<script id="cellExpedientTipusEntornTemplate" type="text/x-jsrender">
							<span title="{{:entorn.nom}}">{{:entorn.codi}}</span>
						</script>
					</th>
				<th data-col-name="entorn.nom" data-visible="false"/>
			</tr>	
		 </thead>
						
		</table>
		<script id="rowhrefTemplate" type="text/x-jsrender"><c:url value="/v3/expedientTipus/{{:id}}"/></script>	


	<script type="text/javascript">
	// <![CDATA[

	   $.views.helpers({
		   formatTemplateDate: function (d) {
			   return moment(new Date(d)).format("DD/MM/YYYY HH:mm:ss");
		    }
		});
		            
	$(document).ready(function() {
	});
	
	// ]]>
	</script>	
</body>
</html>

