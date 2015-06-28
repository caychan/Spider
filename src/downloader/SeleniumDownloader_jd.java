package downloader;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import selector.Html;
import selector.PlainText;
import utils.UrlUtils;
//import clawer.*;
import clawer.Page;
import clawer.Request;
import clawer.Site;
import clawer.Task;

/**
 * 使用Selenium调用浏览器进行渲染。目前仅支持chrome。<br>
 * 需要下载Selenium driver支持。<br>
 *
 */
public class SeleniumDownloader_jd implements Downloader, Closeable {

    private volatile WebDriverPool webDriverPool;

    private Logger logger = Logger.getLogger(getClass());

    private int sleepTime = 0;

    private int poolSize = 1;
    
    /**
     * 新建
     *
     * @param chromeDriverPath
     */
    public SeleniumDownloader_jd(String chromeDriverPath) {
/*    	Map<String, Object> contentSettings = new HashMap<String, Object>();
    	contentSettings.put("images", 2);

    	Map<String, Object> preferences = new HashMap<String, Object>();
    	preferences.put("profile.default_content_settings", contentSettings);

    	DesiredCapabilities caps = DesiredCapabilities.chrome();
    	caps.setCapability("chrome.prefs", preferences);
    	WebDriver driver = new ChromeDriver(caps);
    	
        System.setProperty("webdriver.chrome.driver", "files\\chromedriver.exe");
        File file = new File ("files\\youtube.crx");
        ChromeOptions options = new ChromeOptions();
        options.addExtensions(file);
        WebDriver driver = new ChromeDriver(options);
        driver.get("http://www.baidu.com/");
    	*/
    	
        System.getProperties().setProperty("webdriver.chrome.driver", chromeDriverPath);
    }

    /**
     * set sleep time to wait until load success
     *
     * @param sleepTime
     * @return this
     */
    public SeleniumDownloader_jd setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }
    

    @Override
    public Page download(Request request, Task task) {
    	checkInit();
        WebDriver webDriver;
        try {
            webDriver = webDriverPool.get();
        } catch (InterruptedException e) {
            logger.warn("interrupted", e);
            return null;
        }
        logger.info("downloading page " + request.getUrl());
        webDriver.get(request.getUrl());
      
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebDriver.Options manage = webDriver.manage();
        Site site = task.getSite();
        if (site.getCookies() != null) {
            for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
                Cookie cookie = new Cookie(cookieEntry.getKey(), cookieEntry.getValue());
                manage.addCookie(cookie);
            }
        }

        
        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        String content = webElement.getAttribute("outerHTML");
        Page page = new Page();
//        page.setRawText(content);
        page.setHtml(new Html(UrlUtils.fixAllRelativeHrefs(content, request.getUrl())));
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        webDriverPool.returnToPool(webDriver);
        return page;
    }

    private void checkInit() {
        if (webDriverPool == null) {
            synchronized (this){
                webDriverPool = new WebDriverPool(poolSize);
            }
        }
    }

    @Override
    public void setThread(int thread) {
        this.poolSize = thread;
    }

    @Override
    public void close() throws IOException {
        webDriverPool.closeAll();
    }
}
