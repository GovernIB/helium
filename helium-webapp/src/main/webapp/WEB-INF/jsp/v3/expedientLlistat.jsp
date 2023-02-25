<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="expedient.llistat.titol"/></title>
	<meta name="capsaleraTipus" content="llistat"/>
	<meta name="title" content="<spring:message code='expedient.llistat.titol'/>"/>
	<meta name="screen" content="expedients">
	<meta name="title-icon-class" content="fa fa-folder"/>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/helium.datatable.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	
	<style type="text/css">
		.col-md-1.btn-group {width: 4.333%;}
		.col-md-6.btn-group {width: 54%;}
		.alert-envelope {
			font-size: 21px;
			position: relative;
			top: 3px;
			margin-left: 3px;
		}
		.sup-count {
			position: relative;
			padding: 2px 5px;
			background-color: red;
			font-size: 11px;
			top: -9px;
			left: -10px;
		}
		.error-triangle {
			color: red;
		    font-size: 18px;
		    top: 4px;
		    position: relative;	
		}
		a.no-deco-link {
			text-decoration: none;
			color: inherit;
		}
		a.no-deco-link:hover {
			text-decoration: none;
			color: inherit;
		}
		.navbar-right {
			margin-right: 0px;
		}
	</style>
<script>
$(document).ready(function() {
	var taulaDadesExpedient = $("#taulaDades").heliumDataTable({
		ajaxSourceUrl: "<c:url value="/v3/expedient/datatable"/>",
		localeUrl: "<c:url value="/js/dataTables-locales/dataTables_locale_ca.txt"/>",
		drawCallback: function() {
			$('.show-modal-error').click(function(e) {
				$('#modal-error .modal-title').html($(this).data('error-titol'));
				$('#modal-error .modal-body').html(
						'<p>' + $(this).data('error-missatge') + '</p>');
				if ($(this).data('error-detall')) {
					$('#modal-error .modal-body').append(
							'<p style="word-wrap: break-word;">' + $(this).data('error-detall') + '</p>');
				}
				if ($(this).data('error-pid')) {
					$('#modal-error .modal-body').append(
							'<p>processInstanceId: ' + $(this).data('error-pid') + '</p>');
				}
				$('#modal-error').modal('show');
				if (e.stopPropagation) e.stopPropagation();
			});
			filtreActiu();
		},
		rowClickCallback: function(row, event) {

			if ($('.expedient_tipus',row).data('tipus') == 'ESTAT') {
				event.preventDefault();
				window.location = '<c:url value="/v3/expedient/"/>' + $(row).find(".rdt-seleccio").val();
				return false;
			}
			
			var clickNomesDesplegar = true;
			var numTds = $('td', $(event.target).closest('tr')).length;
			var tdDesplegarIndex = numTds - 6;
			var isTdDesplegar = $(event.target).closest('td').is(':nth-child(' + tdDesplegarIndex + ')');
			if (!isTdDesplegar && !clickNomesDesplegar) {
				$('a.consultar-expedient', $(row))[0].click();
			} else {
				if ($(row).next().hasClass('table-pendents') || $(row).next().hasClass('tasques-pendents')) {
					while ($(row).next().hasClass('table-pendents') || $(row).next().hasClass('tasques-pendents')) {
						$(row).next().remove();
					}
					$('.icona-tasques-pendents', row).removeClass('fa-chevron-up').addClass('fa-chevron-down');
					$('.icona-tasques-pendents', row).attr('title', '<spring:message code="expedient.llistat.tasques.pendents.mostrar"/>');
				} else {
					carregaTasques(row,numTds);
				}
			}
		},
		seleccioCallback: function(seleccio) {			
			$('#tramitacioMassivaCount').html(seleccio.length);
		}
	});
	$("form#expedientConsultaCommand button[data-toggle=button]").click(function() {
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
	$('#expedientTipusId').on('change', function() {
		var tipus = $(this).val();
		$('#estatText').select2('val', '', true);
		$('#estatText option[value!=""]').remove();
		$('#consultaTipo').hide();
		if ($(this).val()) {
			$.get('<c:url value="/v3/expedient/estatsPerTipus/"/>' + $(this).val())
			.done(function(data) {
				$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.INICIAT%>"><spring:message code="comu.estat.iniciat"/></option>');
				for (var i = 0; i < data.length; i++) {
					$('#estatText').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
				}
				$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.FINALITZAT%>"><spring:message code="comu.estat.finalitzat"/></option>');
				// Es fa el submit del formulari per cercar automàticament per tipus de d'expedient
				$('#consultar').trigger('click');
			})
			.fail(function() {
				alert("<spring:message code="expedient.llistat.estats.ajax.error"/>");
			});
		} else {
			$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.INICIAT%>"><spring:message code="comu.estat.iniciat"/></option>');
			$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.FINALITZAT%>"><spring:message code="comu.estat.finalitzat"/></option>');
		}
	});

	$('#expedientTipusId').select2().on("select2-removed", function(e) {
		$('#consultar').trigger('click');
    })
	
	$('#expedientTipusId').trigger('change');

	//Per defecte, si no s'especifica al fitxer de properties
	//tendrem un interval que executa una funció cada 10 segons per a refrescar les
	//ícones d'estat de les tasques en segon pla
	<c:set var="refrescaSegonPla" value="${globalProperties['app.segonpla.refrescar.auto'] == 'false' ? false : true}"/>
	<c:set var="refrescaSegonPlaPeriode" value="${globalProperties['app.segonpla.refrescar.auto.periode'] != null ? globalProperties['app.segonpla.refrescar.auto.periode'] : 10}"/>
	<c:if test="${refrescaSegonPla}">
		setInterval(refrescaEstatSegonPla, (${refrescaSegonPlaPeriode} * 1000));
	</c:if>
	
	// Si es passa el paràmetre accio=iniciar provinent de l'iniciar antic llavors es prem el botó d'iniciar
	// És un cas especial de redirecció des de l'iniciar de la v2.6 amb l'acció d'iniciar expedient
	if (window.location.href.includes("accio=iniciar"))
		$('#iniciar-expediente').find('a').click();
	
	// A vegades es creen dos inputs type hidden i s'envia accio=netejar,consultar #1258
	$('#netejar').click(function() {
		if ($(':input[name=accio][type=hidden]').length > 1 ) {
				var i = 0;
				$(':input[name=accio][type=hidden]').each(function(){
					if (i == 0) {
						$(this).val('netejar');
					} else {
						$(this).remove();
					}
				});
		}
	});

	$("body").on("click", '#btnBdades', function(event) {
		webutilBase64DownloadAndRefresh($(this).attr('href'), event);
	});
});


function refrescarPanell(expedientId, tascaId, tramitar, correcte) {
	if (correcte) {
		var row = $('#tasques-pendents-' + expedientId).prev();
		var numTds = $('td', $(row)).length;
		var jqxhr = $.ajax({
			url: "<c:url value="/nodeco/v3/expedient/"/>" + expedientId + "/tasquesPendents/"+$('#nomesTasquesPersonals').val()+"/"+$('#nomesTasquesGrup').val(),
			beforeSend: function(xhr) {
				$(row).next(".tasques-pendents").remove();
				$(row).after('<tr class="tasques-pendents" id="tasques-pendents-' + expedientId + '"><td colspan="' + (numTds - 1) + '" style="text-align:center"><span class="fa fa-circle-o-notch fa-spin"></span></td></tr>');
			}
		}).done(function(data) {
			$(row).next(".tasques-pendents").remove();
			$(row).after(data);
			$('td:first', $(row).next(".tasques-pendents")).attr('colspan', numTds);
			$(row).next(".tasques-pendents").slideDown(1000);
			$('.icona-tasques-pendents', row).removeClass('fa-chevron-down').addClass('fa-chevron-up');
			$('.icona-tasques-pendents', row).attr('title', '<spring:message code="expedient.llistat.tasques.pendents.ocultar"/>');

			if (tramitar == 'true')
				$('#dropdown-menu-tasca-'+tascaId+' #tramitar-tasca-'+tascaId).click();
		}).fail(function(jqXHR, exception) {
			modalAjaxErrorFunction(jqXHR, exception);
		});
	}
}

function carregaTasques(row, numTds) {
	var jqxhr = $.ajax({
		url: "<c:url value="/nodeco/v3/expedient/"/>" + $(row).find(".rdt-seleccio").val() + "/tasquesPendents/"+$('#nomesTasquesPersonals').val()+"/"+$('#nomesTasquesGrup').val(),
		beforeSend: function(xhr) {
			$(row).after('<tr class="tasques-pendents"><td colspan="' + (numTds - 1) + '" style="text-align:center"><span class="fa fa-circle-o-notch fa-spin"></span></td></tr>');
		}
	}).done(function(data) {
		$(row).next(".tasques-pendents").remove();
		$(row).after(data);
		$('td:first', $(row).next(".tasques-pendents")).attr('colspan', numTds);
		$(row).next(".tasques-pendents").slideDown(1000);
		$('.icona-tasques-pendents', row).removeClass('fa-chevron-down').addClass('fa-chevron-up');
		$('.icona-tasques-pendents', row).attr('title', '<spring:message code="expedient.llistat.tasques.pendents.ocultar"/>');
		$('span.segon-pla-icona > i').tooltip({container: 'body'});
	}).fail(function(jqXHR, exception) {
		modalAjaxErrorFunction(jqXHR, exception);
	});
}

function recarregarTaula(tableId, correcte) {
	if (correcte) {
		refrescarAlertes($("#"+tableId));
		$("#"+tableId).dataTable().fnDraw();
	}
}
function refrescarAlertes(e) {
	$.ajax({
		url: "<c:url value="/nodeco/v3/missatges"/>",
		async: false,
		timeout: 20000,
		success: function (data) {
			$('#contingut-alertes').html(data);
		}
	});
}
function filtreActiu() {
	var filtre = false;
	// Comprovam els inputs del formulari de filtre
	if ($("#nomesTasquesPersonalsCheck").hasClass("active"))
		filtre = true;
	if ($("#nomesTasquesGrupCheck").hasClass("active"))
		filtre = true;
	if ($("#nomesAlertesCheck").hasClass("active"))
		filtre = true;
	if ($("#nomesErrorsCheck").hasClass("active"))
		filtre = true;
	if ($("#numero").val() != "")
		filtre = true;
	if ($("#titol").val() != "")
		filtre = true;
	if ($("#expedientTipusId").val() != "")
		filtre = true;
	if ($("#estatText").val() != "")
		filtre = true;
	if ($("#dataIniciInicial").val() != "")
		filtre = true;
	if ($("#dataIniciFinal").val() != "")
		filtre = true;
	if ($("#dataFiInicial").val() != "")
		filtre = true;
	if ($("#dataFiFinal").val() != "")
		filtre = true;
	if ($("#geoReferencia").val() != "")
		filtre = true;
	if ($("#mostrarAnulats").val() != "NO") {
		if ($("#mostrarAnulats").val() == null)
			$("#mostrarAnulats").select2().val("NO").trigger("change")
		else
			filtre = true;
	}
		
	if (filtre) {
		$('#expedientConsultaCommand').addClass("filtrat");
	} else {
		$('#expedientConsultaCommand').removeClass("filtrat");
	}
}

function refrescaEstatSegonPla() {
	var tasquesSegonPlaIds = [];
	$('span.segon-pla-icona').each(function (index, value) {
		var id = $(value).attr('id').split('spi-')[1]; 
	 	tasquesSegonPlaIds.push(id);	
	});
	if (tasquesSegonPlaIds.length > 0) {
		$.ajax({
		    url: "tasca/actualitzaEstatsSegonPla",
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
					    		var row = $('#spi-' + val).parents('.tasques-pendents').prev()[0];
					    		var numTds = $('td', $('#spi-' + val).parents('.tasques-pendents').prev()).length;
					    		if ($(row).next().hasClass('table-pendents') || $(row).next().hasClass('tasques-pendents')) {
									while ($(row).next().hasClass('table-pendents') || $(row).next().hasClass('tasques-pendents')) {
										$(row).next().remove();
									}
									$('.icona-tasques-pendents', row).removeClass('fa-chevron-up').addClass('fa-chevron-down');
									$('.icona-tasques-pendents', row).attr('title', 'Mostrar tasques pendents');
									
									carregaTasques(row,numTds);
								}
						    }				    	
						} else {
							iconContent = '<i class="fa fa-check-circle-o fa-lg"></i>';
							//refrescam el datatable
							var row = $('#spi-' + val).parents('.tasques-pendents').prev()[0];
				    		var numTds = $('td', $('#spi-' + val).parents('.tasques-pendents').prev()).length;
				    		if ($(row).next().hasClass('table-pendents') || $(row).next().hasClass('tasques-pendents')) {
								while ($(row).next().hasClass('table-pendents') || $(row).next().hasClass('tasques-pendents')) {
									$(row).next().remove();
								}
								$('.icona-tasques-pendents', row).removeClass('fa-chevron-up').addClass('fa-chevron-down');
								$('.icona-tasques-pendents', row).attr('title', 'Mostrar tasques pendents');

								carregaTasques(row,numTds);
							}
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
</script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" commandName="expedientConsultaCommand">
		<div class="row">
			<div class="col-md-2">
				<hel:inputText name="numero" textKey="expedient.llistat.filtre.camp.numero" placeholderKey="expedient.llistat.filtre.camp.numero" inline="true"/>
			</div>
			<div class="col-md-4">
				<hel:inputText name="titol" textKey="expedient.llistat.filtre.camp.titol" placeholderKey="expedient.llistat.filtre.camp.titol" inline="true"/>
			</div>
			<div class="col-md-3">
				<hel:inputSelect emptyOption="true" name="expedientTipusId" textKey="expedient.llistat.filtre.camp.expedient.tipus" placeholderKey="expedient.llistat.filtre.camp.expedient.tipus" optionItems="${expedientTipusAccessibles}" optionValueAttribute="id" optionTextAttribute="nom" disabled="${not empty expedientTipusActual}" inline="true"/>
			</div>
			<div class="col-md-3">
				<hel:inputSelect emptyOption="true" name="estatText" textKey="expedient.llistat.filtre.camp.estat" placeholderKey="expedient.llistat.filtre.camp.estat" optionItems="${estats}" optionValueAttribute="id" optionTextAttribute="nom" inline="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-4">
				<label><spring:message code="expedient.llistat.filtre.camp.data.inici"/></label>
				<div class="row">
					<div class="col-md-6">
						<hel:inputDate name="dataIniciInicial" textKey="expedient.llistat.filtre.camp.data.inici.1" placeholder="dd/mm/aaaa" inline="true"/>
					</div>
					<div class="col-md-6">
						<hel:inputDate name="dataIniciFinal" textKey="expedient.llistat.filtre.camp.data.inici.2" placeholder="dd/mm/aaaa" inline="true"/>
					</div>
				</div>
			</div>
			<div class="col-md-4">
				<label><spring:message code="expedient.llistat.filtre.camp.data.fi"/></label>
				<div class="row">
					<div class="col-md-6">
						<hel:inputDate name="dataFiInicial" textKey="expedient.llistat.filtre.camp.data.fi.1" placeholder="dd/mm/aaaa" inline="true"/>
					</div>
					<div class="col-md-6">
						<hel:inputDate name="dataFiFinal" textKey="expedient.llistat.filtre.camp.data.fi.2" placeholder="dd/mm/aaaa" inline="true"/>
					</div>
				</div>
			</div>
			<div class="col-md-2">
				<label><spring:message code="expedient.llistat.filtre.camp.registreNumero"/></label>
				<div class="row">
					<div class="col-md-12">
						<hel:inputText name="registreNumero"
							textKey="expedient.llistat.filtre.camp.registreNumero"
							placeholderKey="expedient.llistat.filtre.camp.registreNumero" inline = "true"/>
					</div>
				</div>
			</div>
			<!-- 
				<c:if test="${globalProperties['app.georef.actiu']}">
					<label><spring:message code="expedient.llistat.filtre.camp.geopos"/></label>
					<c:choose>
						<c:when test="${globalProperties['app.georef.tipus']=='ref'}">
							<hel:inputText name="geoReferencia" textKey="expedient.llistat.filtre.camp.georef" placeholderKey="expedient.llistat.filtre.camp.georef" inline="true"/>
						</c:when>
						<c:otherwise>
							<div class="row">
								<div class="col-md-6">
									<hel:inputText name="geoPosX" textKey="expedient.llistat.filtre.camp.coordx" placeholderKey="expedient.llistat.filtre.camp.coordx" inline="true"/>
								</div>
								<div class="col-md-6">
									<hel:inputText name="geoPosY" textKey="expedient.llistat.filtre.camp.coordy" placeholderKey="expedient.llistat.filtre.camp.coordy" inline="true"/>
								</div>
							</div>
						</c:otherwise>
					</c:choose>
				</c:if> -->
			<div class="col-md-2">
				<label><spring:message code="expedient.llistat.filtre.camp.anulats"/></label>
				<div class="row">
					<div class="col-md-12">
						<hel:inputSelect inline="true" name="mostrarAnulats" optionItems="${interessatTipus}" optionValueAttribute="valor" optionTextAttribute="codi"/>
					</div>
				</div>
			</div>		
			<div class="col-md-12">
				<form:hidden path="nomesAlertes"/>
				<form:hidden path="nomesErrors"/>
				<form:hidden path="nomesTasquesPersonals"/>
				<form:hidden path="nomesTasquesGrup"/>
				<button id="consultarOcult" style="display:none" type="submit" name="accio" value="consultar"></button>
				<div class="row">
					<div class="col-md-6">
						<div class="btn-group">
							<button id="nomesTasquesPersonalsCheck" data-path="nomesTasquesPersonals" title="<spring:message code="expedient.llistat.filtre.camp.personals"/>" class="btn btn-default filtre-button<c:if test="${expedientConsultaCommand.nomesTasquesPersonals}"> active</c:if>" data-toggle="button"><span class="fa fa-user"></span></button>
							<button id="nomesTasquesGrupCheck" data-path="nomesTasquesGrup" title="<spring:message code="expedient.llistat.filtre.camp.grup"/>" class="btn btn-default filtre-button<c:if test="${expedientConsultaCommand.nomesTasquesGrup}"> active</c:if>" data-toggle="button"><span class="fa fa-users"></span></button>
						</div>
						<button id="nomesErrorsCheck" data-path="nomesErrors" title="<spring:message code="expedient.llistat.filtre.camp.errors"/>" class="btn btn-default filtre-button<c:if test="${expedientConsultaCommand.nomesErrors}"> active</c:if>" data-toggle="button"><span class="fa fa-exclamation-triangle"></span></button>
						<button id="nomesAlertesCheck" data-path="nomesAlertes" title="<spring:message code="expedient.llistat.filtre.camp.alertes"/>" class="btn btn-default filtre-button<c:if test="${expedientConsultaCommand.nomesAlertes}"> active</c:if>" data-toggle="button"><span class="fa  fa-envelope"></span></button>
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
	</form:form>
	<table id="taulaDades" class="table table-striped table-bordered table-hover" data-rdt-button-template="tableButtonsTemplate" data-rdt-filtre-form-id="expedientConsultaCommand" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0" <c:if test="${not empty preferenciesUsuari.numElementosPagina}">data-rdt-display-length-default="${preferenciesUsuari.numElementosPagina}"</c:if>>
		<thead>
			<tr>
				<th data-rdt-property="id" width="4%" data-rdt-sortable="false"></th>
				<th data-rdt-property="id" data-rdt-template="cellPendentsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="2%">
					<script id="cellPendentsTemplate" type="text/x-jsrender">
						{{if tipus.tipus == 'FLOW' }}
							<span class="icona-tasques-pendents fa fa-chevron-down" title="<spring:message code="expedient.llistat.tasques.pendents.mostrar"/>"></span>
						{{/if}}
					</script>
				</th>
				<th data-rdt-property="identificador" data-rdt-template="cellReindexacioTemplate" data-rdt-visible="true">
					<spring:message code="expedient.llistat.columna.expedient"/>
					<script id="cellReindexacioTemplate" type="text/x-jsrender">
					{{:identificador}}
					{{if reindexarData || reindexarError}}
						<div class="pull-right">
							<span class="fa fa-refresh {{if reindexarError}}text-danger {{/if}}" 
							title="{{if reindexarData}}<spring:message code="expedient.consulta.reindexacio.asincrona"/>{{/if}}
								   {{if reindexarError}}<spring:message code="expedient.consulta.reindexacio.error.full"/>{{/if}}"></span>
						</div>
					{{/if}}
					</script>
				</th>
				<th data-rdt-property="tipus.nom" data-rdt-template="cellTipusTemplate" data-rdt-visible="true"><spring:message code="expedient.llistat.columna.tipus"/>
					<script id="cellTipusTemplate" type="text/x-jsrender">
					<span class="expedient_tipus" data-tipus="{{:tipus.tipus}}">{{:tipus.tipus}}
						{{:tipus.nom}}
					</span>
					</script>				
				</th>
				<th data-rdt-property="dataInici" data-rdt-type="datetime" data-rdt-sorting="desc" data-rdt-visible="true"><spring:message code="expedient.llistat.columna.iniciat"/></th>
				<th data-rdt-property="dataFi" data-rdt-type="datetime" data-rdt-visible="true"><spring:message code="expedient.llistat.columna.finalitzat"/></th>
				<th data-rdt-property="estat.nom" data-rdt-template="cellEstatTemplate" data-rdt-visible="true">
					<spring:message code="expedient.llistat.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
					{{if dataFi}}
						<spring:message code="comu.estat.finalitzat"/>
					{{else}}
						{{if estat_nom}}{{:estat_nom}}{{else}}<spring:message code="comu.estat.iniciat"/>{{/if}}
					{{/if}}
					<div class="pull-right">
						{{if ambErrors}}
							<a class="no-deco-link" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true" href="<c:url value="../v3/expedient/{{:id}}/errors"/>">
								<span class="fa fa-exclamation-triangle error-triangle" title="<spring:message code="expedient.consulta.errors"/>"></span>
							</a>
						{{/if}}
						{{if aturat}}
							<span class="label label-danger" title="{{:infoAturat}}">AT</span>
						{{/if}}
						{{if anulat}}
							<span class="label label-warning" title="{{:comentariAnulat}}">AN</span>
						{{/if}}
						<!-- {{if errorDesc}}
							{{if permisAdministration}}
								<span class="label label-warning show-modal-error" title="{{:errorDesc}}" data-error-titol="Informació sobre l'error" data-error-missatge="{{:errorDesc}}" data-error-detall="{{:errorFull}}" data-error-pid="{{:processInstanceId}}"><span class="fa fa-exclamation-circle"></span> </span>
							{{else}}
								<span class="label label-warning show-modal-error" title="{{:errorDesc}}" data-error-titol="Informació sobre l'error" data-error-missatge="{{:errorDesc}}"><span class="fa fa-exclamation-circle"></span> </span>
							{{/if}}						
						{{/if}} -->
						{{if alertesTotals}}
							<a class="no-deco-link" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true" href="<c:url value="../v3/expedient/{{:id}}/alertes"/>">
								<span class="fa fa-envelope alert-envelope show-modal-alertes" title="<spring:message code="expedient.consulta.alertes.totals"/>"></span>
								{{if alertesPendents}}
									<span class="badge sup-count">{{:alertesPendents}}</span>
								{{/if}}
							</a>
						{{/if}}
					</div>
					</script>
				</th>
				<th data-rdt-property="tipus" data-rdt-visible="false"></th>
				<th data-rdt-property="reindexarData" data-rdt-visible="false"></th>
				<th data-rdt-property="reindexarError" data-rdt-visible="false"></th>
				<th data-rdt-property="infoAturat" data-rdt-visible="false"></th>
				<th data-rdt-property="comentariAnulat" data-rdt-visible="false"></th>
				<th data-rdt-property="aturat" data-rdt-visible="false"></th>
				<th data-rdt-property="anulat" data-rdt-visible="false"></th>
				<th data-rdt-property="processInstanceId" data-rdt-visible="false"></th>
				<th data-rdt-property="permisCreate" data-rdt-visible="false"></th>
				<th data-rdt-property="permisAdministration" data-rdt-visible="false"></th>		
				<th data-rdt-property="permisRead" data-rdt-visible="false"></th>
				<th data-rdt-property="permisWrite" data-rdt-visible="false"></th>
				<th data-rdt-property="permisDelete" data-rdt-visible="false"></th>
				<th data-rdt-property="permisStop" data-rdt-visible="false"></th>
				<th data-rdt-property="permisCancel" data-rdt-visible="false"></th>
				<th data-rdt-property="errorDesc" data-rdt-visible="false"></th>	
				<th data-rdt-property="errorFull" data-rdt-visible="false"></th>
				<th data-rdt-property="errorsIntegracions" data-rdt-visible="false"></th>
				<th data-rdt-property="ambErrors" data-rdt-visible="false"></th>
				<th data-rdt-property="alertesTotals" data-rdt-visible="false"></th>
				<th data-rdt-property="alertesPendents" data-rdt-visible="false"></th>
				<th data-rdt-property="id" data-rdt-template="cellAccionsTemplate" data-rdt-context="true" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown navbar-right">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="expedient/{{:id}}" class="consultar-expedient"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.llistat.accio.consultar"/></a></li>
								{{if !aturat && (permisStop)}}<li><a href="../v3/expedient/{{:id}}/aturar" data-rdt-link-modal="true"><span class="fa fa-pause"></span>&nbsp;<spring:message code="expedient.llistat.accio.aturar"/></a></li>{{/if}}
								{{if aturat && (permisStop)}}<li><a href="../v3/expedient/{{:id}}/reprendre" data-rdt-link-callback="recarregarTaula(taulaDades);" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code="expedient.eines.confirm_reprendre_tramitacio"/>"><span class="fa fa-play"></span>&nbsp;<spring:message code="expedient.llistat.accio.reprendre"/></a></li>{{/if}}
								{{if !anulat && (permisCancel)}}<li><a href="../v3/expedient/{{:id}}/anular" data-rdt-link-modal="true"><span class="fa fa-times"></span>&nbsp;<spring:message code="expedient.llistat.accio.anular"/></a></li>{{/if}}
								{{if anulat && (permisCancel)}}<li><a href="../v3/expedient/{{:id}}/activar" data-rdt-link-callback="recarregarTaula(taulaDades);" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code="expedient.consulta.confirm.desanular"/>"><span class="fa fa-check"></span>&nbsp;<spring:message code="expedient.llistat.accio.activar"/></a></li>{{/if}}
								{{if permisDelete}}<li><a href="../v3/expedient/{{:id}}/delete" data-rdt-link-ajax="false" data-rdt-link-confirm="<spring:message code="expedient.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>{{/if}}
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="tableButtonsTemplate" type="text/x-jsrender">
		<div style="text-align:right">
			<div id="btnTramitacio" class="btn-group">
				<a class="btn btn-default" href="../v3/expedient/seleccioTots" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.tots"/>"><span class="fa fa-check-square-o"></span></a>
				<a class="btn btn-default" href="../v3/expedient/seleccioNetejar" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.netejar"/>"><span class="fa fa-square-o"></span></a>
				<a id="btnBdades" class="btn btn-default" href="../v3/expedient/descarregardades"><span class="fa fa-download"></span> <spring:message code="expedient.llistat.accio.descarregar"/></a>
				<a class="btn btn-default" href="../v3/expedient/massiva"><spring:message code="expedient.llistat.accio.massiva"/>&nbsp;<span id="tramitacioMassivaCount" class="badge">&nbsp;</span></a>
			</div>
		</div>
	</script>
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
</body>
</html>
