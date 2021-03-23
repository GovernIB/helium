<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="area.nou.membres.titol"/></c:set>
<c:set var="formAction">new</c:set>
<c:set var="baseUrl"><c:url value="/modal/v3/entorn-area/${entornAreaId}/membres"></c:url></c:set>
<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
		<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/helium.modal.js"/>"></script>


<c:choose>
	<c:when test="${!empty globalProperties['app.capsalera.color.fons']}">
		<c:set var="colorFonsDefault">${globalProperties['app.capsalera.color.fons']}}</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="colorFonsDefault">#ff9523</c:set>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${entornCommand.colorFons!=null  && not empty entornCommand.colorFons}">
		<c:set var="colorFons">${entornCommand.colorFons}</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="colorFons">${colorFonsDefault}</c:set>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty globalProperties['app.capsalera.color.lletra']}">
		<c:set var="colorLletraDefault">${globalProperties['app.capsalera.color.lletra']}}</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="colorLletraDefault">#ffffff</c:set>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${entornCommand.colorLletra!=null  && not empty entornCommand.colorLletra}">
		<c:set var="colorLletra">${entornCommand.colorLletra}</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="colorLletra">${colorLletraDefault}</c:set>
	</c:otherwise>
</c:choose>


<style>
	.cercle {
		border-radius: 50%;
		width: 30px;
		height: 30px;
		position: absolute;
		right: -25px;
		top: 2px;
		border: 0.5px solid gray;
		cursor: pointer;
	}
	
	.mida-selector {
		width: 300px;
	}
</style>
<script type="text/javascript">
	$(document).ready(function() {
		
		// Color de Fons
		$('#colorFons').change(function(e) {
			var colorFons;
			if ($(this).val() != "")
				colorFons = $(this).val();
			else
				colorFons = '${colorFonsDefault}';
			$('#cercleColorFons').css('background', colorFons);
			$('#divExemple').css('background', colorFons);
		});
		$('#cercleColorFons').click(function(e) {
			var colorFons;
			if ($('#colorFons').val() != "")
				colorFons = $('#colorFons').val();
			else
				colorFons = '${colorFonsDefault}';
			document.getElementById("html5ColorFonsPicker").value = colorFons; 
			$('#html5ColorFonsPicker').click();
		});
		$('#html5ColorFonsPicker').change(function(e) {
			$('#colorFons').val($(this).val()).trigger('change');
		});
		// Color de Lletra
		$('#colorLletra').change(function(e) {
			var colorLletra;
			if ($(this).val() != "")
				colorLletra = $(this).val();
			else
				colorLletra = '${colorLletraDefault}';
			$('#cercleColorLletra').css('background', colorLletra);
			$('#divExemple').css('color', colorLletra);
		});
		$('#cercleColorLletra').click(function(e) {
			var colorLletra;
			if ($('#colorLletra').val() != "")
				colorLletra = $('#colorLletra').val();
			else
				colorLletra = '${colorLletraDefault}';
			document.getElementById("html5ColorLletraPicker").value = colorLletra; 
			$('#html5ColorLletraPicker').click();
		});
		$('#html5ColorLletraPicker').change(function(e) {
			$('#colorLletra').val($(this).val()).trigger('change');
		});
	});
</script>	

</head>
<body>
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
	</div>
	<form:form id="createForm" cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="entornAreaMembreCommand" style='${mostraCreate || mostraUpdate ? "":"display:none;"}'>
		<form:hidden id="id" path="id"/>
		<div class="row">
			<div class="col-sm-11">
				<hel:inputSelect emptyOption="false"
 								required="true"
 								name="codi"
 								textKey="area.nou.membre.form.camp.persona"
 								placeholderKey="area.nou.membre.form.camp.persona.placeholder"
 								optionItems="${persones}" optionValueAttribute="codi"
 								optionTextAttribute="nom" disabled="${empty persones}" inline="false"/>
				<hel:inputSelect emptyOption="false"
 								required="false"
 								name="carrecId"
 								textKey="area.nou.membre.form.camp.carrec"
 								placeholderKey="area.nou.membre.form.camp.carrec.placeholder"
 								optionItems="${entornCarrecs}" optionValueAttribute="id"
 								optionTextAttribute="codi" disabled="${empty entornCarrecs}" inline="false"/>
			</div>
		</div>
		<div id="modal-botons" class="well">
			<button id="btnCancelar" name="submit" value="cancel" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></button>
			<button id="btnCreate" style='${mostraCreate ? "":"display:none;"}' class="btn btn-primary right" type="submit" name="accio" value="crear">
				<span class="fa fa-plus"></span> <spring:message code='comu.boto.crear' />
			</button>
		</div>
	</form:form>
	<div class="botons-titol text-right">
		<button id="btnNew" class="btn btn-default" style='${mostraCreate || mostraUpdate ? "display:none;" : ""}'><span class="fa fa-plus"></span>&nbsp;<spring:message code="area.boto.afegir.membre"/></button>
	</div>	
	<div style="height: 390px;">
		<table	id="entornAreaMembre"
				data-toggle="datatable"
				data-url="${baseUrl}/datatable"
				data-paging-enabled="false"
				data-ordering="true"
				data-default-order="1"
				data-rowhref-toggle="modal" 
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="codi"><spring:message code="area.membre.llistat.nom"/></th>
					<th data-col-name="carrec"><spring:message code="area.membre.llistat.carrec"/></th>

					<th data-col-name="id" data-template="#cellAccionsTemplateMembres" data-orderable="false" width="10%">
						<script id="cellAccionsTemplateMembres" type="text/x-jsrender">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li><a href="${baseUrl}/{{:id}}/delete" data-confirm="<spring:message code="area.membre.esborrar.confirm"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
								</ul>
							</div>
						</script>
					</th>
				</tr>
			</thead>
		</table>
	</div>
	<script type="text/javascript">
	// <![CDATA[
	            
	$(document).ready(function() {
		
		$('#btnNew').click(function(){
			$('#btnNew').hide();
			$('#createForm').show();
			$('#btnCreate').show();
			
			$('#createForm').trigger('reset').show(300);
			$('#createForm .help-block').remove();
			$('#createForm .has-error').removeClass('has-error');

			$('#createForm').attr('action','${baseUrl}/new');
		});
		
		$('#btnCancelar').click(function(e) {
			e.preventDefault();
			$('#btnNew').show();
			$('#createForm').hide();
			$('#createForm').attr('action','');
		});
				
	});
	
	// ]]>
	</script>	
</body>
</html>
