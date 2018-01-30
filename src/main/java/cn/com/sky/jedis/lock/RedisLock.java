package cn.com.sky.jedis.lock;

import redis.clients.jedis.Jedis;

import java.util.Objects;


/**
 * 会出现以下的情况，会导致问题：
 * 1、客户端崩溃
 * 2、网络故障
 * 3、服务端宕机
 */
public class RedisLock {


    public boolean tryLock(String key, String value, int seconds) {
        Jedis jedis = RedisUtil.getJedis();

        /**
         * 设置成功，返回 1 。
         设置失败，返回 0 。
         */
        Long ret = jedis.setnx(key, value);

        if (Objects.equals(ret, 1)) {
            //设置成功返回 1
            Long ret2 = jedis.expire(key, seconds);
            return true;
        }
        return false;
    }

    public void unlock(String key) {
        Jedis jedis = RedisUtil.getJedis();
        /**
         * ret：被删除 key 的数量
         */
        Long ret = jedis.del(key);
    }
}
