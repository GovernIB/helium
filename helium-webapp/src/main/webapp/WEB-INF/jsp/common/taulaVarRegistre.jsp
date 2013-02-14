<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%--
Paràmetres:
	campActual
	registreFiles
	registreReadOnly
--%>
<c:if test="${not empty registreFiles}">
	<div style="overflow:auto">
		<display:table name="registreFiles" id="registre" requestURI="" class="displaytag selectable">
			<c:forEach var="membre" items="${campActual.registreMembres}" varStatus="varStatus">
				<c:if test="${membre.llistar}">
					<c:choose>
						<c:when test="${registreReadOnly}">
							<display:column title="${membre.membre.etiqueta}">${registre[varStatus.index]}</display:column>
						</c:when>
						<c:otherwise>
							<display:column title="${membre.membre.etiqueta}">
								<c:if test="${varStatus.first}"><a href="#" onclick="return editarRegistre(${campActual.id}, '${codiActual}', '<%=toJavascript((String)request.getAttribute("etiquetaActual"))%>', ${fn:length(campActual.registreMembres)}, ${registre_rowNum - 1})"></a></c:if>
								${registre[varStatus.index]}
							</display:column>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
			<c:if test="${not registreReadOnly}">
				<display:column style="width:16px">
					<a href="#" onclick="return esborrarRegistre(event, ${campActual.id}, ${registre_rowNum - 1})"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
				</display:column>
			</c:if>
		</display:table>
	</div>
	<c:if test="${not registreReadOnly}"><script type="text/javascript">initSelectable();</script></c:if>
</c:if>
<c:if test="${not registreReadOnly}">
	<c:if test="${campActual.multiple || fn:length(registreFiles) < 1}">
		<button style="font-size:11px;margin-top: 2px" type="submit" class="submitButton" onclick="return editarRegistre(${campActual.id}, '${codiActual}', '<%=toJavascript((String)request.getAttribute("etiquetaActual"))%>', ${fn:length(campActual.registreMembres)})"><fmt:message key='comuns.afegir' /></button>
	</c:if>
	<div style="clear:both"></div>
</c:if>
<%!
private String toJavascript(String str) {
	if (str == null)
		return null;
	return str.replace("'", "\\'");
		/*replace("{", "").
		replace("}", "").
		replace("#", "");*/
}
%>