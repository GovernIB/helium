<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="tasca.llistat.titol"/></title>
	<meta name="capsaleraTipus" content="llistat"/>
	
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	
	<script src="<c:url value="/js/jquery-1.10.2.min.js"/>"></script> 
	<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script src="<c:url value="/js/bootstrap.min.js"/>"></script>
    <script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${idioma}.js"/>"></script>
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/helium.datatable.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<script>
		$(document).ready(function() {
			$("#taulaDades").heliumDataTable({
				ajaxSourceUrl: "<c:url value="/v3/tasca/datatable"/>",
				localeUrl: "<c:url value="/js/dataTables-locales/dataTables_locale_ca.txt"/>",
				alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
				rowClickCallback: function(row) {
					alert($(row));
					<c:if test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId == null}">
						$('a.consultar-tasca', $(row))[0].click();
					</c:if>
				},
				seleccioCallback: function(seleccio) {
					$('#reasignacioMassivaCount').html(seleccio.length);
					<c:if test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId != null}">
						$('#tramitacioMassivaCount').html(seleccio.length);
						$("input[name=correu]").trigger('change');	
					</c:if>
				}
			});		
            $("#inici_timer").on("dp.change",function (e) {
            	$("input[name=correu]").trigger('change');
			});
			$("input[name=correu]").change(function(){
				$("a[id='btnMassiva']").attr("href","../../../v3/expedient/massivaTramitacioTasca?inici="+$("#inici").val()+"&correu="+$("#correu").is(":checked"));
				$("a[id='btnReassignacioMassiva']").attr("href","../../../v3/tasca/massivaReassignacioTasca?massiva=true&inici="+$("#inici").val()+"&correu="+$("#correu").is(":checked"));
			});
			$("#mostrarTasquesPersonalsCheck").click(function() {
				$("input[name=mostrarTasquesPersonals]").val(!$("#mostrarTasquesPersonalsCheck").hasClass('active'));
				$('#tascaConsultaCommand').submit();
			});
			$("#mostrarTasquesGrupCheck").click(function() {
				$("input[name=mostrarTasquesGrup]").val(!$("#mostrarTasquesGrupCheck").hasClass('active'));
				$('#tascaConsultaCommand').submit();
			});			
			$('#inici_timer').datetimepicker({
				language: '${idioma}',
				minDate: new Date(),
				format: "DD/MM/YYYY HH:mm"
		    });
		});
	</script>
	<style type="text/css">
		#opciones .label-titol {padding-bottom: 0px;} 
 		.control-group {width: 100%;display: inline-block;} 
 		.control-group-mid {width: 48%;} 
  		.control-group.left {float: left; margin-right:1%;} 
  		#div_timer label {float:left;} 
	</style>
