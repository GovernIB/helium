<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code="carrec.llistat.titol"/></title>
	<meta name="title" content="<spring:message code="carrec.llistat.titol"/>"/>
	<meta name="title-icon-class" content="fa fa-folder-open"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
<style type="text/css">
	#expedientTipus-pipelles .tab-pane {
		margin-top: .6em;
	}
	.contingut-carregant {
		margin-top: 4em;
		text-align: center;
	}
	.edita {
		color: #428bca
	}
	.edita:hover {
		color: #3071a9
	}
	.right-btn {
		float: right;
		margin-top: -4px;
	}
	.wrapper {
	    position:relative;
	    margin:0 auto;
	    overflow:hidden;
		padding:5px;
	  	height:50px;
	}
	.pipelles {
	    position:absolute;
	    left:0px;
	    top:0px;
	  	min-width:3000px;
	  	margin-left:0px;
	    margin-top:0px;
	}
	.pipelles li{
		display:table-cell;
	    position:relative;
	    text-align:center;
	    cursor:grab;
	    cursor:-webkit-grab;
	    color:#efefef;
	    vertical-align:middle;
	}
	.scroller {
	  text-align:center;
	  cursor:pointer;
	  display:none;
	  padding:7px;
	  padding-top:11px;
	  white-space:no-wrap;
	  vertical-align:middle;
	  background-color:#fff;
	}
	.scroller-right{
	  float:right;
	}
	.scroller-left {
	  float:left;
	}
</style>
<script type="text/javascript">
	var hidWidth;
	var scrollBarWidths = 40;

	$(document).ready(function() {		
		$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
			var targetHref = $(e.target).attr('href');
			var loaded = $(targetHref).data('loaded')
			carregaTab(targetHref);
		})
		<c:choose>
			<c:when test="${not empty pipellaActiva}">$('#carrec-pipelles li#pipella-${pipellaActiva} a').click();</c:when>
			<c:otherwise>$('#carrec-pipelles li:first a').click();</c:otherwise>
		</c:choose>
		
		$(window).on('resize', function(e) {
			reAdjust();
		});

		$('.scroller-right').click(function() {

			$('.scroller-left').fadeIn('slow');
			$('.scroller-right').fadeOut('slow');

			$('.pipelles').animate({
				left : "+=" + widthOfHidden() + "px"
			}, 'slow', function() {

			});
		});

		$('.scroller-left').click(function() {

			$('.scroller-right').fadeIn('slow');
			$('.scroller-left').fadeOut('slow');

			$('.pipelles').animate({
				left : "-=" + getLeftPosi() + "px"
			}, 'slow', function() {

			});
		});
		
		reAdjust();
	});
	
	function carregaTab(targetHref) {
		//mostrem cada cop l'icona de càrrega
		$(targetHref).html('<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>'); 
		///////////////
		$(targetHref).load(
			$(targetHref).data('href'),
			function (responseText, textStatus, jqXHR) {
				if (textStatus == 'error') {
					modalAjaxErrorFunction(jqXHR, textStatus);
				} else {
					$(this).data('loaded', 'true');
				}
			}
		);
	}	
	
	var widthOfList = function() {
		var itemsWidth = 0;
		$('.pipelles li').each(function() {
			var itemWidth = $(this).outerWidth();
			itemsWidth += itemWidth;
		});
		return itemsWidth;
	};

	var widthOfHidden = function() {
		return (($('.wrapper').outerWidth()) - widthOfList() - getLeftPosi())
				- scrollBarWidths;
	};

	var getLeftPosi = function() {
		if ($('.pipelles').size() > 0)
			return $('.pipelles').position().left;
		else 
			return 0;
	};

	var reAdjust = function() {
		if (($('.wrapper').outerWidth()) < widthOfList()) {
			$('.scroller-right').show();
		} else {
			$('.scroller-right').hide();
		}

		if (getLeftPosi() < 0) {
			$('.scroller-left').show();
		} else {
			$('.item').animate({
				left : "-=" + getLeftPosi() + "px"
			}, 'slow');
			$('.scroller-left').hide();
		}
	}
</script>
	
</head>
<body>
	<div class="row">
		<div id="carrec-pipelles" class="col-md-12">
			<div class="scroller scroller-left"><i class="glyphicon glyphicon-chevron-left"></i></div>
  			<div class="scroller scroller-right"><i class="glyphicon glyphicon-chevron-right"></i></div>
  			<div class="wrapper">
				<ul class="nav nav-tabs pipelles" role="tablist">
					<li id="pipella-configurats"><a href="#contingut-configurats" role="tab" data-toggle="tab"><spring:message code="carrec.helium.pipella.configurats"/></a></li>
					<li id="pipella-no-configurats"><a href="#contingut-no-configurats" role="tab" data-toggle="tab"><spring:message code="carrec.helium.pipella.no.configurats"/></a></li>
				</ul>
			</div>
			<div class="tab-content">
				<div id="contingut-configurats" class="tab-pane" data-href="<c:url value="/nodeco/v3/carrec/configurats"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-no-configurats" class="tab-pane" data-href="<c:url value="/nodeco/v3/carrec/sense/configurar"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
	// <![CDATA[
	//]]>
	</script>
</body>
</html>
