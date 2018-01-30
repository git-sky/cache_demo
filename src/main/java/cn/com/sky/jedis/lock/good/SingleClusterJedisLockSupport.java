package cn.com.sky.jedis.lock.good;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

/**
 * Single cluster lock support using Jedis
 *
 * @author Mazra, Gaurav Rai
 */
public class SingleClusterJedisLockSupport implements JedisLockSupport {

    private static final long MIN_WAIT_MS = 100;
    private JedisPool jedisPool;
    private long lockAcquireTime;

    public SingleClusterJedisLockSupport(JedisPool jedisPool, long lockAcquireTime) {
        super();
        this.jedisPool = jedisPool;
        this.lockAcquireTime = lockAcquireTime;
    }

    @Override
    public boolean acquire(String key, String owner) {
        return tryAcquire(key, owner, 0L, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean tryAcquire(String key, String owner, long timeout, TimeUnit timeUnit) {
        boolean acquired = false;
        key = normalizeKey(key);
        long timeToWaitInMs = timeUnit == TimeUnit.MILLISECONDS ? timeout : timeUnit.toMillis(timeout);
        try (Jedis jedis = jedisPool.getResource()) {
            while (!acquired && timeToWaitInMs >= 0) {
                synchronized (key) {
                    acquired = isMine(key, owner, jedis) || locked(key, owner, jedis);
                }
                timeToWaitInMs = timeToWaitInMs - MIN_WAIT_MS;
                if (timeToWaitInMs > 0)
                    waitForRetry();
            }
        }
        return acquired;
    }

    private void waitForRetry() {
        try {
            TimeUnit.MILLISECONDS.wait(MIN_WAIT_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    String lua_script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @Override
    public void release(String key, String owner) {
        key = normalizeKey(key);
        synchronized (key) {
            try (Jedis jedis = jedisPool.getResource();) {
                Object result = jedis.eval(lua_script, 1, key, value(key, owner));
                //TODO need to log it
            }
        }
    }

    private boolean isMine(String key, String owner, Jedis jedis) {
        String value = jedis.get(key);
        return (value != null) && value.endsWith(owner);
    }

    private String value(String key, String owner) {
        return new StringBuilder(key.length() + owner.length() + 2).append(key).append("::").append(owner).toString();
    }

    private boolean locked(String key, String owner, Jedis jedis) {
        return setIfNotExists(key, owner, jedis) != null;
    }

    private String setIfNotExists(String key, String owner, Jedis jedis) {
        return jedis.set(key, value(key, owner), "nx", "ex", lockAcquireTime);
    }

    /**
     * Interning the string key. Used internally.
     * String str1 = new String("abc");
     * String str2 = new String("abc");
     * str1 and str2 are two different objects
     * and we synchronized based on the passed key
     *
     * @param key to normalize
     * @return the normalized key
     */
    private String normalizeKey(String key) {
        return key.intern();
    }
}