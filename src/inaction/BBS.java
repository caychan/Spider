package inaction;

import java.util.ArrayList;
import java.util.List;

import pipeline.FilePipeline_LNU_BBS;
import processor.PageProcessor;
import scheduler.FileCacheQueueScheduler;
import clawer.Page;
import clawer.Site;
import clawer.Spider;

public class BBS implements PageProcessor {

	//保存爬到的网页信息路径，若要保存，必赋值
	private static String filePath = "F:\\Clawer\\LNU_BBS_nsfew\\";
    private Site site;
    
    int i = 0;
	static Spider spider = new Spider(new BBS());
	
	List<String> urlList = new ArrayList<String>();
	
    
	//停止爬虫
    public void stopSpider(){
    	spider.stop();
    }
    //得到爬过但还没显示在前端的url list，并清空list以供后续使用
    public List<String> getProcessedUrl() {
    	List<String> urlListL = getUrlList();
    	clearUrlList(urlList);

    	return urlListL;
    }

    
    @Override
    public void process(Page page) {

    	urlList.add(page.getUrl().toString());
    	
    	if(page.getUrl().regex("http://bbs\\.lnu\\.edu\\.cn/forum\\.php\\?mod=viewthread.*").match()){
    		//bbs版块
	    	String module = page.getHtml().xpath("//*[@id='pt']/div/a[4]/text()").toString();
	    	if (module == null) {
				page.setSkip(true);
				return;
			}
			page.putField("module", module);

			//标题
	    	String title = page.getHtml().xpath("//*[@id='thread_subject']/text()").toString();
	    	if (title == null) {
				page.setSkip(true);
				return;
			}
	    	page.putField("title", title);
	    	
	    	//发帖人
	    	String user = page.getHtml().xpath("//div[@class='pi']/div[@class='authi']/a/text()").toString();
	    	page.putField("user", user);
	    	
	    	//时间
	    	String date = page.getHtml().xpath("//div[@class='pti']/div[@class='authi']/em/tidyText()").toString();
	    	if (date.contains("-")) {
	    		date = date.substring(4);
			} else {
				date = page.getHtml().xpath("//div[@class='pti']/div[@class='authi']/em/span/@title").toString();
			}
	    	page.putField("date", date);
			
			//点击数
	    	String click = page.getHtml().xpath("//*[@id='postlist']/table[1]/tbody/tr/td[1]/div/span[2]/text()").toString();
	    	page.putField("click", click);
	    	
			//回复数
	    	String reply = page.getHtml().xpath("//*[@id='postlist']/table[1]/tbody/tr/td[1]/div/span[5]/text()").toString();
	    	page.putField("reply", reply);
	    	
	    	//内容
	    	String content = page.getHtml().xpath("//*[@id='postlist']/tidyText()").toString();
	    	if (content != null) {
				content = removeTags(content);
			}
	    	page.putField("content", content);

    	} else if(page.getUrl().regex("http://bbs\\.lnu\\.edu\\.cn/forum\\.php\\?mod=forumdisplay.*").match()){
	    	List<String> link = null;     

	    	//抓详情页
	    	link = page.getHtml().xpath("//*[@id='threadlisttableid']/tbody/tr/td/a/@href").all();
	    	page.addTargetRequests(link);
	    	
	    	//抓列表页
	    	link = page.getHtml().xpath("//*[@id='fd_page_top']/div/a/@href").all();
	    	page.addTargetRequests(link);
	    	
	    	//skip决定pipeline要不要处理这个page
	    	page.setSkip(true);
    	}

    }

	public static void main(String[] args) {
//		String url = "http://bbs.lnu.edu.cn/forum.php?mod=viewthread&tid=3173&extra=page%3D1";

		//必须指定至少一个url作为爬虫入口地址
		String url0 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=2";
		String url1 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=38";
		String url2 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=39";
		String url3 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=40";
		String url4 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=41";
		String url5 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=42";
		String url6 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=43";
		String url7 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=44";
		String url8 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=53";
		String url9 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=54";
		String url10 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=45";
		String url11 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=47";
		String url12 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=49";
		String url13 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=50";
		String url14 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=46";
		String url15 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=51";
		String url16 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=48";
		String url17 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=52";
		String url18 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=77";
		String url19 = "http://bbs.lnu.edu.cn/forum.php?mod=forumdisplay&fid=87";

		spider
//			.addUrl(url)
			.addUrl(url0,url1,url2,url3,url4,url5,url6,url7,url8,url9,url10,url11,url12,url13,url14,url15,url16,url17,url18,url19)
			.addPipeline(new FilePipeline_LNU_BBS(filePath))
			.setScheduler(new FileCacheQueueScheduler(filePath))
//			.thread(32)
			.run();
	}


    
    
    private List<String> getUrlList() {
		return urlList;
	}
    
    private void clearUrlList(List<String> urlList){
    	urlList.clear();
    }
	
    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setRetryTimes(3)
            		.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.132 Safari/537.36");
        }

        return site;
    }
    
	
    private String removeTags(String content){
    	String regEx_html = "<[^>]+>";
    	content = content.replaceAll(regEx_html, "");
    	
    	return content;
    }
}
