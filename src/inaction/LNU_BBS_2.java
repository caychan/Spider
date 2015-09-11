package inaction;

import pipeline.FilePipeline_LNU_BBS;
import processor.PageProcessor;
import clawer.Page;
import clawer.Site;
import clawer.Spider;

public class LNU_BBS_2 implements PageProcessor {

	private static String filePath = "F:\\Clawer\\LNU_BBS\\";
//	private static String fileCacheQueueSchedulerPath = "F:\\Clawer\\LNU_TIEBA_Scheduler\\";
    private Site site = null;
    
    @Override
    public void process(Page page) {
    	System.out.println(page.getHtml());
    }

	public static void main(String[] args) {
/*		String url = "http://www.lnubbs.com/forum.php?ForumID=20";
		String url1 = "http://www.lnubbs.com/";
		
		Spider.create(new LNU_BBS_2())
			.addUrl(url1)
			.addUrl(url)
//			.setScheduler(new FileCacheQueueScheduler(filePath))
//			.addPipeline(new FilePipeline_LNU_BBS(filePath))
//			.thread(2)
			.run();*/
	
        Spider.create(new LNU_BBS_2())
//    	.setDownloader(new HttpClientDownloader_old())
    	.addUrl("http://www.lnubbs.com/forum.php?ForumID=20")
        .run();
	}
    
    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setRetryTimes(2);
        }

        return site;
    }
}
