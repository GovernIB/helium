<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="numColumnes" value="${3}"/>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title>${tasca.titol}</title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery.keyfilter.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/helium.tramitar.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/helium.modal.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
	<style>
		.alert {margin-top: 20px;}
		input, select, textarea {
			width: 100%;
		}
		label {
			font-weight: normal;
		}
		.form-group {
			padding-right: 	15px;
			margin-left: 	0px !important;
			margin-bottom:	0px;
		}
		.controls {
			padding-right: 0 !important;
		}
		.form-group input, .form-group textarea {
			width: 100%;
		}
		.input-group-multiple {
			padding-left: 15px;
			padding-right: 0px;}
		.form-group li > .select2-container {
			width: 100%;
			padding-right: 20px;
		}
		.form-group .select2-container {
			width: calc(100% + 14px);
		}
		.pad-left-col-xs-3 {left: 25%;}
		.form-group.condensed {
			margin-bottom: 0px;
		}
		.form-group.registre .btn_afegir{
 			margin-top: 10px; 
		}
		.registre table .colEliminarFila {
			width: 1px;
		}
		.registre table .opciones {
			text-align: center;
		}
		p.help-block {
			padding-top: 0;	
			margin-top: 4px !important;
			margin-bottom:	0px;
		}
		.clear {
			clear: both;
		}
		.clearForm {
			clear: both;
			margin-bottom: 10px;
			margin-top: 15px;
			border-bottom: solid 1px #DADADA;
		}
		.input-append {
			width: calc(100% - 27px);
		}
		.eliminarFila {
			background-color: #FFF;
			border: solid 1px rgb(204, 204, 204);
			border-radius: 4px;
			padding: 9px 6px;
/* 			margin-top: 3px; */
		}
		.eliminarFila:hover {
			color: #333;
			background-color: #e6e6e6;
			border-color: #adadad;
		}
		.btn_eliminar {
			background-color: #FFF;
			border: solid 1px rgb(204, 204, 204);
			border-radius: 4px;
			padding: 9px 6px;
/* 			margin-top: 3px; */
		}
		.table {
			margin-bottom: 0px;
		}
		.col-xs-9 .checkbox {
			width: auto;
		}
		.form_extern {
			padding-bottom: 15px;
			width: 100%;
			text-align: right;
			margin-right: -15px;
		}
		#tabnav .glyphicon {
			padding-right: 10px;
		}
		div.tab-content {
			width: calc(100% - 15px);
			padding-bottom: 15px;
		}
		.multiple input, .multiple textarea, .multiple_camp .input-group, .multiple_camp .inputcheck {
			float: left;
			width: calc(100% - 29px);
		}
		.multiple_camp div.suggest, .multiple_camp div.seleccio {
			float: left;
			width: calc(100% - 29px) !important;
		}
		.registre .multiple input, .registre .multiple textarea, .registre .multiple_camp .input-group, .registre .multiple_camp .inputcheck {
			float: left;
			width: 100% !important;
		}
		.registre .multiple_camp div.suggest, .registre .multiple_camp div.seleccio {
			float: left;
			width: 100% !important;
		}
		.registre .multiple input.checkbox {
			width: auto !important;
		}
		.multiple .termgrup {
			float: left;
			width: calc(100% - 14px);
		}
		.termgrup input, .termgrup select {
			float: left;
 			width: calc(100% - 65px); 
		}
		.tercpre {
			float: left;
			width: 32%;
 			padding-left: 0px !important;
 			padding-right: 8px !important;
		}
		.tercmig {
			float: left;
			width: 32%;
 			padding-left: 4px !important; 
 			padding-right: 4px !important; 
		}
		.tercpost {
			float: left;
			width: 36%;
			padding-left: 8px !important; 
 			padding-right: 0px !important; 
		}
		.label-term {
			float: left;
			width: 60px;
			text-align: left !important;
			margin-right: 5px;
			font-weight: normal !important;
		}
		.multiple_camp .btn_eliminar {
			float: left;
			margin-left: 4px;
		}
		.form-group.multiple_camp {
			margin-bottom: 6px;		
		}
		.btn_date {
			cursor: pointer;
		}
		.has-error .form-control {
			background-color: #ffefe !important;
		}
		#contingut-alertes, .tauladades {
			padding-right: 15px;
		}
