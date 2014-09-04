package net.conselldemallorca.helium.test.configuracio;

import java.util.Calendar;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.fail;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import net.conselldemallorca.helium.test.util.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfiguracioFestius extends BaseTest {

								//DFG.3 - Rols
									//DFG.3.1 - Canviar Any
									//DFG.3.2 - Marcar i desmarcar festius
	
	String entorn 		 = carregarPropietat("config.aplicacio.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	 = carregarPropietat("config.festius.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin   = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String urlBaseEntorn = carregarPropietat("config.url.festiu.service", "No s´ha pogut obtenir la URL base del servei de festius.");
	Calendar dataAvull   = Calendar.getInstance(); 
	
	@Test
	public void a1_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
	}
	
	@Test
	public void b1_canviar_any() {
		
		carregarUrlConfiguracio();
		accedirConfiguracioFestius();
		
		screenshotHelper.saveScreenshot("configuracio/festius/b1_1_llista_festius_any_actual.png");
		
		//Seleccionar any anterior
		String anyAnterior = Integer.toString(dataAvull.get(Calendar.YEAR)-1);
		for (WebElement option : driver.findElement(By.xpath("//*[@id='content']/h3/form/select")).findElements(By.tagName("option"))) {
			if (anyAnterior.equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}

		//Funcio que marca un dia del calendari (donada la id) y espera un maxim de 7s fins que es carregui el nou calendari via ajax.
		//Esperam fins a trobar la cel.la corresponent a dia 1 de gener de l´any escollit (maxim 10s.)
		Wait<WebDriver> wait = new WebDriverWait(driver, 10);
        WebElement element = wait.until(visibilityOfElementLocated(By.id("dia_01/01/"+anyAnterior)));
        
        if (element==null) {
        	fail("La pagina del calendari corresponent a l'id dia_01/01/"+anyAnterior+" ha tardat massa a carregar.");
        }else{
        	screenshotHelper.saveScreenshot("configuracio/festius/b1_2_llista_festius_any_anterior.png");
        }
	}
	
	@Test
	public void c1_provar_festius() {
		
		carregarUrlConfiguracio();
		
		accedirConfiguracioFestius();
		
		screenshotHelper.saveScreenshot("configuracio/festius/c1_1_llista_festius.png");
		
		String anyActual = Integer.toString(dataAvull.get(Calendar.YEAR));

		comprovaDiaMarcat("01/01/"+anyActual);
		
		screenshotHelper.saveScreenshot("configuracio/festius/c1_2_1gener_canviat.png");
		
		comprovaDiaMarcat("10/06/"+anyActual);
		
		screenshotHelper.saveScreenshot("configuracio/festius/c1_3_10juny_canviat.png");
		
		comprovaDiaMarcat("31/12/"+anyActual);
		
		screenshotHelper.saveScreenshot("configuracio/festius/c1_3_31dese_canviat.png");
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarEntorn(entorn);
	}
	
	
	
	// - - - - - - - - - - - - - - - - - - - -
	// F U N C I O N S   P R I V A D E S
	// - - - - - - - - - - - - - - - - - - - -
	
    private ExpectedCondition<WebElement> visibilityOfElementLocated(final By by) {
    	
        return new ExpectedCondition<WebElement>() {
        	
          public WebElement apply(WebDriver driver) {
        	  
        	  try {
        		  
		    	WebElement element = driver.findElement(by);
		    	
		        if (element!=null && element.isDisplayed()) {
		        	return element;
		        }else{
		        	return null;
		        }
        	  }catch (Exception ex) {
        		  return null;        		  
        	  }
          }
          
        };

      }
    
    private boolean esFestiu(String data) {
    	
    	if (existeixElement("//*[@id='dia_"+data+"']")){
    		WebElement elm = driver.findElement(By.id("dia_"+data));
    		if (elm.getAttribute("class").indexOf("festiu")!=-1) {
    			return true;
    		}else{
    			return false;
    		}
    	}else{
    		return false;
    	}
    }
    
    //Marca un dia del calendari, el clica i es queda esperant fins que la pagina s´ha recarregat (maxim de 10s)
    //Hem de indicar també si esperam que el dia es quedi com a festiu o no
    private void marcarDiaCalendari(String idElem, boolean festiu) {
    	
    	String xPathFinal = "";
    	if (festiu) {
    		xPathFinal = "//*[@id='"+idElem+"'][contains(@class, 'festiu')]";
    	}else{
    		xPathFinal = "//*[@id='"+idElem+"'][not[contains(@class, 'festiu')]]";
    	}

    	//Clicam l´element y esperam un max de 10s si la pagina es recarrega amb l´element indicat marcat com a festiu o no (segons el booleà passat)
    	driver.findElement(By.id(idElem)).click();
    	
		//Esperam fins a trobar la cel.la corresponent a dia 1 de gener de l´any escollit (maxim 10s.)
		Wait<WebDriver> wait = new WebDriverWait(driver, 10);
        try {
        	wait.until(visibilityOfElementLocated(By.xpath(xPathFinal)));
        }catch (Exception ex) {}
    }
    
	//Miram un dia predefinit, si es festiu, el marcam y comprovam que ja no es festiu.
	//Si no es festiu, el marcam y comprovam que s´ha quedat com a festiu.
    private void comprovaDiaMarcat(String data) {		

    	if (esFestiu(data)) {

    		marcarDiaCalendari("dia_"+data, false);
    		
			if (esFestiu(data)) {
				fail("El camp corresponent al dia "+data+" continua com a festiu despres de haver-lo marcat.");
			}
		}else{
			
			marcarDiaCalendari("dia_"+data, true);
			
			if (!esFestiu(data)) {
				fail("El camp corresponent al dia "+data+" continua com a no festiu despres de haver-lo marcat.");
			}
		}
    }
    
//    private int realitzaCridaFestiuService(String fecha, boolean festiu) {
//    	
//    	try {
//    	
//    	URI linkToCheck;
//    		
//    	if (festiu) {
//    		linkToCheck = new URI(urlBaseEntorn+"festiuDwrService.crear.dwr");
//    	}else{
//    		linkToCheck = new URI(urlBaseEntorn+"festiuDwrService.esborrar.dwr");
//    	}
//    	
//        HttpClient client = new DefaultHttpClient();
//        BasicHttpContext localContext = new BasicHttpContext();
//
//        RequestMethod httpRequestMethod = RequestMethod.POST;
//              
//        HttpRequestBase requestMethod = httpRequestMethod.getRequestMethod();
//        requestMethod.setURI(linkToCheck);
//        HttpParams httpRequestParameters = requestMethod.getParams();
//        httpRequestParameters.setParameter("param0", fecha);
//        requestMethod.setParams(httpRequestParameters);
//        
//        requestMethod.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(usuariAdmin, passUsuAdmin), "UTF-8", false));
// 
//        HttpResponse response = client.execute(requestMethod, localContext);
// 
//        return response.getStatusLine().getStatusCode();
//        
//    	}catch (Exception ex) {
//    		ex.printStackTrace();
//    		return -1;
//    	}
//    }
}
