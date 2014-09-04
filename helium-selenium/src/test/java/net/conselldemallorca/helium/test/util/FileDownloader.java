package net.conselldemallorca.helium.test.util;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@SuppressWarnings("deprecation")
public class FileDownloader {
	private static final Logger LOG = Logger.getLogger(FileDownloader.class);
    private WebDriver driver;
    private String localDownloadPath = new File("").getAbsolutePath(); //System.getProperty("java.io.tmpdir");
    private boolean followRedirects = true;
    private boolean mimicWebDriverCookieState = true;
    private int httpStatusOfLastDownloadAttempt = 0;
 
    public FileDownloader(WebDriver driverObject) {
        this.driver = driverObject;
    }
 
    /**
     * Specify if the FileDownloader class should follow redirects when trying to download a file
     *
     * @param value
     */
    public void followRedirectsWhenDownloading(boolean value) {
        this.followRedirects = value;
    }
 
    /**
     * Get the current location that files will be downloaded to.
     *
     * @return The filepath that the file will be downloaded to.
     */
    public String localDownloadPath() {
        return this.localDownloadPath;
    }
 
    /**
     * Set the path that files will be downloaded to.
     *
     * @param filePath The filepath that the file will be downloaded to.
     */
    public void localDownloadPath(String filePath) {
        this.localDownloadPath = filePath;
    }
 
    /**
     * Download the file specified in the href attribute of a WebElement
     *
     * @param element
     * @return
     * @throws Exception
     */
    public byte[] downloadFile(WebElement element) throws Exception {
        return downloader(element, "href");
    }
    
    public byte[] downloadFileFromForm(WebElement element) throws Exception {
        return downloader(element, "action");
    }
    
    /**
     * Download the file from post form
     *
     * @param element
     * @return
     * @throws Exception
     */
    public byte[] postDownloadFile(String formToDownloadLocation, List<NameValuePair> params) throws Exception {
    	return postdownloader(formToDownloadLocation, params);
    }
 
    /**
     * Download the image specified in the src attribute of a WebElement
     *
     * @param element
     * @return
     * @throws Exception
     */
    public byte[] downloadImage(WebElement element) throws Exception {
        return downloader(element, "src");
    }
 
    /**
     * Gets the HTTP status code of the last download file attempt
     *
     * @return
     */
    public int getHTTPStatusOfLastDownloadAttempt() {
        return this.httpStatusOfLastDownloadAttempt;
    }
 
    /**
     * Mimic the cookie state of WebDriver (Defaults to true)
     * This will enable you to access files that are only available when logged in.
     * If set to false the connection will be made as an anonymouse user
     *
     * @param value
     */
    public void mimicWebDriverCookieState(boolean value) {
        this.mimicWebDriverCookieState = value;
    }
 
    /**
     * Load in all the cookies WebDriver currently knows about so that we can mimic the browser cookie state
     *
     * @param seleniumCookieSet
     * @return
     */
    private BasicCookieStore mimicCookieState(Set<Cookie> seleniumCookieSet) {
        BasicCookieStore mimicWebDriverCookieStore = new BasicCookieStore();
        for (Cookie seleniumCookie : seleniumCookieSet) {
            BasicClientCookie duplicateCookie = new BasicClientCookie(seleniumCookie.getName(), seleniumCookie.getValue());
            duplicateCookie.setDomain(seleniumCookie.getDomain());
            duplicateCookie.setSecure(seleniumCookie.isSecure());
            duplicateCookie.setExpiryDate(seleniumCookie.getExpiry());
            duplicateCookie.setPath(seleniumCookie.getPath());
            mimicWebDriverCookieStore.addCookie(duplicateCookie);
        }
 
        return mimicWebDriverCookieStore;
    }
 
    /**
     * Perform the file/image download.
     *
     * @param element
     * @param attribute
     * @return
     * @throws IOException
     * @throws NullPointerException
     */
    @SuppressWarnings({ "resource" })
	private byte[] downloader(WebElement element, String attribute) throws IOException, NullPointerException, URISyntaxException {
        String fileToDownloadLocation = element.getAttribute(attribute);
        if (fileToDownloadLocation.trim().equals("")) throw new NullPointerException("The element you have specified does not link to anything!");
 
        URL fileToDownload = new URL(fileToDownloadLocation);
        File downloadedFile = new File(this.localDownloadPath + fileToDownload.getFile().replaceFirst("/|\\\\", ""));
        if (downloadedFile.canWrite() == false) downloadedFile.setWritable(true);
 
        HttpClient client = new DefaultHttpClient();
        BasicHttpContext localContext = new BasicHttpContext();
 
        LOG.info("Mimic WebDriver cookie state: " + this.mimicWebDriverCookieState);
        if (this.mimicWebDriverCookieState) {
            localContext.setAttribute(ClientContext.COOKIE_STORE, mimicCookieState(this.driver.manage().getCookies()));
        }
 
        HttpGet httpget = new HttpGet(fileToDownload.toURI());
        HttpParams httpRequestParameters = httpget.getParams();
        httpRequestParameters.setParameter(ClientPNames.HANDLE_REDIRECTS, this.followRedirects);
        httpget.setParams(httpRequestParameters);
 
        LOG.info("Sending GET request for: " + httpget.getURI());
        HttpResponse response = client.execute(httpget, localContext);
        this.httpStatusOfLastDownloadAttempt = response.getStatusLine().getStatusCode();
        LOG.info("HTTP GET request status: " + this.httpStatusOfLastDownloadAttempt);
        LOG.info("Downloading file: " + downloadedFile.getName());
//        FileUtils.copyInputStreamToFile(response.getEntity().getContent(), downloadedFile);
// 
//        String downloadedFileAbsolutePath = downloadedFile.getAbsolutePath();
//        LOG.info("File downloaded to '" + downloadedFileAbsolutePath + "'");
// 
//        return downloadedFileAbsolutePath;
        byte[] file = IOUtils.toByteArray(response.getEntity().getContent());
        response.getEntity().getContent().close();
        return file;
    }
    
