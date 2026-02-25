<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<html>
<head>
	<title><spring:message code="procediment.actualitzacio.titol"/></title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>

	<script>

		var intervalProgres;
		var writtenBlocs = 0;
		var title="<spring:message code="procediment.actualitzacio.titol"/>";
		var content="<spring:message code="procediment.actualitzacio.cancelarActu"/>";
		var acceptar="<spring:message code="comu.boto.acceptar"/>";
		var cancelar="<spring:message code="comu.boto.cancelar"/>";
		var lnom="<spring:message code="procediment.actualitzacio.camp.nom"/>";
		var ldesc="<spring:message code="procediment.actualitzacio.camp.descripcio"/>";
		var lcomu="<spring:message code="procediment.actualitzacio.camp.comu"/>";
		var lorgan="<spring:message code="procediment.actualitzacio.camp.unitatOrganitzativa"/>";
		var sense="<spring:message code="procediment.actualitzacio.sense.canvis"/>";

		var isUpdating = '${isUpdatingProcediments}' == 'true';
		
		$(document).ready(function(e) {
			
			$('button[name=actualitzarBtn]').click( function(e){
				$('.loading').fadeIn();
				$('.progress').fadeOut();
				$('#actualitzacioInfo').empty().fadeIn();
				$('.confirmacio').fadeOut();
				$("button[name=actualitzarBtn]", window.parent.document).attr('disabled', true).find('.fa-refresh').addClass("fa-spin");			
				if (!isUpdating) {
					writtenBlocs = 0;
					$.post($(this).attr('action'));
					isUpdating = true;
				}
				refreshProgres();
			});
			
			$('.close', parent.document).on('click', function(e) {	
				return tancarConfirmacio(e);
			});
			$('button[name=btnTancaActualitzacio]').click( function(e) {
				return tancarConfirmacio(e);
			});
						
			if (isUpdating) {
				//$('.modal-body .div-dades-carregant').show();
				$('.loading').fadeIn();
				$('#actualitzacioInfo').fadeIn();
				$('.confirmacio').fadeOut();
				$("button[name=actualitzarBtn]").attr('disabled', true).find('.fa-refresh').addClass("fa-spin");
				
				refreshProgres();
			}
		});
				
		/** Tanca comprovant si s'està actualitzant per mostrar una confirmació. */
		function tancarConfirmacio(e) {
			let tancar = false;
			if (isUpdating) {
				if (confirm(content)) {
					tancar = true;
				}
			} else {
				tancar = true;
			}
			if (tancar) {
				// Recarrega la finestra de procediments
            	window.top.location.reload();
			} else {
				e.preventDefault();
				e.stopPropagation();
				return false;
			}
		}

		function refreshProgres() {
			intervalProgres =  setInterval(function(){ getProgres(); }, 250);
		}

		function getProgres() {
			$.ajax({
				type: 'GET',
				url: "<c:url value='/v3/procediment/actualitzar/progres'/>",
				data: {
					index : writtenBlocs
				},
				success: function(data) {
					if (data) {
						writeInfo(data);
						if (data.finished == true) {
							clearInterval(intervalProgres);
							isUpdating = false;
							$('#bar').css('width', '100%');
							$('#bar').attr('aria-valuenow', 100);
							$('#bar').html('100%');
							$('.loading').hide();
			    			$("button[name=actualitzarBtn]", window.parent.document).attr('disabled', false).find('.fa-refresh').removeClass("fa-spin");
						} else {
							if (data.progres > 0) {
								$('.progress').show();
								$('#bar').css('width', data.progres + '%');
								$('#bar').attr('aria-valuenow', data.progres);
								$('#bar').html(data.progres + '%');
							} else if(data.progres == 0 && data.numProcedimentsActualitzats == 0 ){
								$('.loading').hide();
							}
						}
					}
				},
				error: function() {
					console.error("error obtenint progrés...");
					clearInterval(intervalProgres);
					$('.loading').hide();
				}
			});
		}

		/* Escriu la informació de progrés rebuda. */
		function writeInfo(data) {
			let info = data.info;
			let index;
			for (index = 0; index < info.length; index++) {
				if (info[index].index >= writtenBlocs) { // pot ser que l'interval faci peticions mab el mateix índex
					writeDetall(info[index]);
					writtenBlocs++;
				}
			}
			writtenBlocs = Math.min(writtenBlocs, data.ninfo);
			if (data.finished) {
				writeResum(data);
			}
		}
		
		/* Escriu el detall per un ítem d'informació. */
		function writeDetall(info) {
			$detall = $('<div class="panel">' +
							'  <div class="panel-heading">' +
							'    <h3 class="panel-title"></h3>' +
							'  </div>' +
							'  <div class="panel-body"></div>' +
							'</div>');
			// Nivell d'informació
			if (info.tipus == 'AVIS') {
				$detall.addClass('panel-warning');
			} else if (info.tipus == 'ERROR') {
				$detall.addClass('panel-danger');
			} else {
				if (info.linies != null && info.linies.length > 0) {
					$detall.addClass('panel-success');
				} else {
					$detall.addClass('panel-default');
				}
			}
			// Titol
			if (info.titol) {
				$('.panel-title', $detall).html(info.titol);
			} else {
				$detall.find('.panel-heading').remove();
			}			
			// Text
			$('.panel-body', $detall).html(info.text);
			// Línies d'informació
			if (info.linies != null && info.linies.length > 0) {
				$linies = $('<ul/>');
				for (i = 0; i < info.linies.length; i++) {
					$linies.append('<li>' + info.linies[i] + '</li>');
				} 
				$('.panel-body', $detall).append($linies);
			}

			// Afegeix el detall
			$('#actualitzacioInfo').append($detall);

			//scroll to the bottom of "#actualitzacioInfo"
			var infoDiv = document.getElementById("actualitzacioInfo");
			infoDiv.scrollTop = infoDiv.scrollHeight;
		}
		
		function writeResum(data) {
			
			// Llistat d'avisos
			if (data.avisos != null && data.avisos.length > 0) {
				$avisos = $('<div class="panel panel-warning">' +
						'  <div class="panel-heading">' +
						'    <h3 class="panel-title"><span class="fa fa-exclamation-triangle text-warning" /> Avisos</h3>' +
						'  </div>' +
						'  <div class="panel-body"><ul></ul></div>' +
						'</div>');
				$llista = $avisos.find('ul');
				for (i = 0; i < data.avisos.length; i++) {
					$llista.append('<li>' + data.avisos[i] + '</li>');
				} 
				$('#actualitzacioInfo').append($avisos);
			}

			// Resum
			$resum = $('<div class="panel">' +
					'  <div class="panel-heading">' +
					'    <h3 class="panel-title">Resum</h3>' +
					'  </div>' +
					'  <div class="panel-body"><ul></ul></div>' +
					'</div>');
			
			$llista = $('.panel-body', $resum).find('ul');
			$llista.append('<li>Total: ' + data.numOperacions + '</li>');
			$llista.append('<li>Nous: ' + data.nnous + '</li>');
			$llista.append('<li>Extingits: ' + data.nextingits + '</li>');
			$llista.append('<li>Canvis: ' + data.ncanvis + '</li>');
			$llista.append('<li>Avisos: ' + data.navisos + '</li>');
			$llista.append('<li>Errors: ' + data.nerrors  + '</li>');
			if (data.error == true) {
				$resum.addClass('panel-danger');
				$('.panel-body', $resum).append('<div><span class="fa fa-exclamation-triangle text-danger" /> Error: ' + data.errorMsg + '</div>');
			} else {
				$resum.addClass('panel-success');
			}
			$('#actualitzacioInfo').append($resum);
			
			
			//scroll to the bottom of "#actualitzacioInfo"
			var infoDiv = document.getElementById("actualitzacioInfo");
			infoDiv.scrollTop = infoDiv.scrollHeight;			
		}

	</script>
