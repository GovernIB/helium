<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="idioma">ca</c:set>

<script src="<c:url value="/js/webutil.common.js"/>"></script>

<link rel="stylesheet" href="https://unpkg.com/bpmn-js@18.16.1/dist/assets/diagram-js.css" />
<link rel="stylesheet" href="https://unpkg.com/bpmn-js@18.16.1/dist/assets/bpmn-js.css" />
<link rel="stylesheet" href="https://unpkg.com/bpmn-js@18.16.1/dist/assets/bpmn-font/css/bpmn.css" />
<script src="https://unpkg.com/bpmn-js@18.16.1/dist/bpmn-modeler.production.min.js"></script>

<c:choose>
	<c:when test="${not empty definicioProces}">
		<div id="editor-error" class="alert alert-danger" role="alert" style="display: none">
			<span class="fa fa-exclamation-triangle"></span>&nbsp;
			 <spring:message code='definicio.proces.editor.error.no.visualitzar'/>
		</div>
		<div style="position: relative">
			<div id="canvas" style="height: 800px; border: 1px solid #ddd"></div>
			<div id="buttons" style="position: absolute; right: 20px; top: 20px;">
				<button id="editor-action-revert" class="btn btn-default" title="Desfer canvis"><span class="fa fa-undo"></span></button>
				<button id="editor-action-xml" class="btn btn-default" title="Descarregar BPMN"><span class="fa fa-download"></span></button>
				<button id="editor-action-save" class="btn btn-default" title="Desar canvis"><span class="fa fa-floppy-o"></span></button>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='definicio.proces.detall.cap'/></div>
	</c:otherwise>
</c:choose>

<script>
	var bpmnModeler = new BpmnJS({ container: '#canvas' });
	const loadDiagram = async (url) => {
		$('#editor-error').css('display', 'none');
		try {
			const res = await fetch(url);
			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			const xml = await res.text();
			const hasDiagram = xml.includes('BPMNDiagram');
			if (hasDiagram) {
				bpmnModeler.importXML(xml).then(() => {
					bpmnModeler.get('canvas').zoom('fit-viewport');
				});
			} else {
				$('#editor-error').css('display', 'block');
				console.error('<spring:message code='definicio.proces.editor.error.no.visualitzar'/>: ', xml);
			}
		} catch (err) {
			console.error(err);
		}
	}
	const showXml = () => {
		bpmnModeler.saveXML({ format:true }).then(({ xml }) => {
			console.log(xml);
		});
	}
	document.getElementById('editor-action-xml').addEventListener('click', showXml);
	loadDiagram("${definicioProces.id}/editorXml");
</script>
