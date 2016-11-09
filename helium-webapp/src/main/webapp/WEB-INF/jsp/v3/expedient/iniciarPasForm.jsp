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
	<title><spring:message code='expedient.iniciar.iniciar_expedient' />: ${expedientTipus.nom}</title>
	<meta name="capsaleraTipus" content="llistat"/>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<link href="<c:url value="/css/tascaForm.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">

	<script src="//code.jquery.com/ui/1.11.3/jquery-ui.js"></script>
	<link rel="stylesheet" href="//code.jquery.com/ui/1.11.3/themes/smoothness/jquery-ui.css">
	<script src="https://www.java.com/js/deployJava.js"></script>

	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>

	<c:if test="${not empty tasca.tascaFormExternCodi}">
		<script type="text/javascript" src="<c:url value="/dwr/interface/formulariExternDwrService.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	</c:if>
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>

	
	<style>
		.reproForm {
			margin: 2px;
		}
		.repro-group {
			margin-right: 0px !important;
			margin-bottom: 6px;
		}
		.repros {
			z-index: 1000;
		    float: right;
		    bottom: 5px;
		    position: fixed;
		    right: 15px;
		}	
		.repros ul.dropdown-menu {
			width: 250px;
		}
		li.flex {
    		display: flex;
		}
		li.flex > a {
			width: 100%;
		}	
		.borrarRepro {
			position: absolute;
		    right: 5px;
		    padding: 0px 5px;
		}
	</style>
</head>
<body>
	<c:if test="${not empty tasca.tascaFormExternCodi}">
		<div class="alert alert-warning">
			<p>
				<span class="fa fa-warning"></span>
				<spring:message code="tasca.form.compl_form"/>
				<a id="boto-formext" href="<c:url value="/v3/expedient/tasca/${expedientTipus.id}/${definicioProces.id}/${tasca.id}/formExtern"/>" class="btn btn-xs btn-default pull-right"><span class="fa fa-external-link"></span>&nbsp;<spring:message code='tasca.form.obrir_form' /></a>
			</p>
		</div>
		<%--script type="text/javascript">
			// <![CDATA[
				function recargarPanel(tag, correcte) {
					if (correcte) {
						alert('RECARREGAR');
						//location.reload();
					}
				}
			//]]>
		</script--%>
	</c:if>
	
	<c:url var="post_url" value="/modal/v3/expedient" />
	
	<form:form id="command" name="command" action="${post_url}/iniciarForm/${expedientTipus.id}/${definicioProces.id}" cssClass="form-horizontal form-tasca" method="post">
		<form:hidden path="id"/>
		<form:hidden path="entornId"/>
		<form:hidden path="expedientTipusId"/>
		<form:hidden path="definicioProcesId"/>

		<!-- 
		<spring:hasBindErrors name="command">
            <c:forEach items="${errors.allErrors}" var="error">
               - ${error} <br>
            </c:forEach>
		</spring:hasBindErrors>
		 -->
		
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
										<%@ include file="../campsTasca.jsp" %>
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
										<%@ include file="../campsTasca.jsp" %>
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
							<%@ include file="../campsTasca.jsp" %>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<%@ include file="../campsTascaRegistre.jsp" %>
				</c:otherwise>
			</c:choose>
			<c:if test="${not varStatusMain.last}"><div class="clearForm"></div></c:if>
		</c:forEach>
		<div style="min-height: 120px;">
		</div>
		
		<!-- REPROS -->
		<div class="btn-group repros dropup">
		  <button type="button" class="btn btn-info dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
		    <spring:message code='repro.texte.repros' /> <span class="caret"></span>
		  </button>
		  <ul class="dropdown-menu dropdown-menu-right">
			<li class="reproForm">
					<div class="form-group repro-group">
						<input id="nomRepro" name="nomRepro" type="text" class="form-control" placeholder="<spring:message code='repro.texte.nom' />">
					</div>
					<button id="guardarRepro" name="guardarRepro" value="guardar-repro" class="btn btn-primary" type="submit">
						<spring:message code='repro.texte.guardar' />
					</button>
			</li>
			<c:if test="${not empty repros}">
				<li role="separator" class="divider"></li>
			    <strong><li class="dropdown-header">---- <spring:message code='repro.texte.guardats' /> ----</li></strong>
			    <c:forEach var="repro" items="${repros}">
				    <li class="flex">
				    	<a id="repro-${repro.id}" href="<c:url value="/modal/v3/expedient/iniciarForm/"/>${expedientTipus.id}/${definicioProces.id}/fromRepro/${repro.id}">${repro.nom}</a>
				    	<button class="btn btn-danger borrarRepro" type="submit" data-reproid="${repro.id}"><i class="fa fa-trash-o" aria-hidden="true"></i></button>
				    </li>
			    </c:forEach>
		    </c:if>
		  </ul>
		</div>
		<!-- FI REPROS -->
		
		<div id="modal-botons">
		
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code='comuns.cancelar' /></button>
			<button type="submit" id="iniciar" name="accio" value="iniciar" class="botons-iniciar btn btn-primary">
				<spring:message code='comuns.iniciar' />
			</button>
			<script type="text/javascript">
			// <![CDATA[
				$('.dropdown-menu').find('.reproForm').click(function (e) {
					e.stopPropagation();
				});
			
				$('#guardarRepro').click(function(e) {
					var e = e || window.event;
					e.cancelBubble = true;
					if (e.stopPropagation) e.stopPropagation();
					if ($('#nomRepro').val() != '') {
						$('#command').attr('action','<c:url value="/modal/v3/repro/"/>${expedientTipus.id}/${definicioProces.id}/guardarRepro');
						return true;
					} else {
						return false;
					}
				});
	
				$('.borrarRepro').click(function(e) {
					var e = e || window.event;
					e.cancelBubble = true;
					if (e.stopPropagation) e.stopPropagation();
					var reproId = $(this).data('reproid');
					$('#command').attr('action','<c:url value="/modal/v3/repro/"/>${expedientTipus.id}/${definicioProces.id}/borrarRepro/' + reproId);
					return true;
				});
			
		        $(".botons-iniciar").click(function(e) {
					var e = e || window.event;
					e.cancelBubble = true;
					if (e.stopPropagation) e.stopPropagation();
					var accio = $(this).attr('value');
					if (accio.indexOf('cancel') == 0 || accio.indexOf('guardar') == 0) {
						return true;
					}
					$("table").each(function(){
						if ($('#command').hasClass("hide")) {
							$('#command').remove();
						}
					});
					<c:choose>
						<c:when test="${not ((expedientTipus.teNumero and expedientTipus.demanaNumero) or (expedientTipus.teTitol and expedientTipus.demanaTitol))}">
							return confirm("<spring:message code='expedient.iniciar.confirm_iniciar' />");
						</c:when>
						<c:otherwise>
							return true;
						</c:otherwise>
					</c:choose>
				});
			// ]]>
			</script>
		</div>
	</form:form>
</body>
</html>