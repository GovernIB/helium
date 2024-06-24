<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<% pageContext.setAttribute("avisos",net.conselldemallorca.helium.webapp.v3.helper.AvisHelper.getAvisos(request));%>
<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="utf-8">
	<spring:message code="decorator.titol.default" var="titolDefault"/>
	<title>Helium: <decorator:title default="${titolDefault}"/></title>
	<meta name="viewport" content="width=device-width,initial-scale=1.0"/>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
	<meta name="description" content=""/>
	<meta name="author" content=""/>
	<link href="<c:url value="/webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/font-awesome/4.5.0/css/font-awesome.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/estils-v3.css"/>" rel="stylesheet">
	<link rel="shortcut icon" href="<c:url value="/img/ico/favicon.png"/>">
	<link rel="icon" type="image/png" href="<c:url value="/img/ico/favicon.png"/>">
	<script src="<c:url value="/webjars/jquery/1.12.0/dist/jquery.min.js"/>" type="text/javascript"></script>
	<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<script type='text/javascript' src="<c:url value="/js/respond.js"/>"></script>
	<![endif]-->
	<script src="<c:url value="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<style>
		body {
			background-image:url(<c:url value="/img/background-pattern.jpg"/>);
			color:#666666;
			padding-top: 148px;
		}
		.cabecera_reducida {height: 100px;}
		.cabecera_reducida #govern-logo {margin-top: -15px;}
		.cabecera_reducida #govern-logo img {height: 60px;}
		.cabecera_reducida #app-logo {margin-top: -5px;}
		.cabecera_reducida #app-logo > img {width :80%;}
		.cabecera_reducida .navbar-app .navbar-nav {padding-top: 4px;}
		.cabecera_reducida .nav.navbar-nav.navbar-right {padding-top: 8px;}
		.cabecera_reducida .navbar-btn {margin-top: 0px;}
		.cabecera_reducida .navbar-btn .btn {padding: 3px 12px;}
		.cabecera_reducida-main {margin-top: -30px;}
		.navbar-right {margin-right: 0px;}
		.nav-consulta-tipus {    
			color: black;
		    list-style-type: none;
		    margin-left: 30px;
		    text-align: left;
			padding-right: 15px;
		}
		.nav-consulta-tipus-generic{
			color: black;
		    text-align: left;
			padding-right: 15px;
		}
		
		.nav-consulta-tipus a {
			padding: 3px 0px !important;
		}
		.big-size{
			font-size: large;
		}
		.text-limit{
		    display:inline-block;
		    white-space: nowrap;
		    overflow:hidden !important;
		    text-overflow: ellipsis;
		    margin-bottom: -7px;
		}
		
		.text-limit.w475{
			max-width:475px;
		}
		.text-limit.w900{
			max-width:900px;
		}
		
		ul.ul-menu{
			margin-bottom: 5px;
		}
		
		.top-sep{
			margin-top: 20px;
		}
		
		#iniciar-expediente a{
			margin-right: 10px;
			border-bottom-right-radius: 4px;
    		border-top-right-radius: 4px;
		}
		.dada-heretada{
			color:gray !important; 
		}
		#overlay {
			background-color: rgba(0,0,0, 0.25);
			color: #666666;
			position: fixed;
			height: 100%;
			width: 100%;
			z-index: 5000;
			top: 0;
			left: 0;
			float: left;
			text-align: center;
			padding-top: 25%;
		}
		
<c:choose>
	<c:when test="${entornActual.colorFons!=null  && not empty entornActual.colorFons}">
		.navbar-app {
			background-color: ${entornActual.colorFons} !important;
		}
		.navbar-app .list-inline li.dropdown>a {
			background-color: ${entornActual.colorFons} !important;
		}
	</c:when>
	<c:otherwise>
		<c:if test="${!empty globalProperties['app.capsalera.color.fons']}">
			.navbar-app {
				background-color: ${globalProperties['app.capsalera.color.fons']} !important;
			}		
			.navbar-app .list-inline li.dropdown>a { 
				background-color: ${globalProperties['app.capsalera.color.fons']} !important;
			}
		</c:if>		
	</c:otherwise>
