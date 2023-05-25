<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:set var="potDissenyarExpedientTipusAdmin" value="${potAdministrarEntorn 
														or potDissenyarEntorn
														or expedientTipus.permisAdministration 
														or expedientTipus.permisDesignAdmin}"/>
<c:set var="potDissenyarExpedientTipusDelegat" value="${potAdministrarEntorn 
														or potDissenyarEntorn 
														or expedientTipus.permisAdministration 
														or expedientTipus.permisDesignAdmin 
														or expedientTipus.permisDesignDeleg}"/>

<html>
<head>
	<title><spring:message code="expedient.tipus.pipelles.titol"/></title>
	<meta name="title" content="<spring:message code="expedient.tipus.pipelles.titol"/>"/>
	<meta name="subtitle" content="${fn:escapeXml(expedientTipus.nom)}"/>
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
			if (true) {			//Condició per carregar cada vegada les pipelles
				carregaTab(targetHref);
			}
		})
		<c:choose>
			<c:when test="${not empty pipellaActiva}">$('#expedientTipus-pipelles li#pipella-${pipellaActiva} a').click();</c:when>
			<c:otherwise>$('#expedientTipus-pipelles li:first a').click();</c:otherwise>
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
		<div id="expedientTipus-pipelles" class="col-md-12">
			<div class="scroller scroller-left"><i class="glyphicon glyphicon-chevron-left"></i></div>
  			<div class="scroller scroller-right"><i class="glyphicon glyphicon-chevron-right"></i></div>
  			<div class="wrapper">
				<ul class="nav nav-tabs pipelles" role="tablist">
					<li id="pipella-informacio"><a href="#contingut-informacio" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.informacio"/></a></li>
					<c:if test="${potDissenyarExpedientTipusDelegat}">
						<li id="pipella-definicions-proces"><a href="#contingut-definicions-proces" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.definicions.proces"/></a></li>
					</c:if>
					<c:if test="${potDissenyarExpedientTipusAdmin}">
						<li id="pipella-variables"><a href="#contingut-variables" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.variables"/></a></li>
					</c:if>
					<c:if test="${potDissenyarExpedientTipusDelegat}">
						<li id="pipella-documents"><a href="#contingut-documents" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.documents"/></a></li>
					</c:if>
					<c:if test="${potDissenyarExpedientTipusAdmin}">
						<li id="pipella-tasques"><a href="#contingut-tasques" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.tasques"/></a></li>
					</c:if>
					<c:if test="${potDissenyarExpedientTipusAdmin}">
						<li id="pipella-terminis"><a href="#contingut-terminis" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.terminis"/></a></li>
						<li id="pipella-accions"><a href="#contingut-accions" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.accions"/></a></li>						
						<li id="pipella-estats"><a href="#contingut-estats" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.estats"/></a></li>
					</c:if>
					<c:if test="${potDissenyarExpedientTipusDelegat}">
						<li id="pipella-enumeracions"><a href="#contingut-enumeracions" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.enumeracions"/></a></li>
						<li id="pipella-consultes"><a href="#contingut-consultes" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.consultes"/></a></li>
					</c:if>
					<c:if test="${potDissenyarExpedientTipusAdmin}">
						<li id="pipella-dominis"><a href="#contingut-dominis" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.dominis"/></a></li>
					</c:if>
					<c:if test="${potDissenyarExpedientTipusAdmin}">
						<li id="pipella-nti"><a href="#contingut-metadadesnti" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.integracio.arxiu"/></a></li>
						<li id="pipella-integracio-tramits"><a href="#contingut-integracio-tramits" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.integracio.sistra"/></a></li>
						<li id="pipella-integracio-distribucio"><a href="#contingut-integracio-distribucio" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.integracio.distribucio"/></a></li>
						<li id="pipella-integracio-forms"><a href="#contingut-integracio-forms" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.integracio.forms"/></a></li>
						<li id="pipella-integracio-notib"><a href="#contingut-integracio-notib" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.integracio.notib"/></a></li>
						<li id="pipella-integracio-pinbal"><a href="#contingut-integracio-pinbal" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.integracio.pinbal"/></a></li>
						<li id="pipella-redireccions"><a href="#contingut-redireccions" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.redireccions"/></a></li>						
					</c:if>
				</ul>
			</div>
			<div class="tab-content">
				<div id="contingut-informacio" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/informacio"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-definicions-proces" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/definicionsProces"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-variables" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/variables"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-documents" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/documents"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-tasques" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/tasques"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-terminis" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/terminis"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-accions" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/accions"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-estats" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/estats"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-enumeracions" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/enumeracions"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-dominis" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/dominis"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-consultes" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/consultes"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-metadadesnti" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/metadadesNti"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-integracio-tramits" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/integracioTramits"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-integracio-distribucio" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/integracioDistribucio"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-integracio-forms" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/integracioForms"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-integracio-notib" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/integracioNotib"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-integracio-pinbal" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/integracioPinbal"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-redireccions" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/redireccions"/>">
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
