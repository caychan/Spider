package inaction;

import pipeline.ConsolePipeline;
import processor.PageProcessor;
import clawer.Page;
import clawer.Site;
import clawer.Spider;
import downloader.HttpClientDownloader;
import downloader.SeleniumDownloader;

public class Facebook implements PageProcessor  {

    private Site site;

//	QueueScheduler qs = new QueueScheduler();
//	HashSetDuplicateRemover hdr = new HashSetDuplicateRemover();
//    	System.out.println(qs.getLeftRequestsCount());
//    	System.out.println(hdr.getTotalRequestsCount());

    @Override
    public void process(Page page) {
								
    	//*[@id="tl_unit_5797238847148887783"]/div/div[2]/div/div/div/div[2]/h5/div/span/a
//    	page.putField("user", page.getHtml().xpath("//div[@class='_5pcb _4b0l']/div/div/div[2]/div/div/div/div[2]/h5/div/span/a/text()").all().toString());
    	page.putField("user", page.getHtml().xpath("//*[@class='_5pbw']/div/span/a/text()").all().toString());
//    	page.putField("name", page.getHtml().xpath("//*[@id='tl_unit_\\d+']/div/div[2]/div/div/div/div[2]/h5/div/span/a/text()"));
    	page.putField("content_all", page.getHtml().xpath("//*[@id='tl_unit_5797238847148887783']/div/div[3]/allText()"));
    	page.putField("content_tidy", page.getHtml().xpath("//*[@id='tl_unit_5797238847148887783']/div/div[3]/tidyText()"));
    	page.putField("content3", page.getHtml().xpath("//*[@class='_4-u2 mbm _5jmm _5pat _5v3q'][2]//span[@class='fwb']/a/text()"));
    																
    	
    	
    	page.putField("name", page.getHtml().xpath("//*[@id='tl_unit_\\d+']/div/div[2]/div/div/div/div[2]/h5/div/span/a/text()"));
    	
    }
    
	public static void main(String[] args) {
		
		String url = "https://www.facebook.com";
		String url1 = "https://www.facebook.com/profile.php?id=100006372165684";
		String url2 = "https://scontent.xx.fbcdn.net/hphotos-xtp1/v/t1.0-9/p370x247/10577178_1476061615949563_9074074300896989375_n.jpg?oh=93a56a707fd9c2cc3356d24bcc6bfa7d&oe=55DD74A5";

		Spider spider = new Spider(new Facebook());
		spider
			.addUrl(url)
			.addUrl(url1)
//			.addUrl(url2)
			
			.addPipeline(new ConsolePipeline())

//			.setDownloader(new HttpClientDownloader())
			
			.setDownloader(new SeleniumDownloader("F:\\程序\\chromedriver\\chromedriver.exe"))
//			.thread(3)
			.run();
	}
    
    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setCharset("utf-8");
//            		.addCookie("username", "email_wl@163.com")
//            		.addCookie("password", "quantianhou");
//            		.setRetryTimes(3);
//            		.setSleepTime(5000);
        }
        return site;
    }
    
}
