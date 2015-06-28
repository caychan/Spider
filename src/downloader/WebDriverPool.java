package downloader;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author code4crafter@gmail.com <br>
 * Date: 13-7-26 <br>
 * Time: 下午1:41 <br>
 */
class WebDriverPool {
    private Logger logger = Logger.getLogger(getClass());

    private final static int DEFAULT_CAPACITY = 5;

    private final int capacity;

    private final static int STAT_RUNNING = 1;

    private final static int STAT_CLODED = 2;

    private AtomicInteger stat = new AtomicInteger(STAT_RUNNING);

    /**
     * store webDrivers created
     */
    private List<WebDriver> webDriverList = Collections.synchronizedList(new ArrayList<WebDriver>());

    /**
     * store webDrivers available
     */
    private BlockingDeque<WebDriver> innerQueue = new LinkedBlockingDeque<WebDriver>();

    public WebDriverPool(int capacity) {
        this.capacity = capacity;
    }

    public WebDriverPool() {
        this(DEFAULT_CAPACITY);
    }

    public WebDriver get() throws InterruptedException {
        checkRunning();
        WebDriver poll = innerQueue.poll();
        if (poll != null) {
            return poll;
        }
        if (webDriverList.size() < capacity) {
            synchronized (webDriverList) {
                if (webDriverList.size() < capacity) {
/*                	Map<String, Object> contentSettings = new HashMap<String, Object>();
                	contentSettings.put("images", 2);

                	Map<String, Object> preferences = new HashMap<String, Object>();
                	preferences.put("profile.default_content_settings", contentSettings);

                	DesiredCapabilities caps = DesiredCapabilities.chrome();
                	caps.setCapability("chrome.prefs", preferences);
//                	WebDriver driver = new ChromeDriver(caps);*/
                	
/*                	ChromeOptions options = new ChromeOptions();
                	options.
                	    options.prefs = new Dictionary<string, object> {
                	        { "profile.default_content_settings", new Dictionary<string, object>() { "images", 2 } }
                	    };
                	    var driver = new ChromeDriver(options);*/

/*                	
                	Map<String, Object> contentSettings = new HashMap<String, Object>();
                	contentSettings.put("images", 2);
                	HashMap<String, Object> preferences = new HashMap<String, Object>();
                	preferences.put("profile.default_content_settings", contentSettings);
                	    ChromeOptions options = new ChromeOptions();
                	    options.addArguments("profile.default_content_settings");
                	    options.addArguments(preferences);
//                	    ((Object) options).AddUserProfilePreference("profile.default_content_settings", "{\"images\":2");
*/  
                	
                	// 屏蔽图片               	
  					ChromeOptions options = new ChromeOptions();
					//use the block image extension to prevent images from downloading.
					options.addExtensions(new File("F:\\程序\\block_image_1_1.crx"));
                    ChromeDriver e = new ChromeDriver(options);
               		
                	
//                    ChromeDriver e = new ChromeDriver();
                    innerQueue.add(e);
                    webDriverList.add(e);
                }
            }

        }
        return innerQueue.take();
    }

    public void returnToPool(WebDriver webDriver) {
        checkRunning();
        innerQueue.add(webDriver);
    }

    protected void checkRunning() {
        if (!stat.compareAndSet(STAT_RUNNING, STAT_RUNNING)) {
            throw new IllegalStateException("Already closed!");
        }
    }

    public void closeAll() {
        boolean b = stat.compareAndSet(STAT_RUNNING, STAT_CLODED);
        if (!b) {
            throw new IllegalStateException("Already closed!");
        }
        for (WebDriver webDriver : webDriverList) {
            logger.info("Quit webDriver" + webDriver);
            webDriver.quit();
        }
    }

}

