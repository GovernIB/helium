<%@ page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:forEach var="expedient" items="${expedients}">${expedient.identificador}|${expedient.id}
</c:forEach>