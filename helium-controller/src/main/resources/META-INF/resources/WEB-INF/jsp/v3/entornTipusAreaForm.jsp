<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:choose>
	<c:when test="${empty entornTipusAreaCommand.id}"><
		<c:set var="titol"><spring:message code="tipusArea.form.titol.nou"/></c:set>
		<c:set var="formAction">new</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="tipusArea.form.titol.modificar"/></c:set>
		<c:set var="formAction">update</c:set>
	</c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/helium.modal.js"/>"></script>


<c:choose>
	<c:when test="${!empty globalProperties['es.caib.helium.capsalera.color.fons']}">
		<c:set var="colorFonsDefault">${globalProperties['es.caib.helium.capsalera.color.fons']}}</c:set>
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
	<c:when test="${!empty globalProperties['es.caib.helium.capsalera.color.lletra']}">
		<c:set var="colorLletraDefault">${globalProperties['es.caib.helium.capsalera.color.lletra']}}</c:set>
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
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" modelAttribute="entornTipusAreaCommand">
		<form:hidden id="id" path="id"/>
		<div class="row">
			<div class="col-sm-11">
				<hel:inputText required="true" name="codi" textKey="tipusArea.form.camp.codi" disabled="${not empty entornTipusAreaCommand.id}"/>
				<hel:inputText required="true" name="nom" textKey="tipusArea.form.camp.nom" />
				<hel:inputTextarea name="descripcio" textKey="tipusArea.form.camp.descripcio" />
			</div>
		</div>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true">
				<spring:message code="comu.boto.cancelar"/>
			</button>
			<button type="submit" class="btn btn-success right">
				<span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/>
			</button>
		</div>
	</form:form>
</body>
</html>
