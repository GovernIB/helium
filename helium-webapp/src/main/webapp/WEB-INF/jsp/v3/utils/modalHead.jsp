<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="titol" value="${param.titol}"/>
<c:set var="buttonContainerId" value="${param.buttonContainerId}"/>

<meta name="decorator" content="senseCapNiPeus"/>

<link href="<c:url value="/css/commonV3.css"/>" rel="stylesheet">

<c:if test="${not empty buttonContainerId}">
<script>
	$(document).ready(function() {
		window.parent.modalCanviarTitol(
				window.frameElement,
				"${titol}");
		window.parent.modalCopiarBotons(
				window.frameElement,
				$('#${buttonContainerId} *'));
		$('#${buttonContainerId}').hide();
		window.parent.modalAjustarTamany(
				window.frameElement,
				$('html').height());
	});
</script>
</c:if>
<div class="contingut-alertes"> </div>
