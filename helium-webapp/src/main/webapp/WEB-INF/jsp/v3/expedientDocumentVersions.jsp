<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
    <title><spring:message code="expedient.document.versions.historial"/></title>
    <hel:modalHead/>
</head>

<body>

<div class="container-fluid">

    <h4>
        <span class="fa fa-history"></span>
        <spring:message code="expedient.document.versions.historial"/>
    </h4>
    <hr/>

    <c:choose>
        <c:when test="${empty versions}">
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
                        <th><spring:message code="expedient.document.versions.data"/></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="versio" items="${versions}" varStatus="status">
                        <tr>
                            <td>${versio.eniVersio} ${versio.nom}</td>
                            <!--<td>
	                            <a href="<c:url value="/v3/expedient/${expedientId}/proces/${expedientDocument.processInstanceId}/document/${expedientDocument.id}/descarregar/versio/${versio.eniVersio}/${arxiuDetall.expedientTancat}"/>">		
								    <span class="fa fa-file no-doc" title="Descarregar document"></span>
									<strong class="nom_document">
										v.${versio.eniVersio}
									</strong>
									<strong class="nom_document"> ${versio.nom}</strong>
									<c:if test="${document.adjunt}">
										<span class="adjuntIcon icon fa fa-paperclip fa-2x"></span>
									</c:if>
									<span class="extensionIcon">
										${fn:toUpperCase(document.arxiuExtensio)}
									</span>
								</a>
							</td> -->
                            <!--<td>${versio.extensio}</td>-->
                            <td></td>
                            <td>
                                <fmt:formatDate value="${versio.eniDataCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/>
                            </td>
                            <td>
                                <!-- TODO: endpoint real de descarrega, aquest es provisional -->
                                <a class="btn btn-default btn-sm"
                                   href="<c:url value='/v3/expedient/${expedientId}/proces/${expedientDocument.processInstanceId}/document/${expedientDocument.id}/descarregar/versio/${status.index}'/>">
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
    <button type="button" class="btn btn-default modal-tancar">
        <spring:message code="comu.boto.tancar"/>
    </button>
</div>

</body>
</html>
