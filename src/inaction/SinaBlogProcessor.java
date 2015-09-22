package inaction;

import processor.PageProcessor;
import clawer.Page;
import clawer.Site;
import clawer.Spider;
import downloader.HttpClientDownloader;


public class SinaBlogProcessor implements PageProcessor {

 //   public static final String URL_LIST = "http://blog\\.sina\\.com\\.cn/s/articlelist_1487828712_0_\\d+\\.html";
    public static final String URL_LIST = "http://list\\.jd\\.com/list\\.html?cat=\\w+";

    public static final String URL_POST = "http://item\\.jd\\.com/\\w+\\.html";

    private Site site = Site
            .me()
        //  .setDomain("blog.sina.com.cn")
            .setRetryTimes(3)
            .setSleepTime(30)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
    	
    	System.out.println(page.getHtml());
      
            page.addTargetRequests(page.getHtml().xpath("//div[@class=\"articleList\"]").links().regex(URL_POST).all());
            page.addTargetRequests(page.getHtml().links().regex(URL_LIST).all());
           
            page.putField("title", page.getHtml().xpath("//div[@class='articalTitle']/h2"));
            page.putField("content", page.getHtml().xpath("//div[@id='articlebody']//div[@class='articalContent']"));
            page.putField("date",
                    page.getHtml().xpath("//div[@id='articlebody']//span[@class='time SG_txtc']").regex("\\((.*)\\)"));
   
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new SinaBlogProcessor())
//        	.setDownloader(new HttpClientDownloader_old())
        	.addUrl("http://blog.sina.com.cn/s/articlelist_1487828712_0_1.html")
            .run();
    }
}
