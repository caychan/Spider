package inaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pipeline.ConsolePipeline;
import pipeline.FilePipeline;
import pipeline.FilePipeline_jd;
import processor.PageProcessor;
import clawer.Page;
import clawer.Site;
import clawer.Spider;
import clawer.Task;
import downloader.SeleniumDownloader;
import downloader.SeleniumDownloader_jd;

public class Jingdong implements PageProcessor {

    private Site site;
//    private List<Map<String, String>> ;
    private static Map<String, String> kv = new LinkedHashMap<String, String>();
    
    
    @Override
    public void process(Page page) {
    	
    	if (! page.getUrl().regex("http://item\\.jd\\.com/\\d+\\.html.*").match()) {
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
	    	
			page.setSkip(true);
			} else if (page.getUrl().regex("http://item\\.jd\\.com/\\d+\\.html.*").match()) {			
				for (Map.Entry<String, String> entry : kv.entrySet()) {
					if (entry.getKey().equals("商品价格")) {
						page.putField(entry.getKey(), page.getHtml().xpath(entry.getValue()).regex("[\\d\\.]+").toString());
					}
					page.putField(entry.getKey(), page.getHtml().xpath(entry.getValue()).toString());
				}

/*				page.putField("商品类别", 
						page.getHtml().xpath("//div[@class='w']/div[@class='breadcrumb']/span[1]/a[2]/text()").toString());
				page.putField("商品名称", 
						page.getHtml().xpath("//div[@id='name']/h1/text()").toString());
				page.putField("商品价格", 
						page.getHtml().xpath("//*[@id='summary-price']/div[2]/strong/text()").toString());*/
				if (page.getResultItems().get("商品名称")==null){
					page.setSkip(true);
				}
				String category = page.getHtml().xpath("//*[@id='book']/div[4]/div[1]/strong/a/text()").toString();
				if (category != null && category.equals("图书")) {
					page.putField("商品评论", 
							page.getHtml().xpath("//*[@id='comments-list']/div[1]/ul/li[1]/a/em/text()").regex("\\d+").toString());
					page.putField("商品好评", 
							page.getHtml().xpath("//*[@id='comments-list']/div[1]/ul/li[2]/a/em/text()").regex("\\d+").toString());
					page.putField("商品中评", 
							page.getHtml().xpath("//*[@id='comments-list']/div[1]/ul/li[3]/a/em/text()").regex("\\d+").toString());
					page.putField("商品差评", 
							page.getHtml().xpath("//*[@id='comments-list']/div[1]/ul/li[4]/a/em/text()").regex("\\d+").toString());
				} else{
					page.putField("商品评论", 
							page.getHtml().xpath("//*[@id='comments-list']/div[1]/div[1]/ul/li[1]/a/em/text()").regex("\\d+").toString());
					page.putField("商品好评", 
							page.getHtml().xpath("//*[@id='comments-list']/div[1]/div[1]/ul/li[2]/a/em/text()").regex("\\d+").toString());
					page.putField("商品中评", 
							page.getHtml().xpath("//*[@id='comments-list']/div[1]/div[1]/ul/li[3]/a/em/text()").regex("\\d+").toString());
					page.putField("商品差评", 
							page.getHtml().xpath("//*[@id='comments-list']/div[1]/div[1]/ul/li[4]/a/em/text()").regex("\\d+").toString());
				}
				
	/*			page.putField("已售数量", 
						page.getHtml().xpath("").toString());
						page.putField("商品型号", page.getHtml().xpath("").toString());*/
			}
    }
    
    public static void main(String[] args) {
        	File file = new File("F:\\Clawer\\jd\\xpath.csv");
    		BufferedReader reader = null;
    		String temp = null;
    		try {
    			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"gbk"));
				while((temp = reader.readLine()) != null){
					String[] t = temp.split(",");
					if (t.length == 2) {
						kv.put(t[0], t[1]);
					}
				}
    		}catch (IOException e) {
				e.printStackTrace();
			}finally{
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
    		
//        	String url = "http://www.jd.com/allSort.aspx";
        	String url = "http://list.jd.com/list.html?cat=9987,653,655";
//        	String url1 = "http://item.jd.com/11619844.html";
//        	String url22 = "http://item.jd.com/996959.html";
        	String url2 = "http://item.jd.com/996959.html#comment";
//        	String url3 = "http://item.jd.com/757064.html#comment";
//        	String url4 = "http://item.jd.com/1025091224.html#comment";
//        	String url5 = "http://item.jd.com/1428705049.html#comment";
        	
        	Spider spider = new Spider(new Jingdong());
        	spider
//        		.addUrl(url1)
//        	.addUrl(url22)  
        		.addUrl(url2)  
//        		.addUrl(url3)  
//        		.addUrl(url4)
//        		.addUrl(url5)
        		.addUrl(url)
//        		.addPipeline(new DBPipeline())
//        		.addPipeline(new ConsolePipeline())
        		.addPipeline(new FilePipeline_jd("F:\\Clawer\\jd\\jingdong.csv"))
//        		.setDownloader(new HttpClientDownloader())
        		.setDownloader(new SeleniumDownloader_jd("F:\\程序\\chromedriver\\chromedriver.exe"))
//        		.thread(10)
        		.run();
    }
    
    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setCharset("gbk").setRetryTimes(3)
//            		.addCookie("username", cookies.get("username"))
//            		.addCookie("password", cookies.get("password"))
            		.setSleepTime(100);
        }

        return site;
    }
}
