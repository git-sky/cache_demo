package cn.com.sky.jedis.lock.good;

import cn.com.sky.jedis.lock.RedisUtil;
import redis.clients.jedis.Jedis;

import java.util.Objects;
import java.util.UUID;


/**
 * 单实例redis方案（比较完美,官方推荐）
 */
public class RedisLock4 {

    private String random;

    public boolean tryLock(String key,int seconds) {
        Jedis jedis = RedisUtil.getJedis();

        random= UUID.randomUUID().toString();

        String ret = jedis.set(key, random, "nx", "ex", seconds);

        if (Objects.equals(ret, "OK")) {
            return true;
        }
        return false;
    }

    public void unlock(String key) {
        Jedis jedis = RedisUtil.getJedis();

//        jedis.eval();

//        if redis.call("get",KEYS[1]) == ARGV[1] then
//        return redis.call("del",KEYS[1])
//else
//        return 0
//        end

    }
}
