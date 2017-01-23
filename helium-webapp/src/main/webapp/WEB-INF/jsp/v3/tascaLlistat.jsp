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
	<meta name="title" content="<spring:message code='tasca.llistat.titol'/>"/>
	<meta name="title-icon-class" content="fa fa-clipboard"/>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/helium.datatable.js"/>"></script>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	
<script type="text/javascript">
	var tascaIdPerAgafar;
	$(document).ready(function() {
		window['ambPermisReassignment'] = false;
		$("#taulaDades").heliumDataTable({
			ajaxSourceUrl: "<c:url value="/v3/tasca/datatable"/>",
			localeUrl: "<c:url value="/js/dataTables-locales/dataTables_locale_ca.txt"/>",
			alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
			rowClickCallback: function(row) {
				<c:if test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId == null}">
					if ($('a.consultar-tasca', $(row)).length > 0)
						$('a.consultar-tasca', $(row))[0].click();
				</c:if>
			},
			seleccioCallback: function(seleccio) {
				$('#reasignacioMassivaCount').html(seleccio.length);
				<c:if test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId != null}">
					$('#tramitacioMassivaCount').html(seleccio.length);
				</c:if>
			},
			drawCallback: function() {
				$('.tasca-accio-agafar').click(function() {
					tascaIdPerAgafar = $(this).data('tasca-id');
				});
				if (tascaIdPerAgafar) {
					$('#tramitar-tasca-' + tascaIdPerAgafar).click();
				}
				tascaIdPerAgafar = null;
				filtreActiu();
				
				if ( $('#expedientTipusId').val() == "") {
					$('#botoReassignment').addClass('disabled');
					$('#liReassignment').attr('title', "<spring:message code='expedient.llistat.accio.reassignar.expedient.tipus.buit'/>");
				} else {
					if (window['ambPermisReassignment']) {
						$('#botoReassignment').removeClass('disabled');
						$('#liReassignment').attr('title', '');
					}
					else {
						$('#botoReassignment').addClass('disabled');
						$('#liReassignment').attr('title', "<spring:message code='expedient.llistat.accio.reassignar.sense.permis'/>");
					}
				}
				
			}
		});	
		$('.date_time').datetimepicker({
			locale: moment.locale('${idioma}'),
			minDate: new Date(),
			format: "DD/MM/YYYY HH:mm"
	    });
		$("form#tascaConsultaCommand button[data-toggle=button]").click(function() {
			var $formulari = $(this).closest('form');
			$("input[name=" + $(this).data("path") + "]", $formulari).val(!$(this).hasClass('active'));
			if ($(this).is('#nomesTasquesPersonalsCheck', $formulari)) {
				$('#nomesTasquesGrupCheck', $formulari).removeClass('active');
				$("input#nomesTasquesGrup", $formulari).val(false);
			}
			if ($(this).is('#nomesTasquesGrupCheck', $formulari)) {
				$('#nomesTasquesPersonalsCheck', $formulari).removeClass('active');
				$("input#nomesTasquesPersonals", $formulari).val(false);
			}
			$(this).blur();
			$("button#consultar", $formulari).click();
		});
		<c:if test="${entornId != null}">
			$('#expedientTipusId').on('change', function() {
				// Neteja la selecció de tasques
				$('#botoNetejarSeleccio').trigger('click');
				var tipus = $(this).val();
				$('#tasca').select2('val', '', true);
				$('#tasca option[value!=""]').remove();
				var value = -1;
				var entornId = ${entornId};
				if ($(this).val())
					value = $(this).val();
				if (value < 0) {
					// Neteja de la selecció
					$('#consultar').trigger('click');
				} else {
					//tasques per expedientTipus
					$.get('tasca/tasques/${entornId}/' + value)				
					.done(function(data) {
						for (var i = 0; i < data.length; i++) {
							$('#tasca').append('<option value="' + data[i].codi + '">' + data[i].valor + '</option>');
						}
						//Es fa el submit del formulari per cercar automàticament per tipus de d'expedient
						$('#consultar').trigger('click');
					})
					.fail(function() {
						alert("<spring:message code="expedient.llistat.tasca.ajax.error"/>");
					});
				}
				//permisos d'expedientTipus
				if (value != undefined && value != "-1"){
					$.get('tasca/expedientTipusAmbPermis/${entornId}/' + value)				
					.done(function(data) {
						if(data != undefined && data.permisReassignment){
							$('#responsableDiv').show();
							window['ambPermisReassignment'] = true;
						}else{
							$('#responsableDiv').hide();
							window['ambPermisReassignment'] = false;
						}
					})
					.fail(function() {
						alert("<spring:message code="expedient.llistat.expedientTipusPermis.ajax.error"/>");
					});
				}else{
					$('#responsableDiv').hide();
				}
			});
			$('#expedientTipusId').select2().on("select2-removed", function(e) {
				window['ambPermisReassignment'] = false;
		    })
			$('#expedientTipusId').trigger('change');
		</c:if>

		//Per defecte, si no s'especifica al fitxer de properties
		//tendrem un interval que executa una funció cada 10 segons per a refrescar les
		//ícones d'estat de les tasques en segon pla
		<c:set var="refrescaSegonPla" value="${globalProperties['app.segonpla.refrescar.auto'] == 'false' ? false : true}"/>
		<c:set var="refrescaSegonPlaPeriode" value="${globalProperties['app.segonpla.refrescar.auto.periode'] != null ? globalProperties['app.segonpla.refrescar.auto.periode'] : 10}"/>
		<c:set var="refrescaSegonPlaUrl" value="${tascaConsultaCommand.consultaTramitacioMassivaTascaId != null ? '../../tasca/actualitzaEstatsSegonPla' : 'tasca/actualitzaEstatsSegonPla'}"/>
		<c:if test="${refrescaSegonPla}">
			setInterval(refrescaEstatSegonPla, (${refrescaSegonPlaPeriode} * 1000));
		</c:if>
	});
	function refrescaEstatSegonPla() {
		var tasquesSegonPlaIds = [];
		$('span.segon-pla-icona').each(function (index, value) {
			var id = $(value).attr('id').split('spi-')[1]; 
		 	tasquesSegonPlaIds.push(id);	
		});
		if (tasquesSegonPlaIds.length > 0) {
			$.ajax({
			    url: "${refrescaSegonPlaUrl}",
			    data: {"tasquesSegonPlaIds": tasquesSegonPlaIds},
			    type: "POST",
			    success: function(data) {
				    //recorrem de nou les icones de les tasques per 
				    //actualitzar-ne l'estat
				    if (data != undefined) {
					    $.each(tasquesSegonPlaIds, function(ind,val) {
						    var canviar = false;
						    var previousClass = "";
						    var iconContent = "";
						    var tascaEstat = data[val];
						    if (tascaEstat != undefined) {
						    	if (!tascaEstat['completada'] && (tascaEstat['error'] != undefined || tascaEstat['marcadaFinalitzar'] != undefined || tascaEstat['iniciFinalitzacio'] != undefined)) {
									if (tascaEstat['error'] != undefined){
										iconContent = '<i class="fa fa-exclamation-circle fa-lg error" title="<spring:message code="error.finalitzar.tasca"/>: ' + tascaEstat['error'] + '"></i>';
										previousClass = "error";
									}
									if (tascaEstat['error'] == undefined && tascaEstat['marcadaFinalitzar'] != undefined && tascaEstat['iniciFinalitzacio'] == undefined) {
										iconContent = '<i class="fa fa-clock-o fa-lg programada" title="<spring:message code="enum.tasca.etiqueta.marcada.finalitzar"/> ' + (moment(new Date(tascaEstat['marcadaFinalitzar'])).format("DD/MM/YYYY HH:mm:ss")) + '"></i>';
										previousClass = "programada";
									}
									if (tascaEstat['error'] == undefined && tascaEstat['marcadaFinalitzar'] != undefined && tascaEstat['iniciFinalitzacio'] != undefined) {
										iconContent = '<i class="fa fa-circle-o-notch fa-spin fa-lg executant" title="<spring:message code="enum.tasca.etiqueta.execucio"/> ' + (moment(new Date(tascaEstat['iniciFinalitzacio'])).format("DD/MM/YYYY HH:mm:ss")) + '"></i>';
										previousClass = "executant";
									}
						    	} else if (tascaEstat['completada']) {
						    		iconContent = '<i class="fa fa-check-circle-o fa-lg"></i>';
						    		//refrescam el datatable
						    		refrescaPaginacio();
							    }				    	
							} else {
								iconContent = '<i class="fa fa-check-circle-o fa-lg"></i>';
								//refrescam el datatable
								refrescaPaginacio();
							}

							if ($('#spi-' + val).length > 0 && !$('#spi-' + val + ' > i').hasClass(previousClass)) {
								$('#spi-' + val).html(iconContent);
								$('#spi-' + val + ' > i').tooltip('hide')
						          .attr('data-original-title', $('#spi-' + val).attr('title'))
						          .tooltip('fixTitle');
							}
						});
				    }
				}
			});
		}
	}
	function refrescaPaginacio() {
		$('.dataTables_paginate li', $('#taulaDades').parent()).each(function() {
			if ($(this).hasClass('active')) {
				$('a', this).click();
				refrescat = true;
			}
		});
	}
	function botoMassiuClick(element) {
		$(element).attr(
				'href',
				$(element).attr('href') + "?massiva=${tascaConsultaCommand.consultaTramitacioMassivaTascaId != null}&inici="+$('#inici').val()+"&correu="+$('#correu').is(':checked'));
	}
	function seleccionarMassivaTodos() {
		var numColumna = $("#taulaDades").data("rdt-seleccionable-columna");
		if ($('#taulaDades').find('th:eq('+numColumna+')').find('input[type=checkbox]')[0].checked) {
			$('#taulaDades').find('th:eq('+numColumna+')').find('input[type=checkbox]')[0].checked = false;
		}
		$('#taulaDades').find('th:eq('+numColumna+')').find('input[type=checkbox]')[0].click();
	}
	function deseleccionarMassivaTodos() {
		var numColumna = $("#taulaDades").data("rdt-seleccionable-columna");
		if (!$('#taulaDades').find('th:eq('+numColumna+')').find('input[type=checkbox]')[0].checked) {
			$('#taulaDades').find('th:eq('+numColumna+')').find('input[type=checkbox]')[0].checked = true;
		}
		$('#taulaDades').find('th:eq('+numColumna+')').find('input[type=checkbox]')[0].click();
	}
	function filtreActiu() {
		var filtre = false;
		// Comprovam els inputs del formulari de filtre
		if ($("#nomesTasquesPersonalsCheck").hasClass("active"))
			filtre = true;
		if ($("#nomesTasquesGrupCheck").hasClass("active"))
			filtre = true;
		if ($("#titol").val() != "")
			filtre = true;
		if ($("#expedient").val() != "")
			filtre = true;
		if ($("#expedientTipusId").val() != "")
			filtre = true;
		if ($("#tasca").val() != "")
			filtre = true;
		if ($("#dataCreacioInicial").val() != "")
			filtre = true;
		if ($("#dataCreacioFinal").val() != "")
			filtre = true;
		if ($("#dataLimitInicial").val() != "")
			filtre = true;
		if ($("#dataLimitFinal").val() != "")
			filtre = true;
			
		if (filtre) {
			$('#tascaConsultaCommand').addClass("filtrat");
		} else {
			$('#tascaConsultaCommand').removeClass("filtrat");
		}
	}
