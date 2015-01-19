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
#spinner {display: none;}
</style>

<c:import url="procesLogs.jsp"/>
<script type="text/javascript">
// <![CDATA[
$(document).ready(function() {
	$("#registro_tasques table").find("tbody > tr > td > a.retroces").each(function(){
		$(this).hover(
			function(){
				var $fil = $(this).parent().parent();						// Fila de la taula
				var element = $fil.find("td:nth-child(3)").html().trim(); 	// Tasca
				var token = $fil.find("td:nth-child(6)").html().trim();		// Token
				var tokens = token.split("/");
				$fil.addClass("registre_a_retrocedir");				
				if (element.indexOf("Tasca") == 0) {						// És una tasca
					$fil.nextAll().each(function(){
						if ($(this).find("td").length > 0) {
							var elem = $(this).find("td:nth-child(3)").html().trim();
							var tok = $(this).find("td:nth-child(6)").html().trim();
							var toks = tok.split("/");
							
							if (elem.indexOf("Tasca") == 0) {
								var t = "/";
								for (i = 0; i < tokens.length; i++){
									if (tokens[i] != "" && t != "/") t = t + "/";
									t = t + tokens[i];
									subt = t + "/";
									if (tok == t || tok == subt) {
										$(this).addClass("registre_a_retrocedir");
										return;
									}
								}
								var t = "/";
								var subt = "";
								for (i = 0; i < toks.length; i++){
									if (toks[i] != "" && t != "/") t = t + "/";
									t = t + toks[i];
									subt = t + "/";
									if (token == t || token == subt) {
										$(this).addClass("registre_a_retrocedir");
										return;
									}
								}
							}
						}
					});
				}
			},
			function(){
				$("#registro_tasques table").find("tbody > tr").removeClass("registre_a_retrocedir");
			}
		);
	});
});

function recargarRegistro() {
	$("#spinner").show();
	$('#registro_tasques').hide();
	$('#contingut-registre').load('<c:url value="/nodeco/v3/expedient/${expedient.id}/registre?tipus_retroces='+$('#tipus_retroces').val()+'"/>');
}

function recargarPanelesLog (tipus_retroces, correcte) {
	if (correcte) {
		if ($('#tipus_retroces').val() == 0)
			$('#tipus_retroces').val(1);
		else
			$('#tipus_retroces').val(0);
		recargarRegistro();
	}
}
//]]>
</script>