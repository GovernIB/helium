<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%pageContext.setAttribute("interDiv", "</div><div class=\"semana\">");%>

<html>
<head>
	<title>Festius</title>
	<meta name="titolcmp" content="Configuració"/>
	<link href="<c:url value="/css/calendari.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/dwr/interface/festiuDwrService.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
<script type="text/javascript" language="javascript">
// <![CDATA[
	var hihaCanvis = false;
	function clickFestiu(elem) {
		var strData = elem.id.split("_")[1];
		var esFestiu = (elem.className.indexOf("festiu") != -1);
		if (!esFestiu) {
			festiuDwrService.crear(
				strData,
				{
					callback: function(retval) {
						if (retval)
							elem.className = "dia festiu";
					},
					async: false
				});
		} else {
			festiuDwrService.esborrar(
				strData,
				{
					callback: function(retval) {
						if (retval)
							elem.className = "dia";
					},
					async: false
				});
		}
		hihaCanvis = true;
	}
// ]]>
</script>
</head>
<body>

	<h3 class="titol-tab">
		Any seleccionat:
		<form action="" style="display:inline">
			<select name="any" onchange="this.form.submit()">
				<c:forEach begin="${anyInicial}" end="${anyFinal}" varStatus="status">
					<option value="${status.index}"<c:if test="${status.index == anyActual}">selected="selected"</c:if>>${status.index}</option>
				</c:forEach>
			</select>
		</form>
	</h3>

	<div class="calendari">
		<c:forEach begin="1" end="12" varStatus="statusMes">
			<div class="mes">
				<div class="nomMes">
					<c:choose>
						<c:when test="${statusMes.index==1}">Gener</c:when>
						<c:when test="${statusMes.index==2}">Febrer</c:when>
						<c:when test="${statusMes.index==3}">Març</c:when>
						<c:when test="${statusMes.index==4}">Abril</c:when>
						<c:when test="${statusMes.index==5}">Maig</c:when>
						<c:when test="${statusMes.index==6}">Juny</c:when>
						<c:when test="${statusMes.index==7}">Juliol</c:when>
						<c:when test="${statusMes.index==8}">Agost</c:when>
						<c:when test="${statusMes.index==9}">Setembre</c:when>
						<c:when test="${statusMes.index==10}">Octubre</c:when>
						<c:when test="${statusMes.index==11}">Novembre</c:when>
						<c:when test="${statusMes.index==12}">Desembre</c:when>
					</c:choose>
				</div>
				<div class="nomDia">Dl</div>
				<div class="nomDia">Dm</div>
				<div class="nomDia">Dc</div>
				<div class="nomDia">Dj</div>
				<div class="nomDia">Dv</div>
				<div class="nomDia">Ds</div>
				<div class="nomDia">Dg</div>
				<div class="setmana">
					<c:set var="diaMes" value="${0}"/>
					<c:forEach begin="1" end="${diaSetmanaIniciMes[statusMes.index - 1] - 1}">
						<div class="dia"></div>
						<c:set var="diaMes" value="${diaMes + 1}"/>
					</c:forEach>
					<c:forEach begin="1" end="${darrerDiaMes[statusMes.index - 1]}" varStatus="statusDia">
						<c:set var="strdia"><fmt:formatNumber value="${statusDia.index}" pattern="00"/>/<fmt:formatNumber value="${statusMes.index}" pattern="00"/>/${anyActual}</c:set>
						<c:set var="esNolab" value="${false}"/>
						<c:if test="${not empty nolabs}">
							<c:forEach var="nolab" items="${nolabs}">
								<c:if test="${nolab - 1 == diaMes % 7}"><c:set var="esNolab" value="${true}"/></c:if>
							</c:forEach>
						</c:if>
						<c:set var="esFestiu" value="${false}"/>
						<c:forEach var="festiu" items="${festius}">
							<c:set var="strfestiu"><fmt:formatDate value="${festiu.data}" pattern="dd/MM/yyyy"/></c:set>
							<c:if test="${strfestiu == strdia}"><c:set var="esFestiu" value="${true}"/></c:if>
						</c:forEach>
						<c:choose>
							<c:when test="${esNolab}"><c:set var="cssClassDia" value="dia nolab"/></c:when>
							<c:when test="${esFestiu}"><c:set var="cssClassDia" value="dia festiu"/></c:when>
							<c:otherwise><c:set var="cssClassDia" value="dia"/></c:otherwise>
						</c:choose>
						<div id="dia_${strdia}" class="${cssClassDia}"<c:if test="${not esNolab}"> onclick="clickFestiu(this)" title="Activar/desactivar festiu"</c:if>>${statusDia.index}</div>
						<c:set var="diaMes" value="${diaMes + 1}"/>
						<c:if test="${diaMes % 7 == 0}">${interDiv}</c:if>
					</c:forEach>
				</div>
			</div>
		</c:forEach>
	</div>

</body>
</html>
