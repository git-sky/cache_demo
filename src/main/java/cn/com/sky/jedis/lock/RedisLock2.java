package cn.com.sky.jedis.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * 解决RedisLock中的三种问题。
 * 但是会面临服务器之间时间同步的问题。
 * 在分布式场景中，多台服务器之间的时间做到同步是非常困难的，所以在代码中我加了1秒的时间容错，但依赖服务器时间的同步还是可能会不靠谱的。
 *  如果多个主机的时间不同步的话这个同步锁没有意义。
 */
public class RedisLock2 {
    private static final Logger logger = LoggerFactory.getLogger(RedisLock.class);
    private final Jedis jedis;
    private final byte[] lockKey;

    public RedisLock2(Jedis jedis, String lockKey) {
        this.jedis = jedis;
        this.lockKey = lockKey.getBytes();
    }

    private boolean tryLock(byte[] lockKey, int lockSeconds) throws Exception {
        long nowTime = System.currentTimeMillis();
        long expireTime = nowTime + lockSeconds * 1000 + 1000; // 容忍不同服务器时间有1秒内的误差
        if (Objects.equals(jedis.setnx(lockKey, longToBytes(expireTime)), 1)) {
            jedis.expire(lockKey, lockSeconds);
            return true;
        } else {
            byte[] oldValue = jedis.get(lockKey);
            if (oldValue != null && bytesToLong(oldValue) < nowTime) {
                // 这个锁已经过期了，可以获得它
                // PS: 如果setNX和expire之间客户端发生崩溃，可能会出现这样的情况
                byte[] oldValue2 = jedis.getSet(lockKey, longToBytes(expireTime));
                if (Arrays.equals(oldValue, oldValue2)) {
                    // 获得了锁
                    jedis.expire(lockKey, lockSeconds);
                    return true;
                } else {
                    // 被别人抢占了锁(此时已经修改了lockKey中的值，不过误差很小可以忽略)
                    return false;
                }
            }
        }
        return false;
    }


    /**
     * 轮询的方式去获得锁，成功返回true，超过轮询次数或异常返回false
     *
     * @param lockSeconds       加锁的时间(秒)，超过这个时间后锁会自动释放
     * @param tryIntervalMillis 轮询的时间间隔(毫秒)
     * @param maxTryCount       最大的轮询次数
     */
    public boolean tryLock(final int lockSeconds, final long tryIntervalMillis, final int maxTryCount) {

        int tryCount = 0;
        while (true) {
            if (++tryCount >= maxTryCount) {
                // 获取锁超时
                return false;
            }
            try {
                if (tryLock(lockKey, lockSeconds)) {
                    return true;
                }
            } catch (Exception e) {
                logger.error("tryLock Error", e);
                return false;
            }
            try {
                Thread.sleep(tryIntervalMillis);
            } catch (InterruptedException e) {
                logger.error("tryLock interrupted", e);
                return false;
            }
        }

    }

    /**
     * TODO
     * 如果加锁后的操作比较耗时，调用方其实可以在unlock前根据时间判断下锁是否已经过期
     * 如果已经过期可以不用调用，减少一次请求
     */
    public void unlock() {
        jedis.del(new String(lockKey));
    }

    public byte[] longToBytes(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
        buffer.putLong(value);
        return buffer.array();
    }

    public long bytesToLong(byte[] bytes) {
        if (bytes.length != Long.SIZE / Byte.SIZE) {
            throw new IllegalArgumentException("wrong length of bytes!");
        }
        return ByteBuffer.wrap(bytes).getLong();
    }
}