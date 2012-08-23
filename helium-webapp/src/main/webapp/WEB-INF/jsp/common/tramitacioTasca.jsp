<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<script type="text/javascript">
// <![CDATA[
            
            
            
            
$(document).ready(function() {
	outKomes();
});

//funció que renombra tots els botons amb name=submit a name=submitar

function outKomes(){
	var  sortides = "<c:out value="${tasca.outcomes}"/>";
	if(sortides.substring(1,sortides.length-1 == null)){

			<c:forEach var="outcome" items="${tasca.outcomes}">
				<c:choose>
					<c:when test="${not empty outcome}">		
						$("#command button[value=${outcome}]").attr("name","submitar");	
						$("#formFinalitzar button[value=${outcome}]").attr("name","submitar");
						$("#commandReadOnly button[value=${outcome}]").attr("name","submitar");
					</c:when>
					<c:otherwise>
						$("#command button[value=finalitzar]").attr("name","submitar");
						$("#formFinalitzar button[value=finalitzar]").attr("name","submitar");
						$("#formFinalitzar button[value=Finalitzar]").attr("name","submitar");
				 		$("#commandReadOnly button[value=finalitzar]").attr("name","submitar");
					</c:otherwise>
				</c:choose>
			</c:forEach>
	}
}
            
function confirmarFinalitzar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
		return confirm("<fmt:message key='common.tram.confirmacio' />");
}

function finalitzar(){
	
	if($("#command button[value=validate]").length ==0){
		$("#formFinalitzar").attr("action","form.html").submit();
	}else{
		$("#command button[value=finalitzar]").attr("submitar","finalitzar");
		$("#command button[value=validate]").attr("value","finalitzar").click();	
	}
	

}
// ]]>
</script>
<c:choose>
	<c:when test="${not empty tasca.camps and not tasca.validada}">
		<c:set var="tramitacioClass" value="missatgesVermell"/>
		<c:set var="tramitacioOnSubmit"><fmt:message key='common.tram.form_no_validat' /></c:set>
	</c:when>
	<c:when test="${not empty tasca.documents and tasca.documentsNotReadOnly and not tasca.documentsComplet}">
		<c:set var="tramitacioClass" value="missatgesVermell"/>
		<c:set var="tramitacioOnSubmit"><fmt:message key='common.tram.docs_sense_adj' /></c:set>
	</c:when>
	<c:when test="${not empty tasca.signatures and not tasca.signaturesComplet}">
		<c:set var="tramitacioClass" value="missatgesVermell"/>
		<c:set var="tramitacioOnSubmit"><fmt:message key='common.tram.docs_sense_sign' /></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="tramitacioClass" value="missatgesGris"/>
		<c:set var="tramitacioOnSubmit" value="return confirmarFinalitzar(event)"/>
	</c:otherwise>
</c:choose>
<c:if test="${not tasca.delegada or not tasca.delegacioOriginal}">
	<div class="missatgesGrisFort">
		<h4 class="titol-missatge"><fmt:message key='common.tram.finalitzar' /></h4>
		<c:set var="outcomes"><c:forEach var="outcome" items="${tasca.outcomes}" varStatus="status"><c:choose><c:when test="${not empty outcome}">${outcome}</c:when><c:otherwise><fmt:message key='common.tram.outcome.finalitzar' /></c:otherwise></c:choose><c:if test="${not status.last}">,</c:if></c:forEach></c:set>
		
<%-- 		<form action="completar.html" method="post" class="uniForm" onsubmit="<c:out value="${tramitacioOnSubmit}"/>"> --%>

		<form id="formFinalitzar" action="javascript:finalitzar()" method="post"  class="uniForm"  onclick="javascript:confirmarFinalitzar(this)">
			<input type="hidden" name="id" value="${tasca.id}"/>
			<input type="hidden" name="readOnly" value="${hiHaCampsReadOnly}"/>
			<input type="hidden" name="finalitzar" value="finalitzar"/>
			<input type="hidden" name="usuari" value="${usuari}"/>
			<c:if test="${not empty param.pipella}"><input type="hidden" name="pipella" value="${param.pipella}"/>
			</c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">${outcomes}</c:param>
				<c:param name="titles">${outcomes}</c:param>
			</c:import>
		</form>

	</div>
</c:if>