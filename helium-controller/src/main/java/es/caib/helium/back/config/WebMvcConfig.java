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
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;

/**
 * Configuració de Spring web MVC.
 * 
 * @author Limit Tecnologies
 */
@RequiredArgsConstructor
@Configuration
//@EnableWebMvc
//@ComponentScan
public class WebMvcConfig implements WebMvcConfigurer {
	
//	@Override 
//	public void configureViewResolvers(ViewResolverRegistry registry) {
//        registry.jsp("/WEB-INF/jsp/", ".jsp");
//	}

	private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("ca");

	private final AjaxInterceptor ajaxInterceptor;
	private final EntornInterceptor entornInterceptor;
	private final GlobalPropertiesInterceptor globalPropertiesInterceptor;
	private final IdiomaInterceptor idiomaInterceptor;
	private final ModalInterceptor modalInterceptor;
	private final NodecoInterceptor nodecoInterceptor;
	private final PersonaInterceptor personaInterceptor;
	private final VersioInterceptor versioInterceptor;

	@Bean
	public LocaleResolver localeResolver() {
		var slr = new SessionLocaleResolver();
		slr.setDefaultLocale(DEFAULT_LOCALE);
		return slr;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		var lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

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

//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(localeChangeInterceptor());
//		registry.addInterceptor(aplicacioInterceptor);
//		registry.addInterceptor(canviRolInterceptor);
//		registry.addInterceptor(modalInterceptor);
//		registry.addInterceptor(nodecoInterceptor);
//		registry.addInterceptor(ajaxInterceptor);
//	}

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
