<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title>
		<c:choose>
			<c:when test="${empty arxiuDetall}"><spring:message code="document.metadades.nti.titol.nti"/></c:when>
			<c:otherwise><spring:message code="document.metadades.nti.titol.arxiu"/></c:otherwise>
		</c:choose>
	</title>
	<hel:modalHead/>
</head>
<body>
	<!-- Formulari per incoporar el document a l'Arxiu si es detecta error de no Uuid -->
	<c:if test="${errorArxiuNoUuid}">
		<div class="row alert alert-danger" style="margin: 0px; margin-bottom: 10px;">
			<div class="col-sm-10">
				<p><spring:message code="expedient.document.arxiu.error.uuidnoexistent.info"/></p>
			</div>
			<div class="col-sm-2">
				<form action="incoporarArxiu" method="post">
					<button id="incorporarArxiuButton" 
						type="submit" 
						class="btn btn-default"
						title="<spring:message code='expedient.document.arxiu.migrar.arxiu'></spring:message>">
						<span class="fa fa-upload"></span>
						<spring:message code="comu.boto.migrar"></spring:message>
					</button>
				<form>
			</div>
		</div>
	</c:if>

	<c:if test="${not empty arxiuDetall}">
		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active"><a href="#nti" aria-controls="nti" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.nti"/></a></li>
			<li role="presentation"><a href="#arxiu" aria-controls="arxiu" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.info"/></a></li>
			<c:if test="${not empty arxiuDetall.fills}">
				<li role="presentation"><a href="#fills" aria-controls="fills" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.fills"/> <span class="badge badge-default">${fn:length(arxiuDetall.fills)}</span></a></li>
			</c:if>
			<c:if test="${not empty arxiuDetall.firmes}">
				<li role="presentation"><a href="#firmes" aria-controls="firmes" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.firmes"/> <span class="badge badge-default">${fn:length(arxiuDetall.firmes)}</span></a></li>
			</c:if>
			<c:if test="${not empty arxiuDetall.metadadesAddicionals}">
				<li role="presentation"><a href="#metadades" aria-controls="metadades" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.metadades"/> <span class="badge badge-default">${fn:length(arxiuDetall.metadadesAddicionals)}</span></a></li>
			</c:if>
		</ul>
		<br/>
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="nti">
	</c:if>
	<table class="table table-striped table-bordered">
		<tbody>
			<tr>
				<td width="30%"><strong><spring:message code="document.metadades.nti.version"/></strong></td>
				<td>${expedientDocument.ntiVersion}</td>
			</tr>
			<tr>
				<td><strong><spring:message code="document.metadades.nti.identificador"/></strong></td>
				<td>${expedientDocument.ntiIdentificador}</td>
			</tr>
			<tr>
				<td><strong><spring:message code="document.metadades.nti.organo"/></strong></td>
				<td>${expedientDocument.ntiOrgano}</td>
			</tr>
			<tr>
				<td><strong><spring:message code="document.metadades.nti.fecha.captura"/></strong></td>
				<td><fmt:formatDate value="${expedientDocument.dataCreacio}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
			</tr>
			<tr>
				<td><strong><spring:message code="document.metadades.nti.origen"/></strong></td>
				<td>
					<c:if test="${not empty expedientDocument.ntiOrigen}">
						<spring:message code="nti.document.origen.${expedientDocument.ntiOrigen}"/>
					</c:if>
				</td>
			</tr>
			<tr>
				<td><strong><spring:message code="document.metadades.nti.estado.elaboracion"/></strong></td>
				<td>
					<c:if test="${not empty expedientDocument.ntiEstadoElaboracion}">
						<spring:message code="nti.document.estado.elaboracion.${expedientDocument.ntiEstadoElaboracion}"/>
					</c:if>
				</td>
			</tr>
			<tr>
				<td><strong><spring:message code="document.metadades.nti.nombre.formato"/></strong></td>
				<td>
					<c:if test="${not empty expedientDocument.ntiNombreFormato}">
						<spring:message code="nti.document.format.${expedientDocument.ntiNombreFormato}"/>
					</c:if>
				</td>
			</tr>
			<tr>
				<td><strong><spring:message code="document.metadades.nti.tipo.documental"/></strong></td>
				<td>
					<c:if test="${not empty expedientDocument.ntiTipoDocumental}">
						<spring:message code="nti.document.tipo.documental.${expedientDocument.ntiTipoDocumental}"/>
					</c:if>
				</td>
			</tr>
			<c:if test="${not empty expedientDocument.ntiIdDocumentoOrigen}">
				<tr>
					<td><strong><spring:message code="document.metadades.nti.iddoc.origen"/></strong></td>
					<td>${expedientDocument.ntiIdDocumentoOrigen}</td>
				</tr>
			</c:if>
			<c:if test="${not empty expedientDocument.ntiTipoFirma}">
				<tr>
					<td><strong><spring:message code="document.metadades.nti.tipo.firma"/></strong></td>
					<td>
						${expedientDocument.ntiTipoFirma}<if test="${not empty expedientDocument.ntiCsv}">, CSV</if>
					</td>
				</tr>
				<tr>
					<td><strong><spring:message code="document.metadades.nti.csv"/></strong></td>
					<td>${expedientDocument.ntiCsv}</td>
				</tr>
				<tr>
					<td><strong><spring:message code="document.metadades.nti.defgen.csv"/></strong></td>
					<td>${expedientDocument.ntiDefinicionGenCsv}</td>
				</tr>
			</c:if>
		</tbody>
	</table>
	<c:if test="${errorMetadadesNti}">
		<div class="row alert alert-danger" style="margin: 0px; margin-bottom: 10px;">
			<div class="col-sm-10">
				<p><spring:message code="expedient.metadades.nti.dades.error.info"/></p>
			</div>
			<div class="col-sm-2">
				<form  action="<c:url value="/v3/expedient/${expedient.id}/metadadesNti/arreglar"/>" 
					method="post">
					<button id="arreglarNtiButton" 
						type="submit" 
						class="btn btn-default"
						title="<spring:message code='expedient.metadades.nti.dades.error.arreglar.info'></spring:message>">
						<span class="fa fa-cog"></span>
						<spring:message code="expedient.metadades.nti.dades.error.arreglar"></spring:message>
					</button>
				<form>
			</div>
		</div>
	</c:if>
	
	
	<c:if test="${not empty arxiuDetall}">
			</div>
			<div role="tabpanel" class="tab-pane" id="arxiu">
				<table class="table table-striped table-bordered">
					<tbody>
						<tr>
							<td width="30%"><strong><spring:message code="expedient.metadades.nti.camp.identificador"/></strong></td>
							<td>${arxiuDetall.identificador}</td>
						</tr>
						<tr>
							<td><strong><spring:message code="expedient.metadades.nti.camp.nom"/></strong></td>
							<td>${arxiuDetall.nom}</td>
						</tr>
						<c:if test="${not empty arxiuDetall.serieDocumental}">
							<tr>
								<td><strong><spring:message code="expedient.metadades.nti.camp.serie.doc"/></strong></td>
								<td>${arxiuDetall.serieDocumental}</td>
							</tr>
						</c:if>
					</tbody>
				</table>
				<c:if test="${not empty arxiuDetall.contingutTipusMime or not empty arxiuDetall.contingutArxiuNom}">
					<div class="panel panel-default">
						<div class="panel-heading"><h4 style="margin:0"><strong><spring:message code="expedient.metadades.nti.grup.contingut"/></strong></h4></div>
						<table class="table table-striped table-bordered">
						<tbody>
							<c:if test="${not empty arxiuDetall.contingutTipusMime}">
								<tr>
									<td width="30%"><strong><spring:message code="expedient.metadades.nti.camp.contingut.tipus.mime"/></strong></td>
									<td>${arxiuDetall.contingutTipusMime}</td>
								</tr>
							</c:if>
							<c:if test="${not empty arxiuDetall.contingutArxiuNom}">
								<tr>
									<td width="30%"><strong><spring:message code="expedient.metadades.nti.camp.contingut.arxiu.nom"/></strong></td>
									<td>${arxiuDetall.contingutArxiuNom}</td>
								</tr>
							</c:if>
						</tbody>
						</table>
					</div>
				</c:if>
				<c:if test="${not empty arxiuDetall.eniIdentificador}">
					<div class="panel panel-default">
						<!--div class="panel-heading"><h4 style="margin:0"><strong><spring:message code="expedient.metadades.nti.grup.metadades"/></strong></h4></div-->
						<table class="table table-striped table-bordered">
						<tbody>
							<tr>
								<td width="30%"><strong><spring:message code="expedient.metadades.nti.camp.eni.versio"/></strong></td>
								<td>${arxiuDetall.eniVersio}</td>
							</tr>
							<tr>
								<td><strong><spring:message code="expedient.metadades.nti.camp.eni.identificador"/></strong></td>
								<td>${arxiuDetall.eniIdentificador}</td>
							</tr>
							<c:if test="${not empty arxiuDetall.eniOrgans}">
								<tr>
									<td><strong><spring:message code="expedient.metadades.nti.camp.eni.organs"/></strong></td>
									<td>
										<c:forEach var="organ" items="${arxiuDetall.eniOrgans}" varStatus="status">
											${organ}<c:if test="${not status.last}">,</c:if>
										</c:forEach>
									</td>
								</tr>
							</c:if>
							<c:if test="${not empty arxiuDetall.eniDataObertura}">
								<tr>
									<td><strong><spring:message code="expedient.metadades.nti.camp.eni.data.obertura"/></strong></td>
									<td><fmt:formatDate value="${arxiuDetall.eniDataObertura}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
								</tr>
							</c:if>
							<c:if test="${not empty arxiuDetall.eniClassificacio}">
								<tr>
									<td><strong><spring:message code="expedient.metadades.nti.camp.eni.classificacio"/></strong></td>
									<td>${arxiuDetall.eniClassificacio}</td>
								</tr>
							</c:if>
							<c:if test="${not empty arxiuDetall.eniEstat}">
								<tr>
									<td><strong><spring:message code="expedient.metadades.nti.camp.eni.estat"/></strong></td>
									<td><spring:message code="nti.expedient.estat.${arxiuDetall.eniEstat}"/></td>
								</tr>
							</c:if>
							<c:if test="${not empty arxiuDetall.eniDataCaptura}">
								<tr>
									<td><strong><spring:message code="expedient.metadades.nti.camp.eni.data.captura"/></strong></td>
									<td><fmt:formatDate value="${arxiuDetall.eniDataCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
								</tr>
							</c:if>
							<c:if test="${not empty arxiuDetall.eniOrigen}">
								<tr>
									<td><strong><spring:message code="expedient.metadades.nti.camp.eni.origen"/></strong></td>
									<td><spring:message code="nti.document.origen.${arxiuDetall.eniOrigen}"/></td>
								</tr>
							</c:if>
							<c:if test="${not empty arxiuDetall.eniEstatElaboracio}">
								<tr>
									<td><strong><spring:message code="expedient.metadades.nti.camp.eni.estat.elab"/></strong></td>
									<td><spring:message code="nti.document.estado.elaboracion.${arxiuDetall.eniEstatElaboracio}"/></td>
								</tr>
							</c:if>
							<c:if test="${not empty arxiuDetall.eniTipusDocumental}">
								<tr>
									<td><strong><spring:message code="expedient.metadades.nti.camp.eni.tipus.doc"/></strong></td>
									<td><spring:message code="nti.document.tipo.documental.${arxiuDetall.eniTipusDocumental}"/></td>
								</tr>
							</c:if>
							<c:if test="${not empty arxiuDetall.eniFormat}">
								<tr>
									<td><strong><spring:message code="expedient.metadades.nti.camp.eni.format.nom"/></strong></td>
									<td>${arxiuDetall.eniFormat}</td>
								</tr>
							</c:if>
							<c:if test="${not empty arxiuDetall.eniExtensio}">
								<tr>
									<td><strong><spring:message code="expedient.metadades.nti.camp.eni.format.ext"/></strong></td>
									<td>${arxiuDetall.eniExtensio}</td>
								</tr>
							</c:if>
							<c:if test="${not empty arxiuDetall.eniInteressats}">
								<tr>
									<td><strong><spring:message code="expedient.metadades.nti.camp.eni.interessats"/></strong></td>
									<td>
										<c:forEach var="interessat" items="${arxiuDetall.eniInteressats}" varStatus="status">
											${interessat}<c:if test="${not status.last}">,</c:if>
										</c:forEach>
									</td>
								</tr>
							</c:if>
							<c:if test="${not empty arxiuDetall.eniDocumentOrigenId}">
								<tr>
									<td><strong><spring:message code="expedient.metadades.nti.camp.eni.doc.orig.id"/></strong></td>
									<td>${arxiuDetall.eniDocumentOrigenId}</td>
								</tr>
							</c:if>
							<tr>
								<td><strong><spring:message code="expedient.metadades.nti.camp.eni.firma.csv"/></strong></td>
								<td>${arxiuDetall.eniCsv}</td>
							</tr>
							<tr>
								<td><strong><spring:message code="expedient.metadades.nti.camp.eni.firma.csvdef"/></strong></td>
								<td>${arxiuDetall.eniCsvDef}</td>
							</tr>
							<tr>
								<td><strong><spring:message code="expedient.metadades.nti.camp.arxiuEstat"/></strong></td>
								<td>${arxiuDetall.arxiuEstat != null ? arxiuDetall.arxiuEstat : "-"}</td>
							</tr>
						</tbody>
						</table>
					</div>
				</c:if>
			</div>
			<c:if test="${not empty arxiuDetall.fills}">
				<div role="tabpanel" class="tab-pane" id="fills">
					<table class="table table-striped table-bordered">
						<c:forEach var="fill" items="${arxiuDetall.fills}" varStatus="status">
							<tr>
								<td width="10%">${fill.tipus}</td>
								<td>${fill.nom}</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</c:if>
			<c:if test="${not empty arxiuDetall.firmes}">
				<div role="tabpanel" class="tab-pane" id="firmes">
					<div class="panel panel-default">
						<table class="table table-striped table-bordered">
						<thead>
							<tr>
								<th><spring:message code="expedient.metadades.nti.camp.firma.tipus"/></th>
								<th><spring:message code="expedient.metadades.nti.camp.firma.perfil"/></th>
								<th><spring:message code="expedient.metadades.nti.camp.firma.contingut"/></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="firma" items="${arxiuDetall.firmes}" varStatus="status">
								<tr>
									<td width="15%">${firma.tipus}</td>
									<td width="10%">${firma.perfil}</td>
									<td>
										<c:choose>
											<c:when test="${firma.tipus == 'CSV'}">
												${firma.contingutComString}
											</c:when>
											<c:when test="${firma.tipus == 'XADES_DET' or firma.tipus == 'CADES_DET'}">
												<a href="<c:url value='/v3/expedient/${expedientId}/proces/${expedientDocument.processInstanceId}/document/${expedientDocument.id}/firma/${status.index}/descarregar'></c:url>" class="btn btn-default btn-sm pull-right">
													<span class="fa fa-download"  title="<spring:message code="comu.boto.descarregar"/>"></span>
												</a>
											</c:when>
											<c:otherwise>
											</c:otherwise>
										</c:choose>
									</td>
								</tr>
							</c:forEach>
						</tbody>
						</table>
					</div>
				</div>
			</c:if>
			<c:if test="${not empty arxiuDetall.metadadesAddicionals}">
				<div role="tabpanel" class="tab-pane" id="metadades">
					<table class="table table-striped table-bordered">
						<c:forEach var="metadada" items="${arxiuDetall.metadadesAddicionals}" varStatus="status">
							<tr>
								<td width="20%"><strong>${metadada.key}</strong></td>
								<td>${metadada.value}</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</c:if>
		</div>
	</c:if>
		
	
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.tancar"/></button>
	</div>
</body>
</html>
