// Canviar entre obert i tancat del detall del document (Tancar també detalls d'altres documents)
const toggleDocDetails = (row) => {

    // Comprovam si el detall del document actual està obert
    let detallActualVisible = $(row).next().hasClass('doc-details') && $(row).next().is(":visible");

    // Ocultam tots els detalls que es trobin actualment oberts (en principi només n'hi pot haver un)
    $("#expedientDocuments").find('tr.doc-details:visible').each((index, detail) => {
        closeDocDetail(detail);
    });

    // Si el document actual no tenia el detall obert, l'obrim. (Si el tenia obert, l'acabem de tancar)
    if (!detallActualVisible) {
        showDocDetail(row);
    }
}

// Tancar el detall d'un document
const closeDocDetail = (detail) => {
    // Per tancar el detall dels documents ho feim amb un efecte d'acordiò de 500ms
    $(detail).slideUp(500);
}

// Mostrar el detall d'un document
const showDocDetail = (row) => {
    // Si ja s'havia carregat el detall, el tornem a obrir amb un efecte d'acordió de 500ms
    if ($(row).next().hasClass('doc-details')) {
        $(row).next().slideDown(500);

    // Si encara no s'havia carregat el detall del document, l'hem de crear
    } else {
        createDocDetail(row);
    }
}

// Crea la fila amb el detall del document
const createDocDetail = (row) => {
    // Obtenim l'identificador del document
    const documentId = $(row).find(".accionsDocument").first().data("documentId");
    const documentNom = escape($(row).find("td:eq(1)").text().trim());

    // Feim una crida ajax per obtenir els detalls del document
    $.ajax({
        url: urlDescarregaBase + documentId + "/detall/",
        beforeSend: function(xhr) {
            // Abans d'obtenir els detalls mostram un fila amb un spinner per indicar que s'està carregant
            addRowLoadingDetail(row, documentId);
        }
    }).done(function(data) {
        // Al rebre els detalls afegim la fila amb els detalls
        addRowDetail(row, documentId, data);
    }).fail(function(jqXHR, exception) {
        modalAjaxErrorFunction(jqXHR, exception);
    });




    // const documentArxivat = $(element).find(".accionsDocument").first().data("arxivat");
    // showViewer(e, documentId, documentNom);

}

// Afegim dues files per a mantenir els colors de parells i senars de la taula.
// En aquest mètode mostram l'spinner de càrrega
const addRowLoadingDetail = (row, id) => {
    $(row).after('<tr class="doc-details"><td colspan="6"><span class="fa fa-circle-o-notch fa-spin"></span></td></tr><tr class="ocult"><td></td></tr>');
    slideRowDown($(row).next(), 200);
}

// Substituim el contingut de la fila de l'spinner de càrrega amb els detalls del document
const addRowDetail = (row, id, data) => {
    // Ocultam la fila
    slideRowUp($(row).next(),100, () => {
        // Subtituim el contingut de la fina
        $(row).next().find('td').html(data);
        // Mostram la fila
        slideRowDown($(row).next(), 500);
    });
}

// Ocultam una fila d'una taula amb un efecte d'slide.
const slideRowUp = (row, duration, funcio) => {
    $(row).find('td').wrapInner('<div id="wrapping-slide" style="display: block;" />');
    $('#wrapping-slide').slideUp(duration, function(){
        $('#wrapping-slide').replaceWith($('#wrapping-slide').contents());
        if (funcio != undefined) {
            funcio();
        }
    });
}

// Visualitzan una fila d'una taula amb un efecte d'slide
const slideRowDown = (row, duration, funcio) => {
    $(row).find('td').wrapInner('<div id="wrapping-slide" style="display: none;" />');
    $('#wrapping-slide').slideDown(duration, () => {
        var $set = $(this);
        $('#wrapping-slide').replaceWith($('#wrapping-slide').contents());
        if (funcio != undefined) {
            funcio();
        }
    });
}


// Visualitzam / Ocultam el cos del card
const toggleCard = (cardHeader) => {
    $(cardHeader).next().slideToggle(350, toggleCardCaret(cardHeader));
    // let cardBody = $(cardHeader).next();
    // let visible = cardBody.is(':visible');
}

// Canvia el caret entre up i down
const toggleCardCaret = (cardHeader) => {
    let caretUp = $(cardHeader).find('.fa-caret-up').length;
    if (caretUp) {
        $(cardHeader).find('.fa-caret-up').removeClass('fa-caret-up').addClass('fa-caret-down');
    } else {
        $(cardHeader).find('.fa-caret-down').removeClass('fa-caret-down').addClass('fa-caret-up');
    }
}

const toggleViewer = (viewer) => {
    if (!$(viewer).is(':visible')) {
        // let documentId = $(viewer).data('documentid');
        // var urlDescarrega = urlDescarregaBase + documentId + '/returnFitxer';
        $(viewer).slideDown(500);
        console.log('Loaded', $(viewer).data('loaded'));
        if ($(viewer).data('loaded') == undefined) {
            showDocument(viewer);
        }
    } else {
        $(viewer).slideUp(500);
    }

}

