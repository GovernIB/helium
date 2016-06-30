<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:import url="procesAccions.jsp"/>

<script type="text/javascript">
// <![CDATA[	
	$(document).ready(function() {				
		$('.procesaccio').click( function() {
			var icona = $(this).find('.icona-collapse');
			icona.toggleClass('fa-chevron-down');
			icona.toggleClass('fa-chevron-up');
			if ($(this).data('carrega') == "ajax") {
				$('#contingut-carregant').show();
				var processInstanceId = $(this).data('processinstanceid');
				$("#panel_accio_"+processInstanceId).html('<div style="text-align:center"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>');
				recargarPanel (processInstanceId, true);
			}
		});

		$(".btn_accio").click(function() {
			if (confirm("<spring:message code='expedient.info.confirm.accio.executar'/>")) {
				var boto = $(this);
				setTimeout(function() {boto.attr('disabled', true)}, 1);
				return true;
			}
			return false;
		});
	});
	function recargarPanel (processInstanceId, correcte) {
		if (correcte) {
			var url = '<c:url value="/nodeco/v3/expedient/${expedientId}/proces/"/>' + processInstanceId + '/accio';
			var panell = $("#dataTable_accio_"+processInstanceId);
			panell.load(url);
		}
	}
//]]>
</script>