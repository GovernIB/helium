<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code="unitat.organitzativa.llistat.titol"/></title>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<meta name="screen" content="unitatOrganitzativa">
	<meta name="title" content="<spring:message code='unitat.organitzativa.llistat.titol'/>"/>
	<meta name="title-icon-class" content="fa fa-cubes"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>

	<script>
	// <![CDATA[

		$(document).ready(function() {
			
			$("#netejar").click(function() {
				$('#codiUnitatSuperior').val('').change();
				$('#estat').val('VIGENTE').change();
			})
		});
		
		// ]]>
		</script>	
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<c:if test="${dadesPersona.admin}">
			<a href="<c:url value="/v3/unitatOrganitzativa/mostrarArbre"/>" data-toggle="modal"  class="btn btn-default"><span class="fa fa-sitemap"></span> <spring:message code="unitat.organitzativa.list.boto.mostrarArbre"/></a>
			<a href="<c:url value="/v3/unitatOrganitzativa/synchronizeGet"/>" data-toggle="modal" class="btn btn-default"><span class="fa fa-refresh"></span> <spring:message code="unitat.organitzativa.list.boto.synchronize"/></a>
		</c:if>
	</div>
	<form:form action="" method="post" cssClass="well" commandName="unitatOrganitzativaCommand">
		<div class="row">
			<div class="col-md-2">
				<hel:inputText name="codi" textKey="unitat.organitzativa.codi" placeholderKey="unitat.organitzativa.codi" inline="true"/>
			</div>
			<div class="col-md-3">
				<hel:inputText name="denominacio" textKey="unitat.organitzativa.denominacio" placeholderKey="unitat.organitzativa.denominacio" inline="true"/>
			</div>
			<div class="col-md-5">
				<hel:inputSuggest 
					name="codiUnitatSuperior" 
					urlConsultaInicial="/helium/v3/unitatOrganitzativa/suggestInici" 
					urlConsultaLlistat="/helium/v3/unitatOrganitzativa/suggest" 
					textKey="unitat.organitzativa.unitat.superior" 
					placeholderKey="unitat.organitzativa.unitat.superior"/>			
			</div>
			<div class="col-md-2">
				<hel:inputSelect emptyOption="true" name="estat"  textKey="unitat.organitzativa.estat" placeholderKey="unitat.organitzativa.estat" optionItems="${estats}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			</div>
		</div>
		<div class="row">	
			<div class="col-md-12">
				
				<div class="pull-right">
					<button type="submit" name="accio" value="consultar" class="hidden"></button>
					<button id="netejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
					<button id="consultar" type="submit" name="accio" value="consultar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
					
				</div>
			</div>
		</div>
	</form:form>
	<table	id="taulaDades"
			data-toggle="datatable"
			data-url="unitatOrganitzativa/datatable"
			data-filter="#unitatOrganitzativaCommand"
			data-paging-enabled="true"
			data-info-type="button"
			data-ordering="true"
			data-default-order="1"
			data-rowhref-toggle="modal" 
			data-botons-template="#tableButtonsAccionsTemplate"
			class="table table-striped table-bordered table-hover">			
		<thead>
			<tr>
				<th data-col-name="codi"><spring:message code="unitat.organitzativa.codi"/></th>
				<th data-col-name="denominacio" width="50%"><spring:message code="unitat.organitzativa.denominacio"/></th>
				<th data-col-name="codiUnitatSuperior" ><spring:message code="unitat.organitzativa.unitat.superior"/></th>
				<th data-col-name="estat" data-template="#estatTemplate">
					<spring:message code="unitat.organitzativa.estat"/>
					<script id="estatTemplate" type="text/x-jsrender">
						{{if estat =='V'}}
							<spring:message code="unitat.organitzativa.estat.vigent"/>
						{{else estat =='E'}}
							<spring:message code="unitat.organitzativa.estat.extingit"/>
						{{else estat =='A'}}
							<spring:message code="unitat.organitzativa.estat.anulat"/>
						{{else estat =='T'}}
							<spring:message code="unitat.organitzativa.estat.transitori"/>
						{{/if}}
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="tableButtonsAccionsTemplate" type="text/x-jsrender">	
		<div class="text-right" style="padding-top: 8px;">
			<b><spring:message code="unitat.organitzativa.llistat.unitat.arrel"/>:</b> ${codiUnitatArrel}
			&nbsp;
			<b><spring:message code="unitat.organitzativa.llistat.data.sincronitzacio"/>:</b> ${dataSincronitzacio}
			&nbsp;
			<b><spring:message code="unitat.organitzativa.llistat.data.actualitzacio"/>:</b> ${dataActualitzacio}
			&nbsp;
			<b><spring:message code="unitat.organitzativa.cron.sync"/>:</b> ${globalProperties['app.unitats.procediments.sync']}
		</div>
	</script>
</body>
</html>
