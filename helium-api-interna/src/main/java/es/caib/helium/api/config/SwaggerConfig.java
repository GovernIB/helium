package es.caib.helium.api.config;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.models.dto.AuthorizationType;
import com.mangofactory.swagger.models.dto.BasicAuth;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

@Configuration
@EnableSwagger
@ComponentScan(basePackages = {"es.caib.helium.api.interna.controller"})
public class SwaggerConfig {

	private SpringSwaggerConfig springSwaggerConfig;
	 
    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }
 
    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {
		AuthorizationType basicAuth = new BasicAuth();
		return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiInfo(apiInfo())
                .apiVersion("1.0")
				.authorizationTypes(Arrays.asList(basicAuth))
                .ignoredParameterTypes(ResponseEntity.class)
				.includePatterns(
                        ".*/salut",
                        ".*/salut/*",
                        ".*/logs",
                        ".*/logs/*",
                        ".*/estadistiques",
                        ".*/estadistiques/*.*"
                        )
				.useDefaultResponseMessages(false);
    }

	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo(
				"API Interna de HELIUM",
				"API INTERNA de HELIUM",
				"", 	// URL de temes de servei
				"limit@limit.es",
				"",		// Llicència
				""); 	// URL Llicència
				
		return apiInfo;
	}
	
	/**
	 * Class that provides your applications url context path
	 */
	@Bean
	public ApiPathProvider apiPathProvider() {
		ApiPathProvider apiPathProvider = new ApiPathProvider(); //"http://localhost:8080");
		apiPathProvider.setDefaultSwaggerPathProvider(springSwaggerConfig.defaultSwaggerPathProvider());
		return apiPathProvider;
	}
}