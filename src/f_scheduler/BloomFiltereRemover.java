package f_scheduler;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;


public class BloomFiltereRemover {

    private int expectedInsertions;

    private double fpp;

    private AtomicInteger counter;

//    private final BloomFilter<CharSequence> bloomFilter;
    private BloomFilter<CharSequence> bloomFilter;

    
    public BloomFiltereRemover(int expectedInsertions) {
        this(expectedInsertions, 0.001);
    }

    /**
     *
     * @param expectedInsertions the number of expected insertions to the constructed
     * @param fpp the desired false positive probability (must be positive and less than 1.0)
     */
    public BloomFiltereRemover(int expectedInsertions, double fpp) {
        this.expectedInsertions = expectedInsertions;
        this.fpp = fpp;
        this.bloomFilter = rebuildBloomFilter();
    }

    protected BloomFilter<CharSequence> rebuildBloomFilter() {
        counter = new AtomicInteger(0);
        return BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), expectedInsertions, fpp);
    }


    //重复返回true
    public boolean isDuplicate(File file) {
        boolean isDuplicate = bloomFilter.mightContain(file.getAbsolutePath());
        if (!isDuplicate) {
            bloomFilter.put(file.getAbsolutePath());
            counter.incrementAndGet();
        }
        return isDuplicate;
    }

    protected String getPath(File file) {
        return file.getAbsolutePath();
    }
    
    public boolean put(String path) {
		return bloomFilter.put(path);
	}
    
}
