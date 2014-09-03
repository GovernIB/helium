<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div id="modal-botons" class="well">
	<c:choose>
		<c:when test="${tasca.validada}">
			<button type="submit" class="btn btn-primary" name="submit" value="restore"">
				<spring:message code='comuns.modificar' />
			</button>
		</c:when>
		<c:otherwise>
			<button type="submit" class="btn btn-primary" name="submit" value="guardar">
				<spring:message code='tasca.form.guardar' />
			</button>				
			<button type="submit" class="btn btn-primary" name="submit" value="validate">
				<spring:message code='tasca.form.validar' />
			</button>
		</c:otherwise>
	</c:choose>
</div>