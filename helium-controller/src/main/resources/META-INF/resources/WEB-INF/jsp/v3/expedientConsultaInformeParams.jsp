<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
	<head>
		<title><spring:message code="expedient.consulta.params.modal.titol"/></title>	
		<meta name="decorator" content="senseCapNiPeus"/>
		<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
		<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
		<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
		<script src="<c:url value="/js/select2.min.js"/>"></script>
		<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
		<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
		
		<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
		<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
		<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
		
		<style>
			.select2-container-fix {
				padding-left: 15px !important;
				padding-right: 	0px !important;    
				margin-right: -10px;
			}
			.select2-container-fix .form-group{
				padding-left: 0px !important;
				padding-right: 	0px !important;
				margin-right: 	0px !important;
			}
			.form-group {
				padding-right: 	0px;
				margin-left: 	-15px !important;
				margin-right: 	15px !important;
				margin-bottom:	15px;
			}
			.col-xs-9 .form-group {margin-bottom:0px;}
			.controls {
				padding-right: 0 !important;
			}
			.form-group input, .form-group textarea {
				width: 100%;
			}
			.form-group.registre .btn_afegir{
	 			margin-top: 10px; 
			}
			p.help-block {
				padding-top: 0;	
				margin-top: 4px !important;
			}
			.clear {
				clear: both;
			}
			.clearForm {
				clear: both;
				margin-bottom: 10px;
				border-bottom: solid 1px #EAEAEA;
			}
			/*#tabnav .glyphicon {padding-right: 10px;}*/
		</style>
	</head>
	<body>		
		<form:form id="formulariParametres" method="post" cssClass="well_mod form-horizontal form-tasca" modelAttribute="expedientInformeParametrosCommand">
	 		<div class="control-group fila_reducida">
				<c:forEach var="camp" items="${campsInformeParams}">
					<c:set var="campErrors"><form:errors path="${camp.varCodi}"/></c:set>
					<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
						<label for="${camp.varCodi}" class="control-label col-xs-3<c:if test="${camp.required}"> obligatori</c:if>">${camp.campEtiqueta}</label>
						<div class="controls col-xs-9">
							<c:set var="required" value="${false}"/>
							<c:choose>
								<%-- STRING -------------------------------------------------------------------------------------%>
								<c:when test="${camp.campTipus == 'STRING'}">
									<form:input path="${camp.varCodi}" cssClass="form-control" id="${camp.varCodi}" data-required="${camp.required}"/>
								</c:when>
								
								<%-- INTEGER ------------------------------------------------------------------------------------%>
								<c:when test="${camp.campTipus == 'INTEGER'}">
									<form:input path="${camp.varCodi}" cssClass="form-control text-right enter" id="${camp.varCodi}" value="0" data-required="${camp.required}"/>
								</c:when>
					
								<%-- FLOAT --------------------------------------------------------------------------------------%>
								<c:when test="${camp.campTipus == 'FLOAT'}">
									<form:input path="${camp.varCodi}" cssClass="form-control text-right float" id="${camp.varCodi}" value="0.0" data-required="${camp.required}"/>
								</c:when>
					
								<%-- DATE ---------------------------------------------------------------------------------------%>		
								<c:when test="${camp.campTipus == 'DATE'}">
									<div class="input-group">
										<form:input path="${camp.varCodi}" id="${camp.varCodi}" cssClass="date form-control" placeholder="dd/mm/aaaaa" data-required="${camp.required}"/>
										<span class="input-group-addon btn_date"><span class="fa fa-calendar"></span></span>
									</div>
								</c:when>
								<%-- BOOLEAN ---------------------------------------------------------------------------------------%>
								<c:when test="${camp.campTipus == 'BOOLEAN'}">
									<div class="select2-container-fix">		
										<hel:inputSelect inline="true" name="${camp.varCodi}" placeholder="${camp.campEtiqueta}" optionItems="${valorsBoolea}" optionValueAttribute="codi" optionTextAttribute="valor"/>
									</div>
								</c:when>
							</c:choose>
						</div>
					</div>
				</c:forEach>
			</div>
			<div id="modal-botons" class="well">
				<button id="cancelarInformeBtn" type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
				<button type="submit" class="btn btn-primary"><span class="icon-download-alt"></span>&nbsp;<spring:message code="expedient.consulta.informe"/></button>					
			</div>

			<div id="informeDescarrega" style="visibility:hidden;">
				<h4 class="modal-title"><spring:message code="expedient.informe.generacio"/></h4>
				<br/>
        		<p><spring:message code="expedient.informe.generacio.estat"/>: <label id="labelEstat">[estat]</label></p>
        		<div id="divError" style="display:none;"><span class="fa fa-exclamation-triangle text-danger"></span> <label id="labelError"/></div>
        		<div id="divInfo" style="display:none;">
        			<p><spring:message code="expedient.informe.generacio.consulta"/>: <label id="labelConsulta">-</label></p>
        			<p><spring:message code="expedient.informe.numero.expedients"/>: <label id="labelNumeroExpedients">0</label></p>
        		</div>
        		<div class="row">
	        		<div class="col-sm-1">
	        			<span id="spinnerIcon" style="visibility:hidden;" class="fa fa-spinner fa-spin fa-2x"/>
	        		</div>
	        		<div class="col-sm-1">
	        			<span id="dataTransferIcon" style="visibility:hidden;" class="fa fa-exchange fa-rotate-90 fa-2x"/>
	        		</div>
	        		<div class="col-sm-1">
	        			<button id="generacioCancelarButton" title="<spring:message code='expedient.informe.generacio.cancellar'/>" class="btn btn-link" style="visibility:hidden;"><span class="fa fa-times fa-2x"/></button>
	        		</div>
        		</div>
			</div>
			
		</form:form>
		<script>
			// Variable on es guarda la informació de la generació en curs
			var informe = null;
			var interval = null;

			$(document).ready(function(){
				
				$('#formulariParametres').submit(function(e){
					
					$("#labelEstat").html(estats['INICIALITZANT']);
					$("#divError").hide()
					$("#divInfo").hide()
					$("#labelConsulta").html('-');
					$('#spinnerIcon').css('visibility', 'visible');
					$('#informeDescarrega').css('visibility', 'visible');

					$.ajax({
				           type: "POST",
				           data: $(this).serialize(),
				           success: function(data)
				           {
								informe = data;
							    actualitzarInfoDescarrega(informe);
								// Consulta periòdica
								clearInterval(interval);
								interval = setInterval(function(){ 
									if (informe == null || (["NO_TROBAT", "FINALITZAT", "CANCELLAT", "ERROR"].indexOf(informe.estat) >= 0))
										clearInterval(interval);
									else
										informe = getConsultaInfo();
									actualitzarInfoDescarrega(informe);
								}, 5000);
				           }
				    });
					e.preventDefault();
				});
				
				$('#generacioCancelarButton').click(function (e) {
					if (informe != null && (["NO_TROBAT", "INICIALITZANT", "GENERANT"].indexOf(informe.estat) >= 0)) {
					    if (confirm("<spring:message code='expedient.informe.generacio.cancellar.confirmacio'/>")) {
						    // Cancel·lar la generació de l'informe
						    informe = cancellarInforme();
						    actualitzarInfoDescarrega(informe);
					    }
					}
				    e.preventDefault();
				    return false;
				});
			});
			
			/** Mètode per consultar l'estat de la consulta */
			function getConsultaInfo() {
				var ret = null;
				$('#dataTransferIcon').css('visibility', 'visible');
				$.ajax({
					type: 'GET',
					url: '<c:url value="/v3/expedient/consulta/${consultaId}/informeAsync/info"/>',
					dataType: "json",
					traditional: true,
					async: false,
				  	data: {
				  	}
				})
					.done(function( data ) {
						ret = data;
					  })
					.fail(function(jqXHR, textStatus) {
					    console.log( "Error consultant la informació: " + textStatus );
						$("#labelError").html(textStatus);
						$("#divError").show();
					  })
					.always(function() {
						$('#dataTransferIcon').css('visibility', 'hidden');
					  });	
				return ret;
			}
			
			var estats = {};
			estats['NO_TROBAT'] = '<spring:message code="expedient.informe.estat.NO_TROBAT"/>';
			estats['INICIALITZANT'] = '<spring:message code="expedient.informe.estat.INICIALITZANT"/>';
			estats['GENERANT'] = '<spring:message code="expedient.informe.estat.GENERANT"/>';
			estats['FINALITZAT'] = '<spring:message code="expedient.informe.estat.FINALITZAT"/>';
			estats['CANCELLAT'] = '<spring:message code="expedient.informe.estat.CANCELLAT"/>';
			estats['ERROR'] = '<spring:message code="expedient.informe.estat.ERROR"/>';

			
			/** Actualitza visualment la modal de descàrrega segons la informació rebuda. */
			function actualitzarInfoDescarrega(info) {
				if (info == null || info.estat == 'NO_TROBAT') {
					informe = null;
					$('#informeDescarrega').css('visibility', 'hidden');
					$('#spinnerIcon').css('visibility', 'hidden');
					$('#generacioCancelarButton').css('visibility', 'hidden');
					$("#divError").hide();
				} else {
					$('#informeDescarrega').css('visibility', 'visible');
					informe = info;
					$("#labelEstat").html(estats[informe.estat]);
					switch (informe.estat) {
					case 'INICIALITZANT':
						$('#spinnerIcon').css('visibility', 'visible');
						$('#generacioCancelarButton').css('visibility', 'visible');
						break;
					case 'GENERANT':
						$('#spinnerIcon').css('visibility', 'visible');
						$('#labelConsulta').html(info.consulta);
						$('#labelNumeroExpedients').html(info.numeroRegistres);
						$('#generacioCancelarButton').css('visibility', 'visible');
						$('#divInfo').show();
						break;
					case 'FINALITZAT':
						$('#spinnerIcon').css('visibility', 'hidden');
						$('#generacioCancelarButton').css('visibility', 'hidden');
						// Descarrega el document
						window.location = '<c:url value="/v3/expedient/consulta/${consultaId}/informeAsync/exportar"/>';
						informe = null;
						$('#informeDescarrega').css('visibility', 'hidden');
						break;
					case 'CANCELLAT':
						$('#spinnerIcon').css('visibility', 'hidden');
						$('#generacioCancelarButton').css('visibility', 'hidden');
						break;
					case 'ERROR':
						$('#spinnerIcon').css('visibility', 'hidden');
						$('#generacioCancelarButton').css('visibility', 'hidden');
						$("#labelError").html(info.missatge);
						$("#divError").show();
						break;
					default:
						;
					}
				}
			}			
			
			function cancellarInforme() {
				var ret = null;
				if (interval != null)
					clearInterval(interval);
				informe = null;
				$('#dataTransferIcon').css('visibility', 'visible');
				$.ajax({
					type: 'GET',
					url: '<c:url value="/v3/expedient/consulta/${consultaId}/informeAsync/cancellar"/>',
					dataType: "json",
					traditional: true,
					async: false,
				  	data: {
				  	}
				})
					.done(function( data ) {
						ret = data;
						if (ret == null || (["NO_TROBAT", "FINALITZAT", "CANCELLAT", "ERROR"].indexOf(ret.estat) >= 0))
							clearInterval(interval)
					  })
					.fail(function(jqXHR, textStatus) {
					    console.log( "Error cancel·lant la generació: " + textStatus );
						$("#labelError").html(textStatus);
						$("#divError").show();
					  })
					.always(function() {
						$('#dataTransferIcon').css('visibility', 'hidden');
					  });	
				return ret;	
			}
		</script>
	</body>
</html>