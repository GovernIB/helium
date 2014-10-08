<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="expedient.massiva.titol"/></title>

	<script type="text/javascript" src="<c:url value="/js/jquery-1.10.2.min.js"/>"></script> 
	<script src="<c:url value="/js/bootstrap.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/moment.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	
	<script type="text/javascript">
		// <![CDATA[
		function confirmarCanviVersio(e) {
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			return confirm("<spring:message code='expedient.massiva.confirm_canviar_versio_proces'/>");
		}
		function confirmarExecutarAccio(e) {
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			return confirm("<spring:message code='expedient.massiva.confirm_exec_accio'/>");
		}
		function confirmarScript(e) {
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			return confirm("<spring:message code='expedient.eines.confirm_executar_script_proces' />");
		}
		function confirmarAturar(e) {
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			return confirm("<spring:message code='expedient.massiva.confirm_aturar_tramitacio' />");
		}
		function confirmarModificarDocument(e) {
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			return confirm("<spring:message code='expedient.eines.confirm_modificar_document' />");
		}
		function confirmarModificarVariables(e) {
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			return confirm("<spring:message code='expedient.eines.confirm_modificar_variable' />");
		}
		function confirmarReindexar(e) {
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			return confirm("<spring:message code='expedient.eines.confirm_reindexar_expedients' />");
		}
		function confirmarReassignar(e) {
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			return confirm("<spring:message code='expedient.eines.confirm_reassignar_expedients' />");
		}

		$(document).ready(function(){			
			$('#inici_timer').datetimepicker({
				language: '${idioma}',
				minDate: new Date(),
				format: "DD/MM/YYYY HH:mm"
		    });			
            $("#inici_timer").on("dp.change",function (e) {
				$("form").each(function(){
					if (!$(this).find('#inici').length)
						$(this).append('<input type="hidden" id="inici" name="inici">');
					if (!$(this).find('#correu').length)
						$(this).append('<input type="hidden" id="correu" name="correu">');
				});
				$("input[type='hidden'][name='inici']").each(function(){ $(this).val($("input[name=inici]").val()); });
            });
			$('#grup-default-dades').on('shown.bs.collapse', function() {
				$('#grup-default-titol .icona-collapse').toggleClass('fa-chevron-down');
				$('#grup-default-titol .icona-collapse').toggleClass('fa-chevron-up');
			});			
			$('#grup-default-dades').on('hidden.bs.collapse', function() {
				$('#grup-default-titol .icona-collapse').toggleClass('fa-chevron-down');
				$('#grup-default-titol .icona-collapse').toggleClass('fa-chevron-up');
			});
			$("input[name=inici]").change(function(){
				
			});
			$("input[name=correu]").change(function(){
				var correu = $(this).is(":checked") ? true : false;
				$("form").each(function(){
					if (!$(this).find('#inici').length)
						$(this).append('<input type="hidden" id="inici" name="inici">');
					if (!$(this).find('#correu').length)
						$(this).append('<input type="hidden" id="correu" name="correu">');
				});
				$("input[type='hidden'][name='correu']").each(function(){ $(this).val(correu); });
			});
		});
		// ]]>
	</script>
	<style type="text/css">
		div.grup:hover {
			background-color: #e5e5e5 !important;
			border-color: #ccc !important;
		}
		div.grup .panel-body-grup {
			padding-bottom: 0px !important;
		}
		.panel-body-grup {
			margin: -1px;
		}
		.panel-default a, .panel-default a:HOVER {
			text-decoration: none;
		}
		.table {margin-bottom: 5px;}
		.form-horizontal .control-label.col-xs-4 {
			width: 22.333%
		}
		
		.label-titol .control-label {
			padding-bottom: 20px;
		}
		
		.label-titol {
			background-color: #fefefe; border: 1px solid #e3e3e3; border-radius: 4px; box-shadow: 0 1px 1px rgba(0, 0, 0, 0.05) inset; margin-bottom: 20px; min-height: 20px; padding: 19px;
		}
		.form-group { margin-left: 15px;}
		textarea {width: calc(100% - 15px) !important;}
		.control-group {width: 49%;}
		.control-group.left {float: left; margin-right:1%;}
		.control-group.left label {float: left;}
		.control-group.right {float: right; margin-left:1%;}
		.form-group {padding-bottom: 15px;}
		.form-group .right {float: right;}
	</style>