/* 		input, textarea, .select2-choice { */
/* 			background-color: #FFFCF0 !important; */
/* 		} */
/* 		.select2-arrow { */
/* 			background-color: #FFFFFF !important; */
/* 		} */
/* 		.registre table { */
/* 			border: solid 0px; */
			
/* 		} */
/* 		.registre tr td:last-child, .registre tr th:last-child { */
/* 			border: solid 0px; */
/* 			padding-right: 0px; */
/* 		} */
		.registre th {
			border-bottom: solid 1px #CACACA !important;
			background: rgba(221,221,221,1);
			background: -moz-linear-gradient(top, rgba(221,221,221,1) 0%, rgba(245,245,245,1) 100%);
			background: -webkit-gradient(left top, left bottom, color-stop(0%, rgba(221,221,221,1)), color-stop(100%, rgba(245,245,245,1)));
			background: -webkit-linear-gradient(top, rgba(221,221,221,1) 0%, rgba(245,245,245,1) 100%);
			background: -o-linear-gradient(top, rgba(221,221,221,1) 0%, rgba(245,245,245,1) 100%);
			background: -ms-linear-gradient(top, rgba(221,221,221,1) 0%, rgba(245,245,245,1) 100%);
			background: linear-gradient(to bottom, rgba(221,221,221,1) 0%, rgba(245,245,245,1) 100%);
			filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#dddddd', endColorstr='#f5f5f5', GradientType=0 );
		}
		.contingut-carregant {
			padding-top: 50px;
	    	text-align: center;
	    }
	</style>
