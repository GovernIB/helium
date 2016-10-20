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
	<title><spring:message code="definicio.proces.pipelles.titol"/></title>
	<meta name="title" content="<spring:message code="definicio.proces.pipelles.titol"/>"/>
	<meta name="subtitle" content="${fn:escapeXml(definicioProces.jbpmKey)}"/>
	<meta name="title-icon-class" content="fa fa-folder-open"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
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
	#definicioProces-pipelles .tab-pane {
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
			<c:when test="${not empty pipellaActiva}">$('#definicioProces-pipelles li#pipella-${pipellaActiva} a').click();</c:when>
			<c:otherwise>$('#definicioProces-pipelles li:first a').click();</c:otherwise>
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
<c:choose>
	<c:when test="${not empty definicioProces}">		

		<form class="well" style="padding-top: 10px; padding-bottom:10px;">
			<div class="row">
				<div class="col-sm-10">
					<hel:inputSelect required="false" emptyOption="false" name="versions" textKey="definicio.proces.pipelles.definicio.proces.actual" optionItems="${versions}" optionValueAttribute="codi" optionTextAttribute="valor"/>
				</div>
				<div class="col-sm-2 text-right">
					<div id="versioAccions" class="dropdown" style="margin-right: -10px;">
						<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
						<ul class="dropdown-menu">
							<li><a id="accioExportar" data-toggle="modal" data-modal-id="accioExportarDiv" href="../definicioProces/${definicioProces.jbpmKey}/exportar?definicioProcesId=${definicioProces.id}"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.filtre.exportar"/></a></li>
							<li><a id="accioImportar" data-toggle="modal" data-modal-id="accioImportarDiv" href="../definicioProces/importar?definicioProcesId=${definicioProces.id}"><span class="fa fa-sign-in"></span>&nbsp;<spring:message code="comu.filtre.importar"/></a></li>
							<li><a id="accioEsborrar" href="../definicioProces/${definicioProces.jbpmKey}/${definicioProces.id}/delete" data-rdt-link-ajax="true" data-confirm="<spring:message code="definicio.proces.pipelles.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
						</ul>
					</div>
				</div>
			</div>
		</form>

	<div class="row">
		<div id="definicioProces-pipelles" class="col-md-12">
			<div class="scroller scroller-left"><i class="glyphicon glyphicon-chevron-left"></i></div>
  			<div class="scroller scroller-right"><i class="glyphicon glyphicon-chevron-right"></i></div>
  			<div class="wrapper">
				<ul class="nav nav-tabs pipelles" role="tablist">
					<li id="pipella-detall"><a href="#contingut-detall" role="tab" data-toggle="tab"><spring:message code="definicio.proces.pipelles.pipella.detalls"/></a></li>
					<li id="pipella-tasques"><a href="#contingut-tasques" role="tab" data-toggle="tab"><spring:message code="definicio.proces.pipelles.pipella.tasques"/></a></li>
					<li id="pipella-variables"><a href="#contingut-variables" role="tab" data-toggle="tab"><spring:message code="definicio.proces.pipelles.pipella.variables"/></a></li>
					<li id="pipella-recursos"><a href="#contingut-recursos" role="tab" data-toggle="tab"><spring:message code="definicio.proces.pipelles.pipella.recursos"/></a></li>
				</ul>
			</div>
			<div class="tab-content">
				<div id="contingut-detall" class="tab-pane" data-href="<c:url value="/nodeco/v3/definicioProces/${definicioProces.jbpmKey}/${definicioProces.id}/detall"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-tasques" class="tab-pane" data-href="<c:url value="/nodeco/v3/definicioProces/${definicioProces.jbpmKey}/${definicioProces.id}/tasca"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-variables" class="tab-pane" data-href="<c:url value="/nodeco/v3/definicioProces/${definicioProces.jbpmKey}/${definicioProces.id}/variable"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-recursos" class="tab-pane" data-href="<c:url value="/nodeco/v3/definicioProces/${definicioProces.jbpmKey}/${definicioProces.id}/recurs"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
			</div>
		</div>
	</div>
		<script type="text/javascript">
		// <![CDATA[
			$(document).ready(function() {
				$('#versions').val('${definicioProces.id}').change();
				$('#versions').change(function() {
					// adapta els enllaços
					var definicioProcesId = $(this).val();
					$('#contingut-detall').data('href', '<c:url value="/nodeco/v3/definicioProces/${definicioProces.jbpmKey}/"/>'+definicioProcesId+'/detall');
					$('#contingut-tasques').data('href',  '<c:url value="/nodeco/v3/definicioProces/${definicioProces.jbpmKey}/"/>'+definicioProcesId+'/tasca');
					$('#contingut-variables').data('href',  '<c:url value="/nodeco/v3/definicioProces/${definicioProces.jbpmKey}/"/>'+definicioProcesId+'/variables');
					$('#contingut-recursos').data('href',  '<c:url value="/nodeco/v3/definicioProces/${definicioProces.jbpmKey}/"/>'+definicioProcesId+'/recurs');
					$('#accioExportarDiv').remove();
					$('#accioExportar').attr('href',  '<c:url value="/v3/definicioProces/${definicioProces.jbpmKey}/exportar?definicioProcesId="/>'+definicioProcesId );
					$('#accioImportarDiv').remove();
					$('#accioImportar').attr('href',  '<c:url value="/v3/definicioProces/importar?definicioProcesId="/>'+definicioProcesId );
					$('#accioEsborrar').attr('href',  '<c:url value="/v3/definicioProces/${definicioProces.jbpmKey}/"/>'+definicioProcesId+'/delete' );
					// recarrega la pestanya activa
					carregaTab($('#definicioProces-pipelles li#'+$('.active').attr('id')+' a').attr('href'));
				});
			});
		//]]>
		</script>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='definicio.proces.pipelles.cap'/></div>
	</c:otherwise>
</c:choose>
</body>
</html>
