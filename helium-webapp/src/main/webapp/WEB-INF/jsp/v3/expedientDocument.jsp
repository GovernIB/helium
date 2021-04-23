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


<div class="pull-left">
	<a id="descarregarZip"
		href="<c:url value="/v3/expedient/${expedient.id}/document/descarregarZip"/>" class="btn btn-default" title="<spring:message code="expedient.document.descarregar.zip"/>">
		<span class="fa fa-download"></span> <spring:message code="comu.boto.descarregar"></spring:message>
	</a>
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
	
});
function recargarPanel (processInstanceId, correcte) {
	if (correcte) {
		var url = '<c:url value="/nodeco/v3/expedient/${expedientId}/proces/"/>' + processInstanceId + '/document';
		var panell = $("#dataTable_documents_"+processInstanceId);
		panell.load(url);
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
//]]>
</script>

