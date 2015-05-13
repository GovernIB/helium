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
#dataTables_new {padding-top: 5px;padding-bottom: 10px;}
</style>

<c:import url="procesTasques.jsp"/>

<script type="text/javascript">
// <![CDATA[

function agafar(procesId, tascaId, correcte) {
	if (correcte) {		
		var url = '<c:url value="/nodeco/v3/expedient/${expedientId}/tasca/'+tascaId+'/refrescarPanel/"/>' + procesId;
		var panell = $("#dataTable_tasca_"+procesId);
		panell.load(url, function() {
			$('#dropdown-menu-'+tascaId+' #tramitar-tasca-'+tascaId).click();
		});
	}
}

function alliberar(procesId, tascaId, correcte) {
	if (correcte) {
		var url = '<c:url value="/nodeco/v3/expedient/${expedientId}/tasca/'+tascaId+'/refrescarPanel/"/>' + procesId;
		var panell = $("#dataTable_tasca_"+procesId);
		panell.load(url);
	}
}

function recargarPanelTasca(procesId, tascaId, correcte) {
	if (correcte) {
		var url = '<c:url value="/nodeco/v3/expedient/${expedientId}/tasca/'+tascaId+'/refrescarPanel/"/>' + procesId;
		var panell = $("#dataTable_tasca_"+procesId);
		panell.load(url);
	}
}
//]]>
</script>