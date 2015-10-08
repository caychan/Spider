package downloader;

import java.io.Closeable;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
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
    

    public SeleniumDownloader_jd(String chromeDriverPath) { 	
        System.getProperties().setProperty("webdriver.chrome.driver", chromeDriverPath);
    }

    /**
     * set sleep time to wait until load success
     */
    public SeleniumDownloader_jd setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }
    

    @Override
    public Page download(Request request, Task task) {
    	checkInit();
        final WebDriver webDriver;
        try {
            webDriver = webDriverPool.get();
        } catch (InterruptedException e) {
            logger.warn("interrupted", e);
            return null;
        }
        logger.info("downloading page " + request.getUrl());
        webDriver.get(request.getUrl());
        
//        webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

/*        WebElement element = null;
        By selector = By.xpath("//*[@id='comments-list']/div[1]/div[1]/ul");
        element = webDriver.findElement(selector);
        element.click();
*/
//    	((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView();", element);

    	if (request.getUrl().matches("http://item\\.jd\\.com/\\d+\\.html#comment")) {
    		WebDriverWait wait = new WebDriverWait(webDriver, 15);
			WebElement element = wait.until(new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver driver) {
					By selector = By.xpath("//*[@id='comments-list']/div[1]/div[1]/ul/li[1]/a/em");
					WebElement em = null;
					int i = 0;
					try {
						do {
							if ((em = webElementExist(driver, selector)) != null) {
								((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();",em);
								Thread.sleep(1000);
								String str = em.getText();
								System.out.println(str);
								Pattern pattern = Pattern.compile("\\d+");
								Matcher m = pattern.matcher(str);
								if (m.find()) {
									break;
								}
							}
							if (i == 5) {
								driver.navigate().refresh();
							}
						} while (++i < 10);
					} catch (Exception e) {
						e.printStackTrace();
						return em;
					}
					return em;
				}
			});

			if (element == null) {
		        webDriverPool.returnToPool(webDriver);
    			return null;
    		}
		}
        
/*     	//有时出现找不到的情况，抛出no such element错误导致程序中止
    	int retries = 0;
    	boolean exist = false;
		while (retries++ < 5) {
			exist = isWebElementExist(webDriver, selector);
			if (exist) {
				break;
			}
			webDriver.navigate().refresh();
		}
    	
    	if (exist) {
    		element = webDriver.findElement(selector);
        	element.click();
        	WebDriverWait wait = new WebDriverWait(webDriver, 10);
        	wait.until(new ExpectedCondition<WebElement>() {
        		@Override
        		public WebElement apply(WebDriver d) {
                	By selector = By.id("type_1");
    				boolean exists = isWebElementExist(d, selector);
                	if (exists) {
                		return d.findElement(By.id("type_1"));
					} else {
						return null;
					}
        		}
        	});
		} 
        */
        
      
        try {
        	if (this.sleepTime > 0) {
        		Thread.sleep(sleepTime);
			}
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
/*        WebDriver.Options manage = webDriver.manage();
        Site site = task.getSite();
        if (site.getCookies() != null) {
            for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
                Cookie cookie = new Cookie(cookieEntry.getKey(), cookieEntry.getValue());
                manage.addCookie(cookie);
            }
        }*/
        
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
    
	private WebElement webElementExist(WebDriver driver, By selector) {
		try {
			if (driver.findElement(selector) != null) {
				return driver.findElement(selector);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}
