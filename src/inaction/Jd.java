package inaction;

import java.util.List;



import pipeline.ConsolePipeline;
import pipeline.FilePipeline;
import processor.PageProcessor;
import utils.UtilsConstants;
import clawer.Page;
import clawer.Site;
import clawer.Spider;
import clawer.Task;
import downloader.SeleniumDownloader;

public class Jd implements PageProcessor, Task  {

    private Site site;
    
    @Override
    public void process(Page page) {
    	//如果不是评论页，取url。如果是评论页，取数据
    	if (! page.getUrl().regex("http://item\\.jd\\.com/\\d+\\.html#comment").match()
    			&& !page.getUrl().regex("http://item\\.jd\\.com/\\d+\\.html#comment").match()) {
//    	匹配http://list.jd.com/1318-1466-1697.html或者http://www.jd.com/contact/service.html这样的url
//    	List<String> link = page.getHtml().links().regex("http://\\w+\\.jd\\.com/.*\\.html.*").all();
    	List<String> link = null;          

    	link = page.getHtml().links().regex("http://item\\.jd\\.com/\\d+\\.html").all();
    	if (link != null) {
    		for (int i = 0; i < link.size(); ++i) {
    			link.set(i, link.get(i)+"#comment");
    		}
		}
    	page.addTargetRequests(link);

    	link = page.getHtml().links().regex("http://channel\\.jd\\.com/.*\\.html").all();
    	page.addTargetRequests(link);
    	link = page.getHtml().links().regex("http://list\\.jd\\.com/list\\.html\\?cat=.*").all();
    	page.addTargetRequests(link);
    	
/*    	link = page.getHtml().links().regex("http://item\\.jd\\.com/\\d+\\.html#comment").all();
    	page.addTargetRequests(link);
    	
    	for (String string : link) {
    			System.out.println(string);
    	}*/
			page.setSkip(true);
		} else if (page.getUrl().regex("http://item\\.jd\\.com/\\d+\\.html#comment").match()) {
			page.putField("商品类别", 
					page.getHtml().xpath("//div[@class='w']/div[@class='breadcrumb']/span[1]/a[2]/text()").toString());
			page.putField("商品名称", 
					page.getHtml().xpath("//div[@id='name']/h1/text()").toString());
			if (page.getResultItems().get("商品名称")==null){
				page.setSkip(true);
			}
			page.putField("商品价格", 
					page.getHtml().xpath("//*[@id='summary-price']/div[2]/strong/text()").toString());
			String category = page.getHtml().xpath("//*[@id='book']/div[4]/div[1]/strong/a/text()").toString();
			if (category != null && category.equals("图书")) {
				page.putField("商品评论", 
						page.getHtml().xpath("//*[@id='comments-list']/div[1]/ul/li[1]/a/em/text()").toString());
				page.putField("商品好评", 
						page.getHtml().xpath("//*[@id='comments-list']/div[1]/ul/li[2]/a/em/text()").toString());
				page.putField("商品中评", 
						page.getHtml().xpath("//*[@id='comments-list']/div[1]/ul/li[3]/a/em/text()").toString());
				page.putField("商品差评", 
						page.getHtml().xpath("//*[@id='comments-list']/div[1]/ul/li[4]/a/em/text()").toString());
			} else{
				page.putField("商品评论", 
						page.getHtml().xpath("//*[@id='comments-list']/div[1]/div[1]/ul/li[1]/a/em/text()").toString());
				page.putField("商品好评", 
						page.getHtml().xpath("//*[@id='comments-list']/div[1]/div[1]/ul/li[2]/a/em/text()").toString());
				page.putField("商品中评", 
						page.getHtml().xpath("//*[@id='comments-list']/div[1]/div[1]/ul/li[3]/a/em/text()").toString());
				page.putField("商品差评", 
						page.getHtml().xpath("//*[@id='comments-list']/div[1]/div[1]/ul/li[4]/a/em/text()").toString());
			}
		/*    	page.putField("商品评论数", 
	    			page.getHtml().xpath("//*[@id="comments-list"]/div[1]/div[1]/ul/li[1]/a/em").toString());
			
/*			page.putField("已售数量", 
					page.getHtml().xpath("").toString());
*/			/*			page.putField("商品型号", 
					page.getHtml().xpath("").toString());*/
		}
    	

    }
    
    public static void main(String[] args) {

//        	String url = "http://www.jd.com/allSort.aspx";
        	String url = "http://list.jd.com/list.html?cat=9987,653,655";
//        	String url1 = "http://item.jd.com/11619844.html";     	
//        	String url2 = "http://item.jd.com/996959.html#comment";
//        	String url3 = "http://item.jd.com/757064.html#comment";
//        	String url4 = "http://item.jd.com/1025091224.html#comment";
//        	String url5 = "http://item.jd.com/1428705049.html#comment";

        	Spider spider = new Spider(new Jd());
        	spider
//        		.addUrl(url1)
//        		.addUrl(url2)  
//        		.addUrl(url3)  
//        		.addUrl(url4)
//        		.addUrl(url5)
        		.addUrl(url)
//        		.addPipeline(new DBPipeline())
        		.addPipeline(new ConsolePipeline())
        		.addPipeline(new FilePipeline("F:\\Clawer\\京东.txt"))
//        		.setDownloader(new HttpClientDownloader())
        		.setDownloader(new SeleniumDownloader(UtilsConstants.CHROMEDRIVER_PATH))
//        		.thread(10)
        		.run();
        	System.out.println(111);
    }
    
    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setCharset("gbk")
            		.setSleepTime(100);
        }

        return site;
    }

	@Override
	public String getUUID() {
		return null;
	}

	@Override
	public int getTaskId() {
		return 0;
	}
}
