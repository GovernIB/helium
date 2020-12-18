<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="anotacio.llistat.titol"/></title>
	<meta name="title" content="<spring:message code='anotacio.llistat.titol'/>"/>
	<meta name="screen" content="anotacions">
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
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>
</head>
<body>
	<form:form action="" method="post" cssClass="well" commandName="anotacioFiltreCommand">
	
	<div class="row">
		<div class="col-md-2">
			<hel:inputDate name="dataInicial" textKey="anotacio.llistat.filtre.camp.dataInicial" placeholder="dd/mm/aaaa" inline="true"/>
		</div>	
		<div class="col-md-2">
			<hel:inputDate name="dataFinal" textKey="anotacio.llistat.filtre.camp.dataFinal" placeholder="dd/mm/aaaa" inline="true"/>
		</div>	
		<div class="col-md-3">							
			<hel:inputText name="numero" textKey="anotacio.llistat.filtre.camp.numero" placeholderKey="anotacio.llistat.filtre.camp.numero" inline="true"/>
		</div>	
		<div class="col-md-3">							
			<hel:inputText name="extracte" textKey="anotacio.llistat.filtre.camp.extracte" placeholderKey="anotacio.llistat.filtre.camp.extracte" inline="true"/>
		</div>
		<div class="col-md-2 pull-right">
			<div class="pull-right">
					<button id="consultarHidden" type="submit" name="accio" value="consultar" class="btn btn-primary hidden"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
					<button id="netejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
					<button id="consultar" type="submit" name="accio" value="consultar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-2">							
			<hel:inputText name="codiProcediment" textKey="anotacio.llistat.filtre.camp.codiProcediment" placeholderKey="anotacio.llistat.filtre.camp.codiProcediment" inline="true"/>
		</div>	
		<div class="col-md-2">							
			<hel:inputText name="codiAssumpte" textKey="anotacio.llistat.filtre.camp.codiAssumpte" placeholderKey="anotacio.llistat.filtre.camp.codiAssumpte" inline="true"/>
		</div>	
		<div class="col-md-3">							
			<hel:inputText name="numeroExpedient" textKey="anotacio.llistat.filtre.camp.numeroExpedient" placeholderKey="anotacio.llistat.filtre.camp.numeroExpedient" inline="true"/>
		</div>	
		<div class="col-md-3">
			<hel:inputSelect name="expedientTipusId" textKey="anotacio.llistat.filtre.camp.expedientTipus"
				optionItems="${expedientsTipus}" optionValueAttribute="codi" emptyOption="true"
				inline="true" placeholderKey="anotacio.llistat.filtre.camp.expedientTipus" optionTextAttribute="valor" />
		</div>					
		<div class="col-md-2">
			<hel:inputSelect inline="true" name="estat" optionItems="${estats}" emptyOption="true" textKey="anotacio.llistat.filtre.camp.estat" placeholderKey="anotacio.llistat.filtre.camp.estat" optionValueAttribute="codi" optionTextAttribute="valor"/>
		</div>					
	</div>

	</form:form>

	<table	id="anotacio"
			data-toggle="datatable"
			data-url="<c:url value="anotacio/datatable"/>"
			data-paging-enabled="true"
			data-ordering="true"
			data-default-order="1"
			data-default-dir="desc"
			data-rowhref-template="#rowhrefTemplate"
			data-rowhref-toggle="modal"
			data-rowhref-maximized="true"
			class="table table-striped table-bordered table-hover"
			style="width:100%">			
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"/>
				<th data-col-name="data" data-converter="datetime"><spring:message code="anotacio.llistat.columna.data"/></th>
				<th data-col-name="identificador"><spring:message code="anotacio.llistat.columna.identificador"/></th>
				<th data-col-name="extracte" data-renderer="maxLength(50)"><spring:message code="anotacio.llistat.columna.extracte"/></th>
				<th data-col-name="procedimentCodi"><spring:message code="anotacio.llistat.columna.procedimentCodi"/></th>
				<th data-col-name="expedientNumero"><spring:message code="anotacio.llistat.columna.expedientNumero"/></th>
				<th data-col-name="dataRecepcio" data-converter="datetime"><spring:message code="anotacio.llistat.columna.dataRecepcio"/></th>				
				<th data-col-name="expedientTipus.codi" data-template="#cellAnotacioExpedientTipusTemplate">
					<spring:message code="anotacio.llistat.columna.expedientTipus"/>
					<script id="cellAnotacioExpedientTipusTemplate" type="text/x-jsrender">
						{{if expedientTipus != null }}
							{{:expedientTipus.codi}}
							<span class="fa fa-info-circle" 
								title="{{:expedientTipus.codi}} - {{:expedientTipus.nom}} 
(Entrorn {{:expedientTipus.entorn.codi}} - {{:expedientTipus.entorn.nom}})"></span> 
						{{/if}}
					</script>
				</th>
				<th data-col-name="expedient.numero" data-template="#cellAnotacioExpedientTemplate">
					<spring:message code="anotacio.llistat.columna.expedient"/>
					<script id="cellAnotacioExpedientTemplate" type="text/x-jsrender">
						{{if expedient != null }}
							{{:expedient.identificador}}
						{{/if}}
					</script>
				</th>
				<th data-col-name="errorAnnexos" data-visible="false"/>
				<th data-col-name="estat" data-template="#cellEstatExpedientTemplate">
					<spring:message code="anotacio.llistat.columna.estat"/>
					<script id="cellEstatExpedientTemplate" type="text/x-jsrender">
						{{:estat}}
						{{if dataProcessament}}
							<br/><span class="text-muted small">
									{{:~formatTemplateDate(dataProcessament)}} 
								</span>
						{{/if}}
						{{if errorAnnexos}}
							<div class="pull-right">
								<span class="fa fa-exclamation-triangle text-danger" 
								title="<spring:message code="expedient.anotacio.llistat.error.annexos"/>"></span>
							</div>
						{{/if}}
					</script>
				</th>
				<th data-col-name="dataProcessament" data-visible="false"/></th>
				<th data-col-name="id" data-template="#cellAnotacioAccioTemplate" data-orderable="false" width="10%">
					<script id="cellAnotacioAccioTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/v3/anotacio/{{:id}}"/>" data-toggle="modal" data-maximized="true"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
								{{if expedient != null }}
									<li><a href="<c:url value="/v3/expedient/{{:expedient.id}}"/>"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code="anotacio.llistat.accio.expedient"/></a></li>
								{{/if}}
								{{if estat == 'PENDENT'}}
									<li><a href="<c:url value="/v3/anotacio/{{:id}}/acceptar"/>" data-maximized="true" data-toggle="modal"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.acceptar"/></a></li>
									<li><a href="<c:url value="/v3/anotacio/{{:id}}/rebutjar"/>" data-toggle="modal" data-min-height="1200"><span class="fa fa-times"></span>&nbsp;<spring:message code="comu.boto.rebutjar"/></a></li>
									<li><a href="<c:url value="/v3/anotacio/{{:id}}/delete"/>" data-rdt-link-ajax="true" data-confirm="<spring:message code="anotacio.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								{{/if}}
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="rowhrefTemplate" type="text/x-jsrender"><c:url value="/v3/anotacio/{{:id}}"/></script>	
	
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
