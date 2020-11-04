<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma">ca</c:set>

<html>
<head>
	<title>
		<c:choose>
			<c:when test="${dadesPersona.admin}">
				<spring:message code='reindexacions.titol.helium' />
			</c:when>
			<c:otherwise>
				<spring:message code='reindexacions.titol.entorn' arguments="${entornActual.nom}" />
			</c:otherwise>
		</c:choose>
	</title>
	<title><spring:message code='reindexacions.titol' /></title>
	<hel:modalHead/>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<style>
		.row_selected {
			background-color: lightgray !important;			
		};
	</style>
	<script type="text/javascript">
	// <![CDATA[
	    
	    // Per saber quin es carrega
	    var tipusId = null;
	    var tipusEntorn = null;
	    
		$(document).ready( function() {	
			
			$("button[name=refrescar]").click(function() {
				carregaDades();
			});
			carregaDades();			
		});
		
		function carregaDades() {
			// Adapta el gràfic dels controls
			$('#div-error').hide();
			$('#div-error-text').empty();
			$('#td-errors-total,#td-pendents-total').html('-');
			$('#divExpedientsAmbErrorOPendents').empty();
			$('#data').html('-');
			$('#cua').html('-');
			$("button[name=refrescar]", window.parent.document).attr('disabled', true).find('.fa-refresh').addClass("fa-spin");
			// Consulta les dades
			$.ajax({
	            url : '<c:url value="/v3/reindexacions/dades"/>',
	            type : 'GET',
	            dataType : 'json',
	            success : function(data) {
	                emplenaDades(data);
	            },
	            error: function (request, status, error) {
	            	// Mostra l'error
					$('#div-error-text').html(request.responseText);
					$('#div-error').show();
	            },
	            complete: function() {
	            	// Treu els spinners
	    			$("button[name=refrescar]", window.parent.document).attr('disabled', false).find('.fa-refresh').removeClass("fa-spin");
	            }
	        });
		}
		
		function emplenaDades(data) {
			
			// Carrega les dades de la cua i la data
			$("#data").html(data.data);
			$("#cua").html(data.cua);
			
			// Carrega les dades a la taula
			
			var $tbody = $('#dades-taula').find("tbody");
			// Buida la taula
			$tbody.empty();
			// Emplena la taula
			var $tr;
			var errorsTotal = 0;
			var pendentsTotal = 0;			
			for (i = 0; i < data.dades.length; i++) {
				
			    var $tr = $("<tr>");
			    $tr.css("cursor","pointer");
			    $tr.attr("title", "<spring:message code="reindexacions.taula.fila.title"/>");
			    $tr.data("tipusEntorn", data.dades[i].tipusNom + "(" + data.dades[i].entornNom + ")");
			    $tr.data("tipusId", data.dades[i].tipusId);
		    	if (tipusId == data.dades[i].tipusId ) {
		    		$tr.addClass("row_selected");
		    	}
			    // si es prem sobre la fila carrega els expedients
			    $tr.click(function() {
		    		$('#dades-taula tr').removeClass("row_selected");
			    	if (tipusId == $(this).data("tipusId") ) {
			    		tipusId = null;
			    		tipusEntorn = null;
			    		$(this).removeClass("row_selected");
			   	 	} else {
				    	tipusId = $(this).data("tipusId");
				    	tipusEntorn = $(this).data("tipusEntorn");
				    	$(this).addClass("row_selected");
			   	 	}
			    	carregarExpedients(tipusId, tipusEntorn);
			    });

			    // EntornCodi
			    var $td = $("<td>");
			    $td.html(data.dades[i].entornCodi);
			    $tr.append($td);

			    // EntornNom
			    var $td = $("<td>");
			    $td.html(data.dades[i].entornNom);
			    $tr.append($td);

			    // TipusCodi
			    $td = $("<td>");
			    $td.html(data.dades[i].tipusCodi);
			    $tr.append($td);

			    // TipusNom
			    $td = $("<td>");
			    $td.html(data.dades[i].tipusNom);
			    $tr.append($td);

			    // Errors
			    $td = $("<td>");
			    $td.addClass("text-right");
			    $td.addClass("text-danger");
			    $td.html(data.dades[i].errors);
			    $tr.append($td);
			    errorsTotal += +data.dades[i].errors;
			    
			    // Pendents
			    $td = $("<td>");
			    $td.addClass("text-right");
			    $td.html( data.dades[i].pendents);
			    $tr.append($td);
			    pendentsTotal += +data.dades[i].pendents;
			    			    
			    $tbody.append($tr)
			}
			$('#td-errors-total').html(errorsTotal);
			$('#td-pendents-total').html(pendentsTotal);
			
			// Si es prem sobre la fila associa l'event per mostar els expedients
			
			// Aplica la funció a la taula
			$('#dades-taula').DataTable({
				retrieve: true,
				paginate : false,
				language: {
					url: webutilContextPath() + '/js/datatables/i18n/datatables.ca.json'
				},
				order: [[4, 'desc'],[5, 'desc']]
			});	
			
		    carregarExpedients(tipusId, tipusEntorn);
		}
		
		function carregarExpedients(tipusId, tipusEntorn) {			
			
			$('#divExpedientsAmbErrorOPendents').empty();
			if (tipusId && tipusEntorn) {
				$('#tipusEntorn').html(tipusEntorn);
				$('#divExpedientsAmbErrorOPendents').html($('#divCarregant').html());
				$('#divExpedientsAmbErrorOPendents').load('/helium/nodeco/v3/reindexacions/' + tipusId + '/expedients', 
					function() {
						$('#divExpedientsAmbErrorOPendents').find('#taulaExpedients').DataTable({
							'oLanguage': {
								'sUrl': webutilContextPath() + '/js/datatables/i18n/datatables.ca.json'
							},
							'aaSorting': [[1, 'asc']]
						});
				});				
			} else {
				$('#tipusEntorn').html("(<spring:message code="comu.cap"/>)");
			}


		}
		
	// ]]>
	</script>
