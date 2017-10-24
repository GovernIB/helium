<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="expedient.document.afegir"/></title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	
	<style type="text/css">
		.btn-file {position: relative; overflow: hidden;}
		.btn-file input[type=file] {position: absolute; top: 0; right: 0; min-width: 100%; min-height: 100%; font-size: 100px; text-align: right; filter: alpha(opacity = 0); opacity: 0; outline: none; background: white; cursor: inherit; display: block;}
		.col-xs-4 {width: 20%;}		
		.col-xs-8 {width: 80%;}
		#s2id_estatId {width: 100% !important;}
		
		.nav-tabs li.disabled a {
		    pointer-events: none;
		}
		.tab-pane {
			min-height: 300px;
			margin-top: 25px;
		}
	</style>
</head>
<body>		
	<form:form cssClass="form-horizontal form-tasca" action="new" enctype="multipart/form-data" method="post" commandName="documentExpedientCommand">
		<div class="inlineLabels">
			<input type="hidden" id="processInstanceId" name="processInstanceId" value="${processInstanceId}"/>
			
			<div id="selDocument" class="form-group">
				<label class="control-label col-xs-5 obligatori" for="documentCodi">Document</label>
				<div id="elDocument_controls" class="col-xs-7">
					<form:select path="documentCodi" cssClass="form-control" id="documentCodi">
						<optgroup label="<spring:message code='expedient.document.adjuntar.document'/>">
							<option value="##adjuntar_arxiu##"><spring:message code="expedient.document.adjuntar.document"/></option>
						</optgroup>
						<optgroup label="<spring:message code='expedient.nou.document.existent'/>">
							<c:forEach var="opt" items="${documentsNoUtilitzats}">
								<form:option value="${opt.codi}">${opt.documentNom}</form:option>
							</c:forEach>
						</optgroup>
					</form:select>
					<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="documentCodi"/></p></c:if>
				</div>
			</div>
			
			<hr>
			
			<c:if test="${metadades}">
				<div>
					<ul class="nav nav-tabs" role="tablist">
						<li id="pipella-general" class="active"><a href="#dades-generals" role="tab" data-toggle="tab"><spring:message code="expedient.document.pipella.general"/></a></li>
						<li id="pipella-nti"><a href="#dades-nti" role="tab" data-toggle="tab"><spring:message code="expedient.document.pipella.nti"/></a></li>
					</ul>
				</div>
			</c:if>
			
			<div class="tab-content">
				<div id="dades-generals" class="tab-pane in active">	

					<div id="titolArxiu">
						<hel:inputText required="true" name="nom" textKey="expedient.document.titol" placeholderKey="expedient.document.titol"/>
					</div>
					<div class="form-group">
						<label class="control-label col-xs-4 obligatori" for="nom"><spring:message code='expedient.document.arxiu' /></label>
				        <div class="col-xs-8 arxiu">
				            <div class="input-group">
				                <form:input path="nomArxiu" readonly="readonly" cssClass="form-control" />
				                <span class="input-group-btn">
				                    <span class="btn btn-default btn-file">
				                        <spring:message code='expedient.document.arxiu' />… <input type="file" name="arxiu">
				                    </span>
				                </span>
				            </div>
						</div>
					</div>
					<hel:inputDate required="true" name="data" textKey="expedient.document.data" placeholder="dd/mm/aaaa"/>
					
				</div>
				<div id="dades-nti" class="tab-pane">
					<hel:inputSelect required="true" emptyOption="true" name="ntiTipusDocumental" textKey="document.metadades.nti.tipus.documental" optionItems="${ntiTipusDocumental}" optionValueAttribute="codi" optionTextAttribute="valor"/>
					<hel:inputSelect name="ntiTipoFirma" textKey="document.metadades.nti.tipus.firma" required="false" emptyOption="true" optionItems="${ntiTipoFirma}" optionValueAttribute="codi" optionTextAttribute="valor"/>
					<hel:inputText name="ntiValorCsv" textKey="document.metadades.nti.valor.csv" disabled="${documentExpedientCommand.ntiTipoFirma != 'CSV'}"/>
					<hel:inputText name="ntiDefGenCsv" textKey="document.metadades.nti.definicio.generacio.csv" disabled="${documentExpedientCommand.ntiTipoFirma != 'CSV'}"/>
					<hel:inputText name="ntiIdOrigen" textKey="document.metadades.nti.identificador.doc.origen"/>
				</div>
			</div>
						
		</div>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
			<button class="btn btn-primary right" type="submit" name="accio" value="document_adjuntar">
				<spring:message code='comuns.afegir' />
			</button>
		</div>
	</form:form>
	
	<script type="text/javascript">
		// <![CDATA[
		$(document).on('change', '.btn-file :file', function() {
			var input = $(this),
			numFiles = input.get(0).files ? input.get(0).files.length : 1,
			label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
			input.trigger('fileselect', [numFiles, label]);
		});
		
		$(document).ready( function() {
			$('.btn-file :file').on('fileselect', function(event, numFiles, label) {
				var input = $(this).parents('.input-group').find(':text'),
				log = numFiles > 1 ? numFiles + ' files selected' : label;
				if( input.length ) {
					input.val(log);
				} else {
					if( log )
						alert(log);
				}
			});
			
			$('#nomArxiu').on('click', function() {
				$('input[name=arxiu]').click();
			});
			
			$('#documentCodi').on('click', function() {
				var valor = $(this).val();
				if (valor == '##adjuntar_arxiu##') {
					$("#titolArxiu").show();
					$('#pipella-nti').removeClass('disabled');
				} else {
					$("#titolArxiu").hide();
					$('#pipella-nti').addClass('disabled');
					$('#pipella-general a').click();
				}
			}).click();
			
			$('#ntiTipoFirma').on("change", function(e) {
				var data = $("#ntiTipoFirma option:selected").val();
				if(data == 'CSV') {
					$('#ntiValorCsv').prop('disabled', false);
					$('#ntiDefGenCsv').prop('disabled', false);
				} else {
					$('#ntiValorCsv').prop('disabled', true);
					$('#ntiValorCsv').prop('value', null);
					$('#ntiDefGenCsv').prop('disabled', true);
					$('#ntiDefGenCsv').prop('value', null);
				}
			});
			
			// Errors en les pipelles
			$('.tab-pane').each(function() {
				if ($('.has-error', this).length > 0) 
					$('a[href="#' + $(this).attr('id') + '"]').append(' <span class="fa fa-exclamation-triangle text-danger"/>');
			});
		}); 
		// ]]>
	</script>
</body>
</html>