</head>
<body>
	<c:if test="${not empty command.numTascaMassiva && command.numTascaMassiva > 0}">
		<c:import url="import/tasquesMassiva.jsp">
			<c:param name="count" value="${command.numTascaMassiva}"/>
		</c:import>
	</c:if>	
	<c:if test="${not empty dadesNomesLectura}">
		<c:import url="import/expedientDadesTaula.jsp">
			<c:param name="dadesAttribute" value="dadesNomesLectura"/>
			<c:param name="titol" value="Dades de referència"/>
			<c:param name="numColumnes" value="${numColumnes}"/>
			<c:param name="count" value="${fn:length(dadesNomesLectura)}"/>
			<c:param name="desplegat" value="${false}"/>
			<c:param name="desplegadorClass" value="agrupacio-desplegador"/>
		</c:import>
	</c:if>		
	<c:set var="pipellaIndex" value="${1}"/>
	<ul id="tabnav" class="nav nav-tabs">
		<c:if test="${not empty dades}">
			<li class="active"><a href="#dades" data-toggle="tab"><c:if test="${not tasca.validada}"><span class="glyphicon glyphicon-warning-sign"> </span></c:if>${pipellaIndex}. Dades</a></li>
			<c:set var="pipellaIndex" value="${pipellaIndex + 1}"/>
		</c:if>
		<c:if test="${hasDocuments == true}">
			<li class="<c:if test="${empty dades}">active</c:if>"><a id="tab_documents" href="#documents" data-toggle="tab">${pipellaIndex}. Documents</a></li>
			<c:set var="pipellaIndex" value="${pipellaIndex + 1}"/>
		</c:if>
		<c:if test="${hasSignatures == true}">
			<li class="<c:if test="${empty dades and empty documents}">active</c:if>"><a id="tab_signatures" href="#signatures" data-toggle="tab">${pipellaIndex}. Signatures</a></li>
			<c:set var="pipellaIndex" value="${pipellaIndex + 1}"/>
		</c:if>
	</ul>
	<script type="text/javascript">
		$('#tabnav li').on('click', function() {
			var tab = $(this).find('a').attr('href');
			var tamMin = 400;
			var height = Math.max(tamMin, $(window.frameElement).contents().find("html").find(tab).outerHeight());
			if (height < tamMin) {
				modalAdjustHeight(window.frameElement, height);
			}
		});
		$(document).ready(function() {
			$('#tab_documents').click( function() {
				$('#documents .dades').hide();
				$('#documents .contingut-carregant').show();
				$('#documents .dades').load('<c:url value="/nodeco/v3/expedient/${expedientId}/tasca/${tasca.id}/documents"/>', function(responseTxt,statusTxt,xhr){
					if(statusTxt=="success") {
						$('#documents .contingut-carregant').hide();
						$('#documents .dades').show();
					} else if(statusTxt=="error") {
			      		alert("Error: "+xhr.status+": "+xhr.statusText);
					}
			    });
			});
			$('#tab_signatures').click( function() {
				$('#signatures .dades').hide();
				$('#signatures .contingut-carregant').show();
				$('#signatures .dades').load('<c:url value="/nodeco/v3/expedient/${expedientId}/tasca/${tasca.id}/signatures"/>', function(responseTxt,statusTxt,xhr){
					if(statusTxt=="success") {
						$('#signatures .contingut-carregant').hide();
						$('#signatures .dades').show();
					} else if(statusTxt=="error") {
			      		alert("Error: "+xhr.status+": "+xhr.statusText);
					}
			    });
			});
		});
	</script>
	<div class="tab-content">
		<c:if test="${not empty dades}">
			<div class="tab-pane active" id="dades">
				<c:if test="${not tasca.validada}">
					<div class="alert alert-warning">
						<button class="close" data-dismiss="alert">×</button>
						<c:choose>
							<c:when test="${empty tasca.formExtern}">
								<p><spring:message code='tasca.form.no_validades' /></p>
							</c:when>
							<c:otherwise>
								<p><spring:message code='tasca.form.compl_form' /></p>
							</c:otherwise>
						</c:choose>
					</div>
				</c:if>
				
				<div class="well">					
					<c:if test="${not empty tasca.formExtern}">	
						<script type="text/javascript" src="<c:url value="/dwr/interface/formulariExternDwrService.js"/>"></script>
						<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
						<script type="text/javascript">
						// <![CDATA[
							function clickFormExtern(form) {
								formulariExternDwrService.dadesIniciFormulari(
										form.id.value,
										{
											callback: function(retval) {
												if (retval) {
													$("#linkClickFormExtern").attr('href', '<c:url value='../../../../../v3/expedient/formExtern'/>?width='+retval[1]+'&height='+retval[2]+'&url='+retval[0]).click();
												} else {
													alert("<spring:message code='tasca.form.error_ini' />");
												}
											},
											async: false
										});
								return false;
							}
						// ]]>
						</script>
						<div class="form-group">
							<form id="formExtern" action="formExtern" class="form-horizontal form-tasca" onclick="return clickFormExtern(this)">
								<input type="hidden" name="id" value="${tasca.id}"/>
								<div id="modal-botons-form-extern" class="pull-right form_extern">
									<button type="submit" id="btn_formextern" name="accio" value="formextern" class="btn btn-default"><span class="fa fa-pencil-square-o"></span>&nbsp;<spring:message code='tasca.form.obrir_form' /></button>
								</div>								
								<a 	id="linkClickFormExtern" data-rdt-link-modal="true" data-rdt-link-modal-min-height="400" data-rdt-link-callback="recargarPanel(this);" href="#" class="hide"></a>
										
								<script type="text/javascript">
									// <![CDATA[
										$('#linkClickFormExtern').heliumEvalLink({
											refrescarAlertes: true,
											refrescarPagina: false,
											alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>"
										});

										function recargarPanel (tag, correcte) {
											if (correcte) {
												location.reload();
											}
										}
									//]]>
								</script>
							</form>
						</div>
					</c:if>
					<form:form onsubmit="return confirmar(this)" action="" cssClass="form-horizontal form-tasca" method="post" commandName="command">
						<input type="hidden" id="tascaId" name="tascaId" value="${tasca.id}">
						<form:hidden path="inici"/>
						<form:hidden path="correu"/>
						<form:hidden path="numTascaMassiva"/>					
						
						<input type="hidden" id="__transicio__" name="__transicio__" value=""/>
						<c:forEach var="dada" items="${dades}" varStatus="varStatusMain">
							<c:set var="inline" value="${false}"/>
							<c:set var="isRegistre" value="${false}"/>
							<c:set var="isMultiple" value="${false}"/>
							<c:choose>
								<c:when test="${dada.campTipus != 'REGISTRE'}">
									<c:choose>
										<c:when test="${dada.campMultiple}">
											<c:set var="campErrorsMultiple"><form:errors path="${dada.varCodi}"/></c:set>
											<div class="multiple<c:if test="${not empty campErrorsMultiple}"> has-error</c:if>">	
												<label for="${dada.varCodi}" class="control-label col-xs-3<c:if test="${dada.required}"> obligatori</c:if>">${dada.campEtiqueta}</label>
												<c:forEach var="membre" items="${command[dada.varCodi]}" varStatus="varStatusCab">
													<c:set var="inline" value="${true}"/>
													<c:set var="campCodi" value="${dada.varCodi}[${varStatusCab.index}]"/>
													<c:set var="campNom" value="${dada.varCodi}"/>
													<c:set var="campIndex" value="${varStatusCab.index}"/>
													<div class="col-xs-9 input-group-multiple <c:if test="${varStatusCab.index != 0}">pad-left-col-xs-3</c:if>">
														<c:set var="isMultiple" value="${true}"/>
														<%@ include file="campsTasca.jsp" %>
														<c:set var="isMultiple" value="${false}"/>
													</div>
												</c:forEach>
												<c:if test="${empty dada.multipleDades}">
													Buit!!
													<c:set var="inline" value="${true}"/>
													<c:set var="campCodi" value="${dada.varCodi}[0]"/>
													<c:set var="campNom" value="${dada.varCodi}"/>
													<c:set var="campIndex" value="0"/>
													<div class="col-xs-9 input-group-multiple">
														<c:set var="isMultiple" value="${true}"/>
														<%@ include file="campsTasca.jsp" %>
														<c:set var="isMultiple" value="${false}"/>
													</div>
												</c:if>
												<c:if test="${!dada.readOnly && !tasca.validada}">
													<div class="form-group">
														<div class="col-xs-9 pad-left-col-xs-3">
															<c:if test="${not empty dada.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p></c:if>
															<button id="button_add_var_mult_${campCodi}" type="button" class="btn btn-default pull-left btn_afegir btn_multiple"><spring:message code='comuns.afegir' /></button>
															<div class="clear"></div>
															<c:if test="${not empty campErrorsMultiple}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${dada.varCodi}"/></p></c:if>
														</div>
													</div>
												</c:if>
											</div>
										</c:when>
										<c:otherwise>
											<c:set var="campCodi" value="${dada.varCodi}"/>
											<c:set var="campNom" value="${dada.varCodi}"/>
											<%@ include file="campsTasca.jsp" %>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<%@ include file="campsTascaRegistre.jsp" %>
								</c:otherwise>
							</c:choose>
							<c:if test="${not varStatusMain.last}"><div class="clearForm"></div></c:if>
						</c:forEach>
						<div id="guardarValidarTarea">
							<%@ include file="campsTascaBotons.jsp" %>
						</div>
					</form:form>
				</div>
			</div>
		</c:if>
		<c:if test="${hasDocuments == true}">
			<div class="tab-pane" id="documents">
				<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				<div class="dades"></div>
			</div>
		</c:if>
		<c:if test="${hasSignatures == true}">
			<div class="tab-pane" id="signatures">
				<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				<%@ include file="expedientTascaTramitacioSignar.jsp" %>
			</div>
		</c:if>
<!-- 		<div id="finalizarTarea"> -->
<%-- 			<%@ include file="campsTascaTramitacioTasca.jsp" %> --%>
<!-- 		</div> -->
	</div>
</body>
</html>
