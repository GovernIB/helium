<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
    <title><spring:message code="expedient.document.versions.historial"/>: ${expedientDocument.adjuntTitol}</title>
    <hel:modalHead/>
</head>

<body>

<div class="container-fluid">
    <c:choose>
    	<c:when test="errorArxiuNoUuid">
    		<div class="alert alert-warn">
                <spring:message code="expedient.document.versions.warn.arxiunoactiu"/>
            </div>
    	</c:when>
        <c:when test="${(empty arxiuDetall.versionsDocument)}">
            <div class="alert alert-info">
                <spring:message code="expedient.document.versions.cap"/>
            </div>
        </c:when>
        <c:otherwise>
            <table class="table table-striped table-bordered table-condensed">
                <thead>
                    <tr>
                        <th><spring:message code="expedient.document.versions.nom"/></th>
                        <th><spring:message code="expedient.document.versions.extensio"/></th>
                        <th><spring:message code="expedient.document.versions.versio"/></th>
                        <th><spring:message code="expedient.document.versions.data"/></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="versio" items="${arxiuDetall.versionsDocument}" varStatus="status">
                        <tr>
                            <td>${versio.nom}</td>
                            <td>${versio.eniExtensio}</td>
                            <td>${versio.eniVersio}</td>
                            <td>
                                <fmt:formatDate value="${versio.eniDataCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/>
                            </td>
                            <td>
                                <!-- TODO: endpoint real de descarrega, aquest es provisional -->
                                <a class="btn btn-default btn-sm"
                                   href="<c:url value='/expedient/${expedientId}/proces/${expedientDocument.processInstanceId}/document/${expedientDocument.id}/descarregar/versio/${versio.eniVersio}/false'/>">
                                    <span class="fa fa-download"></span>
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

</div>

<div id="modal-botons" class="well">
    <button type="button" class="btn btn-default modal-tancar" data-modal-cancel="true">
        <spring:message code="comu.boto.tancar"/>
    </button>
</div>

</body>
</html>
