/**
 * 
 */
package es.caib.helium.back.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.opensymphony.module.sitemesh.filter.PageFilter;

import es.caib.helium.back.interceptor.AplicacioInterceptor;

/**
 * Configuració dels interceptors de peticions.
 * 
 * @author Limit Tecnologies
 */
@Configuration
@DependsOn("ejbClientConfig")
@SuppressWarnings("deprecation")
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private AplicacioInterceptor aplicacioInterceptor;

	@Bean
	public FilterRegistrationBean<PageFilter> sitemeshFilter() {
		FilterRegistrationBean<PageFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new PageFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(2);
		return registrationBean;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		String[] excludedPathPatterns = new String [] {
				"/js/**",
				"/css/**",
				"/fonts/**",
				"/img/**",
				"/images/**",
				"/extensions/**",
				"/webjars/**",
				"/**/datatable/**",
				"/**/selection/**",
				"/**/rest/notib**",
				"/**/rest/notib/**",
				"/api/rest**",
				"/api/rest/**",
				"/api-docs/**",
				"/**/api-docs/",
				"/public/**"
		};
		registry.addInterceptor(aplicacioInterceptor).excludePathPatterns(excludedPathPatterns);
	}
	
	/** Configura el firewall per permetre caràcters codificats com el % ja que aquests s'usen en la codificació
	 * dels identificadors en els enllaços públics de descàrrega de documents.
	 * 
	 * @return
	 */
	@Bean
    public HttpFirewall getHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowBackSlash(true);
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowUrlEncodedPeriod(true);
        return firewall;
    }
}