    /**
     * Perform the file/image download.
     *
     * @param element
     * @param attribute
     * @return
     * @throws IOException
     * @throws NullPointerException
     */
    @SuppressWarnings({ "resource" })
	private byte[] postdownloader(String formToDownloadLocation, List<NameValuePair> params) throws IOException, NullPointerException, URISyntaxException {
        HttpClient client = new DefaultHttpClient();
        BasicHttpContext localContext = new BasicHttpContext();
 
        LOG.info("Mimic WebDriver cookie state: " + this.mimicWebDriverCookieState);
        if (this.mimicWebDriverCookieState) {
            localContext.setAttribute(ClientContext.COOKIE_STORE, mimicCookieState(this.driver.manage().getCookies()));
        }
 
        HttpPost httppost = new HttpPost(formToDownloadLocation);
        HttpParams httpRequestParameters = httppost.getParams();
        httpRequestParameters.setParameter(ClientPNames.HANDLE_REDIRECTS, this.followRedirects);
        httppost.setParams(httpRequestParameters);
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        
 
        LOG.info("Sending POST request for: " + httppost.getURI());
        HttpResponse response = client.execute(httppost, localContext);
        this.httpStatusOfLastDownloadAttempt = response.getStatusLine().getStatusCode();
        LOG.info("HTTP GET request status: " + this.httpStatusOfLastDownloadAttempt);
 
        byte[] file = IOUtils.toByteArray(response.getEntity().getContent());
        response.getEntity().getContent().close();
        return file;
    }
    
    @SuppressWarnings({ "resource" })
	public byte[] postDownloaderWithRedirects(String formToDownloadLocation, List<NameValuePair> params) throws IOException, NullPointerException, URISyntaxException {
    	
        HttpClient client = new DefaultHttpClient();
        BasicHttpContext localContext = new BasicHttpContext();
 
        LOG.info("Mimic WebDriver cookie state: " + this.mimicWebDriverCookieState);
        if (this.mimicWebDriverCookieState) {
            localContext.setAttribute(ClientContext.COOKIE_STORE, mimicCookieState(this.driver.manage().getCookies()));
        }
 
        HttpResponse response = realizaPeticion(null, client, localContext, formToDownloadLocation, params, null);
        
        boolean segueixRedirs = true;
        while (response.getStatusLine().getStatusCode()==302 && segueixRedirs) {
        	try {
        		String URLaux   = formToDownloadLocation.substring(0, formToDownloadLocation.lastIndexOf("/")+1);
        		String nuevaURL = URLaux + response.getHeaders("RedirectTo")[0].getValue();
        		response = realizaPeticion(response, client, localContext, nuevaURL, params, response.getParams());
        	}catch (Exception ex) {
        		segueixRedirs = false;
        	}
        }
        
        this.httpStatusOfLastDownloadAttempt = response.getStatusLine().getStatusCode();
        LOG.info("HTTP GET request status: " + this.httpStatusOfLastDownloadAttempt);
 
        byte[] file = IOUtils.toByteArray(response.getEntity().getContent());
        response.getEntity().getContent().close();
        return file;
    }
    
    private HttpResponse realizaPeticion (HttpResponse prevResponse , HttpClient client, BasicHttpContext localContext, String URL, List<NameValuePair> params, HttpParams httpParams) 
      throws IOException, NullPointerException, URISyntaxException {
    	//Terminamos la petición anterior en caso de que exista
    	if (prevResponse!=null)
    		prevResponse.getEntity().getContent().close();
    	
        HttpPost httppost = new HttpPost(URL);
        
        if (httpParams!=null)
        	httppost.setParams(httpParams);
        
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        LOG.info("Sending POST request for: " + httppost.getURI());
        return client.execute(httppost, localContext);
    }
}