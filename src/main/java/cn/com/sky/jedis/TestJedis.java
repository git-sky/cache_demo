package cn.com.sky.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class TestJedis {

	public static void main(String[] args) {
		TestJedis test = new TestJedis();
		// test.testString();
		// test.testList();
		// test.testSet();
		// test.testSortedSet();
		test.testHash();
	}

	/**
	 * 测试hash
	 */
	public void testHash() {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

		Jedis jedis = pool.getResource();
		try {

			// 清空数据
			System.out.println(jedis.flushDB());
			// 添加数据
			jedis.hset("hashs", "entryKey", "entryValue");
			jedis.hset("hashs", "entryKey1", "entryValue1");
			jedis.hset("hashs", "entryKey2", "entryValue2");
			System.out.println(jedis.hgetAll("hashs"));
			// 判断某个值是否存在
			System.out.println(jedis.hexists("hashs", "entryKey"));
			// 获取指定的值
			System.out.println(jedis.hget("hashs", "entryKey")); // 批量获取指定的值
			System.out.println(jedis.hmget("hashs", "entryKey", "entryKey1"));
			// 删除指定的值
			System.out.println(jedis.hdel("hashs", "entryKey"));
			System.out.println(jedis.hgetAll("hashs"));
			// 为key中的域 field 的值加上增量 increment
			System.out.println(jedis.hincrBy("hashs", "entryKey", 123l));
			System.out.println(jedis.hgetAll("hashs"));
			// // 获取所有的keys
			System.out.println(jedis.hkeys("hashs"));
			// // 获取所有的values
			System.out.println(jedis.hvals("hashs"));
			
//			jedis.evalsha(script)
			
			// jedis.psetex(key, milliseconds, value)
			// jedis.keys(pattern)
			//

		} finally {
			// 这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			pool.returnResource(jedis);
		}
		// 程序关闭时，需要调用关闭方法
		pool.destroy();

	}

	/**
	 * 测试有序set
	 */
	public void testSortedSet() {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

		Jedis jedis = pool.getResource();
		try {
			// 清空数据
			System.out.println(jedis.flushDB());
			// 添加数据
			jedis.zadd("zset", 10.1, "hello");
			jedis.zadd("zset", 10.0, ":");
			jedis.zadd("zset", 9.0, "zset");
			jedis.zadd("zset", 11.0, "zset!");
			// 元素个数
			System.out.println(jedis.zcard("zset"));
			// 元素下标
			System.out.println(jedis.zscore("zset", "zset"));
			// 集合子集
			System.out.println(jedis.zrange("zset", 0, -1));
			// 删除元素
			jedis.zrem("zset", "zset!");
			System.out.println(jedis.zrange("zset", 0, -1));
			System.out.println(jedis.zcount("zset", 9.5, 10.5));
			// 整个集合值
			System.out.println(jedis.zrange("zset", 0, -1));

		} finally {
			// 这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			pool.returnResource(jedis);
		}
		// 程序关闭时，需要调用关闭方法
		pool.destroy();

	}

	/**
	 * 测试set
	 */
	public void testSet() {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

		Jedis jedis = pool.getResource();
		try {
			// 清空数据
			System.out.println(jedis.flushDB());
			// 添加数据
			jedis.sadd("sets", "HashSet");
			jedis.sadd("sets", "SortedSet");
			jedis.sadd("sets", "TreeSet");
			// 判断value是否在列表中
			System.out.println(jedis.sismember("sets", "TreeSet"));
			// 整个列表值
			System.out.println(jedis.smembers("sets"));
			// 删除指定元素
			jedis.srem("sets", "SortedSet");
			System.out.println(jedis.smembers("sets"));

			// 出栈
			jedis.spop("sets");
			System.out.println(jedis.smembers("sets"));
			//
			jedis.sadd("sets1", "HashSet1");
			jedis.sadd("sets1", "SortedSet1");
			jedis.sadd("sets1", "TreeSet");
			jedis.sadd("sets2", "HashSet2");
			jedis.sadd("sets2", "SortedSet1");
			jedis.sadd("sets2", "TreeSet1");
			// 交集
			System.out.println(jedis.sinter("sets1", "sets2"));
			// 并集
			System.out.println(jedis.sunion("sets1", "sets2"));
			// 差集
			System.out.println(jedis.sdiff("sets1", "sets2"));

		} finally {
			// 这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			pool.returnResource(jedis);
		}
		// 程序关闭时，需要调用关闭方法
		pool.destroy();

	}

	/**
	 * 测试list
	 */
	public void testList() {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

		Jedis jedis = pool.getResource();
		try {
			// 清空数据
			System.out.println(jedis.flushDB());
			// 添加数据
			jedis.lpush("lists", "vector");
			jedis.lpush("lists", "ArrayList");
			jedis.lpush("lists", "LinkedList");
			// 数组长度
			System.out.println(jedis.llen("lists"));
			// 排序
			// System.out.println(jedis.sort("lists"));
			// 字串
			System.out.println(jedis.lrange("lists", 0, 3));
			// 修改列表中单个值
			jedis.lset("lists", 0, "hello list!");
			System.out.println(jedis.lrange("lists", 0, 3));
			// 获取列表指定下标的值
			// System.out.println(jedis.lindex("lists", 0));
			// 删除列表指定下标的值
			// jedis.lrem("lists", 0, "vector");
			// System.out.println(jedis.lrange("lists", 0, 3));
			// // 删除区间以外的数据
			// jedis.ltrim("lists", 0, 1);
			// System.out.println(jedis.lrange("lists", 0, 3));
			// 列表出栈
			jedis.lpop("lists");
			// // 整个列表值
			System.out.println(jedis.lrange("lists", 0, -1));

		} finally {
			// 这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			pool.returnResource(jedis);
		}
		// 程序关闭时，需要调用关闭方法
		pool.destroy();
	}

	/**
	 * 测试string
	 */
	public void testString() {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

		Jedis jedis = pool.getResource();
		try {
			// 清空数据
			System.out.println(jedis.flushDB());
			// 存储数据
			jedis.set("foo", "bar");
			System.out.println(jedis.get("foo"));

			// 若key不存在，则存储
			jedis.setnx("foo", "foo not exits");
			System.out.println(jedis.get("foo"));

			// 覆盖数据
			jedis.set("foo", "foo update");
			System.out.println(jedis.get("foo"));

			// 追加数据
			jedis.append("foo", " hello, world");
			System.out.println(jedis.get("foo"));

			// 设置key的有效期，并存储数据
			jedis.setex("foo", 2, "foo not exits");
			System.out.println(jedis.get("foo"));
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			System.out.println(jedis.get("foo"));

			// 获取并更改数据
			jedis.set("foo", "foo update");
			System.out.println(jedis.getSet("foo", "foo modify"));

			// 截取value的值
			System.out.println(jedis.getrange("foo", 1, 3));
			System.out.println(jedis.mset("mset1", "mvalue1", "mset2",
					"mvalue2", "mset3", "mvalue3", "mset4", "mvalue4"));
			System.out.println(jedis.mget("mset1", "mset2", "mset3", "mset4"));
			System.out.println(jedis
					.del(new String[] { "foo", "foo1", "foo3" }));

		} finally {
			// 这里很重要，一旦拿到的jedis实例使用完毕，必须要返还给池中
			pool.returnResource(jedis);
		}
		// 程序关闭时，需要调用关闭方法
		pool.destroy();

	}
}