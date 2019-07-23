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
	<title><spring:message code="expedient.massiva.titol"/></title>
	<meta name="title" content="<spring:message code="expedient.massiva.titol"/>"/>	
	
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">

	<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/helium.datatable.js"/>"></script>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	
	<script type="text/javascript">
		// <![CDATA[
		var docPlantilla = {
			<c:forEach items="${documents}" var="document">'d_${document.id}' : ${document.plantilla},
			</c:forEach>__none__ : false
		}
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
		function confirmarBuidarLog(e) {
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			return confirm("<spring:message code='expedient.eines.confirm_buidarlog_expedients' />");
		}
		function confirmarReprendreExpedient(e) {
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			return confirm("<spring:message code='expedient.eines.confirm_reprendre_expedients' />");
		}
		function confirmarFinalitzarExpedient(e) {
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			return confirm("<spring:message code='expedient.eines.confirm_finalitzar_expedients' />");
		}
		function confirmarMigrarExpedient(e) {
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			return confirm("<spring:message code='expedient.eines.confirm_migrar_expedients' />");
		}
		$(document).ready(function(){
			$('.date_time').datetimepicker({
				locale: moment.locale('${idioma}'),
				minDate: new Date(),
				format: "DD/MM/YYYY HH:mm"
		    });			
            $("#inici_timer").on("dp.change",function (e) {
				$("form").each(function(){
					if (!$(this).find('#inici').length)
						$(this).append('<input type="hidden" id="inici" name="inici">');
				});
				$("input[type='hidden'][name='inici']").each(function(){ $(this).val($("input[name=inici]").val()); });
				$('#var').trigger('change');
				$('#docId').trigger('change');
            });
        	$("#grup-default-titol").click(function(event) {
       			$("#grup-default-dades").slideToggle("slow", function() {
       				$('#grup-default-titol .icona-collapse').toggleClass('fa-chevron-down');
    				$('#grup-default-titol .icona-collapse').toggleClass('fa-chevron-up');
       			});
       		});
			$("input[name=correu]").change(function(){
				var correu = $(this).is(":checked") ? true : false;
				$("form").each(function(){
					if (!$(this).find('#correu').length)
						$(this).append('<input type="hidden" id="correu" name="correu">');
				});
				$("input[type='hidden'][name='correu']").each(function(){ $(this).val(correu); });
				$('#var').trigger('change');
				$('#docId').trigger('change');
			});
			$('#var').on('change', function() {
				$("a[name='modificar_variable']").toggleClass('disabled', $("#var").val() == "");
				$("a[name='modificar_variable']").attr("href","../../v3/expedient/massiva/"+$("#var").val()+"/modificarVariables?inici="+$("#inici").val()+"&correu="+$("#correu").is(":checked"))
			});
			$('#docId').on('change', function() {
				var doc = "__none__";
				var docId = $("#docId").val();
				if (docId != "") {
					doc = "d_" + docId;
				} 
			 	
				if (docId == "") {
			 		$("button[value='document_esborrar']").prop('disabled', true);
			 		$("a[name='document_modificar']").addClass('disabled');
			 	} else {
			 		$("a[name='document_modificar']").removeClass('disabled');
			 		$("a[name='document_modificar']").attr("href","../../v3/expedient/massiva/"+docId+"/documentModificar?inici="+$("#inici").val()+"&correu="+$("#correu").is(":checked"));
			 		$("button[value='document_esborrar']").prop('disabled', false);
			 	}
				$("a[name='document_adjuntar_massiu']").attr("href","../../v3/expedient/massiva/documentAdjunt?inici="+$("#inici").val()+"&correu="+$("#correu").is(":checked")+"&docId="+docId);
			 	$("button[value='document_generar']").prop('disabled', !eval("docPlantilla." + doc));
			});
			$("button[value='document_esborrar']").prop('disabled', true);
			$("button[value='document_generar']").prop('disabled', true);
			$("a[name='document_modificar']").addClass('disabled');			
			$("a[name='modificar_variable']").addClass('disabled');		
			
			$('#grup-default-titol').click( function() {
				var icona = $(this).find('.icona-collapse');
				icona.toggleClass('fa-chevron-down');
				icona.toggleClass('fa-chevron-up');
				var panell = $('.tableExpedients .panel-body');
				panell.load('<c:url value="/nodeco/v3/expedient/massiva/expedientsSeleccio"/>');
			});
		});
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
		.label-titol .control-label {padding-bottom: 20px;}
		.label-titol {background-color: #fefefe; border: 1px solid #e3e3e3; border-radius: 4px; box-shadow: 0 1px 1px rgba(0, 0, 0, 0.05) inset; margin-bottom: 20px; min-height: 20px; padding: 19px;}
		#opciones .label-titol {padding-bottom: 0px;}
		.form-group { margin-left: 15px;}
		#s2id_definicioProcesId, #subDefinicionsProces .select2-container {margin-bottom:25px;}
		#subDefinicionsProces {padding-bottom:0px;}
		textarea {width: calc(100% - 15px) !important;}
		.control-group {width: 100%;display: inline-block;}
		.control-group-mid {width: 49%;}
 		.control-group.left {float: left; margin-right:1%;}
		.control-group.right {float: right; margin-left:1%;}
		.form-group {padding-bottom: 15px;}
		input[type="checkbox"] {float: left;margin: 4px 10px 0 0;}
		.label-titol .form-group, .col-sm-6 {margin-left: 0px}
		.select2-container.form-control {width: calc(100% - 15px) !important;display: inline-block;}
		#documentModificarMas button, #documentModificarMas a.btn {margin-left: 4.45px; margin-right: 4.45px;}
		#massivaCanviVersio label.control-label {font-weight: normal}
		#massivaCanviVersio btn {font-weight: normal}
		#massivaCanviVersio #subDefinicionsProces.form-group {padding-right: 15px;}
		#div_timer label {float:left;}
		.btn {float: right;}
	</style>
</head>
<body>
	<div class="missatgesBlau">
		<div class="panel-group">
			<div class="panel panel-default">
			    <div id="grup-default-titol" data-toggle="collapse" class="panel-heading clicable grup" >
			    	<spring:message code="expedient.massiva.info"/>
					<div class="pull-right">
						<span class="icona-collapse fa fa-chevron-down"></span>
					</div>
					<span class="badge">${numExpedients}</span>
			    </div>
			    <div id="grup-default-dades" class="tableExpedients panel-collapse collapse">
					<div class="panel-body">
						<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
					</div>
			    </div>
			</div>
		</div>
	</div>
	<div id="opciones" class="control-group left">
		<div class="label-titol">
			<div class="form-group">
				<div class="help-block">
					<div id="div_timer" class="control-group left control-group-mid">
				    	<label for="inici"><spring:message code="expedient.consulta.datahorainici" /></label>
						<div class='col-sm-6'>
				            <div class="form-group">
				                <div class='input-group date date_time' id='inici_timer'>
				                    <input id="inici" name="inici" class="form-control date_time" data-format="dd/MM/yyyy hh:mm" type="text">
				                    <span class="input-group-addon"><span class="fa fa-calendar"></span></span>
				                </div>
				            </div>
				            <script type="text/javascript">
			                    $(document).ready(function() {
									$("#inici").on('focus', function() {
										$('.fa-calendar').click();
									});
								});
		                    </script>
				    	</div>
					</div>
					
					<div class="control-group form-group control-group-mid">
						<input type="checkbox" id="correu" name="correu" value="${correu}"/>
						<label for="correu"><spring:message code="expedient.massiva.correu"/></label>
					</div>
					
					<div class="control-group">
						<label><spring:message code="expedient.tramitacio.massiva"/></label>
						<select name="opciones_tramitaci">
							<option value="aturarMas"><spring:message code='expedient.massiva.aturar' /></option>
							<c:if test="${permisAdministrador}"><option value="scriptMas"><spring:message code='expedient.massiva.executarScriptMas' /></option></c:if>
							<option value="massivaCanviVersio"><spring:message code='expedient.massiva.actualitzar' /></option>
							<option value="massivaExecutarAccio"><spring:message code='expedient.massiva.accions' /></option>
							<option value="reindexarMas"><spring:message code='expedient.eines.reindexar.expedients' /></option>
							<option value="modificarVariablesMasCommand"><spring:message code='expedient.massiva.modificar_variables' /></option>
							<option value="documentModificarMas"><spring:message code='expedient.massiva.documents' /></option>
							<option value="buidarlogMas"><spring:message code='expedient.eines.buidarlog.expedients' /></option>
							<option value="reprendreExpedientMas"><spring:message code='expedient.eines.reprendre_expedients' /></option>
							<option value="finalitzarExpedientMas"><spring:message code='expedient.eines.finalitzar_expedients' /></option>
							<option value="migrarExpedientMas"><spring:message code='expedient.eines.migrar_expedients' /></option>
						</select>
						<script>
							$(document).ready(function() {
								$("select[name='opciones_tramitaci']").select2({
								    width: '100%',
								    allowClear: true,
								    minimumResultsForSearch: 10
								});
								$("select[name='opciones_tramitaci']").on('select2-open', function() {
									var iframe = $('.modal-body iframe', window.parent.document);
									var height = $('html').height() + 30;
									iframe.height(height + 'px');
								});
								$("select[name='opciones_tramitaci']").on('select2-close', function() {
									var iframe = $('.modal-body iframe', window.parent.document);
									var height = $('html').height();
									iframe.height(height + 'px');
								});								

								$("select[name='opciones_tramitaci']").on('change', function() {
									$('.opcionMasiva').hide();
									$("#"+$(this).val()).closest(".opcionMasiva").show();
								});
								$("select[name='opciones_tramitaci']").trigger('change');
							});
						</script>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="opcionMasiva control-group right">
		<div class="label-titol">
			<label class="control-label"><spring:message code='expedient.massiva.aturar' /></label>
			<div class="form-group">
				<form:form cssClass="form-horizontal form-tasca" id="aturarMas" name="aturarMas" action="massiva/aturarMas" method="post" commandName="expedientEinesAturarCommand" onsubmit="return confirmarAturar(event)">
					<hel:inputTextarea inline="true" name="motiu" textKey="expedient.eines.motiu" placeholderKey="expedient.eines.motiu"/>
					<button class="btn btn-primary right" type="submit" name="accio" value="aturar">
						<spring:message code='comuns.aturar' />
					</button>
				</form:form>	
			</div>
		</div>
	</div>

	<div class="opcionMasiva control-group right">
		<div class="label-titol">
			<label class="control-label"><spring:message code='expedient.massiva.executarScriptMas' /></label>
			<div class="form-group">
				<form:form cssClass="form-horizontal form-tasca" id="scriptMas" name="scriptMas" action="massiva/scriptMas" method="post" commandName="expedientEinesScriptCommand" onsubmit="return confirmarScript(event)">
					<hel:inputTextarea inline="true" name="script" textKey="comuns.executar" placeholderKey="comuns.executar"/>
					<button class="btn btn-primary right" type="submit" name="accio" value="executar">
						<spring:message code='comuns.executar' />
					</button>
				</form:form>	
			</div>
		</div>
	</div>

	<div class="opcionMasiva control-group right">
		<div class="label-titol">
			<label class="control-label"><spring:message code='expedient.massiva.actualitzar' /></label>
			<div class="form-group">
				<form:form cssClass="form-horizontal form-tasca" id="massivaCanviVersio" name="massivaCanviVersio" action="massiva/massivaCanviVersio" method="post" commandName="canviVersioProcesCommand" onsubmit="return confirmarCanviVersio(event)">
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
<%-- 										<option value="${item.jbpmId}">${item.etiqueta}</option> --%>
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
											var height = $('html').height() + 30;
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
					
					<button class="btn btn-primary right" type="submit" name="accio" value="canviar_versio">
						<spring:message code='comuns.canviar_versio' />
					</button>
				</form:form>	
			</div>
		</div>
	</div>

	<div class="opcionMasiva control-group right">
		<div class="label-titol">
			<label class="control-label"><spring:message code='expedient.massiva.accions' /></label>
			<div class="form-group">
				<c:if test="${not empty accions}">
					<form:form cssClass="form-horizontal form-tasca" id="massivaExecutarAccio" name="massivaExecutarAccio" action="massiva/massivaExecutarAccio" method="post" commandName="execucioAccioCommand" onsubmit="return confirmarExecutarAccio(event)">
						<hel:inputSelect inline="true" name="accioCodi" textKey="expedient.massiva.exec_accio" placeholderKey="expedient.massiva.exec_accio" optionItems="${accions}" optionValueAttribute="codi" optionTextAttribute="nom"/>
						
						<button class="btn btn-primary right" type="submit" name="accio" value="executar_accio">
							<spring:message code='comuns.executar' />
						</button>
					</form:form>
				</c:if>
				<c:if test="${empty accions}">
					<spring:message code="expedient.document.info.senseaccions"/>
				</c:if>
			</div>
		</div>
	</div>
		
	<div class="opcionMasiva control-group right">
		<div class="label-titol">
			<label class="control-label"><spring:message code='expedient.eines.reindexar.expedients' /></label>
			<div class="form-group">
				<form:form cssClass="form-horizontal form-tasca" id="reindexarMas" name="reindexarMas" action="massiva/reindexarMas" method="post" onsubmit="return confirmarReindexar(event)">
					<button class="btn btn-primary right" type="submit" name="accio" value="reindexar">
						<spring:message code='comuns.reindexar' />
					</button>
				</form:form>
			</div>
		</div>
	</div>
	
	<div class="opcionMasiva control-group right">
		<div class="label-titol">
			<label class="control-label"><spring:message code='expedient.eines.buidarlog.expedients' /></label>
			<div class="form-group">
				<form:form cssClass="form-horizontal form-tasca" id="buidarlogMas" name="buidarlogMas" action="massiva/buidarlogMas" method="post" onsubmit="return confirmarBuidarLog(event)">
					<button class="btn btn-primary right" type="submit" name="accio" value="buidarlog">
						<spring:message code='comuns.buidarlog' />
					</button>
				</form:form>
			</div>
		</div>
	</div>
	
	<div class="opcionMasiva control-group right">
		<div class="label-titol">
			<label class="control-label"><spring:message code='expedient.eines.reprendre_expedients' /></label>
			<div class="form-group">
				<form:form cssClass="form-horizontal form-tasca" id="reprendreExpedientMas" name="reprendreExpedientMas" action="massiva/reprendreExpedientMas" method="post" onsubmit="return confirmarReprendreExpedient(event)">
					<button class="btn btn-primary right" type="submit" name="accio" value="reprendreExpedient">
						<spring:message code='comuns.reprendre' />
					</button>
				</form:form>
			</div>
		</div>
	</div>
	
	<div class="opcionMasiva control-group right">
		<div class="label-titol">
			<label class="control-label"><spring:message code='expedient.eines.migrar_expedients' /></label>
			<div class="form-group">
				<form:form cssClass="form-horizontal form-tasca" id="migrarExpedientMas" name="migrarExpedientMas" action="massiva/migrarExpedientMas" method="post" onsubmit="return confirmarMigrarExpedient(event)">
					<button class="btn btn-primary right" type="submit" name="accio" value="migrarExpedient">
						<spring:message code='comuns.finalitzar' />
					</button>
				</form:form>
			</div>
		</div>
	</div>
	
	<div class="opcionMasiva control-group right">
		<div class="label-titol">
			<label class="control-label"><spring:message code='expedient.eines.finalitzar_expedients' /></label>
			<div class="form-group">
				<form:form cssClass="form-horizontal form-tasca" id="finalitzarExpedientMas" name="finalitzarExpedientMas" action="massiva/finalitzarExpedientMas" method="post" onsubmit="return confirmarFinalitzarExpedient(event)">
					<button class="btn btn-primary right" type="submit" name="accio" value="finalitzarExpedient">
						<spring:message code='comuns.finalitzar' />
					</button>
				</form:form>
			</div>
		</div>
	</div>

	<div class="opcionMasiva control-group right">
		<div class="label-titol">
			<label class="control-label"><spring:message code='expedient.massiva.modificar_variables' /></label>
			<div class="form-group">
				<form:form cssClass="form-horizontal form-tasca" id="modificarVariablesMasCommand" name="modificarVariablesMasCommand" action="massiva/modificarVariablesMasCommand" method="post" commandName="modificarVariablesCommand" onsubmit="return confirmarModificarVariables(event)">
					<hel:inputSelect inline="true" name="var" textKey="expedient.eines.modificar_variables" placeholderKey="expedient.consulta.select.variable" optionItems="${variables}" optionValueAttribute="id" optionTextAttribute="codi"/>
					<a class="btn btn-primary right" name="modificar_variable" href="#" data-rdt-link-modal-min-height="300" data-rdt-link-modal="true"><spring:message code='comuns.modificar'/></a>
				</form:form>
			</div>
		</div>
	</div>

	<div class="opcionMasiva control-group right">
		<div class="label-titol">
			<label class="control-label"><spring:message code='expedient.massiva.documents' /></label>
			<div class="form-group">
				<c:if test="${not empty documents}">
					<form:form cssClass="form-horizontal form-tasca" id="documentModificarMas" name="documentModificarMas" action="massiva/documentModificarMas" method="post" commandName="documentExpedientCommand" onsubmit="return confirmarModificarDocument(event)">
						<form:hidden path="expedientId"/>
						<hel:inputSelect inline="true" name="docId" textKey="expedient.massiva.documents" placeholderKey="expedient.consulta.select.document" optionItems="${documents}" optionValueAttribute="id" optionTextAttribute="documentNom" required="true" emptyOption="true"/>
						<a data-rdt-link-modal-min-height="500" class="btn btn-primary right" name="document_modificar" href="#" data-rdt-link-modal="true"><spring:message code='comuns.modificar' /></a>
						
						<button class="btn btn-primary right" type="submit" name="accio" value="document_generar">
							<spring:message code='tasca.doc.generar' />
						</button>
						<button class="btn btn-primary right" type="submit" name="accio" value="document_esborrar">
							<spring:message code='comuns.esborrar' />
						</button>
						<a data-rdt-link-modal-min-height="500" class="btn btn-primary right" name="document_adjuntar_massiu" href="../../v3/expedient/massiva/documentAdjunt" data-rdt-link-modal="true"><spring:message code='expedient.document.adjuntar_document_massiu' /></a>
					</form:form>
				</c:if>
				<c:if test="${empty documents}">
					<spring:message code="expedient.document.info.sensedocuments"/>
				</c:if>	
			</div>
		</div>
	</div>
	<script type="text/javascript">
		$("#documentModificarMas a").heliumEvalLink({
			refrescarAlertes: true,
			refrescarPagina: false
		});
		$("#modificarVariablesMasCommand a").heliumEvalLink({
			refrescarAlertes: true,
			refrescarPagina: false
		});
	</script>
</body>
</html>
