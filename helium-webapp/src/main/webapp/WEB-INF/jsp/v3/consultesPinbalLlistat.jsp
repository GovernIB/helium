<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="consultes.pinbal.titol"/></title>
	<meta name="title" content="<spring:message code='consultes.pinbal.titol'/>"/>
	<meta name="screen" content="fluxosFirma">
	<meta name="title-icon-class" content="fa fa-exchange"/>
<!-- 	fa-product-hunt -->
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>	
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_ca.js"/>"></script>	
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>	
</head>
<body>

	<form:form action="" method="post" cssClass="well" commandName="consultesPinbalFiltreCommand">
	
	<div class="row">
		<div class="col-md-4">
			<hel:inputSelect name="tipusId"
				textKey="anotacio.llistat.filtre.camp.expedientTipus"
				optionItems="${expedientsTipus}"
				optionValueAttribute="codi"
				emptyOption="true"
				disabled="${not empty expedientTipusActual}"
				inline="true"
				placeholderKey="anotacio.llistat.filtre.camp.expedientTipus"
				optionTextAttribute="valor" />
		</div>					
		<div class="col-md-4">							
			<hel:inputText name="numeroExpedient" textKey="anotacio.llistat.filtre.camp.numeroExpedient" placeholderKey="anotacio.llistat.filtre.camp.numeroExpedient" inline="true"/>
		</div>
		<div class="col-md-4">							
			<hel:inputText name="procediment" textKey="consultes.pinbal.camp.procediment" placeholderKey="consultes.pinbal.camp.procediment" inline="true"/>
		</div>		
	</div>
	
	<div class="row">
		<div class="col-md-4">
			<hel:inputSelect
				name="usuari"
				emptyOption="true" 
				textKey="consultes.pinbal.camp.usuari"
				placeholderKey="consultes.pinbal.camp.usuari"
				optionItems="${persones}"
				optionValueAttribute="codi"
				optionTextAttribute="nom"
				disabled="${empty persones}"
				inline="true"/>
		</div>		
		<div class="col-md-2">
			<hel:inputDate name="dataPeticioIni" textKey="anotacio.llistat.filtre.camp.dataInicial" placeholder="dd/mm/aaaa" inline="true"/>
		</div>	
		<div class="col-md-2">
			<hel:inputDate name="dataPeticioFi" textKey="anotacio.llistat.filtre.camp.dataFinal" placeholder="dd/mm/aaaa" inline="true"/>
		</div>	
		<div class="col-md-2">
			<hel:inputSelect 
				inline="true" 
				name="estat" 
				optionItems="${estats}" 
				emptyOption="true" 
				textKey="consultes.pinbal.camp.estat" 
				placeholderKey="consultes.pinbal.camp.estat" 
				optionValueAttribute="codi" 
				optionTextAttribute="valor"/>
		</div>	
	</div>

	<div class="row">
		<div class="col-md-10"></div>	
		<div class="col-md-2 pull-right">
			<div class="pull-right">
					<button id="consultarHidden" type="submit" name="accio" value="consultar" class="btn btn-primary hidden"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
					<button id="netejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
					<button id="consultar" type="submit" name="accio" value="consultar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
			</div>
		</div>				
	</div>
	
	</form:form>

	<table	id="consultesPinbalDatatable"
			data-toggle="datatable"
			data-url="consultesPinbal/datatable"
			data-paging-enabled="true"
			data-ordering="true"
			data-default-order="4"
			data-default-dir="desc"			
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="tipus.nom" width="18%"><spring:message code="consultes.pinbal.camp.tipus"/></th>
				<th data-col-name="expedient.identificador" width="32%"><spring:message code="consultes.pinbal.camp.exp"/></th>
				<th data-col-name="procediment" width="15%"><spring:message code="consultes.pinbal.camp.procediment"/></th>
				<th data-col-name="usuari" width="10%"><spring:message code="consultes.pinbal.camp.usuari"/></th>
				<th data-col-name="dataPeticio" width="10%" data-converter="datetime"><spring:message code="consultes.pinbal.camp.dataPeticio"/></th>
				<th data-col-name="estat" width="10%"><spring:message code="consultes.pinbal.camp.estat"/></th>
				<th data-col-name="expedientId" data-visible="false"></th>
				<th data-col-name="documentId" data-visible="false"></th>
				<th data-col-name="id" width="5%" data-template="#cellAccionsTemplate" align="center">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="<c:url value="/v3/expedient/{{:expedientId}}/document/{{:documentId}}/descarregar"/>" style="padding-left: 30%;">
							<span class="fa fa-file fa-2x no-doc" title="Descarregar justificant"></span>
						</a>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	
	<div id="modalProcesEstat" class="modal fade">
		<div class="modal-dialog" style="width: 60%; height: 85%">
			<div class="modal-content" style="height: 100%">
				<div class="modal-header" style="height: 10%">
					<button type="button" class="close" data-dismiss="modal" aria-label="<spring:message code="comu.boto.tancar"/>"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title"><span class="fa fa-list"></span> <spring:message code="fluxosFirma.taula.modal.title"></spring:message></h4>
				</div>
				<div class="modal-body" style="height: 80%"></div>
				<div class="modal-footer" style="height: 10%">
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></button>
				</div>
			</div>
		</div>
	</div>	
	
	<script type="text/javascript">
	// <![CDATA[
		$(document).ready(function() {});
	// ]]>
	</script>	

</body>
</html>
