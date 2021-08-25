/**
 * 
 */
package es.caib.helium.back.config;

import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import es.caib.helium.back.interceptor.AjaxInterceptor;
import es.caib.helium.back.interceptor.EntornInterceptor;
import es.caib.helium.back.interceptor.GlobalPropertiesInterceptor;
import es.caib.helium.back.interceptor.IdiomaInterceptor;
import es.caib.helium.back.interceptor.ModalInterceptor;
import es.caib.helium.back.interceptor.NodecoInterceptor;
import es.caib.helium.back.interceptor.PersonaInterceptor;
import es.caib.helium.back.interceptor.VersioInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolverSupport;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Configuració de Spring web MVC.
 * 
 * @author Limit Tecnologies
 */
@DependsOn("entornService") // Assegura que s'han creat els EJB a EjbClientConfig o del ServiceImpl
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("ca");

	@Autowired
	private AjaxInterceptor ajaxInterceptor;
	@Autowired
	private EntornInterceptor entornInterceptor;
	@Autowired
	private GlobalPropertiesInterceptor globalPropertiesInterceptor;
	@Autowired
	private IdiomaInterceptor idiomaInterceptor;
	@Autowired
	private ModalInterceptor modalInterceptor;
	@Autowired
	private NodecoInterceptor nodecoInterceptor;
	@Autowired
	private PersonaInterceptor personaInterceptor;
	@Autowired
	private VersioInterceptor versioInterceptor;

	@Autowired
	BeanNameViewResolver beanNameViewResolver;

	@Bean
	public LocaleResolver localeResolver() {
		var slr = new SessionLocaleResolver();
		slr.setDefaultLocale(DEFAULT_LOCALE);
		return slr;
	}

	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/jsp/");
		resolver.setSuffix(".jsp");
		resolver.setOrder(3);
		return resolver;
	}

	@Bean
	public ViewResolver beanViewResolver() {
		BeanNameViewResolver beanResolver = new BeanNameViewResolver();
		beanResolver.setOrder(1);
		return beanResolver;
	}

//	@Bean
//	public MultipartResolver multipartResolver() {
//		CommonsMultipartResolver multipartResolver
//				= new CommonsMultipartResolver();
//		multipartResolver.setMaxUploadSize(5242880);
//		return multipartResolver;
//	}

//	// Vistes
//	@Bean("arxiuView")
//	public View arxiuView() {
//		return new ArxiuView();
//	}


	/*@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		messageSource.setDefaultLocale(DEFAULT_LOCALE);
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}*/

	@Bean
	public FilterRegistrationBean<SiteMeshFilter> sitemeshFilter() {
		var registrationBean = new FilterRegistrationBean<SiteMeshFilter>();
		registrationBean.setFilter(new SiteMeshFilter());
		registrationBean.addUrlPatterns("*");
		return registrationBean;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		List<String> excludePatterns = new ArrayList<String>();
		excludePatterns.add("/js/**");
		excludePatterns.add("/css/**");
		excludePatterns.add("/fonts/**");
		excludePatterns.add("/img/**");
		excludePatterns.add("/extensions/**");
		excludePatterns.add("/webjars/**");
		excludePatterns.add("/**/datatable/**");
		excludePatterns.add("/**/selection/**");
		
		registry.addInterceptor(personaInterceptor).excludePathPatterns(excludePatterns);
		registry.addInterceptor(entornInterceptor).excludePathPatterns(excludePatterns);
		registry.addInterceptor(globalPropertiesInterceptor).excludePathPatterns(excludePatterns);
		registry.addInterceptor(versioInterceptor).excludePathPatterns(excludePatterns);
		registry.addInterceptor(nodecoInterceptor).excludePathPatterns(excludePatterns);
		registry.addInterceptor(modalInterceptor).excludePathPatterns(excludePatterns);
		registry.addInterceptor(ajaxInterceptor).excludePathPatterns(excludePatterns);
		registry.addInterceptor(idiomaInterceptor).excludePathPatterns(excludePatterns);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		var resolver = new CustomPageableHandlerMethodArgumentResolver();
		resolver.setFallbackPageable(Pageable.unpaged());
		resolvers.add(resolver);
		WebMvcConfigurer.super.addArgumentResolvers(resolvers);
	}
	
	public static class CustomPageableHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolverSupport implements PageableArgumentResolver {
		private static final SortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new SortHandlerMethodArgumentResolver();
		private SortArgumentResolver sortResolver;
		public CustomPageableHandlerMethodArgumentResolver() {
			this((SortArgumentResolver) null);
		}
		public CustomPageableHandlerMethodArgumentResolver(SortHandlerMethodArgumentResolver sortResolver) {
			this((SortArgumentResolver) sortResolver);
		}
		public CustomPageableHandlerMethodArgumentResolver(@Nullable SortArgumentResolver sortResolver) {
			this.sortResolver = sortResolver == null ? DEFAULT_SORT_RESOLVER : sortResolver;
		}
		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return Pageable.class.equals(parameter.getParameterType());
		}
		@Override
		public Pageable resolveArgument(
				MethodParameter methodParameter,
				@Nullable ModelAndViewContainer mavContainer,
				NativeWebRequest webRequest,
				@Nullable WebDataBinderFactory binderFactory) {
			String page = webRequest.getParameter(getParameterNameToUse(getPageParameterName(), methodParameter));
			String pageSize = webRequest.getParameter(getParameterNameToUse(getSizeParameterName(), methodParameter));
			Sort sort = sortResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
			Pageable pageable = getPageable(methodParameter, page, pageSize);
			if (pageable.isPaged() && sort.isSorted()) {
				return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
			}
			return pageable;
		}
	}
}
