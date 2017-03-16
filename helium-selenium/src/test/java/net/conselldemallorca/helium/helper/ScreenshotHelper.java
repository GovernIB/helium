package net.conselldemallorca.helium.helper;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotHelper {

	private WebDriver driver;
	private String prefix;
	
	public ScreenshotHelper(WebDriver driver, String prefix) {
		super();
		this.driver = driver;
		this.prefix = prefix;
	}

	public void saveScreenshot(String screenshotFileName) {
		try {
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenshot, new File("screenshots/" + prefix + "/" + screenshotFileName + ".png"));
		} catch (IOException e) {
    		System.err.println("Error guardant la captura:" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void saveScreenshot(String className, String testName) {
		saveScreenshot(className + "/" + testName);
	}	
	
	/** Mètode per esborrar la captura per un test en concret.
	 * 
	 * @param screenshotDirName
	 */
	public void clearScreenshot(String className, String testName) {
		try {
			File dir = new File("screenshots/" + prefix + "/" + className + "/" + testName + ".png");
			if (dir.exists()) {
				dir.delete();
			}
		} catch (Exception e) {
    		System.err.println("Error esborrant la captura " + className + "/" + testName + ":" + e.getMessage());
			e.printStackTrace();
		}
	}	
}