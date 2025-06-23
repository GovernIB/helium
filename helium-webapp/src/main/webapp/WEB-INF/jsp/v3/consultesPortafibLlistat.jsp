<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="consultes.potafib.titol"/></title>
	<meta name="title" content="<spring:message code='consultes.potafib.titol'/>"/>
	<meta name="screen" content="fluxosFirma">
	<meta name="title-icon-class" content="fa fa-pencil"/>
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

	<script type="text/javascript">
	// <![CDATA[

	$(document).ready(function() {
		
		$("#netejar").click(function() {
			$('#tipusId').val('').change();
			$('#estat').val('').change();
			$('#documentId').val('').change();
		})
		
	});
	// ]]>
	</script>	

</head>
<body>

	<form:form action="" method="post" cssClass="well" commandName="consultesPortafibFiltreCommand">
	
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
			<hel:inputText name="documentNom" textKey="consultes.potafib.camp.nomDoc" placeholderKey="consultes.potafib.camp.nomDoc" inline="true"/>
		</div>		
	</div>
	
	<div class="row">
		<div class="col-md-2">
			<hel:inputDate name="dataPeticioIni" textKey="consultes.potafib.camp.filtreDesde" placeholderKey="consultes.potafib.camp.filtreDesde" inline="true"/>
		</div>		
		<div class="col-md-2">
			<hel:inputDate name="dataPeticioIni" textKey="consultes.potafib.camp.filtreFins" placeholderKey="consultes.potafib.camp.filtreFins" inline="true"/>
		</div>	
		<div class="col-md-4">
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
		<div class="col-md-2">
			<hel:inputNumber name="documentId" textKey="consultes.potafib.camp.documentId" placeholderKey="consultes.potafib.camp.documentId" inline="true"/>
		</div>
		<div class="col-md-4 pull-right">
			<div class="pull-right">
					<button id="consultarHidden" type="submit" name="accio" value="consultar" class="btn btn-primary hidden"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
					<button id="netejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
					<button id="consultar" type="submit" name="accio" value="consultar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
			</div>
		</div>		
	</div>
	
	</form:form>

	<table	id="consultesPortafibDatatable"
			data-toggle="datatable"
			data-url="enviamentsPortafib/datatable"
			data-filter="#consultesPortafibFiltreCommand"
			data-paging-enabled="true"
			data-ordering="true"
			data-default-order="3"
			data-default-dir="desc"			
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name=documentId width="10%"><spring:message code="consultes.potafib.camp.documentId"/></th>
				<th data-col-name="tipusExpedientNom" data-template="#cellTipusTemplate" width="10%"><spring:message code="consultes.pinbal.camp.tipus"/>
					<script id="cellTipusTemplate" type="text/x-jsrender">
						{{:tipusExpedientCodi}} <span class="fa fa-info-circle" title="{{:tipusExpedientCodi}} - {{:tipusExpedientNom}}
(Entorn {{:entornCodi}} - {{:entornNom}})"></span>
					</script>
				</th>
				<th data-col-name="expedientIdentificador" data-template="#cellExpTemplate" width="31%"><spring:message code="consultes.pinbal.camp.exp"/>
					<script id="cellExpTemplate" type="text/x-jsrender">
						<a href="<c:url value="/v3/expedient/{{:expedientId}}"/>" target="_blank">{{:expedientIdentificador}}</a>
					</script>
				</th>
				<th data-col-name="dataEnviat" width="10%" data-converter="datetime"><spring:message code="consultes.potafib.camp.dataEnviat"/></th>
				<th data-col-name="estat" data-template="#cellEstatTemplate" width="10%"><spring:message code="consultes.pinbal.camp.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat=='BLOQUEJAT'}}<span class="fa fa-clock-o"></span> Bloquejat{{/if}}						
						{{if estat=='PENDENT'}}<span class="fa fa-clock-o"></span> Pendent{{/if}}
						{{if estat=='SIGNAT'}}<span class="fa fa-check"></span> Signat{{/if}}
						{{if estat=='REBUTJAT'}}<span class="fa fa-ban"></span> Rebutjat{{/if}}
						{{if estat=='PROCESSAT'}}<span class="fa fa-check"></span> Processat ({{:transicio}}){{/if}}
						{{if estat=='CANCELAT'}}<span class="fa fa-times"></span> Cancel·lat{{/if}}
						{{if estat=='ERROR'}}<span class="fa fa-exclamation-triangle" title="{{>errorProcessant}}"></span> Error ({{:transicio}}){{/if}}
						{{if estat=='ESBORRAT'}}<span class="fa fa-times"></span> Esborrat{{/if}}					
					</script>
				</th>
				<th data-col-name="documentNom" data-template="#cellDocTemplate" width="15%" data-orderable="false"><spring:message code="consultes.potafib.camp.nomDoc"/>
					<script id="cellDocTemplate" type="text/x-jsrender">
						<a href="<c:url value="/v3/expedient/{{:expedientId}}/document/{{:documentStoreId}}/descarregar"/>" target="_blank">{{:documentNom}}</a>
					</script>
				</th>					
				<th data-col-name="expedientId" data-visible="false"></th>
				<th data-col-name="tipusExpedientCodi" data-visible="false"></th>
				<th data-col-name="transicio" data-visible="false"></th>
				<th data-col-name="entornCodi" data-visible="false"></th>
				<th data-col-name="documentStoreId" data-visible="false"></th>
				<th data-col-name="errorProcessant" data-visible="false"></th>
				<th data-col-name="entornNom" data-visible="false"></th>
				<th data-col-name="id" width="5%" data-template="#cellAccionsTemplate" data-orderable="false">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown navbar-right">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/v3/enviamentsPortafib/{{:id}}/info"/>" data-toggle="modal" class="consultar-expedient"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="consultes.pinbal.boto.info"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	
	<script type="text/javascript">
	// <![CDATA[
		$(document).ready(function() {});
	// ]]>
	</script>	

</body>
</html>
