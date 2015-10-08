package inaction;

import java.util.List;

import pipeline.ConsolePipeline;
import processor.PageProcessor;
import utils.UtilsConstants;
import clawer.Page;
import clawer.Site;
import clawer.Spider;
import clawer.Task;
import downloader.SeleniumDownloader_jd;

public class Jd implements PageProcessor, Task {
	

	private Site site;

	@Override
	public void process(Page page) {

		System.out.println(page.getUrl());
		// 如果不是评论页，取url。如果是评论页，取数据
		if (!page.getUrl().regex("http://item\\.jd\\.com/\\d+\\.html#comment").match()) {
			List<String> link = null;

			link = page.getHtml().links().regex("http://item\\.jd\\.com/\\d+\\.html").all();
			if (link != null) {
				for (int i = 0; i < link.size(); ++i) {
					link.set(i, link.get(i) + "#comment");
				}
			}
			page.addTargetRequests(link);

			link = page.getHtml().links()
					.regex("http://channel\\.jd\\.com/.*\\.html").all();
			page.addTargetRequests(link);
			link = page.getHtml().links()
					.regex("http://list\\.jd\\.com/list\\.html\\?cat=.*").all();
			page.addTargetRequests(link);

			page.setSkip(true);
		} else if (page.getUrl().regex("http://item\\.jd\\.com/\\d+\\.html#comment").match()) {
			page.putField("商品类别",  
					page.getHtml().xpath("//*[@id='root-nav']/div/div/strong/a/text()").toString());
			page.putField("商品名称",
					page.getHtml().xpath("//div[@id='name']/h1/text()").toString());
			if (page.getResultItems().get("商品名称") == null) {
				page.setSkip(true);
			}
			page.putField("商品价格",
					page.getHtml().xpath("//*[@id='summary-price']/div[2]/strong/text()").toString());
			
			String comment = page.getHtml().xpath("//*[@id='comments-list']/div[1]/div[1]/ul/li[1]/a/em/text()").regex("\\d+").toString();
			if (comment == null) {				
				page.setSkip(true);
				return;
			}
			page.putField("商品评论",comment);
				
			page.putField("商品好评",
				page.getHtml().xpath("//*[@id='comments-list']/div[1]/div[1]/ul/li[2]/a/em/text()").regex("\\d+").toString());
			page.putField("商品中评",
				page.getHtml().xpath("//*[@id='comments-list']/div[1]/div[1]/ul/li[3]/a/em/text()").regex("\\d+").toString());
			page.putField("商品差评",
				page.getHtml().xpath("//*[@id='comments-list']/div[1]/div[1]/ul/li[4]/a/em/text()").regex("\\d+").toString());
		}
	}

	public static void main(String[] args) {

		// String url = "http://www.jd.com/allSort.aspx";
		String url = "http://list.jd.com/list.html?cat=9987,653,655";
		String url1 = "http://list.jd.com/list.html?cat=1713,3267,3456";

		Spider spider = new Spider(new Jd());
		spider
		.addUrl(url)
		.addUrl(url1)
				.addPipeline(new ConsolePipeline())
//				.addPipeline(new FilePipeline("F:\\Clawer\\京东.txt"))
//				 .setDownloader(new HttpClientDownloader())
				.setDownloader(new SeleniumDownloader_jd(UtilsConstants.CHROMEDRIVER_PATH))
//				 .thread(3)
				.run();
	}

	@Override
	public Site getSite() {
		if (null == site) {
			site = Site.me();
//			.setCharset("gbk");
			// .setSleepTime(100);
		}

		return site;
	}

	@Override
	public String getUUID() {
		return null;
	}

	@Override
	public int getTaskId() {
		return 0;
	}
}
