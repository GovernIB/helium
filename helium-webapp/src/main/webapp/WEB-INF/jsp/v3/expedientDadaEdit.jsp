<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<form:form id="command" commandName="modificarVariableCommand" action="" cssClass="form-horizontal form-tasca" method="post">
    <input type="hidden" id="procesId" name="procesId" value="${procesId}">
    <input type="hidden" id="varCodi" name="varCodi" value="${varCodi}">

    <c:set var="inline" value="${false}"/>
    <c:set var="isRegistre" value="${false}"/>
    <c:set var="isMultiple" value="${false}"/>

    <c:set var="command" value="${modificarVariableCommand}"/>

    <c:choose>
        <c:when test="${dada.campTipus != 'REGISTRE'}">
            <c:choose>
                <c:when test="${dada.campMultiple}">
                    <c:set var="campErrorsMultiple"><form:errors path="${dada.varCodi}"/></c:set>
                    <div class="multiple<c:if test="${not empty campErrorsMultiple}"> has-error</c:if>">
                        <label for="${dada.varCodi}" class="control-label col-xs-3">${dada.campEtiqueta}</label>
                            <%-- 							<c:forEach var="membre" items="${dada.multipleDades}" varStatus="varStatusCab"> --%>
                        <c:forEach var="membre" items="${command[dada.varCodi]}" varStatus="varStatusCab">
                            <c:set var="inline" value="${true}"/>
                            <c:set var="campCodi" value="${dada.varCodi}[${varStatusCab.index}]"/>
                            <c:set var="campNom" value="${dada.varCodi}"/>
                            <c:set var="campIndex" value="${varStatusCab.index}"/>
                            <div class="col-xs-9 input-group-multiple <c:if test="${varStatusCab.index != 0}">pad-left-col-xs-3</c:if>">
                                <c:set var="isMultiple" value="${true}"/>
                                <%@ include file="campsTasca.jsp" %>
                                <c:set var="isMultiple" value="${false}"/>
                            </div>
                        </c:forEach>
                        <c:if test="${empty dada.multipleDades}">
                            <c:set var="inline" value="${true}"/>
                            <c:set var="campCodi" value="${dada.varCodi}[0]"/>
                            <c:set var="campNom" value="${dada.varCodi}"/>
                            <c:set var="campIndex" value="0"/>
                            <div class="col-xs-9 input-group-multiple">
                                <c:set var="isMultiple" value="${true}"/>
                                <%@ include file="campsTasca.jsp" %>
                                <c:set var="isMultiple" value="${false}"/>
                            </div>
                        </c:if>
                        <div class="form-group">
                            <div class="col-xs-9 pad-left-col-xs-3">
                                <c:if test="${not empty dada.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p></c:if>
                                <button id="button_add_var_mult_${campCodi}" type="button" class="btn btn-default pull-left btn_afegir btn_multiple"><spring:message code='comuns.afegir' /></button>
                                <div class="clear"></div>
                                <c:if test="${not empty campErrorsMultiple}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${dada.varCodi}"/></p></c:if>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:set var="campCodi" value="${dada.varCodi}"/>
                    <c:set var="campNom" value="${dada.varCodi}"/>
                    <%@ include file="campsTasca.jsp" %>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <%@ include file="campsTascaRegistre.jsp" %>
        </c:otherwise>
    </c:choose>
</form:form>