</c:choose>
		
<c:choose>
	<c:when test="${entornActual.colorLletra!=null  && not empty entornActual.colorLletra}">
		.navbar-app .list-inline li.dropdown>a {
			color: ${entornActual.colorLletra};
		}
		.caret-white {
			border-top-color: ${entornActual.colorLletra} !important;
		}
		.list-inline.pull-right {
			color: ${entornActual.colorLletra} !important;
		}
		.navbar-app .list-inline li {
			border-right-color: ${entornActual.colorLletra} !important;
		}
		#govern-logo {
			border-right-color: ${entornActual.colorLletra} !important;
		}
	</c:when>
	<c:otherwise>
		<c:if test="${globalProperties['app.capsalera.color.lletra'] !=null  && not empty globalProperties['app.capsalera.color.lletra']}">
			.navbar-app .list-inline li.dropdown>a {
				color: ${globalProperties['app.capsalera.color.lletra']};
			}	
			.caret-white {
				border-top-color: ${globalProperties['app.capsalera.color.lletra']} !important;
			}	
			.list-inline.pull-right {
				color: ${globalProperties['app.capsalera.color.lletra']} !important;
			}
			.navbar-app .list-inline li {
				border-right-color: ${globalProperties['app.capsalera.color.lletra']} !important;
			}
			#govern-logo {
				border-right-color: ${globalProperties['app.capsalera.color.lletra']} !important;
			}
		</c:if>
	</c:otherwise>
</c:choose>
		
		.arrow-top {
			display: none;
			position: fixed;
			bottom: 80px; 
			right: 5px;
			width: 35px;
			height: 35px;
			background-color: #ddd;
			font-size: 25px;
			text-align: center;
			cursor: pointer;
		}
	</style>
	<script type="text/javascript">	
		$(document).ready(function(){

			$('#searchEntorns').on('input', function(){
				searchEntorns($('#searchEntorns').val());
				
			}).click(function(e){
				e.preventDefault();
				e.stopPropagation();
				$('#searchEntorns').focus();
				return false;
			}
			);
			
			$(".dropdown-menu").css("max-height", ($(window).height() - 75) +"px");

			if($(".nav-consulta-tipus").length == 0) {
				$("#btnConsultes").remove();
			}
			
			$('[title]').tooltip({container: 'body', trigger : 'hover'});
			
			$('#topBtn').click(function(){
				// Scroll a l'inici
				window.scrollTo({ top: 0, behavior: 'smooth' });
			})
		}).scroll(function() {
			var y = $(this).scrollTop();
			if (y > 750) {
			$('.arrow-top').fadeIn();
			} else {
			 $('.arrow-top').fadeOut();
			}
		});
		
		function searchEntorns(text) {
			fadeoutMs = 300;
			$('.liEntorn').each(function() {
				$entorn = $(this);
				if (text == ""
						|| $('a', this).text().toLowerCase().includes(text.toLowerCase())) 
				{
					$entorn.show();
					
				} else {
					// amaga l'entorn
					$entorn.hide(fadeoutMs);
				}
			});
		}
		
	</script>
	<decorator:head />
