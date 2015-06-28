package inaction;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pipeline.ConsolePipeline;
import processor.PageProcessor;
import clawer.Page;
import clawer.Request;
import clawer.Site;
import clawer.Spider;
import downloader.SeleniumDownloader_ali;

public class Ali implements PageProcessor  {

    private Site site;
    private static Map<String, String> kv = new LinkedHashMap<String, String>();
    
    @Override
    public void process(Page page) {
    	System.out.println(page.getHtml());
    	if (page.getUrl().regex("http://list\\.taobao\\.com/itemlist/default\\.htm?.*").match()) {
			List<String> link = null;
			link = page.getHtml().links().regex("http://detail\\.tmall\\.com/item\\.htm?.*").all();
			page.addTargetRequests(link, 2);

			link = page.getHtml().links().regex("http://item\\.taobao\\.com/item\\.htm?.*").all();
			page.addTargetRequests(link, 2);
			String lin = null;
            page.addTargetRequest(new Request(lin).putExtra("videoName", page.getHtml().xpath("//span[@class='vname']/text()").toString()));

			link = page.getHtml().links().regex("http://list\\.taobao\\.com/itemlist/default\\.htm?.*").all();
			page.addTargetRequests(link, 1);
			System.out.println(link.size());
		} else if(page.getUrl().regex("http://detail\\.tmall\\.com/item\\.htm?.*").match()){
		
			page.putField("商品名称", page.getHtml().xpath("//*[@id='J_DetailMeta']/div[1]/div[1]/div/div[1]/h1/text()").toString());
			String price = page.getHtml().xpath("//*[@id='J_StrPriceModBox']/dd/div/span/text()").toString();//促销价
			if (price != null) {
				page.putField("商品价格", price);
			} else {
				page.putField("商品价格", page.getHtml().xpath("//*[@id='J_StrPriceModBox']/dd/span/text()").toString());//价格
			}										
			System.out.println(page.getHtml().xpath("//*[@id='J_ItemRates']"));
			System.out.println(page.getHtml().xpath("//*[@id='J_TabBar']/li[2]/a/em"));
			page.putField("商品评价", page.getHtml().xpath("//*[@id='J_ItemRates']/div/span[@class='tm-count']/text()").toString());
															//*[@id="J_ItemRates"]/div/span[2]
		} else if (page.getUrl().regex("http://item\\.taobao\\.com/item\\.htm?.*").match()) {
			System.out.println(page.getHtml().xpath("//*[@id='J_TabBar']/li[2]/a/em"));
			page.putField("商品名称", page.getHtml().xpath("//*[@id='J_Title']/h3/text()").toString());
			String price = page.getHtml().xpath("//*[@id='J_PromoPriceNum']/text()").toString();//促销价
			if (price != null && !price.equals("0")) {
				page.putField("商品价格", price);
			} else {
				page.putField("商品价格", page.getHtml().xpath("//*[@id='J_StrPrice']/em[2]/text()").toString());//价格
			}
			page.putField("商品评价", page.getHtml().xpath("//*[@id='J_RateCounter']").toString());
			page.putField("商品好评", page.getHtml().xpath("//*[@id='reviews']/div[2]/div[1]/div[1]/div[2]/ul[1]/li[4]/label/span[2]/text()").toString());
			page.putField("商品中评", page.getHtml().xpath("//*[@id='reviews']/div[2]/div[1]/div[1]/div[2]/ul[1]/li[5]/label/span[2]/text()").toString());
			page.putField("商品差评", page.getHtml().xpath("//*[@id='reviews']/div[2]/div[1]/div[1]/div[2]/ul[1]/li[6]/label/span[2]/text()").toString());
		}
	    	
    }
    
	public static void main(String[] args) {
		
		//*[@id="J_TabBar"]/li[2]/a
		//*[@id="list-itemList"]/div/div/ul/li[1]/div[2]/ul/li[3]/a/href
		//*[@id="list-itemList"]/div/div/ul/li[2]/div[2]/ul/li[3]/a
		//*[@id="list-itemList"]/div/div/ul/li[1]/div[2]/div[1]/a
		String url = "http://list.taobao.com/itemlist/default.htm?cat=1512&as=0&viewIndex=1&spm=a2106.2206569.0.0.iWOzQ8&atype=b&style=grid&ppath=20000%3A30111&same_info=1&tid=0&isnew=2&_input_charset=utf-8";
		
		String url0 = "http://detail.tmall.com/item.htm?spm=a2106.m896.1000384.19.1vfWIn&id=35492151973&source=dou&scm=1029.newlist-0.1.50034261&ppath=&sku=&ug=#detail";
		String url1 = "http://detail.tmall.com/item.htm?spm=a2156.1712380.1998411126.41.57CPMM&id=44493431381&acm=lb-tms-1712380-62508.1003.4.240528&scm=1003.4.lb-tms-1712380-62508.ITEM_44493431381_240528";
	
		String url11 = "http://detail.tmall.com/item.htm?spm=a2106.m944.1000384.1.SyvuA6&id=21992167509&source=dou&scm=1029.newlist-0.1.50071853&ppath=&sku=&ug=#detail";
		String url12 = "http://item.taobao.com/item.htm?spm=a2106.m872.1000384.110.ppb7oH&id=40613501475&scm=1029.newlist-0.1.50076920&ppath=&sku=&ug=#detail";
	
		
		String url21="http://list.taobao.com/itemlist/default.htm?cat=3306&style=list&as=0&viewIndex=1&spm=a2106.2206569.0.0.EiyVvZ&same_info=1&tid=0&_input_charset=utf-8";
		String url22="http://list.taobao.com/itemlist/default.htm?spm=a2106.2206569.0.0.EiyVvZ&cat=50005700&ppath=20000%3A46491";

		Spider spider = new Spider(new Ali());
		spider
//			.addUrl(url)
			.addUrl(url11)
//			.addUrl(url0)
//			.addUrl(url1)
//			.addUrl(url12)
			
			.addPipeline(new ConsolePipeline())
//			.addPipeline(new FilePipeline_dd("F:\\Clawer\\dd\\dangdang.csv"))
//			.setDownloader(new HttpClientDownloader())
			.setDownloader(new SeleniumDownloader_ali("F:\\程序\\chromedriver\\chromedriver.exe"))
//			.thread(3)
			.run();
	}
    
    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me()
            		.setRetryTimes(3)
            		.setSleepTime(100);
        }
        return site;
    }
    
}
