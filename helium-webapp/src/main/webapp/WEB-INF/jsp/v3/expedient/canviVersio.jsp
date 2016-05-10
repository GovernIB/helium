<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="expedient.massiva.actualitzar"/></title>
	<hel:modalHead/>
	<meta name="title" content="<spring:message code="expedient.massiva.actualitzar"/>"/>	
	
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	
	<script type="text/javascript">
		// <![CDATA[
		function confirmarCanviVersio(e) {
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			return confirm("<spring:message code='expedient.eines.confirm_canviar_versio_proces'/>");
		}
		// ]]>
	</script>
	<style type="text/css">
		.contingut-carregant {
			text-align: center;
		}
		div.grup:hover {background-color: #e5e5e5 !important;border-color: #ccc !important;}
		div.grup .panel-body-grup {padding-bottom: 0px !important;}
		.panel-body-grup {margin: -1px;}
		.panel-default a, .panel-default a:HOVER {text-decoration: none;}
		.table {margin-bottom: 5px;}
		.form-horizontal .control-label.col-xs-4 {width: 22.333%;}		
		.label-titol .control-label {padding: 20px;}
/* 		.label-titol {background-color: #fefefe; border: 1px solid #e3e3e3; border-radius: 4px; box-shadow: 0 1px 1px rgba(0, 0, 0, 0.05) inset; margin-bottom: 20px; min-height: 20px; padding: 19px;} */
		#s2id_definicioProcesId, #subDefinicionsProces .select2-container {margin-bottom:25px;}
		#subDefinicionsProces {padding-bottom:0px; margin-right: 0px;}
		.control-group {width: 100%;display: inline-block;}
		.control-group-mid {width: 49%;}
 		.control-group.left {float: left; margin-right:1%;}
		.control-group.right {float: right; margin-left:1%;}
		.form-group {padding-bottom: 15px;}
		.label-titol .form-group, .col-sm-6 {margin-left: 0px}
		.select2-container.form-control {width: calc(100% - 15px) !important;display: inline-block;}
		#documentModificarMas button, #documentModificarMas a.btn {margin-left: 4.45px; margin-right: 4.45px;}
		#massivaCanviVersio label.control-label {font-weight: normal}
		#massivaCanviVersio btn {font-weight: normal}
		#massivaCanviVersio #subDefinicionsProces.form-group {padding-right: 15px;}
		.btn {float: right;}
	</style>
</head>
<body>
	<div class="opcionMasiva control-group right">
		<div class="label-titol">
			<div class="form-group">
				<form:form cssClass="form-horizontal form-tasca" id="canviVersio" name="canviVersio" action="canviVersio" method="post" commandName="canviVersioProcesCommand" onsubmit="return confirmarCanviVersio(event)">
					<div class="ctrlHolder">
						<h4 style="font-weight: bold;"><spring:message code="expedient.massiva.proces.principal"/>:</h4>
					</div>
					<label>${definicioProces.jbpmKey}</label>					
					<select name="definicioProcesId" id="definicioProcesId">
						<c:forEach var="df" items="${definicioProces.listIdAmbEtiqueta}" varStatus="status">
							<option value="${df.id}">${df.etiqueta}</option>
						</c:forEach>
					</select>
					<script>
						$("#definicioProcesId").select2({
						    width: '100%',
						    allowClear: true
						});
					</script>
					<c:if test="${not empty subDefinicioProces}">
						<div class="ctrlHolder">
							<h4 style="font-weight: bold;"><spring:message code="expedient.massiva.subprocessos"/>:</h4>
						</div>
						<div id="subDefinicionsProces" class="form-group">
							<c:forEach var="subProces" items="${subDefinicioProces}" varStatus="status">
								<label>${subProces.jbpmKey}</label>								
								<select id="subprocesId[${status.index}]" name="subprocesId">
									<c:forEach var="item" items="${subProces.listIdAmbEtiqueta}">
										<option value="${item.id}">${item.etiqueta}</option>
									</c:forEach>
								</select>
								
								<script>
									$(document).ready(function() {
										$("select[name='subprocesId']").select2({
										    width: '100%',
										    allowClear: true,
										    minimumResultsForSearch: 10
										});
										$("select[name='subprocesId']").on('select2-open', function() {
											var iframe = $('.modal-body iframe', window.parent.document);
											var height = $('html').height() + 90;
											iframe.height(height + 'px');
										});
										$("select[name='subprocesId']").on('select2-close', function() {
											var iframe = $('.modal-body iframe', window.parent.document);
											var height = $('html').height();
											iframe.height(height + 'px');
										});
									});
								</script>								
							</c:forEach>
						</div>
					</c:if>
					
					<div id="modal-botons">
						<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel">
							<spring:message code="comu.boto.cancelar"/>
						</button>
						<button class="btn btn-primary right" type="submit" name="accio" value="canviar_versio">
							<spring:message code='comuns.canviar_versio' />
						</button>
					</div>
				</form:form>	
			</div>
		</div>
	</div>
</body>
</html>
