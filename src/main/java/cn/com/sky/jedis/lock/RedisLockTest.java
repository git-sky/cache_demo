package cn.com.sky.jedis.lock;

public class RedisLockTest {


    public static void main(String[] args) {
        RedisLock lock = new RedisLock();

        String key = "key";
        String value = "value";
        int seconds = 10;

        lock.tryLock(key, value, seconds);
        lock.unlock(key);

    }
}