</head>
<body>
	<!-- default v3 -->
	<div class="navbar navbar-default navbar-fixed-top navbar-app <c:if test="${not preferenciesUsuari.cabeceraReducida}">nav-container</c:if><c:if test="${preferenciesUsuari.cabeceraReducida}">cabecera_reducida</c:if>" role="navigation">
		<div class="container container-v3">
			<div class="navbar-header top-sep">
				<div class="navbar-brand">
					<div id="govern-logo" class="pull-left">
						<img src="<c:url value="/img/govern-logo.png?"/>" alt="<spring:message code='decorator.logo.govern'/>" height="80px"/>
					</div>
					<div id="app-logo" class="pull-left">
						<a href="<c:url value="/"/>">
							<img src="<c:url value="/img/logo-helium-w.png"/>" alt="Helium" />
						</a>
					</div>
				</div>
			</div>
			<div class="navbar-collapse collapse">
				<div class="nav navbar-nav navbar-right">
					<ul class="list-inline pull-right ul-menu">
						<li class="dropdown big-size">
							<c:if test="${fn:length(entorns) gt 1}"><a id="menuEntorns" href="#" data-toggle="dropdown"></c:if>
							<span class="fa fa-cubes"></span> <span id="entornActualNom" data-toggle="tooltip" data-placement="bottom" title="${entornActual.nom}" class="text-limit w475" >${entornActual.nom}</span>		
							<ul class="dropdown-menu" id="ulEntorn" role="menu" aria-labelledby="dLabel">
								<li style="display: block; min-width:350px; ">
									<span class="fa fa-search" style="position: absolute;float: left;padding-left:30px;padding-top: 10px;"></span>
									<input id="searchEntorns" class="form-control" 
											placeholder="<spring:message code="perfil.usuari.filtrar"/>"
											 autocomplete="off" spellcheck="false" autocorrect="off" tabindex="1" inline="true"
										 style="padding-left: 30px; margin-left:7%; width:90% ; margin-right:9%; margin-top:2%;">
								</li>
								<c:forEach var="entorn" items="${entorns}">
									<li class="liEntorn">
										<a href="<c:url value="/v3/index"><c:param name="entornCanviarAmbId" value="${entorn.id}"/></c:url>">${entorn.nom}</a>
									</li>
			    				</c:forEach>
							</ul>
						</li>
					</ul>
					<div class="clearfix"></div>
					
					<ul class="list-inline pull-right ul-menu">
						
						<li class="dropdown">
							<span class="fa fa-cube"></span>
							<c:if test="${fn:length(expedientTipusAccessibles) gt 0}"><a href="#" data-toggle="dropdown"></c:if>
							<c:choose>
								<c:when test="${not empty expedientTipusActual}">${expedientTipusActual.nom}</c:when>
								<c:otherwise><spring:message code="comuns.tots.tipus"/></c:otherwise>
							</c:choose>
							<c:if test="${fn:length(expedientTipusAccessibles) gt 0}"><b class="caret caret-white"></b></a></c:if>
							<ul class="dropdown-menu">
								<c:forEach var="expedientTipus" items="${expedientTipusAccessibles}">
									<li><a href="<c:url value="/v3/index"><c:param name="expedientTipusCanviarAmbId" value="${expedientTipus.id}"/></c:url>">${expedientTipus.nom}</a></li>
								</c:forEach>
								<li class="divider"></li>
								<li><a href="<c:url value="/v3/index"><c:param name="expedientTipusCanviarAmbId" value=""/></c:url>"><spring:message code="comuns.tots.tipus"/></a></li>
							</ul>
						</li>
						<li class="dropdown">
							<a href="#" data-toggle="dropdown">
								<span class="fa fa-user"></span> ${dadesPersona.nom} ${dadesPersona.llinatge1} 
								<b class="caret caret-white"></b>
							</a>
							<ul id="ul-perfil" class="dropdown-menu" role="menu" aria-labelledby="dLabel">
								<li><a href="<c:url value="/v3/perfil"/>"><spring:message code='perfil.info.meu_perfil' /></a></li>
								<li><a data-toggle="modal" data-maximized="true" href="<c:url value="/modal/v3/execucionsMassives/user"/>"><spring:message code='comuns.massiu' /></a></li>
			    				<li><a href="<c:url value="/v3/perfil/logout"/>"><i class="fa fa-power-off"></i> <spring:message code='login.desconnectar' /></a></li>
			    			</ul>
			    			<script type="text/javascript">
								$('#ul-perfil a').heliumEvalLink({
									alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
									refrescarAlertes: false,
									refrescarPagina: false
								});
							</script>
						</li>
					</ul>
					<div class="clearfix"></div>
					<div class="btn-group navbar-btn navbar-right">
						<c:choose>
							<c:when test="${entornActual.codi == preferenciesUsuari.defaultEntornCodi && preferenciesUsuari.consultaId != null}">
								<c:url var="expedientsUrl" value="/v3/expedient/consulta/${preferenciesUsuari.consultaId}"/>
							</c:when>
							<c:otherwise>
								<c:url var="expedientsUrl" value="/v3/expedient"/>
							</c:otherwise>
						</c:choose>

						<a id="menuExpedients" class="btn btn-primary" href="${expedientsUrl}"><spring:message code="decorator.menu.expedients"/></a>

						<a id="menuTasques" class="btn btn-primary" href="<c:url value="/v3/tasca"/>"><spring:message code="decorator.menu.tasques"/></a>

						<c:if test="${dadesPersona.admin || potProcessarAnotacions}">
							<a id="menuAnotacions" class="btn btn-primary" href="<c:url value="/v3/anotacio"/>"><spring:message code="decorator.menu.anotacions"/></a>
						</c:if>
						
						<a id="menuFluxosFirma" class="btn btn-primary" href="<c:url value="/v3/fluxeFirma"/>"><spring:message code="decorator.menu.fluxes"/></a>
											
						<c:if test="${potDissenyarEntorn or potDissenyarExpedientTipus}">
							<div id="menuDisseny" class="btn-group" >
								<button class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><spring:message code="comuns.disseny"/> <span class="caret"></span></button>
								<ul class="dropdown-menu">
									<c:if test="${potDissenyarEntorn or potDissenyarExpedientTipus}">
										<li><a id="menuTipusExpedient" href="<c:url value="/v3/expedientTipus"/>"><spring:message code='decorator.menu.disseny.tipus.expedient' /></a></li>
									</c:if>
									<c:if test="${potDissenyarEntorn}">
										<li><a id="menuDefinicioProces" href="<c:url value="/v3/definicioProces"/>"><spring:message code='decorators.entorn.defs_proces' /></a></li>
										<li><a href="<c:url value="/v3/enumeracio"/>"><spring:message code='decorators.entorn.enumeracions' /></a></li>
										<li><a href="<c:url value="/v3/domini"/>"><spring:message code='decorators.entorn.dominis' /></a></li>
									</c:if>
									<c:if test="${dadesPersona.admin}">
										<!-- Accés al menú antic només per usuaris HEL_ADMIN -->
										<li role="separator" class="divider"></li>
										<li><a target="_BLANK" href="<c:url value="/expedientTipus/llistat.html"/>">
												<spring:message code="decorators.entorn.disseny.antic"></spring:message>
												<span class="fa fa-info-circle text-primary" title="<spring:message code="decorators.entorn.disseny.antic.info"/>"></span>
											</a>
										</li>
									</c:if>
								</ul>
							</div>
						</c:if>
						<c:if test="${globalProperties['app.organigrama.actiu'] && dadesPersona.admin}">
							<div id="menuOrganitzacio" class="btn-group">
								<button class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><spring:message code="decorator.menu.organitzacio"/> <span class="caret"></span></button>
								<ul class="dropdown-menu" id="organitzacio">
									<li><a href="<c:url value="/v3/entorn-area"/>"><spring:message code='comuns.arees' /></a></li>
									<li><a href="<c:url value="/v3/entorn-tipus-area"/>"><spring:message code='comuns.tipusArea' /></a></li>
									<li><a href="<c:url value="/v3/entorn-carrec"/>"><spring:message code='comuns.carrecs' /></a></li>
								</ul>
							</div>
						</c:if>
						<c:if test="${dadesPersona.admin || potAdministrarEntorn}">
							<div id="menuConsultar" class="btn-group">
								<button class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><spring:message code="decorator.menu.consultar"/> <span class="caret"></span></button>
								<ul class="dropdown-menu" id="consultar">
									<li><a href="<c:url value="/v3/consultesPinbal"/>"><spring:message code='decorator.menu.consultar.consultes.pinbal' /></a></li>
									<li><a href="<c:url value="/v3/notificacionsNotib"/>"><spring:message code='decorator.menu.consultar.notificacions' /></a></li>
									<li><a href="<c:url value="/v3/enviamentsPortafib"/>"><spring:message code='decorator.menu.consultar.documents.enviats.portafib' /></a></li>
								</ul>
							</div>
						</c:if>
						<c:if test="${dadesPersona.admin || potAdministrarEntorn}">
							<div id="menuAdministracio" class="btn-group">
								<button class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><spring:message code="decorator.menu.administracio"/> <span class="caret"></span></button>
								<ul class="dropdown-menu" id="mesures">
									<c:if test="${ globalProperties['app.expedient.monitor'] && potAdministrarEntorn}">
										<li><a data-toggle="modal" data-maximized="true" id="botoMonitor" href="<c:url value="/modal/v3/monitor"/>"><spring:message code='expedient.monitor' /></a></li>
									</c:if>
									<c:if test="${dadesPersona.admin || potAdministrarEntorn}">
										<li><a data-toggle="modal" data-maximized="true" id="botoMetriques" href="<c:url value="/v3/metriques"/>"><spring:message code='expedient.metriques' /></a></li>
									</c:if>
									<c:if test="${dadesPersona.admin || potAdministrarEntorn}">
										<li><a data-toggle="modal" data-maximized="true" href="<c:url value="/v3/monitorIntegracio"/>"><spring:message code='decorator.menu.administracio.monitor.integracio' /></a></li>
									</c:if>
									<c:if test="${dadesPersona.admin || potAdministrarEntorn}">
										<li><a data-toggle="modal" data-maximized="true" href="<c:url value="/v3/monitorDomini"/>"><spring:message code='decorator.menu.administracio.monitor.domini' /></a></li>
									</c:if>									
									<c:if test="${dadesPersona.admin || potAdministrarEntorn}">
										<li><a data-toggle="modal" href="<c:url value="/modal/v3/tasca/pendentsCompletar"/>"><spring:message code='decorator.menu.administracio.tasques.execucio' /></a></li>
									</c:if>
									<c:if test="${dadesPersona.admin || potAdministrarEntorn}">
										<li><a data-toggle="modal" data-maximized="true" href="<c:url value="/modal/v3/reindexacions"/>"><spring:message code='decorator.menu.administracio.reindexacions' /></a></li>
									</c:if>
									<c:if test="${dadesPersona.admin || potAdministrarEntorn}">
										<li><a data-toggle="modal" data-maximized="true" href="<c:url value="/modal/v3/execucionsMassives/admin"/>"><spring:message code='comuns.massiu' /></a></li>
									</c:if>
									<c:if test="${dadesPersona.admin}"><li><a href="<c:url value="/v3/entorn"/>"><spring:message code='decorators.superior.entorns' /></a></li></c:if>
									<c:if test="${globalProperties['app.jbpm.identity.source'] == 'jbpm'}">
										<c:if test="${dadesPersona.admin}"><li><a href="<c:url value="/v3/carrec"/>"><spring:message code='comuns.carrecs' /></a></li></c:if>
										<c:if test="${dadesPersona.admin}"><li><a href="<c:url value="/v3/area"/>"><spring:message code='comuns.arees' /></a></li></c:if>
									</c:if>
									<c:if test="${dadesPersona.admin}"><li><a data-toggle="modal" data-maximized="true" href="<c:url value="/modal/v3/configuracio/festius"/>"><spring:message code='decorators.superior.festius' /></a></li></c:if>
									<c:if test="${dadesPersona.admin}">
										<li><a data-toggle="modal" href="<c:url value="/modal/v3/configuracio/parametres"/>"><spring:message code='decorators.superior.parametres' /></a></li>
									</c:if>
									<c:if test="${dadesPersona.admin || potAdministrarEntorn}">
										<li><a id="botoMetriques" href="<c:url value="/v3/estadistica"/>"><spring:message code='decorators.superior.estadistica' /></a></li>
									</c:if>
									<c:if test="${dadesPersona.admin || potAdministrarEntorn}">
										<li><a id="botoCercaTipologies" href="<c:url value="/v3/cercadorTipologies"/>"><spring:message code='decorator.menu.administracio.cercador.tipologies' /></a></li>
									</c:if>
									<c:if test="${dadesPersona.admin || potDissenyarAvisos}">
										<li><a id="menuAvisos" href="<c:url value="/v3/avis"/>"><spring:message code="decorator.menu.avisos"/></a></li>
									</c:if>
									<c:if test="${dadesPersona.admin}">
										<li><a id="unitatsOrganitzatives" href="<c:url value="/v3/unitatOrganitzativa"/>"><spring:message code="decorator.menu.unitats.organitzatives"/></a></li>
									</c:if>
									<c:if test="${dadesPersona.admin}">
										<li><a id="procediments" href="<c:url value="/v3/procediment"/>"><spring:message code="decorator.menu.procediments"/></a></li>
									</c:if>
									<c:if test="${dadesPersona.admin}">
										<li><a id="excepcions" href="<c:url value="/v3/excepcions"/>"><spring:message code="decorator.menu.excepcions"/></a></li>
									</c:if>
								</ul>
								<script type="text/javascript">
									$('#mesures a').heliumEvalLink({
										alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
										refrescarAlertes: true,
										refrescarPagina: false
									});
								</script>
							</div>
						</c:if>
					</div>
					<c:if test="${hiHaTramitsPerIniciar}">
						<div id="iniciar-expediente" class="btn-group navbar-btn navbar-right">
							<a data-toggle="modal" data-callback="refrescarTaulaDades();" data-maximized="true" class="btn btn-primary" href="<c:url value="/modal/v3/expedient/iniciar"/>"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.llistat.accio.nou"/></a>
							<script type="text/javascript">
								function refrescarTaulaDades() {
									try {
										$('#taulaDades').dataTable().fnDraw();
									}catch(e) {
										// no hi ha cap taula de dades amb aquest id
									}
								}
								$('#iniciar-expediente a').heliumEvalLink({
									refrescarAlertes: false,
									refrescarPagina: false
								});
							</script>
						</div>
					</c:if>
				</div>
			</div>
		</div>
	</div>
	<div class="container container-v3 container-main <c:if test="${preferenciesUsuari.cabeceraReducida}">cabecera_reducida-main</c:if>">
		<div class="panel panel-default">
			<c:set var="decoratorMetaTitle"><decorator:getProperty property="meta.title"/></c:set>
			<c:set var="screen"><decorator:getProperty property="meta.screen"/></c:set>
			<c:set var="metaTitleIconClass"><decorator:getProperty property="meta.title-icon-class"/></c:set>
			<c:set var="decoratorMetaSubtitle"><decorator:getProperty property="meta.subtitle"/></c:set>
			<c:if test="${not empty avisos}">
			<div id="accordion">
				<c:forEach var="avis" items="${avisos}" varStatus="status">
						<div class="card avisCard ${avis.avisNivell == 'INFO' ? 'avisCardInfo':''} ${avis.avisNivell == 'WARNING' ? 'avisCardWarning':''} ${avis.avisNivell == 'ERROR' ? 'avisCardError':''}">
	
							<div data-toggle="collapse" data-target="#collapse${status.index}" class="card-header avisCardHeader">
								${avis.avisNivell == 'INFO' ? '<span class="fa fa-info-circle text-info"></span>':''} ${avis.avisNivell == 'WARNING' ? '<span class="fa fa-exclamation-triangle text-warning"></span>':''} ${avis.avisNivell == 'ERROR' ? '<span class="fa fa-warning text-danger"></span>':''} ${avis.assumpte}
							<button class="btn btn-default btn-xs pull-right"><span class="fa fa-chevron-down "></span></button>										
							</div>
	
							<div id="collapse${status.index}" class="collapse" data-parent="#accordion">
								<div class="card-body avisCardBody" >${avis.missatge}</div>
							</div>
						</div>
				</c:forEach>
			</div>
		</c:if>

			<c:if test="${not empty decoratorMetaTitle}">
				<div class="panel-heading">
					<div class="row">
						<div class="col-md-10" id="capcalera-titol">
							<h2>
								<div>
									<c:if test="${not empty metaTitleIconClass}"><span class="${metaTitleIconClass}"></span></c:if>
									<div class="text-limit w900" title="${decoratorMetaTitle}">
										${decoratorMetaTitle}
										<c:if test="${not empty decoratorMetaSubtitle}">
											<small><decorator:getProperty property="meta.subtitle"/></small>
										</c:if>
									</div>
								</div>
							</h2>
						</div>
						<div class="col-md-2" id="capcalera-botons">
							<c:if test="${not empty screen && screen=='expedients'}">
								<c:if test="${not empty expedientTipusAccessiblesAmbConsultesActives}">
									<div id="btnConsultes" class="btn-group pull-right" >
										<button class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><spring:message code="decorator.menu.consultes"/> <span class="caret"></span></button>
										<ul class="dropdown-menu">	
											<li class="nav-consulta-tipus"><a href="<c:url value="/v3/expedient"></c:url>"><spring:message code="decorator.menu.consultes.generic"/></a></li>
											<c:forEach var="expedientTipus" items="${expedientTipusAccessiblesAmbConsultesActives}" varStatus="consultaStatus">
												<c:if test="${empty expedientTipusActual or expedientTipusActual.id == expedientTipus.id}">
													<c:if test="${consultaStatus.index == 0}"><li class="divider"></li></c:if>	
													<li class="nav-header">${expedientTipus.nom}</li>
													<c:forEach var="consulta" items="${expedientTipus.consultesSort}">
														<%--li class="nav-consulta-tipus"><a href="<c:url value="/v3/informe?consultaId=${consulta.id}"></c:url>">${consulta.nom}</a></li--%>
														<li class="nav-consulta-tipus"><a href="<c:url value="/v3/expedient/consulta/${consulta.id}"></c:url>">${consulta.nom}</a></li>
													</c:forEach>
												</c:if>
											</c:forEach>
										</ul>
									</div>
								</c:if>
							</c:if>
						</div>
					</div>
				</div>
			</c:if>
			<div class="panel-body">
				<div id="contingut-alertes"><hel:missatges/></div>
				<decorator:body />
			</div>
		</div>
	</div>
    <div class="container container-v3 container-foot">
    	<div class="pull-left app-version"><p>Helium ${versioNom} <span style="color:rgba(0,0,0,0);">(${versioData})</span></p></div>
        <div class="pull-right govern-footer">
        	<p>
	        	<img src="<c:url value="/img/govern-logo-neg.png"/>" hspace="5" height="30" alt="<spring:message code='decorator.logo.govern'/>" />
	        	<img src="<c:url value="/img/una_manera.png"/>" 	 hspace="5" height="30" alt="<spring:message code='decorator.logo.manera'/>" />
	        	<img src="<c:url value="/img/feder7.png"/>" 	     hspace="5" height="35" alt="<spring:message code='decorator.logo.feder'/>" />
	        	<img src="<c:url value="/img/uenegroma.png"/>"	     hspace="5" height="50" alt="<spring:message code='decorator.logo.ue'/>" />
        	</p>
        </div>
    </div>

	<div id="topBtn" class="arrow-top" title="<spring:message code="decorator.arrow-top"/>">
		<span class="fa fa-4 fa-angle-double-up"></span>
	</div>

	<div id="overlay" style="display:none;">
		<span id="overlay-spin" class="fa fa-circle-o-notch fa-spin fa-3x"></span>
	</div>
</body>
</html>
