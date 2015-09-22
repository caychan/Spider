package inaction;

import pipeline.ConsolePipeline;
import processor.PageProcessor;
import clawer.Page;
import clawer.Site;
import clawer.Spider;
import downloader.HttpClientDownloader;
import downloader.SeleniumDownloader;

public class Renren implements PageProcessor  {

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
    	page.putField("content", page.getHtml().xpath("//*[@id='tl_unit_5797238847148887783']/div/div[3]").all());
    	page.putField("content2", page.getHtml().xpath("//*[@id='tl_unit_-7953609296277499934']/div/div[3]/p/text()").toString() + page.getHtml().xpath("//*[@id='tl_unit_-7953609296277499934']/div/div[3]/span/text()").toString());
    	
//    	page.putField("name", page.getHtml().xpath("//*[@id='tl_unit_\\d+']/div/div[2]/div/div/div/div[2]/h5/div/span/a/text()"));
    	
    }
    
	public static void main(String[] args) {
		
		String url = "https://www.facebook.com/profile.php?id=100006372165684";
		String url0 = "https://www.facebook.com";
		String url1 = "https://scontent.xx.fbcdn.net/hphotos-xtp1/v/t1.0-9/p370x247/10577178_1476061615949563_9074074300896989375_n.jpg?oh=93a56a707fd9c2cc3356d24bcc6bfa7d&oe=55DD74A5";
//		String url2 = "http://renren.com";

		Spider spider = new Spider(new Renren());
		spider
			.addUrl(url)
			.addUrl(url0)
			.addUrl(url1)


			.addPipeline(new ConsolePipeline())
//			.addPipeline(new FilePipeline_dd("F:\\Clawer\\dd\\dangdang.csv"))
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
