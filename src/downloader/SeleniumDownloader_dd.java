package downloader;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.jetty.html.Break;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
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
 */
public class SeleniumDownloader_dd implements Downloader, Closeable {

    private volatile WebDriverPool webDriverPool;

    private static final int TIME_GLOBAL = 30;
    private static final int TIME_WAIT = 10;

    private Logger logger = Logger.getLogger(getClass());

    private int sleepTime = 0;

    private int poolSize = 1;
    
    
    /**
     * 新建
     *
     * @param chromeDriverPath
     */
    public SeleniumDownloader_dd(String chromeDriverPath) {
        System.getProperties().setProperty("webdriver.chrome.driver", chromeDriverPath);
    }

    /**
     * set sleep time to wait until load success
     *
     * @param sleepTime
     * @return this
     */
    public SeleniumDownloader_dd setSleepTime(int sleepTime) {
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
       
//        webDriver.get(request.getUrl());
        
       	webDriver.manage().timeouts().implicitlyWait(TIME_GLOBAL, TimeUnit.SECONDS);
        boolean flag = false;
        //在详情页才做点击处理
        if (request.getUrl().matches("http://product\\.dangdang\\.com/\\d+\\.html.*")){
        	int i = 0;
        	do {
        		webDriver.get(request.getUrl());
        		try {
    				((JavascriptExecutor) webDriver).executeScript("window.document.getElementById('comment_tab').click()"); 
    				WebDriverWait wait = new WebDriverWait(webDriver, TIME_WAIT);
    				wait.until(new ExpectedCondition<Boolean>() {
    					@Override
    					public Boolean apply(WebDriver d) {
    						By selector = By.xpath("//*[@id='type_1']");
    						return isWebElementExist(d, selector);
    					}
        			});
        			//如果可以执行到这里，说明程序没有Exception，那么跳出循环
        			flag = true;
        			break;
        		} catch (Exception e) {
        			//什么也不做
        		}
			} while (++i < 5);
				
		} else {
			webDriver.get(request.getUrl());
		}
        if (! flag) {
        	//如果前面出错了，则不处理该页，返回null
        	webDriverPool.returnToPool(webDriver);
			return null;
		}
        
        

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
    
	private boolean isWebElementExist(WebDriver driver, By selector) {
		try {
			driver.findElement(selector);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
