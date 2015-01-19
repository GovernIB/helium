<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code='perfil.info.meu_perfil' /></title>
	<meta name="capsaleraTipus" content="llistat"/>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.ca.js"/>"></script>
	<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/helium.datatable.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script>
		$(document).ready(function() {
			$('select[name=entornCodi]').on('change', function () {
				$("select[name=expedientTipusId] option").each(function (index, option) {
		    		if (index > 0 && $("select[name=expedientTipusId] option").size() > 0)
						$(option).remove();
				});
				if ($(this).val()) {
					$.ajax({
					    url:'perfil/consulta/' + $(this).val(),
					    type:'GET',
					    dataType: 'json',
					    success: function(json) {
					        $.each(json, function(i, value) {
					        	$('<option>').text(value.nom).attr('value', value.id).insertAfter($("select[name=expedientTipusId] option:eq(" + i + ")"));
					        });
					        $('select[name=expedientTipusId]').change();
					    }
					});
				}
			});	
	
			$("select[name=expedientTipusDefecteId]").on('change', function () {
				if($('select[name=expedientTipusId]').val($(this).val())) {
					$('select[name=expedientTipusId]').select2("val", $(this).val());
					$('select[name=expedientTipusId]').change();
				} else {
					$('select[name=expedientTipusId]').select2("val", "");
				}
			});
			
			$('select[name=expedientTipusId]').on('change', function () {
				$("select[name=consultaId] option").each(function (index, option) {
		    		if (index > 0 && $("select[name=consultaId] option").size() > 0)
						$(option).remove();
				});
				if ($(this).val()) {
					if ($('select[name=expedientTipusDefecteId]').val() != $('select[name=expedientTipusId]').val())
						$('select[name=expedientTipusDefecteId]').select2("val", "");	
					$.ajax({
					    url:'perfil/consulta/' + $(this).val() + '/' + $('#entornCodi').val(),
					    type:'GET',
					    dataType: 'json',
					    success: function(json) {
					        $.each(json, function(i, value) {
					        	$('<option>').text(value.nom).attr('value', value.id).insertAfter($("select[name=consultaId] option:eq(" + i + ")"));
					        });
					    }
					});
				}
			});	
		});
	</script>
	<style type="text/css">
		.form-horizontal .control-label.col-xs-4 {
			width: 22.333%
		}
		
		.form-horizontal {
			padding-bottom: 30px;
		}
		
		p.help-block {
			padding-left: calc(22.333% + 7px);
		}
		
		div.help-block {
			padding-left: calc(22.333% + 37px);
		}
		
		.label-titol {
			background-color: #fefefe; border: 1px solid #e3e3e3; border-radius: 4px; box-shadow: 0 1px 1px rgba(0, 0, 0, 0.05) inset; margin-bottom: 20px; min-height: 20px; padding: 19px;
		}
		
		.label-titol .control-label {
			padding-bottom: 20px;
		}
		
		.label-titol .form-group .control-label{
			padding-bottom: 0px;
		}
		
		label.checkbox{
			font-weight: 400;
		}
