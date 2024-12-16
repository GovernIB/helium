<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
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
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
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
		<div class="col-md-2">							
			<hel:inputText name="numero" textKey="anotacio.llistat.filtre.camp.numero" placeholderKey="anotacio.llistat.filtre.camp.numero" inline="true"/>
		</div>	
		<div class="col-md-4">							
			<hel:inputText name="extracte" textKey="anotacio.llistat.filtre.camp.extracte" placeholderKey="anotacio.llistat.filtre.camp.extracte" inline="true"/>
		</div>		
		<div class="col-md-2">
			<hel:inputSelect inline="true" name="estat" optionItems="${estats}" emptyOption="true" textKey="anotacio.llistat.filtre.camp.estat" placeholderKey="anotacio.llistat.filtre.camp.estat" optionValueAttribute="codi" optionTextAttribute="valor"/>
		</div>	
	</div>
	<div class="row">
		<div class="col-md-4">							
			<hel:inputSuggest 
					name="codiProcediment" 
					urlConsultaInicial="/helium/v3/procediment/suggestInici" 
					urlConsultaLlistat="/helium/v3/procediment/suggest" 
					placeholderKey="anotacio.llistat.filtre.camp.codiProcediment"
					inline="true"
					/>	
		</div>	
		<div class="col-md-2">							
			<hel:inputText name="codiAssumpte" textKey="anotacio.llistat.filtre.camp.codiAssumpte" placeholderKey="anotacio.llistat.filtre.camp.codiAssumpte" inline="true"/>
		</div>	
		<div class="col-md-4">
			<hel:inputSelect name="expedientTipusId" textKey="anotacio.llistat.filtre.camp.expedientTipus"
				optionItems="${expedientsTipus}" optionValueAttribute="codi" emptyOption="true"
				inline="true" placeholderKey="anotacio.llistat.filtre.camp.expedientTipus" optionTextAttribute="valor" />
		</div>					
		<div class="col-md-2">							
			<hel:inputText name="numeroExpedient" textKey="anotacio.llistat.filtre.camp.numeroExpedient" placeholderKey="anotacio.llistat.filtre.camp.numeroExpedient" inline="true"/>
		</div>	
	</div>
	<div class="row">

		<div class="col-md-4">
			<hel:inputSuggest 
					name="unitatOrganitzativaCodi" 
					urlConsultaInicial="/helium/v3/unitatOrganitzativa/suggestInici" 
					urlConsultaLlistat="/helium/v3/unitatOrganitzativa/suggest" 
					placeholderKey="anotacio.llistat.filtre.camp.unitat.organitzativa.desti"
					inline="true"
					/>	
		</div>	

	
		<div class="col-md-2 pull-right">
			<div class="pull-right">
					<button id="consultarHidden" type="submit" name="accio" value="consultar" class="btn btn-primary hidden"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
					<button id="netejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
					<button id="consultar" type="submit" name="accio" value="consultar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
			</div>
		</div>				
	</div>
	
	</form:form>
	
	<table	id="anotacio"
			data-toggle="datatable"
			data-url="<c:url value="anotacio/datatable"/>"
			data-filter="#anotacioFiltreCommand"
			data-paging-enabled="true"
			data-ordering="true"
			data-default-order="1"
			data-default-dir="desc"
			data-selection-enabled="true"
			data-selection-url="anotacio/selection"
			data-selection-counter="#tramitacioMassivaCount"
			data-info-type="button"
			data-botons-template="#tableButtonsAccionsTemplate"
			class="table table-striped table-bordered table-hover"
			style="width:100%">			
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false" data-orderable="false"/>
				<th data-col-name="data" data-converter="datetime" width="7%"><spring:message code="anotacio.llistat.columna.data"/></th>
				<th data-col-name="identificador"><spring:message code="anotacio.llistat.columna.identificador"/></th>
				<th data-col-name="extracte" data-renderer="maxLength(50)"><spring:message code="anotacio.llistat.columna.extracte"/></th>
				<th data-col-name="procedimentCodi"><spring:message code="anotacio.llistat.columna.procedimentCodi"/></th>
				<th data-col-name="expedientNumero"><spring:message code="anotacio.llistat.columna.expedientNumero"/></th>
				<th data-col-name="dataRecepcio" data-converter="datetime" width="7%"><spring:message code="anotacio.llistat.columna.dataRecepcio"/></th>				
				<th data-col-name="destiCodiAndNom" data-visible="false" data-orderable="false"/>
				<th data-col-name="expedientTipus.codi" data-template="#cellAnotacioExpedientTipusTemplate" width="12%">
					<spring:message code="anotacio.llistat.columna.expedientTipus"/>
					<script id="cellAnotacioExpedientTipusTemplate" type="text/x-jsrender">
						{{if expedientTipus != null }}
							{{if expedientTipus.procedimentComu == true }}							
								<span class="fa fa-university" title="{{:destiCodiAndNom}}" style="float:left"></span>&nbsp;
							{{/if}} 
							{{:expedientTipus.codi}}
							{{if expedientTipus.distribucioProcesAuto == true }}	
								<span class="fa fa-info-circle" style="color:#337ab7"
									title="{{:expedientTipus.codi}} - {{:expedientTipus.nom}} 
