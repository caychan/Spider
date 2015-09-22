/*package lycosa.crawler.scheduler.dedup;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lycosa.crawler.dao.MysqlGameDAO;
import lycosa.crawler.fetcher.Request;
import lycosa.crawler.utils.MD5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clawer.Task;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

*//**
 * 
 * todo:TODO
 * @author dyliu
 *//*
public class BloomFilterDuplicateChecker implements DuplicateRemover {
	protected Logger logger = LoggerFactory.getLogger(getClass());

    private int expectedInsertions;

    private double fpp;

    private AtomicInteger counter;

    private final BloomFilter<CharSequence> bloomFilter;

    private final static String input_charset = "utf-8";

    public BloomFilterDuplicateChecker(int expectedInsertions) {
    	this(expectedInsertions, 0.00001);
    	logger.info("Init BloomFilterDuplicateChecker");
        // prefetch urls and crawl_timestamp of crawled detail pages from mysql
//        AddBloomFilter();
    }


	private void AddBloomFilter() {
		MysqlNewsPropertyDAO mysqlNewsPropertyDAO = new MysqlNewsPropertyDAO();
		String curDate = getCurDate();
		List<String> todayDetailUrlCrawled = mysqlNewsPropertyDAO.selectNewsUrlsByDate(curDate);
		logger.info("Prefetch crawled urls: " + todayDetailUrlCrawled.size());
		for (String url : todayDetailUrlCrawled) {
			bloomFilter.put(url);
		}
	}

    private void AddBloomFilter(){
    	//新闻的url
    	List<String> NewsDetailUrlCrawled = MysqlNewsPropertyDAO.selectNewsAllUrls();
    	logger.info("Prefetch NewsDetailUrlCrawled urls: " + NewsDetailUrlCrawled.size());
    	//良品购url
    	List<String> goodbuyUrlCrawled = MysqlGoodbuyDAO.selectGoodbuyAllUrls();
    	logger.info("Prefetch goodbuyUrlCrawled urls: " + goodbuyUrlCrawled.size());
    	
    	//城觅
    	List<String> citymiUrlCrawled = MysqlCitymiDAO.selectCitymiAllUrls();
    	logger.info("Prefetch citymiUrlCrawled urls: " + citymiUrlCrawled.size());
    	
    	//咬耳朵
    	List<String> xiaoShengUrlCrawled = MysqlXiaoShengDAO.selectXiaoShengAllUrls();
    	logger.info("Prefetch xiaoShengUrlCrawled urls: " + xiaoShengUrlCrawled.size());
    	
    	//美甲秀
    	List<String> QuXiu8UrlCrawled = MysqlQuXiu8DAO.selectQuXiu8AllUrls();
    	logger.info("Prefetch QuXiu8UrlCrawled urls: " + QuXiu8UrlCrawled.size());
    	
    	//穿衣助手
    	List<String> dressingAssistantUrlCrawled = MysqlDressingAssistantDAO.selectDressingAssistantAllUrls();
    	logger.info("Prefetch dressingAssistantUrlCrawled urls: " + dressingAssistantUrlCrawled.size());
    	
    	//爱奇艺
    	List<String> iQiYiUrlCrawledUrlCrawled = MysqlIQiYiDAO.selectIQiYiAllUrls();
    	logger.info("Prefetch iQiYiUrlCrawledUrlCrawled urls: " + iQiYiUrlCrawledUrlCrawled.size());
    	
    	//秘密
    	List<String>  secretMiMiCrawledUrlCrawled = MysqlSecretMiMiDAO.selectSecretMiMiAllUrls();
    	logger.info("Prefetch secretMiMiCrawledUrlCrawled urls: " + secretMiMiCrawledUrlCrawled.size());
    	
    	//天涯论坛
    	List<String>  tianYaCrawledUrlCrawled = MysqlTianYaDAO.selectTianYaAllUrls();
    	logger.info("Prefetch tianYaCrawledUrlCrawled urls: " + tianYaCrawledUrlCrawled.size());
    	
    	//游戏
    	List<String>  gameCrawledUrlCrawled = MysqlGameDAO.selectGameAllUrls();
    	logger.info("Prefetch gameCrawledUrlCrawled urls: " + gameCrawledUrlCrawled.size());
    	
    	//关键字
    	List<String>  keywordCrawledUrlCrawled = MysqlBaiduDAO.selectKeywordAllUrls();
    	logger.info("Prefetch keywordCrawledUrlCrawled urls: " + keywordCrawledUrlCrawled.size());
    	
		for (String url : gameCrawledUrlCrawled) {
			bloomFilter.put(url);
		}
		logger.info("已将所有url放到bloomfilter");
    }
    *//**
     *
     * @param expectedInsertions the number of expected insertions to the constructed
     * @param fpp the desired false positive probability (must be positive and less than 1.0)
     *//*
    public BloomFilterDuplicateChecker(int expectedInsertions, double fpp) {
        this.expectedInsertions = expectedInsertions;
        this.fpp = fpp;
        this.bloomFilter = rebuildBloomFilter();
    }

    protected BloomFilter<CharSequence> rebuildBloomFilter() {
        counter = new AtomicInteger(0);
        return BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), expectedInsertions, fpp);
    }

    @Override
    public boolean isDuplicate(Request request) {
        String dedupStr = getDedupString(request);
        boolean isDuplicate = bloomFilter.mightContain(dedupStr);
        if (!isDuplicate) {
            bloomFilter.put(dedupStr);
            counter.incrementAndGet();
        }
        return isDuplicate;
    }

    protected String getDedupString(Request request) {
        // CAUTION: append date string to url for avoiding daily_update miss
        return  MD5.sign(request.getUrl(), input_charset);
    }

    protected String getDedupString(Request request) {
        // CAUTION: append date string to url for avoiding daily_update miss
        return getCurDate() + request.getUrl();
    }

    private static String getCurDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = simpledateformat.format(calendar.getTime());
        return strDate;
    }

    @Override
    public void resetDuplicateChecker() {
        rebuildBloomFilter();
    }

    @Override
    public int getTotalRequestsCount() {
        return counter.get();
    }


	@Override
	public boolean isDuplicate(clawer.Request request, Task task) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void resetDuplicateCheck(Task task) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int getTotalRequestsCount(Task task) {
		// TODO Auto-generated method stub
		return 0;
	}
}
*/