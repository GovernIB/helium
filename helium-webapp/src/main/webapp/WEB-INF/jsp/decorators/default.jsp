<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page isErrorPage="true" %>
<decorator:usePage id="pagina"/>

<%--pageContext.setAttribute("requestContext", new org.springframework.web.servlet.support.RequestContext(request));--%>

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
<%-- menu --%>
	<link href="<c:url value="/css/dropdown/dropdown.css"/>" media="all" rel="stylesheet" type="text/css" />
	<link href="<c:url value="/css/dropdown/themes/helium/helium.css"/>" media="all" rel="stylesheet" type="text/css" />
<!--[if lt IE 7]>
<script type="text/javascript" src="<c:url value="/js/dropdown/jquery/jquery.dropdown.js"/>"></script>
<%--script type="text/javascript">var clear="<c:url value="/js/unitpngfix/clear.gif"/>";</script>
<script type="text/javascript" src="<c:url value="/js/unitpngfix/unitpngfix.js"/>"></script--%>
<![endif]-->
<%-- /menu --%>

	<decorator:head />
</head>
<body>
	<div id="main">
		<div id="header">
			<div id="logo-wrapper">
				<h1 id="logo"><span>H</span>elium</h1>
			</div>
			<div id="menu-wrapper">
				<jsp:include page="partMenuSuperior.jsp"/>
				<c:choose>
					<c:when test="${globalProperties['app.logout.actiu']}">
						<ul id="menu-logout" class="dropdown dropdown-horizontal">
							<li class="image sortir"><a title="<fmt:message key='decorators.default.autenticat_amb' />: ${dadesPersona.codi}" href="<c:url value="/logout.jsp"/>"><fmt:message key='decorators.default.sortir' /></a></li>
						</ul>
					</c:when>
					<c:otherwise>
						<ul id="menu-logout" class="dropdown dropdown-horizontal">
							<li class="image sortir"><img src="<c:url value="/img/user_suit.png"/>" alt="<fmt:message key='decorators.default.usuari' /> ${dadesPersona.codi}" title="<fmt:message key='decorators.default.usuari' /> ${dadesPersona.codi}" border="0" style="position:relative;top:3px"/> ${dadesPersona.codi}</li>
						</ul>
					</c:otherwise>
				</c:choose>
			</div>
			<jsp:include page="partMenuEntorn.jsp"/>
			<div id="page-title">
				<h2><span><c:if test="${not empty pagina.title}"><decorator:title/></c:if></span><decorator:getProperty property="meta.titolcmp"/></h2>
			</div>
		</div>
		<div id="content">
			<jsp:include page="../common/missatgesInfoError.jsp"/>
			<decorator:body />
		</div>
		<div id="push"></div>
	</div>
	<div id="footer">${globalProperties['app.copyright.text']}</div>
</body>
</html>
