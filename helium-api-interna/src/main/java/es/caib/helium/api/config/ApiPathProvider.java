package es.caib.helium.api.config;

import com.mangofactory.swagger.paths.SwaggerPathProvider;
 
public class ApiPathProvider extends SwaggerPathProvider {
	
	private SwaggerPathProvider defaultSwaggerPathProvider;

	@Override
	public String getApiResourcePrefix() {
		return defaultSwaggerPathProvider.getApiResourcePrefix();
	}

	public void setDefaultSwaggerPathProvider(SwaggerPathProvider defaultSwaggerPathProvider) {
		this.defaultSwaggerPathProvider = defaultSwaggerPathProvider;
	}

	@Override
	protected String applicationPath() {
		return "/";
	}
 
	@Override
	protected String getDocumentationPath() {
		return "/api-docs/";
	}

}
