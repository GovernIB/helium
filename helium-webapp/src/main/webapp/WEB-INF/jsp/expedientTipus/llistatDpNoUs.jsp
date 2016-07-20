<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key='entorn.llistat.netejar.titol' /></title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='defproc.llistat.confirmacio' />");
}
function selecTots() {
	var ch = $("#selTots:checked").val();
	$("#registre input[type='checkbox'][name='dpId']").each(function() {
		if(!this.disabled) {
			this.checked = ch;
		}
	});
}
$(document).ready(function() {
	$(".bexpborrarlog").click(function(){
		var btn = $(this);
		var exp = btn.parent();
		var expId = btn.data("id");
		$("body").css("cursor", "progress");
		$.ajax({
			type: "POST",
			url: "../nodeco/expedientTipus/borra_logsexp.html",
			dataType: 'json',
			data: {expedientId: expId, expedientTipusId: "${expedientTipusId}"},
			success: function(data){
				btn.remove();
				exp.append('<span class="exp_info">' + data.resultat + '</span>');
				$("body").css("cursor", "default");
			}
		})
		.fail(function( jqxhr, textStatus, error ) {
			debugger;
			var err = textStatus + ', ' + error;
			console.log( "Request Failed: " + err);
			btn.remove();
			exp.append('<span class="exp_info">' + err + '</span>');
			$("body").css("cursor", "default");
		})
	});
	
// 	$(".bexpborrartotslogs").click(function(){
// 		var btn = $(this);
// 		var cont_btn = btn.parent();
// 		var cont_exp = cont_btn.parent();
// 		var expsId = []; 
// 		$(".bexpborrarlog", cont_exp).each(function(){
// 			expsId.push($(this).data("id"));
// 		});
// 		$("body").css("cursor", "progress");
// 		$.ajax({
// 			type: "POST",
// 			url: "../nodeco/expedientTipus/borra_logsexps.html",
// 			dataType: 'json',
// 			data: {expedientsId: JSON.stringify(expsId), expedientTipusId: "${expedientTipusId}"},
// 			success: function(data){
// 				debugger;
// 				btn.remove();
// 				for (var i = 0; i < data.resultats.length; i++) {
// 					var entry = data.resultats[i];
// 					var id = entry.id;
// 					var msg = entry.resultat;
// 					var btn = $("button[data-id='" + id + "']").first();
// 					btn.parent().append('<span class="exp_info">' + msg + '</span>');
// 					btn.remove();
//                 }
// 				$("body").css("cursor", "default");
// 			}
// 		})
// 		.fail(function( jqxhr, textStatus, error ) {
// 			var err = textStatus + ', ' + error;
// 			console.log( "Request Failed: " + err);
// 			btn.remove();
// 			cont_btn.append('<span class="exp_info">' + err + '</span>');
// 			$("body").css("cursor", "default");
// 		})
// 	});

	$(".bexpborrartotslogs").click(function(){
		var btn = $(this);
		var cont_btn = btn.parent();
		var cont_exp = cont_btn.parent();
		var expsId = []; 
		$(".bexpborrarlog", cont_exp).each(function(){
			expsId.push($(this).data("id"));
		});
		$("body").css("cursor", "progress");
		$.ajax({
			type: "POST",
			url: "../nodeco/expedientTipus/borra_logsexps.html",
			dataType: 'json',
			data: {expedientsId: JSON.stringify(expsId), expedientTipusId: "${expedientTipusId}"},
			success: function(data){
				cont_btn.append('<span class="exp_info">' + data.resultat + '</span>');
				btn.remove();
				$(".bexpborrarlog", cont_exp).each(function(){
					$(this).remove();
				});
				$("body").css("cursor", "default");
			}
		})
		.fail(function( jqxhr, textStatus, error ) {
			var err = textStatus + ', ' + error;
			console.log( "Request Failed: " + err);
			btn.remove();
			cont_btn.append('<span class="exp_info">' + err + '</span>');
			$("body").css("cursor", "default");
		})
	});
	
	$(".submitButton").click(function(){
		$("body").css("cursor", "progress");
	});
});
// ]]>
</script>
<style type="text/css">
	.expafectats {padding: 5px 0px;}
	.expborrarlog, .expborrartotslogs {min-height: 22px;}
	.expborrarlog span {font-weight: bold;}
	.bexpborrarlog {float: right;}
	.bexpborrartotslogs {float: right;}
	.exp_info {float: right;}
</style>
</head>
<body>
	<c:set var="msg_afectats"><fmt:message key='defproc.llistat.llistar.afectats'/> <img src='/helium/img/bullet_error.png' title='Aquesta consulta pot resultar molt costosa!'/></c:set>
	<form action="netejar_df.html" method="post">
		<input type="hidden" name="expedientTipusId" value="${expedientTipusId}"/>
		<div class="buttonHolder" style="margin-bottom:10px;">
			<button type="submit" class="submitButton"><fmt:message key='entorn.llistat.netejar.seleccionats' /></button>
			<a href='<c:url value="/expedientTipus/llistat.html"/>'>
				<button type="button" class="submitButton"><fmt:message key='comuns.tornar' /></button>
			</a>
		</div>
		<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
			<display:column title="<input id='selTots' type='checkbox' value='false' onclick='selecTots()'>"  style="width:1%;">
				<input type="checkbox" name="dpId" value="${registre.id}"/>
			</display:column>
			<display:column property="jbpmName" titleKey="comuns.nom" sortable="true" url="/definicioProces/info.html" paramId="definicioProcesId" paramProperty="id"/>
			<display:column property="versio" titleKey="defproc.llistat.versions" sortable="true"/>
			<display:column property="expedientTipus.nom" titleKey="comuns.tipus_exp" sortable="true"/>
<%-- 			<display:column> --%>
<%-- 				<a href="<c:url value="/definicioProces/delete.html"><c:param name="definicioProcesId" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='defproc.llistat.esborrar_darrera' />" title="<fmt:message key='defproc.llistat.esborrar_darrera' />" border="0"/></a> --%>
<%-- 			</display:column> --%>
			<display:column title="${msg_afectats}" style="width:10%;">
				<form action="afectats_df.html"> <%-- onsubmit="return confirmarNetejarDf(event)"> --%>
					<input type="hidden" name="expedientTipusId" value="${expedientTipusId}"/>
					<input type="hidden" name="definicioProcesId" value="${registre.id}"/>
					<button type="submit" class="submitButton"><span><img src="/helium/img/magnifier.png"/></span></button>
				</form>
			</display:column>
		</display:table>
	<%-- 	<script type="text/javascript">initSelectable();</script> --%>
	
		<div style="clear: both"></div>
		
		<div class="buttonHolder">
			<button type="submit" class="submitButton"><fmt:message key='entorn.llistat.netejar.seleccionats' /></button>
			<a href='<c:url value="/expedientTipus/llistat.html"/>'>
				<button type="button" class="submitButton"><fmt:message key='comuns.tornar' /></button>
			</a>
		</div>
	</form>	
</body>
</html>