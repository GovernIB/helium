package net.conselldemallorca.helium.test.util;
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
			FileUtils.copyFile(screenshot, new File("screenshots/" + prefix + "/" + screenshotFileName));
		} catch (IOException e) {
			// Error IO
		}
	}
}