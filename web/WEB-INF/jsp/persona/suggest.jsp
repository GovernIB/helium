<%@ page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", -1); 
%><c:forEach var="persona" items="${persones}">${persona.nomSencer}|${persona.codi}
</c:forEach>