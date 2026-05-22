<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma">ca</c:set>

<html>
<head>
	<title><spring:message code="configuracio.parametres.titol"/></title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/3.4.8/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
</head>
<body>		
	<form:form cssClass="form-horizontal" enctype="multipart/form-data" method="post" modelAttribute="parametresCommand">
		<table class="table">
			<tbody>
				<hel:inputCheckbox name="propagarEsborratExpedients" textKey="configuracio.parametres.propagarEsborratExpedients" comment="configuracio.parametres.propagarEsborratExpedients.info" />	
				<strong><spring:message code="configuracio.parametres.unitats.organitzatives"/></strong>
				
				
				<tr>
					<td colspan="1">
						<hel:inputText  readonly="false" name="fitxerMidaMaxim" textKey="configuracio.parametres.fitxerMidaMaxim"/>
						<strong><spring:message code="configuracio.parametres.fitxerMidaMaxim.info"/></strong>
					</td>
				</tr>
				<c:forEach var="parametres" items="${parametres}">
				<tr>
					<c:if test="${parametres.codi != 'app.configuracio.propagar.esborrar.expedients' and parametres.codi != 'app.configuracio.fitxer.mida.maxim'}">
						<td colspan="1">
							<hel:inputText  readonly="true" name="valor" text="${parametres.nom}" placeholder="${parametres.valor}"/>
						</td>
					</c:if>
				</tr>
				</c:forEach>
				<tr>
					<td colspan="1">
						<div class="form-group">
							<label class="control-label col-xs-4  hiddenInfoContainer" for="fitxerMidaMaxim">
								<spring:message code="perfil.usuari.entorn"/>
							</label>
							<div class="col-xs-8">
								<span class="form-control" disabled>
									${entorn}
								</span>
								<p class="comment">app.entorn.helium</p>
							</div>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true">
				<spring:message code="comu.boto.tancar"/>
			</button>
			<button type="submit" name="accio" value="RESTAURAR" class="btn btn-warning right">
				<span class="fa fa-undo"></span> <spring:message code="configuracio.parametres.accio.restaurar"/>
			</button>
			<button type="submit" name="accio" value="GUARDAR" class="btn btn-success right">
				<span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/>
			</button>
		</div>
	</form:form>
</body>
</html>