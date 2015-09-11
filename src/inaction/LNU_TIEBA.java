package inaction;

import java.util.ArrayList;
import java.util.List;

import pipeline.ConsolePipeline;
import pipeline.FilePipeline_LNU_TIEBA;
import processor.PageProcessor;
import scheduler.FileCacheQueueScheduler;
import clawer.Page;
import clawer.Site;
import clawer.Spider;

public class LNU_TIEBA implements PageProcessor {

	private static String filePath = "F:\\Clawer\\LNU_TIEBA\\";
//	private static String fileCacheQueueSchedulerPath = "F:\\Clawer\\LNU_TIEBA_Scheduler\\";
    private Site site;
    
    @Override
    public void process(Page page) {
    	//用webmagic自带的match竟然匹配“http://tieba.baidu.com/p/3920152176?pn=2”
    	if(page.getUrl().toString().matches("http://tieba\\.baidu\\.com/p/\\d+")){
    		String tieba = page.getHtml().xpath("//*[@id='container']/div/div[1]/div[2]/div[2]/a[1]/text()").toString();
    		if (tieba != null && tieba.trim().equals("辽宁大学吧")) {
    			//标题
    			String title = page.getHtml().xpath("//*[@id='j_core_title_wrap']/h3/text()").toString();
    			if (title == null || title.trim().equals("")) {
    				page.setSkip(true);
					return;
				}
    			page.putField("title", title);
    			
    			//发帖人
    			String user = page.getHtml().xpath("//*[@id='j_p_postlist']/div[1]/div[1]/ul/li[3]/a/text()").toString();
    			page.putField("user", user);
    			
    			//时间									
    			String date = page.getHtml().xpath("//*[@id='j_p_postlist']/div[1]/div[2]/div[3]/div[1]/div/span[4]/text()").toString();
    			if (date == null) {				
    				date = page.getHtml().xpath("//*[@id='j_p_postlist']/div[1]/div[2]/div[3]/div[1]/div/span[3]/text()").toString();
    			} 
    			if (date == null) {				
    				date = page.getHtml().xpath("//*[@id='j_p_postlist']/div/div[2]/div[2]/div[1]/div[2]/span[3]/text()").toString();
    			} 
    			if (date == null || date.trim().equals("")) {
    				page.setSkip(true);
					return;
				}
    			page.putField("date", date);
    			
    			//回复数
    			String reply = page.getHtml().xpath("//*[@id='thread_theme_5']/div[1]/ul/li[2]/span[1]/text()").toString();
    			page.putField("reply", reply);
    			
    			//内容
    			String content = page.getHtml().toString();
    			page.putField("content", content);
    			
    			List<String> link = null;
    			link = page.getHtml().xpath("//*[@id='thread_theme_7']/div[1]/ul/li[1]/a/@href").all();
    			if (link.size() > 0) {
    				page.putField("nextPage", link.size());
    				page.addTargetRequests(link,5);
    			}
			} else {
				page.setSkip(true);
			}

	    	//如果是子页，则只爬内容和子页的href
    	} else if(page.getUrl().regex("http://tieba\\.baidu\\.com/p/\\d+\\?pn=\\d+").match()){
    		String tieba = page.getHtml().xpath("//*[@id='container']/div/div[1]/div[2]/div[2]/a[1]/text()").toString();
    		if (tieba != null && tieba.trim().equals("辽宁大学吧")) {

				//标题								 //*[@id="j_core_title_wrap"]/div[2]/h1
		    	String title = page.getHtml().xpath("//*[@id='j_core_title_wrap']/h3/text()").toString();
		    	if (title.contains("回复：")) {
					title = title.replace("回复：", "");
				} 
    			if (title == null || title.trim().equals("")) {
    				page.setSkip(true);
					return;
				}
		    	page.putField("title", title);
		    	
		    	//内容
		    	String content = page.getHtml().toString();
		    	page.putField("正文", content);
	
		    	List<String> link = null;
		    	link = page.getHtml().xpath("//*[@id='thread_theme_7']/div[1]/ul/li[1]/a/@href").all();
		    	List<String> next = new ArrayList<String>();
	 	    	if (link.size() > 0) {
		    		for (String url : link) {
						if (url.contains("pn=1")) {
							//url中含有"pn=1"的是第一页，这页已经爬过，但是第一页的url中不含"pn=1"字符
							continue;
						}
						next.add(url);
					}
		    		page.addTargetRequests(next,5);
		    		page.putField("nextPage", link.size());
				}
    		} else {
				page.setSkip(true);
			}
		} else if(page.getUrl().regex("http://tieba\\.baidu\\.com/f\\?\\w+").match()){
			String tieba = page.getHtml().xpath("//a[@class='card_title_fname']//text()").toString();
			if (tieba != null && tieba.trim().equals("辽宁大学吧")) {
				List<String> link = null;
				link = page.getHtml().links()
						.regex("http://tieba\\.baidu\\.com/p/\\d+").all();
				page.addTargetRequests(link);

				link = page.getHtml()
						.xpath("//*[@id='frs_list_pager']/a/@href").all();
				page.addTargetRequests(link);
			} 
			// skip决定pipeline要不要处理这个page
			page.setSkip(true);
		}
    }

	public static void main(String[] args) {
		String url = "http://tieba.baidu.com/f?ie=utf-8&kw=%E8%BE%BD%E5%AE%81%E5%A4%A7%E5%AD%A6&fr=search";
		String url1 = "http://tieba.baidu.com/p/4011131260";
		
		Spider spider = new Spider(new LNU_TIEBA());
		spider
		.addUrl(url1)
			.addUrl(url)
			.setScheduler(new FileCacheQueueScheduler(filePath))
//			.addUrl(url1)
//			.addPipeline(new ConsolePipeline())
		//  .setDownloader(new SeleniumDownloader(UtilsConstants.CHROMEDRIVER_PATH))
			.addPipeline(new FilePipeline_LNU_TIEBA(filePath))
			.thread(32)
			.run();
	}
    
    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setRetryTimes(3).setCharset("utf-8");
        }

        return site;
    }
}
