<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<div id="modal-botons" class="pull-right">
	<c:choose>
		<c:when test="${tasca.validada}">
			<button type="submit" id="btn_restore" name="accio" value="restore" class="btn btn-default"><span class="fa fa-undo"></span>&nbsp;<spring:message code="tasca.tramitacio.boto.restaurar"/></button>
		</c:when>
		<c:otherwise>
			<button type="submit" id="btn_save" name="accio" value="save" class="btn btn-default"><span class="fa fa-save"></span>&nbsp;<spring:message code="tasca.tramitacio.boto.guardar"/></button>
			<button type="submit" id="btn_validate" name="accio" value="validate" class="btn btn-default"><span class="fa fa-check"></span>&nbsp;<spring:message code="tasca.tramitacio.boto.validar"/></button>
		</c:otherwise>
	</c:choose>
	<c:if test="${not tasca.delegada or not tasca.delegacioOriginal}">
		<c:choose>
<%-- 		<c:set var="outcomes"><c:forEach var="outcome" items="${tasca.outcomes}" varStatus="status"><c:choose><c:when test="${not empty outcome}">${outcome}</c:when><c:otherwise><fmt:message key="common.tram.outcome.finalitzar"/></c:otherwise></c:choose><c:if test="${not status.last}">,</c:if></c:forEach></c:set> --%>
			<c:when test="${not empty tasca.outcomes && fn:length(tasca.outcomes) gt 1}">
				<div class="outcomes" data-textfi="<spring:message code="tasca.tramitacio.boto.finalitzar"/>">
        			<c:forEach var="outcome" items="${tasca.outcomes}" varStatus="status"><button type="submit" id="btn_completar" name="accio" value="${outcome}" class="btn btn-primary btn_completar"><c:choose><c:when test="${not empty outcome}">${outcome}</c:when><c:otherwise><fmt:message key="tasca.tramitacio.boto.finalitzar"/></c:otherwise></c:choose></button></c:forEach>
	      		</div>
			</c:when>
			<c:otherwise><button type="submit" id="btn_completar" name="accio" value="" class="btn btn-primary btn_completar"><span class="fa fa-thumbs-o-up"></span>&nbsp;<spring:message code="tasca.tramitacio.boto.finalitzar"/></button></c:otherwise>
		</c:choose>
	</c:if>
</div>