</head>

<body>
	<c:if test="${isUpdatingProcediments}">
		<div class="confirmacio">
			<h4><spring:message code="procediment.actualitzacio.procesActiu"/></h4>
		</div>
	</c:if>
	<c:if test="${not isUpdatingProcediments}">
		<div class="confirmacio">
			<h4><spring:message code="procediment.actualitzacio.confirmacio"/></h4>
		</div>
	</c:if>

	<c:set var="formAction"><c:url value="/v3/procediment/actualitzar"/></c:set>		
	<form:form id="formUpdateAuto" action="${formAction}" method="post" cssClass="form-horizontal">
		<div class="progress" style="display: none">
			<div id="bar" class="progress-bar" role="progressbar progress-bar-striped active" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">0%</div>
		</div>
		<div id="actualitzacioInfo" style="display: none; overflow: auto; max-height: 80vh;">
		</div>
		<div class="loading" style="display: none">
			<div style="display: flex; justify-content: center;">
				<span class="fa fa-circle-o-notch fa-2x fa-spin fa-fw"></span>
			</div>
		</div>
		<div id="modal-botons" class="well">
			<button type="button"
					name="actualitzarBtn"
					class="btn btn-success">
				<span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.actualitzar"/>
			</button>
   			<button name="btnTancaActualitzacio" 
   					type="button" class="btn btn-default" data-modal-cancel="false">
				 <spring:message code="comu.boto.tancar"/>
			</button>
			   
		</div>
	</form:form>
	
</body>
</html>
