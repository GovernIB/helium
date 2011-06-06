package net.conselldemallorca.helium.webapp.mvc.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.core.model.service.PersonaService;
import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.util.WebUtils;

public class IdiomaResolver implements LocaleResolver, Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String SESSION_IDIOMA_ACTUAL = "idiomaActual";
	public static final String SESSION_IDIOMES_DISPONIBLES = "idiomesDisponibles";

	private PersonaService personaService;
	
	private Locale idiomaDefecte;
	private List<Locale> idiomesDisponibles = new ArrayList<Locale>();

	public class ParellaCodiNom implements Serializable {
		private static final long serialVersionUID = 1L;
		private String codi;
		private String nom;
		public ParellaCodiNom() { }
		public ParellaCodiNom(String codi, String nom) {
			this.codi = codi;
			this.nom = nom;
		}
		public void setCodi(String codi) {
			this.codi = codi;
		}
		public String getCodi() {
			return codi;
		}
		public void setNom(String nom) {
			this.nom = nom;
		}
		public String getNom() {
			return nom;
		}
	}
	
	public IdiomaResolver() {
		setIdiomesDisponibles( GlobalProperties.getInstance().getProperty("app.idiomes.disponibles").split(",") );
		setIdiomaDefecte( GlobalProperties.getInstance().getProperty("app.idioma.defecte") );
	}

	public Locale resolveLocale(HttpServletRequest request) {
		Locale locale = null;
		LocaleEditor localeEditor = new LocaleEditor();
		
		ParellaCodiNom parella = (ParellaCodiNom)WebUtils.getSessionAttribute(request, SESSION_IDIOMA_ACTUAL);
		if ( parella != null ) {
			localeEditor.setAsText(parella.getCodi());
			locale = (Locale)localeEditor.getValue();
		}
		else {
			if ( personaService!=null && personaService.getUsuariPreferencies()!=null && personaService.getUsuariPreferencies().getIdioma()!=null ) {
				String idioma = personaService.getUsuariPreferencies().getIdioma();
				localeEditor.setAsText(idioma);
				locale = (Locale)localeEditor.getValue();
			}
			
			if ( locale==null || !esIdiomaDisponible(locale) )
				locale = determinarIdiomaDefecte(request);
			
			setLocale(request, locale);
		}

		return locale;
	}

	private Locale determinarIdiomaDefecte(HttpServletRequest request) {
		Locale idiomaDefecte = request.getLocale();
		
		if ( !esIdiomaDisponible(idiomaDefecte) )
			idiomaDefecte = getIdiomaDefecte();
		
		if ( idiomaDefecte==null )
			return Locale.getDefault();

		return idiomaDefecte;
	}
	
	private boolean esIdiomaDisponible(Locale locale) {
		for ( Locale disponible: getIdiomesDisponibles() )
			if ( locale.equals(disponible) )
				return true;
		
		return false;
	}

	private void setLocale(HttpServletRequest request, Locale idioma) {
		if ( esIdiomaDisponible(idioma) ) {
			ParellaCodiNom parella = new ParellaCodiNom( idioma.toString(), capitalize(idioma.getDisplayLanguage(idioma)) );
			WebUtils.setSessionAttribute(request, SESSION_IDIOMA_ACTUAL, parella);
			
			List<ParellaCodiNom> parelles = new ArrayList<ParellaCodiNom>();
			for ( Locale disponible: getIdiomesDisponibles() )
				parelles.add(new ParellaCodiNom( disponible.toString(), capitalize(disponible.getDisplayLanguage(idioma)) ));
			WebUtils.setSessionAttribute(request, SESSION_IDIOMES_DISPONIBLES, parelles);
		}
	}

	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		setLocale(request, locale);
	}

	@Autowired
	public void setPersonaService(PersonaService personaService) {
		this.personaService = personaService;
	}

	public void setIdiomaDefecte(String idiomaDefecte) {
		if ( idiomaDefecte!=null ) {
			LocaleEditor localeEditor = new LocaleEditor();
			localeEditor.setAsText(idiomaDefecte);
			this.idiomaDefecte = (Locale)localeEditor.getValue();
			
			if ( ! esIdiomaDisponible(this.idiomaDefecte) )
				this.idiomesDisponibles.add(0, this.idiomaDefecte);
		}
	}

	public Locale getIdiomaDefecte() {
		return idiomaDefecte;
	}

	public void setIdiomesDisponibles(String idiomesDisponibles[]) {
		if ( idiomesDisponibles!=null && idiomesDisponibles.length>0 ) {
			this.idiomesDisponibles = new ArrayList<Locale>();
			LocaleEditor localeEditor = new LocaleEditor();
			
			for ( String idioma: idiomesDisponibles ) {
				localeEditor.setAsText(idioma);
				this.idiomesDisponibles.add( (Locale)localeEditor.getValue() );
			}
			
			if ( this.idiomaDefecte==null )
				this.idiomaDefecte = this.idiomesDisponibles.get(0);
		}
	}

	public List<Locale> getIdiomesDisponibles() {
		return idiomesDisponibles;
	}
	
	private String capitalize(String cadena) {
		if ( cadena==null || cadena.length()==0 )
			return "";
		if ( cadena.length()==1 )
			return cadena.toUpperCase();
		return cadena.substring(0,1).toUpperCase()+cadena.substring(1).toLowerCase();
	}
}