</script>
<style type="text/css">
	#opciones .label-titol {padding-bottom: 0px;}
	.control-group {width: 100%;display: inline-block;}
	.control-group-mid {width: 48%;}
	.control-group.left {float: left; margin-right:1%;}
	#div_timer label {float:left;}
	.navbar-right {
		margin-right: 0px;
	}
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
			                <div class='input-group date date_time' id='inici_timer'>
			                    <input id="inici" name="inici" class="form-control date_time" data-format="dd/MM/aaaa hh:mm" type="text">
			                    <span class="input-group-addon"><span class="fa fa-calendar"></span></span>
			                </div>
			            </div>
			            <script type="text/javascript">
		                    $(document).ready(function() {
								$("#inici").on('focus', function() {
									$('.fa-calendar').click();
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
						<div class="col-md-3">
							<hel:inputText name="titol" textKey="tasca.llistat.filtre.camp.titol" placeholderKey="tasca.llistat.filtre.camp.titol" inline="true"/>
						</div>
						<div class="col-md-3">
							<hel:inputText name="expedient" textKey="tasca.llistat.filtre.camp.expedient" placeholderKey="tasca.llistat.filtre.camp.expedient" inline="true"/>
						</div>
						<div class="col-md-3">
							<hel:inputSelect emptyOption="true" name="expedientTipusId" textKey="tasca.llistat.filtre.camp.tipexp" placeholderKey="tasca.llistat.filtre.camp.tipexp" optionItems="${expedientTipusAccessibles}" optionValueAttribute="id" optionTextAttribute="nom"  disabled="${not empty expedientTipusActual}" inline="true"/>
						</div>
						<div class="col-md-3">
							<hel:inputSelect emptyOption="true" inline="true" name="tasca" textKey="tasca.llistat.filtre.camp.tasca" placeholderKey="tasca.llistat.filtre.camp.tasca"/>
						</div>
						<div class="col-md-4">
							<label><spring:message code="tasca.llistat.filtre.camp.datcre"/></label>
							<div class="row">
								<div class="col-md-6">
									<hel:inputDate name="dataCreacioInicial" textKey="tasca.llistat.filtre.camp.datcre.ini" placeholder="dd/mm/aaaa" inline="true"/>
								</div>
								<div class="col-md-6">
									<hel:inputDate name="dataCreacioFinal" textKey="tasca.llistat.filtre.camp.datcre.fin" placeholder="dd/mm/aaaa" inline="true"/>
								</div>
							</div>
						</div>
						<div class="col-md-4">
							<label><spring:message code="tasca.llistat.filtre.camp.datlim"/></label>
							<div class="row">
								<div class="col-md-6">
									<hel:inputDate name="dataLimitInicial" textKey="tasca.llistat.filtre.camp.datlim.ini" placeholder="dd/mm/aaaa" inline="true"/>
								</div>
								<div class="col-md-6">
									<hel:inputDate name="dataLimitFinal" textKey="tasca.llistat.filtre.camp.datlim.fin" placeholder="dd/mm/aaaa" inline="true"/>
								</div>
							</div>
						</div>
						<div class="col-md-4">				
							<label>&nbsp;</label>
							<div class="row">
							<c:choose>
								<c:when test="${not empty expedientTipus and expedientTipus.permisReassignment}">
									<div class="col-md-12"  id="responsableDiv">
										<hel:inputSuggest inline="true" name="responsable" urlConsultaInicial="tasca/persona/suggestInici" urlConsultaLlistat="tasca/persona/suggest" textKey="expedient.editar.responsable" placeholderKey="expedient.editar.responsable"/>
									</div>
								</c:when>
								<c:otherwise>
									<div class="col-md-12"  id="responsableDiv" style="display: none">
										<hel:inputSuggest inline="true" name="responsable" urlConsultaInicial="tasca/persona/suggestInici" urlConsultaLlistat="tasca/persona/suggest" textKey="expedient.editar.responsable" placeholderKey="expedient.editar.responsable"/>
									</div>
								</c:otherwise>
								</c:choose>
							</div>
						</div>
					</div>
				</div>
				<button style="display:none" type="submit" name="accio" value="consultar"></button>
				<div class="row">						
					<div class="col-md-12">
						<form:hidden path="nomesTasquesPersonals"/>
						<form:hidden path="nomesTasquesGrup"/>
						<form:hidden path="nomesTasquesMeves"/>
						<div class="row">
							<div class="col-md-6 btn-group">
								<button id="nomesTasquesPersonalsCheck" data-path="nomesTasquesPersonals" title="<spring:message code="tasca.llistat.filtre.camp.personals"/>" class="btn btn-default filtre-button<c:if test="${tascaConsultaCommand.nomesTasquesPersonals}"> active</c:if>" data-toggle="button"><span class="fa fa-user"></span></button>
								<button id="nomesTasquesGrupCheck" data-path="nomesTasquesGrup" title="<spring:message code="tasca.llistat.filtre.camp.grup"/>" class="btn btn-default filtre-button<c:if test="${tascaConsultaCommand.nomesTasquesGrup}"> active</c:if>" data-toggle="button"><span class="fa fa-users"></span></button>
								<button id="nomesTasquesMevesCheck" data-path="nomesTasquesMeves" title="<spring:message code="expedient.llistat.filtre.camp.meves"/>" class="btn btn-default filtre-button<c:if test="${expedientConsultaCommand.nomesTasquesMeves}"> active</c:if>" data-toggle="button"><span class="fa fa-map-marker"></span></button>
							</div>
							<div class="col-md-6">
								<div class="pull-right">
									<input type="hidden" name="consultaRealitzada" value="true"/>
									<button id="netejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
									<button id="consultar" type="submit" name="accio" value="consultar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</form:form>
	<table 
		id="taulaDades" 
		class="table table-striped table-bordered table-hover" 
		data-rdt-button-template="tableButtonsTemplate" 
		<c:if test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId != null}"> data-rdt-paginable="false"</c:if> 
		data-rdt-seleccionable-columna="0" 
		data-rdt-filtre-form-id="tascaConsultaCommand" 
		data-rdt-seleccionable="true" 
		<c:if test="${not empty preferenciesUsuari.numElementosPagina}">data-rdt-display-length-default="${preferenciesUsuari.numElementosPagina}"</c:if>>
		<thead>
			<tr data-toggle="context" data-target="#context-menu">
				<th data-rdt-property="id" width="4%" data-rdt-sortable="false" data-rdt-visible="true"></th>
				<th data-rdt-property="titol" data-rdt-template="cellPersonalGroupTemplate" data-rdt-visible="true" >
					<spring:message code="tasca.llistat.columna.titol"/>
					<script id="cellPersonalGroupTemplate" type="text/x-jsrender">
						{{:titol}}
						{{if !agafada && responsables != null}}
							<span class="fa fa-users" title="<spring:message code="enum.tasca.etiqueta.grup"/>"></span>
						{{/if}}
						<div class="pull-right">
							{{if cancelled}}
								<span class="label label-danger" title="<spring:message code="enum.tasca.etiqueta.CA"/>">CA</span>
							{{/if}}
							{{if suspended}}
								<span class="label label-info" title="<spring:message code="enum.tasca.etiqueta.SU"/>">SU</span>
							{{/if}}
							{{if open}}
								<span class="label label-warning" title="<spring:message code="enum.tasca.etiqueta.PD"/>"></span>
							{{/if}}
							{{if completed}}
								<span class="label label-success" title="<spring:message code="enum.tasca.etiqueta.FI"/>">FI</span>
							{{/if}}
							{{if agafada}}
								<span class="label label-default" title="<spring:message code="enum.tasca.etiqueta.AG"/>">AG</span>
							{{/if}}
 							{{if !completed && tascaTramitacioMassiva && assignadaUsuariActual}}													
								<span <c:if test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId == null}">onclick="javascript: $('td').unbind('click');window.location='../v3/tasca/{{:id}}/massiva';"</c:if>><span class="label label-default" title="<spring:message code="tasca.llistat.accio.tramitar_massivament"/>"><i class="fa fa-files-o"></i></span></span>
							{{/if}}	

							{{if errorFinalitzacio != null || marcadaFinalitzar != null || iniciFinalitzacio != null}}
								<a data-rdt-link-modal="true" href="<c:url value="/modal/v3/expedient/{{:expedientId}}/execucioInfo/{{:id}}"/>">
								<span class="segon-pla-icona" id="spi-{{:id}}">
									{{if errorFinalitzacio != null}}
										<i class="fa fa-exclamation-circle fa-lg error" title="<spring:message code="error.finalitzar.tasca"/>: {{:errorFinalitzacio}}"></i>
									{{/if}}
									{{if errorFinalitzacio == null && marcadaFinalitzar != null && iniciFinalitzacio == null}}
										<i class="fa fa-clock-o fa-lg programada" title="<spring:message code="enum.tasca.etiqueta.marcada.finalitzar"/> {{:marcadaFinalitzarFormat}}"></i>
									{{/if}}
									{{if errorFinalitzacio == null && marcadaFinalitzar != null && iniciFinalitzacio != null}}
										<i class="fa fa-circle-o-notch fa-spin fa-lg executant" title="<spring:message code="enum.tasca.etiqueta.execucio"/> {{:iniciFinalitzacioFormat}}"></i>
									{{/if}}
								</span>
								</a>
							{{/if}}
						</div>
					</script>
				</th>
				<th data-rdt-property="expedientIdentificador" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.expedient"/></th>
				<th data-rdt-property="responsableString" data-rdt-visible="true"><spring:message code="expedient.tasca.columna.asignada_a"/></th>
				<th data-rdt-property="expedientTipusNom" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.tipexp"/></th>
				<th data-rdt-property="createTime" data-rdt-type="datetime" data-rdt-sorting="desc" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.creada"/></th>
				<th data-rdt-property="dueDate" data-rdt-type="date" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.limit"/></th>
				<th data-rdt-property="prioritat" data-rdt-visible="false"><spring:message code="tasca.llistat.columna.prioritat"/></th>
				<th data-rdt-property="id" data-rdt-template="cellAccionsTemplate" data-rdt-context="true" data-rdt-visible="<c:out value="${tascaConsultaCommand.consultaTramitacioMassivaTascaId == null}"/>" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
 						<div id="dropdown-menu-{{:id}}" class="dropdown navbar-right">
 							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								{{if open && !suspended && assignee == "${dadesPersona.codi}" && assignadaUsuariActual}}
									<li><a id="tramitar-tasca-{{:id}}" class="consultar-tasca" href="<c:url value="../v3/tasca/{{:id}}"/>" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true"><span class="fa fa-external-link"></span> <spring:message code="tasca.llistat.accio.tramitar"/></a></li>
									{{if tascaTramitacioMassiva}}
										<li><a href="../v3/tasca/{{:id}}/massiva"><span class="fa fa-files-o"></span> <spring:message code="tasca.llistat.accio.tramitar_massivament"/></a></li>
									{{/if}}
								{{/if}}
								{{if open && !suspended && !agafada && responsables != null && assignadaUsuariActual}}
 										<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/agafar" class="tasca-accio-agafar" data-tasca-id="{{:id}}" data-rdt-link-ajax="true" data-rdt-link-callback="agafar({{:id}});"><span class="fa fa-chain"></span> <spring:message code="tasca.llistat.accio.agafar"/></a></li>
									{{/if}}
								{{if open && !suspended && agafada && (permisReassignment || permisWrite || permisAdministration)}}
									<li><a href="<c:url value="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/alliberar"/>" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.alliberar"/>"><span class="fa fa-chain-broken"></span> <spring:message code="tasca.llistat.accio.alliberar"/></a></li>
								{{/if}}
								{{if permisRead}}
								<li><a href="../v3/expedient/{{:expedientId}}" class="consultar-expedient"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.llistat.accio.consultar.expedient"/></a></li>
								{{/if}}
								{{if open && permisReassignment}}
									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/reassignar" data-rdt-link-modal="true"><span class="fa fa-share-square-o"></span>&nbsp;<spring:message code="tasca.llistat.accio.reassignar"/></a></li>
								{{/if}}
								{{if open && !suspended && permisSupervision}}
									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/suspendre" data-rdt-link-confirm="<spring:message code="tasca.llistat.confirmacio.suspendre"/>"><span class="fa fa-pause"></span> <spring:message code="tasca.llistat.accio.suspendre"/></a></li>
								{{/if}}
								{{if suspended && permisSupervision}}
									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/reprendre" data-rdt-link-confirm="<spring:message code="tasca.llistat.confirmacio.reprendre"/>"><span class="fa fa-play"></span> <spring:message code="tasca.llistat.accio.reprendre"/></a></li>
								{{/if}}
								{{if !cancelled && permisSupervision}}
									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/cancelar" data-rdt-link-confirm="<spring:message code="tasca.llistat.confirmacio.cancelar"/>"><span class="fa fa-times"></span> <spring:message code="tasca.llistat.accio.cancelar"/></a></li>
								{{/if}}
 							</ul>
 						</div>
					</script>
				</th>
				
				<th data-rdt-property="agafada" data-rdt-visible="false"></th>
				<th data-rdt-property="cancelled" data-rdt-visible="false"></th>
				<th data-rdt-property="assignee" data-rdt-visible="false"></th>
				<th data-rdt-property="assignadaUsuariActual" data-rdt-visible="false"></th>
				<th data-rdt-property="suspended" data-rdt-visible="false"></th>
				<th data-rdt-property="tascaTramitacioMassiva" data-rdt-visible="false"></th>
				<th data-rdt-property="open" data-rdt-visible="false"></th>
				<th data-rdt-property="completed" data-rdt-visible="false"></th>				
				<th data-rdt-property="expedientId" data-rdt-visible="false"></th>
				<th data-rdt-property="responsables" data-rdt-visible="false"></th>
				<th data-rdt-property="permisRead" data-rdt-visible="false"></th>
				<th data-rdt-property="permisSupervision" data-rdt-visible="false"></th>
				<th data-rdt-property="permisReassignment" data-rdt-visible="false"></th>
				<th data-rdt-property="marcadaFinalitzar" data-rdt-visible="false"></th>
				<th data-rdt-property="iniciFinalitzacio" data-rdt-visible="false"></th>
				<th data-rdt-property="errorFinalitzacio" data-rdt-visible="false"></th>
				<th data-rdt-property="marcadaFinalitzarFormat" data-rdt-visible="false"></th>
				<th data-rdt-property="iniciFinalitzacioFormat" data-rdt-visible="false"></th>
				<th data-rdt-property="permisWrite" data-rdt-visible="false"></th>
				<th data-rdt-property="permisAdministration" data-rdt-visible="false"></th>
			</tr>
		</thead>
	</table>
	<script id="tableButtonsTemplate" type="text/x-jsrender">
		<div style="text-align:right">
			<div id="btnTramitacio" class="btn-group">
				<c:choose>
					<c:when test="${tascaConsultaCommand.consultaTramitacioMassivaTascaId == null}">
						<a class="btn btn-default" href="../v3/tasca/seleccioTots" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.tots"/>"><span class="fa fa-check-square-o"></span></a>
						<a id="botoNetejarSeleccio" class="btn btn-default" href="../v3/tasca/seleccioNetejar" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.netejar"/>"><span class="fa fa-square-o"></span></a>
						<button class="btn btn-default" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span id="reasignacioMassivaCount" class="badge">&nbsp;&nbsp;</span>&nbsp;<span class="caret"></span></button>
  						<ul class="dropdown-menu">
  							<li id="liReassignment"><a id="botoReassignment" class="btn" href="../v3/tasca/massivaReassignacioTasca" onclick="botoMassiuClick(this)" data-rdt-link-modal="true"><spring:message code="tasca.llistat.reassignacions.massiva"/></a></li>
  							<li><a id="botoAgafar" href="<c:url value="../v3/tasca/seleccioAgafar"/>" data-rdt-link-ajax="true"><spring:message code="tasca.llistat.agafar.seleccionats"/></a></li>
  						</ul>
					</c:when>
					<c:otherwise>
						<a class="btn btn-default" href="#" onclick="seleccionarMassivaTodos()" title="<spring:message code="expedient.llistat.accio.seleccio.tots"/>"><span class="fa fa-check-square-o"></span></a>
						<a id="botoNetejarSeleccio" class="btn btn-default" href="#" onclick="deseleccionarMassivaTodos()" title="<spring:message code="expedient.llistat.accio.seleccio.netejar"/>"><span class="fa fa-square-o"></span></a>
						<button class="btn btn-default" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span id="reasignacioMassivaCount" class="badge">&nbsp;&nbsp;</span>&nbsp;<span class="caret"></span></button>
 						<ul class="dropdown-menu">
							<li id="liReassignment"><a id="botoReassignment" class="btn" href="<c:url value="../../../v3/tasca/massivaReassignacioTasca"/>" onclick="botoMassiuClick(this)" data-rdt-link-modal="true"><spring:message code="tasca.llistat.reassignacions.massiva"/></a></li>
 							<li><a href="<c:url value="../../../v3/tasca/massivaTramitacioTasca"/>" onclick="botoMassiuClick(this)" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true"><spring:message code="expedient.llistat.tramitacio.massiva"/>&nbsp;<span id="tramitacioMassivaCount" class="badge">&nbsp;</span></a></li>
 							<li><a id="botoAgafar" href="<c:url value="../../../v3/tasca/seleccioAgafar"/>" data-rdt-link-ajax="true"><spring:message code="tasca.llistat.agafar.seleccionats"/></a></li>
 						</ul>
					</c:otherwise>
				</c:choose>	
			</div>
		</div>
	</script>
</body>
</html>
