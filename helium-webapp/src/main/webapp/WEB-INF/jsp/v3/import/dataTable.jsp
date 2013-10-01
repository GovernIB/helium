<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="idTable" value="${param.idTable}"/>
<c:set var="sAjaxSource" value="${param.sAjaxSource}"/>

taula = $("#${idTable}").get(0);

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


// Creació del datatable 
$(taula).dataTable({
	"iDisplayLength": 10,
	"aLengthMenu": [[10, 50, 100], [10, 50, 100]],
	"aaSorting": aaSorting,
	"aoColumns": aoColumns,
	"bAutoWidth": false,
	"bProcessing": true,
	"bServerSide": true,
	"oLanguage": {
		"sUrl": "<c:url value="/js/DT_catala.txt"/>"
	},
	"sAjaxSource": "<c:url value="${sAjaxSource}"/>",
	"fnServerData": function (sSource, aoData, fnCallback) {
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
				$('#dades-carregant').hide();
				console.log("S'ha produït un error en la consulta d'expedients: " + xhr.responseText);
				if (textStatus == 'timeout') {
					alert("La consulta d'expedients ha estat molt de temps per retornar els resultats i s'ha cancel·lat.");
				} else {
					alert("S'ha produït un error en la consulta d'expedients: " + errorThrown);
				}
			}
	    });
	},
	"fnDrawCallback": actualitzarVistaSeleccio,
	"fnRowCallback": function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
		fnRowCallback(nRow, aData, iDisplayIndex, iDisplayIndexFull);
	},
	"fnServerParams": function (aoData) {
		$('#dades-carregant').show();
	}
});