</head>
<body>
	<div id="monitor_contens">
	
		<div id="divCarregant" style="display:none;">
			<div class="contingut-carregant text-center"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
		</div>
		
		<!-- div ocult per mostrar en cas d'error -->
		<div id="div-error" style="display:none">
			<div class="alert alert-danger">
				<span class="fa fa-warning"></span>
				<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
				<span id="div-error-text"></span>
			</div>
		</div>

	
		<h4><spring:message code="reindexacions.dataConsulta"/>: <strong><span id="data">-</span></strong></h4>

		<h4><spring:message code="reindexacions.pendentsCua"/>: <strong><span id="cua">-</span></strong></h4>		

		<table id="dades-taula"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th colspan="2"><spring:message code="reindexacions.columna.entorn"/></th>
					<th colspan="2"><spring:message code="reindexacions.columna.tipus"/></th>
					<th colspan="2"></th>
				</tr>
				<tr>
					<th><spring:message code="reindexacions.columna.codi"/></th>
					<th width="30%"><spring:message code="reindexacions.columna.nom"/></th>
					<th><spring:message code="reindexacions.columna.codi"/></th>
					<th width="30%"><spring:message code="reindexacions.columna.nom"/></th>
					<th># <spring:message code="reindexacions.columna.errors"/></th>
					<th># <spring:message code="reindexacions.columna.pendents"/></th>
				</tr>
			</thead>
			<tbody>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="4"><strong><spring:message code="reindexacions.columna.total"/></strong></td>
					<td id="td-errors-total" class="text-right text-danger" style="font-weight: bold;">-</td>
					<td id="td-pendents-total" class="text-right" style="font-weight: bold;">-</td>
				</tr>
			</tfoot>
		</table>
		
		<h4><spring:message code="reindexacions.llistatTipusSeleccionat"/>: <strong><span id="tipusEntorn">(<spring:message code="comu.cap"/>)</span></strong></h4>
		
		<div id="divExpedientsAmbErrorOPendents">

		</div>		
		
		
	</div>
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
		<button type="button" class="btn btn-primary" name="refrescar" value="refrescar"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comuns.refrescar"/></button>
	</div>
</body>
</html>
