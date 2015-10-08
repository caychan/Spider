package component;

import clawer.Request;
import clawer.Task;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * BloomFilterDuplicateRemover for huge number of urls.
 *
 */
public class BloomFilterDuplicateRemover implements DuplicateRemover {

    private int expectedInsertions;

    private double fpp;

    private AtomicInteger counter;

    public BloomFilterDuplicateRemover(int expectedInsertions) {
        this(expectedInsertions, 0.001);
    }

    /**
     *
     * @param expectedInsertions the number of expected insertions to the constructed
     * @param fpp the desired false positive probability (must be positive and less than 1.0)
     */
    public BloomFilterDuplicateRemover(int expectedInsertions, double fpp) {
        this.expectedInsertions = expectedInsertions;
        this.fpp = fpp;
        this.bloomFilter = rebuildBloomFilter();
    }

    protected BloomFilter<CharSequence> rebuildBloomFilter() {
        counter = new AtomicInteger(0);
        return BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), expectedInsertions, fpp);
    }

    private final BloomFilter<CharSequence> bloomFilter;

    @Override
    public boolean isDuplicate(Request request, Task task) {
        boolean isDuplicate = bloomFilter.mightContain(getUrl(request));
        if (!isDuplicate) {
            bloomFilter.put(getUrl(request));
            counter.incrementAndGet();
        }
        return isDuplicate;
    }

    protected String getUrl(Request request) {
        return request.getUrl();
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        rebuildBloomFilter();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return counter.get();
    }
}
