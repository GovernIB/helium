<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<%--<link href="<c:url value="/css/exp-doc.css"/>" rel="stylesheet"/>--%>

<style>
.dl-horizontal dt::after {
  content: ": ";
}
</style>

<script type="text/javascript">
	// <![CDATA[
	$(document).ready( function() {
		// Clica el primer tab amb text-success
		$('#nav-tabs-document-${detall.documentStoreId} li a[role=tab]').children('span').each( function(){
			if ($(this).hasClass('text-success')) {
				$(this).parent('a[role=tab]').click();
				return false;
			}
		});
	});
	// ]]>
</script>

	<!-- Tabuladors -->		
	
	<ul id="nav-tabs-document-${detall.documentStoreId}" class="nav nav-tabs" role="tablist">
		<li id="pipella-documentDetall-arxiu-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-arxiu-${detall.documentStoreId}" role="tab" data-toggle="tab">
				<span class="fa fa-bookmark <c:choose><c:when test="${detall.nti || detall.arxiu}">text-success</c:when><c:otherwise>text-default</c:otherwise></c:choose>">
				<c:choose>
					<c:when test="${detall.arxiu}">Arxiu</c:when>
					<c:when test="${detall.nti}">NTI</c:when>
					<c:otherwise>NTI / Arxiu</c:otherwise>
				</c:choose>
				</span>
			</a>
		</li>
		<li id="pipella-documentDetall-registre-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-registre-${detall.documentStoreId}" role="button" data-toggle="tab">
				<span class="fa fa-book <c:choose><c:when test="${detall.registrat}">text-success</c:when><c:otherwise>text-default</c:otherwise></c:choose>"></span>
				<spring:message code="expedient.document.detall.pipella.registre"/>
			</a>
		</li>
		<li id="pipella-documentDetall-signatura-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-signatura-${detall.documentStoreId}" role="tab" data-toggle="tab">
				<span class="fa fa-certificate <c:choose><c:when test="${detall.signat}">text-success</c:when><c:otherwise>text-default</c:otherwise></c:choose>"></span>
				<spring:message code="expedient.document.detall.pipella.signatura"/>
			</a>
		</li>
		<li id="pipella-documentDetall-portasignatura-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-portasignatura-${detall.documentStoreId}" role="tab" data-toggle="tab">
				<span class="fa fa-clock-o <c:choose><c:when test="${detall.psignaPendent}">text-success</c:when><c:otherwise>text-default</c:otherwise></c:choose>"></span>
				<spring:message code="expedient.document.detall.pipella.portasignatura"/>
			</a>
		</li>
		<li id="pipella-documentDetall-notificacions-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-notificacions-${detall.documentStoreId}" role="tab" data-toggle="tab">
				<span class="fa fa-paper-plane-o <c:choose><c:when test="${detall.notificat}">text-success</c:when><c:otherwise>text-default</c:otherwise></c:choose>"></span>
				<spring:message code="expedient.document.detall.pipella.notificacions"/>
			</a>
		</li>
		<li id="pipella-documentDetall-anotacions-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-anotacions-${detall.documentStoreId}" role="tab" data-toggle="tab">
				<span class="fa fa-file-text <c:choose><c:when test="${detall.deAnotacio}">text-success</c:when><c:otherwise>text-default</c:otherwise></c:choose>"></span>
				<spring:message code="expedient.document.detall.pipella.anotacions"/>
			</a>
		</li>
	
		<li id="pipella-documentDetall-previsualitzacio-${detall.documentStoreId}" class="pull-right" role="button">
			<a href="#contingut-documentDetall-previsualitzacio-${detall.documentStoreId}" class="previs-icon" role="tab" data-toggle="tab">
				<span class="fa fa-eye fa-inverse<c:choose><c:when test="${detall.extensio == 'pdf' or detall.extensio == 'pdt' or detall.extensio == 'docx'}">text-success</c:when><c:otherwise>text-default</c:otherwise></c:choose>"></span>
				<spring:message code="expedient.document.previsualitzacio"/>
			</a>
		</li>
					
	</ul>
	
	
	<div class="tab-content">
	
		<%-- REGISTRE => detall.registreDetall --%>
		
		<div id="contingut-documentDetall-registre-${detall.documentStoreId}" class="tab-pane">
			<c:set var="registre" value="${detall.registreDetall}"/>
			<dl class="dl-horizontal">
				<%-- Número de registre--%>
					<dt><spring:message code="expedient.info.numero_registre"/></dt>
					<dd>${registre != null ? registre.registreNumero : '--'}</dd>
				<%-- Data de registre--%>
					<dt><spring:message code="expedient.info.camp.registre.data"/></dt>
					<dd>${registre != null ? '<fmt:formatDate value="${registre.registreData}" pattern="dd/MM/yyyy HH:mm"/>' : '--'}</dd>
				<%-- Oficina de registre--%>
					<dt><spring:message code="expedient.info.camp.registre.oficina"/></dt>
					<dd>${registre != null ? registre.registreOficinaNom : '--'}</dd>
				<%-- Tipus de registre--%>
					<dt><spring:message code="expedient.info.camp.registre.tipus"/></dt>
					<dd>${registre != null ? (registre.registreEntrada ? '<spring:message code="expedient.info.camp.registre.tipus.entrada"/>' : '<spring:message code="expedient.info.camp.registre.tipus.sortida"/>') : '--'}</dd>
			</dl>
	
			<hr class="dark horizontal my-0"/>
			<c:choose>
				<c:when test="${detall.registrat}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">REGISTRAT</span></p></c:when>
				<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO REGISTRAT</span></p></c:otherwise>
			</c:choose>
		</div>
					
			
		<%-- SIGNATURA => detall.signaturaValidacioDetall --%>
		
		<div id="contingut-documentDetall-signatura-${detall.documentStoreId}" class="tab-pane">
			<c:set var="signatura" value="${detall.signaturaValidacioDetall}"/>
			<dl class="dl-horizontal">
				<dt>Verificació de la signatura</dt>
				<dd><c:choose>
					<c:when test="${signatura != null and not empty signatura.urlVerificacio}">
						<a href="${signatura.urlVerificacio}" target="blank"><span class="fa fa-certificate"></span> <span class="fa fa-external-link"></span></a>
					</c:when>
					<c:otherwise>--</c:otherwise>
					</c:choose>
				</dd>
				
				<c:if test="${not empty signatura.tokenSignatura}">
					<c:url value="/v3/expedient/document/arxiuMostrar" var="downloadUrl"><c:param name="token" value="${signatura.tokenSignatura}"/></c:url>
						<dt>Verificació de la signatura</dt>
						<dd><c:choose><c:when test="${signatura != null and not empty signatura.urlVerificacio}"><a href="${signatura.urlVerificacio}"><span class="fa fa-external-link"></span></a></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
				</c:if>
				<c:if test="${not empty signatura.signatures}">
				
					<table class="table table-bordered">
					<tbody>
						<tr>
							<td>Responsable</td>
							<td>NIF</td>
							<td>Estat</td>
						</tr>
					<tbody>
						<c:forEach var="firma" items="${signatura.signatures}" varStatus="status">
							<tr>
								<td>${firma.nomResponsable}</td>
								<td>${firma.nifResponsable}</td>
								<td>
									<c:choose>
										<c:when test="${firma.estatOk}"><span class="fa fa-check text-success"></span></c:when>
										<c:otherwise><span class="fa fa-times text-danger"></span></c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
					</tbody>
					</table>
					
				</c:if>
			</dl>
			<hr class="dark horizontal my-0"/>
			<c:choose>
				<c:when test="${detall.signat}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">SIGNAT</span></p></c:when>
				<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO SIGNAT</span></p></c:otherwise>
			</c:choose>
		</div>
		
	
		<%-- PSIGNA => detall.signaturaValidacioDetall --%>
		<div id="contingut-documentDetall-portasignatura-${detall.documentStoreId}" class="tab-pane">
			<c:set var="psigna" value="${detall.psignaDetall}"/>
			<c:if test="${not empty psigna}">
				<dl class="dl-horizontal">
					
						<dt><spring:message code="common.icones.doc.psigna.id"/></dt>
						<dd><c:choose><c:when test="${not empty psigna.documentId}">${psigna.documentId}</c:when><c:otherwise>--</c:otherwise></c:choose></dd>
					
					
						<dt><spring:message code="common.icones.doc.psigna.data.enviat"/></dt>
						<dd><c:choose><c:when test="${not empty psigna.dataEnviat}"><fmt:formatDate value="${psigna.dataEnviat}" pattern="dd/MM/yyyy HH:mm"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
					
					
						<dt><spring:message code="common.icones.doc.psigna.estat"/></dt>
						<dd>
							<c:choose>
								<c:when test="${psigna.estat == 'PROCESSAT' && psigna.error}">
									<span>REBUTJAT</span>
								</c:when>
								<c:when test="${psigna.estat != 'PROCESSAT' && psigna.error && not empty psigna.errorProcessant}">
									<span>PENDENT</span>
								</c:when>
								<c:otherwise>
									<span>${psigna.estat}</span>
								</c:otherwise>
							</c:choose>
						</dd>
					
					<c:if test="${not empty psigna.motiuRebuig}">
						
							<dt><spring:message code="common.icones.doc.psigna.motiu.rebuig"/></dt>
							<dd>${psigna.motiuRebuig != null ? psigna.motiuRebuig : '--'}</dd>
						
					</c:if>
					<c:if test="${not empty psigna.dataProcessamentPrimer}">
						
							<dt><spring:message code="common.icones.doc.psigna.data.proces.primer"/></dt>
							<dd><c:choose><c:when test="${not empty psigna.dataProcessamentPrimer}"><fmt:formatDate value="${psigna.dataProcessamentPrimer}" pattern="dd/MM/yyyy HH:mm"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
						
					</c:if>
					<c:if test="${not empty psigna.dataProcessamentDarrer}">
						
							<dt><spring:message code="common.icones.doc.psigna.data.proces.darrer"/></dt>
							<dd><c:choose><c:when test="${not empty psigna.dataProcessamentDarrer}"><fmt:formatDate value="${psigna.dataProcessamentDarrer}" pattern="dd/MM/yyyy HH:mm"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
						
					</c:if>
				</dl>
	
	<%--						<c:if test="${psigna.error && psigna.estat != 'PROCESSAT'}">--%>
	<%--							<c:if test="${expedient.permisDocManagement}">--%>
	<%--								<form id="form_psigna_${document.id}" action="<c:url value='../../v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/psignaReintentar'/>">--%>
	<%--									<input type="hidden" name="id" value="${document.processInstanceId}"/>--%>
	<%--									<input type="hidden" name="psignaId" value="${psigna.documentId}"/>--%>
	<%--								</form>--%>
	<%--							</c:if>--%>
	<%--						</c:if>--%>
			</c:if>
			<hr class="dark horizontal my-0"/>
			<c:choose>
				<c:when test="${detall.psignaPendent}">
					<c:choose>
						<c:when test="${psigna.error and psigna.estat != 'PROCESSAT'}">
							<p class="mb-0">Document amb error: <span class="text-dander text-sm font-weight-bolder"><spring:message code="expedient.document.pendent.psigna.error" /></span></p>
						</c:when>
						<c:when test="${psigna.error and psigna.estat == 'PROCESSAT'}">
							<p class="mb-0">Document amb error: <span class="text-danger text-sm font-weight-bolder"><spring:message code="expedient.document.rebutjat.psigna.error" /></span></p>
						</c:when>
						<c:otherwise>
							<p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">PENDENT de Portasignatures</span></p>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO PENDENT de Portasignatures</span></p></c:otherwise>
			</c:choose>
		</div>
	
		<%-- NOTIFICACIONS => detall.notificacions --%>
		<div id="contingut-documentDetall-notificacions-${detall.documentStoreId}" class="tab-pane">
			<c:set var="notificacions" value="${detall.notificacions}"/>
				
				<table class="table table-bordered">
				<thead>
					<tr>
						<th>Destinatari</th>
						<th>Estat</th>
					</tr>
				</thead>
				<tbody>

					<c:forEach var="notificacio" items="${notificacions}" varStatus="status">
						<c:forEach var="enviament" items="${notificacio.enviaments}" varStatus="statuse">
							<tr>
								<td>
									<span class="label label-default">${notificacio.enviamentTipus == "NOTIFICACIO" ? 'N' : 'C'}</span>
										<b>${enviament.titular.nomSencer}</b>
										<p>${notificacio.enviatData}</p>
								</td>
								<td>
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
								</td>
							</tr>
						</c:forEach>
					</c:forEach>
				</tbody>
				</table>
			
			<hr class="dark horizontal my-0"/>
			<c:choose>
				<c:when test="${detall.notificat}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">NOTIFICAT</span></p></c:when>
				<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO NOTIFICAT</span></p></c:otherwise>
			</c:choose>
		</div>
	
	
		<%-- ARXIU/NTI => detall.arxiuDetall --%>
		<div id="contingut-documentDetall-arxiu-${detall.documentStoreId}" class="tab-pane">
			
			<c:set var="arxiuDetall" value="${detall.arxiuDetall}"/>
			<c:set var="ntiDetall" value="${detall.ntiDetall}"/>
			<c:if test="${not empty detall.arxiuDetall}">
				<ul class="nav nav-pills" role="tablist">
					<li role="presentation" class="active"><a class="pill-link" href="#documentDetall_nti_${detall.documentStoreId}" aria-controls="nti" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.nti"/></a></li>
					<li role="presentation"><a class="pill-link" href="#documentDetall_info_${detall.documentStoreId}" aria-controls="info" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.info"/></a></li>
					<c:if test="${not empty arxiuDetall.fills}"><li role="presentation"><a class="pill-link" href="#documentDetall_fills_${detall.documentStoreId}" aria-controls="fills" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.fills"/> <span class="badge badge-default">${fn:length(arxiuDetall.fills)}</span></a></li></c:if>
					<c:if test="${not empty arxiuDetall.firmes}"><li role="presentation"><a class="pill-link" href="#documentDetall_firmes_${detall.documentStoreId}" aria-controls="firmes" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.firmes"/> <span class="badge badge-default">${fn:length(arxiuDetall.firmes)}</span></a></li></c:if>
					<c:if test="${not empty arxiuDetall.metadadesAddicionals}"><li role="presentation"><a class="pill-link" href="#documentDetall_metadades_${detall.documentStoreId}" aria-controls="metadades" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.metadades"/></a></li></c:if>
				</ul>
			</c:if>

				<div class="tab-content">
					<%-- Metadades NTI --%>
					<c:if test="${not empty detall.ntiDetall }">
					
						<div id="documentDetall_nti_${detall.documentStoreId}" class="tab-pane in active">
							<dl class="dl-horizontal">
								<%-- --%>
								
									<dt><spring:message code="document.metadades.nti.version"/></dt>
									<dd>${ntiDetall.ntiVersion != null ? ntiDetall.ntiVersion : '--'}</dd>
								
								
									<dt><spring:message code="document.metadades.nti.identificador"/></dt>
									<dd>${ntiDetall.ntiIdentificador != null ? ntiDetall.ntiIdentificador : '--'}</dd>
								
								
									<dt><spring:message code="document.metadades.nti.organo"/></dt>
									<dd>${ntiDetall.ntiOrgano != null ? ntiDetall.ntiOrgano : '--'}</dd>
								
								
									<dt><spring:message code="document.metadades.nti.fecha.captura"/></dt>
									<dd><c:choose><c:when test="${not empty detall.dataCreacio}"><fmt:formatDate value="${detall.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="document.metadades.nti.origen"/></dt>
									<dd><c:choose><c:when test="${not empty ntiDetall.ntiOrigen}"><spring:message code="nti.document.origen.${ntiDetall.ntiOrigen}"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="document.metadades.nti.estado.elaboracion"/></dt>
									<dd><c:choose><c:when test="${not empty ntiDetall.ntiEstadoElaboracion}"><spring:message code="nti.document.estado.elaboracion.${ntiDetall.ntiEstadoElaboracion}"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="document.metadades.nti.nombre.formato"/></dt>
									<dd><c:choose><c:when test="${not empty ntiDetall.ntiNombreFormato}"><spring:message code="nti.document.format.${ntiDetall.ntiNombreFormato}"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="document.metadades.nti.tipo.documental"/></dt>
									<dd><c:choose><c:when test="${not empty ntiDetall.ntiTipoDocumental}"><spring:message code="nti.document.tipo.documental.${ntiDetall.ntiTipoDocumental}"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="document.metadades.nti.iddoc.origen"/></dt>
									<dd>${ntiDetall.ntiIdDocumentoOrigen != null ? ntiDetall.ntiIdDocumentoOrigen : '--'}</dd>
								
								
									<dt><spring:message code="document.metadades.nti.tipo.firma"/></dt>
									<dd><c:choose><c:when test="${not empty ntiDetall.ntiTipoFirma}">${ntiDetall.ntiTipoFirma}<c:if test="${not empty detall.ntiCsv}">, CSV</c:if></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="document.metadades.nti.csv"/></dt>
									<dd>${ntiDetall.ntiCsv != null ? ntiDetall.ntiCsv : '--'}</dd>
								
								
									<dt><spring:message code="document.metadades.nti.defgen.csv"/></dt>
									<dd>${ntiDetall.ntiDefinicionGenCsv != null ? ntiDetall.ntiDefinicionGenCsv : '--'}</dd>
								
							</dl>
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
						</div>
					</c:if>

					<c:if test="${not empty detall.arxiuDetall}">

						<%-- Informació arxiu --%>
						<div id="documentDetall_info_${detall.documentStoreId}" class="tab-pane">
							<dl class="dl-horizontal">
								
									<dt><spring:message code="expedient.metadades.nti.camp.identificador"/></dt>
									<dd>${arxiuDetall.identificador != null ? arxiuDetall.identificador : '--'}</dd>
								
								
									<dt><spring:message code="expedient.metadades.nti.camp.nom"/></dt>
									<dd>${arxiuDetall.nom != null ? arxiuDetall.nom : '--'}</dd>
								
								<c:if test="${not empty arxiuDetall.serieDocumental}">
									
										<dt><spring:message code="expedient.metadades.nti.camp.serie.doc"/></dt>
										<dd>${arxiuDetall.serieDocumental != null ? arxiuDetall.serieDocumental : '--'}</dd>
									
								</c:if>
							</dl>

							<c:if test="${not empty arxiuDetall.contingutTipusMime or not empty arxiuDetall.contingutArxiuNom}">
							<hr class="dark horizontal my-0">

							<div class="text-dark font-weight-bold"><spring:message code="expedient.metadades.nti.grup.contingut"/></div>
							<dl class="dl-horizontal">
								<c:if test="${not empty arxiuDetall.contingutTipusMime}">
								
									<dt><spring:message code="expedient.metadades.nti.camp.contingut.tipus.mime"/></dt>
									<dd>${arxiuDetall.contingutTipusMime != null ? arxiuDetall.contingutTipusMime : '--'}</dd>
								
								</c:if>
								<c:if test="${not empty arxiuDetall.contingutArxiuNom}">
								
									<dt><spring:message code="expedient.metadades.nti.camp.contingut.arxiu.nom"/></dt>
									<dd>${arxiuDetall.contingutArxiuNom != null ? arxiuDetall.contingutArxiuNom : '--'}</dd>
								
								</c:if>
							</dl>
							</c:if>

							<c:if test="${not empty arxiuDetall.eniIdentificador}">
								<hr class="dark horizontal my-0">

								<div class="text-dark font-weight-bold"><spring:message code="expedient.metadades.nti.grup.metadades"/></div>
								<dl class="dl-horizontal">
									
										<dt><spring:message code="expedient.metadades.nti.camp.eni.versio"/></dt>
										<dd>${arxiuDetall.eniVersio != null ? arxiuDetall.eniVersio : '--'}</dd>
									
									
										<dt><spring:message code="expedient.metadades.nti.camp.eni.identificador"/></dt>
										<dd>${arxiuDetall.eniIdentificador != null ? arxiuDetall.eniIdentificador : '--'}</dd>
									
									<c:if test="${not empty arxiuDetall.eniOrgans}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.organs"/></dt>
											<dd>
												<c:if test="${empty arxiuDetall.eniOrgans}">--</c:if>
												<c:forEach var="organ" items="${arxiuDetall.eniOrgans}" varStatus="status">
													${organ}<c:if test="${not status.last}">,</c:if>
												</c:forEach>
											</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniDataObertura}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.data.obertura"/></dt>
											<dd><c:choose><c:when test="${not empty arxiuDetall.eniDataObertura}"><fmt:formatDate value="${arxiuDetall.eniDataObertura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniClassificacio}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.classificacio"/></dt>
											<dd>${arxiuDetall.eniClassificacio != null ? arxiuDetall.eniClassificacio : '--'}</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniEstat}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.estat"/></dt>
											<dd>${arxiuDetall.eniEstat != null ? arxiuDetall.eniEstat : '--'}</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniDataCaptura}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.data.captura"/></dt>
											<dd><c:choose><c:when test="${not empty arxiuDetall.eniDataCaptura}"><fmt:formatDate value="${arxiuDetall.eniDataCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniOrigen}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.origen"/></dt>
											<dd>${arxiuDetall.eniOrigen != null ? arxiuDetall.eniOrigen : '--'}</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniEstatElaboracio}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.estat.elab"/></dt>
											<dd>${arxiuDetall.eniEstatElaboracio != null ? arxiuDetall.eniEstatElaboracio : '--'}</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniTipusDocumental}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.tipus.doc"/></dt>
											<dd>${arxiuDetall.eniTipusDocumental != null ? arxiuDetall.eniTipusDocumental : '--'}</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniFormat}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.format.nom"/></dt>
											<dd>${arxiuDetall.eniFormat != null ? arxiuDetall.eniFormat : '--'}</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniExtensio}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.format.ext"/></dt>
											<dd>${arxiuDetall.eniExtensio != null ? arxiuDetall.eniExtensio : '--'}</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniInteressats}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.interessats"/></dt>
											<dd>
												<c:if test="${empty arxiuDetall.eniInteressats}">--</c:if>
												<c:forEach var="interessat" items="${arxiuDetall.eniInteressats}" varStatus="status">
													${interessat}<c:if test="${not status.last}">,</c:if>
												</c:forEach>
											</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniDocumentOrigenId}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.doc.orig.id"/></dt>
											<dd>${arxiuDetall.eniDocumentOrigenId != null ? arxiuDetall.eniDocumentOrigenId : '--'}</dd>
										
									</c:if>
									
										<dt><spring:message code="expedient.metadades.nti.camp.eni.firma.csv"/></dt>
										<dd>${arxiuDetall.eniCsv != null ? arxiuDetall.eniCsv : '--'}</dd>
									
									
										<dt><spring:message code="expedient.metadades.nti.camp.eni.firma.csvdef"/></dt>
										<dd>${arxiuDetall.eniCsvDef != null ? arxiuDetall.eniCsvDef : '--'}</dd>
									
									
										<dt><spring:message code="expedient.metadades.nti.camp.arxiuEstat"/></dt>
										<dd>${arxiuDetall.arxiuEstat != null ? arxiuDetall.arxiuEstat : "--"}</dd>
									
								</dl>
							</c:if>
						</div>
						
						<%-- Fills --%>
						<c:if test="${not empty arxiuDetall.fills}">
						<div id="documentDetall_fills_${detall.documentStoreId}" class="tab-pane">
							<dl class="dl-horizontal">
								<c:forEach var="fill" items="${arxiuDetall.fills}" varStatus="status">
									
										<dt>${fill.tipus}</dt>
										<dd>${fill.nom}</dd>
									
								</c:forEach>
							</dl>
						</div>
						</c:if>
	
						<%-- Firmes --%>
						<c:if test="${not empty arxiuDetall.firmes}">
						<div id="documentDetall_firmes_${detall.documentStoreId}" class="tab-pane">
							<table class="table table-bordered">
								<thead>
									<tr>
										<th><spring:message code="expedient.metadades.nti.camp.firma.tipus"/></th>
										<th><spring:message code="expedient.metadades.nti.camp.firma.perfil"/></th>
										<th><spring:message code="expedient.metadades.nti.camp.firma.contingut"/></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${arxiuDetall.firmes}" var="firma" varStatus="status">
										<tr>
											<td>${firma.tipus}</td>
											<td>${firma.perfil}</td>
											<td>
												<c:choose>
													<c:when test="${firma.tipus == 'CSV'}">${firma.contingutComString}</c:when>
													<c:when test="${firma.tipus == 'XADES_DET' or firma.tipus == 'CADES_DET'}">
														<a href="<c:url value='/v3/expedient/${expedientId}/document/${detall.documentStoreId}/firma/${status.index}/descarregar'></c:url>" class="btn btn-default btn-sm pull-right">
															<span class="fa fa-download"  title="<spring:message code="comu.boto.descarregar"/>"></span>
														</a>
													</c:when>
													<c:otherwise></c:otherwise>
												</c:choose>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
								
						</div>
						</c:if>
	
						<%-- Metadades Arxiu --%>
						<c:if test="${not empty arxiuDetall.metadadesAddicionals}">
						<div id="documentDetall_metadades_${detall.documentStoreId}" class="tab-pane">
							<dl class="dl-horizontal">
							<c:forEach var="metadada" items="${arxiuDetall.metadadesAddicionals}" varStatus="status">
								
									<dt>${metadada.key}</dt>
									<dd>${metadada.value}</dd>
								
							</c:forEach>
							</dl>
						</div>
						</c:if>
						
					</c:if>
					
				</div>		
						
			<hr class="dark horizontal my-0"/>
			<c:choose>
				<c:when test="${detall.arxiu}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">ARXIVAT</span></p></c:when>
				<c:when test="${detall.nti}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">CUSTODIAT</span></p></c:when>
				<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO ARXIVAT ni CUSTODIAT</span></p></c:otherwise>
			</c:choose>				
		</div>
		
	
		<%-- ANOTACIO => detall.anotacio --%>
		<div id="contingut-documentDetall-anotacions-${detall.documentStoreId}" class="tab-pane">
			<c:set var="anotacio" value="${detall.anotacio}"/>
			<c:choose>
				<c:when test="${not empty anotacio.annexos}">
					<ul class="nav nav-pills" role="tablist">
						<li role="presentation" class="active"><a class="pill-link" href="#informacio_${anotacio.id}" aria-controls="nti" role="tab" data-toggle="tab"><spring:message code="anotacio.detalls.pipella.informacio"/></a></li>
						<li role="presentation"><a class="pill-link" href="#interessats_${anotacio.id}" aria-controls="info" role="tab" data-toggle="tab"><spring:message code="anotacio.detalls.pipella.interessats"/></a></li>
						<li role="presentation"><a class="pill-link" href="#annexos_${anotacio.id}" aria-controls="fills" role="tab" data-toggle="tab">
							<c:choose>
								<c:when test="${anotacio.errorAnnexos || anotacio.annexosInvalids}"><span class="fa fa-warning text-danger"></span></c:when>
								<c:when test="${anotacio.annexosEsborranys}"><span class="fa fa-warning text-warning"></span></c:when>
							</c:choose>
							<spring:message code="anotacio.detalls.pipella.annexos"/></a></li>
						<c:if test="${not empty anotacio.distibucioErrorNotificacio}"><li role="presentation"><a class="pill-link" href="#error_${anotacio.id}" aria-controls="firmes" role="tab" data-toggle="tab"><spring:message code="anotacio.detalls.pipella.error"/></a></li></c:if>
					</ul>
	
					<%-- Informació --%>
					<div class="tab-content">
					
						<div id="informacio_${anotacio.id}" class="tab-pane in active">
							<dl class="dl-horizontal">
								
									<dt><spring:message code="anotacio.detalls.camp.tipus"/></dt>
									<dd><spring:message code="anotacio.detalls.entrada"/></dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.numero"/></dt>
									<dd>${anotacio.identificador != null ? anotacio.identificador : '--'}</dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.data"/></dt>
									<dd><c:choose><c:when test="${not empty anotacio.data}"><fmt:formatDate value="${anotacio.data}" pattern="dd/MM/yyyy HH:mm:ss"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.estat"/></dt>
									<dd><c:choose><c:when test="${not empty anotacio.estat}"><spring:message code="enum.anotacio.estat.${anotacio.estat}"/></c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
									<c:choose>
										<c:when test="${anotacio.estat == 'ERROR_PROCESSANT'}">
											<div class="d-flex flex-column text-sm text-danger">
												<span class="fa fa-exclamation-triangle"></span>
												<spring:message code="anotacio.detalls.errorProcessament" arguments="${anotacio.errorProcessament}"/>
											</div>
										</c:when>
										<c:when test="${anotacio.estat == 'COMUNICADA'}">
											<div class="d-flex flex-column text-sm text-warning" <c:if test="${anotacio.consultaError != null}">title="${anotacio.consultaError}"</c:if>>
												<span class="fa fa-exclamation-triangle"></span>
												<spring:message code="anotacio.detalls.consulta" arguments="${anotacio.consultaIntents},${ maxConsultaIntents}"/>
											</div>
											<span class="pull-right" id="psignaEstat">PENDENT</span>
										</c:when>
									</c:choose>
								
							</dl>
	
							<hr class="dark horizontal my-0">
	
							<div><h6><spring:message code="anotacio.detalls.titol.obligatories"/></h6></div>
							<dl class="dl-horizontal">
								
									<dt><spring:message code="anotacio.detalls.camp.oficina"/></dt>
									<dd><c:choose><c:when test="${anotacio.oficinaCodi != null}">${anotacio.oficinaDescripcio} (${anotacio.oficinaCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.llibre"/></dt>
									<dd><c:choose><c:when test="${anotacio.llibreCodi != null}">${anotacio.llibreDescripcio} (${anotacio.llibreCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.extracte"/></dt>
									<dd>${anotacio.extracte != null ? anotacio.extracte : '--'}</dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.docfis"/></dt>
									<dd><c:choose><c:when test="${anotacio.docFisicaCodi != null}">${anotacio.docFisicaCodi} - ${anotacio.docFisicaDescripcio})</c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.desti"/></dt>
									<dd><c:choose><c:when test="${anotacio.destiCodi != null}">${anotacio.destiDescripcio} (${anotacio.destiCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.assumpte.tipus"/></dt>
									<dd><c:choose><c:when test="${anotacio.assumpteTipusDescripcio != null}">${anotacio.destiDescripcio} (${anotacio.assumpteTipusCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.idioma"/></dt>
									<dd><c:choose><c:when test="${anotacio.idiomaDescripcio != null}">${anotacio.destiDescripcio} (${anotacio.idiomaCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
								
							</dl>
	
							<hr class="dark horizontal my-0">
	
							<div><h6><spring:message code="anotacio.detalls.titol.opcionals"/></h6></div>
							<dl class="dl-horizontal">
								
									<dt><spring:message code="anotacio.detalls.camp.procediment.codi"/></dt>
									<dd>${anotacio.procedimentCodi != null ? anotacio.procedimentCodi : '--'}</dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.assumpte.codi"/></dt>
									<dd>${anotacio.assumpteCodiCodi != null ? anotacio.assumpteCodiCodi : '--'}</dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.refext"/></dt>
									<dd>${anotacio.refExterna != null ? anotacio.refExterna : '--'}</dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.numexp"/></dt>
									<dd>${anotacio.expedientNumero != null ? anotacio.expedientNumero : '--'}</dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.transport.tipus"/></dt>
									<dd><c:choose><c:when test="${anotacio.transportTipusCodi != null}">${anotacio.transportTipusDescripcio} (${anotacio.transportTipusCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.transport.num"/></dt>
									<dd>${anotacio.transportNumero != null ? anotacio.transportNumero : '--'}</dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.origen.num"/></dt>
									<dd>${anotacio.origenRegistreNumero != null ? anotacio.origenRegistreNumero : '--'}</dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.origen.data"/></dt>
									<dd><c:choose><c:when test="${not empty anotacio.origenData}"><fmt:formatDate value="${anotacio.origenData}" pattern="dd/MM/yyyy HH:mm:ss"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.observacions"/></dt>
									<dd>${anotacio.observacions != null ? anotacio.observacions : '--'}</dd>
								
							</dl>
	
						</div>
	
						<%-- Interessats --%>
						<div id="interessats_${anotacio.id}" class="tab-pane">
							<c:choose>
								<c:when test="${not empty anotacio.interessats}">
									<table class="table table-bordered">
										<thead>
											<tr>
												<th><spring:message code="anotacio.detalls.camp.interessat.tipus"/></th>
												<th><spring:message code="anotacio.detalls.camp.interessat.document"/></th>
												<th><spring:message code="anotacio.detalls.camp.interessat.nom"/></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="interessat" items="${anotacio.interessats}" varStatus="status">
												<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
												
													<td><spring:message code="anotacio.interessat.tipus.enum.${interessat.tipus}"/></td>
													<td>${interessat.documentTipus}: ${interessat.documentNumero}</td>
													<td>
														<c:choose>
															<c:when test="${interessat.tipus == 'PERSONA_FISICA'}">${interessat.nom} ${interessat.llinatge1} ${interessat.llinatge2}</c:when>
															<c:otherwise>${interessat.raoSocial}</c:otherwise>
														</c:choose>
														<button type="button" class="btn btn-default desplegable fr10" href="#detalls_${status.index}" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}"><span class="fa fa-caret-down"></span></button>
													</td>
												</tr>
												<!-- TODO interessats detall -->
											</c:forEach>											
										</tbody>
									</table>
							</c:when>
							<c:otherwise>
								<dt><spring:message code="anotacio.interessat.buit"/></dt>
							</c:otherwise>
							</c:choose>
						</div>
	
						<%-- Annexos --%>
						<div id="annexos_${anotacio.id}" class="tab-pane">
							<h1>TODO: annexos</h1>
						</div>
	
						<%-- Error --%>
						<div id="error_${anotacio.id}" class="tab-pane">
							<dl class="dl-horizontal">
								
	<%--											<dt><spring:message code="anotacio.detalls.errorNotifacio"/></dt>--%>
	<%--											<dl>--%>
	<%--												<a href="<c:url value="/v3/anotacio/${anotacio.id}/reintentarNotificar"/>"--%>
	<%--												   class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span>--%>
	<%--													<spring:message code="anotacio.detalls.annex.accio.reintentar" /></a>--%>
	<%--											</dl>--%>
								
								
									<dt><spring:message code="anotacio.detalls.errorNotifacio"/></dt>
									<dd><pre>${anotacio.distibucioErrorNotificacio}</pre></dd>
							</dl>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<span class="text-dark font-weight-bold">Sense ANOTACIÓ</span>
				</c:otherwise>
			</c:choose>
			<hr class="dark horizontal my-0"/>
			<div class="card-footer p-3">
				<c:choose>
					<c:when test="${detall.deAnotacio}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">PROVÉ d'una ANOTACIÓ</span></p></c:when>
					<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO PROVÉ d'una ANOTACIÓ</span></p></c:otherwise>
				</c:choose>
			</div>
		</div>
		
		<%-- PREVISUALITZACIO--%>
		<div id="contingut-documentDetall-previsualitzacio-${detall.documentStoreId}" class="tab-pane">
		
			<%-- Depenent del format del document serà previsualització o descàrrega--%>
			<c:choose>
				<c:when test="${detall.extensio == 'pdf' or detall.extensio == 'pdt' or detall.extensio == 'docx'}">
					<div class="viewer mtop-4" data-documentid="${detall.documentStoreId}" style="display: none; width: 100%;">
						<iframe class="viewer-iframe" width="100%" height="540" frameBorder="0" style="padding: 15px;"></iframe>
					</div>
				</c:when>
				<c:otherwise>
					<a href="${expedientId}/document/${detall.id}/descarregar" class="position-absolute bg-gradient-info shadow-info text-white font-weight-bold pointer">
						<span class="fa fa-download fa-inverse fa-1x"></span>&nbsp;<spring:message code="expedient.document.descarregar"/></a></li>
						<span>Descarregar</span>
				</c:otherwise>
			</c:choose>			
		
		</div>			
	</div>