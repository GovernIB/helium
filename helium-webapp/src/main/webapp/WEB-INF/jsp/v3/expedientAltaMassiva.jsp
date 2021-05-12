<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code="expedient.alta.massiva.titol"/></title>
	<hel:modalHead/>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
</head>
<body>		

	<form:form id="altaMassiva" cssClass="form-horizontal" action="altaMassiva" enctype="multipart/form-data" method="post" commandName="command">

		<!-- Selector del tipus d'expedient -->
		<div class="row">
			<hel:inputSelect labelSize="3" emptyOption="true" required="true" name="expedientTipusId" textKey="expedient.llistat.filtre.camp.expedient.tipus" placeholderKey="expedient.llistat.filtre.camp.expedient.tipus" optionItems="${expedientTipusPermesos}" optionValueAttribute="id" optionTextAttribute="nom" />
		</div>
		
		<div class="row">
			<div class="form-group">
				<label class="control-label col-xs-3 obligatori" for="file">
					<spring:message code="expedient.alta.massiva.form.file"/>					
				</label>
				<div class="col-xs-9">
					<input type="file" name="file" id="file" accept=".csv" />
					<c:set var="fileErrors"><form:errors path="file"/></c:set>
					<c:if test="${not empty fileErrors}">
						<div class="has-error">
							<p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="file"/></p>
						</div>
					</c:if>
				</div>
				<p class="comment col-xs-9 col-xs-offset-3">
					<spring:message code="expedient.alta.massiva.form.file.info"/>
					<a id="arxiuCsvExemple" href="<c:url value="/v3/expedient/altaMassiva/exempleCsv"/>" class="text text-success" title="<spring:message code="expedient.alta.massiva.form.file.exemple"></spring:message>"><span class="fa fa-download"></span></a>
				</p>
			</div>
			
		</div>
		
		<div class="row">
			<div class="col-sm-6">
				<hel:inputDate name="dataInici" time="true" labelSize="6" textKey="expedient.consulta.datahorainici" placeholder="dd/mm/yyyy HH:mm" />
			</div>
			<div class="col-sm-6">
				<hel:inputCheckbox name="correu" labelSize="6" textKey="expedient.massiva.correu" />
			</div>
		</div>

		<!-- Info darrera execució -->
		<div class="row">
			<div class="col-md-12">
				<h4><spring:message code="expedient.alta.massiva.darrera"></spring:message>:</h4>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<table class="table table-bordered" role="grid" style="width: 100%;">
					<thead>
						<tr role="row" id="">
							<th style="width: 15%"><spring:message code="expedient.alta.massiva.darrera.files"></spring:message></th>
							<th style="width: 15%"><spring:message code="expedient.alta.massiva.darrera.tpc"></spring:message></th>
							<th style="width: 15%"><spring:message code="expedient.alta.massiva.darrera.processats"></spring:message></th>
							<th style="width: 15%"><spring:message code="expedient.alta.massiva.darrera.errors"></spring:message></th>
							<th style="width: 15%"><spring:message code="expedient.alta.massiva.darrera.dataInici"></spring:message></th>
							<th style="width: 15%"><spring:message code="expedient.alta.massiva.darrera.dataFi"></spring:message></th>
							<th style="width: 10%"><spring:message code="expedient.alta.massiva.darrera.usuari"></spring:message></th>
						</tr>
					</thead>
					<tbody>
						<tr role="row">
							<td class="text-right"><strong><span id="total">-</span></strong></td>
							<td class="text-right">							
								<div class="progress">
									<div id="tpcExecutatBar" class="progress-bar progress-bar-success"
										role="progressbar" style="width: 0%">
										<span><div id="tpcExecutat" class="value">-%</div></span>
									</div>
								</div>
							</td>
							<td class="text-right"><span id="processats">-</span></td>
							<td class="text-right"><span id="errors">-</span></td>
							<td><span id="tdDataInici">-</span></td>
							<td><span id="dataFi">-</span></td>
							<td><span id="usuari">-</span></td>
						</tr>
				</table>
			</div>
		</div>
		<div class="row">
			<div class="col-md-4">
				<spring:message code="expedient.alta.massiva.data.consulta"></spring:message>:
				<span id="data">-</span>
			</div>
			<div class="col-md-8">
				<center><span id="missatgeCapResultat" style="display:none;">(<spring:message code="expedient.alta.massiva.no.trobada"></spring:message>)</span></center>
			</div>
		</div>

		
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-primary right" name="altaMassiva" accio="altaMassiva" disabled="disabled"><i class="fa fa-file-o"></i> <spring:message code="expedient.alta.massiva.processar"></spring:message></button>
			<button class="btn btn-success right" name="refrescar"><i class="fa fa-refresh"></i> <spring:message code="comu.boto.refrescar"></spring:message></button>
			<button class="btn btn-success right" name="resultats"><i class="fa fa-download"></i> <spring:message code="expedient.alta.massiva.resultats"></spring:message></button>
			<button type="button" class="btn btn-default right" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
		</div>

	</form:form>
	
	<script type="text/javascript">
		// <![CDATA[

		var darreraExecucioMassiva = null;
		
		$(document).ready( function() {

			$('#expedientTipusId').change(function() {
				var expedientTipusId = $(this).val();
				if ($(this).val()) {
					$('button[name=altaMassiva]').removeAttr('disabled');
					$('button[name=refrescar]').removeAttr('disabled');
					$('button[name=resultats]').removeAttr('disabled');				
				} else {
					$('button[name=altaMassiva]').attr('disabled');
					$('button[name=refrescar]').attr('disabled', 'disabled');
					$('button[name=resultats]').attr('disabled', 'disabled');				
				}
				carregaDades();
			});
			$('#expedientTipusId').change();
			
			// Botó per recarregar les dades
			$("button[name=refrescar]").click(function(e) {
				webutilEsborrarAlertes();
				carregaDades();
				e.preventDefault();
			});
			
			// Botó per descarregar els resultats
			$("button[name=resultats]").click(function(e) {
				if (darreraExecucioMassiva) {
					$("button[name=resultats]", window.parent.document).attr('disabled', true).find('.fa-download').removeClass('fa-download').addClass('fa-refresh').addClass('fa-spin')
					var resultatsUrl = '<c:url value="/v3/expedient/altaMassiva"/>/' + darreraExecucioMassiva.id + '/resultat';
					webutilDownloadAndRefresh(resultatsUrl, e, resultatsCallbackFunction);
				} else {
					webutilAlertaError('<spring:message code="expedient.alta.massiva.resultats.error.darrera.execucio" />');
				}
			});
			
			// Enllaç al CSV d'exemple
			$('#arxiuCsvExemple').click(function(e) {
				webutilDownloadAndRefresh($(this).attr('href'), e);
			})
		}); 
		
		function resultatsCallbackFunction() {
			$("button[name=resultats]", window.parent.document).removeAttr('disabled').find('.fa-refresh').removeClass("fa-spin").removeClass('fa-refresh').addClass('fa-download');
		}
		
		function carregaDades() {
			
			// Consulta les dades
			var expedientTipusId = $('#expedientTipusId').val();

			if (expedientTipusId) {
				$("button[name=refrescar]", window.parent.document).attr('disabled', true).find('.fa-refresh').addClass("fa-spin");
				var url = '<c:url value="/v3/expedient/altaMassiva/dades"/>/' + expedientTipusId;
				$.ajax({
		            url : url,
		            type : 'GET',
		            dataType : 'json',
		            success : function(data) {
		                emplenaDades(data);
		            },
		            error: function (request, status, error) {
		            	// Mostra l'error
		            	webutilAlertaError(request.responseText);
		            },
		            complete: function() {
		            	// Treu els spinners
		    			$("button[name=refrescar]", window.parent.document).attr('disabled', false).find('.fa-refresh').removeClass("fa-spin");
		            }
		        });
			} else {
                emplenaDades(null);				
			}
		}
		
		function emplenaDades(data) {
			
			if (data) {
				// Carrega les dades de la cua i la data
				$("#data").html(data.data);
				
				// Carrega les dades a la taula
				if (data.execucioMassiva) {
					$('#total').html(data.execucioMassiva.total);
					var tpcExecutat = data.execucioMassiva.processat * 100 / data.execucioMassiva.total;
					$('#tpcExecutatBar').css('width', tpcExecutat + '%');
					$('#tpcExecutat').html(tpcExecutat + '%');
					$('#processats').html(data.execucioMassiva.processat);
					$('#errors').html(data.execucioMassiva.error);
					$("#tdDataInici").html(data.execucioMassiva.dataInici != null ? (moment(new Date(data.execucioMassiva.dataInici)).format("DD/MM/YYYY HH:mm:ss")) : "-");
					$("#dataFi").html(data.execucioMassiva.dataFi != null ? (moment(new Date(data.execucioMassiva.dataFi)).format("DD/MM/YYYY HH:mm:ss")) : "-");
					$('#usuari').html(data.execucioMassiva.usuari);				
				} else {
					$('#total').html('-');
					$('#tpcExecutatBar').css('width','0%');
					$('#tpcExecutat').html('-%');
					$('#processats').html('-');
					$('#errors').html('-');
					$('#tdDataInici').html('-');
					$('#dataFi').html('-');
					$('#usuari').html('-');
				}
				// Guarda l'execucio massiva per consultar els resultats 
				darreraExecucioMassiva = data.execucioMassiva
			} else {
				darreraExecucioMassiva = null;
			}
			
			// Adequa els controls segons les dades
			var expedientTipusId = $('#expedientTipusId').val();
			
			var btnAltaMassivaDisabled = !expedientTipusId || (darreraExecucioMassiva && !darreraExecucioMassiva.dataFi);
			var btnResultatsDisabled = !darreraExecucioMassiva || !darreraExecucioMassiva.dataFi;
			var btnRefrescarDisabled = !expedientTipusId;
			
			if (darreraExecucioMassiva) {
				$('#missatgeCapResultat').hide();
			} else {
				$('#missatgeCapResultat').show();
			}
			
			$("button[name=altaMassiva]", window.parent.document).attr('disabled', btnAltaMassivaDisabled);
			$("button[name=resultats]", window.parent.document).attr('disabled', btnResultatsDisabled);
			$("button[name=refrescar]", window.parent.document).attr('disabled', btnRefrescarDisabled);
		}
		// ]]>
	</script>	
</body>
</html>