</head>
<body>
	<div class="missatgesBlau">		
		<div class="panel-group" id="accordion">
			<div class="panel panel-default">
				<a data-toggle="collapse" data-parent="#accordion" href="#grup-default-dades">					
				    <div id="grup-default-titol"" class="panel-heading clicable grup" >
				    	<spring:message code="expedient.massiva.info"/>
						<div class="pull-right">
							<span class="icona-collapse fa fa-chevron-down"></span>
						</div>
						<span class="badge">${fn:length(expedients)}</span>
				    </div>
			    </a>
			    <div id="grup-default-dades" class="panel-collapse collapse">
			      <div class="panel-body">
			       <table class="table table-bordered" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0">
				   		<thead>
							<tr>
								<th><spring:message code="expedient.llistat.columna.expedient"/></th>
								<th><spring:message code="expedient.llistat.columna.tipus"/></th>
								<th><spring:message code="expedient.llistat.columna.iniciat"/></th>
								<th><spring:message code="expedient.llistat.columna.estat"/></th>
							</tr>
						</thead>
				        <c:forEach var="expedient" items="${expedients}">
							<tbody>
								<tr>
									<td>${expedient.identificadorLimitat}</td>
									<td>${expedient.tipus.nom}</td>
									<td><fmt:formatDate value="${expedient.dataInici}" pattern="dd/MM/yyyy HH:mm"/></td>
									<td>${expedient.estat.nom}</td>
								</tr>
							</tbody>
						 </c:forEach>				       		
					</table>
			      </div>
			    </div>
			</div>
		</div>
		<div class="clear"></div>
			
		<div class="label-titol">
			<div class="form-group">
				<div class="help-block">
					<div class="control-group left">
				    	<label for="inici"><spring:message code="expedient.consulta.datahorainici" /></label>
						<div class='col-sm-6'>
				            <div class="form-group">
				                <div class='input-group date' id='inici_timer'>
				                    <input id="inici" name="inici" class="form-control" data-format="dd/MM/yyyy hh:mm" type="text">
				                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
				                </div>
				            </div>
				    	</div>
					</div>
					<div class="control-group right">
						<input type="checkbox" id="correu" name="correu" value="${correu}"/>
						<label for="correu"><spring:message code="expedient.massiva.correu"/></label>
					</div>						
				</div>
			</div>
		</div>
		<div class="clear"></div>		
		
		<div class="control-group left">
			<div class="label-titol">
				<label class="control-label"><spring:message code='expedient.eines.script' /></label>
				<div class="form-group">
					<form:form cssClass="form-horizontal form-tasca" id="aturarMas" name="aturarMas" action="" method="post" commandName="expedientEinesAturarCommand" onsubmit="return confirmarAturar(event)">
						<hel:inputTextarea inline="true" name="motiu" textKey="expedient.eines.motiu" placeholderKey="expedient.eines.motiu"/>
						<button class="btn btn-primary right" type="submit" name="accio" value="aturar">
							<spring:message code='comuns.aturar' />
						</button>
					</form:form>	
				</div>
			</div>
		</div>

		<div class="control-group right">
			<div class="label-titol">
				<label class="control-label"><spring:message code='expedient.eines.script' /></label>
				<div class="form-group">
					<form:form cssClass="form-horizontal form-tasca" id="scriptMas" name="scriptMas" action="" method="post" commandName="expedientEinesScriptCommand" onsubmit="return confirmarScript(event)">
						<hel:inputTextarea inline="true" name="script" textKey="comuns.executar" placeholderKey="comuns.executar"/>
						<button class="btn btn-primary right" type="submit" name="accio" value="executar">
							<spring:message code='comuns.executar' />
						</button>
					</form:form>	
				</div>
			</div>
		</div>
			
		<div class="control-group left">
			<div class="label-titol">
				<label class="control-label"><spring:message code='expedient.eines.reindexar.expedients' /></label>
				<div class="form-group">
					<form:form cssClass="form-horizontal form-tasca" id="reindexarMas" name="reindexarMas" action="" method="post" onsubmit="return confirmarReindexar(event)">
						<button class="btn btn-primary right" type="submit" name="accio" value="reindexar">
							<spring:message code='comuns.reindexar' />
						</button>
					</form:form>
				</div>
			</div>
		</div>
	</div>			
</body>
</html>
