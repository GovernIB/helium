<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%-- Calendari --%>
<c:if test="${empty param.withoutCss || param.withoutCss == 'false'}">
	<link href="<c:url value="/css/uni-form/uni-form.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/uni-form/default.uni-form.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/uni-form/helium.uni-form.css"/>" rel="stylesheet" type="text/css"/>
</c:if>
<script type="text/javascript" src="<c:url value="/js/jquery/ui/ui.core.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery/ui/ui.datepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery/ui/i18n/ui.datepicker-ca.js"/>"></script>
<link href="<c:url value="/js/jquery/ui/themes/base/ui.all.css"/>" rel="stylesheet" type="text/css"/>
<%-- Suggest --%>
<script type="text/javascript" src="<c:url value="/js/jquery/jquery.autocomplete.js"/>"></script>
<link media="all" type="text/css" href="http://code.jquery.com/ui/1.8.21/themes/base/jquery-ui.css" rel="stylesheet">
<link href="<c:url value="/css/autocomplete.css"/>" rel="stylesheet" type="text/css"/>
<jsp:include page="/js/helforms.jsp" />


<script type="text/javascript" src="<c:url value="/js/jquery/jquery.meio.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.7.min.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery/ui/ui.core.js"/>"></script>
<script  type="text/javascript" src="<c:url value="/js/jquery/ui/jquery-ui-1.7.2.custom.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery/jquery.blockUI.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery/ui/ui.timepicker-addon.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/dropdown/jquery/jquery.dropdown.js"/>"></script>