</style>
</head>
<body>
	<c:set var="esReadOnly" value="${globalProperties['app.persones.readonly'] == 'true'}"/>
	<c:set var="tipusText"><c:choose><c:when test="${not esReadOnly}">text</c:when><c:otherwise>static</c:otherwise></c:choose></c:set>
	<c:set var="tipusSelect"><c:choose><c:when test="${not esReadOnly}">select</c:when><c:otherwise>static</c:otherwise></c:choose></c:set>
	<h3 class="capsalera"><spring:message code='perfil.info.meu_perfil' /></h3>
	
	<div class="page-header">
		<h4><spring:message code='perfil.info.dades_perso' /></h4>
	</div>
	<div class="well well-white">
		<div class="row-fluid">
			<form:form action="" cssClass="form-horizontal form-tasca" commandName="personaUsuariCommand" method="post">
				<div class="control-group fila_reducida">
					<hel:inputText disabled="${esReadOnly}" name="nom" textKey="comuns.nom" placeholderKey="comuns.nom" inline="false"/>
				</div>
				<div class="control-group fila_reducida">
					<hel:inputText disabled="${esReadOnly}" name="llinatge1" textKey="persona.form.primer_llin" placeholderKey="persona.form.primer_llin" inline="false"/>
				</div>
				<div class="control-group fila_reducida">
					<hel:inputText disabled="${esReadOnly}" name="llinatge2" textKey="persona.form.segon_llin" placeholderKey="persona.form.segon_llin" inline="false"/>
				</div>
				<div class="control-group fila_reducida">
					<hel:inputText disabled="${esReadOnly}" name="dni" textKey="persona.form.dni" placeholderKey="persona.form.dni" inline="false"/>
				</div>
				<div class="control-group fila_reducida">
					<hel:inputText disabled="${esReadOnly}" name="email" textKey="persona.consulta.email" placeholderKey="persona.consulta.email" inline="false"/>
				</div>
				<div class="control-group fila_reducida">
					<hel:inputSelect disabled="${esReadOnly}" name="hombre" textKey="comuns.sexe" placeholderKey="comuns.sexe" optionItems="${sexes}" optionValueAttribute="codi" optionTextAttribute="valor" inline="false"/>
				</div>
				<div class="pull-right">
					<input <c:if test="${esReadOnly}">disabled="disabled"</c:if> class="btn btn-primary" type="submit" id="guardar" name="accio" value="Modificar" />
				</div>				
			</form:form>			
		</div>
	</div>
	<div class="page-header">
		<h4><spring:message code='perfil.info.preferencies' /></h4>
	</div>		
	<div class="well well-white">
		<div class="row-fluid">    
			<form:form action="" method="post" cssClass="formbox form-horizontal" commandName="personaUsuariCommand">
				<div class="control-group">
					<div class="label-titol">
						<label class="control-label" for="lc_time"><spring:message code="perfil.usuari.nota.generales"/></label>
						<!-- 
						<div class="control-group">
							<hel:inputSelect name="expedientTipusId" text="Tipo de expediente" placeholder="Tipo de expediente" optionItems="${expedientTipus}" optionValueAttribute="id" optionTextAttribute="nom" inline="false"/>
							<p class="help-block">
								<span class="label label-info">Nota</span> Tipo de expediente per defecte.
							</p>
						</div>
						 -->
						<div class="control-group">
							<hel:inputSelect name="listado" text="Listado inicial" placeholder="Listado inicial" optionItems="${pantallas}" optionValueAttribute="codi" optionTextAttribute="valor" inline="false"/>
							<p class="help-block">
								<span class="label label-info"><spring:message code="perfil.usuari.nota"/></span> <spring:message code="perfil.usuari.nota.pantalla"/>
							</p>
						</div>
						<div class="control-group">
							<hel:inputSelect name="cabeceraReducida" text="Capcalera" placeholder="Capcalera" optionItems="${cabeceras}" optionValueAttribute="codi" optionTextAttribute="valor" inline="false"/>
							<p class="help-block">
								<span class="label label-info"><spring:message code="perfil.usuari.nota"/></span> <spring:message code="perfil.usuari.nota.cabecera"/>
							</p>
						</div>
						<div class="control-group">
							<hel:inputSelect name="entornCodi" text="Entorno" placeholder="Entorno" optionItems="${entorns}" optionValueAttribute="codi" optionTextAttribute="nom" inline="false"/>
							<p class="help-block">
								<span class="label label-info"><spring:message code="perfil.usuari.nota"/></span> <spring:message code="perfil.usuari.nota.entorn"/>
							</p>
						</div>
						<div class="control-group">
							<hel:inputSelect name="numElementosPagina" text="Elementos por página" placeholder="Elementos por página" optionItems="${numElementsPagina}" optionValueAttribute="codi" optionTextAttribute="valor" inline="false"/>
							<p class="help-block">
								<span class="label label-info"><spring:message code="perfil.usuari.nota"/></span> <spring:message code="perfil.usuari.nota.numelements"/>
							</p>
						</div>
						<div class="control-group">
							<hel:inputSelect name="expedientTipusDefecteId" text="Tipo de expediente" placeholder="Tipo de expediente" optionItems="${expedientTipus}" optionValueAttribute="id" optionTextAttribute="nom" inline="false"/>
							<p class="help-block">
								<span class="label label-info"><spring:message code="perfil.usuari.nota"/></span> <spring:message code="perfil.usuari.nota.tipusexpedientdefecte"/>
							</p>
						</div>
						</div>
						</div>
						<div class="control-group">
							<div class="label-titol">
								<label class="control-label" for="lc_time"><spring:message code="perfil.usuari.consulta"/></label>
								<hel:inputSelect name="expedientTipusId" text="Tipo de expediente" placeholder="Tipo de expediente" optionItems="${expedientTipusConConsultas}" optionValueAttribute="id" optionTextAttribute="nom" inline="false"/>
								<hel:inputSelect name="consultaId" text="Consulta por tipo" placeholder="Consulta por tipo" optionItems="${consultes}" optionValueAttribute="id" optionTextAttribute="nom" inline="false"/>
								<p class="help-block">
									<span class="label label-info"><spring:message code="perfil.usuari.nota"/></span> <spring:message code="perfil.usuari.nota.consulta"/>
								</p>
							</div>
						</div>
				<div class="control-group">
					<div class="label-titol">
						<label class="control-label"><spring:message code="perfil.usuari.nota.filtres"/></label>
						<div class="form-group">
							<div class="help-block">
								<hel:inputCheckbox name="filtroExpedientesActivos" text="Llistat d'expedients filtra per defecte o no expedients amb tasques actives per part de l'usuari." inline="true"/>
							</div>
						</div>
					</div>
				</div>
				<div class="pull-right">
					<input class="btn btn-primary" type="submit" id="guardar" name="accio" value="Guardar" />
				</div>
			</form:form>
		</div>
	</div>
</body>
</html>
