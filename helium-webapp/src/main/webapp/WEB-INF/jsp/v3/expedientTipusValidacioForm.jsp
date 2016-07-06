<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${empty expedientTipusValidacioCommand.id}"><
		<c:set var="titol"><spring:message code="expedient.tipus.campValidacio.form.titol.nou"/></c:set>
		<c:set var="formAction"><c:url value="/modal/v3/expedientTipus/${expedientTipusValidacioCommand.expedientTipusId}/variable/${expedientTipusValidacioCommand.campId}/validacio/"></c:url>new</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.tipus.campValidacio.form.titol.modificar"/></c:set>
		<c:set var="formAction"><c:url value="/modal/v3/expedientTipus/${expedientTipusValidacioCommand.expedientTipusId}/variable/${expedientTipusValidacioCommand.campId}/validacio/${expedientTipusValidacioCommand.id}/"></c:url>update</c:set>
	</c:otherwise>
</c:choose>

<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
</head>
<body>		
	<form:form id="modal-form" cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="expedientTipusValidacioCommand">
		<div class="inlineLabels">        
			<input type="hidden" name="id" value="${expedientTipusValidacioCommand.id}"/>
			<hel:inputText required="true" name="expressio" textKey="expedient.tipus.campValidacio.form.camp.expressio" />
			<hel:inputTextarea required="true" name="missatge" textKey="expedient.tipus.campValidacio.form.camp.missatge" />
		</div>

		<div id="modal-botons" class="well">
			<button id="btnCancelar" name="submit" value="cancel" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></button>
			<c:choose>
				<c:when test="${empty expedientTipusValidacioCommand.id}">
					<button class="btn btn-primary right" type="submit" name="accio" value="crear">
						<span class="fa fa-plus"></span> <spring:message code='comu.boto.crear' />
					</button>
				</c:when>
				<c:otherwise>
					<button class="btn btn-primary right" type="submit" name="accio" value="modificar">
						<span class="fa fa-pencil"></span> <spring:message code='comu.boto.modificar' />
					</button>
				</c:otherwise>
			</c:choose>
	
		</div>

		<script type="text/javascript">
			// <![CDATA[
			$(document).ready(function(){
				$('#btnCancelar').click(function(e) {
					e.preventDefault();
					var url = '<c:url value="/modal/v3/expedientTipus/${expedientTipusValidacioCommand.expedientTipusId}/variable/${expedientTipusValidacioCommand.campId}/validacio/"/>';
					console.log('btnCancelar ur='+url);
					window.location = url
				});
			});
			// ]]>
		</script>			

	</form:form>
</body>
</html>