<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
<head>
	<title><spring:message code='perfil.info.meu_perfil' /></title>
	<meta name="capsaleraTipus" content="llistat"/>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.ca.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/helium.datatable.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
<script>
	$(document).ready(function() {
		$("#filtroExpedientesActivosCheck").click(function() {
			$("input[name=filtroExpedientesActivos]").val($("#filtroExpedientesActivosCheck").attr("checked") == "checked");
		});

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

		$('select[name=expedientTipusId]').on('change', function () {
			$("select[name=consultaId] option").each(function (index, option) {
	    		if (index > 0 && $("select[name=consultaId] option").size() > 0)
					$(option).remove();
			});
			if ($(this).val()) {
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
					<c:set var="campPath" value="nom"/>
					<label class="control-label" data-required="true" for="${campPath}"><spring:message code='comuns.nom' /></label>
					<div class="controls ">
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
							<spring:bind path="${campPath}">
								<input <c:if test="${tipusText == 'static'}">readonly="readonly"</c:if> type="text" id="${campPath}" name="${campPath}" placeholder="<spring:message code='comuns.nom' />"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span11">
							</spring:bind>
							${campErrors}
						</div>
					</div>
				</div>
				<div class="control-group fila_reducida">
					<c:set var="campPath" value="llinatge1"/>
					<label class="control-label" data-required="true" for="${campPath}"><spring:message code='persona.form.primer_llin' /></label>
					<div class="controls">
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
							<spring:bind path="${campPath}">
								<input <c:if test="${tipusText == 'static'}">readonly="readonly"</c:if> type="text" id="${campPath}" name="${campPath}" placeholder="<spring:message code='persona.form.primer_llin' />"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span11">
							</spring:bind>
							${campErrors}
						</div>
					</div>
				</div>
				<div class="control-group fila_reducida">
					<c:set var="campPath" value="llinatge2"/>
					<label class="control-label" for="${campPath}"><spring:message code='persona.form.segon_llin' /></label>
					<div class="controls">
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
							<spring:bind path="${campPath}">
								<input <c:if test="${tipusText == 'static'}">readonly="readonly"</c:if> type="text" id="${campPath}" name="${campPath}" placeholder="<spring:message code='persona.form.segon_llin' />"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span11">
							</spring:bind>
							${campErrors}
						</div>
					</div>
				</div>
				<div class="control-group fila_reducida">
					<c:set var="campPath" value="dni"/>
					<label class="control-label" for="${campPath}"><spring:message code='persona.form.dni' /></label>
					<div class="controls">
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
							<spring:bind path="${campPath}">
								<input <c:if test="${tipusText == 'static'}">readonly="readonly"</c:if> type="text" id="${campPath}" name="${campPath}" placeholder="<spring:message code='persona.form.dni' />"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span11">
							</spring:bind>
							${campErrors}
						</div>
					</div>
				</div>
				<div class="control-group fila_reducida">
					<c:set var="campPath" value="email"/>
					<label class="control-label" data-required="true" for="${campPath}"><spring:message code='persona.consulta.email' /></label>
					<div class="controls">
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
							<spring:bind path="${campPath}">
								<input <c:if test="${tipusText == 'static'}">readonly="readonly"</c:if> type="text" id="${campPath}" name="${campPath}" placeholder="<spring:message code='persona.consulta.email' />"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span11">
							</spring:bind>
							${campErrors}
						</div>
					</div>
				</div>
				<div class="control-group fila_reducida">
					<c:set var="campPath" value="hombre"/>
					<label class="control-label" for="${campPath}"><spring:message code='comuns.sexe' /></label>
					<div class="controls">
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<form:select id="sexe" name="sexe" path="${campPath}" cssClass="span11">
							<form:options items="${sexes}" itemLabel="valor" itemValue="codi"/>
						</form:select>
						${campErrors}
					</div>
				</div>
				<br/>
				<div class="pull-right">
					<input class="btn btn-primary" type="submit" id="guardar" name="accio" value="Modificar" />
				</div>
			</form:form>
		</div>
	</div>
	<br/>
	<div class="page-header">
		<h4><spring:message code='perfil.info.preferencies' /></h4>
	</div>		
	<div class="well well-white">
		<div class="row-fluid">    
			<form:form action="" method="post" cssClass="formbox form-horizontal" commandName="personaUsuariCommand">
				<div class="control-group">
					<label for="tz" class="control-label">Listado inicial</label>
					<div class="controls">
						<c:set var="campPath" value="listado"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<form:select path="${campPath}" cssClass="span11">
							<form:options items="${pantallas}" itemLabel="valor" itemValue="codi"/>
						</form:select>
						${campErrors}
						<p class="help-block">
							<span class="label label-info">Nota</span> Veure la pantalla d'expedients o de tasques per defecte.
						</p>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="lc_time">Capcalera</label>
					<div class="controls">
						<c:set var="campPath" value="cabeceraReducida"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<form:select path="${campPath}" cssClass="span11">
							<form:options items="${cabeceras}" itemLabel="valor" itemValue="codi"/>
						</form:select>
						${campErrors}
						<p class="help-block">
							<span class="label label-info">Nota</span> Mida i format de capcalera.
						</p>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="lc_time">Entorno</label>
					<div class="controls">
						<c:set var="campPath" value="entornCodi"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<form:select path="${campPath}" cssClass="span11">
							<form:options items="${entorns}" itemLabel="nom" itemValue="codi"/>
						</form:select>
						${campErrors}
						<p class="help-block">
							<span class="label label-info">Nota</span> Entorn per defecte que s'ha d'obrir.
						</p>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="lc_time">Consulta por tipo</label>
					<div class="controls">
						<label class="control-label" for="lc_time" style="text-align: left">Tipo de expediente</label>
						<c:set var="campPath" value="expedientTipusId"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<form:select path="${campPath}" cssClass="span11">
							<option value="">&lt;&lt; <spring:message code="js.helforms.selec_valor" /> &gt;&gt;</option>
							<form:options items="${expedientTipus}" itemLabel="nom" itemValue="id"/>
						</form:select>
						${campErrors}
						<label class="control-label" for="lc_time" style="text-align: left">Consulta por tipo</label>
						<c:set var="campPath" value="consultaId"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<form:select path="${campPath}" cssClass="span11">
							<option value="">&lt;&lt; <spring:message code="js.helforms.selec_valor" /> &gt;&gt;</option>
							<form:options items="${consultes}" itemLabel="nom" itemValue="id"/>
						</form:select>
						${campErrors}
						<p class="help-block">
							<span class="label label-info">Nota</span> Consulta per tipus per defecte que s'ha d'obrir.
						</p>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="lc_time">Elementos por página</label>
					<div class="controls">
						<c:set var="campPath" value="numElementosPagina"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<form:select path="${campPath}" cssClass="span11">
							<form:options items="${numElementsPagina}" itemLabel="valor" itemValue="codi"/>
						</form:select>
						${campErrors}
						<p class="help-block">
							<span class="label label-info">Nota</span> Numero d'elements que s'han de mostrar per defecte en les taules paginades.
						</p>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">Filtros por defecto</label>
					<div class="controls">
						<form:hidden path="filtroExpedientesActivos"/>
						<label class="checkbox inline">
							<input type="checkbox" id="filtroExpedientesActivosCheck" <c:if test="${personaUsuariCommand.filtroExpedientesActivos}">checked="checked"</c:if>> <label>Llistat d'expedients filtra per defecte o no expedients amb tasques actives per part de l'usuari.</label>
						</label>
						${campErrors}
					</div>
				</div>
				<br />
				<div class="pull-right">
					<input class="btn btn-primary" type="submit" id="guardar" name="accio" value="Guardar" />
				</div>
			</form:form>
		</div>
	</div>
	
	<script>	
		$( '[data-required="true"]' )
			.closest(".control-group")
			.children("label")
			.prepend("<i class='icon-asterisk'></i> ");
	</script>  
	
</body>
</html>
