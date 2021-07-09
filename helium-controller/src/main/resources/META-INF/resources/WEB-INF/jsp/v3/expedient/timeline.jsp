<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<script type="text/javascript">
	// <![CDATA[
		Timeline_ajax_url='<c:url value="/js/timeline_2.3.0/timeline_ajax/simile-ajax-api.js"/>';
		Timeline_urlPrefix='<c:url value="/js/timeline_2.3.0/timeline_js/"/>';       
		Timeline_parameters="bundle=true";
	// ]]>
	</script>
	<script src="<c:url value="/js/timeline_2.3.0/timeline-2.3.0.js?defaultLocale=ca"/>" type="text/javascript"></script>
	<link href="<c:url value="/js/timeline_2.3.0/timeline_js/timeline-2.3.0.css"/>" rel="stylesheet" type="text/css"/>
	<style>
		.simileAjax-bubble-container, #simile-ajax-history {display: none;}
	</style>
</head>

<script type="text/javascript">
// <![CDATA[            
	var tl;
	$(function() {
		var iniciProces = new Date();
		iniciProces.setTime(Date.parse('<fmt:formatDate value="${expedient.dataInici}" pattern="yyyy/MM/dd"/>'));
		var eventSource = new Timeline.DefaultEventSource();
		var theme = Timeline.ClassicTheme.create();
		theme.firstDayOfWeek = 1;
		var bandInfos = [
			Timeline.createBandInfo({
				eventSource:    eventSource,
				width:          "90%",
				intervalUnit:   Timeline.DateTime.WEEK,
				intervalPixels: 100,
				timeZone:		1,
				date:			iniciProces,
				theme:			theme
			}),
			Timeline.createBandInfo({
				eventSource:    eventSource,
				width:          "10%",
				intervalUnit:   Timeline.DateTime.MONTH,
		           trackGap:       0.4,
				intervalPixels: 50,
				timeZone:		1,
				date:			iniciProces,
				layout:         'overview'
			})
		];
		
		bandInfos[1].syncWith = 0;
		bandInfos[1].highlight = true;
		tl = Timeline.create(document.getElementById("cronograma"), bandInfos);
		Timeline.loadXML("${expedient.id}/timelineXml", function(xml, url) { eventSource.loadXML(xml, url); });
				
		var resizeTimerID = null;
		function onResize() {
		    if (resizeTimerID == null) {
		        resizeTimerID = window.setTimeout(function() {
		            resizeTimerID = null;
		            tl.layout();
		        }, 500);
		    }
		}
	});
// ]]>
</script>

<body>
<div id="cronograma" style="height: 400px; border: 1px solid #aaa"></div>
</body>
</html>