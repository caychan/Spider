package inaction;

import java.util.List;

import pipeline.FilePipeline_nen;
import processor.PageProcessor;
import clawer.Page;
import clawer.Site;
import clawer.Spider;

public class Nen implements PageProcessor {

    private Site site;
     
    @Override
    public void process(Page page) {
    	
    	if(page.getUrl().regex("http://\\w+\\.nen\\.com\\.cn/system/[\\d/_]+\\.shtml").match()){
	    	String title = "";
	    	title = page.getHtml().xpath("//*[@class='inversey' OR @class='content' OR @class='con680' OR @class='contentleft' OR @class='bt5']/h1/text()").toString();
	    	if (title == null) {			
	    		title = page.getHtml().xpath("//*[@class='contentt']/text()").toString();
			}
			if (title == null) {			
				title = page.getHtml().xpath("/html/body/div/div[4]/div[3]/div[1]/h2/text()").toString();
			}
			if (title == null) {
				title = page.getHtml().xpath("//*[@id='w716']/h1/text()").toString();
			}
			if (title == null) {			
				title = page.getHtml().xpath("/html/body/div[2]/div[8]/div[1]/div[1]/h2/text()").toString();
			}
			if (title == null) {						
				title = page.getHtml().xpath("//*[@id='main']/div[2]/div[1]/div[2]/h1/text()").toString();
			}
			if (title == null) {						
				title = page.getHtml().xpath("/html/body/div[3]/div[1]/h1/text()").toString();
			}
			if (title != null && title.length() < 5) {
				title = page.getHtml().xpath("/html/body/table/tbody/tr/td[1]/table[2]/tbody/tr[2]/td/text()").toString();
			}
			if (title == null) {
				title = "";
			}
			page.putField("title", title);

	    	String source = page.getHtml().xpath("/html/body/div/div[4]/div[1]/div[4]/a[2]/text()").toString();
	    	if (source == null) {
				source = page.getHtml().xpath("//*[@id='source_baidu']/allText()").toString();
			}
	    	if (source == null) {			
	    		source = page.getHtml().xpath("/html/body/div[7]/div[1]/h2/a[2]/text()").toString();
	    	}
	    	if (source == null) {			
	    		source = page.getHtml().xpath("//*[@id='main']/div[2]/div[1]/div[2]/p[1]/span[3]/text()").toString();
	    	}
	    	if (source == null) {			
	    		source = "";
	    	}
	    	page.putField("source", source);
			
	    	String content = page.getHtml().xpath("//*[@class='content' OR @class='conttitel' OR @class='con' OR @class='contentconshindex']/tidyText()").toString();
	    	if (content == null) {
	    		content = page.getHtml().xpath("//*[@class='contentcon']/tidyText()").toString();
			}
	    	if (content == null) {
	    		content = page.getHtml().xpath("/html/body/div[7]/div[1]/div[2]/tidyText()").toString();
	    	}
	    	if (content == null) {
	    		content = page.getHtml().xpath("/html/body/div[2]/div[8]/div[1]/div[1]/div[6]/tidyText()").toString();
	    	}
	    	if (content == null) {
	    		content = page.getHtml().xpath("//*[@id='main']/div[2]/div[1]/div[2]/p[4]/tidyText()").toString();
	    	}
			if (content == null) {
				content = "";
			}
			page.putField("content", content);
			
	    	List<String> link = null;          
	    	link = page.getHtml().xpath("//*[@id='news_more_page_div_id']/a/@href").all();
	    	page.addTargetRequests(link,3);
			
    	} else if(page.getUrl().regex("http://house\\.nen\\.com\\.cn/fangchan/[\\w/_]+\\.shtml").match()){
	    	String title = page.getHtml().xpath("/html/body/table[7]/tbody/tr/td[1]/table/tbody/tr/td/table/tbody/tr/td/p/text()").toString();
			page.putField("title", title);

			String date = page.getHtml().xpath("/html/body/table[7]/tbody/tr/td[1]/table/tbody/tr/td/table/tbody/tr/td/table[1]/tbody/tr/td[1]/span[2]/text()").toString();
			page.putField("date", date);

			String source = page.getHtml().xpath("/html/body/table[7]/tbody/tr/td[1]/table/tbody/tr/td/table/tbody/tr/td/table[1]/tbody/tr/td[1]/span[3]/text()").toString();
	    	page.putField("source", source);
			
	    	String content = page.getHtml().xpath("/html/body/table[7]/tbody/tr/td[1]/table/tbody/tr/td/table/tbody/tr/td/table[3]/tbody/tidyText()").toString();

			page.putField("content", content);
			
	    	List<String> link = null;          
	    	link = page.getHtml().xpath("//*[@id='news_more_page_div_id']/a/@href").all();
	    	page.addTargetRequests(link,3);
    	} else if(page.getUrl().regex("http://it\\.nen\\.com\\.cn/[\\w/]+\\.shtml").match()){
    		String date = page.getHtml().xpath("/html/body/div/div[1]/div[4]/span/text()").toString();
    		page.putField("date", date);
    		if (date == null) {
				page.setSkip(true);
			}
    		String title = page.getHtml().xpath("/html/body/div/div[1]/h1/text()").toString();
    		page.putField("title", title);
    		String content = page.getHtml().xpath("/html/body/div/div[1]/div[6]/tidyText()").toString();
    		
    		page.putField("content", content);
    		
    		List<String> link = null;          
    		link = page.getHtml().xpath("//*[@id='news_more_page_div_id']/a/@href").all();
    		page.addTargetRequests(link,3);
    		
    	} else{
	    	List<String> link = null;       
	    	link = page.getHtml().links().regex("(http://(?!dbsh|video|bbs|englishchannel).*\\.nen\\.com\\.cn.*)").all();
	    	page.addTargetRequests(link);
	    	
	    	page.setSkip(true);
    	}
    }

    public static void main(String[] args) {
        	String url = "http://www.nen.com.cn/";
        	String url1 = "http://edu.nen.com.cn/system/2015/06/16/017773793.shtml";
        	String url2 = "http://house.nen.com.cn/fangchan/web/html/100309/2015620/1434765836861.shtml";
        	String url3 = "http://it.nen.com.cn/247941/101546037511b.shtml";
        	
        	String url4 = "http://in.nen.com.cn/system/2015/06/19/017798420.shtml";
        	String url5 = "http://finance.nen.com.cn/system/2015/06/19/017796859.shtml";
        	String url6 = "http://in.nen.com.cn/system/2015/06/19/017796552.shtml";
        	String url7 = "http://dbsh.nen.com.cn";

        	Spider spider = new Spider(new Nen());
        	spider
        		.addUrl(url)
//        		.addUrl(url1)
//        		.addUrl(url2)
//        		.addUrl(url3)
//        		.addUrl(url4)
//        		.addUrl(url5)
//        		.addUrl(url6)
//        		.addUrl(url7)
//        		.addPipeline(new ConsolePipeline())
        		.addPipeline(new FilePipeline_nen("F:\\Clawer\\nen\\"))
        		.thread(50)
        		.run();
    }
    
    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setCharset("gbk").setRetryTimes(3);
        }

        return site;
    }
}
