package cn.com.sky.jedis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class TestJedis3 {

	// 连接redis服务
	static Jedis jedis = new Jedis("localhost", 6379);

	@BeforeClass
	public static void setUpBeforeClass() {
		// 密码验证-如果你没有设置redis密码,可不验证即可使用相关命令
		jedis.auth("abcdefg");

		// 查看服务是否运行
		System.out.println("Server is running: " + jedis.ping());

	}

	@AfterClass
	public static void tearDownAfterClass() {
		jedis.close();
	}

	@Test
	public void testString() {
		
//		jedis.keys(pattern);

		// 字符串string
		jedis.set("redis", "myredis");
		System.out.println(jedis.get("redis"));

		jedis.set("w3ckey", "Redis tutorial");
		System.out.println(jedis.get("w3ckey"));

		// 在原有值得基础上添加,如若之前没有该key，则导入该key
		// 之前已经设定了redis对应"myredis",此句执行便会使redis对应"myredisyourredis"
		jedis.append("redis", "yourredis");
		jedis.append("content", "rabbit");

		// mset 是设置多个key-value值 参数（key1,value1,key2,value2,...,keyn,valuen）
		// mget 是获取多个key所对应的value值 参数（key1,key2,key3,...,keyn） 返回的是个list
		jedis.mset("name1", "yangw", "name2", "demon", "name3", "elena");
		System.out.println(jedis.mget("name1", "name2", "name3"));

		// STRING 操作

		// SET key value将字符串值value关联到key。
		jedis.set("name", "wangjun1");
		jedis.set("id", "123456");
		jedis.set("address", "guangzhou");

		// SETEX key seconds value将值value关联到key，并将key的生存时间设为seconds(以秒为单位)。
		jedis.setex("foo", 5, "haha");

		// MSET key value [key value ...]同时设置一个或多个key-value对。
		jedis.mset("haha", "111", "xixi", "222");

		// jedis.flushAll();清空所有的key
		System.out.println(jedis.dbSize());// dbSize是多少个key的个数

		// APPEND key value如果key已经存在并且是一个字符串，APPEND命令将value追加到key原来的值之后。
		jedis.append("foo", "00");// 如果key已经存在并且是一个字符串，APPEND命令将value追加到key原来的值之后。

		// GET key 返回key所关联的字符串值
		jedis.get("foo");

		// MGET key [key ...] 返回所有(一个或多个)给定key的值
		List<String> list1 = jedis.mget("haha", "xixi");
		for (int i = 0; i < list1.size(); i++) {
			System.out.println(list1.get(i));
		}

	}

	@Test
	public void testHash() {
		// 哈希hash
		Map<String, String> user = new HashMap<String, String>();
		user.put("name", "cd");
		user.put("password", "123456");
		// map存入redis
		jedis.hmset("user", user);
		// mapkey个数
		System.out.println(String.format("len:%d", jedis.hlen("user")));
		// map中的所有键值
		System.out.println(String.format("keys: %s", jedis.hkeys("user")));
		// map中的所有value
		System.out.println(String.format("values: %s", jedis.hvals("user")));
		// 取出map中的name字段值
		List<String> rsmap = jedis.hmget("user", "name", "password");
		System.out.println(rsmap);
		// 删除map中的某一个键值 password
		jedis.hdel("user", "password");
		System.out.println(jedis.hmget("user", "name", "password"));

		// Hash 操作

		// HSET key field value将哈希表key中的域field的值设为value。
		jedis.hset("website", "google", "www.google.cn");
		jedis.hset("website", "baidu", "www.baidu.com");
		jedis.hset("website", "sina", "www.sina.com");

		// HMSET key field value [field value ...] 同时将多个field -
		// value(域-值)对设置到哈希表key中。
		Map<String, String> map = new HashMap<>();
		map.put("cardid", "123456");
		map.put("username", "jzkangta");
		jedis.hmset("hash", map);

		// HGET key field返回哈希表key中给定域field的值。
		System.out.println(jedis.hget("hash", "username"));

		// HMGET key field [field ...]返回哈希表key中，一个或多个给定域的值。
		List<String> list2 = jedis.hmget("website", "google", "baidu", "sina");
		for (int i = 0; i < list2.size(); i++) {
			System.out.println(list2.get(i));
		}

		// HGETALL key返回哈希表key中，所有的域和值。
		Map<String, String> map2 = jedis.hgetAll("hash");
		for (Map.Entry<String, String> entry : map2.entrySet()) {
			System.out.print(entry.getKey() + ":" + entry.getValue() + "\t");
		}

	}

	@Test
	public void testList() {
		// list
		jedis.del("listDemo");
		System.out.println(jedis.lrange("listDemo", 0, -1));
		jedis.lpush("listDemo", "A");
		jedis.lpush("listDemo", "B");
		jedis.lpush("listDemo", "C");
		System.out.println(jedis.lrange("listDemo", 0, -1));
		System.out.println(jedis.lrange("listDemo", 0, 1));

		// 存储数据到列表中
		jedis.lpush("tutorial-list", "Redis");
		jedis.lpush("tutorial-list", "Mongodb");
		jedis.lpush("tutorial-list", "Mysql");
		// 获取存储的数据并输出
		List<String> list = jedis.lrange("tutorial-list", 0, 5);
		for (int i = 0; i < list.size(); i++) {
			System.out.println("Stored string in redis:: " + list.get(i));
		}

		// 一般SORT用法 最简单的SORT使用方法是SORT key。
		jedis.lpush("sort", "1");
		jedis.lpush("sort", "4");
		jedis.lpush("sort", "6");
		jedis.lpush("sort", "3");
		jedis.lpush("sort", "0");

		List<String> list2 = jedis.sort("sort");// 默认是升序
		for (int i = 0; i < list2.size(); i++) {
			System.out.println(list2.get(i));
		}

		// LIST 操作 //LPUSH key value [value ...]将值value插入到列表key的表头。
		jedis.lpush("list", "abc");
		jedis.lpush("list", "xzc");
		jedis.lpush("list", "erf");
		jedis.lpush("list", "bnh");

		// LRANGE key start
		// stop返回列表key中指定区间内的元素，区间以偏移量start和stop指定。下标(index)参数start和stop都以0为底
		// ，也就是说，以0表示列表的第一个元素，以1表示列表的第二个元素，以此类推。你也可以使用负数下标，以-1表示列表的最后一个元素，-2
		// 表示列表的倒数第二个元素，以此类推。
		List<String> list3 = jedis.lrange("list", 0, -1);
		for (int i = 0; i < list3.size(); i++) {
			System.out.println(list3.get(i));
		}

	}

	@Test
	public void testKey() {

		// KEY操作
		Set<String> keys = jedis.keys("*");// 列出所有的key，查找特定的key如：jedis.keys("foo")
		Iterator<String> t1 = keys.iterator();
		while (t1.hasNext()) {
			String obj1 = t1.next();
			System.out.println(obj1);
		}

		// DEL 移除给定的一个或多个key。如果key不存在，则忽略该命令。
		jedis.del("name1");
		// TTL 返回给定key的剩余生存时间(time to live)(以秒为单位)
		jedis.ttl("foo");
		// PERSIST key 移除给定key的生存时间。
		jedis.persist("foo");
		// EXISTS 检查给定key是否存在。
		jedis.exists("foo");

		// MOVE key db
		// 将当前数据库(默认为0)的key移动到给定的数据库db当中。如果当前数据库(源数据库)和给定数据库(目标数据库)
		// 有相同名字的给定key，或者key不存在于当前数据库，那么MOVE没有任何效果。
		jedis.move("foo", 1);// 将foo这个key，移动到数据库1

		// RENAME key newkey
		// 将key改名为newkey。当key和newkey相同或者key不存在时，返回一个错误。当newkey已经存在时，RENAME命令将覆盖旧值。
		jedis.rename("foo", "foonew");

		// TYPE key 返回key所储存的值的类型。
		System.out.println(jedis.type("foo"));
		// none(key不存在),string(字符串),list(列表),set(集合),zset(有序集),hash(哈希表)

		// EXPIRE key seconds 为给定key设置生存时间。当key过期时，它会被自动删除。
		jedis.expire("foo", 5);// 5秒过期
		// EXPIREAT
		// EXPIREAT的作用和EXPIRE一样，都用于为key设置生存时间。不同在于EXPIREAT命令接受的时间参数是UNIX时间戳(unix
		// timestamp)。
	}

	@Test
	public void testSet() {

		// set
		jedis.sadd("sname", "wobby");
		jedis.sadd("sname", "kings");
		jedis.sadd("sname", "demon");
		System.out.println(String.format("set num: %d", jedis.scard("sname")));
		System.out.println(String.format("all members: %s", jedis.smembers("sname")));
		System.out.println(String.format("is member: %B", jedis.sismember("sname", "wobby")));
		System.out.println(String.format("rand member: %s", jedis.srandmember("sname")));
		// 删除一个对象
		jedis.srem("sname", "demon");
		System.out.println(String.format("all members: %s", jedis.smembers("sname")));

		// SET 操作
		// SADD key member [member ...]将member元素加入到集合key当中。
		jedis.sadd("testSet", "s1");
		jedis.sadd("testSet", "s2");
		jedis.sadd("testSet", "s3");
		jedis.sadd("testSet", "s4");
		jedis.sadd("testSet", "s5");

		// SREM key member移除集合中的member元素。
		jedis.srem("testSet", "s5");

		// SMEMBERS key返回集合key中的所有成员。
		Set<String> set = jedis.smembers("testSet");
		Iterator<String> t2 = set.iterator();
		while (t2.hasNext()) {
			String obj1 = t2.next();
			System.out.println(obj1);
		}

		// SISMEMBER key member判断member元素是否是集合key的成员。是（true），否则（false）
		System.out.println(jedis.sismember("testSet", "s4"));

	}

}
