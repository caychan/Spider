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

public class Amazon implements PageProcessor  {

    private Site site;
    private static Map<String, String> kv = new LinkedHashMap<String, String>();
    
    @Override
    public void process(Page page) {	
	    	String category = page.getHtml().xpath("//*[@id='wayfinding-breadcrumbs_feature_div']/ul/li/span/a/text()").toString();
	    	//如果该页有分类信息，说明是详情页，不搜集详情页的url
	    	if (category != null) {
	    		page.putField("商品分类", category);
	    		page.putField("商品名称", page.getHtml().xpath("//*[@id='productTitle']/text()").toString());
	    		
	    		String price = page.getHtml().xpath("//*[@id='priceblock_ourprice']").regex("[\\d\\.]+").toString();
	    		page.putField("商品价格", price);
	    		if (price == null) {
	    			price = page.getHtml().xpath("//*[@id='soldByThirdParty']/span[1]/text()").regex("[\\d\\.]+").toString();
	    			page.putField("商品价格", price);
				}
	    		if (price == null) {
	    			price = page.getHtml().xpath("//*[@id='priceblock_saleprice']").regex("[\\d\\.]+").toString();
	    			page.putField("商品价格", price);
				}
	    		page.putField("商品评论", page.getHtml().xpath("//*[@id='summaryStars']/a/text()").regex("\\d+").toString());
	    		getComment(page, "5星评论", "//*[@id='histogramTable']/tbody/tr[1]/td[3]/a/text()");
	    		getComment(page, "4星评论", "//*[@id='histogramTable']/tbody/tr[2]/td[3]/a/text()");
	    		getComment(page, "3星评论", "//*[@id='histogramTable']/tbody/tr[3]/td[3]/a/text()");
	    		getComment(page, "2星评论", "//*[@id='histogramTable']/tbody/tr[4]/td[3]/a/text()");
	    		getComment(page, "1星评论", "//*[@id='histogramTable']/tbody/tr[5]/td[3]/a/text()");
	    		

	    	} else {
		    	List<String> link = page.getHtml().links().regex("http://www\\.amazon\\.cn.*").all();
		    	page.addTargetRequests(link);
			}
	    	
//			page.setSkip(true);


/*			if (page.getResultItems().get("商品名称") == null){
				page.setSkip(true);
			}*/
    }
    
	public static void main(String[] args) {
		//*[@id="olp_feature_div"]/div/span/span
		String url = "http://www.amazon.cn/gp/site-directory/ref=nav_sad";
		String url0 = "http://www.amazon.cn/adidas-%E9%98%BF%E8%BF%AA%E8%BE%BE%E6%96%AF-EVERGREEN-ORG-5-%E5%A5%B3%E5%BC%8F-%E5%8D%95%E8%82%A9%E5%8C%85-%E6%96%9C%E6%8C%8E%E5%8C%85-%E9%BB%91-%E9%B2%9C%E7%BA%A2-S14-%E9%9D%92%E7%81%B0-%E5%9D%87%E7%A0%81-S03870/dp/B00L4WF72E/ref=sr_1_2?s=luggage&ie=UTF8&qid=1427719767&sr=1-2";
		String url1 = "http://www.amazon.cn/gp/product/B002KFYYXC/ref=s9_ps_bw_d99_g14_i3?pf_rd_m=A1AJ19PSB66TGU&pf_rd_s=merchandised-search-5&pf_rd_r=0PCJ79K81B6PT9JSKFEC&pf_rd_t=101&pf_rd_p=163663652&pf_rd_i=658400051";
		

		Spider spider = new Spider(new Amazon());
		spider
//			.addUrl(url)
			.addUrl(url0)
			.addUrl(url1)
			.addPipeline(new ConsolePipeline())
//			.addPipeline(new FilePipeline_dd("F:\\Clawer\\dd\\dangdang.csv"))
			.setDownloader(new HttpClientDownloader_change())
//			.setDownloader(new SeleniumDownloader_dd("F:\\程序\\chromedriver\\chromedriver.exe"))
//			.thread(3)
			.run();
	}
    
    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me()
            		.setRetryTimes(3);
//            		.setSleepTime(100);
        }
        return site;
    }
    
    private void getComment(Page page, String c, String comm){
		String comment = page.getHtml().xpath(comm).toString();
		if (comment != null) {
			page.putField(c, comment);
		} else {
			page.putField(c, 0);
		}
    }
}
