<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code='expedient.nova.data.nova'/></title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/helium.tramitar.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
		<style>
		body {margin-top:15px;}
		form {width:calc(100% - 15px);}
		input, select, textarea {width:100%;}
		label {font-weight:normal;}
		div.tab-content {width:calc(100% - 15px);padding-bottom:15px;}
		p.help-block {padding-top:0;margin-top:4px !important;margin-bottom:0px;}
		#tabnav .glyphicon {padding-right:10px;}
		#contingut-alertes, .tauladades {padding-right:15px;}
		.alert {margin-top:20px;}
		.form-group {padding-right:15px;margin-left:0px !important;margin-bottom:10px;}
		.form-group input, .form-group textarea {width:100%;}
		.form-group li > .select2-container {width:100%;padding-right:20px;}
		.form-group .select2-container {width:calc(100% + 14px);}
		.form-group.condensed {margin-bottom:0px;}
		.form-group.registre .btn_afegir{margin-top:10px;}
		.form-group.multiple_camp {margin-bottom:6px;}
		.controls {padding-right:5px !important;}
		.input-group-multiple {padding-left:15px;padding-right:0px;}
		.multiple input, .multiple textarea, .multiple_camp .input-group, .multiple_camp .inputcheck {float:left;width:calc(100% - 29px);}
		.multiple_camp div.suggest, .multiple_camp div.seleccio {float:left;width:calc(100% - 29px) !important;}
		.multiple_camp .btn_eliminar {float:left;margin-left:4px;}
		.multiple .termgrup {float:left;width:calc(100% - 14px);}
		.pad-left-col-xs-3 {left:25%;}
		.clear {clear:both;}
		.clearForm {clear:both;margin-bottom:10px;margin-top:15px;border-bottom:solid 1px #DADADA;}
		.input-append {width:calc(100% - 27px);}
		.eliminarFila {background-color:#FFF;border:solid 1px rgb(204, 204, 204);border-radius:4px;padding:9px 6px;}
		.eliminarFila:hover {color:#333;background-color:#e6e6e6;border-color:#adadad;}
		.btn_eliminar {background-color:#FFF;border:solid 1px rgb(204, 204, 204);border-radius:4px;padding:9px 6px;}
		.table {margin-bottom:0px;}
		.col-xs-9 .checkbox {width:auto;}
		.termgrup input, .termgrup select {float:left;width:calc(100% - 65px);}
		.tercpre {float:left;width:32%;padding-left:0px !important;padding-right:8px !important;}
		.tercmig {float:left;width:32%;padding-left:4px !important;padding-right:4px !important;}
		.tercpost {float:left;width:36%;padding-left:8px !important;padding-right:0px !important;}
		.label-term {float:left;width:60px;text-align:left !important;margin-right:5px;font-weight:normal !important;}
		.btn_date {cursor:pointer;}
		.has-error .form-control {background-color:#ffefe !important;}
		.hide {display: none;}
		.carregant {margin: 1em 0 2em 0;text-align: center;}
		.registre_taula {overflow: auto;}
		.registre table .colEliminarFila {width:1px;}
		.registre table .opciones {text-align:center;}
		.registre .multiple input, .registre .multiple textarea, .registre .multiple_camp .input-group, .registre .multiple_camp .inputcheck {float:left;width:100% !important;}
		.registre .multiple_camp div.suggest, .registre .multiple_camp div.seleccio {float:left;width:100% !important;}
		.registre .multiple input.checkbox {width:auto !important;}
		.registre th {
			border-bottom:solid 1px #CACACA !important;
			background:rgba(221,221,221,1);
			background:-moz-linear-gradient(top, rgba(221,221,221,1) 0%, rgba(245,245,245,1) 100%);
			background:-webkit-gradient(left top, left bottom, color-stop(0%, rgba(221,221,221,1)), color-stop(100%, rgba(245,245,245,1)));
			background:-webkit-linear-gradient(top, rgba(221,221,221,1) 0%, rgba(245,245,245,1) 100%);
			background:-o-linear-gradient(top, rgba(221,221,221,1) 0%, rgba(245,245,245,1) 100%);
			background:-ms-linear-gradient(top, rgba(221,221,221,1) 0%, rgba(245,245,245,1) 100%);
			background:linear-gradient(to bottom, rgba(221,221,221,1) 0%, rgba(245,245,245,1) 100%);
			filter:progid:DXImageTransform.Microsoft.gradient( startColorstr='#dddddd', endColorstr='#f5f5f5', GradientType=0 );
			min-width: 160px;
		}
		.registre th.colEliminarFila{min-width: 1px;}
	</style>
</head>
<body>		
	<div id="formulari">
	<c:import url="procesDadaNova.jsp"/>
	</div>
	
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
		<button class="btn btn-primary right" type="submit" name="accio" value="desar_variable">
			<spring:message code='comuns.guardar' />
		</button>
	</div>
</body>
</html>
