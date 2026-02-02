<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
  <meta charset="UTF-8">
  <title>Documentació API Rest</title>
  <link rel="icon" type="image/png" href="<c:url value="/img/favicon.png"/>" sizes="32x32"/>

  <link href='<c:url value="/js/swagger-ui/css/typography.css"/>' media='screen' rel='stylesheet' type='text/css'/>
  <link href='<c:url value="/js/swagger-ui/css/reset.css"/>' media='screen' rel='stylesheet' type='text/css'/>
  <link href='<c:url value="/js/swagger-ui/css/screen.css"/>' media='screen' rel='stylesheet' type='text/css'/>
  <link href='<c:url value="/js/swagger-ui/css/style.css"/>' media='print' rel='stylesheet' type='text/css'/>
  <link href='<c:url value="/js/swagger-ui/css/print.css"/>' media='print' rel='stylesheet' type='text/css'/>
  
  <script src='<c:url value="/js/swagger-ui/lib/object-assign-pollyfill.js"/>' type='text/javascript'></script>
  <script src='<c:url value="/js/swagger-ui/lib/jquery-1.8.0.min.js"/>' type='text/javascript'></script>
  <script src='<c:url value="/js/swagger-ui/lib/jquery.slideto.min.js"/>' type='text/javascript'></script>
  <script src='<c:url value="/js/swagger-ui/lib/jquery.wiggle.min.js"/>' type='text/javascript'></script>
  <script src='<c:url value="/js/swagger-ui/lib/jquery.ba-bbq.min.js"/>' type='text/javascript'></script>
  <script src='<c:url value="/js/swagger-ui/lib/handlebars-4.0.5.js"/>' type='text/javascript'></script>
  <script src='<c:url value="/js/swagger-ui/lib/lodash.min.js"/>' type='text/javascript'></script>
  <script src='<c:url value="/js/swagger-ui/lib/backbone-min.js"/>' type='text/javascript'></script>
  <script src='<c:url value="/js/swagger-ui/swagger-ui.js"/>' type='text/javascript'></script>
  <script src='<c:url value="/js/swagger-ui/lib/highlight.9.1.0.pack.js"/>' type='text/javascript'></script>
  <script src='<c:url value="/js/swagger-ui/lib/highlight.9.1.0.pack_extended.js"/>' type='text/javascript'></script>
  <script src='<c:url value="/js/swagger-ui/lib/jsoneditor.min.js"/>' type='text/javascript'></script>
  <script src='<c:url value="/js/swagger-ui/lib/marked.js"/>' type='text/javascript'></script>
  <script src='<c:url value="/js/swagger-ui/lib/swagger-oauth.js"/>' type='text/javascript'></script>

  <!-- Some basic translations -->
  <script src='<c:url value="/js/swagger-ui/lang/translator.js"/>' type='text/javascript'></script>
  <script src='<c:url value="/js/swagger-ui/lang/ca.js"/>' type='text/javascript'></script>
<%--   <script src='<c:url value="/js/swagger-ui/lang/es.js"/>' type='text/javascript'></script> --%>

  <style>
    #header > div.swagger-ui-wrap { display: none; }
  </style>

  <c:url var="baseUrl" value="/"/>

  <script type='text/javascript'>
    var collapsable;

    $(document).ready(function () {

      $("#swagger-ui-container").on("click", ".operation .heading", function(event) {
        event.preventDefault();
        event.stopPropagation();
        collapsable = $(this).closest(".operation").find(".content:first");
        collapsable.slideToggle();
      });

    });

    $(function () {
      var url = window.location.search.match(/url=([^&]+)/);
      if (url && url.length > 1) {
        url = decodeURIComponent(url[1]);
      } else {
        url = "${baseUrl}" + "/api-docs";
      }

      hljs.configure({
        highlightSizeThreshold: 5000
      });

      // Pre load translate...
      if(window.SwaggerTranslator) {
        window.SwaggerTranslator.translate();
      }
      window.swaggerUi = new SwaggerUi({
        url: url,
        dom_id: "swagger-ui-container",
        supportedSubmitMethods: ['get', 'post', 'put', 'delete', 'patch'],
        onComplete: function(swaggerApi, swaggerUi){
          if(typeof initOAuth == "function") {
            initOAuth({
              clientId: "your-client-id",
              clientSecret: "your-client-secret-if-required",
              realm: "your-realms",
              appName: "your-app-name",
              scopeSeparator: " ",
              additionalQueryStringParams: {}
            });
          }

          if(window.SwaggerTranslator) {
            window.SwaggerTranslator.translate();
          }
        },
        onFailure: function(data) {
          log("Unable to Load SwaggerUI");
        },
        docExpansion: "none",
        jsonEditor: false,
        defaultModelRendering: 'schema',
        showRequestHeaders: false
      });

      window.swaggerUi.load();

      function log() {
        if ('console' in window) {
          console.log.apply(console, arguments);
        }
      }

      $("body").addClass("swagger-section");
    });
    function desplegaEndpoints() {
      if (!$("#api\\/services_endpoint_list").is(":visible") &&
              $("#api\\/services_endpoint_list li").length > 0) {
        $("#api\\/services_endpoint_list").show();
      } else {
        setTimeout(desplegaEndpoints, 200);
      }
    }
    setTimeout(desplegaEndpoints, 200);
  </script>
</head>

<body class="swagger-section">
<div id='header'>
  <div class="swagger-ui-wrap">
    <a id="logo" href="http://swagger.io">swagger</a>

    <form id='api_selector'>
<!--       <div class='input'><select id="select_baseUrl" name="select_baseUrl"/></div> -->
      <div class='input'><input placeholder="http://example.com/api" id="input_baseUrl" name="baseUrl" type="text"/></div>
      <div id='auth_container'></div>
<!--       <div class='input'><input placeholder="api_key" id="input_apiKey" name="apiKey" type="text"/></div> -->
      <div class='input'><a id="explore" href="#" data-sw-translate>Explore</a></div>
    </form>
  </div>
</div>

<div id="message-bar" class="swagger-ui-wrap" data-sw-translate>&nbsp;</div>
<div id="swagger-ui-container" class="swagger-ui-wrap"></div>
</body>
</html>