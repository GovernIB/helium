package com.mangofactory.swagger.controllers;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.core.SwaggerCache;
import com.mangofactory.swagger.models.dto.ApiListing;
import com.mangofactory.swagger.models.dto.ResourceListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class DefaultSwaggerController {
    public static final String DOCUMENTATION_BASE_PATH = "/api-docs";
    @Autowired
    private SwaggerCache swaggerCache;

    public DefaultSwaggerController() {
    }

    @ApiIgnore
    @RequestMapping(value = {"/api-docs"}, method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<ResourceListing> getResourceListing(@RequestParam(value = "group",required = false) String swaggerGroup) {
        return this.getSwaggerResourceListing(swaggerGroup);
    }

    @ApiIgnore
    @RequestMapping(value = {"/api-docs/{swaggerGroup}/{apiDeclaration}"}, method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<ApiListing> getApiListing(@PathVariable String swaggerGroup, @PathVariable String apiDeclaration) {
        return this.getSwaggerApiListing(swaggerGroup, apiDeclaration);
    }

    private ResponseEntity<ApiListing> getSwaggerApiListing(String swaggerGroup, String apiDeclaration) {
        ResponseEntity<ApiListing> responseEntity = new ResponseEntity(HttpStatus.NOT_FOUND);
        Map<String, ApiListing> apiListingMap = (Map)this.swaggerCache.getSwaggerApiListingMap().get(swaggerGroup);
        if (null != apiListingMap) {
            ApiListing apiListing = (ApiListing)apiListingMap.get(apiDeclaration);
            if (null != apiListing) {
                if (apiListing.getResourcePath() != null && apiListing.getResourcePath().substring(1).contains("/")) {
                    String resourcePath = apiListing.getResourcePath();
                    if (resourcePath.charAt(0) == '/') {
                        resourcePath = "/" + resourcePath.substring(1).replace("/", "_");
                    } else {
                        resourcePath = resourcePath.replace("/", "_");
                    }

                    apiListing = new ApiListing(
                            apiListing.getApiVersion(),
                            apiListing.getSwaggerVersion(),
                            apiListing.getBasePath(),
                            resourcePath,
                            apiListing.getProduces(),
                            apiListing.getConsumes(),
                            apiListing.getProtocol(),
                            apiListing.getAuthorizations(),
                            apiListing.getApis(),
                            apiListing.getModels(),
                            apiListing.getDescription(),
                            apiListing.getPosition());
                }
                responseEntity = new ResponseEntity(apiListing, HttpStatus.OK);
            }
        }

        return responseEntity;
    }

    private ResponseEntity<ResourceListing> getSwaggerResourceListing(String swaggerGroup) {
        ResponseEntity<ResourceListing> responseEntity = new ResponseEntity(HttpStatus.NOT_FOUND);
        ResourceListing resourceListing = null;
        if (null == swaggerGroup) {
            resourceListing = (ResourceListing)this.swaggerCache.getSwaggerApiResourceListingMap().values().iterator().next();
        } else if (this.swaggerCache.getSwaggerApiResourceListingMap().containsKey(swaggerGroup)) {
            resourceListing = (ResourceListing)this.swaggerCache.getSwaggerApiResourceListingMap().get(swaggerGroup);
        }

        if (null != resourceListing) {
            responseEntity = new ResponseEntity(resourceListing, HttpStatus.OK);
        }

        return responseEntity;
    }
}
