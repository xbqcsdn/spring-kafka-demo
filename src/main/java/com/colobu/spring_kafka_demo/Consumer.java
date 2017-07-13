package com.colobu.spring_kafka_demo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;


public class Consumer {
	private static final String CONFIG = "/consumer_context.xml";

	@SuppressWarnings({ "unchecked","rawtypes" })
	public static void main(String[] args) {
		
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, Consumer.class);
		ctx.start();

		final QueueChannel channel = ctx.getBean("inputFromKafka", QueueChannel.class);
		Message msg;		
		
		while((msg = channel.receive()) != null) {
			HashMap map = (HashMap)msg.getPayload();
			Set<Map.Entry> set = map.entrySet();
			for (Map.Entry entry : set) {
				String topic = (String)entry.getKey();
				System.out.println("Topic:" + topic);
				ConcurrentHashMap<Integer,List<String>> messages = (ConcurrentHashMap<Integer,List<String>>)entry.getValue();
				Collection<List<String>> values = messages.values();
				for (Iterator<List<String>> iterator = values.iterator(); iterator.hasNext();) {
					List<String> list = iterator.next();
					// 接受的消息 ：消息完整，但是多了一部分 乱码，不知道怎么解决，就做了一个约定，以xsjt后面的消息 为 主要消息
					for (String object : list) {
						String message = new String(object);
						System.out.println("接收到的Message: " + message.substring(message.lastIndexOf("xsjt") + 4));
					}
				}
			}
		}
		ctx.close();
	}
}
