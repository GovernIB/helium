package net.consellemallorca.helium.util;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;

import net.conselldemallorca.helium.helper.ScreenshotHelper;

/** Classe @Rule per capturar automàticament la pantalla en cas d'error.
 * 
 * {@link http://memorynotfound.com/capturing-screenshots-failed-selenium-test-java/}
 */
public class ScreenShotOnFailure implements MethodRule {

	/** Objecte per facilitar les captures. */
    private ScreenshotHelper screenshotHelper;
    /** Referència al driver. Si es fixa la propietat es cridarà a quit després d'avaluar. */
	private WebDriver driver;


    /** Mètode que aplica la regla d'avaluar el resultat del test. */
    public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, final Object o) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    statement.evaluate();
                	screenshotHelper.clearScreenshot(frameworkMethod.getMethod().getDeclaringClass().getSimpleName(), frameworkMethod.getName());
                } catch (Throwable t) {
                    // exception will be thrown only when a test fails.
                	screenshotHelper.saveScreenshot(frameworkMethod.getMethod().getDeclaringClass().getSimpleName(), frameworkMethod.getName());
                    // rethrow to allow the failure to be reported by JUnit
                    throw t;
                }
            }
        };
    }

	public ScreenshotHelper getScreenshotHelper() {
		return screenshotHelper;
	}
	public void setScreenshotHelper(ScreenshotHelper sreenshotHelper) {
		this.screenshotHelper = sreenshotHelper;
	}
	public WebDriver getDriver() {
		return driver;
	}
	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}
}
