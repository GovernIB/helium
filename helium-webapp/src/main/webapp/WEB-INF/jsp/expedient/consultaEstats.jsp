<%@ page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>[
<c:forEach var="estat" items="${estats}" varStatus="status">{"id":"${estat.id}","codi":"${estat.codi}","nom":"${estat.nom}"}<c:if test="${not status.last}">,
</c:if></c:forEach>
]