<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<%--<link href="<c:url value="/css/exp-doc.css"/>" rel="stylesheet"/>--%>

<style type="text/css">
	.container-fluid {padding-right: 10px; padding-left: 10px; margin-right: auto; margin-left: auto;}
	.py-3 {padding-top: 1em !important; padding-bottom: 0em !important;}
	.py-4 {padding-top: 1.5em !important; padding-bottom: 1.5em !important;}
	.mtop-3 {margin-top: 1em !important;}
	.mtop-4 {margin-top: 1.5em !important;}
	.pointer {cursor: pointer;}
	.ch-titol {margin: 4px 0px 0px 0px;}

	.previs-icon {left: 2.5em; border-radius: 0.75em; width: 180px; height: 40px; background-position: 50%; text-align: center!important; margin-top: -1.5em!important; fill: currentColor; stroke: none; display: inline-block; color: #111;}
	.previs-icon > i {top: 24%; position: relative;}
	.previs-text {top: 24%; position: relative; margin-left: 0.5rem !important;}
	.body-ocult {display: none;}

	.card {--bs-card-spacer-y: 1em; --bs-card-spacer-x: 1em; --bs-card-title-spacer-y: 0.5em; --bs-card-border-width: 0; --bs-card-border-color: rgba(0, 0, 0, 0.125); --bs-card-border-radius: 0.75em; --bs-card-box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06); --bs-card-inner-border-radius: 0.7em; --bs-card-cap-padding-y: 0.5em; --bs-card-cap-padding-x: 1em; --bs-card-cap-bg: #fff; --bs-card-bg: #fff; --bs-card-img-overlay-padding: 1em; --bs-card-group-margin: 0.75em; position: relative; display: flex; flex-direction: column; min-width: 0; /*height: var(--bs-card-height);*/ word-wrap: break-word; background-color: var(--bs-card-bg); background-clip: border-box; border: var(--bs-card-border-width) solid var(--bs-card-border-color); border-radius: var(--bs-card-border-radius);}
	.card > hr {margin-right: 0; margin-left: 0; margin-top: 2px; margin-bottom: 7px;}
    .card > .list-group {border-top: inherit; border-bottom: inherit;}
    .card > .list-group:first-child {border-top-width: 0; border-top-left-radius: var(--bs-card-inner-border-radius); border-top-right-radius: var(--bs-card-inner-border-radius);}
    .card > .list-group:last-child {border-bottom-width: 0; border-bottom-right-radius: var(--bs-card-inner-border-radius); border-bottom-left-radius: var(--bs-card-inner-border-radius);}
    .card > .card-header + .list-group, .card > .list-group + .card-footer {border-top: 0;}
    .card-body {flex: 1 1 auto; padding: var(--bs-card-spacer-y) var(--bs-card-spacer-x); color: var(--bs-card-color);}
    .card-title {margin-bottom: var(--bs-card-title-spacer-y);}
    .card-subtitle {margin-top: calc(-0.5 * var(--bs-card-title-spacer-y));}
    .card-subtitle, .card-text:last-child {margin-bottom: 0;}
    .card-link + .card-link {margin-left: var(--bs-card-spacer-x);}
    .card-header {padding: var(--bs-card-cap-padding-y) var(--bs-card-cap-padding-x) var(--bs-card-cap-padding-y) 0.25em; margin-bottom: 0; color: var(--bs-card-cap-color); background-color: var(--bs-card-cap-bg); border-bottom: var(--bs-card-border-width) solid var(--bs-card-border-color);}
    .card-header:first-child {border-radius: var(--bs-card-inner-border-radius) var(--bs-card-inner-border-radius) 0 0;}
    .card-footer {padding: var(--bs-card-cap-padding-y) var(--bs-card-cap-padding-x); color: var(--bs-card-cap-color); background-color: var(--bs-card-cap-bg); border-top: var(--bs-card-border-width) solid var(--bs-card-border-color);}
    .card-footer:last-child {border-radius: 0 0 var(--bs-card-inner-border-radius) var(--bs-card-inner-border-radius);}
    .card-header-tabs {margin-right: calc(-0.5 * var(--bs-card-cap-padding-x)); margin-bottom: calc(-1 * var(--bs-card-cap-padding-y)); margin-left: calc(-0.5 * var(--bs-card-cap-padding-x));	border-bottom: 0;}
    .card-header-tabs .nav-link.active {background-color: var(--bs-card-bg); border-bottom-color: var(--bs-card-bg);}
    .card-header-pills {margin-right: calc(-0.5 * var(--bs-card-cap-padding-x)); margin-left: calc(-0.5 * var(--bs-card-cap-padding-x));}
    .card-img-overlay {position: absolute; top: 0; right: 0; bottom: 0; left: 0; padding: var(--bs-card-img-overlay-padding); border-radius: var(--bs-card-inner-border-radius);}
    .card-img, .card-img-bottom, .card-img-top {width: 100%;}
    .card-img, .card-img-top {border-top-left-radius: var(--bs-card-inner-border-radius); border-top-right-radius: var(--bs-card-inner-border-radius);}
    .card-img, .card-img-bottom {border-bottom-right-radius: var(--bs-card-inner-border-radius); border-bottom-left-radius: var(--bs-card-inner-border-radius);}

    .card-icon {border-radius: 0.5em; width: 54px; height: 35px; background-position: 50%; text-align: center!important; margin-top: -0.25em!important; fill: currentColor; stroke: none; display: inline-block; color: #111;}
    .card-icon > i {top: 24%; position: relative; padding-right: 2px; padding-left: 2px;}
    h6 {font-size: 1em !important; line-height: 1.625 !important; font-weight: bold !important;}
    .bg-gradient-primary {background-image: linear-gradient(195deg, #ec407a, #d81b60);}
    .bg-gradient-secondary {background-image: linear-gradient(195deg, #747b8a, #495361);}
    .bg-gradient-success {background-image: linear-gradient(195deg, #66bb6a, #43a047);}
    .bg-gradient-info {background-image: linear-gradient(195deg, #49a3f1, #1a73e8);}
    .bg-gradient-warning {background-image: linear-gradient(195deg, #ffa726, #fb8c00);}
    .bg-gradient-danger {background-image: linear-gradient(195deg, #ef5350, #e53935);}
    .bg-gradient-light {background-image: linear-gradient(195deg, #ebeff4, #ced4da);}
    .bg-gradient-dark {background-image: linear-gradient(195deg, #42424a, #191919);}
    .shadow-primary {box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.14), 0 7px 10px -5px rgba(233, 30, 99, 0.4) !important;}
    .shadow-secondary {box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.14), 0 7px 10px -5px hsla(0, 0%, 82%, 0.4) !important;}
    .shadow-info {box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.14), 0 7px 10px -5px rgba(0, 188, 212, 0.4) !important;}
    .shadow-warning {box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.14), 0 7px 10px -5px rgba(255, 152, 0, 0.4) !important;}
    .shadow-success {box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.14), 0 7px 10px -5px rgba(76, 175, 80, 0.4) !important;}
    .shadow-danger {box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.14), 0 7px 10px -5px rgba(244, 67, 54, 0.4) !important;}
    .shadow-dark {box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.14), 0 7px 10px -5px rgba(64, 64, 64, 0.4) !important;}
    .shadow-light {box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.14), 0 7px 10px -5px rgba(233, 30, 99, 0.4) !important;}
    .position-static {position: static !important;}
    .position-relative {position: relative !important;}
    .position-absolute {position: absolute !important;}
    .position-fixed {position: fixed !important;}
    .position-sticky {position: sticky !important;}
    .text-start {text-align: left !important;}
    .text-end {text-align: right !important;}
    .text-center {text-align: center !important;}
    .text-decoration-underline {text-decoration: underline !important;}
    .text-decoration-line-through {text-decoration: line-through !important;}
    .text-lowercase {text-transform: lowercase !important;}
    .text-uppercase {text-transform: uppercase !important;}
    .text-capitalize {text-transform: capitalize !important;}
    .text-break {word-break: break-word !important;}
    .text-primary {color: #e91e63 !important;}
    .text-secondary {color: #7b809a !important;}
    .text-success {color: #4caf50 !important;}
    .text-info {color: #1a73e8 !important;}
    .text-warning {color: #fb8c00 !important;}
    .text-danger {color: #f44335 !important;}
    .text-light {color: #f0f2f5 !important;}
    .text-dark {color: #344767 !important;}
    .text-white {color: #fff !important;}
    .text-body {color: #7b809a !important;}
    .text-rose {color: #e91e63 !important;}
    .text-muted {color: #6c757d !important;}
    .text-opacity-25 {--bs-text-opacity: 0.25;}
    .text-opacity-50 {--bs-text-opacity: 0.5;}
    .text-opacity-75 {--bs-text-opacity: 0.75;}
    .text-opacity-100 {--bs-text-opacity: 1;}
    .d-inline {display: inline !important;}
    .d-inline-block {display: inline-block !important;}
    .d-block {display: block !important;}
    .d-grid {display: grid !important;}
    .d-table {display: table !important;}
    .d-table-row {display: table-row !important;}
    .d-table-cell {display: table-cell !important;}
    .d-flex {display: flex !important;}
    .d-inline-flex {display: inline-flex !important;}
    .d-none {display: none !important;}.list-group {--bs-list-group-color: inherit; --bs-list-group-bg: #fff; --bs-list-group-border-color: rgba(0, 0, 0, 0.125); --bs-list-group-border-width: 1px; --bs-list-group-border-radius: 0.375em; --bs-list-group-item-padding-x: 1em; --bs-list-group-item-padding-y: 0.5em; --bs-list-group-action-color: #495057; --bs-list-group-action-hover-color: #495057; --bs-list-group-action-hover-bg: #f8f9fa; --bs-list-group-action-active-color: #7b809a; --bs-list-group-action-active-bg: #f0f2f5; --bs-list-group-disabled-color: #6c757d; --bs-list-group-disabled-bg: #fff; --bs-list-group-active-color: #fff; --bs-list-group-active-bg: #e91e63; --bs-list-group-active-border-color: #e91e63; display: flex; flex-direction: column; padding-left: 0; margin-bottom: 0; border-radius: var(--bs-list-group-border-radius);}
    .flex-fill {flex: 1 1 auto !important;}
    .flex-row {flex-direction: row !important;}
    .flex-column {flex-direction: column !important;}
    .flex-row-reverse {flex-direction: row-reverse !important;}
    .flex-column-reverse {flex-direction: column-reverse !important;}
    .flex-grow-0 {flex-grow: 0 !important;}
    .flex-grow-1 {flex-grow: 1 !important;}
    .flex-shrink-0 {flex-shrink: 0 !important;}
    .flex-shrink-1 {flex-shrink: 1 !important;}
    .flex-wrap {flex-wrap: wrap !important;}
    .flex-nowrap {flex-wrap: nowrap !important;}
    .flex-wrap-reverse {flex-wrap: wrap-reverse !important;}
    .justify-content-start {justify-content: flex-start !important;}
    .justify-content-end {justify-content: flex-end !important;}
    .justify-content-center {justify-content: center !important;}
    .justify-content-between {justify-content: space-between !important;}
    .justify-content-around {justify-content: space-around !important;}
    .justify-content-evenly {justify-content: space-evenly !important;}
    .align-items-start {align-items: flex-start !important;}
    .align-items-end {align-items: flex-end !important;}
    .align-items-center {align-items: center !important;}
    .align-items-baseline {align-items: baseline !important;}
    .align-items-stretch {align-items: stretch !important;}
    .align-content-start {align-content: flex-start !important;}
    .align-content-end {align-content: flex-end !important;}
    .align-content-center {align-content: center !important;}
    .align-content-between {align-content: space-between !important;}
    .align-content-around {align-content: space-around !important;}
    .align-content-stretch {align-content: stretch !important;}
    .align-self-auto {align-self: auto !important;}
    .align-self-start {align-self: flex-start !important;}
    .align-self-end {align-self: flex-end !important;}
    .align-self-center {align-self: center !important;}
    .align-self-baseline {align-self: baseline !important;}
    .align-self-stretch {align-self: stretch !important;}
    .text-lg {font-size: 1.125em !important;}
    .text-md {font-size: 1em !important;}
    .text-sm {font-size: 0.875em !important;}
    .text-xs {font-size: 0.75em !important;}
    .text-xxs {font-size: 0.65em !important;}
    p {line-height: 1; font-weight: 300; margin-bottom: 4px;}
    .text-justify {text-align: justify !important;}
    .text-wrap {white-space: normal !important;}
    .text-nowrap {white-space: nowrap !important;}
    .text-truncate {overflow: hidden; text-overflow: ellipsis; white-space: nowrap;}
    .font-weight-light {font-weight: 300 !important;}
    .font-weight-lighter {font-weight: lighter !important;}
    .font-weight-normal {font-weight: 400 !important;}
    .font-weight-bold {font-weight: 600 !important;}
    .font-weight-bolder {font-weight: 700 !important;}
    .font-italic {font-style: italic !important;}
    .border {border: 1px solid #dee2e6 !important;}
    .border-0 {border: 0 !important; border-width: 0 !important;
    .border-top {border-top: 1px solid #dee2e6 !important;}
    .border-top-0 {border-top: 0 !important;}
    .border-end {border-right: 1px solid #dee2e6 !important;}
    .border-end-0 {border-right: 0 !important;}
    .border-bottom {border-bottom: 1px solid #dee2e6 !important;}
    .border-bottom-0 {border-bottom: 0 !important;}
    .border-start {border-left: 1px solid #dee2e6 !important;}
    .border-start-0 {border-left: 0 !important;}
    .border-primary {border-color: #e91e63 !important;}
    .border-secondary {border-color: #7b809a !important;}
    .border-success {border-color: #4caf50 !important;}
    .border-info {border-color: #1a73e8 !important;}
    .border-warning {border-color: #fb8c00 !important;}
    .border-danger {border-color: #f44335 !important;}
    .border-light {border-color: #f0f2f5 !important;}
    .border-dark {border-color: #344767 !important;}
    .border-white {border-color: #fff !important;}
    .border-1 {border-width: 1px !important;}
    .border-2 {border-width: 2px !important;}
    .border-3 {border-width: 3px !important;}
    .border-4 {border-width: 4px !important;}
    .border-5 {border-width: 5px !important;}
    .border-opacity-10 {--bs-border-opacity: 0.1;}
    .border-opacity-25 {--bs-border-opacity: 0.25;}
    .border-opacity-50 {--bs-border-opacity: 0.5;}
    .border-opacity-75 {--bs-border-opacity: 0.75;}
    .border-opacity-100 {--bs-border-opacity: 1;}

    .list-group-item {position: relative; display: block; padding: var(--bs-list-group-item-padding-y) var(--bs-list-group-item-padding-x); color: var(--bs-list-group-color); background-color: var(--bs-list-group-bg); border: var(--bs-list-group-border-width) solid var(--bs-list-group-border-color);}
    .list-group-item:first-child {border-top-left-radius: inherit; border-top-right-radius: inherit;}
    .list-group-item:last-child {border-bottom-right-radius: inherit; border-bottom-left-radius: inherit;}

    html * {
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
    }
    *, :after, :before {
        box-sizing: border-box;
    }
</style>
<script type="text/javascript">
	// <![CDATA[
	$(document).ready( function() {

	});
	// ]]>
</script>

<div class="container-fluid">
	<div class="row mtop-3">

		<%-- REGISTRE => detall.registreDetall --%>
		<c:set var="registre" value="${detall.registreDetall}"/>
		<div class="col-xl-3 col-sm-3 mb-xl-0 mb-4">
			<div class="card">
				<div class="card-header pointer p-3 pt-2">
					<div class="card-icon position-absolute <c:choose><c:when test="${detall.registrat}">bg-gradient-success shadow-success</c:when><c:otherwise>bg-gradient-secondary shadow-secondary</c:otherwise></c:choose>">
						<i class="fa fa-book fa-inverse fa-1x"></i>
						<i class="fa fa-caret-down fa-inverse fa-1x"></i>
					</div>
					<div class="text-end pt-1">
						<h6 class="ch-titol">Registre</h6>
					</div>
				</div>
				<div class="card-body body-ocult">
					<ul class="list-group">
						<%-- Número de registre--%>
						<li class="list-group-item d-flex justify-content-between border-0">
							<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.info.numero_registre"/></div>
							<div class="d-flex flex-column text-sm">${registre != null ? registre.registreNumero : '--'}</div>
						</li>
						<%-- Data de registre--%>
						<li class="list-group-item d-flex justify-content-between border-0">
							<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.info.camp.registre.data"/></div>
							<div class="d-flex flex-column text-sm">${registre != null ? '<fmt:formatDate value="${registre.registreData}" pattern="dd/MM/yyyy HH:mm"/>' : '--'}</div>
						</li>
						<%-- Oficina de registre--%>
						<li class="list-group-item d-flex justify-content-between border-0">
							<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.info.camp.registre.oficina"/></div>
							<div class="d-flex flex-column text-sm">${registre != null ? registre.registreOficinaNom : '--'}</div>
						</li>
						<%-- Tipus de registre--%>
						<li class="list-group-item d-flex justify-content-between border-0">
							<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.info.camp.registre.tipus"/></div>
							<div class="d-flex flex-column text-sm">${registre != null ? (registre.registreEntrada ? '<spring:message code="expedient.info.camp.registre.tipus.entrada"/>' : '<spring:message code="expedient.info.camp.registre.tipus.sortida"/>') : '--'}</div>
						</li>
					</ul>
				</div>
				<hr class="dark horizontal my-0">
				<div class="card-footer p-3">
					<c:choose>
						<c:when test="${detall.registrat}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">REGISTRAT</span></p></c:when>
						<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO REGISTRAT</span></p></c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>

		<%-- SIGNATURA => detall.signaturaValidacioDetall --%>
		<c:set var="signatura" value="${detall.signaturaValidacioDetall}"/>
		<div class="col-xl-3 col-sm-3 mb-xl-0 mb-4">
			<div class="card">
				<div class="card-header p-3 pt-2 pointer">
					<div class="card-icon position-absolute <c:choose><c:when test="${detall.signat}">bg-gradient-success shadow-success</c:when><c:otherwise>bg-gradient-secondary shadow-secondary</c:otherwise></c:choose>">
						<i class="fa fa-certificate fa-inverse fa-1x"></i>
						<i class="fa fa-caret-down fa-inverse fa-1x"></i>
					</div>
					<div class="text-end pt-1">
						<h6 class="ch-titol">Signatura</h6>
					</div>
				</div>
				<div class="card-body body-ocult">
					<ul class="list-group">
						<%-- --%>
						<li class="list-group-item d-flex justify-content-between border-0">
							<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.info.numero_registre"/></div>
							<div class="d-flex flex-column text-sm">${registre != null ? registre.registreNumero : '--'}</div>
						</li>
					</ul>
				</div>
				<hr class="dark horizontal my-0">
				<div class="card-footer p-3">
					<c:choose>
						<c:when test="${detall.signat}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">SIGNAT</span></p></c:when>
						<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO SIGNAT</span></p></c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>

		<%-- PSIGNA => detall.signaturaValidacioDetall --%>
		<c:set var="signatura" value="${detall.psignaDetall}"/>
		<div class="col-xl-3 col-sm-3 mb-xl-0 mb-4">
			<div class="card">
				<div class="card-header pointer p-3 pt-2">
					<div class="card-icon position-absolute <c:choose><c:when test="${detall.psignaPendent}">bg-gradient-success shadow-success</c:when><c:otherwise>bg-gradient-secondary shadow-secondary</c:otherwise></c:choose>">
						<i class="fa fa-clock-o fa-inverse fa-1x"></i>
						<i class="fa fa-caret-down fa-inverse fa-1x"></i>
					</div>
					<div class="text-end pt-1">
						<h6 class="ch-titol">Pendent de Portasignatures</h6>
					</div>
				</div>
				<div class="card-body body-ocult">
					<ul class="list-group">
						<%-- --%>
						<li class="list-group-item d-flex justify-content-between border-0">
							<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.info.numero_registre"/></div>
							<div class="d-flex flex-column text-sm">${registre != null ? registre.registreNumero : '--'}</div>
						</li>
					</ul>
				</div>
				<hr class="dark horizontal my-0">
				<div class="card-footer p-3">
					<c:choose>
						<c:when test="${detall.psignaPendent}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">PENDENT de Portasignatures</span></p></c:when>
						<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO PENDENT de Portasignatures</span></p></c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>

		<%-- NOTIFICACIONS => detall.notificacions --%>
		<c:set var="notificacions" value="${detall.notificacions}"/>
		<div class="col-xl-3 col-sm-3 mb-xl-0 mb-4">
			<div class="card">
				<div class="card-header pointer p-3 pt-2">
					<div class="card-icon position-absolute <c:choose><c:when test="${detall.notificat}">bg-gradient-success shadow-success</c:when><c:otherwise>bg-gradient-secondary shadow-secondary</c:otherwise></c:choose>">
						<i class="fa fa-paper-plane-o fa-inverse fa-1x"></i>
						<i class="fa fa-caret-down fa-inverse fa-1x"></i>
					</div>
					<div class="text-end pt-1">
						<h6 class="ch-titol">Notificacions</h6>
					</div>
				</div>
				<div class="card-body body-ocult">
					<ul class="list-group">
						<li class="list-group-item d-flex justify-content-between border-0">
							<div class="d-flex flex-column text-dark font-weight-bold text-sm">Destinatari</div>
							<div class="d-flex flex-column text-dark font-weight-bold text-sm">Estat</div>
						</li>
						<c:forEach var="notificacio" items="${notificacions}" varStatus="status">
							<c:forEach var="enviament" items="${notificacio.enviaments}" varStatus="statuse">
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-sm">
										<span class="label label-default">${notificacio.enviamentTipus == "NOTIFICACIO" ? 'N' : 'C'}</span>
										<span>
										<h6>${enviament.titular.nomSencer}</h6>
										<p>${notificacio.enviatData}</p>
									</span>
									</div>
									<div class="d-flex flex-column text-sm">
										<c:set var="ecolor" value="default"/>
										<c:choose>
											<c:when test="${enviament.estat == 'LLEGIDA' or
														enviament.estat == 'NOTIFICADA'}"><c:set var="ecolor" value="success"/></c:when>
											<c:when test="${enviament.estat == 'ABSENT' or
														enviament.estat == 'DESCONEGUT' or
														enviament.estat == 'ADRESA_INCORRECTA' or
														enviament.estat == 'MORT' or
														enviament.estat == 'EXTRAVIADA' or
														enviament.estat == 'SENSE_INFORMACIO' or
														enviament.estat == 'ERROR_ENTREGA' or
														enviament.estat == 'EXPIRADA'}"><c:set var="ecolor" value="Danger"/></c:when>
										</c:choose>
										<span class="label label-${ecolor}">${enviament.estat}</span>
									</div>
								</li>
							</c:forEach>
						</c:forEach>
					</ul>
				</div>
				<hr class="dark horizontal my-0">
				<div class="card-footer p-3">
					<c:choose>
						<c:when test="${detall.notificat}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">NOTIFICAT</span></p></c:when>
						<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO NOTIFICAT</span></p></c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>


	</div>
	<div class="row mtop-2">

		<%-- ARXIU/NTI => detall.arxiuDetall --%>
		<c:set var="arxiuDetall" value="${detall.arxiuDetall}"/>
		<c:set var="ntiDetall" value="${detall.ntiDetall}"/>
		<div class="col-lg-6 col-md-6 mtop-3 mb-4">
			<div class="card">
				<div class="card-header pointer p-3 pt-2">
					<div class="card-icon position-absolute <c:choose><c:when test="${detall.nti || detall.arxiu}">bg-gradient-success shadow-success</c:when><c:otherwise>bg-gradient-secondary shadow-secondary</c:otherwise></c:choose>">
						<i class="fa fa-bookmark fa-inverse fa-1x"></i>
						<i class="fa fa-caret-down fa-inverse fa-1x"></i>
					</div>
					<div class="text-end pt-1">
						<h6 class="ch-titol">
							<c:choose>
								<c:when test="${detall.arxiu}">ARXIU</c:when>
								<c:when test="${detall.nti}">NTI</span></p></c:when>
								<c:otherwise>ARXIU / NTI</c:otherwise>
							</c:choose>
						</h6>
					</div>
				</div>
				<div class="card-body body-ocult">

				<!-- Formulari per incoporar el document a l'Arxiu si es detecta error de no Uuid -->
				<c:if test="${errorArxiuNoUuid}">
					<div class="row alert alert-danger" style="margin: 0px; margin-bottom: 10px;">
						<div class="col-sm-10">
							<p><spring:message code="expedient.document.arxiu.error.uuidnoexistent.info"/></p>
						</div>
<%--						TODO: --%>
<%--						<div class="col-sm-2">--%>
<%--							<form action="incoporarArxiu" method="post">--%>
<%--								<button id="incorporarArxiuButton" type="submit" class="btn btn-default" title="<spring:message code='expedient.document.arxiu.migrar.arxiu'></spring:message>">--%>
<%--									<span class="fa fa-upload"></span>--%>
<%--									<spring:message code="comu.boto.migrar"></spring:message>--%>
<%--								</button>--%>
<%--							<form>--%>
<%--						</div>--%>
					</div>
				</c:if>

				<c:if test="${not empty detall.arxiuDetall}">
					<ul class="nav nav-pills" role="tablist">
						<li id = role="presentation" class="active"><a class="pill-link" href="#nti_${detall.documentStoreId}" aria-controls="nti" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.nti"/></a></li>
						<li role="presentation"><a class="pill-link" href="#info_${detall.documentStoreId}" aria-controls="info" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.info"/></a></li>
						<c:if test="${not empty arxiuDetall.fills}"><li role="presentation"><a class="pill-link" href="#fills_${detall.documentStoreId}" aria-controls="fills" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.fills"/> <span class="badge badge-default">${fn:length(arxiuDetall.fills)}</span></a></li></c:if>
						<c:if test="${not empty arxiuDetall.firmes}"><li role="presentation"><a class="pill-link" href="#firmes_${detall.documentStoreId}" aria-controls="firmes" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.firmes"/> <span class="badge badge-default">${fn:length(arxiuDetall.firmes)}</span></a></li></c:if>
						<c:if test="${not empty arxiuDetall.metadadesAddicionals}"><li role="presentation"><a class="pill-link" href="#metadades_${detall.documentStoreId}" aria-controls="metadades" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.metadades"/></a></li></c:if>
					</ul>

					<div class="tab-content">
						<%-- Metadades NTI --%>
						<div id="nti_${detall.documentStoreId}" class="tab-pane in active">
				</c:if>
<%--							metadades nti--%>
							<ul class="list-group">
								<%-- --%>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.version"/></div>
									<div class="d-flex flex-column text-sm">${ntiDetall.ntiVersion != null ? ntiDetall.ntiVersion : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.identificador"/></div>
									<div class="d-flex flex-column text-sm">${ntiDetall.ntiIdentificador != null ? ntiDetall.ntiIdentificador : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.organo"/></div>
									<div class="d-flex flex-column text-sm">${ntiDetall.ntiOrgano != null ? ntiDetall.ntiOrgano : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.fecha.captura"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty detall.dataCreacio}"><fmt:formatDate value="${detall.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.origen"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty ntiDetall.ntiOrigen}"><spring:message code="nti.document.origen.${ntiDetall.ntiOrigen}"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.estado.elaboracion"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty ntiDetall.ntiEstadoElaboracion}"><spring:message code="nti.document.estado.elaboracion.${ntiDetall.ntiEstadoElaboracion}"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.nombre.formato"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty ntiDetall.ntiNombreFormato}"><spring:message code="nti.document.format.${ntiDetall.ntiNombreFormato}"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.tipo.documental"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty ntiDetall.ntiTipoDocumental}"><spring:message code="nti.document.tipo.documental.${ntiDetall.ntiTipoDocumental}"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.iddoc.origen"/></div>
									<div class="d-flex flex-column text-sm">${ntiDetall.ntiIdDocumentoOrigen != null ? ntiDetall.ntiIdDocumentoOrigen : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.tipo.firma"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty ntiDetall.ntiTipoFirma}">${ntiDetall.ntiTipoFirma}<c:if test="${not empty detall.ntiCsv}">, CSV</c:if></c:when><c:otherwise>--</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.csv"/></div>
									<div class="d-flex flex-column text-sm">${ntiDetall.ntiCsv != null ? ntiDetall.ntiCsv : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.defgen.csv"/></div>
									<div class="d-flex flex-column text-sm">${ntiDetall.ntiDefinicionGenCsv != null ? ntiDetall.ntiDefinicionGenCsv : '--'}</div>
								</li>
							</ul>
							<c:if test="${detall.errorMetadadesNti}">
								<div class="row alert alert-danger" style="margin: 0px; margin-bottom: 10px;">
									<div class="col-sm-10">
										<p><spring:message code="expedient.metadades.nti.dades.error.info"/></p>
									</div>
<%--					TODO:--%>
<%--									<div class="col-sm-2">--%>
<%--										<form  action="<c:url value="/v3/expedient/${expedient.id}/metadadesNti/arreglar"/>" method="post">--%>
<%--											<button id="arreglarNtiButton" type="submit" class="btn btn-default" title="<spring:message code='expedient.metadades.nti.dades.error.arreglar.info'></spring:message>">--%>
<%--												<span class="fa fa-cog"></span>--%>
<%--												<spring:message code="expedient.metadades.nti.dades.error.arreglar"></spring:message>--%>
<%--											</button>--%>
<%--										<form>--%>
<%--									</div>--%>
								</div>
							</c:if>
				<c:if test="${not empty arxiuDetall}">
						</div>

						<%-- Informació arxiu --%>
						<div id="info_${detall.documentStoreId}" class="tab-pane">
							<ul class="list-group">
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.identificador"/></div>
									<div class="d-flex flex-column text-sm">${arxiuDetall.identificador != null ? arxiuDetall.identificador : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.nom"/></div>
									<div class="d-flex flex-column text-sm">${arxiuDetall.nom != null ? arxiuDetall.nom : '--'}</div>
								</li>
								<c:if test="${not empty arxiuDetall.serieDocumental}">
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.serie.doc"/></div>
										<div class="d-flex flex-column text-sm">${arxiuDetall.serieDocumental != null ? arxiuDetall.serieDocumental : '--'}</div>
									</li>
								</c:if>
							</ul>

							<c:if test="${not empty arxiuDetall.contingutTipusMime or not empty arxiuDetall.contingutArxiuNom}">
							<hr class="dark horizontal my-0">

							<div class="text-dark font-weight-bold"><spring:message code="expedient.metadades.nti.grup.contingut"/></div>
							<ul class="list-group">
								<c:if test="${not empty arxiuDetall.contingutTipusMime}">
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.contingut.tipus.mime"/></div>
									<div class="d-flex flex-column text-sm">${arxiuDetall.contingutTipusMime != null ? arxiuDetall.contingutTipusMime : '--'}</div>
								</li>
								</c:if>
								<c:if test="${not empty arxiuDetall.contingutArxiuNom}">
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.contingut.arxiu.nom"/></div>
									<div class="d-flex flex-column text-sm">${arxiuDetall.contingutArxiuNom != null ? arxiuDetall.contingutArxiuNom : '--'}</div>
								</li>
								</c:if>
							</ul>
							</c:if>

							<c:if test="${not empty arxiuDetall.eniIdentificador}">
								<hr class="dark horizontal my-0">

								<div class="text-dark font-weight-bold"><spring:message code="expedient.metadades.nti.grup.metadades"/></div>
								<ul class="list-group">
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.versio"/></div>
										<div class="d-flex flex-column text-sm">${arxiuDetall.eniVersio != null ? arxiuDetall.eniVersio : '--'}</div>
									</li>
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.identificador"/></div>
										<div class="d-flex flex-column text-sm">${arxiuDetall.eniIdentificador != null ? arxiuDetall.eniIdentificador : '--'}</div>
									</li>
									<c:if test="${not empty arxiuDetall.eniOrgans}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.organs"/></div>
											<div class="d-flex flex-column text-sm">
												<c:if test="${empty arxiuDetall.eniOrgans}">--</c:if>
												<c:forEach var="organ" items="${arxiuDetall.eniOrgans}" varStatus="status">
													${organ}<c:if test="${not status.last}">,</c:if>
												</c:forEach>
											</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniDataObertura}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.data.obertura"/></div>
											<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty arxiuDetall.eniDataObertura}"><fmt:formatDate value="${arxiuDetall.eniDataObertura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniClassificacio}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.classificacio"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniClassificacio != null ? arxiuDetall.eniClassificacio : '--'}</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniEstat}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.estat"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniEstat != null ? arxiuDetall.eniEstat : '--'}</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniDataCaptura}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.data.captura"/></div>
											<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty arxiuDetall.eniDataCaptura}"><fmt:formatDate value="${arxiuDetall.eniDataCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniOrigen}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.origen"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniOrigen != null ? arxiuDetall.eniOrigen : '--'}</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniEstatElaboracio}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.estat.elab"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniEstatElaboracio != null ? arxiuDetall.eniEstatElaboracio : '--'}</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniTipusDocumental}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.tipus.doc"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniTipusDocumental != null ? arxiuDetall.eniTipusDocumental : '--'}</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniFormat}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.format.nom"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniFormat != null ? arxiuDetall.eniFormat : '--'}</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniExtensio}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.format.ext"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniExtensio != null ? arxiuDetall.eniExtensio : '--'}</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniInteressats}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.interessats"/></div>
											<div class="d-flex flex-column text-sm">
												<c:if test="${empty arxiuDetall.eniInteressats}">--</c:if>
												<c:forEach var="interessat" items="${arxiuDetall.eniInteressats}" varStatus="status">
													${interessat}<c:if test="${not status.last}">,</c:if>
												</c:forEach>
											</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniDocumentOrigenId}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.doc.orig.id"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniDocumentOrigenId != null ? arxiuDetall.eniDocumentOrigenId : '--'}</div>
										</li>
									</c:if>
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.firma.csv"/></div>
										<div class="d-flex flex-column text-sm">${arxiuDetall.eniCsv != null ? arxiuDetall.eniCsv : '--'}</div>
									</li>
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.firma.csvdef"/></div>
										<div class="d-flex flex-column text-sm">${arxiuDetall.eniCsvDef != null ? arxiuDetall.eniCsvDef : '--'}</div>
									</li>
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.arxiuEstat"/></div>
										<div class="d-flex flex-column text-sm">${arxiuDetall.arxiuEstat != null ? arxiuDetall.arxiuEstat : "--"}</div>
									</li>
								</ul>
							</c:if>
						</div>

						<%-- Fills --%>
						<c:if test="${not empty arxiuDetall.fills}">
						<div id="fills_${detall.documentStoreId}" class="tab-pane">
							<ul class="list-group">
								<c:forEach var="fill" items="${arxiuDetall.fills}" varStatus="status">
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-sm">${fill.tipus}</div>
										<div class="d-flex flex-column text-sm">${fill.nom}</div>
									</li>
								</c:forEach>
							</ul>
						</div>
						</c:if>

						<%-- Firmes --%>
						<c:if test="${not empty arxiuDetall.firmes}">
						<div id="firmes_${detall.documentStoreId}" class="tab-pane">
							<ul class="list-group">
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.firma.tipus"/></div>
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.firma.perfil"/></div>
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.firma.contingut"/></div>
								</li>
								<c:forEach var="firma" items="${arxiuDetall.firmes}" varStatus="status">
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-sm">${firma.tipus}</div>
										<div class="d-flex flex-column text-sm">${firma.perfil}</div>
										<div class="d-flex flex-column text-sm">
											<c:choose>
												<c:when test="${firma.tipus == 'CSV'}">${firma.contingutComString}</c:when>
												<c:when test="${firma.tipus == 'XADES_DET' or firma.tipus == 'CADES_DET'}">
													<a href="<c:url value='/v3/expedient/${expedientId}/document/${detall.documentStoreId}/firma/${status.index}/descarregar'></c:url>" class="btn btn-default btn-sm pull-right">
														<span class="fa fa-download"  title="<spring:message code="comu.boto.descarregar"/>"></span>
													</a>
												</c:when>
												<c:otherwise></c:otherwise>
											</c:choose>
										</div>
									</li>
								</c:forEach>
							</ul>
						</div>
						</c:if>

						<%-- Metadades Arxiu --%>
						<c:if test="${not empty arxiuDetall.metadadesAddicionals}">
						<div id="metadades_${detall.documentStoreId}" class="tab-pane">
							<ul class="list-group">
							<c:forEach var="metadada" items="${arxiuDetall.metadadesAddicionals}" varStatus="status">
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm">${metadada.key}</div>
									<div class="d-flex flex-column text-sm">${metadada.value}</div>
								</li>
							</c:forEach>
							</ul>
						</div>
						</c:if>
					</div>
				</c:if>
				</div>

				<hr class="dark horizontal my-0">

				<div class="card-footer p-3">
					<c:choose>
						<c:when test="${detall.arxiu}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">ARXIVAT</span></p></c:when>
						<c:when test="${detall.nti}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">CUSTODIAT</span></p></c:when>
						<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO ARXIVAT ni CUSTODIAT</span></p></c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>

		<%-- ANOTACIO => detall.arxiuDetall --%>
		<c:set var="anotacio" value="${detall.anotacio}"/>
		<div class="col-lg-6 col-md-6 mtop-3 mb-4">
			<div class="card">
				<div class="card-header pointer p-3 pt-2">
					<div class="card-icon position-absolute <c:choose><c:when test="${detall.deAnotacio}">bg-gradient-success shadow-success</c:when><c:otherwise>bg-gradient-secondary shadow-secondary</c:otherwise></c:choose>">
						<i class="fa fa-file-text fa-inverse fa-1x"></i>
						<i class="fa fa-caret-down fa-inverse fa-1x"></i>
					</div>
					<div class="text-end pt-1">
						<h6 class="ch-titol">Anotació origen</h6>
					</div>
				</div>
				<div class="card-body body-ocult">
					<ul class="list-group">
						<%-- --%>
						<li class="list-group-item d-flex justify-content-between border-0">
							<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.info.numero_registre"/></div>
							<div class="d-flex flex-column text-sm">${registre != null ? registre.registreNumero : '--'}</div>
						</li>
					</ul>
				</div>
				<hr class="dark horizontal my-0">
				<div class="card-footer p-3">
					<c:choose>
						<c:when test="${detall.deAnotacio}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">PROVÉ d'una ANOTACIÓ</span></p></c:when>
						<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO PROVÉ d'una ANOTACIÓ</span></p></c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</div>
	<footer class="footer py-4">
		<div class="row mtop-4">
			<div class="previs-icon position-absolute bg-gradient-info shadow-info text-white font-weight-bold pointer">
				<i class="fa fa-eye fa-inverse fa-1x"></i>
				<span class="previs-text">Previsualització</span>
				<i class="fa fa-caret-down fa-inverse fa-1x"></i>
			</div>
			<div class="viewer mtop-4" data-documentid="${detall.documentStoreId}" style="display: none; width: 100%;">
				<iframe class="viewer-iframe" width="100%" height="540" frameBorder="0" style="padding: 15px;"></iframe>
			</div>
		</div>
	</footer>
</div>