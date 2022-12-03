function showViewer(event, documentId, documentNom, contingutCustodiat) {
    var resumViewer = $('#resum-viewer');

    // No executar si ...
    if (event.target.tagName.toLowerCase() !== 'a' && (event.target.cellIndex === undefined || event.target.cellIndex === 0 || event.target.cellIndex === 5)) return;

    // Mostrar/amagar visor
    if (!resumViewer.is(':visible')) {
        resumViewer.slideDown(500);
    } else if (previousDocumentId == undefined || previousDocumentId == documentId) {
        closeViewer();
        previousDocumentId = documentId;
        return;
    }
    previousDocumentId = documentId;

    // Mostrar contingut capçalera visor
    resumViewer.find('*').not('#container').remove();
    var signantsViewerContent = '<div style="padding: 0% 2% 2% 2%; margin-top: -8px;">\
									<table style="width: 453px;"><tbody id="detallSignantsPreview"></tbody></table>\
								 </div>';
    var viewerContent = '<div class="panel-heading">' + msgViewer['previs'] + '<span class="fa fa-close" style="float: right; cursor: pointer;" onClick="closeViewer()"></span></div>\
    					 <div class="viewer-content viewer-padding">\
    						<dl class="dl-horizontal">\
	        					<dt style="text-align: left;">' + msgViewer['nom'] + ' </dt><dd>' + documentNom + '</dd>\
        					</dl>\
    					 </div>';

    // if (contingutCustodiat) {
    //     viewerContent += signantsViewerContent;
    // }
    resumViewer.prepend(viewerContent);
    // if (contingutCustodiat) {
    //     getDetallsSignants($("#detallSignantsPreview"), documentId, true);
    // }


    // Recuperar i mostrar document al visor
    var urlDescarrega = urlDescarrebaBase + documentId + '/returnFitxer';
    $('#container').attr('src', '');
    $('#container').addClass('rmodal_loading');
    showDocument(urlDescarrega);

    $([document.documentElement, document.body]).animate({
        scrollTop: $("#resum-viewer").offset().top - 110
    }, 500);
}

function showDocument(arxiuUrl) {
    // Fa la petició a l'url de l'arxiu
    $.ajax({
        type: 'GET',
        url: arxiuUrl,
        responseType: 'arraybuffer',
        success: function(json) {

            if (json.error) {
                $('#container').removeClass('rmodal_loading');
                $("#resum-viewer .viewer-padding:last").before('<div class="viewer-padding"><div class="alert alert-danger">' + msgViewer['error'] + ': ' + json.errorMsg + '</div></div>');
            } else if (json.warning) {
                $('#container').removeClass('rmodal_loading');
                $("#resum-viewer .viewer-padding:last").before('<div class="viewer-padding"><div class="alert alert-warning">' + msgViewer['warning'] + '</div></div>');
            } else {
                response = json.data;
                var blob = base64toBlob(response.contingut, response.contentType);
                var file = new File([blob], response.contentType, {type: response.contentType});
                link = URL.createObjectURL(file);

                var viewerUrl = urlViewer + '?file=' + encodeURIComponent(link);
                $('#container').removeClass('rmodal_loading');
                $('#container').attr('src', viewerUrl);
            }

        },
        error: function(xhr, ajaxOptions, thrownError) {
            $('#container').removeClass('rmodal_loading');
            alert(thrownError);
        }
    });
}

// Amagar visor
function closeViewer() {
    $('#resum-viewer').slideUp(500, function(){ });
}

function getDetallsSignants(idTbody, contingutId, header) {

    idTbody.html("");
    idTbody.append('<tr class="datatable-dades-carregant"><td colspan="7" style="margin-top: 2em; text-align: center"><img src="<c:url value="/img/loading.gif"/>"/></td></tr>');
    $.get("<c:url value="/contingut/document/"/>" + contingutId + "/mostraDetallSignants", function(json){
        if (json.error) {
            idTbody.html('<tr><td colspan="2" style="width:100%"><div class="alert alert-danger"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button><spring:message code="expedient.document.info.firma.error"/>: ' + json.errorMsg + '</div></td></tr>');
        } else {
            idTbody.html("");
            if(json.data != null && json.data.length > 0){
                json.data.forEach(function(firma){
                    if(firma != null){
                        var firmaDataStr = "";
                        if(firma.responsableNom == null){
                            firma.responsableNom = "";
                        }
                        if(firma.responsableNif == null){
                            firma.responsableNif = "";
                        }
                        if(firma.data != null){
                            firmaDataStr = new Date(firma.data);
                        }
                        if(firma.emissorCertificat == null){
                            firma.emissorCertificat = "";
                        }
                        if (header){
                            idTbody.append('<tr><th style="padding-bottom: 2px;"><strong>'
                                + '<u><spring:message code="expedient.document.info.firma"/></u>'
                                + "</strong></th><tr>");
                        }
                        idTbody.append(
                            "<tr><th><strong>"
                            + '<spring:message code="expedient.document.camp.firma.responsable.nom"/>'
                            + "</strong></th><th>"
                            + firma.responsableNom
                            + "</th></tr><tr><td><strong>"
                            + '<spring:message code="expedient.document.camp.firma.responsable.nif"/>'
                            + "</strong></td><td>"
                            + firma.responsableNif
                            + "</td></tr><tr><td><strong>"
                            + '<spring:message code="expedient.document.camp.firma.responsable.data"/>'
                            + "</strong></td><td>"
                            + (firmaDataStr != "" ? firmaDataStr.toLocaleString() : "")
                            + "</td></tr><tr><td><strong>"
                            + '<spring:message code="expedient.document.camp.firma.emissor.certificat"/>'
                            + "</strong></td><td>"
                            + firma.emissorCertificat
                            + "</td></tr>");
                    }
                })
            }
        }
        webutilRefreshMissatges();
    });
}

function base64toBlob(b64Data, contentType) {
    var contentType = contentType || '';
    var sliceSize = 512;
    var byteCharacters = atob(b64Data);
    var byteArrays = [];
    for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
        var slice = byteCharacters.slice(offset, offset + sliceSize);
        var byteNumbers = new Array(slice.length);
        for (var i=0; i<slice.length; i++) {
            byteNumbers[i] = slice.charCodeAt(i);
        }
        var byteArray = new Uint8Array(byteNumbers);
        byteArrays.push(byteArray);
    }
    var blob = new Blob(byteArrays, {type: contentType});
    return blob;
}

function escape(htmlStr) {
    return htmlStr.replace(/&/g, "&amp;")
        .replace(/\</g, "&lt;")
        .replace(/\>/g, "&gt;")
        .replace(/\"/g, "&quot;")
        .replace(/\'/g, "&#39;");
}