(Entorn {{:expedientTipus.entorn.codi}} - {{:expedientTipus.entorn.nom}}) 
(automàtic)">
								</span> 
							{{else}}
								<span class="fa fa-info-circle"
									title="{{:expedientTipus.codi}} - {{:expedientTipus.nom}} 
(Entorn {{:expedientTipus.entorn.codi}} - {{:expedientTipus.entorn.nom}})
(manual)">
								</span> 
							{{/if}}
						</span>
							 
						{{/if}}
					</script>
				</th>
				<th data-col-name="expedient.numero" data-template="#cellAnotacioExpedientTemplate" width="12%">
					<spring:message code="anotacio.llistat.columna.expedient"/>
					<script id="cellAnotacioExpedientTemplate" type="text/x-jsrender">
						{{if expedient != null }}
							{{:expedient.identificador}}
						{{/if}}
					</script>
				</th>
				<th data-col-name="errorAnnexos" data-visible="false"/>
				<th data-col-name="annexosInvalids" data-visible="false"/>
				<th data-col-name="annexosEsborranys" data-visible="false"/>
				<th data-col-name="processant" data-visible="false"/>
				<th data-col-name="estat" data-template="#cellEstatExpedientTemplate" width="7%">
					<spring:message code="anotacio.llistat.columna.estat"/> <span class="fa fa-list" id="showModalProcesEstatButton" title="<spring:message code="anotacio.llistat.columna.estat.llegenda"/>" style="cursor:over; opacity: 0.5"></span>
					<script id="cellEstatExpedientTemplate" type="text/x-jsrender">
						{{if estat == 'PENDENT'}}
							<spring:message code="enum.anotacio.estat.PENDENT"></spring:message>
						{{else estat == 'PENDENT_AUTO'}}
							<spring:message code="enum.anotacio.estat.PENDENT_AUTO"></spring:message>
						{{else estat == 'PROCESSADA'}}
							<spring:message code="enum.anotacio.estat.PROCESSADA"></spring:message>
						{{else estat == 'REBUTJADA'}}
							<spring:message code="enum.anotacio.estat.REBUTJADA" ></spring:message>
							<span class="fa fa-info-circle" title="<spring:message code="anotacio.detalls.rebuigMotiu">{{>rebuigMotiu}}</spring:message>"></span>
						{{else estat == 'COMUNICADA'}}
							<spring:message code="enum.anotacio.estat.COMUNICADA"></spring:message>
						{{else estat == 'ERROR_PROCESSANT'}}
							<spring:message code="enum.anotacio.estat.ERROR_PROCESSANT"></spring:message>
						{{else}}
							{{:estat}}
						{{/if}}

						{{if dataProcessament}}
							<br/><span class="text-muted small">
									{{:~formatTemplateDate(dataProcessament)}} 
								</span>
						{{/if}}
						{{if errorProcessament != null}}
							<br/><span class="fa fa-exclamation-triangle text-danger" title="{{>errorProcessament}}"></span>
						{{/if}}
						{{if errorAnnexos}}
							<div class="pull-right">
								<span class="fa fa-exclamation-triangle text-danger" 
								title="<spring:message code="expedient.anotacio.llistat.error.annexos"/>"></span>
							</div>
						{{/if}}
						{{if annexosInvalids}}
							<div class="pull-right">
								<span class="fa fa-exclamation-triangle text-danger" 
								title="<spring:message code="expedient.anotacio.llistat.annexos.invalids"/>"></span>
							</div>
						{{/if}}
						{{if annexosEsborranys}}
							<div class="pull-right">
								<span class="fa fa-exclamation-triangle text-warning"
								title="<spring:message code="expedient.anotacio.llistat.annexos.esborranys"/>"></span>
							</div>
						{{/if}}
						{{if processant}}
							<div class="pull-right">
								<span class="fa fa-cog fa-spin"
								title="<spring:message code="expedient.anotacio.llistat.processant"/>"></span>
							</div>
						{{/if}}
						{{if (estat == 'PENDENT_AUTO') &&  !processant}}
							<div class="pull-right">
								<span class="fa fa-clock-o"
								title="<spring:message code="expedient.anotacio.llistat.processar.auto"/>"></span>
							</div>
						{{/if}}
						{{if estat == 'COMUNICADA'}}
							<br/><span class="text-muted small">
									{{:consultaIntents}} / ${maxConsultaIntents}
									{{if consultaError != null }}
										<span class="fa fa-exclamation-triangle text-danger" title="{{>consultaError}}"></span>
									{{/if}}
									{{if consultaData }}
										{{:~formatTemplateDate(consultaData)}}
									{{/if}}
								</span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="dataProcessament" data-visible="false"/></th>
				<th data-col-name="errorProcessament" data-visible="false"/>
				<th data-col-name="rebuigMotiu" data-visible="false"/>	
				<th data-col-name="consultaIntents" data-visible="false"/>
				<th data-col-name="consultaError" data-visible="false"/>
				<th data-col-name="consultaData" data-visible="false"/>
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
								{{/if}}
								{{if estat == 'PENDENT' || estat == 'COMUNICADA' || estat == 'ERROR_PROCESSANT'}}
									<li><a href="<c:url value="/v3/anotacio/{{:id}}/delete"/>" data-rdt-link-ajax="true" data-confirm="<spring:message code="anotacio.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								{{/if}}
								{{if estat == 'ERROR_PROCESSANT'}}
									<li><a href="<c:url value="/v3/anotacio/{{:id}}/reprocessar"/>" data-rdt-link-ajax="true"><span class="fa fa-cog"></span>&nbsp;<spring:message code="anotacio.llistat.accio.reprocessar"/></a></li>
									<li><a href="<c:url value="/v3/anotacio/{{:id}}/marcarPendent"/>" data-rdt-link-ajax="true"><span class="fa fa-check"></span>&nbsp;<spring:message code="anotacio.llistat.accio.marcarPendent"/></a></li>
								{{/if}}
								{{if estat == 'COMUNICADA' && consultaIntents >= ${maxConsultaIntents}}}
 									<li><a href="<c:url value="/v3/anotacio/{{:id}}/reintentarConsulta"/>" data-rdt-link-ajax="true"><span class="fa fa-eraser"></span>&nbsp;<spring:message code="anotacio.llistat.accio.reintentarConsulta"/></a></li>
								{{/if}}
								<!-- Opció de comunicar per email -->
								{{if estat == 'PENDENT' || estat =='PROCESSADA'  }}
									<li>
										<a href="<c:url value="/v3/anotacio/{{:id}}/email"/>" data-rdt-link-ajax="true" 
												{{if estat == 'PENDENT'}}
													data-confirm="<spring:message code="anotacio.llistat.email.avisar.confirmacio.pedent"/>"
												{{else}}
													data-confirm="<spring:message code="anotacio.llistat.email.avisar.confirmacio.processada"/>"
												{{/if}}
											>
											<span class="fa fa-envelope"></span>&nbsp;<spring:message code="anotacio.llistat.email.avisar"/>
										</a>
									</li>
								{{/if}}
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>	

	<div id="modal-error" class="modal fade">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="<spring:message code="comu.boto.tancar"/>"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title"></h4>
				</div>
				<div class="modal-body">
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></button>
				</div>
			</div>
		</div>
	</div>	
	<!-- Modal pels estats del processament -->
	<div id="modalProcesEstat" class="modal fade">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="<spring:message code="comu.boto.tancar"/>"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title"><span class="fa fa-list"></span> <spring:message code="anotacio.llistat.columna.estat.llegenda"></spring:message></h4>
				</div>
				<div class="modal-body">
					<ul>
						<c:set var="enumValues" value="<%=net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto.values()%>"/>
						<c:forEach items="${enumValues}" var="enumValue">
						  	<li>
						  		<strong><spring:message code="anotacio.proces.estat.enum.${enumValue}"/></strong> :
						  		<br/>
						  		<span><spring:message code="anotacio.proces.estat.enum.${enumValue}.info"/></span>
						  	</li>
						</c:forEach>
						<li>
							<span><spring:message code="anotacio.proces.estat.rodeta.info"/></span>
						</li>
					</ul>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></button>
				</div>
			</div>
		</div>
	</div>	
	<script id="rowhrefTemplate" type="text/x-jsrender"><c:url value="/v3/anotacio/{{:id}}"/></script>	
	
	<script id="tableButtonsAccionsTemplate" type="text/x-jsrender">	
		<div class="botons-titol text-right">
			<b><spring:message code="anotacio.llistat.numero.threads.consultats"/></b>
			<c:choose>
				<c:when test="${!empty globalProperties['app.anotacions.consulta.num.threads']}">
					${globalProperties['app.anotacions.consulta.num.threads']}
				</c:when>
				<c:otherwise>
					5
				</c:otherwise>
			</c:choose>	
			<span ></span>&nbsp;
			<a id="exportar_excel" href="<c:url value="/v3/anotacio/excel"/>" class="btn btn-default">
				<span class="fa fa-download"></span>&nbsp;<spring:message code="comuns.descarregar"/>
			</a>
			<div id="btnTramitacio" class="btn-group">
			
				<button id="seleccioAll" title="<spring:message code="expedient.llistat.accio.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></button>
				<button id="seleccioNone" title="<spring:message code="expedient.llistat.accio.seleccio.netejar"/>" class="btn btn-default"><span class="fa fa-square-o"></span></button>

				<button class="btn btn-default" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span id="tramitacioMassivaCount" class="badge">&nbsp;&nbsp;</span>&nbsp;<span class="caret"></span></button>
 					<ul class="dropdown-menu">
						<li><a id="selection" href="<c:url value="/v3/anotacio/reintentarConsulta"/>" data-maximized="true" data-rdt-link-ajax="true" title="<spring:message code="anotacio.llistat.reintentar.consulta.title"/>"><spring:message code="anotacio.llistat.reintentar.consulta"/></a></li>
 						<li><a id="selection" href="<c:url value="/v3/anotacio/reintentarProcessament"/>" data-rdt-link-ajax="true" title="<spring:message code="anotacio.llistat.reintentar.processament.title"/>"><spring:message code="anotacio.llistat.reintentar.processament"/></a></li>
						<li><a id="selection" href="<c:url value="/v3/anotacio/reprocessarMapeig"/>" data-toggle="modal" data-datatable-id="" title="<spring:message code="anotacio.llistat.reintentar.mapeig.title"/>"><spring:message code="anotacio.llistat.reintentar.mapeig"/></a></li>
						<li><a id="selection" href="<c:url value="/v3/anotacio/reintentarProcessamentNomesAnnexos"/>" data-rdt-link-ajax="true" title="<spring:message code="anotacio.llistat.reintentar.processament.nomes.annexos.title"/>"><spring:message code="anotacio.llistat.reintentar.processament.nomes.annexos"/></a></li>
						<li><a id="selection" href="<c:url value="/v3/anotacio/esborrarAnotacions"/>" data-rdt-link-ajax="true" title="<spring:message code="anotacio.llistat.esborrar.anotacions.title"/>"><spring:message code="anotacio.llistat.esborrar.anotacions"/></a></li>
 					</ul>
			</div>
		</div>
	</script>
		
	<script type="text/javascript">
	// <![CDATA[

	   $.views.helpers({
		   formatTemplateDate: function (d) {
			   return moment(new Date(d)).format("DD/MM/YYYY HH:mm:ss");
		    }
		});
		            
	$(document).ready(function() {
		$('#showModalProcesEstatButton').click(function(e) {
			$('#modalProcesEstat').modal();
			e.stopPropagation();
		});	
		
			var selectButtonsInitialized = false;
		
			$('#anotacio').on( 'draw.dt', function () {		

				if (!selectButtonsInitialized) {

					selectButtonsInitialized = true;
					$('#seleccioAll').on('click', function(e) {
						$.get(
								"anotacio/seleccioTots",
								function(data) {
									$("#tramitacioMassivaCount").html(data);
									$('#anotacio').webutilDatatable('refresh');
								}
						);
						return false;
					});
					$('#seleccioNone').on('click', function() {
						$.get(
								"anotacio/seleccioNetejar",
								function(data) {
									$("#tramitacioMassivaCount").html(data);
									$('#anotacio').webutilDatatable('select-none');
								}
						);
						return false;
					});	
				}//if
			})
			.on('selectionchange.dataTable', function (accio, ids) {
				console.log('selectionchange.dataTable');
				$.get(
					"anotacio/" + accio,
					{ids: ids},
					function(data) {
						$("#tramitacioMassivaCount").html(data);
					});
			});
			
		$("#netejar").click(function() {
			$('#codiProcediment').val('').change();
			$('#unitatOrganitzativaCodi').val('').change();
			$('#expedientTipusId').val('').change();
			$('#estat').val("PENDENT").change().change();
		})
			
	});
	
	// ]]>
	</script>	

</body>
</html>
