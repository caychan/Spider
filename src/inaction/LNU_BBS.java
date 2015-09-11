package inaction;

import java.util.List;

import pipeline.ConsolePipeline;
import pipeline.FilePipeline_LNU_BBS;
import processor.PageProcessor;
import utils.UrlUtils;
import utils.UtilsConstants;
import clawer.Page;
import clawer.Site;
import clawer.Spider;
import downloader.HttpClientDownloader;
import downloader.HttpClientDownloader_old;
import downloader.SeleniumDownloader;

public class LNU_BBS implements PageProcessor {

	private static String filePath = "F:\\Clawer\\LNU_BBS\\";
    private Site site;
    
    @Override
    public void process(Page page) {
    	System.out.println(page.getHtml());
    	
    	if(page.getUrl().regex("http://www\\.lnubbs\\.com/thread\\.php.*").match()){
    		//bbs版块
	    	String module = page.getHtml().xpath("//*[@id='topNav']/div[1]/a[3]/text()").toString();
			page.putField("module", module);

			//标题
	    	String title = page.getHtml().xpath("//*[@id='tie_wrapper']/div[1]/div[2]/b/text()").toString();
	    	page.putField("title", title);
	    	
	    	//发帖人
	    	String user = page.getHtml().xpath("//*[@id='tie_wrapper']/div[2]/div[1]/div/span/a/text()").toString();
	    	page.putField("user", user);

	    	//时间
	    	String date = page.getHtml().xpath("//*[@id='floor-2']/text()").toString().trim().substring(4);
	    	page.putField("date", date);
			
			//点击数
	    	String click = page.getHtml().xpath("//*[@id='tie_wrapper']/div[1]/div[3]/span[1]/text()").toString();
	    	page.putField("click", click);
	    	
			//回复数
	    	String reply = page.getHtml().xpath("//*[@id='tie_wrapper']/div[1]/div[3]/span[2]/text()").toString();
	    	page.putField("reply", reply);
	    	
	    	//内容
	    	String content = page.getHtml().toString();
	    	page.putField("content", content);

	    	List<String> link = null;
	    	link = page.getHtml().xpath("//*[@id='tie_wrapper']/div[12]/div/div/a/@href").all();
	    	page.addTargetRequests(link,3);
//	    							   http://www.lnubbs.com/forum.php?ForumID=20
    	} else if(page.getUrl().regex("http://www\\.lnubbs\\.com/forum\\.php.*").match()){

	    	List<String> link = null;     

	    	link = page.getHtml().links().regex("http://www\\.lnubbs\\.com/thread\\.php.*").all();
	    	page.addTargetRequests(link);
	    	
	    	link = page.getHtml().links().regex("http://www\\.lnubbs\\.com/forum\\.php.*").all();
	    	page.addTargetRequests(link);
	    	
	    	//skip决定pipeline要不要处理这个page
	    	page.setSkip(true);
    	}
    }

	public static void main(String[] args) {
		String url = "http://www.lnubbs.com/forum.php?ForumID=20";
//		String url1 = "http://www.lnubbs.com/thread.php?ThreadID=4370";
		// String url = "http://www.nowcoder.com/activity/campus2016";

		Spider spider = new Spider(new LNU_BBS());
		spider
//			.addUrl(url1)
			.addUrl(url)
			.setDownloader(new HttpClientDownloader())
//			.addPipeline(new ConsolePipeline())
		//  .setDownloader(new SeleniumDownloader(UtilsConstants.CHROMEDRIVER_PATH))
			.addPipeline(new FilePipeline_LNU_BBS(filePath))
			.thread(2)
			.run();
	}
    
    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setRetryTimes(3);
        }

        return site;
    }
}