// function showViewer(event, documentId, documentNom, contingutCustodiat) {
//     var resumViewer = $('#resum-viewer');
//
//     // No executar si ...
//     if (event.target.tagName.toLowerCase() !== 'a' && (event.target.cellIndex === undefined || event.target.cellIndex === 0 || event.target.cellIndex === 5)) return;
//
//     // Mostrar/amagar visor
//     if (!resumViewer.is(':visible')) {
//         resumViewer.slideDown(500);
//     } else if (previousDocumentId == undefined || previousDocumentId == documentId) {
//         closeViewer();
//         previousDocumentId = documentId;
//         return;
//     }
//     previousDocumentId = documentId;
//
//     // Mostrar contingut capçalera visor
//     resumViewer.find('*').not('#container').remove();
//     var signantsViewerContent = '<div style="padding: 0% 2% 2% 2%; margin-top: -8px;">\
// 									<table style="width: 453px;"><tbody id="detallSignantsPreview"></tbody></table>\
// 								 </div>';
//     var viewerContent = '<div class="panel-heading">' + msgViewer['previs'] + '<span class="fa fa-close" style="float: right; cursor: pointer;" onClick="closeViewer()"></span></div>\
//     					 <div class="viewer-content viewer-padding">\
//     						<dl class="dl-horizontal">\
// 	        					<dt style="text-align: left;">' + msgViewer['nom'] + ' </dt><dd>' + documentNom + '</dd>\
//         					</dl>\
//     					 </div>';
//
//     // if (contingutCustodiat) {
//     //     viewerContent += signantsViewerContent;
//     // }
//     resumViewer.prepend(viewerContent);
//     // if (contingutCustodiat) {
//     //     getDetallsSignants($("#detallSignantsPreview"), documentId, true);
//     // }
//
//
//     // Recuperar i mostrar document al visor
//     var urlDescarrega = urlDescarregaBase + documentId + '/returnFitxer';
//     $('#container').attr('src', '');
//     $('#container').addClass('rmodal_loading');
//     showDocument(urlDescarrega);
//
//     $([document.documentElement, document.body]).animate({
//         scrollTop: $("#resum-viewer").offset().top - 110
//     }, 500);
// }

const showDocument = (viewer) => {
    console.log('Show document');

    let documentId = $(viewer).data('documentid');
    let arxiuUrl = urlDescarregaBase + documentId + '/returnFitxer';
    let contenidor = $(viewer).find('iframe');

    $(viewer).prepend('<span class="fa fa-circle-o-notch fa-spin"></span>');
    contenidor.addClass('rmodal_loading');

    // Fa la petició a l'url de l'arxiu
    $.ajax({
        type: 'GET',
        url: arxiuUrl,
        responseType: 'arraybuffer',
        success: function(json) {

            if (json.error) {
                $(viewer).before('<div class="viewer-padding"><div class="alert alert-danger">' + msgViewer['error'] + ': ' + json.errorMsg + '</div></div>');
            } else if (json.warning) {
                $(viewer).before('<div class="viewer-padding"><div class="alert alert-warning">' + msgViewer['warning'] + '</div></div>');
            } else {
                response = json.data;
                let blob = base64toBlob(response.contingut, response.contentType);
                let file = new File([blob], response.contentType, {type: response.contentType});
                let link = URL.createObjectURL(file);

                var viewerUrl = urlViewer + '?file=' + encodeURIComponent(link);
                contenidor.attr('src', viewerUrl);
                $(viewer).data('loaded', "true");
            }
            $(viewer).find('.fa-spin').remove();

        },
        error: function(xhr, ajaxOptions, thrownError) {
            $(viewer).find('.fa-spin').remove();
            alert(thrownError);
        }
    });
}

// // Amagar visor
// function closeViewer() {
//     $('#resum-viewer').slideUp(500);
// }

// function getDetallsSignants(idTbody, contingutId, header) {
//
//     idTbody.html("");
//     idTbody.append('<tr class="datatable-dades-carregant"><td colspan="7" style="margin-top: 2em; text-align: center"><img src="<c:url value="/img/loading.gif"/>"/></td></tr>');
//     $.get("<c:url value="/contingut/document/"/>" + contingutId + "/mostraDetallSignants", function(json){
//         if (json.error) {
//             idTbody.html('<tr><td colspan="2" style="width:100%"><div class="alert alert-danger"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button><spring:message code="expedient.document.info.firma.error"/>: ' + json.errorMsg + '</div></td></tr>');
//         } else {
//             idTbody.html("");
//             if(json.data != null && json.data.length > 0){
//                 json.data.forEach(function(firma){
//                     if(firma != null){
//                         var firmaDataStr = "";
//                         if(firma.responsableNom == null){
//                             firma.responsableNom = "";
//                         }
//                         if(firma.responsableNif == null){
//                             firma.responsableNif = "";
//                         }
//                         if(firma.data != null){
//                             firmaDataStr = new Date(firma.data);
//                         }
//                         if(firma.emissorCertificat == null){
//                             firma.emissorCertificat = "";
//                         }
//                         if (header){
//                             idTbody.append('<tr><th style="padding-bottom: 2px;"><strong>'
//                                 + '<u><spring:message code="expedient.document.info.firma"/></u>'
//                                 + "</strong></th><tr>");
//                         }
//                         idTbody.append(
//                             "<tr><th><strong>"
//                             + '<spring:message code="expedient.document.camp.firma.responsable.nom"/>'
//                             + "</strong></th><th>"
//                             + firma.responsableNom
//                             + "</th></tr><tr><td><strong>"
//                             + '<spring:message code="expedient.document.camp.firma.responsable.nif"/>'
//                             + "</strong></td><td>"
//                             + firma.responsableNif
//                             + "</td></tr><tr><td><strong>"
//                             + '<spring:message code="expedient.document.camp.firma.responsable.data"/>'
//                             + "</strong></td><td>"
//                             + (firmaDataStr != "" ? firmaDataStr.toLocaleString() : "")
//                             + "</td></tr><tr><td><strong>"
//                             + '<spring:message code="expedient.document.camp.firma.emissor.certificat"/>'
//                             + "</strong></td><td>"
//                             + firma.emissorCertificat
//                             + "</td></tr>");
//                     }
//                 })
//             }
//         }
//         webutilRefreshMissatges();
//     });
// }

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