</head>
<body>

	<form:form action="" method="post" cssClass="well formbox" commandName="tascaConsultaCommand">
		<form:hidden path="filtreDesplegat"/>
		<c:choose>
			<c:when test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId != null}">
				<div id="div_timer" class="control-group left control-group-mid">
			    	<label for="inici"><spring:message code="expedient.consulta.datahorainici" /></label>
					<div class='col-sm-6'>
			            <div class="form-group">
			                <div class='input-group date' id='inici_timer'>
			                    <input id="inici" name="inici" class="form-control" data-format="dd/MM/yyyy hh:mm" type="text">
			                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
			                </div>
			            </div>
			            <script type="text/javascript">
		                    $(document).ready(function() {
								$("#inici").on('focus', function() {
									$('.glyphicon-calendar').click();
								});
							});
	                    </script>
			    	</div>
				</div>
				
				<div class="control-group form-group control-group-mid">
					<input type="checkbox" id="correu" name="correu" value="${correu}"/>
					<label for="correu"><spring:message code="expedient.massiva.correu"/></label>
				</div>
			</c:when>
			<c:otherwise>			
				<div id="filtresCollapsable" class="collapse<c:if test="${true or tascaConsultaCommand.filtreDesplegat}"> in</c:if>">
					<div class="row">
						<div class="col-md-2">
							<hel:inputText name="tasca" textKey="tasca.llistat.filtre.camp.titol" placeholderKey="tasca.llistat.filtre.camp.titol" inline="true"/>
						</div>
						<div class="col-md-4">
							<hel:inputText name="expedient" textKey="tasca.llistat.filtre.camp.expedient" placeholderKey="tasca.llistat.filtre.camp.expedient" inline="true"/>
						</div>
						<!-- <div class="col-md-3">
							<hel:inputSelect name="prioritat" textKey="tasca.llistat.filtre.camp.prioritat" placeholderKey="tasca.llistat.filtre.camp.prioritat" optionItems="${prioritats}" optionValueAttribute="valor" optionTextAttribute="codi" inline="true"/>
						</div>
						 -->
						<div class="col-md-3">
							<hel:inputSelect name="expedientTipusId" textKey="tasca.llistat.filtre.camp.tipexp" placeholderKey="tasca.llistat.filtre.camp.tipexp" optionItems="${expedientTipusAccessibles}" optionValueAttribute="id" optionTextAttribute="nom"  disabled="${not empty expedientTipusActual}" inline="true"/>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4">
							<label><spring:message code="tasca.llistat.filtre.camp.datcre"/></label>
							<div class="row">
								<div class="col-md-6">
									<hel:inputDate name="dataCreacioInicial" textKey="tasca.llistat.filtre.camp.datcre.ini" placeholder="dd/mm/yyyy" inline="true"/>
								</div>
								<div class="col-md-6">
									<hel:inputDate name="dataCreacioFinal" textKey="tasca.llistat.filtre.camp.datcre.fin" placeholder="dd/mm/yyyy" inline="true"/>
								</div>
							</div>
						</div>
						<div class="col-md-4">
							<label><spring:message code="tasca.llistat.filtre.camp.datlim"/></label>
							<div class="row">
								<div class="col-md-6">
									<hel:inputDate name="dataLimitInicial" textKey="tasca.llistat.filtre.camp.datlim.ini" placeholder="dd/mm/yyyy" inline="true"/>
								</div>
								<div class="col-md-6">
									<hel:inputDate name="dataLimitFinal" textKey="tasca.llistat.filtre.camp.datlim.fin" placeholder="dd/mm/yyyy" inline="true"/>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-6">
						<form:hidden path="mostrarTasquesPersonals"/>
						<form:hidden path="mostrarTasquesGrup"/>
						<div class="btn-group">
							<button id="mostrarTasquesPersonalsCheck" title="<spring:message code="tasca.llistat.filtre.camp.mostrar.usuari"/>" class="btn btn-default<c:if test="${tascaConsultaCommand.mostrarTasquesPersonals}"> active</c:if>" data-toggle="button"><span class="fa fa-user"></span></button>
							<button id="mostrarTasquesGrupCheck" title="<spring:message code="tasca.llistat.filtre.camp.mostrar.grup"/>" class="btn btn-default<c:if test="${tascaConsultaCommand.mostrarTasquesGrup}"> active</c:if>" data-toggle="button"><span class="fa fa-users"></span></button>
						</div>
					</div>
					<div class="col-md-6">
						<div class="pull-right">
							<input type="hidden" name="consultaRealitzada" value="true"/>
							<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
							<button type="submit" name="accio" value="consultar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
						</div>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</form:form>

	<table id="taulaDades" class="table table-striped table-bordered table-hover" data-rdt-button-template="tableButtonsTemplate" <c:if test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId != null}"> data-rdt-paginable="false"</c:if> data-rdt-seleccionable-columna="0" data-rdt-filtre-form-id="tascaConsultaCommand" data-rdt-seleccionable="true" <c:if test="${not empty preferenciesUsuari.numElementosPagina}">data-rdt-display-length-default="${preferenciesUsuari.numElementosPagina}"</c:if>>
		<thead>
			<tr>
				<th data-rdt-property="id" width="4%" data-rdt-sortable="false" data-rdt-visible="true"></th>
				<th data-rdt-property="titol" data-rdt-template="cellPersonalGroupTemplate" data-rdt-visible="true" >
					<spring:message code="tasca.llistat.columna.titol"/>
					<script id="cellPersonalGroupTemplate" type="text/x-jsrender">
						{{:titol}}
						{{if responsables != null && !agafada}}
							<span class="fa fa-users"></span>
						{{/if}}
						<div class="pull-right">
						{{if cancelada}}
							<span class="label label-danger" title="<spring:message code="enum.tasca.etiqueta.CA"/>">CA</span>
						{{/if}}
						{{if suspesa}}
							<span class="label label-info" title="<spring:message code="enum.tasca.etiqueta.SU"/>">SU</span>
						{{/if}}
						{{if oberta}}
							<span class="label label-warning" title="<spring:message code="enum.tasca.etiqueta.PD"/>">PD</span>
						{{/if}}
						{{if completed}}
							<span class="label label-success" title="<spring:message code="enum.tasca.etiqueta.FI"/>">FI</span>
						{{/if}}
						{{if agafada}}
							<span class="label label-default" title="<spring:message code="enum.tasca.etiqueta.AG"/>">AG</span>
						{{/if}}
 						{{if tramitacioMassiva}}													
							<a <c:if test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId != null}">onclick="return false;"</c:if> href="../v3/tasca/{{:id}}/massiva"><span class="label label-default" title="<spring:message code="tasca.llistat.accio.tramitar_massivament"/>"><i class="fa fa-files-o"></i></span></a>
						{{/if}}						
						</div>
					</script>
				</th>
				<th data-rdt-property="expedientIdentificador" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.expedient"/></th>
				<th data-rdt-property="expedientTipusNom" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.tipexp"/></th>
				<th data-rdt-property="dataCreacio" data-rdt-type="datetime" data-rdt-sorting="desc" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.creada"/></th>
				<th data-rdt-property="dataLimit" data-rdt-type="datetime" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.limit"/></th>
				<th data-rdt-property="prioritat" data-rdt-visible="false"><spring:message code="tasca.llistat.columna.prioritat"/></th>
				<th data-rdt-property="id" data-rdt-template="cellAccionsTemplate" data-rdt-visible="<c:out value="${tascaConsultaCommand.consultaTramitacioMassivaTascaId == null}"/>" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
 						<div class="dropdown navbar-right"> 
 							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button> 
							<ul class="dropdown-menu"> 
								{{if oberta && !suspesa}}
									{{if tramitacioMassiva}}
										<li><a href="../v3/tasca/{{:id}}/massiva"><span class="fa fa-files-o"></span> <spring:message code="tasca.llistat.accio.tramitar_massivament"/></a></li>
									{{/if}}
 									<li><a class="consultar-tasca" href="<c:url value="../v3/expedient/{{:expedientId}}/tasca/{{:id}}"/>" data-rdt-link-modal="true"><span class="fa fa-folder-open"></span> <spring:message code="tasca.llistat.accio.tramitar"/></a></li>
									<li><a href="<c:url value="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/delegar"/>" data-rdt-link-modal="true"><span class="fa fa-hand-o-right"></span> <spring:message code="tasca.llistat.accio.delegar"/></a></li>
								{{/if}}
								{{if responsables != null && !agafada && oberta && !suspesa}}
 									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/agafar" data-rdt-link-ajax="true"><span class="fa fa-chain"></span> <spring:message code="tasca.llistat.accio.agafar"/></a></li>
								{{/if}}
								<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/reassignar" data-rdt-link-modal="true"><span class="fa fa-share-square-o"></span>&nbsp;<spring:message code="tasca.llistat.accio.reassignar"/></a></li>
								{{if oberta && !suspesa}}
									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/suspendre" data-rdt-link-confirm="<spring:message code="tasca.llistat.confirmacio.suspendre"/>"><span class="fa fa-pause"></span> <spring:message code="tasca.llistat.accio.suspendre"/></a></li>
								{{/if}}
								{{if suspesa}}
									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/reprendre" data-rdt-link-confirm="<spring:message code="tasca.llistat.confirmacio.reprendre"/>"><span class="fa fa-play"></span> <spring:message code="tasca.llistat.accio.reprendre"/></a></li>
								{{/if}}
								{{if !cancelada}}
									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/cancelar" data-rdt-link-confirm="<spring:message code="tasca.llistat.confirmacio.cancelar"/>"><span class="fa fa-times"></span> <spring:message code="tasca.llistat.accio.cancelar"/></a></li>
								{{/if}}
						
 							</ul> 
 						</div>
					</script> 
				</th>
				<th data-rdt-property="agafada" data-rdt-visible="false"></th>
				<th data-rdt-property="cancelada" data-rdt-visible="false"></th>
				<th data-rdt-property="suspesa" data-rdt-visible="false"></th>
				<th data-rdt-property="tramitacioMassiva" data-rdt-visible="false"></th>
				<th data-rdt-property="oberta" data-rdt-visible="false"></th>
				<th data-rdt-property="completed" data-rdt-visible="false"></th>				
				<th data-rdt-property="expedientId" data-rdt-visible="false"></th>
				<th data-rdt-property="responsables" data-rdt-visible="false"></th>
			</tr>
		</thead>
	</table>
	
		<script id="tableButtonsTemplate" type="text/x-jsrender">
			<div style="text-align:right">
				<div id="btnTramitacio" class="btn-group">
					<c:choose>
						<c:when test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId == null}">
							<a class="btn btn-default" href="../v3/tasca/seleccioTots" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.tots"/>"><span class="fa fa-check-square-o"></span></a>
							<a class="btn btn-default" href="../v3/tasca/seleccioNetejar" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.netejar"/>"><span class="fa fa-square-o"></span></a>
							<a class="btn btn-default" data-rdt-link-modal="true" href="../v3/tasca/massivaReassignacioTasca?massiva=false"><spring:message code="tasca.llistat.reassignacions.massiva"/>&nbsp;<span id="reasignacioMassivaCount" class="badge">&nbsp;</span></a>
						</c:when>
						<c:otherwise>
							<a class="btn btn-default" href="../../../v3/tasca/seleccioTots" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.tots"/>"><span class="fa fa-check-square-o"></span></a>
							<a class="btn btn-default" href="../../../v3/tasca/seleccioNetejar" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.netejar"/>"><span class="fa fa-square-o"></span></a>
							<a id="btnReassignacioMassiva" class="btn btn-default" data-rdt-link-modal="true" href="../../../v3/tasca/massivaReassignacioTasca?massiva=true"><spring:message code="tasca.llistat.reassignacions.massiva"/>&nbsp;<span id="reasignacioMassivaCount" class="badge">&nbsp;</span></a>
							<a id="btnMassiva" class="btn btn-default" data-rdt-link-modal-maximize="true" data-rdt-link-modal="true" href="../../../v3/expedient/massivaTramitacioTasca"><spring:message code="expedient.llistat.accio.massiva"/>&nbsp;<span id="tramitacioMassivaCount" class="badge">&nbsp;</span></a>
						</c:otherwise>
					</c:choose>	
				</div>
			</div>
		</script>				
					
		<script type="text/javascript">
			// <![CDATA[
				$('#btnTramitacio').heliumEvalLink({
					refrescarAlertes: true,
					refrescarPagina: false
				});
			//]]>
		</script>
</body>
</html>
