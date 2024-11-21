<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
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
	
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>
	
	<style type="text/css">
	</style>
	
	<script>
	// <![CDATA[

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

		$(document).ready(function() {
			
			$("#netejar").click(function() {
				$('#unitatOrganitzativa').val('').change();
				$('#estat').val('').change();
			})
		});
		
		// ]]>
		</script>	
</head>
<body>
	<form:form action="" method="post" cssClass="well" commandName="procedimentFiltreCommand">
		<div class="row">
			<div class="col-md-2">
				<hel:inputText name="codiSia" inline="true" placeholderKey="procediment.llistat.columna.codiSia"/>
			</div>
			<div class="col-md-3">
				<hel:inputText name="nom" inline="true" placeholderKey="procediment.llistat.filtre.procediment"/>
			</div>			
			<div class="col-md-5">
				<hel:inputSuggest 
					name="unitatOrganitzativa" 
					urlConsultaInicial="/helium/v3/unitatOrganitzativa/suggestInici" 
					urlConsultaLlistat="/helium/v3/unitatOrganitzativa/suggest" 
					textKey="procediment.llistat.columna.unitatOrganitzativa" 
					placeholderKey="procediment.llistat.columna.unitatOrganitzativa"/>			
			</div>
			<div class="col-md-2">
				<hel:inputSelect inline="true" name="estat" optionItems="${estats}" emptyOption="true" textKey="procediment.llistat.columna.estat" placeholderKey="procediment.llistat.columna.estat" optionValueAttribute="codi" optionTextAttribute="valor"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary hidden" ><span class="fa fa-filter"></span></button>
					<button id="netejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="ml-2 btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.filtre.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>

	<div class="text-right" data-toggle="botons-titol">
		<c:if test="${dadesPersona.admin}">
			<a class="btn btn-default" href="<c:url value='/v3/procediment/actualitzar'/>" data-toggle="modal" data-maximized="true"><span id="span-refresh" class="fa fa-refresh"></span>&nbsp; <spring:message code="procediment.taula.actualitzar"/></a>
		</c:if>
	</div>

	<table
		id="procediments"
		data-refresh-tancar="true"
		data-toggle="datatable"
		data-url="<c:url value="/v3/procediment/datatable"/>"
		data-filter="#procedimentFiltreCommand"
		data-default-order="1"
		data-default-dir="asc"
		data-botons-template="#tableButtonsAccionsTemplate"
		class="table table-striped table-bordered table-hover">
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
				<th data-col-name="comu" data-template="#cellComuTemplate">
					<spring:message code="procediment.llistat.columna.comu"/>
					<script id="cellComuTemplate" type="text/x-jsrender">
						{{if comu}}<span class="fa fa-check"></span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="estat" data-template="#cellEstatProcedimentTemplate">
					<spring:message code="anotacio.llistat.columna.estat"/>
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
	
	<script id="tableButtonsAccionsTemplate" type="text/x-jsrender">	
		<div class="text-right" style="padding-top: 8px;">
			<b><spring:message code="unitat.organitzativa.cron.sync"/>:</b> ${globalProperties['app.unitats.procediments.sync']}
			&nbsp;
		</div>
	</script>

</body>