<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:set var="titol"><spring:message code="anotacio.detalls.titol" arguments="${anotacio.identificador}"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
<style>
body {
	min-height: 400px;
}
.tab-content {
    margin-top: 0.8em;
}
.icona-doc {
	color: #666666
}
.file-dt {
	margin-top: 9px;
}
.file-dd {
	margin-top: 3px;
}
tr.odd {
	background-color: #f9f9f9;
}
tr.detall {
/* 	background-color: cornsilk; */
}
tr.clicable {
	cursor: pointer;
}
</style>
<script type="text/javascript">
	
	$(document).ready(function() {
		
		$(".desplegable").click(function(){
			$(this).find("span").toggleClass("fa-caret-up");
			$(this).find("span").toggleClass("fa-caret-down");
		});		
});

</script>
</head>
<body>

	<!------------------------------ TABLIST ------------------------------------------------->
	<ul class="nav nav-tabs" role="tablist">
		<li class="active" role="presentation"><a href="#informacio" aria-controls="informacio" role="tab" data-toggle="tab"><spring:message code="anotacio.detalls.pipella.informacio"/></a>
		</li>
		<li role="presentation">
			<a href="#interessats" aria-controls="interessats" role="tab" data-toggle="tab"><spring:message code="anotacio.detalls.pipella.interessats"/>&nbsp;<span class="badge">${fn:length(anotacio.interessats)}</span></a>
		</li>
		<li role="presentation">
			<a href="#annexos" aria-controls="annexos" role="tab" data-toggle="tab">
				<c:choose>
					<c:when test="${anotacio.errorAnnexos || anotacio.annexosInvalids}"><span class="fa fa-warning text-danger"></span></c:when>
					<c:when test="${anotacio.annexosEsborranys}"><span class="fa fa-warning text-warning"></span></c:when>
				</c:choose>
				<spring:message code="anotacio.detalls.pipella.annexos"/>&nbsp;
				<span class="badge">${fn:length(anotacio.annexos)}</span>
			</a>
		</li>
		<c:if test="${not empty peticio.notificaDistError}">
			<li role="presentation">
				<a href="#error" aria-controls="error" role="tab" data-toggle="tab">
					<spring:message code="anotacio.detalls.pipella.error"/>
					<span class="fa fa-warning text-danger"></span>				
				</a>
			</li>		
		</c:if>

	</ul>
	
	<div class="tab-content">
		<!------------------------------ TABPANEL INFORMACIO ------------------------------------->
		<div class="tab-pane active in" id="informacio" role="tabpanel">
			<table class="table table-bordered">
			<tbody>
				<tr>
					<td><strong><spring:message code="anotacio.detalls.camp.tipus"/></strong></td>
					<td><spring:message code="anotacio.detalls.entrada"/></td>
				</tr>
				<tr>
					<td><strong><spring:message code="anotacio.detalls.camp.numero"/></strong></td>
					<td>${anotacio.identificador}</td>
				</tr>
				<tr>
					<td><strong><spring:message code="anotacio.detalls.camp.data"/></strong></td>
					<td><fmt:formatDate value="${anotacio.data}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
				</tr>
				<tr>
					<td><strong><spring:message code="anotacio.detalls.camp.estat"/></strong></td>
					<td>
						<spring:message code="enum.anotacio.estat.${anotacio.estat}"></spring:message>
						
						<c:if test="${anotacio.estat == 'ERROR_PROCESSANT'}">
							<div class="alert alert-danger">
								<span class="fa fa-exclamation-triangle"></span>
								<spring:message code="anotacio.detalls.errorProcessament" arguments="${anotacio.errorProcessament}"></spring:message>
							</div>
						</c:if>
						<c:if test="${anotacio.estat == 'COMUNICADA'}">
							<div class="alert alert-warning">
								<span class="fa fa-exclamation-triangle"></span>
								<spring:message code="anotacio.detalls.consulta" arguments="${anotacio.consultaIntents},${ maxConsultaIntents}"></spring:message>
								<c:if test="${anotacio.consultaError != null}">
										<span class="fa fa-exclamation-triangle text-danger" title="${anotacio.consultaError }"></span>
								</c:if>
							</div>
						</c:if>
												
					</td>
				</tr>
			</tbody>
			</table>
			<div class="row">
				<div class="col-sm-6">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title"><spring:message code="anotacio.detalls.titol.obligatories"/></h3>
						</div>
						<table class="table table-bordered">
							<tbody>
								<tr>
									<td><strong><spring:message code="anotacio.detalls.camp.oficina"/></strong></td>
									<td>${anotacio.oficinaDescripcio} (${anotacio.oficinaCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="anotacio.detalls.camp.llibre"/></strong></td>
									<td>${anotacio.llibreDescripcio} (${anotacio.llibreCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="anotacio.detalls.camp.extracte"/></strong></td>
									<td>${anotacio.extracte}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="anotacio.detalls.camp.docfis"/></strong></td>
									<td>${anotacio.docFisicaCodi} - ${anotacio.docFisicaDescripcio}</td>
								</tr>
								<tr>
									<td><strong>
										<spring:message code="anotacio.detalls.camp.desti"/>
									</strong></td>
									<td>${anotacio.destiDescripcio} (${anotacio.destiCodi})</td>
									
								</tr>
								<tr>
									<td><strong><spring:message code="anotacio.detalls.camp.assumpte.tipus"/></strong></td>
									<td>${anotacio.assumpteTipusDescripcio} (${anotacio.assumpteTipusCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="anotacio.detalls.camp.idioma"/></strong></td>
									<td>${anotacio.idiomaDescripcio} (${anotacio.idiomaCodi})</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				<div class="col-sm-6">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title"><spring:message code="anotacio.detalls.titol.opcionals"/></h3>
						</div>
						<table class="table table-bordered">
							<tbody>
								<tr>
									<td colspan="2"><strong><spring:message code="anotacio.detalls.camp.procediment.codi"/></strong></td>
									<td colspan="2">${anotacio.procedimentCodi}</td>
								</tr>
								<tr>
									<td colspan="2"><strong><spring:message code="anotacio.detalls.camp.assumpte.codi"/></strong></td>
									<td colspan="2">(${anotacio.assumpteCodiCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="anotacio.detalls.camp.refext"/></strong></td>
									<td>${anotacio.refExterna}</td>
									<td><strong><spring:message code="anotacio.detalls.camp.numexp"/></strong></td>
									<td>${anotacio.expedientNumero}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="anotacio.detalls.camp.transport.tipus"/></strong></td>
									<td>${anotacio.transportTipusDescripcio} ${anotacio.transportTipusCodi!=null?'(':''}${anotacio.transportTipusCodi}${anotacio.transportTipusCodi!=null?')':''}</td>
									<td><strong><spring:message code="anotacio.detalls.camp.transport.num"/></strong></td>
									<td>${anotacio.transportNumero}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="anotacio.detalls.camp.origen.num"/></strong></td>
									<td>${anotacio.origenRegistreNumero}</td>
									<td><strong><spring:message code="anotacio.detalls.camp.origen.data"/></strong></td>
									<td><fmt:formatDate value="${anotacio.origenData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
								</tr>
								<tr>
									<td colspan="2"><strong><spring:message code="anotacio.detalls.camp.observacions"/></strong></td>
									<td colspan="2">${anotacio.observacions}</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		
		<!------------------------------ TABPANEL INTERESSATS ------------------------------------->
		<div class="tab-pane" id="interessats" role="tabpanel">
			<c:choose>
				<c:when test="${not empty anotacio.interessats}">
					<table class="table table-bordered">
						<thead>
							<tr>
								<th style="width: 150px;"><spring:message code="anotacio.detalls.camp.interessat.tipus"/></th>
								<th style="width: 150px;"><spring:message code="anotacio.detalls.camp.interessat.document"/></th>
								<th><spring:message code="anotacio.detalls.camp.interessat.nom"/></th>
								<th style="width: 50px;"></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="interessat" items="${anotacio.interessats}" varStatus="status">
								<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
									<td>
										<spring:message code="anotacio.interessat.tipus.enum.${interessat.tipus}"/>
									</td>
									<td>${interessat.documentTipus}: ${interessat.documentNumero}</td>
									<c:choose>
										<c:when test="${interessat.tipus == 'PERSONA_FISICA'}">
											<td>${interessat.nom} ${interessat.llinatge1} ${interessat.llinatge2}</td>
										</c:when>
										<c:otherwise>
											<td>${interessat.raoSocial}</td>
										</c:otherwise>
									</c:choose>
									<td>
										<c:if test="${interessat.tipus != 'ADMINISTRACIO'}">
											<button type="button" class="btn btn-default desplegable" href="#detalls_${status.index}" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}">
												<span class="fa fa-caret-down"></span>
											</button>
										</c:if>
									</td>
								</tr>
								<tr class="collapse detall" id="detalls_${status.index}">
									<td colspan="4">
										<div class="row">
											<div class="col-xs-6">
												<dl class="dl-horizontal">
													<dt><spring:message code="anotacio.interessat.detalls.camp.pais"/></dt><dd>${interessat.pais}</dd>
													<dt><spring:message code="anotacio.interessat.detalls.camp.provincia"/></dt><dd>${interessat.provincia}</dd>											
													<dt><spring:message code="anotacio.interessat.detalls.camp.municipi"/></dt><dd>${interessat.municipi}</dd>
													<dt><spring:message code="anotacio.interessat.detalls.camp.adresa"/></dt><dd>${interessat.adresa}</dd>
													<dt><spring:message code="anotacio.interessat.detalls.camp.codiPostal"/></dt><dd>${interessat.cp}</dd>
												</dl>
											</div>
											<div class="col-xs-6">
												<dl class="dl-horizontal">
													<dt><spring:message code="anotacio.interessat.detalls.camp.email"/></dt><dd>${interessat.email}</dd>
													<dt><spring:message code="anotacio.interessat.detalls.camp.telefon"/></dt><dd>${interessat.telefon}</dd>
													<dt><spring:message code="anotacio.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty interessat.canal}"><spring:message code="anotacio.interessat.detalls.camp.canalPreferent.${interessat.canal}"/></c:if></dd>
													<dt><spring:message code="anotacio.interessat.detalls.camp.observacions"/></dt><dd>${interessat.observacions}</dd>
												</dl>
											</div>
											
											<!-- NOU APARTAT REPRESENTANT -->
											<c:if test="${not empty interessat.representant}">
												<c:set var="representant" value="${interessat.representant}"/>
												<div class="col-xs-12">
													<table class="table table-bordered">
														<thead>
															<tr><th colspan="4"><spring:message code="anotacio.interessat.detalls.camp.representant"/></th></tr>
															<tr>
																<th style="width: 150px;"><spring:message code="anotacio.detalls.camp.interessat.tipus"/></th>
																<th style="width: 150px;"><spring:message code="anotacio.detalls.camp.interessat.document"/></th>
																<th><spring:message code="anotacio.detalls.camp.interessat.nom"/></th>
																<th style="width: 50px;"></th>
															</tr>
														</thead>
														<tbody>
															<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
																<td>
																	<spring:message code="anotacio.interessat.tipus.enum.${representant.tipus}"/>
																</td>
																<td>${representant.documentTipus}: ${representant.documentNumero}</td>
																<c:choose>
																	<c:when test="${representant.tipus == 'PERSONA_FISICA'}">
																		<td>${representant.nom} ${representant.llinatge1} ${representant.llinatge2}</td>
																	</c:when>
																	<c:otherwise>
																		<td>${representant.raoSocial}</td>
																	</c:otherwise>
																</c:choose>
																<td>
																	<c:if test="${representant.tipus != 'ADMINISTRACIO'}">
																		<button type="button" class="btn btn-default desplegable" href="#detalls_${status.index}_rep" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}_rep">
																			<span class="fa fa-caret-down"></span>
																		</button>
																	</c:if>
																</td>
															</tr>
															<tr class="collapse detall" id="detalls_${status.index}_rep">
																<td colspan="4">
																	<div class="row">
																		<div class="col-xs-6">
																			<dl class="dl-horizontal">
																				<dt><spring:message code="anotacio.interessat.detalls.camp.pais"/></dt><dd>${representant.pais}</dd>
																				<dt><spring:message code="anotacio.interessat.detalls.camp.provincia"/></dt><dd>${representant.provincia}</dd>											
																				<dt><spring:message code="anotacio.interessat.detalls.camp.municipi"/></dt><dd>${representant.municipi}</dd>
																				<dt><spring:message code="anotacio.interessat.detalls.camp.adresa"/></dt><dd>${representant.adresa}</dd>
																				<dt><spring:message code="anotacio.interessat.detalls.camp.codiPostal"/></dt><dd>${representant.cp}</dd>
																			</dl>
																		</div>
																		<div class="col-xs-6">
																			<dl class="dl-horizontal">
																				<dt><spring:message code="anotacio.interessat.detalls.camp.email"/></dt><dd>${representant.email}</dd>
																				<dt><spring:message code="anotacio.interessat.detalls.camp.telefon"/></dt><dd>${representant.telefon}</dd>
																				<dt><spring:message code="anotacio.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty representant.canal}"><spring:message code="anotacio.interessat.detalls.camp.canalPreferent.${representant.canal}"/></c:if></dd>
																				<dt><spring:message code="anotacio.interessat.detalls.camp.observacions"/></dt><dd>${representant.observacions}</dd>
																			</dl>
																		</div>
																	</div>
																</td>						
															</tr>
														</tbody>
													</table>
												</div>
											</c:if>
										</div>
									</td>						
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:otherwise>
					<div class="row col-xs-12">
						<div class="well">
							<spring:message code="anotacio.interessat.buit"/>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
		
		<!------------------------------ TABPANEL ANNEXOS ------------------------------------->
		<div class="tab-pane" id="annexos" role="tabpanel">
			<c:choose>
				<c:when test="${not empty anotacio.annexos}">
				
					<c:forEach var="annex" items="${anotacio.annexos}" varStatus="status">
					
						<script type="text/javascript">
							$(document).ready(function() {
							    $("#collapse-registre-firmes-<c:out value='${annex.id}'/>").on('show.bs.collapse', function(data){  	
								    if (!$(this).data("loaded")) {
								        var annexId = $(this).data("annexId");
								        $(this).append("<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x' title='<spring:message code="anotacio.annex.detalls.annex.firmes.consultant" />'/></div>");
								        $(this).load('<c:url value="/nodeco/v3/anotacio/${anotacio.id}/annex/"/>' + ${annex.id} + '/firmaInfo');
								        $(this).data("loaded", true);
								    }
							    });
						 	});
						</script>
					
						<div class="panel panel-default">
							<div class="panel-heading">
								<h3 class="panel-title">
									<span class="fa fa-file"></span>
									${annex.titol}
									<c:if test="${annex.error != null }">
										<span class="fa fa-warning text-danger" title="<spring:message code="anotacio.annex.detalls.annex.error" arguments="${annex.error}"/>"></span>
									</c:if>
									<c:if test="${!annex.documentValid}">
										<span class="fa fa-warning text-danger" title="<spring:message code="anotacio.annex.detalls.annex.invalid" arguments="${annex.documentError}"/>"></span>
									</c:if>
									<c:if test="${annex.arxiuEstat == 'ESBORRANY'}">
										<span class="fa fa-warning text-warning" title="<spring:message code="anotacio.annex.detalls.annex.esborrany"/>"></span>
									</c:if>
									<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-annex-${status.index}"><span class="fa fa-chevron-down"></span></button>
								</h3>
							</div>
 							<div id="collapse-annex-${status.index}" class="panel-collapse collapse collapse-annex" role="tabpanel" aria-labelledby="dadesAnnex${status.index}" data-registre-id="${anotacio.id}"  data-fitxer-arxiu-uuid="${annex.uuid}">


								<div>
									<c:if test="${annex.estat == 'PENDENT' && not empty annex.error}">
									
										<div class="alert well-sm alert-danger alert-dismissable" style="margin-bottom: 0px;">
											<span class="fa fa-exclamation-triangle"></span>
											<spring:message code="anotacio.annex.detalls.annex.processament.error" />
											<a href="<c:url value="/v3/anotacio/${anotacio.id}/annex/${annex.id}/reintentar"/>"
												class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span>
												<spring:message code="anotacio.annex.detalls.annex.accio.reintentar" /></a>
										</div>
										<pre style="height: 200px; background-color: white; margin-bottom: 0px;">${annex.error}</pre>
									</c:if>
								</div>

								<table class="table table-bordered">
								<tbody>														
									<tr>
										<td><strong><spring:message code="anotacio.annex.detalls.camp.eni.data.captura"/></strong></td>
										<td><c:if test="${not empty annex.ntiFechaCaptura}"><fmt:formatDate value="${annex.ntiFechaCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if></td>
									</tr>
									<tr>
										<td><strong><spring:message code="anotacio.annex.detalls.camp.eni.origen"/></strong></td>
										<td><c:if test="${not empty annex.ntiOrigen}">${annex.ntiOrigen}</c:if></td>
									</tr>
									<tr>
										<td><strong><spring:message code="anotacio.annex.detalls.camp.eni.tipus.documental"/></strong></td>
										<td><c:if test="${not empty annex.ntiTipoDocumental}"><spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.${annex.ntiTipoDocumental}"/></c:if></td>
									</tr>
									<tr>
										<td><strong><spring:message code="anotacio.annex.detalls.camp.sicres.tipus.document"/></strong></td>
										<td><c:if test="${not empty annex.sicresTipoDocumento}"><spring:message code="anotacio.annex.detalls.camp.sicresTipusDocument.${annex.sicresTipoDocumento}"/></c:if></td>
									</tr>
									<tr>
										<td><strong><spring:message code="anotacio.annex.detalls.camp.arxiu.uuid"/></strong></td>
										<td>
											${annex.uuid}
											<c:if test="${annex.uuid == null }">
												<span class="fa fa-warning text-warning" title="<spring:message code="anotacio.annex.detalls.camp.arxiu.uuid.buit.avis"/>"></span>
											</c:if>
										</td>
									</tr>
								
									<c:if test="${not empty annex.observacions}">
										<tr>
											<td><strong><spring:message code="anotacio.annex.detalls.camp.observacions"/></strong></td>
											<td>${annex.observacions}</td>
										</tr>
									</c:if>
									<tr>
										<td><strong><spring:message code="anotacio.annex.detalls.camp.fitxer"/></strong></td>
										<td>
											${annex.nom}
											<a href='<c:url value="/v3/anotacio/${anotacio.id}/annex/${annex.id}/descarregar/imprimible"></c:url>' 
												class="btn btn-default btn-sm pull-right arxiu-download">
												<spring:message code="anotacio.annex.detalls.camp.fitxer.descarregar.imprimible"/>
												<span class="fa fa-download" title="<spring:message code="anotacio.annex.detalls.camp.fitxer.descarregar.imprimible"/>"></span>
											</a>
											<c:if test="${not empty annex.firmaTipus}">
												<a href='<c:url value="/v3/anotacio/${anotacio.id}/annex/${annex.id}/descarregar/original"></c:url>' 
													class="btn btn-default btn-sm pull-right arxiu-download">
													<spring:message code="anotacio.annex.detalls.camp.fitxer.descarregar.original"/>
													<span class="fa fa-download" title="<spring:message code="anotacio.annex.detalls.camp.fitxer.descarregar.original"/>"></span>
												</a>	
											</c:if>	
										</td>
									</tr>
									<tr>
										<td><strong><spring:message code="anotacio.annex.detalls.camp.estat"/></strong></td>
										<td>
											${annex.estat}
											<c:if test="${annex.error != null}">
											<span 
												class="fa fa-exclamation-triangle text-danger" 
												title="${annex.error}"></span>
											</c:if>
										</td>
									</tr>
									<tr>
										<td><strong><spring:message code="anotacio.annex.detalls.camp.estat.arxiu"/></strong></td>
										<td>
											${annex.arxiuEstat}
											<c:if test="${annex.arxiuEstat == 'ESBORRANY'}">
											<span 
												class="fa fa-exclamation-triangle text-warning" 
												title="<spring:message code='anotacio.annex.detalls.camp.estat.arxiu.esborrany.avis'></spring:message>"></span>
											</c:if>
										</td>
									</tr>
									<tr>
										<td><strong><spring:message code="anotacio.annex.detalls.camp.valid"/></strong></td>
										<td>
											<c:choose>
												<c:when test="${annex.documentValid }">
													<spring:message code="enum.si"></spring:message>
												</c:when>
												<c:when test="${!annex.documentValid }">
													<span 
														class="fa fa-exclamation-triangle text-danger"></span>
													<spring:message code="enum.no"></spring:message>
													: ${annex.documentError}
												</c:when>
											</c:choose>
										</td>
									</tr>
									<tr>
										<td colspan="2">
											<div class="panel panel-default">
												<div class="panel-heading">
													<h3 class="panel-title">
														<span class="fa fa-certificate"></span>
														<spring:message code="anotacio.annex.detalls.camp.firmes"/>
														<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-registre-firmes-${annex.id}"><span class="fa fa-chevron-down"></span></button>
													</h3>
												</div>
												<div id="collapse-registre-firmes-${annex.id}" class="panel-collapse collapse collapse-annex collapse-registre-firmes" role="tabpanel" data-annex-id="${annex.id}"> 
								
												</div> 
											</div>
										</td>
									</tr>
								
								</table>
 							</div> 
						</div>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${not empty annexosErrorMsg}">
							<div class="row col-xs-12">
								<div class="alert alert-danger">
									${annexosErrorMsg}
								</div>
							</div>						
						</c:when>
						<c:otherwise>
							<div class="row col-xs-12">
								<div class="well">
									<spring:message code="anotacio.annex.buit"/>
								</div>
							</div>
						</c:otherwise>
					</c:choose>				
				</c:otherwise>
			</c:choose>
		</div>
		
		<!------------------------------ TABPANEL ERROR ------------------------------------->
		<div class="tab-pane" id="error" role="tabpanel">
			<div>
				<div class="alert well-sm alert-danger alert-dismissable" style="margin-bottom: 0px;">
					<span class="fa fa-exclamation-triangle"></span>
					<spring:message code="anotacio.detalls.errorNotifacio" />
					<a href="<c:url value="/v3/anotacio/${anotacio.id}/reintentarNotificar"/>"
						class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span>
						<spring:message code="anotacio.detalls.annex.accio.reintentar" /></a>
				</div>
				<pre style="height: 200px; background-color: white; margin-bottom: 0px;">${anotacio.distibucioErrorNotificacio}</pre>
			</div>
		</div>		
		

	</div>
	<div id="modal-botons" class="well">
		<a href="<c:url value="/anotacio"/>" class="btn btn-default modal-tancar" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
