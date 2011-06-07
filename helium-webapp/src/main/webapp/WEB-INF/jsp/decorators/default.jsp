<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<%@page import="net.conselldemallorca.helium.core.model.update.Versio"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page isErrorPage="true" %>
<decorator:usePage id="pagina"/>

<%--pageContext.setAttribute("requestContext", new org.springframework.web.servlet.support.RequestContext(request));--%>
<%
	request.setAttribute("versio", (String)Versio.getVersion());
	request.setAttribute("error", (String)Versio.getError());
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<title>Helium - <decorator:title default="<fmt:message key='decorators.default.benvinguts' />"/></title>
	<link rel="icon" href="<c:url value="/img/favicon.ico"/>" type="image/x-icon"/>
	<link rel="shortcut icon" href="<c:url value="/img/favicon.ico"/>" type="image/x-icon"/> 
	<link href="<c:url value="/css/reset.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/common.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/layout.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.extend.js"/>"></script>
<%-- menu --%>
	<link href="<c:url value="/css/dropdown/dropdown.css"/>" media="all" rel="stylesheet" type="text/css" />
	<link href="<c:url value="/css/dropdown/themes/helium/helium.css"/>" media="all" rel="stylesheet" type="text/css" />
<!--[if lt IE 7]>
<script type="text/javascript" src="<c:url value="/js/dropdown/jquery/jquery.dropdown.js"/>"></script>
<%--script type="text/javascript">var clear="<c:url value="/js/unitpngfix/clear.gif"/>";</script>
<script type="text/javascript" src="<c:url value="/js/unitpngfix/unitpngfix.js"/>"></script--%>
<![endif]-->
<%-- /menu --%>
	
	<!-- script type="text/javascript" src="<c:url value="/js/msdropdown/jquery-1.3.2.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/msdropdown/jquery.dd.js"/>"></script>
	<link href="<c:url value="/css/msdropdown/dd.css"/>" rel="stylesheet" type="text/css" />
	<script language="javascript">
		$(document).ready(function(e) {
			try {
				$("body select").msDropDown();
			} catch(e) {
				alert(e.message);
			}
		});
	</script -->
	<c:set var="hiHaTaula" value='${not empty llistat or not empty personaLlistat or not empty grupLlistat
			or not empty variablesProcesSenseAgrupar or not empty instanciaProces.sortedDocumentKeys
			or not empty registre or not empty estats or not empty tasques or not empty camps
			or not empty documents or not empty agrupacions or not empty accions or not empty recursos
			or not empty terminis or not empty tokens}'/>
	<c:if test="${hiHaTaula}">
		<script type="text/javascript">
			$(document).ready(function(){
				if ( $(".displaytag").length == 0 ) {
					$("#filtre").hide();
				} else {
					
					var $anchor = $("#filtre a:first");
					var $input = $("#filtre input:first");
					
					$anchor.click(function(){
						if ( $input.css("visibility")=="hidden" ) {
							$(this).hide();
							$input.css("visibility", "visible");
							$input.animate({width: "194px"}, 200);
							setTimeout(function() {
								$input.focus();
								$anchor.css("top", "5px");
								$anchor.css("left", "196px");
								$anchor.show();
							}, 200);
						} else {
							$(this).hide();
							$input.val("");
							$(".displaytag").children("tbody").find("tr").show();
							$input.blur();
							$input.animate({width: "0px"}, 200);
							setTimeout(function() {
								$input.css("visibility", "hidden");
								$anchor.css("top", "5px");
								$anchor.css("left", "3px");
								$anchor.show();
							}, 200);
						}
						return false;
					});
					
					$("#filtre input").keyFilter(".displaytag");
					
				}
			});
		</script>
	</c:if>
	
	<decorator:head />
</head>
<body>
	<div id="main">
		<div id="header">
		<c:if test="${(error != null) && (error!='')}">
			<div id="error" style="background-color: red; padding:5px 10px 5px 24px;">
				<span style="text-decoration: blink; color: white; font-weight: bold;">${error}</span> 
			</div>
		</c:if>
			<div id="logo-wrapper"">
					<h1 id="logo"><span>H</span>elium</h1>
			</div>
			<div id="menu-wrapper">
				<jsp:include page="partMenuSuperior.jsp"/>
				<ul id="menu-logout" class="dropdown dropdown-horizontal">
					<c:choose>
						<c:when test="${globalProperties['app.logout.actiu']}">
							<li class="image sortir"><a title="<fmt:message key='decorators.default.autenticat_amb' />: ${dadesPersona.codi}" href="<c:url value="/logout.jsp"/>"><fmt:message key='decorators.default.sortir' /></a></li>
						</c:when>
						<c:otherwise>
							<li class="image usuari"><a title="<fmt:message key='decorators.default.usuari' /> ${dadesPersona.codi}">${dadesPersona.codi}</a></li>
						</c:otherwise>
					</c:choose>
					<c:set var="actual" value='${sessionScope["idiomaActual"]}'/>
					<c:set var="disponibles" value='${sessionScope["idiomesDisponibles"]}'/>
					<c:if test="${(not empty disponibles) && not(fn:length(disponibles)==1 && disponibles[0].codi==actual.codi)}">
						<li class="dir image rtl idioma">
							<a style="background-image: url(<c:url value='/img/locale/${actual.codi}.png' />);">${actual.nom}</a>
							<ul>
								<c:forEach var="item" items="${disponibles}">
									<c:if test="${item.codi!=actual.codi}">
										<li class="image idioma"><a href="?lang=${item.codi}" style="background-image: url(<c:url value="/img/locale/${item.codi}.png"/>);">${item.nom}</a></li>
									</c:if>
								</c:forEach>
							</ul>
						</li>
					</c:if>
				</ul>
			</div>
			<jsp:include page="partMenuEntorn.jsp"/>
			<div id="page-title">
				<h2><span><c:if test="${not empty pagina.title}"><decorator:title/></c:if></span><decorator:getProperty property="meta.titolcmp"/></h2>
				<c:if test="${hiHaTaula}">
					<span id="filtre">
						<input type="text" title="<fmt:message key='decorators.default.cerca' />" />
						<a href=""><img src="<c:url value="/img/magnifier.png"/>" /></a>
					</span>
				</c:if>
			</div>
		</div>
		
		<div id="content">
			<jsp:include page="../common/missatgesInfoError.jsp"/>
			<decorator:body />
		</div>
		<div id="push"></div>
	</div>
	
	<div id="footer"><span id="version" style="float: left;">${versio}</span> ${globalProperties['app.copyright.text']}</div>
</body>
</html>
