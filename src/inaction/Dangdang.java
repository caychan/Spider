package inaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Keyboard;

import pipeline.ConsolePipeline;
import pipeline.FilePipeline;
import pipeline.FilePipeline_dd;
import processor.PageProcessor;
import clawer.Page;
import clawer.Site;
import clawer.Spider;
import downloader.Downloader;
import downloader.HttpClientDownloader_change;
import downloader.SeleniumDownloader;
import downloader.SeleniumDownloader_dd;

public class Dangdang implements PageProcessor  {

    private Site site;
    private static Map<String, String> kv = new LinkedHashMap<String, String>();
    
    @Override
    public void process(Page page) {	

    	//如果不是详情页，则抓有用的url
    	if (! page.getUrl().regex("http://product\\.dangdang\\.com/\\d+\\.html.*").match()) {        
	    	List<String> link = null; 
	    	link = page.getHtml().links().regex("http://product\\.dangdang\\.com/\\d+\\.html").all();
//	    	if (link != null) {
//	    		for (int i = 0; i < link.size(); ++i) {
//	    			link.set(i, link.get(i)+"#comment");
//	    		}
//			}
	    	page.addTargetRequests(link);
	    	
	    	link = page.getHtml().links().regex("http://category\\.dangdang\\.com/[\\w\\.]+\\.html.*").all();
	    	page.addTargetRequests(link);  
	    	
	    	link = page.getHtml().links().regex("http://e\\.dangdang\\.com/list_[\\w\\.]+\\.html.*").all();
	    	page.addTargetRequests(link);
	    	
			page.setSkip(true);
		}
    	//如果是详情页，则抓商品数据
  
    	else if (page.getUrl().regex("http://product\\.dangdang\\.com/\\d+\\.html.*").match()) {
			for (Map.Entry<String, String> entry : kv.entrySet()) {
				if (entry.getKey().equals("商品价格")) {
					page.putField(entry.getKey(), page.getHtml().xpath(entry.getValue()).regex("[\\d\\.]+").toString());
				}
				else if (entry.getKey().contains("评")) {
					page.putField(entry.getKey(), page.getHtml().xpath(entry.getValue()).regex("\\d+").toString());
				} else {
					page.putField(entry.getKey(), page.getHtml().xpath(entry.getValue()).toString());
				}
			}
			if (page.getResultItems().get("商品名称") == null){
				page.setSkip(true);
			}
/*			page.putField("商品类别",
					page.getHtml().xpath("//div[@class='breadcrumb']/a[2]/text()").toString());
			page.putField("商品名称", 
					page.getHtml().xpath("//div[@class='breadcrumb']/span/text()").toString());
			if (page.getResultItems().get("商品名称") == null){
				page.setSkip(true);
			}
			
			page.putField("商品价格", 
					page.getHtml().xpath("//b[@class='d_price']").regex("[\\d\\.]+").toString());
			page.putField("商品评论", 
					page.getHtml().xpath("//*[@id='type_1']/text()").regex("\\d+").toString());
			page.putField("商品好评", 
					page.getHtml().xpath("//*[@id='type_2']/text()").regex("\\d+").toString());
			page.putField("商品中评", 
					page.getHtml().xpath("//a[@id='type_3']/text()").regex("\\d+").toString());
			page.putField("商品差评", 
					page.getHtml().xpath("//a[@id='type_4']/text()").regex("\\d+").toString());
*/
		}
    }
    
	public static void main(String[] args) {
    	File file = new File("F:\\Clawer\\dd\\xpath.csv");
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

		String url0 = "http://product.dangdang.com/1004087525.html";
		String url1 = "http://product.dangdang.com/1093549534.html";
		String url2 = "http://category.dangdang.com/cid4005370.html";
		
		
		String url = "http://category.dangdang.com/?ref=www-0-C";
		String url3 = "http://category.dangdang.com/cp01.50.03.00.00.00.html";
		String url4 = "http://e.dangdang.com/list_98.10.02.00.00.00.htm#ref=yc-0-A";
		String url5 = "http://product.dangdang.com/20769374.html#ddclick?act=click&pos=20769374_1_1_m&cat=4003632&key=&qinfo=&pinfo=&minfo=2784_1_48&ninfo=&custid=&permid=20150103193327053132119331023557264&ref=&rcount=&type=&t=1427183126000";

		Spider spider = new Spider(new Dangdang());
		spider
		.addUrl(url0)
		.addUrl(url1)
		.addUrl(url2)
//		.addUrl(url)
		// .addUrl(url1)
		// .addUrl(url3)
		// .addUrl(url4)
		// .addUrl(url5)
		// .addPipeline(new DBPipeline())
				.addPipeline(new ConsolePipeline())
				 .addPipeline(new FilePipeline_dd("F:\\Clawer\\dd\\dangdang.csv"))
//				 .setDownloader(new HttpClientDownloader())
				.setDownloader(new SeleniumDownloader_dd("F:\\程序\\chromedriver\\chromedriver.exe"))
//				 .thread(3)
//				 .runAsync()
				.run();
	}
    
    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setCharset("gbk")
            		.setRetryTimes(3);
//            		.setSleepTime(100);
        }
        return site;
    }
}
