package downloader;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import selector.PlainText;
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
public class SeleniumDownloader implements Downloader, Closeable {

    private volatile WebDriverPool webDriverPool;

    private Logger logger = Logger.getLogger(getClass());

    private int sleepTime = 0;

    private int poolSize = 1;
    
    /**
     * 新建
     * @param chromeDriverPath
     */
    public SeleniumDownloader(String chromeDriverPath) {
        System.getProperties().setProperty("webdriver.chrome.driver", chromeDriverPath);
    }

    /**
     * set sleep time to wait until load success
     *
     * @param sleepTime
     * @return this
     */
    public SeleniumDownloader setSleepTime(int sleepTime) {
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

//        ((JavascriptExecutor) webDriver).executeScript("scroll(0," + (i * 500) + ");");
     	
        //最长等待30秒到页面加载完成
        webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

/*        if (request.getUrl().matches("http://renren\\.com")) {
        	webDriver.findElement(By.id("email")).sendKeys("zichen0322@yahoo.cn");
        	webDriver.findElement(By.id("password")).sendKeys("aoe123456");
        	webDriver.findElement(By.id("login")).click();
        	String url = webDriver.getCurrentUrl();
            webDriver.get(url);

			webDriver.findElement(By.xpath("//*[@id='nxSlidebar']/div[1]/div/ul/li[3]/a/span")).click();
            try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			webDriver.navigate().back();
			
		}*/
        

/*        if (request.getUrl().matches("https://www.facebook\\.com/?")) {
        	webDriver.findElement(By.id("email")).sendKeys("email_wl@163.com");
        	webDriver.findElement(By.id("pass")).sendKeys("quantianhou");
        	webDriver.findElement(By.id("loginbutton")).click();

            webDriverPool.returnToPool(webDriver);
        	return null;
		} 
        if (request.getUrl().contains("scontent")) {
        	System.out.println("photo");
        	System.out.println(webDriver.getPageSource());
        }*/
        

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
        page.setRawText(content);
//        page.setHtml(new Html(UrlUtils.fixAllRelativeHrefs(content, request.getUrl())));
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
