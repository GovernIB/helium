<%@ attribute name="tableId" required="true"%>
<%@ attribute name="paginate" required="true"%>
<%@ attribute name="ajaxSourceUrl" required="false"%>
<%@ attribute name="drawCallback" required="false"%>
<%@ attribute name="initComplete" required="false"%>
<%@ attribute name="rowCallback" required="false"%>
<%@ attribute name="serverParamsCallback" required="false"%>
<%@ attribute name="ajaxRefrescarAlertes" required="false"%>
<%@ attribute name="ajaxRefrescarTaula" required="false"%>
<%@ attribute name="ajaxRefrescarPagina" required="false"%>
<%@ attribute name="infoOcultar" required="false"%>
<%@ attribute name="hoverRow" required="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<script>
	// @${ajaxSourceUrl}@ - ${not empty ajaxSourceUrl}
	$(document).ready(function() {
		window.taula = $('#${tableId}');
		<c:if test="${not empty ajaxSourceUrl}">$('thead', taula).append('<tr class="datatable-dades-carregant"><td colspan="7" style="margin-top: 2em; text-align: center"><i class="icon-spinner icon-2x icon-spin"></i></td></tr>');</c:if>
		var aoColumns = new Array();
		var aaSorting = new Array();
		var aProps = new Array();
		$('thead th', taula).each(function() {
			var sortable = ($(this).data("sortable") != undefined) ? $(this).data("sortable") === true : true;
			var visible = ($(this).data("visible") != undefined) ? $(this).data("visible") === true: true;
			aoColumns.push({"bSortable": sortable, "bVisible": visible});
			if ($(this).data("sorting"))
				aaSorting.push([aoColumns.length - 1, $(this).data("sorting")]);
			aProps.push($(this).data("property"));
		});
		taula.dataTable({
			"iDisplayLength": 10,
			"aLengthMenu": [[10, 50, 100], [10, 50, 100]],
			"aaSorting": aaSorting,
			"aoColumns": aoColumns,
			"bAutoWidth": false,
			"bProcessing": ${not empty ajaxSourceUrl},
			"bServerSide": ${not empty ajaxSourceUrl},
			"bPaginate": ${paginate},
			"oLanguage": {
				"sUrl": "<c:url value="/js/DT_catala.txt"/>"
			}<c:if test="${not empty ajaxSourceUrl}">,
			"sAjaxSource": "${ajaxSourceUrl}",
			"fnServerData": function (sSource, aoData, fnCallback, oSettings) {
				for (var i = 0; i < aProps.length; i++) {
					aoData.push({"name": "aProp_" + i, "value": aProps[i]});
				}
				$.ajax({
					"dataType": 'json',
					"type": "GET",
					"url": sSource,
					"data": aoData,
					"success": fnCallback,
					"timeout": 20000,
					"error": function (xhr, textStatus, errorThrown) {
						$('.datatable-dades-carregant', taula).hide();
						console.log("<spring:message code="datatable.ajax.error"/>: " + xhr.responseText);
						if (textStatus == 'timeout')
							alert("<spring:message code="datatable.ajax.timeout"/>");
						else
							alert("<spring:message code="datatable.ajax.error"/>: " + errorThrown);
					}
			    });
			},
			"fnDrawCallback": function (oSettings) {
				$('.datatable-dades-carregant', taula).hide();
				$('.dataTables_info', taula.parent()).removeClass('hidden');
				$('.tramitacioMassiva').removeClass('hidden');
				if (oSettings.aoData.length == 0) {
					$('.dataTables_info', taula.parent()).addClass('hidden');
					$('.tramitacioMassiva').addClass('hidden');
				}
				<c:if test="${infoOcultar == 'true'}">$('.dataTables_info', taula.parent()).addClass('hidden');</c:if>
				<c:if test="${not empty drawCallback}">${drawCallback}();</c:if>
				<c:if test="${hoverRow != 'false'}">
					$('.dataTable').addClass('table-hover');
					$('.dataTable > tbody > tr > td:not(:last-child)').css('cursor','pointer');
					var tcheck = ${hoverRow=='check'};
					var hfcol = oSettings.aoColumns[0].bVisible;
					$('.dataTable > tbody > tr > td:not(:last-child' + ( tcheck && hfcol ? ', :first-child)' : ')')).click(function(event) {
						event.stopPropagation();
						
						var url = $('ul a:first', $(this).parent()).attr("href");
						var idExpedient = url.substr("expedient/".length ,url.length);

						if ($("tr.info-" + idExpedient).length) {
							return;
						}
						
						var numCols = $("tr#" + idExpedient).children('td').length;

						$( ".tr-pendents" ).each(function( index ) {
							$( this ).fadeOut();
							$( this ).remove();
						});
						
						$("tr#" + idExpedient).after("<tr id='contingut-carregant' class='tr-pendents hide'>"+
								"<td colspan='" + (numCols - 1)+ "'>"+
									"<div><p style='margin-top: 2em; text-align: center'><i class='icon-spinner icon-2x icon-spin'></i></p></div>"+
								"</td></tr>");
						
						$('#contingut-carregant').show();
						
						$.ajax({
							"url": "/helium/nodecorar/v3/expedient/" + idExpedient + "/tasquesPendents",
							"success": function (data) {								
								$("tr#" + idExpedient).after("<tr class='tr-pendents info-" + idExpedient + "'>"+
										"<td colspan='" + (numCols - 1)+ "'>" + data + "</td>").fadeIn();
								$('#contingut-carregant').remove();
							},
						  	"error": function(XMLHttpRequest, textStatus, errorThrown) {
						  		$('#contingut-carregant').remove();
							}
					    });
					});
				</c:if>
				$("a", taula.parent()).on('click', function() {
					var confirmat = true;
					if ($(this).data('confirm'))
						confirmat = confirm($(this).data('confirm'));
					var ajax = ($(this).data("ajax") != undefined) ? $(this).data("ajax") === true : false;
					var modal = ($(this).data("modal") != undefined) ? $(this).data("modal") === true : false;
					if (confirmat) {
						if (ajax) {
							var ajaxUrl = $(this).attr("href");
							if (ajaxUrl.indexOf("../") != -1)
								ajaxUrl = ajaxUrl.substr(0, ajaxUrl.lastIndexOf("../") + "../".length) + "ajax/" + ajaxUrl.substr(ajaxUrl.lastIndexOf("../") + "../".length);
							else
								ajaxUrl = "ajax/" + ajaxUrl;
							//alert("AJAX: " + ajaxUrl);
							$.ajax({
								"type": "GET",
								"url": ajaxUrl,
								"timeout": 20000,
								"success": function() {
									<c:if test="${ajaxRefrescarTaula == 'true'}">
									var refrescat = false;
									$('.dataTables_paginate li', taula.parent()).each(function() {
										if ($(this).hasClass('active')) {
											$('a', this).click();
											refrescat = true;
										}
									});
									if (!refrescat)
										taula.dataTable().fnDraw();
									</c:if>
									<c:if test="${ajaxRefrescarPagina == 'true'}">
										window.parent.location.reload();
									</c:if>
									<c:if test="${ajaxRefrescarAlertes == 'true'}">
									$.ajax({
										"url": "<c:url value="/nodeco/util/alertes"/>",
										"success": function (data) {
											$('.contingut-alertes *').remove();
											$('.contingut-alertes').append(data);
										}
								    });
									</c:if>
								},
								"error": function(xhr, textStatus, errorThrown) {
									console.log("<spring:message code="peticio.ajax.error"/>: " + xhr.responseText);
									if (textStatus == 'timeout')
										alert("<spring:message code="peticio.ajax.timeout"/>");
									else
										alert("<spring:message code="peticio.ajax.error"/>: " + errorThrown);
								}
						    });
							return false;
						} else if (modal) {
							var modalUrl = $(this).attr("href");
							if (modalUrl.indexOf("../") != -1)
								modalUrl = modalUrl.substr(0, modalUrl.lastIndexOf("../") + "../".length) + "modal/" + modalUrl.substr(modalUrl.lastIndexOf("../") + "../".length);
							else
								modalUrl = "modal/" + modalUrl;
							var modalobj = $('#modal-' + $(this).data("modal-id"));
							modalobj.on('show.bs.modal', function () {
								$('iframe', modalobj).attr(
										"src",
										modalUrl);
							});
							modalobj.modal('show');
							return false;
						} else {
							return true;
						}
					} else {
						return false;
					}
				});
			},
			"fnRowCallback": function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
				<c:if test="${not empty rowCallback}">${rowCallback}(nRow, aData, iDisplayIndex, iDisplayIndexFull);</c:if>
			},
			"fnServerParams": function (aoData) {
				$('.datatable-dades-carregant', taula).show();
				<c:if test="${not empty serverParamsCallback}">${serverParamsCallback}(aoData);</c:if>
			},
			"fnInitComplete": function (oSettings, json) {
				<c:if test="${not empty initComplete}">${initComplete}();</c:if>
			}
			</c:if>
		});
	});
</script>