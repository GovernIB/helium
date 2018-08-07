<c:set var="document" value="${dada}"/>
<style type="text/css">
	#ra-document-${expedientId}-${document.id} .nom_document{
		padding-right: 15px;
	}	
	#ra-document-${expedientId}-${document.id} .obligatori {
	    background-position: right 7px;
	}
	#ra-document-${expedientId}-${document.id} .tableDocumentsTd {
	    font-size: 15px;
	}
</style>

<script type="text/javascript">
	// <![CDATA[
		$(document).ready(function() {
			$('#ra-document-${expedientId}-${document.id} .icon').heliumEvalLink({
				refrescarAlertes: true,
				refrescarPagina: false,
				alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>"
			});
		});
	//]]>
</script>

<td id="ra-document-${expedientId}-${document.id}">									
	<c:choose>
		<c:when test="${not empty document.error}">
			<span class="fa fa-warning fa-2x" title="${document.error}"></span>
		</c:when>
		<c:otherwise>
			<table id="document_${document.id}" class="table-condensed marTop6 tableDocuments">
				<tr <c:if test="${empty document.id}">class="hide"</c:if>>
					<td>
						<label class="nom_document<c:if test="${document.required}"> obligatori</c:if>">${document.documentNom}</label>
					</td>
					<td class="tableDocumentsTd">		
		 				<c:url value="/v3/expedient/document/arxiuMostrar" var="downloadUrl"><c:param name="token" value="${document.tokenSignatura}"/></c:url>
						<c:if test="${not empty document.tokenSignatura}">
							<a title="<spring:message code='comuns.descarregar' />" id="downloadUrl" href="${downloadUrl}">
								<i class="fa fa-download"></i>
							</a>
						</c:if>												
						<c:if test="${document.signat}">																					
							<a 	data-rdt-link-modal="true" 
								<c:if test="${not empty document.urlVerificacioCustodia}">data-rdt-link-modal-min-height="400"</c:if>
								class="icon signature" 
								href="${document.urlVerificacioCustodia}">
								<span class="fa fa-certificate" title="<spring:message code='expedient.document.signat' />"></span>
							</a>
						</c:if>
						
						<c:if test="${document.registrat}">
							<a 	data-rdt-link-modal="true" 
								class="icon registre" 
								href="<c:url value='/modal/v3/expedient/${expedientId}/verificarRegistre/${document.documentStoreId}/${document.documentCodi}'/>">
								<span class="fa fa-book" title="<spring:message code='expedient.document.registrat' />"></span>
							</a>
						</c:if>											
					</td>
				</tr>
			</table>
		</c:otherwise>
	</c:choose>
</td>