<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:forEach var="attributeName" items="${pageContext.request.attributeNames}">
	<c:if test="${not fn:contains(attributeName, '.') && fn:contains(attributeName, 'ommand')}">
		<spring:hasBindErrors name="${attributeName}">
			<c:if test="${not empty errors.globalErrors}">
				<c:forEach var="error" items="${errors.globalErrors}">
					<div class="alert alert-danger">
						<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
						<spring:message message="${error}"/>
					</div>
				</c:forEach>
			</c:if>
		</spring:hasBindErrors>
	</c:if>
</c:forEach>
<%
	request.setAttribute(
		"sessionErrors",
		session.getAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_ERROR));
%>
<c:forEach var="alert" items="${sessionErrors}">
	<div class="alert alert-danger">
		<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
		<span class="fa fa-warning"></span>
		<c:out value="${alert.text}"/>
		<c:if test="${not empty alert.trace}">
			<div id="trace-container">
				<div class="trace">
					<h3><spring:message code="comu.error.mostrartrasa" /><span class="fa fa-clipboard trace-copy" title="<spring:message code="comu.copiar.portapapers"/>" style="float: right; color: gray;"></span></h3>
					<div style="display:none;"><c:out value="${alert.trace}"/></div>
				</div>
			</div>
		</c:if>
	</div>
</c:forEach>
<%
	session.removeAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_ERROR);
%>

<%
	request.setAttribute(
		"sessionWarnings",
		session.getAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_WARNING));
%>
<c:forEach var="alert" items="${sessionWarnings}">
	<div class="alert alert-warning">
		<span class="fa fa-warning"></span>
		<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
		${alert.text}
	</div>
</c:forEach>
<%
	session.removeAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_WARNING);
%>

<%
	request.setAttribute(
		"sessionSuccesses",
		session.getAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_SUCCESS));
%>
<c:forEach var="alert" items="${sessionSuccesses}">
	<div class="alert alert-success">
		<span class="fa fa-check"></span>
		<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
		${alert.text}
	</div>
</c:forEach>
<%
	session.removeAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_SUCCESS);
%>

<%
	request.setAttribute(
		"sessionInfos",
		session.getAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_INFO));
%>
<c:forEach var="alert" items="${sessionInfos}">
	<div class="alert alert-info">
		<span class="fa fa-info-circle"></span>
		<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
		${alert.text}
	</div>
</c:forEach>
<%
	session.removeAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_INFO);
%>

<script>
	$(document).ready(function() {
		$(".trace").accordion({
			active: false,
			collapsible: true,
			heightStyle: "content"
		});
		$(".trace-copy").click(function (event) {
			event.stopPropagation();
			navigator.clipboard.writeText($(this).parent().next().text());
			$("#modal-copied").modal();
			setTimeout(hideCopyTrace, 2000);
		});
	});
	function hideCopyTrace() {
		$("#modal-copied").modal('hide');
	}
</script>