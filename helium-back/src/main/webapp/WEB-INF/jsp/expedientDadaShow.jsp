<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="valor" value="${dada.valor}"/>
<c:choose>
    <c:when test="${valor.registre}">
        <ul class="list-group registre">
            <li class="list-group-item d-flex justify-content-between border-0">
                <c:forEach var="vheader" items="${valor.valorHeader}">
                    <div class="d-flex flex-column text-dark font-weight-bold text-sm<c:if test='${vheader.value}'> obligatori</c:if>">${vheader.key}</div>
                </c:forEach>
            </li>
            <c:forEach var="vbody" items="${valor.valorBody}">
                <li class="list-group-item d-flex justify-content-between border-0">
                    <c:forEach var="dada" items="${vbody}">
                        <div class="d-flex flex-column text-sm">${dada}</div>
                    </c:forEach>
                </li>
            </c:forEach>
        </ul>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${valor.multiple}">
                <ul class="list-group multiple">
                    <c:forEach var="dada" items="${valor.valorMultiple}">
                        <li class="list-group-item d-flex justify-content-between border-0">
                            <div class="d-flex flex-column text-sm">${dada}</div>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>
            <c:otherwise>
                ${valor.valorSimple}
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
