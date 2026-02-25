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
						<c:if test="${document.documentStoreId != null }">
							<a href="<c:url value="/v3/expedient/${tasca.expedientId}/proces/${tasca.processInstanceId}/document/${document.documentStoreId}/descarregar"/>"><span class="fa fa-download" title="<spring:message code="comuns.descarregar"/>"></span></a>
							<c:if test="${document.signat}">
								<c:choose>
									<c:when test="${not empty document.urlVerificacioCustodia}">
											<a class="icon signature" href="${document.urlVerificacioCustodia}" target="_blank"><span class="fa fa-certificate" title="<spring:message code="expedient.document.signat"/>"></span></a>
									</c:when>								
									<c:when test="${not empty document.signaturaUrlVerificacio}">
											<c:choose>
												<c:when test="${document.ntiCsv != null}">
													<!--  Url del ConCSV per l'Arxiu -->
													<a class="icon signature" href="${document.signaturaUrlVerificacio}" target="_blank"><span class="fa fa-certificate" title="<spring:message code="expedient.document.signat"/>"></span></a>
												</c:when>
												<c:otherwise>
													<!-- S'ha de consultar el CSV -->
													<a class="icon signature" href="<c:url value='/v3/expedient/${tasca.expedientId}/proces/${tasca.processInstanceId}/document/${document.documentStoreId}/signatura/verificarCsv'/>" target="_blank"><span class="fa fa-certificate" title="<spring:message code="expedient.document.signat"/>"></span></a>
												</c:otherwise>
											</c:choose>
									</c:when>
								</c:choose>								
							</c:if>
							<c:if test="${document.registrat}">
								<a 	data-rdt-link-modal="true" 
									class="icon registre" 
									href="<c:url value='/modal/v3/expedient/${expedientId}/verificarRegistre/${document.documentStoreId}/${document.documentCodi}'/>">
									<span class="fa fa-book" title="<spring:message code='expedient.document.registrat' />"></span>
								</a>
							</c:if>
						</c:if>
					</td>
				</tr>
			</table>
		</c:otherwise>
	</c:choose>
</td>