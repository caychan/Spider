package scheduler;

import org.apache.commons.codec.digest.DigestUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import clawer.Request;
import clawer.Task;

import com.alibaba.fastjson.JSON;
import component.DuplicateRemover;

/**
 * Use Redis as url scheduler for distributed crawlers.<br>
 *
 */
public class RedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {

    private JedisPool pool;

    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";

    public RedisScheduler(String host) {
        this(new JedisPool(new JedisPoolConfig(), host));
    }

    public RedisScheduler(JedisPool pool) {
        this.pool = pool;
        setDuplicateRemover(this);
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        Jedis jedis = pool.getResource();
        try {
            jedis.del(getSetKey(task));
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        Jedis jedis = pool.getResource();
        try {
            boolean isDuplicate = jedis.sismember(getSetKey(task), request.getUrl());
            if (!isDuplicate) {
                jedis.sadd(getSetKey(task), request.getUrl());
            }
            return isDuplicate;
        } finally {
            pool.returnResource(jedis);
        }

    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        Jedis jedis = pool.getResource();
        try {
            jedis.rpush(getQueueKey(task), request.getUrl());
            if (request.getExtras() != null) {
                String field = DigestUtils.shaHex(request.getUrl());
                String value = JSON.toJSONString(request);
                jedis.hset((ITEM_PREFIX + task.getUUID()), field, value);
            }
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public synchronized Request poll(Task task) {
        Jedis jedis = pool.getResource();
        try {
            String url = jedis.lpop(getQueueKey(task));
            if (url == null) {
                return null;
            }
            String key = ITEM_PREFIX + task.getUUID();
            String field = DigestUtils.shaHex(url);
            byte[] bytes = jedis.hget(key.getBytes(), field.getBytes());
            if (bytes != null) {
                Request o = JSON.parseObject(new String(bytes), Request.class);
                return o;
            }
            Request request = new Request(url);
            return request;
        } finally {
            pool.returnResource(jedis);
        }
    }

    protected String getSetKey(Task task) {
        return SET_PREFIX + task.getUUID();
    }

    protected String getQueueKey(Task task) {
        return QUEUE_PREFIX + task.getUUID();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        Jedis jedis = pool.getResource();
        try {
            Long size = jedis.llen(getQueueKey(task));
            return size.intValue();
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        Jedis jedis = pool.getResource();
        try {
            Long size = jedis.scard(getQueueKey(task));
            return size.intValue();
        } finally {
            pool.returnResource(jedis);
        }
    }
}
