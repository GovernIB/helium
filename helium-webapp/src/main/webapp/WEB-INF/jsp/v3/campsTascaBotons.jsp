<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<div id="modal-botons" class="pull-right">
	<c:choose>
		<c:when test="${tasca.validada}">
			<button type="button" id="tasca-boto-restaurar" name="accio" value="restaurar" class="btn btn-default tasca-boto"><span class="fa fa-undo"></span>&nbsp;<spring:message code="tasca.tramitacio.boto.restaurar"/></button>
		</c:when>
		<c:otherwise>
			<button type="button" id="tasca-boto-guardar" name="accio" value="guardar" class="btn btn-default tasca-boto"><span class="fa fa-save"></span>&nbsp;<spring:message code="tasca.tramitacio.boto.guardar"/></button>
			<button type="button" id="tasca-boto-validar" name="accio" value="validar" class="btn btn-default tasca-boto"><span class="fa fa-check"></span>&nbsp;<spring:message code="tasca.tramitacio.boto.validar"/></button>
		</c:otherwise>
	</c:choose>
	<c:if test="${not tasca.delegada or not tasca.delegacioOriginal}">
		<c:choose>
			<c:when test="${not empty tasca.outcomes && fn:length(tasca.outcomes) gt 1}">
				<div class="outcomes" data-textfi="<spring:message code="tasca.tramitacio.boto.finalitzar"/>">
        			<c:forEach var="outcome" items="${tasca.outcomes}" varStatus="status">
        				<c:choose>
        					<c:when test="${not empty outcome}"><c:set var="textCompletar" value="${outcome}"/></c:when>
        					<c:otherwise><c:set var="textCompletar"><spring:message code="tasca.tramitacio.boto.finalitzar"/></c:set></c:otherwise>
        				</c:choose>
        				<button type="button" id="tasca-boto-completar" name="accio" value="completar/${outcome}" class="btn btn-primary tasca-boto">${textCompletar}</button>
        			</c:forEach>
	      		</div>
			</c:when>
			<c:otherwise>
				<button type="button" id="tasca-boto-completar" name="accio" value="completar" class="btn btn-primary tasca-boto"><span class="fa fa-thumbs-o-up"></span>&nbsp;<spring:message code="tasca.tramitacio.boto.finalitzar"/></button>
			</c:otherwise>
		</c:choose>
	</c:if>
</div>
