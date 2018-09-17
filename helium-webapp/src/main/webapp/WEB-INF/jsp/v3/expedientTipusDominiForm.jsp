<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${!heretat}">
		<c:choose>
			<c:when test="${empty expedientTipusDominiCommand.id}">
				<c:set var="titol"><spring:message code="expedient.tipus.domini.form.titol.nou"/></c:set>
				<c:set var="formAction">new</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="titol"><spring:message code="expedient.tipus.domini.form.titol.modificar"/></c:set>
				<c:set var="formAction">update</c:set>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.tipus.domini.form.titol.visualitzar"/></c:set>
		<c:set var="formAction">none</c:set>		
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
</head>
<body>		
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="expedientTipusDominiCommand">
		<div>
			<h3><fmt:message key="expedient.tipus.domini.form.domini.dades"/></h3>
			<input type="hidden" name="id" value="${expedientTipusDominiCommand.id}"/>
			<hel:inputText required="true" name="codi" textKey="expedient.tipus.domini.form.domini.codi" />
			<hel:inputText required="true" name="nom" textKey="expedient.tipus.domini.form.domini.nom" />
			<hel:inputSelect required="true" name="tipus" textKey="expedient.tipus.domini.form.domini.tipus" optionItems="${tipusDominis}" optionTextKeyAttribute="valor" optionValueAttribute="codi" emptyOption="true" placeholderKey="expedient.tipus.domini.form.domini.tipus.placeholder" />
			<hel:inputText required="true" name="cacheSegons" textKey="expedient.tipus.domini.form.domini.cache.segons" comment="expedient.tipus.domini.form.domini.cache.segons.comentari"/>
			<hel:inputText name="timeout" textKey="expedient.tipus.domini.form.domini.timeout" comment="expedient.tipus.domini.form.domini.timeout.comentari"/>
			<hel:inputTextarea name="descripcio" textKey="expedient.tipus.domini.form.domini.descripcio"/>
			<h3><fmt:message key="expedient.tipus.domini.form.domini.dades.ws"/></h3>
			<hel:inputTextarea name="url" textKey="expedient.tipus.domini.form.domini.url"/>
			<hel:inputSelect name="tipusAuth" textKey="expedient.tipus.domini.form.domini.tipus.auth" optionItems="${tipusAutenticacio}" optionTextKeyAttribute="valor" optionValueAttribute="codi"/>
			<hel:inputSelect name="origenCredencials" textKey="expedient.tipus.domini.form.domini.origen.credencials" optionItems="${credencialsOrigen}" optionTextKeyAttribute="valor" optionValueAttribute="codi"/>
			<hel:inputText name="usuari" textKey="expedient.tipus.domini.form.domini.usuari" />
			<hel:inputText name="contrasenya" textKey="expedient.tipus.domini.form.domini.contrasenya" />
			<h3><fmt:message key="expedient.tipus.domini.form.domini.dades.sql"/></h3>
			<hel:inputText name="jndiDatasource" textKey="expedient.tipus.domini.form.domini.datasource" />
			<hel:inputTextarea name="sql" textKey="expedient.tipus.domini.form.domini.sql" />
		</div>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<c:if test="${!heretat}">
				<c:choose>
					<c:when test="${empty expedientTipusDominiCommand.id}">
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
			</c:if>		
		</div>
		
	<script type="text/javascript">
		$(document).ready(function() {
			$("#cacheSegons").keyfilter(/^[-+]?[0-9]*$/);
			$("#timeout").keyfilter(/^[-+]?[0-9]*$/);
			$("#tipus").change(function(){
				if( $(this).val() ==  'CONSULTA_SQL' ) {
					$("#jndiDatasource").prop("disabled", false);
					$("#sql").prop("disabled", false);
					$("#url").prop("disabled", true);
					$("#tipusAuth").prop("disabled", true);
					$("#origenCredencials").prop("disabled", true);
					$("#usuari").prop("disabled", true);
					$("#contrasenya").prop("disabled", true);
				} else if( $(this).val() ==  'CONSULTA_WS' ) {
					$("#url").prop("disabled", false);
					$("#tipusAuth").prop("disabled", false);
					$("#origenCredencials").prop("disabled", false);
					$("#usuari").prop("disabled", false);
					$("#contrasenya").prop("disabled", false);
					$("#jndiDatasource").prop("disabled", true);
					$("#sql").prop("disabled", true);
				} else {
					$("#url").prop("disabled", true);
					$("#tipusAuth").prop("disabled", true);
					$("#origenCredencials").prop("disabled", true);
					$("#usuari").prop("disabled", true);
					$("#contrasenya").prop("disabled", true);
					$("#jndiDatasource").prop("disabled", true);
					$("#sql").prop("disabled", true);
				}
				$("#tipusAuth").change();
			});
			$("#tipusAuth").change(function(){
				if($("#tipus").val() == 'CONSULTA_WS') {
					if($(this).val() == 'NONE' ) {
						$("#usuari").prop("disabled", true);
						$("#contrasenya").prop("disabled", true);
						$("#origenCredencials").prop("disabled", true);
					} else {
						$("#usuari").prop("disabled", false);
						$("#contrasenya").prop("disabled", false);
						$("#origenCredencials").prop("disabled", false);
					}
				}
			});
			$("#tipus").change();
		});
	</script>			

	</form:form>
</body>
</html>