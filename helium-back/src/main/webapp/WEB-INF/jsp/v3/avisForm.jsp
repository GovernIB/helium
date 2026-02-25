<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:choose>
	<c:when test="${empty avisCommand.id}"><
		<c:set var="titol"><spring:message code="avis.form.titol.nou"/></c:set>
		<c:set var="formAction">new</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="avis.form.titol.modificar"/></c:set>
		<c:set var="formAction">update</c:set>
	</c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
		<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/helium.datatable.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	
<hel:modalHead/>
	

<c:choose>
	<c:when test="${!empty globalProperties['app.capsalera.color.fons']}">
		<c:set var="colorFonsDefault">${globalProperties['app.capsalera.color.fons']}}</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="colorFonsDefault">#ff9523</c:set>
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

	
</script>	

</head>
<body>
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="avisCommand">
		<form:hidden id="id" path="id"/>
		<div class="row">
			<div class="col-sm-11">
				<hel:inputText required="true" name="assumpte" textKey="avis.assumpte" />
				<hel:inputTextarea required="true" name="missatge" textKey="avis.missatge" />
				<hel:inputDate required="true" name="dataInici" textKey="avis.dataInici" placeholder="dd/MM/yyyy hh:mm" />
				<fmt:formatDate value="${avis.dataInici}" pattern="dd/MM/yyyy"/>
				<hel:inputDate required="true" name="dataFinal" textKey="avis.dataFi" placeholder="dd/MM/yyyy hh:mm" />
				<hel:inputText  name="horaInici" textKey="avis.horaInici" placeholderKey="avis.hora.minuts" />
				<hel:inputText  name="horaFi" textKey="avis.horaFi" placeholderKey="avis.hora.minuts" />
				<hel:inputSelect required="true" name="avisNivell" textKey="avis.avisNivell"  optionItems="${avisNivell}" optionValueAttribute="valor" optionTextAttribute="codi" inline="false"/>

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
