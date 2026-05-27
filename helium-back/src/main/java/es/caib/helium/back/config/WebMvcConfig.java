/**
 *
 */
package es.caib.helium.back.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.module.sitemesh.filter.PageFilter;

import es.caib.helium.back.interceptor.AjaxInterceptor;
import es.caib.helium.back.interceptor.AplicacioInterceptor;
import es.caib.helium.back.interceptor.EntornInterceptor;
import es.caib.helium.back.interceptor.ModalInterceptor;
import es.caib.helium.back.interceptor.NodecoInterceptor;
import es.caib.helium.back.interceptor.PersonaInterceptor;
import es.caib.helium.back.interceptor.PropertiesInterceptor;
import es.caib.helium.back.mvc.ArxiuView;
import es.caib.helium.back.mvc.SerialitzarView;

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
	@Autowired
	private PersonaInterceptor personaInterceptor;
	@Autowired
	private ModalInterceptor modalInterceptor;
	@Autowired
	private NodecoInterceptor nodecoInterceptor;
	@Autowired
	private EntornInterceptor entornInterceptor;
	@Autowired
	private AjaxInterceptor ajaxInterceptor;
	@Autowired
	private PropertiesInterceptor globalPropertiesInterceptor;

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer
			.favorParameter(false)
			.ignoreAcceptHeader(true)
			.defaultContentType(MediaType.APPLICATION_JSON)
			.useRegisteredExtensionsOnly(false); //.useJaf(true)
	}

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
		registry.addInterceptor(personaInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(nodecoInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(entornInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(ajaxInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(globalPropertiesInterceptor).excludePathPatterns(excludedPathPatterns);

		excludedPathPatterns = new String [] {
				"/js/**",
				"/css/**",
				"/fonts/**",
				"/img/**",
				"/images/**",
				"/extensions/**",
				"/webjars/**",
				"/**/selection/**",
				"/public/**"
		};
		registry.addInterceptor(modalInterceptor).excludePathPatterns(excludedPathPatterns);
	}

	@Bean
	public MappingJackson2HttpMessageConverter jsonConverter() {
		ObjectMapper mapper = new ObjectMapper();
		//mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(mapper);
		return converter;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(jsonConverter());
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

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(1000000);
		return multipartResolver;
	}

	/** Resolver per poder retornar noms de vistes per nom, per exemple arxiuView. */
	@Bean
    public BeanNameViewResolver beanNameViewResolver() {
        BeanNameViewResolver resolver = new BeanNameViewResolver();
        resolver.setOrder(0);
        return resolver;
    }

	@Bean(name ="serialitzarView")
	public SerialitzarView serialitzarView() {
		return new SerialitzarView();
	}

	@Bean(name ="arxiuView")
	public ArxiuView arxiuView() {
		return new ArxiuView();
	}

	/**
	 * Resolver per JSPs
	 */
	@Bean
	public InternalResourceViewResolver jspViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/jsp/");
		resolver.setSuffix(".jsp");
		resolver.setViewClass(JstlView.class);
		resolver.setOrder(1);
		return resolver;
	}

}
