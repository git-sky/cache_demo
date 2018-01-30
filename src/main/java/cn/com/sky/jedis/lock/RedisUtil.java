package cn.com.sky.jedis.lock;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.HashSet;
import java.util.Set;


public class RedisUtil {

    public static Jedis getJedis() {
        JedisPool jedisPool;
        Jedis jedis = null;
        try {
            GenericObjectPoolConfig poolConfig = config();
            poolConfig.setTestWhileIdle(true);
            poolConfig.setMinEvictableIdleTimeMillis(60000);
            poolConfig.setTimeBetweenEvictionRunsMillis(30000);
            poolConfig.setNumTestsPerEvictionRun(-1);


            String redisHost = "";
            int redisPort = 6379;
            String redisPassword = "";
            int timeout = 2000;

            if (redisPassword != "") {
                jedisPool = new JedisPool(poolConfig, redisHost, redisPort, timeout, redisPassword);
            } else {
                jedisPool = new JedisPool(poolConfig, redisHost, redisPort, timeout);
            }
            jedis = jedisPool.getResource();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jedis;
    }

    public static JedisCluster getJedisCluster() {

        JedisCluster jedisCluster = null;

        try {

            String address = "a,b,c";
            Set<HostAndPort> hostAndPorts = new HashSet<>();

            String[] addressArray = address.split(",");

            for (int i = 0; i < addressArray.length; i++) {
                String hostAndPort = addressArray[i];
                hostAndPort = hostAndPort.trim();
                String[] hostAndPortArray = hostAndPort.split(":");
                int port = Integer.valueOf(hostAndPortArray[1]).intValue();
                HostAndPort hap = new HostAndPort(hostAndPortArray[0], port);
                hostAndPorts.add(hap);
            }

            GenericObjectPoolConfig poolConfig = config();

            int timeout = 2000;
            int maxRedirections = 3;

            jedisCluster = new JedisCluster(hostAndPorts, timeout, maxRedirections, poolConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jedisCluster;
    }


    private static GenericObjectPoolConfig config() {

        //构造属性
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(500);
        poolConfig.setMaxIdle(100);
        poolConfig.setMinIdle(10);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setMaxWaitMillis(1000L);

        return poolConfig;
    }


}
