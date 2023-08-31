<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<style>
div.proces {
	color: white !important;
	background-color: #428bca !important;
	border-color: #357ebd !important;
	font-weight: bold;
}
div.proces:hover {
	background-color: #3071a9 !important;
	border-color: #285e8e !important;
}
.panel-body {
	padding-bottom: 0px !important;
}
.panel-body > table {
	overflow: hidden;
}
.panel-body table:last-child {
	margin-bottom: 0px;
}
.contingut-carregant-proces {
	margin: 1em 0 2em 0;
	text-align: center;
}
.panell-error {
	max-height: 180px;
	overflow-y: scroll;
}
#dataTables_new {padding-top: 5px;padding-bottom: 10px;}

</style>

<c:set var="potAfegir">${expedient.permisDocManagement && (empty expedient.dataFi || !expedient.arxiuActiu)}</c:set>
<div class="${potAfegir ? 'pull-left' : '' }" style="padding: 5px; height: 50px;">
	<div class="pull-left">
		<a id="descarregarZip"
			href="<c:url value="/v3/expedient/${expedient.id}/document/descarregarZip"/>" class="btn btn-default" title="<spring:message code="expedient.document.descarregar.zip"/>">
			<span class="fa fa-download"></span> <spring:message code="comu.boto.descarregar"></spring:message>
		</a>
	</div>
	<div class="pull-left" style="padding-left: 5px; width: 70px;">	
		<!-- Botó d'ordenació -->
		<div id="sortDocuments" class="btn-group btn-group-justified" data-sort="default" title="<spring:message code="expedient.document.ordenar.default"/>">
		  <a class="btn btn-default dropdown-toggle" data-toggle="dropdown" href="#">
		  	<span id="sortDocumentsMainIcon">
		  		<i class="fa fa-sort-amount-asc"></i>
		  	</span>
		    <span class="fa fa-caret-down"></span>
		  </a>
		  <ul class="dropdown-menu">
		    <li><a class="documentsSortOption" data-sort="default"><i class="fa fa-sort-amount-asc documentsSortIcon"></i> <spring:message code="expedient.document.ordenar.default"/></a></li>
		    <li><a class="documentsSortOption" data-sort="titol"><i class="fa fa-sort-alpha-asc documentsSortIcon"></i> <spring:message code="expedient.document.ordenar.titol"/></a></li>
		    <li><a class="documentsSortOption" data-sort="data"><i class="documentsSortIcon"><span class="fa fa-sort-amount-asc fa-fw"></span><span class="fa fa-clock-o"></span></i> <spring:message code="expedient.document.ordenar.data"/></a></li>
		  </ul>
		</div>
	</div>
	<div class="pull-left" style="padding-left: 5px">
		<table border="0">
			<tr>
				<td>
					<span class="fa fa-search" style="position: absolute;float: left;padding-left: 10px;padding-top: 10px;"></span>
					<input id="searchDocuments" class="form-control" placeholder="<spring:message code="expedient.document.filtrar"/>" autocomplete="off" spellcheck="false" autocorrect="off" tabindex="1"
						style="padding-left: 35px;">
				</td>
				<td>
					<span id="searchTotal" style="padding-left: 20px; font-weight: bold;"></span>
				</td>
			</tr>
		</table>
	</div>
	<div class="pull-left">
		
		<a id="notificarZip" class="btn btn-default" 
			href="../../v3/expedient/${expedientId}/document/notificarZip" 
			data-rdt-link-modal="true" 
			data-rdt-link-modal-min-height="180">
				<span class="fa fa-paper-plane"></span>
				<spring:message code="expedient.boto.notificar_zip"/>
		</a>
	</div>
</div>


<c:import url="procesDocuments.jsp"/>
<script type="text/javascript">
// <![CDATA[			
$(document).ready(function() {				
	$('.procesDocument').click( function() {
		var icona = $(this).find('.icona-collapse');
		icona.toggleClass('fa-chevron-down');
		icona.toggleClass('fa-chevron-up');
		if ($(this).data('carrega') == "ajax") {
			$('#contingut-carregant').show();
			var processInstanceId = $(this).data('processinstanceid');
			$("#panel_document_"+processInstanceId).html('<div style="text-align:center"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>');
			recargarPanel (processInstanceId, true);
		}
	});

	$('.psigna-info').click(function(element){
		var docId = $(this).data('psigna');
		$('#psigna_' + docId).modal();
	});
	
	$('#searchDocuments').on('input', function(){
		searchDocuments($('#searchDocuments').val());
	});
	
	$('.documentsSortOption').click(function() {
		$('#sortDocumentsMainIcon').empty().append($('.documentsSortIcon', this).clone());
		sort = $(this).data('sort');
		$('#sortDocuments').data('sort', sort);
		sortDocuments(sort);
	});
	
});
function recargarPanel (processInstanceId, correcte) {
	if (correcte) {
		var sort = $('#sortDocuments').data('sort');
		var url = '<c:url value="/nodeco/v3/expedient/${expedientId}/proces/"/>' + processInstanceId + '/document?sort=' + sort;
		var panell = $("#dataTable_documents_"+processInstanceId);
		panell.load(url, function() {
			// Després de recarregar filtra si hi ha text per filtrar
			if ($('#searchDocuments').val() != '')
				$('#searchDocuments').trigger('input');
		});
	}
}

function esborrarSignatura(documentStoreId, correcte) {
	if (correcte) {
		$("#document_"+documentStoreId).find(".signature").remove();
	}
}

function reprocessar(documentId) {
	$('#form_psigna_' + documentId).submit();
}

function searchDocuments(text) {
	total = 0;
	totalProces = 0;
	fadeoutMs = 300;
	$('.procesDocument').each(function() {
		$proces = $(this);
		totalProces = 0;
		processInstanceId = $proces.data('processinstanceid');
		$taula = $('#panel_document_'+processInstanceId);
		$('.taulaFila', $taula).each(function() {
			totalFila = 0;
			$fila = $(this);
			$('.cellDocument', $fila).each(function(){
				$document = $(this);
				if (text == ""
						|| $('.nom_document', this).text().toLowerCase().includes(text.toLowerCase())) 
				{
					hideFila = false;
					$document.show();
					total++;
					totalFila ++;
					totalProces ++;
				} else {
					// amaga la cel·la
					$document.hide(fadeoutMs);
				}
			});
			if (totalFila == 0) {
				$fila.hide(fadeoutMs);
			} else {
				$fila.show();
				hideProcess = false;
			}
		});
		if (totalProces == 0) {
			$taula.hide(fadeoutMs);
		} else {
			$taula.show();
		}
		if (text != '') {
			$('#searchTotal').text(total + ' <spring:message code="expedient.document.filtrar.trobats"/>');
		} else {
			$('#searchTotal').text('');
		}
	});
}

function sortDocuments(sort) {
	$('.procesDocument').each(function() {
		$proces = $(this);
		totalProces = 0;
		processInstanceId = $proces.data('processinstanceid');
		if ($('#panel_document_'+processInstanceId).find('table').length) {
			recargarPanel($(this).data('processinstanceid'), true);
		}
	});
}
//]]>
</script>

