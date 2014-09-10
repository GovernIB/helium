<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<div id="modal-botons" class="pull-right">
	<c:choose>
		<c:when test="${tasca.validada}">
			<button type="submit" name="accio" value="restore" class="btn btn-default"><span class="fa fa-undo"></span>&nbsp;<spring:message code="tasca.tramitacio.boto.restaurar"/></button>
		</c:when>
		<c:otherwise>
			<button type="submit" name="accio" value="save" class="btn btn-default"><span class="fa fa-save"></span>&nbsp;<spring:message code="tasca.tramitacio.boto.guardar"/></button>
			<button type="submit" name="accio" value="validate" class="btn btn-default"><span class="fa fa-check"></span>&nbsp;<spring:message code="tasca.tramitacio.boto.validar"/></button>
		</c:otherwise>
	</c:choose>
	<button type="submit" name="accio" value="completar" class="btn btn-primary"><span class="fa fa-thumbs-o-up"></span>&nbsp;<spring:message code="tasca.tramitacio.boto.finalitzar"/></button>
</div>