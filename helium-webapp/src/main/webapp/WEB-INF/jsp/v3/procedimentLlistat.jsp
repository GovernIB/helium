<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="procediment.llistat.titol"/></title>
	<meta name="title" content="<spring:message code='procediment.llistat.titol'/>"/>
	<meta name="screen" content="procediments">
	<meta name="title-icon-class" content="fa fa-book"/>
	
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
	<script src="<c:url value="/webjars/datatables.net-select/1.1.0/js/dataTables.select.min.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>
	
	<style type="text/css">
		span.badge {
			font-size: 1.2rem !important;
			padding-right: 1.2rem !important;
		}
		
		span.fa-cog {
			margin: 4px 1.5rem 0 0; 
		}
		
		tbody tr.selectable td #div-btn-accions #btn-accions span.caret {
			margin: 8px 0 0 2px; 
		}
		
		span.select2-container {
			width: 100% !important;
		}
		
		button#netejarFiltre, 
		button#filtrar {
			width: 50%;
		}
	</style>
	
	<script>
		function actualitzarProcediments() {
			$("#span-refresh").addClass('fa-spin');
			$("#actualitzarProcediments").addClass('disabled');
		}

		function formatSelectUnitat(item) {
			if (!item.id) {
			    return item.text;
			}
			if (item.data && item.data.estat=="V"){
				return item.text;
			} else {
				return $("<span>" + item.text + " <span class='fa fa-exclamation-triangle text-warning' title=\"<spring:message code='unitat.arbre.unitatObsoleta'/>\"></span></span>");
			}
		}
	</script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" commandName="procedimentFiltreCommand">
		<div class="row">
			<div class="col-md-3">
				<hel:inputText name="codiSia" inline="true" placeholderKey="procediment.llistat.columna.codiSia"/>
			</div>
			<div class="col-md-3">
				<hel:inputText name="nom" inline="true" placeholderKey="procediment.llistat.filtre.procediment"/>
			</div>			
			<div class="col-md-3">
				<c:url value="/unitatajax/unitat" var="urlConsultaInicial"/>
				<c:url value="/unitatajax/senseEntitat" var="urlConsultaLlistat"/>
				<b>suggest d'unitats</b>
			</div>
			<div class="col-md-3">
				<hel:inputSelect inline="true" name="estat" optionItems="${estats}" emptyOption="true" textKey="procediment.llistat.columna.estat" placeholderKey="procediment.llistat.columna.estat" optionValueAttribute="codi" optionTextAttribute="valor"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary hidden" ><span class="fa fa-filter"></span></button>
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="ml-2 btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.filtre.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>

<script id="botonsTemplate" type="text/x-jsrender">
	<c:if test="${dadesPersona.admin || true}">
		<div style="float: right;"> 
			<a href="<c:url value='/v3/procediment/actualitzar'/>" onclick="actualitzarProcediments()" id="actualitzarProcediments" class="btn btn-default"><span id="span-refresh" class="fa fa-refresh"></span>&nbsp; <spring:message code="procediment.taula.actualitzar"/></a>
		</div>
	</c:if>
</script>
<table
	id="procediments"
	data-refresh-tancar="true"
	data-toggle="datatable"
	data-url="<c:url value="/v3/procediment/datatable"/>"
	data-filter="#procedimentFiltreCommand"
	data-botons-template="#botonsTemplate"
	data-default-order="1"
	data-default-dir="asc"
	class="table table-striped table-bordered">
	<thead>
		<tr>
			<th data-col-name="id" data-visible="false" width="4%">#</th>
			<th data-col-name="codiSia" data-orderable="true"><spring:message code="procediment.llistat.columna.codiSia"/></th>
			<th data-col-name="nom" data-orderable="true"><spring:message code="procediment.llistat.columna.nom"/></th>
			<th data-col-name="unitatOrganitzativa.codi" data-template="#uoTemplate">
				<spring:message code="procediment.llistat.columna.unitatOrganitzativa"/>
				<script id="uoTemplate" type="text/x-jsrender">

					{{if unitatOrganitzativa.estat!='V'}}
						<span class="fa fa-warning text-warning  pull-right" style="margin-top: 3px;" title="<spring:message code="unitat.arbre.unitatObsoleta"/>"></span>
					{{/if}}
 
					{{:unitatOrganitzativa.codi}} -  {{:unitatOrganitzativa.denominacio}}

				</script>
			</th>
			<th data-col-name="estat" data-template="#cellEstatProcedimentTemplate">
				<spring:message code="anotacio.llistat.columna.estat"/> <span class="fa fa-list" id="showModalProcesEstatButton" title="<spring:message code="anotacio.llistat.columna.estat.llegenda"/>" style="cursor:over; opacity: 0.5"></span>
				<script id="cellEstatProcedimentTemplate" type="text/x-jsrender">
						{{if estat == 'VIGENT'}}
							<spring:message code="procediment.estat.enum.VIGENT"></spring:message>
						{{else estat == 'EXTINGIT'}}
							<spring:message code="procediment.estat.enum.EXTINGIT"></spring:message>
						{{else}}
							{{:estat}}
						{{/if}}
					</script>
			</th>			
		</tr>
	</thead>
</table>

</body>