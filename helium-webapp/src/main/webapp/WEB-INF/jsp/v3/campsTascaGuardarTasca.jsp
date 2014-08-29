<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>


<div class="pull-right">
	<c:choose>
		<c:when test="${tasca.validada}">
			<button type="submit" class="btn btn-primary" name="submit" value="restore" onclick="saveAction(this, 'restore');">
				<fmt:message key='comuns.modificar' />
			</button>
		</c:when>
		<c:otherwise>
			<button type="submit" class="btn btn-primary" name="submit" value="submit" onclick="saveAction(this, 'submit', <c:if test="${not empty urlAction}">'${urlAction}'</c:if>);">
				<fmt:message key='tasca.form.guardar' />
			</button>				
			<button type="submit" class="btn btn-primary" name="submit" value="validate" onclick="saveAction(this, 'validate');">
				<fmt:message key='tasca.form.validar' />
			</button>
		</c:otherwise>
	</c:choose>
</div>