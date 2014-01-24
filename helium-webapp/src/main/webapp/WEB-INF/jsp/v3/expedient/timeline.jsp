<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<style>
.timeline-event-bubble-time {
	display: none;
}
</style>

<div id="cronograma" style="height: 400px; border: 1px solid #aaa"></div>

<script type="text/javascript">
// <![CDATA[
	var tl;
	
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
	Timeline.loadXML("${expedient.id}/timelineXml?id=${instanciaProces.id}", function(xml, url) { eventSource.loadXML(xml, url); });
	
	
	var resizeTimerID = null;
	function onResize() {
	    if (resizeTimerID == null) {
	        resizeTimerID = window.setTimeout(function() {
	            resizeTimerID = null;
	            tl.layout();
	        }, 500);
	    }
	}
	
	window.onresize = onResize;
// ]]>
</script>