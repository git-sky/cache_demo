package cn.com.sky.memcache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.junit.Test;

/**
 * xmemcached客户端
 * 
 */
public class MemcachedClientTest {

	@Test
	public void test() {
		String address = "127.0.0.1:11211 127.0.0.2:11211 127.0.0.3:11211 127.0.0.4:11211";
		int[] weights = { 1, 1, 1, 1 };

		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(address), weights);

		// 设置连接池大小，即客户端个数
		builder.setConnectionPoolSize(50);

		// 宕机报警
		builder.setFailureMode(true);

		// 使用二进制文件
		builder.setCommandFactory(new BinaryCommandFactory());

		MemcachedClient memcachedClient = null;
		try {
			memcachedClient = builder.build();
			try {
				// 设置/获取
				memcachedClient.set("zlex", 36000, "set/get");
				assertEquals("set/get", memcachedClient.get("zlex"));

				// 替换
				memcachedClient.replace("zlex", 36000, "replace");
				assertEquals("replace", memcachedClient.get("zlex"));

				// 移除
				memcachedClient.delete("zlex");
				assertNull(memcachedClient.get("zlex"));
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (MemcachedException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (memcachedClient != null) {
				try {
					memcachedClient.shutdown();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
